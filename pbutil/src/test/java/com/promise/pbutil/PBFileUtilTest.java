package com.promise.pbutil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.PrintUtil;

/**  
 * 功能描述:PBFileUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月7日 上午9:33:41  
 */
public class PBFileUtilTest {

    @Test
    public void testReadPropertiesFile(){
        String filePath = "G:\\项目文档\\公交都市\\数据\\busline.properties";
        Map<String,String> map= PBFileUtil.ReadPropertiesFile(filePath);
        PrintUtil.PrintObject(map);
    }
    
    @Test
    public void testExportDataBySQL(){
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select array_to_string(ARRAY(SELECT unnest(array_agg(wkt))),',') wkts,array_to_string(ARRAY(SELECT unnest(array_agg(busstationid)) "+
            "),',') busstationids ,linkid,name from (select rank() OVER (PARTITION BY t1.id ORDER BY t1.id) rank,ST_AsText(t1.the_geom) wkt,t1.id,t1.busstationid,t1.linkid,t2.name from busstationlink  t1 left join busstation t2 on t1.busstationid= t2.id order by name desc) t3 group by linkid,name";
       String excelPath = "d:\\result.xls";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    @Test
    public void testExportDataBySQLLIQ(){
        String sql = "select c.linename,a.tjcc,a.name,a.index,st_x(b.the_geom) x,st_y(b.the_geom) y from busstation a "+
        "left join busstationlink b on a.id = b.busstationid "+
        "left join busline c on a.buslineid = c.id "+
        "where 1=1 "+
        "and buslineid in("+
        "select id "+
        "from busline "+ 
        "where 1=1 "+
        "and linename in("+
        "'44外','44内','345快','344快','885','882','837快','835快','838','616','631快','366快','937','808','806','669','668','667','133','57','701','118','300外','300内','300快外','300快内','740外1','740内','988','973','621','486','99','52','1','332'"+
        ")) "+
        " order by tjcc,buslineid,index";
        
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
       String excelPath = "d:\\result.xls";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    @Test
    public void testReadCSVFile() throws Exception{
        String filePath = "G:\\项目文档\\节能减排\\paifall\\paifall.csv";
        String url = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username = "postgres";
        String passwd = "postgres";
        String insertSQL = "INSERT INTO paifall(linkid, flow, speed, dtime, co, co2, hc, nox, pm, oil, intime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        int eCount = 0;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            while((s = br.readLine())!=null){
                if(s.trim()!=""){
                    s = s.replace("\"", "");
                    String[] rowData = s.split(",");
                    ps.setString(1,rowData[0]);
                    ps.setDouble(2, Double.parseDouble(rowData[1]));
                    ps.setDouble(3, Double.parseDouble(rowData[2]));
                    ps.setString(4,rowData[3]);
                    ps.setDouble(5, Double.parseDouble(rowData[4]));
                    ps.setDouble(6, Double.parseDouble(rowData[5]));
                    ps.setDouble(7, Double.parseDouble(rowData[6]));
                    ps.setDouble(8, Double.parseDouble(rowData[7]));
                    ps.setDouble(9, Double.parseDouble(rowData[8]));
                    ps.setDouble(10, Double.parseDouble(rowData[9]));
                    ps.setString(11,rowData[10]);
                    ps.addBatch();
                    eCount++;
                    if(eCount%5000==0){
                        ps.executeBatch();
                    }
                }
            }
            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ps.executeBatch();
    }
    
    @Test
    public void testArea4Export(){
        String url = "jdbc:postgresql://localhost:5432/sw_navigation";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
       String excelPath = "d:\\result.xls";
       String result = POIExcelUtil.ExportDataBySQL("select * from area4", connection, excelPath);
    }
}
