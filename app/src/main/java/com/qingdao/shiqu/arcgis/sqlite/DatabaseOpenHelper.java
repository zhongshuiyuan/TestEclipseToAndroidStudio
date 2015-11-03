package com.qingdao.shiqu.arcgis.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 解决
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "customData.db"; //数据库名称
    private static final int version = 1; //数据库版本

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String sql = "create table geometry(polyline blob not null);";
        // 创建光缆路由表
        String sql = "create table glly(geometry blob not null);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
