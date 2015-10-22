package com.qingdao.shiqu.arcgis.activity;

import java.util.ArrayList;
import java.util.List;

import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.esri.android.map.FeatureLayer;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.SearchAdapter;
import com.qingdao.shiqu.arcgis.mode.Road;
import com.qingdao.shiqu.arcgis.utils.DBOpterate;

/**
 * ************************************
 * 
 * 作 者： 潘跃瑞
 * 
 * 功 能： 搜索功能（道路、单元、光机、井）
 * 
 * 时 间： 2014-10-30
 * 
 * 版 权： 青岛盛天科技有限公司
 * 
 *************************************** 
 */
public class Search extends Activity
{
	List<Road> searchRoad;
	ListView listView = null;
	SearchAdapter adapterHistory = null;
	Button btn = null;
	EditText where = null;
	EditText mph,lh,dyh;
	RadioGroup radioGroup;
	RadioButton daoLu, danYuan, guangJi, jing;
	String type = "道路";
	jsqlite.Database db;
	boolean is = true;//是否
	
	DBOpterate dbOpterate;
	List<String> functions = new ArrayList<String>();
	LinearLayout linearDanYuan;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				int id = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) Search.this.findViewById(id);
				type = rb.getText().toString();
				if("单元".equals(type))
				{
					where.setHint("请输入道路名称");
					linearDanYuan.setVisibility(View.VISIBLE);
				}else {
					linearDanYuan.setVisibility(View.GONE);
				}
				if("道路".equals(type))
				{
					where.setHint("请输入道路名称");
				}
				if("光机".equals(type))
				{
					where.setHint("请输入光机编号");
				}
				if("井".equals(type))
				{
					where.setHint("请输入井名称");
				}
				//Toast.makeText(Search.this, type+"", Toast.LENGTH_SHORT).show();
			}
		});
		listView = (ListView) findViewById(R.id.listview);
		daoLu = (RadioButton)  findViewById(R.id.daoLu);
		danYuan = (RadioButton)  findViewById(R.id.danYuan);
		guangJi = (RadioButton)  findViewById(R.id.guangJi);
		jing = (RadioButton)  findViewById(R.id.jing);
		where = (EditText) findViewById(R.id.where);
		lh = (EditText) findViewById(R.id.lh);
		mph = (EditText) findViewById(R.id.mph);
		dyh = (EditText) findViewById(R.id.dyh);
		SearchChangeLister changeLister = new SearchChangeLister();
		where.addTextChangedListener(changeLister);
//		lh.addTextChangedListener(changeLister);
//		mph.addTextChangedListener(changeLister);
//		dyh.addTextChangedListener(changeLister);
		adapterHistory = new SearchAdapter(this);
		linearDanYuan = (LinearLayout) findViewById(R.id.linearDanYuan);
		listView.setAdapter(adapterHistory);
		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) // 获取到坐标回到主页面进行绘画
			{
				if(is)
				{
					Intent intent = new Intent();
					intent.putExtra("value", searchRoad.get(arg2).getPoint());
					intent.putExtra("type", type);
					setResult(Activity.RESULT_OK, intent);
					finish();// 结束之后会将结果传回From
				}else 
				{
					where.setText(searchRoad.get(arg2).getName());
					listView.setVisibility(View.GONE);
					adapterHistory.setData(null);
					adapterHistory.notifyDataSetChanged();
				}
			}
		});
		/**
		 * 初始化
		 */
		dbOpterate = DBOpterate.getDbOpterate(this);
		super.onCreateView();
	}
    public void click(View view)
    {
    	switch (view.getId()) {
		case R.id.back:
			this.finish();
			break;
		case R.id.btnSearch:
			String name = where.getText().toString();
			if("".equals(name))
			{
				if("单元".equals(type))
				{
					Toast.makeText(this, "道路名称不能为空!", Toast.LENGTH_SHORT).show();
				} else if("道路".equals(type))
				{
					Toast.makeText(this, "道路名称不能为空!", Toast.LENGTH_SHORT).show();
				} else if("光机".equals(type))
				{
					Toast.makeText(this, "光机编号不能为空!", Toast.LENGTH_SHORT).show();
				} else if("井".equals(type))
				{
					Toast.makeText(this, "井名称不能为空!", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			if("单元".equals(type))
			{
				searchRoad = dbOpterate.searchDanYuan(name, mph.getText().toString(), lh.getText().toString(), dyh.getText().toString());
			}else {
				searchRoad = dbOpterate.search(name, type);
			}
			is = true;
			listView.setVisibility(View.VISIBLE);
			adapterHistory.setData(searchRoad);
			adapterHistory.notifyDataSetChanged();
			break;
		default:
			break;
		}
    }
	@Override
	protected void onResume()
	{
		super.onResume();
		functions = Activity.getFunction();
		// 是否显示单元定位查询
		if (functions.contains("000001"))
		{
			danYuan.setVisibility(View.VISIBLE);
		}
		else
		{
			danYuan.setVisibility(View.GONE);
		}
		// 是否显示道路定位查询
		if (functions.contains("000002"))
		{
			daoLu.setVisibility(View.VISIBLE);
		}
		else
		{
			daoLu.setVisibility(View.GONE);
		}
		// 是否显示光机定位查询
		if (functions.contains("000003"))
		{
			guangJi.setVisibility(View.VISIBLE);
		}
		else
		{
			guangJi.setVisibility(View.GONE);
		}
		// 是否显示井定位查询
		if (functions.contains("000004"))
		{
			jing.setVisibility(View.VISIBLE);
		}
		else
		{
			jing.setVisibility(View.GONE);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}

	private class SearchChangeLister implements TextWatcher
	{
		@Override
		public void afterTextChanged(Editable s)
		{

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			if ("".equals(s + ""))
			{
//				if("单元".equals(type))
//				{
//					if(!"".equals(where.getText().toString()) || !"".equals(where.getText().toString()) || !"".equals(where.getText().toString()) || !"".equals(where.getText().toString()))
//					{
//						return;
//					}
//				}
				listView.setVisibility(View.GONE);
				adapterHistory.setData(null);
				adapterHistory.notifyDataSetChanged();
			}
			else
			{
				if("单元".equals(type))
				{
					searchRoad = dbOpterate.searchDaoLu(where.getText().toString());
					listView.setVisibility(View.VISIBLE);
					adapterHistory.setData(searchRoad);
					adapterHistory.notifyDataSetChanged();
					is = false;
				}
				
			}
//			else
//			{
//				if("单元".equals(type))
//				{
//					searchRoad = dbOpterate.searchDanYuan(where.getText().toString(), mph.getText().toString(), lh.getText().toString(), dyh.getText().toString());
//				}else {
//					searchRoad = dbOpterate.search(s + "", type);
//				}
//				listView.setVisibility(View.VISIBLE);
//				adapterHistory.setData(searchRoad);
//				adapterHistory.notifyDataSetChanged();
//			}
		}

	}

}
