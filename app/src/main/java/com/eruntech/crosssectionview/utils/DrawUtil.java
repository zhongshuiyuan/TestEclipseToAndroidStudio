package com.eruntech.crosssectionview.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * 画图工具类
 * @author 覃远逸
 */
public class DrawUtil
{
	/** 调试标志 **/
	private static final String DEBUG_TAG = "debug "
			+ DrawUtil.class.getSimpleName();

	/** 默认描边宽度 **/
	public static final int DEFAULT_STROKE_WIDTH = 2;
	/** 透明度（完全透明，100%透明） **/
	public static final int ALPHA_EMPTY = 0x00;
	/** 透明度（接近完全透明，80%透明） **/
	public static final int ALPHA_20_PERCENTAGE = 0x33;
	/** 透明度（透明，60%透明） **/
	public static final int ALPHA_40_PERCENTAGE = 0x66;
	/** 透明度（不透明，40%透明） **/
	public static final int ALPHA_60_PERCENTAGE = 0x99;
	/** 透明度（接近完全不透明，20%透明） **/
	public static final int ALPHA_80_PERCENTAGE = 0xcc;
	/** 透明度（完全不透明，0%透明） **/
	public static final int ALPHA_FULL = 0xff;

	/**
	 * 设置传入的 Paint 用于填充背景
	 * @param paint 用于填充背景的 Paint
	 * @param fillColor 填充颜色
	 * @param alpha 背景透明度，数值为0-255，0为完全透明
	 * @return 用于填充背景的 Paint
	 */
	private static Paint setPaintFillStyle(Paint paint, int fillColor, int alpha)
	{
		if (paint == null) {
			Log.w(DEBUG_TAG, "执行setPaintFillStyle方法时传入的Paint类为null");
			paint = new Paint();
		}
		paint.setStyle(Style.FILL);
		paint.setColor(fillColor);
		paint.setAlpha(alpha);
		return paint;
	}

	/**
	 * 设置传入的 Paint 用于描边
	 * @param paint 用于描边的 Paint
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @return 用于描边的 Paint
	 */
	private static Paint setPaintDrawStyle(Paint paint, int drawColor,
										   float strokeWidth)
	{
		if (paint == null) {
			Log.w(DEBUG_TAG, "执行setPaintDrawStyle方法时传入的Paint类为null");
			paint = new Paint();
		}
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(strokeWidth);
		paint.setColor(drawColor);
		return paint;
	}

	/**
	 * 设置传入的 Paint 用于描边
	 * @param paint 用于描边的 Paint
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param alpha 描边
	 * @return 用于描边的 Paint
	 */
	private static Paint setPaintDrawStyleIncludingAlpha(Paint paint,
														 int drawColor, float strokeWidth, int alpha)
	{
		if (paint == null) {
			Log.w(DEBUG_TAG,
					"执行setPaintDrawStyleIncludingAlpha方法时传入的Paint类为null");
			paint = new Paint();
		}
		paint = setPaintDrawStyle(paint, drawColor, strokeWidth);
		paint.setAlpha(alpha);
		return paint;
	}

	private static Paint setPaintForText(Paint paint, int textColor,
										 float strokeWidth, float textSize)
	{
		paint.setColor(textColor);
		paint.setTextSize(textSize);
		paint.setStrokeWidth(strokeWidth);
		return paint;
	}

	/**
	 * 根据传入的条件绘制圆形，如果半径小于0，将不会绘制，需手动定制 Paint 类。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param cx 圆心的x轴坐标
	 * @param cy 圆心的y轴坐标
	 * @param radius 半径
	 */
	public static void drawCircle(Canvas canvas, Paint paint, float cx,
								  float cy, float radius)
	{
		canvas.drawCircle(cx, cy, radius, paint);
	}

