/** @文件名: DownLoadServiceImpl.java @创建人：邢健  @创建日期： 2012-8-6 下午2:30:57 */

package com.promise.cn.download.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Vector;

import com.promise.cn.download.service.DownLoadService;
import com.promise.cn.download.vo.HttpRespons;

/**   
 * @类名: DownLoadServiceImpl.java 
 * @包名: com.promise.cn.download.service.impl 
 * @描述: 下载地图服务接口实现类 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2012-8-6 下午2:30:57 
 * @版本: V1.0   
 */
public class DownLoadServiceImpl implements DownLoadService {
	
	private String defaultContentEncoding = Charset.defaultCharset().name();

	@Override
	public HttpRespons sendGet(String urlString) throws IOException {
		return this.send(urlString, "GET", null, null); 
	}

	@Override
	public HttpRespons sendGet(String urlString, Map<String, String> params)throws IOException {
		return this.send(urlString, "GET", params, null);      
	}

	@Override
	public HttpRespons sendGet(String urlString, Map<String, String> params,Map<String, String> propertys) throws IOException {
		return this.send(urlString, "GET", params, propertys);      
	}

	@Override
	public HttpRespons sendPost(String urlString) throws IOException {
		return this.send(urlString, "POST", null, null);      
	}

	@Override
	public HttpRespons sendPost(String urlString, Map<String, String> params)throws IOException {
		return this.send(urlString, "POST", params, null);
	}

	@Override
	public HttpRespons sendPost(String urlString, Map<String, String> params,Map<String, String> propertys) throws IOException {
		return this.send(urlString, "POST", params, propertys);
	}

	@Override
	public HttpRespons send(String urlString, String method,Map<String, String> parameters, Map<String, String> propertys)throws IOException {
		HttpURLConnection urlConnection = null;      
        if (method.equalsIgnoreCase("GET") && parameters != null) {      
            StringBuffer param = new StringBuffer();      
            int i = 0;      
            for (String key : parameters.keySet()) {      
                if (i == 0)      
                    param.append("?");      
                else     
                    param.append("&");      
                param.append(key).append("=").append(parameters.get(key));      
                i++;      
            }      
            urlString += param;      
        }      
        URL url = new URL(urlString);      
        urlConnection = (HttpURLConnection) url.openConnection();      
        urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/534.36 (KHTML, like Gecko) Chrome/13.0.766.0 Safari/534.36");
        urlConnection.setRequestMethod(method);      
        urlConnection.setDoOutput(true);      
        urlConnection.setDoInput(true);      
        urlConnection.setUseCaches(false);      
       
        if (propertys != null)      
            for (String key : propertys.keySet()) {      
                urlConnection.addRequestProperty(key, propertys.get(key));      
            }      
       
        if (method.equalsIgnoreCase("POST") && parameters != null) {      
            StringBuffer param = new StringBuffer();      
            for (String key : parameters.keySet()) {      
                param.append("&");      
                param.append(key).append("=").append(parameters.get(key));      
            }
            urlConnection.getOutputStream().write(param.toString().getBytes());      
            urlConnection.getOutputStream().flush();      
            urlConnection.getOutputStream().close();      
        }      
        return this.makeContent(urlString, urlConnection);  
	}

	@Override
	public HttpRespons makeContent(String urlString,HttpURLConnection urlConnection) throws IOException {
		HttpRespons httpResponser = new HttpRespons();      
        try {      
            InputStream in = urlConnection.getInputStream(); 
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));      
            httpResponser.contentCollection = new Vector<String>();      
            StringBuffer temp = new StringBuffer();      
            String line = bufferedReader.readLine();      
            while (line != null) {  
                httpResponser.contentCollection.add(line);      
                temp.append(line).append("\r\n");      
                line = bufferedReader.readLine();      
            }      
            bufferedReader.close();      
            String ecod = urlConnection.getContentEncoding();      
            if (ecod == null){
            	ecod = this.defaultContentEncoding;
            }      
            httpResponser.urlString = urlString;      
            httpResponser.defaultPort = urlConnection.getURL().getDefaultPort();      
            httpResponser.file = urlConnection.getURL().getFile();      
            httpResponser.host = urlConnection.getURL().getHost();      
            httpResponser.path = urlConnection.getURL().getPath();      
            httpResponser.port = urlConnection.getURL().getPort();      
            httpResponser.protocol = urlConnection.getURL().getProtocol();      
            httpResponser.query = urlConnection.getURL().getQuery();      
            httpResponser.ref = urlConnection.getURL().getRef();      
            httpResponser.userInfo = urlConnection.getURL().getUserInfo();      
            httpResponser.content = new String(temp.toString().getBytes(), ecod);      
            httpResponser.contentEncoding = ecod;      
            httpResponser.code = urlConnection.getResponseCode();      
            httpResponser.message = urlConnection.getResponseMessage();      
            httpResponser.contentType = urlConnection.getContentType();      
            httpResponser.method = urlConnection.getRequestMethod();      
            httpResponser.connectTimeout = urlConnection.getConnectTimeout();      
            httpResponser.readTimeout = urlConnection.getReadTimeout();      
            return httpResponser;      
        } catch (IOException e) {      
            throw e;      
        } finally {      
            if (urlConnection != null){
            	urlConnection.disconnect();
            }      
        }  
	}

	@Override
	public String getDefaultContentEncoding() {
		return defaultContentEncoding;
	}

	@Override
	public void setDefaultContentEncoding(String defaultContentEncoding) {
		this.defaultContentEncoding = defaultContentEncoding;
	} 
}
