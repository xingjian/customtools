/** @文件名: DBUtil.java @创建人：邢健  @创建日期： 2014-5-11 下午3:28:23 */
package com.promise.adcd.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import com.promise.adcd.vo.ADCDVO;

/**
 * @类名: DBUtil.java
 * @包名: com.promise.adcd.util
 * @描述: TODO
 * @作者: xingjian xingjian@yeah.net
 * @日期:2014-5-11 下午3:28:23
 * @版本: V1.0
 */
public class DBUtil {

	public static Connection connect = null;
	public static String username = "root";
	public static String password = "000000";
	public static String url = "jdbc:mysql://localhost:3386/test";

	public static Connection connectSQLServer = null;
	public static String usernameSQLServer = "floodwarn";
	public static String passwordSQLServer = "floodwarn";
	public static String urlSQLServer = "jdbc:sqlserver://172.18.18.35:1433;DatabaseName=floodwarnprovince";
	
	
	public static Connection GetConnection() {
		if (connect == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connect = DriverManager.getConnection(url, username, password);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return connect;
	}
	
	public static Connection GetSQLServerConnection() {
		if (connectSQLServer == null) {
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				connectSQLServer = DriverManager.getConnection(urlSQLServer, usernameSQLServer, passwordSQLServer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return connectSQLServer;
	}

	public static void insertADCD(ADCDVO adcdvo) {
		String sql = "insert into adcd values(?,?)";
		GetConnection();
		PreparedStatement pst;
		try {
			pst = GetConnection().prepareStatement(sql);
			pst.setString(1, adcdvo.getCode());
			pst.setString(2, adcdvo.getName());
			pst.execute();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<ADCDVO> GetAllADCDVO(){
        GetConnection();
        List<ADCDVO> list = new ArrayList<ADCDVO>();
        String sql = "select * from adcd";
        PreparedStatement pst;
        try {
            pst = connect.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
            	ADCDVO adcdvo = new ADCDVO();
            	adcdvo.setCode(rs.getString(1));
            	adcdvo.setName(rs.getString(2));
            	list.add(adcdvo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
   }
	
	public static String MySQLDBToSQLServer(String mysql,String sqlserver){
		GetConnection();
		GetSQLServerConnection();
		PreparedStatement pst;
		PreparedStatement pstSQLServer;
		String sqlSQLServer = "insert into AD_CD_B values(?,?,?,?)";
        try {
            pst = connect.prepareStatement(mysql);
            pstSQLServer = connectSQLServer.prepareStatement(sqlSQLServer);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                    String adcdCode = rs.getString(1);
                    String adcdName = rs.getString(2);
                    pstSQLServer.setString(1, adcdCode);
                    pstSQLServer.setString(2, adcdName);
                    pstSQLServer.setDouble(3, 0);
                    pstSQLServer.setDouble(4, 0);
                    pstSQLServer.execute();
            }
            pstSQLServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return "success";
	}
	
	public static String ReadADCDToExcelBySQL(String sql,String fileName){
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
		GetConnection();
		PreparedStatement pst;
        try {
        	int i=0;
            pst = connect.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
            	HSSFRow row = sheet.createRow(i + 1);
                for (int colu=0;colu<names.length;colu++) {
                    HSSFCell xh = row.createCell(0);
                    xh.setCellValue(rs.getString(1));
                    HSSFCell xm = row.createCell(1);
                    xm.setCellValue(rs.getString(2));
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      //创建文件输出流，准备输出电子表格
        try{
        	OutputStream out = new FileOutputStream(fileName+".xls");
        	hwb.write(out);
        	out.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
		return "success";
	}
	
	public static String ReadADCDToExcelBySQLForMSheet(String sql,String fileName){
		int countColumnNum = 2;
		HSSFWorkbook hwb = new HSSFWorkbook();
		HSSFSheet sheet = hwb.createSheet("行政区划代码");
		HSSFRow firstrow = sheet.createRow(0); // 下标为0的行开始
		
		HSSFSheet sheet2 = hwb.createSheet("行政区划代码2");
		HSSFRow firstrow2 = sheet.createRow(0); // 下标为0的行开始
		
        HSSFCell[] firstcell = new HSSFCell[countColumnNum];
        String[] names = new String[countColumnNum];
        names[0] = "编码";
        names[1] = "名称";
        for (int j = 0; j < countColumnNum; j++){
            firstcell[j] = firstrow.createCell(j);
            firstcell[j].setCellValue(new HSSFRichTextString(names[j]));
        }
		GetConnection();
		PreparedStatement pst;
        try {
        	int i=0;
            int j=0;
        	pst = connect.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
            	if(i<60000){
            		HSSFRow row = sheet.createRow(i + 1);
                    for (int colu=0;colu<names.length;colu++) {
                        HSSFCell xh = row.createCell(0);
                        xh.setCellValue(rs.getString(1));
                        HSSFCell xm = row.createCell(1);
                        xm.setCellValue(rs.getString(2));
                    }
                    i++;
            	}else{
            		HSSFRow row = sheet2.createRow(j + 1);
                    for (int colu=0;colu<names.length;colu++) {
                        HSSFCell xh = row.createCell(0);
                        xh.setCellValue(rs.getString(1));
                        HSSFCell xm = row.createCell(1);
                        xm.setCellValue(rs.getString(2));
                    }
                    j++;
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      //创建文件输出流，准备输出电子表格
        try{
        	OutputStream out = new FileOutputStream(fileName+".xls");
        	hwb.write(out);
        	out.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
		return "success";
	}
	
	public static void UpdateSQLServer(String adcd,double lgtd,double lttd){
		String upSql = "update AD_CD_B set lgtd=?,lttd=? where adcd=?";
		try {
			GetSQLServerConnection();
			PreparedStatement pstSQLServer = connectSQLServer.prepareStatement(upSql);
            pstSQLServer.setDouble(1, lgtd);
            pstSQLServer.setDouble(2, lttd);
            pstSQLServer.setString(3, adcd);
            pstSQLServer.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
