/*@文件名: TableConfigVO.java  @创建人: 邢健   @创建日期: 2011-10-24 下午03:05:45*/
package com.promise.cn.vo;

/**   
 * @类名: TableConfigVO.java 
 * @包名: com.promise.cn.vo 
 * @描述: 配置表格对象 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-24 下午03:05:45 
 * @版本 V1.0   
 */
public class TableConfigVO {

	private String id;
	private String name;
	private String type;
	private String initValue;
	private String step;
	private String endValue;
	private String tableName;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInitValue() {
		return initValue;
	}
	public void setInitValue(String initValue) {
		this.initValue = initValue;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getEndValue() {
		return endValue;
	}
	public void setEndValue(String endValue) {
		this.endValue = endValue;
	}
	
}
