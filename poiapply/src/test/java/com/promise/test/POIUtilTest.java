/** @文件名: POIUtilTest.java @创建人：邢健  @创建日期： 2013-9-27 下午3:39:01 */
package com.promise.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import com.promise.utils.ExcelUtil;
import com.promise.utils.POIUtil;

/**   
 * @类名: POIUtilTest.java 
 * @包名: com.promise.test 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-9-27 下午3:39:01 
 * @版本: V1.0   
 */
public class POIUtilTest {

	@Test
	public void testReadXLS() throws Exception{
		List<String> list = POIUtil.ReadXLSX(new File(this.getClass().getResource("/").getPath()+"STWarn_Record_Detail.xlsx"));
		for(String s:list){
			System.out.println(s);
		}
	}
	
	@Test
	public void testBigExcel() throws Exception{
		File excel_file = new File(this.getClass().getResource("/").getPath()+"STWarn_Record_Detail.xlsx");//读取的文件路径    
        FileInputStream input = new FileInputStream(excel_file);  //读取的文件路径   
        XSSFWorkbook wb = new XSSFWorkbook(new BufferedInputStream(input)); 
	}
	
	@Test
	public void testExcelUtil()throws Exception{
		ExcelUtil eu = new ExcelUtil();
		eu.process(this.getClass().getResource("/").getPath()+"STWarn_Record_Detail.xlsx");
	}
}
