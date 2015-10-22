package com.qingdao.shiqu.arcgis.adapter;

import java.util.List;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.CheckboxAdapter.ViewHold;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectPropAdatpter extends BaseAdapter {
	Context context;
	List<String> list;
	LayoutInflater inflater = null;
	List<Bitmap> rlist;


	public SelectPropAdatpter(Context context, List<String> list,List<Bitmap> rlist) {
		super();
		this.context = context;
		this.list = list;
		this.rlist = rlist;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO 自动生成的方法存根
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO 自动生成的方法存根
		return arg0;
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	@Override
	public View getView(int i, View view, ViewGroup arg2) {
		// TODO 自动生成的方法存根
		/*if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.selectprop, null);
			TextView tView = (TextView)convertView.findViewById(R.id.proptv);
			ImageView iv = (ImageView)convertView.findViewById(R.id.propig);
			convertView.setTag(tView);
		}
		TextView tView = (TextView)convertView.getTag();
		tView.setText(list.get(i));
		return convertView;*/
		ViewHold hold;
		TextView tv;
		if(view == null){
			hold = new ViewHold();
			view = LayoutInflater.from(context).inflate(R.layout.selectprop, null);
			view.setTag(hold);
		}else
			hold = (ViewHold)view.getTag();
		final int p = i;
		hold.tView = (TextView)view.findViewById(R.id.proptv);
		hold.tView.setText(list.get(i));
		hold.iv = (ImageView)view.findViewById(R.id.propig);
		//		hold.iv.setImageResource(rlist.get(i));
		hold.iv.setBackground(new BitmapDrawable(rlist.get(i)));
		return view;
	}
	static class ViewHold{
		public TextView tView ;
		public ImageView iv ;
	}

}
