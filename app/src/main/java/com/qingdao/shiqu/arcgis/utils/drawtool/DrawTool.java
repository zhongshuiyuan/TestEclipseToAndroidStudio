package com.qingdao.shiqu.arcgis.utils.drawtool;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.FillSymbol;
import com.esri.core.symbol.LineSymbol;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

/**
 * 画图实现类，支持画点、矩形、线、多边形、圆、手画线、手画多边形，可设置各种图形的symbol。
 * @author Qinyy
 * Date 2015-11-03
 */
public class DrawTool extends DrawToolBase {

	/** 用于 **/
	private MapView mMapView;
	private GraphicsLayer mTempDrawLayer;
	private MarkerSymbol mMarkerSymbol;
	private LineSymbol mLineSymbol;
	private FillSymbol mFillSymbol;
	private Point mPoint;
	private Envelope mEnvelope;
	private Polyline mPolyline;
	private Polygon mPolygon;
	/** 开始绘图时 mMapView 的 MapOnTouchListener **/
	private DrawTouchListener mMapViewDrawListener;
	/** mMapView 原本的 MapOnTouchListener **/
	private MapOnTouchListener mMapViewDefaultListener;
	/** 是否保存了当前绘制的图形 **/
	private boolean mIsSavedCurrentGraphic;
	private Graphic mDrawGraphic;
	private Point mStartPoint;
	private int mGraphicID;

	private int mDrawType;
	public static final int NULL = -1;
	public static final int POINT = 1;
	public static final int ENVELOPE = 2;
	public static final int POLYLINE = 3;
	public static final int POLYGON = 4;
	public static final int CIRCLE = 5;
	public static final int ELLIPSE = 6;
	public static final int FREEHAND_POLYGON = 7;
	public static final int FREEHAND_POLYLINE = 8;

	private boolean mIsActivated;
	public boolean isActivated() {
		return mIsActivated;
	}

	public DrawTool(MapView mapView) {
		mMapView = mapView;
		mTempDrawLayer = new GraphicsLayer();
		mMapView.addLayer(mTempDrawLayer);
		mMapViewDrawListener = new DrawTouchListener(mMapView.getContext(), mMapView);
		mMapViewDefaultListener = new MapOnTouchListener(mMapView.getContext(), mMapView);
		mMarkerSymbol = new SimpleMarkerSymbol(Color.BLACK, 16, SimpleMarkerSymbol.STYLE.CIRCLE);
		mLineSymbol = new SimpleLineSymbol(Color.BLACK, 2);
		mFillSymbol = new SimpleFillSymbol(Color.BLACK);
		mFillSymbol.setAlpha(90);
	}

	/**
	 * 激活绘图工具，开始绘图
	 * @param drawType 绘制的图形类型
	 */
	public void activate(int drawType) {
		if (mMapView == null)
			return;

		if (!mIsSavedCurrentGraphic) {
			if (mDrawGraphic != null) {
				if (!mDrawGraphic.getGeometry().isEmpty()) {
					sendDrawEndEvent();
				}
			}
		}

		reset();

		mMapView.setOnTouchListener(mMapViewDrawListener);
		mDrawType = drawType;
		mIsActivated = true;
		mIsSavedCurrentGraphic = false;
		switch (mDrawType) {
		case DrawTool.POINT:
			mPoint = new Point();
			mDrawGraphic = new Graphic(mPoint, mMarkerSymbol);
			break;
		case DrawTool.ENVELOPE:
			mEnvelope = new Envelope();
			mDrawGraphic = new Graphic(mEnvelope, mFillSymbol);
			break;
		case DrawTool.POLYGON:
		case DrawTool.CIRCLE:
		case DrawTool.FREEHAND_POLYGON:
			mPolygon = new Polygon();
			mDrawGraphic = new Graphic(mPolygon, mFillSymbol);
			break;
		case DrawTool.POLYLINE:
		case DrawTool.FREEHAND_POLYLINE:
			mPolyline = new Polyline();
			mDrawGraphic = new Graphic(mPolyline, mLineSymbol);
			break;
		}
		mGraphicID = mTempDrawLayer.addGraphic(mDrawGraphic);
	}

	/**
	 * 关闭绘图工具
	 */
	public void deactivate() {
		if (!mIsSavedCurrentGraphic) {
			if (mDrawGraphic != null) {
				if (!mDrawGraphic.getGeometry().isEmpty()) {
					sendDrawEndEvent();
				}
			}
		}
		reset();
		mMapView.setOnTouchListener(mMapViewDefaultListener);
	}

