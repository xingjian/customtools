package com.promise.gistool;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISDBUtil;
import com.vividsolutions.jts.geom.MultiLineString;

/**  
 * 功能描述: 处理高德数据程序
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年8月19日 上午10:04:16  
 */
public class GDRoadDataTest {

    @Test
    public void updateSpeedByTxt() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        String updateSQL = "update gd_road_status set speed=? where \"ROADID\"=?";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        PreparedStatement psUpdate = connectionPG.prepareStatement(updateSQL);
        List<String> list = PBFileUtil.ReadFileByLine("D:\\gddata\\201611151740");
        int count = 0;
        for(String str : list){
            String[] arr = str.split(",");
            if(null==arr||arr.length!=4){
                continue;
            }else{
                String roadid = arr[1];
                double speed  = Double.parseDouble(arr[3]);
                psUpdate.setDouble(1, speed);
                psUpdate.setString(2, roadid);
                psUpdate.addBatch();
                count++;
                System.out.println("count value "+ count);
                if(count%10000==0){
                    psUpdate.executeBatch();
                    System.out.println("commit count value "+ count);
                }
            }
        }
        psUpdate.executeBatch();
        
    }
    
    
    
    @Test
    public void chekcGDSpeedByTxt() throws Exception{
        File files = new File("D:\\dist_road_20161014");
        //5940812039571156280
        String searchRoadID = "5958878115127126228";
        for(File f:files.listFiles()){
            List<String> list = PBFileUtil.ReadFileByLine("D:\\dist_road_20161014\\"+f.getName());
            for(String str : list){
                String[] arr = str.split(",");
                if(null==arr||arr.length!=4){
                    continue;
                }else{
                    String roadid = arr[1];
                    if(roadid.trim().equals(searchRoadID)){
                        double speed  = Double.parseDouble(arr[3]);
                        System.out.println(speed);
                        break;
                    }
                    
                }
            }
        }
    }
    
    
    @Test
    public void exportExcelGDDateByName() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        String querySQL = "select \"ROADNAME\" from gd_road_status where \"ROADCLASS\"=41000 group by \"ROADNAME\"";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        ResultSet rs = connectionPG.createStatement().executeQuery(querySQL);
        Map<String,String> mapGD = new HashMap<String,String>();
        while(rs.next()){
            mapGD.put(rs.getString(1), "1");
        }
        rs.close();
        for (Map.Entry<String, String> entry : mapGD.entrySet()) {
            System.out.println(entry.getKey() + "--->" + entry.getValue());
            String sqlQuerySub = "select roadid,roadname,timestr,state,speed from gd_road_status_20161007 where roadname='"+entry.getKey()+"' order by timestr";
            //PBFileUtil.ExportDataBySQL(sqlQuerySub, connectionPG, "d:\\lijingexcels\\"+entry.getKey()+".csv",",",true);
            POIExcelUtil.ExportDataBySQLNew(sqlQuerySub, connectionPG, "d:\\lijingexcels\\"+entry.getKey()+".xlsx");
        }
    }
    
    
    @Test
    public void importGDSpeedByTxt() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        //高速路链查询
        String querySQL = "select \"ROADID\" from gd_beijing where \"ROADCLASS\" = 41000";
        String insertSQL = "insert into gd_road_status_20161122 (id,roadid,roadname,timestr,state,speed) values (?,?,?,?,?,?) ";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        Map<String,String> mapGD = new HashMap<String,String>();
        ResultSet rs = connectionPG.createStatement().executeQuery(querySQL);
        while(rs.next()){
            mapGD.put(rs.getString(1), "1");
        }
        rs.close();
        
        PreparedStatement psUpdate = connectionPG.prepareStatement(insertSQL);
        File files = new File("D:\\dist_road_20161122");
        int index = 0;
        int count = 0;
        for(File f:files.listFiles()){
            String fileNameStr = f.getName();
            List<String> list = PBFileUtil.ReadFileByLine("D:\\dist_road_20161122\\"+fileNameStr);
            for(String str : list){
                String[] arr = str.split(",");
                if(null==arr||arr.length!=4){
                    continue;
                }else{
                    String roadid = arr[1];
                    if(null!=mapGD.get(roadid.trim())){
                        psUpdate.setString(1,StringUtil.GetUUIDString());
                        psUpdate.setString(2, roadid);
                        psUpdate.setString(3, arr[0]);
                        psUpdate.setString(4, fileNameStr);
                        psUpdate.setString(5, arr[2]);
                        psUpdate.setInt(6, Integer.parseInt(arr[3]));
                        psUpdate.addBatch();
                        index++;
                        if(index%10000==0){
                            psUpdate.executeBatch(); 
                            index = 0;
                            count++;
                            System.out.println("count==="+count);
                        }
                    }
                    
                }
            }
        }
        psUpdate.executeBatch(); 
    }
    
    
    @Test
    public void importGDSpeedByPBFile() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        //高速路链查询
        String querySQL = "select \"ROADID\" from gd_beijing where \"ROADCLASS\" = 41000";
        String insertSQL = "insert into gd_road_status_20161007 (id,roadid,roadname,timestr,state,speed) values (?,?,?,?,?,?) ";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        Map<String,String> mapGD = new HashMap<String,String>();
        ResultSet rs = connectionPG.createStatement().executeQuery(querySQL);
        while(rs.next()){
            mapGD.put(rs.getString(1), "1");
        }
        rs.close();
        
        PreparedStatement psUpdate = connectionPG.prepareStatement(insertSQL);
        File files = new File("D:\\高德路况\\dist_road_20161007\\root\\dist\\road\\2016\\DR20161007\\");
        int index = 0;
        int count = 0;
        for(File f:files.listFiles()){
            String fileNameStr = f.getName();
            List<String> list = PBFileUtil.ReadFileByLine("D:\\高德路况\\dist_road_20161007\\root\\dist\\road\\2016\\DR20161007\\"+fileNameStr);
            for(String str : list){
                String[] arr = str.split(",");
                if(null==arr||arr.length!=4){
                    continue;
                }else{
                    String roadid = arr[1];
                    if(null!=mapGD.get(roadid.trim())){
                        psUpdate.setString(1,StringUtil.GetUUIDString());
                        psUpdate.setString(2, roadid);
                        psUpdate.setString(3, arr[0]);
                        psUpdate.setString(4, fileNameStr);
                        psUpdate.setString(5, arr[2]);
                        psUpdate.setInt(6, Integer.parseInt(arr[3]));
                        psUpdate.addBatch();
                        index++;
                        if(index%10000==0){
                            psUpdate.executeBatch(); 
                            index = 0;
                            count++;
                            System.out.println("count==="+count);
                        }
                    }
                    
                }
            }
        }
        psUpdate.executeBatch(); 
    }
    
    
    
    @Test
    public void exportExcelGDDateByRoadID() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        String querySQL = "select \"ROADID\" from gd_beijing_gs where \"ROADNAME\"='京承高速'";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        ResultSet rs = connectionPG.createStatement().executeQuery(querySQL);
        String ids = "";
        while(rs.next()){
            String id = rs.getString(1);
            ids = ids+"'"+id+"'"+",";
        }
        ids = ids.substring(0, ids.length()-1);
        System.out.println(ids);
        rs.close();
        String sqlQuerySub = "select roadid,roadname,timestr,state,speed from gd_road_status_20161007 where roadid in ("+ids+") order by timestr";
        System.out.println(sqlQuerySub);
        POIExcelUtil.ExportDataBySQLNew(sqlQuerySub, connectionPG, "d:\\lijingexcels\\京承高速1007.xlsx");
    }
    
    @Test
    public void createTablesGDdata() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";//ttyj用户名
        String passwdPG = "postgis";
        String dir = "H:\\高德数据";
        File path = new File(dir);
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        Statement statement = connectionPG.createStatement();
        for(File f:path.listFiles()){
            String name = f.getName();
            if(name.endsWith(".zip")){
                String tableName = name.substring(0,name.lastIndexOf("."));
                String sql = "create table "+tableName+" as select * from gd_road_status_20160915 where 1=2";
                statement.addBatch(sql);
            }
        }
        statement.executeBatch();
        statement.close();
        connectionPG.close();
    }
    
    
    /**
     * 批量插入数据
     * @throws Exception
     */
    @Test
    public void importGDSpeedByPBFiles() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        String dir = "H:\\高德数据";
        File path = new File(dir);
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String querySQL = "select \"ROADID\" from gd_beijing where \"ROADCLASS\" = 41000";
        Map<String,String> mapGD = new HashMap<String,String>();
        ResultSet rs = connectionPG.createStatement().executeQuery(querySQL);
        while(rs.next()){
            mapGD.put(rs.getString(1), "1");
        }
        rs.close();
        for(File f:path.listFiles()){
            String name = f.getName();
            if(name.endsWith(".zip")){
                String tableName = name.substring(0,name.lastIndexOf("."));
                String insertSQL = "insert into "+tableName+" (id,roadid,roadname,timestr,state,speed) values (?,?,?,?,?,?) ";
                PreparedStatement psUpdate = connectionPG.prepareStatement(insertSQL);
                File files = new File("H:\\高德数据\\gd1101-1221\\"+tableName+"\\root\\dist\\road\\2016\\DR2016"+tableName.substring(tableName.length()-4, tableName.length())+"\\");
                int index = 0;
                int count = 0;
                String stime = StringUtil.GetCurrentDateString();
                for(File f1:files.listFiles()){
                    String fileNameStr = f1.getName();
                    List<String> list = PBFileUtil.ReadFileByLine("H:\\高德数据\\gd1101-1221\\"+tableName+"\\root\\dist\\road\\2016\\DR2016"+tableName.substring(tableName.length()-4, tableName.length())+"\\"+fileNameStr);
                    for(String str : list){
                        String[] arr = str.split(",");
                        if(null==arr||arr.length!=4){
                            continue;
                        }else{
                            String roadid = arr[1];
                            if(null!=mapGD.get(roadid.trim())){
                                psUpdate.setString(1,StringUtil.GetUUIDString());
                                psUpdate.setString(2, roadid);
                                psUpdate.setString(3, arr[0]);
                                psUpdate.setString(4, fileNameStr);
                                psUpdate.setString(5, arr[2]);
                                psUpdate.setInt(6, Integer.parseInt(arr[3]));
                                psUpdate.addBatch();
                                index++;
                                if(index%15000==0){
                                    psUpdate.executeBatch(); 
                                    index = 0;
                                    count++;
                                    System.out.println("count==="+count);
                                }
                            }
                            
                        }
                    }
                }
                psUpdate.executeBatch(); 
                psUpdate.close();
                System.out.println(tableName+":"+stime+"----"+StringUtil.GetCurrentDateString());
            }
        }
        
        
        
    }
    /**
     * 批量导出
     * @throws Exception
     */
    @Test
    public void exportExcelGDDateByNameMany() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        String querySQL = "select \"ROADNAME\" from lijinggsname";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        ResultSet rs = connectionPG.createStatement().executeQuery(querySQL);
        Map<String,String> mapGD = new HashMap<String,String>();
        String tablenames = "dist_road_20161101, dist_road_20161102, dist_road_20161103, dist_road_20161104, dist_road_20161105, dist_road_20161106, dist_road_20161107, dist_road_20161108, dist_road_20161109, dist_road_20161110, dist_road_20161111, dist_road_20161112, dist_road_20161113, dist_road_20161114, dist_road_20161115, dist_road_20161116, dist_road_20161117, dist_road_20161118, dist_road_20161119, dist_road_20161120, dist_road_20161121, dist_road_20161122, dist_road_20161123, dist_road_20161124, dist_road_20161125, dist_road_20161126, dist_road_20161127, dist_road_20161128, dist_road_20161129, dist_road_20161130, dist_road_20161201, dist_road_20161202, dist_road_20161203, dist_road_20161204, dist_road_20161205, dist_road_20161206, dist_road_20161207, dist_road_20161208, dist_road_20161209, dist_road_20161210, dist_road_20161211, dist_road_20161212, dist_road_20161213, dist_road_20161214, dist_road_20161215, dist_road_20161216, dist_road_20161217, dist_road_20161218, dist_road_20161219, dist_road_20161220, dist_road_20161221, dist_road_20161222, dist_road_20161223, dist_road_20161224";
        while(rs.next()){
            mapGD.put(rs.getString(1), "1");
        }
        rs.close();
        String[] arr = tablenames.split(",");
        for(String str:arr){
            for (Map.Entry<String, String> entry : mapGD.entrySet()) {
                String sqlQuerySub = "select roadid,roadname,timestr,state,speed from "+str+" where roadname='"+entry.getKey()+"' order by timestr";
                File dir = new File("d:\\lijingexcels\\"+str.split("_")[2]);
                dir.mkdirs();
                POIExcelUtil.ExportDataBySQLNew(sqlQuerySub, connectionPG, "d:\\lijingexcels\\"+str.split("_")[2]+"\\"+entry.getKey()+".xlsx");
            }
        }
        
    }
    
    
    @Test
    public void testShapeToPostGISGDGS(){
        String shapePath = "G:\\项目文档\\国省干线\\数据\\gd_gs_road_google_all_sxx.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("localhost", "5432", "sw_navigation", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "gd_gs_road_google_all_sxx", MultiLineString.class, "EPSG:4326");
        System.out.println(result);
    }
    
    
    /**
     * 批量导出
     * @throws Exception
     */
    @Test
    public void exportExcelGDGS() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        String querySQL = "select \"ROADID\" as roadid,gsname as roadname,\"SXX\" as sxx,\"LENGTH\" as length from gd_gs_road_google_all_sxx where \"SXX\" in('1','2') order by gsname,\"SXX\"";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        POIExcelUtil.ExportDataBySQLNew(querySQL, connectionPG, "d:\\gd_gs.xlsx");
        
    }
    
    @Test
    public void testShapeToPostGISGDGS1(){
        String shapePath = "D:\\高德_京通_京银路\\高德_京通快速路.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("localhost", "5432", "sw_navigation", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "jtkslT11111111", MultiLineString.class, "EPSG:3857");
        System.out.println(result);
    }
    
    @Test
    public void exportExcelGDGS111() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        String querySQL = "select \"ROADID\" as roadid,'京通快速路' as roadname,\"SXX\" as sxx,\"LENGTH\" as length from jtksl where \"SXX\" in('1','2') order by \"SXX\"";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        POIExcelUtil.ExportDataBySQLNew(querySQL, connectionPG, "d:\\jtksl.xlsx");
        
    }
    
}
