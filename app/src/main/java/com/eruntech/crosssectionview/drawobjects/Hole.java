package com.eruntech.crosssectionview.drawobjects;

import com.eruntech.crosssectionview.utils.DrawUtil;
import com.eruntech.crosssectionview.valueobjects.BusinessEnum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;


/**
 * 管孔类（管孔的子孔），移植自VC++客户端，存在一些历史遗留变量
 * @author 覃远逸
 *
 */
public class Hole extends DrawObject
{
	// //////////////////////////////////////////////////////公有常量
	// //////////////////////////////////////////////////////私有常量
	@SuppressWarnings("unused")
	private final String DEBUG_TAG = "debug " + this.getClass().getSimpleName();
	/** 选中判断误差 **/
	private final float DEVIATION_FIX = 0.0f;
	private final int HOLE_OWNER_YOUXIAN = 0;
	private final int HOLE_OWNER_TIETONG = 1;
	private final int HOLE_OWNER_LIANTONG = 2;
	private final int HOLE_OWNER_YIDONG = 3;
	private final int HOLE_OWNER_DIANLI = 4;
	private final int HOLE_OWNER_DIANXIN = 5;
	private final int HOLE_OWNER_OTHER = 6;
	private final int HOLE_OWNER_REND = 7;
	// //////////////////////////////////////////////////////公有变量
	public float m_radius;
	public Point center;
	public Point m_rectPos;
	public float m_angle;
	public boolean isLeftWay;
	public int indexInTube;
	/** 编号，用于显示在表格 **/
	public String indexId;
	/** 产权，用于显示在表格 **/
	private String owner;
	public String getOwner()
	{
		return owner;
	}
	public void setOwner(String owner)
	{
		this.owner = owner;
		setOwnerViaString(owner);
	}
	/** 状态，用于显示在表格 **/
	public String status;
	/** 材质，用于显示在表格 **/
	public String material;
	// //////////////////////////////////////////////////////私有变量
	@SuppressWarnings("unused")
	private Context mContext;
	private int m_holeType;
	private int m_holeState;
	/** 是否有子孔 **/
	private boolean mIsHaveChild;
	private int m_HoleShape;
	private boolean m_isChildHole;
	private int m_holeOwner;
	private int m_childHoleRowNum;
	private int m_childHoleColumnNum;
	// //////////////////////////////////////////////////////属性
	private int rowIndex = 0;
	public int getRowIndex()
	{
		if (rowIndex == 0) {
			calculateRowAndColumnIndex();
		}
		return rowIndex;
	}

	private int columnIndex = 0;
	public int getColumnIndex()
	{
		if (columnIndex == 0) {
			calculateRowAndColumnIndex();
		}
		return columnIndex;
	}

	private int mChildHoleNum = 0;
	public int getChildHoleNum()
	{
		return mChildHoleNum;
	}

	private Hole[] mChildHoles;
	public Hole[] getChildHoles()
	{
		return mChildHoles;
	}
	public void setChildHoles(Hole[] childHoles)
	{
		mChildHoles = childHoles;
		mChildHoleNum = childHoles.length;
		mIsHaveChild = true;
	}

	/**
	 * @deprecated
	 * 请使用 {@link #Hole(Contex contex)} 来代替
	 */
	public Hole()
	{
		initHole();
	}

	public Hole(Context context) {
		mContext = context;

		initHole();
	}

	private void initHole()
	{
		center = new Point();
		m_rectPos = new Point();
		mIsHaveChild = false;
		m_HoleShape = 0;
		m_holeState = 8;
	}

	private void calculateRowAndColumnIndex()
	{
		// 计算管孔的行序号和列序号
		// 注意：如果是表头，attribute[INDEX] = "编号"
		// attribute[INDEX] = "1-2" 形如XX-XX
		String[] indexs = indexId.split("-");
		if (indexs.length == 2) {
			// 列表项
			rowIndex = Integer.parseInt(indexs[0]);
			columnIndex = Integer.parseInt(indexs[1]);
		} else {
			throw new RuntimeException("管孔编号格式错误，应该形如X-X, XX-X, XX-XX等");
		}
	}

