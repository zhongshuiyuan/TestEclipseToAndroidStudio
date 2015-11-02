package com.qingdao.shiqu.arcgis.utils;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;

/**
 * Geometry工具类
 * @deprecated ESRI已经提供类似接口，不需要手动实现，白干了
 */
@Deprecated
public class GeometryUtil {

    /** 将几何对象生成wkt字符串 **/
    public static String GeometryToWkt(Geometry geometry){
        if(geometry == null){
            return null;
        }

        String geoString = "";
        Geometry.Type type = geometry.getType();
        if (Geometry.Type.POINT.equals(type)) {
            Point pt = (Point) geometry;
            geoString = type.name() + "(" + pt.getX() + " " + pt.getY() + ")";
        } else if (Geometry.Type.POLYGON.equals(type) || Geometry.Type.POLYLINE.equals(type)) {
            MultiPath multiPath = (MultiPath) geometry;
            geoString = type.name() + "(" + "";
            int pathCount = multiPath.getPathCount();
            for(int i = 0; i < pathCount; ++i){
                String tempString = "(";
                int pathSize = multiPath.getPathSize(i);
                for(int j = 0; j < pathSize; ++j){
                    Point pt = multiPath.getPoint(j);
                    tempString += pt.getX() + " " + pt.getY() + ",";
                }
                tempString = tempString.substring(0, tempString.length()-1) + ")";
                geoString += tempString + ",";
            }
            geoString = geoString.substring(0, geoString.length()-1) + ")";
        } else if (Geometry.Type.ENVELOPE.equals(type)) {
            Envelope env = (Envelope) geometry;
            geoString = type.name() + "(" + env.getXMin() + "," + env.getYMin() + "," + env.getXMax() + "," + env.getYMax() + ")";
        } else if (Geometry.Type.MULTIPOINT.equals(type)) {
            // TODO 补全MultiPoint的方法
        } else{
            geoString = null;
        }
         return geoString;
    }

    /**
     * 将wkt字符串拼成几何对象
     */
    public static Geometry WktToGeometry(String wkt){
        if(wkt == null || "".equals(wkt)) {
            return null;
        }

        Geometry geometry = null;
        String headStr = wkt.substring(0, wkt.indexOf("("));
        String tempString = wkt.substring(wkt.indexOf("(") + 1, wkt.lastIndexOf(")"));
        if (headStr.equals(Geometry.Type.POINT.name())) {
            String[] values = tempString.split(" ");
            geometry = new Point(Double.valueOf(values[0]), Double.valueOf(values[1]));
        } else if(headStr.equals(Geometry.Type.POLYGON.name()) || headStr.equals(Geometry.Type.POLYLINE.name())){
            geometry = parseWkt(tempString, headStr);
        } else if(headStr.equals(Geometry.Type.ENVELOPE.name())){
            String[] extents = tempString.split(",");
            geometry = new Envelope(Double.valueOf(extents[0]), Double.valueOf(extents[1]), Double.valueOf(extents[2]), Double.valueOf(extents[3]));
        } else if(headStr.equals(Geometry.Type.MULTIPOINT.name())){
            // TODO 补全MultiPoint的方法
        } else{
            return null;
        }
        return geometry;
    }

    private static Geometry parseWkt(String multipath, String type) {
        String subMultipath = multipath.substring(1, multipath.length()-1);
        String[] paths;
        if (subMultipath.contains("),(")) {
            //String regularExpression = "),(";
            String regularExpression = "&";
            paths = subMultipath.split(regularExpression);//多个几何对象的字符串
        } else {
            paths = new String[]{subMultipath};
        }
        Point startPoint;
        MultiPath path;
        if (type.equals(Geometry.Type.POLYLINE.name())) {
            path = new Polyline();
        } else {
            path = new Polygon();
        }
        int pathCount = paths.length;
        for (int i = 0; i < pathCount; ++i){
            String[] points = paths[i].split(",");
            startPoint = null;
            int pointCount = points.length;
            for (int j = 0; j < pointCount; ++j){
                String[] pointStr = points[j].split(" ");
                if(startPoint ==null){
                    startPoint = new Point(Double.valueOf(pointStr[0]),Double.valueOf(pointStr[1]));
                    path.startPath(startPoint);
                }else{
                    path.lineTo(new Point(Double.valueOf(pointStr[0]),Double.valueOf(pointStr[1])));
                }
            }
        }
        return path;
    }
}
