package com.qingdao.shiqu.arcgis.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.view.View;

/***
 * 
 * 
 * 作 者： 杜明悦
 * 
 * 功 能： 绘图工具类
 * 
 * 时 间： 2014年11月12日
 * 
 * 版 权： 青岛盛天科技有限公司
 */
public class DrawTools
{
	Context context;

	/***
	 * 绘图辅助类
	 * 
	 * @param context 上下文
	 */
	public DrawTools( Context context )
	{
		this.context = context;
	}

	/***
	 * 
	 * 功 能： 高斯模糊
	 * 
	 * @param bkg 背景图
	 * @param view 控件对象
	 * @param radius 模糊半径
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void blur(Bitmap bkg, View view, int width, int height, float radius)
	{
		Bitmap overlay = Bitmap.createBitmap(width-1, height-1, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		int x = -view.getLeft()+50;
		int y = -view.getTop()+50;
		canvas.drawBitmap(bkg, x, y, null);
		RenderScript rs = RenderScript.create(context);
		Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
		//ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
		//blur.setInput(overlayAlloc);
		
//		blur.setRadius(radius);
//		blur.forEach(overlayAlloc);
//		overlayAlloc.copyTo(overlay);
//		view.setBackground(new BitmapDrawable(context.getResources(), overlay));
//		rs.destroy();
	}

	/**
	 * Drawable 转 bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable)
	{
		if(drawable instanceof BitmapDrawable)
		{
			return ((BitmapDrawable) drawable).getBitmap();
		}
		else if(drawable instanceof NinePatchDrawable)
		{
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		}
		else
		{
			return null;
		}
	}
}