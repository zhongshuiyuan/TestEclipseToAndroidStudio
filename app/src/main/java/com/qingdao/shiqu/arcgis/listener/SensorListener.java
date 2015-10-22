package com.qingdao.shiqu.arcgis.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.Message;

/**************************************
 *
 *      作  者：  潘跃瑞 
 *
 *      功  能：  方向传感器监听事件
 *
 *      时  间：  2014-10-30
 *
 *      版  权：  青岛盛天科技有限公司 
 *      
 ****************************************/
public class SensorListener implements SensorEventListener
{
   private Handler handler;
   public SensorListener(Handler handler)
   {
	   this.handler = handler;
   }
	@Override
	public void onSensorChanged(SensorEvent event)
	{
		try
		{
			Message message = Message.obtain(handler);
			message.obj = event.values[0];
			message.sendToTarget();
			//handler.sendEmptyMessage();
//			Message message = Message.obtain(handler);
//			message
//			han
//			new NSXAsyncTask<Object, Object>()
//			{
//				@Override
//				protected Object doInBackground(Object... params)
//				{
//					if(pictureMarkerSymbol != null)
//					{
//						
//					}
//			     	return null;
//				}
//
//				@Override
//				protected void onPostExecute(Object rs)
//				{
//				}
//			 }.execute();
		} catch (Exception e)
		{
		//	MessageBox.showMessage(Main.this,"11", e.getMessage());
			//Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		
	}

}
