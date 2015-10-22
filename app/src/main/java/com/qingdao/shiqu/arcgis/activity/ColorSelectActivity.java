package com.qingdao.shiqu.arcgis.activity;


import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.control.ColorPickerView;
import com.qingdao.shiqu.arcgis.control.ColorPickerView.onColorChangedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ColorSelectActivity extends Activity {

	private String selectedcolor;
	private Button rst;
	private String cred="",cblue="",cgreen="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_colorselect);
		rst = (Button) findViewById(R.id.ok);
		ColorPickerView colorPickerView = (ColorPickerView) findViewById(R.id.cpv);
		colorPickerView.setColorChangedListener(new onColorChangedListener() {

			@Override
			public void colorChanged(int red, int blue, int green) {
				/*Toast.makeText(ColorSelectActivity.this, "red="+red+"  blue="+blue+"   green="+green,
						Toast.LENGTH_SHORT).show();*/
				cred = String.valueOf(red);
				cblue = String.valueOf(blue);
				cgreen = String.valueOf(green);
			}
		});
		rst.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				if(cred.equals("") && cgreen.equals("") && cblue.equals(""))
					Toast.makeText(ColorSelectActivity.this, "白色不可用，请重新选择！", Toast.LENGTH_LONG).show();
				else{
					selectedcolor = cred+","+cgreen+","+cblue;
					backresult();
				}
			}
		});
	}
	private void backresult(){
		Intent intent = new Intent();
		intent.putExtra("color", selectedcolor);
		setResult(Activity.RESULT_OK, intent);
		finish();// 结束之后会将结果传回From
	}

}
