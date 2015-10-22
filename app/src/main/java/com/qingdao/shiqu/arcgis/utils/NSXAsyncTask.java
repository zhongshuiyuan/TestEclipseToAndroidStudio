package com.qingdao.shiqu.arcgis.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/***
 * 线程任务处理类
 * @author MinG
 *
 */
@SuppressLint("HandlerLeak") 
public abstract class NSXAsyncTask<Params, Result>
{
	public Params[] params;
	public NSXAsyncTask()
	{
		
	}
	
	/***
	 * 开始执行任务
	 * 
	 * @param params 输入参数
	 * @return AsyncTaskServer对象
	 */
	public final NSXAsyncTask<Params, Result> execute(Params... params)
	{
		try
		{
			this.params = params;
			new TaskThread().start();
		}
		catch(Exception ex)
		{
			Log.e("异步任务", ex.getMessage());
		}
		return this;
	}
	
	/***
	 * 后台执行任务
	 * 
	 * @param params 输入参数
	 * @return 返回执行是否成功
	 */
	protected abstract Result doInBackground(Params... params);
	
	/***
	 * 执行与UIView操作相关的方法
	 * 
	 * @param rs 返回结果
	 */
	protected abstract void onPostExecute(Result rs);
	
	
	/***
	 * 线程调度任务
	 * @author MinG
	 *
	 */
	class TaskThread  extends Thread
	{
		public void run()
		{
			if(Looper.myLooper() == null)
			{
				Looper.prepare();
			}
			if(true)
			{
				Message msg = updateUIHandler.obtainMessage();
				msg.obj = doInBackground(params);
				updateUIHandler.sendMessage(msg);
			}
			Looper.loop();
		}
	}
	
	/***
	 * UIView消息处理对象
	 */
	Handler updateUIHandler = new Handler()
	{
		@SuppressWarnings("unchecked") 
		@Override
		public void handleMessage(Message msg)
		{
			onPostExecute((Result)msg.obj);
		}
	};
}
