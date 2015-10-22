package com.qingdao.shiqu.arcgis.activity;


import java.util.ArrayList;
import java.util.List;

import com.qingdao.shiqu.arcgis.R;

import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class GuanglanActivity extends Activity {

	private Button qd,qx,more;
	private Spinner gl;
	private List<String> list = new ArrayList<String>();
	private ArrayAdapter<String> adpter ;
	static String gtype;
	private SQLiteDatabase db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		db = new SQLiteDatabase(this);
		addGLType();
		setContentView(R.layout.selectguanglan);
		qd = (Button)findViewById(R.id.qd);
		qx = (Button)findViewById(R.id.qx);
		gl = (Spinner)findViewById(R.id.gl);
		more = (Button)findViewById(R.id.more);
		adpter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		gl.setAdapter(adpter);
		gl.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){    
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {    
				// TODO Auto-generated method stub    
				String ab = list.get(arg2);
				gtype = ab;
			}    
			public void onNothingSelected(AdapterView<?> arg0) {    
				// TODO Auto-generated method stub    
				gtype = list.get(0);
			}    
		});
		qd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent intent = new Intent();
				intent.putExtra("gltype", gtype);
				setResult(Activity.RESULT_OK, intent);
				finish();// 结束之后会将结果传回From
			}
		});
		qx.setOnClickListener(null);
		more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent inte = new Intent(GuanglanActivity.this, GDEditActivity.class);
				startActivity(inte);
			}
		});
	}
	
	public void addGLType(){
		DataTable result = db.executeTable("aqBS_GLTypeQueryAll", null);
		for(int i=0;i<result.size();i++){
			DataCollection dc = result.next();
			list.add(dc.get("gltypename").Value.toString());
		}
		/*list.add("1号光缆");
		list.add("2号光缆");*/
	}

}
