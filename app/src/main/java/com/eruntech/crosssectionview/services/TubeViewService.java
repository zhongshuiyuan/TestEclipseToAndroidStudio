package com.eruntech.crosssectionview.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.eruntech.crosssectionview.interfaces.OnTubeViewServiceListener;
import com.eruntech.crosssectionview.valueobjects.HoleInXml;
import com.eruntech.crosssectionview.valueobjects.TubePadInXml;
import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

/**
 * <p>在管道截面展开图中用于和服务端交互数据
 * <p>通过new出一个该类，然后调用实例方法{@link #getTubePadInfo(String)}获取管道截面展开图的数据
 * @author 覃远逸
 */
public class TubeViewService
{
	private final String DEBUG_TAG = "debug " + this.getClass().getSimpleName();
	private final boolean DEBUG = true;

	private Context mContext;
	private OnTubeViewServiceListener mOnTubeViewServiceListener;

	/** namespace **/
	private String ns = null;

	/** 从服务端获取的管道截面展开图管块数据对象 **/
	private TubePadInXml mTubePadInXml;
	/** 获取{@link #mTubePadInXml} **/
	public TubePadInXml getTubeData()
	{
		return mTubePadInXml;
	}

	/** 从服务端获取的管道截面展开图管孔数据对象 **/
	private List<HoleInXml> mHolesInXml = null;
	/** 获取{@link #mHolesInXml} **/
	public List<HoleInXml> getHolesInXml()
	{
		return mHolesInXml;
	}

	public TubeViewService(Context context)
	{
		mContext = context;
		if (context instanceof OnTubeViewServiceListener) {
			mOnTubeViewServiceListener = (OnTubeViewServiceListener) context;
		} else {
			Log.w(DEBUG_TAG, "传进本类的Context必须实现OnTubeViewServiceListener接口");
		}
	}

	/**
	 * 从服务端获取管块信息
	 * @param tubePadId 管块ID
	 * @throws IOException
	 */
	public void getTubePadInfo(long tubePadId) throws IOException
	{

		StringWriter writer = new StringWriter();

		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);

		serializer.startTag(null, "root");
		serializer.startTag(null, "action");
		serializer.attribute(null, "actionname", "wgs0037");
		serializer.startTag(null, "record");
		serializer.attribute(null, "type", "query");

		serializer.startTag(null, "field");
		serializer.attribute(null, "name", "id");
		serializer.attribute(null, "type", "string");
		serializer.attribute(null, "value", Long.toString(tubePadId));

		serializer.endTag(null, "field");
		serializer.endTag(null, "record");
		serializer.endTag(null, "action");
		serializer.endTag(null, "root");
		serializer.endDocument();

		String xmlSendToServer = writer.toString();
		if(DEBUG) {
			Log.d(DEBUG_TAG, "发送到服务端的xml为：" + xmlSendToServer);

		}

		serializer.flush();

		writer.flush();
		writer.close();

