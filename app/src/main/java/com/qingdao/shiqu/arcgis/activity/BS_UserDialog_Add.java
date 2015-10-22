package com.qingdao.shiqu.arcgis.activity;

import java.util.UUID;

import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.UI.Controls.TextBox;
import Eruntech.BirthStone.UI.Controls.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.qingdao.shiqu.arcgis.R;

public class BS_UserDialog_Add extends Activity
{
	/*
	 * 控件声明
	 * */
	TextView userID;
	TextBox userNo, password;

	/*
	 *类声明 
	 */
	final UUID uuid = UUID.randomUUID();
	Boolean reName = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bs_userdialog_add);
		userID = (TextView) findViewById(R.id.UserID);
		userNo = (TextBox) findViewById(R.id.UserNo);
		password = (TextBox) findViewById(R.id.Password);
		onCreateView();
		Bundle extras = getIntent().getExtras();
		reName = extras.getBoolean("reName");
		if(reName)
		{
			userID.setText(extras.getString("userID"));
			userNo.setText(extras.getString("userNo"));
			password.setText(extras.getString("password"));
		}
	}

	public void click(View view)
	{
		if(!userNo.getText().toString().trim().equals("")&&!password.getText().toString().trim().equals(""))
		{
			Intent intent = new Intent();
			//如果重命名
			if(reName)
			{
				intent.putExtra("isAdd", false);
				intent.putExtra("userID", userID.getText().toString());
			}
			else
			{
				intent.putExtra("isAdd", true);
				intent.putExtra("userID", UUID.randomUUID().toString());
			}
			intent.putExtra("userNo", userNo.getText().toString());
			intent.putExtra("password", password.getText().toString());
			setResult(Activity.RESULT_OK, intent);
			this.finish();
		}
		else
		{
			Toast.makeText(this, "工号、密码不能为空！", Toast.LENGTH_LONG).show();
		}
	}
}
