package com.eruntech.crosssectionview.adapters;

import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eruntech.crosssectionview.drawobjects.TubePad;
import com.eruntech.crosssectionview.utils.HoleUtil;
import com.eruntech.crosssectionview.valueobjects.HoleInDataGrid;
import com.eruntech.crosssectionview.widgets.datagrid.libs.adapters.BaseTableAdapter;
import com.eruntech.crosssectionview.widgets.datagrid.libs.adapters.TableAdapter;
import com.eruntech.crosssectionview.widgets.datagrid.libs.TableFixHeaders;
import com.qingdao.shiqu.arcgis.R;


/**
 * 完全实现 {@link TableAdapter} 接口的类，本类用于当 {@link TableFixHeaders} 表格的适配器(Adapter)
 * 专用于管道截面展开图下的表格
 * @author 覃远逸
 */
public class TubeViewAdapter extends BaseTableAdapter
{
	private final String DEBUG_TAG = "debug " + this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private final boolean DEBUG = false;

	public final int VIEW_TYPE_COUNT = 2;
	public final int VIEW_TYPE_HEADER = 0;
	public final int VIEW_TYPE_ITEM = 1;

	private HoleInDataGrid[] holes;
	private final Context context;
	private final LayoutInflater inflater;
	private final int width;
	private final int height;

	public TubeViewAdapter(Context context)
	{
		this.context = context;
		inflater = LayoutInflater.from(context);

		Resources resources = context.getResources();

		width = resources.getDimensionPixelSize(R.dimen.datagrid_table_width);
		height = resources.getDimensionPixelSize(R.dimen.datagrid_table_height);
	}

	public HoleInDataGrid[] getHoles()
	{
		return holes;
	}

	@Override
	public int getRowCount()
	{
		// 上下滑动时表头是固定不动的，所以需要减掉表头，故而少一行
		return holes.length - 1;
	}

	@Override
	public int getColumnCount()
	{
		// 左右滑动时每行第一列是固定不动的，所以需要减掉，故而少一列
		return holes[0].attribute.length - 1;
	}

	@Override
	public View getView(int row, int column, View convertView, ViewGroup parent)
	{
		if (convertView == null) {
			convertView = inflater.inflate(getLayoutResource(row, column), parent, false);
		}
		if (row < getRowCount() && column < getColumnCount()) {
			setText(convertView, holes[row+1].attribute[column+1]);
		}
		return convertView;
	}

	@Override
	public int getWidth(int column)
	{
		return width;
	}

	@Override
	public int getHeight(int row)
	{
		return height;
	}

	@Override
	public int getItemViewType(int row, int column)
	{
		if (row < 0) {
			return VIEW_TYPE_HEADER;
		} else {
			return VIEW_TYPE_ITEM;
		}
	}

	@Override
	public int getViewTypeCount()
	{
		return VIEW_TYPE_COUNT;
	}

	public int getLayoutResource(int row, int column) {
		final int layoutResource;
		switch (getItemViewType(row, column)) {
			case VIEW_TYPE_HEADER:
				layoutResource = R.layout.datagrid_item_table_header;
				break;
			case VIEW_TYPE_ITEM:
				layoutResource = R.layout.datagrid_item_table;
				break;
			default:
				throw new RuntimeException("u are kidding me!");
		}
		return layoutResource;
	}

	public int getBackgroundResource(int row, int column) {
		final int backgroundResource;
		switch (getItemViewType(row, column)) {
			case VIEW_TYPE_HEADER:
				backgroundResource = R.drawable.datagrid_item_table_header;
				break;
			case VIEW_TYPE_ITEM:
				backgroundResource = R.drawable.datagrid_item_table;
				break;
			default:
				throw new RuntimeException("u are kidding me!");
		}
		return backgroundResource;
	}

	public Context getContext()
	{
		return context;
	}

	public LayoutInflater getInflater()
	{
		return inflater;
	}

	public void setSourceData(TubePad tubePad)
	{
		int holeCount = tubePad.getHoleNum();

		if (holeCount > 0) {
			HoleInDataGrid[] holes = new HoleInDataGrid[holeCount];
			for (int i = 0; i < holeCount; ++i) {
				holes[i] = HoleUtil.holeToHoleInDataGrid(tubePad.getHoles()[i]);
			}

			setSourceData(holes);
		} else {
			Log.w(DEBUG_TAG, "管块下没有管孔数据，请核对数据是否正确");
		}
	}

	public void setSourceData(HoleInDataGrid[] sourceData)
	{
		int countOfSourceData = sourceData.length + 1;
		holes = new HoleInDataGrid[countOfSourceData];
		holes[0] = holes[0] = new HoleInDataGrid("编号", "产权", "管孔状态", "管孔材质");
		for (int i = 1; i<countOfSourceData; ++i) {
			holes[i] = sourceData[i-1];
		}
		Arrays.sort(holes, new HoleComparator());
	}

	/**
	 * 管孔排序比较器，对管孔的编号进行比较，行序号越小的越往前，相同行序号时列序号越小的越往前
	 */
	private class HoleComparator implements Comparator<HoleInDataGrid>
	{

		@Override
		public int compare(HoleInDataGrid lhs, HoleInDataGrid rhs)
		{
			if (lhs.getRowIndex() == rhs.getRowIndex()) {
				if (lhs.getColumnIndex() > rhs.getColumnIndex()) {
					return 1;
				} else if (lhs.getColumnIndex() < rhs.getColumnIndex()) {
					return -1;
				} else {
					throw new RuntimeException("出现相同编号（行列序号）的管孔！");
				}
			} else {
				if(lhs.getRowIndex() > rhs.getRowIndex()) {
					return 1;
				} else {
					return -1;
				}
			}
		}

	}

	private void setText(View view, String text)
	{
		((TextView)view.findViewById(R.id.datagrid_item_text)).setText(text);
	}
}
