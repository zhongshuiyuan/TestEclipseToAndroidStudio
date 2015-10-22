package com.qingdao.shiqu.arcgis.helper;

import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.content.Context;
import android.widget.Toast;

/**
 * 初始化角色菜单缓冲
 * @author MinG
 *
 */
public class RoleForMenuBufferInitialize
{
	Context context = null;
	SQLiteDatabase db = null;
	/**
	 * 角色初始化构造函数
	 * @param context 上下文
	 * @param db 数据库访问对象
	 */
	public RoleForMenuBufferInitialize(Context context)
	{
		this.context = context;
	}
	
	/**
	 * 载入菜单权限到缓存
	 */
	public void loadMenuToBuffer()
	{
		try
		{
			DataTable roleTable, menuTable;
			db = new SQLiteDatabase(context);
			//取出所有状态为1的角色
			roleTable = db.executeTable("spBS_RoleList", null);
			//循环角色列表
			for(DataCollection roles: roleTable)
			{
				//取出该角色的所有菜单
				menuTable = db.executeTable("spBS_MenuList", roles);
				for(DataCollection menus: menuTable)
				{
					if(!menus.get("ParentID").getValue().toString().equals("0"))
					{
						RoleForMeunBuffer.put(roles.get("RoleID").getValue().toString(), menus.get("FuncID").getValue().toString().replace('\n', ' ').trim());
					}
				}
			}
		}
		catch(Exception ex)
		{
			Toast.makeText(context, "初始化缓存错误，原因："+ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
}
