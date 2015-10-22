package com.eruntech.crosssectionview.drawobjects;

import com.eruntech.crosssectionview.utils.DrawUtil;
import com.eruntech.crosssectionview.valueobjects.BusinessEnum;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * 人/手井展开图中的设备类
 * @author 覃远逸
 */
public class Device extends DrawObject
{

	// //////////////////////////////////////////////////////公有常量

	// //////////////////////////////////////////////////////私有常量
	// private final String DEBUG_TAG = "debug " +
	// this.getClass().getSimpleName();
	private final int MARK_SELECTED_CIRCLE_DRAWING_COLOR = Color.RED;
	private final int MARK_SELECTED_CIRCLE_FILLING_ALPHA = 0x00;
	private final int MARK_SELECTED_CIRCLE_RADIUS = 3;
	// //////////////////////////////////////////////////////公有变量
	/** 设备的中点，和井的中点一致 **/
	public Point center;
	// //////////////////////////////////////////////////////私有变量
	/* 旋转角度 **
	private float mRotateAngle;*/
	/** 离井中心的X偏移量 **/
	private float mOffsetX;
	/** 离井中心的Y偏移量 **/
	private float mOffsetY;
	/** 设备的宽 **/
	private float mWidth;
	/** 设备的高 **/
	private float mHeight;
	/** 设备类型 **/
	private int mDeviceType;

	/** 熔接桶半径 **/
	private float mRjtRadius;
	/** 熔接桶旋转角 **/
	private float mAngle;
	/** 熔接器半径 **/
	private float mRjqRadius;
	/** 熔接器数量 **/
	private int mRjqNum;
	/** 熔接桶子孔数量 **/
	private int mHoleNum;
	// //////////////////////////////////////////////////////属性
	/** 熔接器列表 **/
	private Rjq[] mRjqList;

	public Rjq[] getRjqList()
	{
		return mRjqList;
	}

	public void setRjqList(Rjq[] rjqList)
	{
		mRjqList = rjqList;
		mRjqNum = rjqList.length;
	}

	/** 熔接桶子孔列表 **/
	private RjtHole[] mRjtHoleList;

	public RjtHole[] getRjtHoleList()
	{
		return mRjtHoleList;
	}

	public void setRjtHoleList(RjtHole[] rjtHoleList)
	{
		this.mRjtHoleList = rjtHoleList;
		mHoleNum = rjtHoleList.length;
	}

	public Device()
	{
		super();
		//mRotateAngle = 0;
		mOffsetX = 0;
		mOffsetY = 0;
		mWidth = 30;
		mHeight = 30 * 0.618f;

		mRjtRadius = 20;

		mRjqNum = mRjqList.length;
		mHoleNum = mRjtHoleList.length;

		mAngle = 0.0f;
	}

	/**
	 * 被选中时高亮显示
	 * @param canvas 画布
	 */
	@Override
	protected void drawHighlight(Canvas canvas)
	{
		// 如果设备类型为熔接桶
		if (mDeviceType == BusinessEnum.EQU_TYPE_RJT) {
			Point rjtPoint = new Point();
			rjtPoint.x = (int) (center.x + mOffsetX);
			rjtPoint.y = (int) (center.y + mOffsetY);

			DrawUtil.drawCircle(canvas, rjtPoint, mRjtRadius, Color.RED, 5,
					Color.GRAY, (int) (0.2 * 255));
			/*
			 * this.getPaint().setStrokeWidth(5);
			 * this.getPaint().setStyle(Style.FILL_AND_STROKE);
			 * this.getPaint().setColor(Color.GRAY);
			 * this.getPaint().setAlpha(20); canvas.drawCircle(rjtPoint.x,
			 * rjtPoint.y, mRjtRadius, this.getPaint());
			 * 
			 * this.getPaint().setStyle(Style.STROKE);
			 * this.getPaint().setColor(Color.RED);
			 * this.getPaint().setAlpha(255); canvas.drawCircle(rjtPoint.x,
			 * rjtPoint.y, mRjtRadius, this.getPaint());
			 */
		}

		// 否则为其他设备
		else {
			Point devicePoint = new Point();
			devicePoint.x = (int) (center.x + mOffsetX - mWidth / 2 * mScale);
			devicePoint.y = (int) (center.y + mOffsetY - mHeight / 2 * mScale);
			float drawWidth = 2 * (mWidth / 2 * mScale);
			float drawHeight = 2 * (mHeight / 2 * mScale);
			// 左上角
			canvas.drawCircle(devicePoint.x, devicePoint.y, 3, this.getPaint());
			// 右上角
			devicePoint.x += drawWidth;
			canvas.drawCircle(devicePoint.x, devicePoint.y, 3, this.getPaint());
			// 右下角
			devicePoint.y += drawHeight;
			canvas.drawCircle(devicePoint.x, devicePoint.y, 3, this.getPaint());
			// 左下角
			devicePoint.x -= drawWidth;
			canvas.drawCircle(devicePoint.x, devicePoint.y, 3, this.getPaint());
		}
	}

