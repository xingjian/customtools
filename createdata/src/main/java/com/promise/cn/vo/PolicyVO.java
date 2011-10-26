/*@文件名: PolicyVO.java  @创建人: 邢健   @创建日期: 2011-10-25 下午03:14:08*/
package com.promise.cn.vo;

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
	
	private String name;
	
	private String value;
	
	private String initValue;
	
	private String stepValue;
	
	private String endValue;

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
	
	
	
}