	/**
	 * 根据传入的条件绘制圆形，如果半径小于0，将不会绘制。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param cx 圆心的x轴坐标
	 * @param cy 圆心的y轴坐标
	 * @param radius 半径
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawCircle(Canvas canvas, Paint paint, float cx,
								  float cy, float radius, int drawColor, float strokeWidth,
								  int fillColor, int alpha)
	{
		paint = setPaintFillStyle(paint, fillColor, alpha);
		drawCircle(canvas, paint, cx, cy, radius);

		paint = setPaintDrawStyle(paint, drawColor, strokeWidth);
		drawCircle(canvas, paint, cx, cy, radius);
	}

	/**
	 * 根据传入的条件绘制圆形，如果半径小于0，将不会绘制。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param cx 圆心的x轴坐标
	 * @param cy 圆心的y轴坐标
	 * @param radius 半径
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawCircle(Canvas canvas, float cx, float cy,
								  float radius, int drawColor, float strokeWidth, int fillColor,
								  int alpha)
	{
		drawCircle(canvas, new Paint(), cx, cy, radius, drawColor, strokeWidth,
				fillColor, alpha);
	}

	/**
	 * 根据传入的条件绘制圆形，如果半径小于0，将不会绘制。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param cx 圆心的x轴坐标
	 * @param cy 圆心的y轴坐标
	 * @param radius 半径
	 * @param drawColor 描边颜色
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawCircle(Canvas canvas, float cx, float cy,
								  float radius, int drawColor, int fillColor, int alpha)
	{
		drawCircle(canvas, cx, cy, radius, drawColor, DEFAULT_STROKE_WIDTH,
				fillColor, alpha);
	}

	/**
	 * 根据传入的条件绘制圆形，如果半径小于0，将不会绘制，需手动定制 Paint 类。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param center 圆心
	 * @param radius 半径
	 */
	public static void drawCircle(Canvas canvas, Paint paint, Point center,
								  float radius)
	{
		canvas.drawCircle(center.x, center.y, radius, paint);
	}

