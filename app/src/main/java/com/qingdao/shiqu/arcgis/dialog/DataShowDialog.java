package com.qingdao.shiqu.arcgis.dialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Authenticator.RequestorType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlSerializer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

//import com.gc.materialdesign.widgets.Dialog;
import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.activity.RJInforActivity;
import com.qingdao.shiqu.arcgis.adapter.CheckboxAdapter;
import com.qingdao.shiqu.arcgis.adapter.MyAdapter;
import com.qingdao.shiqu.arcgis.utils.RJInformation;

import Eruntech.BirthStone.Core.Parse.Data;

public class DataShowDialog {



	private static String METHODNAME = "sendPdaXmlInfo";
	private static String NAMESPACE = "http://trans.pdamid.catv.st/";
	private static String URL = "http://192.168.9.45:7001/BI/util/SqlExcuteJsp.jsp?sys_request_xml=";
	private static String XML;
	/**
	 * 说 明：将数据，使用自己的Dialog进行显示
	 * @param list 数据列表集合，集合中使用Data类型，Data中的Name保存属性名，Value保存属性值
	 * @param titile 设置的对话框标题
	 * @param context 上下文
	 */
	public static void showDialog_List(List<Data> list,String titile,Context context){
		List  strarr = new ArrayList<String>();
		String[] title = new String[list.size()];
		String[] text = new String[list.size()];
		for(int index=0;index<list.size();index++){
			if(list.get(index).getValue() == null){
				title[index] = list.get(index).getName()+": ";
			}else{
				title[index] = list.get(index).getName()+":";
				text[index] = list.get(index).getValue().toString();
			}
		}
		ListAdapter mAdapter = new ArrayAdapter(context, R.layout.item, title);

		AlertDialog alertDialog = new AlertDialog.Builder(context)
		.setIcon(R.drawable.logo)
		.setTitle(titile)
		.setAdapter(/*new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,at)*/new MyAdapter(context, title,text), null)
		.setNegativeButton("确定", null)
		.create();
		Window window = alertDialog.getWindow();        
		WindowManager.LayoutParams lp = window.getAttributes();        
		// 设置透明度为0.3         
		lp.alpha = 0.9f;
		window.setAttributes(lp);        
		alertDialog.show();
	}
	public static String[] gdIDs;
	public static void showDialog_StringArray(String[] list,String titile,Context context){
		final CheckboxAdapter ca = new CheckboxAdapter(context, list,gdIDs);
		final List<Integer> listItemID = new ArrayList<Integer>();
		final Context cont = context;

		AlertDialog alertDialog = new AlertDialog.Builder(context)
		.setIcon(R.drawable.logo)
		.setTitle(titile)
		.setAdapter(ca,null)
		.setNegativeButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO 自动生成的方法存根
				listItemID.clear();
				for(int i=0;i<ca.mChecked.size();i++){
					if(ca.mChecked.get(i)){
						listItemID.add(i);
					}
				}
				if(listItemID.size()==0){
					AlertDialog.Builder builder1 = new AlertDialog.Builder(cont);
					builder1.setMessage("没有选中任何记录");
					builder1.show();
				}else{
					StringBuilder sb = new StringBuilder();

					String[] aGD = new String[listItemID.size()];
					for(int i=0;i<listItemID.size();i++){
						sb.append("gdID="+ca.gdIDs[i]+" . ");
						//                        sb.append("ItemID="+listItemID.get(i)+" . ");
						aGD[i] = ca.gdIDs[i];
					}
					/*AlertDialog.Builder builder2 = new AlertDialog.Builder(cont);
                    builder2.setMessage(sb.toString());
                    builder2.show();*/
					Writer writer = new StringWriter();
					try {
						save1(aGD, writer, cont);
					} catch (Throwable e) {
						// TODO 自动生成的 catch 块
						Log.e("error", e.getMessage());
						e.printStackTrace();
					}
				}
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
	public static OnClickListener createListener(){
		return null;
	}
	public static void showDialog_Record(){

	}
	private static class myDialog extends AlertDialog{

		protected myDialog(Context context, boolean cancelable,
				OnCancelListener cancelListener) {
			super(context, cancelable, cancelListener);
			// TODO 自动生成的构造函数存根
		}

		@Override
		public Window getWindow() {
			// TODO 自动生成的方法存根
			return super.getWindow();
		}


	}
	/*
	 * var httpURL:String = "http://192.168.9.45:7001/BI/util/SqlExcuteJsp.jsp?sys_request_xml="
	 * */
	public static void save1(String[] gds, Writer writer,Context context) throws Throwable{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);

		serializer.startTag(null, "root");
		serializer.startTag(null, "action");
		serializer.attribute(null, "actionname", "wgs0005");
		serializer.startTag(null, "record");
		serializer.attribute(null, "type", "query");
		for(int j=0;j<gds.length;j++){
			serializer.startTag(null, "field");
			serializer.attribute(null, "name","id"+String.valueOf(j) );
			serializer.attribute(null, "type","string" );
			serializer.attribute(null, "value",gds[j] );
			serializer.endTag(null, "field");
		}
		serializer.endTag(null, "record");
		serializer.endTag(null, "action");
		serializer.endTag(null, "root");		
		serializer.endDocument();
		/*AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
		builder2.setMessage(writer.toString());*/
		/////
		XML = writer.toString();
		RequestToServer(context);
		/////
//		builder2.show();
		writer.flush();
		writer.close();
	}
	private static boolean RequestToServer(Context context) throws Exception{
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();  
		params.add(new BasicNameValuePair("sys_request_xml", XML));  

		String param  = URLEncodedUtils.format(params, "utf-8");
		String basURL = "http://192.168.9.45:7001/BI/util/SqlExcuteJsp.jsp";
		HttpPost post = new HttpPost(basURL);
		post.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse resp = httpClient.execute(post);

		String xml_a = EntityUtils.toString(resp.getEntity(),"utf-8");
		Bundle bund = new Bundle();
		bund.putString("xml", xml_a);
		Intent inte = new Intent(context,RJInforActivity.class);
		inte.putExtras(bund);
		context.startActivity(inte);
		return true;
	}
}
