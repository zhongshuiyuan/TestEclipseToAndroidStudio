package com.qingdao.shiqu.arcgis.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImage extends ImageView
{
	private Context context;
    private int defaultWidth = 0;  
    private int defaultHeight = 0;  
	public RoundImage(Context context)
	{
		super(context);
		this.context = context;
	}
	public RoundImage(Context context,AttributeSet attributeSet)
	{
		super(context, attributeSet);
		this.context = context;
	}
	public RoundImage(Context context,AttributeSet attributeSet,int defStyle)
	{
		super(context, attributeSet, defStyle);
		this.context = context;
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		Drawable drawable = getDrawable();  
        if (drawable == null) {  
            return;  
        }  
  
        if (getWidth() == 0 || getHeight() == 0) {  
            return;  
        }  
        this.measure(0, 0);  
//        if (drawable.getClass() == NinePatchDrawable.class)  
//            return;  
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();  
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);  
        if (defaultWidth == 0) {  
            defaultWidth = getWidth();  
  
        }  
        if (defaultHeight == 0) {  
            defaultHeight = getHeight();  
        }  
        int radius = 0; 
        radius = (defaultWidth < defaultHeight ? defaultWidth  
                : defaultHeight) / 2; 
        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);  
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight  
                / 2 - radius, null);  
	}
    /** 
     * 锟斤拷取锟矫硷拷锟斤拷锟皆诧拷锟酵计?
     *  
     * @param radius 
     *            锟诫径 
     */  
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {  
        Bitmap scaledSrcBmp;  
        int diameter = radius * 2;  
  
        // 为锟剿凤拷止锟斤拷卟锟斤拷锟饺ｏ拷锟斤拷锟皆诧拷锟酵计拷锟斤拷危锟斤拷锟剿斤拷取锟斤拷锟斤拷锟斤拷锟叫达拷锟斤拷锟叫硷拷位锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷图片  
        int bmpWidth = bmp.getWidth();  
        int bmpHeight = bmp.getHeight();  
        int squareWidth = 0, squareHeight = 0;  
        int x = 0, y = 0;  
        Bitmap squareBitmap;  
        if (bmpHeight > bmpWidth) {// 锟竭达拷锟节匡拷  
            squareWidth = squareHeight = bmpWidth;  
            x = 0;  
            y = (bmpHeight - bmpWidth) / 2;  
            // 锟斤拷取锟斤拷锟斤拷图片  
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,  
                    squareHeight);  
        } else if (bmpHeight < bmpWidth) {// 锟斤拷锟斤拷诟锟? 
            squareWidth = squareHeight = bmpHeight;  
            x = (bmpWidth - bmpHeight) / 2;  
            y = 0;  
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,  
                    squareHeight);  
        } else {  
            squareBitmap = bmp;  
        }  
  
        if (squareBitmap.getWidth() != diameter  
                || squareBitmap.getHeight() != diameter) {  
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,  
                    diameter, true);  
  
        } else {  
            scaledSrcBmp = squareBitmap;  
        }  
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),  
                scaledSrcBmp.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
  
        Paint paint = new Paint();  
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),  
                scaledSrcBmp.getHeight());  
  
        paint.setAntiAlias(true);  
        paint.setFilterBitmap(true);  
        paint.setDither(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,  
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,  
                paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);  
        // bitmap锟斤拷锟斤拷(recycle锟斤拷锟斤拷锟节诧拷锟斤拷锟侥硷拷XML锟斤拷锟斤拷锟斤拷效锟斤拷)  
        // bmp.recycle();  
        // squareBitmap.recycle();  
        // scaledSrcBmp.recycle();  
        bmp = null;  
        squareBitmap = null;  
        scaledSrcBmp = null;  
        return output;  
    }  
}
