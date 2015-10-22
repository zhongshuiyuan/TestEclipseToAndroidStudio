package com.eruntech.crosssectionview.drawobjects;

import com.eruntech.crosssectionview.utils.DrawUtil;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;

/**
 * 人/手井展开图中的井类
 * @author 覃远逸
 */
public class Well extends DrawObject
{
	// //////////////////////////////////////////////////////公有常量
	// //////////////////////////////////////////////////////私有常量
	// //////////////////////////////////////////////////////公有变量
	// //////////////////////////////////////////////////////私有变量
	/** 各个井壁面起点位置 **/
	private Point[] m_wallPos;
	/** 井中心 **/
	private Point m_pos;
	/** 井壁面数量 **/
	private int m_wallNum;
	/** 设备数量 **/
	private int m_deviceNum;
	/** 旋转角 **/
	private float m_angRotate;
	/** 井长度 **/
	private float m_length;
	/** 井宽度 **/
	private float m_width;
	// //////////////////////////////////////////////////////属性
	/** 设备集合 **/
	private Device[] m_deviceList;

	public Device[] getDeviceList()
	{
		return m_deviceList;
	}

	public void setDeviceList(Device[] deviceList)
	{
		m_deviceList = deviceList;
		m_deviceNum = deviceList.length;
	}

	/** 井壁面集合 **/
	private WellWall[] m_wallList;

	public WellWall[] getWallList()
	{
		return m_wallList;
	}

	public void setWallList(WellWall[] wallList)
	{
		m_wallList = wallList;
		m_wallNum = wallList.length;
	}

	public Well()
	{

	}

	/**
	 * 被选中时高亮显示
	 * @param CDC
	 * @param selectedType
	 * @param selectedID
	 */
	public void drawSelected(Canvas CDC, int selectedType, int selectedID)
	{
		int i = 0;
		if (1 <= selectedType && selectedType <= 3)// 被选中的对象在设备区内
		{
			for (i = 0; i < m_deviceList.length; i++)
				m_deviceList[i].drawSelected(CDC, selectedType, selectedID);
		} else if (4 <= selectedType && selectedType <= 7)// 被选中的对象在井壁面内
		{
			for (i = 0; i < m_wallList.length; i++) {
				m_wallList[i].drawSelected(CDC, selectedType, selectedID);
			}
		}
	}

	@Override
	public void draw(Canvas CDC)
	{

		super.draw(CDC);

		int i = 0;

		Path wellPath = new Path();
		wellPath.moveTo(m_wallPos[0].x, m_wallPos[0].y);
		// 绘制井，即绘制井的每一边
		for (i = 0; i < m_wallNum; i++) {
			wellPath.lineTo(m_wallPos[i].x, m_wallPos[i].y);
		}
		DrawUtil.drawPath(CDC, wellPath, Color.BLACK, Color.BLACK, 0x00);

		// 绘制井底设备
		for (i = 0; i < m_deviceNum; i++)
			m_deviceList[i].draw(CDC);

		// 绘制井壁面
		float angle = 0.0f + m_angRotate;
		for (i = 0; i < m_wallNum; i++) {
			if (i < m_wallList.length) {
				m_wallList[i].draw(CDC);
			}
			// else//else的内容可删除
			// {
			// var tempPoint:Point = new Point(m_wallPos[i].x, m_wallPos[i].y);
			// var rectPts:Array = getRectPoint(tempPoint, m_width, m_length,
			// angle);
			// //设置画空井壁面的画笔和画刷
			// CDC.contentGroup.graphics.lineStyle(1, 0x000000,
			// enum.FILL_DEEP100);
			// CDC.contentGroup.graphics.beginFill(0xffffff, enum.FILL_DEEP0);
			// //画空井壁面
			// CDC.contentGroup.graphics.moveTo(rectPts[0].x, rectPts[0].y);
			// for(var j:uint=1; j<5; j++)
			// {
			// CDC.contentGroup.graphics.lineTo(rectPts[j].x, rectPts[j].y);
			// }
			// }
			angle = angle - (float) (Math.PI * 2 / m_wallNum);
		}

		// 绘制井中点
		DrawUtil.drawCircle(CDC, m_pos, 2, Color.BLACK, Color.BLACK, 0xff);

	}

