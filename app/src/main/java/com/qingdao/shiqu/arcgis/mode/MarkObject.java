package com.qingdao.shiqu.arcgis.mode;

import android.content.Context;

import com.esri.core.geometry.Geometry;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.TextSymbol;

import java.io.Serializable;

/**
 * 地图标注对象
 */
public class MarkObject implements Serializable {
    private static final long serialVersionUID = 1101201192314L;

    private String mTitle;
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
        mTextSymbol = SimpleSymbolTemplate.getMarkText(title);
    }

    private String mContent;
    public String getContent() {
        return mContent;
    }
    public void setContent(String content) {
        mContent = content;
    }

    private String[] mImageIds;
    public String[] getImageIds() {
        return mImageIds;
    }
    public void setImageIds(String[] imageIds) {
        mImageIds = imageIds;
    }

    private Geometry mGeometry;
    public Geometry getGeometry() {
        return mGeometry;
    }
    public void setGeometry(Geometry geometry) {
        mGeometry = geometry;
    }

    private TextSymbol mTextSymbol;
    public TextSymbol getTextSymbol() {
        return mTextSymbol;
    }
    private PictureMarkerSymbol mPictureMarkerSymbol;
    public PictureMarkerSymbol getPictureMarkerSymbol(Context context) {
        mPictureMarkerSymbol = SimpleSymbolTemplate.getMarkPicture(context);
        return mPictureMarkerSymbol;
    }

    private Graphic mGraphic;
    public Graphic getGraphic() {
        return mGraphic;
    }
    public void setGraphic(Graphic graphic) {
        mGraphic = graphic;
    }

    public MarkObject() {
    }

    public MarkObject(String title, String content, Geometry geometry) {
        mTitle = title;
        mContent = content;
        mGeometry = geometry;
        mTextSymbol = SimpleSymbolTemplate.getMarkText(title);
    }

    public MarkObject(String title, String content, String[] imageIds, Geometry geometry) {
        mTitle = title;
        mContent = content;
        mImageIds = imageIds;
        mGeometry = geometry;
        mTextSymbol = SimpleSymbolTemplate.getMarkText(title);
    }

    public void reset() {
        mTitle = "";
        mContent = "";
        mImageIds = null;
        mGeometry = null;
        mGraphic = null;
    }
}
