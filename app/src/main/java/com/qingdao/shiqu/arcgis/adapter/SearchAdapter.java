package com.qingdao.shiqu.arcgis.adapter;

import java.util.List;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.mode.Road;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * 搜索历史列表适配器
 *
 */
public class SearchAdapter extends BaseAdapter
{
    Context context = null;
    LayoutInflater inflater = null;
    List<Road> list = null;
   
    public SearchAdapter(Context context)
    {
    	this(context, null);
    }
    
    public SearchAdapter(Context context,List<Road> list)
    {
    	this.context = context;
    	inflater = LayoutInflater.from(context);
    	this.list = list;
    }
    
    public void setData(List<Road> list)
    {
    	this.list = list;
    }
    
	@Override
	public int getCount()
	{
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.search_listview_item, null);
			TextView tView = (TextView)convertView.findViewById(R.id.name);
			convertView.setTag(tView);
		}
		TextView tView = (TextView)convertView.getTag();
		tView.setText(list.get(position).getName());
		return convertView;
	}

}