	@Override
	public void draw(Canvas canvas)
	{
		// 绘制管孔
		int drawColor;
		int fillColor;

		// 确定画笔颜色
		if (m_holeType == BusinessEnum.TAO_VHOLE) {
			drawColor = Color.GRAY;
		} else {
			drawColor = Color.BLACK;
		}
		switch (m_holeState)// 根据管孔的不同状态，填充不同的颜色
		{
			case BusinessEnum.EMPTY_HOLE:// 空管孔
			{
				fillColor = 0xd7d7d7;
				break;
			}
			case BusinessEnum.OTHER_IMPROPRIATE_HOLE:// 占用
			{
				fillColor = 0x408080;
				break;
			}
			case BusinessEnum.FIBER_IMPROPRIATE_HOLE:// 光缆
			{
				if (m_holeType == BusinessEnum.PAD_HOLE) {
					fillColor = Color.WHITE;
				} else {
					fillColor = 0xffff00;
				}
				break;
			}
			case BusinessEnum.CABLE_IMPROPRIATE_HOLE:// 电缆
			{
				fillColor = 0xff00ff;
				break;
			}
			case BusinessEnum.BAD_HOLE:// 坏管孔
			{
				fillColor = Color.BLACK;
				break;
			}
			case BusinessEnum.ZUYONG_HOLE:// 租用
			{
				fillColor = 0x008000;
				break;
			}
			case BusinessEnum.YULIU_HOLE:// 预留
			{
				fillColor = 0x00ff00;
				break;
			}
			case BusinessEnum.NO_HOLE:// 无子孔
			{
				fillColor = 0xc8c8c8;
				break;
			}
			case BusinessEnum.HAVE_CHILD:// 有子孔
			{
				fillColor = 0xc8c8c8;
				break;
			}
			default:
			{
				fillColor = 0x640000;
				break;
			}
		}
		if (mIsHaveChild) {
			fillColor = 0xc8c8c8;
		}


		if (m_holeState == BusinessEnum.FIBER_IMPROPRIATE_HOLE
				&& m_holeType == BusinessEnum.PAD_HOLE) {
			switch (m_HoleShape)// 管孔材质
			{
				case 0:// 圆形管
				{
					//先画管孔外径
					DrawUtil.drawCircle(canvas, center, m_radius, drawColor,
							fillColor, DrawUtil.ALPHA_FULL);
					break;
				}
				case 1:// 波纹管
				{
					if (mIsHaveChild && m_holeType != BusinessEnum.PAD_SUBTAO
							|| m_holeType == BusinessEnum.PAD_HOLE)// 代表块孔
					{
						// 先画管孔外径
						DrawUtil.drawCircle(canvas, center, m_radius + 1,
								drawColor, fillColor, DrawUtil.ALPHA_FULL);
						DrawUtil.drawCircle(canvas, center, m_radius - 1,
								drawColor, fillColor, DrawUtil.ALPHA_FULL);
					} else {
						if (m_holeState != 0) {
							DrawUtil.drawCircle(canvas, center, m_radius - 1,
									drawColor, fillColor, DrawUtil.ALPHA_FULL);
						}

					}

					break;
				}
				case 2:// 蜂窝管
				{
					if (!mIsHaveChild)
						drawSixPolygon(canvas, center, m_radius);
					break;
				}
			}
			// 绘制填充的光缆
			drawColor = Color.BLACK;
			fillColor = 0xffff00;
			float radius = m_radius * 3 / 5;
			if (m_holeState != 0) {
				DrawUtil.drawCircle(canvas, center, radius, drawColor,
						fillColor, DrawUtil.ALPHA_FULL);
			}
		} else// 其他情况按正常方式显示
		{
			switch (m_HoleShape)// 管孔材质
			{
				case 0:// 圆形管
				{
					if (m_holeState != 0) {
						//先画管孔外径
						DrawUtil.drawCircle(canvas, center, m_radius, drawColor,
								fillColor, DrawUtil.ALPHA_FULL);
					}
					break;
				}
				case 1:// 波纹管
				{
					if (m_holeState != 0) {
						if (mIsHaveChild
								&& m_holeType != BusinessEnum.PAD_SUBTAO
								|| m_holeType == BusinessEnum.PAD_HOLE)// 代表块孔
						{
							// 先画管孔外径
							DrawUtil.drawCircle(canvas, center, m_radius + 1,
									drawColor, fillColor, DrawUtil.ALPHA_FULL);
							DrawUtil.drawCircle(canvas, center, m_radius - 1,
									drawColor, fillColor, DrawUtil.ALPHA_FULL);

						} else {
							DrawUtil.drawCircle(canvas, center, m_radius - 1,
									drawColor, fillColor, DrawUtil.ALPHA_FULL);
						}
					}

					break;
				}
				case 2:// 蜂窝管
				{
					if (!mIsHaveChild)
						drawSixPolygon(canvas, center, m_radius);
					break;
				}
			}
		}

		if (mIsHaveChild) {
			calChildHolePos();

			for (int i = 0; i < mChildHoleNum; i++) {
				mChildHoles[i].draw(canvas);
			}
		}

		// 管孔owner字体设置
		float size = (float) (Math.sqrt(2.0) * m_radius * 0.6);
		if (m_holeOwner < 6) {
			String ownerStr = "";
			switch (m_holeOwner)// 根据不同的管孔Owner填充不同的文字或颜色
			{
				case HOLE_OWNER_YOUXIAN:// 有线
				{
					break;
				}
				case HOLE_OWNER_TIETONG:// 铁通
				{
					ownerStr = "铁";
					break;
				}
				case HOLE_OWNER_LIANTONG:// 联通
				{
					ownerStr = "联";
					break;
				}
				case HOLE_OWNER_YIDONG:// 移动
				{
					ownerStr = "移";
					break;
				}
				case HOLE_OWNER_DIANLI:// 电力
				{
					ownerStr = "电力";
					break;
				}
				case HOLE_OWNER_DIANXIN:// 电信
				{
					ownerStr = "电";
					break;
				}
			}
			float textX = (float) (center.x - (size * 0.5));
			float textY = (float) (center.y - (size * 0.5));
			DrawUtil.drawText(canvas, ownerStr, textX, textY, Color.BLACK, 10);
			if (m_holeOwner == 4) {
				textX -= size * 0.45;
			}

		} else if (m_holeOwner == HOLE_OWNER_OTHER)// 其他
		{
			if (m_holeState != 0) {
				DrawUtil.drawCircle(canvas, center, m_radius, Color.BLACK,
						0x7d7d7d, DrawUtil.ALPHA_60_PERCENTAGE);
				if (m_holeState == BusinessEnum.FIBER_IMPROPRIATE_HOLE
						&& m_holeType == BusinessEnum.PAD_HOLE) {
					// 绘制填充的光缆
					float fillRadius = m_radius * 3.0f / 5.0f;
					DrawUtil.drawCircle(canvas, center, fillRadius, Color.BLACK,
							0x7d7d7d, DrawUtil.ALPHA_60_PERCENTAGE);
				}
			}
		} else if (m_holeOwner == HOLE_OWNER_REND)// 租用
		{
			if (m_holeState != 0) {
				DrawUtil.drawCircle(canvas, center, m_radius, Color.BLACK,
						0x008000, DrawUtil.ALPHA_60_PERCENTAGE);
			}
		}

		// 绘制管孔被选中标志
		drawSelectedRect(canvas);

		super.draw(canvas);
	}

