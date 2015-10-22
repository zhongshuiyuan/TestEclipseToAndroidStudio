package com.qingdao.shiqu.arcgis.activity;

import Eruntech.BirthStone.Base.Forms.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.qingdao.shiqu.arcgis.R;

public class Options extends Activity
{
	ProgressDialog dialog;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		onCreateView(); 
	}
	
	public void onCreateView()
	{
		super.onCreateView();
	}
	
}
