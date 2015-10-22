package com.qingdao.shiqu.arcgis.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelPhoneHelper
{
	/***
	 * 从字符串中匹配电话号码并拼串
	 * 
	 * @param number
	 * @return
	 */
	public static ArrayList<String> getPhoneString(String number)
	{
		number = number.replaceAll("[^x00-xff]", " ");
		ArrayList<String> phoneListStr = new ArrayList<String>();
		String phoneRegexp = "^(\\d{8}|\\d{11})$"; //手机 固话的匹配模式
		try
		{
			Matcher m;			
			Pattern phoneReg = Pattern.compile(phoneRegexp);
			String[] result = number.split(" ");
			int size = result.length;
			for(int i=0; i<size; i++)
			{
				// 比较数组中的每一个数字是不是电话号码
				m = phoneReg.matcher(result[i]);
				while(m.find())
				{
					phoneListStr.add(m.group().trim());
				}
			}
		}
		catch(Exception e)
		{
		}
		return phoneListStr;
	}
}