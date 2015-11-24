package com.qingdao.shiqu.arcgis.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.celerysoft.imagepager.ImagePager;
import com.celerysoft.imagepager.adapter.SimpleImagePagerAdapter;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.sqlite.DatabaseOpenHelper;
import com.qingdao.shiqu.arcgis.sqlite.SQLiteAction;
import com.qingdao.shiqu.arcgis.utils.ImageUtil;

import Eruntech.BirthStone.Base.Forms.Activity;

/**
 * 查看现场图
 */
public class DisplaySceneImageActivity extends Activity {
    private static final String TAG = DisplaySceneImageActivity.class.getSimpleName();

    android.database.sqlite.SQLiteDatabase mSQLiteDatabase;

    private String[] mImageIds;

    private ImagePager mImagePager;
    private View mActionBar;
    private View mActionBarShadow;
    private View mBtnBack;
    private TextView mTvTitle;
    private View mBtnDelete;
    private View mBtnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_scene_image);

        onCreateView();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onCreateView() {
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        mSQLiteDatabase = databaseOpenHelper.getWritableDatabase();

        mActionBar = findViewById(R.id.display_scene_actionbar);
        mActionBarShadow = findViewById(R.id.display_scene_actionbar_shadow);

        mTvTitle = (TextView) findViewById(R.id.display_scene_tv_title);

        mBtnBack = findViewById(R.id.display_scene_btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBtnDelete = findViewById(R.id.display_scene_btn_delete);
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });

        mBtnShare = findViewById(R.id.display_scene_btn_share);
        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });

        mImagePager = (ImagePager) findViewById(R.id.display_scene_image_pager);
        mImagePager.setOnPageClickListener(new ImagePager.OnPageClickListener() {
            @Override
            public void onPageClick() {
                toggleActionBarVisibility();
            }
        });
        mImagePager.setOnImageChangeListener(new ImagePager.OnImageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}
            @Override
            public void onPageScrollStateChanged(int i) {}
            @Override
            public void onPageSelected(int i) {
                updateImageTitle(i);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            mImageIds = (String[]) intent.getSerializableExtra("imageIds");
            if (mImageIds != null) {
                int imageCount = mImageIds.length;
                String[] imagePaths = new String[imageCount];

                for (int i = 0; i < imageCount; ++i) {
                    String imageId = mImageIds[i];
                    String imagePath = queryImagePath(imageId);
                    imagePaths[i] = imagePath;
                }

                SimpleImagePagerAdapter adapter = new SimpleImagePagerAdapter(this);
                adapter.setImagePaths(imagePaths);
                mImagePager.setAdapter(adapter);
            }
        }

        updateImageTitle(0);

        hideActionBar();

        super.onCreateView();
    }

    private void deleteImage() {

    }

    private void shareImage() {

    }

    private void toggleActionBarVisibility() {
        if (mActionBar.getVisibility() == View.VISIBLE) {
            hideActionBar();
        } else {
            showActionBar();
        }
    }

    private void showActionBar() {
        mActionBar.setVisibility(View.VISIBLE);
        mActionBarShadow.setVisibility(View.VISIBLE);
    }

    private void hideActionBar() {
        mActionBar.setVisibility(View.INVISIBLE);
        mActionBarShadow.setVisibility(View.INVISIBLE);
    }

    private String queryImagePath(String imageId) {
        String imagePath = null;

        Cursor c = SQLiteAction.queryImageViaId(mSQLiteDatabase, imageId);
        if (c != null) {
            if (c.moveToFirst()) {
                try {
                    int recordCount = c.getCount();
                    if (recordCount != 1) {
                        if (recordCount == 0) {
                            Log.w(TAG, "没有查询到已有的图片信息，可能图片已被删除");
                        } else if (recordCount > 1) {
                            Log.w(TAG, "有" + recordCount + "个图片对象具有相同的id");
                        }
                    } else {
                        imagePath = c.getString(c.getColumnIndex("path"));
                    }
                } finally {
                    c.close();
                }
            }
        }

        return imagePath;
    }

    private void updateImageTitle(int position) {
        String imageId = mImageIds[position];
        String imagePath = queryImagePath(imageId);
        String imageName = ImageUtil.getImageNameFromPath(imagePath);
        mTvTitle.setText(imageName);
    }
}
