/*@文件名: DataConnectConfigVO.java  @创建人: 邢健   @创建日期: 2011-10-20 下午05:07:43*/
package com.promise.cn.vo;

/**   
 * @类名: DataConnectConfigVO.java 
 * @包名: com.promise.cn 
 * @描述: 数据库连接配置对象 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-20 下午05:07:43 
 * @版本 V1.0   
 */
public class DataConnectConfigVO {

	private String name;
	
	private String userName;
	
	private String password;
	
	private String driverClassName;
	
	private String url;
	//数据状态
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("	 <datasource>"+"\n");
		sb.append("			<name>"+name+"</name>"+"\n");
		sb.append("			<userName>"+userName+"</userName>"+"\n");
		sb.append("			<password>"+password+"</password>"+"\n");
		sb.append("			<driverClassName>"+driverClassName+"</driverClassName>"+"\n");
		sb.append("			<url>"+url+"</url>"+"\n");
		sb.append("	 </datasource>"+"\n");
		return sb.toString();
	}
	
	
}
