package com.qingdao.shiqu.arcgis.listener;

import android.content.ContentValues;
import android.content.Context;

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
 */
public abstract class MapViewOnDrawEvenListener implements DrawEventListener {

    // const
    /** 绘制类型 **/
    private int mType;
    public static final int TYPE_NULL = 0;
    /** 新增光缆路由 **/
    public static final int TYPE_ADD_GLLY = 1;
    /** 新增电缆路由 **/
    public static final int TYPE_ADD_DLLY = 2;

    // private field
    private Context mContext;
    private android.database.sqlite.SQLiteDatabase mSQLiteDatabase;
    //private DatabaseOpenHelper mDatabaseOpenHelper;

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

    public void onDrawEnd(DrawEvent event, int drawType) {
        switch (drawType) {
            case TYPE_ADD_GLLY:
                onAddGllyEnd(event);
            case TYPE_NULL:
                break;
            default:
                break;
        }
    }

    /** 绘制光缆路由完毕 **/
    private void onAddGllyEnd(DrawEvent event) {
        //mGllyLayer.addGraphic(event.getDrawGraphic());
        Geometry geometry = event.getDrawGraphic().getGeometry();
        Graphic graphic = new Graphic(geometry, SimpleSymbolTemplate.GLLY);
        mGllyLayer.addGraphic(graphic);

                                                                                                                                                                                                                    storeGllyToDatabase(geometry);
    }

    private void storeGllyToDatabase(Geometry geometry) {
        byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
        ContentValues cv = new ContentValues();
        cv.put("geometry", geometryByte);
        //mSQLiteDatabase.insert("glly", null, cv);
    }
}
