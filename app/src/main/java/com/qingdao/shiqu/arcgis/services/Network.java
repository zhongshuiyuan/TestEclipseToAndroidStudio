package com.qingdao.shiqu.arcgis.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class Network
{
	public static boolean checkNetwork(final Context context, Boolean isShow)
	{
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cwjManager.getActiveNetworkInfo() != null) flag = cwjManager.getActiveNetworkInfo().isAvailable();
		if(!flag && isShow)
		{
			Toast.makeText(context, "没有可用的网络连接！", Toast.LENGTH_SHORT).show();
//			Builder b = new AlertDialog.Builder(context).setTitle("没有可用的网络").setMessage("请开启3G或WIFI网络连接");
//			b.setPositiveButton("设置网络", new DialogInterface.OnClickListener()
//			{
//				public void onClick(DialogInterface dialog, int whichButton)
//				{
//					if(android.os.Build.VERSION.SDK_INT > 10)
//					{
//						// 3.0以上打开设置界面
//						context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
//					}
//					else
//					{
//						context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
//					}
////					Intent mIntent = new Intent("/");
////					ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
////					mIntent.setComponent(comp);
////					mIntent.setAction("<SPAN class=hilite>android</SPAN>.intent.action.VIEW");
////					context.startActivity(mIntent);
//				}
//			}).setNeutralButton("取消", new DialogInterface.OnClickListener()
//			{
//				public void onClick(DialogInterface dialog, int whichButton)
//				{
//					dialog.cancel();
//				}
//			}).create();
//			b.show();
		}
		return flag;
	}
}
