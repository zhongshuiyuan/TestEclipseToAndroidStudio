package com.qingdao.shiqu.arcgis.activity;

import com.esri.core.internal.io.handler.l;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

public class Main02 extends Activity {
	
	private static int miscount = 0;
	private SQLiteDatabase mSQLiteDatabase = null;
	private final static String DATABASE_NAME = "Example_06_24.db";
	private final static String TABLE_NAME = "table";
	
	private final static String TABLE_ID ="id";
	private final static String TABLE_NUM="num";
	private final static String TABLE_DATA ="data";
	
	private final static String CREATE_TABLE ="create table "+TABLE_NAME
			+"("+TABLE_ID+" integer primary key,"+TABLE_NUM+" integer "+
			TABLE_DATA+" text)";
	LinearLayout m_LineLayout = null;
	ListView m_ListView = null;
	Button bi = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		m_LineLayout = new LinearLayout(this);
		m_LineLayout.setOrientation(LinearLayout.VERTICAL);
		m_LineLayout.setBackgroundColor(android.graphics.Color.BLACK);
		m_ListView = new ListView(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		m_ListView.setBackgroundColor(android.graphics.Color.BLACK);
		m_LineLayout.addView(m_ListView,params);
		bi = new Button(this);
		bi.setText("add");
		m_LineLayout.addView(bi);
		bi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				addData();
			}
		});
		setContentView(m_LineLayout);
		mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
		try {
			mSQLiteDatabase.execSQL(CREATE_TABLE);
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			updateAdapter();
		}
	}
	private void updateAdapter(){
		Cursor cur = mSQLiteDatabase.query(TABLE_NAME, new String[]{TABLE_ID,TABLE_NUM,TABLE_DATA}, null, null, null, null, null);
		miscount = cur.getCount();
		if(cur!=null && cur.getCount()>=0){
			ListAdapter listadapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cur, new String[]{TABLE_NUM,TABLE_DATA}, new int[]{android.R.id.text1,android.R.id.text2});
			m_ListView.setAdapter(listadapter);
		}
	}
	private void DeleteDataBase(){
		this.deleteDatabase(DATABASE_NAME);
		this.finish();
	}
	private void DropTable(){
		mSQLiteDatabase.execSQL("drop table "+TABLE_NAME);
		this.finish();
	}
	private void addData(){
		ContentValues cv = new ContentValues();
		cv.put(TABLE_NUM, miscount);
		cv.put(TABLE_DATA, "test value"+miscount);
		mSQLiteDatabase.insert(TABLE_NAME, null, cv);
		miscount++;
		updateAdapter();
	}
}
