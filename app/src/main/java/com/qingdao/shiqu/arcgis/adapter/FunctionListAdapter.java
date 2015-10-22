package com.qingdao.shiqu.arcgis.adapter;

import java.util.UUID;

import Eruntech.BirthStone.Core.Parse.Data;
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
import android.widget.Toast;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.activity.BS_Role;
import com.qingdao.shiqu.arcgis.helper.RoleForMeunBuffer;

/**
 * 权限列表适配器
 * 
 * @author MinG
 * 
 */
public class FunctionListAdapter extends BaseAdapter
{
	Context context;
	LayoutInflater inflater;
	DataTable table;
	SQLiteDatabase db = null;
	UUID uuid = UUID.randomUUID();
	public FunctionListAdapter( Context context )
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
		int ResourceID = R.layout.bs_function_listview_item_child;
		if(dataCollection.get("ParentID").getValue().toString().equals("0"))
		{
			ResourceID = R.layout.bs_function_listview_item_main;
		}
		viewHolder = new ViewHolder();
		view = inflater.inflate(ResourceID, null);
		viewHolder.funcID = (TextView) view.findViewById(R.id.FuncID);
		viewHolder.funcName = (TextView) view.findViewById(R.id.FuncName);
		viewHolder.funcCode = (TextView) view.findViewById(R.id.FuncCode);
		viewHolder.funcSrc = (TextView) view.findViewById(R.id.FuncSrc);
		viewHolder.parentID = (TextView) view.findViewById(R.id.ParentID);
		viewHolder.checkbox = (CheckBox) view.findViewById(R.id.CheckBox);
		viewHolder.checkbox.setChecked(false);
		view.setTag(viewHolder);
		viewHolder.checkbox.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				CheckBox checkbox = ((CheckBox) arg0);
				DataCollection params = new DataCollection();
				params.add(new Data("MenuID", UUID.randomUUID().toString()));
				params.add(new Data("MenuName",viewHolder.funcName.getText().toString()));
				params.add(new Data("MenuCode",viewHolder.funcCode.getText().toString()));
				params.add(new Data("FuncSrc",viewHolder.funcSrc.getText().toString()));
				params.add(new Data("ParentID",viewHolder.parentID.getText().toString()));
				params.add(new Data("RoleID",BS_Role.staticRoleID));
				params.add(new Data("FuncID", viewHolder.funcID.getText().toString()));
				if(checkbox.isChecked() == true)
				{
					checkbox.setChecked(true);
					addorRemoveMenu(context, params, true);
					RoleForMeunBuffer.put(BS_Role.staticRoleID, viewHolder.funcID.getText().toString().replace('\n', ' ').trim());
				}
				else
				{
					checkbox.setChecked(false);
					addorRemoveMenu(context, params, true);
					RoleForMeunBuffer.remove(BS_Role.staticRoleID, viewHolder.funcID.getText().toString().replace('\n', ' ').trim());
				}
			}
		});
		// view.setOnTouchListener(new ListViewItemOnTouchListener());
		viewHolder.funcID.setText(dataCollection.get("FuncID").getValue().toString());
		viewHolder.funcName.setText(dataCollection.get("FuncName").getValue().toString());
		viewHolder.funcCode.setText(dataCollection.get("FuncCode").getValue().toString());
		viewHolder.funcSrc.setText(dataCollection.get("FuncSrc").getValue().toString());
		viewHolder.parentID.setText(dataCollection.get("ParentID").getValue().toString());
		if(RoleForMeunBuffer.contains(BS_Role.staticRoleID, viewHolder.funcID.getText().toString().replace('\n', ' ').trim()))
		{
			viewHolder.checkbox.setChecked(true);
		}
		return view;
	}
	
	/**
	 * 选中或移除菜单权限
	 * @param viewHolder
	 * @param isAdd 是否添加权限
	 */
	public static void addorRemoveMenu(Context context, DataCollection params, Boolean isAdd)
	{
		try
		{
			SQLiteDatabase db = new SQLiteDatabase(context);
			if(isAdd)
			{
				db.execute("spBS_MenuAdd", params);
			}
			else
			{
				db.execute("spBS_MenuDel", params);
			}
		}
		catch(Exception ex)
		{
			Toast.makeText(context, "插入菜单时错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	class ViewHolder
	{
		public TextView funcID, funcName, funcCode, funcSrc, parentID;
		public CheckBox checkbox;
	}
}
