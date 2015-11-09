package com.qingdao.shiqu.arcgis.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;

/**
 * 包含了一些用于读写本地数据库的静态方法
 */
public class SQLiteAction {
    /**
     * 查询本地数据库
     * @param database 数据库对象
     * @param table 表名
     * @param columns 列名
     * @param selection 条件子句，相当于where
     * @param selectionArgs 条件语句的参数数组
     * @param groupBy 分组
     * @param having 分组条件
     * @param orderBy 排序
     * @param limit 分页查询的限制
     * @return 返回值，相当于结果集ResultSet
     */
    public static Cursor query(SQLiteDatabase database, String table, String[] columns,
                               String selection, String[] selectionArgs, String groupBy,
                               String having, String orderBy, String limit) {
        return database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * 保存光缆路由到数据库
     * @param database 本地数据库
     * @param geometry 光缆路由的Geometry
     */
    public static void storeGllyToDatabase(SQLiteDatabase database, Geometry geometry) {
        Integer hashcode = geometry.hashCode();
        byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
        ContentValues cv = new ContentValues();
        cv.put("geometry", geometryByte);
        cv.put("hashcode", hashcode.toString());
        database.insert("glly", null, cv);
    }

    /**
     * 查询所有光缆路由数据
     * @param database 数据库
     * @return 查询结果
     */
    public static Cursor queryGlly(SQLiteDatabase database) {
        return query(database, "glly", null, null, null, null, null, null, null);
    }

    /**
     * 保存光缆路由到数据库
     * @param geometry 光缆路由的Geometry
     */
    public static void storeDllyToDatabase(SQLiteDatabase database, Geometry geometry) {
        Integer hashcode = geometry.hashCode();
        byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
        ContentValues cv = new ContentValues();
        cv.put("geometry", geometryByte);
        cv.put("hashcode", hashcode.toString());
        database.insert("dlly", null, cv);
    }

    /**
     * 查询所有电缆路由数据
     * @param database 数据库
     * @return 查询结果
     */
    public static Cursor queryDlly(SQLiteDatabase database) {
        return query(database, "dlly", null, null, null, null, null, null, null);
    }

    // TODO 保存和查询标注数据

    /**
     * 保存标注到数据库
     * @param database 数据库
     * @param geometry 标注的Geometry
     */
    public static void storeMarkToDatabase(SQLiteDatabase database, Geometry geometry) {

    }

    /**
     * 查询所有标注的数据
     * @param database 数据库
     * @return 查询结果
     */
    public static Cursor queryMark(SQLiteDatabase database) {
        return null;
        //return query(database, "mark", null, null, null, null, null, null, null);
    }
}