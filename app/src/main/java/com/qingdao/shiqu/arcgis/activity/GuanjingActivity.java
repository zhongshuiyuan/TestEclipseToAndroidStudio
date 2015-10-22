package com.qingdao.shiqu.arcgis.activity;

import java.util.ArrayList;
import java.util.List;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.SelectPropAdatpter;
import com.qingdao.shiqu.arcgis.sqlite.DoAction;

import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * @author 王成达
 *
 */
public class GuanjingActivity extends Activity {

	private Spinner mySpinner ,gdcq_spinner;
	private List<String> list ;
	private List<Bitmap> rlist ;
	private ArrayList<String> gdcqList ;
	private Button ok,cancel,edit,editgdcq;
	private static String jtype,gdcqname;
	private SQLiteDatabase db;
	private ArrayAdapter<String> adp;

	private SelectPropAdatpter spa ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localproperties);
		db = new SQLiteDatabase(GuanjingActivity.this);
		getGDData();
		mySpinner = (Spinner)findViewById(R.id.degree);
		ok = (Button)findViewById(R.id.qd);
		cancel = (Button)findViewById(R.id.qx);
		edit = (Button)findViewById(R.id.edit);
		editgdcq = (Button)findViewById(R.id.editcq);
		gdcq_spinner = (Spinner)findViewById(R.id.degreecq);

		getGJNameAndPic();
		mySpinner.setAdapter(spa/*adapter*/);    
		mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){    
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {    
				// TODO Auto-generated method stub    
				String ab = list.get(arg2);
				jtype = ab;
			}    
			public void onNothingSelected(AdapterView<?> arg0) {    
				// TODO Auto-generated method stub    
				jtype = list.get(0);
			}    
		});    
		gdcq_spinner.setAdapter(adp);
		gdcq_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){    
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {    
				// TODO Auto-generated method stub    
				gdcqname = gdcqList.get(arg2);
			}    
			public void onNothingSelected(AdapterView<?> arg0) {    
				// TODO Auto-generated method stub    
				gdcqname = gdcqList.get(0);
			}    
		});    
		setListner();
	}


	/**
	 * 说	明：设置控件监听
	 */
	public void setListner(){
		//回传选择信息
		ok.setOnClickListener(new OnClickListener() {

			/* （非 Javadoc）
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent intent = new Intent();
				intent.putExtra("jtype", jtype);
				intent.putExtra("gdcq", gdcqname);
				setResult(Activity.RESULT_OK, intent);
				finish();// 结束之后会将结果传回From
			}
		});
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent inte = new Intent(GuanjingActivity.this,GJEditActivity.class);
				startActivity(inte);
			}
		});
		editgdcq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent inte = new Intent(GuanjingActivity.this,GDEditActivity.class);
				startActivity(inte);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				finish();
			}
		});
	}

	/**
	 * 说	明：获取数据库中管道产权信息
	 */
	public void getGDData(){
		gdcqList = new ArrayList<String>();

		DataTable rst = db.executeTable("aqBS_GDTypeQueryAll", null);
		for(int i=0;i<rst.size();i++){
			DataCollection dc = rst.next();
			gdcqList.add(dc.get("GuanDaoTypeName").Value.toString());
		}
		adp = new ArrayAdapter<String>(GuanjingActivity.this ,android.R.layout.simple_dropdown_item_1line, gdcqList);
	}
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		//重新更新管道类型下拉表数据
		getGDData();
		gdcq_spinner.setAdapter(adp);
		//重新更新管井类型下拉表数据
		getGJNameAndPic();
		//		mySpinner.removeAllViews();
		mySpinner.setAdapter(spa);    
		//TODO:更新管井类型下拉表数据
		super.onResume();
	}
	/**
	 * 说	明：获取数据库中管井信息包括图片 
	 */
	public void getGJNameAndPic(){
		list = new ArrayList<String>();
		rlist = new ArrayList<Bitmap>();
		DataTable rst = db.executeTable("aqBS_GJTypeQuery", null);
		Bitmap bmp = null;
		for(int i=0;i<rst.size();i++){
			DataCollection dc = rst.next();
			list.add(dc.get("gjtypename").Value.toString());
			bmp = DoAction.TransPath2Bmp(dc.get("GJTypeImg").Value.toString());
			rlist.add(bmp);
		}
		spa = new SelectPropAdatpter(this, list,rlist);
	}

}
