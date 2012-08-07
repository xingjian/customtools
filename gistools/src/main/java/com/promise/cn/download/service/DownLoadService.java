/** @文件名: DownLoadService.java @创建人：邢健  @创建日期： 2012-8-6 下午2:30:03 */

package com.promise.cn.download.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import com.promise.cn.download.vo.HttpRespons;

/**
 * @类名: DownLoadService.java
 * @包名: com.promise.cn.download.service
 * @描述: 下载GIS地图服务接口
 * @作者: xingjian xingjian@yeah.net
 * @日期:2012-8-6 下午2:30:03
 * @版本: V1.0
 */
public interface DownLoadService {
	/**
	 * 发送GET请求
	 * @param urlString
	 * URL地址
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendGet(String urlString) throws IOException;

	/**
	 * 发送GET请求
	 * @param urlString
	 * URL地址
	 * @param params
	 * 参数集合
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendGet(String urlString, Map<String, String> params)throws IOException;
	/**
	 * 发送GET请求
	 * @param urlString
	 * URL地址
	 * @param params
	 * 参数集合
	 * @param propertys
	 * 请求属性
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendGet(String urlString, Map<String, String> params,Map<String, String> propertys) throws IOException;
	/**
	 * 发送POST请求
	 * @param urlString
	 * URL地址
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendPost(String urlString) throws IOException;
	/**
	 * 发送POST请求
	 * @param urlString
	 * URL地址
	 * @param params
	 * 参数集合
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendPost(String urlString, Map<String, String> params)throws IOException; 
	/**
	 * 发送POST请求
	 * @param urlString
	 * URL地址
	 * @param params
	 * 参数集合
	 * @param propertys
	 * 请求属性
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendPost(String urlString, Map<String, String> params,Map<String, String> propertys) throws IOException;
	/**    
     * 发送HTTP请求    
     * @param urlString    
     * @return 响映对象    
     * @throws IOException    
     */     
    public HttpRespons send(String urlString, String method,Map<String, String> parameters, Map<String, String> propertys)throws IOException;
    /**    
     * 得到响应对象    
     * @param urlConnection    
     * @return 响应对象    
     * @throws IOException    
     */     
    public HttpRespons makeContent(String urlString,HttpURLConnection urlConnection)throws IOException;
    /**    
     * 默认的响应字符集    
     */     
    public String getDefaultContentEncoding();     
    /**    
     * 设置默认的响应字符集    
     */     
    public void setDefaultContentEncoding(String defaultContentEncoding);
}
