package com.eruntech.crosssectionview.drawobjects;

import com.eruntech.crosssectionview.utils.DrawUtil;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;

public class WellWall extends DrawObject
{
	// //////////////////////////////////////////////////////公有常量
	// //////////////////////////////////////////////////////私有常量
	@SuppressWarnings("unused")
	private final String DEBUG_TAG = "debug " + this.getClass().getSimpleName();
	// //////////////////////////////////////////////////////公有变量
	public Point m_pos;//井壁面左上角的位置
	public float m_angle;
	public float m_width;
	public float m_height;
	public boolean m_isActivated;
	public boolean m_isLeftWay;
	// //////////////////////////////////////////////////////私有变量
	/** 管块数量 **/
	private int m_tubePadNum;
	// //////////////////////////////////////////////////////属性
	/** 管块列表 **/
	private TubePad[] m_padList;

	public TubePad[] getM_padList()
	{
		return m_padList;
	}

	public void setM_padList(TubePad[] padList)
	{
		m_padList = padList;
		m_tubePadNum = padList.length;
	}

	public void drawSelected(Canvas CDC, int selectedType, int selectedID)
	{

	}

	@Override
	public void draw(Canvas canvas)
	{
		int i = 0;

		//Point wellPt = new Point(m_pos.x, m_pos.y);
		//float angle = m_angle;
		Point[] rectPts = getRectPoints(m_pos, m_height, m_width, m_angle);

		int brushColor;
		int penColor;
		// 是否连接管沟
		if (id > 0) {
			brushColor = 0x9b9b9b;
		} else {
			brushColor = 0xd2d2d2;
		}

		// 活动井壁面画的颜色不一样
		if (m_isActivated) {
			penColor = Color.BLUE;
		} else {
			penColor = Color.BLACK;
		}

		// 画井壁面
		Path wallPath = new Path();
		wallPath.moveTo(rectPts[0].x, rectPts[0].y);
		for (i = 1; i < 5; i++) {
			wallPath.lineTo(rectPts[i].x, rectPts[i].y);
		}
		DrawUtil.drawPath(canvas, wallPath, penColor, brushColor,
				DrawUtil.ALPHA_80_PERCENTAGE);

		// 如果被选中
		if (mIsSelected) {
			if (id > 0)
				drawTracker(canvas);
		}

		// 设置管道连接信息显示位置及偏转角
		// if(m_connAdd) {
		// drawRemark(CDC);
		// }

		// 绘制该井壁面所有的管块对象
		calculatePos();
		for (i = 0; i < m_tubePadNum; i++) {
			m_padList[i].draw(canvas);
		}

		super.draw(canvas);
	}

	@Override
	protected void drawHighlight(Canvas canvas)
	{
		// TODO 自动生成的方法存根
		super.drawHighlight(canvas);
	}

	public void clearFlag()
	{

	}

