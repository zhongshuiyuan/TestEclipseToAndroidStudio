package com.eruntech.crosssectionview.activities;

import java.util.List;

import com.eruntech.crosssectionview.adapters.TubeViewAdapter;
import com.eruntech.crosssectionview.drawobjects.Hole;
import com.eruntech.crosssectionview.drawobjects.TubePad;
import com.eruntech.crosssectionview.interfaces.OnDataGridRowSelectedListener;
import com.eruntech.crosssectionview.interfaces.OnTubeViewObjectSeletedListener;
import com.eruntech.crosssectionview.interfaces.OnTubeViewServiceListener;
import com.eruntech.crosssectionview.services.TubeViewService;
import com.eruntech.crosssectionview.utils.DensityUtil;
import com.eruntech.crosssectionview.utils.HoleUtil;
import com.eruntech.crosssectionview.utils.TubePadUtil;
import com.eruntech.crosssectionview.valueobjects.HoleInXml;
import com.eruntech.crosssectionview.valueobjects.TubePadInXml;
import com.eruntech.crosssectionview.views.TubeView;
import com.eruntech.crosssectionview.widgets.datagrid.libs.TableFixHeaders;
import com.qingdao.shiqu.arcgis.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class TubeViewActivity extends Activity
		implements OnTubeViewObjectSeletedListener, OnDataGridRowSelectedListener, OnTubeViewServiceListener
{
	// //////////////////////////////////////////////////////公有常量
	// //////////////////////////////////////////////////////私有常量
	private final String DEBUG_TAG = "debug " + this.getClass().getSimpleName();
	private final float DATAGRID_SHOW = 4;
	private final float DATAGRID_HIDE = 0;
	private final int ACTIVITY_HORIZONTAL_MARGIN = 16;
	// //////////////////////////////////////////////////////公有变量
	// //////////////////////////////////////////////////////私有变量
	private TubeViewService mTubeViewService;
	private boolean isShowedAnimation = true;
	private int mAnimationDuration;
	private TableFixHeaders mDataGrid;
	private TubeView mDisplayView;
	private RelativeLayout mDataGridContainer;
	private ToggleButton mAutoHideTgbtn;
	private boolean mIsDataGridShowed = false;
	private boolean mIsDataGridAutoHide = true;
	private boolean isGotId = false;

	// //////////////////////////////////////////////////////属性

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tube_view);

		mTubeViewService = new TubeViewService(this);

		mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

		mDisplayView = (TubeView)findViewById(R.id.tubeview_draw_table);

		mAutoHideTgbtn = (ToggleButton) findViewById(R.id.tubeview_tgbtn_auto_hide_datagrid);

		mDataGridContainer = (RelativeLayout) findViewById(R.id.tubeview_container_datagrid);

		mDataGrid = (TableFixHeaders) findViewById(R.id.tubeview_datagrid);

		if(getIntent() != null) {
			isGotId = false;
			long tubeId = getIntent().getLongExtra(getString(R.string.intent_extra_key_tube_id), -110);
			if (tubeId > 0) {
				isGotId = true;
				getTubePadData(tubeId);
			} else if (tubeId == -110) {
				Log.e(DEBUG_TAG, "无法正确获取通过Intent传入的管道ID，请调试排查");
			} else {
				Log.e(DEBUG_TAG, "通过Intent传入的管道ID应大于0，请调试排查");

			}

			if (!isGotId) {
				this.setResult(RESULT_CANCELED);
				this.finish();
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		if (isGotId) {
			this.setResult(RESULT_OK);
		} else {
			this.setResult(RESULT_CANCELED);
		}

		super.onBackPressed();
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		isShowedAnimation = sharedPrefs.getBoolean(getString(R.string.preference_file_key_show_animation), true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tube_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		Log.d(DEBUG_TAG, "选中“" + item.toString() + "”菜单项");

		int itemId = item.getItemId();

		switch (itemId) {
			case R.id.action_settings:
				Intent settingIntent = new Intent(this, SettingsActivity.class);
				startActivity(settingIntent);
				return false;
			default:
				Log.w(DEBUG_TAG, "尚未给“" + item.toString() + "”添加动作");
				break;
		}

		return true;
	}

	public void tubeViewOnClick(View view)
	{
		Button clickBtn = (Button) view;
		Log.d(DEBUG_TAG, "按下“" + clickBtn.getText() + "”按钮");

		switch (view.getId()) {
			case R.id.tubeview_tgbtn_auto_hide_datagrid:
				onAutoHideTgbtnClicked();
				break;
			case R.id.tubeview_btn_open_option_menu:
				openOptionsMenu();
				break;
			default:
				Log.w(DEBUG_TAG, "尚未给“" + clickBtn.getText() + "”添加动作");
				break;
		}
	}

	private void getTubePadData(long tubePadId)
	{
		try {
			mTubeViewService.getTubePadInfo(tubePadId);
		} catch (Throwable e) {
			Log.w(DEBUG_TAG, "获取管块信息失败，请调试排查原因");
			Toast.makeText(this, R.string.tubeview_toast_fail_to_get_tubepad_data, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onHoleSelected(Hole hole)
	{
		if (!mIsDataGridShowed) {
			if (isShowedAnimation) {
				showDataGridWithAnimation();
			} else {
				showDataGrid();
			}

			mIsDataGridShowed = true;
		}
		mDataGrid.setSelectedRow(((Hole) hole).indexInTube + 1);
		/*HoleInDataGrid[] holes = ((TubeViewAdapter) mDataGrid.getAdapter()).getHoles();
		int holeCount = holes.length;
		for (int i = 0; i < holeCount; ++i) {
			if (hole.id == holes[i].id) {
				mDataGrid.setSelectedRow(i);
				break;
			}
		}*/
	}

	@Override
	public void onHoleUnselected()
	{
		if (mIsDataGridShowed) {
			if (mIsDataGridAutoHide) {
				if (isShowedAnimation) {
					hideDataGridWithAnimation();
				} else {
					hideDataGrid();
				}
				mIsDataGridShowed = false;
			}
		}
		mDataGrid.clearHighlightSelectedRow();
	}

	@Override
	public void onRowSeleted(long selectedObjectId)
	{
		mDisplayView.getTubeA().clearSelectedFlag();
		mDisplayView.getTubeA().setObjectIsSelected(selectedObjectId);
		mDisplayView.getTubeZ().clearSelectedFlag();
		mDisplayView.getTubeZ().setObjectIsSelected(selectedObjectId);
		mDisplayView.postInvalidate();
	}

	@Override
	public void onRowUnselected()
	{
		mDisplayView.getTubeA().clearSelectedFlag();
		mDisplayView.getTubeZ().clearSelectedFlag();
		mDisplayView.postInvalidate();
	}

	private void hideDataGridWithAnimation()
	{
		activateAnimation(false);
	}

	private void showDataGridWithAnimation()
	{
		activateAnimation(true);
	}

	/**
	 * 激活显示/隐藏表格的动画
	 * @param showDataGrid 激活显示表格的动画则设置 true，激活隐藏表格的动画则设置 false;
	 */
	private void activateAnimation(final boolean showDataGrid)
	{
		//表格的动画
		float alphaFrom = showDataGrid ? 0.0f : 1.0f;
		float alphaTo = showDataGrid ? 1.0f : 0.6f;
		AlphaAnimation dataGridAlphaAnimation = new AlphaAnimation(alphaFrom, alphaTo);

		float dataGridScaleFrom = showDataGrid ? 0.0f : 1.0f;
		float dataGridScaleTo = showDataGrid ? 1.0f : 0.0f;
		ScaleAnimation dataGirdTranslateAnimation = new ScaleAnimation(dataGridScaleFrom, dataGridScaleTo, 1, 1);
		dataGirdTranslateAnimation.setDuration(mAnimationDuration);
		dataGirdTranslateAnimation.setFillBefore(false);
		TubeViewAnimationListener listenerAdapter = new TubeViewAnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
				if (showDataGrid) {
					showDataGrid();
				}
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				if (!showDataGrid) {
					hideDataGrid();
				}
			}
		};
		dataGirdTranslateAnimation.setAnimationListener((AnimationListener) listenerAdapter);

		AnimationSet dataGridAnimationSet = new AnimationSet(true);
		dataGridAnimationSet.addAnimation(dataGridAlphaAnimation);
		dataGridAnimationSet.addAnimation(dataGirdTranslateAnimation);
		mDataGridContainer.startAnimation(dataGridAnimationSet);

		//管道截面展开图的动画
		if (!showDataGrid) {
			float tubeViewScaleFrom = 1.0f;
			float tubeViewScaleTo = 1.6f;
			ScaleAnimation tubeViewScaleAnimation = new ScaleAnimation(tubeViewScaleFrom, tubeViewScaleTo, 1, 1);

			float fromX = 0;
			float toX = - mDisplayView.getLeft() + DensityUtil.dip2px(this, ACTIVITY_HORIZONTAL_MARGIN);
			TranslateAnimation tubeViewTranslateAnimation = new TranslateAnimation(fromX, toX, 0, 0);
			tubeViewTranslateAnimation.setDuration(mAnimationDuration);
			tubeViewTranslateAnimation.setFillBefore(false);

			AnimationSet tubeViewAnimationSet = new AnimationSet(true);
			tubeViewAnimationSet.addAnimation(tubeViewScaleAnimation);
			tubeViewAnimationSet.addAnimation(tubeViewTranslateAnimation);
			mDisplayView.startAnimation(tubeViewAnimationSet);
		}
	}

	private void hideDataGrid()
	{
		LinearLayout.LayoutParams viewLinearParams = (LinearLayout.LayoutParams) mDisplayView.getLayoutParams();
		viewLinearParams.leftMargin = 0;

		LinearLayout.LayoutParams girdLinearParams = (LinearLayout.LayoutParams) mDataGridContainer.getLayoutParams();
		girdLinearParams.weight = DATAGRID_HIDE;
		mDataGridContainer.setLayoutParams(girdLinearParams);
	}

	private void showDataGrid()
	{
		LinearLayout.LayoutParams viewLinearParams = (LinearLayout.LayoutParams) mDisplayView.getLayoutParams();
		viewLinearParams.leftMargin = DensityUtil.dip2px(this, ACTIVITY_HORIZONTAL_MARGIN);

		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mDataGridContainer.getLayoutParams();
		linearParams.weight = DATAGRID_SHOW;
		mDataGridContainer.setLayoutParams(linearParams);
	}

	private void onAutoHideTgbtnClicked()
	{
		if (mAutoHideTgbtn.isChecked()) {
			mIsDataGridAutoHide = true;
		} else {
			mIsDataGridAutoHide = false;
		}
	}

	@Override
	public void onTubeViewDataGot(TubePadInXml tubePadData,
								  List<HoleInXml> holesData)
	{
		TubePad tubePad = TubePadUtil.tubePadInXmlToTubePad(tubePadData, this);

		int holeCount = holesData.size();
		Hole[] holes = new Hole[holeCount];
		for (int i = 0; i < holeCount; ++i) {
			holes[i] = HoleUtil.holeInXmlToHole(holesData.get(i), this);
		}
		tubePad.setHoles(holes);

		TubeViewAdapter adapter = new TubeViewAdapter(this);
		adapter.setSourceData(tubePad);

		mDataGrid.setAdapter(adapter);

		mDisplayView.setTubeA(tubePad);
		mDisplayView.setTubeZ(tubePad.getMirroredTubePad());
		mDisplayView.postInvalidate();
	}

	private abstract class TubeViewAnimationListener implements AnimationListener
	{
		@Override
		public void onAnimationRepeat(Animation animation){}
	}

}
