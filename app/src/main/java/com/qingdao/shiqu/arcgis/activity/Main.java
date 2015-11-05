package com.qingdao.shiqu.arcgis.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Eruntech.BirthStone.Base.Forms.Activity;
import Eruntech.BirthStone.Base.Forms.ActivityManager;
import Eruntech.BirthStone.Base.Forms.Helper.MessageBox;
import Eruntech.BirthStone.Core.Helper.File;
import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import at.markushi.ui.ActionView;
import at.markushi.ui.action.BackAction;
import at.markushi.ui.action.DrawerAction;
import me.drakeet.materialdialog.MaterialDialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonIcon;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.ContentAdapter;
import com.qingdao.shiqu.arcgis.adapter.MyExpandableListAdapter;
import com.qingdao.shiqu.arcgis.dialog.Dialog;
import com.qingdao.shiqu.arcgis.helper.FunctionHelper;
import com.qingdao.shiqu.arcgis.layer.LayerOpter;
import com.qingdao.shiqu.arcgis.listener.MapTouchListener;
import com.qingdao.shiqu.arcgis.listener.MapTouchListener.OnMapListener;
import com.qingdao.shiqu.arcgis.listener.MapViewOnDrawEvenListener;
import com.qingdao.shiqu.arcgis.mode.ContentModel;
import com.qingdao.shiqu.arcgis.mode.SimpleSymbolTemplate;
import com.qingdao.shiqu.arcgis.mode.Take;
import com.qingdao.shiqu.arcgis.sqlite.DatabaseOpenHelper;
import com.qingdao.shiqu.arcgis.sqlite.DoAction;
import com.qingdao.shiqu.arcgis.utils.DBOpterate;
import com.qingdao.shiqu.arcgis.utils.FileUtil;
import com.qingdao.shiqu.arcgis.utils.LocalDataModify;
import com.qingdao.shiqu.arcgis.utils.NSXAsyncTask;
import com.qingdao.shiqu.arcgis.utils.Session;
import com.qingdao.shiqu.arcgis.utils.Util;
import com.qingdao.shiqu.arcgis.utils.drawtool.DrawEvent;
import com.qingdao.shiqu.arcgis.utils.drawtool.DrawTool;

/**
 * @author Administrator
 *
 */
public class Main extends Activity implements OnMapListener
{

    // 大部分变量都重新命名，补全了注释，原来连注释都没有，变量命名超级随意你敢信？ by Qin

    /** Log Tag **/
    private final String TAG = Main.this.getClass().getSimpleName();
    private int requestCode = 100;
    int index = 1;
    double xmin,xmax,ymin,ymax;
    SQLiteDatabase db = null;

    /** 新数据库 **/
    android.database.sqlite.SQLiteDatabase mSQLiteDatabase;

    /** 地图控件 **/
    MapView map = null;
    /** 本地离线切片图层 **/
    ArcGISLocalTiledLayer mLocalTiledLayerMap;
    /** 标注 **/
    ArcGISLocalTiledLayer mLocalTiledLayerLabel;
    /** 光机 **/
    ArcGISLocalTiledLayer mLocalTiledLayerGuangJi;
    /** 分配网 **/
    ArcGISLocalTiledLayer mLocalTiledLayerFenpei;
    /** 旧分配网 **/
    ArcGISLocalTiledLayer mLocalTiledLayerFenpeiOld;
    /** 谷歌切片图层 **/
    ArcGISLocalTiledLayer mLocalTiledLayerGoogle;
    /** 三网覆盖图层 **/
    ArcGISLocalTiledLayer mLocalTiledLayerSanWang;


    /** 第一次加载地图时需要全图显示 **/
    private boolean mIsFullExtentNeeded = true;

    /** 地图模式 **/
    private int mMapState;
    /** 普通模式 **/
    private final int MAP_STATE_NORMAL = 0;
    /** 实时定位模式 **/
    private final int MAP_STATE_NAVIGATION = 1;

    /** 是否开启了实时更新位置模式（只更新位置图标，不会移动屏幕位置） **/
    private boolean mIsUpdatePositionStarted;
    /** 实时更新位置模式专用线程 **/
    private Thread mUpdatePositionThread;
    /** 最近通过GPS定位获取的位置 **/
    private Location mLastLocationFromGps;

    /** 定位标志图层 **/
    GraphicsLayer mLocationGraphicsLayer;

    /** 新建节点图层 **/
    GraphicsLayer newNodeLayer;
    /** 新建管道图层 **/
    GraphicsLayer newGuandaoLayer;
    /** 新建光缆图层 **/
    GraphicsLayer newgllayer;
    /** 新建光缆路由图层 **/
    GraphicsLayer newglly;
    /** 新建电缆路由图层 **/
    GraphicsLayer newdlly;
    /** 用于标注的临时图层 **/
    GraphicsLayer mTempDrawLayer;
    /** 绘图工具 **/
    DrawTool mDrawTool;
    /** 绘制类型 **/
    private int mDrawType;
    /** 绘制事件类型 **/
    private int mDrawAction;

    /** 方向传感器初始化 **/
    SensorManager sensorManager;
    /** 定位坐标初始 **/
    LocationManager locationManager;

    // 控件声明
    private ActionView mActionView;
    private ButtonIcon mBtnToc;

    ButtonFloat mBtnLocation;
    TextView mTvNumSatellites;

    ButtonFloat mBtnActionMenu;
    FloatingActionsMenu mActionMenu;

    // 位置信息显示
    TextView mTvScale;
    TextView mTvCoordinate;
    private TextView mTvLongitude;
    private TextView mTvLatitude;

    // 绘图提示框
    private RelativeLayout mDrawingToolbar;
    private TextView mTvDrawingTitle;
    private TextView mTvDrawingTips;
    private CheckBox mCbIsFreehandDrawing;
    private ButtonFloat mBtnStopDrawing;
    private ButtonFloat mBtnStartOrPauseDrawing;

    private DrawerLayout drawerLayout;
    private RelativeLayout leftLayout;
    private RelativeLayout rightLayout;
    private ContentAdapter adapter;
    private String GL_TYPE = "";
    // TOC
    ExpandableListView mElvToc;
    // TOC 的子数据源
    private ArrayList<ArrayList<Layer>> childs = new ArrayList<ArrayList<Layer>>(); //USE IN MyExpandableListAdapter CLASS
    // TOC 的数据源
    private ArrayList<String> titles = new ArrayList<String>(); //USE IN MyExpandableListAdapter CLASS

    /** 退出App的Dialog **/
    ProgressDialog mProgressDialog = null;
    /** 最近一次通过GPS定位的位置 **/
    Point mCurrentLocationPoint;
    // 定位点添加的graphicID
    int mLocationGraphicId = 0;
    // 定位点添加的graphic图片资源
    Drawable mLocationDrawable;
    /** 是否为第一次将graphic显示到地图上 **/
    boolean isFirst = true;

    PictureMarkerSymbol pictureMarkerSymbol;

    SensorListener sensorListener = new SensorListener();// 方向监听事件

    SharedPreferences sharedPreferences;

    MapTouchListener touchListener;

    List<String> functions = new ArrayList<String>();