	/**
	 * 绘制蜂窝管(正六边形)
	 * @param canvas
	 * @param centerPoint
	 * @param radius
	 */
	public void drawSixPolygon(Canvas canvas, Point centerPoint, float radius)
	{
		Point tempPoint;
		Point[] points = new Point[7];
		int i;
		for (i = 0; i < 7; i++) {
			tempPoint = new Point();
			tempPoint.x = centerPoint.x
					+ (int) (radius * Math.cos(Math.PI / 6 * (i * 2 + 1)
					+ m_angle));
			tempPoint.y = centerPoint.y
					+ (int) (radius * Math.sin(Math.PI / 6 * (i * 2 + 1)
					+ m_angle));
			points[i] = tempPoint;
		}
		Path sixPolygonPath = new Path();
		sixPolygonPath.moveTo(points[0].x, points[0].y);
		for (i = 1; i < 7; i++) {
			sixPolygonPath.lineTo(points[i].x, points[i].y);
		}
		DrawUtil.drawPath(canvas, sixPolygonPath, Color.BLACK, Color.GRAY,
				DrawUtil.ALPHA_EMPTY);
	}

	// 绘制管孔被选中标志
	public void drawSelectedRect(Canvas CDC)
	{
		// 如果自身不是子孔，则判断是否有子孔被选中，有则绘制子孔被选中的标志（外切矩形）
		if (m_isChildHole == false) {
			if (mIsHaveChild) {
				for (int i = 0; i < (m_childHoleRowNum * m_childHoleColumnNum); i++) {
					mChildHoles[i].drawSelectedRect(CDC);
				}
			}
		}

		if (mIsSelected) {
			Point[] rectPt = easyGetRectPoint();

			Path rectPath = new Path();
			rectPath.moveTo(rectPt[0].x, rectPt[0].y);
			for (int i = 1; i < 5; i++) {
				rectPath.lineTo(rectPt[i].x, rectPt[i].y);
			}
			DrawUtil.drawPath(CDC, rectPath, Color.RED, Color.GRAY,
					DrawUtil.ALPHA_EMPTY);
		}
	}

