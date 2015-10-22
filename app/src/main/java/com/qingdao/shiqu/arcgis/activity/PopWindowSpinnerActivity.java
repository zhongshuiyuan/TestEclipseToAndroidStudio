package com.qingdao.shiqu.arcgis.activity;

import java.util.ArrayList;
import java.util.List;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.ListViewAdapter;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

public class PopWindowSpinnerActivity extends Activity {

	private static int SHOW_GJ = 0;
	private static int SHOW_GD = 1;

	private TextView MyButton;
	private PopupWindow pw;
	private List<String> list ;
	private List<Bitmap> rlist ;
	private ArrayList<String> gdcqList ;
	private SQLiteDatabase db;
	private Button ok,cancel,edit,editgdcq,mySpinner ,gdcq_spinner;
	private SelectPropAdatpter spa ;
	private ArrayAdapter<String> adp;
	private static String jtype,gdcqname;
	int clickPsition = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop_window);
		db = new SQLiteDatabase(PopWindowSpinnerActivity.this);
		ok = (Button)findViewById(R.id.qd);
		cancel = (Button)findViewById(R.id.qx);
		edit = (Button)findViewById(R.id.edit);
		editgdcq = (Button)findViewById(R.id.editcq);
		mySpinner = (Button)findViewById(R.id.degree);
		gdcq_spinner = (Button)findViewById(R.id.degreecq);
		getGDData();
		getGJNameAndPic();
		setListner();
		if(list.size()>0 && gdcqList.size()>0){
			mySpinner.setText(list.get(0));
			gdcq_spinner.setText(gdcqList.get(0));
		}
	}


	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		getGDData();
		getGJNameAndPic();
		super.onResume();
	}


	/**
	 * 说	明：设置控件监听
	 */
	public void setListner(){
		//回传选择信息
		ok.setOnClickListener(new OnClickListener() {

			/* （非 Javadoc）
			 * @see android.view.View.OnClickListener#onClick(android.view.View)*/

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent intent = new Intent();
				jtype = mySpinner.getText().toString();
				gdcqname = gdcq_spinner.getText().toString();
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
				Intent inte = new Intent(PopWindowSpinnerActivity.this,GJEditActivity.class);
				startActivity(inte);
			}
		});
		editgdcq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent inte = new Intent(PopWindowSpinnerActivity.this,GDEditActivity.class);
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
		mySpinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				setPopWindow(spa,SHOW_GJ);
			}
		});
		gdcq_spinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				setPopWindow(adp,SHOW_GD);
			}
		});
	}
	private void setPopWindow(BaseAdapter adp,final int flage){
		View myView = getLayoutInflater().inflate(R.layout.pop, null);
		pw = new PopupWindow(myView,500,500,true);
		pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.diaolog_bg));
		pw.setOutsideTouchable(true);
		pw.setFocusable(true);
		if(flage == 0){
			pw.showAsDropDown(mySpinner);
		}else{
			pw.showAsDropDown(gdcq_spinner);
		}
		ListView lv = (ListView)myView.findViewById(R.id.lv_pop);
		lv.setAdapter(adp);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 自动生成的方法存根
				if(flage == 0)
					mySpinner.setText(list.get(position));
				else
					gdcq_spinner.setText(gdcqList.get(position));
				if (clickPsition != position) {
					clickPsition = position;
				}
				pw.dismiss();
			}
		});
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
		adp = new ArrayAdapter<String>(PopWindowSpinnerActivity.this ,R.layout.item, gdcqList);
	}
}
