package com.qingdao.shiqu.arcgis.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Eruntech.BirthStone.Core.Helper.File;

/**
 * 文件工具类
 */
public class FileUtil {


    /**
     * 一般将文件拷到手机根目录下，无法判断这个根目录是手机内存的根目录，还是SD卡的根目录
     * 本方法直接根据相对路径返回绝对路径
     * @param context
     * @param relativePath 相对路径
     * @return 绝对路径
     */
    @Nullable
    public static String getFileAbsolutePath(Context context, String relativePath) {
        String absolutePath = "";
        if (File.exists(Environment.getExternalStorageDirectory().getAbsolutePath() + relativePath)) {
            absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + relativePath;
        } else if (File.exists(Environment.getRootDirectory().getAbsolutePath() + relativePath)) {
            absolutePath = Environment.getRootDirectory().getAbsolutePath() + relativePath;
        } else if (File.exists(getStoragePath(context, true) + relativePath)) {
            absolutePath = getStoragePath(context, true) + relativePath;
        }

        if ("".equals(absolutePath)) {
            return null;
        } else {
            return absolutePath;
        }
    }

    private static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path;
                path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
