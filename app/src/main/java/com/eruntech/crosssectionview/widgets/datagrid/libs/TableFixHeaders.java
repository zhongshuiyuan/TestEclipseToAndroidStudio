package com.eruntech.crosssectionview.widgets.datagrid.libs;

import java.util.ArrayList;
import java.util.List;

import com.eruntech.crosssectionview.adapters.TubeViewAdapter;
import com.eruntech.crosssectionview.interfaces.OnDataGridRowSelectedListener;
import com.eruntech.crosssectionview.widgets.datagrid.libs.adapters.TableAdapter;
import com.qingdao.shiqu.arcgis.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.Scroller;

/**
 * This view shows a table which can scroll in both directions. Also still
 * leaves the headers fixed.
 * 
 * @author Brais Gabín (InQBarna), 覃远逸
 */
public class TableFixHeaders extends ViewGroup {
	//@SuppressWarnings("unused")
	private final String DEBUG_TAG = "debug " + this.getClass().getSimpleName();
	//@SuppressWarnings("unused")
	private boolean DEBUG = true;
	
	private int currentX;
	private int currentY;
	
	private int velocityX;
	private int velocityY;

	private TableAdapter adapter;
	private int scrollX;
	private int scrollY;
	private int firstRow;
	private int firstColumn;
	private int[] widths;
	private int[] heights;

	@SuppressWarnings("unused")
	private View headView;
	private List<View> rowViewList;
	private List<View> columnViewList;
	private List<List<View>> bodyViewTable;

	private int rowCount;
	private int columnCount;

	private int width;
	private int height;

	private Recycler recycler;

	private TableAdapterDataSetObserver tableAdapterDataSetObserver;
	private boolean needRelayout;

	private final ImageView[] shadows;
	private final int shadowSize;

	private final int minimumVelocity;
	private final int maximumVelocity;

	private final Flinger flinger;

	private VelocityTracker velocityTracker;

	private int touchSlop;
	
	/** set true if u want to select a row **/
	private boolean rowSelectable;
	private int lastSelectedRowIndex;
	private final int NO_ROW_SELECTED = -2;
	private int touchRowIndex;
	private float motionDownX;
	private float motionDownY;
	private final int THRESHOLD_VALUE_CLICK = 2;
	private OnDataGridRowSelectedListener mRowSelectedListener;

	/**
	 * Simple constructor to use when creating a view from code.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 */
	public TableFixHeaders(Context context) {
		this(context, null);
	}

	/**
	 * Constructor that is called when inflating a view from XML. This is called
	 * when a view is being constructed from an XML file, supplying attributes
	 * that were specified in the XML file. This version uses a default style of
	 * 0, so the only attribute values applied are those in the Context's Theme
	 * and the given AttributeSet.
	 * 
	 * The method onFinishInflate() will be called after all children have been
	 * added.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view.
	 */
	public TableFixHeaders(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.headView = null;
		this.rowViewList = new ArrayList<View>();
		this.columnViewList = new ArrayList<View>();
		this.bodyViewTable = new ArrayList<List<View>>();

		this.needRelayout = true;

		this.shadows = new ImageView[4];
		this.shadows[0] = new ImageView(context);
		this.shadows[0].setImageResource(R.drawable.datagrid_shadow_left);
		this.shadows[1] = new ImageView(context);
		this.shadows[1].setImageResource(R.drawable.datagrid_shadow_top);
		this.shadows[2] = new ImageView(context);
		this.shadows[2].setImageResource(R.drawable.datagrid_shadow_right);
		this.shadows[3] = new ImageView(context);
		this.shadows[3].setImageResource(R.drawable.datagrid_shadow_bottom);

		this.shadowSize = getResources().getDimensionPixelSize(R.dimen.datagrid_shadow_size);

		this.flinger = new Flinger(context);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		this.touchSlop = configuration.getScaledTouchSlop();
		this.minimumVelocity = configuration.getScaledMinimumFlingVelocity();
		this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
		
		this.setWillNotDraw(false);
		
		final TypedArray a = context.obtainStyledAttributes(R.styleable.View);
		try {
			// TODO fix initializeScrollbars
			//initializeScrollbars(a);
		} finally {
			if (a != null) {
				a.recycle();
			}
		}

		this.setHorizontalScrollBarEnabled(true);
		this.setVerticalScrollBarEnabled(true);
		
		this.rowSelectable = true;
		if (context instanceof OnDataGridRowSelectedListener) {
			mRowSelectedListener = (OnDataGridRowSelectedListener) context;
		}
		
	}

