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

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBMathUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.tongtu.nomap.core.transform.BeijingToGis84;
import com.tongtu.nomap.core.transform.Gis84ToCehui;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;

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
                double[] cehuiDouble = Gis84ToCehui.transform(x_double,y_double);
                wktCopy = wktCopy.replaceAll(temp, cehuiDouble[0]+" "+cehuiDouble[1]);
            }
            ps.setString(1, wktCopy);
            ps.setString(2, id);
            ps.addBatch();
       }
        ps.executeBatch();
    }
    
    @Test
    public void conver54To84() throws Exception {
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
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql_query = "select id,name,ST_AsText(the_geom) wkt,linkids,buslineid,buslinename from station_dm_singlebus  order by buslinename";
        String sql_insert = "insert into station_dm (id,name,linkids,buslineid,buslinename,the_geom) values(?,?,?,?,?,ST_GeomFromText(?,4326))";
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_insert);
        ResultSet rs = statement.executeQuery(sql_query);
        Map<String,String> map = new HashMap<String, String>();
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
            }
        }
        ps.executeBatch();
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
            String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
            String username = "buscity";
            String passwd = "bs789&*(";
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
            
            String sql1 = "select t1.*,t2.arrow from (select s_stationname,e_stationname,linkids,s_stationid,e_stationid,buslineid,buslinename,index from busstation_distance_temp  order by buslinename,index"
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
                 if(null!=linkids&&!(linkids.trim().equals(""))){
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
}
