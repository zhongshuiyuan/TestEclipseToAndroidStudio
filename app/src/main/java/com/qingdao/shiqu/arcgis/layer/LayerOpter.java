package com.qingdao.shiqu.arcgis.layer;

import java.util.List;

import jcifs.dcerpc.msrpc.netdfs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.activity.Main;

/**************************************
 *
 *      作  者： 潘跃瑞 
 *
 *      功  能：  
 *
 *      时  间：  2014-10-30
 *
 *      版  权：  青岛盛天科技有限公司 
 *      
 ****************************************/
public class LayerOpter
{
	public final int TYPE_DAO_LU = 1;

	private GraphicsLayer layer;
	SimpleLineSymbol lineSymbol;
	Context context;


	public LayerOpter(Context context,GraphicsLayer layer)
	{
		this.layer = layer;
		this.context = context;
		lineSymbol = new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH);
	}

	public void setLayer(GraphicsLayer layer)
	{
		this.layer = layer;
	}
	public void DrawRoad(List<Point> list){
		Polyline polyline = new Polyline();
		Drawable image_start = context.getResources().getDrawable(R.drawable.icon_track_navi_end);
		image_start.setBounds(new Rect(0, 0, 10, 10));
		drwaPoint(list.get(0), image_start);
		Drawable image_end = context.getResources().getDrawable(R.drawable.icon_track_navi_start);
		drwaPoint(list.get(list.size()-1), image_end); 
		polyline.startPath(list.get(0));
		for(int i=1;i<list.size();i++){
			polyline.lineTo(list.get(i));
		}
		Graphic tempGraphic = new Graphic(polyline, lineSymbol);
		layer.addGraphic(tempGraphic);
	}
	public void drawRoad(List<Point> list)
	{
		Polyline polyline = new Polyline();
		Point startPoint;
		Point endPoint;
		for (int i = 1; i < list.size(); i++)
		{
			if(i==1)
			{
				Drawable image = context.getResources().getDrawable(R.drawable.icon_track_navi_end);
				image.setBounds(new Rect(0, 0, 10, 10));
				drwaPoint(list.get(0), image);
			}else if(i==list.size()-1){
				Drawable image = context.getResources().getDrawable(R.drawable.icon_track_navi_start);
				drwaPoint(list.get(list.size()-1), image); 
			}
			startPoint = list.get(i-1);
			endPoint = list.get(i);
			Line line = new Line();
			line.setStart(startPoint); 
			line.setEnd(endPoint);

			polyline.addSegment(line, false);
		}
		Graphic tempGraphic = new Graphic(polyline, lineSymbol);
		layer.addGraphic(tempGraphic);
	}
	public void drwaPoint(Point point,Drawable drawable)
	{
		//  Drawable image = context.getResources().getDrawable(R.drawable.location_marker);
		//drawable.set
		PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol(drawable);
		Graphic graphic = new Graphic(point, pictureMarkerSymbol);
		//layer.addGraphic(graphic);
		// Graphic graphic = new graphic
		//layer.setRenderer(new SimpleRenderer(pictureMarkerSymbol));
		layer.addGraphic(graphic);
		layer.setMaxScale(1.0);
	}

}
