package com.qingdao.shiqu.arcgis.helper;

import java.util.HashMap;

import com.qingdao.shiqu.arcgis.utils.ValueList;


/**
 * 角色菜单权限状态缓冲区
 * 
 * @author MinG
 * 
 */
public class RoleForMeunBuffer
{
	/**
	 * 菜单缓存
	 */
	static HashMap<String, ValueList> menuBuffer;

	/**
	 * 将角色权限添加到缓冲区
	 * 
	 * @param roleID 角色ID
	 * @param functionID 权限ID
	 */
	public static void put(String roleID, String funcID)
	{
		// 判断缓存是否为null
		if(menuBuffer == null)
		{
			menuBuffer = new HashMap<String, ValueList>();
		}
		// 确认是否创建该角色缓冲
		if(menuBuffer.containsKey(roleID))
		{
			ValueList indexList = menuBuffer.get(roleID);
			if(!indexList.contains(funcID))
			{
				indexList.add(funcID);
			}
		}
		else
		{
			ValueList indexList = new ValueList();
			indexList.add(funcID);
			menuBuffer.put(roleID, indexList);
		}
	}

	/**
	 * 返回角色权限缓冲
	 * 
	 * @param roleID
	 * @return
	 */
	public static ValueList get(String roleID)
	{
		// 判断缓存是否为null
		if(menuBuffer != null) { return menuBuffer.get(roleID); }
		return null;
	}

	/**
	 * 移除角色权限缓冲
	 * 
	 * @param roleID
	 */
	public static void remove(String roleID)
	{
		// 判断缓存是否为null
		if(menuBuffer != null)
		{
			menuBuffer.remove(roleID);
		}
	}

	/**
	 * 移除角色的指定权限
	 * 
	 * @param roleID 角色ID
	 * @param funcID 权限ID
	 */
	public static void remove(String roleID, String funcID)
	{
		// 判断缓存是否为null
		if(menuBuffer != null)
		{
			// 确认是否该角色缓冲
			if(menuBuffer.containsKey(roleID))
			{
				ValueList indexList = menuBuffer.get(roleID);
				indexList.remove(funcID);
			}
		}
	}

	/**
	 * 检测角色缓存中是否存在指定权限
	 * @param roleID 角色
	 * @param funcID 权限
	 * @return 是否存在指定权限
	 */
	public static Boolean contains(String roleID, String funcID)
	{
		// 确认是否该角色缓冲
		if(menuBuffer != null && menuBuffer.containsKey(roleID))
		{
			ValueList indexList = menuBuffer.get(roleID);
			return indexList.contains(funcID);
		}

		return false;
	}

}