	/**
	 * Returns the adapter currently associated with this widget.
	 * 
	 * @return The adapter used to provide this view's content.
	 */
	public TableAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Sets the data behind this TableFixHeaders.
	 * 
	 * @param adapter
	 *            The TableAdapter which is responsible for maintaining the data
	 *            backing this list and for producing a view to represent an
	 *            item in that data set.
	 */
	public void setAdapter(TableAdapter adapter) {
		if (this.adapter != null) {
			this.adapter.unregisterDataSetObserver(tableAdapterDataSetObserver);
		}

		this.adapter = adapter;
		tableAdapterDataSetObserver = new TableAdapterDataSetObserver();
		this.adapter.registerDataSetObserver(tableAdapterDataSetObserver);

		this.recycler = new Recycler(adapter.getViewTypeCount());

		scrollX = 0;
		scrollY = 0;
		firstColumn = 0;
		firstRow = 0;

		needRelayout = true;
		requestLayout();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		
		
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		boolean intercept = false;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				currentX = (int) event.getRawX();
				currentY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				int x2 = Math.abs(currentX - (int) event.getRawX());
				int y2 = Math.abs(currentY - (int) event.getRawY());
				if (x2 > touchSlop || y2 > touchSlop) {
					intercept = true;
				}
				break;
			}
		}
		return intercept;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (velocityTracker == null) { // If we do not have velocity tracker
			velocityTracker = VelocityTracker.obtain(); // then get one
		}
		velocityTracker.addMovement(event); // add this movement to it

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				// used to judge is click action or not
				motionDownX = getActualMotionX(event);
				motionDownY = getActualMotionY(event);
				
				if (!flinger.isFinished()) { // If scrolling, then stop now
					flinger.forceFinished();
				}
				currentX = (int) event.getRawX();
				currentY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				final int x2 = (int) event.getRawX();
				final int y2 = (int) event.getRawY();
				final int diffX = currentX - x2;
				final int diffY = currentY - y2;
				currentX = x2;
				currentY = y2;
				
				final VelocityTracker velocityTracker = this.velocityTracker;
				velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
				velocityX = (int) velocityTracker.getXVelocity();
				velocityY = (int) velocityTracker.getYVelocity();

				scrollBy(diffX, diffY);
				break;
			}
			case MotionEvent.ACTION_UP: {
				//Click action
				float motionUpX = getActualMotionX(event);
				float motionUpY = getActualMotionY(event);
				if (Math.abs(motionUpX - motionDownX) < THRESHOLD_VALUE_CLICK && Math.abs(motionUpY - motionDownY) < THRESHOLD_VALUE_CLICK) {
					// handle highlight selected row
					if (rowSelectable) {
						touchRowIndex = getTouchRowIndex();
						if (touchRowIndex == lastSelectedRowIndex) {
							clearHighlightSelectedRow();
							mRowSelectedListener.onRowUnselected();
						} else {
							highlightRow(touchRowIndex);
							if (touchRowIndex == 0) {
								mRowSelectedListener.onRowUnselected();
							} else if (touchRowIndex > 0 && touchRowIndex <= adapter.getRowCount()) {
								mRowSelectedListener.onRowSeleted(((TubeViewAdapter) adapter).getHoles()[touchRowIndex].id);
							}
							
						}
						
						if (DEBUG) {
							Log.d(DEBUG_TAG, "Click action");
							Log.d(DEBUG_TAG, "upX - donwX = " + (motionUpX - motionDownX));
							Log.d(DEBUG_TAG, "upX - donwX = " + (motionUpY - motionDownY));
							Log.d(DEBUG_TAG, "touchRowIndex: " + touchRowIndex);
						}	
					}
				}
				
				if (Math.abs(velocityX) > minimumVelocity || Math.abs(velocityY) > minimumVelocity) {
					flinger.start(getActualScrollX(), getActualScrollY(), velocityX, velocityY, getMaxScrollX(), getMaxScrollY());
				} else {
					if (this.velocityTracker != null) { // If the velocity less than threshold
						this.velocityTracker.recycle(); // recycle the tracker
						this.velocityTracker = null;
					}
				}
				velocityX = 0;
				velocityY = 0;
				break;
			}
		}
		return true;
	}

	@Override
	public void scrollTo(int x, int y) {
		if (needRelayout) {
			scrollX = x;
			firstColumn = 0;

			scrollY = y;
			firstRow = 0;
		} else {
			scrollBy(x - sumArray(widths, 1, firstColumn) - scrollX, y - sumArray(heights, 1, firstRow) - scrollY);
		}
	}

	@Override
	public void scrollBy(int x, int y) {
		scrollX += x;
		scrollY += y;

		if (needRelayout) {
			return;
		}

		scrollBounds();

		/*
		 * TODO Improve the algorithm. Think big diagonal movements. If we are
		 * in the top left corner and scrollBy to the opposite corner. We will
		 * have created the views from the top right corner on the X part and we
		 * will have eliminated to generate the right at the Y.
		 */
		if (scrollX == 0) {
			// no op
		} else if (scrollX > 0) {
			while (widths[firstColumn + 1] < scrollX) {
				if (!rowViewList.isEmpty()) {
					removeLeft();
				}
				scrollX -= widths[firstColumn + 1];
				firstColumn++;
			}
			while (getFilledWidth() < width) {
				addRight();
			}
		} else {
			while (!rowViewList.isEmpty() && getFilledWidth() - widths[firstColumn + rowViewList.size()] >= width) {
				removeRight();
			}
			if (rowViewList.isEmpty()) {
				while (scrollX < 0) {
					firstColumn--;
					scrollX += widths[firstColumn + 1];
				}
				while (getFilledWidth() < width) {
					addRight();
				}
			} else {
				while (0 > scrollX) {
					addLeft();
					firstColumn--;
					scrollX += widths[firstColumn + 1];
				}
			}
		}

		if (scrollY == 0) {
			// no op
		} else if (scrollY > 0) {
			while (heights[firstRow + 1] < scrollY) {
				if (!columnViewList.isEmpty()) {
					removeTop();
				}
				scrollY -= heights[firstRow + 1];
				firstRow++;
			}
			while (getFilledHeight() < height) {
				addBottom();
			}
		} else {
			while (!columnViewList.isEmpty() && getFilledHeight() - heights[firstRow + columnViewList.size()] >= height) {
				removeBottom();
			}
			if (columnViewList.isEmpty()) {
				while (scrollY < 0) {
					firstRow--;
					scrollY += heights[firstRow + 1];
				}
				while (getFilledHeight() < height) {
					addBottom();
				}
			} else {
				while (0 > scrollY) {
					addTop();
					firstRow--;
					scrollY += heights[firstRow + 1];
				}
			}
		}

		repositionViews();

		shadowsVisibility();
		
		awakenScrollBars();
	}
	
	/*
	 * The expected value is: percentageOfViewScrolled * computeHorizontalScrollRange()
	 */
	@Override
	protected int computeHorizontalScrollExtent() {
		final float tableSize = width - widths[0];
		final float contentSize = sumArray(widths) - widths[0];
		final float percentageOfVisibleView = tableSize / contentSize;

		return Math.round(percentageOfVisibleView * tableSize);
	}

	/*
	 * The expected value is between 0 and computeHorizontalScrollRange() - computeHorizontalScrollExtent()
	 */
	@Override
	protected int computeHorizontalScrollOffset() {
		final float maxScrollX = sumArray(widths) - width;
		final float percentageOfViewScrolled = getActualScrollX() / maxScrollX;
		final int maxHorizontalScrollOffset = width - widths[0] - computeHorizontalScrollExtent();

		return widths[0] + Math.round(percentageOfViewScrolled * maxHorizontalScrollOffset);
	}

	/*
	 * The base measure
	 */
	@Override
	protected int computeHorizontalScrollRange() {
		return width;
	}

	/*
	 * The expected value is: percentageOfViewScrolled * computeVerticalScrollRange()
	 */
	@Override
	protected int computeVerticalScrollExtent() {
		final float tableSize = height - heights[0];
		final float contentSize = sumArray(heights) - heights[0];
		final float percentageOfVisibleView = tableSize / contentSize;
		return Math.round(percentageOfVisibleView * tableSize);
	}

	/*
	 * The expected value is between 0 and computeVerticalScrollRange() - computeVerticalScrollExtent()
	 */
	@Override
	protected int computeVerticalScrollOffset() {
		final float maxScrollY = sumArray(heights) - height;
		final float percentageOfViewScrolled = getActualScrollY() / maxScrollY;
		final int maxHorizontalScrollOffset = height - heights[0] - computeVerticalScrollExtent();

		return heights[0] + Math.round(percentageOfViewScrolled * maxHorizontalScrollOffset);
	}

	/*
	 * The base measure
	 */
	@Override
	protected int computeVerticalScrollRange() {
		return height;
	}

	public int getActualScrollX() {
		return scrollX + sumArray(widths, 1, firstColumn);
	}

	public int getActualScrollY() {
		return scrollY + sumArray(heights, 1, firstRow);
	}

	private int getMaxScrollX() {
		return Math.max(0, sumArray(widths) - width);
	}

	private int getMaxScrollY() {
		return Math.max(0, sumArray(heights) - height);
	}

	private int getFilledWidth() {
		return widths[0] + sumArray(widths, firstColumn + 1, rowViewList.size()) - scrollX;
	}

	private int getFilledHeight() {
		return heights[0] + sumArray(heights, firstRow + 1, columnViewList.size()) - scrollY;
	}

	private void addLeft() {
		addLeftOrRight(firstColumn - 1, 0);
	}

	private void addTop() {
		addTopAndBottom(firstRow - 1, 0);
	}

	private void addRight() {
		final int size = rowViewList.size();
		addLeftOrRight(firstColumn + size, size);
	}

	private void addBottom() {
		final int size = columnViewList.size();
		addTopAndBottom(firstRow + size, size);
	}

	private void addLeftOrRight(int column, int index) {
		View view = makeView(-1, column, widths[column + 1], heights[0]);
		rowViewList.add(index, view);

		int i = firstRow;
		for (List<View> list : bodyViewTable) {
			view = makeView(i, column, widths[column + 1], heights[i + 1]);
			list.add(index, view);
			i++;
		}
	}

	private void addTopAndBottom(int row, int index) {
		View view = makeView(row, -1, widths[0], heights[row + 1]);
		columnViewList.add(index, view);

		List<View> list = new ArrayList<View>();
		final int size = rowViewList.size() + firstColumn;
		for (int i = firstColumn; i < size; i++) {
			view = makeView(row, i, widths[i + 1], heights[row + 1]);
			list.add(view);
		}
		bodyViewTable.add(index, list);
	}

	private void removeLeft() {
		removeLeftOrRight(0);
	}

	private void removeTop() {
		removeTopOrBottom(0);
	}

	private void removeRight() {
		removeLeftOrRight(rowViewList.size() - 1);
	}

	private void removeBottom() {
		removeTopOrBottom(columnViewList.size() - 1);
	}

	private void removeLeftOrRight(int position) {
		removeView(rowViewList.remove(position));
		for (List<View> list : bodyViewTable) {
			removeView(list.remove(position));
		}
	}

	private void removeTopOrBottom(int position) {
		removeView(columnViewList.remove(position));
		List<View> remove = bodyViewTable.remove(position);
		for (View view : remove) {
			removeView(view);
		}
	}

	@Override
	public void removeView(View view) {
		super.removeView(view);
		
		if (view.getTag(R.id.datagrid_tag_row) != null) {
			int row = (Integer)view.getTag(R.id.datagrid_tag_row);
			int column = (Integer)view.getTag(R.id.datagrid_tag_column);
			view.setBackgroundResource(adapter.getBackgroundResource(row, column));
		}
		
		final int typeView = (Integer) view.getTag(R.id.datagrid_tag_type_view);
		if (typeView != TableAdapter.IGNORE_ITEM_VIEW_TYPE) {
			recycler.addRecycledView(view, typeView);
		}
	}

	private void repositionViews() {
		int left, top, right, bottom, i;

		left = widths[0] - scrollX;
		i = firstColumn;
		for (View view : rowViewList) {
			right = left + widths[++i];
			view.layout(left, 0, right, heights[0]);
			left = right;
		}

		top = heights[0] - scrollY;
		i = firstRow;
		for (View view : columnViewList) {
			bottom = top + heights[++i];
			view.layout(0, top, widths[0], bottom);
			top = bottom;
		}

		top = heights[0] - scrollY;
		i = firstRow;
		for (List<View> list : bodyViewTable) {
			bottom = top + heights[++i];
			left = widths[0] - scrollX;
			int j = firstColumn;
			for (View view : list) {
				right = left + widths[++j];
				view.layout(left, top, right, bottom);
				left = right;
			}
			top = bottom;
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		final int w;
		final int h;

		if (adapter != null) {
			this.rowCount = adapter.getRowCount();
			this.columnCount = adapter.getColumnCount();

			widths = new int[columnCount + 1];
			for (int i = -1; i < columnCount; i++) {
				widths[i + 1] += adapter.getWidth(i);
			}
			heights = new int[rowCount + 1];
			for (int i = -1; i < rowCount; i++) {
				heights[i + 1] += adapter.getHeight(i);
			}

			if (widthMode == MeasureSpec.AT_MOST) {
				w = Math.min(widthSize, sumArray(widths));
			} else if (widthMode == MeasureSpec.UNSPECIFIED) {
				w = sumArray(widths);
			} else {
				w = widthSize;
				int sumArray = sumArray(widths);
				if (sumArray < widthSize) {
					final float factor = widthSize / (float) sumArray;
					for (int i = 1; i < widths.length; i++) {
						widths[i] = Math.round(widths[i] * factor);
					}
					widths[0] = widthSize - sumArray(widths, 1, widths.length - 1);
				}
			}

			if (heightMode == MeasureSpec.AT_MOST) {
				h = Math.min(heightSize, sumArray(heights));
			} else if (heightMode == MeasureSpec.UNSPECIFIED) {
				h = sumArray(heights);
			} else {
				h = heightSize;
			}
		} else {
			if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
				w = 0;
				h = 0;
			} else {
				w = widthSize;
				h = heightSize;
			}
		}

		if (firstRow >= rowCount || getMaxScrollY() - getActualScrollY() < 0) {
			firstRow = 0;
			scrollY = Integer.MAX_VALUE;
		}
		if (firstColumn >= columnCount || getMaxScrollX() - getActualScrollX() < 0) {
			firstColumn = 0;
			scrollX = Integer.MAX_VALUE;
		}

		setMeasuredDimension(w, h);
	}

	private int sumArray(int array[]) {
		return sumArray(array, 0, array.length);
	}

	private int sumArray(int array[], int firstIndex, int count) {
		int sum = 0;
		count += firstIndex;
		for (int i = firstIndex; i < count; i++) {
			sum += array[i];
		}
		return sum;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (needRelayout || changed) {
			needRelayout = false;
			resetTable();

			if (adapter != null) {
				width = r - l;
				height = b - t;

				int left, top, right, bottom;

				right = Math.min(width, sumArray(widths));
				bottom = Math.min(height, sumArray(heights));
				addShadow(shadows[0], widths[0], 0, widths[0] + shadowSize, bottom);
				addShadow(shadows[1], 0, heights[0], right, heights[0] + shadowSize);
				addShadow(shadows[2], right - shadowSize, 0, right, bottom);
				addShadow(shadows[3], 0, bottom - shadowSize, right, bottom);

				headView = makeAndSetup(-1, -1, 0, 0, widths[0], heights[0]);

				scrollBounds();
				adjustFirstCellsAndScroll();

				left = widths[0] - scrollX;
				for (int i = firstColumn; i < columnCount && left < width; i++) {
					right = left + widths[i + 1];
					final View view = makeAndSetup(-1, i, left, 0, right, heights[0]);
					rowViewList.add(view);
					left = right;
				}

				top = heights[0] - scrollY;
				for (int i = firstRow; i < rowCount && top < height; i++) {
					bottom = top + heights[i + 1];
					final View view = makeAndSetup(i, -1, 0, top, widths[0], bottom);
					columnViewList.add(view);
					top = bottom;
				}

				top = heights[0] - scrollY;
				for (int i = firstRow; i < rowCount && top < height; i++) {
					bottom = top + heights[i + 1];
					left = widths[0] - scrollX;
					List<View> list = new ArrayList<View>();
					for (int j = firstColumn; j < columnCount && left < width; j++) {
						right = left + widths[j + 1];
						final View view = makeAndSetup(i, j, left, top, right, bottom);
						list.add(view);
						left = right;
					}
					bodyViewTable.add(list);
					top = bottom;
				}

				shadowsVisibility();
				
			}
		}
		
		highlightRow(lastSelectedRowIndex);
			
	}

	private void scrollBounds() {
		scrollX = scrollBounds(scrollX, firstColumn, widths, width);
		scrollY = scrollBounds(scrollY, firstRow, heights, height);
	}

	private int scrollBounds(int desiredScroll, int firstCell, int sizes[], int viewSize) {
		if (desiredScroll == 0) {
			// no op
		} else if (desiredScroll < 0) {
			desiredScroll = Math.max(desiredScroll, -sumArray(sizes, 1, firstCell));
		} else {
			desiredScroll = Math.min(desiredScroll, Math.max(0, sumArray(sizes, firstCell + 1, sizes.length - 1 - firstCell) + sizes[0] - viewSize));
		}
		return desiredScroll;
	}

	private void adjustFirstCellsAndScroll() {
		int values[];

		values = adjustFirstCellsAndScroll(scrollX, firstColumn, widths);
		scrollX = values[0];
		firstColumn = values[1];

		values = adjustFirstCellsAndScroll(scrollY, firstRow, heights);
		scrollY = values[0];
		firstRow = values[1];
	}

	private int[] adjustFirstCellsAndScroll(int scroll, int firstCell, int sizes[]) {
		if (scroll == 0) {
			// no op
		} else if (scroll > 0) {
			while (sizes[firstCell + 1] < scroll) {
				firstCell++;
				scroll -= sizes[firstCell];
			}
		} else {
			while (scroll < 0) {
				scroll += sizes[firstCell];
				firstCell--;
			}
		}
		return new int[] { scroll, firstCell };
	}

	private void shadowsVisibility() {
		final int actualScrollX = getActualScrollX();
		final int actualScrollY = getActualScrollY();
		final int[] remainPixels = {
				actualScrollX,
				actualScrollY,
				getMaxScrollX() - actualScrollX,
				getMaxScrollY() - actualScrollY,
		};

		for (int i = 0; i < shadows.length; i++) {
			setAlpha(shadows[i], Math.min(remainPixels[i] / (float) shadowSize, 1));
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private void setAlpha(ImageView imageView, float alpha) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			imageView.setAlpha(alpha);
		} else {
			imageView.setAlpha(Math.round(alpha * 255));
		}
	}

	private void addShadow(ImageView imageView, int l, int t, int r, int b) {
		imageView.layout(l, t, r, b);
		addView(imageView);
	}

	private void resetTable() {
		headView = null;
		rowViewList.clear();
		columnViewList.clear();
		bodyViewTable.clear();

		removeAllViews();
	}

	private View makeAndSetup(int row, int column, int left, int top, int right, int bottom) {
		final View view = makeView(row, column, right - left, bottom - top);
		view.layout(left, top, right, bottom);
		return view;
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		final boolean ret;

		final Integer row = (Integer) child.getTag(R.id.datagrid_tag_row);
		final Integer column = (Integer) child.getTag(R.id.datagrid_tag_column);
		// row == null => Shadow view
		if (row == null || (row == -1 && column == -1)) {
			ret = super.drawChild(canvas, child, drawingTime);
		} else {
			canvas.save();
			if (row == -1) {
				canvas.clipRect(widths[0], 0, canvas.getWidth(), canvas.getHeight());
			} else if (column == -1) {
				canvas.clipRect(0, heights[0], canvas.getWidth(), canvas.getHeight());
			} else {
				canvas.clipRect(widths[0], heights[0], canvas.getWidth(), canvas.getHeight());
			}

			ret = super.drawChild(canvas, child, drawingTime);
			canvas.restore();
		}
		return ret;
	}

	private View makeView(int row, int column, int w, int h) {
		final int itemViewType = adapter.getItemViewType(row, column);
		final View recycledView;
		if (itemViewType == TableAdapter.IGNORE_ITEM_VIEW_TYPE) {
			recycledView = null;
		} else {
			recycledView = recycler.getRecycledView(itemViewType);
		}
		final View view = adapter.getView(row, column, recycledView, this);
		view.setTag(R.id.datagrid_tag_type_view, itemViewType);
		view.setTag(R.id.datagrid_tag_row, row);
		view.setTag(R.id.datagrid_tag_column, column);
		view.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
		addTableView(view, row, column);
		return view;
	}

	private void addTableView(View view, int row, int column) {
		if (row == -1 && column == -1) {
			addView(view, getChildCount() - 4);
		} else if (row == -1 || column == -1) {
			addView(view, getChildCount() - 5);
		} else {
			addView(view, 0);
		}
	}
	
	private float getActualMotionX(MotionEvent event)
	{
		return event.getX() + getActualScrollX();
	}
	
	private float getActualMotionY(MotionEvent event)
	{
		return event.getY() + getActualScrollY();
	}
	
	public void setRowSelectable(boolean rowSelectable)
	{
		this.rowSelectable = rowSelectable;
	}
	
	private int getTouchRowIndex()
	{
		int rowIndex = 0;
		int heightSum = 0;
		for (int i=0; i < this.heights.length; i++) {
			heightSum += heights[i];
			if (motionDownY - heightSum > 0) {
				rowIndex++;
			}
		}
		if (DEBUG) {
			Log.d("debug", "touchRowIndex: " + rowIndex);
		}
		return rowIndex;
	}
	
	private void highlightRow(int row)
	{
		if (row == 0) {
			clearHighlightSelectedRow();
			return;
		}
		lastSelectedRowIndex = row;
		
		int childCount = this.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			View view = getChildAt(i);
			if (view.findViewById(R.id.datagrid_item_text) != null) {
				int rowIndex = (Integer)view.getTag(R.id.datagrid_tag_row);
				int columnIndex = (Integer)view.getTag(R.id.datagrid_tag_column);
				//DataGrid�ĵ�1�е�������-1����1�е�����Ҳ��-1
				if (rowIndex == row - 1 && columnIndex == -1) {
					view.setBackgroundResource(R.drawable.datagrid_highlight_rect);
				} else {
					view.setBackgroundResource(adapter.getBackgroundResource(rowIndex, columnIndex));
				}
			}
		}
	}
	
	/**
	 * ����ĳһ��Ϊѡ�и���״̬
	 * @param selectedRow ������ʾ���е���ţ���ͷ�ǵ�0��
	 */
	public void setSelectedRow(int selectedRow)
	{
		if (selectedRow <= 0 || selectedRow > adapter.getRowCount()) {
			return;
		}
		
		int scrollToY = sumArray(heights, 1, selectedRow) - heights[selectedRow] - shadowSize;
		scrollToY = scrollToY < getMaxScrollY() ? scrollToY : getMaxScrollY();
		if (scrollToY > 0) {
			scrollTo(0, scrollToY);
		} else {
			scrollTo(0, 0);
		}
		highlightRow(selectedRow);
	}
	
	public void clearHighlightSelectedRow()
	{
		lastSelectedRowIndex = NO_ROW_SELECTED;
		int childCount = this.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			View view = getChildAt(i);
			if (view.findViewById(R.id.datagrid_item_text) != null) {
				int rowIndex = (Integer)view.getTag(R.id.datagrid_tag_row);
				int columnIndex = (Integer)view.getTag(R.id.datagrid_tag_column);
				//DataGrid�ĵ�1�е�������-1����1�е�����Ҳ��-1
				if (rowIndex >= 0 && columnIndex == -1) {
					view.setBackgroundResource(adapter.getBackgroundResource(rowIndex, columnIndex));
				}
			}
		}
	}

	private class TableAdapterDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			needRelayout = true;
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			// Do nothing
		}
	}

	// http://stackoverflow.com/a/6219382/842697
	private class Flinger implements Runnable {
		private final Scroller scroller;

		private int lastX = 0;
		private int lastY = 0;

		Flinger(Context context) {
			scroller = new Scroller(context);
		}

		void start(int initX, int initY, int initialVelocityX, int initialVelocityY, int maxX, int maxY) {
			scroller.fling(initX, initY, initialVelocityX, initialVelocityY, 0, maxX, 0, maxY);

			lastX = initX;
			lastY = initY;
			post(this);
		}

		public void run() {
			if (scroller.isFinished()) {
				return;
			}

			boolean more = scroller.computeScrollOffset();
			int x = scroller.getCurrX();
			int y = scroller.getCurrY();
			int diffX = lastX - x;
			int diffY = lastY - y;
			if (diffX != 0 || diffY != 0) {
				scrollBy(diffX, diffY);
				lastX = x;
				lastY = y;
			}

			if (more) {
				post(this);
			}
		}

		boolean isFinished() {
			return scroller.isFinished();
		}

		void forceFinished() {
			if (!scroller.isFinished()) {
				scroller.forceFinished(true);
			}
		}
	}
}
