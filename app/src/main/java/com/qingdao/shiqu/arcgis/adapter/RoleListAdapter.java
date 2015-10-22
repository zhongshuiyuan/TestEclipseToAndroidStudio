package com.qingdao.shiqu.arcgis.adapter;

import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.UI.Controls.TextView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.qingdao.shiqu.arcgis.R;

/**
 * 角色列表适配器
 * 
 * @author MinG
 * 
 */
public class RoleListAdapter extends BaseAdapter
{
	Context context;
	LayoutInflater inflater;
	DataTable table;

	public RoleListAdapter( Context context )
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
		if(table != null) { return table.size(); }
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
		Log.i("BillListAdapter", "绑定" + position);
		View view = null;
		ViewHolder viewHolder;
		DataCollection dataCollection = table.get(position);

		viewHolder = new ViewHolder();
		view = inflater.inflate(R.layout.bs_role_listview_item, null);
		viewHolder.roleID = (TextView) view.findViewById(R.id.RoleID);
		viewHolder.roleName = (Button) view.findViewById(R.id.RoleName);
		view.setTag(viewHolder);
		// view.setOnTouchListener(new ListViewItemOnTouchListener());
		viewHolder.roleID.setText(dataCollection.get("RoleID").getValue().toString());
		viewHolder.roleName.setText(dataCollection.get("RoleName").getValue().toString());
		/*(if(position == 0)
		{
			view.setBackgroundResource(R.drawable.tab_selected);
			BS_Role.selectedView = view;
			BS_Role.staticRoleID = viewHolder.roleID.getText().toString();
			BS_User.selectedView = view;
			BS_User.staticRoleID = viewHolder.roleID.getText().toString();
		}*/
		return view;
	}

	class ViewHolder
	{
		Button roleName;
		TextView roleID;
	}
}
