package com.qingdao.shiqu.arcgis.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jsqlite.Callback;
import jsqlite.Database;
import Eruntech.BirthStone.Core.Helper.File;
import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import Eruntech.BirthStone.Core.Sqlite.SQLiteOpenbase;
import Eruntech.BirthStone.Core.Sqlite.SQLiteOpenbase.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.esri.core.geometry.Point;
import com.qingdao.shiqu.arcgis.ActivityHelper;
import com.qingdao.shiqu.arcgis.AssetHelper;
import com.qingdao.shiqu.arcgis.activity.Login;
import com.qingdao.shiqu.arcgis.mode.Road;

/**************************************
 *
 *      作  者： 潘跃瑞 
 *
 *      功  能： 数据库操作，包括数据库创建，查询 
 *
 *      时  间： 2014-10-30
 *
 *      版  权： 青岛盛天科技有限公司 
 *     
 ****************************************/
public class DBOpterate
{
	static DBOpterate dbOpterate;
	Context context;
	// private jsqlite.Database db;
	Database db;
	String dbFile;
	SQLiteDatabase database = null;

	private DBOpterate(Context context)
	{
		this.context = context;
		/**
		 * 拷贝数据库
		 */
		if(!File.exists(ActivityHelper.getPath(context, false) + "/gisdb.sqlite"))
		{
			// 写入数据库文件
			//			File.write(appPath + DbName, File.getAssetsByte(DbName, context));

			try
			{
				AssetHelper.CopyAsset(context, ActivityHelper.getPath(context, false),"gisdb.sqlite");
			} catch (IOException e)
			{
				e.printStackTrace();
			}}
		initDB();
	}
	private void initDB()
	{
		try
		{
			dbFile = ActivityHelper.getDataBase(context,"gisdb.sqlite");
			Class.forName("jsqlite.JDBCDriver").newInstance();
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		//	db = new jsqlite.Database();
		db = new Database();
		//jsqlite.Database
		//db.o
		//	db.open(filename, mode, vfs)
		//		jsqlite.
		//	db.open(dbFile, jsqlite.Constants.SQLITE_OPEN_READONLY);
		//	} catch (Exception e) {
		//	}

		if (database == null)
		{
			database = new SQLiteDatabase(context);
		}
	}
	public static DBOpterate getDbOpterate(Context context)
	{
		if(dbOpterate == null)
		{
			dbOpterate = new DBOpterate(context);
		}
		return dbOpterate;
	}
	public List<Road> search(String name,String type)
	{
		if("道路".equals(type))
		{
			return searchRoad(name);
		}else {
			return searchPoint(name, type);
		}
	}

	public List<Road> searchDaoLu(String _name)
	{

		final List<Road> list = new ArrayList<Road>();
		DataCollection collecter = new DataCollection();
		collecter.add(new Data("roadname", "%"+_name+"%"));
		collecter.add(new Data("roadspell", "%"+_name+"%"));
		DataTable table = database.executeTable("spDaoLuList", collecter);
		for (DataCollection dataCollection : table) 
		{
			Road road = new Road();
			String roadName = dataCollection.get("roadname").getValue().toString();
			road.setName(roadName);
			list.add(road);
		}
		return list;
	}

	public List<Road> searchDanYuan(String _name,String _mph,String _lh,String _dyh)
	{

		final List<Road> list = new ArrayList<Road>();
		DataCollection collecter = new DataCollection();
		collecter.add(new Data("roadname", "%"+_name+"%"));
		collecter.add(new Data("roadspell", "%"+_name+"%"));
		collecter.add(new Data("mph", "%"+_mph+"%"));
		collecter.add(new Data("lh", "%"+_lh+"%"));
		collecter.add(new Data("dyh", "%"+_dyh+"%"));
		DataTable table = database.executeTable("spDanYuanList", collecter);
		for (DataCollection dataCollection : table) 
		{
			Road road = new Road();
			String roadName = dataCollection.get("roadname").getValue().toString();
			String mph = dataCollection.get("mph").getValue().toString();
			String lh = dataCollection.get("lh").getValue().toString();
			String dyh = dataCollection.get("dyh").getValue().toString();
			String x = dataCollection.get("x").getValue().toString();
			String y = dataCollection.get("y").getValue().toString();

			String point = "POINT("+x+" "+y+")";
			String r = roadName + mph +"号" + lh +"号楼" + dyh +"单元";
			road.setName(r);
			road.setPoint(point);
			list.add(road);
		}
		return list;
	}

	private List<Road> searchPoint(String name,String type)
	{
		String typeName = "";
		if(type.equals("单元"))
		{
			typeName = "34";
		}else if(type.equals("光机")){
			typeName = "11";
		}else if(type.equals("井")){
			typeName = "1";
		}

		final List<Road> list = new ArrayList<Road>();
		try
		{
			Callback cb = new Callback() {
				@Override
				public void columns(String[] coldata) {
				}

				@Override
				public void types(String[] types) {
				}

				@Override
				public boolean newrow(String[] rowdata) {
					Road road = new Road();
					road.setName(rowdata[0]);
					road.setPoint(rowdata[1]);
					list.add(road);
					//POINT(228414.864412 106650.017212)
					return false;
				}
			};
			db.open(dbFile,  jsqlite.Constants.SQLITE_OPEN_READONLY);
			String query = "select nodename,astext(Geometry),nodetypeid from jiedian where nodename like '%"+name+"%' and nodetypeid='"+typeName+"'";
			db.exec(query, cb);
			db.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 *   功  能：搜索道路
	 *   @param name 道路名称
	 *	 @return  搜索到的道路集合
	 */
	private List<Road> searchRoad(String name)
	{
		final List<Road> list = new ArrayList<Road>();
		try
		{
			Callback cb = new Callback() {
				@Override
				public void columns(String[] coldata) {
				}

				@Override
				public void types(String[] types) {
				}

				@Override
				public boolean newrow(String[] rowdata) {
					Road road = new Road();
					road.setName(rowdata[0]);
					road.setPoint(rowdata[1]);
					list.add(road);
					return false;
				}
			};
			db.open(dbFile,  jsqlite.Constants.SQLITE_OPEN_READONLY);
			String query = "select roadname,AsText(Geometry)AS X from daolu where roadname like '%"+name+"%'";
			db.exec(query, cb);
			db.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	public List<Road> searchID(String name,String type){
		String typeName = "";
		if(type.equals("单元"))
		{
			typeName = "34";
		}else if(type.equals("光机")){
			typeName = "11";
		}else if(type.equals("井")){
			typeName = "1";
		}

		final List<Road> list = new ArrayList<Road>();
		try
		{
			Callback cb = new Callback() {
				@Override
				public void columns(String[] coldata) {
				}

				@Override
				public void types(String[] types) {
				}

				@Override
				public boolean newrow(String[] rowdata) {
					Road road = new Road();
					road.setName(rowdata[0]);
					road.setPoint(rowdata[1]);
					list.add(road);
					//POINT(228414.864412 106650.017212)
					return false;
				}
			};
			db.open(dbFile,  jsqlite.Constants.SQLITE_OPEN_READONLY);
			String query = "select nodename,astext(Geometry),nodetypeid from jiedian where nodename = '"+name+"' and nodetypeid='"+typeName+"'";
			db.exec(query, cb);
			db.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public List<Data> searchPointByID(String id,final String[] selectionArgs){
		final List<Data> list = new ArrayList<Data>();
		try
		{
			Callback cb = new Callback() {
				@Override
				public void columns(String[] coldata) {
				}

				@Override
				public void types(String[] types) {
				}

				@Override
				public boolean newrow(String[] rowdata) {
					/*dt.setName(rowdata[0]);
					dt.setValue(rowdata[1]);*/
					for(int i=0;i<selectionArgs.length;i++){
						Data dt = new Data();
						if(selectionArgs[i].equals("astext(Geometry)")){
							Data dt1 = new Data();
							Data dt2 = new Data();
							Point pp = Util.convertPoint(rowdata[i]);
							dt1.setName("X");
							dt1.setValue(pp.getX());
							dt2.setName("Y");
							dt2.setValue(pp.getY());
							list.add(dt1);
							list.add(dt2);
							continue;
						}
						else{
							dt.setName(selectionArgs[i]);
							dt.setValue(rowdata[i]);
						}
						
						list.add(dt);
					}
					return false;
				}
			};
			db.open(dbFile,  jsqlite.Constants.SQLITE_OPEN_READONLY);
			StringBuffer columns = new StringBuffer();
			for(int i=0;i<selectionArgs.length-1;i++){
				columns.append(selectionArgs[i]);
				columns.append(" , ");
			}
			columns.append(selectionArgs[selectionArgs.length-1]);
			String query = "select "+ columns +" from jiedian where object_use = "+id;
			db.exec(query, cb);
			db.close();
		} catch (Exception e)
		{
			Log.e("error:", e.getMessage());
		}
		return list;
	}
	public int searchNodeidbyName(String name){
		
		return 0;
	}
}
