package com.eruntech.crosssectionview.drawobjects;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;

/**
 * 可绘制对象，井壁面、管块、管孔、设备等均为可绘制对象类的子类
 * @author 覃远逸
 */
public class DrawObject extends ShapeDrawable
{

	// //////////////////////////////////////////////////////公有常量
	// //////////////////////////////////////////////////////私有常量
	// private final String DEBUG_TAG = "debug " +
	// this.getClass().getSimpleName();
	// //////////////////////////////////////////////////////公有变量
	/** 对象id **/
	public long id;
	/** 父对象id **/
	public long parentId;
	// //////////////////////////////////////////////////////私有变量
	/** 当前的缩放系数 **/
	protected float mScale;
	/** 判断是否被选中 **/
	protected boolean mIsSelected;

	// //////////////////////////////////////////////////////属性

	public DrawObject()
	{
		id = 1;
		parentId = 1;
		mScale = 1;
		mIsSelected = false;
	}

	/**
	 * 绘制自身的形状以便在屏幕上显示出来，重写此方法时请在方法最后加上super.draw(canvas);
	 * @param canvas 用于绘图的 Canvas 类
	 */
	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		if (mIsSelected) {
			drawHighlight(canvas);
		}

	}

	/**
	 * 当自身被选中时高亮显示，无需手动调用此方法，重写时只需正确画出高亮效果即可
	 * @param canvas 用于绘图的 Canvas 类
	 */
	protected void drawHighlight(Canvas canvas)
	{

	}

	/**
	 * 计算两点之间的距离。
	 * @param sPoint 其中一点
	 * @param ePoint 另一点
	 * @return 两点之间的距离
	 */
	protected float lengthOfPointToPoint(Point sPoint, Point ePoint)
	{
		return (float)(Math.sqrt(Math.pow((sPoint.y - ePoint.y),2) + Math.pow((sPoint.x - ePoint.x),2)));
	}

	/**
	 * 计算点到直线的距离
	 * @param point 待测定的点
	 * @param startPoint 确定直线的点一
	 * @param endPoint 确定直线的点二
	 * @return 点到直线的距离
	 */
	public double lengthOfPointToLine(Point point, Point startPoint,
									  Point endPoint)
	{

		double A, B, C, D;

		// 直线一般式:A*x+B*y+C=0
		// 直线两点式: (y-y1)/(y2-y1)=(x-x1)/(x2-x1)
		if (startPoint.x == endPoint.x) {
			D = point.x - startPoint.x;
			return D;
		}
		if (startPoint.y == endPoint.y) {
			D = point.y - startPoint.y;
			return D;
		}

		// 根据两点求直线一般式的系数A、B、C
		A = -(endPoint.y - startPoint.y) / (endPoint.x - startPoint.x);
		B = 1.0f;
		C = -A * startPoint.x - startPoint.y;
		D = (A * point.x + B * point.y + C) / Math.sqrt(A * A + B * B);
		return -D;
	}

	/**
	 * 根据矩形的起点、长和宽计算矩形的其他3个顶点坐标
	 * @param startPoint 矩形的起点
	 * @param width 矩形的长
	 * @param height 矩形的宽
	 * @return 矩形的4个顶点
	 */
	public Point[] getRectPoints(Point startPoint, float width, float height)
	{
		return getRectPoints(startPoint, width, height, 0);
	}

	/**
	 * 根据矩形的起点、长、宽和倾斜角度计算矩形的其他3个顶点坐标
	 * @param startPoint 矩形的起点
	 * @param width 矩形的长
	 * @param height 矩形的宽
	 * @param angle 矩形的倾斜角度
	 * @return 矩形的4个顶点
	 */
	public Point[] getRectPoints(Point startPoint, float width, float height,
								 float angle)
	{
		Point[] rectPoints = new Point[4];

		// 第一点
		Point point1 = new Point(startPoint.x, startPoint.y);
		rectPoints[0] = point1;

		// 第二点
		Point point2 = new Point();
		point2.x = point1.x + (int) (width * Math.cos(angle));
		point2.y = point1.y + (int) (width * Math.sin(angle));
		rectPoints[1] = point2;

		// 第三点
		angle += Math.PI / 2;
		Point point3 = new Point();
		point3.x = point2.x + (int) (height * Math.cos(angle));
		point3.y = point2.y + (int) (height * Math.sin(angle));
		rectPoints[2] = point3;

		// 第四点
		angle += Math.PI / 2;
		Point point4 = new Point();
		point4.x = point3.x + (int) (width * Math.cos(angle));
		point4.y = point3.y + (int) (width * Math.sin(angle));
		rectPoints[3] = point4;

		return rectPoints;
	}

	/**
	 * 判断点是否在线段上，点在线段上的条件是点满足线段所在的直线方程并且点在以线段为对角线的矩形内
	 * @param point
	 * @param startPoint
	 * @param endPoint
	 * @return 点是否在线段上
	 */
	protected boolean isInLine(Point point, Point startPoint, Point endPoint)
	{
		// 判断是否满足直线方程
		if ((startPoint.x - point.x) * (endPoint.y - point.y) == (endPoint.x - point.x)
				* (startPoint.y - point.y)) {
			// 判断该点是否在以给定线段为对角线的矩形内
			double xMax = startPoint.x > endPoint.x ? startPoint.x : endPoint.x;
			double xMin = startPoint.x < endPoint.x ? startPoint.x : endPoint.x;
			double yMax = startPoint.y > endPoint.y ? startPoint.y : endPoint.y;
			double yMin = startPoint.y < endPoint.y ? startPoint.y : endPoint.y;
			if (point.x >= xMin && point.x <= xMax && point.y >= yMin
					&& point.y <= yMax) {
				return true;
			}
		}
		return false;
	}

	//判断点point是否在以topLeftPoint为左上角，width为宽，height为长，angle为倾斜角的矩形内
	protected boolean IsInRotateRect(Point topLeftPoint, float width, float height, float angle, Point point)
	{
		Point[] rectPoints = getRectPoints(topLeftPoint, width, height, angle);
		return isInPolygon(point, rectPoints);
	}

	protected boolean isInPolygon(Point point, Point[] polygonPoints)
	{

		int polygenVertexNum = polygonPoints.length;
		int i = 0;

		// 如果待判定的点和多边形的顶点相同，则说明点在多边形内
		for (Point polygonPoint : polygonPoints) {
			if (point.equals(polygonPoint.x, polygonPoint.y)) {
				return true;
			}
		}

		Point p1, p2;

		// 如果待判断的点在多边形的边上，则说明点在多边形内
		p1 = polygonPoints[0];
		for (i = 1; i <= polygenVertexNum; i++) {
			p2 = polygonPoints[i % polygenVertexNum];
			if (isInLine(point, p1, p2)) {
				return true;
			}
			p1 = p2;
		}

		// 交点数
		int counter = 0;
		float xinters = 0;

		p1 = polygonPoints[0];
		for (i = 1; i <= polygenVertexNum; i++) {
			p2 = polygonPoints[i % polygenVertexNum];
			if (point.y > Math.min(p1.y, p2.y)) {
				if (point.y <= Math.max(p1.y, p2.y)) {
					if (point.x <= Math.max(p1.x, p2.x)) {
						if (p1.y != p2.y) {
							xinters = (point.y - p1.y) * (p2.x - p1.x)
									/ (p2.y - p1.y) + p1.x;
							if (p1.x == p2.x || point.x <= xinters)
								counter++;
						}
					}
				}
			}
			p1 = p2;
		}

		if (counter % 2 == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 清除被选中的标志
	 */
	public void clearSelectedFlag()
	{
		mIsSelected = false;
	}

	/**
	 * 判断点是否在图形内
	 * @param point 待判断的点
	 * @return 是否在图形内
	 */
	public boolean contains(Point point)
	{
		return contains(point.x, point.y);
	}

	/**
	 * 判断点是否在图形内
	 * @param x 待判断的点的x坐标
	 * @param y 待判断的点的y坐标
	 * @return 是否在图形内
	 */
	public boolean contains(float x, float y)
	{
		return false;
	}


	public void setScale(float scale)
	{
		this.mScale = scale;
	}

}
