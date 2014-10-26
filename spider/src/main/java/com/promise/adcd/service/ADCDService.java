/** @文件名: ADCDService.java @创建人：邢健  @创建日期： 2014-5-8 下午3:21:08 */
package com.promise.adcd.service;

import java.util.ArrayList;
import java.util.List;

import com.promise.adcd.vo.ADCDVO;

/**   
 * @类名: ADCDService.java 
 * @包名: com.promise.adcd.service 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-5-8 下午3:21:08 
 * @版本: V1.0   
 */
public interface ADCDService {

	List<ADCDVO> adcdvoList = new ArrayList<ADCDVO>();
	/**
	 * 获取行政区划代码的页面并保存到指定的路径
	 * @param htmlURL 初始页面网址
	 * @param saveSrc 保存路径
	 * @param regStr 抽取规则
	 * @param isLoop 是否循环抽取
	 * @param depth 指定页面循环深度
	 * @return 运行结果
	 */
	public String getADCDPage(String htmlURL,String saveSrc,String regStr,boolean isLoop,int depth,String method);
	
	/**
	 * 
	 * @param htmlURL
	 * @param saveSrc
	 * @param regStr
	 * @param isLoop
	 * @param depth
	 * @param method
	 * @return
	 */
	public String getVillageListByURL(String htmlURL, String saveSrc, String regStr,boolean isLoop, int depth,String method);
}
