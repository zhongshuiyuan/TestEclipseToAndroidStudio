package com.qingdao.shiqu.arcgis.test;

import com.qingdao.shiqu.arcgis.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class InforShowActivity extends Activity {

	private String name,x,y;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infoshow);
		getData();
	}
	private void getData(){
		Intent it = this.getIntent();
		name = it.getStringExtra("name");
		x = it.getStringExtra("x");
		y = it.getStringExtra("y");
	}

}
