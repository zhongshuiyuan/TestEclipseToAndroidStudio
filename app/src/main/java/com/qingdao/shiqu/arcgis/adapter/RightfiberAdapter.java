package com.qingdao.shiqu.arcgis.adapter;

import com.qingdao.shiqu.arcgis.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RightfiberAdapter extends BaseAdapter {

	private Context context;
	private String[] name;
	private int[] color;

	public RightfiberAdapter(Context context, String[] name, int[] color) {
		super();
		this.context = context;
		this.name = name;
		this.color = color;
	}

	@Override
	public int getCount() {
		return name.length;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int index, View view, ViewGroup arg2) {
		final HoldView hold;
		if(view == null){
			hold = new HoldView();
			view = LayoutInflater.from(context).inflate(R.layout.rightfiberadapter, null);
			view.setTag(hold);
		}else
			hold = (HoldView)view.getTag();
		hold.col = (TextView)view.findViewById(R.id.color);
		hold.text = (TextView)view.findViewById(R.id.text);
		hold.text.setText(name[index]);
		//		hold.col.setHeight(hold.text.getHeight());
		hold.col.setBackgroundColor(color[index]);
		hold.text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*new AlertDialog.Builder(context)
				.setMessage(hold.text.getText())
				.setPositiveButton("确定", null)
				.show();*/
				AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setMessage(hold.text.getText());
                builder2.show();
			}
		});
		return view;
	}	
	static class HoldView{
		TextView col;
		TextView text;
	}

}
