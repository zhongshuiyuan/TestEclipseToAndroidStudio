package com.qingdao.shiqu.arcgis.listener;

import com.qingdao.shiqu.arcgis.activity.Main;

import Eruntech.BirthStone.Base.Forms.Helper.MessageBox;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.os.Message;

/**************************************
 *
 *      作  者：  潘跃瑞 
 *
 *      功  能：  GPS事件监听
 *
 *      时  间：  2014-10-30
 *
 *      版  权：  青岛盛天科技有限公司 
 *      
 ****************************************/
public class GPSListener implements Listener
{
	/*********************GPS监听事件*****************/
	/**
	 * GPS 状态改变监听
	 */
	@Override
	public void onGpsStatusChanged(int event)
	{
		switch(event)
		{
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				System.out.println("GPS_EVENT_FIRST_FIX------------------");
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				System.out.println("GPS_EVENT_SATELLITE_STATUS------------------");
				break;
			case GpsStatus.GPS_EVENT_STARTED:
				System.out.println("GPS_EVENT_STARTED------------------");
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				System.out.println("GPS_EVENT_STOPPED------------------");
				break;
		}
		/**
		 * 计算卫星数
		 */
		// GpsStatus gpsStatus = locationManager.getGpsStatus(null);
		// if(gpsStatus == null){
		// Toast.makeText(LocalTiledLayer.this, "
		// Toast.LENGTH_SHORT).show();
		// }else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
		// int maxSatellites = gpsStatus.getMaxSatellites();
		// Iterator<GpsSatellite> iterator =
		// gpsStatus.getSatellites().iterator();
		// int count = 0;
		// while(iterator.hasNext() && count <= maxSatellites){
		// iterator.next();
		// count++;
		// }
		// NumSatellites.setText(""+count);
		// }
	}

}
