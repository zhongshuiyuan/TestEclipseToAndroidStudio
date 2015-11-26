package com.qingdao.shiqu.arcgis.listener;

import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * Created by Administrator on 2015-11-25.
 */
public class TencentUiListener implements IUiListener {
    private static final String TAG = TencentUiListener.class.getSimpleName();

    @Override
    public void onComplete(Object response) {
        Log.d(TAG, "onComplete");
        Log.d(TAG, "response: " + response.toString());
    }

    @Override
    public void onError(UiError e) {
        Log.d(TAG, "onError");
        Log.d(TAG, "error: " + e.errorMessage);
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "onCancel");

    }
}
