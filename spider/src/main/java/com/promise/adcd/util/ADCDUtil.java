/** @文件名: ADCDUtil.java @创建人：邢健  @创建日期： 2014-5-9 下午4:08:00 */
package com.promise.adcd.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.promise.adcd.vo.ADCDVO;

/**   
 * @类名: ADCDUtil.java 
 * @包名: com.promise.adcd.util 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-5-9 下午4:08:00 
 * @版本: V1.0   
 */
public class ADCDUtil {

	/**
	 * 将行政区划信息写入excel
	 * @param list
	 * @param file
	 * @return
	 */
	public static String WriteADCDToExcel(List<ADCDVO> list){
		String retResult = "success";
		int countColumnNum = 2;
		HSSFWorkbook hwb = new HSSFWorkbook();
		HSSFSheet sheet = hwb.createSheet("行政区划代码");
		HSSFRow firstrow = sheet.createRow(0); // 下标为0的行开始
        HSSFCell[] firstcell = new HSSFCell[countColumnNum];
        String[] names = new String[countColumnNum];
        names[0] = "编码";
        names[1] = "名称";
        
        for (int j = 0; j < countColumnNum; j++){
            firstcell[j] = firstrow.createCell(j);
            firstcell[j].setCellValue(new HSSFRichTextString(names[j]));
        }
        for (int i = 0; i < list.size(); i++) {
            HSSFRow row = sheet.createRow(i + 1);
            ADCDVO adcdvo = list.get(i);
            for (int colu=0;colu<names.length;colu++) {
                HSSFCell xh = row.createCell(0);
                xh.setCellValue(adcdvo.getCode());
                HSSFCell xm = row.createCell(1);
                xm.setCellValue(adcdvo.getName());
            }
        }
        //创建文件输出流，准备输出电子表格
        try{
        	OutputStream out = new FileOutputStream("shanxi_adcd.xls");
        	hwb.write(out);
        	out.close();
        }catch(Exception e){
        	retResult = "failure";
        	e.printStackTrace();
        }
		return retResult;
	}
	
	public static String ReadADCDXLS(File file) throws Exception{
	    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
	    POIFSFileSystem fs = new POIFSFileSystem(in);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFCell cell = null;
        HSSFCell cell1 = null;
        HSSFCell cell2 = null;
        HSSFCell cell3 = null;
        for(int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++){
        	HSSFSheet st = wb.getSheetAt(sheetIndex);
        	for (int rowIndex = 1; rowIndex <= st.getLastRowNum(); rowIndex++) {
               HSSFRow row = st.getRow(rowIndex);
               if (row == null) {
                   continue;
               }
               cell = row.getCell(0);
               cell1 = row.getCell(1);
               cell2 = row.getCell(2);
               cell3 = row.getCell(3);
               if(null!=cell){
            	   String adcd = cell.getStringCellValue();
            	   String adnm = cell1.getStringCellValue();
            	   double lgtd = cell2.getNumericCellValue();
            	   double lttd = cell3.getNumericCellValue();
            	   DBUtil.UpdateSQLServer(adcd,lgtd,lttd);
               }
        	}
        }
        return "success";
	}
}
