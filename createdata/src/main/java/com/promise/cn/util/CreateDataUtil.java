/*@文件名: CreateDataUtil.java  @创建人: 邢健   @创建日期: 2011-10-20 下午01:46:59*/
package com.promise.cn.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.Random;

/**   
 * @类名: CreateDataUtil.java 
 * @包名: com.promise.cn.util 
 * @描述: 辅助类 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-20 下午01:46:59 
 * @版本 V1.0   
 */
public class CreateDataUtil {
	
	public static String RANDOMDOUBLE_AB = "randomDouble[a,b]";
	public static String RANDOMINT_AB = "randomInt[a,b]";
	public static String RANDOMSTRING = "randomString";
	public static String SITETYPE_LEFT = "append_left";
	public static String SITETYPE_RIGHT = "append_right";
	public static String DATABASETYPE_ORACLE = "Oracle";
	public static String DATABASETYPE_MYSQL = "MySQL";
	public static String DATABASETYPE_SQLSERVER = "SQLServer";
	public static String DATABASETYPE_POSTGRESQL = "PostgreSQL";
	public static String DATABASETYPE_DB2 = "DB2";
	
	public static String JDBC_URL_ORACLE = "";
	public static String JDBC_URL_MYSQL = "";
	public static String JDBC_URL_SQLSERVER = "";
	public static String JDBC_URL_DB2 = "";
	public static String JDBC_URL_POSTGRESQL = "";
	
	public static String JDBC_DRIVERNAME_ORACLE = "oracle.jdbc.driver.OracleDriver";
	public static String JDBC_DRIVERNAME_MYSQL = "com.mysql.jdbc.Driver";
	public static String JDBC_DRIVERNAME_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static String JDBC_DRIVERNAME_DB2 = "";
	public static String JDBC_DRIVERNAME_POSTGRESQL = "org.postgresql.Driver";
	
	/**
	 * 组件居中
	 */
	public static void setCenter(Component component){
		Toolkit toolkit = Toolkit.getDefaultToolkit(); 
		int x = (int)(toolkit.getScreenSize().getWidth()-component.getWidth())/2; 
		int y = (int)(toolkit.getScreenSize().getHeight()-component.getHeight())/2; 
		component.setLocation(x, y); 
	}
	
	/**
	 * 获取字体对象
	 */
	public static Font getFont(String fontType,int fontStyle,int fontSize){
		return new Font(fontType,fontStyle,fontSize);
	}
	
	/**
	 * 获取Image对象
	 */
	public static Image getImage(String imageURL){
		Toolkit toolkit = Toolkit.getDefaultToolkit(); 
		return toolkit.createImage(CreateDataUtil.class.getResource(imageURL));
	}
	
	/**
	 * 产生随机数区间范围minInt---maxInt
	 * @param minInt
	 * @param maxInt
	 * @return
	 */
	public static int getRandomInt(int minInt, int maxInt) {
		Random random = new Random();
		int retInt = 0;
		if(maxInt>minInt){
			retInt = random.nextInt(maxInt-minInt)+minInt;
		}
		return retInt;
	}
	
	/**
	 * 产生随机数区间范围mindouble---maxdouble
	 * @param minInt
	 * @param maxInt
	 * @return
	 */
	public static double getRandomDouble(double mindouble, double maxdouble,int count) {
		Random random = new Random();
		double retdouble = 0.0;
		if(maxdouble>mindouble){
			retdouble = random.nextDouble()*(maxdouble-mindouble)+mindouble;
			DecimalFormat dcmFmt = new DecimalFormat(getDecimalPointFormat(count));
			retdouble = Double.parseDouble(dcmFmt.format(retdouble));
		}
		return retdouble;
	}

	/**
	 * 返回小数点格式,count最小值为1
	 * @return
	 */
	public static String getDecimalPointFormat(int count){
		String retStr = "0.";
		for(int i=0;i<count;i++){
			retStr = retStr+"0";
		}
		return retStr;
	}
	
	/**
	 * 随机产生字符串
	 */
	public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
        Random random = new Random();  
        StringBuffer sb = new StringBuffer();  
        for(int i = 0 ; i < length; ++i){  
            int number = random.nextInt(62);  
            sb.append(str.charAt(number));  
        }  
        return sb.toString();  
	}

	/**
	 * 随机产生字符串
	 */
	public static String getRandomString(String baseStr,int length,String position){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
        Random random = new Random();  
        StringBuffer sb = new StringBuffer();
        if(position.equals(SITETYPE_LEFT)){
        	sb.append(baseStr);
        	for(int i = baseStr.length() ; i < length; ++i){  
                int number = random.nextInt(62);  
                sb.append(str.charAt(number));  
            } 
        }else if(position.equals(SITETYPE_RIGHT)){
        	for(int i = 0 ; i < length - baseStr.length(); ++i){  
                int number = random.nextInt(62);  
                sb.append(str.charAt(number));  
            }
        	sb.append(baseStr);
        }
        return sb.toString();  
	}
	
}