	/**
	 * 求圆的外切矩形的4个顶点坐标，圆心为m_pos，矩形的旋转角为m_angle
	 * @return 圆的外切矩形的4个顶点
	 */
	public Point[] easyGetRectPoint()
	{
		Point tempPoint;
		Point[] rectPts = new Point[5];
		float radius = (float) (m_radius * Math.sqrt(2.0));

		for (int i = 0; i < 5; i++) {
			tempPoint = new Point();
			rectPts[i] = tempPoint;
		}

		rectPts[0].x = center.x
				+ (int) (radius * Math.cos(Math.PI * 5 / 4 + m_angle));
		rectPts[0].y = center.y
				+ (int) (radius * Math.sin(Math.PI * 5 / 4 + m_angle));
		rectPts[1].x = center.x
				+ (int) (radius * Math.cos(Math.PI * 3 / 4 + m_angle));
		rectPts[1].y = center.y
				+ (int) (radius * Math.sin(Math.PI * 3 / 4 + m_angle));
		rectPts[2].x = center.x
				+ (int) (radius * Math.cos(Math.PI * 1 / 4 + m_angle));
		rectPts[2].y = center.y
				+ (int) (radius * Math.sin(Math.PI * 1 / 4 + m_angle));
		rectPts[3].x = center.x
				+ (int) (radius * Math.cos(-Math.PI * 1 / 4 + m_angle));
		rectPts[3].y = center.y
				+ (int) (radius * Math.sin(-Math.PI * 1 / 4 + m_angle));
		rectPts[4].x = rectPts[0].x;
		rectPts[4].y = rectPts[0].y;

		return rectPts;
	}

