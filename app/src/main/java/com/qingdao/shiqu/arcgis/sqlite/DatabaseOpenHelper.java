package com.qingdao.shiqu.arcgis.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 解决使用平台托管的数据库无法正常存取Blob数据
 * @author Qinyy
 * @Date 2015-11-04
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "customData.db"; //数据库名称
    private static final int version = 1; //数据库版本

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + "glly";

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String sql = "create table geometry(polyline blob not null);";
        // 创建光缆路由表
        String creatGlly = "create table glly(geometry blob not null, hashcode TEXT not null);";
        db.execSQL(creatGlly);
        // 创建电缆路由表
        String creatDlly = "create table dlly(geometry blob not null, hashcode TEXT not null);";
        db.execSQL(creatDlly);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
