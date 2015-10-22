package com.qingdao.shiqu.arcgis.adapter;

import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class UpdateDataAdapter extends BaseAdapter {
	Context context;
	String tname;
	String tid;
	String ename;
	String eid;
	

	public UpdateDataAdapter(Context context, String tname, String tid,
			String ename, String eid) {
		super();
		this.context = context;
		this.tname = tname;
		this.tid = tid;
		this.ename = ename;
		this.eid = eid;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return 1;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO 自动生成的方法存根
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO 自动生成的方法存根
		return arg0;
	}

	@Override
	public View getView(int index, View view, ViewGroup parent) {
		// TODO 自动生成的方法存根
		ViewHold hold;
		TextView tname = null,tid = null;
		EditText ename = null,eid = null;
		if(view == null){
			hold = new ViewHold();
			view = LayoutInflater.from(context).inflate(R.layout.updatedatadapter, null);
			view.setTag(hold);
		}else
			hold = (ViewHold)view.getTag();
		hold.tname = (TextView) view.findViewById(R.id.tname);
		hold.tid = (TextView)view.findViewById(R.id.tid);
		hold.ename = (EditText)view.findViewById(R.id.ename);
		hold.eid = (EditText)view.findViewById(R.id.eid);
		
		hold.tname.setText(this.tname);
		hold.tid.setText(this.tid);
		hold.ename.setText(this.ename);
		hold.eid.setText(this.eid);
		return view;
	}
	static class ViewHold{
		TextView tname;
		TextView tid;
		EditText ename;
		EditText eid;
	}

}
