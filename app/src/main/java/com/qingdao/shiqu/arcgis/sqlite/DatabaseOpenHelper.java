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
    public static final int version = 1; //数据库版本

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + "glly";

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建光缆路由表
        String creatGlly = "create table glly(id TEXT not null, geometry blob not null);";
        db.execSQL(creatGlly);
        // 创建电缆路由表
        String creatDlly = "create table dlly(id TEXT not null, geometry blob not null);";
        db.execSQL(creatDlly);
        // 创建标注内容表
        String creatMark = "create table mark(id TEXT not null, geometry blob not null, title TEXT, content TEXT, imageIds TEXT);";
        db.execSQL(creatMark);
        // 创建图片表
        String creatImage = "create table image(id TEXT not null, path TEXT, image blob);";
        db.execSQL(creatImage);
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
