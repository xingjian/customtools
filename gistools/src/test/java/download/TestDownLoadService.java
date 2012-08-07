/** @文件名: TestDownLoadService.java @创建人：邢健  @创建日期： 2012-8-6 下午2:57:14 */

package download;

import org.junit.Test;

import com.promise.cn.download.service.DownLoadService;
import com.promise.cn.download.service.impl.DownLoadServiceImpl;
import com.promise.cn.download.vo.HttpRespons;

/**   
 * @类名: TestDownLoadService.java 
 * @包名: download 
 * @描述: 测试downloadservice 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2012-8-6 下午2:57:14 
 * @版本: V1.0   
 */
@SuppressWarnings("all")
public class TestDownLoadService {

	/**
	 * 测试sendGet
	 */
	@Test
	public void testSendGet(){
		try {      
			//http://172.18.18.31:8399/arcgis/rest/services/bjroad6/MapServer/tile/2/1/1
			DownLoadService request = new DownLoadServiceImpl();
            HttpRespons hr = request.sendGet("http://www.iteye.com/");      
//            System.out.println(hr.getUrlString());      
//            System.out.println(hr.getProtocol());      
//            System.out.println(hr.getHost());      
//            System.out.println(hr.getPort());      
//            System.out.println(hr.getContentEncoding());      
//            System.out.println(hr.getMethod());      
//            System.out.println(hr.getContent());      
        } catch (Exception e) {      
            e.printStackTrace();      
        }  
	}
}
