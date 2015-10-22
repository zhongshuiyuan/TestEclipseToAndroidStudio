package com.qingdao.shiqu.arcgis.services;

import java.io.StringWriter;
import java.util.UUID;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlSerializer;

import Eruntech.BirthStone.Core.Helper.Basic;
import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;
//import android.os.StrictMode;
/**
 * web服务访问类
 * @author MinG
 *
 */
public class CatvService
{

	private String methodName = "sendPdaXmlInfo";
	private String nameSpace = "http://trans.pda.catv.st/";
	//private String URL = "http://192.168.1.88:7005/services/WebServicesBoss?wsdl";
	private String URL = "http://202.136.60.124:8005/services/WebServicesBoss?wsdl";
	//private String URL = "http://202.136.60.11/services/WebServicesBoss?wsdl";

	private XmlSerializer serializer;
	private StringWriter writer;

	private String XML;

	protected static String IP;
	protected static String OperNo;
	public static String DeviceNo;
	public static String Phone;
	
	
	private String bussType;
	private final UUID uuid = UUID.randomUUID();
	private DataCollection dataCollection = new DataCollection();
	private DataTable dataTable;
	private String netSeqNo;
	
	private boolean postResult;
	private Context context;
	private boolean showMessage=false;
	private String message="";
	/***
	 * 登录状态
	 */
	public static boolean IsLogin=false;
	public CatvService( String URL, String nameSpace )
	{
		netSeqNo = uuid.toString();
		this.nameSpace = nameSpace;
		this.URL = URL;
	}

	public CatvService( Context context )
	{
		netSeqNo = uuid.toString();
		this.context = context;
	}

	public CatvService( )
	{
		netSeqNo = uuid.toString();
	}

	public CatvService( DataCollection datas )
	{
		netSeqNo = uuid.toString();
		this.dataCollection = datas;
	}
	
