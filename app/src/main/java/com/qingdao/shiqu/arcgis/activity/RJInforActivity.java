package com.qingdao.shiqu.arcgis.activity;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import com.esri.core.internal.tasks.ags.ad;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.CenterfiberAdapter;
import com.qingdao.shiqu.arcgis.adapter.LeftfiberAdapter;
import com.qingdao.shiqu.arcgis.adapter.RightfiberAdapter;
import com.qingdao.shiqu.arcgis.handlers.PullRJParser;
import com.qingdao.shiqu.arcgis.handlers.PullRJParser01;
import com.qingdao.shiqu.arcgis.interfaces.RJParser;
import com.qingdao.shiqu.arcgis.utils.Fiber;
import com.qingdao.shiqu.arcgis.utils.RJInformation;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class RJInforActivity extends Activity {

	private static final String TAG = "XML";
	private RJParser parser;
	private List<RJInformation> books;
	ListView leftfiber = null;
	ListView rightfiber = null;
	ListView centertext = null;
	Spinner center = null;
	int equNum;					//设备个数
	String xml;

	private String[] name = null;
	private String[] lName = null;
	private String[] rName = null;
	private int[] lcolor = null;
	private int[] rcolor = null;
	private ArrayAdapter< String> adapter;
	protected void loadXMLName(){
		InputStream is;
		try {
			InputStream isx = new ByteArrayInputStream(xml.getBytes());
			parser = new PullRJParser01();
			String ts = isx.toString();
			books = parser.parse(isx);
			/*if(books.size() == 0){
				Toast.makeText(this, "无熔接信息！", Toast.LENGTH_SHORT).show();
				return;
			}*/
			name = new String[books.size()];
			int i=0;
			for(RJInformation book:books){
				//中间设备名称数组
				name[i] = book.getEquName();

				List<Fiber> lList = new ArrayList<Fiber>();
				List<Fiber> rList = new ArrayList<Fiber>();

				//所有光纤的list列表
				List<Fiber> fibers = book.getFibers();
				//循环所有的列表，分出左右
				for(int j=0;j<fibers.size();j++){
					if(fibers.get(j).getRorl() == 0){
						//左光纤
						lList.add(fibers.get(j));
					}
					else {
						//右光纤
						rList.add(fibers.get(j));
					}
				}
				lName = new String[lList.size()];
				lcolor = new int[lList.size()];
				for(int j=0;j<lList.size();j++){
					lName[j] = lList.get(j).getName();
					lcolor[j] = ChangColor(lList.get(j).getColor());
				}

				rName = new String[rList.size()];
				rcolor = new int[rList.size()];
				for(int j=0;j<rList.size();j++){
					rName[j] = rList.get(j).getName();
					rcolor[j] = ChangColor(rList.get(j).getColor());
				}
				i++;
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	protected int ChangColor(String color){
		int col = 0;
		if(color.equalsIgnoreCase("红色"))
			col = Color.RED;
		else if(color.equalsIgnoreCase("蓝色"))
			col = Color.BLUE;
		else if(color.equalsIgnoreCase("黄色"))
			col = Color.YELLOW;
		else if(color.equalsIgnoreCase("黑色"))
			col = Color.BLACK;
		else if(color.equalsIgnoreCase("绿色"))
			col = Color.GREEN;
		else if(color.equalsIgnoreCase("棕色"))
			col = Color.argb(255, 139, 69, 19);
		else if(color.equalsIgnoreCase("橙色"))
			col = Color.argb(255, 255, 165, 0);
		return col;
	}
	protected void loadItem(int i){
		List<Fiber> lList = new ArrayList<Fiber>();
		List<Fiber> rList = new ArrayList<Fiber>();

		RJInformation book = books.get(i);
		//所有光纤的list列表
		List<Fiber> fibers = book.getFibers();
		//循环所有的列表，分出左右
		for(int j=0;j<fibers.size();j++){
			if(fibers.get(j).getRorl() == 0){
				//左光纤
				lList.add(fibers.get(j));
			}
			else {
				//右光纤
				rList.add(fibers.get(j));
			}
		}
		lName = new String[lList.size()];
		lcolor = new int[lList.size()];
		for(int j=0;j<lList.size();j++){
			lName[j] = lList.get(j).getName();
			lcolor[j] = ChangColor(lList.get(j).getColor());
		}

		rName = new String[rList.size()];
		rcolor = new int[rList.size()];
		for(int j=0;j<rList.size();j++){
			rName[j] = rList.get(j).getName();
			rcolor[j] = ChangColor(rList.get(j).getColor());
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rjinforadapter);

		Bundle bund = this.getIntent().getExtras();
		xml = bund.getString("xml");

		loadXMLName();
		center = (Spinner)findViewById(R.id.center);
		leftfiber = (ListView)findViewById(R.id.leftfiber);
		rightfiber = (ListView)findViewById(R.id.rightfiber);
		centertext = (ListView)findViewById(R.id.centeimg);
		adapter = new ArrayAdapter<String>(this, R.layout.myspinneritem,name);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		center.setAdapter(adapter);
		center.setVisibility(View.VISIBLE);

		center.setOnItemSelectedListener(new OnItemSelectedListener() {


			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int index, long arg3) {
				// TODO 自动生成的方法存根
				for(int i=0;i<books.size();i++){
					if(i == index){
						loadItem(index);
						leftfiber.setAdapter(new LeftfiberAdapter(RJInforActivity.this,lName,lcolor));
						leftfiber.setVisibility(View.VISIBLE);
						rightfiber.setAdapter(new RightfiberAdapter(RJInforActivity.this, rName,rcolor));
						rightfiber.setVisibility(View.VISIBLE);
						/*OutputStream os = openFileOutput(name, mode)
						save(books, os);*/
						if(lName.length > rName.length)
							centertext.setAdapter(new CenterfiberAdapter(RJInforActivity.this,lName));
						else
							centertext.setAdapter(new CenterfiberAdapter(RJInforActivity.this, rName));
						//设置三个控件同时动
						setListViewHeight(leftfiber);
						setListViewHeight(rightfiber);
						setListViewHeight(centertext);
					}
				}

				/*switch (index) {
				case 0:
					loadItem(index);
					leftfiber.setAdapter(new LeftfiberAdapter(RJInforActivity.this,lName,lcolornew String[]{"ll1","ll2"}, new int[]{Color.BLUE,Color.BLACK}));
					leftfiber.setVisibility(View.VISIBLE);
					rightfiber.setAdapter(new RightfiberAdapter(RJInforActivity.this, rName,rcolornew String[]{"rr1","rr2"}, new int[]{Color.BLUE,Color.BLACK}));
					rightfiber.setVisibility(View.VISIBLE);
					break;

				case 1:
					loadItem(index);
					leftfiber.setAdapter(new LeftfiberAdapter(RJInforActivity.this,lName,lcolornew String[]{"ll1","ll2"}, new int[]{Color.BLUE,Color.BLACK}));
					leftfiber.setVisibility(View.VISIBLE);
					rightfiber.setAdapter(new RightfiberAdapter(RJInforActivity.this, rName,rcolornew String[]{"rr1","rr2"}, new int[]{Color.BLUE,Color.BLACK}));
					rightfiber.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}*/
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自动生成的方法存根
				leftfiber.setAdapter(null);
			}
		});

		/*if(lName.length > rName.length)
			centertext.setAdapter(new CenterfiberAdapter(RJInforActivity.this,lName));
		else
			centertext.setAdapter(new CenterfiberAdapter(RJInforActivity.this, rName));*/


	}

	public  void save(List<RJInformation> rj,OutputStream os)throws Exception{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(os,"utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "rjinfor");
		Toast.makeText(RJInforActivity.this, os.toString(), Toast.LENGTH_LONG).show();
	}
	public static void save1(List<RJInformation> rjList, Writer writer) throws Throwable{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);

		serializer.startTag(null, "rjinfor");
		for(RJInformation rjInfo : rjList){
			serializer.startTag(null, "equipment");
			serializer.attribute(null, "id",String.valueOf(rjInfo.getEquid()) );
			serializer.attribute(null, "name", rjInfo.getEquName());

			for(int i=0;i<rjInfo.getFibers().size();i++){
				serializer.startTag(null, "");
			}

			serializer.endTag(null, "equipment");
		}
		serializer.endTag(null, "rjinfor");		
		serializer.endDocument();
		writer.flush();
		writer.close();
	}
	public static void  setListViewHeight(ListView listview){    
	       int totalHeight = 0;      
	      ListAdapter adapter= listview.getAdapter();  
	      if(null != adapter){  
	       for (int i = 0; i <adapter.getCount(); i++) {      
	       View listItem = adapter.getView(i, null, listview);  
	        if (null != listItem) {  
	        listItem.measure(0, 0);//注意listview子项必须为LinearLayout才能调用该方法  
	        totalHeight += listItem.getMeasuredHeight();  
	        }  
	       }      
	             
	       ViewGroup.LayoutParams params = listview.getLayoutParams();      
	       params.height = totalHeight + (listview.getDividerHeight() * (listview.getCount() - 1));      
	       listview.setLayoutParams(params);    
	      }  
	   }

}