		new GetTubePadDataAsyncTask().execute(xmlSendToServer);
	}

	/**
	 * 向服务端获取数据
	 * @param sendToServer 需要传到服务端的数据
	 * @return 从服务端获取的数据
	 * @throws Exception
	 */
	private String requestFromServer(String sendToServer) throws Exception
	{
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("sys_request_xml", sendToServer));

		String serverURL = mContext.getString(R.string.config_tubeview_server_url);

		HttpPost post = new HttpPost(serverURL);
		post.setEntity(new UrlEncodedFormEntity(params,"utf-8"));

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response;

		response = httpClient.execute(post);

		String xmlGetFromServer = EntityUtils.toString(response.getEntity(),"utf-8");

		if (DEBUG) {
			Log.d(DEBUG_TAG, "从服务端接收到的xml为：" + xmlGetFromServer);
		}

		return xmlGetFromServer;
	}

	/**
	 * 解析xml数据
	 * @param stream xml输入流
	 */
	private void parseXmlData(InputStream stream) throws XmlPullParserException, IOException
	{
		/* 
		 * 从服务端获取的xml文件的格式如下：
		 * <root>
		 * 	<action> //一个<action>，一个管块对应一个action
		 * 		<record> //n个<record>，一个管孔对应一个record
		 * 			<field/>
		 * 			...6个<field/>
		 * 			<field/>
		 * 		</record>
		 * 	</action>
		 * </root>
		 */
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(stream, "UTF-8");
		parser.next();

		try {
			mHolesInXml = readRoot(parser);
		} catch (Exception e) {
			Log.e(DEBUG_TAG, e.getMessage());
		} finally {
			stream.close();

			if (DEBUG) {
				if (mHolesInXml != null) {
					Log.d(DEBUG_TAG, "有" + mHolesInXml.size() + "个管孔");
				}
			}

			if (mHolesInXml != null && mTubePadInXml != null && mOnTubeViewServiceListener != null) {
				mOnTubeViewServiceListener.onTubeViewDataGot(mTubePadInXml, mHolesInXml);
			} else {
				Toast.makeText(mContext, R.string.tubeviewservice_toast_fail_to_get_tubepad_data, Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 读取root标签
	 * @param parser 解析xml的parser，确保parser的位置位于root标签的startTag
	 * @return 包含所有管孔信息的List
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private List<HoleInXml> readRoot(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, ns, "root");

		List<HoleInXml> records = null;

		while (parser.next() != XmlPullParser.END_TAG) {

			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equalsIgnoreCase("action")) {
				try {
					records = readAction(parser);
				} catch (Exception e) {
					Log.e(DEBUG_TAG, e.getMessage());
				}
			} else {
				Log.e(DEBUG_TAG, "root标签的下级标签应为action，请检查从服务端接收的xml文件的正确性");
			}
		}

		return records;
	}

	/**
	 * 读取action标签，读取过程中会为{@link #mTubePadInXml}赋值
	 * @param parser 解析xml的parser，确保parser的位置位于action标签的startTag
	 * @return 包含所有管孔信息的List
	 * @throws Exception
	 */
	private List<HoleInXml> readAction(XmlPullParser parser) throws Exception
	{
		// 不知为何，下面这句检查parser位置的语句报错
		//parser.require(XmlPullParser.START_TAG, ns, "aciton");

		List<HoleInXml> records = new ArrayList<HoleInXml>();

		// 处理管块数据
		// <action result="true" type="1" atti="广州路（西）#3,广州路（西）#4,450.0,300.0,3,2">
		String tubePadData = parser.getAttributeValue(ns, "atti");
		String[] tubePadDatas = tubePadData.split(",");
		if (tubePadDatas.length == TubePadInXml.attributeCount) {
			mTubePadInXml = new TubePadInXml(tubePadDatas[0], tubePadDatas[1], tubePadDatas[2], tubePadDatas[3], tubePadDatas[4], tubePadDatas[5]);
		} else {
			Log.e(DEBUG_TAG, "从服务端获取的管块数据异常，管道ID错误或者不存在");
			throw new Exception("从服务端获取的管块数据异常，管道ID错误或者不存在");
		}

		//处理管孔数据
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			if (parser.getName().equalsIgnoreCase("record")) {
				records.add(readRecord(parser));
			}

		}

		return records;
	}

	/**
	 * 读取record标签，遍历record标签下的field标签，构建管孔数据
	 * @param parser 解析xml的parser，确保parser的位置位于record标签的startTag
	 * @return 管孔数据对象
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private HoleInXml readRecord(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, ns, "record");

		HoleInXml hole = null;
		String holeId = null;
		String status = null;
		String owner = null;
		String material = null;
		String pipId = null;
		String childId = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			if (parser.getName().equalsIgnoreCase("field")) {
				String attributeName = parser.getAttributeValue(ns, "name");
				if (attributeName.equalsIgnoreCase("holeid")) {
					holeId = parser.getAttributeValue(ns, "value");
				} else if (attributeName.equalsIgnoreCase("statucs"/*后台拼写错误，忽略就行*/)) {
					status = parser.getAttributeValue(ns, "value");
				} else if (attributeName.equalsIgnoreCase("owner")) {
					owner = parser.getAttributeValue(ns, "value");
				} else if (attributeName.equalsIgnoreCase("material")) {
					material = parser.getAttributeValue(ns, "value");
				} else if (attributeName.equalsIgnoreCase("pipid")) {
					pipId = parser.getAttributeValue(ns, "value");
				} else if (attributeName.equalsIgnoreCase("childids")) {
					childId = parser.getAttributeValue(ns, "value");
				}
				parser.nextTag();
			} else {
				Log.w(DEBUG_TAG, "record下出现非field标签");
			}
		}

		hole = new HoleInXml(holeId, status, owner, material, pipId, childId);

		return hole;
	}

	/** 用于向服务端请求数据的异步线程类 **/
	private class GetTubePadDataAsyncTask extends AsyncTask<String, Object, String>
	{
		/**
		 * 向服务端获取数据
		 */
		@Override
		protected String doInBackground(String... params) {
			String result = null;
			if (params.length != 1) {
				Log.w(DEBUG_TAG, "向后台传送的管块ID大于1个，无法判断具体需要传哪个到后台");
			}
			try {
				result = requestFromServer(params[0]);
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "无法从服务端取回数据，服务端地址出错或者后台未打开");
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "requestFromServerError: " + e.getMessage());
			}
			return result;
		}

		/**
		 * 处理获取的数据
		 */
		@Override
		protected void onPostExecute(String result)
		{
			int beginIndex = result.indexOf("<root>");
			int endIndex = result.indexOf("</root>");
			result = result.substring(beginIndex, endIndex + "</root>".length());
			result = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + result;
			InputStream stream = null;
			try {
				stream = new ByteArrayInputStream(result.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Log.e(DEBUG_TAG, "xml转换成输入流时发生错误，错误原因：" + e.getMessage());
			}
			try {
				parseXmlData(stream);
			} catch (XmlPullParserException e) {
				Log.e(DEBUG_TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(DEBUG_TAG, e.getMessage());
			}
		}
	}


}
