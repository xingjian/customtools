package com.promise.gistool.util;

/**  
 * 功能描述: GIS常用坐标转换
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月7日 上午10:34:31  
 */
public class GISCoordinateTransform {

    /**
     * 投影坐标转换84坐标
     * @param x x坐标
     * @param y y坐标
     * @return
     */
    public static double[] From900913To84(double x,double y){
        double[] r = new double[2];
        double lon = (x / 20037508.34) * 180;
        double lat = (y / 20037508.34) * 180;
        lat = 180/Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);
        r[0] = lon;
        r[1] = lat; 
        return r;
    }
    /**
     * 84坐标转换投影坐标
     * @param x x坐标
     * @param y y坐标
     * @return
     */
    public static double[] From84To900913(double x,double y){
        double[] r = new double[2];
        double lon = x *20037508.34/180;
        double lat = Math.log(Math.tan((90+y)*Math.PI/360))/(Math.PI/180);
        lat = lat *20037508.34/180;
        r[0] = lon;
        r[1] = lat; 
        return r;
    }
}
