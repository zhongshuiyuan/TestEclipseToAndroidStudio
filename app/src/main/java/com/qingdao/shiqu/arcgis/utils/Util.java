package com.qingdao.shiqu.arcgis.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.qingdao.shiqu.arcgis.Des;
import com.qingdao.shiqu.arcgis.R;

/**************************************
 *
 *      作  者：  潘跃瑞 
 *
 *      功  能：  工具类
 *
 *      时  间：  2014-10-30
 *
 *      版  权：  青岛盛天科技有限公司 
 *      
 ****************************************/
public class Util extends Object
{
	/**
	 * 偏移坐标
	 */
	private static final double SWEWING_X = 300005.00;
	private static final double SWEWING_Y = 3890001.00;
	
	/**
	 * 
	 *      功  能：将搜索到的数据转换成线  
	 *		@param value
	 *		@return
	 */
   public static List<Point> convertLines(String value)
   {
	   List<Point> list  = new ArrayList<Point>();
	   StringTokenizer tokenizer = new StringTokenizer(value, "POINT()");  
		String points = "";
		while (tokenizer.hasMoreTokens())
		{
			points = tokenizer.nextToken();
			//Toast.makeText(Main.this, tokenizer.nextToken(), Toast.LENGTH_SHORT).show();
		}
		String[] split = points.split(",");
		for (int i = 0; i < split.length; i++) 
		{
			String string = split[i].trim();
			String[] split2 = string.split(" ");
			Point p = new Point();
			p.setXY(Double.parseDouble(split2[0].trim()), Double.parseDouble(split2[1].trim()));
			list.add(p);
		}
		return list;
   }
   /**
    * 
    *      功  能：搜索的数据转换成点  
    *		@param value
    *		@return
    */
   public static Point convertPoint(String value)
   {
	//   List<Point> list  = new ArrayList<Point>();
	   Point point = new Point();
	   StringTokenizer tokenizer = new StringTokenizer(value, "LINESTRING()");  
		String points = "";
		while (tokenizer.hasMoreTokens())
		{
			points = tokenizer.nextToken();
		}
		String string = points.trim();
		String[] split2 = string.split(" ");
		point.setXY(Double.parseDouble(split2[0].trim()), Double.parseDouble(split2[1].trim()));
		return point;
   }
   /**
    * 
    *      功  能：获取屏幕中心坐标  
    *		@param activity
    *		@return
    */
   public static Point getCenterPoint(Activity activity)
   {
	   DisplayMetrics dm = new DisplayMetrics();
	   activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
	   float screenWidth = dm.widthPixels / 2;
	   float screenHeight = dm.heightPixels / 2;
	   return new Point(screenWidth,screenHeight);
   }
  /**
   * 
   *      功  能：校正坐标点，将wgs84坐标转换成北京54，并纠正偏移量 
   *		@param point
   *		@return
   */
   public static Point checkPoint(Point point)
   {
	   	Point newPoint = new Point();
	   	SpatialReference sr4326 = SpatialReference.create(4326); // wgs84坐标
		SpatialReference sr2437 = SpatialReference.create(2437);// 北京54坐标--120E
		point = (Point) GeometryEngine.project(point, sr4326, sr2437);
		double x = point.getX() - SWEWING_X;
		double y = point.getY() - SWEWING_Y;
		newPoint.setXY(x, y);
		
		return newPoint;
   }
   /**
    * 将配置文件写入sd卡
    */
	private void writeFiles(Context context)
	{
		try
		{
			InputStream inputStream = context.getAssets().open("conf.xml");
			InputStream open = null;
			try
			{
				open = new Des().decode(inputStream);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			File file = new File("/mnt/sdcard/layers/conf.xml");
			OutputStream outputStream = new FileOutputStream(file);
			byte[] buff = new byte[1024];
			int read;
			while((read = open.read(buff)) != -1)
			{
				if(read == 0)
				{
					read = open.read();
					if(read < 0) break;
					outputStream.write(read);
					continue;
				}
				outputStream.write(buff, 0, read);
			}
			open.close();
			outputStream.close();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * dip转像素
	 * @param context Context
	 * @param dpValue dip值
	 * @return 像素值
	 */
	public static int dip2px(Context context, float dpValue) {
		final float density = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * density + 0.5f);
	}

	/**
	 * 判断设备是否联网
	 * @param context 上下文
	 * @return true表示已联网，false表示没有联网
	 */
	public static boolean isConnectToInternet(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}
