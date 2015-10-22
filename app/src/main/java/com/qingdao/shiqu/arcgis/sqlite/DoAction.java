package com.qingdao.shiqu.arcgis.sqlite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esri.core.geometry.Geometry.Type;
import com.esri.core.geometry.Point;
import com.qingdao.shiqu.arcgis.activity.GJEditActivity;
import com.qingdao.shiqu.arcgis.adapter.GJPictureAdapter;
import com.qingdao.shiqu.arcgis.helper.FunctionHelper;

import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/**
 * The action about LocalSQLite the almost method is static 
 * @author ouseitatsu　
 *
 */
public class DoAction {

	Context context;
	private DataCollection params;
	private SQLiteDatabase db;
	DataTable rst;

	public DoAction(Context context) {
		super();
		this.context = context;
		db = new SQLiteDatabase(context);
	}
	/**
	 * @param x 
	 * 			坐标
	 * @param y
	 * 			坐标
	 * @return
	 * 			点名称
	 */
	public String QueryPointByXY(String x,String y){
		params = new DataCollection();
		params.add(new Data("x",x));
		params.add(new Data("y", y));
		String name = "";
		rst = db.executeTable("spBS_LocalDataQueryXY", params);
		if(rst != null){
			for(int i=0;i<rst.size();i++){
				DataCollection dc = rst.next();
				name = dc.get("gjname").Value.toString();
				//				id = Integer.valueOf(dc.get("gjid").Value.toString());
			}
		}
		return name;
	}
	/**
	 * @param x
	 * 			坐标
	 * @param y
	 * 			坐标
	 * @return
	 * 			点ID
	 */
	public int GetPointIDByXY(String x,String y){
		params = new DataCollection();
		params.add(new Data("x",x));
		params.add(new Data("y", y));
		int id = 0;
		rst = db.executeTable("spBS_LocalDataQueryXY", params);
		if(rst != null){
			for(int i=0;i<rst.size();i++){
				DataCollection dc = rst.next();
				id = Integer.valueOf(dc.get("gjid").Value.toString());
			}
		}
		return id;
	}
	/**
	 * @param context
	 * 					上下文
	 * @param cq
	 * 					产权名称
	 * @return
	 * 					管道颜色
	 */
	public static int getGDColorByCQ(Context context,String cq){
		SQLiteDatabase db1 = new SQLiteDatabase(context);
		int result = 0;
		DataCollection params = new DataCollection();
		params.add(new Data("name", cq));
		DataTable rst = db1.executeTable("aqBS_QueryGDCQByName", params);
		for(int i=0;i<rst.size();i++){
			DataCollection dc = rst.next();
			String[] cls = dc.get("gdcolor").Value.toString().split(",");
			result = Color.rgb(Integer.valueOf(cls[0]), Integer.valueOf(cls[1]), Integer.valueOf(cls[2]));
		}
		return result;
	}
	/**
	 * 	Get segment by start point and end point
	 * @param context	
	 * 			context
	 * @param al
	 * 			start point and end point list
	 * @return
	 * 			SQL result
	 */
	public static DataTable getSegmentByStartAndEnd(Context context,ArrayList<Point> al){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection params = new DataCollection();
		params.add(new Data("sx1",String.valueOf(al.get(0).getX())));
		params.add(new Data("sy1", String.valueOf(al.get(0).getY())));
		params.add(new Data("ex1", String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("ey1", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("sx2",String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("sy2", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("ex2", String.valueOf(al.get(0).getX())));
		params.add(new Data("ey2", String.valueOf(al.get(0).getY())));
		DataTable rst = sdb.executeTable("spBS_LocalDataQuerySE", params);
		return rst;
	}
	/**
	 * Get guandao width by chanquan
	 * @param context
	 * 			context
	 * @param cq
	 * 			chanquan
	 * @return
	 * 			the width
	 */
	public static int getGDWidthByCQ(Context context,String cq){
		SQLiteDatabase db1 = new SQLiteDatabase(context);
		int result = 0;
		DataCollection params = new DataCollection();
		params.add(new Data("name", cq));
		DataTable rst = db1.executeTable("aqBS_QueryGDCQByName", params);
		for(int i=0;i<rst.size();i++){
			DataCollection dc = rst.next();
			String width = dc.get("gdcx").Value.toString();
			result = Integer.valueOf(width);
		}
		return result;
	}
	/**
	 * Get GJ pictrue path by GJName
	 * @param context
	 * 			context
	 * @param name
	 * 			GJ name
	 * @return
	 * 			GJ picture path
	 */
	public static String getGJPathByName(Context context,String name){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		String paths = null;
		DataCollection param = new DataCollection();
		param.add(new Data("name", name));
		DataTable rst = sdb.executeTable("aqBS_GJTypePathByName", param);
		for(int i=0;i<rst.size();i++){
			DataCollection dc = rst.next();
			paths = dc.get("GJTypeImg").Value.toString();
		}
		return paths;
	}
	/**
	 * Transition the pictrue path to Bitmap
	 * @param path
	 * 			pictrue path
	 * @return
	 * 			Bitmap
	 */
	public static Bitmap TransPath2Bmp(String path){
		Bitmap bm = null;
		File file = new File(path);
		if(file.exists()){
			bm = BitmapFactory.decodeFile(path);
		}
		return bm;
	}
	/**
	 * Charge the GJName is exist in table or not exist
	 * @param context
	 * 			context
	 * @param name
	 * 			gjname
	 * @return
	 * 			the result : true is exist ; false is not exist
	 */
	public static boolean IsGJNameExist(Context context,String name){
		boolean rst = false;
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("name",name));
		DataTable dt = sdb.executeTable("aqBS_GJTypePathByName", param);
		if(dt.size() > 0)
			rst = true;
		return rst;
	} 
	/**
	 * Charge the GDName is exist in table or not exist
	 * @param context
	 * 			context
	 * @param name
	 * 			gdname
	 * @return
	 * 			the result : true is exist ; false is not exist
	 */
	public static boolean IsGDNameExist(Context context,String name){
		boolean rst = false;
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("name",name));
		DataTable dt = sdb.executeTable("aqBS_QueryGDCQByName", param);
		if(dt.size() > 0)
			rst = true;
		return rst;
	}
	/**
	 * Update GD information 
	 * @param context
	 * 			context
	 * @param id
	 * 			the GDID
	 * @param name
	 * 			the GDName
	 */
	public static void UpdateGDTypeInfo(Context context,String id,String name,String path){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id", id));
		param.add(new Data("name",name));
		param.add(new Data("path",path));
		sdb.execute("aqBS_GJTypeUpdate", param);
	}
	/**
	 * Delete information from GD table
	 * @param context
	 * 			context
	 * @param id
	 * 			GDID
	 */
	public static void DeleteGDType(Context context,String id){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id", id));
		sdb.execute("aqBS_GJTypeDel", param);
	}
	/**
	 * Compress the Bitmap
	 * @param image
	 * 			the Bitmap to be compressed
	 * @param options
	 * 			the compress  ratio
	 * @return
	 * 			the new bitmap
	 */
	public static Bitmap compressImage(Bitmap image,int options ){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
		image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
		/*while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        } */ 
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
		return bitmap;  
	}
	/**
	 * Get the pictrue by the path if the size of pictrue > 1KB ,compressing the pictrue
	 * @param path
	 * 			pictrue path
	 * @return
	 * 			the picture's bitmap 
	 */
	public static Bitmap compressImage4Filepath(String path){
		File file = new File(path);
		if(file.exists()){
			Bitmap bm = BitmapFactory.decodeFile(path);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();   
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
			int options = 100;  
			while ( baos.toByteArray().length / 1024>10) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
				baos.reset();//重置baos即清空baos  
				bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
				options -= 10;//每次都减少10  
			}  
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
			return bitmap;
		}
		return null;
	}
	/**
	 * Get the information of the point by the graphic uid
	 * @param context
	 * 			context
	 * @param uid
	 * 			graphic id
	 * @return
	 * 			the information of the point in SQL table
	 */
	public static DataTable getPointInfoByUID(Context context,String uid){
		DataTable rst = null;
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("uid", uid));
		rst = sdb.executeTable("aqBS_GJQueryByUID", param);
		return rst;
	}
	/**
	 * 	Get the information of the segment by the graphic uid
	 * @param context
	 * 			context
	 * @param uid
	 * 			graphic id
	 * @return
	 * 			the information of the segment in SQL table
	 */
	public static DataTable getSegmentInfoByUID(Context context, String uid) {
		// TODO 自动生成的方法存根
		DataTable rst = null;
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("uid", uid));
		rst = sdb.executeTable("aqBS_GDQueryByUID", param);
		return rst;
	}
	/**
	 * Get the information of the segment by the SID or EID
	 * @param context
	 * 			context
	 * @param pid
	 * 			SID or EID in the GD table
	 * @return
	 * 			the result of the search all
	 */
	public static DataTable getSegmentInfoByPID(Context context,String pid){
		DataTable rst = null;
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("sid", pid));
		param.add(new Data("eid", pid));
		rst = sdb.executeTable("aqBS_GDQueryByPID", param);
		return rst;
	}
	/**
	 * 	Delete the data in the SQL table by the graphic UID 
	 * @param context
	 * 			context
	 * @param uid
	 * 			graphic uid
	 */
	public static void DeletePointByID(Context context,String uid){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id", uid));
		sdb.execute("aqBS_GJDelByID", param);
	}
	/**
	 * 	Charge the segment is in the table segment2fiber
	 * @param context
	 * 			context
	 * @param id
	 * 			segment id
	 * @return
	 * 			the result of the segment to fiber
	 */
	public static DataTable ChargeSegIsinGD2GL(Context context,String id){
		DataTable rst = null;
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("gdid", id));
		rst = sdb.executeTable("aqBS_GD2GLQueryByGDID", param);
		return rst;
	}
	public static void DeleteSegmentByID(Context context, String uid) {
		// TODO 自动生成的方法存根
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id", uid));
		sdb.execute("aqBS_GDDelByID", param);
	} 
	/**
	 * 	Get the point information from the SQL table by X and Y
	 * @param context
	 * 			context
	 * @param pt
	 * 			point get X and Y
	 * @return
	 * 			the information of the point,if does not have the point return NULL
	 */
	public static DataTable getPointInfoByXY(Context context,Point pt){
		DataTable rst = null;
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("x", pt.getX()));
		param.add(new Data("y",pt.getY()));
		rst = sdb.executeTable("aqBS_GJQueryByXY", param);
		return rst;
	}
	/**
	 * 	Get the segment information from the SQL table by the points
	 * @param context
	 * 			context
	 * @param al
	 * 			the arraylist of points
	 * @return
	 * 			the information of the segment , if not found return NULL
	 */
	public static DataTable getSegmentInfoByPts(Context context,ArrayList<Point> al){
		DataCollection params = new DataCollection();
		params.add(new Data("sx1",String.valueOf(al.get(0).getX())));
		params.add(new Data("sy1", String.valueOf(al.get(0).getY())));
		params.add(new Data("ex1", String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("ey1", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("sx2",String.valueOf(al.get(al.size()-1).getX())));
		params.add(new Data("sy2", String.valueOf(al.get(al.size()-1).getY())));
		params.add(new Data("ex2", String.valueOf(al.get(0).getX())));
		params.add(new Data("ey2", String.valueOf(al.get(0).getY())));
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataTable rst = sdb.executeTable("spBS_LocalDataQuerySE", params);
		return rst;
	}
	/**
	 * @param context
	 * @param pts
	 */
	public static void saveGLLY(Context context,List<Point> pts){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		StringBuffer listData = new StringBuffer();
		//组合折点链表,以string类型存储到数据库
		for(int i=0;i<pts.size();i++){
			listData.append(pts.get(i).getX()+","+pts.get(i).getY());
			if(i != pts.size()-1)
				listData.append("/");
		}
		DataCollection params1 = new DataCollection();
		params1.add(new Data("gllyid", null));
		params1.add(new Data("gllyname","光缆-000"));
		params1.add(new Data("points",listData.toString()));
		params1.add(new Data("operator",FunctionHelper.userName));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");     
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String str = formatter.format(curDate);   
		params1.add(new Data("time",str));
		sdb = new SQLiteDatabase(context);
		sdb.execute("spBS_LocalGLLYAdd", params1);
	}
	/**
	 * @param context
	 * @param pts
	 */
	public static void saveDLLY(Context context,List<Point> pts){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		StringBuffer listData = new StringBuffer();
		//组合折点链表,以string类型存储到数据库
		for(int i=0;i<pts.size();i++){
			listData.append(pts.get(i).getX()+","+pts.get(i).getY());
			if(i != pts.size()-1)
				listData.append("/");
		}
		DataCollection params1 = new DataCollection();
		params1.add(new Data("dllyid", null));
		params1.add(new Data("dllyname","电缆-000"));
		params1.add(new Data("points",listData.toString()));
		params1.add(new Data("operator",FunctionHelper.userName));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");     
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String str = formatter.format(curDate);   
		params1.add(new Data("time",str));
		sdb = new SQLiteDatabase(context);
		sdb.execute("spBS_LocalDLLYAdd", params1);
	}
	/**
	 * @param context
	 * @param id
	 */
	public static void removeGLFromGLT(Context context,String id){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id",id));
		sdb.execute("spBS_LocalGLDel", param);
	}
	/**
	 * @param context
	 * @param id
	 */
	public static void removeGLFromGL2GD(Context context,String id){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id",id));
		sdb.execute("spBS_LocalGL2GDDel", param);
	}
	/**
	 * @param context
	 * @param id
	 * @return
	 */
	public static String getGLIDByGDID(Context context,String id){
		StringBuffer sb = new StringBuffer();
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id",id));
		DataTable rst = sdb.executeTable("spBS_LocalGL2GDQueryByGdid", param);
		for(int i=0;i<rst.size();i++){
			DataCollection dc = rst.next();
			sb.append(dc.get("glid").Value.toString());
			if(i != rst.size()-1)
				sb.append(",");
		}
		return sb.toString();
	}
	/**
	 * @param context
	 * @param id
	 * @return
	 */
	public static String getGLNameByID(Context context,String id){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id",id));
		String name = null;
		DataTable rst = sdb.executeTable("spBS_LocalGLQueryByID", param);
		for(int i=0;i<rst.size();i++){
			DataCollection dc = rst.next();
			name = dc.get("glname").Value.toString();
		}
		return name;
	}
	/**
	 * @param context
	 * @param id
	 * @param name
	 */
	public static void updateGLInformation(Context context,String id,String name){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id",id));
		param.add(new Data("name",name));
		sdb.execute("spBS_UpdateGLByID", param);
	}
	/**
	 * @param context
	 * @param pts
	 */
	public static void removeGLLY(Context context,String pts){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("points",pts));
		sdb.execute("spBS_DeleteGLLY", param);
	}
	/**
	 * @param context
	 * @param pts
	 */
	public static void removeDLLY(Context context,String pts){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("points",pts));
		sdb.execute("spBS_DeleteDLLY", param);
	}
	/**
	 * @param context
	 * @param id
	 * @return
	 */
	public static String QueryGJIMGPath(Context context,String id){
		String path = null;
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection param = new DataCollection();
		param.add(new Data("id",id));
		DataTable result = sdb.executeTable("spBSGJTYPEQueryByID", param);
		for(int i=0;i<result.size();i++){
			DataCollection dc = result.next();
			path = dc.get("GJTypeImg").Value.toString();
		}
		return path;
	}
	public static void addGJType(Context context,String name,String path){
		SQLiteDatabase sdb = new SQLiteDatabase(context);
		DataCollection pramas = new DataCollection();
		pramas.add(new Data("gjtypeid", null));
		pramas.add(new Data("gjtypename",name));
		if(path.equals("")){
			Toast.makeText(context, "图片错误，请重新选择！", Toast.LENGTH_SHORT).show();
			return;
		}
		pramas.add(new Data("gjtypeimg", path));
		sdb.execute("aqBS_GJTypeAdd", pramas);
	}

}
