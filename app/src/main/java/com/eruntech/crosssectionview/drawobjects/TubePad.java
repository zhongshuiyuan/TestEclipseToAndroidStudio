package com.eruntech.crosssectionview.drawobjects;

import java.util.Arrays;
import java.util.Comparator;

import com.eruntech.crosssectionview.utils.DrawUtil;
import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

public class TubePad extends DrawObject implements Cloneable
{

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO 自动生成的方法存根
		TubePad tp = null;
		tp = (TubePad)super.clone();
		return tp;
	}

	// //////////////////////////////////////////////////////公有常量
	// //////////////////////////////////////////////////////私有常量
	private final String DEBUG_TAG = "debug " + this.getClass().getSimpleName();
	// //////////////////////////////////////////////////////公有变量
	/** 管块中心，在管道截面展开图中需要用到 **/
	public Point center;
	/** 管块所在井壁面的左上角 **/
	public Point wallTopLeftPoint;
	/** 旋转角 **/
	public float angle;
	/** 管块宽度 **/
	public float width;
	/** 管块高度 **/
	public float height;
	/** 是否是左向，在管道截面展开图中需要用到 **/
	public boolean isLeftWay;
	/** 是否属于有线 **/
	public boolean isBelongToYouxian;
	/** 管块形状 **/
	public float tubeShape;
	/** 管孔间距 **/
	public float holeKern;
	/** 管块在井壁上的x轴偏移量 **/
	public float offsetX;
	/** 管块在井壁上的y轴偏移量 **/
	public float offsetY;
	/** 管块的A端地址，用于管道截面展开图 **/
	public String addressA;
	/** 管块的Z端地址，用于管道截面展开图 **/
	public String addressZ;

	// //////////////////////////////////////////////////////私有变量
	/** 井壁面是否被选中，在管道截面展开图中需要用到 **/
	private boolean mIsWallSelected;
	/** 井壁面宽度，在管道截面展开图中需要用到 **/
	private float mWallWidth;
	/** 井壁面高度，在管道截面展开图中需要用到 **/
	private float mWallHeight;
	/** 上下文 **/
	private Context mContext;
	// //////////////////////////////////////////////////////属性
	/** 管孔数量 **/
	private int mHoleNum = 0;
	public int getHoleNum()
	{
		return mHoleNum;
	}

	/** 管块中管孔的列数 **/
	private int columnNum;
	public int getColumnNum()
	{
		return columnNum;
	}
	public void setColumnNum(int columnNum)
	{
		this.columnNum = columnNum;
		if (rowNum == 0) {
			if (mHoleNum > 0) {
				rowNum = mHoleNum % columnNum == 0 ? mHoleNum / columnNum
						: (mHoleNum / columnNum) + 1;
			}
		}
		Log.v(DEBUG_TAG, "TubePad" + this.id);
		Log.v(DEBUG_TAG, "columnNum: " + this.columnNum);
		Log.v(DEBUG_TAG, "rowNum: " + this.rowNum);
	}

	/** 管块中管孔的行数 **/
	private int rowNum;
	public int getRowNum()
	{
		return rowNum;
	}
	public void setRowNum(int rowNum)
	{
		this.rowNum = rowNum;
		if (mHoleNum > 0) {
			columnNum = mHoleNum % rowNum == 0 ? mHoleNum / rowNum
					: (mHoleNum / rowNum) + 1;
		}
		Log.v(DEBUG_TAG, "TubePad" + this.id);
		Log.v(DEBUG_TAG, "columnNum: " + this.columnNum);
		Log.v(DEBUG_TAG, "rowNum: " + this.rowNum);
	}

	/** 管孔集合 **/
	private Hole[] mHoles;
	public Hole[] getHoles()
	{
		return mHoles;
	}
	public void setHoles(Hole[] holes)
	{
		try {
			Arrays.sort(holes, new HoleComparator());
		} catch (Exception e) {
			// 异常出现在调用getMirroredTubePad方法时，getMirroredTubePad方法调用了setHoles方法
			// 生成的mirroredTubePad缺少一些不需要的属性，导致无法进行排序，
			// 并且mirroredTubePad也不需要进行排序
			// 直接忽略就行
		} finally {
			this.mHoles = holes;
			mHoleNum = holes.length;
		}
	}


	/**
	 * @deprecated
	 * 请使用 {@link #TubePad(Contex contex)} 来代替
	 */
	@Deprecated
	public TubePad()
	{
		super();

		initTubePad();
	}

	public TubePad(Context context)
	{
		super();

		mContext = context;

		initTubePad();
	}

	private void initTubePad()
	{
		center = new Point();
		id = 9999;//默认id，管道截面展开图中不需要管块id
		wallTopLeftPoint = new Point();
		holeKern = 0;
		offsetX = 0;
		offsetY = 0;
		isLeftWay = false;
		mHoleNum = 0;
		angle = 0;
		mIsWallSelected = false;
		mWallWidth = 0;
		mWallHeight = 0;

		// 以下为测试代码，可删除
		tubeShape = 0;
		width = 120;
		height = 80;
	}


	public void calHolePos()
	{
		// 采用相对坐标
		Point topLeftPoint = getTopLeftCornerPosition();// 管道左上角的坐标
		float xD = 0;// X方向计算的直径
		float yD = 0;// Y方向计算的直径
		float xKern = 0;// X方向管孔间距
		float yKern = 0;// Y方向管孔间距
		float dx = 0;
		float dy = 0;
		float rectDx = 0;
		float rectDy = 0;
		float diameter = 0;// 孔直径
		int i = 0;
		int j = 0;

		xD = (width - (columnNum + 1) * holeKern) / columnNum;
		yD = (height - (rowNum + 1) * holeKern) / rowNum;
		if (xD > yD)// 取最小的直径，否则在另一个方向会溢出管块
		{
			diameter = yD;
			xKern = (width - columnNum * diameter) / (columnNum + 1);
			yKern = holeKern;
		} else {
			diameter = xD;
			xKern = holeKern;
			yKern = (height - rowNum * diameter) / (rowNum + 1);
		}

		for (i=0; i<rowNum; i++) {
			dy = offsetY + diameter / 2 + (i + 1) * yKern + i * diameter;
			rectDy = offsetY + i * yKern + i * diameter;

			for (j=0; j<columnNum; j++) {
				if (isLeftWay) {
					dx = offsetX + width
							- (xKern * j + diameter * j + diameter / 2);
				} else {
					dx = offsetX + diameter / 2 + j * xKern + j * diameter;
					rectDx = offsetX + j * xKern + j * diameter;
				}

				// 防止溢出
				int overflow = i * columnNum + j + 1;
				if (overflow > mHoles.length)
					return;
				// 子孔的圆心计算思路：已知平行四边形的顶点m_pos，宽dx和高dy，求右下顶点即为子孔的圆心
				mHoles[i * columnNum + j].m_radius = diameter / 2;
				mHoles[i * columnNum + j].center.x = (int) (topLeftPoint.x + dx
						* Math.cos(angle) - dy * Math.sin(angle));
				mHoles[i * columnNum + j].center.y = (int) (topLeftPoint.y + dx
						* Math.sin(angle) + dy * Math.cos(angle));
				mHoles[i * columnNum + j].m_rectPos.x = (int) (topLeftPoint.x + rectDx
						* Math.sin(Math.PI / 2) + rectDy
						* Math.cos(Math.PI / 2));
				mHoles[i * columnNum + j].m_rectPos.y = (int) (topLeftPoint.y - rectDx
						* Math.cos(Math.PI / 2) + rectDy
						* Math.sin(Math.PI / 2));
				mHoles[i * columnNum + j].m_angle = angle;
				mHoles[i * columnNum + j].isLeftWay = isLeftWay;
				mHoles[i * columnNum + j].indexInTube = i * columnNum + j;
				mHoles[i * columnNum + j].calChildHolePos();
			}
		}
	}

	@Override
	public void draw(Canvas canvas)
	{
		// 绘制背景
		// drawBackground(canvas);

		// 绘制井壁范围
		// drawWall(canvas);

		// 绘制管道
		int penColor;
		if (mIsSelected) {
			penColor = Color.BLUE;
		} else if (isBelongToYouxian) {
			penColor = 0x9400d3;
		} else {
			penColor = Color.BLUE;
		}

		Point topLeftPt = getTopLeftCornerPosition();
		Point[] rectPt;
		rectPt = getRectPoints(topLeftPt, width, height);

		int i = 0;
		if ((tubeShape == 0) || mIsSelected || isBelongToYouxian)// 管块形状：0、圆形管 1、波纹管 2、蜂窝管
		{
			// 绘制管块（即管块的4条边）
			DrawUtil.drawRect(canvas, topLeftPt.x, topLeftPt.y, topLeftPt.x + width, topLeftPt.y + height, penColor, Color.GRAY, DrawUtil.ALPHA_EMPTY);

			// 如果是被选中的管孔则在管块的4个角绘制圆形标识
			if (mIsSelected) {
				float highlightCircleRadius = 3;
				for (i = 0; i < 4; i++) {
					DrawUtil.drawCircle(canvas, rectPt[i],
							highlightCircleRadius, Color.RED, Color.GRAY,
							DrawUtil.ALPHA_40_PERCENTAGE);
				}
			}

		}

		calHolePos();

		for (i = 0; i < mHoleNum; i++) {
			mHoles[i].draw(canvas); // 绘制管孔对象
		}

		for (i = 0; i < mHoleNum; i++) {
			// m_holeList[i].drawSelectedRect(canvas); //绘制管孔选中方框
		}

		for (i = 0; i < mHoleNum; i++) {
			// m_holeList[i].drawFiberRect(canvas); //绘制光缆选中方框
		}
		super.draw(canvas);
	}

	@Override
	protected void drawHighlight(Canvas canvas)
	{
		// TODO 自动生成的方法存根
		super.drawHighlight(canvas);
	}

	/**
	 * 获取管块左上角的点
	 * @return 管块左上角的点
	 */
	public Point getTopLeftCornerPosition()
	{
		Point topLeftCorner = new Point();
		if (center.x != 0 && center.y != 0) {
			topLeftCorner.x = center.x - (int) (width / 2);
			topLeftCorner.y = center.y - (int) (height / 2);
		} else if (wallTopLeftPoint.x != 0 && wallTopLeftPoint.y != 0) {
			topLeftCorner.x = wallTopLeftPoint.x + (int)(offsetX * Math.sin(Math.PI / 2 + angle) + offsetY * Math.cos(Math.PI / 2 + angle));
			topLeftCorner.y = wallTopLeftPoint.y - (int)(offsetX * Math.cos(Math.PI / 2 + angle) + offsetY * Math.sin(Math.PI / 2 + angle));
		} else {
			Log.e(DEBUG_TAG, "无法正确计算出管块左上角的位置！");
			Log.i(DEBUG_TAG, "如果是在管道界面展开图中，请检查管块中点的赋值是否正确。");
			Log.i(DEBUG_TAG, "如果是在人/手井展开图中，请检查管块左上角点的赋值是否正确，在WellWall的calculatePos方法中。");
		}
		return topLeftCorner;
	}

	/**
	 * 绘制背景，仅用于管道截面展开图
	 * @param canvas 用于绘制的 Canvas 类
	 */
	public void drawBackground(Canvas canvas)
	{
		DrawUtil.drawRect(canvas, center.x - width, center.y - height,
				center.x + width, center.y + height, 0xffffbb,
				0xffffbb, DrawUtil.ALPHA_FULL);
	}

	/**
	 * 绘制井壁面，仅用于管道截面展开图
	 * @param canvas 用于绘制的 Canvas 类
	 */
	public void drawWall(Canvas canvas)
	{
		mWallWidth = width * 1.3f;
		mWallHeight = height * 1.3f;
		wallTopLeftPoint.x = (int)(center.x - mWallWidth / 2);
		wallTopLeftPoint.y = (int)(center.y - mWallHeight / 2);
		int drawColor = mIsWallSelected ? Color.BLUE : Color.BLACK;
		int strokeWidth = mIsWallSelected ? 4 : DrawUtil.DEFAULT_STROKE_WIDTH;
		DrawUtil.drawRect(canvas, center.x - mWallWidth / 2, center.y - mWallHeight / 2,
				center.x + mWallWidth / 2, center.y + mWallHeight / 2, drawColor, strokeWidth,
				0xffffbb, DrawUtil.ALPHA_FULL);
	}

	/**
	 * 用于在管道截面展开图中绘制管块地址信息
	 * @param canvas
	 */
	public void drawText(Canvas canvas)
	{
		final int fontSize = 20;
		//获取管块左上角的位置
		Point point = getTopLeftCornerPosition();
		//通过管块左上角位置获取背景左上角的位置
		point.x -= width / 2;
		point.y -= height / 2 + fontSize;

		DrawUtil.drawText(canvas, getTubeViewAddress(), point.x, point.y, Color.RED, 1, fontSize);
	}

	public DrawObject getSelectedObject()
	{
		DrawObject object = null;

		if (mIsSelected) {
			object = this;
		} else if (this.getHoleNum() > 0) {
			for (Hole hole : mHoles) {
				object = hole.getSelectedObject();
				if (object != null) {
					break;
				}
			}
		}

		return object;
	}

	/**
	 * 获取被选中对象的ID
	 * @return 被选中对象的ID，返回0时代表没有对象被选中
	 */
	public long getSelectedObjectId()
	{
		long selectedObjectId = 0;

		if (mIsSelected) {
			selectedObjectId = this.id;
		} else if (this.getHoleNum() > 0) {
			for (Hole hole : mHoles) {
				selectedObjectId = hole.getSelectedObjectId();
				if (selectedObjectId > 0) {
					break;
				}
			}
		}

		return selectedObjectId;
	}

	/**
	 * 通过对象ID来设置对象为选中状态，用于管道截面展开图
	 * @param selectedObjectId 对象ID
	 */
	public void setObjectIsSelected(long selectedObjectId)
	{
		if (this.id == selectedObjectId) {
			this.mIsSelected = true;
		} else if (this.getHoleNum() > 0) {
			for (Hole hole : mHoles) {
				hole.setObjectIsSelected(selectedObjectId);
			}
		}
	}

	/**
	 * 在管道截面展开图中，返回管块地址
	 * @return 管块地址
	 */
	public String getTubeViewAddress()
	{
		String tubeViewAddress;

		if (!isLeftWay) {
			tubeViewAddress = mContext.getString(R.string.tubepad_address_title_a) + addressA;
		} else {
			tubeViewAddress = mContext.getString(R.string.tubepad_address_title_z) + addressZ;
		}

		return tubeViewAddress;
	}

	/**
	 * 获取镜像管块（包含管孔、子孔信息），镜像管块的信息和原管块一致，只是方向发生了变化。
	 * @return 镜像管块
	 */
	public TubePad getMirroredTubePad()
	{
		TubePad tempTP = new TubePad(mContext);
		try {
			tempTP = (TubePad) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		tempTP.isLeftWay = !this.isLeftWay;
		return tempTP;
		/*//镜像管块
		TubePad mirroredTubePad = new TubePad(mContext);
		//管孔数量
		final int holeNum = mHoleNum;
		if (holeNum > 0) {
			//镜像管孔集合
			Hole[] mirroredHoles = new Hole[holeNum];
			for (int i=0; i<holeNum; i++) {
				//原管孔
				Hole hole = mHoles[i];
				//镜像管孔
				Hole mirroredHole = new Hole(mContext);
				//复制管孔信息到镜像管孔
				mirroredHole.id = hole.id;
				mirroredHole.parentId = hole.parentId;
				mirroredHole.setOwner(hole.getOwner());
				//子孔数量
				final int childHoleNum = hole.getChildHoleNum();
				if (childHoleNum > 0) {
					//镜像子孔集合
					Hole[] mirroredChildHoles = new Hole[childHoleNum];
					for (int j=0; j<childHoleNum; j++) {
						//子孔
						Hole childHole = hole.getChildHoles()[j];
						//镜像子孔
						Hole mirroredChildHole = new Hole(mContext);
						//复制子孔信息到镜像子孔
						mirroredChildHole.id = childHole.id;
						mirroredChildHole.parentId = childHole.parentId;
						//将镜像子孔放入镜像子孔集合
						mirroredChildHoles[j] = mirroredChildHole;
					}
					mirroredHole.setChildHoles(mirroredChildHoles);
				}
				//将管孔放入镜像管孔集合
				mirroredHoles[i] = mirroredHole;
			}
			mirroredTubePad.setHoles(mirroredHoles);
		}
		//复制管块信息到镜像管块
		mirroredTubePad.isLeftWay = !this.isLeftWay;
		mirroredTubePad.setRowNum(this.getRowNum());
		mirroredTubePad.holeKern = this.holeKern;
		mirroredTubePad.addressA = this.addressA;
		mirroredTubePad.addressZ = this.addressZ;
		return mirroredTubePad;*/
	}

	@Override
	public boolean contains(float x, float y)
	{
		boolean isContained = false;
		Point tapPoint = new Point((int)x, (int)y);
		Point tubeTlPt = getTopLeftCornerPosition();

		// 如果是管道截面展开图
		if (center.x > 0 && center.y > 0) {
			//如果按下的点落在井壁面内
			if(IsInRotateRect(wallTopLeftPoint, mWallWidth, mWallHeight, angle, tapPoint)) {
				mIsWallSelected = true;
			}
		}

		//如果按下的点落在管道
		if(IsInRotateRect(tubeTlPt, width, height, angle, tapPoint)) {
			//判断是否选中管孔
			if (getHoleNum() > 0) {
				for(Hole hole : mHoles) {
					isContained = hole.contains(x, y);
					if (isContained) {
						break;
					}

				}
			}
			//如果没有选中管孔，则是只选中管块
			if(!isContained) {
				isContained = true;
				mIsSelected = true;
			}
		}
		return isContained;
	}

	@Override
	public void clearSelectedFlag()
	{
		mIsWallSelected = false;
		if (getHoleNum() > 0) {
			for (Hole hole : mHoles) {
				hole.clearSelectedFlag();
			}
		}

		super.clearSelectedFlag();
	}

	/**
	 * 管孔排序比较器，对管孔的编号进行比较，行序号越小的越往前，相同行序号时列序号越小的越往前
	 */
	private class HoleComparator implements Comparator<Hole>
	{

		@Override
		public int compare(Hole lhs, Hole rhs)
		{
			if (lhs.getRowIndex() == rhs.getRowIndex()) {
				if (lhs.getColumnIndex() > rhs.getColumnIndex()) {
					return 1;
				} else if (lhs.getColumnIndex() < rhs.getColumnIndex()) {
					return -1;
				} else {
					throw new RuntimeException("出现相同编号（行列序号）的管孔！");
				}
			} else {
				if(lhs.getRowIndex() > rhs.getRowIndex()) {
					return 1;
				} else {
					return -1;
				}
			}
		}

	}

}
