package com.qingdao.shiqu.arcgis.mode;

import android.content.Context;
import android.graphics.Color;

import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.TextSymbol;
import com.qingdao.shiqu.arcgis.R;

/**
 * 定义了一些标识各个对象的 Symbol
 */
public class SimpleSymbolTemplate {
    /** 标注（图片） **/
    public static PictureMarkerSymbol getMarkPicture(Context context) {
        return new PictureMarkerSymbol(context.getResources().getDrawable(R.drawable.ic_pin_green));
    }
    /** 标注（文字） **/
    public static TextSymbol getMarkText(String text) {
        TextSymbol textSymbol = new TextSymbol(20, text, Color.BLACK);
        textSymbol.setFontFamily("DroidSansFallback.ttf");
        textSymbol.setOffsetX(-10);
        textSymbol.setOffsetY(-40);
        return textSymbol;
    }
    /** 光缆路由 **/
    public static final SimpleLineSymbol GLLY = new SimpleLineSymbol(Color.DKGRAY, 1, SimpleLineSymbol.STYLE.SOLID);
    /** 电缆路由 **/
    public static final SimpleLineSymbol DLLY = new SimpleLineSymbol(Color.LTGRAY, 1, SimpleLineSymbol.STYLE.SOLID);
}
