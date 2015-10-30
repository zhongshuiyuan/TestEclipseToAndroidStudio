package com.qingdao.shiqu.arcgis.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015-10-30.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "test.db"; //数据库名称
    private static final int version = 1; //数据库版本

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table custom(graphic blob not null );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