	public void drawTitle(Canvas CDC)
	{
		// var title:RichText = new RichText();
		// title.x = 15;
		// title.text = "\n" + m_title + "\n\n" + m_textID + "\n\n" + m_address;
		// CDC.contentGroup.addElement(title);
		// title.setStyle('fontSize', CONNECTION_TEXT_SIZE);
		// title.setStyle('color', CONNECTION_TEXT_COLOR);
		// title.setStyle("fontFamily", CONNECTION_TEXT_FONT);
	}

	public void setProperty(Point pos)
	{
		m_pos = pos;
		calculatePos();
	}

	// 计算井壁面和设备的位置
	public void calculatePos()
	{
		// PI/m_wallNum 为每个井边对应的圆心角的一半
		float wellRadius = (float) (m_length / Math.sin(Math.PI / m_wallNum) / 2);// 计算井半径
		int wellX = (int) (m_pos.x - wellRadius
				* Math.sin(Math.PI / m_wallNum + m_angRotate));
		int wellY = (int) (m_pos.y + wellRadius
				* Math.cos(Math.PI / m_wallNum + m_angRotate));
		Point pos = new Point();
		float angle = 0.0f + m_angRotate;

		int i = 0;

		// 初始化每个井设备对象
		for (i = 0; i < m_deviceNum; i++) {
			Point devicePos = new Point(m_pos.x, m_pos.y);
			switch (m_deviceNum) {
				case 1:
					m_deviceList[i].center = devicePos;
					break;
				case 2:// x += -+
					devicePos.x += Math.pow(-1, i + 1) * m_length / 4;
					m_deviceList[i].center = devicePos;
					break;
				case 3:// x += -0+
					devicePos.x += (i - 1) / Math.abs(i - 1) * Math.pow(-1, i)
							* m_length / 4;
					if (i == 1)
						devicePos.x = m_pos.x;
					devicePos.y += Math.pow(-1, i) * m_width / 4;
					m_deviceList[i].center = devicePos;
					break;
				case 4:// x += -+-+ , y += --++
					if (i <= 1)
						devicePos.y -= m_width / 4;
					else
						devicePos.y += m_width / 4;
					devicePos.x += Math.pow(-1, i + 1) * m_length / 4;
					m_deviceList[i].center = devicePos;
					break;
				default:
					m_deviceList[i].center = devicePos;
					break;
			}

		}

		// 初始化每个井壁对象
		for (i = 0; i < m_wallNum; i++) {
			pos = new Point(wellX, wellY);
			m_wallPos[i] = pos;// 井对象中每个井壁对象位置

			// 初始化每个井壁对象
			if (i < m_wallList.length) {
				m_wallList[i].m_pos = pos;
				m_wallList[i].m_angle = angle;
				m_wallList[i].m_width = m_width;
				m_wallList[i].m_height = m_length;
				// TODO 是否还需要以下变量？
				// m_wallList[i].m_wallNo = i + 1; //井壁号
				// m_wallList[i].m_marginX = m_marginX;
				// m_wallList[i].m_marginY = m_marginY;
				// m_wallList[i].m_gridX = m_gridX;
				// m_wallList[i].m_gridY = m_gridY;
				/*
				 * 暂不需要活动井壁面 if(m_wallList[i].m_wallNo -1 == m_activeWall)
				 * m_wallList[i].m_isActive = 1; else m_wallList[i].m_isActive =
				 * 0;
				 */
				m_wallList[i].calculatePos(); // 井壁面对象负责计算所属它的对象(管块)
			}

			wellX = wellX + (int) (m_length * Math.cos(angle));
			wellY = wellY + (int) (m_length * Math.sin(angle));
			angle = angle - (float) (Math.PI * 2 / m_wallNum);
		}
	}

	// 清除选中状态，即把所以选中状态重置为未选中
	public void clearFlag()
	{
		mIsSelected = false;
		int i = 0;
		for (i = 0; i < m_deviceList.length; i++) {
			m_deviceList[i].clearFlag();
		}
		for (i = 0; i < m_wallList.length; i++) {
			m_wallList[i].clearFlag();
		}
	}

}
