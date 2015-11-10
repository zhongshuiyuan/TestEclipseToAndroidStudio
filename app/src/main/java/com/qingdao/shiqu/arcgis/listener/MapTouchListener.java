package com.qingdao.shiqu.arcgis.listener;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.esri.core.geometry.GeometryEngine;
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
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.activity.Dialog_Range;
import com.qingdao.shiqu.arcgis.dialog.DataShowDialog;
import com.qingdao.shiqu.arcgis.helper.FunctionHelper;
import com.qingdao.shiqu.arcgis.mode.SimpleSymbolTemplate;
import com.qingdao.shiqu.arcgis.sqlite.DatabaseOpenHelper;
import com.qingdao.shiqu.arcgis.sqlite.DoAction;
import com.qingdao.shiqu.arcgis.utils.DBOpterate;
import com.qingdao.shiqu.arcgis.utils.LocalDataModify;
import com.qingdao.shiqu.arcgis.utils.LocalDataAction;
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
	private final String LOG_TAG = this.getClass().getSimpleName();

	List<Point> points;
	Context mContext;
	MapView mMapView;
	SimpleLineSymbol lineSymbol,lineSysb;
	SimpleMarkerSymbol markerSymbol;
	SimpleFillSymbol fillSymbol;
	Point fristPoint;
	/** 用于绘制的临时图层 **/
	GraphicsLayer mTempDrawingLayer;
	Geometry.Type geoType;
	/** 用于标注的临时图层 **/
	GraphicsLayer mTempMarkingLayer;
	OnMapListener click;
	List<FeatureLayer> featureLayers;
	FeatureLayer guandao ;
	FeatureLayer guangji,guanglan;
	SQLiteDatabase db = null;
	String startingPointName,endPointName;
	Polygon tempPolygon = null;//记录绘制过程中的多边形  
	boolean isrj = false;
	boolean addNode = false;
	/** 用于临时绘制 **/
	GraphicsLayer newNodeLayer;
	GraphicsLayer newgdlayer;
	GraphicsLayer newGLLayer;
	GraphicsLayer newglly;
	GraphicsLayer newdlly;
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
	FeatureLayer gisgj2,loufang,daolu,lupaihao,fenzhiqi;
	FeatureLayer zhuanxiangl,zhixiangl,gugangl;

	//Qinyy 新增
	private int mMapTouchListenerState;
	private android.database.sqlite.SQLiteDatabase mSQLiteDatabase;
	//Qinyy 新增完毕

	TextView mTvCoordinate;
	public void setTvCoordinate(TextView tvCoordinate) {
		mTvCoordinate = tvCoordinate;
	}

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

	public GraphicsLayer getNewNodeLayer() {
		return newNodeLayer;
	}

	public void setNewNodeLayer(GraphicsLayer newNodeLayer) {
		this.newNodeLayer = newNodeLayer;
	}

	public boolean isAddNode() {
		return addNode;
	}

	public void setAddNode(boolean addNode) {
		this.addNode = addNode;
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
		this.mContext = context;
		db = new SQLiteDatabase(context);
		this.mMapView = mapView;
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

		DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(mContext);
		mSQLiteDatabase = databaseOpenHelper.getWritableDatabase();
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
	 * 功 能：设置用于绘制和标注的临时图层
	 * 
	 * @param layer 用于绘制和标注的临时图层
	 */
	public void setTempDrawingLayer(GraphicsLayer layer)
	{
		this.mTempDrawingLayer = layer;
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

	public void setTempMarkingLayer(GraphicsLayer tempMarkingLayer) {
		mTempMarkingLayer = tempMarkingLayer;
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

	/**
	 * 地图单击事件
	 */
	@Override
	public boolean onSingleTap(MotionEvent event)
	{
		mTempMarkingLayer.removeAll();
		Drawable img = null;
		isnew = true;

		// 新增光缆路由，已弃用
		if(drawglly){
			// 光缆路由
			Point currentPoint  = null;
			if(isSelectedGraphic(event.getX(), event.getY(), newNodeLayer)){
				currentPoint = oldPoint;
				points.add(oldPoint);
			}
			else{
				currentPoint = mMapView.toMapPoint(new Point(event.getX(), event.getY()));
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

		// 新增电缆路由，已弃用
		if(drawdlly){
			// 电缆路由
			Point currentPoint  = null;
			if(isSelectedGraphic(event.getX(), event.getY(), newNodeLayer)){
				currentPoint = oldPoint;
				points.add(oldPoint);
			}
			else{
				currentPoint = mMapView.toMapPoint(new Point(event.getX(), event.getY()));
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

		// 新增光缆
		if(addGL){

			Point currentPoint  = null;
			if(isSelectedGraphic(event.getX(), event.getY(), newNodeLayer)){
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

		// 新增管道
		if(addNode){
			Point currentPoint  = null;
			if(isSelectedGraphic(event.getX(), event.getY(), newNodeLayer)){
				currentPoint = oldPoint;
				points.add(oldPoint);
			}
			else{
				currentPoint = mMapView.toMapPoint(new Point(event.getX(), event.getY()));
				points.add(currentPoint);
				//Get jing pictrue path
				String path = DoAction.getGJPathByName(mContext,jtype);
				//transition path to drawable 
				Bitmap bm = DoAction.TransPath2Bmp(path);
				BitmapDrawable bd = new BitmapDrawable(bm);
				img = bd;
				pms = new PictureMarkerSymbol(com.qingdao.shiqu.arcgis.utils.Utils.zoomDrawable(img, 40, 40));
				Graphic graphic = new Graphic(currentPoint, pms);
				uid_point = newNodeLayer.addGraphic(graphic);
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
					lineSysb = new SimpleLineSymbol(/*Color.RED*/DoAction.getGDColorByCQ(mContext,gdcq), DoAction.getGDWidthByCQ(mContext, gdcq), SimpleLineSymbol.STYLE.SOLID);
					Graphic tempGraphic = new Graphic(polyline, lineSysb);
					uid_segment = newgdlayer.addGraphic(tempGraphic);
					uid_seg_list.add(uid_segment);
					fristPoint = currentPoint;
				}
			}
			return true;
		}

		// 上面是某个人写的代码
		// ------- 不同人的分割线
		// 下面是另一个人写的代码
		// 什么，你问我？我是给你写注释的人
		// 划线测距功能和单击选中要素事件
		else {
			//划线测距功能
			if (geoType != null && geoType != Geometry.Type.POLYGON)
			{
				Point currentPoint = mMapView.toMapPoint(new Point(event.getX(), event.getY()));
				points.add(currentPoint);
				Graphic graphic = new Graphic(currentPoint, markerSymbol);
				mTempDrawingLayer.addGraphic(graphic);

				if (fristPoint == null)
				{
					fristPoint = currentPoint;
				} else	{
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
					mTempDrawingLayer.addGraphic(tempGraphic);

					String lenth = Double.toString(Math.round(l.calculateLength2D())) + "米";
					Toast.makeText(mContext, lenth, Toast.LENGTH_SHORT).show();
					fristPoint = currentPoint;
				}
			}

			// 点击光机范围图层显示工程附图
			if (guangji != null) {
				long[] featureIDs = guangji.getFeatureIDs(event.getX(), event.getY(), 30);
				if (featureIDs.length > 0)
				{
					// Toast.makeText(mContext, "查出光机信息"+featureIDs[0],
					// Toast.LENGTH_SHORT).show();
					// 获取工程附图路径
					Intent intentAuthority = new Intent(mContext, Dialog_Range.class);
					intentAuthority.putExtra("tt_id", featureIDs[0] + "");
					mContext.startActivity(intentAuthority);
				}

			}

			// TODO 修复好添加管井的功能之后再回来看看这些功能
			if(newNodeLayer != null ){
				isSelectedGraphic(event.getX(), event.getY(), newNodeLayer);
			}
			if(newgdlayer != null && isnew){
				isSelectedGraphic(event.getX(), event.getY(), newgdlayer);
			}
			if(newGLLayer != null && showglly){
				showglly = false;
				isSelectedGraphic(event.getX(), event.getY(), newGLLayer);
			}

			// 单击选中对象查看对象属性
			boolean isSelectedObject = false;
			// 展示属性信息，管井，单元
			if (featureLayers != null)
			{
				for (int i = 0; i < featureLayers.size(); i++)
				{
					FeatureLayer featureLayer = featureLayers.get(i);
					long[] featureIDs = featureLayer.getFeatureIDs(event.getX(), event.getY(), 20);
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

								DBOpterate dbo = DBOpterate.getDbOpterate(mContext);
								String[] selectionArgs = {"tt_id","tt_name","refname","astext(Geometry)","cadtype","nodename","funtype","state","areaid","userlocati"};
								List<Data> list = dbo.searchPointByID(nodeid, selectionArgs);
								DataShowDialog.showDialog_List(list, "节点属性信息", mContext);

								isSelectedObject = true;
							}
						}
					}
				} 
			}
			// 显示管道属性信息或者开启查看管道熔接信息
			if (guandao != null && !isSelectedObject) {
				long[] featureIDs ;
				if(geoType == Geometry.Type.POLYGON)
					featureIDs = guandao.getFeatureIDs(event.getX(), event.getY(), 50);
				else
					featureIDs = guandao.getFeatureIDs(event.getX(), event.getY(), 30);
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
							XMLToServer.SendToServer(gdIDS, writer, mContext);
						} catch (Throwable e) {
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
						DataShowDialog.showDialog_StringArray(gdNames, "光纤熔接选择", mContext);
					} else {
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
									final Button openTubeViewBtn = new Button(mContext);

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


											Intent tubeViewIntent = new Intent(mContext, TubeViewActivity.class);
											tubeViewIntent.putExtra(mContext.getString(R.string.intent_extra_key_tube_id), tubeId);
											mContext.startActivity(tubeViewIntent);
										}
									});
									//							showAlertDialog(itemList);
									new AlertDialog.Builder(mContext)
									.setIcon(R.drawable.logo)
									.setTitle("管道属性信息")
									.setItems(itemList,null)
									.setPositiveButton("确定", null)
									//覃远逸做的，暂时屏蔽
									/*.setNegativeButton("管道界面图",new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											String tubePadId = nodeid;
											Float id = Float.parseFloat(tubePadId);
											Log.d("debug", id.toString());

											Long tubeId = id.longValue();
											Log.d("debug", tubeId.toString());


											Intent tubeViewIntent = new Intent(mContext, TubeViewActivity.class);
											tubeViewIntent.putExtra(mContext.getString(R.string.intent_extra_key_tube_id), tubeId);
											mContext.startActivity(tubeViewIntent);
										}
									})*/
											.show();

								}
							}
						}
					}

					isSelectedObject = true;
				}
			}
			// 显示光机属性信息，大红圈
			if (gisgj2 != null && !isSelectedObject) {
				long[] featureIDs ;
				gisgj2.clearSelection();
				// 获取该图层中，point点 100 误差范围内的所有id
				featureIDs = gisgj2.getFeatureIDs(event.getX(), event.getY(), 100);
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
							DataShowDialog.showDialog_List(gisinfo, "光机属性信息", mContext);
						}
					}
					isSelectedObject = true;
				}
			}
			// 显示楼放属性信息
			if (loufang != null && !isSelectedObject) {
				long[] featureIDs ;
				loufang.clearSelection();
				featureIDs = loufang.getFeatureIDs(event.getX(), event.getY(), 20);
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
							DataShowDialog.showDialog_List(gisinfo, "楼放属性信息", mContext);
						}
					}
					isSelectedObject = true;
				}
			}
			// 显示道路属性信息
			if (daolu != null && !isSelectedObject) {
				long[] featureIDs ;
				daolu.clearSelection();
				featureIDs = daolu.getFeatureIDs(event.getX(), event.getY(), 20);
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
							DataShowDialog.showDialog_List(gisinfo, "道路属性信息", mContext);
						}
					}
					isSelectedObject = true;
				}
			}
			// 显示路牌号属性信息
			if (lupaihao != null && !isSelectedObject){
				long[] featureIDs ;
				lupaihao.clearSelection();
				featureIDs = lupaihao.getFeatureIDs(event.getX(), event.getY(), 20);
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
							DataShowDialog.showDialog_List(gisinfo, "路牌号属性信息", mContext);
						}
					}
					isSelectedObject = true;
				}
			}
			// 显示分支器属性信息
			if (fenzhiqi != null && !isSelectedObject) {
				long[] featureIDs ;
				fenzhiqi.clearSelection();
				featureIDs = fenzhiqi.getFeatureIDs(event.getX(), event.getY(), 20);
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
							DataShowDialog.showDialog_List(gisinfo, "分支器属性信息", mContext);
						}
					}
					isSelectedObject = true;
				}
			}
			// 显示专网光缆属性信息
			if (zhuanxiangl != null && !isSelectedObject) {
				long[] featureIDs ;
				zhuanxiangl.clearSelection();
				featureIDs = zhuanxiangl.getFeatureIDs(event.getX(), event.getY(), 20);
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
							DataShowDialog.showDialog_List(gisinfo, "专网光缆属性信息", mContext);
						}
					}
					isSelectedObject = true;
				}
			}
			//显示支线光缆属性信息
			if (zhixiangl != null && !isSelectedObject) {
				long[] featureIDs ;
				zhixiangl.clearSelection();
				featureIDs = zhixiangl.getFeatureIDs(event.getX(), event.getY(), 20);
				if(featureIDs.length > 0){
					zhixiangl.selectFeatures(featureIDs, true);
					List<Feature> selectFeatures  = zhixiangl.getSelectedFeatures();
					for(int i=0;i<selectFeatures.size();i++){
						Feature tempFea = selectFeatures.get(i);
						Map<String, Object> attributes = tempFea.getAttributes();
						Set<String> keySet = attributes.keySet();
						if(attributes != null && attributes.size()>0){
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
							DataShowDialog.showDialog_List(gisinfo, "支线光缆属性信息", mContext);
						}
					}
					isSelectedObject = true;
				}
			}
			// 显示骨干光缆属性信息
			if (gugangl != null && !isSelectedObject) {
				long[] featureIDs ;
				gugangl.clearSelection();
				featureIDs = gugangl.getFeatureIDs(event.getX(), event.getY(), 20);
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
							DataShowDialog.showDialog_List(gisinfo, "骨干光缆属性信息", mContext);
						}
					}
					isSelectedObject = true;
				}
			}

			// 选中本地新增（用户绘制）对象进行删除
			DataTable result = null;
			Graphic graphicToDelete;
			// 删除节点
			if (!isSelectedObject) {
				graphicToDelete = getGraphicFromLayer(event.getX(), event.getY(), newNodeLayer);
				if (graphicToDelete != null) {
					isSelectedObject = true;
				}
			}
			// 删除管道
			if (!isSelectedObject) {
				graphicToDelete = getGraphicFromLayer(event.getX(), event.getY(), newgdlayer);
				if(graphicToDelete != null) {
					isSelectedObject = true;
					// Get the graphicToDelete type and do different things by the type
					Geometry gy = graphicToDelete.getGeometry();
					Type ty = gy.getType();
					graphicToDelete.getSymbol();
					if (ty == Type.POINT) {
						Point pt = (Point)gy;
						result = DoAction.getPointInfoByXY(mContext,pt);
						//				result = DoAction.getPointInfoByUID(mContext, String.valueOf(graphicToDelete.getId()));
						for(int i=0;i<result.size();i++){
							DataCollection dc = result.next();
							//search the gjid in gdtable , if find the gj is not to be delete,should delete the linked gd first!
							DataTable rst = DoAction.getSegmentInfoByPID(mContext, dc.get("gjid").Value.toString());
							// show dialog
							showDelDialog(newNodeLayer,graphicToDelete.getId(),Integer.valueOf(dc.get("gjid").Value.toString()),rst,ty);
						}
					} else if (ty == Type.POLYLINE) {
						Polyline pl = (Polyline) gy;
						ArrayList<Point> alpl = new ArrayList<Point>();
						for(int i=0;i<pl.getPointCount();i++){
							alpl.add(pl.getPoint(i));
						}
						result = DoAction.getSegmentInfoByPts(mContext,alpl);
						//				result = DoAction.getSegmentInfoByUID(mContext, String.valueOf(graphicToDelete.getId()));
						for(int i=0;i<result.size();i++){
							DataCollection dc = result.next();
							DataTable rst = DoAction.ChargeSegIsinGD2GL(mContext,dc.get("gdid").Value.toString());
							// show dialog
							showDelDialog(newgdlayer,graphicToDelete.getId(),Integer.valueOf(dc.get("gdid").Value.toString()),rst,ty);
						}
					}
				}
			}
			// 删除光缆路由
			if (!isSelectedObject) {
				graphicToDelete = getGraphicFromLayer(event.getX(), event.getY(), newglly);
				if(graphicToDelete != null) {
					isSelectedObject = true;
					final Graphic temp = graphicToDelete;
					AlertDialog alertDialog = new AlertDialog.Builder(mContext)
							.setIcon(R.drawable.logo)
							.setTitle("删除光缆路由")
							.setMessage("你确定要删除该条光缆路由吗？")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Geometry geometry = temp.getGeometry();
									if(geometry.getType() == Type.POLYLINE) {
										// 从旧数据库移除数据(已弃用)
										Polyline polyline = (Polyline) geometry;
										ArrayList<Point> gllist = new ArrayList<>();
										for(int i=0;i<polyline.getPointCount();i++){
											gllist.add(polyline.getPoint(i));
										}
										//组合节点字符串
										StringBuffer sb = new StringBuffer();
										for(int i=0;i<gllist.size();i++){
											sb.append(gllist.get(i).getX() + "," + gllist.get(i).getY());
											if(i != gllist.size()-1)
												sb.append("/");
										}
										DoAction.removeGLLY(mContext, sb.toString());
										// 从数据库移除数据
										byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
										Integer id = geometry.hashCode();
										String whereClause = "id=?";
										String[] whereArgs = {id.toString()};
										mSQLiteDatabase.delete("glly", whereClause, whereArgs);
										// 从图层中移除符号
										newglly.removeGraphic(temp.getUid());
									}
								}
							})
							.setNegativeButton("取消", null)
							.create();
					Window window = alertDialog.getWindow();
					WindowManager.LayoutParams lp = window.getAttributes();
					lp.alpha = 0.9f;
					window.setAttributes(lp);
					alertDialog.show();
				}
			}
			// 删除电缆路由
			if (!isSelectedObject) {
				graphicToDelete = getGraphicFromLayer(event.getX(), event.getY(), newdlly);
				if (graphicToDelete != null) {
					isSelectedObject = true;
					final Graphic temp = graphicToDelete;
					AlertDialog alertDialog = new AlertDialog.Builder(mContext)
							.setIcon(R.drawable.logo)
							.setTitle("删除电缆路由")
							.setMessage("你确定要删除该条电缆路由吗？")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									Geometry geometry = temp.getGeometry();
									if (geometry.getType() == Type.POLYLINE) {
										// 从旧数据库移除数据(已弃用)
										Polyline polyline = (Polyline) geometry;
										ArrayList<Point> gllist = new ArrayList<>();
										for(int i=0;i<polyline.getPointCount();i++){
											gllist.add(polyline.getPoint(i));
										}
										// 组合节点字符串
										StringBuffer sb = new StringBuffer();
										for(int i=0;i<gllist.size();i++){
											sb.append(gllist.get(i).getX()+","+gllist.get(i).getY());
											if(i != gllist.size()-1)
												sb.append("/");
										}
										DoAction.removeDLLY(mContext, sb.toString());
										// 从数据库移除数据
										byte[] geometryByte = GeometryEngine.geometryToEsriShape(geometry);
										Integer id = geometry.hashCode();
										String whereClause = "id=?";
										String[] whereArgs = {id.toString()};
										mSQLiteDatabase.delete("dlly", whereClause, whereArgs);
										// 从图层中移除符号
										newdlly.removeGraphic(temp.getUid());
									}
								}
							})
							.setNegativeButton("取消", null)
							.create();
					Window window = alertDialog.getWindow();
					WindowManager.LayoutParams lp = window.getAttributes();
					// 设置透明度为0.3
					lp.alpha = 0.9f;
					window.setAttributes(lp);
					alertDialog.show();
				}
			}

			if (!isSelectedObject) {
				Log.i(LOG_TAG, "单击地图没有选中对象");
			}

			return true;
		}

	}

	@Override
	public boolean onDoubleTap(MotionEvent point) {
		//保存光缆路由数据，已弃用
		if (drawglly) {
			drawglly = false;
			DoAction.saveGLLY(mContext, points);
			fristPoint = null;
			points.clear();
			newglly.removeAll();
			loadGLLY();
			return false;
		}
		//保存电缆路由数据，已弃用
		if (drawdlly) {
			drawdlly = false;
			DoAction.saveDLLY(mContext, points);
			fristPoint = null;
			points.clear();
			newdlly.removeAll();
			loadDLLY();
			return false;
		}
		// 新增光缆
		if (addGL) {
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
			Toast.makeText(mContext, "光缆添加成功！", Toast.LENGTH_SHORT).show();
			return false;
		}
		// TODO 这是啥
		if(addNode){
			addNode = false;
			fristPoint = null;

			for(int i=0;i<points.size()-1;i++){
				//				AddSegmentToLocalSQLite(points.get(i), points.get(i+1));
				sld.saveSegment(points.get(i), points.get(i+1),gdcq,uid_seg_list.get(i));
			}
			points.clear();
			return false;
		}
		// 放大地图
		else {
			mMapView.zoomin();
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

	@Override
	public void onLongPress(MotionEvent event)
	{
		super.onLongPress(event);

		// 在坐标TextView显示当前按压位置的坐标
		Point onLongPressPoint = new Point(event.getX(), event.getY());
		Point mapPoint = mMapView.toMapPoint(onLongPressPoint);
		if (mapPoint != null) {
			String x = String.valueOf(mapPoint.getX()).substring(0, 8);
			String y = String.valueOf(mapPoint.getY()).substring(0, 8);
			mTvCoordinate.setText(x + ", " + y);
		}

		// 在当前按压位置显示标记图钉
		mTempMarkingLayer.removeAll();
		mMapView.centerAt(mapPoint, true);
		click.onMoveAndZoom();
		click.onLocationMarked(mapPoint);
		Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_pin);
		PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol(drawable);
		Graphic graphic = new Graphic(mapPoint, pictureMarkerSymbol);
		mTempMarkingLayer.addGraphic(graphic);
	}

	/**
	 *
	 * @param nList
	 * @param title
	 */
	private void showAlertDialog( final String[] nList,String title) {
		ListAdapter mAdapter = new ArrayAdapter(mContext, R.layout.item, nList);
		LayoutInflater inflater = LayoutInflater.from(mContext);
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

			}
		});
		listview.setOnItemClickListener( null );

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		AlertDialog mAlertDialog = builder.create();
		mAlertDialog.show();
		mAlertDialog.getWindow().setContentView(view);
		//		mAlertDialog.getWindow().setLayout(600, 400);
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	private String searchNodeIDbyName(String name){
		String nodeid="";
		DataTable result = db.executeTable( "spNodeInfoByName", null);
		for( int i=0;i<result.size();i++){
			if(result.hasNext())
				nodeid = result.next().get("nodeid").Value.toString();
		}
		return nodeid;

	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param llayer
	 * @return
	 */
	private boolean isSelectedGraphic(float x, float y, GraphicsLayer llayer) {
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
	private Graphic getGraphicFromLayer(double xScreen, double yScreen,
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

	/**
	 *
	 * @param xScreen
	 * @param yScreen
	 * @param layer
	 * @return
	 */
	private Graphic GetGraphicsFromLayer(double xScreen, double yScreen, GraphicsLayer layer) {
		Graphic result = null;
		try {
			int[] graphicIDs = layer.getGraphicIDs((float)xScreen,(float)yScreen,30);
			if(graphicIDs.length > 0)
				isnew = false;
			for (int i = 0; i < graphicIDs.length; i++) {
				Graphic graphic = layer.getGraphic(graphicIDs[i]);
				result = graphic;

				// TODO 弄明白下面这是啥
				if (graphic != null) {
					Geometry geometry = graphic.getGeometry();
					Type type = geometry.getType();
					LocalDataModify localDataModify = new LocalDataModify(mContext);
					if(0 == type.compareTo(Type.POINT)){
						Point point = (Point) graphic.getGeometry();
						oldPoint = point;
						String a = String.valueOf(point.getX())+","+String.valueOf(point.getY());
						a += "/";
						if(!addGL && !addNode){
							localDataModify.modifyPoint(point.getX(), point.getY());
						}
					} else {
						Polyline pl1 = (Polyline) graphic.getGeometry();
						pl1.getPathCount();
						ArrayList<Point> al = new ArrayList<Point>();
						for(int ii=0;ii<pl1.getPointCount();ii++){
							al.add(pl1.getPoint(ii));
						}
						if (showglly) {
							glList = localDataModify.getGlPts(al,layer, mTempDrawingLayer);

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
							mTempDrawingLayer.addGraphic(tempGraphic);
						} else {
							localDataModify.modifySegment(al);
						}

					}

				}
			}
		} catch (Exception e) {
			return null;
		}
		return result;
	}

	private void showDelDialog(final GraphicsLayer layer,final long graphicid,final int pkid,final DataTable rst,final Type t){
		AlertDialog alertDialog = new AlertDialog.Builder(mContext)
		.setIcon(R.drawable.logo)
		.setTitle("删除元素")
		.setMessage("你确定要删除该元素吗？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() { 

			@Override 
			public void onClick(DialogInterface dialog, int which) { 
				if(rst != null && rst.size() > 0){
					if(t == Type.POINT)
						Toast.makeText(mContext, "请先删除关联管道！", Toast.LENGTH_SHORT).show();
					else if(t == Type.POLYLINE)
						Toast.makeText(mContext, "请先移除管道内的光缆！", Toast.LENGTH_SHORT).show();
					return;
				}
				//delete the graphic on the layer
				layer.removeGraphic((int) graphicid);
				//delete the data in the SQL table
				if(t == Type.POINT)
					DoAction.DeletePointByID(mContext, String.valueOf(pkid));
				else if(t == Type.POLYLINE)
					DoAction.DeleteSegmentByID(mContext, String.valueOf(pkid));
			} 
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() { 

			@Override 
			public void onClick(DialogInterface dialog, int which) { 
				return;
			} 
		})
		.create();
		Window window = alertDialog.getWindow();        
		WindowManager.LayoutParams lp = window.getAttributes();        
		lp.alpha = 0.9f;
		window.setAttributes(lp);        
		alertDialog.show();
	}

	void AlertMsg(String str, Object... arg) {
		String msg = String.format(str, arg);
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
		Log.i("AlertMsg", msg);
	}

	/** 加载本地新增（用户绘制）的光缆路由 **/
	public void loadGLLY(){
		Graphic tempGraphic ;
		db = new SQLiteDatabase(mContext);
		DataCollection params = new DataCollection();
		params.add(new Data("uname",FunctionHelper.userName));
		DataTable rst = db.executeTable("spBS_LocalGLLYQueryByUName", params);
		for(int i=0;i<rst.size();i++){
			Polyline pl = new Polyline();
			DataCollection dc = rst.next();

			String pts = dc.get("points").Value.toString();
			ArrayList<Point> points = LocalDataModify.splitePts(pts);
			for(int j=0;j<points.size();j++){
				if(j == 0)
					pl.startPath(points.get(j));
				else
					pl.lineTo(points.get(j));
			}

			tempGraphic = new Graphic(pl, SimpleSymbolTemplate.GLLY);

			newglly.addGraphic(tempGraphic);
		}
	}

	/** 加载本地新增（用户绘制）的电缆路由 **/
	public void loadDLLY(){
		Graphic tempGraphic ;
		db = new SQLiteDatabase(mContext);
		DataCollection params = new DataCollection();
		params.add(new Data("uname",FunctionHelper.userName));
		DataTable rst = db.executeTable("spBS_LocalDLLYQueryByUName", params);
		for(int i=0;i<rst.size();i++){
			Polyline pl = new Polyline();
			DataCollection dc = rst.next();

			String pts = dc.get("points").Value.toString();
			ArrayList<Point> points = LocalDataModify.splitePts(pts);
			for(int j=0;j<points.size();j++){
				if(j == 0)
					pl.startPath(points.get(j));
				else
					pl.lineTo(points.get(j));
			}

			tempGraphic = new Graphic(pl, new SimpleLineSymbol(Color.LTGRAY, 1, SimpleLineSymbol.STYLE.SOLID));

			newdlly.addGraphic(tempGraphic);
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
		void onMoveAndZoom();
		/** 在地图上进行标注时 **/
		void onLocationMarked(Geometry markedLocation);
	}
}
