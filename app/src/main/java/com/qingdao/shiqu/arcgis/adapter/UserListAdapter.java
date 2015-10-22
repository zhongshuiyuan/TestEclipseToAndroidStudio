package com.qingdao.shiqu.arcgis.adapter;

import java.util.UUID;

import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import Eruntech.BirthStone.UI.Controls.TextView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.activity.BS_User;
import com.qingdao.shiqu.arcgis.helper.RoleForUserBuffer;

/**
 * 用户列表适配器
 * 
 * @author MinG
 * 
 */
public class UserListAdapter extends BaseAdapter
{
	Context context;
	LayoutInflater inflater;
	DataTable table;
	SQLiteDatabase db = null;
	UUID uuid = UUID.randomUUID();
	public UserListAdapter( Context context )
	{
		this.context = context;
		inflater = LayoutInflater.from(context);
		db = new SQLiteDatabase(context);
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
		final ViewHolder viewHolder;
		DataCollection dataCollection = table.get(position);
		viewHolder = new ViewHolder();
		view = inflater.inflate(R.layout.bs_user_listview_item, null);
		viewHolder.userID = (TextView) view.findViewById(R.id.UserID);
		viewHolder.userNo = (TextView) view.findViewById(R.id.UserNo);
		viewHolder.password = (TextView) view.findViewById(R.id.Password);
		viewHolder.roleID = (TextView) view.findViewById(R.id.RoleID);
		viewHolder.checkbox = (CheckBox) view.findViewById(R.id.CheckBox);
		viewHolder.checkbox.setChecked(false);
		view.setTag(viewHolder);
		viewHolder.checkbox.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				CheckBox checkbox = ((CheckBox) arg0);
				if(checkbox.isChecked() == true)
				{
					checkbox.setChecked(true);
					RoleForUserBuffer.put(BS_User.staticRoleID, viewHolder.userID.getText().toString().replace('\n', ' ').trim());
				}
				else
				{
					checkbox.setChecked(false);
					RoleForUserBuffer.remove(BS_User.staticRoleID, viewHolder.userID.getText().toString().replace('\n', ' ').trim());
				}
			}
		});
		// view.setOnTouchListener(new ListViewItemOnTouchListener());
		viewHolder.userID.setText(dataCollection.get("UserID").getValue().toString());
		viewHolder.userNo.setText(dataCollection.get("UserNo").getValue().toString());
		viewHolder.password.setText(dataCollection.get("Password").getValue().toString());
		viewHolder.roleID.setText(dataCollection.get("RoleID").getValue().toString());
		if(RoleForUserBuffer.contains(BS_User.staticRoleID, viewHolder.userID.getText().toString().replace('\n', ' ').trim()))
		{
			viewHolder.checkbox.setChecked(true);
		}
		return view;
	}
	

	class ViewHolder
	{
		public TextView userID, roleID, userNo, password;
		public CheckBox checkbox;
	}
}