	/**
	 * 计算子孔位置
	 */
	public void calChildHolePos()
	{
		if (!mIsHaveChild)
			return;

		int i = 0;
		float radius;// 子孔半径
		float angle;// 子孔圆心相对管孔圆心偏角落

		int childHoleNum = 0;
		if (mChildHoleNum != 19) {
			if (mChildHoleNum >= 7) {
				childHoleNum = mChildHoleNum - 1;
			} else {
				childHoleNum = mChildHoleNum;
			}
			angle = (float) (Math.PI / childHoleNum);
			if (childHoleNum == 1)
				radius = m_radius * 0.75f;
			else
				radius = (float) (m_radius * Math.sin(angle) / (1.0 + Math
						.sin(angle)));

			int x;
			int y;

			if (isLeftWay) {
				angle = (float) (Math.PI * 3.0 / 2.0 + Math.PI / childHoleNum + m_angle);
				for (i = 0; i < childHoleNum; i++) {
					x = (int) (center.x + (m_radius - radius) * Math.cos(angle));
					y = (int) (center.y + (m_radius - radius) * Math.sin(angle));
					mChildHoles[i].m_radius = radius;// 子孔半径
					mChildHoles[i].center.x = x;// 子孔的中心位置
					mChildHoles[i].center.y = y;// 子孔的中心位置
					mChildHoles[i].m_angle = m_angle;// 子孔的角度，用于绘制外切矩形
					// m_childHoleList[i].m_tubeID = m_tubeID;//子孔所属的管道ID
					// m_childHoleList[i].m_pipID = m_pipID;//子孔所属的管道编号
					mChildHoles[i].indexInTube = indexInTube;
					angle = (float) (angle - Math.PI * 2.0 / childHoleNum);
				}
			} else {
				angle = (float) (-Math.PI / 2.0 - Math.PI / childHoleNum + m_angle);
				for (i = 0; i < childHoleNum; i++) {
					x = (int) (center.x + (m_radius - radius) * Math.cos(angle));
					y = (int) (center.y + (m_radius - radius) * Math.sin(angle));
					mChildHoles[i].m_radius = radius; // 子孔半径
					mChildHoles[i].center.x = x;// 子孔的中心位置
					mChildHoles[i].center.y = y;// 子孔的中心位置
					mChildHoles[i].m_angle = m_angle;
					// m_childHoleList[i].m_tubeID = m_tubeID;
					// m_childHoleList[i].m_pipID = m_pipID;
					mChildHoles[i].indexInTube = indexInTube;
					angle = (float) (angle + Math.PI * 2.0 / childHoleNum);
				}
			}
			if (childHoleNum == 1) {
				mChildHoles[0].center.x = center.x; // 子孔的中心位置
				mChildHoles[0].center.y = center.y; // 子孔的中心位置
				mChildHoles[0].m_angle = m_angle;
				// m_childHoleList[0].m_tubeID = m_tubeID;
				// m_childHoleList[0].m_pipID = m_pipID;
			}
			if (mChildHoleNum >= 7) {
				mChildHoles[mChildHoleNum - 1].m_radius = radius; // 子孔半径
				mChildHoles[mChildHoleNum - 1].center.x = center.x; // 子孔的中心位置
				mChildHoles[mChildHoleNum - 1].center.y = center.y; // 子孔的中心位置
				mChildHoles[mChildHoleNum - 1].m_angle = m_angle;
				// m_childHoleList[m_childHoleNum - 1].m_tubeID = m_tubeID;
				// m_childHoleList[m_childHoleNum - 1].m_pipID = m_pipID;
			}
		} else// 19孔特殊处理
		{
			radius = m_radius / 5.0f; // 子孔半径
			for (i = 0; i < mChildHoleNum; i++) {
				mChildHoles[i].m_radius = radius;// * m_Scale; //子孔半径
				mChildHoles[i].m_angle = m_angle;
				// m_childHoleList[i].m_tubeID = m_tubeID;
				// m_childHoleList[i].m_pipID = m_pipID;
				mChildHoles[i].indexInTube = indexInTube;
			}

			for (i = 0; i < 5; i++) {
				mChildHoles[i].center.x = (int) ((center.x - m_radius + radius
						* (i * 2 + 1)));// * m_Scale;
				mChildHoles[i].center.y = (center.y);// * m_Scale;
				mChildHoles[i].m_angle = m_angle;
				// m_childHoleList[i].m_tubeID = m_tubeID;
				// m_childHoleList[i].m_pipID = m_pipID;
			}

			for (i = 5; i < 9; i++) {
				mChildHoles[i].center.x = (int) (mChildHoles[2].center.x + (radius * ((i - 6) * 2 - 1)));// *
				// m_Scale;
				mChildHoles[i].center.y = (int) (mChildHoles[2].center.y - (radius * 2));// *
				// m_Scale;
				mChildHoles[i].m_angle = m_angle;
				// m_childHoleList[i].m_tubeID = m_tubeID;
				// m_childHoleList[i].m_pipID = m_pipID;
			}

			for (i = 9; i < 13; i++) {
				mChildHoles[i].center.x = (int) (mChildHoles[2].center.x + (radius * ((i - 10) * 2 - 1)));// *
				// m_Scale;
				mChildHoles[i].center.y = (int) (mChildHoles[2].center.y + (radius * 2));// *
				// m_Scale;
				mChildHoles[i].m_angle = m_angle;
				// m_childHoleList[i].m_tubeID = m_tubeID;
				// m_childHoleList[i].m_pipID = m_pipID;
			}

			for (i = 13; i < 16; i++) {
				mChildHoles[i].center.x = (int) (mChildHoles[2].center.x + radius
						* ((i - 14) * 2));// * m_Scale;
				mChildHoles[i].center.y = (int) (mChildHoles[2].center.y - (radius * 4));// *
				// m_Scale;
				mChildHoles[i].m_angle = m_angle;
				// m_childHoleList[i].m_tubeID = m_tubeID;
				// m_childHoleList[i].m_pipID = m_pipID;
			}

			for (i = 16; i < 19; i++) {
				mChildHoles[i].center.x = (int) (mChildHoles[2].center.x + radius
						* ((i - 17) * 2));// * m_Scale;
				mChildHoles[i].center.y = (int) (mChildHoles[2].center.y + (radius * 4));// *
				// m_Scale;
				mChildHoles[i].m_angle = m_angle;
				// m_childHoleList[i].m_tubeID = m_tubeID;
				// m_childHoleList[i].m_pipID = m_pipID;
			}
		}
	}

