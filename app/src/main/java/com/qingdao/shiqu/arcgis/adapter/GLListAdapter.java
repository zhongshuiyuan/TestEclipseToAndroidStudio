package com.qingdao.shiqu.arcgis.adapter;

import java.util.ArrayList;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.GJPictureAdapter.ViewHold;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GLListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> al;
	
	public GLListAdapter(Context context, ArrayList<String> al) {
		super();
		this.context = context;
		this.al = al;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return this.al.size();
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
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO 自动生成的方法存根
		ViewHold hold = null ;
		if(hold == null){
			hold = new ViewHold();
			view = LayoutInflater.from(context).inflate(R.layout.item, null);
			view.setTag(hold);
		}else
			hold = (ViewHold)view.getTag();
		hold.tv = (TextView)view.findViewById(R.id.text2);
		hold.tv.setText(al.get(arg0));
		return view;
	}
	static class ViewHold{
		public TextView tv;
	}

}
