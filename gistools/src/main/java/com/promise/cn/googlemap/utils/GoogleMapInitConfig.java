/** @文件名: GoogleMapInitConfig.java @创建人：邢健  @创建日期： 2012-8-9 上午9:03:05 */

package com.promise.cn.googlemap.utils;

import java.util.List;

/**   
 * @类名: GoogleMapInitConfig.java 
 * @包名: com.promise.cn.googlemap.utils 
 * @描述: GoogleMap初始化需要的参数 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2012-8-9 上午9:03:05 
 * @版本: V1.0   
 */
public class GoogleMapInitConfig {

	private String downLoadPath;//下载保存图片路径
	private String logPath;//日志路径
	private String errorPath;//错误日志路径集合
	private List<String> googleServerList;//谷歌服务起地址
	private List<String> proxyNetWorkList;//代理集合
	private String mapType;//下载地图类型 1 普通  2 影像
	
	public String getDownLoadPath() {
		return downLoadPath;
	}
	public void setDownLoadPath(String downLoadPath) {
		this.downLoadPath = downLoadPath;
	}
	public String getLogPath() {
		return logPath;
	}
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	public String getErrorPath() {
		return errorPath;
	}
	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}
	public List<String> getGoogleServerList() {
		return googleServerList;
	}
	public void setGoogleServerList(List<String> googleServerList) {
		this.googleServerList = googleServerList;
	}
	public List<String> getProxyNetWorkList() {
		return proxyNetWorkList;
	}
	public void setProxyNetWorkList(List<String> proxyNetWorkList) {
		this.proxyNetWorkList = proxyNetWorkList;
	}
	public String getMapType() {
		return mapType;
	}
	public void setMapType(String mapType) {
		this.mapType = mapType;
	}
	
}
