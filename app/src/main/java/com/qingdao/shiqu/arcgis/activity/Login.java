package com.qingdao.shiqu.arcgis.activity;

import java.io.IOException;

import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.Base.Forms.Helper.FormHelper;
import Eruntech.BirthStone.Base.Forms.Parse.CollectForm;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import Eruntech.BirthStone.UI.Controls.TextBox;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qingdao.shiqu.arcgis.ActivityHelper;
import com.qingdao.shiqu.arcgis.AssetHelper;
//import com.gc.materialdesign.views.Button;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.helper.FunctionHelper;
import com.qingdao.shiqu.arcgis.utils.DBOpterate;
import com.qingdao.shiqu.arcgis.utils.Initalize;

public class Login extends Activity
{
	SQLiteDatabase db = null;
	LinearLayout linearLayout;
	Button btnLogin;
	TextBox userNo, password;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		onCreateView();
	}

	public void onCreateView()
	{
		linearLayout = (LinearLayout) findViewById(R.id.linearLayout01);
		userNo = (TextBox) findViewById(R.id.userNo);
		password = (TextBox) findViewById(R.id.password);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(btnLoginOnClickListener);
		super.onCreateView();
	}

	/**
	 * 登陆操作
	 */
	OnClickListener btnLoginOnClickListener = new android.view.View.OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			try
			{
				if (db == null)
				{
					db = new SQLiteDatabase(Login.this);
				}
				if (userNo.getText().toString().trim().equals("") || password.getText().toString().trim().equals(""))
				{
					Toast.makeText(Login.this, "用户名或者密码不能为空!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					CollectForm collecter = new CollectForm(Login.this, "ForSave");
					DataTable table = db.executeTable("spBS_UserLogin", collecter.collect());
					if (table.size() > 0)
					{
						// 获取用户权限代码
						FunctionHelper.USER_ROLE = table.getFirst();
						FunctionHelper.userName = userNo.getText().toString();
						FunctionHelper.getFunction(Login.this);
						FormHelper open = new FormHelper();
						open.open(Login.this, "com.qingdao.shiqu.arcgis.activity.Main");
						Login.this.finish();
					}
					else
					{
						Toast.makeText(Login.this, "登录失败，请检查用户名、密码！", Toast.LENGTH_SHORT).show();
					}
				}
			}
			catch (Exception e)
			{
				Toast.makeText(Login.this, "登录错误！", Toast.LENGTH_SHORT).show();
			}
		}
	};

	public void onResume()
	{
		super.onResume();
		setBgAnim();
		Initalize.createDatabase(Login.this);
		DBOpterate.getDbOpterate(Login.this);
	}

	/**
	 * 动画开始
	 */
	private void setBgAnim()
	{
		Animation animation = AnimationUtils.loadAnimation(Login.this, R.anim.splash_translate);
		linearLayout.setAnimation(animation);
		animation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{

			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				linearLayout.setVisibility(View.VISIBLE);
			}
		});

	}

}
