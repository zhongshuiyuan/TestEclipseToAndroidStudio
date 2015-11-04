package com.qingdao.shiqu.arcgis.utils.drawtool;

import java.util.EventObject;

import com.esri.core.map.Graphic;

/**
 * 绘图事件
 * 目前只有DRAW_END事件
 * @author Qinyy
 * @Date 2015-11-03
 */
public class DrawEvent extends EventObject {

	/** 实现 Serializable 接口用 **/
	private static final long serialVersionUID = 262656798L;

	/** 事件类型 **/
	private int type;
	/** 绘图结束 **/
	public static final int DRAW_END = 1;

	private Graphic drawGrahic;

	public DrawEvent(Object source, int type, Graphic drawGrahic) {
		super(source);
		this.drawGrahic = drawGrahic;
		this.type = type;
	}

	public Graphic getDrawGraphic() {
		return drawGrahic;
	}

	public int getType() {
		return type;
	}

}
