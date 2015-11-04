package com.qingdao.shiqu.arcgis.utils.drawtool;

import java.util.EventListener;

/**
 * 定义监听画图事件需要实现的方法
 * @author Qinyy
 * @Date 2015-11-03
 */
public interface DrawEventListener extends EventListener {
	/**
	 * 绘图结束时调用
	 * @param event 绘图事件
	 */
	void onDrawEnd(DrawEvent event);
}
