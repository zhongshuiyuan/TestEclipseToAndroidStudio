package com.qingdao.shiqu.arcgis.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.celerysoft.imagepager.ImagePager;
import com.celerysoft.imagepager.adapter.SimpleImagePagerAdapter;
import com.celerysoft.imagepager.animation.DepthPageTransformer;
import com.celerysoft.materialdesigndialog.MaterialDesignDialog;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.ShareImageListViewAdapter;
import com.qingdao.shiqu.arcgis.listener.TencentUiListener;
import com.qingdao.shiqu.arcgis.sqlite.DatabaseOpenHelper;
import com.qingdao.shiqu.arcgis.sqlite.SQLiteAction;
import com.qingdao.shiqu.arcgis.utils.ImageUtil;
import com.qingdao.shiqu.arcgis.utils.Util;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXFileObject;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.open.GameAppOperation;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.util.ArrayList;

import Eruntech.BirthStone.Base.Forms.Activity;

/**
 * 查看现场图
 */
public class DisplaySceneImageActivity extends Activity {
    private static final String TAG = DisplaySceneImageActivity.class.getSimpleName();

    private static final String TENCENT_QQ_APP_ID = "1104989728";
    private static final String TENCENT_WECHAT_APP_ID = "wx86ca47e20773e7d8";

    private android.database.sqlite.SQLiteDatabase mSQLiteDatabase;
    private Tencent mTencent;
    private IWXAPI mWechat;
    private Bitmap mShareToWechatThumb;


    private String[] mImageIds;
    private ArrayList<String> mImagePaths;
    private int mImagePosition = 0;

    private ImagePager mImagePager;
    SimpleImagePagerAdapter mAdapter;

    private MaterialDesignDialog mShareDialog;

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

        mTencent = Tencent.createInstance(TENCENT_QQ_APP_ID, getApplicationContext());
        mWechat = WXAPIFactory.createWXAPI(this, TENCENT_WECHAT_APP_ID, true);
        mWechat.registerApp(TENCENT_WECHAT_APP_ID);

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
                showShareImageDialog();
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
        // TODO 为Adater类里的集合获取get方法
        mImageIds = removeImageIdAtPosition(mImagePosition);
        updateAdapter();
    }

    private void showShareImageDialog() {
        mShareDialog = new MaterialDesignDialog(this);
        mShareDialog.setTitle("分享现场图片")
                .setCanceledOnTouchOutside(true)
                .setContentView(createShareImageListView());
        mShareDialog.show();
    }

    private ListView createShareImageListView() {
        ListView listView = new ListView(this);

        listView.setDividerHeight(0);
        listView.setAdapter(new ShareImageListViewAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ((int) id) {
                    case ShareImageListViewAdapter.WECHAT:
                        shareImageToWechatFriend();
                        break;
                    case ShareImageListViewAdapter.WECHAT_TIMELINE:
                        shareImageToWechatTimeline();
                        break;
                    case ShareImageListViewAdapter.QQ:
                        shareImageToMyComputerViaQQ();
                        break;
                    case ShareImageListViewAdapter.EMAIL:
                        shareImageViaEmail();
                        break;
                    case ShareImageListViewAdapter.DATABASE:
                        shareImageToDatabase();
                        break;
                    case ShareImageListViewAdapter.OTHER:
                        shareImage();
                        break;
                    default:
                        break;
                }
                mShareDialog.dismiss();
            }
        });

        return listView;
    }

    private void shareImageToWechatFriend() {
        shareImageToWechat(true);
    }

    private void shareImageToWechatTimeline() {
        shareImageToWechat(false);
    }

    /**
     * 分享图片到微信
     * @param isToFriend true分享给朋友，false分享到朋友圈
     */
    private void shareImageToWechat(boolean isToFriend) {

        if (!Util.isConnectToInternet(this)) {
            showConnectToInternetDialog();
            return;
        }

        if (!mWechat.isWXAppInstalled()) {
            showInstallWechatDialog();
            return;
        }

        String imagePath = mImagePaths.get(mImagePosition);

        WXFileObject fileObject = new WXFileObject();
        fileObject.filePath = imagePath;
        WXImageObject imageObject = new WXImageObject();
        imageObject.imagePath = imagePath;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imageObject;
        msg.mediaTagName = "name";
        msg.description = "deicription";
        msg.mediaTagName = "tag";
        if (mShareToWechatThumb != null && !mShareToWechatThumb.isRecycled()) {
            mShareToWechatThumb.recycle();
        }
        mShareToWechatThumb = ImageUtil.getBitmapThumb(imagePath);
        msg.setThumbImage(mShareToWechatThumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());

        req.message = msg;
        if (isToFriend) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }


        mWechat.sendReq(req);
    }


    private void shareImageToMyComputerViaQQ() {

        if (!Util.isConnectToInternet(this)) {
            showConnectToInternetDialog();
            return;
        }

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

    private void shareImage() {
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        String path = mImagePaths.get(mImagePosition);
        File file = new File(path);
        sendIntent.putExtra(android.content.Intent.EXTRA_STREAM,
                Uri.fromFile(file));
        sendIntent.setType("image/*");
        startActivity(sendIntent);
    }

    private void shareImageViaEmail() {
        Intent sendEmailIntent = new Intent(android.content.Intent.ACTION_SEND);

        //sendEmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
        sendEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "现场图");
        sendEmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "分享一张现场图给你");
        String path = mImagePaths.get(mImagePosition);
        File file = new File(path);
        sendEmailIntent.putExtra(android.content.Intent.EXTRA_STREAM,
                Uri.fromFile(file));
        sendEmailIntent.setType("image/*");
        sendEmailIntent.setType("message/rfc882");

        startActivity(sendEmailIntent);
        //startActivity(Intent.createChooser(sendEmailIntent, "请选择发送软件"));

    }

    private void shareImageToDatabase() {
        // TODO 等待后台接口
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

    private void showInstallWechatDialog() {
        final MaterialDesignDialog dialog = new MaterialDesignDialog(this);
        dialog.setTitle("尚未安装微信")
                .setMessage("您尚未安装微信，请先安装微信再通过微信分享本现场图")
                .setPositiveButton("知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void showConnectToInternetDialog() {
        final MaterialDesignDialog dialog = new MaterialDesignDialog(this);
        dialog.setTitle("需要连接到互联网")
                .setMessage("您通过QQ，微信分享现场图片需要连接到互联网，建议在WIFI环境下再分享")
                .setPositiveButton("知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}
