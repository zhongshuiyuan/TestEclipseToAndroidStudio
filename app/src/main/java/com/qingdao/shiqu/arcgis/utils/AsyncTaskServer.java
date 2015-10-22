package com.qingdao.shiqu.arcgis.utils;

import com.qingdao.shiqu.arcgis.billtype.BillType;
import com.qingdao.shiqu.arcgis.billtype.BillTypeConvert;
import com.qingdao.shiqu.arcgis.services.CatvService;

import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/***
 * 以异步方式访问服务器服务类
 * 
 * @author MinG
 * 
 * @param <Params> 输入参数
 * @param <Result> 返回结果
 */
public abstract class AsyncTaskServer<Params, Result>
{
	protected CatvService bossService = null;
	Context context = null;
	DataCollection dataCollection = null;
	BillType pathTyep;
	boolean showMessage = false;
	public Params[] params;

	/***
	 * 
	 * @param context Activity上下文
	 * @param dataCollection 参数集合
	 * @param pathTyep 服务端调用枚举
	 * @param showMessage 是否显示服务端返回消息
	 */
	public AsyncTaskServer( Context context, DataCollection dataCollection, BillType pathType, boolean showMessage )
	{
		this.context = context;
		this.dataCollection = dataCollection;
		this.pathTyep = pathType;
		this.showMessage = showMessage;
	}

	/***
	 * 开始执行任务
	 * 
	 * @param params 输入参数
	 * @return AsyncTaskServer对象
	 */
	public final AsyncTaskServer<Params, Result> execute(Params... params)
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
	protected boolean doInBackground(Params... params)
	{
		try
		{
			bossService = new CatvService(context);
			bossService.setShowMessage(showMessage);
			bossService.setBussType(BillTypeConvert.ConvertToCode(pathTyep));
			bossService.setDataCollection(dataCollection);
			if(bossService.post()) { return true; }
		}
		catch(Exception ex)
		{

		}
		return false;
	}

	/***
	 * 执行与UIView操作相关的方法
	 * 
	 * @param value 1成功，0失败
	 * @param rs 返回Boss结果集
	 */
	protected abstract void onPostExecute(Integer value, DataTable rs);

	/***
	 * 线程调度任务
	 * 
	 * @author MinG
	 * 
	 */
	class TaskThread extends Thread
	{
		public void run()
		{
			try
			{
				if(Looper.myLooper() == null)
				{
					Looper.prepare();
				}
				if(doInBackground(params))
				{
					Message msg = updateUIHandler.obtainMessage(1);
					msg.obj = bossService.getDataTable();
					updateUIHandler.sendMessage(msg);
				}
				else
				{
					Message msg = updateUIHandler.obtainMessage(0);
					updateUIHandler.sendMessage(msg);
				}
				Looper.loop();
			}
			catch(Exception ex)
			{

			}
		}

	}

	/***
	 * UIView消息处理对象
	 */
	Handler updateUIHandler = new Handler()
	{
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg)
		{
			try
			{
				if(msg.obj != null)
				{
					onPostExecute(msg.what, (DataTable) msg.obj);
				}
				else
				{
					onPostExecute(msg.what, null);
				}
			}
			catch(Exception ex)
			{

			}
		}
	};

}
