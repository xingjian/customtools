/** @文件名: ADCDTest.java @创建人：邢健  @创建日期： 2014-5-8 下午4:06:59 */
package adcd;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.promise.adcd.service.ADCDService;
import com.promise.adcd.service.impl.ADCDServiceImpl;
import com.promise.adcd.util.ADCDUtil;
import com.promise.adcd.util.DBUtil;
import com.promise.adcd.vo.ADCDVO;

/**   
 * @类名: ADCDTest.java 
 * @包名: adcd 
 * @描述: 测试类 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-5-8 下午4:06:59 
 * @版本: V1.0   
 */
@SuppressWarnings("all")
public class ADCDTest {

	private ADCDService adcdService = new ADCDServiceImpl();
	
	/**
	 * 测试adcdService的getADCDPage方法
	 */
	@Test
	public void testGetADCDPage(){
		String htmlURL = "http://www.stats.gov.cn/zjtj/tjbz/tjyqhdmhcxhfdm/2012/65.html";
		String saveSrc = "E:\\eclipse4.2HomeWorkspace\\spider\\src\\test\\java\\14.txt";
		String regStr = "";
		boolean isLoop = false;
		int depth = 0;
		String method = "POST";
		String result = adcdService.getADCDPage(htmlURL, saveSrc, regStr, isLoop, depth, method);
		//将下载好的数据存储到文本文件中
		if(result.equals("success")){
			System.out.println("开始制作EXCEL,共计:"+adcdService.adcdvoList.size()+"条数据.");
			String resultStr = ADCDUtil.WriteADCDToExcel(adcdService.adcdvoList);
			System.out.println(resultStr);
		}
	}
	
	/**
	 * 测试adcdService的getADCDPage方法
	 */
	@Test
	public void testGetVillageListByURL(){
		String htmlURL = "http://www.stats.gov.cn/zjtj/tjbz/tjyqhdmhcxhfdm/2012/14/01/05/140105002.html";
		String saveSrc = "E:\\eclipse4.2HomeWorkspace\\spider\\src\\test\\java\\14.txt";
		String regStr = "";
		boolean isLoop = false;
		int depth = 0;
		String method = "POST";
		String retStr = adcdService.getVillageListByURL(htmlURL, saveSrc, regStr, isLoop, depth, method);
		if(retStr.equals("success")){
			for(ADCDVO adcdvo:adcdService.adcdvoList){
				System.out.println(adcdvo.getCode()+"---"+adcdvo.getName());
			}
		}
	}
	
	/**
	 * 测试保存数据到excel
	 */
	@Test
	public void testWriteADCDToExcel(){
		String htmlURL = "http://www.stats.gov.cn/zjtj/tjbz/tjyqhdmhcxhfdm/2012/14/01/05/140105002.html";
		String saveSrc = "E:\\eclipse4.2HomeWorkspace\\spider\\src\\test\\java\\14.txt";
		String regStr = "";
		boolean isLoop = false;
		int depth = 0;
		String method = "POST";
		String retStr = adcdService.getVillageListByURL(htmlURL, saveSrc, regStr, isLoop, depth, method);
		if(retStr.equals("success")){
			String result = ADCDUtil.WriteADCDToExcel(adcdService.adcdvoList);
			System.out.println(result);
		}
	}
	
	@Test
	public void testGetConnection(){
		DBUtil.GetConnection();
	}
	
	@Test
	public void testGetAllADCDVO(){
		List<ADCDVO> list = DBUtil.GetAllADCDVO();
		System.out.println(list.size());
	}
	
	/**
	 * test adcd conten write to excel
	 */
	@Test
	public void testWriteExcel(){
		List<ADCDVO> list = DBUtil.GetAllADCDVO();
		String result = ADCDUtil.WriteADCDToExcel(list);
		System.out.println(result);
	}
	
	@Test
	public void testString(){
		String adcdurl = "http://www.stats.gov.cn/zjtj/tjbz/tjyqhdmhcxhfdm/2012/14/01/05/140105001.html";
		System.out.println(adcdurl.substring(adcdurl.lastIndexOf("/")+1, adcdurl.lastIndexOf(".html")));
		System.out.println(adcdurl.substring(adcdurl.lastIndexOf("/")+1, adcdurl.lastIndexOf(".html")).length()==9);
	}
	
	@Test
	public void testReadADCDToExcelBySQL(){
		String sql = "select * from adcd where adcd LIKE '37%%%%%%%%%%%%%'";
		DBUtil.ReadADCDToExcelBySQL(sql, "山东");
	}
	
	@Test
	public void testMysqlDBToSQLServer(){
		String sql = "select * from adcd where adcd LIKE '14%%%%%%%%%%%%%'";
		String result = DBUtil.MySQLDBToSQLServer(sql, "");
		System.out.println(result);
	}
	
	@Test
	public void testReadADCDXLS(){
		File file = new File("shanxiadcd.xls");
		try {
			ADCDUtil.ReadADCDXLS(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
