package com.qingdao.shiqu.arcgis.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.R.layout;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.activity.BS_Role;
import com.qingdao.shiqu.arcgis.adapter.MyAdapter;
import com.qingdao.shiqu.arcgis.helper.FunctionHelper;

public class LocalDataHelp {
	SQLiteDatabase db = null;
	Context context;
	GraphicsLayer layer;


	public LocalDataHelp(Context context,GraphicsLayer layer) {
		super();
		this.context = context;
		this.layer = layer;
		db = new SQLiteDatabase(context);
	}
	public  void AddEleOfPoint(Point point){
		Graphic grahpic = new Graphic(point, new SimpleMarkerSymbol(Color.YELLOW, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
		layer.addGraphic(grahpic);
//		savePoint(point);
		saveSegment();
	}
	public  void AddEleOfSegment(ArrayList<Point> points){
		for(int i=0;i<points.size();i++){
			points.get(i);
			Polyline pline = new Polyline();
			pline.lineTo(points.get(i));
			Graphic graphic = new Graphic(pline, new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH));
			layer.addGraphic(graphic);
		}
	}

	public void savePoint(final Point p){

		LayoutInflater factory = LayoutInflater.from(context);
		final View pView = factory.inflate(R.layout.localpoint, null);
		AlertDialog alertDialog = new AlertDialog.Builder(context)
		.setIcon(R.drawable.logo)
		.setTitle("节点属性录入")
		.setView(pView)
		.setNegativeButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO 自动生成的方法存根
				EditText id = (EditText)pView.findViewById(R.id.id);
				EditText name = (EditText)pView.findViewById(R.id.name);

				DataCollection params = new DataCollection();
				params.add(new Data("gjid", id.getText().toString()));
				params.add(new Data("gjname",name.getText().toString()));
				params.add(new Data("x", p.getX()));
				params.add(new Data("y", p.getY()));
				params.add(new Data("operator",FunctionHelper.userName));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");     
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
				String str = formatter.format(curDate);   
				params.add(new Data("time",str));
				db = new SQLiteDatabase(context);
				db.execute("aqBS_LocalDataAdd", params);
			}
		})
		.create();
		Window window = alertDialog.getWindow();        
		WindowManager.LayoutParams lp = window.getAttributes();        
		// 设置透明度为0.3         
		lp.alpha = 0.6f;   
		window.setAttributes(lp);        
		alertDialog.show();

	}
	public void saveSegment(){
		RelativeLayout relativeLayout = new RelativeLayout(context);
		AbsoluteLayout abslayout=new AbsoluteLayout (context);
	    abslayout.setId(11);
	    TextView btn4 = new TextView(context);
	    btn4.setText("管道ID：");
	    btn4.setId(4);
	    EditText btn1 = new EditText(context);
	    btn1.setText("dsdsf");
	    btn1.setId(1);
	    AbsoluteLayout.LayoutParams lp0 = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
	    ViewGroup.LayoutParams.WRAP_CONTENT,200,0);
	    AbsoluteLayout.LayoutParams lp4 = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
	    	    ViewGroup.LayoutParams.WRAP_CONTENT,50,0);
	    abslayout.addView(btn1, lp0 );
	    abslayout.addView(btn4,lp4);
	    //将这个子布局添加到主布局中
	    RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	    lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
	    relativeLayout.addView(abslayout ,lp1);
	 
	    //再添加一个子布局
	    LinearLayout line1 = new LinearLayout(context);
	    line1.setOrientation(LinearLayout.HORIZONTAL);
	    TextView btn2 = new TextView(context);
	    btn2.setText("管道ID：");
	    btn2.setId(2);
	    EditText et1 = new EditText(context);
	    et1.setId(22);
	    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	    lp2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
	    line1.addView(btn2 ,lp2);
	    line1.addView(et1,lp2);
	 
	    //将这个布局添加到主布局中
	    RelativeLayout.LayoutParams lp11 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    lp11.addRule(RelativeLayout.BELOW ,11);
	    relativeLayout.addView(line1 ,lp11);
	    
        AlertDialog alertDialog = new AlertDialog.Builder(context)
		.setIcon(R.drawable.logo)
		.setTitle("节点属性录入")
		.setView(relativeLayout)
		.setNegativeButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {}
		})
		.create();
		Window window = alertDialog.getWindow();        
		WindowManager.LayoutParams lp = window.getAttributes();        
		// 设置透明度为0.3         
		lp.alpha = 0.6f;   
		window.setAttributes(lp);        
		alertDialog.show();
	}
}
