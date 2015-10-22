package com.qingdao.shiqu.arcgis.utils;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Geometry.Type;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.GLListAdapter;
import com.qingdao.shiqu.arcgis.adapter.MyAdapter;
import com.qingdao.shiqu.arcgis.adapter.UpdateDataAdapter;
import com.qingdao.shiqu.arcgis.helper.FunctionHelper;
import com.qingdao.shiqu.arcgis.sqlite.DoAction;

import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LocalDataModify {
	static Context context;
	static SQLiteDatabase db = null;
	String start = "";
	String end = "";
	String opreator="",operatTime="";

	ArrayList<Point> pts;
	ArrayList<String> glnams;


	public LocalDataModify(Context context) {
		super();
		this.context = context;

		pts = new ArrayList<Point>();
	}


	public  void modifyPoint(double x,double y){
		String name = null;
		int id = 0;
		DataCollection params = new DataCollection();
		params.add(new Data("x", x));
		params.add(new Data("y", y));
		db = new SQLiteDatabase(context);
		DataTable rst = db.executeTable("spBS_LocalDataQueryXY", params);
		if(rst != null){
			for(int i=0;i<rst.size();i++){
				DataCollection dc = rst.next();
				name = dc.get("gjname").Value.toString();
				id = Integer.valueOf(dc.get("gjid").Value.toString());
				opreator = dc.get("operator").Value.toString();
				operatTime = dc.get("operattime").Value.toString();
			}
			updateData(name, id,Type.POINT);
		}
	}
	/**
	 * 	Get the point's information in the SQL table
	 * @param x
	 * 			x
	 * @param y
	 * 			x
	 * @return
	 * 			the result of datatable
	 */
	public static DataTable getPointInfo(double x,double y){
		DataCollection params = new DataCollection();
		params.add(new Data("x", x));
		params.add(new Data("y", y));
		db = new SQLiteDatabase(context);
		DataTable rst = db.executeTable("spBS_LocalDataQueryXY", params);
		return rst;
	}
	/**
	 * 	Get the segment's information int SQL table
	 * @param al
	 * 			the arraylist of point,also the segment's start point and end point
	 * @return
	 * 			the result of datatable
	 */
	public static DataTable getSegmentInfo(ArrayList<Point> al){
		DataCollection params = new DataCollection();
		params.add(new Data("sx1",String.valueOf(al.get(0).getX())));
		params.add(new Data("sy1", String.valueOf(al.get(0).getY())));
		params.add(new Data("ex1", String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("ey1", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("sx2",String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("sy2", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("ex2", String.valueOf(al.get(0).getX())));
		params.add(new Data("ey2", String.valueOf(al.get(0).getY())));
		db = new SQLiteDatabase(context);
		DataTable rst = db.executeTable("spBS_LocalDataQuerySE", params);
		return rst;
	}
	public void modifySegment(ArrayList<Point> al){
		String name="";
		int id=0;
		DataCollection params = new DataCollection();
		params.add(new Data("sx1",String.valueOf(al.get(0).getX())));
		params.add(new Data("sy1", String.valueOf(al.get(0).getY())));
		params.add(new Data("ex1", String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("ey1", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("sx2",String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("sy2", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("ex2", String.valueOf(al.get(0).getX())));
		params.add(new Data("ey2", String.valueOf(al.get(0).getY())));
		db = new SQLiteDatabase(context);
		DataTable rst = db.executeTable("spBS_LocalDataQuerySE", params);
		if(rst != null){
			for(int i=0;i<rst.size();i++){
				DataCollection dc = rst.next();
				name = dc.get("gdname").Value.toString();
				id = Integer.valueOf(dc.get("gdid").Value.toString());
				opreator = dc.get("operator").Value.toString();
				operatTime = dc.get("operattime").Value.toString();
			}
			if(rst.size() > 0){
				DoAction da = new DoAction(context);
				start = da.QueryPointByXY(String.valueOf(al.get(0).getX()), String.valueOf(al.get(0).getY()));
				end = da.QueryPointByXY(String.valueOf(al.get(al.size()-1).getX()), String.valueOf(al.get(al.size()-1).getY()));
				//通过管道id获取包含光缆id
				String rid = DoAction.getGLIDByGDID(context, String.valueOf(id));
				//解析返回来的id的组合，各个id直接以，进行分割
				String[] ids = rid.split(",");
				//通过光缆ID获取光缆名称
				glnams = new ArrayList<String>();
				for(int i=0;i<ids.length;i++){
					if(DoAction.getGLNameByID(context, ids[i]) == null)
						continue;
					glnams.add("光缆名称："+DoAction.getGLNameByID(context, ids[i]));
				}

				updateData(name, id, Type.POLYLINE);
			}

		}
	}
	public ArrayList<String> getGLInfo(ArrayList<Point> al){
		ArrayList<String> info = new ArrayList<String>();
		int id=0;
		DataCollection params = new DataCollection();
		params.add(new Data("sx1",String.valueOf(al.get(0).getX())));
		params.add(new Data("sy1", String.valueOf(al.get(0).getY())));
		params.add(new Data("ex1", String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("ey1", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("sx2",String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("sy2", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("ex2", String.valueOf(al.get(0).getX())));
		params.add(new Data("ey2", String.valueOf(al.get(0).getY())));
		db = new SQLiteDatabase(context);
		//query gd get gdid
		DataTable rst = db.executeTable("spBS_LocalDataQuerySE", params);
		if(rst != null){
			for(int i=0;i<rst.size();i++){
				DataCollection dc = rst.next();
				id = Integer.valueOf(dc.get("gdid").Value.toString());
			}
			if(rst.size() > 0){
				DataCollection param = new DataCollection();
				param.add(new Data("gdid",id));
				// query the table of gd2gl get glid by gdid
				DataTable rt_gd2gl = db.executeTable("spBS_QueryGD2GLByGDID", param);
				//if the result > 1,show dialog let's user select one and get information
				final String[] ids = new String[rt_gd2gl.size()];

				if(rt_gd2gl.size() > 1){
					StringBuffer sb_id = new StringBuffer();
					StringBuffer sb_nm = new StringBuffer();
					for(int i=0;i<rt_gd2gl.size();i++){
						DataCollection dc = rt_gd2gl.next();
						String glid = dc.get("glid").Value.toString();
						String glname = getGLNameByGLID(glid);
						sb_id.append(glid);
						sb_nm.append(glname);
						if(i != rt_gd2gl.size()-1){
							sb_id.append(",");
							sb_nm.append(",");
						}
					}
					info.add(sb_id.toString());
					info.add(sb_nm.toString());
				}else if(rt_gd2gl.size() <= 0){
					// no glid, show toast
					Toast.makeText(context, "无光缆路由图！", Toast.LENGTH_SHORT).show();
					return null;
				}else{
					//only on fiber in this hole ,and show this fiber
					for(int i=0;i<rt_gd2gl.size();i++){
						DataCollection dc = rt_gd2gl.next();
						String glid = dc.get("glid").Value.toString();
						String glname = getGLNameByGLID(glid);
						info.add(glid);
						info.add(glname);
					}
				}

			}

		}
		return info;
	}
	/**
	 * 	说 	明：通过管道坐标来获得该管道中某一条光缆的路由折点
	 * @param al
	 * 			管道坐标
	 * @return
	 * 			光缆坐标折点数组的list
	 */
	@SuppressLint("NewApi")
	public ArrayList<Point> getGlPts(ArrayList<Point> al,final GraphicsLayer layer,final GraphicsLayer drawLayer){
		int id=0;
		DataCollection params = new DataCollection();
		params.add(new Data("sx1",String.valueOf(al.get(0).getX())));
		params.add(new Data("sy1", String.valueOf(al.get(0).getY())));
		params.add(new Data("ex1", String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("ey1", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("sx2",String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("sy2", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("ex2", String.valueOf(al.get(0).getX())));
		params.add(new Data("ey2", String.valueOf(al.get(0).getY())));
		db = new SQLiteDatabase(context);
		//query gd get gdid
		DataTable rst = db.executeTable("spBS_LocalDataQuerySE", params);
		if(rst != null){
			for(int i=0;i<rst.size();i++){
				DataCollection dc = rst.next();
				id = Integer.valueOf(dc.get("gdid").Value.toString());
			}
			if(rst.size() > 0){
				DataCollection param = new DataCollection();
				param.add(new Data("gdid",id));
				// query the table of gd2gl get glid by gdid
				DataTable rt_gd2gl = db.executeTable("spBS_QueryGD2GLByGDID", param);
				//if the result > 1,show dialog let's user select one and get information
				final String[] ids = new String[rt_gd2gl.size()];

				if(rt_gd2gl.size() > 1){
					String[] items =new String[rt_gd2gl.size()];
					String points = null;
					for(int i=0;i<rt_gd2gl.size();i++){
						DataCollection dc = rt_gd2gl.next();
						String glid = dc.get("glid").Value.toString();
						String glname = getGLNameByGLID(glid);
						items[i] = glname;
						ids[i] = glid;
					}
					// show dialog
					AlertDialog alertDialog = new AlertDialog.Builder(context,R.style.dialog001)
							.setIcon(R.drawable.logo)
							.setItems(items, new OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO 自动生成的方法存根
									pts = splitePts(getGLPtsByGLID(ids[arg1]));
									Polyline polyline = new Polyline();
									Point startPoint;
									Point endPoint;
									for (int m = 0; m < pts.size()-1; m++)
									{
										startPoint = pts.get(m);
										endPoint = pts.get(m+1);

										Line line = new Line();
										line.setStart(startPoint);
										line.setEnd(endPoint);

										polyline.addSegment(line, false);
									}
									Graphic tempGraphic = new Graphic(polyline, new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH));
									drawLayer.addGraphic(tempGraphic);
								}
							})
							.setPositiveButton("确定", new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {}
							})
							.setNegativeButton("取消",null)
							.create();
					Window window = alertDialog.getWindow();
					WindowManager.LayoutParams lp = window.getAttributes();
					// 设置透明度为0.3         
					lp.alpha = 0.6f;
					window.setAttributes(lp);
					alertDialog.show();

				}else if(rt_gd2gl.size() <= 0){
					// no glid, show toast
					Toast.makeText(context, "无光缆路由图！", Toast.LENGTH_SHORT).show();
					return null;
				}else{
					//only on fiber in this hole ,and show this fiber
					for(int i=0;i<rt_gd2gl.size();i++){
						String points = null;
						DataCollection dc = rt_gd2gl.next();
						String glid = dc.get("glid").Value.toString();
						points  = getGLPtsByGLID(glid);
						pts = splitePts(points);
					}
				}

			}

		}
		return pts;

	}
	public void setValue(ArrayList<Point> targe,ArrayList<Point> result){
		targe = result;
	}
	/**
	 * 说	明：通过光缆id获取光缆折点
	 * @param id
	 * 			光缆id
	 * @return
	 * 			光缆折点
	 */
	public String getGLPtsByGLID(String id){
		String pts = null;
		DataCollection gl_params = new DataCollection();
		gl_params.add(new Data("glid", id));
		DataTable rt_gl = db.executeTable("spBS_QueryGLByID", gl_params);
		String[] title = {"光缆名称：","光缆类型：","操作员：","操作时间："};
		String[] text = new String[4];
		for(int j=0;j<rt_gl.size();j++){
			DataCollection dc_gl = rt_gl.next();
			pts = dc_gl.get("points").Value.toString();
			text[0] = dc_gl.get("glname").Value.toString();
			text[1] = dc_gl.get("gltype").Value.toString();
			text[2] = dc_gl.get("operator").Value.toString();
			text[3] = dc_gl.get("operattime").Value.toString();
		}
		showGLDialog(context, title, text,id);
		return pts;
	}
	public String getGLNameByGLID(String id){
		String pts = null;
		DataCollection gl_params = new DataCollection();
		gl_params.add(new Data("glid", id));
		DataTable rt_gl = db.executeTable("spBS_QueryGLByID", gl_params);
		for(int j=0;j<rt_gl.size();j++){
			DataCollection dc_gl = rt_gl.next();
			pts = dc_gl.get("glname").Value.toString();
		}
		return pts;
	}
	/**
	 * 说	明：分解String类型的折点为ArrayList<Point>
	 * @param s
	 * 			String类型的折点	(x1,y1/x2,y2/x3,y3....)
	 * @return
	 * 			解析后的Point的折点list
	 */
	public static ArrayList<Point> splitePts(String s){
		ArrayList<Point> pts = new ArrayList<Point>();
		String[] points = s.split("/");
		for(int i=0;i<points.length;i++){
			String[] xy = points[i].split(",");
			Point p = new Point();
			p.setX(Double.valueOf(xy[0]));
			p.setY(Double.valueOf(xy[1]));
			pts.add(p);
		}
		return pts;
	}
	/**
	 * 	show dialog the update
	 * @param name
	 *
	 * @param id
	 * @param type
	 */
	@SuppressLint("NewApi")
	protected void updateData(String name,int id,final Type type){
		LayoutInflater factory = LayoutInflater.from(context);
		final View pView = factory.inflate(R.layout.localpoint, null);
		LinearLayout lx = (LinearLayout)pView.findViewById(R.id.lsegx);
		LinearLayout ly = (LinearLayout)pView.findViewById(R.id.lsegy);
		final EditText uid = (EditText)pView.findViewById(R.id.id);
		final EditText uname = (EditText)pView.findViewById(R.id.name);
		EditText ux = (EditText)pView.findViewById(R.id.esegx);
		EditText uy = (EditText)pView.findViewById(R.id.esegy);
		TextView tid = (TextView)pView.findViewById(R.id.tvid);
		TextView tname = (TextView)pView.findViewById(R.id.tvname);
		TextView tx = (TextView)pView.findViewById(R.id.segx);
		TextView ty = (TextView)pView.findViewById(R.id.segy);
		EditText ope = (EditText)pView.findViewById(R.id.eope);
		EditText opt = (EditText)pView.findViewById(R.id.eopt);
		ope.setText(opreator);
		opt.setText(operatTime);
		uid.setText(String.valueOf(id));
		uname.setText(name);
		String title="";
		String sqlName="";

		if(0 == type.compareTo(Type.POINT)){
			title = "节点属性录入";
			tid.setText("节点ID：");
			tname.setText("节点名称：");
			sqlName = "spBS_LocalDataUpdate";
		}else if(0 == type.compareTo(Type.POLYLINE)){
			title = "管道属性录入";
			tid.setText("管道ID：");
			tname.setText("管道名称：");
			tx.setVisibility(TextView.VISIBLE);
			lx.setVisibility(LinearLayout.VISIBLE);
			ly.setVisibility(LinearLayout.VISIBLE);
			ux.setText(start);
			uy.setText(end);
			sqlName = "spBS_LocalDataGDUpdate";
		}
		if(0 == type.compareTo(Type.POLYLINE)){
			String[] a = {"查看所含光缆"};
			final String[] array=new String[glnams.size()];
			for(int i=0;i<glnams.size();i++){
				array[i]=(String)glnams.get(i);
			}
			AlertDialog alertDialog = new AlertDialog.Builder(context,R.style.dialog001)
					.setIcon(R.drawable.logo)
					.setTitle(title)
					.setView(pView)
					.setPositiveButton("修改", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO 自动生成的方法存根
							DataCollection params = new DataCollection();
							params.add(new Data("id", uid.getText().toString()));
							params.add(new Data("name",uname.getText().toString()));
							db = new SQLiteDatabase(context);
							if(0 == type.compareTo(Type.POINT))
								db.execute("spBS_LocalDataUpdate", params);
							else if(0 == type.compareTo(Type.POLYLINE))
								db.execute("spBS_LocalDataGDUpdate", params);
						}
					})
					.setNeutralButton("查看所含光缆", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO 自动生成的方法存根
							new AlertDialog.Builder(context)
									.setTitle("光缆信息")
									.setItems(array, null)
									.setPositiveButton("确定",new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int which) {
										}
									})
									.setNegativeButton( "取消", new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog,int which) {
										}
									}).show();
						}
					})
					.setNegativeButton("取消",null)
					.create();
			Window window = alertDialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			// 设置透明度为0.3         
			lp.alpha = 1f;
			window.setAttributes(lp);
			alertDialog.show();
		}
		else{
			AlertDialog alertDialog = new AlertDialog.Builder(context,R.style.dialog001)
					.setIcon(R.drawable.logo)
					.setTitle(title)
					.setView(pView)
					.setPositiveButton("修改", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO 自动生成的方法存根
							DataCollection params = new DataCollection();
							params.add(new Data("id", uid.getText().toString()));
							params.add(new Data("name",uname.getText().toString()));
							db = new SQLiteDatabase(context);
							if(0 == type.compareTo(Type.POINT))
								db.execute("spBS_LocalDataUpdate", params);
							else if(0 == type.compareTo(Type.POLYLINE))
								db.execute("spBS_LocalDataGDUpdate", params);
						}
					})
					.setNegativeButton("取消",null)
					.create();
			Window window = alertDialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			// 设置透明度为0.3         
			lp.alpha = 1f;
			window.setAttributes(lp);
			alertDialog.show();
		}
	}
	public void showHighLightElement(ArrayList<Point> pts,GraphicsLayer layer){
		Polyline polyline = new Polyline();
		for(int i=0;i<pts.size();i++){
			polyline.lineTo(pts.get(i));
		}
		SimpleLineSymbol lineSysb = new SimpleLineSymbol(Color.GREEN, 1, SimpleLineSymbol.STYLE.SOLID);
		Graphic tempGraphic = new Graphic(polyline, lineSysb);
		int uid = layer.addGraphic(tempGraphic);
	}
	@SuppressLint("NewApi")
	public void showGLDialog(final Context context,String[] title,String[] text,final String glid){
		MyAdapter ma = new MyAdapter(context,title ,text);
		View myView = LayoutInflater.from(context).inflate(R.layout.gllayout, null);
		final EditText glname = (EditText) myView.findViewById(R.id.glname);
		TextView type = (TextView)myView.findViewById(R.id.gltype);
		TextView operator = (TextView) myView.findViewById(R.id.operator);
		TextView time = (TextView) myView.findViewById(R.id.time);
		glname.setText(text[0]);
		type.setText(text[1]);
		operator.setText(text[2]);
		time.setText(text[3]);
		AlertDialog alertDialog = new AlertDialog.Builder(context,R.style.dialog001)
				.setIcon(R.drawable.logo)
		/*.setAdapter(ma, null)*/
				.setView(myView)
				.setTitle("光缆信息")
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						DoAction.updateGLInformation(context, glid, glname.getText().toString());
					}
				})
		/*.setNegativeButton("取消",null)*/
				.setNeutralButton("移除光缆", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO 自动生成的方法存根
						//从光缆表中移除
						DoAction.removeGLFromGLT(context, glid);
						//从光缆2管道表中移除
						DoAction.removeGLFromGL2GD(context, glid);
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
