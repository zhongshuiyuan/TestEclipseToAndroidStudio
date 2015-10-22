package com.qingdao.shiqu.arcgis.activity;


import java.util.ArrayList;
import java.util.List;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.GDColorAdapter;
import com.qingdao.shiqu.arcgis.sqlite.DoAction;

import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract.Colors;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GDEditActivity extends Activity {
	private Button add , sall;
	private ListView list;
	private EditText gdtype,gdcx;
	private SQLiteDatabase db;
	public String MID; 
	private String itemName;
	private ImageView coloriv;
	private String select_Color = "255,0,0";
	private int requestCode = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gledit_activity);
		add = (Button)findViewById(R.id.add);
		sall = (Button)findViewById(R.id.seeall);
		list = (ListView)findViewById(R.id.list);
		gdtype = (EditText)findViewById(R.id.gltype);
		gdcx = (EditText)findViewById(R.id.gdcx);
		coloriv = (ImageView)findViewById(R.id.gdcolor);
		db = new SQLiteDatabase(this);
		setClickListner();
		setItemLongPress();
		showData();
	}
	/**
	 * 说	 明：设置控件的相应
	 */
	public void setClickListner(){
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				if(add.getText().toString().equals("添加")){
					if(DoAction.IsGDNameExist(GDEditActivity.this, gdtype.getText().toString())){
						Toast.makeText(GDEditActivity.this, "该管道名称已存在，请修改名称！", Toast.LENGTH_SHORT).show();
						return;
					}
					DataCollection prama = new DataCollection();
					prama.add(new Data("guandaotypename", gdtype.getText().toString()));
					prama.add(new Data("gdcolor", select_Color));
					prama.add(new Data("gdcx", gdcx.getText().toString().equals("")==true?"1":gdcx.getText().toString()));
					db.execute("aqBS_GDTypeAdd", prama);
					gdtype.setText("");
					showData();
				}else{
					DataCollection pramas = new DataCollection();
					pramas.add(new Data("id", MID));
					pramas.add(new Data("name", gdtype.getText().toString()));
					pramas.add(new Data("gdcolor", select_Color));
					pramas.add(new Data("gdcx", gdcx.getText().toString().equals("")==true?"1":gdcx.getText().toString()));
					db.execute("aqBS_GDTypeUpdate", pramas);
					showData();
					add.setText("添加");
					gdtype.setText("");
				}
			}
		});
		sall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
//				list.setAdapter(new ArrayAdapter<String>(GDEditActivity.this, R.layout.coloritem/*ndroid.R.layout.simple_expandable_list_item_1*/,getData()));
				showData();
			}
		});
		coloriv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent inte = new Intent(GDEditActivity.this, ColorSelectActivity.class);
				startActivityForResult(inte,requestCode);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自动生成的方法存根
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			select_Color =  data.getStringExtra("color");
			int red,green,blue;
			String[] cls = select_Color.split(",");
			red = Integer.valueOf(cls[0]);
			green = Integer.valueOf(cls[1]);
			blue = Integer.valueOf(cls[2]);
			coloriv.setBackgroundColor(Color.rgb(red, green, blue));
			break;

		default:
			break;
		}
	}
	/**
	 * 说	明：从数据库中获取管道产权信息
	 * @return
	 * 			产权信息
	 */
	private List<String> getData(){

		List<String> data = new ArrayList<String>();
		DataTable result = db.executeTable("aqBS_GDTypeQueryAll", null);
		for(int i=0;i<result.size();i++){
			DataCollection dc = result.next();
			data.add(dc.get("GuandaoTypeID").Value.toString()+":"+dc.get("GuanDaoTypeName").Value.toString());
		}

		return data;
	}
	private List<String> getGDColor(){
		List<String> data = new ArrayList<String>();
		DataTable result = db.executeTable("aqBS_GDTypeQueryAll", null);
		for(int i=0;i<result.size();i++){
			DataCollection dc = result.next();
			data.add(dc.get("GDColor").Value.toString());
		}

		return data;
	}
	/**
	 * 说	明：设置ListVew中Item长按的响应
	 */
	private void setItemLongPress(){
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View view,
					ContextMenuInfo menuInfo) {
				// TODO 自动生成的方法存根
				TextView tv = (TextView)view.findViewById(R.id.gdname/*android.R.id.text1*/);
				itemName = (String) tv.getText();
				menu.add(0,0,0,"编辑");
				menu.add(0,1,0,"删除");
			}
		});
		
	}
	// 长按菜单响应函数 
	@Override
	public boolean onContextItemSelected(MenuItem item) { 

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
				.getMenuInfo(); 
		info.targetView.findViewById(android.R.id.text1);
		String[] ids = itemName.split(":");

		switch (item.getItemId()) { 
		case 0: 
			// 修改操作
			add.setText("修改");
			gdtype.setText(ids[1]);
			//通过管道权限id获取管道权限的颜色和粗细
			DataCollection pram = new DataCollection();
			pram.add(new Data("id", ids[0]));
			DataTable rst = db.executeTable("aqBS_GDTypeQueryByID", pram);
			for(int i=0;i<rst.size();i++){
				DataCollection dc = rst.next();
				String[] cls = dc.get("gdcolor").Value.toString().split(",");
				gdcx.setText(dc.get("gdcx").Value.toString());
				coloriv.setBackgroundColor(Color.rgb(Integer.valueOf(cls[0]), Integer.valueOf(cls[1]), Integer.valueOf(cls[2])));
			}
			MID = ids[0];
			break; 

		case 1: 
			// 删除操作 
			DataCollection pramas1 = new DataCollection();
			pramas1.add(new Data("id", ids[0]));
			db.execute("aqBS_GDTypeDel", pramas1);
			showData();
			break; 

		case 2: 
			// 删除ALL操作 
			break; 

		default: 
			break; 
		} 

		return super.onContextItemSelected(item); 
	} 
	public void showData(){
		GDColorAdapter gca = new GDColorAdapter(GDEditActivity.this, getData(), getGDColor());
//		list.setAdapter(new ArrayAdapter<String>(GDEditActivity.this, android.R.layout.simple_expandable_list_item_1,getData()));
		list.setAdapter(gca);
	}
}
