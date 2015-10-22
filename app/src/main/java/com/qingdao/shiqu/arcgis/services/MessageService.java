package com.qingdao.shiqu.arcgis.services;

import java.util.Timer;
import java.util.TimerTask;

import com.qingdao.shiqu.arcgis.billtype.BillType;
import com.qingdao.shiqu.arcgis.billtype.BillTypeConvert;

import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MessageService
{
	CatvService service;
	Data data;
	String message;
	Context context;
	mThreadStop treadStop;

	Timer timer = new Timer();
	TimerTask task;
	int timeout = 5;

	/* 提示用 */
	Intent intent;
	PendingIntent pendingIntent;
	// 声明Notification对象
	Notification notification;

	/*
	 * 构造函数
	 */
	public MessageService( Context context )
	{
		this.context = context;
		this.service = new CatvService();
	}

	/*
	 * 下载消息
	 */
	public void start() throws Exception
	{
		try
		{
			Log.i("下载消息", "下载消息");
			treadStop = new mThreadStop();
			Thread myThread = new Thread(treadStop);
			myThread.start();
			task = new TimerTask()
			{
				public void run()
				{
					if(timeout > 0)
					{
						timeout--;
					}
					else
					{
						Log.d("下载工单服务", "线程终止执行");
						handler.sendEmptyMessage(0);
						treadStop.stopRequest();
						timer.cancel();
					}
				}

			};
		}
		catch(Exception ex)
		{
			throw ex;
		}
		// timer.scheduleAtFixedRate(task,0,1000);
	}

	/*
	 * 获取系统消息
	 */
	public boolean getMessage()
	{
		dataCheck();
		service.setContex(context);
		service.setShowMessage(false);
		service.setBussType(BillTypeConvert.ConvertToCode(BillType.获取消息数));
		try
		{
			return service.post();
		}
		catch(Exception e)
		{
			Log.e("获取消息", e.getMessage());
		}
		return false;
	}

	/*
	 * 检查是否有消息未确认
	 */
	public void dataCheck()
	{
		try
		{
			service = new CatvService();
			SQLiteDatabase db = new SQLiteDatabase(context);
			DataCollection params = new DataCollection();
			DataTable table = db.executeTable("spDataCheckList", null);
			data = new Data();
			int size = 0;
			if(table == null)
			{
				return;
			}
			else
			{
				size = table.size();
				for(int i = 0; i < size; i++)
				{
					params.clear();
					data = table.get(i).get("NETSEQNO");
					service.setNetSeqNo(data.Value.toString());
					params.add(data);
					service.setContex(context);
					service.setDataCollection(params);
					service.setBussType(BillTypeConvert.ConvertToCode(BillType.消息处理));
					if(service.post())
					{
						Log.i("消息确认", "成功");
						db.execute("spDataCkeckDel", params);
						params.clear();
					}
					Log.i("消息确认", "无新消息");
				}
			}
		}
		catch(Exception e)
		{
			Log.e("消息确认错误", e.getMessage());
		}
	}

	class mThreadStop extends Object implements Runnable
	{
		private Thread runThread;

		public void run()
		{
			runThread = Thread.currentThread();
			if(Looper.myLooper() == null)
			{
				Looper.prepare();
			}
			if(true)
			{
				timer.cancel();
				handler.sendEmptyMessage(1);
			}
			Looper.loop();
		}

		public void stopRequest()
		{
			if(runThread != null)
			{
				runThread.interrupt();
			}
		}
	}

	Handler handler = new Handler()
	{
		@SuppressLint("HandlerLeak") 
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case 1:
				if(getMessage())
				{
					/**
					 * 发送通知到主页面的提醒按钮
					 */
//					NotificationManager.UNMessageNotifier.setMessage(service.getDataTable().get(0).get(0).Value.toString());
				}
				break;
			default:
				break;
			}
		}
	};
}