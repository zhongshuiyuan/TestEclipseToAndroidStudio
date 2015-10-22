package com.qingdao.shiqu.arcgis.listener;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TextViewClickListener implements View.OnClickListener {

	TextView text;
	Context context;
	
	public TextViewClickListener(Context context,TextView text) {
		super();
		this.text = text;
		this.context = context;
	}

	@Override
	public void onClick(View arg0) {
		Toast.makeText(context, text.getText(), Toast.LENGTH_LONG).show();
	}

}
