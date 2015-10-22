package com.qingdao.shiqu.arcgis.activity;

import Eruntech.BirthStone.Base.Forms.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.utils.Initalize;

/**
 * 
 * 闪屏界面业务处理
 *
 */
public class Splash extends Activity
{
	/** 初始化控件 **/
	private RelativeLayout bgImage = null; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
//    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 强制为横屏
    	setContentView(R.layout.splash);
        init();
    }
    /**
     * 初始化方法
     */
	private void init() 
	{
		bgImage = (RelativeLayout) findViewById(R.id.linearLayout1);
		Initalize.createDatabase(Splash.this);
		setBgAnim();
	}
	
	/**
	 * 设置背景渐变动画效果
	 */
	private void setBgAnim()
	{
		Animation animation = AnimationUtils.loadAnimation(Splash.this, R.anim.splash_alpha);
		bgImage.setAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
					Intent intent = new Intent(Splash.this,Main.class);
					startActivity(intent);
					Splash.this.finish();			
			}
		});
		
	}
	
	
}
