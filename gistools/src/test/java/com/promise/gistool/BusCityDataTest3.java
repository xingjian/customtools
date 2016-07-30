package com.promise.gistool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBMathUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.tongtu.nomap.core.transform.Gis84ToCehui;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;

/**  
 * 功能描述: 公交都市项目数据
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月5日 下午4:04:33  
 */
public class BusCityDataTest3 {

    /**
     * 初始化adcd_busline_ref
     * 先清空在插入
     * 行政区域和公交线路表
     * 根据公交线路的所在的导航路链id来判断，因为导航路链本身有所属行政区划的信息
     */
    @Test
    public void testInsertAdcdBuslineRef() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
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
        Statement statementDel = connection.createStatement();
        statementDel.execute("delete from adcd_busline_ref");
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
        Statement statementUpdate = connection.createStatement();
        statementUpdate.execute("update adcd_busline_ref set adcd_id=t2.id from beijingqx t2 where adcd_name=t2.name");
   }
    
    
    /**
     * 根据最近的100条通道，合并成20条通道。根据通道名称进行合并，验证关系也参照这个
     * @throws Exception
     */
    @Test
    public void testMergeBusLineChannelToMergeBusLineChanne_LXMC() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql = "select lxmc,string_agg(id,',') from busline_channel group by lxmc";
        String insertSQL = "insert into MergeBusLineChanne_LXMC (name,the_geom,id) values (?,st_geometryfromtext(?,4326),?)";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        Statement statementDel = connection.createStatement();
        statementDel.execute("delete from MergeBusLineChanne_LXMC");
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        int index =0;
        while(rs.next()){
            String channelids = rs.getString(2);
            String[] channelidArr = channelids.split(",");
            String channelids_temp = "";
            for(String str1 : channelidArr){
                channelids_temp = channelids_temp+"'"+str1+"',";
            }
            channelids_temp = channelids_temp+"''";
            String channelName = rs.getString(1);
            String sqlTemp = "select channelid,st_astext(the_geom) from busline_channel_link where channelid in ("+channelids_temp+")";
            Statement statement2 = connection.createStatement();
            ResultSet rs2 = statement2.executeQuery(sqlTemp);
            List<Geometry> listGeo = new ArrayList<Geometry>();
            while(rs2.next()){
                String channelid = rs2.getString(1);
                String wkt = rs2.getString(2); 
                listGeo.add(GeoToolsGeometry.createGeometrtyByWKT(wkt));
            }
            Geometry unionGeo = null;
            if(null!=listGeo.get(0)){
              unionGeo = listGeo.get(0);
              for(int i=1;i<listGeo.size();i++){
                unionGeo = unionGeo.union(listGeo.get(i));
              }
            }
            ps.setString(1, channelName);
            ps.setString(2, unionGeo.toText());
            ps.setString(3, StringUtil.GetUUIDString());
            ps.addBatch();
            index++;
            System.out.println("merge index "+index+"---"+channelName);
        }
        ps.executeBatch();
    }
    
    /**
     * 根据最近的100条通道，合并成40条通道。根据通道名称方向进行合并，验证关系也参照这个
     * @throws Exception
     */
    @Test
    public void testMergeBusLineChannelToMergeBusLineChanne_LXMCArrow() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql = "select lxmc,string_agg(id,','),arrow from busline_channel group by lxmc,arrow";
        String insertSQL = "insert into MergeBusLineChanne_LXMC_Arrow (name,the_geom,id,arrow) values (?,st_geometryfromtext(?,4326),?,?)";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        Statement statementDel = connection.createStatement();
        statementDel.execute("delete from MergeBusLineChanne_LXMC_Arrow");
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        int index =0;
        while(rs.next()){
            String channelids = rs.getString(2);
            String[] channelidArr = channelids.split(",");
            String channelids_temp = "";
            for(String str1 : channelidArr){
                channelids_temp = channelids_temp+"'"+str1+"',";
            }
            channelids_temp = channelids_temp+"''";
            String channelName = rs.getString(1);
            String arrow = rs.getString(3);
            String sqlTemp = "select channelid,st_astext(the_geom) from busline_channel_link where channelid in ("+channelids_temp+")";
            Statement statement2 = connection.createStatement();
            ResultSet rs2 = statement2.executeQuery(sqlTemp);
            List<Geometry> listGeo = new ArrayList<Geometry>();
            while(rs2.next()){
                String channelid = rs2.getString(1);
                String wkt = rs2.getString(2); 
                listGeo.add(GeoToolsGeometry.createGeometrtyByWKT(wkt));
            }
            Geometry unionGeo = null;
            System.out.println(sqlTemp);
            if(null!=listGeo.get(0)){
              unionGeo = listGeo.get(0);
              for(int i=1;i<listGeo.size();i++){
                unionGeo = unionGeo.union(listGeo.get(i));
              }
            }
            ps.setString(1, channelName);
            ps.setString(2, unionGeo.toText());
            ps.setString(3, StringUtil.GetUUIDString());
            ps.setString(4,arrow);
            ps.addBatch();
            index++;
            System.out.println("merge index "+index+"---"+channelName);
        }
        ps.executeBatch();
    }
    
    
    /**
     * 修改busline_channel_link，channelname,subchannelname,labelname
     * @throws Exception
     */
    @Test
    public void testUpdateBuslineChannelLinkNames() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql = "select id,lxmc,name from busline_channel";
        String sql1 = "select id,channelid from busline_channel_link";
        String sql2 = "update busline_channel_link set channelname=?,subchannelname=? where id=?";
        Map<String,String> map1 = new HashMap<String, String>();
        Statement st1 = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql2);
        ResultSet rs1 = st1.executeQuery(sql);
        while(rs1.next()){
            String rs1_id = rs1.getString(1);
            String rs1_lxmc = rs1.getString(2);
            String rs1_name = rs1.getString(3);
            map1.put(rs1_id, rs1_lxmc+":"+rs1_name);
        }
        Statement st2 = connection.createStatement();
        ResultSet rs2 = st2.executeQuery(sql1);
        while(rs2.next()){
            String idTemp = rs2.getString(1);
            String channelid = rs2.getString(2);
            String namesTemp = map1.get(channelid);
            String[] arr1 = namesTemp.split(":");
            String name1 = arr1[0];
            String name2 = arr1[1];
            ps.setString(1, name1);
            ps.setString(2, name2);
            ps.setString(3, idTemp);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    /**
     * 修改busline_channel_link labelname
     * @throws Exception
     */
    @Test
    public void testUpdateBuslineChannelLinkLabelName() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql1 = "select id,channelname from busline_channel_link";
        String sql2 = "update busline_channel_link set labelname=? where id=?";
        Map<String,String> map1 = new HashMap<String, String>();
        Statement st1 = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql2);
        ResultSet rs1 = st1.executeQuery(sql1);
        while(rs1.next()){
            String idTemp = rs1.getString(1);
            String channelnameTemp = rs1.getString(2);
            if(null==map1.get(channelnameTemp)){
                map1.put(channelnameTemp, idTemp);
            }
            
        }
        int index = 0;
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            index++;
            String key = entry.getKey();
            String values = entry.getValue();
            ps.setString(1, key);
            ps.setString(2, values);
            ps.addBatch();
            System.out.println("index====="+index+"====="+values);
        }
        ps.executeBatch();
    }
    
    
    /**
     * 根据最近的100条通道，按照channelid合并空间对象。
     * @throws Exception
     */
    @Test
    public void testMergeToMergeBusLineChanne_ChannelID() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql = "select channelid,string_agg(st_astext(the_geom),'#') from busline_channel_link group by channelid";
        String insertSQL = "insert into MergeBusLineChanne_ChannelID (channelid,the_geom) values (?,st_geometryfromtext(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        Statement statementDel = connection.createStatement();
        statementDel.execute("delete from MergeBusLineChanne_ChannelID");
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        int index = 0;
        while(rs.next()){
            String channelid = rs.getString(1);
            index++;
            System.out.println("merge index "+ index +"---"+channelid);
            String[] wktArr = rs.getString(2).split("#");
            List<Geometry> listGeo = new ArrayList<Geometry>();
            for(String wltStr : wktArr){
                listGeo.add(GeoToolsGeometry.createGeometrtyByWKT(wltStr));
            }
            Geometry unionGeo = null;
            if(null!=listGeo.get(0)){
              unionGeo = listGeo.get(0);
              for(int i=1;i<listGeo.size();i++){
                unionGeo = unionGeo.union(listGeo.get(i));
              }
            }
            ps.setString(1, channelid);
            ps.setString(2, unionGeo.toText());
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    /**
     * 生成行政区划和通道关系表 adcd_channel_ref 通道采用MergeBusLineChanne_LXMC(有测试用例可以跑)
     * 程序会先情况表，在插入数据，执行之前如果数据重要的话，要先备份
     */
    @Test
    public void testInsertAdcdChannelRef() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        Statement statement = connection.createStatement();
        statement.addBatch("delete from adcd_channel_ref");
        statement.addBatch("insert into adcd_channel_ref select a.id,a.name,b.channelid,b.name from beijingqx a,(select channelid,the_geom,t2.name from MergeBusLineChanne_ChannelID left join busline_channel t2 on channelid = t2.id) b where  1=1 and (ST_Contains(a.the_geom,b.the_geom) or ST_Intersects(a.the_geom,b.the_geom))");
        statement.executeBatch();
    }
    
    /**
     * 处理100通道和公交线路的关系,都是当前有效的公交线路
     * 通道不分上下行，公交线路是区分上下行的
     * 根据最新的通道生成的，什么时候运行都有效，因为没有借助中间表
     */
    @Test
    public void testQBLineIntersectsTD(){
        try{
            String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
            String username = "basedata";
            String passwd = "basedata";
            Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
            //查询通道名称---所关联的路链id
            String queryTD = "select channelid,string_agg(navigationid,'#') naviids from busline_channel_link group by channelid";
            //查询公交线路id---关联的路链id
            String queryBusLine = "select buslineid,string_agg(navigationid,'#') from ("+
                            "select buslineid,navigationid from buslinelink where buslineid in (select id from busline where isvalid='1')"+
                            ") t1 group by buslineid";
            //先删除关系表中的td_line_station_ref type=1的公交线路关系
            Statement statementDel = connection.createStatement();
            statementDel.execute("delete from td_line_station_ref where type='1'");
            String insertSQL = "insert into td_line_station_ref (id,channelid,ref_id,type) values(?,?,?,?)";
            PreparedStatement psInsert = connection.prepareStatement(insertSQL);
            ResultSet rsBusLine = connection.createStatement().executeQuery(queryBusLine);
            List<String> listBS = new ArrayList<String>();
            while(rsBusLine.next()){
                String buslineid = rsBusLine.getString(1);
                String bsNaviIDS = rsBusLine.getString(2);
                listBS.add(buslineid+"@"+bsNaviIDS);
            }
            
            ResultSet rsTD = connection.createStatement().executeQuery(queryTD);
            int index =1;
            while(rsTD.next()){
                String channelid = rsTD.getString(1);
                String tdNaviIDS = rsTD.getString(2);
                for(String strT:listBS){
                    String[] strArr_1 = strT.split("@");
                    String buslineid = strArr_1[0];
                    String bsNaviIDS1 = strArr_1[1];
                    String[] navigationArr_BusLine = bsNaviIDS1.split("#");
                    boolean boo = false;
                    for(String subNavid:navigationArr_BusLine){
                        if(tdNaviIDS.indexOf(subNavid)!=-1){
                            boo = true;
                            break;
                        }
                    }
                    if(boo){
                        psInsert.setString(1, StringUtil.GetUUIDString());
                        psInsert.setString(2, channelid);
                        psInsert.setString(3, buslineid);
                        psInsert.setString(4, "1");
                        psInsert.addBatch();
                    }
                }
                System.out.println(channelid+"---"+index +" successful.");
                index++;
                psInsert.executeBatch();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
       
    }
    
    
    /**
     * 处理100通道和站点的关系,都是当前有效的公交线路
     * 通道不分上下行，公交站点(不分上下行)使用的busstation_merge，数不属于通道根据busstation_merge navigationids
     * 根据最新的通道生成的，什么时候运行都有效，因为没有借助中间表
     */
    @Test
    public void testQBStationIntersectsTD() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        //查询通道名称---所关联的路链id
        String queryTD = "select channelid,string_agg(navigationid,'#') naviids from busline_channel_link group by channelid";
        //查询busstation_merge_id---关联的路链id
        String queryStation = "select id,navigationids from busstation_merge";
        //先删除关系表中的td_line_station_ref type=2的公交站点
        Statement statementDel = connection.createStatement();
        statementDel.execute("delete from td_line_station_ref where type='2'");
        String insertSQL = "insert into td_line_station_ref (id,channelid,ref_id,type) values(?,?,?,?)";
        PreparedStatement psInsert = connection.prepareStatement(insertSQL);
        ResultSet rsBusStation = connection.createStatement().executeQuery(queryStation);
        List<String> listBS = new ArrayList<String>();
        while(rsBusStation.next()){
            String busstationid = rsBusStation.getString(1);
            String bsNaviIDS = rsBusStation.getString(2);
            listBS.add(busstationid+"@"+bsNaviIDS);
        }
        
        ResultSet rsTD = connection.createStatement().executeQuery(queryTD);
        int index =1;
        while(rsTD.next()){
            String channelid = rsTD.getString(1);
            String tdNaviIDS = rsTD.getString(2);
            for(String strT:listBS){
                String[] strArr_1 = strT.split("@");
                String busstationid = strArr_1[0];
                String bsNaviIDS1 = strArr_1[1];
                String[] navigationArr_BusStation = bsNaviIDS1.split("#");
                boolean boo = false;
                for(String subNavid:navigationArr_BusStation){
                    if(tdNaviIDS.indexOf(subNavid)!=-1){
                        boo = true;
                        break;
                    }
                }
                if(boo){
                    psInsert.setString(1, StringUtil.GetUUIDString());
                    psInsert.setString(2, channelid);
                    psInsert.setString(3, busstationid);
                    psInsert.setString(4, "2");
                    psInsert.addBatch();
                }
            }
            System.out.println(channelid+"---"+index +" successful.");
            index++;
            psInsert.executeBatch();
        }
       
    }
    
    
    /**
     * 程序会自动清空busstation_merge表，id为原始busstationid
     * 生成公交站点数据保存到busstation_merge
     * 根据name确定唯一站原则 id和busstationid一致
     * 根据最新版本数据生成 生成对应的navigationids adcd_id adcd_name
     */
    @Test
    public void testbusstation_merge() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql = "select t1.name,t1.id,t1.buslineid,t1.index,st_astext(t2.the_geom) wkt,t2.linkid from ("+
                "select * from busstation where buslineid in (select id from busline where isvalid='1')"+
                ") t1 left join busstationlink t2 on t1.id = t2.busstationid order by buslineid ,index";
        Statement statement = connection.createStatement();
        Statement statementDel = connection.createStatement();
        statementDel.execute("delete from busstation_merge");
        ResultSet rs = statement.executeQuery(sql);
        Map<String,String> map1 = new HashMap<String,String>();
        Map<String,String> map2 = new HashMap<String,String>();
        Map<String,String> map3 = new HashMap<String,String>();
        while(rs.next()){
             String name = rs.getString(1);
             String busstationid = rs.getString(2);
             String buslineid = rs.getString(3);
             int stationIndex = rs.getInt(4);
             String wkt = rs.getString(5);
             String linkid = rs.getString(6);
             if(null==map1.get(name)){
                 map1.put(name, wkt);
                 map2.put(name, busstationid);
                 map3.put(name, linkid);
             }else{
                 if(map3.get(name).indexOf(linkid)==-1){
                     map3.put(name, map3.get(name)+"#"+linkid);
                 }
             }
        }
        String insert_sql = "insert into busstation_merge (id,name,the_geom,navigationids) values(?,?,ST_GeomFromText(?,4326),?)";
        PreparedStatement ps = connection.prepareStatement(insert_sql);
        int index = 1;
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            String name = entry.getKey();
            String wkt = entry.getValue();
            String idTemp = map2.get(name);
            String linkids = map3.get(name);
            ps.setString(1,idTemp);
            ps.setString(2,name);
            ps.setString(3,wkt);
            ps.setString(4,linkids);
            ps.addBatch();
            System.out.println("index---"+index+"---"+name);
            index++;
        }
        ps.executeBatch();
        System.out.println("合并站点数据生成完毕。开始处理和行政区划的关系，写入adcd_id,adcdname......");
        Statement statementUpdate = connection.createStatement();
        String sql_1 = "update busstation_merge t1 set adcdname=t2.name from (select a.id,a.name,b.id,b.name stname from beijingqx a,busstation_merge b where  1=1 and (ST_Contains(a.the_geom,b.the_geom) or ST_Intersects(a.the_geom,b.the_geom))) t2 where t1.name=t2.stname";
        String sql_2 = "update busstation_merge set adcd_id=t2.id from beijingqx t2 where adcdname=t2.name";
        statementUpdate.addBatch(sql_1);
        statementUpdate.addBatch(sql_2);
        statementUpdate.executeBatch();
        System.out.println("busstation_merge 数据生成完毕。");
    }
    
    /**
     * 生成轨道站点数据保存到subwaystation_merge 先清除subwaystation_merge
     * 根据name确定唯一站原则 id和subwaystationid一致
     * 根据最新版本数据生成 
     * 自动生成adcd_id,adcdname
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
        Statement statementDel = connection.createStatement();
        statementDel.execute("delete from subwaystation_merge");
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
        int index = 1;
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            String name = entry.getKey();
            String wkt = entry.getValue();
            String idTemp = map2.get(name);
            ps.setString(1,idTemp);
            ps.setString(2,name);
            ps.setString(3,wkt);
            ps.addBatch();
            System.out.println("index---"+index+"---"+name);
        }
        ps.executeBatch();
        System.out.println("地铁站点合并完成。准备进行设置行政区划id和名称......");
        
        Statement statementUpdate = connection.createStatement();
        String sql_1 = "update subwaystation_merge t1 set adcdname=t2.name from (select a.id,a.name,b.id,b.name stname from beijingqx a,subwaystation_merge b where  1=1 and (ST_Contains(a.the_geom,b.the_geom) or ST_Intersects(a.the_geom,b.the_geom))) t2 where t1.name=t2.stname";
        String sql_2 = "update subwaystation_merge set adcd_id=t2.id from beijingqx t2 where adcdname=t2.name";
        statementUpdate.addBatch(sql_1);
        statementUpdate.addBatch(sql_2);
        statementUpdate.executeBatch();
        System.out.println("subwaystation_merge 数据生成完毕。");
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
     * adcd_bssection_ref insert
     * 注意保证station_dm是最新的数据
     */
    @Test
    public void testInsertAdcdBssectionRef() throws Exception{
        String sql0 = "delete from adcd_bssection_ref";
        String sql1 = "insert into adcd_bssection_ref select a.id,a.name,b.id,b.name from beijingqx a,station_dm b where  1=1 and (ST_Contains(a.the_geom,b.the_geom) or ST_Intersects(a.the_geom,b.the_geom))";
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata!@#&*(";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        Statement statement = connectionPG.createStatement();
        statement.addBatch(sql0);
        statement.addBatch(sql1);
        statement.executeBatch();
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
     * 将生成的通道信息插入到de_cfg_td,因为通道的数量增加8条
     */
    @Test
    public void testInsertDeCfgTD() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata!@#&*(";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        String sql1 = "select id,name from busline_channel";
        Statement statement = connectionPG.createStatement();
        ResultSet rs1 = statement.executeQuery(sql1);
        String sql2 = "delete from de_cfg_td";
        Statement statement2 = connectionOracle.createStatement();
        statement2.addBatch(sql2);
        statement2.executeBatch();
        String sq3 = "insert into de_cfg_td (id,name) values (?,?)";
        PreparedStatement ps = connectionOracle.prepareStatement(sq3);
        while(rs1.next()){
            String id = rs1.getString(1);
            String name = rs1.getString(2);
            ps.setString(1, id);
            ps.setString(2,name);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    /**
     * 更新 de_cfg_td buslinenum
     * @throws Exception
     */
    @Test
   public void testUpdateDeCfgTDBusLineNum() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata!@#&*(";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       
       String sql1_PG = "select channelid,count(*) cc from td_line_station_ref where type='1' group by channelid";
       String sql1_OR = "update de_cfg_td set buslinenum=? where id=?";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String channelid = rs_pg.getString(1);
           int cc = rs_pg.getInt(2);
           ps_oracle.setInt(1,cc);
           ps_oracle.setString(2,channelid);
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
        String passwdPG = "basedata!@#&*(";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
       
       String sql1_PG = "select channelid,count(*) cc from td_line_station_ref where type='2' group by channelid ";
       String sql1_OR = "update de_cfg_td set stationnum=? where id=?";
       Statement sta_PG = connectionPG.createStatement();
       ResultSet rs_pg = sta_PG.executeQuery(sql1_PG);
       PreparedStatement ps_oracle = connectionOracle.prepareStatement(sql1_OR);
       while(rs_pg.next()){
           String channelid = rs_pg.getString(1);
           int cc = rs_pg.getInt(2);
           ps_oracle.setInt(1,cc);
           ps_oracle.setString(2,channelid);
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
   
    /**
     * busstation_distance_2016_01_23 先运行ExportStationDistanceNew
     * 注意清空busstation_distance_2016_01_23数据
     * 生成断面数据 station_dm_singlebus 根据公交线路来生成
     * 公交车0为顺时针 1 逆时针
     * 导航 0 未调查：默认为双方向都可以通行  1双向：双方向可以通行    2顺方向：单向通行，通行方向为起点到终点方向   3逆方向：单向通行，通行方向为终点到起点方向
     */
    @Test
    public void testCreateDM_Singlebus(){
        try{
            GeoToolsGeometry gtg = new GeoToolsGeometry();
            String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
            String username = "basedata";
            String passwd = "basedata!@#&*(";
            //把需要的导航数据加载到内存中
            Map<String,NavigationObject> navigationMap = new HashMap<String, NavigationObject>();
            Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
            String sql_query_nl = "select t1.navigationid,ST_AsText(t2.the_geom) wkt,t2.direction from (select navigationid from buslinelink group by navigationid"+
                    ") t1 left join navigationline t2 on t1.navigationid = t2.id";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql_query_nl);
            while(rs.next()){
                String navigationid = rs.getString(1);
                String wkt = rs.getString(2);
                String direct = rs.getString(3);
                NavigationObject no = new NavigationObject(navigationid, direct, wkt);
                navigationMap.put(navigationid, no);
            }
            //把需要的公交站点加载到内存中
            String sql_query_bsl = "select busstationid,ST_AsText(the_geom) wkt from busstationlink";
            Statement statement0 = connection.createStatement();
            ResultSet rs0 = statement0.executeQuery(sql_query_bsl);
            Map<String,String> busstationlinkMap = new HashMap<String, String>();
            while(rs0.next()){
                String busstationid = rs0.getString(1);
                String wkt = rs0.getString(2);
                busstationlinkMap.put(busstationid, wkt);
            }
            
            String sql1 = "select t1.*,t2.arrow from (select s_stationname,e_stationname,linkids,s_stationid,e_stationid,buslineid,buslinename,index from busstation_distance_2016_04_14  order by buslinename,index"
                        +") t1 left join busline t2 on t1.buslineid=t2.id";
            Statement statement1 = connection.createStatement();
            Statement statementDel = connection.createStatement();
            statementDel.addBatch("delete from station_dm_singlebus");
            statementDel.executeBatch();
            ResultSet rs1 = statement1.executeQuery(sql1);
            //准备将生成的断面数据插入到station_dm_singlebus
            String insert_sql = "insert into station_dm_singlebus (id,name,linkids,buslineid,buslinename,the_geom) values(?,?,?,?,?,ST_GeomFromText(?,4326))";
            PreparedStatement ps = connection.prepareStatement(insert_sql);
            int commitValue = 5000;
            int index = 0;
            //MULTILINESTRING((0 0, 2 0),(1 1,2 2)) 构造类似这样的wkt多线格式  注意终点是下一起点，程序可以优化
            while(rs1.next()){
                 String s_name = rs1.getString(1);
                 String e_name = rs1.getString(2);
                 String linkids = rs1.getString(3);
                 String s_stationid = rs1.getString(4);
                 String e_stationid = rs1.getString(5);
                 String buslineid = rs1.getString(6);
                 String buslinename = rs1.getString(7);
                 String arrow = rs1.getString(9);
                 if(null!=linkids&&!(linkids.trim().equals(""))&&linkids.length()>32){
                     index++;
                     String multilineWkt = "MULTILINESTRING(";
                     String[] linkidArr = linkids.split(",");
                     String s_nameWkt = busstationlinkMap.get(s_stationid);
                     NavigationObject s_namelinkWkt = navigationMap.get(linkidArr[0]);
                     String e_nameWkt = busstationlinkMap.get(e_stationid);
                     NavigationObject e_namelinkWkt = navigationMap.get(linkidArr[linkidArr.length-1]);
                     List<String> wktList = pointSplitLine(s_nameWkt, s_namelinkWkt);
                     MultiLineString subline1 = gtg.createMLineByWKT(wktList.get(0));
                     MultiLineString subline2 = gtg.createMLineByWKT(wktList.get(1));
                     MultiLineString naviLine = gtg.createMLineByWKT(navigationMap.get(linkidArr[1]).wkt);
                     double dis1 = gtg.distanceGeo(subline1, naviLine);
                     double dis2 = gtg.distanceGeo(subline2, naviLine);
                     List<String> startString = null;
                     if(dis1>dis2){//取dis2 wktList(1)
                         startString = getXYByWkt(wktList.get(1));
                     }else{//取dis1 wktList(2)
                         startString = getXYByWkt(wktList.get(0));
                     }
                     String startStringwkt="(";
                     for(int k=0;k<startString.size();k++){
                         if(k==startString.size()-1){
                             startStringwkt +=startString.get(k)+"),";
                         }else{
                             startStringwkt +=startString.get(k)+",";
                         }
                     }
                     multilineWkt +=startStringwkt;
                     
                     if(linkidArr.length>2){
                         for(int i=1;i<linkidArr.length-1;i++){
                             String middleLineWkt="(";
                             NavigationObject navigationWkt = navigationMap.get(linkidArr[i]);
                             List<String> middStrList = getXYByWkt(navigationWkt.wkt);
                             for(int u = 0;u<middStrList.size();u++){
                                 if(u==middStrList.size()-1){
                                     middleLineWkt += middStrList.get(u)+"),";
                                 }else{
                                     middleLineWkt += middStrList.get(u)+",";
                                 }
                                 
                             }
                             multilineWkt +=middleLineWkt;
                         }
                     }
                     
                     List<String> wktList1 = pointSplitLine(e_nameWkt, e_namelinkWkt);
                     MultiLineString subline3 = gtg.createMLineByWKT(wktList1.get(0));
                     MultiLineString subline4 = gtg.createMLineByWKT(wktList1.get(1));
                     MultiLineString naviLine1 = gtg.createMLineByWKT(navigationMap.get(linkidArr[linkidArr.length-2]).wkt);
                     double dis3 = gtg.distanceGeo(subline3, naviLine1);
                     double dis4 = gtg.distanceGeo(subline4, naviLine1);
                     List<String> endString = null;
                     
                     if(dis3>dis4){//取dis2 wktList(1)
                         endString = getXYByWkt(wktList1.get(1));
                     }else{//取dis1 wktList(2)
                         endString = getXYByWkt(wktList1.get(0));
                     }
                     
                     String endStringwkt="(";
                     for(int h=0;h<endString.size();h++){
                         if(h==endString.size()-1){
                             endStringwkt +=endString.get(h)+")";
                         }else{
                             endStringwkt +=endString.get(h)+",";
                         }
                     }
                     multilineWkt +=endStringwkt;
                     multilineWkt +=")";
                     
                     ps.setString(1, StringUtil.GetUUIDString());
                     ps.setString(2, s_name+"-"+e_name);
                     ps.setString(3, linkids);
                     ps.setString(4,buslineid);
                     ps.setString(5,buslinename);
                     ps.setString(6, multilineWkt);
                     ps.addBatch();
                     if(index%5000==0){
                         ps.executeBatch();
                     }
                 }
            }
            ps.executeBatch();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    /**
     * 提取station_dm_singlebus 数据到 station_dm
     * 根据name唯一
     */
    @Test
    public void testCreateStationDM() throws Exception{
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql_query = "select id,name,ST_AsText(the_geom) wkt,linkids,buslineid,buslinename from station_dm_singlebus  order by buslinename";
        String sql_insert = "insert into station_dm (id,name,linkids,buslineid,buslinename,the_geom) values(?,?,?,?,?,ST_GeomFromText(?,4326))";
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_insert);
        ResultSet rs = statement.executeQuery(sql_query);
        Map<String,String> map = new HashMap<String, String>();
        Map<String,String> mapBusName = new HashMap<String, String>();
        int index=1;
        while(rs.next()){
            String id = rs.getString(1);
            String name = rs.getString(2);
            String wkt = rs.getString(3);
            String linkids = rs.getString(4);
            String buslineid = rs.getString(5);
            String buslinename = rs.getString(6);
            
            if(null==map.get(name)){
                map.put(name, id);
                mapBusName.put(name, buslinename);
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, linkids);
                ps.setString(4, buslineid);
                ps.setString(5, buslinename);
                ps.setString(6, wkt);
                ps.addBatch();
                index++;
                if(index%2000==0){
                    ps.executeBatch();
                }
            }else{
                mapBusName.put(name, mapBusName.get(name)+","+buslinename);
            }
        }
        ps.executeBatch();
        String updateSQL = "update station_dm set cc=? ,buslinenames=? where name=?";
        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
        int index1 = 0;
        for (Map.Entry<String, String> entry : mapBusName.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            psUpdate.setInt(1, value.split(",").length);
            psUpdate.setString(2, value);
            psUpdate.setString(3, key);
            index1++;
            psUpdate.addBatch();
            if(index1%2000==0){
                psUpdate.executeBatch();
            }
        }
        psUpdate.executeBatch();
    }
    
    /**
     * 抽取wkt当中的xy对
     * @param wkt
     * @return
     */
    public List<String> getXYByWkt(String wkt){
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(wkt);
        int i=0;
        while(matcher.find()){
            result.add(i, matcher.group());
            i++;
        }
        return result;
    }
    
    /**
     * 点打断线
     * @param pointWkt
     * @param lineWkt
     * @return
     */
    public List<String> pointSplitLine(String pointWkt,NavigationObject lineWkt){
        List<String> retList = new ArrayList<String>();
        List<String> pointList = getXYByWkt(pointWkt);
        String pointStr = pointList.get(0);
        String[] xyArr = pointStr.split(" ");
        double pointx = Double.parseDouble(xyArr[0]);
        double pointy = Double.parseDouble(xyArr[1]);
        List<String> linePointList =getXYByWkt(lineWkt.wkt);
        WKTObject[] arrTemp = new WKTObject[linePointList.size()];
        if(linePointList.size()==2){//线只有2个点表示的话，就直接将点插入
            String multilineWkt1 = "MULTILINESTRING(";
            multilineWkt1 +="("+linePointList.get(0)+","+pointStr+"))";
            String multilineWkt2 = "MULTILINESTRING(";
            multilineWkt2 +="("+pointStr+","+linePointList.get(1)+"))";
            retList.add(0,multilineWkt1);
            retList.add(1,multilineWkt2);
        }else{//一个线多个点组成，先求最近距离，将点插入到中间
            for(int i=0;i<linePointList.size();i++){
                String[] xyArrLine = linePointList.get(i).split(" ");
                double pointx2 = Double.parseDouble(xyArrLine[0]);
                double pointy2 = Double.parseDouble(xyArrLine[1]);
                double dis = PBMathUtil.Distance(pointx, pointy, pointx2, pointy2);
                arrTemp[i] = new WKTObject(i,dis);
            }
            Arrays.sort(arrTemp, new Comparator(){
                @Override
                public int compare(Object o1, Object o2) {
                    WKTObject w1 = (WKTObject)o1;
                    WKTObject w2 = (WKTObject)o2;
                    return (w1.distance < w2.distance ? -1 : (w1.distance == w2.distance ? 0 : 1));
                }
            });
            int index1 = arrTemp[0].index;
            int index2 = arrTemp[1].index;
            int a = index1;
            if(index1<index2){
                a = index2;
            }
            String multilineWkt3 = "MULTILINESTRING((";
            for(int j = 0;j<a;j++){
                multilineWkt3 +=linePointList.get(j)+",";
            }
            multilineWkt3 +=pointStr+"))";
            retList.add(0, multilineWkt3);
            
            String multilineWkt4 = "MULTILINESTRING(("+pointStr+",";
            for(int j = a;j<linePointList.size();j++){
                if(j==linePointList.size()-1){
                    multilineWkt4 +=linePointList.get(j)+"))";
                }else{
                    multilineWkt4 +=linePointList.get(j)+",";
                }
            }
            retList.add(1, multilineWkt4);
        }
        return retList;
    }
    
    /**
     * bikestation 修改adcd_id adcdname
     * @throws Exception
     */
    @Test
    public void testUpdateBikeStation() throws Exception{
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        Statement statementUpdate = connection.createStatement();
        String sql_1 = "update bikestation t1 set adcd_name=t2.name from (select a.id,a.name,b.name stname from beijingqx a,bikestation b where  1=1 and (ST_Contains(a.the_geom,b.the_geom) or ST_Intersects(a.the_geom,b.the_geom))) t2 where t1.name=t2.stname";
        String sql_2 = "update bikestation set adcdcode=t2.code from beijingqx t2 where adcd_name=t2.name";
        statementUpdate.addBatch(sql_1);
        statementUpdate.addBatch(sql_2);
        statementUpdate.executeBatch();
    }
    
    /**
     * 生成断面和通道的关系表de_td_dm插入到oracle中
     */
    @Test
    public void testInsertDETDDM() throws Exception{
        String urlPG = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata!@#&*(";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        String sql1 = "select channelid,name from busline_channel_section";
        Statement statement = connectionPG.createStatement();
        ResultSet rs1 = statement.executeQuery(sql1);
        String sql2 = "delete from de_td_dm";
        Statement statement2 = connectionOracle.createStatement();
        statement2.addBatch(sql2);
        statement2.executeBatch();
        String sq3 = "insert into de_td_dm (id,tdid,dmname) values (?,?,?)";
        PreparedStatement ps = connectionOracle.prepareStatement(sq3);
        while(rs1.next()){
            String dmname = rs1.getString(2);
            String tdid = rs1.getString(1);
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setString(2,tdid);
            ps.setString(3,dmname);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    @Test
    public void testCopyTDLineStationRef() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        Statement statementDel = connection.createStatement();
        ResultSet rs = statementDel.executeQuery("select id,tdname,ref_id,type from td_line_station_ref");
        String insertSQL = "insert into td_line_station_ref (id,tdname,ref_id,type) values(?,?,?,?)";
        PreparedStatement psInsert = connectionOracle.prepareStatement(insertSQL);
        
        int index =0;
        while(rs.next()){
            String id = rs.getString(1);
            String name = rs.getString(2);
            String ref_id = rs.getString(3);
            String type = rs.getString(4);
            psInsert.setString(1, id);
            psInsert.setString(2, name);
            psInsert.setString(3, ref_id);
            psInsert.setString(4, type);
            psInsert.addBatch();
            index++;
            if(index%5000==0){
                psInsert.executeBatch();
            }
        }
        psInsert.executeBatch();
     }
    
    /**
     * copy adcd_channel_ref 到武汉oracle
     */
    @Test
    public void testCopy_adcd_channel_ref() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        String sql2 = "delete from adcd_channel_ref";
        Statement statement2 = connectionOracle.createStatement();
        statement2.addBatch(sql2);
        statement2.executeBatch();
        String insert = "INSERT INTO adcd_channel_ref( adcd_id, adcd_name, channel_id, channel_name) VALUES (?, ?, ?, ?)";
        ResultSet rs1 = connection.createStatement().executeQuery("SELECT adcd_id, adcd_name, channel_id, channel_name FROM adcd_channel_ref");
        PreparedStatement ps = connectionOracle.prepareStatement(insert); 
        while(rs1.next()){
            ps.setString(1, rs1.getString(1));
            ps.setString(2, rs1.getString(2));
            ps.setString(3, rs1.getString(3));
            ps.setString(4, rs1.getString(4));
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    /**
     * copy td_line_station_ref 到武汉oracle
     */
    @Test
    public void testCopy_td_line_station_ref() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        String sql2 = "delete from td_line_station_ref";
        Statement statement2 = connectionOracle.createStatement();
        statement2.addBatch(sql2);
        statement2.executeBatch();
        String insert = "INSERT INTO td_line_station_ref(id, channelid, ref_id, type) VALUES (?, ?, ?, ?)";
        ResultSet rs1 = connection.createStatement().executeQuery("SELECT id, channelid, ref_id, type FROM td_line_station_ref");
        PreparedStatement ps = connectionOracle.prepareStatement(insert); 
        while(rs1.next()){
            ps.setString(1, rs1.getString(1));
            ps.setString(2, rs1.getString(2));
            ps.setString(3, rs1.getString(3));
            ps.setString(4, rs1.getString(4));
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    @Test
    public void testConver84To02BikeStation() throws Exception {
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String tableName = "bikestation";
        String sql_update = "update "+tableName+" set the_geom=st_geometryfromtext(?,4326) where netid=?";
        String sql_query = "select netid, st_astext(the_geom) from "+tableName;
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_update);
        ResultSet rs = statement.executeQuery(sql_query);
        while(rs.next()){
            String id = rs.getString(1);
            String wkt = rs.getString(2);
            String wktCopy= wkt;
            Matcher matcher = pattern.matcher(wkt);
            while(matcher.find()){
                String temp = wkt.substring(matcher.start(),matcher.end());
                String[] xyArrTemp = temp.split(" ");
                double x_double = Double.parseDouble(xyArrTemp[0]);
                double y_double = Double.parseDouble(xyArrTemp[1]);
                double[] cehuiDouble = Gis84ToCehui.transform(x_double,y_double);
                wktCopy = wktCopy.replaceAll(temp, cehuiDouble[0]+" "+cehuiDouble[1]);
            }
            ps.setString(1, wktCopy);
            ps.setString(2, id);
            ps.addBatch();
       }
        ps.executeBatch();
    }
    
    /**
     * 更新busline_channel
     * 增加arrowstr字段  钱惠敏提供
     * 20160310
     */
    @Test
    public void testUpdateBusLineChanelByExcel() throws Exception{
        String filePath = "d:\\busline_channel.xls";
        List<String> list = POIExcelUtil.ReadXLS(filePath, ",", 2, 135, 1, 8);
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String updateSQL = "update busline_channel set arrowstr=? where id=?";
        PreparedStatement ps = connection.prepareStatement(updateSQL);
        for(String s:list){
            String[] sArr = s.split(",");
            ps.setString(1, sArr[7]);
            ps.setString(2, sArr[0]);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    @Test
    public void testUpdateTableOwner() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/emission";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        DatabaseMetaData dmd = connection.getMetaData();
        String[] types   =   {"TABLE"};
        ResultSet rs = dmd.getTables(null, null, null, types);
        while(rs.next()){
            String tableName = rs.getObject("TABLE_NAME").toString();
            System.out.println("alter table "+tableName+" owner to emission;");
        }
    }
    
    @Test
    public void updatejtwtest_channel_color() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql = "select  channelid ,buslineid from busline_channel_section_station";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        Map<String,Integer> map1 = new HashMap<String, Integer>();
        Map<String,Integer> map0 = new HashMap<String, Integer>();
        Map<String,String> map2 = new HashMap<String, String>();
        Map<String,String> map3 = new HashMap<String, String>();
        String sql1 = "select id,lxmc from busline_channel";
        ResultSet rs1 = connection.createStatement().executeQuery(sql1);
        while(rs1.next()){
            String id = rs1.getString(1);
            String lxmc = rs1.getString(2);
            map2.put(id, lxmc);
            map3.put(lxmc, "1");
        }
        
        while(rs.next()){
            String channelid = rs.getString(1);
            String buslineid = rs.getString(2);
            String lxmc = map2.get(channelid);
            if(null==map1.get(lxmc+":"+buslineid)){
                map1.put(lxmc+":"+buslineid, 1);
                if(null==map0.get(lxmc)){
                    map0.put(lxmc, 1);
                }else{
                    map0.put(lxmc, map0.get(lxmc)+1);
                }
            }
        }
        String update  = "update jtwtest_channel_color set buslinecount=? where name=?";
        PreparedStatement ps = connection.prepareStatement(update);
        for (Map.Entry<String, Integer> entry : map0.entrySet()) {
            String s1 = entry.getKey() ; 
            Integer s2 = entry.getValue();
            ps.setInt(1, s2);
            ps.setString(2, s1);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    /**
     * 更新adcd_busstation_ref 保证busstation_merge最新
     */
    @Test
    public void testInsertadcd_busstation_ref() throws Exception{
        String queryAllRef = "insert into adcd_busstation_ref select a.id,a.name,b.id,b.name from beijingqx a,busstation_merge b where  1=1 and (ST_Contains(a.the_geom,b.the_geom) or ST_Intersects(a.the_geom,b.the_geom))";
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        Statement statementDel = connection.createStatement();
        statementDel.execute("delete from adcd_busstation_ref");
        Statement statementUpdate = connection.createStatement();
        statementUpdate.execute(queryAllRef);
    }
    
    /**
     * 合并buslinelink到buslinelink_merge
     */
    @Test
    public void testMergeBusLinelinkByBuslineid() throws Exception{
        //"205cab9a6319405182052921861c9b31""f9635e545f8b4b3aa413cd4c5585d060"
        String buslineid1="f9635e545f8b4b3aa413cd4c5585d060";
        String queryTD = "select buslineid,string_agg(st_astext(the_geom),'%') wkts from buslinelink  where buslineid='"+buslineid1+"' group by buslineid";
        String updateSQL = "update busline set the_geom=ST_GeomFromText(?,4326) where id=?";
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        PreparedStatement ps = connection.prepareStatement(queryTD);
        PreparedStatement psInsert = connection.prepareStatement(updateSQL);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while(rs.next()){
            count++;
            String buslineid =  rs.getString(1);
            String wkts = rs.getString(2);
            String[] wktArr = wkts.split("%");
            List<String> wktList = Arrays.asList(wktArr);
            Geometry result =  GeoToolsGeometry.UnionManyGeo(wktList);
            String wktnew = result.toText();
            psInsert.setString(1, wktnew);
            psInsert.setString(2, buslineid1);
            psInsert.addBatch();
            System.out.println(buslineid+"------"+count);
        }
        psInsert.executeBatch();
    }
    
    /**
     * 根据钱惠敏提供的excel导入到qhm_excel_busline_channel_ref中
     * @throws Exception
     */
    @Test
    public void insertqhm_excel_busline_channel_ref() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        
        Map<String,String> map = new HashMap<String,String>();
        String sqlq = "select buslineid,id,name from busstation where buslineid in (select id from busline where isvalid='1') order by buslineid";
        ResultSet rs = connection.createStatement().executeQuery(sqlq);
        while(rs.next()){
            String buslineid = rs.getString(1);
            String id = rs.getString(2);
            String name = rs.getString(3);
            map.put(buslineid+name, id);
        }
        rs.close();
        
        String sql = "INSERT INTO qhm_excel_busline_channel_ref( id, lxmc, ldmc, channelname, sectionname, linelabel, channelid, sectionid, buslineid, linecode, s_stationid, e_stationid, s_stationname, e_stationname) VALUES (?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(sql);
        List<String> list = POIExcelUtil.ReadXLS("D:\\通道断面与线路断面关系知春路通道.xls", "@", 2, 763, 1, 10);
        int index=0;
        for(String s:list){
            String[] arr = s.split("@");
            if((arr.length==10)&&!(arr[9].equals("NULL"))){
                index++;
                String uuid = StringUtil.GetUUIDString();
                String lxmc = arr[0];
                String ldmc = arr[1];
                String channelname = arr[2];
                String sectionname = arr[3];
                String linelabel = arr[4];
                String dm = arr[5];
                String[] arr2 = dm.split("-");
                String s_stationname = arr2[0];
                String e_stationname = arr2[1];
                String channelid = arr[6];
                String sectionid = arr[7];
                String buslineid = arr[8];
                String linecode = arr[9];
                String s_stationid = map.get(buslineid+s_stationname);
                String e_stationid = map.get(buslineid+e_stationname);
                ps.setString(1, uuid);
                ps.setString(2, lxmc);
                ps.setString(3, ldmc);
                ps.setString(4, channelname);
                ps.setString(5, sectionname);
                ps.setString(6, linelabel);
                ps.setString(7, channelid);
                ps.setString(8, sectionid);
                ps.setString(9, buslineid);
                ps.setString(10, linecode);
                ps.setString(11, s_stationid);
                ps.setString(12, e_stationid);
                ps.setString(13, s_stationname);
                ps.setString(14, e_stationname);
                ps.addBatch();
                if(index%5000==0){
                    ps.executeBatch();
                }
            }
            
        }
        ps.executeBatch();
    }
    
    
}