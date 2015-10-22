package com.qingdao.shiqu.arcgis.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import android.util.Xml.Encoding;

import Eruntech.BirthStone.Core.Helper.Basic;
import Eruntech.BirthStone.Core.Helper.ValidatorHelper;
import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;

/**
 * XML解析服务
 * @author MinG
 *
 */
public class SaxParseService extends DefaultHandler
{
	private DataTable table;
	private DataCollection fields;
	private Data field;
	private String preTag = null;// 作用是记录解析时的上一个节点名称

	private static String message;// 記錄BOSS返回消息
	private static Boolean result; // 本次消息是否成功
	private static String netseqno;
	/**
	 * 获取本次操作集合
	 * @param xml
	 * @return
	 */
	public DataTable getTable(String xml)
	{
		SaxParseService handler = null;
		try
		{
			message = null;
			result = false;
			InputStream xmlStream = new ByteArrayInputStream(xml.getBytes(Encoding.UTF_8.toString()));
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			handler = new SaxParseService();
			parser.parse(xmlStream, handler);
		}
		catch(Exception ex)
		{
			Log.e("SAXError", ex.getMessage());
		}
		return handler.getTable();
	}

	private DataTable getTable()
	{
		return table;
	}

	@Override
	public void startDocument() throws SAXException
	{
		table = new DataTable();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if("record".equals(qName) || "sqlrow".equals(qName))
		{
			fields = new DataCollection();
		}
		if("action".equals(qName))
		{
			getAcitonattributes(attributes);
		}
		if("field".equals(qName))
		{
			getField(attributes);
		}

		preTag = qName;// 将正在解析的节点名称赋给preTag
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if("field".equals(qName))
		{
			fields.add(field);
			field = null;
		}
		if("record".equals(qName) || "sqlrow".equals(qName))
		{
			table.add(fields);
			fields = null;
		}
		preTag = null;
	}

	private void getField(Attributes attributes)
	{
		if(attributes.getValue(0).contains("_"))
		{
			String value = attributes.getValue("value");
			if(!ValidatorHelper.isNumber(value))
			{
				field = new Data();
				field.setName(attributes.getValue(0).split("_")[1].toString());
				field.setValue(Basic.decode(value));
			}
			else
			{
				field = new Data();
				field.setName(attributes.getValue(0).split("_")[1].toString());
				field.setValue(value);
			}
		}
		else
		{
			String value = attributes.getValue("value");
			if(!ValidatorHelper.isNumber(value))
			{
				field = new Data();
				field.setName(attributes.getValue(0));
				field.setValue(Basic.decode(value));
			}
			else
			{
				field = new Data();
				field.setName(attributes.getValue(0));
				field.setValue(value);
			}
		}
	}

	private void getAcitonattributes(Attributes attributes)
	{
		if(attributes.getValue(1).contains("true"))
		{
			result = true;
		}
		else
		{
			result = false;
		}
		if(attributes.getValue(2)!=null)
		{
			netseqno = attributes.getValue(2).toString();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if(preTag != null)
		{
			String content = new String(ch, start, length);
			if("action".equals(preTag))
			{
				message = Basic.decode(content);
				Log.e("BOSS消息", message);
			}
		}
	}

	/**
	 * 获取XML解析消息
	 * @return
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * 获取本次执行结果
	 * @return
	 */
	public Boolean getResult()
	{
		return result;
	}

	public String getNetseqno()
	{
		return netseqno;
	}
}
