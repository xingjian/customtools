/** @文件名: ProvinceDBUtil.java @创建人：邢健  @创建日期： 2014-6-20 上午9:36:20 */
package com.promise.province;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.promise.db.DBUtil;
/**   
 * @类名: ProvinceDBUtil.java 
 * @包名: com.promise.province 
 * @描述: 省平台辅助类 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-6-20 上午9:36:20 
 * @版本: V1.0   
 */
public class ProvinceDBUtil {

	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	
	/**
	 * 读取测站excel到province
	 * @param file
	 */
	public static String ReadStationExcelToDataBase(File file) throws Exception{
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		XSSFWorkbook wb = new XSSFWorkbook(in);
		String sqlSTINFOB = "insert into ST_INFO_B (STCD,CACD,ADCD,MODITIME) values(?,?,?,?)";
		String sqlSTSTBPRPB = "insert into ST_STBPRP_B(STCD,STNM,RVNM,HNNM,BSNM,LGTD,LTTD,STLC,ADDVCD,STTP,PHCD,MODITIME)values(?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection connect = DBUtil.GetSQLServerConnection();
        PreparedStatement pst1 = connect.prepareStatement(sqlSTSTBPRPB);
        PreparedStatement pst2 = connect.prepareStatement(sqlSTINFOB);
		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			XSSFSheet st = wb.getSheetAt(sheetIndex);
			for (int rowIndex = 1; rowIndex <= st.getLastRowNum(); rowIndex++) {
				XSSFRow row = st.getRow(rowIndex);
				if(row == null){
					continue;
				}
				System.out.println("rowIndex==="+rowIndex);
	            pst1.setString(1,row.getCell(0).getStringCellValue());
	            pst1.setString(2,row.getCell(1).getStringCellValue());
	            pst1.setString(3,"1410220005");
	            pst1.setString(4,row.getCell(3).getStringCellValue());
	            pst1.setString(5,row.getCell(4).getStringCellValue());
	            pst1.setDouble(6,row.getCell(5).getNumericCellValue());
	            pst1.setDouble(7,row.getCell(6).getNumericCellValue());
	            pst1.setString(8,row.getCell(7).getStringCellValue());
	            pst1.setString(9,row.getCell(11).getStringCellValue());
	            pst1.setString(10,row.getCell(8).getStringCellValue());
	            String cell9Str = "";
	            if(null!=row.getCell(9)){
	            	cell9Str =row.getCell(9).getStringCellValue();
	            }
	            pst1.setString(11,cell9Str);
	            Date date = new Date();
	            pst1.setDate(12,new java.sql.Date(date.getTime()));
	            pst1.execute();
	            String cell3Str = "";
	            if(null!=row.getCell(10)){
	            	cell3Str = row.getCell(10).getStringCellValue();
	            }
	            pst2.setString(1,row.getCell(0).getStringCellValue());
	            pst2.setString(2,cell3Str);
	            pst2.setString(3,row.getCell(11).getStringCellValue());
	            pst2.setDate(4,new java.sql.Date(date.getTime()));
	            pst2.execute();
	            
			}
		}
		pst1.close();
		pst2.close();
		connect.close();
		return SUCCESS;
	}
	
	
	public static void main(String[] args) {
		String filePath = "E:/eclipse4.2HomeWorkspace/poiapply/山西省.xlsx";
		System.out.println(filePath);
		File file = new File(filePath);
		String result = "";
		try {
			result = ReadStationExcelToDataBase(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result);
	}

}
