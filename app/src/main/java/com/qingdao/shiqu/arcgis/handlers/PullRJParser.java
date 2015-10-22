package com.qingdao.shiqu.arcgis.handlers;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.xmlpull.v1.XmlPullParser;



import android.util.Xml;

import com.qingdao.shiqu.arcgis.interfaces.RJParser;
import com.qingdao.shiqu.arcgis.utils.Fiber;
import com.qingdao.shiqu.arcgis.utils.RJInformation;



public class PullRJParser implements RJParser {

	@Override
	public List<RJInformation> parse(InputStream is) throws Exception {

		// TODO 自动生成的方法存根

		List<RJInformation> rjList = null;  
		List<Fiber> fiberList = null;
		RJInformation rjinfor = null;  
		Fiber lfiber = null;
		Fiber rfiber = null;

		//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
		//      XmlPullParser parser = factory.newPullParser();  

		XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例  
		parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式  

		int eventType = parser.getEventType();  
		while (eventType != XmlPullParser.END_DOCUMENT) {  
			switch (eventType) {  
			case XmlPullParser.START_DOCUMENT:  
				rjList = new ArrayList<RJInformation>();  
				fiberList = new ArrayList<Fiber>();
				break;  
			case XmlPullParser.START_TAG:  
				String tag = parser.getName(); 
				if(tag.equalsIgnoreCase("equipment")){
					rjinfor = new RJInformation();
					rjinfor.setEquid(Integer.parseInt(parser.getAttributeValue(null, "id")));
					rjinfor.setEquName(parser.getAttributeValue(null, "name"));
				}else if(tag.equalsIgnoreCase("leftoptialfiber")){
					lfiber = new Fiber();
					lfiber.setColor(parser.getAttributeValue(null, "color"));
					lfiber.setIsrj(parser.getAttributeValue(null, "isrj").equals("1")?true:false);
					lfiber.setStartnodeid(Integer.parseInt(parser.getAttributeValue(null, "startnodeid")));
					lfiber.setEndnodeid(Integer.parseInt(parser.getAttributeValue(null, "endnodeid")));
					lfiber.setName(parser.getAttributeValue(null, "name"));
					lfiber.setRorl(0);
					fiberList.add(lfiber);
					eventType = parser.next();
				}else if(tag.equalsIgnoreCase("righoptialtfiber")){
					rfiber = new Fiber();
					rfiber.setColor(parser.getAttributeValue(null, "color"));
					rfiber.setIsrj(parser.getAttributeValue(null, "isrj").equals("1")?true:false);
					rfiber.setStartnodeid(Integer.parseInt(parser.getAttributeValue(null, "startnodeid")));
					rfiber.setEndnodeid(Integer.parseInt(parser.getAttributeValue(null, "endnodeid")));
					rfiber.setName(parser.getAttributeValue(null, "name"));
					rfiber.setRorl(1);
					fiberList.add(rfiber);
					eventType = parser.next();
				}
				/*else if(rjinfor != null || lfiber == null){
					if(tag.equalsIgnoreCase("left"))
						lfiber = new Fiber();
				}else if(rjinfor != null || lfiber != null){
					if(tag.equals("leftoptialfiber")){
						lfiber.setColor(parser.getAttributeValue(null, "color"));
						lfiber.setIsrj(parser.getAttributeValue(null, "isrj").equals("1")?true:false);
						lfiber.setStartnodeid(Integer.parseInt(parser.getAttributeValue(null, "startnodeid")));
						lfiber.setEndnodeid(Integer.parseInt(parser.getAttributeValue(null, "endnodeid")));
						lfiber.setRorl(0);
						fiberList.add(lfiber);
						eventType = parser.next();
					}
				}else if(rjinfor != null || rfiber == null){
					if(tag.equalsIgnoreCase("rigt"))
						rfiber = new Fiber();
				}else if(rjinfor != null || rfiber != null){
					if(tag.equals("righoptialtfiber")){
						rfiber.setColor(parser.getAttributeValue(null, "color"));
						rfiber.setIsrj(parser.getAttributeValue(null, "isrj").equals("1")?true:false);
						rfiber.setStartnodeid(Integer.parseInt(parser.getAttributeValue(null, "startnodeid")));
						rfiber.setEndnodeid(Integer.parseInt(parser.getAttributeValue(null, "endnodeid")));
						rfiber.setRorl(0);
						fiberList.add(rfiber);
						eventType = parser.next();
					}
				}*/
				break;  
			case XmlPullParser.END_TAG:  
				if (parser.getName().equals("equipment") ) {  
					rjinfor.setFibers(fiberList);
					fiberList = new ArrayList<Fiber>();
					rjList.add(rjinfor);  
					rjinfor = null;      
				}  
				break;  
			}  
			eventType = parser.next();  
		}  
		return rjList;  



	}

	@Override
	public String serialize(List<RJInformation> rjList) throws Exception {
		// TODO 自动生成的方法存根

		return null;
	}

}
