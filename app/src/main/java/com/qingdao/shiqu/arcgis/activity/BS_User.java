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
import com.qingdao.shiqu.arcgis.adapter.RoleListAdapter;
import com.qingdao.shiqu.arcgis.adapter.UserListAdapter;
import com.qingdao.shiqu.arcgis.control.OnTopBarClickListener;
import com.qingdao.shiqu.arcgis.control.TopBar;
import com.qingdao.shiqu.arcgis.helper.ListViewHelper;
import com.qingdao.shiqu.arcgis.helper.RoleForUserBuffer;
import com.qingdao.shiqu.arcgis.utils.NSXAsyncTask;

/**
 * 用户管理模块
 *
 * @author MinG
 *
 */
public class BS_User extends Activity
{
	final int REQUEST_CODE = 1;
	ListView roleListView, userListView;
	TextView roleID;
	Button roleName;
	TopBar topBar = null;
	SQLiteDatabase db = null;
	DataTable menuTable;
	final UUID uuid = UUID.randomUUID();

	public static View selectedView = null;
	public static String staticRoleID, tempRoleID="00";

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bs_user);
		onCreateView();
	}

	@Override
	public void onCreateView()
	{
		db = new SQLiteDatabase(BS_User.this);
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
		userListView = (ListView) findViewById(R.id.UserListView);
		userListView.setOnItemClickListener(onUserItemClickListener);
		userListView.setOnItemLongClickListener(onUserItemLongClickListener);
		bindRoleList();
		bindUserList();
		ListViewHelper.setMenuListItemCheckState(staticRoleID, userListView);
	}

	/**
	 * TopBar左侧按钮处理事件
	 */
	OnTopBarClickListener leftButtonOnClickListener = new OnTopBarClickListener()
	{
		@Override
		public void OnClick()
		{
			if (staticRoleID == null || staticRoleID.equals(""))
			{
				Toast.makeText(BS_User.this, "请选择用户角色", Toast.LENGTH_LONG).show();
			}
			else
			{
				Intent intent = new Intent(BS_User.this, BS_UserDialog_Add.class);
				intent.putExtra("reName", false);
				startActivityForResult(intent, REQUEST_CODE);
			}
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
				int size = userListView.getChildCount();
				for (int i = 0; i < size; i++)
				{
					View view = userListView.getChildAt(i);
					CheckBox chk = (CheckBox) view.findViewById(R.id.CheckBox);
					if (chk != null && chk.isChecked() == true)
					{
						TextView userID = (TextView) view.findViewById(R.id.UserID);
						DataCollection params = new DataCollection();
						params.add(new Data("UserID", userID.getText().toString()));
						db = new SQLiteDatabase(BS_User.this);
						db.execute("spBS_UserDel", params);
					}
				}
				bindUserList();
			}
			catch (Exception ex)
			{
				Toast.makeText(BS_User.this, "删除用户错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
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
				if(!tempRoleID.equals(staticRoleID))
				{
					bindUserList();
				}
				tempRoleID=staticRoleID;
			}
			catch (Exception ex)
			{
				Toast.makeText(BS_User.this, "角色操作错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	};

	/**
	 * 用户列表点击事件项目处理
	 */
	OnItemClickListener onUserItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			if (staticRoleID == null)
			{
				Toast.makeText(BS_User.this, "请先选择要设置的角色", Toast.LENGTH_LONG).show();
				return;
			}
			CheckBox checkbox = (CheckBox) arg1.findViewById(R.id.CheckBox);
			if (checkbox != null)
			{
				TextView userID = (TextView) arg1.findViewById(R.id.UserID);
				checkbox.setChecked(!checkbox.isChecked());
				if (checkbox.isChecked() == true)
				{
					RoleForUserBuffer.put(BS_User.staticRoleID, userID.getText().toString().replace('\n', ' ').trim());
				}
				else
				{
					RoleForUserBuffer.remove(BS_User.staticRoleID, userID.getText().toString().replace('\n', ' ')
							.trim());
				}
			}
		}
	};

	/**
	 * 用户列表长按事件处理
	 */
	OnItemLongClickListener onUserItemLongClickListener = new OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			roleID = (TextView) arg1.findViewById(R.id.RoleID);
			TextView userID = (TextView) arg1.findViewById(R.id.UserID);
			TextView userNo = (TextView) arg1.findViewById(R.id.UserNo);
			TextView password = (TextView) arg1.findViewById(R.id.Password);
			Intent intent = new Intent(BS_User.this, BS_UserDialog_Add.class);
			intent.putExtra("reName", true);
			intent.putExtra("roleID", roleID.getText().toString());
			intent.putExtra("userID", userID.getText().toString());
			intent.putExtra("userNo", userNo.getText().toString());
			intent.putExtra("password", password.getText().toString());
			startActivityForResult(intent, REQUEST_CODE);
			return false;
		}
	};

	/**
	 * 绑定用户列表
	 */
	private void bindUserList()
	{
		try
		{
			new NSXAsyncTask<Object, Object>()
			{
				@Override
				protected Object doInBackground(Object... params)
				{
					db = new SQLiteDatabase(BS_User.this);
					DataCollection params1 = new DataCollection();
					params1.add(new Data("RoleID", staticRoleID));
					DataTable result = db.executeTable("spBS_UserList", params1);
					return result;
				}

				@Override
				protected void onPostExecute(Object rs)
				{
					UserListAdapter adapter = new UserListAdapter(BS_User.this);
					adapter.setData((DataTable) rs);
					userListView.setAdapter(adapter);
				}
			}.execute();
		}
		catch (Exception ex)
		{
			Toast.makeText(BS_User.this, "绑定权限菜单错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
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
					db = new SQLiteDatabase(BS_User.this);
					DataTable result = db.executeTable("spBS_RoleList", null);
					return result;
				}

				@Override
				protected void onPostExecute(Object rs)
				{
					RoleListAdapter adapter = new RoleListAdapter(BS_User.this);
					adapter.setData((DataTable) rs);
					roleListView.setAdapter(adapter);
				}
			}.execute();
		}
		catch (Exception ex)
		{
			Toast.makeText(BS_User.this, "绑定角色列表错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
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
						if (staticRoleID == null || staticRoleID.equals(""))
						{
							Toast.makeText(BS_User.this, "请选择用户角色", Toast.LENGTH_LONG).show();
						}
						else
						{
							Boolean isAdd = data.getBooleanExtra("isAdd", false);
							DataCollection params = new DataCollection();
							params.add(new Data("UserID", data.getStringExtra("userID")));
							params.add(new Data("UserNo", data.getStringExtra("userNo")));
							params.add(new Data("Password", data.getStringExtra("password")));
							params.add(new Data("RoleID", staticRoleID));
							db = new SQLiteDatabase(BS_User.this);
							if (isAdd)
							{
								db.execute("spBS_UserAdd", params);
							}
							else
							{
								db.execute("spBS_UserUpdate", params);
							}
							bindUserList();
						}
					}
					break;
			}
		}
		catch (Exception ex)
		{
			Toast.makeText(BS_User.this, "保存角色错误，原因：" + ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

}
