package com.eruntech.crosssectionview.views;

import com.eruntech.crosssectionview.drawobjects.DrawObject;
import com.eruntech.crosssectionview.drawobjects.Hole;
import com.eruntech.crosssectionview.drawobjects.TubePad;
import com.eruntech.crosssectionview.interfaces.OnTubeViewObjectSeletedListener;
import com.eruntech.crosssectionview.utils.DrawUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 通过扩展View类，用来绘制管道截面展开图
 * @author 覃远逸
 * @see android.widget.ImageView
 */
public class TubeView extends View
{
	// //////////////////////////////////////////////////////公有常量
	// //////////////////////////////////////////////////////私有常量
	@SuppressWarnings("unused")
	private final String DEBUG_TAG = "debug " + this.getClass().getSimpleName();
	private final int NO_FINGER_TOUCH = 0;
	private final int ONE_FINGER_TOUCH = 1;
	private final int TWO_FINGER_TOUCH = 2;
	private final int THRESHOLD_FINGER_MOVE = 1;

	private final int MARGIN_CENTER_LINE_TO_TUBE = 20;
	// //////////////////////////////////////////////////////公有变量
	// //////////////////////////////////////////////////////私有变量
	private float mScale = 1;
	/** 屏幕中进行触控的点的数量（进行触控操作的手指数） **/
	private int touchNum = 0;
	/** 双指触控中两指的距离 **/
	private float distanceOfTwoFingers = 0;
	/** 双指按下时的中心点，或者单指按下时的点 **/
	private Point centerOfFingerTouch = new Point();
	/** 两指缩放时两指中心点相对 TubeView 的位置 **/
	private Point positionOfTouchPointInView = new Point();
	/** 检测手势 **/
	public GestureDetector mGestureDetector;
	/** DisplayView的宽度 **/
	private int mWidth = 0;
	/** DisplayView的高度 **/
	private int mHeight = 0;
	private Point positionOfTubeA = new Point();
	private Point positionOfTubeZ = new Point();
	private boolean mIsFirstTimeDraw = true;
	private OnTubeViewObjectSeletedListener mTubeObjectSeletedListener;
	// //////////////////////////////////////////////////////属性
	/** DisplayView的中点 **/
	private Point centerOfView = new Point();
	public Point getCenterOfView()
	{
		if (centerOfView == null) {
			centerOfView = new Point();
		}
		return centerOfView;
	}

	private TubePad tubeA;

	public TubePad getTubeA()
	{
		return tubeA;
	}

	public void setTubeA(TubePad tubeA)
	{
		this.tubeA = tubeA;
	}

	private TubePad tubeZ;

	public TubePad getTubeZ()
	{
		return tubeZ;
	}

	public void setTubeZ(TubePad tubeZ)
	{
		this.tubeZ = tubeZ;
	}

	public TubeView(Context context)
	{
		super(context);
		initView(context);
	}

	public TubeView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public TubeView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	/**
	 * 初始化变量值
	 */
	private void initView(Context context)
	{
		this.setLongClickable(true);
		mGestureDetector = new GestureDetector(context,
				new TubeViewGestureListener());
		if (context instanceof OnTubeViewObjectSeletedListener)
		{
			mTubeObjectSeletedListener = (OnTubeViewObjectSeletedListener)context;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = this.getWidth();
		mHeight = this.getHeight();
		centerOfView.x = mWidth / 2;
		centerOfView.y = mHeight / 2;

		calculateTubePadPosition();

		initDrawing();

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		// 首先判断是否包含GestureDetector手势（移动和双击）
		boolean isHadGestrue = mGestureDetector.onTouchEvent(ev);
		if (isHadGestrue) {
			return true;
		}

		//若不包含GestureDetector手势则判断是否包含其他手势
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:// 单指按下
				touchNum = ONE_FINGER_TOUCH;
				break;
			case MotionEvent.ACTION_UP:// 单指抬起
				touchNum = NO_FINGER_TOUCH;
				break;
			case MotionEvent.ACTION_POINTER_DOWN://多点按下
				touchNum += 1;
				//计算两指之间的距离，并计算两个手指间的中心点在缩放级别为1时（原始大小）的坐标点
				if(touchNum == 2) {
					distanceOfTwoFingers = calculateDistance(ev);
					centerOfFingerTouch = getCenterPoint(ev);
				}
				break;
			case MotionEvent.ACTION_POINTER_UP://多点抬起
				touchNum -= 1;
				break;
			case MotionEvent.ACTION_MOVE:// 移动
				// 双指手势
				if (touchNum == TWO_FINGER_TOUCH) {
					//计算两指之间的距离
					float newDistanceOfTwoFingers = calculateDistance(ev);
					//如果两指间距发生变化的大小超过阈值，则进行缩放
					if (Math.abs(newDistanceOfTwoFingers - distanceOfTwoFingers) > THRESHOLD_FINGER_MOVE) {
						zoomBy(centerOfFingerTouch, positionOfTouchPointInView, newDistanceOfTwoFingers / distanceOfTwoFingers);
						//计算在当前缩放级别以手指按下的点为屏幕中心时，屏幕左上角的位置
						//int scrollToX = (int)(centerOfFingerTouch.x * mScale - positionOfTouchPointInView.x);
						//int scrollToY = (int)(centerOfFingerTouch.y * mScale - positionOfTouchPointInView.y);
						//移动到计算好的位置，实现中心点缩放
						//scrollTo(scrollToX, scrollToY);

						//centerOfFingerTouch = getCenterPoint(ev);
						//zoomBy(centerOfFingerTouch, newDistanceOfTwoFingers / distanceOfTwoFingers);
						distanceOfTwoFingers = newDistanceOfTwoFingers;
					}
				}
				break;
			default:// 默认不进行处理
				break;
		}
		return true;
	}

