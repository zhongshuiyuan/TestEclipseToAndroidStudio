package com.qingdao.shiqu.arcgis.activity;

import Eruntech.BirthStone.Base.Form.Event.OnClickedListener;
import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.UI.Controls.ActionButton.ButtonOpen;
import android.os.Bundle;
import android.view.View;

import com.qingdao.shiqu.arcgis.R;
/**
 * 跳转设置用户、角色界面
 * @author MinG
 *
 */
public class Dialog_Authority extends Activity
{
	/*
	 * 控件声明
	 * */
	ButtonOpen btnRole, btnUser;
	
	   @Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_authority);
		btnRole = (ButtonOpen) findViewById(R.id.btnRole);
		btnRole.setOnClickedListener(btnOpenOnClickedListener);
		btnUser = (ButtonOpen) findViewById(R.id.btnUser);
		btnUser.setOnClickedListener(btnOpenOnClickedListener);
		onCreateView();
	}
	   
	   /**
	    * 点击后关闭当前窗口
	    */
	   OnClickedListener btnOpenOnClickedListener = new OnClickedListener()
	   {

		@Override
		public void onClicked()
		{
			overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
			Dialog_Authority.this.finish();
		}
		   
	   };
	
	public void click(View view)
	{
		this.finish();
	}
}