	// 通过传入对象ID来重绘对象，实现被选中时高亮显示
	public void drawSelected(Canvas canvas, int selectedType, long selectedID)
	{
		int i = 0;
		if (selectedType == 1)// 被选中的对象为设备
		{
			if (id == selectedID) {
				drawHighlight(canvas);
			}
		} else if (selectedType == 2)// 被选中的对象为熔接器
		{
			for (i = 0; i < mRjqList.length; i++) {
				mRjqList[i].drawSelected(canvas, selectedType, selectedID);
			}
		} else// 被选中的对象为熔接桶子孔
		{
			for (i = 0; i < mRjtHoleList.length; i++) {
				mRjtHoleList[i].drawSelected(canvas, selectedType, selectedID);
			}
		}
	}

	@Override
	public void draw(Canvas canvas)
	{

		if (mDeviceType == BusinessEnum.EQU_TYPE_RJT)// 画熔接桶和熔接器
		{
			Point rjtPoint = new Point();
			rjtPoint.x = (int) (center.x + mOffsetX);
			rjtPoint.y = (int) (center.y + mOffsetY);
			// 绘制熔接桶
			drawRjt(canvas, rjtPoint);
			// 绘制熔接桶中的熔接器
			// drawrjtrjqRelation(CDC,rjtPoint);
			calHolePos(rjtPoint);
			calRjtHolePos();
			int i = 0;
			for (i = 0; i < mRjqNum; i++) {
				mRjqList[i].draw(canvas);// 绘制熔接器
			}
			for (i = 0; i < mHoleNum; i++) {
				mRjtHoleList[i].draw(canvas);// 绘制熔接桶的子孔
			}
		} else// 绘制其他设备
		{
			Point devicePoint = new Point();
			devicePoint.x = (int) (center.x + mOffsetX);
			devicePoint.y = (int) (center.y + mOffsetY);
			Point drawPoint = new Point();
			drawPoint.x = (int) (devicePoint.x - mWidth / 2 * mScale);
			drawPoint.y = (int) (devicePoint.y - mHeight / 2 * mScale);
			float drawWidth = 2 * (mWidth / 2 * mScale);
			float drawHeight = 2 * (mHeight / 2 * mScale);

			int penColor;
			int fillColor;
			int fillDeep;
			switch (mDeviceType) {
				case BusinessEnum.EQU_TYPE_JIETOU:// 2,电缆接头
				{
					// CDC.contentGroup.graphics.lineStyle(0, enmu.BLACK, 1);
					// CDC.contentGroup.graphics.beginFill(enmu.BLACK,
					// enmu.FILL_DEEP80);
					penColor = Color.BLACK;
					fillColor = Color.BLACK;
					fillDeep = (int) (0.2 * 255);
					break;
				}
				case BusinessEnum.EQU_TYPE_RONGJIE:// 1,光缆熔接器
				{

					// CDC.contentGroup.graphics.lineStyle(0, 0x000000, 1);
					// CDC.contentGroup.graphics.beginFill(0xffffff,
					// enmu.FILL_DEEP80);
					penColor = Color.BLACK;
					fillColor = Color.WHITE;
					fillDeep = (int) (0.2 * 255);
					break;
				}
				case BusinessEnum.EQU_TYPE_VRONGJIE:// 14,光缆虚拟熔接器
				{
					// CDC.contentGroup.graphics.lineStyle(0, 0x000000, 1);
					// CDC.contentGroup.graphics.beginFill(enmu.GRAY,
					// enmu.FILL_DEEP80);
					penColor = Color.BLACK;
					fillColor = Color.GRAY;
					fillDeep = (int) (0.2 * 255);
					break;
				}
				default:
				{
					// CDC.contentGroup.graphics.beginFill(enmu.GRAY,
					// enmu.FILL_DEEP80);
					penColor = Color.BLACK;
					fillColor = Color.GRAY;
					fillDeep = (int) (0.2 * 255);
					break;
				}
			}

			if (mIsSelected)// 选中绘制选中标志
			{
				Point tempPoint = new Point(drawPoint.x, drawPoint.y);
				// 左上角
				DrawUtil.drawCircle(canvas, tempPoint,
						MARK_SELECTED_CIRCLE_RADIUS,
						MARK_SELECTED_CIRCLE_DRAWING_COLOR, Color.GRAY,
						MARK_SELECTED_CIRCLE_FILLING_ALPHA);
				// 右上角
				tempPoint.x += drawWidth;
				DrawUtil.drawCircle(canvas, tempPoint,
						MARK_SELECTED_CIRCLE_RADIUS,
						MARK_SELECTED_CIRCLE_DRAWING_COLOR, Color.GRAY,
						MARK_SELECTED_CIRCLE_FILLING_ALPHA);
				// 右下角
				tempPoint.y += drawHeight;
				DrawUtil.drawCircle(canvas, tempPoint,
						MARK_SELECTED_CIRCLE_RADIUS,
						MARK_SELECTED_CIRCLE_DRAWING_COLOR, Color.GRAY,
						MARK_SELECTED_CIRCLE_FILLING_ALPHA);
				// 左下角
				tempPoint.x -= drawWidth;
				DrawUtil.drawCircle(canvas, tempPoint,
						MARK_SELECTED_CIRCLE_RADIUS,
						MARK_SELECTED_CIRCLE_DRAWING_COLOR, Color.GRAY,
						MARK_SELECTED_CIRCLE_FILLING_ALPHA);
			}

			RectF rect = new RectF(drawPoint.x, drawPoint.y, drawPoint.x
					+ drawWidth, drawPoint.y + drawHeight);
			DrawUtil.drawRoundRect(canvas, rect, 25, 25, penColor, fillColor,
					fillDeep);
		}

		super.draw(canvas);
	}

