package com.qingdao.shiqu.arcgis.adapter;


import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.ContentAdapter.ViewHold;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private Context context;
	private String[] title;
	private String[] text;
	private String[] str;
	private int[] colors = new int[] { 0xff626569, 0xff4f5257 };  

	public MyAdapter(Context context,String[] title,String[] text) {
		this.context = context;
		this.title = title;
		this.text = text;
	}
	public MyAdapter(Context context,String[] str) {
		this.context = context;
		this.str = str;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return title.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int index, View view, ViewGroup parent) {
		// TODO 自动生成的方法存根
		ViewHold hold;
		TextView title = null;
		TextView text = null;
		if(view == null){
			hold = new ViewHold();
			view = LayoutInflater.from(context).inflate(R.layout.myadapter, null);
//			iv = (ImageView)view.findViewById(R.id.baseimg);
//			title = (TextView)view.findViewById(R.id.basetitle);
//			text = (TextView)view.findViewById(R.id.basetext);
			view.setTag(hold);
		}else
			hold=(ViewHold) view.getTag();
		/*int colpos = index % colors.length;
		view.setBackgroundColor(colors[colpos]);*/
		hold.titleView = (TextView)view.findViewById(R.id.basetitle);
		hold.textView = (TextView)view.findViewById(R.id.basetext);
		
		hold.titleView.setText(this.title[index]);
		hold.textView.setText(this.text[index]);
//		iv.setBackgroundResource(R.drawable.ic_launcher);
		return view;
	}

	public String[] getImage() {
		return title;
	}

	public void setImage(String[] image) {
		this.title = image;
	}

	static class ViewHold {
		public TextView titleView;
		public TextView textView;
	}
}
