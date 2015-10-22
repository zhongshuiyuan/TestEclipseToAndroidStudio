package com.qingdao.shiqu.arcgis.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.Geometry.Type;
import com.qingdao.shiqu.arcgis.helper.FunctionHelper;
import com.qingdao.shiqu.arcgis.sqlite.DoAction;

import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.content.Context;
import android.widget.Toast;

/**
 * 功 能：保存本地数据
 * 
 * @author ouseitatsu
 *
 */
public class LocalDataAction {
	private Context context;
	private SQLiteDatabase db;


	public LocalDataAction(Context context) {
		super();
		this.context = context;
		db = new SQLiteDatabase(context);
	}



	/**
	 * @param p
	 * 			点
	 * @param jtype
	 * 			类型
	 * @param uid
	 * 			graphic id
	 */
	public  void  savePoint(Point p,String jtype,int uid){
		DataCollection params1 = new DataCollection();
		params1.add(new Data("gjid", null));
		params1.add(new Data("uid",uid));
		params1.add(new Data("gjname","井-000"));
		params1.add(new Data("x",String.valueOf(p.getX()) ));
		params1.add(new Data("y",String.valueOf(p.getY()) ));
		if(jtype==null)
			params1.add(new Data("type",""));
		else
			params1.add(new Data("type",jtype));//管井类型，
		params1.add(new Data("pq",""));
		params1.add(new Data("operator",FunctionHelper.userName));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");     
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String str = formatter.format(curDate);   
		params1.add(new Data("time",str));
//		db = new SQLiteDatabase(context);
		db.execute("aqBS_LocalDataAdd", params1);
	}
	/**
	 * @param s
	 * 			起点
	 * @param e
	 * 			终点
	 * @param uid
	 * 			graphics id
	 */
	public void saveSegment(Point s,Point e,String gdcq,int uid){
		//获取起始点ID
		DoAction da = new DoAction(context);
		int sid = da.GetPointIDByXY(String.valueOf(s.getX()), String.valueOf(s.getY()));
		int eid = da.GetPointIDByXY(String.valueOf(e.getX()), String.valueOf(e.getY()));
		if(sid == 0|| eid == 0){
			Toast.makeText(context, "节点出错，管线数据添加失败！",Toast.LENGTH_LONG ).show();
			return;
		}
		DataCollection params1 = new DataCollection();
		params1.add(new Data("gdid", null));
		params1.add(new Data("uid",uid));
		params1.add(new Data("gdname","管道-000"));
		params1.add(new Data("type", gdcq));
		params1.add(new Data("sid",sid));
		params1.add(new Data("sx", String.valueOf(s.getX())));
		params1.add(new Data("sy", String.valueOf(s.getY())));
		params1.add(new Data("eid",eid));
		params1.add(new Data("ex", String.valueOf(e.getX())));
		params1.add(new Data("ey", String.valueOf(e.getY())));
		params1.add(new Data("operator",FunctionHelper.userName));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");     
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String str = formatter.format(curDate);   
		params1.add(new Data("time",str));
//		db = new SQLiteDatabase(context);
		db.execute("spBS_LocalGDAdd", params1);
	}
	public void savaGL(List<Point> pts,String type){
		if(pts.size() < 2){
			Toast.makeText(context, "光缆添加失败！", Toast.LENGTH_SHORT).show();
			return;
		}
		String gl_id = null;
		//保存光缆数据，有两个表，一个是光缆表，一个是光缆与管道表
		//先保存数据到光缆表
		StringBuffer listData = new StringBuffer();
		//组合折点链表,以string类型存储到数据库
		for(int i=0;i<pts.size();i++){
			listData.append(pts.get(i).getX()+","+pts.get(i).getY());
			if(i != pts.size()-1)
				listData.append("/");
		}

		DataCollection params1 = new DataCollection();
		params1.add(new Data("glid", null));
		params1.add(new Data("glname","光缆-000"));
		params1.add(new Data("gltype", type));
		params1.add(new Data("points",listData.toString()));
		params1.add(new Data("operator",FunctionHelper.userName));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");     
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String str = formatter.format(curDate);   
		params1.add(new Data("time",str));
		db = new SQLiteDatabase(context);
		db.execute("spBS_LocalGLAdd", params1);
		//查找光缆
		DataCollection dcgl = new DataCollection();
		dcgl.add(new Data("points",listData.toString()));
		DataTable rst_gl = db.executeTable("spBS_QueryGLByPts", dcgl);
		for(int i=0;i<rst_gl.size();i++){
			DataCollection dc_gl = rst_gl.next();
			gl_id = dc_gl.get("glid").Value.toString();
		}
		//查找管道
		for(int i=0;i<pts.size()-1;i++){
			String name,id;
			DataCollection params = new DataCollection();
			params.add(new Data("sx1",String.valueOf(pts.get(i).getX())));
			params.add(new Data("sy1", String.valueOf(pts.get(i).getY())));
			params.add(new Data("ex1", String.valueOf(pts.get(i+1).getX())));
			params.add(new Data("ey1", String.valueOf(pts.get(i+1).getY())));
			params.add(new Data("sx2",String.valueOf(pts.get(i+1).getX())));
			params.add(new Data("sy2", String.valueOf(pts.get(i+1).getY())));
			params.add(new Data("ex2", String.valueOf(pts.get(i).getX())));
			params.add(new Data("ey2", String.valueOf(pts.get(i).getY())));
//			db = new SQLiteDatabase(context);
			DataTable rst = db.executeTable("spBS_LocalDataQuerySE", params);
			if(rst != null){
				for(int i1=0;i1<rst.size();i1++){
					DataCollection dc = rst.next();
					name = dc.get("gdname").Value.toString();
					id = dc.get("gdid").Value.toString();
					DataCollection dc_gd_gl = new DataCollection();
					dc_gd_gl.add(new Data("glid", gl_id));
					dc_gd_gl.add(new Data("gdid", id));
					db.execute("spBS_GD2GLAdd", dc_gd_gl);
				}
			}
		}
		//首先查找到管道ID
	}
	public List<Point> getGLPoints(String id){
		List<Point> pts = new ArrayList<Point>();
		DataCollection dc = new DataCollection();
		String points = null;
		dc.add(new Data("id", id));                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
		DataTable rst = db.executeTable("spBS_QueryGLPTSByID", dc);
		for(int i=0;i<rst.size();i++){
			DataCollection dc_gl = rst.next();
			points = dc_gl.get("points").Value.toString();
		}
		if(points != null){
			//解析points字符串，组合成Point放到pts中返回
			String[] xy = points.split("/");
			for(int i=0;i<xy.length;i++){
				String[] cross = xy[i].split(",");
				Point p = new Point();
				p.setX(Double.valueOf(cross[0]));
				p.setY(Double.valueOf(cross[1]));
				pts.add(p);
			}
		}
		return pts;
	}
}
