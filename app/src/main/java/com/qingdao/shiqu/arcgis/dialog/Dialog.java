package com.qingdao.shiqu.arcgis.dialog;

import com.qingdao.shiqu.arcgis.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class Dialog extends Activity
{
	CheckBox checkBox;
	   @Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		
		checkBox = (CheckBox) findViewById(R.id.checkBox);
	}
	@SuppressLint("NewApi") public void click(View view)
	{
		boolean checked = checkBox.isChecked();
		SharedPreferences sharedPreferences = getSharedPreferences("dialog", MODE_PRIVATE);
		Editor edit = sharedPreferences.edit();
		edit.putBoolean("is", checked);
		edit.commit();
		edit.apply();
		switch (view.getId())
		{
			case R.id.btnCancel: //ȡ��
				this.finish();
				break;
			case R.id.btnSure: //ȷ��
				startActivity(new Intent(Settings.ACTION_SETTINGS));
				break;
		}
	}
}
