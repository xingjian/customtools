/** @文件名: GoogleMapUtils.java @创建人：邢健  @创建日期： 2012-8-8 上午11:01:39 */

package com.promise.cn.googlemap.utils;

/**   
 * @类名: GoogleMapUtils.java 
 * @包名: com.promise.cn.googlemap.utils 
 * @描述: 谷歌地图常用帮助 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2012-8-8 上午11:01:39 
 * @版本: V1.0   
 */
@SuppressWarnings("all")
public class GoogleMapUtils {
	/**
	 * Web墨卡托投影的X，Y坐标取值范围为：[-20037508.3427892,20037508.3427892]，
	 * 经度取值范围为[-180,180],纬度范围则为[-85.05112877980659，85.05112877980659]。
	 */
	public static double XMAX = 20037508.3427892;
	public static double YMAX = 20037508.3427892;
	public static double XMIN = -20037508.3427892;
	public static double YMIN = -20037508.3427892;
	public static double LGTDMAX = 180;
	public static double LGTDMIN = -180;
	public static double LTTDMAX = 85.05112877980659;
	public static double LTTDMIN = -85.05112877980659;
	
	/**
	 * 坐标转换
	 * 经纬度坐标转换到投影坐标
	 */
	public static double[] getWebMercatorXY(double lgtd,double lttd){
		double[] webMercatorXY = new double[2];
		if(checkLgtdAndLttd(lgtd,lttd)){
			
		}
		return webMercatorXY;
	}
	
	/**
	 * 验证经纬度的合理性
	 * @param lgtd
	 * @param lttd
	 * @return
	 */
	public static boolean checkLgtdAndLttd(double lgtd,double lttd){
		if(LGTDMIN<=lgtd&&lgtd<=LGTDMAX&&LTTDMIN<=lttd&&lttd<=LTTDMAX){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 通过经纬度返回图片序号
	 * @return
	 */
	public static int[] getXYCodeByLgtdAndLttd(double lgtd,double lttd,int z){
		int[] xy = new int[2];
		if(checkLgtdAndLttd(lgtd,lttd)&&z<19&&z>0){
			//先算经度(由于经度是等分的)
			double longtilesize = 360/(Math.pow(2, z));
			xy[0] = (int)Math.floor((lgtd+180)/longtilesize);
			//纬度处理
			if(lttd > 90){
				lttd = lttd-180;
			}else if(lttd < -90){
				lttd = lttd + 180;
			}
			double phi = (Math.PI*lttd)/180.0;
			double temp = Math.tan(Math.PI/4.0 + phi/2.0);
			double res = Math.log(temp);
			double maxtiley = (double)(Math.pow(2,z));
			xy[1] = (int)(Math.floor(((1-res/Math.PI)/2.0)*maxtiley));
		}
		return xy;
	}
	
	/**
	 * 入口函数
	 * @param args
	 */
	public static void main(String[] args) {
		//111319.49079327333(1经度)
		double a = 20037508.3427892;
		GoogleMapUtils g = new GoogleMapUtils();
		int[] xy = g.getXYCodeByLgtdAndLttd(116.407013, 39.901309, 10);
		System.out.println(xy[0]+"***"+xy[1]);
	}
}
