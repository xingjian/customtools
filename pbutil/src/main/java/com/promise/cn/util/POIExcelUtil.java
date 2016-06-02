package com.promise.cn.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
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
     * 根据集合导出excel
     * 标准的domain写法
     * @param list
     * @param excelPath
     * @return 导出结果
     */
    public static <T> String ExportDataByList(List<T> list, String excelPath) {
        String result = "success";
        try {
            Field[] fds = null;
            Class clazz = null;
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            int iRow=1;
            //遍历集合
            for (Object object : list) {
                //获取集合中的对象类型
                if (null == clazz) {
                    clazz = object.getClass();
                    workbook.setSheetName(0,clazz.getName());
                    //获取他的字段数组
                    fds = clazz.getDeclaredFields();
                    HSSFRow row= sheet.createRow(0); 
                    HSSFCell cell;
                    for(int i=0;i<fds.length;i++){  
                        cell = row.createCell(i);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
                        cell.setCellValue(fds[i].getName());  
                    }
                }
               //遍历该数组
               HSSFRow row = sheet.createRow(iRow);
               for(int i=0;i<fds.length;i++){
                   HSSFCell cell = row.createCell(i);
                   cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                   //得到字段名
                   String fdname = fds[i].getName();
                   //根据字段名找到对应的get方法，null表示无参数
                   Method metd = clazz.getMethod("get"+ChangeFirstUpper(fdname), null);
                   if (null != metd) {
                       //调用该字段的get方法
                       Object name = metd.invoke(object, null);
                       cell.setCellValue(name.toString());
                   }else{
                       cell.setCellValue("no method");
                   }
               } 
               iRow++;
            }
            FileOutputStream fOut = new FileOutputStream(excelPath);
            workbook.write(fOut);
            fOut.flush();
            fOut.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = "fail";
        }
        return result;
    }
    
    /**
     * @param src
     * 源字符串
     * @return 字符串，将src的第一个字母转换为大写，src为空时返回null
     */
    public static String ChangeFirstUpper(String src) {
        if (src != null) {
            StringBuffer sb = new StringBuffer(src);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        } else {
            return null;
        }
    }
    
    /**
     * 通过sql导出数据到excel
     * @param sql
     * @param conect
     * @return
     */
    public static String ExportDataBySQL(String sql,Connection connect,String excelPath){
        String result = "success";
        try{
            ResultSet resultSet = connect.createStatement().executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int nColumn = rsmd.getColumnCount();
            HSSFWorkbook workbook = new HSSFWorkbook();  
            HSSFSheet sheet = workbook.createSheet();  
            workbook.setSheetName(0,"export1");  
            HSSFRow row= sheet.createRow(0); 
            HSSFCell cell;
            for(int i=1;i<=nColumn;i++){  
                cell = row.createCell(i-1);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
                cell.setCellValue(rsmd.getColumnLabel(i));  
           }
          int iRow=1;
          //写入各条记录，每条记录对应Excel中的一行
          while(resultSet.next()){
              row= sheet.createRow(iRow);
              for(int j=1;j<=nColumn;j++){
                  cell = row.createCell(j-1);
                  cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                  if(null==resultSet.getObject(j)){
                      cell.setCellValue("");
                  }else{
                      cell.setCellValue(resultSet.getObject(j).toString());
                  }
              }
              iRow++;
          }
          FileOutputStream fOut = new FileOutputStream(excelPath);
          workbook.write(fOut);
          fOut.flush();
          fOut.close();
        }catch(Exception e){
            result = "error";
            e.printStackTrace();
        }
        return result;
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
