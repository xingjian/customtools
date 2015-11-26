package com.promise.cn.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.NumberToTextConverter;

/**  
 * 功能描述: poiexcel帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年8月27日 下午1:01:52  
 */
public class POIExcelUtil {
    
    /**
     * 读取excel文件,将每行数据按照分隔符拼接成字符组存放到集合当中
     * 目前支持2003版本
     * @param separator 分隔符
     * @param filePath 文件路径
     * @return 字符串集合
     */
    public static List<String> ReadXLS(String filePath,String separator,int rowSNum,int rowENum,int columnSNum,int columnENum){
        List<String> retList = new ArrayList<String>();
        try{
            File file = new File(filePath);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            POIFSFileSystem fs = new POIFSFileSystem(in);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            for(int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++){
                HSSFSheet st = wb.getSheetAt(sheetIndex);
                for (int rowIndex = rowSNum-1; rowIndex < rowENum; rowIndex++) {
                   HSSFRow row = st.getRow(rowIndex);
                   if (row == null) {
                       continue;
                   }
                   String rowStr="";
                   for(int columnIndex=columnSNum-1;columnIndex<columnENum;columnIndex++){
                       Cell cell = row.getCell(columnIndex);
                       String cellValue = GetCellValue(cell);
                       if(cellValue.trim().equals("")){
                           cellValue="0";
                       }
                       if(columnIndex==columnENum-1){
                           rowStr +=cellValue;
                       }else{
                           rowStr +=cellValue+separator;    
                       }
                       
                   }
                   retList.add(rowStr);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return retList;
    }
    
    /**
     * 读取excel指定行，并转换拼音
     * 为了创建表用
     * @param filePath 路径
     * @param row 指定行
     * @param column 列数
     * @return
     */
    public static List<String> GetExcelRowPinYin(String filePath,int sheetIndex,int rowIndex){
        List<String> retList = new ArrayList<String>();
        try{
            File file = new File(filePath);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            POIFSFileSystem fs = new POIFSFileSystem(in);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet st = wb.getSheetAt(sheetIndex);
            HSSFRow row = st.getRow(rowIndex);
            if (row == null) {
                return retList;
            }
            Iterator<Cell> iter = row.cellIterator();
            while(iter.hasNext()){
                Cell cell = iter.next();
                String cellValue = GetCellValue(cell);
                retList.add(StringUtil.ConverterToSpell(cellValue));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return retList;
    }
    
    /**
     * 读取Cell内容
     * @param cell
     * @return cell内容
     */
    public static String GetCellValue(Cell cell){
        if(null==cell){
            return "NULL";
        }
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_BLANK:
                return "";
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_ERROR:
                break;
            case Cell.CELL_TYPE_FORMULA:
                return cell.getRichStringCellValue().getString();
            case Cell.CELL_TYPE_NUMERIC:
                return NumberToTextConverter.toText(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default :
                return cell.getStringCellValue();
        }
        return cell.getStringCellValue();
    }
}
