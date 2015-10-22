package com.qingdao.shiqu.arcgis.utils;

import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlSerializer;

import com.qingdao.shiqu.arcgis.activity.RJInforActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;

public class XMLToServer {
	public static String XML ;

	public static String SendToServer(String[] gds, Writer writer,Context context) throws Throwable{

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
	
		return null;
	}
	private static boolean RequestToServer(Context context) throws Exception{
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();  
		params.add(new BasicNameValuePair("sys_request_xml", XML));  

		String param  = URLEncodedUtils.format(params, "utf-8");
		String basURL = "http://192.168.9.45:7001/BI/util/SqlExcuteJsp.jsp";
		String abc = basURL+"?"+params;
		
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
