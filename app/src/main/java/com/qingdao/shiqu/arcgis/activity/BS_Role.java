package com.qingdao.shiqu.arcgis.activity;

import java.util.UUID;

import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import Eruntech.BirthStone.UI.Controls.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.FunctionListAdapter;
import com.qingdao.shiqu.arcgis.adapter.RoleListAdapter;
import com.qingdao.shiqu.arcgis.control.OnTopBarClickListener;
import com.qingdao.shiqu.arcgis.control.TopBar;
import com.qingdao.shiqu.arcgis.helper.FunctionHelper;
import com.qingdao.shiqu.arcgis.helper.ListViewHelper;
import com.qingdao.shiqu.arcgis.helper.RoleForMenuBufferInitialize;
import com.qingdao.shiqu.arcgis.helper.RoleForMeunBuffer;
import com.qingdao.shiqu.arcgis.utils.NSXAsyncTask;

/**
 * 角色管理模块
 *
 * @author MinG
 *
 */
public class BS_Role extends Activity
{
	final int REQUEST_CODE = 1;
	ListView roleListView, functionListView;
	TextView roleID;
	Button roleName;
	TopBar topBar = null;

	SQLiteDatabase db = null;
	DataTable menuTable;
	final UUID uuid = UUID.randomUUID();
	public static View selectedView = null;
	public static String staticRoleID, tempRoleID = "00";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bs_role);
		onCreateView();
	}

	@Override
	public void onCreateView()
	{
		db = new SQLiteDatabase(BS_Role.this);
		topBar = (TopBar) findViewById(R.id.topBar);
		topBar.setLeftImageButtonVisibility(false);
		topBar.setLeftTextButtonVisibility(true);
		topBar.setLeftButtonText("添加");
		topBar.setLeftButtonOnClickListener(leftButtonOnClickListener);
		topBar.setRightButtonText("删除");
		topBar.setRightImageButtonVisibility(false);
		topBar.setRightButtonOnClickListener(rightButtonOnClickListener);
		roleListView = (ListView) findViewById(R.id.RoleListView);
		roleListView.setOnItemClickListener(onRoleItemClickListener);
		roleListView.setOnItemLongClickListener(onRoleItemLongClickListener);
		functionListView = (ListView) findViewById(R.id.FunctionListView);
		functionListView.setOnItemClickListener(onFunctionItemClickListener);
		bindRoleList();
		bindFunctionList();
		new RoleForMenuBufferInitialize(BS_Role.this).loadMenuToBuffer();
		ListViewHelper.setMenuListItemCheckState(staticRoleID, functionListView);
		super.onCreateView();
	}

	/**
	 * TopBar左侧按钮处理事件
	 */
	OnTopBarClickListener leftButtonOnClickListener = new OnTopBarClickListener()
	{
		@Override
		public void OnClick()
		{
			Intent intent = new Intent(BS_Role.this, BS_RoleDialog_Add.class);
			intent.putExtra("reName", false);
			startActivityForResult(intent, REQUEST_CODE);
		}
	};

	/**
	 * TopBar右侧按钮处理事件
	 */
	OnTopBarClickListener rightButtonOnClickListener = new OnTopBarClickListener()
	{
		@Override
		public void OnClick()
		{
			try
			{
				if (roleListView.getCount() > 1)
				{
					if (roleID == null)
					{
						Toast.makeText(BS_Role.this, "请选中要删除的角色！", Toast.LENGTH_LONG).show();
					}
					else
					{
						DataCollection params = new DataCollection();
						params.add(new Data("RoleID", roleID.getText().toString()));
						db = new SQLiteDatabase(BS_Role.this);
						db.execute("spBS_RoleDel", params);
						bindRoleList();
					}
				}
				else
				{
					Toast.makeText(BS_Role.this, "至少保留一个角色！", Toast.LENGTH_LONG).show();
				}
			}
			catch (Exception ex)
			{
				Toast.makeText(BS_Role.this, "删除角色错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	};

	/**
	 * 角色列表点击项目处理事件
	 */
	OnItemClickListener onRoleItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			try
			{
				roleID = (TextView) arg1.findViewById(R.id.RoleID);
				staticRoleID = roleID.getText().toString();
				roleName = (Button) arg1.findViewById(R.id.RoleName);
				if (selectedView != null)
				{
					selectedView.setBackgroundResource(R.drawable.button_role_drawable);
				}
				selectedView = arg1;
				arg1.setBackgroundResource(R.drawable.button_role_pressed);
				if (!tempRoleID.equals(staticRoleID))
				{
					ListViewHelper.setMenuListItemCheckState(staticRoleID, functionListView);
				}
				tempRoleID=staticRoleID;

			}
			catch (Exception ex)
			{
				Toast.makeText(BS_Role.this, "角色操作错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	};

	/**
	 * 角色列表长按事件
	 */
	OnItemLongClickListener onRoleItemLongClickListener = new OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			roleID = (TextView) arg1.findViewById(R.id.RoleID);
			staticRoleID = roleID.getText().toString();
			roleName = (Button) arg1.findViewById(R.id.RoleName);
			Intent intent = new Intent(BS_Role.this, BS_RoleDialog_Add.class);
			intent.putExtra("reName", true);
			intent.putExtra("roleID", staticRoleID);
			intent.putExtra("roleName", roleName.getText().toString());
			startActivityForResult(intent, REQUEST_CODE);
			return false;
		}
	};

	/**
	 * 权限列表点击项目处理事件
	 */
	OnItemClickListener onFunctionItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			if (staticRoleID == null)
			{
				Toast.makeText(BS_Role.this, "请先选择要设置的角色", Toast.LENGTH_LONG).show();
				return;
			}
			CheckBox checkbox = (CheckBox) arg1.findViewById(R.id.CheckBox);
			if (checkbox != null)
			{
				DataCollection params = new DataCollection();
				params.add(new Data("MenuID", UUID.randomUUID().toString()));
				TextView funcName = (TextView) arg1.findViewById(R.id.FuncName);
				params.add(new Data("MenuName", funcName.getText().toString()));
				TextView funcCode = (TextView) arg1.findViewById(R.id.FuncCode);
				params.add(new Data("MenuCode", funcCode.getText().toString()));
				TextView funcSrc = (TextView) arg1.findViewById(R.id.FuncSrc);
				params.add(new Data("FuncSrc", funcSrc.getText().toString()));
				TextView parentID = (TextView) arg1.findViewById(R.id.ParentID);
				params.add(new Data("ParentID", parentID.getText().toString()));
				params.add(new Data("RoleID", staticRoleID));
				TextView funcID = (TextView) arg1.findViewById(R.id.FuncID);
				params.add(new Data("FuncID", funcID.getText().toString()));
				checkbox.setChecked(!checkbox.isChecked());
				if (checkbox.isChecked() == true)
				{
					FunctionListAdapter.addorRemoveMenu(BS_Role.this, params, true);
					RoleForMeunBuffer.put(BS_Role.staticRoleID, funcID.getText().toString().replace('\n', ' ').trim());
				}
				else
				{
					FunctionListAdapter.addorRemoveMenu(BS_Role.this, params, false);
					RoleForMeunBuffer.remove(BS_Role.staticRoleID, funcID.getText().toString().replace('\n', ' ')
							.trim());
				}
			}
		}
	};

	/**
	 * 绑定权限列表
	 */
	private void bindFunctionList()
	{
		try
		{
			new NSXAsyncTask<Object, Object>()
			{
				@Override
				protected Object doInBackground(Object... params)
				{
					db = new SQLiteDatabase(BS_Role.this);
					DataTable result = db.executeTable("spBS_FunctionList", null);
					return result;
				}

				@Override
				protected void onPostExecute(Object rs)
				{
					FunctionListAdapter adapter = new FunctionListAdapter(BS_Role.this);
					adapter.setData((DataTable) rs);
					functionListView.setAdapter(adapter);
				}
			}.execute();
		}
		catch (Exception ex)
		{
			Toast.makeText(BS_Role.this, "绑定权限菜单错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 绑定角色列表
	 */
	private void bindRoleList()
	{
		try
		{
			new NSXAsyncTask<Object, Object>()
			{
				@Override
				protected Object doInBackground(Object... params)
				{
					db = new SQLiteDatabase(BS_Role.this);
					DataTable result = db.executeTable("spBS_RoleList", null);
					return result;
				}

				@Override
				protected void onPostExecute(Object rs)
				{
					RoleListAdapter adapter = new RoleListAdapter(BS_Role.this);
					adapter.setData((DataTable) rs);
					roleListView.setAdapter(adapter);
				}
			}.execute();
		}
		catch (Exception ex)
		{
			Toast.makeText(BS_Role.this, "绑定角色列表错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// super.onActivityResult(requestCode, resultCode, data);
		try
		{
			switch (requestCode)
			{
				case REQUEST_CODE:
					if (resultCode == Activity.RESULT_OK)
					{
						Boolean isAdd = data.getBooleanExtra("isAdd", false);
						if (isAdd)
						{
							DataCollection params = new DataCollection();
							params.add(new Data("RoleID", data.getStringExtra("roleID")));
							staticRoleID = data.getStringExtra("roleID");
							params.add(new Data("RoleName", data.getStringExtra("roleName")));
							db = new SQLiteDatabase(BS_Role.this);
							db.execute("spBS_RoleAdd", params);
							addRoleForMenu();
						}
						else
						{
							DataCollection params = new DataCollection();
							params.add(new Data("RoleID", roleID.getText().toString()));
							staticRoleID = roleID.getText().toString();
							params.add(new Data("RoleName", data.getStringExtra("roleName")));
							db = new SQLiteDatabase(BS_Role.this);
							db.execute("spBS_RoleUpdate", params);
						}
						bindRoleList();
					}
					break;
			}
		}
		catch (Exception ex)
		{
			Toast.makeText(BS_Role.this, "保存角色错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 初始化角色基本菜单
	 */
	private void addRoleForMenu()
	{
		try
		{
			if (menuTable == null)
			{
				db = new SQLiteDatabase(BS_Role.this);
				menuTable = db.executeTable("spBS_FunctionParentList", null);
			}
			db = new SQLiteDatabase(BS_Role.this);
			for (DataCollection params : menuTable)
			{
				params.add(new Data("MenuID", UUID.randomUUID().toString()));
				params.add(new Data("MenuName", params.get(1).getValue()));
				params.add(new Data("MenuCode", params.get(2).getValue()));
				params.add(new Data("FuncSrc", params.get(3).getValue()));
				params.add(new Data("ParentID", params.get(4).getValue()));
				params.add(new Data("RoleID", staticRoleID));
				params.add(new Data("FuncID", params.get(0).getValue()));
				db.execute("spBS_MenuAdd", params);
			}
		}
		catch (Exception ex)
		{
			Toast.makeText(BS_Role.this, "初始化菜单错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void finish()
	{
		try
		{
			FunctionHelper.getFunction(this);
			super.finish();
		}
		catch (Exception ex)
		{
			Toast.makeText(BS_Role.this, "更新权限错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
