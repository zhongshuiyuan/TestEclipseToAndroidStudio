package com.qingdao.shiqu.arcgis.adapter;

import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.UI.Controls.TextView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qingdao.shiqu.arcgis.R;

/**
 * 工程附图列表适配器
 * 
 * @author MinG
 * 
 */
public class Dialog_RangeListAdapter extends BaseAdapter
{
	Context context;
	LayoutInflater inflater;
	DataTable table;

	public Dialog_RangeListAdapter(Context context)
	{
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(DataTable table)
	{
		this.table = table;
	}

	@Override
	public int getCount()
	{
		if (table != null)
		{
			return table.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		return table.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = null;
		ViewHolder viewHolder;
		DataCollection dataCollection = table.get(position);

		viewHolder = new ViewHolder();
		view = inflater.inflate(R.layout.dialog_range_listview_item, null);
		viewHolder.att_Name = (TextView) view.findViewById(R.id.att_name);
		view.setTag(viewHolder);
		viewHolder.att_Name.setText(dataCollection.get("att_name").getValue().toString());
		return view;
	}

	class ViewHolder
	{
		public TextView att_Name;
	}
}
