package com.promise.gistool.util;

import java.util.HashMap;
import java.util.Map;


/**  
 * 功能描述: 常量应用
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年12月22日 下午4:58:23  
 */
public class ConstantUtil {
    
    public static String MultiPolygon = "MULTIPOLYGON";
    public static String Point = "POINT";
    public static String MultiPoint = "MULTIPOINT";
    public static String LineString = "LINESTRING";
    public static String MultiLineString = "MULTILINESTRING";
    public static String Polygon = "POLYGON";
    public static Map<String , String> GeometryTypeMap = new HashMap<String , String>();
    
    static {
        GeometryTypeMap.put(Point, "Point");
        GeometryTypeMap.put(MultiPoint, "MultiPoint");
        GeometryTypeMap.put(LineString, "LineString");
        GeometryTypeMap.put(MultiLineString, "MultiLineString");
        GeometryTypeMap.put(Polygon, "Polygon");
        GeometryTypeMap.put(MultiPolygon, "MultiPolygon");
    }
    
    /**
     * 判断str是不是几何类型关键字
     * @param str
     * @return
     */
    public static boolean IsGeometryTypeKey(String str){
        if(null!=GeometryTypeMap.get(str.toUpperCase().trim())){
            return true;
        }else{
            return false;
        }
        
    }
}
