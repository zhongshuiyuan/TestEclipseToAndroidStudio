package com.qingdao.shiqu.arcgis.adapter;

import java.util.ArrayList;

import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
    
    private ArrayList<String> list; 

    public ListViewAdapter(Context context, ArrayList<String> list) {
        super();
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }
 
	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO 自动生成的方法存根
		 if (convertView == null) {
	            convertView = inflater.inflate(R.layout.lv_items, null);
	        }
	        TextView tv = (TextView)convertView.findViewById(R.id.text);
	        tv.setText(list.get(position));
	         
	        return convertView;
	}

}
