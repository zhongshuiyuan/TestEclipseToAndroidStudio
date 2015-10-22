package com.qingdao.shiqu.arcgis.utils;

import java.util.LinkedList;
import java.util.List;

import com.qingdao.shiqu.arcgis.INotification;

/**
 * 消息通知器，负责将消息通知给消息接口
 * @author MinG
 *
 */
public class Notification
{
	List<INotification> notifiers;
	public Notification()
	{
	}
	
	/**
	 * 添加消息通知器接口对象
	 * @param notifier
	 */
	public void add(INotification notifier)
	{
		if(notifiers==null)
		{
			notifiers = new LinkedList<INotification>();
		}
		notifiers.add(notifier);
	}
	
	/**
	 * 将消息传递给消息接收器
	 * @param msg 消息描述
	 */
	public void setMessage(String msg)
	{
		if(notifiers!=null && notifiers.size()>0)
		{
			for(INotification notifier : notifiers)
			{
				notifier.call(msg);
			}
		}
	}
}
