package com.qingdao.shiqu.arcgis.mode;

import com.esri.core.geometry.Point;

/**************************************
 *
 *      作  者：  潘跃瑞 
 *
 *      功  能：  
 *
 *      时  间：  2014-10-30
 *
 *      版  权：  青岛盛天科技有限公司 
 *      
 ****************************************/
public class Take
{
    private double zoom;
    private Point point;
    
	public double getZoom()
	{
		return zoom;
	}
	public void setZoom(double zoom)
	{
		this.zoom = zoom;
	}
	public Point getPoint()
	{
		return point;
	}
	public void setPoint(Point point)
	{
		this.point = point;
	}
    
}
