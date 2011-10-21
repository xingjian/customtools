/*@文件名: CreateDataUtil.java  @创建人: 邢健   @创建日期: 2011-10-20 下午01:46:59*/
package com.promise.cn.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

/**   
 * @类名: CreateDataUtil.java 
 * @包名: com.promise.cn.util 
 * @描述: 辅助类 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-20 下午01:46:59 
 * @版本 V1.0   
 */
public class CreateDataUtil {
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
}
