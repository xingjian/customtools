package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.StringUtil;

/**  
 * 功能描述: 公交都市项目数据
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月5日 下午4:04:33  
 */
public class BusCityDataTest3 {

    /**
     * 初始化adcd_busline_ref
     * 行政区域和公交线路表
     */
    @Test
    public void testInsertAdcdBuslineRef() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String urllocalhost = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String queryAllRef = "select * from (select t1.*,t2.name from ( select id,adcdcode from navigationline where id in "
                + "(select navigationid from buslinelink where buslineid in (select id from busline where isvalid='1') group by navigationid)" 
                +") t1 left join beijingqx t2 on cast (t1.adcdcode as integer)=t2.code ) t3 where t3.adcdcode is not null";
        //加载所有的和公交相关路链
        Map<String ,String> navigationMap = new HashMap<String,String>();
        Statement statement1 = connection.createStatement();
        ResultSet rs1 = statement1.executeQuery(queryAllRef);
        while(rs1.next()){
            String navigationid = rs1.getString("id");
            String adcdcode = rs1.getString("adcdcode");
            String adcdname = rs1.getString("name");
            navigationMap.put(navigationid, adcdcode+":"+adcdname);
        }
        
        String querysql1 = "select id,label from busline where isvalid='1'";
        Statement statement = connection.createStatement();
        ResultSet rs2 = statement.executeQuery(querysql1);
        String insertAdcdBuslineRef = "insert into adcd_busline_ref (adcd_id,adcd_name,busline_id,busline_name) values (?,?,?,?)";
        PreparedStatement ps1 = connection.prepareStatement(insertAdcdBuslineRef);
        int index = 1;
        while(rs2.next()){
            String buslineid = rs2.getString(1);
            String buslinelabel = rs2.getString(2);
            String querysql2 = "select navigationid from buslinelink where buslineid='"+buslineid+"'";
            Statement statementTemp = connection.createStatement();
            ResultSet rsTemp = statementTemp.executeQuery(querysql2);
            Map<String ,String> mapTemp = new HashMap<String,String>();
            while(rsTemp.next()){
                String navigationidTemp = rsTemp.getString(1);
                if(null!=navigationMap.get(navigationidTemp)){
                    String str1 = navigationMap.get(navigationidTemp);
                    String[] arr1 = str1.split(":");
                    if(null==mapTemp.get(arr1[0])){
                        mapTemp.put(arr1[0],arr1[1]);
                    }
                }
            }
            
            for (Map.Entry<String, String> entry : mapTemp.entrySet()) {
                String str1 = entry.getKey();
                String str2 = entry.getValue();
                ps1.setString(1,str1);
                ps1.setString(2,str2);
                ps1.setString(3,buslineid);
                ps1.setString(4,buslinelabel);
                ps1.addBatch();
            }
            
            System.out.println("处理到----"+index);
            index++;
        }
        ps1.executeBatch();
   }
    
   
    /**
     * 生成公交站点数据保存到busstation_merge
     * 根据name确定唯一站原则 id和busstationid一致
     * 根据最新版本数据生成
     */
    @Test
    public void testbusstation_merge() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql = "select t1.name,t1.id,t1.buslineid,t1.index,st_astext(t2.the_geom) wkt from ("+
                "select * from busstation where buslineid in (select id from busline where isvalid='1')"+
                ") t1 left join busstationlink t2 on t1.id = t2.busstationid order by buslineid ,index";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        Map<String,String> map1 = new HashMap<String,String>();
        Map<String,String> map2 = new HashMap<String,String>();
        while(rs.next()){
             String name = rs.getString(1);
             String busstationid = rs.getString(2);
             String buslineid = rs.getString(3);
             int stationIndex = rs.getInt(4);
             String wkt = rs.getString(5);
             if(null==map1.get(name)){
                 map1.put(name, wkt);
                 map2.put(name, busstationid);
             }
        }
        String insert_sql = "insert into busstation_merge (id,name,the_geom) values(?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insert_sql);
        
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            String name = entry.getKey();
            String wkt = entry.getValue();
            String idTemp = map2.get(name);
            ps.setString(1,idTemp);
            ps.setString(2,name);
            ps.setString(3,wkt);
            ps.addBatch();
        }
        ps.executeBatch();
   }
    
    /**
     * 生成轨道站点数据保存到subwaystation_merge
     * 根据name确定唯一站原则 id和subwaystationid一致
     * 根据最新版本数据生成
     */
    @Test
    public void testsubwaystation_merge() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql = "select name,id,st_astext(the_geom) wkt from subwaystation order by tjcc,index";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        Map<String,String> map1 = new HashMap<String,String>();
        Map<String,String> map2 = new HashMap<String,String>();
        while(rs.next()){
             String name = rs.getString(1);
             String busstationid = rs.getString(2);
             String wkt = rs.getString(3);
             if(null==map1.get(name)){
                 map1.put(name, wkt);
                 map2.put(name, busstationid);
             }
        }
        String insert_sql = "insert into subwaystation_merge (id,name,the_geom) values(?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insert_sql);
        
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            String name = entry.getKey();
            String wkt = entry.getValue();
            String idTemp = map2.get(name);
            ps.setString(1,idTemp);
            ps.setString(2,name);
            ps.setString(3,wkt);
            ps.addBatch();
        }
        ps.executeBatch();
   } 
   
    /**
     * 更新 de_cfg_area stationnum字段
     * @throws Exception
     */
    @Test
   public void testUpdateDeCfgAreaStaionNum() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       //更新武汉数据 行政区划和合并站点数量 
       String sql1_PG = "select adcdname,count(*) cc from busstation_merge group by adcdname";
       String sql1_OR = "update de_cfg_area set stationnum=? where type='5' and name=?";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String adcdname = rs_pg.getString(1);
           int cc = rs_pg.getInt(2);
           ps_oracle.setInt(1, cc);
           ps_oracle.setString(2,adcdname);
           ps_oracle.addBatch();
       }
       ps_oracle.executeBatch();
   }
    
    /**
     * 更新 de_cfg_area buslinenum字段
     * @throws Exception
     */
    @Test
   public void testUpdateDeCfgAreaBusLineNum() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       //更新武汉数据 行政区划和合并站点数量 
       String sql1_PG = "select adcd_name,count(*) cc from adcd_busline_ref group by adcd_name";
       String sql1_OR = "update de_cfg_area set buslinenum=? where type='5' and name=?";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String adcdname = rs_pg.getString(1);
           int cc = rs_pg.getInt(2);
           ps_oracle.setInt(1, cc);
           ps_oracle.setString(2,adcdname);
           ps_oracle.addBatch();
       }
       ps_oracle.executeBatch();
   }
    
    
    /**
     * 更新 de_cfg_area subwaystation字段
     * @throws Exception
     */
    @Test
   public void testUpdateDeCfgAreaSubwaystation() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       //更新武汉数据 行政区划和合并站点数量 
       String sql1_PG = "select adcdname,count(*) cc  from subwaystation_merge group by adcdname";
       String sql1_OR = "update de_cfg_area set subwaystation=? where type='5' and name=?";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String adcdname = rs_pg.getString(1);
           int cc = rs_pg.getInt(2);
           ps_oracle.setInt(1, cc);
           ps_oracle.setString(2,adcdname);
           ps_oracle.addBatch();
       }
       ps_oracle.executeBatch();
   }
    
    /**
     * 更新 de_cfg_area subwayline字段
     * @throws Exception
     */
    @Test
   public void testUpdateDeCfgAreaSubwayLine() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       //更新武汉数据 行政区划和合并站点数量 
       String sql1_PG = "select adcd_name,count(*) cc from adcd_subwayline_ref group by adcd_name";
       String sql1_OR = "update de_cfg_area set subwayline=? where type='5' and name=?";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String adcdname = rs_pg.getString(1);
           int cc = rs_pg.getInt(2);
           ps_oracle.setInt(1, cc);
           ps_oracle.setString(2,adcdname);
           ps_oracle.addBatch();
       }
       ps_oracle.executeBatch();
   }
    
    
    /**
     * 更新 de_cfg_area bikestation字段
     * @throws Exception
     */
    @Test
   public void testUpdateDeCfgAreaBikestation() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       //更新武汉数据 行政区划和合并站点数量 
       String sql1_PG = "select adcd_name,count(*) cc from bikestation group by adcd_name";
       String sql1_OR = "update de_cfg_area set bikestation=? where type='5' and name=?";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String adcdname = rs_pg.getString(1);
           int cc = rs_pg.getInt(2);
           ps_oracle.setInt(1, cc);
           ps_oracle.setString(2,adcdname);
           ps_oracle.addBatch();
       }
       ps_oracle.executeBatch();
   }
    
    /**
     * 更新 de_cfg_dm
     * @throws Exception
     */
    @Test
   public void testUpdateDeCfgDM() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       
       String sql1_PG = "select id,name from busstation_section";
       String sql1_OR = "insert into de_cfg_dm (id,name,startstationname,endstationname) values (?,?,?,?)";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String id = rs_pg.getString(1);
           String name = rs_pg.getString(2);
           String[] arrTemp = name.split("-");
           String s_name = arrTemp[0];
           String e_name = arrTemp[1];
           ps_oracle.setString(1,id);
           ps_oracle.setString(2,name);
           ps_oracle.setString(3,s_name);
           ps_oracle.setString(4,e_name);
           ps_oracle.addBatch();
       }
       ps_oracle.executeBatch();
   }
    
    
    /**
     * 更新 de_cfg_td buslinenum
     * @throws Exception
     */
    @Test
   public void testUpdateDeCfgTDBusLineNum() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       
       String sql1_PG = "select tdname,count(*) cc from td_line_station_ref where type='1' group by tdname";
       String sql1_OR = "update de_cfg_td set buslinenum=? where name=?";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String tdname = rs_pg.getString(1);
           int cc = rs_pg.getInt(2);
           ps_oracle.setInt(1,cc);
           ps_oracle.setString(2,tdname);
           ps_oracle.addBatch();
       }
       ps_oracle.executeBatch();
   } 
    
    /**
     * 更新 de_cfg_td stationnum
     * @throws Exception
     */
    @Test
   public void testUpdateDeCfgTDStationNum() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       
       String sql1_PG = "select tdname,count(*) cc from td_line_station_ref where type='2' group by tdname ";
       String sql1_OR = "update de_cfg_td set stationnum=? where name=?";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String tdname = rs_pg.getString(1);
           int cc = rs_pg.getInt(2);
           ps_oracle.setInt(1,cc);
           ps_oracle.setString(2,tdname);
           ps_oracle.addBatch();
       }
       ps_oracle.executeBatch();
   } 
    
    
    /**
     * 生成 busline_navigation_station_ref
     * @throws Exception
     */
    @Test
   public void testInsertBuslineNavigationStationRef() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata!@#&*(";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        //把需要的导航数据加载到内存中
        Map<String,NavigationObject> navigationMap = new HashMap<String, NavigationObject>();
        String sql_query_nl = "select t1.navigationid,ST_AsText(t2.the_geom) wkt,t2.direction from (select navigationid from buslinelink group by navigationid"+
                ") t1 left join navigationline t2 on t1.navigationid = t2.id";
        Statement statement = connectionPG.createStatement();
        ResultSet rs = statement.executeQuery(sql_query_nl);
        while(rs.next()){
            String navigationid = rs.getString(1);
            String wkt = rs.getString(2);
            String direct = rs.getString(3);
            NavigationObject no = new NavigationObject(navigationid, direct, wkt);
            navigationMap.put(navigationid, no);
        }
        String insertSQL = "INSERT INTO busline_navigation_station_ref(id, buslineid, buslinename, navigationid, dmname, the_geom)"+
                            "VALUES (?, ?, ?, ?, ?, ST_GeomFromText(?,4326))";
       String sql1_PG = "select buslineid,buslinename,name,linkids from station_dm_singlebus";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_pg = connectionPG.prepareStatement(insertSQL);
       int commitIndex = 1;
       while(rs_pg.next()){
           String buslineid = rs_pg.getString(1);
           String buslinename = rs_pg.getString(2);
           String name = rs_pg.getString(3);
           String linkids = rs_pg.getString(4);
           if(null!=linkids){
               String[] arrLinekids = linkids.split(",");
               if(null!=arrLinekids&&arrLinekids.length>0){
                   for(String s1 : arrLinekids){
                       ps_pg.setString(1, StringUtil.GetUUIDString());
                       ps_pg.setString(2, buslineid);
                       ps_pg.setString(3, buslinename);
                       ps_pg.setString(4, s1);
                       ps_pg.setString(5, name);
                       ps_pg.setString(6,navigationMap.get(s1).wkt);
                       ps_pg.addBatch();
                       if(commitIndex%5000==0){
                           ps_pg.executeBatch();
                           System.out.println("commitIndex===="+commitIndex);
                       }
                       commitIndex++;
                   }
               }
           }
       }
       ps_pg.executeBatch();
       System.out.println("commitIndex===="+commitIndex);
   }  
    
    /**
     * 修改公交线路一卡通上下行ykt_sxx
     * 小到大是1 上行  大到小是0  下行 
     */
    @Test
    public void testUpdateBusLineSXX() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata!@#&*(";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        //--符合计算一卡通数据的公交线路
        String sqlQuery1 = "select id,label,linecode,starttime,endtime from busline where linecode is not null and linecode !=0 and starttime is not null and endtime is not null order by linecode";
        //--根据下面查询结果设置公交线路一卡通的ykt_sxx
        String sqlQuery2 = "select buslineid,label,name,index,stationid from busstation where index=1 and stationid is not null order by label";
        Statement sta_PG = connectionPG.createStatement();
        ResultSet rs_pg = sta_PG.executeQuery(sqlQuery2);
        String updateSQL = "update busline set ykt_sxx=? where id=?";
        PreparedStatement ps_pg = connectionPG.prepareStatement(updateSQL);
        while(rs_pg.next()){
            String buslineid = rs_pg.getString(1);
            int index = rs_pg.getInt(4);
            int stationid = rs_pg.getInt(5);
            if(index==1){
                if(index==stationid){
                    ps_pg.setInt(1, 1);
                    ps_pg.setString(2, buslineid);
                }else{
                    ps_pg.setInt(1, 0);
                    ps_pg.setString(2, buslineid);
                }
                ps_pg.addBatch();
            }
        }
        ps_pg.executeBatch();
    }
    
    
    /**
     * 更新 td_line_station_ref
     * @throws Exception
     */
    @Test
   public void testUpdateTDLineStationRef() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/buscity";
        String usernamePG = "buscity";
        String passwdPG = "bs789&*(";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        Statement sta_OR = connectionOracle.createStatement();
        String sql1_OR = "select id,tdname,ref_id,type from td_line_station_ref where type = '1'";
        String update1_OR = "update td_line_station_ref set ref_id = ? where id=?";
        ResultSet rs_OR = sta_OR.executeQuery(sql1_OR);
        PreparedStatement ps_oracle = connectionOracle.prepareStatement(update1_OR);
        String sql2_PG = "select id,buslineid from buslinelink_merge";
        ResultSet rs2_PG = connectionPG.createStatement().executeQuery(sql2_PG);
        Map<String,String> map1 = new HashMap<String, String>();
        while(rs2_PG.next()){
            String str_id = rs2_PG.getString(1);
            String str_buslineid = rs2_PG.getString(2);
            map1.put(str_id, str_buslineid);
        }
        while(rs_OR.next()){
            String idd = rs_OR.getString(1);
            String ref_id = rs_OR.getString(3);
            ps_oracle.setString(1, map1.get(ref_id));
            ps_oracle.setString(2, idd);
            ps_oracle.addBatch();
        }
        ps_oracle.executeBatch();
   } 
    
}