	/**
	 * 获取用户信息
	 * @param context
	 * @throws Exception 
	 */
	public void getUserInfo(Context context) throws Exception
	{
		try
		{
			SharedPreferences sharedPreferences = context.getSharedPreferences("checked", Context.MODE_PRIVATE);
			OperNo = sharedPreferences.getString("username", "");
			DeviceNo = sharedPreferences.getString("deviceNo", "");
			Phone = sharedPreferences.getString("phone", "");
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	/*
	 * 生成本次操作XML描述
	 */
	public void convertXML() throws Exception
	{
		serializer = Xml.newSerializer();
		writer = new StringWriter();
		try
		{
			if(OperNo==null)
			{
				getUserInfo(context);
			}
			serializer.setOutput(writer);
			serializer.startDocument("UTF8", true);

			serializer.startTag("", "root");
			serializer.startTag("", "action");
			serializer.attribute("", "type", "1");
			serializer.attribute("", "result", "true");
			serializer.attribute("", "netseqno", netSeqNo.replace('-', '1'));
			serializer.attribute("", "operno", OperNo);
			serializer.attribute("", "phone", Phone);
			serializer.attribute("", "pdano", DeviceNo);
			serializer.attribute("", "pdabusstype", bussType);
			serializer.startTag("", "record");
			for(int i = 0; i < dataCollection.size(); i++)
			{
				serializer.startTag("", "field");
				Data data = dataCollection.get(i);
				serializer.attribute("", "name", data.getName());
				serializer.attribute("", "type", "text");
				if(data.getValue() != null)
				{
					serializer.attribute("", "value",Basic.encode(data.getValue().toString()));
				}
				else
				{
					serializer.attribute("", "value", "");
				}
				serializer.endTag("", "field");
			}
			serializer.endTag("", "record");
			serializer.endTag("", "action");
			serializer.endTag("", "root");
			serializer.endDocument();
			XML = writer.toString();
			writer.close();
			Log.i("localhostXML", XML);
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * 访问Web服务
	 * 
	 * @return 获取执行结果
	 */
	public boolean post()
	{
		try
		{
			if(Network.checkNetwork(context, true))
			{
				if(submit() && XML.length() > 20)
				{
					SaxParseService sax = new SaxParseService();
					dataTable = sax.getTable(XML);
					message = sax.getMessage();
					if(showMessage && sax.getMessage() != null && !"".equals(sax.getMessage().trim()))
					{
						Toast.makeText(context, sax.getMessage().trim(), Toast.LENGTH_LONG).show(); 
//						MessageBox.showMessage((Activity) context, "消息", sax.getMessage());
					}
					netSeqNo = sax.getNetseqno();
					return sax.getResult();
				}
			}
		}
		catch(Exception ex)
		{
			Toast.makeText(context, "无法连接到远程服务器或服务器无响应！", Toast.LENGTH_LONG).show(); 
			Log.e("BOSS Error:", ex.getMessage());
		}
		return false;
	}

	/**
	 * 访问Web服务
	 * 
	 * @param methodName 调用web端方法名称
	 * @return 获取执行结果
	 */
	public boolean post(String methodName)
	{
		try
		{
			if(Network.checkNetwork(context, true))
			{
				if(submit(methodName) && XML.length() > 20)
				{
					SaxParseService sax = new SaxParseService();
					try
					{
						sax.getTable(XML);
						message = sax.getMessage();
						if(showMessage && sax.getMessage() != null && !"".equals(sax.getMessage().trim()))
						{
							Toast.makeText(context, sax.getMessage().trim(), Toast.LENGTH_LONG).show(); 
//							MessageBox.showMessage((Activity) context, "消息", sax.getMessage());
						}
					}
					catch(Exception ex)
					{
						Log.e("BOSS Error:", ex.getMessage());
					}
					return true;
				}
			}
			else
			{
				
			}
		}
		catch(Exception ex)
		{
			Toast.makeText(context, "无法连接到远程服务器或服务器无响应！", Toast.LENGTH_LONG).show(); 
			Log.e("BOSS Error:", ex.getMessage());
		}
		return false;
	}

	/**
	 * 访问webservice服务
	 * 
	 * @return 是否执行成功
	 */
	private boolean submit()
	{
		try
		{
			convertXML();
			SoapObject soap = new SoapObject(nameSpace, methodName);
			soap.addProperty("arg0", XML);
			SoapSerializationEnvelope Envelope = new

			SoapSerializationEnvelope(SoapEnvelope.VER11);
			Envelope.setOutputSoapObject(soap);
			AndroidHttpTransport AndHttpTransport = new	AndroidHttpTransport(URL);
			AndHttpTransport.debug = true;
			AndHttpTransport.call(null, Envelope);
			SoapObject so = (SoapObject) Envelope.bodyIn;
			int size = so.getPropertyCount();
			for(int i = 0; i < size; i++)
			{
				if(so.getProperty(i) != null)
				{
					XML = so.getProperty(i).toString();
				}
			}
			Log.i("BossXML", XML);
			return true;
		}
		catch(Exception ex)
		{
			Log.e("submit", ex.getMessage());
		}
		return false;
	}

	/**
	 * 访问webservice服务
	 * 
	 * @param methodName 调用web端方法名称
	 * @return
	 */
	private boolean submit(String methodName)
	{
		try
		{
			convertXML();
			SoapObject soap = new SoapObject(nameSpace, methodName);
			soap.addProperty("arg0", XML);
			SoapSerializationEnvelope Envelope = new

			SoapSerializationEnvelope(SoapEnvelope.VER11);
			Envelope.setOutputSoapObject(soap);
			AndroidHttpTransport AndHttpTransport = new

			AndroidHttpTransport(URL);
			AndHttpTransport.debug = true;
			AndHttpTransport.call(null, Envelope);
			SoapObject so = (SoapObject) Envelope.bodyIn;
			int size = so.getPropertyCount();
			for(int i = 0; i < size; i++)
			{
				if(so.getProperty(i) != null)
				{
					XML = so.getProperty(i).toString();
				}
			}
			//Log.i("BossXML", XML);
			return true;
		}
		catch(Exception ex)
		{
			Log.e("submit", ex.getMessage());
		}
		return false;
	}

	public String getXML()
	{
		return XML;
	}

	public void setXML(String xML)
	{
		XML = xML;
	}

	public void setDataCollection(DataCollection dataCollection)
	{
		this.dataCollection = dataCollection;
	}

	public DataTable getDataTable()
	{
		return dataTable;
	}

	public void setDataTable(DataTable dataTable)
	{
		this.dataTable = dataTable;
	}

	public String getNetSeqNo()
	{
		return netSeqNo;
	}

	public void setNetSeqNo(String netSeqNo)
	{
		this.netSeqNo = netSeqNo;
	}

	public boolean isPostResult()
	{
		return postResult;
	}

	public void setPostResult(boolean postResult)
	{
		this.postResult = postResult;
	}

	public Context getContex()
	{
		return context;
	}

	public void setContex(Context contex)
	{
		this.context = contex;
	}

	public boolean isShowMessage()
	{
		return showMessage;
	}

	public void setShowMessage(boolean showMessage)
	{
		this.showMessage = showMessage;
	}

	public static String getIP()
	{
		return IP;
	}

	public static void setIP(String iP)
	{
		IP = iP;
	}

	public static String getOperNo()
	{
		return OperNo;
	}

	public static void setOperNo(String operNo)
	{
		OperNo = operNo;
	}

	public static String getDeviceNo()
	{
		return DeviceNo;
	}

	public static void setDeviceNo(String deviceNo)
	{
		DeviceNo = deviceNo;
	}

	public static String getPhone()
	{
		return Phone;
	}

	public static void setPhone(String phone)
	{
		Phone = phone;
	}

	public String getBussType()
	{
		return bussType;
	}

	public void setBussType(String bussType)
	{
		this.bussType = bussType;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

}
