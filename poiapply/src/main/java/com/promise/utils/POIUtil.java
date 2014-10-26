/** @文件名: POIUtil.java @创建人：邢健  @创建日期： 2013-9-27 下午3:39:33 */
package com.promise.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**   
 * @类名: POIUtil.java 
 * @包名: com.promise.utils 
 * @描述: 帮助类 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-9-27 下午3:39:33 
 * @版本: V1.0   
 */
public class POIUtil {

	/**
	 * 通过路径读取xls文件并把每行按字符串输出
	 * @param fileURL
	 * @return
	 */
	public static List<String> ReadXLS(File file) throws Exception{
		List<String> retList = new ArrayList<String>();
	    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
	    POIFSFileSystem fs = new POIFSFileSystem(in);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFCell cell = null;
        for(int sheetIndex = 1; sheetIndex < wb.getNumberOfSheets(); sheetIndex++){
        	HSSFSheet st = wb.getSheetAt(sheetIndex);
        	for (int rowIndex = 0; rowIndex <= 12; rowIndex++) {
               HSSFRow row = st.getRow(rowIndex);
               if (row == null) {
                   continue;
               }
               cell = row.getCell(0);
               if(null!=cell){
            	   System.out.println(cell.getCellType());
//            	  retList.add(cell.getStringCellValue());
            	  if(cell.getCellType()==0){
            		  System.out.println(cell.getNumericCellValue());
            	  }else{
            		  retList.add(cell.getStringCellValue());
            	  }
            	  
               }
        	}
        }
        return retList;
	}
	
	
	public static List<String> ReadXLSX(File file) throws Exception{
		List<String> retList = new ArrayList<String>();
	    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
//	    POIFSFileSystem fs = new POIFSFileSystem(in);
	    XSSFWorkbook wb  = new XSSFWorkbook(in);
        XSSFCell cell = null;
        for(int sheetIndex = 1; sheetIndex < wb.getNumberOfSheets(); sheetIndex++){
        	XSSFSheet st = wb.getSheetAt(sheetIndex);
        	for (int rowIndex = 0; rowIndex <= 12; rowIndex++) {
               XSSFRow row = st.getRow(rowIndex);
               if (row == null) {
                   continue;
               }
               cell = row.getCell(0);
               if(null!=cell){
            	   System.out.println(cell.getCellType());
//            	  retList.add(cell.getStringCellValue());
            	  if(cell.getCellType()==0){
            		  System.out.println(cell.getNumericCellValue());
            	  }else{
            		  retList.add(cell.getStringCellValue());
            	  }
            	  
               }
        	}
        }
        return retList;
	}
}
