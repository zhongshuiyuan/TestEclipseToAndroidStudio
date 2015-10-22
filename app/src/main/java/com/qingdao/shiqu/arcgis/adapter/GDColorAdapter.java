package com.qingdao.shiqu.arcgis.adapter;

import java.util.List;

import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GDColorAdapter extends BaseAdapter {
	Context context;
	List<String> names;
	List<String> colors;

	public GDColorAdapter(Context context, List<String> names,
			List<String> colors) {
		super();
		this.context = context;
		this.names = names;
		this.colors = colors;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return names.size();
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
		ViewHold hold;
		if(view == null){
			hold = new ViewHold();
			view = LayoutInflater.from(context).inflate(R.layout.gdcoloradapter, null);
			view.setTag(hold);
		}else
			hold = (ViewHold)view.getTag();
		hold.name = (TextView)view.findViewById(R.id.gdname);
		hold.color = (ImageView)view.findViewById(R.id.gdcoloradp);
		hold.name.setText(names.get(arg0));
		String[] cls = colors.get(arg0).split(",");
		hold.color.setBackgroundColor(Color.rgb(Integer.valueOf(cls[0]), Integer.valueOf(cls[1]), Integer.valueOf(cls[2])));
		return view;
	}
	static class ViewHold{
		public TextView name;
		public ImageView color;
	}

}