	/**
	 * 根据传入的条件绘制圆形，如果半径小于0，将不会绘制。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param center 圆心
	 * @param radius 半径
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawCircle(Canvas canvas, Paint paint, Point center,
								  float radius, int drawColor, float strokeWidth, int fillColor,
								  int alpha)
	{
		paint = setPaintFillStyle(paint, fillColor, alpha);
		drawCircle(canvas, paint, center, radius);

		paint = setPaintDrawStyle(paint, drawColor, strokeWidth);
		drawCircle(canvas, paint, center, radius);
	}

	/**
	 * 根据传入的条件绘制圆形，如果半径小于0，将不会绘制。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param center 圆心
	 * @param radius 半径
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawCircle(Canvas canvas, Point center, float radius,
								  int drawColor, float strokeWidth, int fillColor, int alpha)
	{
		drawCircle(canvas, new Paint(), center, radius, drawColor, strokeWidth,
				fillColor, alpha);
	}

	/**
	 * 根据传入的条件绘制圆形，如果半径小于0，将不会绘制。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param center 圆心
	 * @param radius 半径
	 * @param drawColor 描边颜色
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawCircle(Canvas canvas, Point center, float radius,
								  int drawColor, int fillColor, int alpha)
	{
		drawCircle(canvas, center, radius, drawColor, DEFAULT_STROKE_WIDTH,
				fillColor, alpha);
	}

	/**
	 * 根据传入的条件绘制矩形，需手动定制 Paint 类。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param left 矩形的左边缘
	 * @param top 矩形的上边缘
	 * @param right 矩形的右边缘
	 * @param bottom 矩形的下边缘
	 */
	public static void drawRect(Canvas canvas, Paint paint, float left,
								float top, float right, float bottom)
	{
		canvas.drawRect(left, top, right, bottom, paint);
	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param left 矩形的左边缘
	 * @param top 矩形的上边缘
	 * @param right 矩形的右边缘
	 * @param bottom 矩形的下边缘
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRect(Canvas canvas, Paint paint, float left,
								float top, float right, float bottom, int drawColor,
								float strokeWidth, int fillColor, int alpha)
	{
		paint = setPaintFillStyle(paint, fillColor, alpha);
		drawRect(canvas, paint, left, top, right, bottom);

		paint = setPaintDrawStyle(paint, drawColor, strokeWidth);
		drawRect(canvas, paint, left, top, right, bottom);
	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param left 矩形的左边缘
	 * @param top 矩形的上边缘
	 * @param right 矩形的右边缘
	 * @param bottom 矩形的下边缘
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRect(Canvas canvas, float left, float top,
								float right, float bottom, int drawColor, float strokeWidth,
								int fillColor, int alpha)
	{
		drawRect(canvas, new Paint(), left, top, right, bottom, drawColor,
				strokeWidth, fillColor, alpha);
	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param left 矩形的左边缘
	 * @param top 矩形的上边缘
	 * @param right 矩形的右边缘
	 * @param bottom 矩形的下边缘
	 * @param drawColor 描边颜色
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRect(Canvas canvas, float left, float top,
								float right, float bottom, int drawColor, int fillColor, int alpha)
	{
		drawRect(canvas, left, top, right, bottom, drawColor,
				DEFAULT_STROKE_WIDTH, fillColor, alpha);
	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param left 矩形的左边缘
	 * @param top 矩形的上边缘
	 * @param right 矩形的右边缘
	 * @param bottom 矩形的下边缘
	 * @param drawColor 描边颜色
	 * @param fillColor 填充颜色
	 */
	public static void drawRect(Canvas canvas, float left, float top,
								float right, float bottom, int drawColor, int fillColor)
	{
		drawRect(canvas, left, top, right, bottom, drawColor, fillColor, ALPHA_FULL);
	}

	/**
	 * 根据传入的条件绘制矩形，需手动定制 Paint 类。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param rect 需要绘制的矩形
	 */
	public static void drawRect(Canvas canvas, Paint paint, Rect rect)
	{
		canvas.drawRect(rect, paint);
	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param rect 需要绘制的矩形
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRect(Canvas canvas, Paint paint, Rect rect,
								int drawColor, float strokeWidth, int fillColor, int alpha)
	{
		paint = setPaintFillStyle(paint, fillColor, alpha);
		drawRect(canvas, paint, rect);

		paint = setPaintDrawStyle(paint, drawColor, strokeWidth);
		drawRect(canvas, paint, rect);
	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param rect 需要绘制的矩形
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRect(Canvas canvas, Rect rect, int drawColor,
								float strokeWidth, int fillColor, int alpha)
	{
		drawRect(canvas, new Paint(), rect, drawColor, strokeWidth, fillColor,
				alpha);
	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param rect 需要绘制的矩形
	 * @param drawColor 描边颜色
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRect(Canvas canvas, Rect rect, int drawColor,
								int fillColor, int alpha)
	{
		drawRect(canvas, rect, drawColor, DEFAULT_STROKE_WIDTH, fillColor,
				alpha);
	}

	/**
	 * 根据传入的条件绘制矩形，需手动定制 Paint 类。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param rect 需要绘制的矩形
	 */
	public static void drawRect(Canvas canvas, Paint paint, RectF rect)
	{
		canvas.drawRect(rect, paint);
	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param rect 需要绘制的矩形
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRect(Canvas canvas, Paint paint, RectF rect,
								int drawColor, float strokeWidth, int fillColor, int alpha)
	{
		paint = setPaintFillStyle(paint, fillColor, alpha);
		drawRect(canvas, paint, rect);

		paint = setPaintDrawStyle(paint, drawColor, strokeWidth);
		drawRect(canvas, paint, rect);

	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param rect 需要绘制的矩形
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRect(Canvas canvas, RectF rect, int drawColor,
								float strokeWidth, int fillColor, int alpha)
	{
		drawRect(canvas, new Paint(), rect, drawColor, strokeWidth, fillColor,
				alpha);
	}

	/**
	 * 根据传入的条件绘制矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param rect 需要绘制的矩形
	 * @param drawColor 描边颜色
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRect(Canvas canvas, RectF rect, int drawColor,
								int fillColor, int alpha)
	{
		drawRect(canvas, rect, drawColor, DEFAULT_STROKE_WIDTH, fillColor,
				alpha);
	}

	/**
	 * 根据传入的条件绘制椭圆形，需手动定制 Paint 类。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param oval 需要绘制的椭圆形
	 */
	public static void drawOval(Canvas canvas, Paint paint, RectF oval)
	{
		canvas.drawOval(oval, paint);
	}

	/**
	 * 根据传入的条件绘制椭圆形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param oval 需要绘制的椭圆形
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawOval(Canvas canvas, Paint paint, RectF oval,
								int drawColor, float strokeWidth, int fillColor, int alpha)
	{
		paint = setPaintFillStyle(paint, fillColor, alpha);
		canvas.drawOval(oval, paint);

		paint = setPaintDrawStyle(paint, drawColor, strokeWidth);
		canvas.drawOval(oval, paint);
	}

	/**
	 * 根据传入的条件绘制椭圆形
	 * @param canvas 用于绘图的Canvas类
	 * @param oval 需要绘制的椭圆形
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawOval(Canvas canvas, RectF oval, int drawColor,
								float strokeWidth, int fillColor, int alpha)
	{
		drawOval(canvas, new Paint(), oval, drawColor, strokeWidth, fillColor,
				alpha);
	}

	/**
	 * 根据传入的条件绘制椭圆形
	 * @param canvas 用于绘图的Canvas类
	 * @param oval 需要绘制的椭圆形
	 * @param drawColor 描边颜色
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawOval(Canvas canvas, RectF oval, int drawColor,
								int fillColor, int alpha)
	{
		drawOval(canvas, oval, drawColor, DEFAULT_STROKE_WIDTH, fillColor,
				alpha);
	}

	/**
	 * 根据传入的条件绘制路径，需手动定制 Paint 类。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param path 需要绘制的路径
	 */
	public static void drawPath(Canvas canvas, Paint paint, Path path)
	{
		canvas.drawPath(path, paint);
	}

	/**
	 * 根据传入的条件绘制路径
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param path 需要绘制的路径
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawPath(Canvas canvas, Paint paint, Path path,
								int drawColor, float strokeWidth, int fillColor, int alpha)
	{
		paint = setPaintFillStyle(paint, fillColor, alpha);
		drawPath(canvas, paint, path);

		paint = setPaintDrawStyle(paint, drawColor, strokeWidth);
		drawPath(canvas, paint, path);
	}

	/**
	 * 根据传入的条件绘制路径
	 * @param canvas 用于绘图的 Canvas 类
	 * @param path 需要绘制的路径
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawPath(Canvas canvas, Path path, int drawColor,
								float strokeWidth, int fillColor, int alpha)
	{
		drawPath(canvas, new Paint(), path, drawColor, strokeWidth, fillColor,
				alpha);
	}

	/**
	 * 根据传入的条件绘制路径
	 * @param canvas 用于绘图的 Canvas 类
	 * @param path 需要绘制的路径
	 * @param drawColor 描边颜色
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawPath(Canvas canvas, Path path, int drawColor,
								int fillColor, int alpha)
	{
		drawPath(canvas, path, drawColor, DEFAULT_STROKE_WIDTH, fillColor,
				alpha);
	}

	// TODO 为画线画点方法添加注释
	public static void drawLine(Canvas canvas, Paint paint, float startX,
								float startY, float stopX, float stopY)
	{
		canvas.drawLine(startX, startY, stopX, stopY, paint);
	}

	public static void drawLine(Canvas canvas, Paint paint, float startX,
								float startY, float stopX, float stopY, int drawColor,
								float strokeWidth, int alpha)
	{
		paint = setPaintDrawStyleIncludingAlpha(paint, drawColor, strokeWidth,
				alpha);
		drawLine(canvas, paint, startX, startY, stopX, stopY);
	}

	public static void drawLine(Canvas canvas, float startX, float startY,
								float stopX, float stopY, int drawColor, float strokeWidth,
								int alpha)
	{
		drawLine(canvas, new Paint(), startX, startY, stopX, stopY, drawColor, strokeWidth,
				alpha);
	}

	public static void drawLine(Canvas canvas, float startX,
								float startY, float stopX, float stopY, int drawColor,
								float strokeWidth)
	{
		drawLine(canvas, startX, startY, stopX, stopY, drawColor,
				strokeWidth, ALPHA_FULL);
	}

	public static void drawLine(Canvas canvas, Paint paint, float startX,
								float startY, float stopX, float stopY, int drawColor)
	{
		drawLine(canvas, startX, startY, stopX, stopY, drawColor,
				DEFAULT_STROKE_WIDTH);
	}

	public static void drawLine(Canvas canvas, Paint paint, Point startPoint,
								Point stopPoint, int drawColor, float strokeWidth, int alpha)
	{
		paint = setPaintDrawStyleIncludingAlpha(paint, drawColor, strokeWidth,
				alpha);
		drawLine(canvas, paint, startPoint.x, startPoint.y, stopPoint.x,
				stopPoint.y);
	}

	public static void drawLine(Canvas canvas, Point startPoint,
								Point stopPoint, int drawColor, float strokeWidth, int alpha)
	{
		drawLine(canvas, new Paint(), startPoint, stopPoint, drawColor, strokeWidth, alpha);
	}

	public static void drawLine(Canvas canvas, Point startPoint,
								Point stopPoint, int drawColor, float strokeWidth)
	{
		drawLine(canvas, startPoint, stopPoint, drawColor, strokeWidth,
				ALPHA_FULL);
	}

	public static void drawLine(Canvas canvas, Point startPoint,
								Point stopPoint, int drawColor)
	{
		drawLine(canvas, startPoint, stopPoint, drawColor,
				DEFAULT_STROKE_WIDTH);
	}

	public static void drawLines(Canvas canvas, Paint paint, float[] points)
	{
		canvas.drawLines(points, paint);
	}

	public static void drawLines(Canvas canvas, Paint paint, float[] points,
								 int drawColor, float strokeWidth, int alpha)
	{
		paint = setPaintDrawStyleIncludingAlpha(paint, drawColor, strokeWidth,
				alpha);
		drawLines(canvas, paint, points);
	}

	public static void drawLines(Canvas canvas, float[] points,
								 int drawColor, float strokeWidth, int alpha)
	{
		drawLines(canvas, new Paint(), points, drawColor, strokeWidth, alpha);
	}

	public static void drawLines(Canvas canvas, float[] points,
								 int drawColor, float strokeWidth)
	{
		drawLines(canvas, points, drawColor, strokeWidth, ALPHA_FULL);
	}

	public static void drawLines(Canvas canvas, float[] points,
								 int drawColor)
	{
		drawLines(canvas, points, drawColor, DEFAULT_STROKE_WIDTH);
	}

	public static void drawPoint(Canvas canvas, Paint paint, float x, float y)
	{
		canvas.drawPoint(x, y, paint);
	}

	public static void drawPoint(Canvas canvas, Paint paint, float x, float y,
								 int drawColor, float strokeWidth, int alpha)
	{
		paint = setPaintDrawStyleIncludingAlpha(paint, drawColor, strokeWidth,
				alpha);
		drawPoint(canvas, paint, x, y);
	}

	public static void drawPoint(Canvas canvas, float x, float y,
								 int drawColor, float strokeWidth, int alpha)
	{
		drawPoint(canvas, new Paint(), x, y, drawColor, strokeWidth, alpha);
	}

	public static void drawPoint(Canvas canvas, float x, float y,
								 int drawColor, float strokeWidth)
	{
		drawPoint(canvas, x, y, drawColor, strokeWidth, ALPHA_FULL);
	}

	public static void drawPoint(Canvas canvas, float x, float y,
								 int drawColor)
	{
		drawPoint(canvas, x, y, drawColor, DEFAULT_STROKE_WIDTH);
	}

	public static void drawPoint(Canvas canvas, Paint paint, Point point,
								 int drawColor, float strokeWidth, int alpha)
	{
		paint = setPaintDrawStyleIncludingAlpha(paint, drawColor, strokeWidth,
				alpha);
		drawPoint(canvas, paint, point.x, point.y);
	}

	public static void drawPoint(Canvas canvas, Point point,
								 int drawColor, float strokeWidth, int alpha)
	{
		drawPoint(canvas, new Paint(), point, drawColor, strokeWidth, alpha);
	}

	public static void drawPoint(Canvas canvas, Point point,
								 int drawColor, float strokeWidth)
	{
		drawPoint(canvas, point, drawColor, strokeWidth, ALPHA_FULL);
	}

	public static void drawPoint(Canvas canvas, Point point,
								 int drawColor)
	{
		drawPoint(canvas, point, drawColor, DEFAULT_STROKE_WIDTH);
	}

	/**
	 * 根据传入的条件绘制圆角矩形，需手动定制 Paint 类。
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param rect 需要绘制的圆角矩形的矩形边界
	 * @param ellipseWidth 椭圆的宽度，用于确定圆角
	 * @param ellipseHeight 椭圆的高度，用于确定圆角
	 */
	public static void drawRoundRect(Canvas canvas, Paint paint, RectF rect,
									 float ellipseWidth, float ellipseHeight)
	{
		canvas.drawRoundRect(rect, ellipseWidth, ellipseHeight, paint);
	}

	/**
	 * 根据传入的条件绘制圆角矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param paint 用于绘图的 Paint 类
	 * @param rect 需要绘制的圆角矩形的矩形边界
	 * @param ellipseWidth 椭圆的宽度，用于确定圆角
	 * @param ellipseHeight 椭圆的高度，用于确定圆角
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRoundRect(Canvas canvas, Paint paint, RectF rect,
									 float ellipseWidth, float ellipseHeight, int drawColor,
									 float strokeWidth, int fillColor, int alpha)
	{
		paint = setPaintFillStyle(paint, fillColor, alpha);
		canvas.drawRoundRect(rect, ellipseWidth, ellipseHeight, paint);

		paint = setPaintDrawStyle(paint, drawColor, strokeWidth);
		canvas.drawRoundRect(rect, ellipseWidth, ellipseHeight, paint);
	}

	/**
	 * 根据传入的条件绘制圆角矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param rect 需要绘制的圆角矩形的矩形边界
	 * @param ellipseWidth 椭圆的宽度，用于确定圆角
	 * @param ellipseHeight 椭圆的高度，用于确定圆角
	 * @param drawColor 描边颜色
	 * @param strokeWidth 描边宽度
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRoundRect(Canvas canvas, RectF rect,
									 float ellipseWidth, float ellipseHeight, int drawColor,
									 float strokeWidth, int fillColor, int alpha)
	{
		drawRoundRect(canvas, new Paint(), rect, ellipseWidth, ellipseHeight,
				drawColor, strokeWidth, fillColor, alpha);
	}

	/**
	 * 根据传入的条件绘制圆角矩形
	 * @param canvas 用于绘图的 Canvas 类
	 * @param rect 需要绘制的圆角矩形的矩形边界
	 * @param ellipseWidth 椭圆的宽度，用于确定圆角
	 * @param ellipseHeight 椭圆的高度，用于确定圆角
	 * @param drawColor 描边颜色
	 * @param fillColor 填充颜色
	 * @param alpha 填充透明度，数值为0-255，0为完全透明
	 */
	public static void drawRoundRect(Canvas canvas, RectF rect,
									 float ellipseWidth, float ellipseHeight, int drawColor,
									 int fillColor, int alpha)
	{
		drawRoundRect(canvas, new Paint(), rect, ellipseWidth, ellipseHeight,
				drawColor, DEFAULT_STROKE_WIDTH, fillColor, alpha);
	}

	/**
	 * 根据传入的条件绘制文字，需手动定制 Paint 类。
	 * @param canvas 用于绘制的 Canvas 类
	 * @param paint 用于绘制的 Paint 类
	 * @param text 所需绘制的文字
	 * @param x 绘制文字起点的x坐标
	 * @param y 绘制文字起点的y坐标
	 */
	public static void drawText(Canvas canvas, Paint paint, String text,
								float x, float y)
	{
		canvas.drawText(text, x, y, paint);
	}

	/**
	 * 根据传入的条件绘制文字
	 * @param canvas 用于绘制的 Canvas 类
	 * @param paint 用于绘制的 Paint 类
	 * @param text 所需绘制的文字
	 * @param x 绘制文字起点的x坐标
	 * @param y 绘制文字起点的y坐标
	 * @param textColor 文字颜色
	 * @param strokeWidth 描边宽度
	 * @param textSize 文字大小
	 */
	public static void drawText(Canvas canvas, Paint paint, String text,
								float x, float y, int textColor, float strokeWidth, float textSize)
	{
		paint = setPaintForText(paint, textColor, strokeWidth, textSize);
		drawText(canvas, paint, text, x, y);
	}

	/**
	 * 根据传入的条件绘制文字
	 * @param canvas 用于绘制的 Canvas 类
	 * @param text 所需绘制的文字
	 * @param x 绘制文字起点的x坐标
	 * @param y 绘制文字起点的y坐标
	 * @param textColor 文字颜色
	 * @param strokeWidth 描边宽度
	 * @param textSize 文字大小
	 */
	public static void drawText(Canvas canvas, String text, float x, float y,
								int textColor, float strokeWidth, float textSize)
	{
		drawText(canvas, new Paint(), text, x, y, textColor, strokeWidth,
				textSize);
	}

	/**
	 * 根据传入的条件绘制文字
	 * @param canvas 用于绘制的 Canvas 类
	 * @param text 所需绘制的文字
	 * @param x 绘制文字起点的x坐标
	 * @param y 绘制文字起点的y坐标
	 * @param textColor 文字颜色
	 * @param textSize 文字大小
	 */
	public static void drawText(Canvas canvas, String text, float x, float y,
								int textColor, float textSize)
	{
		drawText(canvas, text, x, y, textColor, DEFAULT_STROKE_WIDTH, textSize);
	}

}