    // 初始化可以控制的图层
    FeatureLayer danyuan, guangjigui, guanjing, Segment, guanli, guangji,guanglan,daolu,pda,pda1;
    List<FeatureLayer> featureLayers = new ArrayList<FeatureLayer>();
    PictureMarkerSymbol locationSymbol;
    DBOpterate dbo = null;

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState)
    {
        titles.add("MAP10");

        setContentView(R.layout.main);

        onCreateView();

        seeAll();

        startUpdatePositionMode();

        showNewVersionChanglog();

        super.onCreate(savedInstanceState);
    }

    /** 显示新版本特性 **/
    private void showNewVersionChanglog() {
        boolean show = sharedPreferences.getBoolean(getString(R.string.preference_file_key_main_boolean_show_changelog), true);
        if (show) {
            final MaterialDialog materialDialog = new MaterialDialog(this);
            String title = getString(R.string.app_version) + "版本变化";
            String message = "1、使用谷歌的Material Design重新设计整个app，虽然暂未完成，但是希望你会喜欢\n"
                    + "2、重新设计定位的显示，实时更新所在位置\n"
                    + "3、显示当前通过GPS定位位置的经纬度\n"
                    + "4、保存图层控制的设置，下次登录时能读取之前的设置\n"
                    + "5、修复登录成功后地图显示空白的bug\n"
                    + "6、修复导航模式无法取消的bug\n"
                    + "7、修复了很多处细微的bug，只为使用起来更舒适\n";
            materialDialog.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    })
                    .setNegativeButton("不再显示", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(getString(R.string.preference_file_key_main_boolean_show_changelog), false);
                            editor.commit();
                            materialDialog.dismiss();
                        }
                    });

            materialDialog.show();
        }
    }

    /**
     * 初始化类和控件
     */
    public void onCreateView()
    {
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        mMapState = MAP_STATE_NORMAL;

        //顶部动作条
        mActionView = (ActionView) findViewById(R.id.main_av_menu);
        mBtnToc = (ButtonIcon) findViewById(R.id.main_btn_toc);
        mBtnToc.setDrawableIcon(getResources().getDrawable(R.drawable.ic_layers_white_48dp));

        map = (MapView) findViewById(R.id.map);
        map.getSpatialReference();
        map.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (STATUS.LAYER_LOADED == status) {
                    seeAll();
                    map.setEsriLogoVisible(false);
                    SpatialReference spatialReference = map.getSpatialReference();

                }
            }
        });

        // 信息显示框
        mTvScale = (TextView) findViewById(R.id.scale);
        mTvCoordinate = (TextView) findViewById(R.id.coordinate);
        mTvNumSatellites = (TextView) findViewById(R.id.tv_num_satellites);
        mTvLongitude = (TextView) findViewById(R.id.main_tv_longitude);
        mTvLatitude = (TextView) findViewById(R.id.main_tv_latitude);

        // 绘图提示框
        mDrawingToolbar = (RelativeLayout) findViewById(R.id.main_rl_drawing_toolbar);
        mTvDrawingTitle = (TextView) findViewById(R.id.main_tv_drawing_title);
        mTvDrawingTips = (TextView) findViewById(R.id.main_tv_drawing_tips);
        mCbIsFreehandDrawing = (CheckBox) findViewById(R.id.main_cb_drawing_toolbar_isfreehand);
        mBtnStartOrPauseDrawing = (ButtonFloat) findViewById(R.id.main_btn_drawing_toolbar_startandpause);
        mBtnStopDrawing = (ButtonFloat) findViewById(R.id.main_btn_drawing_toolbar_stopandclose);

        mBtnLocation = (ButtonFloat) findViewById(R.id.btn_location);
        mBtnLocation.setDrawableIcon(getResources().getDrawable(R.drawable.ic_my_location_white_48dp));

        // 按钮菜单
        mActionMenu = (FloatingActionsMenu) findViewById(R.id.main_multiple_actions);
        mBtnActionMenu = (ButtonFloat) findViewById(R.id.main_btn_action_menu);
        //mBtnActionMenu.setDrawableIcon(getResources().getDrawable(R.drawable.ic_map));
        mBtnActionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionMenu.isExpanded()) {
                    mActionMenu.collapse();
                } else {
                    mActionMenu.expand();
                }
            }
        });
        mActionMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mBtnActionMenu.setDrawableIcon(getResources().getDrawable(R.drawable.ic_clear_white_48dp));
            }

            @Override
            public void onMenuCollapsed() {
                mBtnActionMenu.setDrawableIcon(getResources().getDrawable(R.drawable.ic_add_white_48dp));
            }
        });

        createDrawer();

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 讲解密后的文件写入sd卡
        // writeFiles();
        mLocalTiledLayerMap = new ArcGISLocalTiledLayer(FileUtil.getFileAbsolutePath(this, "/PDAlayers/layers"));
        mLocalTiledLayerLabel = new ArcGISLocalTiledLayer(FileUtil.getFileAbsolutePath(this, "/PDAlayers/annlayers"));
        mLocalTiledLayerGuangJi = new ArcGISLocalTiledLayer(FileUtil.getFileAbsolutePath(this, "/PDAlayers/gjlay"));
        mLocalTiledLayerFenpei = new ArcGISLocalTiledLayer(FileUtil.getFileAbsolutePath(this, "/PDAlayers/fpwlay"));
        mLocalTiledLayerFenpeiOld = new ArcGISLocalTiledLayer(FileUtil.getFileAbsolutePath(this, "/PDAlayers/jfpwlay"));
        mLocalTiledLayerGoogle = new ArcGISLocalTiledLayer(FileUtil.getFileAbsolutePath(this, "/PDAlayers/qdgoogle"));
        mLocalTiledLayerSanWang = new ArcGISLocalTiledLayer(FileUtil.getFileAbsolutePath(this, "/PDAlayers/swfglay"));
