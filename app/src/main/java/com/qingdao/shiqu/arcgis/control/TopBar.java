package com.qingdao.shiqu.arcgis.control;

import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * topbar 自定义控件
 * 
 */
public class TopBar extends RelativeLayout
{
	private OnTopBarClickListener topBarLeftOnClickListener = null, topBarRightOnClickListener = null;
	private ImageButton leftButton, rightButton;
	private Button leftButtonText, rightButtonText;
	private TextView title = null;
	private TypedArray typedArray = null;

	public TopBar( Context context )
	{
		super(context);
	}

	public TopBar( Context context, AttributeSet attrs )
	{
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.topbar, this, true);
		typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
		init();
	}

	private void init()
	{

		leftButton = (ImageButton) findViewById(R.id.LeftBtn);
		title = (TextView) findViewById(R.id.Title);
		leftButton.setVisibility(Button.GONE);
		leftButtonText = (Button) findViewById(R.id.LeftBtnText);
		leftButtonText.setVisibility(Button.GONE);
		rightButton = (ImageButton) findViewById(R.id.RightBtn);
		rightButton.setVisibility(Button.GONE);
		rightButtonText = (Button) findViewById(R.id.RightBtnText);
		rightButtonText.setVisibility(Button.GONE);
		String titleText = typedArray.getString(R.styleable.TopBar_title_text);
		if(titleText != null)
		{
			title.setText(titleText);
		}
		leftButton.setOnClickListener(leftOnClickListener);
		leftButtonText.setOnClickListener(leftOnClickListener);
		rightButton.setOnClickListener(rightOnClickListener);
		rightButtonText.setOnClickListener(rightOnClickListener);
	}

	OnClickListener leftOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			if(topBarLeftOnClickListener != null)
			{
				topBarLeftOnClickListener.OnClick();
			}
		}
	};

	OnClickListener rightOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(topBarRightOnClickListener != null)
			{
				topBarRightOnClickListener.OnClick();
			}
		}
	};

	/**
	 * 设置topBar左侧图片按钮是否显示，图片按钮显示则文本按钮隐藏
	 * 
	 * @param bool
	 */
	public void setLeftImageButtonVisibility(boolean bool)
	{
		if(bool)
		{
			leftButtonText.setVisibility(View.GONE);
			leftButton.setVisibility(Button.VISIBLE);
		}
		else
		{
			leftButton.setVisibility(Button.GONE);
			leftButtonText.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置topBar左侧文本按钮是否显示，文本按钮显示则图片按钮隐藏
	 * 
	 * @param bool
	 */
	public void setLeftTextButtonVisibility(boolean bool)
	{
		if(bool)
		{
			leftButton.setVisibility(View.GONE);
			leftButtonText.setVisibility(Button.VISIBLE);
		}
		else
		{
			leftButton.setVisibility(Button.VISIBLE);
			leftButtonText.setVisibility(Button.GONE);
		}
	}

	/**
	 * 设置topBar右侧图片按钮是否显示，图片按钮显示则文本按钮隐藏
	 * 
	 * @param bool
	 */
	public void setRightImageButtonVisibility(boolean bool)
	{
		if(bool)
		{
			rightButtonText.setVisibility(View.GONE);
			rightButton.setVisibility(Button.VISIBLE);
		}
		else
		{
			rightButtonText.setVisibility(View.VISIBLE);
			rightButton.setVisibility(Button.GONE);
		}
	}

	/**
	 * 设置topBar右侧文本按钮是否显示，文本按钮显示则图片按钮隐藏
	 * 
	 * @param bool
	 */
	public void setRightTextButtonVisibility(boolean bool)
	{
		if(bool)
		{
			rightButton.setVisibility(View.GONE);
			rightButtonText.setVisibility(Button.VISIBLE);
		}
		else
		{
			rightButton.setVisibility(View.VISIBLE);
			rightButtonText.setVisibility(Button.GONE);
		}
	}

	/**
	 * 设置左侧按钮文本
	 * @param text 显示的文本
	 */
	public void setLeftButtonText(String text)
	{
		if(text != null)
		{
			leftButtonText.setText(text);
		}
	}

	/**
	 * 设置右侧按钮文本
	 * @param text 显示的文本
	 */
	public void setRightButtonText(String text)
	{
		if(text != null)
		{
			rightButtonText.setText(text);
		}
	}

	/**
	 * 设置工具栏标题
	 * @param text 显示的标题
	 */
	public void setTitleText(String text)
	{
		if(text != null) title.setText(text);
	}

	/**
	 * 设置右侧按钮激发事件
	 * @param topBarRightOnClickListener
	 */
	public void setRightButtonOnClickListener(OnTopBarClickListener topBarRightOnClickListener)
	{
		this.topBarRightOnClickListener = topBarRightOnClickListener;
	}

	/**
	 * 设置左侧按钮激发事件
	 * @param topBarLeftOnClickListener
	 */
	public void setLeftButtonOnClickListener(OnTopBarClickListener topBarLeftOnClickListener)
	{
		this.topBarLeftOnClickListener = topBarLeftOnClickListener;
	}

	public void setLeftButtonBg(int bg)
	{
		leftButton.setImageResource(bg);
		// leftButton.setBackgroundResource(bg);
	}

	public void setRightButtonBg(int bg)
	{
		rightButton.setImageResource(bg);
	}

}
