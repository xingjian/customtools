/** @文件名: ADCDServiceImpl.java @创建人：邢健  @创建日期： 2014-5-8 下午3:21:47 */
package com.promise.adcd.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promise.adcd.service.ADCDService;
import com.promise.adcd.util.DBUtil;
import com.promise.adcd.vo.ADCDVO;

/**   
 * @类名: ADCDServiceImpl.java 
 * @包名: com.promise.adcd.service.impl 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-5-8 下午3:21:47 
 * @版本: V1.0   
 */
public class ADCDServiceImpl implements ADCDService{

	private Logger log = LoggerFactory.getLogger(ADCDServiceImpl.class);
	
	@Override
	public String getADCDPage(String htmlURL, String saveSrc, String regStr,boolean isLoop, int depth,String method) {
		log.debug("htmlURL:"+htmlURL);
		String retResult = "success";
		try{
			URL url = new URL(htmlURL);      
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();      
	        urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/534.36 (KHTML, like Gecko) Chrome/13.0.766.0 Safari/534.36");
	        urlConnection.setRequestMethod(method);      
	        urlConnection.setDoOutput(true);      
	        urlConnection.setDoInput(true);      
	        urlConnection.setUseCaches(false);
	        urlConnection.setConnectTimeout(1000 * 10); 
	        BufferedReader bufr = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"GB2312"));
	        String line = null;  
	        String regex = "<tr\\s+class=\'(citytr|countytr|towntr)\'><td><a\\s+href=\'([^\']+)'>([^>]*)</a></td><td><a\\s+href=\'[^\']+\'>([^>]+)</a></td></tr>";
	        Pattern p = Pattern.compile(regex); 
	        String mainURL = htmlURL.substring(0, htmlURL.lastIndexOf("/")+1);
	        while((line = bufr.readLine()) != null) {
	            Matcher m = p.matcher(line);
	            while(m.find()) {  
	                String adcdurl = mainURL+m.group(2);
	                String adcdCode = m.group(3);
	                String adcdName = m.group(4);
	                ADCDVO adcdvo = new ADCDVO();
	                adcdvo.setName(adcdName);
	                adcdvo.setCode(adcdCode);
	                adcdvoList.add(adcdvo);
	                DBUtil.insertADCD(adcdvo);
	                if(adcdurl.substring(adcdurl.lastIndexOf("/")+1, adcdurl.lastIndexOf(".html")).length()==9){
	                	getVillageListByURL(adcdurl,saveSrc,regex,isLoop,depth,method);
	                }else{
	                	getADCDPage(mainURL+m.group(2),saveSrc,regex,isLoop,depth,method);
	                }
	            }  
	        }
		}catch(Exception e){
			retResult = "failure";
			e.printStackTrace();
		}
		return retResult;
	}

	public String getVillageListByURL(String htmlURL, String saveSrc, String regStr,boolean isLoop, int depth,String method){
		log.debug("htmlURL:"+htmlURL);
		String retResult = "success";
		try{
			URL url = new URL(htmlURL);      
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();      
	        urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/534.36 (KHTML, like Gecko) Chrome/13.0.766.0 Safari/534.36");
	        urlConnection.setRequestMethod(method);      
	        urlConnection.setDoOutput(true);      
	        urlConnection.setDoInput(true);      
	        urlConnection.setUseCaches(false);
	        urlConnection.setConnectTimeout(1000 * 10); 
	        BufferedReader bufr = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"GB2312"));
	        String line = null;  
	        String regex = "<tr\\s+class=\'villagetr\'><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td></tr>";
	        Pattern p = Pattern.compile(regex); 
	        while((line = bufr.readLine()) != null) {
	            Matcher m = p.matcher(line);
	            while(m.find()) {  
	                ADCDVO adcdvo = new ADCDVO();
	                adcdvo.setName(m.group(3));
	                adcdvo.setCode(m.group(1));
	                adcdvoList.add(adcdvo);
	                DBUtil.insertADCD(adcdvo);
	            }  
	        }
		}catch(Exception e){
			retResult = "failure";
			e.printStackTrace();
		}
		return retResult;
	} 
}
