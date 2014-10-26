/** @文件名: AndroidMessageTest.java @创建人：邢健  @创建日期： 2013-7-5 下午5:39:44 */

package other;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

/**   
 * @类名: AndroidMessageTest.java 
 * @包名: other 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-7-5 下午5:39:44 
 * @版本: V1.0   
 */
public class AndroidMessageTest {

	@Test
	public void sendMessage(){
		URL url;
		try {
			url = new URL("http://10.10.191.1:8888/AndroidpnSecond/notification_api.do?action=send");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-type", "application/x-java-serialized-object");
			conn.addRequestProperty("apiKey", "1234567890");
	        conn.addRequestProperty("username", "");
	        conn.addRequestProperty("broadcast", "Y");
	        conn.addRequestProperty("title", "admin");
	        conn.addRequestProperty("message","aaaaaaaaaaaaa");
	        conn.addRequestProperty("remindType","bbbbbbbbbbb");
	        conn.addRequestProperty("uri","");
	        conn.setRequestMethod("POST");
	        conn.setConnectTimeout(5*1000);
	        conn.connect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
