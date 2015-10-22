package com.qingdao.shiqu.arcgis.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BillBroadcastReceiver extends BroadcastReceiver
{
//	BillDownLoadService downBill;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			//判断是否为登录状态
			if(!CatvService.IsLogin)
			{
				Log.i("接收广播成功", "用户未登录，不能获取工单！");
				return;
			}
			if(CatvService.getOperNo()==null || CatvService.getOperNo().equals(""))
			{
				return;
			}
//			if(!Activity.getFunctionList().contains("000101"))
//			{
//				return;
//			}
			Log.i("接收广播成功", "准备调用下载工单方法");
//			downBill = new BillDownLoadService(context);
//			// 调用下载工单方法
//			downBill.start();
		}
		catch(Exception ex)
		{
			Log.e("接收广播", ex.getMessage());
		}
	}
}