	/**
	 * 画熔接桶
	 * @param canvas 用于画图的 Canvas 类
	 * @param point 熔接桶的中心
	 */
	public void drawRjt(Canvas canvas, Point point)
	{
		int drawColor;
		if (mIsSelected) {
			drawColor = Color.RED;
		} else {
			drawColor = Color.BLACK;
		}

		// m_rjtX1 = point.x + m_rjtR * m_scaleX;//用于计算熔接桶子孔
		// m_rjtY1 = point.y + m_rjtR * m_scaleX;//用于计算熔接桶子孔
		DrawUtil.drawCircle(canvas, point, mRjtRadius, drawColor, Color.WHITE,
				(int) (0.8 * 255));
	}

	// 计算熔接桶孔位置信息
	public void calRjtHolePos()
	{
		if (mDeviceType != 42)
			return;

		int i = 0;
		float radius;// 子孔半径
		float angle;// 子孔圆心相对管孔圆心偏角落

		int childHoleSum = 0;
		if (mHoleNum != 19) {
			if (mHoleNum >= 7) {
				childHoleSum = mHoleNum - 1;
			} else {
				childHoleSum = mHoleNum;
			}
			angle = (float) (Math.PI / childHoleSum);
			if (childHoleSum == 1) {
				// radius = mRjtRadius * 0.75;
				radius = (float) ((mRjtRadius * 0.75) > 4 ? 4
						: (mRjtRadius * 0.75));
			} else {
				// radius = m_rjtR * Math.sin(angle) / (1.0 + Math.sin(angle));
				radius = (float) ((mRjtRadius * Math.sin(angle) / (1.0 + Math
						.sin(angle))) > 4 ? 4
						: (mRjtRadius * Math.sin(angle) / (1.0 + Math
						.sin(angle))));
			}

			float x;
			float y;

			angle = (float) (Math.PI * 3.0 / 2.0 + Math.PI / childHoleSum + mAngle);
			for (i = 0; i < childHoleSum; i++) {
				x = (float) (center.x + (mRjtRadius - radius) * Math.cos(angle));
				y = (float) (center.y + (mRjtRadius - radius) * Math.sin(angle));
				mRjtHoleList[i].radius = radius;// 子孔半径
				mRjtHoleList[i].center.x = (int) x;// 子孔的中心位置
				mRjtHoleList[i].center.y = (int) y;// 子孔的中心位置
				mRjtHoleList[i].angle = mAngle;// 子孔的角度，用于绘制外切矩形
				angle = angle - (float) (Math.PI * 2.0 / childHoleSum);
			}
			/*
			 * if(m_isLeftWay) { angle = Math.PI*3.0/2.0 + Math.PI/childHoleSum
			 * + m_angle; for(i=0;i<childHoleSum;i++) { x = m_pos.x +
			 * (m_rjtR-radius)*Math.cos(angle); y = m_pos.y +
			 * (m_rjtR-radius)*Math.sin(angle); m_rjtHoleList[i].m_rjtR =
			 * radius;//子孔半径 m_rjtHoleList[i].m_pos.x = x;//子孔的中心位置
			 * m_rjtHoleList[i].m_pos.y = y;//子孔的中心位置 m_rjtHoleList[i].m_angle =
			 * m_angle;//子孔的角度，用于绘制外切矩形 m_rjtHoleList[i].m_tubeID =
			 * m_tubeID;//子孔所属的管道ID m_rjtHoleList[i].m_pipID =
			 * m_pipID;//子孔所属的管道编号 m_rjtHoleList[i].m_indexInTube =
			 * m_indexInTube; angle = angle - Math.PI*2.0/childHoleSum; } } else
			 * { angle = -Math.PI/2.0 - Math.PI/childHoleSum + m_angle;
			 * for(i=0;i<childHoleSum;i++) { x = m_pos.x +
			 * (m_rjtR-radius)*Math.cos(angle); y = m_pos.y +
			 * (m_rjtR-radius)*Math.sin(angle); m_rjtHoleList[i].m_rjtR =
			 * radius; //子孔半径 m_rjtHoleList[i].m_pos.x = x;//子孔的中心位置
			 * m_rjtHoleList[i].m_pos.y = y;//子孔的中心位置 m_rjtHoleList[i].m_angle =
			 * m_angle; m_rjtHoleList[i].m_tubeID = m_tubeID;
			 * m_rjtHoleList[i].m_pipID = m_pipID;
			 * m_rjtHoleList[i].m_indexInTube = m_indexInTube; angle = angle +
			 * Math.PI*2.0/childHoleSum; } }
			 */
			if (childHoleSum == 1) {
				mRjtHoleList[0].center.x = center.x; // 子孔的中心位置
				mRjtHoleList[0].center.y = center.y; // 子孔的中心位置
				mRjtHoleList[0].angle = mAngle;
			}
			if (mHoleNum >= 7) {
				mRjtHoleList[mHoleNum - 1].radius = radius; // 子孔半径
				mRjtHoleList[mHoleNum - 1].center.x = center.x; // 子孔的中心位置
				mRjtHoleList[mHoleNum - 1].center.y = center.y; // 子孔的中心位置
				mRjtHoleList[mHoleNum - 1].angle = mAngle;
			}
		} else// 19孔特殊处理
		{
			// radius = m_rjtR / 5.0; //子孔半径
			radius = (float) ((mRjtRadius / 5.0) > 4 ? 4 : (mRjtRadius / 5.0));
			for (i = 0; i < mHoleNum; i++) {
				mRjtHoleList[i].radius = radius;// * m_Scale; //子孔半径
				mRjtHoleList[i].angle = mAngle;
			}

			for (i = 0; i < 5; i++) {
				mRjtHoleList[i].center.x = (int) (center.x - mRjtRadius + radius
						* (i * 2 + 1));// * m_Scale;
				mRjtHoleList[i].center.y = (center.y);// * m_Scale;
				mRjtHoleList[i].angle = mAngle;
			}

			for (i = 5; i < 9; i++) {
				mRjtHoleList[i].center.x = mRjtHoleList[2].center.x
						+ (int) (radius * ((i - 6) * 2 - 1));// * m_Scale;
				mRjtHoleList[i].center.y = mRjtHoleList[2].center.y
						- (int) (radius * 2);// * m_Scale;
				mRjtHoleList[i].angle = mAngle;
			}

			for (i = 9; i < 13; i++) {
				mRjtHoleList[i].center.x = mRjtHoleList[2].center.x
						+ (int) (radius * ((i - 10) * 2 - 1));// * m_Scale;
				mRjtHoleList[i].center.y = mRjtHoleList[2].center.y
						+ (int) (radius * 2);// * m_Scale;
				mRjtHoleList[i].angle = mAngle;
			}

			for (i = 13; i < 16; i++) {
				mRjtHoleList[i].center.x = mRjtHoleList[2].center.x
						+ (int) (radius * ((i - 14) * 2));// * m_Scale;
				mRjtHoleList[i].center.y = mRjtHoleList[2].center.y
						- (int) (radius * 4);// * m_Scale;
				mRjtHoleList[i].angle = mAngle;
			}

			for (i = 16; i < 19; i++) {
				mRjtHoleList[i].center.x = mRjtHoleList[2].center.x
						+ (int) (radius * ((i - 17) * 2));// * m_Scale;
				mRjtHoleList[i].center.y = mRjtHoleList[2].center.y
						+ (int) (radius * 4);// * m_Scale;
				mRjtHoleList[i].angle = mAngle;
			}
		}
	}

