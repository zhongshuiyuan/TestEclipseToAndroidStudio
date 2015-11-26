package com.qingdao.shiqu.arcgis.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.celerysoft.imagepager.ImagePager;
import com.celerysoft.imagepager.adapter.SimpleImagePagerAdapter;
import com.celerysoft.imagepager.animation.DepthPageTransformer;
import com.celerysoft.materialdesigndialog.MaterialDesignDialog;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.listener.TencentUiListener;
import com.qingdao.shiqu.arcgis.sqlite.DatabaseOpenHelper;
import com.qingdao.shiqu.arcgis.sqlite.SQLiteAction;
import com.qingdao.shiqu.arcgis.utils.ImageUtil;
import com.tencent.open.GameAppOperation;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

import Eruntech.BirthStone.Base.Forms.Activity;

/**
 * 查看现场图
 */
public class DisplaySceneImageActivity extends Activity {
    private static final String TAG = DisplaySceneImageActivity.class.getSimpleName();

    android.database.sqlite.SQLiteDatabase mSQLiteDatabase;
    Tencent mTencent;

    private String[] mImageIds;
    private ArrayList<String> mImagePaths;
    private int mImagePosition = 0;

    private ImagePager mImagePager;
    SimpleImagePagerAdapter mAdapter;

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
        setResultAndFinish(RESULT_OK);
    }

    @Override
    public void onCreateView() {
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        mSQLiteDatabase = databaseOpenHelper.getWritableDatabase();

        mTencent = Tencent.createInstance("1104989728", getApplicationContext());

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
                showDeleteImageDialog();
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
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }

            @Override
            public void onPageSelected(int i) {
                updateImageTitle(i);
                mImagePosition = i;
            }
        });
        mImagePager.setPageTransformer(true, new DepthPageTransformer());

        Intent intent = getIntent();
        if (intent != null) {
            mImageIds = (String[]) intent.getSerializableExtra("imageIds");
            updateAdapter();
        }

        updateImageTitle(0);

        hideActionBar();

        super.onCreateView();
    }

    private void showDeleteImageDialog() {
        final MaterialDesignDialog dialog = new MaterialDesignDialog(this);
        dialog.setTitle("删除图片")
                .setMessage("要在从标注点删除本张现场图吗？")
                .setPositiveButton("删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteImage();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void deleteImage() {
        mImageIds = removeImageIdAtPosition(mImagePosition);
        updateAdapter();
    }

    private void shareImage() {
        String currentImagePath = mImagePaths.get(mImagePager.getCurrentImagePosition());
        if (currentImagePath != null) {
            ArrayList<String> fileDataList = new ArrayList<>();
            fileDataList.add(currentImagePath);

            final Bundle params = new Bundle();
            params.putString(GameAppOperation.QQFAV_DATALINE_APPNAME, "青岛广电GIS系统");
            params.putString(GameAppOperation.QQFAV_DATALINE_TITLE, "图片");
            params.putInt(GameAppOperation.QQFAV_DATALINE_REQTYPE,GameAppOperation.QQFAV_DATALINE_TYPE_IMAGE_TEXT);
            params.putString(GameAppOperation.QQFAV_DATALINE_DESCRIPTION, "图片描述");
            params.putStringArrayList(GameAppOperation.QQFAV_DATALINE_FILEDATA, fileDataList);
            mTencent.sendToMyComputer(this, params, new TencentUiListener());
        }
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

    private String[] removeImageIdAtPosition(int position) {
        String[] imageIds = null;

        if (position < mImageIds.length) {
            imageIds = new String[mImageIds.length - 1];
            int imageCount = imageIds.length;
            for (int i = 0; i < imageCount; ++i) {
                if (i < position) {
                    imageIds[i] = mImageIds[i];
                } else {
                    imageIds[i] = mImageIds[i+1];
                }
            }
        }

        return imageIds;
    }

    private void setResultAndFinish(int resultCode) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, DisplaySceneImageActivity.class);
            intent.putExtra("imageIds", mImageIds);
            setResult(RESULT_OK, intent);
        } else if (resultCode == RESULT_CANCELED) {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void updateAdapter() {
        if (mImageIds != null) {
            if (mAdapter == null ) {
                createAdapter();
            } else {
                mAdapter.removeImage(mImagePager.getCurrentImagePosition());
            }
        }
    }

    private void createAdapter() {
        int imageCount = mImageIds.length;
        mImagePaths = new ArrayList<>();

        for (int i = 0; i < imageCount; ++i) {
            String imageId = mImageIds[i];
            String imagePath = queryImagePath(imageId);
            mImagePaths.add(imagePath);
        }

        if (mAdapter == null ) {
            mAdapter = new SimpleImagePagerAdapter(this);
            mAdapter.setImagePaths(mImagePaths);
        }

        mImagePager.setAdapter(mAdapter);
    }
}
