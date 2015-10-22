package com.eruntech.crosssectionview.activities;



import com.qingdao.shiqu.arcgis.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;


public class SettingsActivity extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragement()).commit();
	}
	
	public static class SettingFragement extends PreferenceFragment{  
		@Override  
		public void onCreate(Bundle savedInstanceState) {  
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.tubeview_pref_setting);
		}  
	}
	
}
