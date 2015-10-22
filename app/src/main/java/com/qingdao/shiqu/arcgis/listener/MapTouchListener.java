package com.qingdao.shiqu.arcgis.listener;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.MediaStore;
import android.renderscript.RenderScript.ContextType;
import android.text.AlteredCharSequence;
import android.text.TextPaint;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eruntech.crosssectionview.activities.TubeViewActivity;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Geometry.Type;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Feature;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
//import com.gc.materialdesign.utils.Utils;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.activity.BS_User;
import com.qingdao.shiqu.arcgis.activity.Dialog_Range;
import com.qingdao.shiqu.arcgis.activity.Main;
import com.qingdao.shiqu.arcgis.activity.RJInforActivity;
import com.qingdao.shiqu.arcgis.adapter.MyAdapter;
import com.qingdao.shiqu.arcgis.dialog.DataShowDialog;
import com.qingdao.shiqu.arcgis.helper.FunctionHelper;
import com.qingdao.shiqu.arcgis.layer.LayerOpter;
import com.qingdao.shiqu.arcgis.mode.Road;
import com.qingdao.shiqu.arcgis.sqlite.DoAction;
import com.qingdao.shiqu.arcgis.test.InforShowActivity;
import com.qingdao.shiqu.arcgis.utils.DBOpterate;
import com.qingdao.shiqu.arcgis.utils.LocalDataHelp;
import com.qingdao.shiqu.arcgis.utils.LocalDataModify;
import com.qingdao.shiqu.arcgis.utils.LocalDataAction;
import com.qingdao.shiqu.arcgis.utils.Util;
//import com.qingdao.shiqu.arcgis.utils.Util;
import com.qingdao.shiqu.arcgis.utils.XMLToServer;

/**
 * ************************************
 * 
 * 作 者： 潘跃瑞
 * 
 * 功 能： 离线地图事件监听
 * 
 * 时 间： 2014-10-30
 * 
 * 版 权： 青岛盛天科技有限公司
 * 
 *************************************** 
 */
public class MapTouchListener extends MapOnTouchListener implements OnZoomListener
{
	List<Point> points;
	Context context;
	MapView mapView;
	SimpleLineSymbol lineSymbol,lineSysb;
	SimpleMarkerSymbol markerSymbol;
	SimpleFillSymbol fillSymbol;
	Point fristPoint;
	GraphicsLayer layer;
	Geometry.Type geoType;
	OnMapListener click;
	List<FeatureLayer> featureLayers;
	FeatureLayer guandao ;
	FeatureLayer guangji,guanglan;
	SQLiteDatabase db = null;
	String startingPointName,endPointName;
	Polygon tempPolygon = null;//记录绘制过程中的多边形  
	boolean isrj = false;
	boolean addEle = false;
	GraphicsLayer newEleLayer,newgdlayer,newGLLayer,newglly,newdlly;
	String gdcq;
	int uid_point,uid_segment,uid_fiber;
	ArrayList<Integer> uid_seg_list ;
	PictureMarkerSymbol pms;
	boolean isnew = true;
	boolean addGL = false;
	boolean drawglly = false;
	boolean drawdlly = false;
	int oldFlag = 0;//判断是否连接老井，0 代表 都是新加井连接，1 代表 头点是老井 ，2 代表 尾点是老井，3 代表 头点尾点都是老井
	Point oldPoint = null;
	String jtype ;//井类型
	String gltype;//光缆类型
	LocalDataAction sld;
	boolean showglly = false;
	ArrayList<Point> glList;
	TextView zb ;
	FeatureLayer gisgj2,loufang,daolu,lupaihao,fenzhiqi;
	FeatureLayer zhuanxiangl,zhixiangl,gugangl;



	public void setZhuanxiangl(FeatureLayer zhuanxiangl) {
		this.zhuanxiangl = zhuanxiangl;
	}

	public void setZhixiangl(FeatureLayer zhixiangl) {
		this.zhixiangl = zhixiangl;
	}

	public void setGugangl(FeatureLayer gugangl) {
		this.gugangl = gugangl;
	}

	public void setFenzhiqi(FeatureLayer fenzhiqi) {
		this.fenzhiqi = fenzhiqi;
	}

	public void setLupaihao(FeatureLayer lupaihao) {
		this.lupaihao = lupaihao;
	}

	public void setDaolu(FeatureLayer daolu) {
		this.daolu = daolu;
	}

	public void setLoufang(FeatureLayer loufang) {
		this.loufang = loufang;
	}

	public void setGisgj2(FeatureLayer gisgj2) {
		this.gisgj2 = gisgj2;
	}

	public String getGdcq() {
		return gdcq;
	}

	public void setGdcq(String gdcq) {
		this.gdcq = gdcq;
	}

	public GraphicsLayer getNewGLLayer() {
		return newGLLayer;
	}

	public void setNewGLLayer(GraphicsLayer newGLLayer) {
		this.newGLLayer = newGLLayer;
	}
	public void setDrawglly(boolean drawglly) {
		this.drawglly = drawglly;
	}

	public void setDrawdlly(boolean drawdlly) {
		this.drawdlly = drawdlly;
	}
	public GraphicsLayer getNewglly() {
		return newglly;
	}

	public void setNewglly(GraphicsLayer newglly) {
		this.newglly = newglly;
	}

	public GraphicsLayer getNewdlly() {
		return newdlly;
	}

	public void setNewdlly(GraphicsLayer newdlly) {
		this.newdlly = newdlly;
	}
	public boolean isShowglly() {
		return showglly;
	}

	public void setShowglly(boolean showglly) {
		this.showglly = showglly;
	}

	public String getGltype() {
		return gltype;
	}

	public void setGltype(String gltype) {
		this.gltype = gltype;
	}

	public String getJtype() {
		return jtype;
	}

	public void setJtype(String jtype) {
		this.jtype = jtype;
	}

	public boolean isDrawGL() {
		return addGL;
	}

	public void setDrawGL(boolean drawGL) {
		this.addGL = drawGL;
	}

	public GraphicsLayer getNewgdlayer() {
		return newgdlayer;
	}

	public void setNewgdlayer(GraphicsLayer newgdlayer) {
		this.newgdlayer = newgdlayer;
	}

	public GraphicsLayer getNewEleLayer() {
		return newEleLayer;
	}

	public void setNewEleLayer(GraphicsLayer newEleLayer) {
		this.newEleLayer = newEleLayer;
	}