//        mLocalTiledLayerMap = new ArcGISLocalTiledLayer("file:///" + "sdcard/PDAlayers/layers");
//        mLocalTiledLayerLabel = new ArcGISLocalTiledLayer("file:///" + "sdcard/PDAlayers/annlayers");
//        mLocalTiledLayerGuangJi = new ArcGISLocalTiledLayer("file:///" + "sdcard/PDAlayers/gjlay");
//        mLocalTiledLayerFenpei = new ArcGISLocalTiledLayer("file:///" + "sdcard/PDAlayers/fpwlay");
//        mLocalTiledLayerFenpeiOld = new ArcGISLocalTiledLayer("file:///" + "sdcard/PDAlayers/jfpwlay");
        mLocationGraphicsLayer = new GraphicsLayer();
        mLocationGraphicsLayer.setName("定位图层");
        newNodeLayer = new GraphicsLayer();
        newNodeLayer.setName("新加节点图层");
        newGuandaoLayer = new GraphicsLayer();
        newGuandaoLayer.setName("新加管道图层");
        newgllayer = new GraphicsLayer();
        newgllayer.setName("新加光缆图层");
        mTempDrawLayer = new GraphicsLayer();
        mTempDrawLayer.setName("临时绘制图层");
        newglly = new GraphicsLayer();
        newglly.setName("新加光缆路由");
        newdlly = new GraphicsLayer();
        newdlly.setName("新加电缆路由");

        // 添加底图
        map.setMapBackground(0xffffff, 0xffffff, 0, 0);
        if (mLocalTiledLayerMap != null) {
            mLocalTiledLayerMap.setName("青岛底图");
            map.addLayer(mLocalTiledLayerMap);
        }
        if (mLocalTiledLayerLabel != null) {
            mLocalTiledLayerLabel.setName("路牌号标注");
            map.addLayer(mLocalTiledLayerLabel);
        }
        if (mLocalTiledLayerGuangJi != null) {
            mLocalTiledLayerGuangJi.setName("光机标注");
            map.addLayer(mLocalTiledLayerGuangJi);
        }
        if (mLocalTiledLayerFenpei != null) {
            mLocalTiledLayerFenpei.setName("分配网");
            map.addLayer(mLocalTiledLayerFenpei);
        }
        if (mLocalTiledLayerFenpeiOld != null) {
            mLocalTiledLayerFenpeiOld.setName("旧分配网");
            map.addLayer(mLocalTiledLayerFenpeiOld);
        }
        if (mLocalTiledLayerSanWang != null) {
            mLocalTiledLayerSanWang.setName("三网覆盖");
            map.addLayer(mLocalTiledLayerSanWang);
        }

        // 添加绘画图层
        touchListener = new MapTouchListener(Main.this, map);
        touchListener.setTempDrawingLayer(mTempDrawLayer);
        touchListener.setNewNodeLayer(newNodeLayer);
        touchListener.setNewgdlayer(newGuandaoLayer);
        touchListener.setNewGLLayer(newgllayer);
        touchListener.setNewglly(newglly);
        touchListener.setNewdlly(newdlly);
        map.setOnTouchListener(touchListener); // 地图手势操作监听事件
        map.setOnZoomListener(touchListener); // 地图缩放监听事件

        // 下面这两个方法包含了给 TOC 数据源赋值的过程 by QYY
        initGeodatabase();
        loadExtraLayers();

        if (mLocalTiledLayerGoogle != null) {
            mLocalTiledLayerGoogle.setName("谷歌切片图");
            map.addLayer(mLocalTiledLayerGoogle);
            childs.get(0).add(mLocalTiledLayerGoogle);
        }

        map.addLayer(mTempDrawLayer);
        map.addLayer(newNodeLayer);
        map.addLayer(newGuandaoLayer);
        map.addLayer(newgllayer);
        map.addLayer(newglly);
        map.addLayer(newdlly);
        map.addLayer(mLocationGraphicsLayer); // 定位图层

        for(int i=0;i<map.getLayers().length;i++){
            if(map.getLayer(i).getName().equals("新加管道图层") || map.getLayer(i).getName().equals("新加节点图层")
                    ||map.getLayer(i).getName().equals("新加光缆路由") ||map.getLayer(i).getName().equals("新加电缆路由") )
                map.getLayer(i).setMinScale(8000);
            if(map.getLayer(i).getName().equals("定位图层")){
                map.getLayer(i).setMinScale(0);
                map.getLayer(i).setMaxScale(0);
            }
        }

        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        mSQLiteDatabase = databaseOpenHelper.getWritableDatabase();

        mLocationDrawable = this.getResources().getDrawable(R.drawable.icon_track_map_bar);

        setupDrawTool();

        super.onCreateView();
    }

    private void setupDrawTool() {
        mDrawTool = new DrawTool(map, touchListener);
        MapViewOnDrawEvenListener onDrawEvenListener = new MapViewOnDrawEvenListener(this) {
            @Override
            public void onDrawEnd(DrawEvent event) {
                onDrawEnd(event, mDrawAction);
            }
        };
        onDrawEvenListener.setGllyLayer(newglly);
        onDrawEvenListener.setDllyLayer(newdlly);
        mDrawTool.setOnDrawEvenListener(onDrawEvenListener);
    }

    /** 为DrawerLayout添加抽屉 **/
    public void createDrawer(){

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (drawerView.equals(leftLayout)) {
                    mActionView.setAction(new BackAction(), ActionView.ROTATE_CLOCKWISE);
                    mBtnToc.setDrawableIcon(getResources().getDrawable(R.drawable.ic_layers_white_48dp));
                    drawerLayout.closeDrawer(rightLayout);
                } else if (drawerView.equals(rightLayout)) {
                    mActionView.setAction(new DrawerAction(), ActionView.ROTATE_CLOCKWISE);
                    mBtnToc.setDrawableIcon(getResources().getDrawable(R.drawable.ic_highlight_off_white_48dp));
                    drawerLayout.closeDrawer(leftLayout);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (drawerView.equals(leftLayout)) {
                    mActionView.setAction(new DrawerAction(), ActionView.ROTATE_CLOCKWISE);
                } else if (drawerView.equals(rightLayout)) {
                    mBtnToc.setDrawableIcon(getResources().getDrawable(R.drawable.ic_layers_white_48dp));
                }
            }
        });


        leftLayout = (RelativeLayout) findViewById(R.id.left);
        final ListView lvLeftDrawer = (ListView) leftLayout.findViewById(R.id.left_listview);
        // 创建左抽屉数据
        ArrayList<ContentModel> leftDrawerData = initLeftDrawerData();
        adapter = new ContentAdapter(this, leftDrawerData);
        lvLeftDrawer.setAdapter(adapter);
        lvLeftDrawer.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {
                switch (index) {
                    case 0: //权限控制
                        if (functions.contains("001001")) {
                            Intent intentAuthority = new Intent(Main.this, Dialog_Authority.class);
                            startActivityForResult(intentAuthority, 1);
                            drawerLayout.closeDrawers();
                            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        } else {
                            Toast.makeText(Main.this, "权限不够，请管理员提高权限！", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 1: //查看熔接
                        touchListener.setGeoType(Geometry.Type.POLYGON);
                        touchListener.setFeatureLayers(null);
                        break;
                    case 2: // 编辑管井
                        Intent iii = new Intent(Main.this, PopWindowSpinnerActivity.class/*GuanjingActivity.class*/);
                        startActivityForResult(iii, requestCode);
                        drawerLayout.closeDrawers();
                        break;
                    case 3: // 编辑光缆
                        drawerLayout.closeDrawers();
					/*Intent iii2 = new Intent(Main.this,GuanglanActivity.class);
					startActivityForResult(iii2,requestCode);*/
                        GL_TYPE = "default";
                        touchListener.setDrawGL(true);
                        touchListener.setGltype(GL_TYPE);
                        GL_TYPE = "";
                        break;
                    case 4: // 查看管道光缆走向
                        touchListener.setShowglly(true);
                        drawerLayout.closeDrawers();
                        break;
                    case 5: // 新增光缆路由
                        startAddingGlly();
                        //startAddingGllyOld();
                        drawerLayout.closeDrawers();
                        break;
                    case 6: // 新增电缆路由
                        startAddingDlly();
                        //startAddingDllyOld();
                        drawerLayout.closeDrawers();
                        break;
                    case 7: // 测试绘制工具
                        // TODO 添加测试代码
                        drawerLayout.closeDrawers();
                        mDrawType = DrawTool.POLYLINE;
                        mDrawAction = MapViewOnDrawEvenListener.ACTION_ADD_DLLY;
                        mDrawTool.activate(mDrawType);
                        showDrawingToolbar("测试绘制工具");
                        break;
                    default:
                        break;
                }
            }
        });

        rightLayout = (RelativeLayout) findViewById(R.id.right);

        createToc();
    }

    /** 创建TOC **/
    private void createToc() {
        mElvToc = (ExpandableListView) findViewById(R.id.right_listview);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(Main.this, titles, childs);
        mElvToc.setAdapter(adapter);
    }

    /** 创建左抽屉数据 **/
    private ArrayList<ContentModel> initLeftDrawerData() {
        ArrayList<ContentModel> leftDrawerDatas = new ArrayList<>();

		/*leftDrawerDatas.add(new ContentModel(R.drawable.doctoradvice2, "新闻"));
		leftDrawerDatas.add(new ContentModel(R.drawable.infusion_selected, "查阅"));*/
        leftDrawerDatas.add(new ContentModel(R.drawable.mypatient_selected, "权限控制"));
        leftDrawerDatas.add(new ContentModel(R.drawable.rj, "查看熔接信息"));
        leftDrawerDatas.add(new ContentModel(R.drawable.personal_selected, "编辑管井"));
        leftDrawerDatas.add(new ContentModel(R.drawable.nursingcareplan2, "编辑光缆"));
        leftDrawerDatas.add(new ContentModel(R.drawable.infusion_selected, "查看管道光缆走向"));
        leftDrawerDatas.add(new ContentModel(R.drawable.ic_flashlight, "新增光缆路由"));
        leftDrawerDatas.add(new ContentModel(R.drawable.ic_battery, "新增电缆路由"));
        leftDrawerDatas.add(new ContentModel(R.drawable.infusion_selected, "测试绘图"));
        //		leftDrawerDatas.add(new ContentModel(R.drawable.infusion_selected, "井/管道类型"));

        return leftDrawerDatas;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            // 第一次加载地图时全图显示
            if (mIsFullExtentNeeded) {
                //seeAll();
                //map.setEsriLogoVisible(false);
                mIsFullExtentNeeded = false;

                // 如果弹出更新日志，则保证隐藏软键盘
                boolean show = sharedPreferences.getBoolean(getString(R.string.preference_file_key_main_boolean_show_changelog), true);
                if (show) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume()
    {
        super.onResume();

        map.unpause();

        // 实时更新位置
        startUpdatePositionMode();
        
        functions = Activity.getFunction();
        featureLayers = new ArrayList<FeatureLayer>();
        // 是否显示管道图层
        if (functions.contains("002002"))
        {
            if(Segment != null)
            {
                Segment.setVisible(true);
                touchListener.setGuanDao(Segment);
            }
        }
        else
        {
            if(Segment != null)
            {
                Segment.setVisible(false);
            }
        }
        // 是否显示单元图层
        if (functions.contains("002001"))
        {
            if(danyuan != null)
            {
                danyuan.setVisible(true);
                featureLayers.add(danyuan);
            }
        }
        else
        {
            if(danyuan != null)
            {
                danyuan.setVisible(false);
            }
        }
        // 是否显示光机图层
        if (functions.contains("002003"))
        {
            guangji.setVisible(true);
            // featureLayers.add(guangji);
            touchListener.setguangJi(guangji);
        }
        else
        {
            if(guanjing != null)
            {
                guangjigui.setVisible(false);
                touchListener.setguangJi(null);
            }
        }
        // 是否显示井图层
        if (functions.contains("002004"))
        {
            // 100181759145
            if(guanjing != null)
            {
                guanjing.setVisible(true);
                featureLayers.add(guanjing);
            }
        }
        else
        {
            if(guanjing != null)
            {
                guanjing.setVisible(false);
            }
        }
        //是否显示光缆
        if (functions.contains("002005"))
        {
            if(guanglan != null)
            {
                guanglan.setVisible(true);
                touchListener.setGuanglan(guanglan);
            }
        }
        else
        {
            if(guanglan != null)
            {
                guanglan.setVisible(false);
            }
        }
        //是否显示道路
        /**
         if (functions.contains("002006"))
         {
         if(daolu != null)
         {
         daolu.setVisible(true);
         touchListener.setGuanDao(daolu);
         }
         }
         else
         {
         if(daolu != null)
         {
         daolu.setVisible(false);
         }
         }**/
        touchListener.setFeatureLayers(featureLayers);
        // 是否显示权限按钮
		/*if (functions.contains("001001"))
		{
			btnAuthority.setVisibility(View.VISIBLE);
		}
		else
		{
			btnAuthority.setVisibility(View.GONE);
		}*/
        //		btnAuthority.setVisibility(View.VISIBLE);
        createToc();
        loadTocSetting();

        loadMyDraw();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.pause();
        saveTocSetting();
        stopUpdatePositionMode();
    }

    private void saveTocSetting() {
        if (sharedPreferences == null) {
            sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int titleCount = childs.size();
        for (int i = 0; i < titleCount; ++i) {
            ArrayList<Layer> layers = childs.get(i);
            int layerCount = layers.size();
            for (int j = 0; j < layerCount; ++j) {
                Layer layer = layers.get(j);
                editor.putBoolean(layer.getName(), layer.isVisible());
            }
        }

        editor.commit();
    }

    /** 读取TOC设置 **/
    private void loadTocSetting() {
        if (sharedPreferences == null) {
            sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        }
//        String tocSetting = sharedPreferences.getString(getString(R.string.preference_file_key_string_main_toc), null);
//        if (tocSetting == null) {
//            return;
//        }
//        // 判断tocSetting的长度和图层数量是否一致，不一致代表新版本新增了或减少了图层，则之前保存的设置就无效了
//        int tempCount = 0;
//        int tempSize = childs.size();
//        for (int i = 0; i< tempSize; ++i) {
//            tempCount += childs.get(i).size();
//        }
//        if (tempCount != tocSetting.length()) {
//            return;
//        }
        // 开始恢复TOC设置
        int titleCount = childs.size();
        for (int i = 0; i < titleCount; ++i) {
            ArrayList<Layer> layers = childs.get(i);
            int layerCount = layers.size();
            for (int j = 0; j < layerCount; ++j) {
                Layer layer = layers.get(j);
                Boolean visible = sharedPreferences.getBoolean(layer.getName(), true);
                layer.setVisible(visible);
            }
        }
    }

    /**
     *
     * 功 能：初始化Geodatabase
     */
    private void initGeodatabase()
    {
        try {
            // UsernamePasswordCredentials userCredentials = new
            // UsernamePasswordCredentials("", "");
            //			 UserCredentials a = new UserCredentials();
            //			 a.setUserAccount(userName, password);
            String map10 = FileUtil.getFileAbsolutePath(this, "/map/map10.geodatabase");
            if(map10 != null) {
                Geodatabase geodatabase = new Geodatabase(map10);
                List<GeodatabaseFeatureTable> tables = geodatabase.getGeodatabaseTables();
                danyuan = new FeatureLayer(tables.get(0));
                guangjigui = new FeatureLayer(tables.get(1));
                guanjing = new FeatureLayer(tables.get(2));
                guangji = new FeatureLayer(tables.get(4));
                guanli = new FeatureLayer(tables.get(5));
                map.addLayer(danyuan);
                map.addLayer(guangjigui);
                map.addLayer(guanjing);
                map.addLayer(guangji);
                // map.addLayer(guanli);
            }

            String guandaoPath = FileUtil.getFileAbsolutePath(this, "/map/guandao.geodatabase");
            if(guandaoPath != null) {
                Geodatabase guandaogeodatabase = new Geodatabase(guandaoPath);
                List<GeodatabaseFeatureTable> guandaotables = guandaogeodatabase.getGeodatabaseTables();
                Segment = new FeatureLayer(guandaotables.get(0));
                Segment.setName("管道");
                map.addLayer(Segment);
            }

            String guanglanPath = FileUtil.getFileAbsolutePath(this, "/map/guanglan.geodatabase");
            if(guanglanPath != null) {
                Geodatabase guanlangeodatabase = new Geodatabase(guanglanPath);
                List<GeodatabaseFeatureTable> guanlantables = guanlangeodatabase.getGeodatabaseTables();
                guanglan = new FeatureLayer(guanlantables.get(0));
                //map.addLayer(guanglan);
            }

            //加载道路
            String daoluPath = FileUtil.getFileAbsolutePath(this, "/map/daolu.geodatabase");
            if(daoluPath != null) {
                Geodatabase daolugeodatabase = new Geodatabase(daoluPath);
                List<GeodatabaseFeatureTable> daolutables = daolugeodatabase.getGeodatabaseTables();
                daolu = new FeatureLayer(daolutables.get(0));
                map.addLayer(daolu);
            }


            String pad = FileUtil.getFileAbsolutePath(this, "/map/pdageodatabase.geodatabase");
            if(pad != null) {
                Geodatabase daolugeodatabase = new Geodatabase(pad);
                List<GeodatabaseFeatureTable> daolutables = daolugeodatabase.getGeodatabaseTables();
                pda = new FeatureLayer(daolutables.get(0));
                pda1 = new FeatureLayer(daolutables.get(1));
				//map.addLayer(pda);
				//map.addLayer(pda1);
            }

            ArrayList<Layer> old = new ArrayList<>();
            for(int i=0;i<map.getLayers().length;i++){
                old.add(map.getLayer(i));
            }
            childs.add(old);

        } catch (Exception e) {
            Log.e(TAG, "初始化Geodatabase错误：" + e.getMessage());
        }
    }
    private void loadExtraLayers(){
        //加载道路
        ArrayList<Layer> hfc_lay = new ArrayList<>();
        String htfPath = FileUtil.getFileAbsolutePath(this, "/map/hfc.geodatabase");
        if(htfPath != null) {
            loadLayerByGeoPath("HFC", htfPath);
        }


        String eponPath = FileUtil.getFileAbsolutePath(this, "/map/epon.geodatabase");
        if(eponPath != null) {
            loadLayerByGeoPath("EPON网", eponPath);
        }

        ///
        String gj2Path = FileUtil.getFileAbsolutePath(this, "/map/GISGJ.geodatabase");
        if(gj2Path != null) {
            loadLayerByGeoPath("GIS新增数据", gj2Path);
        }

        //
        String glpath = FileUtil.getFileAbsolutePath(this, "/map/glw.geodatabase");
        if(glpath != null) {
            loadLayerByGeoPath("光缆网", glpath);
        }

        //
        String fenpeipath = FileUtil.getFileAbsolutePath(this, "/map/fenpeiwang.geodatabase");
        if(fenpeipath != null) {
            loadLayerByGeoPath("分配网", fenpeipath);
        }

        //
        String PDAguanglan = FileUtil.getFileAbsolutePath(this, "/map/pdagl.geodatabase");
        if(PDAguanglan != null) {
            loadLayerByGeoPath("PDA光缆网", PDAguanglan);
        }

        //
        String daolupath = FileUtil.getFileAbsolutePath(this, "/map/hfcdaolu.geodatabase");
        if(daolupath != null) {
            loadLayerByGeoPath("道路网", daolupath);
        }
    }
    /**
     * @param title
     * 				右拉抽屉主标题
     * @param path
     * 				geodatabase路径
     */
    private void loadLayerByGeoPath(String title, String path) {

        titles.add(title);
        Geodatabase tempGeodatabase = null;
        try {
            tempGeodatabase = new Geodatabase(path);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        List<GeodatabaseFeatureTable> tempTables = tempGeodatabase.getGeodatabaseTables();
        ArrayList<Layer> ep = new ArrayList<Layer>();
        for(int i=0;i<tempTables.size();i++){
            FeatureLayer tempLayer = new FeatureLayer(tempTables.get(i));
            ep.add(tempLayer);
            if(tempLayer.getName().equals("GIS光机2")){
                tempLayer.setMinScale(4000);
                touchListener.setGisgj2(tempLayer);
            } else if(tempLayer.getName().equals("楼放")){
                touchListener.setLoufang(tempLayer);
            }else if(tempLayer.getName().equals("道路")){
                touchListener.setDaolu(tempLayer);
            }else if(tempLayer.getName().equals("路牌号")){
                touchListener.setLupaihao(tempLayer);
            }else if(tempLayer.getName().equals("分支器")){
                touchListener.setFenzhiqi(tempLayer);
            }else if(tempLayer.getName().equals("专网光缆")){
                touchListener.setZhuanxiangl(tempLayer);
            }else if(tempLayer.getName().equals("支线光缆")){
                touchListener.setZhixiangl(tempLayer);
            }else if(tempLayer.getName().equals("骨干光缆")){
                touchListener.setGugangl(tempLayer);
            }
            if(tempLayer.getName().equals("GIS电缆干线") || tempLayer.getName().equals("EPON支线光缆")||tempLayer.getName().equals("GIS电缆支线")
                    || tempLayer.getName().equals("双绞线") || tempLayer.getName().equals("骨干光缆")||tempLayer.getName().equals("专网光缆")
                    ||tempLayer.getName().equals("支线光缆")){
                tempLayer.setVisible(false);
            }
            map.addLayer(tempLayer);
			/*long testnumb = tempLayer.getFeatureTable().getNumberOfFeatures();
			Map<String,Object> attributes;
			for(int x=0;x<testnumb;x++){
				Field fld=tempLayer.getFeatureTable().getField("站点名称");
				try {
					Feature temf = tempLayer.getFeatureTable().getFeature(x);
					if(temf != null)
						attributes =temf.getAttributes();
					//				ob = tempLayer.getFeatureTable().getFeature(x).getAttributeValue("站点名称");
					//				Log.i("zdmc", String.valueOf(ob));
				} catch (TableException e) {
					e.printStackTrace();
				}
			}*/
        }

        childs.add(ep);

    }

    /**
     * 判断gps是否打开，没又打开提示进入到设置界面
     */
    private void openGPSSetting()
    {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            SharedPreferences sharedPreferences = getSharedPreferences("dialog", 0);
            boolean boolean1 = sharedPreferences.getBoolean("is", false);
            if (!boolean1)
            {
                Intent intent = new Intent(Main.this, Dialog.class);
                startActivity(intent);
            }
        }
    }


    /**
     *
     * 功 能： 在地图上一个点创建图层并附带属性，并获取该图层
     *
     * @param point
     * @param map
     * @return
     */
    private Graphic getGraphic(Point point, Map<String, Object> map)
    {
        GraphicsLayer layer = getGraphicsLayer();
        mLocationDrawable = this.getResources().getDrawable(R.drawable.icon_track_map_bar);
        PictureMarkerSymbol symbol = new PictureMarkerSymbol(mLocationDrawable);
        Graphic graphic = new Graphic(point, symbol, map);
        return graphic;
    }

    /**
     *
     * 功 能：在地图上添加图层，并获取该图层
     *
     * @return
     */
    private GraphicsLayer getGraphicsLayer()
    {
        GraphicsLayer layer = new GraphicsLayer();
        map.addLayer(layer);
        return layer;
    }

    /**
     *
     * 功 能：单击事件
     *
     * @param view
     */
    public void click(View view)
    {
        switch (view.getId())
        {
            case R.id.main_btn_zoomin: // 放大地图
                map.zoomin();
                break;
            case R.id.main_btn_zoomout: // 缩小地图
                map.zoomout();
                break;
            case R.id.main_btn_distance_measurement: // 计算长度
                touchListener.setGeoType(Geometry.Type.POLYLINE);
                break;
            case R.id.main_btn_clear: // 清空计算长度画的线和点
                mTempDrawLayer.removeAll();
                touchListener.setGeoType(null);
                map.postInvalidate();
                break;
            case R.id.main_ll_search:// 搜索功能
                onSearchClick();
                break;
            case R.id.main_btn_previous: // 后退操作
                List<Take> takes = Session.getTakes();
                int size = takes.size();
                if (index > 0 && size > 0)
                {
                    index--;
                    Take take = takes.get(index);
                    Point point2 = take.getPoint();
                    map.setScale(take.getZoom());
                    map.centerAt(take.getPoint(), true);
                }
                break;
            case R.id.main_btn_next: // 前进操作
                List<Take> t = Session.getTakes();
                int s = t.size();
                if (index < s - 1)
                {
                    index++;
                    Take take = t.get(index);
                    Point point2 = take.getPoint();
                    map.setScale(take.getZoom());
                    map.centerAt(take.getPoint(), true);
                }
                break;
            case R.id.btn_location: // 切换导航模式和普通模式
                onBtnNavigationClick();
                break;
            case R.id.main_btn_fullextent: // 全图按钮
                seeAll();
                break;
            case R.id.main_btn_toc: // TOC按钮
                onBtnTocClick();
                break;
            case R.id.main_av_menu: // 菜单按钮
                onActionViewClick();
                break;
            case R.id.main_cb_drawing_toolbar_isfreehand: // 使用自由线条绘图按钮
                onCbIsFreehandDrawingClick();
                break;
            case R.id.main_btn_drawing_toolbar_startandpause: // 暂停或开始绘图按钮
                onBtnStartOrPauseDrawingClick();
                break;
            case R.id.main_btn_drawing_toolbar_stopandclose: // 结束绘图按钮
                onBtnStopDrawingClick();
                break;
            default:
                break;
        }
    }

    /**
     * 搜索回调，进行画线或者画点操作
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		/*if(!data.getStringExtra("jtype").equals("")){
			switch (resultCode)
			{
			case RESULT_OK:
				String jtype = data.getStringExtra("jtype");
				touchListener.setJtype(jtype);
				break;
			}
		}else{*/
        switch (resultCode)
        {
            case RESULT_OK:
                String value = data.getStringExtra("value");
                String type = data.getStringExtra("type");
                String jtype = data.getStringExtra("jtype");
                String gtype = data.getStringExtra("gltype");
                String gdcq = data.getStringExtra("gdcq");
                if (jtype != null) {
                    touchListener.setAddNode(true);
                    touchListener.setJtype(jtype);
                    touchListener.setGdcq(gdcq);
                } else if (gtype != null || GL_TYPE.equalsIgnoreCase("default")) {
                    //不需要设置光缆类型，所以不用选择光缆类型
                    touchListener.setDrawGL(true);
                    touchListener.setGltype(GL_TYPE);
                    GL_TYPE = "";
                } else {
                    if ("道路".equals(type)) {
                        List<Point> list = Util.convertLines(value);
                        LayerOpter opter = new LayerOpter(this, mTempDrawLayer);
                        //				opter.drawRoad(list);
                        opter.DrawRoad(list);
                        map.centerAt(list.get(list.size() / 2), true);
                        map.setScale(1200.0000);
                    } else {
                        Point point = Util.convertPoint(value);
                        LayerOpter opter = new LayerOpter(this, mTempDrawLayer);
                        Drawable drawable = Main.this.getResources().getDrawable(R.drawable.sendtocar_balloon);
                        opter.drwaPoint(point, drawable);
                        map.centerAt(point, true);
                        map.setScale(1200.0000);
                    }

                }
                break;
            case RESULT_CANCELED:
                Log.w(TAG, requestCode + "请求失败");
                break;
        }
    }

    /**
     *
     * 方向传感监听
     */
    private final class SensorListener implements SensorEventListener
    {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
        }

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            try
            {
                Message message = Message.obtain(handler);
                message.obj = event.values[0];
                message.sendToTarget();
            }
            catch (Exception e)
            {
                MessageBox.showMessage(Main.this, "11", e.getMessage());
                e.printStackTrace();
            }

        }
    }

    /**
     * 根据陀螺仪在地图上实时展示移动方向
     */
    private Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if (pictureMarkerSymbol != null)
            {
                try
                {
                    Thread.sleep(1000 * 5);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                float degree = (Float) msg.obj;
                Toast.makeText(Main.this, degree + ";x:" + mCurrentLocationPoint.getX() + "y:" + mCurrentLocationPoint.getY(), Toast.LENGTH_SHORT)
                        .show();
                // float degree = (Float)msg.obj;
                MarkerSymbol setAngle = pictureMarkerSymbol.setAngle(degree);
                Graphic graphic = new Graphic(mCurrentLocationPoint, pictureMarkerSymbol);
                mLocationGraphicsLayer.updateGraphic(mLocationGraphicId, graphic);
            }
        };
    };

    /********************* OnMapListener ******************/
    /**
     * 在移动或者缩放后进行记录
     */
    @Override
    public void onMoveAndZoom()
    {
        // 退出实时导航模式
        mMapState = MAP_STATE_NORMAL;
        onMapStateChanged();

        updateUi();

        try
        {

            Point centerPoint = Util.getCenterPoint(this);
            Point mapPoint = map.toMapPoint(centerPoint);
            double scale = map.getScale();

            Take take = new Take();
            take.setPoint(mapPoint);
            take.setZoom(scale);
            Session.getTakes().add(take);
            index = Session.getTakes().size() - 1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder builder = new Builder(this);
            builder.setMessage(R.string.app_name);
            builder.setTitle("退出");
            builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    mProgressDialog = new ProgressDialog(Main.this);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setTitle("退出");
                    mProgressDialog.setMessage("正在退出......");
                    mProgressDialog.setIndeterminate(false);
                    exit();
                }
            });
            builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface arg0, int arg1)
                {
                    return;
                }
            });
            builder.create().show();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出当前应用程序
     * */
    private void exit()
    {
        saveTocSetting();
        saveExtent();
        //		copyFile("/data/data/com.qingdao.shiqu.arcgis/databases/gisdb.s3db", "/storage/sdcard0/SQL/gisdb.s3db");
        //TODO:delete
        //		Initalize.copySQL(Environment.getExternalStorageDirectory().getAbsolutePath()
        //				+ "/SQL/gisdb.s3db");
        try
        {
            new NSXAsyncTask<Object, Object>()
            {
                @Override
                protected Object doInBackground(Object... params)
                {
                    int size = ActivityManager.size();
                    for (int i = 0; i < size; i++)
                    {
                        ActivityManager.popActivity();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object rs)
                {
                    mProgressDialog.cancel();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }.execute();

        }
        catch (Exception ex)
        {
            Log.e("退出异常", ex.getMessage());
        }
    }
    /**
     * 保存程序退出前地图的范围
     */
    private void saveExtent(){
        DataTable dt = QuyExtent();
        db = new SQLiteDatabase(Main.this);
        Envelope  extent = new Envelope();
        map.getExtent().queryEnvelope(extent);
        xmin = extent.getXMin();
        ymin = extent.getYMin();
        xmax = extent.getXMax();
        ymax = extent.getYMax();

        DataCollection params = new DataCollection();
        params.add(new Data("min_x", xmin));
        params.add(new Data("min_y", ymin));
        params.add(new Data("max_x", xmax));
        params.add(new Data("max_y", ymax));
        params.add(new Data("roleid", FunctionHelper.USER_ROLE.get("userno").Value.toString()));
        if(dt.size()>0)
            db.execute("aqBS_UpdateExtent", params);
        else
            db.execute("aqBS_InsExtent", params);
    }
    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File();
            if (oldfile.exists(oldPath)) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }
    /**
     * 说 明：加载上次退出时的地图范围
     */
    private void loadLastExtent() {
        db = new SQLiteDatabase(Main.this);
        DataCollection params = new DataCollection();
        params.add(new Data("roleid", FunctionHelper.USER_ROLE.get("userno").Value.toString()));
        DataTable result = db.executeTable("aqBS_ListExtent", params);
        for(int i=0;i<result.size();i++) {
            DataCollection dc = result.next();
            String xmi = dc.get("min_x").Value.toString();
            String ymi = dc.get("min_y").Value.toString();
            String xma = dc.get("max_x").Value.toString();
            String yma = dc.get("max_y").Value.toString();
            xmax = Double.valueOf(xma); ymax = Double.valueOf(yma);
            xmin = Double.valueOf(xmi); ymin = Double.valueOf(ymi);
        }
        if (result.size()>0) {
            Envelope en = new Envelope();
            en.setXMax(xmax);en.setYMax(ymax);en.setXMin(xmin);en.setYMin(ymin);
            map.setExtent(en);
        } else {
            seeAll();
        }
    }

    /** 显示地图全图 **/
    private void seeAll() {
        map.setScale(175590);
        Point point = new Point();
        point.setX(236038.1424072377);
        point.setY(109233.05352031846);
        map.centerAt(point, true);
        map.zoomToScale(point, 175590);

        forceUpdateCoordinate(236038.1424072377, 109233.05352031846);
        forceUpdateScale(175590);

        mMapState = MAP_STATE_NORMAL;
        onMapStateChanged();
    }

    /**
     * 说 明：查询该用户是否首次登陆
     * @return
     * 		查询结果
     */
    private DataTable QuyExtent(){
        DataCollection params = new DataCollection();
        params.add(new Data("roleid", FunctionHelper.USER_ROLE.get("userno").Value.toString()));
        db = new SQLiteDatabase(Main.this);

        DataTable table = db.executeTable("aqBS_QuyExtent", params);
        return table;
    }

    private void markLocation(Location location) {
        mTempDrawLayer.removeAll();
        double locx = location.getLongitude();
        double locy = location.getLatitude();
        Point wgspoint = new Point(locx, locy);
		/*Point mapPoint = (Point) GeometryEngine.project(wgspoint,SpatialReference.create(4326),map.getSpatialReference());

		//图层的创建
		Graphic graphic = new Graphic(mapPoint,locationSymbol);
		mTempDrawLayer.addGraphic(graphic);
		map.centerAt(mapPoint, true);*/
        LayerOpter opter = new LayerOpter(this, mTempDrawLayer);
        Drawable drawable = Main.this.getResources().getDrawable(R.drawable.sendtocar_balloon);
        opter.drwaPoint(wgspoint, drawable);
        map.centerAt(wgspoint, true);
        map.setScale(1200.0000);
    }

    /** 加载自定义图层 **/
    public void loadMyDraw() {
        PictureMarkerSymbol pms = null;
        Drawable img = null;
        db = new SQLiteDatabase(this);
        DataCollection params = new DataCollection();
        params.add(new Data("operator",FunctionHelper.userName));
        DataTable result = db.executeTable("aqBS_LocalDataQuery", params);
        for(int i=0;i<result.size();i++){
            DataCollection dc = result.next();
            String xmi = dc.get("x").Value.toString();
            String ymi = dc.get("y").Value.toString();
            String uid = dc.get("uid").Value.toString();
            String t = dc.get("operator").Value.toString();
            String a = dc.get("operattime").Value.toString();
            String n = dc.get("gjname").Value.toString();
            String type = dc.get("type").Value.toString();
            String pid = dc.get("gjid").Value.toString();
            Double.valueOf(xmi);
            //Get jing pic path
            String path = DoAction.getGJPathByName(Main.this, type);
            Bitmap bm =  DoAction.TransPath2Bmp(path);
            img = new BitmapDrawable(bm);
			/*if(type.equals("有线圆井")){
				img = this.getResources().getDrawable(R.drawable.yj);
			}else
				img = this.getResources().getDrawable(R.drawable.fjj);*/
            pms = new PictureMarkerSymbol(com.qingdao.shiqu.arcgis.utils.Utils.zoomDrawable(img, 40, 40));
            Graphic graphic = new Graphic(new Point(Double.valueOf(xmi),Double.valueOf(ymi)), pms);
            newNodeLayer.addGraphic(graphic);
            long uid2 = graphic.getId();
        }
        loadSegment();
        //		loadGL();
        loadGLLY();
        loadDLLY();
    }

    public void loadSegment() {
        Graphic tempGraphic ;
        db = new SQLiteDatabase(this);
        DataCollection params = new DataCollection();
        params.add(new Data("operator", FunctionHelper.userName));
        DataTable rst = db.executeTable("spBS_LocalGDQuery", params);
        for(int i=0;i<rst.size();i++){
            Polyline pl = new Polyline();
            DataCollection dc = rst.next();
            double sx,sy,ex,ey;
            sx =  Double.valueOf( dc.get("sx").Value.toString());
            sy =  Double.valueOf( dc.get("sy").Value.toString());
            ex =  Double.valueOf( dc.get("ex").Value.toString());
            ey =  Double.valueOf( dc.get("ey").Value.toString());
            String id = dc.get("gdid").Value.toString();
            String uid = dc.get("uid").Value.toString();
            String type = dc.get("type").Value.toString();
            DataCollection dc_g2g = new DataCollection();
            dc_g2g.add(new Data("gdid", id));
            DataTable rt_g = db.executeTable("spBS_QueryGD2GLByGDID", dc_g2g);

			/*if(rt_g != null && rt_g.size()>0)
				tempGraphic = new Graphic(pl, new SimpleLineSymbol(Color.GREEN, 1, SimpleLineSymbol.STYLE.SOLID));
			else*/
            tempGraphic = new Graphic(pl, new SimpleLineSymbol(DoAction.getGDColorByCQ(Main.this,type), DoAction.getGDWidthByCQ(Main.this, type), SimpleLineSymbol.STYLE.SOLID));
            pl.startPath(ex, ey);
            pl.lineTo(sx, sy);
            newGuandaoLayer.addGraphic(tempGraphic);
        }
    }

    public void loadGL() {
        Graphic tempGraphic ;
        db = new SQLiteDatabase(this);
        DataCollection params = new DataCollection();
        params.add(new Data("uname", FunctionHelper.userName));
        DataTable rst = db.executeTable("spBS_LocalGLQueryByUName", params);
        for(int i=0;i<rst.size();i++){
            Polyline pl = new Polyline();
            DataCollection dc = rst.next();

            String pts = dc.get("points").Value.toString();
            ArrayList<Point> points = LocalDataModify.splitePts(pts);
            for(int j=0;j<points.size();j++){
                if(j == 0)
                    pl.startPath(points.get(j));
                else
                    pl.lineTo(points.get(j));
            }


            tempGraphic = new Graphic(pl, new SimpleLineSymbol(Color.GREEN, 1, SimpleLineSymbol.STYLE.SOLID));

            newgllayer.addGraphic(tempGraphic);
        }
    }

    /**
     * 加载自定义光缆路由数据到地图
     */
    public void loadGLLY() {
        Graphic tempGraphic ;
        // 从平台数据库读取旧数据
        db = new SQLiteDatabase(this);
        DataCollection params = new DataCollection();
        params.add(new Data("uname", FunctionHelper.userName));
        DataTable rst = db.executeTable("spBS_LocalGLLYQueryByUName", params);
        for(int i=0;i<rst.size();i++){
            Polyline pl = new Polyline();
            DataCollection dc = rst.next();

            String pts = dc.get("points").Value.toString();
            ArrayList<Point> points = LocalDataModify.splitePts(pts);
            for(int j=0;j<points.size();j++){
				/*SimpleMarkerSymbol msb = new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
				Graphic graphic = new Graphic(points.get(j), msb);
				newglly.addGraphic(graphic);*/
                if(j == 0)
                    pl.startPath(points.get(j));
                else
                    pl.lineTo(points.get(j));
            }


            tempGraphic = new Graphic(pl, SimpleSymbolTemplate.GLLY);

            newglly.addGraphic(tempGraphic);
        }
        // 从新数据库读取新数据
        Cursor c = mSQLiteDatabase.query("glly", null, null, null, null, null, null);
        if(c.moveToFirst()){
            for(int i = 0; i < c.getCount(); ++i) {
                c.moveToPosition(i);
                byte[] geometryByte = c.getBlob(c.getColumnIndex("geometry"));
                String hashcode = c.getString(c.getColumnIndex("hashcode"));
                Geometry geometry = GeometryEngine.geometryFromEsriShape(geometryByte, Geometry.Type.POLYLINE);
                tempGraphic = new Graphic(geometry, SimpleSymbolTemplate.GLLY);
                newglly.addGraphic(tempGraphic);
            }
        }
    }

    /**
     * 加载自定义光缆路由数据到地图
     */
    public void loadDLLY() {
        Graphic tempGraphic ;
        // 从平台数据库读取旧数据
        db = new SQLiteDatabase(this);
        DataCollection params = new DataCollection();
        params.add(new Data("uname",FunctionHelper.userName));
        DataTable rst = db.executeTable("spBS_LocalDLLYQueryByUName", params);
        for(int i=0;i<rst.size();i++){
            Polyline pl = new Polyline();
            DataCollection dc = rst.next();

            String pts = dc.get("points").Value.toString();
            ArrayList<Point> points = LocalDataModify.splitePts(pts);
            for(int j=0;j<points.size();j++){
				/*SimpleMarkerSymbol msb = new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
				Graphic graphic = new Graphic(points.get(j), msb);
				newdlly.addGraphic(graphic);*/
                if(j == 0)
                    pl.startPath(points.get(j));
                else
                    pl.lineTo(points.get(j));
            }

            tempGraphic = new Graphic(pl, new SimpleLineSymbol(Color.LTGRAY, 1, SimpleLineSymbol.STYLE.SOLID));
            newdlly.addGraphic(tempGraphic);
        }

        // 从新数据库读取新数据
        Cursor c = mSQLiteDatabase.query("dlly", null, null, null, null, null, null);
        if(c.moveToFirst()){
            for(int i = 0; i < c.getCount(); ++i) {
                c.moveToPosition(i);
                byte[] geometryByte = c.getBlob(c.getColumnIndex("geometry"));
                String hashcode = c.getString(c.getColumnIndex("hashcode"));
                Geometry geometry = GeometryEngine.geometryFromEsriShape(geometryByte, Geometry.Type.POLYLINE);
                tempGraphic = new Graphic(geometry, SimpleSymbolTemplate.DLLY);
                newdlly.addGraphic(tempGraphic);
            }
        }
    }

    /** 选中使用自由线条绘图 **/
    private void onCbIsFreehandDrawingClick() {
        if (mCbIsFreehandDrawing.isChecked()) {
            mDrawType = DrawTool.FREEHAND_POLYLINE;
            mTvDrawingTips.setText("在屏幕上滑动以绘制自由线条");
        } else {
            mDrawType = DrawTool.POLYLINE;
            mTvDrawingTips.setText("单击绘制下一点\n双击完成绘制");
        }
        if (mDrawTool.isActivated()) {
            mDrawTool.activate(mDrawType);
        }
    }

    /** 按下开始（暂停）绘图 **/
    private void onBtnStartOrPauseDrawingClick() {
        if (mDrawTool.isActivated()) {
            mDrawTool.deactivate();
            setBtnStartOrPauseDrawingToStartState();
        } else {
            mDrawTool.activate(mDrawType);
            setBtnStartOrPauseDrawingToPauseState();
        }
    }

    private void setBtnStartOrPauseDrawingToStartState() {
        mBtnStartOrPauseDrawing.setDrawableIcon(getResources().getDrawable(R.drawable.ic_play));
        mBtnStartOrPauseDrawing.setBackgroundColor(getResources().getColor(R.color.dark_green));
    }

    private void setBtnStartOrPauseDrawingToPauseState() {
        mBtnStartOrPauseDrawing.setDrawableIcon(getResources().getDrawable(R.drawable.ic_pause));
        mBtnStartOrPauseDrawing.setBackgroundColor(getResources().getColor(R.color.app_design_background));
    }

    /**
     * 显示绘图工具栏
     */
    private void showDrawingToolbar(String title) {
        setBtnStartOrPauseDrawingToPauseState();
        mTvDrawingTitle.setText(title);
        mCbIsFreehandDrawing.setChecked(false);
        mDrawingToolbar.setVisibility(View.VISIBLE);
    }

    /**
     * 开启地图编辑模式，新增光缆路由
     */
    private void startAddingGlly() {
        mDrawType = DrawTool.POLYLINE;
        mDrawAction = MapViewOnDrawEvenListener.ACTION_ADD_GLLY;
        mDrawTool.activate(mDrawType);
        showDrawingToolbar("新增光缆路由");
    }

    /**
     * 旧的添加光缆路由的方法，已弃用
     * @deprecated
     */
    @Deprecated
    private void startAddingGllyOld() {
        touchListener.setDrawglly(true);
    }

    /**
     * 开启地图编辑模式，新增电缆路由
     */
    private void startAddingDlly() {
        mDrawType = DrawTool.POLYLINE;
        mDrawAction = MapViewOnDrawEvenListener.ACTION_ADD_DLLY;
        mDrawTool.activate(mDrawType);
        showDrawingToolbar("新增电缆路由");
    }

    /**
     * 旧的添加电缆路由的方法，已弃用
     * @deprecated
     */
    @Deprecated
    private void startAddingDllyOld() {
        touchListener.setDrawdlly(true);
    }

    /** 按下停止绘图 **/
    private void onBtnStopDrawingClick() {
        mDrawTool.deactivate();
        mDrawType = DrawTool.NULL;
        mDrawAction = MapViewOnDrawEvenListener.ACTION_NULL;

        mBtnStartOrPauseDrawing.setDrawableIcon(getResources().getDrawable(R.drawable.ic_pause));
        mBtnStartOrPauseDrawing.setBackgroundColor(getResources().getColor(R.color.app_design_background));

        mDrawingToolbar.setVisibility(View.GONE);
    }

    /** 按下动作条的图层按钮 **/
    private void onBtnTocClick() {
        if (drawerLayout.isDrawerOpen(rightLayout)) {
            mBtnToc.setDrawableIcon(getResources().getDrawable(R.drawable.ic_layers_white_48dp));
            drawerLayout.closeDrawer(rightLayout);
        } else {
            mBtnToc.setDrawableIcon(getResources().getDrawable(R.drawable.ic_highlight_off_white_48dp));
            drawerLayout.openDrawer(rightLayout);
        }
    }

    /** 按下动作条的菜单按钮 **/
    private void onActionViewClick() {
        if (drawerLayout.isDrawerOpen(leftLayout)) {
            mActionView.setAction(new DrawerAction(), ActionView.ROTATE_CLOCKWISE);
            drawerLayout.closeDrawer(leftLayout);
        } else {
            mActionView.setAction(new BackAction(), ActionView.ROTATE_CLOCKWISE);
            drawerLayout.openDrawer(leftLayout);
        }
    }

    /** 按下动作条的搜索栏 **/
    private void onSearchClick() {
        Intent intent = new Intent(Main.this, Search.class);
        startActivityForResult(intent, requestCode);
    }

    /** 按下定位按钮 **/
    private void onBtnNavigationClick() {
        if (mMapState == MAP_STATE_NORMAL) {
            updateLocation(mLastLocationFromGps, true);
            mMapState = MAP_STATE_NAVIGATION;
            onMapStateChanged();
        } else if (mMapState == MAP_STATE_NAVIGATION) {
            mMapState = MAP_STATE_NORMAL;
            onMapStateChanged();
        }
    }

    /** 当地图状态改变时 **/
    private void onMapStateChanged() {
        switch (mMapState) {
            case MAP_STATE_NORMAL:
                mBtnLocation.setDrawableIcon(getResources().getDrawable(R.drawable.ic_my_location_white_48dp));
                break;
            case MAP_STATE_NAVIGATION:
                mBtnLocation.setDrawableIcon(getResources().getDrawable(R.drawable.ic_navigation_white_48dp));
                break;
            default:
                // do nothing.
        }
    }

    /** 开启导航（实时跟踪位置）模式 **/
    private void startNavigationMode() {

    }

    /** 关闭导航（实时跟踪位置）模式 **/
    private void stopNavigationMode() {

    }

    /** 更新Activity的UI **/
    private void updateUi() {
        if (mMapState != MAP_STATE_NAVIGATION) {
            updateCoordinate();
            updateScale();
        }
        updateLongitude();
        updateLatitude();
    }

    /** 更新坐标 **/
    private void updateCoordinate() {
        Point centerPoint = Util.getCenterPoint(this);
        if (centerPoint != null && map != null) {
            Point mapPoint = map.toMapPoint(centerPoint);
            if (mapPoint != null) {
                String x = String.valueOf(mapPoint.getX()).substring(0, 8);
                String y = String.valueOf(mapPoint.getY()).substring(0, 8);
                mTvCoordinate.setText(x + ", " + y);
            }
        }
    }

    /** 更新比例 **/
    private void updateScale() {
        double currentScale = map.getScale();
        int scale = (int) currentScale;
        mTvScale.setText("比例 1:" + scale);
    }

    /** 更新经度 **/
    private void updateLongitude() {
        if (mLastLocationFromGps != null) {
            double longitude = mLastLocationFromGps.getLongitude();
            String longitudeStr = String.valueOf(longitude).substring(0, 9);
            if (longitude > 0) {
                longitudeStr += "°E";
            } else {
                longitudeStr += "°W";
            }
            mTvLongitude.setText("经度：" + longitudeStr);
        }

    }

    /** 更新纬度 **/
    private void updateLatitude() {
        if (mLastLocationFromGps != null) {
            double latitude = mLastLocationFromGps.getLatitude();
            String latitudeStr = String.valueOf(latitude).substring(0, 8);
            if (latitude > 0) {
                latitudeStr += "°N";
            } else {
                latitudeStr += "°S";
            }
            mTvLatitude.setText("纬度：" + latitudeStr);
        }
    }

    /** 手动更新坐标 **/
    private void forceUpdateCoordinate(double x, double y) {
        String xStr = String.valueOf(x).substring(0, 8);
        String yStr = String.valueOf(y).substring(0, 8);
        mTvCoordinate.setText(xStr + ", " + yStr);
    }

    /** 手动更新比例 **/
    private void forceUpdateScale(double scale) {
        int scaleInt = (int) scale;
        mTvScale.setText("比例 1:" + scaleInt);
    }

    /**
     * 在地图上更新显示当前位置
     * @param location 当前位置
     * @param isMapMoved 如果为true，则移动地图，以当前位置为中心；如果为false，则只更新显示当前位置
     */
    private void updateLocation(Location location, boolean isMapMoved) {
        if (location != null)
        {
            mCurrentLocationPoint = new Point(location.getLongitude(), location.getLatitude());
            // 校正坐标点
            mCurrentLocationPoint = Util.checkPoint(mCurrentLocationPoint);

            if (isFirst) {
                // 第一次加
                pictureMarkerSymbol = new PictureMarkerSymbol(mLocationDrawable);
                Graphic graphic = new Graphic(mCurrentLocationPoint, pictureMarkerSymbol);
                mLocationGraphicId = mLocationGraphicsLayer.addGraphic(graphic);
                isFirst = false;
            } else {
                pictureMarkerSymbol = new PictureMarkerSymbol(mLocationDrawable);
                Graphic graphic = new Graphic(mCurrentLocationPoint, pictureMarkerSymbol);
                mLocationGraphicsLayer.updateGraphic(mLocationGraphicId, graphic);
            }

            if (isMapMoved) {
                map.centerAt(mCurrentLocationPoint, true);
                map.setScale(1200.0000);
                forceUpdateScale(1200);
                forceUpdateCoordinate(mCurrentLocationPoint.getX(), mCurrentLocationPoint.getY());
            } else {
                updateUi();
            }

        }
    }

    /** 实时更新位置模式 **/
    private void stopUpdatePositionMode() {
        mIsUpdatePositionStarted = false;
        locationManager.removeGpsStatusListener(mGpsStatusListener);
        locationManager.removeUpdates(mLocationListener);
    }

    /** 开启实时更新位置模式（只更新位置图标，不会移动屏幕位置） **/
    private void startUpdatePositionMode() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent intent = new Intent(Main.this, Dialog.class);
            startActivity(intent);
            return;
        }

        String bestProvider = locationManager.getBestProvider(getCriteria(), true);
        //获取位置信息
        //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            mLastLocationFromGps = location;
            updateLocation(location, false);
            updateUi();
            //监听状态
            locationManager.addGpsStatusListener(mGpsStatusListener);

            // 绑定监听，有4个参数
            // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
            // 参数2，位置信息更新周期，单位毫秒
            // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息，单位为米
            // 参数4，监听器
            // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
            // 1秒更新一次，或最小位移变化超过1米更新一次；
            // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
        } else {
            Toast.makeText(Main.this, "定位失败", Toast.LENGTH_SHORT).show();
        }
    }

    /** 位置变化监听器 **/
    private LocationListener mLocationListener = new LocationListener() {

        /** 位置信息变化时触发 **/
        public void onLocationChanged(Location location) {
            mLastLocationFromGps = location;
            updateUi();
            boolean isMapMoved;
            isMapMoved = mMapState == MAP_STATE_NAVIGATION ? true : false;
            updateLocation(location, isMapMoved);

            Log.v(TAG, "时间：" + location.getTime());
            Log.v(TAG, "经度：" + location.getLongitude());
            Log.v(TAG, "纬度：" + location.getLatitude());
            Log.v(TAG, "海拔：" + location.getAltitude());
            Log.v(TAG, "精度：" + location.getAccuracy());
            Log.v(TAG, "速度" + location.getSpeed());

        }

        /** GPS状态变化时触发 **/
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /** GPS开启时触发 **/
        public void onProviderEnabled(String provider) {
            // TODO 判断GPS开启时是否需要做些事
        }

        /** GPS禁用时触发 **/
        public void onProviderDisabled(String provider) {
            // TODO 判断GPS禁用时是否需要做些事
        }
    };

    /** GPS状态变化监听器 **/
    GpsStatus.Listener mGpsStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位成功
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.v(TAG, "卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    Log.v(TAG, "搜索到：" + count + "颗卫星");
                    break;
                // 启动定位
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.v(TAG, "启动定位");
                    break;
                // 中止定位
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.v(TAG, "中止定位");
                    break;
            }
        }
    };

    /**
     * 返回查询条件
     * @return
     */
    private Criteria getCriteria(){
        Criteria criteria = new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

}