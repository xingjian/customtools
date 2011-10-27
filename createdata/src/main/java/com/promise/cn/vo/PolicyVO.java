/*@文件名: PolicyVO.java  @创建人: 邢健   @创建日期: 2011-10-25 下午03:14:08*/
package com.promise.cn.vo;

import com.promise.cn.util.CreateDataUtil;

/**   
 * @类名: PolicyVO.java 
 * @包名: com.promise.cn.vo 
 * @描述: 策略对象 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-25 下午03:14:08 
 * @版本 V1.0   
 */
public class PolicyVO {

	//1 代表常量,2代表递增,3代表递减,4代表区间,5代表固定集合,6代表随机,7代表uuid
	private String type;
	
	private String name;//名称
	
	private String value;//固定值
	
	private String initValue;//开始值
	
	private String stepValue;//步长
	
	private String endValue;//最大值
	
	private String strLength;//长度
	
	private String siteStr;//字符串追加方式
	
	private String numberDecimal ;//小数点位数
	
	public String getStrLength() {
		return strLength;
	}

	public void setStrLength(String strLength) {
		this.strLength = strLength;
	}

	public String getSiteStr() {
		return siteStr;
	}

	public void setSiteStr(String siteStr) {
		this.siteStr = siteStr;
	}

	public String getNumberDecimal() {
		return numberDecimal;
	}

	public void setNumberDecimal(String numberDecimal) {
		this.numberDecimal = numberDecimal;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getInitValue() {
		return initValue;
	}

	public void setInitValue(String initValue) {
		this.initValue = initValue;
	}

	public String getStepValue() {
		return stepValue;
	}

	public void setStepValue(String stepValue) {
		this.stepValue = stepValue;
	}

	public String getEndValue() {
		return endValue;
	}

	public void setEndValue(String endValue) {
		this.endValue = endValue;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("	 <policy>"+"\n");
		sb.append("			<name>"+name+"</name>"+"\n");
		sb.append("			<userName>"+type+"</userName>"+"\n");
		if(type.equals(CreateDataUtil.RANDOMDOUBLE_AB)){
			sb.append("			<initValue>"+initValue+"</initValue>"+"\n");
			sb.append("			<endValue>"+endValue+"</endValue>"+"\n");
			sb.append("			<numberDecimal>"+numberDecimal+"</numberDecimal>"+"\n");
		}else if(type.equals(CreateDataUtil.RANDOMINT_AB)){
			sb.append("			<initValue>"+initValue+"</initValue>"+"\n");
			sb.append("			<endValue>"+endValue+"</endValue>"+"\n");
		}else if(type.equals(CreateDataUtil.RANDOMSTRING)){
			sb.append("			<value>"+value+"</value>"+"\n");
			sb.append("			<siteStr>"+siteStr+"</siteStr>"+"\n");
			sb.append("			<strLength>"+strLength+"</strLength>"+"\n");
		}
		sb.append("	 </policy>"+"\n");
		return sb.toString();
		
	}
	
	
}
