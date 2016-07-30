package com.promise.pbutil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
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
    
    @Test
    public void testReadCSVFile1() throws Exception{
        String filePath = "G:\\项目文档\\节能减排\\data\\trafficflow.csv";
        List<String> list = PBFileUtil.ReadCSVFile(filePath, "UTF-8");
        String url = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username = "postgres";
        String passwd = "postgres";
        String insertSQL = "INSERT INTO trafficflow(linkid, roadtype, conf, district, loop, startnode, endnode, sectype,"+ 
            "seclen, linenum, flow_all, flow_car, flow_bus, flow_taxi, load,"+ 
            "speed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        int countIndex = 0;
        for(String str:list){
            String[] arr = str.split(",");
            ps.setInt(1, Integer.parseInt(arr[0]));
            ps.setString(2, arr[1]);
            ps.setString(3, arr[2]);
            ps.setString(4, arr[3]);
            ps.setString(5, arr[4]);
            ps.setInt(6, Integer.parseInt(arr[5]));
            ps.setInt(7, Integer.parseInt(arr[6]));
            ps.setString(8, arr[7]);
            ps.setDouble(9, Double.parseDouble(arr[8]));
            ps.setInt(10, Integer.parseInt(arr[9]));
            ps.setDouble(11, Double.parseDouble(arr[10]));
            ps.setDouble(12, Double.parseDouble(arr[11]));
            ps.setDouble(13, Double.parseDouble(arr[12]));
            ps.setDouble(14, Double.parseDouble(arr[13]));
            ps.setInt(15, Integer.parseInt(arr[14]));
            ps.setDouble(16, Double.parseDouble(arr[15]));
            ps.addBatch();
            countIndex++;
            if(countIndex%5000==0){
                ps.executeBatch();
            }
        }
        ps.executeBatch();
    }
    
    /**
     * 提供王绪华给工大的数据
     */
    @Test
    public void testData_wxh(){
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
       String excelPath = "d:\\subwaystation.xls";
       String result = POIExcelUtil.ExportDataBySQL("select name,tjcc,index,linecode,st_x(the_geom) x,st_y(the_geom) y from subwaystation order by tjcc,index", connection, excelPath);
    }
    
    @Test
    public void testData_wxh1(){
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String excelPath = "d:\\busstation.xls";
        String sql = "select * from (select t2.label,t2.linecode,t1.stationorder,t1.name,t1.index,t1.stationid,t1.x,t1.y from ("+
                "select name,index,buslineid,stationorder,stationid,st_x(the_geom) x,st_y(the_geom) y from busstation where buslineid in (select id from busline where isvalid='1') "+
                ") t1 left join busline t2 on t1.buslineid= t2.id ) t3 order by t3.label,t3.index";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    @Test
    public void testData_wxh2(){
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String excelPath = "d:\\bikestation.xls";
        String sql = "select netid,name,x,y from bikestation";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    //
    @Test
    public void testData_wxh3(){
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String excelPath = "d:\\busline_-1.xls";
        String sql = "select id,label,name  from busline where isvalid!='1' and state='-1'";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    @Test
    public void testData_wxh4(){
        String url = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String username = "buscity";
        String passwd = "admin123ttyj7890uiop";
        Connection connection = DBConnection.GetOracleConnection(url, username, passwd);
        String excelPath = "d:\\de_base_station_change.xls";
        String sql = "select * from de_base_station_change";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    
    @Test
    public void testData_wxh6(){
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String excelPath = "d:\\busstation_adcd_no.xls";
        String sql = "select tjcc as label, name,adcd_code adcdcode,stationid,adcd_name from busstation_not_adcd order by tjcc,stationid asc";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    
    @Test
    public void testPOIExcelUtilExportDataByList(){
       List<Student> list = new ArrayList<Student>();
       Student s1 = new Student();
       s1.setAge(10);
       s1.setName("xingjian");
       s1.setSarry(100.0);
       Student s2 = new Student();
       s2.setAge(101);
       s2.setName("pengbo");
       s2.setSarry(1070.0);
       list.add(s1);
       list.add(s2);
       String result = POIExcelUtil.ExportDataByList(list,"d:\\ExportDataByList.xls");
    }
    
    @Test
    public void testData_qhm() throws Exception{
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
       String excelPath = "d:\\adcd_wkt.xls";
       String sql = "select name,st_astext(the_geom) wkt from beijingqx_new";
       ResultSet rs = connection.createStatement().executeQuery(sql);
       while(rs.next()){
           String name = rs.getString(1);
           String wkt = rs.getString(2);
           PBFileUtil.WriteStringToTxt(name+"****"+wkt+"\n", "d:\\adcd_wkt.txt");
       }
       //String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    @Test
    public void testExport111(){
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        String excelPath = "d:\\bd_channel_section_dm_ref.xls";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select * from bd_channel_section_dm_ref";
        String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    @Test
    public void testExport222(){
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        String excelPath = "d:\\busline_history.xls";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select id,label,name from (select * from busline_history where batchtime='201603' ) t1 where t1.name not in (select name from busline where isvalid='1') order by name";
        String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    //
    @Test
    public void testData_calcBuslinecount() throws Exception{
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        
        String url1 = "jdbc:postgresql://localhost:5432/basedata";
        String username1 = "basedata";
        String passwd1 = "basedata";
        Connection connection1 = DBConnection.GetPostGresConnection(url1, username1, passwd1);
        
        String sql = "select id from busline_channel_geometry";
        String updateSQL = "update busline_channel_geometry set buslinecount=? where id=?";
        PreparedStatement ps = connection1.prepareStatement(updateSQL);
        ResultSet rs = connection1.createStatement().executeQuery(sql);
        while(rs.next()){
            String channelid = rs.getString(1);
           String query = "select count(*) from busline where id in ( select buslineid from buslinelink where navigationid in ( "+
                   " select navigationid from busline_channel_section_link where sectionid in (select id from busline_channel_section where channelid='"+channelid+"')"+
                   " ) group by buslineid ) and isvalid='1'";
           ResultSet rs1 = connection.createStatement().executeQuery(query);
           rs1.next();
           int count = rs1.getInt(1);
           ps.setInt(1, count);
           ps.setString(2, channelid);
           ps.addBatch();
       }
        ps.executeBatch();
    }
    
    @Test
    public void testArea4Export11(){
        String url = "jdbc:postgresql://localhost:5432/sw_navigation";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String excelPath = "d:\\result_wxh3.xls";
        String result = POIExcelUtil.ExportDataBySQL("select * from result_wxh3", connection, excelPath);
    }
    
    @Test
    public void testReadLZOFileToTxtFile(){
        String lzoPath = "G:\\项目文档\\武汉tocc\\gisdata\\WHTaxiDataPart1\\WHTaxiDataPart1";
        String txtPath = "G:\\项目文档\\武汉tocc\\gisdata\\WHTaxiDataPart1\\gpstxt\\";
        List<String> result = PBFileUtil.FindFilesByEndName(lzoPath,".lzo");
        for(String str:result){
            int index = str.indexOf("d=");
            String str2 = str.substring(index, str.length()-3).replaceAll("=", "").replaceAll("\\\\", "")+"txt";
            String result1  = PBFileUtil.ReadLZOFileToTxtFile(str, txtPath+str2,null,true);
            System.out.println(result1+":"+txtPath+str2);
        }
    }
    
    @Test
    public void testFindFilesByEndName(){
        String lzoPath = "G:\\项目文档\\武汉tocc\\gisdata\\WHTaxiDataPart1\\WHTaxiDataPart1";
        List<String> result = PBFileUtil.FindFilesByEndName(lzoPath,".lzo");
        for(String s:result){
            System.out.println(s);
        }
    }
    
    
    @Test
    public void testExportBaseinfo(){
        String url = "jdbc:postgresql://localhost:5432/park";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select seq, area, oldstatcode, newstatcode, oldlinecode, newlinecode, linename, roadlevel, zhuanghao, zhuanghaovalue, newname, ongitude, latitude, supply, type, typenum, typelevel, keytype, electricity, sunupdate, batterychange, supplyid, cardno, builder, completedate, operationyear, operationun, operationlink, tel, checktype, startplace, startzhuanghao, endzhuanghao, checklong, roadnum, ground, etclevel, roadtype, roadwidth, baseroadwidth, speed, isstop, mx, my, st_astext(geom) as wkt FROM base_info";
       String excelPath = "d:\\baseinfo_wkt.xls";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
}
