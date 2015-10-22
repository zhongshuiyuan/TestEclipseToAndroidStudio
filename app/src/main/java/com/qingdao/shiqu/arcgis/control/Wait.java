package com.qingdao.shiqu.arcgis.control;


import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Wait extends LinearLayout
{
    TextView textView = null;
    TypedArray typedArray = null;
	public Wait(Context context) 
	{
		super(context);
		init(context);
	}

	public Wait(Context context,AttributeSet attr) 
	{
		super(context,attr);
		typedArray = context.obtainStyledAttributes(attr, R.styleable.Wait);
		init(context);
	}
	/**
	 * 初始化
	 */
	private void init(Context context) 
	{
		LayoutInflater.from(context).inflate(R.layout.wait, this, true);
		textView = (TextView) findViewById(R.id.waitText);
		String text = typedArray.getString(R.styleable.Wait_text);
		System.out.println(text+"----------------");
		typedArray.recycle();
		setText(text);
	}
	/**
	 * 设置等待时的文字提示
	 * @param text 文字提示
	 */
    public void setText(String text)
    {
    	if(text != null)
    	textView.setText(text);
    }
}
