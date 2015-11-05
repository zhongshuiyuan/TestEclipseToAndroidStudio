package com.qingdao.shiqu.arcgis.listener;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.map.Graphic;
import com.qingdao.shiqu.arcgis.mode.SimpleSymbolTemplate;
import com.qingdao.shiqu.arcgis.sqlite.DatabaseOpenHelper;
import com.qingdao.shiqu.arcgis.utils.drawtool.DrawEvent;
import com.qingdao.shiqu.arcgis.utils.drawtool.DrawEventListener;

/**
 * Main activity 上画图工具的监听器，
 * 原来的{@link MapTouchListener}太臃肿了，既要监听选中对象，又要实现画图，
 * 所以新写一个专门监听绘图事件的，将绘图逐步从{@link MapTouchListener}里分离出来
 * @author Qinyy
 * @Date 2015-11-03
 */
public abstract class MapViewOnDrawEvenListener implements DrawEventListener {

    // const
    /** 绘制动作类型 **/
    private int mAction;
    public static final int ACTION_NULL = 0;
    /** 新增光缆路由 **/
    public static final int ACTION_ADD_GLLY = 1;
    /** 新增电缆路由 **/
    public static final int ACTION_ADD_DLLY = 2;

    // private field
    private Context mContext;
    private android.database.sqlite.SQLiteDatabase mSQLiteDatabase;

    // property (including setter and getter)
    private GraphicsLayer mGllyLayer;
    public void setGLLYLayer(GraphicsLayer gllyLayer) {
        mGllyLayer = gllyLayer;
    }

    public MapViewOnDrawEvenListener(Context context) {
        mContext = context;

        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(mContext);
        mSQLiteDatabase = databaseOpenHelper.getWritableDatabase();
    }

    @Override
    public abstract void onDrawEnd(DrawEvent event);

    public void onDrawEnd(DrawEvent event, int actionType) {
        switch (actionType) {
            case ACTION_ADD_GLLY:
                onAddGllyEnd(event);
            case ACTION_ADD_DLLY:
                onAddDllyEnd(event);
                break;
            case ACTION_NULL:
                break;
            default:
                break;
        }
    }

    /**
     * 绘制光缆路由完毕，使用光缆路由的符号将绘制的光缆路由添加到地图上，保存光缆路由到数据库
     * @param event
     */
    private void onAddGllyEnd(DrawEvent event) {
        Geometry geometry = event.getDrawGraphic().getGeometry();
        Graphic graphic = new Graphic(geometry, SimpleSymbolTemplate.GLLY);
        mGllyLayer.addGraphic(graphic);

        storeGllyToDatabase(geometry);
    }

    /**
     * 保存光缆路由到数据库
     * @param geometry 光缆路由的Geometry
     */
    private void storeGllyToDatabase(Geometry geometry) {
        Integer hashcode = geometry.hashCode();
        byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
        ContentValues cv = new ContentValues();
        cv.put("geometry", geometryByte);
        cv.put("hashcode", hashcode.toString());
        mSQLiteDatabase.insert("glly", null, cv);
    }

    /**
     * 绘制光缆路由完毕，使用光缆路由的符号将绘制的光缆路由添加到地图上，保存光缆路由到数据库
     * @param event
     */
    private void onAddDllyEnd(DrawEvent event) {
        Geometry geometry = event.getDrawGraphic().getGeometry();
        Graphic graphic = new Graphic(geometry, SimpleSymbolTemplate.DLLY);
        mGllyLayer.addGraphic(graphic);

        storeGllyToDatabase(geometry);
    }

    /**
     * 保存光缆路由到数据库
     * @param geometry 光缆路由的Geometry
     */
    private void storeDllyToDatabase(Geometry geometry) {
        Integer hashcode = geometry.hashCode();
        byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
        ContentValues cv = new ContentValues();
        cv.put("geometry", geometryByte);
        cv.put("hashcode", hashcode.toString());
        mSQLiteDatabase.insert("dlly", null, cv);
    }
}
