package com.qingdao.shiqu.arcgis.helper;

import com.qingdao.shiqu.arcgis.activity.Login;

import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.content.Context;

/***
 * 权限辅助类
 * @author MinG
 *
 */
public class FunctionHelper
{
	/**
	 * 用户角色参数
	 */
	public static DataCollection USER_ROLE; 
	
	public static String userName;
	
	/**
	 *获取用户权限集合 
	 * @param context 屏幕上下文
	 */
	public static void getFunction(Context context)
	{
		try
		{
			SQLiteDatabase db = new SQLiteDatabase(context);
			// 获取用户权限代码
			DataTable table = db.executeTable("spBS_MenuList", USER_ROLE);
			StringBuilder funcString = new StringBuilder();
			for (DataCollection datas : table)
			{
				funcString.append(datas.get(2).Value.toString() + ",");
			}
			Activity.setFunction(funcString.deleteCharAt(funcString.lastIndexOf(",")).toString());
		}
		catch(Exception ex)
		{
			
		}
	}
}
