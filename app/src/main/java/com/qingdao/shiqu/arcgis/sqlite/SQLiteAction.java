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
    public static void storeGlly(SQLiteDatabase database, Geometry geometry) {
        Integer id = geometry.hashCode();
        byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
        ContentValues cv = new ContentValues();
        cv.put("geometry", geometryByte);
        cv.put("id", id.toString());
        database.beginTransaction();
        try {
            database.insert("glly", null, cv);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
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
    public static void storeDlly(SQLiteDatabase database, Geometry geometry) {
        Integer id = geometry.hashCode();
        byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
        ContentValues cv = new ContentValues();
        cv.put("geometry", geometryByte);
        cv.put("id", id.toString());
        database.beginTransaction();
        try {
            database.insert("dlly", null, cv);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * 查询所有电缆路由数据
     * @param database 数据库
     * @return 查询结果
     */
    public static Cursor queryDlly(SQLiteDatabase database) {
        return query(database, "dlly", null, null, null, null, null, null, null);
    }

    /**
     * 保存标注到数据库
     * @param database 数据库
     * @param geometry 标注的Geometry
     * @param title 标注的标题
     * @param content 标注的内容
     */
    public static void storeMark(SQLiteDatabase database, Geometry geometry, String title, String content, String[] imageIds) {
        Integer id = geometry.hashCode();
        byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
        String imageId = null;
        if (imageIds != null) {
            int imageCount = imageIds.length;
            imageId = "";
            for (int i = 0; i < imageCount; ++i) {
                if (i == 0) {
                    imageId = imageIds[0];
                } else {
                    imageId += "#" + imageIds[i];
                }
            }
        }
        ContentValues cv = new ContentValues();
        cv.put("id", id.toString());
        cv.put("geometry", geometryByte);
        cv.put("title", title);
        cv.put("content", content);
        cv.put("imageIds", imageId);

        boolean isNewData = true;
        String[] selectionArgs = {id.toString()};
        Cursor c = database.query("mark", null, "id=?", selectionArgs, null, null, null, null);
        if (c != null) {
            try {
                if (c.getCount() > 0) {
                    isNewData = false;
                }
            } finally {
                c.close();
            }

        }
        database.beginTransaction();
        try {
            if (isNewData) {
                database.insert("mark", null, cv);
            } else {
                String whereClause = "id=?";
                String[] whereArgs = {id.toString()};
                database.update("mark", cv, whereClause, whereArgs);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public static void deleteMark(SQLiteDatabase database, Geometry geometry) {
        Integer id = geometry.hashCode();

        // 删除mark上的图片
        Cursor c = queryMarkViaId(database, id.toString());
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    String imageIdString = c.getString(c.getColumnIndex("imageIds"));
                    if (imageIdString != null) {
                        String[] imageIds = imageIdString.split("#");
                        for (String imageId : imageIds) {
                            deleteImage(database, imageId);
                        }
                    }
                }
            } finally {
                c.close();
            }
        }

        // 删除mark
        String whereClause = "id=?";
        String[] whereArgs = {id.toString()};
        database.beginTransaction();
        try {

            database.delete("mark", whereClause, whereArgs);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * 查询所有标注的数据
     * @param database 数据库
     * @return 查询结果
     */
    public static Cursor queryMark(SQLiteDatabase database) {
        return query(database, "mark", null, null, null, null, null, null, null);
    }

    /**
     * 通过ID查询标注数据
     * @param database 数据库
     * @param id 所需查询的标注的id
     * @return 查询结果
     */
    public static Cursor queryMarkViaId(SQLiteDatabase database, String id) {
        String[] ids = {id.toString()};
        return query(database, "mark", null, "id=?", ids, null, null, null, null);
    }

    /**
     * 保存标注现场图到数据库
     * @param database 数据库
     * @param imagePath 标注的Geometry
     */
    public static void storeImage(SQLiteDatabase database, String imagePath) {
        Integer id = imagePath.hashCode();
        ContentValues cv = new ContentValues();
        cv.put("id", id.toString());
        cv.put("path", imagePath);
        //cv.put("image", imageBlob);

        boolean isNewData = true;
        String[] selectionArgs = {id.toString()};
        Cursor c = database.query("image", null, "id=?", selectionArgs, null, null, null, null);
        if (c != null) {
            try {
                if (c.getCount() > 0) {
                    isNewData = false;
                }
            } finally {
                c.close();
            }
        }

        database.beginTransaction();
        try {
            if (isNewData) {
                database.insert("image", null, cv);
            } else {
                String whereClause = "id=?";
                String[] whereArgs = {id.toString()};
                database.update("image", cv, whereClause, whereArgs);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * 从数据库删除图片
     * @param database 数据库
     * @param id 图片ID
     */
    public static void deleteImage(SQLiteDatabase database, String id) {
        String whereClause = "id=?";
        String[] whereArgs = {id};

        database.beginTransaction();
        try {
            database.delete("image", whereClause, whereArgs);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * 查询所有的图片数据
     * @param database 数据库
     * @return 查询结果
     */
    public static Cursor queryImage(SQLiteDatabase database) {
        return query(database, "image", null, null, null, null, null, null, null);
    }

    /**
     * 通过ID查询图片数据
     * @param database 数据库
     * @param id 所需查询的标注的id
     * @return 查询结果
     */
    public static Cursor queryImageViaId(SQLiteDatabase database, String id) {
        String[] ids = {id};
        return query(database, "image", null, "id=?", ids, null, null, null, null);
    }
}
