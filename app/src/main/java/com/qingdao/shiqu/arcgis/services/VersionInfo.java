package com.qingdao.shiqu.arcgis.services;



import com.qingdao.shiqu.arcgis.billtype.BillType;
import com.qingdao.shiqu.arcgis.billtype.BillTypeConvert;

import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


/**
 * 获取版本信息
 * */
public class VersionInfo
{
	Activity context;
	CatvService catvService;
	DataCollection collection;
	Handler handler;
	boolean isNewVersion = false;
    String TAG = "VersionInfo";
	// 下载地址
	String downloadUrl = null;
	// 版本
	String version = null;
	// 文件名
	String fileName = null;
	// 更新内容
	String content = null;

	public VersionInfo( Activity context )
	{
		this.context = context;
	}

	public VersionInfo( Activity context, Handler handler )
	{
		this.context = context;
		this.handler = handler;
	}

	/**
	 * 检查版本，并返回是否有新版本
	 * */
	public void checkVersion()
	{
		new ThreadGetServerVersion().start();
	}

	class ThreadGetServerVersion extends Thread
	{
		public void run()
		{
			if(Looper.myLooper() == null)
			{
				Looper.prepare();
			}
			try
			{
//				while(true)
//				{
					if(getServerVersion())
					{
						Log.e("版本", "获取成功");
						Log.e("是否新版本", String.valueOf(isNewVersion));
						if(handler != null)
						{
							handler.sendEmptyMessage(1);
						}
//						break;
					}
					else
					{
						Log.e("版本", "获取失败");
						if(handler != null)
						{
							handler.sendEmptyMessage(0);
						}
					}
//					sleep(10000);
//				}
			}
			catch(Exception ex)
			{
				Log.e("版本信息错误", ex.getMessage());
			}
			Looper.loop();
		}
	}

	/**
	 * 作者： 付琳 2012-11-27 获取服务器最新客户端信息，检查是否需要更新
	 */
	private synchronized boolean getServerVersion()
	{
		try
		{
			catvService = new CatvService();
			catvService.setContex(context);
			catvService.setShowMessage(false);
			catvService.setBussType(BillTypeConvert.ConvertToCode(BillType.获取最新版本));
			DataCollection dataCollection = new DataCollection();
			String version = findVersion();
			dataCollection.add(new Data("version", version));
			dataCollection.add(new Data("filename", "ChengYangWeiXiu.apk"));
			catvService.setDataCollection(dataCollection);

			if(catvService.post())
			{
				collection = catvService.getDataTable().get(0);
				if(collection != null)
				{
					version = collection.get("versionid").getValue().toString();// 版本
					downloadUrl = collection.get("url").getValue().toString(); // 下载地址
					fileName = collection.get("filename").getValue().toString(); // 文件名
					content = collection.get("updatecontent").getValue().toString(); // 更新内容
				}
				String currentVersion = context.getApplicationVersion();
				Log.i(TAG, "当前版本:"+currentVersion);
				Log.i(TAG, "最新版本:"+version);
				if(!version.equals(currentVersion))
				{
					isNewVersion = true;
				}
			}
		}
		catch(Exception ex)
		{
			Log.e("获取版本错误", ex.getMessage());
		}
		return isNewVersion;
	}
	/**
	 * 获取当前版本号
	 * @return
	 */
    private String findVersion()
    {
    	PackageManager manager = context.getPackageManager();
    	String value = "";
    	try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
		    value = info.versionName;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	return ""+value;
    }
	public Handler getHandler()
	{
		return handler;
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	/**
	 * 是否有新版本
	 * */
	public synchronized boolean isNewVersion()
	{
		return isNewVersion;
	}

	/**
	 * 更新地址
	 * */
	public String getDownloadUrl()
	{
		return downloadUrl;
	}

	/**
	 * 更新版本号
	 * */
	public String getVersion()
	{
		return version;
	}

	/**
	 * 文件名
	 * */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * 更新内容
	 * */
	public String getContent()
	{
		return content;
	}

}