	/**
	 * 重置绘图工具
	 */
	private void reset() {
		mTempDrawLayer.removeAll();
		mIsActivated = false;
		mDrawType = NULL;
		mPoint = null;
		mEnvelope = null;
		mPolygon = null;
		mPolyline = null;
		mDrawGraphic = null;
		mStartPoint = null;
	}

	public MarkerSymbol getMarkerSymbol() {
		return mMarkerSymbol;
	}

	public void setMarkerSymbol(MarkerSymbol markerSymbol) {
		mMarkerSymbol = markerSymbol;
	}

	public LineSymbol getLineSymbol() {
		return mLineSymbol;
	}

	public void setLineSymbol(LineSymbol lineSymbol) {
		mLineSymbol = lineSymbol;
	}

	public FillSymbol getFillSymbol() {
		return mFillSymbol;
	}

	public void setFillSymbol(FillSymbol fillSymbol) {
		mFillSymbol = fillSymbol;
	}

	private void finishCurrentDrawingAndStartNext() {
		sendDrawEndEvent();
		activate(mDrawType);
	}

	private void sendDrawEndEvent() {
		DrawEvent drawEvent = new DrawEvent(this, DrawEvent.DRAW_END, mDrawGraphic);
		notifyEvent(drawEvent);
		mIsSavedCurrentGraphic = true;
	}

	/**
	 * 扩展 MapOnTouchListener，实现画图功能
	 */
	class DrawTouchListener extends MapOnTouchListener {

		public DrawTouchListener(Context context, MapView view) {
			super(context, view);
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			if (mIsActivated
					&& (mDrawType == POINT || mDrawType == ENVELOPE
							|| mDrawType == CIRCLE
							|| mDrawType == FREEHAND_POLYLINE || mDrawType == FREEHAND_POLYGON)
					&& event.getAction() == MotionEvent.ACTION_DOWN) {
				Point point = mMapView.toMapPoint(event.getX(), event.getY());
				switch (mDrawType) {
				case DrawTool.POINT:
					mPoint.setXY(point.getX(), point.getY());
					finishCurrentDrawingAndStartNext();
					break;
				case DrawTool.ENVELOPE:
					mStartPoint = point;
					mEnvelope.setCoords(point.getX(), point.getY(), point.getX(), point.getY());
					break;
				case DrawTool.CIRCLE:
					mStartPoint = point;
					break;
				case DrawTool.FREEHAND_POLYGON:
					mPolygon.startPath(point);
					break;
				case DrawTool.FREEHAND_POLYLINE:
					mPolyline.startPath(point);
					break;
				}
			}
			return super.onTouch(view, event);
		}

		@Override
		public boolean onDragPointerMove(MotionEvent from, MotionEvent to) {
			if (mIsActivated
					&& (mDrawType == ENVELOPE || mDrawType == FREEHAND_POLYGON
							|| mDrawType == FREEHAND_POLYLINE || mDrawType == CIRCLE)) {
				Point point = mMapView.toMapPoint(to.getX(), to.getY());
				switch (mDrawType) {
				case DrawTool.ENVELOPE:
					mEnvelope.setXMin(mStartPoint.getX() > point.getX() ? point.getX() : mStartPoint.getX());
					mEnvelope.setYMin(mStartPoint.getY() > point.getY() ? point.getY() : mStartPoint.getY());
					mEnvelope.setXMax(mStartPoint.getX() < point.getX() ? point.getX() : mStartPoint.getX());
					mEnvelope.setYMax(mStartPoint.getY() < point.getY() ? point.getY() : mStartPoint.getY());
					mTempDrawLayer.updateGraphic(mGraphicID, mEnvelope.copy());
					break;
				case DrawTool.FREEHAND_POLYGON:
					mPolygon.lineTo(point);
					mTempDrawLayer.updateGraphic(mGraphicID, mPolygon);
					break;
				case DrawTool.FREEHAND_POLYLINE:
					mPolyline.lineTo(point);
					mTempDrawLayer.updateGraphic(mGraphicID, mPolyline);
					break;
				case DrawTool.CIRCLE:
					double radius = Math.sqrt(Math.pow(mStartPoint.getX() - point.getX(), 2)
							+ Math.pow(mStartPoint.getY() - point.getY(), 2));
					getCircle(mStartPoint, radius, mPolygon);
					mTempDrawLayer.updateGraphic(mGraphicID, mPolygon);
					break;
				}
				return true;
			}
			return super.onDragPointerMove(from, to);
		}