	// 井壁面被选中时绘制特殊显示
	public void drawTracker(Canvas CDC)
	{
		final float trackerRadius = 3;

		Point wellPoint = new Point();
		wellPoint.x = m_pos.x;
		wellPoint.y = m_pos.y;
		// 设置画笔和画刷
		int drawColor = Color.RED;
		int fillColor = Color.GRAY;

		// 为井壁面的4条边上画小圆
		float angle = m_angle;
		for (int i = 0; i < 4; i++) {
			DrawTrackerInOneLine(CDC, wellPoint, angle, trackerRadius,
					drawColor, fillColor);
		}
		/*
		 * 为井壁面的4条边上画小圆（未封装） wellPoint.x = wellPoint.x + (int)(m_height *
		 * Math.cos(angle)); wellPoint.y = wellPoint.y + (int)(m_height *
		 * Math.sin(angle)); DrawUtil.drawCircle(CDC, m_pos, trackerRadius,
		 * drawColor, fillColor, DrawUtil.ALPHA_80_PERCENTAGE); angle = angle +
		 * (float)Math.PI / 2; wellPoint.x = wellPoint.x + (int)(m_width / 2 *
		 * Math.cos(angle)); wellPoint.y = wellPoint.y + (int)(m_width / 2 *
		 * Math.sin(angle)); DrawUtil.drawCircle(CDC, m_pos, trackerRadius,
		 * drawColor, fillColor, DrawUtil.ALPHA_80_PERCENTAGE); wellPoint.x =
		 * wellPoint.x + (int)(m_width/2*Math.cos(angle)); wellPoint.y =
		 * wellPoint.y + (int)(m_width/2*Math.sin(angle));
		 * DrawUtil.drawCircle(CDC, m_pos, trackerRadius, drawColor, fillColor,
		 * DrawUtil.ALPHA_80_PERCENTAGE); angle = angle + (float)Math.PI / 2;
		 * wellPoint.x = wellPoint.x + (int)(m_height / 2 * Math.cos(angle));
		 * wellPoint.y = wellPoint.y + (int)(m_height / 2 * Math.sin(angle));
		 * DrawUtil.drawCircle(CDC, m_pos, trackerRadius, drawColor, fillColor,
		 * DrawUtil.ALPHA_80_PERCENTAGE); wellPoint.x = wellPoint.x +
		 * (int)(m_height / 2 * Math.cos(angle)); wellPoint.y = wellPoint.y +
		 * (int)(m_height / 2 * Math.sin(angle)); DrawUtil.drawCircle(CDC,
		 * m_pos, trackerRadius, drawColor, fillColor,
		 * DrawUtil.ALPHA_80_PERCENTAGE); angle = angle + (float)Math.PI / 2;
		 * wellPoint.x = wellPoint.x + (int)(m_width / 2 * Math.cos(angle));
		 * wellPoint.y = wellPoint.y + (int)(m_width / 2 * Math.sin(angle));
		 * DrawUtil.drawCircle(CDC, m_pos, trackerRadius, drawColor, fillColor,
		 * DrawUtil.ALPHA_80_PERCENTAGE); wellPoint.x = wellPoint.x +
		 * (int)(m_width / 2 * Math.cos(angle)); wellPoint.y = wellPoint.y +
		 * (int)(m_width / 2 * Math.sin(angle)); DrawUtil.drawCircle(CDC, m_pos,
		 * trackerRadius, drawColor, fillColor, DrawUtil.ALPHA_80_PERCENTAGE);
		 * angle = angle + (float)Math.PI / 2; wellPoint.x = wellPoint.x +
		 * (int)(m_height / 2 * Math.cos(angle)); wellPoint.y = wellPoint.y +
		 * (int)(m_height / 2 * Math.sin(angle)); DrawUtil.drawCircle(CDC,
		 * m_pos, trackerRadius, drawColor, fillColor,
		 * DrawUtil.ALPHA_80_PERCENTAGE);
		 */

	}

	private Point calculateAndDrawTracker(Canvas canvas, Point wellPoint,
										  float angle, float trackerRadius, int drawColor, int fillColor)
	{
		wellPoint.x = wellPoint.x + (int) (m_height * Math.cos(angle));
		wellPoint.y = wellPoint.y + (int) (m_height * Math.sin(angle));
		DrawUtil.drawCircle(canvas, m_pos, trackerRadius, drawColor, fillColor,
				DrawUtil.ALPHA_80_PERCENTAGE);
		return wellPoint;
	}

	private void DrawTrackerInOneLine(Canvas canvas, Point wellPoint,
									  float angle, float trackerRadius, int drawColor, int fillColor)
	{
		wellPoint = calculateAndDrawTracker(canvas, wellPoint, angle,
				trackerRadius, drawColor, fillColor);
		angle += (float) Math.PI / 2;
		wellPoint = calculateAndDrawTracker(canvas, wellPoint, angle,
				trackerRadius, drawColor, fillColor);
	}

	// 计算井中的管道对象相关属性
	public void calculatePos()
	{
		for (int i = 0; i < m_tubePadNum; i++)// 计算每个管块的属性值
		{
			m_padList[i].angle = m_angle; // ？旋转后井壁面的方位角
			m_padList[i].wallTopLeftPoint = m_pos; // ？旋转后井壁面的位置
			m_padList[i].isLeftWay = m_isLeftWay;
//			m_padList[i].wallWidth = m_width;
			m_padList[i].calHolePos();
		}
	}
}
