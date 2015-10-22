package com.qingdao.shiqu.arcgis.adapter;

import java.util.ArrayList;
import java.util.List;

import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class CheckboxAdapter extends BaseAdapter {

	private Context context;
	private String[] text;
	public List<Boolean> mChecked;
	public String[] gdIDs;
	public CheckboxAdapter(Context context, String[] text,String[] gdIDs) {
		super();
		this.context = context;
		this.text = text;
		this.gdIDs = gdIDs;
		mChecked = new ArrayList<Boolean>();
		for(int i=0;i<text.length;i++){
			mChecked.add(false);
		}
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return text.length;
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
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO 自动生成的方法存根
		ViewHold hold;
		TextView tv;
		if(view == null){
			hold = new ViewHold();
			view = LayoutInflater.from(context).inflate(R.layout.checkboxadapter, null);
			view.setTag(hold);
		}else
			hold = (ViewHold)view.getTag();
		final int p = arg0;
		hold.tv = (TextView)view.findViewById(R.id.gdtext);
		hold.tv.setText(text[arg0]);
		hold.cb = (CheckBox)view.findViewById(R.id.gdcheck);
		hold.cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				CheckBox cb1= (CheckBox)arg0;
				mChecked.set(p,cb1.isChecked());
				if(!cb1.isChecked())
					gdIDs[p] = "";
			}
		});
		hold.cb.setChecked(mChecked.get(arg0));
		return view;
	}

	static class ViewHold{
		public CheckBox cb;
		public TextView tv;
	}
}
