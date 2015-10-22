package com.qingdao.shiqu.arcgis.utils;

import java.util.ArrayList;
import java.util.List;

import com.qingdao.shiqu.arcgis.mode.Take;

/**************************************
 *
 *      作  者：  潘跃瑞 
 *
 *      功  能：  记录Session数据 
 *
 *      时  间：  2014-10-30
 *
 *      版  权：  青岛盛天科技有限公司 
 *      
 ****************************************/
public class Session
{
   static List<Take> takes;
   public static List<Take> getTakes()
   {
	   if(takes == null)
	   {
		   takes = new ArrayList<Take>();
	   }
	   return takes;
   }
}
