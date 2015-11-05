package com.qingdao.shiqu.arcgis.adapter;

import java.util.ArrayList;

import com.esri.android.map.Layer;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.activity.Main;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	private Context context;
	private ArrayList<String> parents;
	private ArrayList<ArrayList<Layer>> childs;
	
	TextView getParentTextView(){
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);
		TextView tv = new TextView(context);
		tv.setLayoutParams(lp);
		tv.setPadding(36, 0, 0, 0);
		//				tv.setTextSize(20dp);
		tv.setTextColor(Color.BLACK);
		return tv;
	}
	TextView getChildTextView(){
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);
		TextView tv = new TextView(context);
		tv.setLayoutParams(lp);
		tv.setTextColor(Color.BLACK);
		return tv;
	}
	
	

	public MyExpandableListAdapter(Context context, ArrayList<String> titles,ArrayList<ArrayList<Layer>> childs) {
		super();
		this.context = context;
		this.parents = titles;
		this.childs = childs;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO 自动生成的方法存根
//		return lys[arg1];
		return childs.get(arg0).get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO 自动生成的方法存根
		return arg1;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ParrentContent parrentContent;
		if (convertView == null) {
			parrentContent = new ParrentContent();
			convertView = LayoutInflater.from(context).inflate(R.layout.toc_parent, null);
			convertView.setTag(parrentContent);
		} else {
			parrentContent = (ParrentContent) convertView.getTag();
		}

		parrentContent.indicator = (ImageView) convertView.findViewById(R.id.toc_parent_iv_group_indicator);
		parrentContent.icon = (ImageView) convertView.findViewById(R.id.toc_parent_iv_icon);
		parrentContent.title = (TextView) convertView.findViewById(R.id.toc_parent_tv_title);


		if (isExpanded) {
			parrentContent.indicator.setImageResource(R.drawable.ic_chevron_down);
		} else {
			parrentContent.indicator.setImageResource(R.drawable.ic_chevron_right);
		}
		parrentContent.icon.setImageResource(R.drawable.ic_layers);
		parrentContent.title.setText(getGroup(groupPosition).toString());
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ChildContent childContent;
		if (convertView == null) {
			childContent = new ChildContent();
			convertView = LayoutInflater.from(context).inflate(R.layout.toc_child, null);
			convertView.setTag(childContent);
		} else {
			childContent = (ChildContent) convertView.getTag();
		}

		childContent.isVisible = (CheckBox) convertView.findViewById(R.id.toc_cb_isvisible);
		childContent.layerName = (TextView) convertView.findViewById(R.id.toc_tv_layername);

		if(childs.get(groupPosition).get(childPosition).isVisible()) {
			childContent.isVisible.setChecked(true);
		}
		childContent.isVisible.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				childs.get(groupPosition).get(childPosition).setVisible(!childs.get(groupPosition).get(childPosition).isVisible());
			}
		});
		childContent.layerName.setText(childs.get(groupPosition).get(childPosition).getName());
		return convertView;
//		LinearLayout ll = new LinearLayout(context);
//		ll.setOrientation(LinearLayout.HORIZONTAL);
//		LinearLayout lll = new LinearLayout(context);
//		lll.setOrientation(LinearLayout.HORIZONTAL);
//		lll.setPadding(85, 0, 0, 0);
//		CheckBox cb = new CheckBox(context);
//		/*if(lys[childPosition].isVisible())
//			cb.setChecked(true);*/
//		if(childs.get(groupPosition).get(childPosition).isVisible())
//			cb.setChecked(true);
//		cb.setPadding(0,0,0, 0);
//		cb.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO 自动生成的方法存根
////				lys[childPosition].setVisible(!lys[childPosition].isVisible());
//				childs.get(groupPosition).get(childPosition).setVisible(!childs.get(groupPosition).get(childPosition).isVisible());
//			}
//		});
//		lll.addView(cb);
//
//		TextView tex = new TextView(context);
////		tex.setText(lys[childPosition].getName());
//		tex.setText(childs.get(groupPosition).get(childPosition).getName());
//		//				tex.setPadding(50, 0, 0, 0);
//		lll.addView(tex);
//		ll.addView(lll);
//		return ll;
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO 自动生成的方法存根
//		return lys.length;
		return childs.get(arg0).size();
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO 自动生成的方法存根
		return parents.get(arg0);
	}

	@Override
	public int getGroupCount() {
		// TODO 自动生成的方法存根
		return parents.size();
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO 自动生成的方法存根
		return arg0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO 自动生成的方法存根
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO 自动生成的方法存根
		return true;
	}

	private static class ParrentContent {
		public ImageView indicator;
		public ImageView icon;
		public TextView title;
	}

	private static class ChildContent {
		public CheckBox isVisible;
		public TextView layerName;
	}

}
