/** @文件名: TableVO.java @创建人：邢健  @创建日期： 2013-3-5 下午2:21:51 */

package com.promise.cn.vo;

/**   
 * @类名: TableVO.java 
 * @包名: com.promise.cn.vo 
 * @描述: TABLE 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-3-5 下午2:21:51 
 * @版本: V1.0   
 */
public class TableVO {

	private String id;
	
	private String name;
	
	private String columnStr;

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

	public String getColumnStr() {
		return columnStr;
	}

	public void setColumnStr(String columnStr) {
		this.columnStr = columnStr;
	}
	
	
}