	private void initDrawing()
	{
		if (tubeA != null && tubeZ != null) {
			mIsFirstTimeDraw = false;
			//计算管块位置
			calculateTubePadPosition();

			// 计算全图显示两个管块的缩放级别
			float tubeActualWidth = tubeA.width * 2;
			float scale = (mWidth / 2) / (tubeActualWidth + 40);

			// 进行缩放
			setScale(scale * 0.95f);

			// 计算屏幕需要滚动的距离
			int scrollToX = (int)(centerOfView.x * mScale - centerOfView.x);
			int scrollToY = (int)(centerOfView.y * mScale - centerOfView.y);

			// 移动到计算好的位置，实现中心点缩放
			scrollTo(scrollToX, scrollToY);
		}
	}

	/**
	 * 计算A端和Z端管块位置
	 */
	private void calculateTubePadPosition()
	{
		if (tubeA != null && tubeZ != null) {
			positionOfTubeA.x = (int)(mWidth / 2 - tubeA.width - MARGIN_CENTER_LINE_TO_TUBE);
			positionOfTubeA.y = mHeight / 2;
			positionOfTubeZ.x = (int)(mWidth / 2 + tubeZ.width + MARGIN_CENTER_LINE_TO_TUBE);
			positionOfTubeZ.y = mHeight / 2;
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (mIsFirstTimeDraw) {
			initDrawing();
		}
		canvas.scale(mScale, mScale);

		// 绘制背景
		canvas.drawColor(0xc0c0c0);
		canvas.drawRGB(0xc0, 0xc0, 0xc0);
		// 绘制镜面分割线，即中线
		//int left = 3;
		//int top = mHeight / 2 - 3;
		//int right = mWidth - 3;
		//int bottom = mHeight / 2 + 3;
		int left = mWidth / 2 - 3;
		int top = -mHeight;
		int right = mWidth / 2 + 3;
		int bottom = 2 * mHeight;
		DrawUtil.drawRect(canvas, left, top, right, bottom, Color.BLACK,
				0xffffbb, DrawUtil.ALPHA_FULL);

		// 绘制管块
		if (tubeA != null && tubeZ != null) {
			//绘制管块图形
			tubeA.center = positionOfTubeA;
			tubeA.drawBackground(canvas);
			tubeA.drawWall(canvas);
			tubeA.draw(canvas);
			tubeA.drawText(canvas);

			tubeZ.center = positionOfTubeZ;
			tubeZ.drawBackground(canvas);
			tubeZ.drawWall(canvas);
			tubeZ.draw(canvas);
			tubeZ.drawText(canvas);
		}
	}

	/**
	 * 计算触屏事件中两个触屏点之间的距离
	 * @param event 触屏事件MotionEvent
	 * @return 两个触屏点之间的距离
	 */
	private float calculateDistance(MotionEvent event)
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) (Math.sqrt(x * x + y * y));
	}

	/**
	 * 计算在缩放级别为1时(1:1)双指触屏事件中两个触屏点的中点
	 * @param event 触屏事件MotionEvent
	 * @return 两个触屏点的中点
	 */
	private Point getCenterPoint(MotionEvent event)
	{
		float x = (event.getX(0) + event.getX(1)) / 2;
		float y = (event.getY(0) + event.getY(1)) / 2;
		positionOfTouchPointInView = new Point((int)x, (int)y);
		x = x / mScale + getScrollX() / mScale;
		y = y / mScale + getScrollY() / mScale;
		return new Point((int)x, (int)y);
	}

	/**
	 * 在TubeView中用于处理双击手势和滚动手势
	 * @author 覃远逸
	 */
	private class TubeViewGestureListener extends
			GestureDetector.SimpleOnGestureListener
	{
		//双击
		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			centerOfFingerTouch.x = (int) e.getX();
			centerOfFingerTouch.y = (int) e.getY();
			zoomBy(centerOfFingerTouch, 2);
			return true;
			// return super.onDoubleTap(e);
		}

		//单击
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e)
		{
			if (tubeA == null || tubeZ == null) {
				return true;
			}

			tubeA.clearSelectedFlag();
			tubeZ.clearSelectedFlag();
			//计算手指按下的点在缩放级别为1时（原始大小）的坐标点
			int touchPointX = (int)(e.getX() / mScale + getScrollX() / mScale);
			int touchPointY = (int)(e.getY() / mScale + getScrollY() / mScale);

			// 设置镜像管块相同的选中效果
			// 通知Activity操作表格在表格显示选中效果
			DrawObject seletedObject = null;
			if (tubeA.contains(touchPointX, touchPointY)) {
				seletedObject = tubeA.getSelectedObject();
				tubeZ.setObjectIsSelected(seletedObject.id);
			}
			if (tubeZ.contains(touchPointX, touchPointY)) {
				seletedObject = tubeZ.getSelectedObject();
				tubeA.setObjectIsSelected(seletedObject.id);
			}
			if (seletedObject != null && seletedObject instanceof Hole) {
				mTubeObjectSeletedListener.onHoleSelected((Hole) seletedObject);
			} else {
				mTubeObjectSeletedListener.onHoleUnselected();
			}
			postInvalidate();
			return true;
			//return super.onSingleTapConfirmed(e);
		}

		//滚动
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
								float distanceX, float distanceY)
		{
			scrollBy((int) distanceX, (int) distanceY);
			return true;
			// return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/**
	 * 缩放管道截面展开图
	 * @param scale 缩放系数，大于1时进行放大，小于1时进行缩小
	 */
	public void setScale(float scale)
	{
		mScale = scale;
		postInvalidate();
	}

	/** 放大至当前的2倍 **/
	public void zoomIn()
	{
		zoomBy(2);
	}

	/** 缩小至当前的1/2 **/
	public void zoomOut()
	{
		zoomBy(1/2);
	}

	/**
	 * 基于当前的缩放级别，缩放到指定倍数，当缩放倍数大于1时进行放大，缩放倍数小于1时进行缩小
	 * @param multiple 缩放倍数
	 */
	public void zoomBy(float multiple)
	{
		this.setScale(mScale * multiple);
	}

	/**
	 * 基于当前的缩放级别，缩放到指定倍数，当缩放倍数大于1时进行放大，缩放倍数小于1时进行缩小
	 * @param point 聚焦点，缩放前后，该点相对于 TubeView 左上角的位置不发生改变
	 * @param multiple 缩放倍数
	 */
	public void zoomBy(Point point, float multiple)
	{
		// 计算聚焦点在缩放级别为1时（原始大小）的坐标点
		Point focusedPoint = new Point();
		focusedPoint.x = (int)((point.x + getScrollX()) / mScale);
		focusedPoint.y = (int)((point.y + getScrollY()) / mScale);
		// 缩放
		zoomBy(multiple);
		// 计算在缩放前后，聚焦点相对于 TubeView 左上角的位置不发生改变时， TubeView 左上角的位置
		int scrollToX = (int)(focusedPoint.x * mScale - point.x);
		int scrollToY = (int)(focusedPoint.y * mScale - point.y);
		// 移动到计算好的位置，实现聚焦点缩放
		scrollTo(scrollToX, scrollToY);
	}

	/**
	 * 基于当前的缩放级别，缩放到指定倍数，当缩放倍数大于1时进行放大，缩放倍数小于1时进行缩小
	 * @param originPoint 聚焦点在缩放级别为1时（原始大小）的坐标点
	 * @param point 聚焦点，缩放前后，该点相对于 TubeView 左上角的位置不发生改变
	 * @param multiple 缩放倍数
	 */
	public void zoomBy(Point originPoint, Point point, float multiple)
	{
		// 缩放
		zoomBy(multiple);
		// 计算在缩放前后，聚焦点相对于 TubeView 左上角的位置不发生改变时， TubeView 左上角的位置
		int scrollToX = (int)(originPoint.x * mScale - point.x);
		int scrollToY = (int)(originPoint.y * mScale - point.y);
		// 移动到计算好的位置，实现聚焦点缩放
		scrollTo(scrollToX, scrollToY);
	}



}
