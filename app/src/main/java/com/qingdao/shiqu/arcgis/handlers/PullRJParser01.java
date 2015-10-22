package com.qingdao.shiqu.arcgis.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;
import android.util.Xml.Encoding;

import com.qingdao.shiqu.arcgis.interfaces.RJParser;
import com.qingdao.shiqu.arcgis.utils.Fiber;
import com.qingdao.shiqu.arcgis.utils.RJInformation;

public class PullRJParser01 implements RJParser {

	@Override
	public List<RJInformation> parse(InputStream is) throws Exception {

		// TODO 自动生成的方法存根

		List<RJInformation> rjList = null;  
		List<Fiber> fiberList = null;
		RJInformation rjinfor = null;  
		Fiber lfiber = new Fiber();
		Fiber rfiber =  new Fiber();
		String[] at = null;

		//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
		//      XmlPullParser parser = factory.newPullParser();  

		XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例  
		parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式  

		int eventType = parser.getEventType();  
		int isrl = 0;//0-left 1-right
		String rorl = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {  
			switch (eventType) {  
			case XmlPullParser.START_DOCUMENT:  
				rjList = new ArrayList<RJInformation>();  
				fiberList = new ArrayList<Fiber>();
				break;  
			case XmlPullParser.START_TAG:  
				String tag = parser.getName(); 
				if(tag.equalsIgnoreCase("action")){
					if(parser.getAttributeValue(null, "result").equalsIgnoreCase("false")){
						
						continue;
					}
					else{
						rjinfor = new RJInformation();
						rjinfor.setEquName(parser.getAttributeValue(null, "atti"));
					}
					
				}else if(tag.equalsIgnoreCase("record")){
					rorl = parser.getAttributeValue(null, "atti");
					/*at = parser.getAttributeValue(null, "atti").split("/");
					rjinfor.setEquName(at[1]);
					rjinfor.setEquid(Integer.parseInt(parser.getAttributeValue(null, "id")));
					rjinfor.setEquName(parser.getAttributeValue(null, "name"))*/;
				}else if(tag.equalsIgnoreCase("field") && rorl.trim().equalsIgnoreCase("left")){
					if(parser.getAttributeValue(null, "name").equalsIgnoreCase("color")){
						String col = parser.getAttributeValue(null, "value");
						lfiber.setColor(col);
					}
					else if(parser.getAttributeValue(null, "name").equalsIgnoreCase("gdname"))
						lfiber.setName(parser.getAttributeValue(null, "value"));
					else if(parser.getAttributeValue(null, "name").equalsIgnoreCase("type"))
						lfiber.setVtype(parser.getAttributeValue(null, "type"));
//					fiberList.add(lfiber);
					isrl = 0;
					eventType = parser.next();
				}else if(tag.equalsIgnoreCase("field") && rorl.trim().equalsIgnoreCase("right")){
					if(parser.getAttributeValue(null, "name").equalsIgnoreCase("color"))
						rfiber.setColor(parser.getAttributeValue(null, "value"));
					else if(parser.getAttributeValue(null, "name").equalsIgnoreCase("gdname"))
						rfiber.setName(parser.getAttributeValue(null, "value"));
					else if(parser.getAttributeValue(null, "name").equalsIgnoreCase("type"))
						rfiber.setVtype(parser.getAttributeValue(null, "type"));
//					fiberList.add(lfiber); escreen://
					isrl = 1;
					eventType = parser.next();
				}
				break;  
			case XmlPullParser.END_TAG:  
				if (parser.getName().equals("action") ) {  
					rjinfor.setFibers(fiberList);
					fiberList = new ArrayList<Fiber>();
					rjList.add(rjinfor);  
					rjinfor = null;      
				}else if(parser.getName().equalsIgnoreCase("record")){
					if(isrl == 0){
						lfiber.setRorl(0);
						fiberList.add(lfiber);
					}else{
						rfiber.setRorl(1);
						fiberList.add(rfiber);
					}
						
					lfiber = new Fiber();
					rfiber = new Fiber();
				}
				break;  
			}  
			eventType = parser.next();  
		}  
		return rjList;  
	}

	@Override
	public String serialize(List<RJInformation> books) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}
}
