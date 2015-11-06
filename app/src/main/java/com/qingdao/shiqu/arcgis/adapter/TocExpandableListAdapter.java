package com.qingdao.shiqu.arcgis.adapter;

import java.util.ArrayList;

import com.esri.android.map.Layer;
import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * TOC 控件（Main Activity的右抽屉）的适配器
 */
public class TocExpandableListAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private ArrayList<String> mParents;
	private ArrayList<ArrayList<Layer>> mChilds;

	public TocExpandableListAdapter(Context context, ArrayList<String> titles, ArrayList<ArrayList<Layer>> childs) {
		super();
		this.mContext = context;
		this.mParents = titles;
		this.mChilds = childs;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ParrentContent parrentContent;
		if (convertView == null) {
			parrentContent = new ParrentContent();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.toc_parent, null);
			convertView.setTag(parrentContent);
		} else {
			parrentContent = (ParrentContent) convertView.getTag();
		}

		parrentContent.indicator = (ImageView) convertView.findViewById(R.id.toc_parent_iv_group_indicator);
		parrentContent.icon = (ImageView) convertView.findViewById(R.id.toc_parent_iv_icon);
		parrentContent.title = (TextView) convertView.findViewById(R.id.toc_parent_tv_title);
		parrentContent.childrenVisible = (CheckBox) convertView.findViewById(R.id.toc_parent_cb_isvisible);

		if (isExpanded) {
			parrentContent.indicator.setImageResource(R.drawable.ic_chevron_down);
		} else {
			parrentContent.indicator.setImageResource(R.drawable.ic_chevron_right);
		}

		parrentContent.icon.setImageResource(R.drawable.ic_layers);

		parrentContent.title.setText(getGroup(groupPosition).toString());

		boolean isChecked = true;
		final int childrenCount = getChildrenCount(groupPosition);
		for (int i = 0; i < childrenCount; ++i) {
			if (!mChilds.get(groupPosition).get(i).isVisible()) {
				isChecked = false;
				break;
			}
		}
		parrentContent.childrenVisible.setChecked(isChecked);
		parrentContent.childrenVisible.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isChecked = ((CheckBox) v).isChecked();
				for (int i = 0; i < childrenCount; ++i) {
					mChilds.get(groupPosition).get(i).setVisible(isChecked);
				}
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ChildContent childContent;
		if (convertView == null) {
			childContent = new ChildContent();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.toc_child, null);
			convertView.setTag(childContent);
		} else {
			childContent = (ChildContent) convertView.getTag();
		}

		childContent.visible = (CheckBox) convertView.findViewById(R.id.toc_cb_isvisible);
		childContent.layerName = (TextView) convertView.findViewById(R.id.toc_tv_layername);

		boolean isChecked = mChilds.get(groupPosition).get(childPosition).isVisible();
		childContent.visible.setChecked(isChecked);
		childContent.visible.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean visible = ((CheckBox) v).isChecked();
				mChilds.get(groupPosition).get(childPosition).setVisible(visible);
				notifyDataSetChanged();
			}
		});
		childContent.layerName.setText(mChilds.get(groupPosition).get(childPosition).getName());

		return convertView;
	}

	@Override
	public int getGroupCount() {
		return mParents.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return mChilds.get(groupPosition).hashCode();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mParents.get(groupPosition);
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mChilds.get(groupPosition).size();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mChilds.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return mChilds.get(groupPosition).get(childPosition).getID();
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		//return mChilds.get(groupPosition).get(childPosition).isVisible();
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	private static class ParrentContent {
		public ImageView indicator;
		public ImageView icon;
		public TextView title;
		public CheckBox childrenVisible;
	}

	private static class ChildContent {
		public CheckBox visible;
		public TextView layerName;
	}

}
