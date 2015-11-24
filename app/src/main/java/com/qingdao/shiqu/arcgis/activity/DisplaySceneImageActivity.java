package com.qingdao.shiqu.arcgis.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.celerysoft.imagepager.ImagePager;
import com.celerysoft.imagepager.adapter.ImagePagerAdapter;
import com.celerysoft.imagepager.adapter.SimpleImagePagerAdapter;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.mode.MarkObject;
import com.qingdao.shiqu.arcgis.sqlite.DatabaseOpenHelper;
import com.qingdao.shiqu.arcgis.sqlite.SQLiteAction;

import Eruntech.BirthStone.Base.Forms.Activity;

/**
 * 查看现场图
 */
public class DisplaySceneImageActivity extends Activity {
    private static final String TAG = DisplaySceneImageActivity.class.getSimpleName();

    android.database.sqlite.SQLiteDatabase mSQLiteDatabase;

    private String[] imageIds;
    private ImagePager mImagePager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_scene_image);

        onCreateView();
    }

    @Override
    public void onCreateView() {
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        mSQLiteDatabase = databaseOpenHelper.getWritableDatabase();

        mImagePager = (ImagePager) findViewById(R.id.display_scene_image_pager);

        Intent intent = getIntent();
        if (intent != null) {
            imageIds = (String[]) intent.getSerializableExtra("imageIds");
            if (imageIds != null) {
                int imageCount = imageIds.length;
                String[] imagePaths = new String[imageCount];

                for (int i = 0; i < imageCount; ++i) {
                    String imageId = imageIds[i];
                    Cursor c = SQLiteAction.queryImageViaId(mSQLiteDatabase, imageId);
                    if (c != null) {
                        if (c.moveToFirst()) {
                            int recordCount = c.getCount();
                            if (recordCount != 1) {
                                if (recordCount == 0) {
                                    Log.w(TAG, "没有查询到已有的图片信息，可能图片已被删除");
                                } else if (recordCount > 1) {
                                    Log.w(TAG, "有" + recordCount + "个图片对象具有相同的id");
                                }
                            } else {
                                String imagePath = c.getString(c.getColumnIndex("path"));
                                imagePaths[i] = imagePath;
                            }
                        }
                    }

                    SimpleImagePagerAdapter adapter = new SimpleImagePagerAdapter(this);
                    adapter.setImagePaths(imagePaths);
                    mImagePager.setAdapter(adapter);
                }
            }
        }

        super.onCreateView();
    }
}
