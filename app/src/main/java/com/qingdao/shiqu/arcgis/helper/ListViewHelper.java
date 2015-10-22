package com.qingdao.shiqu.arcgis.helper;

import Eruntech.BirthStone.UI.Controls.TextView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.utils.NSXAsyncTask;
import com.qingdao.shiqu.arcgis.utils.ValueList;

/**
 * 权限列表辅助类，完成角色和权限互动操作
 * 
 * @author MinG
 * 
 */
public class ListViewHelper
{
	/**
	 * 根据角色设置权限菜单状态
	 * 
	 * @param roleID
	 *            角色ID
	 * @param listView
	 *            列表
	 * @throws Exception
	 */
	public static void setMenuListItemCheckState(final String roleID, final ListView listView)
	{
		try
		{
			new NSXAsyncTask<Object, Object>()
			{
				@Override
				protected Object doInBackground(Object... params)
				{
					return null;
				}

				@Override
				protected void onPostExecute(Object rs)
				{
					try
					{
						ValueList funcList = RoleForMeunBuffer.get(roleID);
						int size = listView.getChildCount();
						if (funcList == null)
						{
							for (int i = 0; i < size; i++)
							{
								View item = (View) listView.getChildAt(i);
								if (item != null)
								{
									CheckBox chk = (CheckBox) item.findViewById(R.id.CheckBox);
									chk.setChecked(false);
								}
							}
							return;
						}

						for (int i = 0; i < size; i++)
						{
							View item = (View) listView.getChildAt(i);
							if (item != null)
							{
								TextView funcID = (TextView) item.findViewById(R.id.FuncID);
								CheckBox chk = (CheckBox) item.findViewById(R.id.CheckBox);
								String funcIDStr = funcID.getText().toString().trim();
								if (funcList.contains(funcIDStr))
								{
									chk.setChecked(true);
								}
								else
								{
									chk.setChecked(false);
								}
							}
						}
					}
					catch (Exception ex)
					{
						Log.e("角色事件错误：", ex.getMessage());
					}
				}
			}.execute();
		}
		catch (Exception ex)
		{
			Log.e("FunctionList", ex.getMessage());
		}
	}

	/**
	 * 根据角色设置用户列表状态
	 * 
	 * @param roleID
	 *            角色ID
	 * @param listView
	 *            列表
	 * @throws Exception
	 */
	public static void setUserListItemCheckState(final String roleID, final ListView listView)
	{
		try
		{
			new NSXAsyncTask<Object, Object>()
			{
				@Override
				protected Object doInBackground(Object... params)
				{
					return null;
				}

				@Override
				protected void onPostExecute(Object rs)
				{
					try
					{
						ValueList funcList = RoleForUserBuffer.get(roleID);
						int size = listView.getChildCount();
						if (funcList == null)
						{
							for (int i = 0; i < size; i++)
							{
								View item = (View) listView.getChildAt(i);
								CheckBox chk = (CheckBox) item.findViewById(R.id.CheckBox);
								chk.setChecked(false);
							}
							return;
						}

						for (int i = 0; i < size; i++)
						{
							View item = (View) listView.getChildAt(i);
							TextView userID = (TextView) item.findViewById(R.id.UserID);
							CheckBox chk = (CheckBox) item.findViewById(R.id.CheckBox);
							String userIDStr = userID.getText().toString().trim();
							if (funcList.contains(userIDStr))
							{
								chk.setChecked(true);
							}
							else
							{
								chk.setChecked(false);
							}
						}
					}
					catch (Exception ex)
					{
						Log.e("角色事件错误：", ex.getMessage());
					}
				}
			}.execute();
		}
		catch (Exception ex)
		{
			Log.e("FunctionList", ex.getMessage());
		}
	}
}