	public boolean isAddEle() {
		return addEle;
	}

	public void setAddEle(boolean addEle) {
		this.addEle = addEle;
	}

	public boolean isIsrj() {
		return isrj;
	}

	public void setIsrj(boolean isrj) {
		this.isrj = isrj;
	}

	public MapTouchListener(Context context, MapView mapView)
	{
		super(context, mapView);
		this.context = context;
		db = new SQLiteDatabase(context);
		this.mapView = mapView;
		points = new ArrayList<Point>();
		Drawable img = context.getResources().getDrawable(R.drawable.fjj);
		pms = new PictureMarkerSymbol(com.qingdao.shiqu.arcgis.utils.Utils.zoomDrawable(img, 40, 40));
		lineSymbol = new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH);
		lineSysb = new SimpleLineSymbol(Color.RED, 1, SimpleLineSymbol.STYLE.SOLID);
		markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
		fillSymbol = new SimpleFillSymbol(Color.GREEN);
		fillSymbol.setAlpha(60);
		click = (OnMapListener) context;
		jtype = "";
		sld = new LocalDataAction(context);
		glList = new ArrayList<Point>();
		uid_seg_list = new ArrayList<Integer>();
		zb = (TextView)LayoutInflater.from(context).inflate(R.layout.main, null).findViewById(R.id.coordinate);
	}

	public void setFeatureLayers(List<FeatureLayer> featureLayers)
	{
		this.featureLayers = featureLayers;
	}



	public void setguangJi(FeatureLayer guangJi)
	{
		this.guangji = guangJi;
	}

	public void setGuanDao(FeatureLayer guandao)
	{
		this.guandao = guandao;
	}
	public FeatureLayer getGuanglan() {
		return guanglan;
	}

	public void setGuanglan(FeatureLayer guanglan) {
		this.guanglan = guanglan;
	}

	/**
	 * 
	 * 功 能：设置测距时划线图层
	 * 
	 * @param layer
	 */
	public void setLayer(GraphicsLayer layer)
	{
		this.layer = layer;
	}

	/**
	 * 
	 * 功 能：设置绘画类型
	 * 
	 * @param geoType
	 */
	public void setGeoType(Geometry.Type geoType)
	{
		this.geoType = geoType;
		if (geoType == null)
		{ //清空
			points.clear();
			fristPoint = null;
		}
	}

	/**
	 * 地图单击事件
	 */
	@Override
	public boolean onSingleTap(MotionEvent point)
	{
		zb.setText(String.valueOf(point.getX()+","+String.valueOf(point.getY())));
		Drawable img = null;
		isnew = true;
		if(drawglly){
			// 光缆路由
			Point currentPoint  = null;
			if(SelectOneGraphic(point.getX(),point.getY(),newEleLayer)){
				currentPoint = oldPoint;
				points.add(oldPoint);
			}
			else{
				currentPoint = mapView.toMapPoint(new Point(point.getX(), point.getY()));
				points.add(currentPoint);
				/*SimpleMarkerSymbol msb = new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
				Graphic graphic = new Graphic(currentPoint, msb);
				uid_point = newglly.addGraphic(graphic);*/
			}

			if (fristPoint == null)
			{
				fristPoint = currentPoint;
			}
			else
			{
				if(!fristPoint.equals(currentPoint)){
					Line l = new Line();
					l.setStart(fristPoint);
					l.setEnd(currentPoint);

					Polyline polyline = new Polyline();
					polyline.addSegment(l, false);
					lineSysb = new SimpleLineSymbol(Color.DKGRAY,1, SimpleLineSymbol.STYLE.SOLID);
					Graphic tempGraphic = new Graphic(polyline, lineSysb);
					uid_segment = newglly.addGraphic(tempGraphic);
					fristPoint = currentPoint;
				}
			}
			return true;
		}
		if(drawdlly){
			// 电缆路由
			Point currentPoint  = null;
			if(SelectOneGraphic(point.getX(),point.getY(),newEleLayer)){
				currentPoint = oldPoint;
				points.add(oldPoint);
			}
			else{
				currentPoint = mapView.toMapPoint(new Point(point.getX(), point.getY()));
				points.add(currentPoint);
				/*SimpleMarkerSymbol msb = new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
				Graphic graphic = new Graphic(currentPoint, msb);
				uid_point = newdlly.addGraphic(graphic);*/
			}

			if (fristPoint == null)
			{
				fristPoint = currentPoint;
			}
			else
			{
				if(!fristPoint.equals(currentPoint)){
					Line l = new Line();
					l.setStart(fristPoint);
					l.setEnd(currentPoint);

					Polyline polyline = new Polyline();
					polyline.addSegment(l, false);
					lineSysb = new SimpleLineSymbol(Color.LTGRAY,1, SimpleLineSymbol.STYLE.SOLID);
					Graphic tempGraphic = new Graphic(polyline, lineSysb);
					uid_segment = newdlly.addGraphic(tempGraphic);
					fristPoint = currentPoint;
				}
			}
			return true;
		}
		/**
		 * 添加光缆
		 * **/
		if(addGL){

			Point currentPoint  = null;
			if(SelectOneGraphic(point.getX(),point.getY(),newEleLayer)){
				currentPoint = oldPoint;
				points.add(oldPoint);
			}
			/*if (fristPoint == null)
			{
				fristPoint = currentPoint;
			}
			else
			{
				if(!fristPoint.equals(currentPoint)){
					//如此添加光缆可能会有问题
					Line l = new Line();
					l.setStart(fristPoint);
					l.setEnd(currentPoint);

					Polyline polyline = new Polyline();
					polyline.addSegment(l, false);
					lineSysb = new SimpleLineSymbol(Color.GREEN, 1, SimpleLineSymbol.STYLE.SOLID);
					Graphic tempGraphic = new Graphic(polyline, lineSysb);
					int uid = newgdlayer.addGraphic(tempGraphic);
					fristPoint = currentPoint;
				}
			}*/
			return true;
		}
		/**
		 * 添加管道
		 * **/
		if(addEle){
			Point currentPoint  = null;
			if(SelectOneGraphic(point.getX(),point.getY(),newEleLayer)){
				currentPoint = oldPoint;
				points.add(oldPoint);
			}
			else{
				currentPoint = mapView.toMapPoint(new Point(point.getX(), point.getY()));
				points.add(currentPoint);
				//Get jing pictrue path
				String path = DoAction.getGJPathByName(context,jtype);
				//transition path to drawable 
				Bitmap bm = DoAction.TransPath2Bmp(path);
				BitmapDrawable bd = new BitmapDrawable(bm);
				img = bd;
				pms = new PictureMarkerSymbol(com.qingdao.shiqu.arcgis.utils.Utils.zoomDrawable(img, 40, 40));
				Graphic graphic = new Graphic(currentPoint, pms);
				uid_point = newEleLayer.addGraphic(graphic);
				sld.savePoint(currentPoint,jtype,uid_point);
			}

			if (fristPoint == null)
			{
				fristPoint = currentPoint;
			}
			else
			{
				if(!fristPoint.equals(currentPoint)){
					Line l = new Line();
					l.setStart(fristPoint);
					l.setEnd(currentPoint);

					Polyline polyline = new Polyline();
					polyline.addSegment(l, false);
					lineSysb = new SimpleLineSymbol(/*Color.RED*/DoAction.getGDColorByCQ(context,gdcq), DoAction.getGDWidthByCQ(context, gdcq), SimpleLineSymbol.STYLE.SOLID);
					Graphic tempGraphic = new Graphic(polyline, lineSysb);
					uid_segment = newgdlayer.addGraphic(tempGraphic);
					uid_seg_list.add(uid_segment);
					fristPoint = currentPoint;
				}
			}
			return true;
		}
		else
		{
			/**
			 * 划线测距功能
			 */
			if (geoType != null && geoType != Geometry.Type.POLYGON)
			{

				Point currentPoint = mapView.toMapPoint(new Point(point.getX(), point.getY()));
				points.add(currentPoint);
				Graphic graphic = new Graphic(currentPoint, markerSymbol);
				layer.addGraphic(graphic);


				if (fristPoint == null)
				{
					fristPoint = currentPoint;
				}
				else
				{
					Line l = new Line();
					l.setStart(fristPoint);
					l.setEnd(currentPoint);

					Polyline polyline = new Polyline();
					Point startPoint;
					Point endPoint;
					for (int i = 1; i < points.size(); i++)
					{
						startPoint = points.get(i - 1);
						endPoint = points.get(i);

						Line line = new Line();
						line.setStart(startPoint);
						line.setEnd(endPoint);

						polyline.addSegment(line, false);
					}
					Graphic tempGraphic = new Graphic(polyline, lineSymbol);
					layer.addGraphic(tempGraphic);

					String lenth = Double.toString(Math.round(l.calculateLength2D())) + "米";
					Toast.makeText(context, lenth, Toast.LENGTH_SHORT).show();
					fristPoint = currentPoint;
				}
			}/*else*/ if (guangji != null) 
			{
				long[] featureIDs = guangji.getFeatureIDs(point.getX(), point.getY(), 30);
				if (featureIDs.length > 0)
				{
					// Toast.makeText(context, "查出光机信息"+featureIDs[0],
					// Toast.LENGTH_SHORT).show();
					// 获取工程附图路径
					Intent intentAuthority = new Intent(context, Dialog_Range.class);
					intentAuthority.putExtra("tt_id", featureIDs[0] + "");
					context.startActivity(intentAuthority);
				}

			}
			/*else */if(newEleLayer != null ){
				SelectOneGraphic(point.getX(),point.getY(),newEleLayer);
			}
			/*else*/ if(newgdlayer != null && isnew){
				SelectOneGraphic(point.getX(), point.getY(), newgdlayer);
			}
			if(newGLLayer != null && showglly){
				showglly = false;
				SelectOneGraphic(point.getX(), point.getY(), newGLLayer);
			}

			boolean is = true;
			if (featureLayers != null)
			{ // 展示属性信息
				for (int i = 0; i < featureLayers.size(); i++)
				{
					FeatureLayer featureLayer = featureLayers.get(i);
					long[] featureIDs = featureLayer.getFeatureIDs(point.getX(), point.getY(), 20);
					featureLayer.clearSelection();
					if (featureIDs.length > 0)
					{
						featureLayer.selectFeatures(featureIDs, true);
						List<Feature> selectedFeatures = featureLayer.getSelectedFeatures();
						for (int j = 0; j < selectedFeatures.size(); j++)
						{
							Feature feature = selectedFeatures.get(j);
							Map<String, Object> attributes = feature.getAttributes();
							Set<String> keySet = attributes.keySet();
							if (attributes != null && attributes.size() > 0)
							{
								String nodeid = attributes.get("OBJECT_USE").toString();

								DBOpterate dbo = DBOpterate.getDbOpterate(context);
								String[] selectionArgs = {"tt_id","tt_name","refname","astext(Geometry)","cadtype","nodename","funtype","state","areaid","userlocati"};
								List<Data> list = dbo.searchPointByID(nodeid, selectionArgs);
								DataShowDialog.showDialog_List(list, "节点属性信息", context);
								is = false;

							}
						}
					}
				} 
			}
			if (guandao != null && is)
			{
				long[] featureIDs ;
				if(geoType == Geometry.Type.POLYGON)
					featureIDs = guandao.getFeatureIDs(point.getX(), point.getY(), 50);
				else
					featureIDs = guandao.getFeatureIDs(point.getX(), point.getY(), 30);
				guandao.clearSelection();
				if (featureIDs.length > 0)
				{
					guandao.selectFeatures(featureIDs, true);
					if(geoType == Geometry.Type.POLYGON && featureIDs.length == 2)
					{
						String[] gdIDS = new String[2];
						List<Feature> selectedFeatures = guandao.getSelectedFeatures();
						for(int i=0;i<selectedFeatures.size();i++){
							Feature feature = selectedFeatures.get(i);
							Map<String,Object> attributes = feature.getAttributes();
							if(attributes != null && attributes.size()>0)
								gdIDS[i] = attributes.get("SEGID").toString();
						}
						Writer writer = new StringWriter();
						try {
							XMLToServer.SendToServer(gdIDS, writer, context);
						} catch (Throwable e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}else if(geoType == Geometry.Type.POLYGON && featureIDs.length > 2){
						String[] gdNames = new String[featureIDs.length];
						String[] gdIDs = new String[featureIDs.length];
						List<Feature> selectedFeatures = guandao.getSelectedFeatures();
						for(int i=0;i<selectedFeatures.size();i++){
							Feature feature = selectedFeatures.get(i);
							Map<String,Object> attributes = feature.getAttributes();
							Set<String> keySet = attributes.keySet();
							if(attributes != null && attributes.size()>0){
								String gdid = attributes.get("SEGID").toString();
								DataCollection coll = new DataCollection();
								coll.add(new Data("id", gdid));
								DataTable dt = db.executeTable("spGuanDaoInfo",coll,"GBK");
								if(dt!=null && dt.size()>0){
									DataCollection first = dt.getFirst();
									startingPointName = first.get("StartingPointName").getValue().toString();
									endPointName = first.get("EndPointName").getValue().toString();
									gdNames[i] = startingPointName +"-"+endPointName+"("+gdid+")";
									gdIDs[i] = gdid;
								}
							}

						}
						DataShowDialog.gdIDs = gdIDs;
						DataShowDialog.showDialog_StringArray(gdNames, "光纤熔接选择", context);
					}else{
						List<Feature> selectedFeatures = guandao.getSelectedFeatures();
						for (int j = 0; j < selectedFeatures.size(); j++)
						{
							Feature feature = selectedFeatures.get(j);
							Map<String, Object> attributes = feature.getAttributes();
							Set<String> keySet = attributes.keySet();

							if (attributes != null && attributes.size() > 0)
							{
								final String nodeid = attributes.get("SEGID").toString();
								DataCollection collection = new DataCollection();
								collection.add(new Data("id", nodeid));
								DataTable executeTable = db.executeTable("spGuanDaoInfo", collection, "GBK");
								if (executeTable != null && executeTable.size() > 0)
								{
									DataCollection first = executeTable.getFirst();
									startingPointName = first.get("StartingPointName").getValue().toString();
									endPointName = first.get("EndPointName").getValue().toString();
									final String[] itemList = {"起点:" + startingPointName,"终点:" + endPointName};
									final Button openTubeViewBtn = new Button(context);

									openTubeViewBtn.setText("打开管道截面展开图");
									openTubeViewBtn.setOnClickListener(new OnClickListener()
									{
										private String tubePadId = nodeid;

										@Override
										public void onClick(View v)
										{
											Float id = Float.parseFloat(tubePadId);
											Log.d("debug", id.toString());

											Long tubeId = id.longValue();
											Log.d("debug", tubeId.toString());


											Intent tubeViewIntent = new Intent(context, TubeViewActivity.class);
											tubeViewIntent.putExtra(context.getString(R.string.intent_extra_key_tube_id), tubeId);
											context.startActivity(tubeViewIntent);
										}
									});
									//							showAlertDialog(itemList);
									new AlertDialog.Builder(context)
									.setIcon(R.drawable.logo)
									.setTitle("管道属性信息")
									.setItems(itemList,null)
									.setPositiveButton("确定", null)
									//覃远逸做的，暂时屏蔽
									/*.setNegativeButton("管道界面图",new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											// TODO 自动生成的方法存根
											String tubePadId = nodeid;
											Float id = Float.parseFloat(tubePadId);
											Log.d("debug", id.toString());

											Long tubeId = id.longValue();
											Log.d("debug", tubeId.toString());


											Intent tubeViewIntent = new Intent(context, TubeViewActivity.class);
											tubeViewIntent.putExtra(context.getString(R.string.intent_extra_key_tube_id), tubeId);
											context.startActivity(tubeViewIntent);
										}
									})*/
									.show();

								}
							}
						}
					}

					is = false;
				}
			}
			if (gisgj2 != null && is){
				long[] featureIDs ;
				gisgj2.clearSelection();
				// 获取该图层中，point点 100 误差范围内的所有id
				featureIDs = gisgj2.getFeatureIDs(point.getX(), point.getY(), 100);
				if(featureIDs.length > 0){
					//选择获取到的所有ID
					gisgj2.selectFeatures(featureIDs, true);
					//通过ID 获取到所有的feature
					List<Feature> selectFeatures  = gisgj2.getSelectedFeatures();
					//遍历所有的feature
					for(int i=0;i<selectFeatures.size();i++){
						Feature tempFea = selectFeatures.get(i);
						//获取所有feature中的键值对，即GEO数据中的列与值
						Map<String, Object> attributes = tempFea.getAttributes();
						Set<String> keySet = attributes.keySet();
						if(attributes!=null && attributes.size()>0){
							List<Data> gisinfo = new ArrayList<Data>();
							//获取相应的值，进行显示
							String a = attributes.get("光机编号")!=null?attributes.get("光机编号").toString():"";
							String b = attributes.get("所带区域")!=null?attributes.get("所带区域").toString():"";
							String c = attributes.get("光机类型")!=null?attributes.get("光机类型").toString():"";
							String d = attributes.get("ups位置")!=null?attributes.get("ups位置").toString():"";
							String e = attributes.get("网络类型")!=null?attributes.get("网络类型").toString():"";
							gisinfo.add(new Data("光机编号", a));
							gisinfo.add(new Data("所带区域", b));
							gisinfo.add(new Data("光机类型", c));
							gisinfo.add(new Data("ups位置", d));
							gisinfo.add(new Data("网络类型", e));
							Point tempp = null;
							if(tempFea.getGeometry().getType() == Type.POINT)	
								tempp = (Point)tempFea.getGeometry();
							gisinfo.add(new Data("X", String.valueOf(tempp.getX())));
							gisinfo.add(new Data("Y", String.valueOf(tempp.getY())));
							//信息显示
							DataShowDialog.showDialog_List(gisinfo, "光机属性信息", context);
						}
					}
					is = false;
				}
			}
			if (loufang != null && is){
				long[] featureIDs ;
				loufang.clearSelection();
				featureIDs = loufang.getFeatureIDs(point.getX(), point.getY(), 20);
				if(featureIDs.length > 0){
					loufang.selectFeatures(featureIDs, true);
					List<Feature> selectFeatures  = loufang.getSelectedFeatures();
					for(int i=0;i<selectFeatures.size();i++){
						Feature tempFea = selectFeatures.get(i);
						Map<String, Object> attributes = tempFea.getAttributes();
						Set<String> keySet = attributes.keySet();
						if(attributes!=null && attributes.size()>0){
							List<Data> gisinfo = new ArrayList<Data>();
							String a = attributes.get("安装位置1")!=null?attributes.get("安装位置1").toString():"";
							String b = attributes.get("安装位置2")!=null?attributes.get("安装位置2").toString():"";
							gisinfo.add(new Data("路牌号", a));
							gisinfo.add(new Data("楼   号", b));
							Point tempp = null;
							if(tempFea.getGeometry().getType() == Type.POINT)	
								tempp = (Point)tempFea.getGeometry();
							gisinfo.add(new Data("X", String.valueOf(tempp.getX())));
							gisinfo.add(new Data("Y", String.valueOf(tempp.getY())));
							DataShowDialog.showDialog_List(gisinfo, "楼放属性信息", context);
						}
					}
					is = false;
				}
			}
			if(daolu != null && is){
				long[] featureIDs ;
				daolu.clearSelection();
				featureIDs = daolu.getFeatureIDs(point.getX(), point.getY(), 20);
				if(featureIDs.length > 0){
					daolu.selectFeatures(featureIDs, true);
					List<Feature> selectFeatures  = daolu.getSelectedFeatures();
					for(int i=0;i<selectFeatures.size();i++){
						Feature tempFea = selectFeatures.get(i);
						Map<String, Object> attributes = tempFea.getAttributes();
						Set<String> keySet = attributes.keySet();
						if(attributes!=null && attributes.size()>0){
							List<Data> gisinfo = new ArrayList<Data>();
							String a = attributes.get("道路名称")!=null?attributes.get("道路名称").toString():"";
							gisinfo.add(new Data("道 路 名 称", a));
							if(tempFea.getGeometry().getType() == Type.POINT) {
							}
							/*gisinfo.add(new Data("X", String.valueOf(tempp.getX())));
							gisinfo.add(new Data("Y", String.valueOf(tempp.getY())));*/
							DataShowDialog.showDialog_List(gisinfo, "道路属性信息", context);
						}
					}
					is = false;
				}
			}
			if(lupaihao != null && is){
				long[] featureIDs ;
				lupaihao.clearSelection();
				featureIDs = lupaihao.getFeatureIDs(point.getX(), point.getY(), 20);
				if(featureIDs.length > 0){
					lupaihao.selectFeatures(featureIDs, true);
					List<Feature> selectFeatures  = lupaihao.getSelectedFeatures();
					for(int i=0;i<selectFeatures.size();i++){
						Feature tempFea = selectFeatures.get(i);
						Map<String, Object> attributes = tempFea.getAttributes();
						Set<String> keySet = attributes.keySet();
						if(attributes!=null && attributes.size()>0){
							List<Data> gisinfo = new ArrayList<Data>();
							String a = attributes.get("路牌号名称")!=null?attributes.get("路牌号名称").toString():"";
							String b = attributes.get("站点名称")!=null?attributes.get("站点名称").toString():"";
							gisinfo.add(new Data("站 点 名 称",b));
							gisinfo.add(new Data("路牌号名称", a));
							if(tempFea.getGeometry().getType() == Type.POINT) {
							}
							/*gisinfo.add(new Data("X", String.valueOf(tempp.getX())));
							gisinfo.add(new Data("Y", String.valueOf(tempp.getY())));*/
							DataShowDialog.showDialog_List(gisinfo, "路牌号属性信息", context);
						}
					}
					is = false;
				}
			}
			if(fenzhiqi != null && is){
				long[] featureIDs ;
				fenzhiqi.clearSelection();
				featureIDs = fenzhiqi.getFeatureIDs(point.getX(), point.getY(), 20);
				if(featureIDs.length > 0){
					fenzhiqi.selectFeatures(featureIDs, true);
					List<Feature> selectFeatures  = fenzhiqi.getSelectedFeatures();
					for(int i=0;i<selectFeatures.size();i++){
						Feature tempFea = selectFeatures.get(i);
						Map<String, Object> attributes = tempFea.getAttributes();
						Set<String> keySet = attributes.keySet();
						if(attributes!=null && attributes.size()>0){
							List<Data> gisinfo = new ArrayList<Data>();
							String a = attributes.get("所在道路")!=null?attributes.get("所在道路").toString():"";
							String b = attributes.get("地址")!=null?attributes.get("地址").toString():"";
							String c = attributes.get("型号")!=null?attributes.get("型号").toString():"";
							String d = attributes.get("ID")!=null?attributes.get("ID").toString():"";
							gisinfo.add(new Data("ID", d));
							gisinfo.add(new Data("所在道路",a));
							gisinfo.add(new Data("地	       址", b));
							gisinfo.add(new Data("型       号", c));
							if(tempFea.getGeometry().getType() == Type.POINT) {
							}
							/*gisinfo.add(new Data("X", String.valueOf(tempp.getX())));
							gisinfo.add(new Data("Y", String.valueOf(tempp.getY())));*/
							DataShowDialog.showDialog_List(gisinfo, "分支器属性信息", context);
						}
					}
					is = false;
				}
			}
			if(zhuanxiangl != null && is){
				long[] featureIDs ;
				zhuanxiangl.clearSelection();
				featureIDs = zhuanxiangl.getFeatureIDs(point.getX(), point.getY(), 20);
				if(featureIDs.length > 0){
					zhuanxiangl.selectFeatures(featureIDs, true);
					List<Feature> selectFeatures  = zhuanxiangl.getSelectedFeatures();
					for(int i=0;i<selectFeatures.size();i++){
						Feature tempFea = selectFeatures.get(i);
						Map<String, Object> attributes = tempFea.getAttributes();
						Set<String> keySet = attributes.keySet();
						if(attributes!=null && attributes.size()>0){
							List<Data> gisinfo = new ArrayList<Data>();
							String a = attributes.get("起点")!=null?attributes.get("起点").toString():"";
							String b = attributes.get("终点")!=null?attributes.get("终点").toString():"";
							String c = attributes.get("芯数")!=null?attributes.get("芯数").toString():"";
							String d = attributes.get("长度")!=null?attributes.get("长度").toString():"";
							gisinfo.add(new Data("起点", a));
							gisinfo.add(new Data("终点",b));
							gisinfo.add(new Data("芯数", c));
							gisinfo.add(new Data("长度", d));
							if(tempFea.getGeometry().getType() == Type.POINT) {
							}
							/*gisinfo.add(new Data("X", String.valueOf(tempp.getX())));
							gisinfo.add(new Data("Y", String.valueOf(tempp.getY())));*/
							DataShowDialog.showDialog_List(gisinfo, "专网光缆属性信息", context);
						}
					}
					is = false;
				}
			}
			if(zhixiangl != null && is){
				long[] featureIDs ;
				zhixiangl.clearSelection();
				featureIDs = zhixiangl.getFeatureIDs(point.getX(), point.getY(), 20);
				if(featureIDs.length > 0){
					zhixiangl.selectFeatures(featureIDs, true);
					List<Feature> selectFeatures  = zhixiangl.getSelectedFeatures();
					for(int i=0;i<selectFeatures.size();i++){
						Feature tempFea = selectFeatures.get(i);
						Map<String, Object> attributes = tempFea.getAttributes();
						Set<String> keySet = attributes.keySet();
						if(attributes!=null && attributes.size()>0){
							List<Data> gisinfo = new ArrayList<Data>();
							String a = attributes.get("起点")!=null?attributes.get("起点").toString():"";
							String b = attributes.get("终点")!=null?attributes.get("终点").toString():"";
							String c = attributes.get("芯数")!=null?attributes.get("芯数").toString():"";
							String d = attributes.get("长度")!=null?attributes.get("长度").toString():"";
							String e = attributes.get("用途")!=null?attributes.get("用途").toString():"";
							String f = attributes.get("穿缆方式")!=null?attributes.get("穿缆方式").toString():"";
							gisinfo.add(new Data("起       点", a));
							gisinfo.add(new Data("终       点",b));
							gisinfo.add(new Data("芯       数", c));
							gisinfo.add(new Data("长	       度", d));
							gisinfo.add(new Data("用	       途", e));
							gisinfo.add(new Data("穿缆方式", f));
							if(tempFea.getGeometry().getType() == Type.POINT) {
							}
							/*gisinfo.add(new Data("X", String.valueOf(tempp.getX())));
							gisinfo.add(new Data("Y", String.valueOf(tempp.getY())));*/
							DataShowDialog.showDialog_List(gisinfo, "支线光缆属性信息", context);
						}
					}
					is = false;
				}
			}
			if(gugangl != null && is){
				long[] featureIDs ;
				gugangl.clearSelection();
				featureIDs = gugangl.getFeatureIDs(point.getX(), point.getY(), 20);
				if(featureIDs.length > 0){
					gugangl.selectFeatures(featureIDs, true);
					List<Feature> selectFeatures  = gugangl.getSelectedFeatures();
					for(int i=0;i<selectFeatures.size();i++){
						Feature tempFea = selectFeatures.get(i);
						Map<String, Object> attributes = tempFea.getAttributes();
						Set<String> keySet = attributes.keySet();
						if(attributes!=null && attributes.size()>0){
							List<Data> gisinfo = new ArrayList<Data>();
							String a = attributes.get("起点")!=null?attributes.get("起点").toString():"";
							String b = attributes.get("终点")!=null?attributes.get("终点").toString():"";
							String c = attributes.get("芯数")!=null?attributes.get("芯数").toString():"";
							String d = attributes.get("长度")!=null?attributes.get("长度").toString():"";
							gisinfo.add(new Data("起点", a));
							gisinfo.add(new Data("终点",b));
							gisinfo.add(new Data("芯数", c));
							gisinfo.add(new Data("长度", d));
							if(tempFea.getGeometry().getType() == Type.POINT) {
							}
							/*gisinfo.add(new Data("X", String.valueOf(tempp.getX())));
							gisinfo.add(new Data("Y", String.valueOf(tempp.getY())));*/
							DataShowDialog.showDialog_List(gisinfo, "骨干光缆属性信息", context);
						}
					}
					is = false;
				}
			}
			
			return true;
		}

	}

	@Override
	public boolean onDoubleTap(MotionEvent point) {
		// TODO 自动生成的方法存根
		if(drawglly){
			//保存光缆路由数据
			drawglly = false;
			DoAction.saveGLLY(context, points);
			fristPoint = null;
			points.clear();
			newglly.removeAll();
			loadGLLY();
			return false;
		}
		if(drawdlly){
			//保存电缆路由数据
			drawdlly = false;
			DoAction.saveDLLY(context, points);
			fristPoint = null;
			points.clear();
			newdlly.removeAll();
			loadDLLY();
			return false;
		}
		if(addGL){
			addGL = false;
			/*Polyline polyline = new Polyline();
			polyline.startPath(points.get(0));
			for(int i=0;i<points.size();i++){
				polyline.lineTo(points.get(i));
			}
			lineSysb = new SimpleLineSymbol(Color.GREEN, 1, SimpleLineSymbol.STYLE.SOLID);
			Graphic tempGraphic = new Graphic(polyline, lineSysb);
			int uid = newgdlayernewGLLayer.addGraphic(tempGraphic);*/
			sld.savaGL(points,gltype);
			fristPoint = null;
			points.clear();
			Toast.makeText(context, "光缆添加成功！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(addEle){
			addEle = false;
			fristPoint = null;

			for(int i=0;i<points.size()-1;i++){
				//				AddSegmentToLocalSQLite(points.get(i), points.get(i+1));
				sld.saveSegment(points.get(i), points.get(i+1),gdcq,uid_seg_list.get(i));
			}
			points.clear();
			return false;
		}
		else{
			mapView.zoomin();
			return false;
		}

	}

	float startX = 0;
	float startY = 0;
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			startX = event.getX();
			startY = event.getY();
		}

		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			float x = event.getX();
			float y = event.getY();
			float endX = x - startX;
			float endY = y - startY;

			x = Math.abs(endX);
			y = Math.abs(endY);

			if (x > 10 || y > 10)
			{
				startX = 0;
				startY = 0;
				click.onMoveAndZoom();
			}
		}
		return super.onTouch(v, event);
	}

	/****************** OnZoomListener回调事件 ***************/
	@Override
	public void postAction(float arg0, float arg1, double arg2)
	{
		// 记录缩放操作，用来实现前进后退
		click.onMoveAndZoom();
	}

	@Override
	public void preAction(float arg0, float arg1, double arg2)
	{

	}

	@Override
	public void onLongPress(MotionEvent event)
	{
		DataTable result = null;
		super.onLongPress(event);
		boolean next = true;
		Graphic del = GetOneGraphicFromLayer(event.getX(),event.getY(),newEleLayer);
		if(del == null)
			del = GetOneGraphicFromLayer(event.getX(),event.getY(),newgdlayer);
		if(del != null){
			next = false;
			// Get the graphic type and do different things by the type
			Geometry gy = del.getGeometry();
			Type ty = gy.getType();
			del.getSymbol();
			if(ty == Type.POINT){
				Point pt = (Point)gy;
				result = DoAction.getPointInfoByXY(context,pt);
				//				result = DoAction.getPointInfoByUID(context, String.valueOf(del.getId()));
				for(int i=0;i<result.size();i++){
					DataCollection dc = result.next();
					//search the gjid in gdtable , if find the gj is not to be delete,should delete the linked gd first!
					DataTable rst = DoAction.getSegmentInfoByPID(context, dc.get("gjid").Value.toString());
					// show dialog 
					showDelDialog(newEleLayer,del.getId(),Integer.valueOf(dc.get("gjid").Value.toString()),rst,ty);
				}
			}else if(ty == Type.POLYLINE){
				Polyline pl = (Polyline) gy;
				ArrayList<Point> alpl = new ArrayList<Point>();
				for(int i=0;i<pl.getPointCount();i++){
					alpl.add(pl.getPoint(i));
				}
				result = DoAction.getSegmentInfoByPts(context,alpl);
				//				result = DoAction.getSegmentInfoByUID(context, String.valueOf(del.getId()));
				for(int i=0;i<result.size();i++){
					DataCollection dc = result.next();
					DataTable rst = DoAction.ChargeSegIsinGD2GL(context,dc.get("gdid").Value.toString());
					// show dialog 
					showDelDialog(newgdlayer,del.getId(),Integer.valueOf(dc.get("gdid").Value.toString()),rst,ty);
				}
			}
		}
		if(del == null)
			del = GetOneGraphicFromLayer(event.getX(),event.getY(),newglly);
		if(del != null && next){
			next = false;
			final Graphic temp = del;
			AlertDialog alertDialog = new AlertDialog.Builder(context)
			.setIcon(R.drawable.logo)
			.setTitle("删除光缆路由")
			.setMessage("你确定要删除该条光缆路由吗？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() { 

				@Override 
				public void onClick(DialogInterface dialog, int which) {
					Geometry gt = temp.getGeometry();
					if(gt.getType() == Type.POLYLINE){
						Polyline  line = (Polyline)gt;
						ArrayList<Point> gllist = new ArrayList<Point>();
						for(int i=0;i<line.getPointCount();i++){
							gllist.add(line.getPoint(i));
						}
						//组合节点字符串
						StringBuffer sb = new StringBuffer();
						for(int i=0;i<gllist.size();i++){
							sb.append(gllist.get(i).getX()+","+gllist.get(i).getY());
							if(i != gllist.size()-1)
								sb.append("/");
						}
						DoAction.removeDLLY(context, sb.toString());
						//从图层中移除符号
						newglly.removeGraphic(temp.getUid());
					}
				} 
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() { 

				@Override 
				public void onClick(DialogInterface dialog, int which) { 
					// TODO Auto-generated method stub  
					return;
				} 
			})
			.create();
			Window window = alertDialog.getWindow();        
			WindowManager.LayoutParams lp = window.getAttributes();        
			// 设置透明度为0.3         
			lp.alpha = 0.6f;   
			window.setAttributes(lp);        
			alertDialog.show();

		}
		if(del == null)
			del = GetOneGraphicFromLayer(event.getX(),event.getY(),newdlly);
		if(del != null && next){
			final Graphic temp = del;
			AlertDialog alertDialog = new AlertDialog.Builder(context)
			.setIcon(R.drawable.logo)
			.setTitle("删除电缆路由")
			.setMessage("你确定要删除该条电缆路由吗？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() { 

				@Override 
				public void onClick(DialogInterface dialog, int which) {
					Geometry gt = temp.getGeometry();
					if(gt.getType() == Type.POLYLINE){
						Polyline  line = (Polyline)gt;
						ArrayList<Point> gllist = new ArrayList<Point>();
						for(int i=0;i<line.getPointCount();i++){
							gllist.add(line.getPoint(i));
						}
						//组合节点字符串
						StringBuffer sb = new StringBuffer();
						for(int i=0;i<gllist.size();i++){
							sb.append(gllist.get(i).getX()+","+gllist.get(i).getY());
							if(i != gllist.size()-1)
								sb.append("/");
						}
						DoAction.removeDLLY(context, sb.toString());
						//从图层中移除符号
						newdlly.removeGraphic(temp.getUid());
					}
				} 
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() { 

				@Override 
				public void onClick(DialogInterface dialog, int which) { 
					// TODO Auto-generated method stub  
					return;
				} 
			})
			.create();
			Window window = alertDialog.getWindow();        
			WindowManager.LayoutParams lp = window.getAttributes();        
			// 设置透明度为0.3         
			lp.alpha = 0.6f;   
			window.setAttributes(lp);        
			alertDialog.show();

		}


	}

	/**
	 *************************************** 
	 * 功 能：事件回调接口
	 *************************************** 
	 */
	public interface OnMapListener
	{
		/**
		 * 
		 * 功 能：放大和缩小监听回来记录前进后退
		 */
		public void onMoveAndZoom();
	}
	private void showAlertDialog( final String[] nList,String title) {
		ListAdapter mAdapter = new ArrayAdapter(context, R.layout.item, nList);
		LayoutInflater inflater = LayoutInflater.from(context);  
		View view = inflater.inflate(R.layout.alertdialog, null);  

		TextView titleView = (TextView)view.findViewById(R.id.titleView);
		//		String title = "管道属性信息";
		titleView.setText(title);
		TextPaint tpaint = titleView.getPaint();
		tpaint .setFakeBoldText(true);

		ListView listview = (ListView)view.findViewById(android.R.id.list);
		listview.setAdapter(mAdapter);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根

			}
		});
		listview.setOnItemClickListener( null );

		AlertDialog.Builder builder = new AlertDialog.Builder(context); 
		AlertDialog mAlertDialog = builder.create();
		mAlertDialog.show();
		mAlertDialog.getWindow().setContentView(view);
		//		mAlertDialog.getWindow().setLayout(600, 400);
	}
	private String searchNodeIDbyName(String name){
		String nodeid="";
		DataTable result = db.executeTable( "spNodeInfoByName", null);
		for( int i=0;i<result.size();i++){
			if(result.hasNext())
				nodeid = result.next().get("nodeid").Value.toString();
		}
		return nodeid;

	}
	private boolean SelectOneGraphic(float x, float y,GraphicsLayer llayer) {
		// 获得图层
		//         GraphicsLayer layer = GetGraphicLayer();
		if (llayer != null && llayer.isInitialized() && llayer.isVisible()) {
			Graphic result = null;
			// 检索当前 光标点（手指按压位置）的附近的 graphic对象
			result = GetGraphicsFromLayer(x, y, llayer);
			if (result != null) 
				return true;
		}
		return false;
	}
	/*
	 * 从一个图层里里 查找获得 Graphics对象. x,y是屏幕坐标,layer
	 * 是GraphicsLayer目标图层（要查找的）。相差的距离是50像素。
	 */
	private Graphic GetOneGraphicFromLayer(double xScreen, double yScreen,
			GraphicsLayer layer){
		Graphic result = null;
		try {
			int[] idsArr = layer.getGraphicIDs((float)xScreen,(float)yScreen,30);
			for (int i = 0; i < idsArr.length; i++) {
				Graphic gpVar = layer.getGraphic(idsArr[i]);

				result = gpVar;
			}
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	private Graphic GetGraphicsFromLayer(double xScreen, double yScreen,
			GraphicsLayer tlayer) {
		Graphic result = null;
		try {
			int[] idsArr = tlayer.getGraphicIDs((float)xScreen,(float)yScreen,30);
			if(idsArr.length > 0)
				isnew = false;
			for (int i = 0; i < idsArr.length; i++) {
				Graphic gpVar = tlayer.getGraphic(idsArr[i]);
				result = gpVar;

				if (gpVar != null) {
					Geometry gy = gpVar.getGeometry();
					Type ty = gy.getType();
					LocalDataModify ldm = new LocalDataModify(context);
					if(0 == ty.compareTo(Type.POINT)){
						Point pt = (Point)gpVar.getGeometry();
						oldPoint = pt;
						String a = String.valueOf(pt.getX())+","+String.valueOf(pt.getY());
						a += "/";
						if(!addGL && !addEle){
							ldm.modifyPoint(pt.getX(), pt.getY());
						}
					}else{
						Polyline pl1 = (Polyline) gpVar.getGeometry();
						pl1.getPathCount();
						ArrayList<Point> al = new ArrayList<Point>();
						for(int ii=0;ii<pl1.getPointCount();ii++){
							al.add(pl1.getPoint(ii));
						}
						if(showglly){
							glList = ldm.getGlPts(al,tlayer,layer);

							Polyline polyline = new Polyline();
							Point startPoint;
							Point endPoint;
							for (int m = 0; m < glList.size()-1; m++)
							{
								startPoint = glList.get(m);
								endPoint = glList.get(m+1);

								Line line = new Line();
								line.setStart(startPoint);
								line.setEnd(endPoint);

								polyline.addSegment(line, false);
							}
							Graphic tempGraphic = new Graphic(polyline, lineSymbol);
							layer.addGraphic(tempGraphic);
						}
						else
							ldm.modifySegment(al);
					}

				}
			}
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	private void showDelDialog(final GraphicsLayer layer,final long graphicid,final int pkid,final DataTable rst,final Type t){
		AlertDialog alertDialog = new AlertDialog.Builder(context)
		.setIcon(R.drawable.logo)
		.setTitle("删除元素")
		.setMessage("你确定要删除该元素吗？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() { 

			@Override 
			public void onClick(DialogInterface dialog, int which) { 
				// TODO Auto-generated method stub  
				if(rst != null && rst.size() > 0){
					if(t == Type.POINT)
						Toast.makeText(context, "请先删除关联管道！", Toast.LENGTH_SHORT).show();
					else if(t == Type.POLYLINE)
						Toast.makeText(context, "请先移除管道内的光缆！", Toast.LENGTH_SHORT).show();
					return;
				}
				//delete the graphic on the layer
				layer.removeGraphic((int) graphicid);
				//delete the data in the SQL table
				if(t == Type.POINT)
					DoAction.DeletePointByID(context, String.valueOf(pkid));
				else if(t == Type.POLYLINE)
					DoAction.DeleteSegmentByID(context, String.valueOf(pkid));
			} 
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() { 

			@Override 
			public void onClick(DialogInterface dialog, int which) { 
				// TODO Auto-generated method stub  
				return;
			} 
		})
		.create();
		Window window = alertDialog.getWindow();        
		WindowManager.LayoutParams lp = window.getAttributes();        
		// 设置透明度为0.3         
		lp.alpha = 0.6f;   
		window.setAttributes(lp);        
		alertDialog.show();
	}
	void AlertMsg(String str, Object... arg) {
		String msg = String.format(str, arg);
		Toast.makeText(context, msg, 2).show();
		Log.i("AlertMsg", msg);
	}
	public void loadGLLY(){
		Graphic tempGraphic ;
		db = new SQLiteDatabase(context);
		DataCollection params = new DataCollection();
		params.add(new Data("uname",FunctionHelper.userName));
		DataTable rst = db.executeTable("spBS_LocalGLLYQueryByUName", params);
		for(int i=0;i<rst.size();i++){
			Polyline pl = new Polyline();
			DataCollection dc = rst.next();

			String pts = dc.get("points").Value.toString();
			ArrayList<Point> points = LocalDataModify.splitePts(pts);
			for(int j=0;j<points.size();j++){
				/*SimpleMarkerSymbol msb = new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
				Graphic graphic = new Graphic(points.get(j), msb);
				newglly.addGraphic(graphic);*/
				if(j == 0)
					pl.startPath(points.get(j));
				else
					pl.lineTo(points.get(j));
			}


			tempGraphic = new Graphic(pl, new SimpleLineSymbol(Color.DKGRAY, 1, SimpleLineSymbol.STYLE.SOLID));

			newglly.addGraphic(tempGraphic);
		}
	}
	public void loadDLLY(){
		Graphic tempGraphic ;
		db = new SQLiteDatabase(context);
		DataCollection params = new DataCollection();
		params.add(new Data("uname",FunctionHelper.userName));
		DataTable rst = db.executeTable("spBS_LocalDLLYQueryByUName", params);
		for(int i=0;i<rst.size();i++){
			Polyline pl = new Polyline();
			DataCollection dc = rst.next();

			String pts = dc.get("points").Value.toString();
			ArrayList<Point> points = LocalDataModify.splitePts(pts);
			for(int j=0;j<points.size();j++){
				/*SimpleMarkerSymbol msb = new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
				Graphic graphic = new Graphic(points.get(j), msb);
				newdlly.addGraphic(graphic);*/
				if(j == 0)
					pl.startPath(points.get(j));
				else
					pl.lineTo(points.get(j));
			}


			tempGraphic = new Graphic(pl, new SimpleLineSymbol(Color.LTGRAY, 1, SimpleLineSymbol.STYLE.SOLID));

			newdlly.addGraphic(tempGraphic);
		}
	}
}
