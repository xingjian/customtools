package com.promise.gistool;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.StringUtil;

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
    
}