	public DrawObject getSelectedObject()
	{
		DrawObject selectedObject = null;

		if (mIsSelected) {
			selectedObject = this;
		} else if (mIsHaveChild) {
			for (Hole childHole : mChildHoles) {
				selectedObject = childHole.getSelectedObject();
				if (selectedObject != null) {
					break;
				}
			}
		}

		return selectedObject;
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
		} else if (mIsHaveChild) {
			for (Hole childHole : mChildHoles) {
				selectedObjectId = childHole.getSelectedObjectId();
				if (selectedObjectId > 0) {
					break;
				}
			}
		}

		return selectedObjectId;
	}

	public void setObjectIsSelected(long selectedObjectId)
	{
		if (this.id == selectedObjectId) {
			this.mIsSelected = true;
		} else if (mIsHaveChild) {
			for (Hole childHole : mChildHoles) {
				childHole.setObjectIsSelected(selectedObjectId);
			}
		}
	}

	@Override
	public boolean contains(float x, float y)
	{
		boolean isContained = false;
		Point tapPoint = new Point((int)x, (int)y);
		//如果点在管孔范围内
		if(lengthOfPointToPoint(tapPoint, center) <= (m_radius + DEVIATION_FIX)) {
			//如果有子孔，判断是否选中管孔的子孔
			if(mIsHaveChild) {
				for(Hole childHole : mChildHoles) {
					isContained = childHole.contains(x, y);
					if (isContained) {
						break;
					}
				}
			}
			//如果没有选中子孔，则是只选中管孔
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
		super.clearSelectedFlag();
		if(mIsHaveChild) {
			for(Hole childHole : mChildHoles) {
				childHole.clearSelectedFlag();
			}
		}
	}

	private void setOwnerViaString(String m_owner)//根据从XML获取的数据注明管孔的产权者
	{
		if (owner.equals("有线")) {
			m_holeOwner = HOLE_OWNER_YOUXIAN;
		} else if (owner.equals("铁通")) {
			m_holeOwner = HOLE_OWNER_TIETONG;
		} else if (owner.equals("联通")) {
			m_holeOwner = HOLE_OWNER_LIANTONG;
		} else if (owner.equals("移动")) {
			m_holeOwner = HOLE_OWNER_YIDONG;
		} else if (owner.equals("电力")) {
			m_holeOwner = HOLE_OWNER_DIANLI;
		} else if (owner.equals("电信")) {
			m_holeOwner = HOLE_OWNER_DIANXIN;
		} else if (owner.equals("其他")) {
			m_holeOwner = HOLE_OWNER_OTHER;
		} else if (owner.equals("租用")) {
			m_holeOwner = HOLE_OWNER_REND;
		} else {
			m_holeOwner = HOLE_OWNER_OTHER;
		}
	}

}