	// 计算熔接桶子孔和熔接器的位置
	public void calHolePos(Point rjtPoint)
	{
		int i = 0;
		/*
		 * //计算熔接桶子孔位置 //var num:Number; m_holeNum = m_rjtHoleList.length;//子孔数量
		 * 
		 * var scaleX:Number = 1;//来自模板 var scaleY:Number = 1;//来自模板 //scaleX =
		 * rjtTempInfo->m_Length/(m_rjtR*m_scaleX);//从模板获取长度 //scaleY =
		 * rjtTempInfo->m_Width/(m_rjtR*m_scaleY);//从模板获取宽度 var dx:Number =
		 * 0;//来自模板 var dy:Number = 0;//来自模板
		 * 
		 * for(i=0; i<m_holeNum; i++) { //var tempRjtHole:RjtHole =
		 * m_rjtHoleList[i]; dx = m_rjtHoleList[i].m_dx / scaleX; dy =
		 * m_rjtHoleList[i].m_dy / scaleY; m_rjtHoleList[i].m_radius =
		 * 4*m_scaleX; m_rjtHoleList[i].m_rectPos.x = m_rjtX1 + dx -
		 * 28*m_scaleX; m_rjtHoleList[i].m_rectPos.y = m_rjtY1 + dy -
		 * 28*m_scaleX; m_rjtHoleList[i].m_pos.x = m_rjtHoleList[i].m_rectPos.x
		 * - 3*m_scaleX; m_rjtHoleList[i].m_pos.y = m_rjtHoleList[i].m_rectPos.y
		 * - 3*m_scaleY; }
		 */

		// 计算熔接器位置
		// Point rjqPos = new Point();
		int rjqPosX;
		int rjqPosY;

		mRjqRadius = (mWidth / 6) * mScale;// 熔接器半径
		float angle = (float) ((2 * Math.PI) / mRjqNum);

		for (i = 0; i < mRjqNum; i++) {
			// 设置图形大小及位置
			if (i == 0) {
				rjqPosX = (int) (rjtPoint.x - mRjtRadius - mRjqRadius / mScale);
				rjqPosY = rjtPoint.y;
			} else {
				if (i * angle <= (Math.PI / 2)) {
					rjqPosX = rjtPoint.x
							- (int) ((mRjqRadius / mScale + mRjtRadius) * Math
							.cos(i * angle));
					rjqPosY = rjtPoint.y
							+ (int) ((mRjqRadius / mScale + mRjtRadius) * Math
							.sin(i * angle));
				} else if (i * angle > (Math.PI / 2) && i * angle <= Math.PI) {
					rjqPosX = rjtPoint.x
							+ (int) ((mRjqRadius / mScale + mRjtRadius) * Math
							.sin(i * angle - Math.PI / 2));
					rjqPosY = rjtPoint.y
							+ (int) ((mRjqRadius / mScale + mRjtRadius) * Math
							.cos(i * angle - Math.PI / 2));
				} else if (i * angle > Math.PI && i * angle <= 3 * Math.PI / 2) {
					rjqPosX = rjtPoint.x
							+ (int) ((mRjqRadius / mScale + mRjtRadius) * Math
							.cos(i * angle - Math.PI));
					rjqPosY = rjtPoint.y
							- (int) ((mRjqRadius / mScale + mRjtRadius) * Math
							.sin(i * angle - Math.PI));
				} else {
					rjqPosX = rjtPoint.x
							- (int) ((mRjqRadius / mScale + mRjtRadius) * Math
							.sin(i * angle - 3 * Math.PI / 2));
					rjqPosY = rjtPoint.y
							- (int) ((mRjqRadius / mScale + mRjtRadius) * Math
							.cos(i * angle - 3 * Math.PI / 2));
				}
			}

			mRjqList[i].center.x = rjqPosX;
			mRjqList[i].center.y = rjqPosY;
			mRjqList[i].radius = mRjqRadius;
		}
	}

