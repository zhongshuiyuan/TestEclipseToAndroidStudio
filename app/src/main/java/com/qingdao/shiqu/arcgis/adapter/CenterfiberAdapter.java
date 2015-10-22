package com.qingdao.shiqu.arcgis.adapter;

import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CenterfiberAdapter extends BaseAdapter {

	private Context context;
	private String[] i;
	
	public CenterfiberAdapter(Context context, String[] i) {
		super();
		this.context = context;
		this.i = i;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return i.length;
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
	public View getView(int index, View view, ViewGroup arg2) {
		// TODO 自动生成的方法存根
		HoldView hold;
		if(view == null){
			hold = new HoldView();
			view = LayoutInflater.from(context).inflate(R.layout.cneterfiberadapter, null);
			view.setTag(hold);
		}else
			hold = (HoldView)view.getTag();
		hold.text = (TextView)view.findViewById(R.id.textcen);
		hold.text.setText(String.valueOf(index+1));
		return view;
	}
	static class HoldView{
		TextView text;
	}

}
