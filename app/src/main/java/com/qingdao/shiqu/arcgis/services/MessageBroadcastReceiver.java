package com.qingdao.shiqu.arcgis.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageBroadcastReceiver extends BroadcastReceiver
{
	MessageService messageService;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			// 判断是否为登录状态
			if(!CatvService.IsLogin)
			{
				Log.i("接收广播成功", "用户未登录，不能获取消息！");
				return;
			}
			Log.i("接收广播成功", "准备调用下载消息方法");
			if(CatvService.getOperNo() == null || CatvService.getOperNo().equals("")) { return; }
			messageService = new MessageService(context);
			messageService.start();// 调用下载消息方法
		}
		catch(Exception ex)
		{
			Log.e("接收广播", ex.getMessage());
		}
	}

}
