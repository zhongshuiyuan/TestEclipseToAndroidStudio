package com.qingdao.shiqu.arcgis.mode;

import android.graphics.Color;

import com.esri.core.symbol.SimpleLineSymbol;

/**
 * 定义了一些标识各个对象的 Symbol
 */
public class SimpleSymbolTemplate {
    /** 光缆路由 **/
    public static final SimpleLineSymbol GLLY = new SimpleLineSymbol(Color.DKGRAY, 1, SimpleLineSymbol.STYLE.SOLID);
}