	// 清除选中状态，即把所以选中状态重置为未选中
	public void clearFlag()
	{
		mIsSelected = false;
		int i = 0;
		for (i = 0; i < mHoleNum; i++) {
			mRjtHoleList[i].mIsSelected = false;
		}
		for (i = 0; i < mRjqNum; i++) {
			mRjqList[i].mIsSelected = false;
		}
	}

	/*
	 * //判断是否选中对象，并返回选中的对象类型及其ID public void
	 * selectedChecking(mouseDownPoint:Point) { var array:Array = new Array();
	 * var i:uint = 0; var selectedType:uint = 0; var selectedID:uint = 0; var
	 * selectedFronTube:uint = 0; var selectedPipID:int = -1; var
	 * devicePoint:Point = new Point((m_pos.x + m_dx), (m_pos.y + m_dy));//设备中点
	 * if(m_deviceType == enum.EQU_TYPE_RJT)//如果设备类型是熔接桶 { var length:Number =
	 * lengthOfPointToPoint(mouseDownPoint, devicePoint); if(length <= (m_rjtR +
	 * m_rjqRadius + 5))//如果距离小于熔接桶半径+熔接器半径（5为误差调整），则点落在熔接桶及其设备范围内 {
	 * //判断是否选中熔接桶子孔 for(i=0 ; i<m_rjtHoleList.length; i++) { array =
	 * m_rjtHoleList[i].selectedChecking(mouseDownPoint); if(array.length == 4)
	 * { m_isSelected = false;//选中的是子孔而不是熔接桶 return array; } } //判断是否选中熔接器
	 * for(i=0; i<m_rjqList.length; i++) { array =
	 * m_rjqList[i].selectedChecking(mouseDownPoint); if(array.length == 4) {
	 * m_isSelected = false;//选中的是熔接器而不是熔接桶 return array; } }
	 * //否则点落在熔接桶范围内时为只选中熔接桶 if(length <= m_rjtR) { m_isSelected = true;
	 * selectedType = 1; array.push(selectedType); selectedID = m_id;
	 * array.push(selectedID); selectedFronTube = 0;
	 * array.push(selectedFronTube); selectedPipID = 0;
	 * array.push(selectedPipID); } } } else//如果选中的是除熔接桶外的其他设备 { var
	 * startPoint:Point = new Point(); startPoint.x = devicePoint.x - m_width/2
	 * * m_scaleX; startPoint.y = devicePoint.y - m_height/2 * m_scaleY; var
	 * width:Number = 2 * ( m_width/2 * m_scaleX ); var height:Number = 2 * (
	 * m_height/2 * m_scaleY ); if(IsInRotateRect(startPoint, width, height,
	 * 0.0, mouseDownPoint) == true)//点中设备 { m_isSelected = true; selectedType =
	 * 1; array.push(selectedType); selectedID = m_id; array.push(selectedID);
	 * selectedFronTube = 0; array.push(selectedFronTube); selectedPipID = -1;
	 * array.push(selectedPipID); } }
	 * 
	 * return array; }
	 */

}
