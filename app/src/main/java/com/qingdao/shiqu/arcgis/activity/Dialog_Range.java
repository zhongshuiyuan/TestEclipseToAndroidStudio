package com.qingdao.shiqu.arcgis.activity;

import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.Core.Helper.File;
import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.Dialog_RangeListAdapter;
import com.qingdao.shiqu.arcgis.utils.OpenFiles;

/**
 * 点击光机范围图层展示工程附图文件列表
 *
 * @author MinG
 */
public class Dialog_Range extends Activity
{
	/*
	 * 控件声明
	 */
	ListView listView;

	SQLiteDatabase db = null;
	String tt_id;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_range);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(listViewOnItemClickListener);
		Bundle extras = getIntent().getExtras();
		tt_id = extras.getString("tt_id");
		onCreateView();
		bindListView();
	}

	/**
	 * 单击展示工程附图
	 */
	OnItemClickListener listViewOnItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			try
			{
				TextView att_name = (TextView) arg1.findViewById(R.id.att_name);
				String path = File.getSDCardPath() + "/attach/"+att_name.getText().toString().trim();
				if(File.exists(path))
				{
					OpenFiles.openFile(new java.io.File(path), Dialog_Range.this);
				}
				else
				{
					Toast.makeText(Dialog_Range.this, "无效文件，附图查看失败！", Toast.LENGTH_SHORT).show();
				}
			}
			catch (Exception ex)
			{
				Toast.makeText(Dialog_Range.this, "绑定失败，原因：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	};

	/***
	 * 绑定工程附图
	 */
	private void bindListView()
	{
		try
		{
			db = new SQLiteDatabase(Dialog_Range.this);
			DataCollection params = new DataCollection();
			params.add(new Data("tt_id", tt_id));
			DataTable table = db.executeTable("spOptical_Range_AttList", params, "GBK");
			if(table == null || table.size() < 1)
			{
				this.finish();
			}
			Dialog_RangeListAdapter adapter = new Dialog_RangeListAdapter(this);
			adapter.setData(table);
			listView.setAdapter(adapter);
		}
		catch (Exception ex)
		{
			Toast.makeText(this, "绑定失败，原因：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public void click(View view)
	{
		this.finish();
	}
}
