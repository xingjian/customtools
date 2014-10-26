/** @文件名: ADCDVO.java @创建人：邢健  @创建日期： 2014-5-9 下午3:48:34 */
package com.promise.adcd.vo;

/**   
 * @类名: ADCDVO.java 
 * @包名: com.promise.adcd.vo 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-5-9 下午3:48:34 
 * @版本: V1.0   
 */
public class ADCDVO {

	private String code;
	private String name;
	private String url;
	public String getCode() {
		if(code.length()!=15){
			code +="000";
		}
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	
}
