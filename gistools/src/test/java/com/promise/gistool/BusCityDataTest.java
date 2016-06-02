package com.promise.gistool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBMathUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.tongtu.nomap.core.transform.BeijingToGis84;
import com.tongtu.nomap.core.transform.Gis84ToCehui;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述: 公交都市项目数据
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月5日 下午4:04:33  
 */
public class BusCityDataTest {

    /**
     * 生成公交站点数据保存到busstation_merge
     * 根据linkid和name确定唯一站原则
     */
    @Test
    public void testGenerateStation() throws Exception{
        String url = "jdbc:postgresql://123.116.104.249:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        //map1统计测站和路段的关系数目
        HashMap<String,Integer> map1= new HashMap<String,Integer>();
        //map2记录测站key(测站名称+linkid) value(busstationids)
        HashMap<String,String> map2= new HashMap<String,String>();
        //map3记录key(测站名称+linkid) value(wkts)
        HashMap<String,String> map3= new HashMap<String,String>();
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select array_to_string(ARRAY(SELECT unnest(array_agg(wkt))),',') wkts,array_to_string(ARRAY(SELECT unnest(array_agg(busstationid)) "+
            "),',') busstationids ,linkid,name from (select rank() OVER (PARTITION BY t1.id ORDER BY t1.id) rank,ST_AsText(t1.the_geom) wkt,t1.id,t1.busstationid,t1.linkid,t2.name from busstationlink  t1 left join busstation t2 on t1.busstationid= t2.id order by name desc) t3 group by linkid,name";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while(rs.next()){
             String wkts = rs.getString(1);
             String busstationids = rs.getString(2);
             String linkid = rs.getString(3);
             String name = rs.getString(4);
             if(null==map1.get(name)){
                 map1.put(name, 1);
                 map2.put(name+":"+linkid, busstationids);
                 map3.put(name+":"+linkid, wkts);
             }else{
                 map1.put(name, map1.get(name)+1);
             }
        }
        String insert_sql = "insert into busstation_merge (id,linkid,name,busstationids,the_geom) values(?,?,?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insert_sql);
        
        for (Map.Entry<String, Integer> entry : map1.entrySet()) {
            if(entry.getValue()<=2){
                String nameStr = entry.getKey();
                List<String> list_stationid = GetValuesByName(nameStr,map2);
                for(String itemStr : list_stationid){
                    String linkidTemp = itemStr.split(":")[0];
                    String busstationidsTemp = itemStr.split(":")[1];
                    String uuid = StringUtil.GetUUIDString();
                    String wkts = map3.get(nameStr+":"+linkidTemp).split(",")[0];
                    ps.setString(1, uuid);
                    ps.setString(2, linkidTemp);
                    ps.setString(3, nameStr);
                    ps.setString(4, busstationidsTemp);
                    ps.setString(5, wkts);
                    ps.addBatch();
                }
            }
        }
        ps.executeBatch();
   }
    
    public static List<String> GetValuesByName(String stationname,HashMap<String,String> map){
        List<String> retList = new ArrayList<String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
           String name = entry.getKey().split(":")[0];
           if(name.equals(stationname)){
               retList.add(entry.getKey().split(":")[1]+":"+entry.getValue());
           }
        }
        return retList;
    }
    
    @Test
    public void conver54To02() throws Exception {
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String tableName = "beijinghuanxian";
        String sql_update = "update "+tableName+" set the_geom=st_geometryfromtext(?,4326) where id=?";
        String sql_query = "select id, st_astext(the_geom) from "+tableName;
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
                double[] wgs84XYArr = BeijingToGis84.transSingle(x_double, y_double);
                double[] cehuiDouble = Gis84ToCehui.transform(wgs84XYArr[0],wgs84XYArr[1]);
                wktCopy = wktCopy.replaceAll(temp, cehuiDouble[0]+" "+cehuiDouble[1]);
            }
            ps.setString(1, wktCopy);
            ps.setString(2, id);
            ps.addBatch();
       }
        ps.executeBatch();
    }
    
    @Test
    public void conver84To02() throws Exception {
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String tableName = "busline_history_bak";
        String sql_update = "update "+tableName+" set the_geom=st_geometryfromtext(?,4326) where id=?";
        String sql_query = "select id, st_astext(the_geom) from "+tableName+" where batchtime='201511'";
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_update);
        ResultSet rs = statement.executeQuery(sql_query);
        int i=1;
        while(rs.next()){
            String id = rs.getString(1);
            String wkt = rs.getString(2);
            System.out.println(id +"-----"+i);
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
            i++;
       }
        ps.executeBatch();
    }
    
    @Test
    public void conver54To84() throws Exception {
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String tableName = "busstation_history";
        String sql_update = "update "+tableName+" set the_geom=st_geometryfromtext(?,4326) where id=?";
        String sql_query = "select id, st_astext(the_geom) from "+tableName;
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
                double[] wgs84XYArr = BeijingToGis84.transSingle(x_double, y_double);
                wktCopy = wktCopy.replaceAll(temp, wgs84XYArr[0]+" "+wgs84XYArr[1]);
            }
            ps.setString(1, wktCopy);
            ps.setString(2, id);
            ps.addBatch();
       }
        ps.executeBatch();
    }
    
    /**
     * 将postgresql公交站点基本信息表导入到oracle中
     */
    @Test
    public void postToOracle_DeBusstationMerge() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@182.92.183.85:1521:orcl", "buscity", "admin123ttyj7890uiop");
        String queryPG = "select id,name,arrow,st_astext(the_geom) wkt from busstation_merge_temp";
        String inertOracle = "insert into de_busstation_merge (id,name,arrow,x,y) values(?,?,?,?,?)";
        Statement statement = connection.createStatement();
        PreparedStatement ps = connectOracle.prepareStatement(inertOracle);
        ResultSet rs = statement.executeQuery(queryPG);
        while(rs.next()){
             String id = rs.getString(1);
             String name = rs.getString(2);
             String arrow = rs.getString(3);
             String wkt = rs.getString(4);
             String pointXYStr = wkt.substring(wkt.indexOf("(")+1, wkt.indexOf(")"));
             String[] xyArr = pointXYStr.split(" ");
             String x = xyArr[0];
             String y = xyArr[1];
             ps.setString(1, id);
             ps.setString(2, name);
             ps.setString(3, arrow);
             ps.setString(4, x);
             ps.setString(5, y);
             ps.addBatch();
        }
        ps.executeBatch();
        
    }
    
    /**
     * 生成指定几何对象中心点信息
     * 表格字段必须包含id和centerxy
     * @throws Exception
     */
    @Test
    public void testCreateGeometryCenter() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String tableName = "traffic_area_big";
        String sql_query = "select id,ST_AsText(ST_Centroid(the_geom)) wkt from "+tableName;
        String sql_update = "update "+tableName+" set centerxy=? where id=?";
        
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_update);
        ResultSet rs = statement.executeQuery(sql_query);
        while(rs.next()){
             String id = rs.getString(1);
             String wkt = rs.getString(2);
             String subwkt = wkt.substring(wkt.indexOf("(")+1, wkt.indexOf(")"));
             String[] xyArr = subwkt.split(" ");
             String x = xyArr[0];
             String y = xyArr[1];
             ps.setString(1, id);
             ps.setString(2, x+","+y);
             ps.addBatch();
        }
        ps.executeBatch();
        
    }
    
    
    /**
     * 测试多面wkt获取坐标信息正则表达
     */
    @Test
    public void testRegWKTStr(){
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String wkt = "MULTIPOLYGON(((116.034602615 40.4626854890001,116.034492188 40.4626025220001,116.034324938 40.462544345,116.034161115 40.4625366990001,116.033920114 40.4625315820001,116.033605745 40.4625521740001,116.033372318)))";
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
        System.out.println(wktCopy);
    }
    
    /**
     * 提取station_dm_singlebus 数据到 station_dm
     */
    @Test
    public void testCreateStationDM() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
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
     * 生成断面数据
     * 公交车0为顺时针 1 逆时针
     * 导航 0 未调查：默认为双方向都可以通行  1双向：双方向可以通行    2顺方向：单向通行，通行方向为起点到终点方向   3逆方向：单向通行，通行方向为终点到起点方向
     */
    @Test
    public void testCreateDM(){
        try{
            GeoToolsGeometry gtg = new GeoToolsGeometry();
            String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
            String username = "basedata";
            String passwd = "basedata";
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
            
            String sql1 = "select t1.*,t2.arrow from (select s_stationname,e_stationname,linkids,s_stationid,e_stationid,buslineid,buslinename,index from busstation_distance_2016_01_07  order by buslinename,index"
                        +") t1 left join busline t2 on t1.buslineid=t2.id";
            Statement statement1 = connection.createStatement();
            ResultSet rs1 = statement1.executeQuery(sql1);
            //准备将生成的断面数据插入到station_dm
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
    
    public List<String> pointSplitLine(){
        return null;
    }
    
    
    public List<String> testGetNavigationByIDs(String ids) throws Exception{
        String url = "jdbc:postgresql://221.218.105.161:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String allID = "";
        String[] sArr = ids.split(",");
        for(String sid : sArr){
            allID += "'"+sid+"',";
        }
        allID = allID.substring(0, allID.length()-1);
        String sql = "select * from navigationline t where id in ("+allID+")";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        return null;
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
     * insert point to line
     * 将点插入到line中，并重新生成多线
     * 导航 0 未调查：默认为双方向都可以通行  1双向：双方向可以通行    2顺方向：单向通行，通行方向为起点到终点方向   3逆方向：单向通行，通行方向为终点到起点方向
     * @return
     */
    public List<String> pointSplitMultiLineString(String pointWkt,NavigationObject lineWkt,boolean direct,String arrow){
        List<String> retList = new ArrayList<String>();
        List<String> pointList = getXYByWkt(pointWkt);
        String pointStr = pointList.get(0);
        String[] xyArr = pointStr.split(" ");
        double pointx = Double.parseDouble(xyArr[0]);
        double pointy = Double.parseDouble(xyArr[1]);
        List<String> linePointList =getXYByWkt(lineWkt.wkt);
        WKTObject[] arrTemp = new WKTObject[linePointList.size()];
        if(linePointList.size()==2){//线只有2个点表示的话，就直接将点插入
            if(arrow.equals("0")&&direct){//开始站点--取后半段 断面顺时针
                if(lineWkt.direct.equals("3")){
                    retList.add(0,pointStr);
                    retList.add(1,linePointList.get(1));
                }else{
                    retList.add(0,pointStr);
                    retList.add(1,linePointList.get(0));
                }
            }else{//结尾站点--取前半段
                if(lineWkt.direct.equals("3")){
                    retList.add(0,linePointList.get(0));
                    retList.add(1,pointStr);
                }else{
                    retList.add(0,linePointList.get(1));
                    retList.add(1,pointStr);
                }
            }
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
            if(arrow.equals("0")&&direct){
                if(lineWkt.direct.equals("3")){
                    int indexTemp1 = 0;
                    for(int j = linePointList.size()-1;j>=a;j--){
                        retList.add(indexTemp1,linePointList.get(j));
                        indexTemp1++;
                    }
                    retList.add(indexTemp1,pointStr);
                }else{
                    for(int j = 0;j<a;j++){
                        retList.add(j,linePointList.get(j));
                    }
                    retList.add(a,pointStr);
                }
            }else{
                if(lineWkt.direct.equals("3")){
                    for(int j = 0;j<a;j++){
                        retList.add(j,linePointList.get(j));
                    }
                    retList.add(a,pointStr);
                }else{
                    int indexTemp1 = 0;
                    for(int j = linePointList.size()-1;j>=a;j--){
                        retList.add(indexTemp1,linePointList.get(j));
                        indexTemp1++;
                    }
                    retList.add(indexTemp1,pointStr);
                }
            }
        }
        return retList;
    }
    
    /**
     * 修改busline linename,linecode 
     * 读取bus_line_station_ref
     * 通过label == linelabel
     */
    @Test
    public void testUpdateBuslineValue() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql_query = "select linelabel,linecode,linename from bus_line_station_ref t4  group by linelabel,linecode,linename order by linecode";
        String sql_update = "update busline set linename=?,linecode=? where label=?";
        
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_update);
        ResultSet rs = statement.executeQuery(sql_query);
        int index=0;
        while(rs.next()){
            index++;
             String linelabel = rs.getString(1);
             int linecode = rs.getInt(2);
             String linename = rs.getString(3);
             ps.setString(1, linename);
             ps.setInt(2, linecode);
             ps.setString(3, linelabel);
             ps.addBatch();
             if(index%500==0){
                 ps.executeBatch();
             }
        }
        ps.executeBatch();
    }
    
    
    /**
     * 修改busline linename,linecode 
     * 读取bus_line_station_ref
     * 通过label == linelabel
     */
    @Test
    public void testUpdateBusstationValue() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql_query = "select  linelabel,stationname,stationid,stationorder from bus_line_station_ref t1";
        String sql_update = "update busstation set stationid=?,stationorder=? where tjcc=? and name=?";
        
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_update);
        ResultSet rs = statement.executeQuery(sql_query);
        int index=0;
        while(rs.next()){
            index++;
             String linelabel = rs.getString(1);
             String stationname = rs.getString(2);
             int stationid = rs.getInt(3);
             int stationorder = rs.getInt(4);
             ps.setInt(1, stationid);
             ps.setInt(2, stationorder);
             ps.setString(3, linelabel);
             ps.setString(4, stationname);
             ps.addBatch();
             if(index%5000==0){
                 ps.executeBatch();
             }
        }
        ps.executeBatch();
    }
    
    /**
     * 计算wkt格式有多少对xy
     * @param wkt
     * @return
     */
    public int getGeoJSONXYCount(String json){
        int retCount = 0;
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?),([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(json);
        while(matcher.find()){
            retCount++;
        }
        return retCount;
    }
    
    /**
     * 抽取wkt当中的xy对
     * @param wkt
     * @return
     */
    public List<String> getXYByGeoJSON(String json){
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?),([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(json);
        int i=0;
        while(matcher.find()){
            result.add(i, matcher.group());
            i++;
        }
        return result;
    }
    
    
    /**
     * 计算wkt格式有多少对xy
     * @param wkt
     * @return
     */
    public int getWktXYCount(String wkt){
        int retCount = 0;
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(wkt);
        while(matcher.find()){
            retCount++;
        }
        return retCount;
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
    
    @Test
    public void testInsertList(){
        String s1 = "aaaaaa";
        String s2 = "bbbbbb";
        String s3 = "ccccccc";
        String s4 = "ddddddd";
        String s5 = "eeeeeee";
        String s6 = "fffffff";
        List<String> list = new ArrayList<String>();
        list.add(0, s1);
        list.add(1, s2);
        list.add(2, s3);
        list.add(3, s4);
        list.add(4, s5);
        list.add(5, s6);
        for(int i=0;i<list.size();i++){
            System.out.println(i+"---"+list.get(i));
        }
        list.add(4, "wwwwww");
        for(int j=0;j<list.size();j++){
            System.out.println(j+"***"+list.get(j));
        }
        
    }
    
    /**
     * 处理json格式数据
     */
    @Test
    public void testHongNanTongDao() throws Exception{
//        String name = "西大望路";
//        String json = "{'paths':[[[12966277.285574466,4853682.582611528],[12966260.803840248,4852971.688390166],[12966251.697084926,4852625.3331057],[12966244.85955388,4852180.027699449],[12966243.157635663,4852061.75931309],[12966235.245208897,4851488.451742659],[12966227.273065718,4851002.210724772],[12966218.196168616,4850438.009909662],[12966196.793601645,4849143.817161443],[12966173.862493157,4847888.338971826],[12966193.185734099,4847265.6289955685],[12966193.30516695,4846755.770130341]]],'spatialReference':{'wkid':102113}}";

//        String name = "两广路";
//        String json = "{'paths':[[[12944140.787446097,4847512.805927073],[12944645.868997077,4847797.1755588725],[12944784.64997656,4847870.029601458],[12944973.473323,4847954.110332575],[12945125.869648147,4848020.1567023285],[12945330.57756453,4848106.626090579],[12945458.012422627,4848152.130008981],[12945567.17405365,4848197.514494526],[12945735.574381595,4848256.633758592],[12946065.32849894,4848370.333838169],[12946590.95250127,4848570.622738851],[12946902.552824402,4848729.707303974],[12946986.752988374,4848779.74967093],[12947145.956986353,4848879.834404844],[12947325.584002828,4848986.72681159],[12947523.484246379,4849098.277099746],[12947643.99199879,4849182.477263719],[12947814.661551012,4849307.5234646825],[12948146.804325491,4849512.350813921],[12948385.670038885,4849621.512444947],[12948540.33558831,4849710.251057474],[12948817.897547279,4849869.455055454],[12948997.64399661,4849921.766646688],[12949154.578770312,4849949.116770872],[12949393.444483709,4849958.193667981],[12949698.237134,4849948.997338016],[12950337.441783052,4849953.53578657],[12952987.656873196,4849880.801176839],[12954741.647806685,4849855.720276932],[12955883.66478244,4849889.878073945],[12956523.008504404,4849928.601390964],[12957014.116411136,4850019.370362053],[12957378.147758344,4850146.446921577],[12957906.518716363,4850292.632738172],[12958270.550063571,4850347.094120826],[12959689.412401114,4850419.709297696],[12961509.569137152,4850474.170680349],[12963092.771085506,4850474.170680349],[12964476.281297466,4850474.170680349],[12966214.02936238,4850424.725477692],[12967583.685363,4850424.725477694],[12968825.787072668,4850411.11013203],[12970436.219712399,4850402.0332349185],[12971728.48322188,4850397.494786364],[12973502.777740996,4850383.879440701],[12973662.101171829,4850388.417889256]]],'spatialReference':{'wkid':102113}}";
//
//        String name = "平安大街";
//        String json = "{'paths':[[[12944140.787446097,4847512.805927073],[12944645.868997077,4847797.1755588725],[12944784.64997656,4847870.029601458],[12944973.473323,4847954.110332575],[12945125.869648147,4848020.1567023285],[12945330.57756453,4848106.626090579],[12945458.012422627,4848152.130008981],[12945567.17405365,4848197.514494526],[12945735.574381595,4848256.633758592],[12946065.32849894,4848370.333838169],[12946590.95250127,4848570.622738851],[12946902.552824402,4848729.707303974],[12946986.752988374,4848779.74967093],[12947145.956986353,4848879.834404844],[12947325.584002828,4848986.72681159],[12947523.484246379,4849098.277099746],[12947643.99199879,4849182.477263719],[12947814.661551012,4849307.5234646825],[12948146.804325491,4849512.350813921],[12948385.670038885,4849621.512444947],[12948540.33558831,4849710.251057474],[12948817.897547279,4849869.455055454],[12948997.64399661,4849921.766646688],[12949154.578770312,4849949.116770872],[12949393.444483709,4849958.193667981],[12949698.237134,4849948.997338016],[12950337.441783052,4849953.53578657],[12952987.656873196,4849880.801176839],[12954741.647806685,4849855.720276932],[12955883.66478244,4849889.878073945],[12956523.008504404,4849928.601390964],[12957014.116411136,4850019.370362053],[12957378.147758344,4850146.446921577],[12957906.518716363,4850292.632738172],[12958270.550063571,4850347.094120826],[12959689.412401114,4850419.709297696],[12961509.569137152,4850474.170680349],[12963092.771085506,4850474.170680349],[12964476.281297466,4850474.170680349],[12966214.02936238,4850424.725477692],[12967583.685363,4850424.725477694],[12968825.787072668,4850411.11013203],[12970436.219712399,4850402.0332349185],[12971728.48322188,4850397.494786364],[12973502.777740996,4850383.879440701],[12973662.101171829,4850388.417889256]]],'spatialReference':{'wkid':102113}}";
//
//        String json = "{'paths':[[[12949319.192328442,4857068.208058915],[12949264.730945788,4857696.424885133],[12949046.407683749,4859834.750751408],[12948964.237878343,4860599.121034259],[12948891.622701472,4860999.459969902],[12948409.59169185,4862455.585358732],[12948309.268092226,4862937.616368355],[12948291.114298008,4863629.371474335],[12948154.960841369,4865903.850797244],[12948145.88394426,4866622.836594551],[12948227.57601824,4866758.990051183],[12948336.976514973,4866832.082959481],[12948518.992188577,4866850.236753698],[12948582.53046834,4866904.698136352],[12948591.607365448,4867023.175530193],[12948473.129971607,4868169.730954468],[12948436.822383171,4868270.054554093],[12948391.437897626,4868406.208010726],[12948254.806709567,4868597.300581438],[12948245.729812458,4868615.454375655]]],'spatialReference':{'wkid':102113}}";
        String name = "长安大街";
        String json = "{'paths':[[[12966250.93053521,4852624.284924689],[12964235.381645562,4852640.16949463],[12963195.599195147,4852644.82737604],[12961571.31234404,4852651.635048873],[12960599.96492051,4852647.216033176],[12959585.263369996,4852624.404357546],[12958864.247214105,4852590.246560533],[12957724.61889548,4852542.592850707],[12956616.759716744,4852501.627380861],[12955677.30086595,4852465.0809267135],[12954710.372458117,4852462.811702436],[12952758.481281087,4852469.619375267],[12951331.079494407,4852475.352152377],[12950639.563254202,4852490.161826604],[12949061.974650241,4852501.507947984],[12948867.537959555,4852502.642560122],[12947559.389880285,4852501.567664413],[12945873.714541027,4852505.031217255],[12944605.457035897,4852503.896605114],[12943561.195853464,4852501.627380839],[12942523.802060295,4852499.358156559],[12941221.446474569,4852493.625379439],[12939327.539949685,4852500.433052271],[12936554.965898002,4852500.94064192]]],'spatialReference':{'wkid':102113}}";
        List<String> list = GeoToolsGeometry.getXYByGeoJSON(json);
        Geometry geo = GeoToolsGeometry.createGeometryLine(list,",","1");
        String sql = "insert into tongdaooldline (id,name,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, StringUtil.GetUUIDString());
        ps.setString(2, name);
        ps.setString(3, GISCoordinateTransform.From900913To84(geo.toText()));
        ps.addBatch();
        ps.executeBatch();
    }
    
    /**
     * 匹配北京二环
     */
    @Test
    public void testPatchERHuan() throws Exception{
//        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures("G:\\项目文档\\公交都市\\giss数据\\导航环线\\beijinghuanxian54.shp", "GBK");
//        SimpleFeature sf = sfc.features().next();
//        System.out.println(sf.getDefaultGeometryProperty().toString());
//        String wkt54 = "MULTILINESTRING ((502714.7879646504 309268.04114394885, 502762.53140352434 304630.5453774695, 502762.12550444866 304329.0231627237, 502464.6339618199 304279.95152649615, 502191.3006077342 304221.07142619113, 502109.771950196 304029.7852786853, 502099.2915242518 300926.3285854072, 502824.5091864477 300941.6737621284, 503754.54690747394 301106.71605806483, 504745.0404044586 301206.3739018377, 506190.6693855033 301313.4679952121, 507851.0865540866 301354.15313162416, 508330.9625220178 301277.93876631337, 510132.9758900636 301227.38548013393, 510293.7475525077 301377.36814861355, 510356.8647117007 301776.5020819748, 510468.1694637435 302119.7554477086, 510282.1066241019 304207.97722501884, 510170.77057528787 304327.82078799664, 509649.8798736897 304566.2225596715, 509565.756605144 305196.9014127299, 509361.0772922621 309815.5852591119, 509232.45421024127 309969.56730120967, 508927.3173781525 309981.9180329594, 504099.0107226221 309876.8175136124, 503364.8771883102 309589.3121828786, 502714.7879646504 309268.04114394885))";
//        String wkt84 = GISCoordinateTransform.From54To84(wkt54);
//        System.out.println(wkt84);
//        String wkt02 = GISCoordinateTransform.From84To02(wkt84);
//        System.out.println(wkt02);
//        "中关村大街"
//        "西大望路"
//        "两广路"
//        "平安大街"
//        "长安大街"
//        "四环辅路"

        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql_query = "select id,name,ST_AsText(ST_Buffer(ST_Transform(the_geom,900913),100,12)) wkt from tongdaooldline where name='京港澳高速'";
        String sql_insert = "insert into xingjian_polygon_test (id,name,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_insert);
        ResultSet rs = statement.executeQuery(sql_query);
        int index=0;
        while(rs.next()){
            index++;
            String id = rs.getString(1);
            String name = rs.getString(2);
            String wkt = rs.getString(3);
            String wkt84 = GISCoordinateTransform.From900913To84(wkt);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, wkt84);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    /**
     * 把公交通道多线合并省一个要素，并坐标转换成84坐标，存储到tongdaooldline表中
     * 
     * @throws Exception
     */
    @Test
    public void mergeLineGaoSu() throws Exception{
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures("G:\\项目文档\\公交都市\\giss数据\\路况_图形_发布版\\路况_图形_发布版\\jgagsgl.shp", "GBK");
        SimpleFeatureIterator sfi = sfc.features();
        SimpleFeature sf = sfi.next();
        Geometry geo1 = (Geometry)sf.getProperty("the_geom").getValue();
        System.out.println(geo1.toText());
        while(sfi.hasNext()){
            SimpleFeature sf1 = sfi.next();
            Geometry geo2 = (Geometry)sf1.getProperty("the_geom").getValue();
            geo1 = geo1.union(geo2);
        }
        String name = "京港澳高速";
        String sql = "insert into tongdaooldline (id,name,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, StringUtil.GetUUIDString());
        ps.setString(2, name);
        ps.setString(3, GISCoordinateTransform.From900913To84(geo1.toText()));
        ps.addBatch();
        ps.executeBatch();
    }
    
    public int checkDMDirect(Map<String,NavigationObject> navigationMap,String linkids){
        int d1 = 0;
        int d2 = 0;
        for(String s : linkids.split(",")){
            NavigationObject no = navigationMap.get(s);
            if(no.direct.equals("0")||no.direct.equals("1")||no.direct.equals("2")){//上行
                d1++;  
            }else if(no.direct.equals("3")){//下行
                d2++;
            } 
        }
        
        return d1>d2?1:2;
    }
    
    
    /**
     * 生成公交通道
     * 把多个表的公交通道多记录合并，合并到一张表中，发布GIS图层
     * 插入 oracle de_td_dm
     * ID   VARCHAR2(32 BYTE)   No      1   id
     * TDID    VARCHAR2(32 BYTE)   Yes     2   通道id
     * DMNAME  VARCHAR2(100 BYTE)  Yes     3   断面名称
     * SXX NUMBER(5,0) Yes     4   上下行 1：上行 2：下行
     * ORDERNUM    NUMBER(5,0) Yes     5   断面在通道上的顺序
     * 导航 0 未调查：默认为双方向都可以通行  1双向：双方向可以通行    2顺方向：单向通行，通行方向为起点到终点方向   3逆方向：单向通行，通行方向为终点到起点方向
     */
    @Test
    public void createGJTDGISToPostGis() throws Exception{
        String querySQLOracle = "select id,name from de_cfg_td";
        String insertOracle = "insert into de_td_dm(id,tdid,dmname,sxx) values (?,?,?,?)";
        String isnertPGSQL = "insert into bus_td (id,name,sxx,the_geom) values (?,?,?,ST_GeomFromText(?,4326))";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        //把需要的导航数据加载到内存中
        Map<String,NavigationObject> navigationMap = new HashMap<String, NavigationObject>();
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
        
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@182.92.183.85:1521:orcl", "buscity", "admin123ttyj7890uiop");
        Statement stOracleQuery = connectOracle.createStatement();
        ResultSet rsOraleQuery = stOracleQuery.executeQuery(querySQLOracle);
        Map<String,String> decfgtdMap = new HashMap<String, String>();
        while(rsOraleQuery.next()){
            decfgtdMap.put(rsOraleQuery.getString(2), rsOraleQuery.getString(1));
        }
        //二环路,三环路,长安街,四环辅路,平安大街,两广路,西大望路,中关村大街,京通快速路,京藏高速,京开高速,京港澳高速
        String str1 = "erhuantongdao:二环路,sanhuantongdao:三环路,tongdao_shfl:四环辅路,tongdao_cadj:长安街,tongdao_padj:平安大街,tongdao_lgl:两广路,"+
                      "tongdao_xdwl:西大望路,tongdao_zgc:中关村大街,tongdao_jtksl:京通快速路,tongdao_jzgsl:京藏高速,tongdao_jtksl:京开高速,tongdao_jgagsl:京港澳高速"  ;
        PreparedStatement psOracle = connectOracle.prepareStatement(insertOracle);
        PreparedStatement psPGSQL = connection.prepareStatement(isnertPGSQL);
        for(String str : str1.split(",")){
            String[] arrTemp = str.split(":");
            String tableName = arrTemp[0];
            String gsName = arrTemp[1];
            String sqlTongDao = "select id,name,ST_AsText(the_geom) wkt,linkids from "+tableName;
            ResultSet rsTemp = connection.createStatement().executeQuery(sqlTongDao);
            List<String[]> listArrTemp1 = new ArrayList<String[]>();
            List<String[]> listArrTemp2 = new ArrayList<String[]>();
            List<String> listArrTemp11 = new ArrayList<String>();
            List<String> listArrTemp21 = new ArrayList<String>();
            int i1=0;
            int i2=0;
            while(rsTemp.next()){
                //id,tdid,dmname,sxx
                String id = rsTemp.getString(1);
                String name = rsTemp.getString(2);
                String wkt = rsTemp.getString(3);
                String linkids = rsTemp.getString(4);
                
                psOracle.setString(1, StringUtil.GetUUIDString());
                psOracle.setString(2, decfgtdMap.get(gsName));
                psOracle.setString(3, name);
                
                int sxx = checkDMDirect(navigationMap,linkids);
                if(sxx==1){//上行
                    listArrTemp1.add(i1,new String[]{id,name,wkt,linkids});
                    listArrTemp11.add(i1,wkt);
                    psOracle.setString(4, "1");
                    i1++;
                }else{//下行
                    listArrTemp2.add(i2,new String[]{id,name,wkt,linkids});
                    listArrTemp21.add(i2,wkt);
                    psOracle.setString(4, "2");
                    i2++;
                }
                psOracle.addBatch();
            }
            psOracle.executeBatch();
            i1=0;
            i2=0;
            Geometry geo1 = GeoToolsGeometry.UnionManyGeo(listArrTemp11);
            Geometry geo2 = GeoToolsGeometry.UnionManyGeo(listArrTemp21);
            //id,name,sxx,the_geom
            psPGSQL.setString(1, StringUtil.GetUUIDString());
            psPGSQL.setString(2, gsName);
            psPGSQL.setString(3, "1");
            psPGSQL.setString(4, geo1.toText());
            psPGSQL.addBatch();
            psPGSQL.setString(1, StringUtil.GetUUIDString());
            psPGSQL.setString(2, gsName);
            psPGSQL.setString(3, "2");
            psPGSQL.setString(4, geo2.toText());
            psPGSQL.addBatch();
        }  
        psPGSQL.executeBatch();
    }
    
    
    /**
     * 生成公交通道
     * 把多个表的公交通道多记录合并，合并到一张表中，发布GIS图层
     * 插入 oracle de_td_dm
     * ID   VARCHAR2(32 BYTE)   No      1   id
     * TDID    VARCHAR2(32 BYTE)   Yes     2   通道id
     * DMNAME  VARCHAR2(100 BYTE)  Yes     3   断面名称
     * SXX NUMBER(5,0) Yes     4   上下行 1：上行 2：下行
     * ORDERNUM    NUMBER(5,0) Yes     5   断面在通道上的顺序
     * 导航 0 未调查：默认为双方向都可以通行  1双向：双方向可以通行    2顺方向：单向通行，通行方向为起点到终点方向   3逆方向：单向通行，通行方向为终点到起点方向
     * 根据传平修改，bus_td不合并
     */
    @Test
    public void createGJTDGISToPostGisNew() throws Exception{
        String isnertPGSQL = "insert into bus_td_dm_color (id,name,sxx,the_geom,dmname) values (?,?,?,ST_GeomFromText(?,4326),?)";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        //把需要的导航数据加载到内存中
        Map<String,NavigationObject> navigationMap = new HashMap<String, NavigationObject>();
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
        //二环路,三环路,长安街,四环辅路,平安大街,两广路,西大望路,中关村大街,京通快速路,京藏高速,京开高速,京港澳高速
        String str1 = "erhuantongdao:二环路,sanhuantongdao:三环路,tongdao_shfl:四环辅路,tongdao_cadj:长安街,tongdao_padj:平安大街,tongdao_lgl:两广路,"+
                      "tongdao_xdwl:西大望路,tongdao_zgc:中关村大街,tongdao_jtksl:京通快速路,tongdao_jzgsl:京藏高速,tongdao_jtksl:京开高速,tongdao_jgagsl:京港澳高速"  ;
        PreparedStatement psPGSQL = connection.prepareStatement(isnertPGSQL);
        for(String str : str1.split(",")){
            String[] arrTemp = str.split(":");
            String tableName = arrTemp[0];
            String gsName = arrTemp[1];
            String sqlTongDao = "select id,name,ST_AsText(the_geom) wkt,linkids from "+tableName;
            ResultSet rsTemp = connection.createStatement().executeQuery(sqlTongDao);
            List<String[]> listArrTemp1 = new ArrayList<String[]>();
            List<String[]> listArrTemp2 = new ArrayList<String[]>();
            List<String> listArrTemp11 = new ArrayList<String>();
            List<String> listArrTemp21 = new ArrayList<String>();
            int i1=0;
            int i2=0;
            while(rsTemp.next()){
                //id,tdid,dmname,sxx
                String id = rsTemp.getString(1);
                String name = rsTemp.getString(2);
                String wkt = rsTemp.getString(3);
                String linkids = rsTemp.getString(4);
                int sxx = checkDMDirect(navigationMap,linkids);
                if(sxx==1){//上行
                    listArrTemp1.add(i1,new String[]{id,name,wkt,linkids});
                    listArrTemp11.add(i1,wkt);
                    i1++;
                }else{//下行
                    listArrTemp2.add(i2,new String[]{id,name,wkt,linkids});
                    listArrTemp21.add(i2,wkt);
                    i2++;
                }
                psPGSQL.setString(1, StringUtil.GetUUIDString());
                psPGSQL.setString(2, gsName);
                psPGSQL.setString(3, sxx+"");
                psPGSQL.setString(4, wkt);
                psPGSQL.setString(5, name);
                psPGSQL.addBatch();
            }
            i1=0;
            i2=0;
        }  
        psPGSQL.executeBatch();
    }
    
    /**
     * 对接公交数据
     */
    @Test
    public void joinBusGPS() throws Exception{
        String readfilePath = "G:\\项目文档\\公交都市\\2015-08-19\\2015-08-19";
        String writefilePath = "G:\\项目文档\\公交都市\\2015-08-19\\BusMisResult.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(writefilePath)));
        File file = new File(readfilePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = null;
        Map<String,String> map = new HashMap<String, String>();
        while((s = br.readLine())!=null){
            if(s.trim()!=""){
                System.out.println(s);
                String[] arrTemp = s.split(",");
                String keyTemp = arrTemp[2];
                String value = arrTemp[3];
                if(null==map.get(keyTemp)&&null!=keyTemp&&!keyTemp.trim().equals("")){
                    map.put(keyTemp, value);
                }
            }
        }
        br.close();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            writer.write(entry.getKey()+","+entry.getValue()+"\n");
        }
        writer.close();
    }
    
    /**
     * 合并双向通道
     */
    @Test
    public void testMergeBUSTD() throws Exception{
        String queryTD = "select name,string_agg(st_astext(the_geom),'%') wkts from bus_td group by name";
        String insertSQL = "insert into bus_td_new (id,name,length,the_geom) values(?,?,?,ST_GeomFromText(?,4326))";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        PreparedStatement ps = connection.prepareStatement(queryTD);
        PreparedStatement psInsert = connection.prepareStatement(insertSQL);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            String name =  rs.getString(1);
            String wkts = rs.getString(2);
            String[] wktArr = wkts.split("%");
            List<String> wktList = Arrays.asList(wktArr);
            Geometry result =  GeoToolsGeometry.UnionManyGeo(wktList);
            String wktnew = result.toText();
            double length = GISCoordinateTransform.From84To900913(result).getLength();
            psInsert.setString(1, StringUtil.GetUUIDString());
            psInsert.setString(2, name);
            psInsert.setDouble(3, length);
            psInsert.setString(4, wktnew);
            psInsert.addBatch();
        }
        psInsert.executeBatch();
    }
    
    /**
     * 合并buslinelink到buslinelink_merge
     */
    @Test
    public void testMergeBusLinelink() throws Exception{
        String queryTD = "select buslineid,string_agg(st_astext(the_geom),'%') wkts from buslinelink group by buslineid";
        String insertSQL = "insert into buslinelink_merge (id,buslineid,the_geom) values(?,?,ST_GeomFromText(?,4326))";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        PreparedStatement ps = connection.prepareStatement(queryTD);
        PreparedStatement psInsert = connection.prepareStatement(insertSQL);
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
            psInsert.setString(1, StringUtil.GetUUIDString());
            psInsert.setString(2, buslineid);
            psInsert.setString(3, wktnew);
            psInsert.addBatch();
            System.out.println(buslineid+"------"+count);
        }
        psInsert.executeBatch();
    }
    
    /**
     * 处理12交通通道和公交线路的关系
     */
    @Test
    public void testQBLineIntersectsTD() throws Exception{
        String queryTD = "select name,st_astext(the_geom) wkt from bus_td_new";
        String queryBusLine = "select id,st_astext(the_geom) wkt from buslinelink_merge";
        String insertSQL = "insert into td_line_station_ref (id,tdname,ref_id,type) values(?,?,?,?)";
        String updateSQL = "update bus_td_new set buslinecount=? where name=?";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        Statement psTD = connection.createStatement();
        ResultSet rsTD = psTD.executeQuery(queryTD);
        Statement psBusLine = connection.createStatement();
        ResultSet rsBusLine = psBusLine.executeQuery(queryBusLine);
        Map<String,Geometry> mapTD = new HashMap<String, Geometry>();
        Map<String,Geometry> mapBusLine = new HashMap<String, Geometry>();
        while(rsTD.next()){
            mapTD.put(rsTD.getString(1), GeoToolsGeometry.createGeometrtyByWKT(rsTD.getString(2)));
        }
        while(rsBusLine.next()){
            mapBusLine.put(rsBusLine.getString(1), GeoToolsGeometry.createGeometrtyByWKT(rsBusLine.getString(2)));
        }
        PreparedStatement psInsert = connection.prepareStatement(insertSQL);
        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
        for (Map.Entry<String, Geometry> entry : mapTD.entrySet()) {
            String tdName = entry.getKey();
            Geometry tdGeometry = entry.getValue();
            int count = 0;
            for (Map.Entry<String, Geometry> entryTemp : mapBusLine.entrySet()) {
                String ref_id = entryTemp.getKey();
                Geometry busLineGeometry = entryTemp.getValue();
                if(GeoToolsGeometry.isIntersects(tdGeometry, busLineGeometry)){
                    count++;
                    psInsert.setString(1, StringUtil.GetUUIDString());
                    psInsert.setString(2, tdName);
                    psInsert.setString(3, ref_id);
                    psInsert.setString(4, "1");// 1公交线路 2公交站点
                    psInsert.addBatch();
                }
            }
            psInsert.executeBatch();
            psUpdate.setInt(1, count);
            psUpdate.setString(2, tdName);
            psUpdate.addBatch();
            psUpdate.executeBatch();
            System.out.println(tdName + "---"+count);
        }
       
    }
    
    
    /**
     * 处理12交通通道和公交站点的关系（先使用公交车站点进行统计，在按重复名字去统计）
     */
    @Test
    public void testQBStationIntersectsTD() throws Exception{
        String queryTD = "select name,st_astext(the_geom) wkt from bus_td_new";
        String queryBusStation = "select * from ( select t1.id,t1.busstationid,st_astext(t1.the_geom) wkt ,t2.name,t2.direct from busstationlink t1 left join busstation t2 on t1.busstationid=t2.id ) t3 where t3.busstationid is NOT NULL";
        String insertSQL = "insert into td_line_station_ref (id,tdname,ref_id,type) values(?,?,?,?)";
        String updateSQL = "update bus_td_new set busstationcount=? where name=?";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        Statement psTD = connection.createStatement();
        ResultSet rsTD = psTD.executeQuery(queryTD);
        Statement psBusLine = connection.createStatement();
        ResultSet rsBusStation = psBusLine.executeQuery(queryBusStation);
        Map<String,Geometry> mapTD = new HashMap<String, Geometry>();
        Map<String,Geometry> mapBStation = new HashMap<String, Geometry>();
        Map<String,String[]> mapBusStation = new HashMap<String, String[]>();
        while(rsTD.next()){
            mapTD.put(rsTD.getString(1), GeoToolsGeometry.createGeometrtyByWKT(rsTD.getString(2)));
        }
        while(rsBusStation.next()){
            //id busstationid wkt name direct
            String[] strArr = new String[5];
            String id = rsBusStation.getString(1);
            String wkt = rsBusStation.getString(3);
            strArr[0] = id;
            strArr[1] = rsBusStation.getString(2);
            strArr[2] = wkt;
            strArr[3] = rsBusStation.getString(4);
            strArr[4] = rsBusStation.getString(5);
            mapBusStation.put(id, strArr);
            mapBStation.put(id, GeoToolsGeometry.createGeometrtyByWKT(wkt));
        }
        PreparedStatement psInsert = connection.prepareStatement(insertSQL);
        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
        double d = 5.0/111000;
        for (Map.Entry<String, Geometry> entry : mapTD.entrySet()) {
            String tdName = entry.getKey();
            Geometry tdGeometry = entry.getValue();
            int count = 0;
            Map<String,Integer> mapTemp= new HashMap<String, Integer>();
            for (Map.Entry<String, Geometry> entryTemp : mapBStation.entrySet()) {
                String ref_id = entryTemp.getKey();
                if(GeoToolsGeometry.distanceGeo(tdGeometry, entryTemp.getValue())<d){
                    String[] arr1 = mapBusStation.get(ref_id);
                    String arr1_name = arr1[3].trim();
                    String arr1_direct = arr1[4].trim();
                    if(mapTemp.get(arr1_name+arr1_direct)==null){
                        mapTemp.put(arr1_name+arr1_direct, 1);
                        count++;
                        psInsert.setString(1, StringUtil.GetUUIDString());
                        psInsert.setString(2, tdName);
                        psInsert.setString(3, arr1[3]);
                        psInsert.setString(4, "2");// 1公交线路 2公交站点
                        psInsert.addBatch();
                    }else{
                        mapTemp.put(arr1_name+arr1_direct, mapTemp.get(arr1_name+arr1_direct)+1);
                    }
                }
            }
            psInsert.executeBatch();
            psUpdate.setInt(1, count);
            psUpdate.setString(2, tdName);
            psUpdate.addBatch();
            System.out.println(tdName + "---"+count);
        }
        psUpdate.executeBatch();
    }
    
    /**
     * 导入王昌同学的通道数据
     */
    @Test
    public void testImportWangChangTD() throws Exception{
        String filePath = "G:\\项目文档\\公交都市\\数据\\12条通道内所有线路2015.xls";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        List<String> list = POIExcelUtil.ReadXLS(filePath, ",", 1, 224, 1, 12);
        String insertSQL = "insert into td_busline_ref_wc (id,tdname,buslinename) values (?,?,?)";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        String[] tdNameArr = list.get(0).split(",");
        for(int i=1;i<list.size();i++){
            String[] buslineNames = list.get(i).split(",");
            for(int j=0;j<buslineNames.length;j++){
                if(!buslineNames[j].equals("NULL")){
                   ps.setString(1, StringUtil.GetUUIDString());
                   ps.setString(2, tdNameArr[j]);
                   ps.setString(3, buslineNames[j]);
                   ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }
    
    @Test
    public void testPatchWCTDData() throws Exception{
        String insertSQL = "insert into td_line_station_ref_wc (id,tdname,ref_id,type) values (?,?,?,?)";
        String querySQL = "select t1.id,t1.buslineid,t2.linenumber from buslinelink_merge t1 left join busline t2 on t1.buslineid=t2.id";
        String querySQL1 = "select id,tdname,buslinename from td_busline_ref_wc";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        Statement statement = connection.createStatement();
        Statement statement1 = connection.createStatement();
        PreparedStatement psInsert = connection.prepareStatement(insertSQL);
        ResultSet rs = statement.executeQuery(querySQL);
        ResultSet rs1 = statement1.executeQuery(querySQL1);
        Map<String,String> map1 = new HashMap<String, String>();
        Map<String,String> map2 = new HashMap<String, String>();
        while(rs.next()){
            String id = rs.getString(1);
            String buslineid = rs.getString(2);
            String linenumber = rs.getString(3).trim();
            map1.put(linenumber, id);
        }
        
        int okCount = 0;
        int allCount = 0;
        int failCouont = 0;
        while(rs1.next()){
            allCount++;
            String idTemp = rs1.getString(1);
            String tdNameTemp = rs1.getString(2);
            String buslinenameTemp = rs1.getString(3);
            String subbuslinenameTemp = buslinenameTemp.replaceAll("路", "").trim().replaceAll("线", "");
            subbuslinenameTemp = subbuslinenameTemp.replaceAll("环", "");
            
            if(null!=map1.get(subbuslinenameTemp)){
                okCount++;
                psInsert.setString(1, StringUtil.GetUUIDString());
                psInsert.setString(2, tdNameTemp);
                psInsert.setString(3, map1.get(subbuslinenameTemp));
                psInsert.setString(4, "3");
                psInsert.addBatch();
            }else{
                failCouont++;
                if(null!=map2.get(buslinenameTemp)){
                    
                }else{
                    map2.put(buslinenameTemp, buslinenameTemp);
                    System.out.println(buslinenameTemp);
                }
            }
        }
        psInsert.executeBatch();
        System.out.println(allCount + "---"+okCount+"---"+failCouont);
    }
    
    /**
     * 生成断面和路链的关系  20151209
     * @throws Exception
     */
    @Test
    public void testCopyDMToOracle() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@182.92.183.85:1521:orcl", "buscity", "admin123ttyj7890uiop");
        String query1 = "select name,linkids from station_dm";
        String insertSQL = "insert into dm_navigationlink_ref (name,linkid) values (?,?)";
        PreparedStatement ps = connectOracle.prepareStatement(insertSQL);
        Statement statement = connection.createStatement();
        ResultSet rs1 = statement.executeQuery(query1);
        int insertCount = 0;
        while(rs1.next()){
            String dmName = rs1.getString(1);
            String linkids = rs1.getString(2);
            if(null!=linkids){
                String[] linkidArr = linkids.split(",");
                for(String linkid : linkidArr){
                   ps.setString(1, dmName); 
                   ps.setString(2, linkid);
                   insertCount++;
                   ps.addBatch();
                   if(insertCount%5000==0){
                       ps.executeBatch();
                       System.out.println("insertCount===="+insertCount);
                   }
                   
                }
            }
        }
        ps.executeBatch();
    }
    
    
    @Test
    public void testBusChannelSplit() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@182.92.183.85:1521:orcl", "buscity", "admin123ttyj7890uiop");
        String query1 = "select name,direction,code,dmname from bus_channel_split";
        String insert1 = "insert into de_td_dm (id,tdid,dmname,sxx) values (?,?,?,?)";
        PreparedStatement ps =  connectOracle.prepareStatement(insert1);
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query1);
        while(rs.next()){
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setInt(2, Integer.parseInt(rs.getString(3)));
            String dmname="";
            if(null!=rs.getString(4)){
                dmname = rs.getString(4).trim();
            }
            ps.setString(3, dmname);
            ps.setString(4, rs.getString(2));
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    @Test
    public void testBusTdDmColor() throws Exception{
        String insert1 = "INSERT INTO bus_td_dm_color(id, name, sxx, the_geom, dmname,color) VALUES (?, ?, ?, ST_GeomFromText(?,4326), ?,?)";
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String query1 = "select name,direction,code,st_astext(the_geom),dmname from bus_channel_split";
        PreparedStatement ps =  connection.prepareStatement(insert1);
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query1);
        int i=0;
        while(rs.next()){
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setString(2,rs.getString(1));
            ps.setString(3, rs.getString(2));
            ps.setString(4, rs.getString(4));
            ps.setString(5, rs.getString(5));
            i++;
            ps.setInt(6, (i%4)+1);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    /**
     * 生成断面和线路的关系 20151209
     */
    @Test
    public void testGeneratorDMBuslineRef() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@182.92.183.85:1521:orcl", "buscity", "admin123ttyj7890uiop");
        String query1 = "select buslineid,string_agg(navigationid,',') from buslinelink group by buslineid";
        String query2 = "select name,linkids from station_dm";
        String insertSQL = "insert into dm_busline_ref (name,buslineid) values (?,?)";
        PreparedStatement ps = connectOracle.prepareStatement(insertSQL);
        Statement statement = connection.createStatement();
        ResultSet rs1 = statement.executeQuery(query1);
        Map<String,String> map = new HashMap<String,String>();
        while(rs1.next()){
            String buslineid = rs1.getString(1);
            String linkids = rs1.getString(2);
            map.put(buslineid, linkids);
        }
        Statement statement2 = connection.createStatement();
        ResultSet rs2 = statement2.executeQuery(query2);
        int insertCount = 0;
        while(rs2.next()){
            String dmName = rs2.getString(1);
            String linkids = rs2.getString(2);
            if(null!=linkids){
                for(Entry<String, String > entry:map.entrySet()){
                    String buslineidTemp = entry.getKey();
                    String[] arrStr = entry.getValue().split(",");
                    for(String str:arrStr){
                        if(linkids.indexOf(str)!=-1){
                            ps.setString(1, dmName);
                            ps.setString(2, buslineidTemp);
                            ps.addBatch();
                            insertCount++;
                            if(insertCount%5000==0){
                                ps.executeBatch();
                                System.out.println("insertCount===="+insertCount);
                            }
                            break;
                        }
                    }
                }
            }
        }
        ps.executeBatch();
    }
    
    /**
     * 按照公交线路打断100
     */
    @Test
    public void testHundredBusLine() throws Exception{
        GeoToolsGeometry gtg = new GeoToolsGeometry();
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String querySQL = "select t1.navigationid,ST_AsText(t2.the_geom) wkt,t2.direction,t2.snodeid,t2.enodeid from (select navigationid from buslinelink group by navigationid"
                    +") t1 left join navigationline t2 on t1.navigationid = t2.id";
        Statement statement = connection.createStatement();
        ResultSet rs1 = statement.executeQuery(querySQL);
        Map<String,NavigationObject> map1 = new HashMap<String,NavigationObject>();
        while(rs1.next()){
            String navigationid = rs1.getString(1);
            String wktStr = rs1.getString(2);
            String direction = rs1.getString(3);
            String snode = rs1.getString(4);
            String enode = rs1.getString(5);
            NavigationObject nObject = new NavigationObject();
            nObject.setId(navigationid);
            nObject.setEnode(enode);
            nObject.setSnode(snode);
            nObject.setWkt(wktStr);
            nObject.setGeometry(GeoToolsGeometry.createGeometrtyByWKT(wktStr));
            nObject.setDirect(direction);
            map1.put(navigationid, nObject);
        }
        //5534a40c66a44f8fae27348c2878cf39 653路 五路居-北苑家园
        //String okbuslinesql = "select buslineid,buslinename from busstation_distance_2015_12_21  where buslineid not in(select buslineid from busstation_distance_2015_12_21 where distance = 0 group by buslineid ) group by buslineid,buslinename";
        String okbuslinesql = "select buslineid,buslinename from busstation_distance_2015_12_21  where buslineid not in(select buslineid from busstation_distance_2015_12_21 where distance = 0  group by buslineid ) and buslineid='eed2c6102f96467b839de5d7a17b13f7' group by buslineid,buslinename ";
        Statement statementall = connection.createStatement();
        ResultSet rsall = statementall.executeQuery(okbuslinesql);
        int uuuu = 1;
        while(rsall.next()){
            //String buslineid = "5534a40c66a44f8fae27348c2878cf39";
            String buslineName = rsall.getString("buslinename");
            String buslineid = rsall.getString("buslineid");
            System.out.println("start----"+uuuu+"----"+buslineName+"----"+buslineid);
            String querySQL1 = "select * from busstation_distance_2015_12_21 where buslineid='"+buslineid+"' order by buslineid,index";
            String querySQL2 = "select t1.id,t1.name,t1.index,t2.linkid,ST_AsText(t2.the_geom) wkt from busstation t1 left join busstationlink t2 on t1.id = t2.busstationid where t1.buslineid='"+buslineid+"' order by t1.index";
            Statement statement1 = connection.createStatement();
            ResultSet rs2 = statement1.executeQuery(querySQL1);
            Statement statement2 = connection.createStatement();
            ResultSet rs3 = statement2.executeQuery(querySQL2);
            StringBuffer sbf = new StringBuffer();
            while(rs2.next()){
                String linkids = rs2.getString("linkids")+",";
                sbf.append(linkids);
            }
            String firstLink = null;
            String firstWKT = null;
            String endLink = null;
            String endWKT = null;
            while(rs3.next()){
                if(rs3.isFirst()){
                    firstLink = rs3.getString("linkid");
                    firstWKT = rs3.getString("wkt");
                }
                if(rs3.isLast()){
                    endLink = rs3.getString("linkid");
                    endWKT = rs3.getString("wkt");
                }
            }
            String[] linkArr = sbf.toString().split(",");
            List<String> points = new ArrayList<String>();
            int pointsIndex = 0;
            Map<String,String> mapPoint= new HashMap<String, String>();
            points.add(pointsIndex,GeoToolsGeometry.getXYByWkt(firstWKT).get(0));
            pointsIndex++;
            mapPoint.put(GeoToolsGeometry.getXYByWkt(firstWKT).get(0), GeoToolsGeometry.getXYByWkt(firstWKT).get(0));
            String forward = "";
            for(int i=0;i<linkArr.length;i++){
                NavigationObject naviTemp = map1.get(linkArr[i]);
                if(i==0&&firstLink.equals(naviTemp.getId())){
                    List<String> wktList = pointSplitLine(firstWKT,naviTemp);
                    MultiLineString subline1 = gtg.createMLineByWKT(wktList.get(0));
                    MultiLineString subline2 = gtg.createMLineByWKT(wktList.get(1));
                    MultiLineString naviLine = gtg.createMLineByWKT(map1.get(linkArr[1]).wkt);
                    double dis1 = gtg.distanceGeo(subline1, naviLine);
                    double dis2 = gtg.distanceGeo(subline2, naviLine);
                    if(dis1<dis2){
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(subline1.toText());
                        for(String s1:lTemp1){
                            if(!mapPoint.containsKey(s1)){
                                mapPoint.put(s1, s1);
                                points.add(pointsIndex, s1);
                                pointsIndex++;
                            }
                        }
                    }else{
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(subline2.toText());
                        
                        for(String s1:lTemp1){
                            if(!mapPoint.containsKey(s1)){
                                mapPoint.put(s1, s1);
                                points.add(pointsIndex, s1);
                                pointsIndex++;
                            }
                            
                        }
                    }
                    forward = points.get(pointsIndex);
                }else if(i==linkArr.length-1&&endLink.equals(linkArr[linkArr.length-1])){
                    List<String> wktList = pointSplitLine(endWKT,naviTemp);
                    MultiLineString subline1 = gtg.createMLineByWKT(wktList.get(0));
                    MultiLineString subline2 = gtg.createMLineByWKT(wktList.get(1));
                    MultiLineString naviLine = gtg.createMLineByWKT(map1.get(linkArr[linkArr.length-2]).wkt);
                    double dis1 = gtg.distanceGeo(subline1, naviLine);
                    double dis2 = gtg.distanceGeo(subline2, naviLine);
                    if(dis1<dis2){
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(subline1.toText());
                        for(String s1:lTemp1){
                            if(!mapPoint.containsKey(s1)){
                                mapPoint.put(s1, s1);
                                mapPoint.put(s1, s1);
                                points.add(pointsIndex, s1);
                                pointsIndex++;
                            }
                            
                        }
                    }else{
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(subline2.toText());
                        for(String s1:lTemp1){
                            if(!mapPoint.containsKey(s1)){
                                mapPoint.put(s1, s1);
                                points.add(pointsIndex, s1);
                                pointsIndex++;
                            }
                        }
                    }
                }else{
                    if(null!=forward){
                        String[] arrFor = forward.split(" ");
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(naviTemp.wkt);
                        String[] arrFor1 = lTemp1.get(0).split(" ");
                        String[] arrFor2 = lTemp1.get(lTemp1.size()-1).split(" ");
                        Point pointForward = GeoToolsGeometry.createPoint(Double.parseDouble(arrFor[0]), Double.parseDouble(arrFor[1]));
                        Point pointForward1 = GeoToolsGeometry.createPoint(Double.parseDouble(arrFor1[0]), Double.parseDouble(arrFor1[1]));
                        Point pointForward2 = GeoToolsGeometry.createPoint(Double.parseDouble(arrFor2[0]), Double.parseDouble(arrFor2[1]));
                        
                        double d1 = GeoToolsGeometry.distanceGeo(pointForward, pointForward1);
                        double d2 = GeoToolsGeometry.distanceGeo(pointForward, pointForward2);
                        if(d1<d2){
                            for(String s1:lTemp1){
                                if(!mapPoint.containsKey(s1)){
                                    mapPoint.put(s1, s1);
                                    points.add(pointsIndex, s1);
                                    pointsIndex++;
                                }
                            } 
                        }else{
                            for(int j=lTemp1.size()-1;j>=0;j--){
                                String s1 = lTemp1.get(j);
                                if(!mapPoint.containsKey(s1)){
                                    mapPoint.put(s1, s1);
                                    points.add(pointsIndex, s1);
                                    pointsIndex++;
                                }
                            }
                        }
                        forward = points.get(pointsIndex);
                    }
                }
                
            }
            System.out.println(points.size());
            //开始计算个点之间的位置关系
            String[] resultArr = new String[points.size()];
            resultArr[0] = mapPoint.get(GeoToolsGeometry.getXYByWkt(firstWKT).get(0));
            points.remove(0);
            int m = 1;
            int count = points.size();
            for(int a=0;a<=count-1;a++){
                int iii = calcDisMin(resultArr[a],points);
                resultArr[m] = points.get(iii);
                m++;
                points.remove(iii);
            }
            //System.out.println(resultArr.length);
            
            String insertSQL = "insert into busline_point (buslineid,buslinename,index,the_geom) values (?,?,?,ST_GeomFromText(?,4326))";
            PreparedStatement ps = connection.prepareStatement(insertSQL);
            for(int p=0;p<resultArr.length;p++){
                ps.setString(1, buslineid);
                ps.setString(2,buslineName);
                ps.setInt(3, p);
                String[] arrtt = resultArr[p].split(" ");
                String wkt = GeoToolsGeometry.createPoint(Double.parseDouble(arrtt[0]), Double.parseDouble(arrtt[1])).toText();
                ps.setString(4, wkt);
                ps.addBatch();
            }
            ps.executeBatch();
            int sum = 100;
            double target = 100;
            String resultStr=resultArr[0];
            for(int w=0;w<resultArr.length-1;w++){
                String wkt1 = resultArr[w];
                String wkt2 = resultArr[w+1];
                String[] arrtt = wkt1.split(" ");
                String[] arrtt2 = wkt2.split(" ");
                double[] d1 = GISCoordinateTransform.From84To900913(Double.parseDouble(arrtt[0]), Double.parseDouble(arrtt[1]));
                double[] d2 = GISCoordinateTransform.From84To900913(Double.parseDouble(arrtt2[0]), Double.parseDouble(arrtt2[1]));
                Point p1 = GeoToolsGeometry.createPoint(d1[0],d1[1]);
                Point p2 = GeoToolsGeometry.createPoint(d2[0],d2[1]);
                double dis = GeoToolsGeometry.distanceGeo(p1, p2);
//                double dis = PBMathUtil.Distance(d1[0],d1[1],d2[0], d2[1]);
                if(dis>target){
                    double[] d3 = PBMathUtil.SplitSegmentByLength(d1[0],d2[0],d1[1], d2[1], target);
                    double[] d33 = GISCoordinateTransform.From900913To84(d3[0],d3[1]);
                    resultStr = resultStr+","+d33[0]+" "+d33[1];
                    dis = dis-target;
                    while(dis>sum){
                        double[] dt = PBMathUtil.SplitSegmentByLength(d3[0],d2[0],d3[1], d2[1], sum);
                        double[] dtt = GISCoordinateTransform.From900913To84(dt[0],dt[1]);
                        resultStr = resultStr+","+dtt[0]+" "+dtt[1];
                        d3 = dt;
                        dis = dis-sum;
                    }
                    target = sum - dis;
                }else if(dis==target){
                    resultStr = resultStr+","+wkt2;
                    target = sum;
                    if(w+1==resultArr.length-1){
                        break;
                    }
                }else{
                    target = target - dis;
                    if(w+1==resultArr.length-1){
                        resultStr = resultStr+","+wkt2;
                        break;
                    }
                }
            }
            
            String[] resultArr11 = resultStr.split(",");
            
            String insertSQL11 = "insert into buslinesplit100point (id,buslineid,buslinename,stationflag,direct,index,the_geom) values (?,?,?,?,?,?,ST_GeomFromText(?,4326))";
            PreparedStatement ps11 = connection.prepareStatement(insertSQL11);
            for(int p=0;p<resultArr11.length;p++){
              ps11.setString(1, StringUtil.GetUUIDString());
              ps11.setString(2, buslineid);
              ps11.setString(3, buslineName);
              if(p==0){
                  ps11.setString(4, "1");
              }else if(p==resultArr11.length-1){
                  ps11.setString(4, "2");
              }else{
                  ps11.setString(4, "0");
              }
              ps11.setString(5, "");
              ps11.setInt(6, p+1);
              String[] arrtt = resultArr11[p].split(" ");
              String wkt = GeoToolsGeometry.createPoint(Double.parseDouble(arrtt[0]), Double.parseDouble(arrtt[1])).toText();
              ps11.setString(7, wkt);
              ps11.addBatch();
            }
            ps11.executeBatch();
            System.out.println("end----"+uuuu+"----"+buslineName+"----"+buslineid);
            uuuu++;
        }
        
        
    }
    
    
    
    
    public void testInsert100TestData(String str) throws Exception{//buslinesplit100point
        String[] resultArr = str.split(",");
        GeoToolsGeometry gtg = new GeoToolsGeometry();
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String insertSQL = "insert into testbuslinepoint (buslineid,index,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        for(int p=0;p<resultArr.length;p++){
          ps.setString(1, "aaaaaa");
          ps.setInt(2, p);
          String[] arrtt = resultArr[p].split(" ");
          String wkt = GeoToolsGeometry.createPoint(Double.parseDouble(arrtt[0]), Double.parseDouble(arrtt[1])).toText();
          ps.setString(3, wkt);
          ps.addBatch();
        }
        ps.executeBatch();
    }
    
    @Test
    public void testTestData() throws Exception{
        String str = "116.281738235931 39.932405677162,116.28263542889594 39.93238415923538,116.28352590313804 39.932302135888925,116.28441315453105 39.932197105684,116.28486225235493 39.93179396235399,116.28486131236598 39.93110530414042,116.28486000000001 39.930416463356515,116.28486000000001 39.92972760970467,116.28486000000001 39.9290387491207,116.28488778100186 39.928350288450766,116.28490015909676 39.92766190936984,116.28486002016574 39.92697378443066,116.28486000000001 39.92628513301848,116.28485957805476 39.925596456973395,116.28487830279205 39.92490770519034,116.28489534200409 39.92421944608392,116.28569670058715 39.92412080342953,116.28659426712375 39.924092762457335,116.28749191268844 39.924066165551515,116.28838951981402 39.924043832389984,116.28928741296623 39.924023972295224,116.29018516086471 39.9239997525976,116.29108310026426 39.92398541033587,116.29198125702149 39.92397999999997,116.29287952983167 39.923975703141664,116.29377775953185 39.923968075722875,116.29467583138711 39.92395203872548,116.29557350430477 39.9239262574464,116.29647124904939 39.92391562494739,116.29736891104615 39.92391,116.29826706286576 39.92390000000001,116.29916535772742 39.92389884671409,116.30006351635245 39.92389,116.30096164685875 39.92388000000001,116.30185913254745 39.92388000000001,116.30275740224963 39.92388455767406,116.3036556118498 39.92389512484585,116.3045362664253 39.92380079437816,116.30543442426413 39.92380376627845,116.30633271946488 39.92380837292042,116.30723098683681 39.923804718301646,116.30812922917102 39.923804034353054,116.30902744084085 39.92381444001124,116.30981225005085 39.92359355206276,116.3106329394491 39.92373293945096,116.31067682294147 39.924363177065025,116.31041 39.924960647386186,116.31040493037997 39.92564952607339,116.3104 39.92633839845898,116.31040722265566 39.92702726287868,116.31041999999998 39.92771602973754,116.31041999999998 39.92840490363203,116.31041999999998 39.92909377059442,116.31041999999998 39.92978263062472,116.3104121712524 39.930471437690954,116.31041 39.93116027109088,116.31040603746212 39.93184906373724,116.31076520422461 39.93225,116.31166351950884 39.93225,116.31249295148848 39.93231000000001,116.31339123059868 39.932315615383516,116.31428951763743 39.932320000000026,116.31518783292167 39.932320000000026,116.31608614820593 39.932320000000026,116.3169844167512 39.932322913523784,116.31788234394651 39.932322983998866,116.31878051375244 39.932330000000015,116.3196788290367 39.932330000000015,116.32057714432094 39.932330000000015,116.32147530215428 39.932320000000026,116.3223735965583 39.932322946638166,116.32327185497218 39.93233064818721,116.32417007085805 39.932340000000025,116.32506838614229 39.932340000000025,116.3259 39.93238099867799,116.3259 39.93306982562732,116.32588540419046 39.93375842906055,116.3258679603499 39.93444711029587,116.32584585733719 39.93513563821362,116.32583000000001 39.93582425467677,116.32580978225559 39.93651283725195,116.32580956873683 39.93720145573846,116.3257907831123 39.93788963911652,116.32579000000001 39.93857840177312,116.32575 39.93926573871734,116.32566980124066 39.93995159008118,116.32558266963252 39.94063708944774,116.32550064139662 39.94132289081242,116.32542246904293 39.94200901426859,116.32534176971177 39.94269495961609,116.32525011156756 39.94338003244661,116.3251470779757 39.944064203369166,116.32504540928109 39.94474848767028,116.32494862613638 39.94543291436142,116.32486430331237 39.946118458953734,116.3247532582698 39.94680184397571,116.32466200625016 39.94748695191955,116.32457168916206 39.94817207243635,116.32449080138383 39.948857881627944,116.32439408661085 39.94954252365874,116.32430145054317 39.95022745158624,116.32421475181158 39.950912877533995,116.3241264895613 39.95159808353678,116.32403686618031 39.95228314221179,116.32394411322119 39.95296808828603,116.32385465181079 39.953653271980556,116.32376992186347 39.95433881487262,116.32367800478222 39.95502372339523,116.3235699719453 39.95570716605737,116.32345601513516 39.95639019305693,116.32334125846867 39.957073078433204,116.32322522682905 39.95775588834932,116.32311633126818 39.95843934993414,116.32303115792998 39.959124772131155,116.32295382389829 39.959810512672405,116.32288334002796 39.96049660496375,116.32280969677429 39.961182831247434,116.32272502659062 39.96186826650218,116.32263228617714 39.96255311757773,116.32254157220476 39.963238121177596,116.32242029873024 39.963920327247486,116.32225443631617 39.96459669114836,116.32203207752076 39.96526376806895,116.32179572784133 39.96592798515555,116.32155689121403 39.966591694570795,116.32131803725305 39.96725496855479,116.32102833671372 39.9679066592572,116.32074149893651 39.968559048196326,116.32046002041315 39.96921281275411,116.32017610765708 39.96986595675849,116.31991802564544 39.97052537560631,116.31965640961158 39.97118397665662,116.31939312831364 39.97184218170149,116.319128494248 39.97250005967545,116.31884893801455 39.97315427891934,116.31857054286627 39.97380877867022,116.31827899919249 39.97445992496055,116.31813188293337 39.97512149079447,116.31788851258221 39.975783966494156,116.31852741683593 39.97596332805592,116.31942479886668 39.97598499841642,116.32032281467538 39.97599906257506,116.32121975900961 39.976036963635075,116.3221170460239 39.97606989159722,116.32301493659263 39.97609104503711,116.3239130436022 39.97610309463078,116.32481104661844 39.97611942565516,116.32570721680797 39.97615669656972,116.326603523451 39.97620250838915,116.32750049586777 39.97623774981377,116.32839725887024 39.976275899125085,116.32929483981732 39.97629939359418,116.33019235473857 39.976328067278494,116.3310899735196 39.97635499911789,116.33198803371575 39.97636997378287,116.33288482381221 39.976408494128634,116.33378153955482 39.976439999999975,116.33467979731694 39.9764364189187,116.33557791145458 39.97642424177108,116.33647604785311 39.97641072908311,116.33737419464077 39.976397400080685,116.33827239199816 39.976387446241915,116.33917055078231 39.976375256972965,116.34006858675329 39.976360000000014,116.34096690203755 39.976360000000014,116.3418652173218 39.976360000000014,116.34276353260603 39.976360000000014,116.3436618353672 39.97636233858734,116.34456012039807 39.976367988178716,116.34545823984737 39.97638000000002,116.34635626317265 39.976395890573706,116.34725431922307 39.97640999999999,116.3481521921683 39.97642481376083,116.3490503366915 39.97643252101266,116.34994861001017 39.976439174889,116.35084689726364 39.976442520183774,116.35174509456243 39.97645339294932,116.35264317859328 39.976469011801754,116.35354147924622 39.97646999999999,116.3544396434651 39.97648019841886,116.35533788071876 39.97648927152247,116.35623605734374 39.976501323959326,116.35713419164648 39.97651514141046,116.35803241648193 39.9765242824743,116.35893069597114 39.976530000000004,116.35982895750838 39.97653744879258,116.36072718232566 39.97654710931354,116.36162537780979 39.97655605926045,116.3625234848761 39.97657000000001,116.36342173519684 39.976578202188584,116.36431998430086 39.97658655799391,116.3652182723235 39.97658999999998,116.36611615426334 39.97660336121146,116.36701426628709 39.97661800434323,116.36791241506027 39.976630000000014,116.36881055533937 39.97663657985452,116.36970877965759 39.97664,116.37060648899366 39.97664193450028,116.37150464770265 39.97665041399778,116.37240291909195 39.97665721908418,116.37330121003247 39.97666062072774,116.3741994129585 39.97667150803656,116.37509761457166 39.97668172769174,116.37599569034577 39.97669762283825,116.3768938799347 39.976707072835,116.37779212570128 39.97671516268916,116.37869041070097 39.976719999999965,116.37958872406537 39.97672033145626,116.3804870039552 39.976726442203955,116.38138362375639 39.97673999999999,116.38228190681774 39.97674469279708,116.38318017145953 39.97675200142895,116.38407843363157 39.97675948694696,116.3849767337535 39.97676150210597,116.3858749869645 39.97676959447715,116.38677319509067 39.976780028527585,116.38767144940502 39.97678804865552,116.3885692185711 39.9768,116.3894673091109 39.97681124692124,116.39036544582282 39.97682493674772,116.3912636153287 39.97683580200634,116.39216172039447 39.97685000000002,116.39306003567873 39.97685000000002,116.39395808499009 39.97685999999998,116.39485519428138 39.97687,116.39575350956562 39.97687,116.39665182484988 39.97687,116.39754943266364 39.97685999999998,116.39844771523174 39.97686238250385,116.39934587671142 39.976874064598555,116.4002440374124 39.97688683465677,116.4011421981134 39.976899604712585,116.40204029660316 39.97691122022444,116.40293851087225 39.976920000000014,116.40383679214656 39.976924673437324,116.40473502221816 39.97693387355254,116.40563326403708 39.97694000000002,116.40653150925075 39.97694748911277,116.4074295646007 39.97696,116.40756999999999 39.97755022083811,116.40756999999999 39.97823859299835,116.40756999999999 39.97892695822434,116.40756403985338 39.97961529078421,116.40755660972044 39.98030361654247,116.40755096765214 39.98099194738936,116.40754399723771 39.98168026400083,116.40754 39.98236858220415,116.40754 39.98305690582516,116.40755925549504 39.98374470941606,116.40758331360004 39.984432772288244,116.40760088776824 39.985120899333815,116.40761289177554 39.98580913377133,116.40762489578287 39.98649736127554,116.40762999999998 39.98718561716943,116.40763702829571 39.98787382113708,116.40765 39.98856195802797,116.40767 39.98924994630882,116.40767 39.98993820059989,116.40767685401629 39.99062640927359,116.40769719561222 39.991314466500974,116.40771469675265 39.99200256781774,116.40773065686265 39.99269068561027,116.40774623673363 39.9933788017184,116.40776181660458 39.994066910893736,116.40777739647551 39.994755013136256,116.40779230751347 39.995443072250524,116.40781868774691 39.996130965365566,116.40783517715707 39.99681899654869,116.40784719777275 39.997507107159066,116.40786332418611 39.998195167349536,116.40788 39.99888306008153,116.40788671335542 39.99957119699502,116.40789 40.000259337339,116.4079059180462 40.0009473370039,116.4079131353332 40.00163541357949,116.40793349655543 40.00232335905366,116.40794 40.00301143506889,116.40794 40.00369955068277,116.40794 40.00438765936132,116.40794747432852 40.005075729326705,116.40795999999999 40.00576373411779,116.40798277113537 40.00645157331744,116.408008380763 40.00713937458903,116.40803291668456 40.007827189228536,116.40805163545511 40.008515106906415,116.40806999999998 40.009203020478914,116.40873701005924 40.00936999999998,116.40963512246918 40.00936000000002,116.41053290596368 40.009349999999976,116.41143121459554 40.00934936772105,116.41232943131955 40.009340000000016,116.4132277206758 40.009337809435195,116.41412594352595 40.00933,116.41502420647612 40.0093241662478,116.4159224843853 40.00932000000002,116.41652918545302 40.00949927645222,116.41645280889735 40.010184621596736,116.41639153668835 40.010871048510595,116.41636610715524 40.01155825033722,116.41641450485679 40.012245266383616,116.41646695036135 40.01293210430346,116.41659734537558 40.01361224797313,116.41674691552689 40.01429065309474,116.41688916065122 40.01496997267027,116.41703004586915 40.01564945378086,116.41716980760947 40.016329064322335,116.4173092223941 40.017008709964365,116.41745573843687 40.01768746821708,116.41760356810214 40.01836605646834,116.4177092155741 40.01904911717661,116.41777000000002 40.019734286744786,116.41777000000002 40.0204222337858,116.41777000000002 40.02111017389079,116.4177762593032 40.021798077457895,116.41778 40.02248598600053,116.41778999999998 40.02317381906912,116.41779686603299 40.02386165092314,116.4178 40.02454951960264,116.41784718842837 40.02523626116162,116.41794470326536 40.025919977843394,116.41806296830923 40.02660077824386,116.41817075601512 40.02728285246735,116.4182129389813 40.027969925540305,116.41823486398417 40.02865755177454,116.41824488221464 40.02934528945173,116.41826 40.030033028797,116.41825000000001 40.03072078578384,116.41825000000001 40.031408622044765,116.41820358109153 40.03209493249829,116.41808836444135 40.03277695114125,116.41792955904356 40.03345374963796,116.41775056227199 40.03412775094584,116.41757416845378 40.03480200707484,116.41736213590835 40.03546849520485,116.41698887460461 40.0360933546934,116.41658030103247 40.036705761083276,116.41619234222706 40.03732598475699,116.41652623648405 40.0376406572111,116.41742449482891 40.03764840081759,116.418322587248 40.03765818959745,116.41921953300695 40.03762243794952,116.42011379940274 40.037558169278014,116.4210082495846 40.03749450345689,116.4219049220811 40.037456807720126,116.42229999999999 40.037816997848616,116.42229542100799 40.03850474017695,116.42228995948771 40.03919247126148,116.42228 40.039880174234796,116.42228 40.040567918119606,116.42227255146251 40.04125559607786,116.42297946142519 40.041410452592785,116.42387738899687 40.041430000000005,116.42477556166742 40.04144078390256,116.4256736271883 40.04145674727103,116.42657184825511 40.0414563349142,116.42747001576619 40.04145557722646,116.428201481076 40.0414297531541";
        String[] resultArr = str.split(",");
        GeoToolsGeometry gtg = new GeoToolsGeometry();
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String insertSQL = "insert into testbuslinepoint (buslineid,index,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        for(int p=0;p<resultArr.length;p++){
          ps.setString(1, "aaaaaa");
          ps.setInt(2, p);
          String[] arrtt = resultArr[p].split(" ");
          String wkt = GeoToolsGeometry.createPoint(Double.parseDouble(arrtt[0]), Double.parseDouble(arrtt[1])).toText();
          ps.setString(3, wkt);
          ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    public int calcDisMin(String s1,List<String> list){
        String[] arr1 = s1.split(" ");
        double x1 = Double.parseDouble(arr1[0]);
        double y1 = Double.parseDouble(arr1[1]);
        String[] arrTemp = list.get(0).split(" ");
        double x2 = Double.parseDouble(arrTemp[0]);
        double y2 = Double.parseDouble(arrTemp[1]);
        double disTemp = PBMathUtil.Distance(x1, y1, x2, y2);
        int ret = 0;
        for(int i=1;i<list.size();i++){
            String[] arrTemp1 = list.get(i).split(" ");
            double x21 = Double.parseDouble(arrTemp1[0]);
            double y21 = Double.parseDouble(arrTemp1[1]);
            double disTemp1 = PBMathUtil.Distance(x1, y1, x21, y21);
            if(disTemp1<disTemp){
                disTemp = disTemp1;
                ret = i;
            }
        }
        return ret;
    }
    
    @Test
    public void testUpdataBuslineNetwork0106() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String queryBusline = "select * from busline";
        Statement state = connection.createStatement();
        ResultSet rs1 = state.executeQuery(queryBusline);
        Map<String,String> map1 = new HashMap<String,String>();
        while(rs1.next()){
            String id = rs1.getString("id");
            String label = rs1.getString("label");
            map1.put(id,label);
        }
        String sql = "select * from busline_network_0106";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        String updateSQL = "update busline_network_0106 set buslinenames=? where navigationid=?";
        PreparedStatement ps = connection.prepareStatement(updateSQL);
        int c = 0;
        while(rs.next()){
            String navigation = rs.getString("navigationid");
            String buslineids = rs.getString("buslineids");
            String[] buslineidArr = buslineids.split(",");
            String buslineNames = "";
            for(String buslineid:buslineidArr){
                buslineNames = buslineNames+map1.get(buslineid)+",";
            }
            buslineNames = buslineNames.substring(0, buslineNames.length()-1);
            ps.setString(1, buslineNames);
            ps.setString(2, navigation);
            ps.addBatch();
            c++;
            if(c%5000==0){
                ps.executeBatch();
            }
        }
        ps.executeBatch();
    }
    
    @Test
    public void testShapeToPostGis(){
        String shapePath = "d:\\tongdaofulu.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "basedata", "basedata", "basedata");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "tongdaofulu", MultiLineString.class, "EPSG:4326");
        System.out.println(result);
    }
}