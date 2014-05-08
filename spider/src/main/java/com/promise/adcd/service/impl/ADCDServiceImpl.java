/** @文件名: ADCDServiceImpl.java @创建人：邢健  @创建日期： 2014-5-8 下午3:21:47 */
package com.promise.adcd.service.impl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promise.adcd.service.ADCDService;

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
	private static int BUFFER_SIZE = 1024;
	
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
	        
	        InputStream in = urlConnection.getInputStream(); 
            File file= new File(saveSrc);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file)); 
            int c=0;
            byte buffer[]=new byte[BUFFER_SIZE];
            while((c=in.read(buffer, 0, buffer.length ))!=-1){
               out.write(buffer,0,c);
            }
            out.flush();
            out.close();
		}catch(Exception e){
			retResult = "failure";
			log.debug(e.getMessage());
		}
		return retResult;
	}

}
