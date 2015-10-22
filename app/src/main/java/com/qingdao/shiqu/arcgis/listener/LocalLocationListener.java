package com.qingdao.shiqu.arcgis.listener;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**************************************
 *
 *      作  者：  潘跃瑞 
 *
 *      功  能：  
 *
 *      时  间：  2014-10-30
 *
 *      版  权：  青岛盛天科技有限公司 
 *      
 ****************************************/
public class LocalLocationListener implements LocationListener
{
	private Handler handler;
	Message message;
	public LocalLocationListener(Handler handler)
	{
		this.handler = handler;
		
	}
	
	long oldGPSTime = 0;
	/**
	 * 当位置发生改变时调用
	 */
	@Override
	public void onLocationChanged(Location location)
	{
		
//		if(oldGPSTime != 0){
//			long time = location.getTime();
//			if(time - oldGPSTime <= 1000 * 5)  
//				return; 
//		}
//		oldGPSTime = location.getTime();
		message = Message.obtain(handler);
		message.obj = location;
		message.sendToTarget();
	}

	/**
	 * 当关闭gps时调用
	 */
	@Override
	public void onProviderDisabled(String provider)
	{
		//Toast.makeText(Main.this, "gps已关闭无法获取到位置", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 当开启gps时调用
	 */
	@Override
	public void onProviderEnabled(String provider)
	{
		//Toast.makeText(Main.this, "gps已开", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		switch(status)
		{
		case LocationProvider.AVAILABLE:// 当前gps为可见状态
			break;
		case LocationProvider.OUT_OF_SERVICE:// 当前gps不在服务区
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:// 为暂停状态
			break;
		}
	}
}
