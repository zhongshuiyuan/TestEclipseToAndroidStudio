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

public class BS_RoleDialog_Add extends Activity
{
	/*
	 * 控件声明
	 * */
	TextView roleID;
	TextBox roleName;

	/* 
	 *类声明 
	 */
	final UUID uuid = UUID.randomUUID();
	Boolean reName = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bs_roledialog_add);
		roleID = (TextView) findViewById(R.id.RoleID);
		roleName = (TextBox) findViewById(R.id.RoleName);
		onCreateView();
		Bundle extras = getIntent().getExtras();
		reName = extras.getBoolean("reName");
		if(reName)
		{
			roleID.setText(extras.getString("roleID"));
			roleName.setText(extras.getString("roleName"));
		}
	}

	public void click(View view)
	{
		if(!roleName.getText().toString().trim().equals(""))
		{
			Intent intent = new Intent();
			if(reName)
			{
				intent.putExtra("isAdd", false);
				intent.putExtra("roleID", roleID.getText().toString());
			}
			else
			{
				intent.putExtra("isAdd", true);
				intent.putExtra("roleID", UUID.randomUUID().toString());
			}
			intent.putExtra("roleName", roleName.getText().toString());
			setResult(Activity.RESULT_OK, intent);
			this.finish();
		}
		else
		{
			Toast.makeText(this, "角色不能为空！", Toast.LENGTH_LONG).show();
		}
	}
}
