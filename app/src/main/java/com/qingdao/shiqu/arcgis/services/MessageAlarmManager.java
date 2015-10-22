package com.qingdao.shiqu.arcgis.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

public class MessageAlarmManager
{
	AlarmManager alarmManager, am;
	PendingIntent pendingIntent, pi;
	Intent intent, it;

	public void startDownBill(Activity ac, int intentType)
	{
		try
		{
			if(intentType == 0)
			{
				// 下载工单
				// 设定广播被UpdateReceiver接收
				intent = new Intent(ac, BillBroadcastReceiver.class);
			     
				pendingIntent = PendingIntent.getBroadcast(ac, 0, intent, 0);
				alarmManager = (AlarmManager) ac.getSystemService(Activity.ALARM_SERVICE);
				// 从当前时间开始每隔一定时间发送广播
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60*5, pendingIntent);
			}
			if(intentType == 1)
			{
				// 下载消息
				it = new Intent(ac, MessageBroadcastReceiver.class);// 设定广播被MessageReceiver接收
				pi = PendingIntent.getBroadcast(ac, 0, it, 0);
				am = (AlarmManager) ac.getSystemService(Activity.ALARM_SERVICE);
				am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000*60*6, pi);
			}
		}
		catch(Exception e)
		{
			Log.e("广播的形式下载消息和工单", e.toString());
		}
	}

	public void closeServices()
	{
		alarmManager.cancel(pendingIntent);// 停止下载工单广播
		am.cancel(pi);// 停止下载消息广播
	}
}
