package com.qingdao.shiqu.arcgis.utils;

import android.os.Environment;
import android.support.annotation.Nullable;

import Eruntech.BirthStone.Core.Helper.File;

/**
 * 文件工具类
 */
public class FileUtil {


    /**
     * 一般将文件拷到手机根目录下，无法判断这个根目录是手机内存的根目录，还是SD卡的根目录
     * 本方法直接根据相对路径返回绝对路径
     * @param relativePath 相对路径
     * @return 绝对路径
     */
    @Nullable
    public static String getFileAbsolutePath(String relativePath) {
        String absolutePath = "";
        if(File.exists(Environment.getExternalStorageDirectory().getAbsolutePath() + relativePath)) {
            absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + relativePath;
        } else if(File.exists(Environment.getRootDirectory().getAbsolutePath() + relativePath)) {
            absolutePath = Environment.getRootDirectory().getAbsolutePath() + relativePath;
        }

        if("".equals(absolutePath)) {
            return null;
        } else {
            return absolutePath;
        }
    }
}
