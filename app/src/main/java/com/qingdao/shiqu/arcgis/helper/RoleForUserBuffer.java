package com.qingdao.shiqu.arcgis.helper;

import java.util.HashMap;

import com.qingdao.shiqu.arcgis.utils.ValueList;


/**
 * 角色用户状态缓冲区
 * 
 * @author MinG
 * 
 */
public class RoleForUserBuffer
{
	/**
	 * 菜单缓存
	 */
	static HashMap<String, ValueList> userBuffer;

	/**
	 * 将角色用户ID添加到缓冲区
	 * 
	 * @param roleID 角色ID
	 * @param userID 用户ID
	 */
	public static void put(String roleID, String userID)
	{
		// 判断缓存是否为null
		if(userBuffer == null)
		{
			userBuffer = new HashMap<String, ValueList>();
		}
		// 确认是否创建该角色缓冲
		if(userBuffer.containsKey(roleID))
		{
			ValueList indexList = userBuffer.get(roleID);
			if(!indexList.contains(userID))
			{
				indexList.add(userID);
			}
		}
		else
		{
			ValueList indexList = new ValueList();
			indexList.add(userID);
			userBuffer.put(roleID, indexList);
		}
	}

	/**
	 * 返回角色缓冲
	 * 
	 * @param roleID 角色ID
	 * @return
	 */
	public static ValueList get(String roleID)
	{
		// 判断缓存是否为null
		if(userBuffer != null) { return userBuffer.get(roleID); }
		return null;
	}

	/**
	 * 移除角色用户ID缓冲
	 * 
	 * @param roleID
	 */
	public static void remove(String roleID)
	{
		// 判断缓存是否为null
		if(userBuffer != null)
		{
			userBuffer.remove(roleID);
		}
	}

	/**
	 * 移除角色的指定用户ID
	 * 
	 * @param roleID 角色ID
	 * @param userID 用户IDID
	 */
	public static void remove(String roleID, String userID)
	{
		// 判断缓存是否为null
		if(userBuffer != null)
		{
			// 确认是否该角色缓冲
			if(userBuffer.containsKey(roleID))
			{
				ValueList indexList = userBuffer.get(roleID);
				indexList.remove(userID);
			}
		}
	}

	/**
	 * 检测角色缓存中是否存在指定用户ID
	 * @param roleID 角色
	 * @param userID 用户ID
	 * @return 是否存在指定用户ID
	 */
	public static Boolean contains(String roleID, String userID)
	{
		// 确认是否该角色缓冲
		if(userBuffer != null && userBuffer.containsKey(roleID))
		{
			ValueList indexList = userBuffer.get(roleID);
			return indexList.contains(userID);
		}

		return false;
	}

}
