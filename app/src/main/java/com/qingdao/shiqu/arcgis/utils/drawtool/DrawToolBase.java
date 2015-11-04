package com.qingdao.shiqu.arcgis.utils.drawtool;

/**
 * DrawTool的父类，具备添加监听的能力
 * @author Qinyy
 * @Date 2015-11-03
 */
public class DrawToolBase {

	private DrawEventListener mDrawEventListener;

	/**
	 * 添加绘图事件监听器
	 * @param listener 绘图事件监听器
	 */
	public void setOnDrawEvenListener(DrawEventListener listener) {
		mDrawEventListener = listener;
	}

	/**
	 * 移除绘图事件监听器
	 */
	public void removeDrawEvenListener() {
		mDrawEventListener = null;
	}

	/**
	 * 派发绘图事件
	 * @param event 绘图事件
	 */
	public void notifyEvent(DrawEvent event) {
		mDrawEventListener.onDrawEnd(event);
	}
}
