package com.qingdao.shiqu.arcgis.utils;

import java.util.LinkedList;

/**
 * 字符串集合
 * @author MinG
 *
 */
public class ValueList extends LinkedList<String>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Boolean contains(String value)
	{
		for(String str: this)
		{
			if(str.equals(value))
			{
				return true;
			}
		}
		return false;
	}

}
