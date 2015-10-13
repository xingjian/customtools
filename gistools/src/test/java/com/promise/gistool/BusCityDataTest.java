package com.promise.gistool;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBMathUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.tongtu.nomap.core.transform.BeijingToGis84;
import com.tongtu.nomap.core.transform.Gis84ToCehui;
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
        String tableName = "huanmian";
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
}
