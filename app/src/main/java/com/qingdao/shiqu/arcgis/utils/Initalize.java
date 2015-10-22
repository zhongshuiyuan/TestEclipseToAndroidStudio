package com.qingdao.shiqu.arcgis.utils;

import Eruntech.BirthStone.Core.Helper.File;
import Eruntech.BirthStone.Core.Sqlite.SQLiteOpenbase;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Initalize
{
	public static String DbName = "gisdb.s3db";
	public static String appPath;

	/**
	 * 创建数据库文件到应用程序databases目录
	 * 
	 * @param context 应用程序上下文
	 * */
	public static void createDatabase(Context context)
	{
		try
		{
			appPath = context.getApplicationContext().getFilesDir().getAbsolutePath();
			appPath = appPath.replace("files", "databases") + "/";
			SQLiteOpenbase.DB_NAME=DbName;
			SQLiteOpenbase.DB_PATH=appPath;
			// 如果数据库文件不存在则创建数据库文件
			if(!File.exists(appPath + DbName))
			{
				// 写入数据库文件
				File.write(appPath + DbName, File.getAssetsByte(DbName, context));
			}
			/*else
			{
				String pathstr = File.getSDCardPath()+"/"+DbName;
				File.write(pathstr, File.readFile(appPath + DbName));
			}*/
			SharedPreferences sharedPreferences = context.getSharedPreferences("checked", Context.MODE_PRIVATE);
			Editor edit = sharedPreferences.edit();
			edit.putString("DB_NAME", DbName);
			edit.putString("DB_PATH", appPath);
			edit.commit();
		}
		catch(Exception ex)
		{
			Log.e("初始化错误：", ex.getMessage());
		}
	}
	/**
	 * 说 明：将数据库拷贝出来，方便查看
	 * @param newPath
	 */
	public static void copySQL(String newPath){
		File.write(newPath, File.readFile(appPath + DbName));
	}

}
