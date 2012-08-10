/** @文件名: ImageRequestUtil.java @创建人：邢健  @创建日期： 2012-8-6 下午4:04:02 */

package com.promise.cn.download.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**   
 * @类名: ImageRequestUtil.java 
 * @包名: com.promise.cn.download.utils 
 * @描述: 图片请求流 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2012-8-6 下午4:04:02 
 * @版本: V1.0   
 */
@SuppressWarnings("all")
public class ImageRequestUtil {

	
		public void downGoogleMap(int z,String serverName) throws Exception{
			int xMax = 8;
			for(int i=0;i<xMax;i++){
				for(int j=0;j<xMax;j++){
					String webURL="http://"+serverName+".google.cn/vt/lyrs=m@180000000&hl=zh-CN&gl=cn&src=app&x="+i+"&y="+j+"&z="+z+"";
					URL url = new URL(webURL);
			        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			        conn.setRequestMethod("GET");
			        conn.setConnectTimeout(5*1000);
			        InputStream inStream =  conn.getInputStream();//通过输入流获取图片数据
			        byte[] data = readInputStream(inStream);//得到图片的二进制数据
			        File ImageFile = new File(i+"-"+j+".png");//存到本地硬盘名为
			        FileOutputStream  outstream = new FileOutputStream(ImageFile);
			        outstream.write(data);
			        outstream.close();
				}
			}
		}
	
		/**
		 **@param args
	     *@throws MalformedURLException
	     */
	    public static void main(String[] args) throws Exception{
	    	String mapServer = "http://172.18.18.203:8399/arcgis/rest/services/xingjiantest/xingjiantest_traffic/MapServer/export?size=630,559&f=image&dpi=96&bboxSR=4326&bbox=116.04812150673,39.59411654409708,116.81745682435022,40.27674899258867&transparent=true&imageSR=4326&format=png8";
	    	String mapServer1 = "http://mt1.google.cn/vt/lyrs=m@180000000&hl=zh-CN&gl=cn&src=app&x=843&y=387&z=10&s=Gali";
	    	String webURL="http://mt2.google.cn/vt/lyrs=m@180000000&hl=zh-CN&gl=cn&src=app&x=3&y=3&z=2";
	   
	    	ImageRequestUtil imageRequestUtil = new ImageRequestUtil();
	    	imageRequestUtil.downGoogleMap(3, "mt2");
	    }
		public static byte[] readInputStream(InputStream instream) throws Exception{
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[]  buffer = new byte[1204];
			int len = 0;
			while ((len = instream.read(buffer)) != -1){
			    outStream.write(buffer,0,len);
			}
			instream.close();
			return outStream.toByteArray();        
		}


}