		public boolean onDragPointerUp(MotionEvent from, MotionEvent to) {
			if (mIsActivated && (mDrawType == ENVELOPE || mDrawType == FREEHAND_POLYGON
					|| mDrawType == FREEHAND_POLYLINE || mDrawType == CIRCLE)) {
				Point point = mMapView.toMapPoint(to.getX(), to.getY());
				switch (mDrawType) {
				case DrawTool.ENVELOPE:
					mEnvelope.setXMin(mStartPoint.getX() > point.getX() ? point.getX() : mStartPoint.getX());
					mEnvelope.setYMin(mStartPoint.getY() > point.getY() ? point.getY() : mStartPoint.getY());
					mEnvelope.setXMax(mStartPoint.getX() < point.getX() ? point.getX() : mStartPoint.getX());
					mEnvelope.setYMax(mStartPoint.getY() < point.getY() ? point.getY() : mStartPoint.getY());
					break;
				case DrawTool.FREEHAND_POLYGON:
					mPolygon.lineTo(point);
					break;
				case DrawTool.FREEHAND_POLYLINE:
					mPolyline.lineTo(point);
					break;
				case DrawTool.CIRCLE:
					double radius = Math.sqrt(Math.pow(mStartPoint.getX() - point.getX(), 2)
							+ Math.pow(mStartPoint.getY() - point.getY(), 2));
					getCircle(mStartPoint, radius, mPolygon);
					break;
				}
				finishCurrentDrawingAndStartNext();
				mStartPoint = null;
				return true;
			}
			return super.onDragPointerUp(from, to);
		}

		public boolean onSingleTap(MotionEvent event) {
			if (mIsActivated && (mDrawType == POLYGON || mDrawType == POLYLINE)) {
				Point point = mMapView.toMapPoint(event.getX(), event.getY());
				switch (mDrawType) {
				case DrawTool.POLYGON:
					if (mStartPoint == null) {
						mStartPoint = point;
						mPolygon.startPath(point);
					} else
						mPolygon.lineTo(point);
					mTempDrawLayer.updateGraphic(mGraphicID, mPolygon);
					break;
				case DrawTool.POLYLINE:
					if (mStartPoint == null) {
						mStartPoint = point;
						mPolyline.startPath(point);
					} else {
						mPolyline.lineTo(point);
					}
					mTempDrawLayer.updateGraphic(mGraphicID, mPolyline);
					break;
				}
				return true;
			}
			return false;
		}

		public boolean onDoubleTap(MotionEvent event) {
			if (mIsActivated && (mDrawType == POLYGON || mDrawType == POLYLINE)) {
				Point point = mMapView.toMapPoint(event.getX(), event.getY());
				switch (mDrawType) {
				case DrawTool.POLYGON:
					mPolygon.lineTo(point);
					break;
				case DrawTool.POLYLINE:
					mPolyline.lineTo(point);
					break;
				}
				finishCurrentDrawingAndStartNext();
				mStartPoint = null;
				return true;
			}
			return false;
			//return super.onDoubleTap(event);
		}

		@Override
		public boolean onFling(MotionEvent from, MotionEvent to, float velocityX, float velocityY) {
			if (mIsActivated && (mDrawType == FREEHAND_POLYLINE)) {
				finishCurrentDrawingAndStartNext();
				return true;
			}
			return super.onFling(from, to, velocityX, velocityY);
		}

		private void getCircle(Point center, double radius, Polygon circle) {
			circle.setEmpty();
			Point[] points = getPoints(center, radius);
			circle.startPath(points[0]);
			for (int i = 1; i < points.length; i++)
				circle.lineTo(points[i]);
		}

		private Point[] getPoints(Point center, double radius) {
			Point[] points = new Point[50];
			double sin;
			double cos;
			double x;
			double y;
			for (double i = 0; i < 50; i++) {
				sin = Math.sin(Math.PI * 2 * i / 50);
				cos = Math.cos(Math.PI * 2 * i / 50);
				x = center.getX() + radius * sin;
				y = center.getY() + radius * cos;
				points[(int) i] = new Point(x, y);
			}
			return points;
		}
	}
}
