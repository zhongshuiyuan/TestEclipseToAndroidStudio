package com.qingdao.shiqu.arcgis.adapter;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.listener.TextViewClickListener;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class LeftfiberAdapter extends SimpleAdapter {

	private String[] name;
	private int[] color;
	private Context context;
	
	public LeftfiberAdapter(Context context,String[] name, int[] color) {
		super(context, null, 0, name, color);
		this.context = context;
		this.name = name;
		this.color = color;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return name.length;
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
	public View getView(int index, View view, ViewGroup arg2) {
		// TODO 自动生成的方法存根
		final ViewHold hold;
		final View vw = view;
		if(view == null){
			hold = new ViewHold();
			view = LayoutInflater.from(context).inflate(R.layout.leftfiberadapter, null);
			view.setTag(hold);
		}else
			hold = (ViewHold)view.getTag();
		hold.text = (TextView)view.findViewById(R.id.text);
		hold.col = (TextView)view.findViewById(R.id.color);
		hold.text.setText(name[index]);
		hold.col.setBackgroundColor(color[index]);
//		LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams)hold.col.getLayoutParams(); //取控件textView当前的布局参数
//		linearParams.height = hold.text.getHeight();
//		hold.col.setLayoutParams(linearParams);
		TextViewClickListener tvc = new TextViewClickListener(context, hold.text);
		hold.text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
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
	static class ViewHold {
		Context context;
		public TextView text;
		public TextView col;
	}

}
