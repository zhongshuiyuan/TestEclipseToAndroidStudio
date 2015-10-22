package com.eruntech.crosssectionview.drawobjects;

import com.eruntech.crosssectionview.utils.DrawUtil;
import com.eruntech.crosssectionview.valueobjects.BusinessEnum;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * 熔接桶孔
 * @author 覃远逸
 */
public class RjtHole extends DrawObject
{
	public float radius;
	public Point center;
	public float angle;
	public int holeState;

	public RjtHole()
	{

	}

	public void drawSelected(Canvas canvas, int selectedType, long selectedID)
	{
		// if(m_id == selectedID)
		// {
		// CDC.contentGroup.graphics.lineStyle(1, enum.RED, enum.FILL_DEEP100);
		// CDC.contentGroup.graphics.beginFill(enum.GRAY, enum.FILL_DEEP0);
		// CDC.contentGroup.graphics.drawCircle(m_pos.x, m_pos.y, m_radius);
		// CDC.contentGroup.graphics.endFill();
		// }
	}

	@Override
	public void draw(Canvas canvas)
	{
		int fillColor;
		if (holeState == BusinessEnum.FIBER_IMPROPRIATE_HOLE) {
			fillColor = Color.GREEN;
		} else {
			fillColor = Color.WHITE;
		}
		DrawUtil.drawCircle(canvas, center, radius, Color.BLACK, fillColor,
				DrawUtil.ALPHA_80_PERCENTAGE);

		super.draw(canvas);
	}

	@Override
	protected void drawHighlight(Canvas canvas)
	{
		super.drawHighlight(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(DrawUtil.DEFAULT_STROKE_WIDTH);
		DrawUtil.drawCircle(canvas, paint, center, radius);
	}

}
