/** @文件名: TestMapImageService.java @创建人：邢健  @创建日期： 2012-8-7 下午3:31:39 */

package googlemap;

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import com.promise.cn.googlemap.service.MapImageService;
import com.promise.cn.googlemap.service.impl.MapImageServiceImpl;

/**   
 * @类名: TestMapImageService.java 
 * @包名: googlemap 
 * @描述: 测试 MapImageService
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2012-8-7 下午3:31:39 
 * @版本: V1.0   
 */
@SuppressWarnings("all")
public class TestMapImageService {

	private MapImageService mapImageService;

	@Before
	public void init(){
		mapImageService = new MapImageServiceImpl();
	}
	
	@Test
	public void testMergeImage(){
		String source = "E:\\eclipse4.2jeeWorkspace\\GISTools\\googleimage";
		mapImageService.mergeImage(source, source, 125, 197,0,0);
	}
}
