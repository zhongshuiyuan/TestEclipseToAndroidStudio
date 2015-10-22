package com.eruntech.crosssectionview.drawobjects;

import com.eruntech.crosssectionview.utils.DrawUtil;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * 熔接器
 * @author 覃远逸
 */
public class Rjq extends DrawObject
{

	public Point center;
	public float radius;

	public Rjq()
	{

	}

	public void drawSelected(Canvas canvas, int selectedType, long selectedID)
	{

	}

	@Override
	public void draw(Canvas canvas)
	{
		DrawUtil.drawCircle(canvas, center, radius, Color.BLACK, Color.GRAY,
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
