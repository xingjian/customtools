package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.geotools.data.DataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.promise.gistool.util.GeoToolsUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述: 节能减排
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月2日 上午10:50:26  
 */
public class PollutionReductionTest {

    /**
     * 生成正六边形在指定的范围内
     */
    @Test
    public void testSixPolygon() throws Exception{
        //获取范围
        DataStore ds = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "pollutionreduction", "postgis", "postgis");
        ReferencedEnvelope re = GISDBUtil.GetBoundsByTableName(ds,"beijingqx");
        double minx = re.getMinX();
        double miny = re.getMinY();
        double maxx = re.getMaxX();
        double maxy = re.getMaxY();
        String url = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String insert_sql = "insert into hexagon_beijingqx_all (id,the_geom) values(?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insert_sql);
        //纬度1度 = 大约111km 
        //纬度1分 = 大约1.85km 
        //纬度1秒 = 大约30.9m
        //1英里= 63360 米
        //1米=1/1852 海里
        //1海里= 1/60度
        //如果要进行具体的运算，需要进行一下单位换算，比如要求一个500米的范围，那么应该是
        //500*1/1852*1/60（度）
        List<Polygon> list = GeoToolsUtil.CreateHexagonsByExtents(minx,miny,maxx,maxy,(double)500/1852/60);
        for(int i=0;i<list.size();i++){
            Polygon pTemp = list.get(i);
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setString(2, pTemp.toText());
            ps.addBatch();
        }
        ps.executeBatch();
        ds.dispose();
    }
    
    @Test
    public void testClipGeometry() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username = "postgis";
        String passwd = "postgis";
        DataStore ds = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "pollutionreduction", "postgis", "postgis");
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        DataStore ds1 = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "pollutionreduction", "postgis", "postgis");
        List<SimpleFeature> list1 = GISDBUtil.GetFeaturesByTableName(ds,"beijingqx");
        List<SimpleFeature> list2 = GISDBUtil.GetFeaturesByTableName(ds1,"hexagon_beijingqx_all");
        List<SimpleFeature> result = GeoToolsUtil.ClipGeometry(list2, list1);
        System.out.println(result.size());
        String url1 = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username1 = "postgis";
        String passwd1 = "postgis";
        Connection connection1 = DBConnection.GetPostGresConnection(url1, username1, passwd1);
        String insert_sql = "insert into test_hexagon (id,the_geom) values(?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection1.prepareStatement(insert_sql);
        int index = 0;
        for(SimpleFeature sfTemp:result){
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setString(2, ((Geometry)sfTemp.getDefaultGeometry()).toText());
            ps.addBatch();
        }
        ps.executeBatch();
    }
   
    @Test
    public void testExportNavigationKind01(){
        String url = "jdbc:postgresql://localhost:5432/buscity";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql_query = "select *,st_astext(the_geom) wkt from navigationline where substr(kind,0,3)='01'";
        String shapeFilePath = "G:\\项目文档\\节能减排\\gisdata-test\\navigation_kind01.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"UTF-8","4","EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testShapeToPostGISNavigationlinekind01(){
        String url = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username = "postgis";
        String passwd = "postgis";
        String shapePath = "G:\\项目文档\\节能减排\\gisdata-test\\navigation_kind01.shp";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\节能减排\\gisdata-test\\navigation_kind01_bnk.properties");
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "UTF-8","navigationline_kind01_bnk",mapping);
        Assert.assertEquals("success", result);
    }
    
    @Test
    public void testSetRandomValue() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String updateSQL = "update hexagon_navigation_kind01_result set pValue=? where id=?";
        String querySQL = "select id from hexagon_navigation_kind01_result";
        PreparedStatement ps = connection.prepareStatement(querySQL);
        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            String id = rs.getString(1);
            Random rand = new Random();
            int randNum = rand.nextInt(5)+1;
            psUpdate.setDouble(1, Double.parseDouble(randNum+""));
            psUpdate.setString(2, id);
            psUpdate.addBatch();
        }
        psUpdate.executeBatch();
    }
    
    /**
     * 测试 读取shape属性字段方法
     */
    @Test
    public void testGetShapeAttributes(){
        String shapePath = "G:\\项目文档\\节能减排\\gisdata-test\\navigation_kind01.shp";
        List<String> attributes = ConversionUtil.GetShapeAttributes(shapePath, "UTF-8");
        PrintUtil.PrintObject(attributes);
    }
    
    @Test
    public void testShapeToPostGis1(){
        String shapePath = "G:\\项目文档\\节能减排\\北京地图20150429\\link_84_english_1.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "pollutionreduction", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "navigation_link", MultiLineString.class, "EPSG:4326");
        System.out.println(result);
    }
    
    /**
     * 生成蜂窝图和导航路链的关系，并且计算出长度比例系数
     */
    @Test
    public void testHexagonTouchRefNavilink() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String queryTouch = "select id, st_astext(the_geom) wkt from hexagon_touch";
        String queryLink = "select \"LINKID\" linkid,\"SHAPE_LEN\" length,st_astext(the_geom) wkt from navigation_link";
        String query = "select hexagonid,navigationid from hexagon_touch_ref_navilink";
        Map<String,Geometry> map1 = new HashMap<String, Geometry>();
        Map<String,Double> map2 = new HashMap<String, Double>();
        Map<String,Geometry> map3 = new HashMap<String, Geometry>();
        Statement statement1 = connection.createStatement();
        ResultSet rs1 = statement1.executeQuery(queryTouch);
        while(rs1.next()){
            String uuid = rs1.getString(1);
            Geometry geom1 = GeoToolsGeometry.createGeometrtyByWKT(rs1.getString(2));
            map1.put(uuid, geom1);
        }
        Statement statement2 = connection.createStatement();
        ResultSet rs2 = statement2.executeQuery(queryLink);
        while(rs2.next()){
            String linkid = rs2.getString(1);
            double length = rs2.getDouble(2);
            Geometry geom2 = GeoToolsGeometry.createGeometrtyByWKT(rs2.getString(3));
            map2.put(linkid, length);
            map3.put(linkid, geom2);
        }
        Statement statement3 = connection.createStatement();
        ResultSet rs3 = statement3.executeQuery(query);
        String update = "update hexagon_touch_ref_navilink set linklength=? where hexagonid=? and navigationid=?";
        PreparedStatement ps = connection.prepareStatement(update);
        int i = 0;
        while(rs3.next()){
            String hexagonid = rs3.getString(1);
            String navigationid = rs3.getString(2);
            Geometry g1 =  map1.get(hexagonid);
            Geometry g2 = map3.get(navigationid);
            Geometry intersection = GeoToolsGeometry.intersection(g1,g2);
            Geometry g3 = GeoToolsGeometry.createGeometrtyByWKT(GISCoordinateTransform.From84To900913(g2.toText()));
            ps.setDouble(1,g3.getLength());
            ps.setString(2, hexagonid);
            ps.setString(3,navigationid);
            ps.addBatch();
            i++;
            if(i%5000==0){
                ps.executeBatch();
                System.out.println("i====="+i);
            }
        }
        ps.executeBatch();
    }
    
    /**
     * 计算总排放量
     * CO_g CO2_kg THC_g NOx_g
     */
    @Test
    public void testCalcAllPaiFang() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String txtPath = "G:\\项目文档\\节能减排\\排放数据输出0624\\66.txt";
        List<String> list = PBFileUtil.ReadFileByLine(txtPath);
        String queryRef = "select navigationid,string_agg(hexagonid||':'||factor,';') from hexagon_touch_ref_navilink group by navigationid";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(queryRef);
        Map<String,String> mapRS = new HashMap<String, String>();
        while(rs.next()){
            String navigationid = rs.getString(1);
            String value = rs.getString(2);
            mapRS.put(navigationid, value);
        }
        //计算结果存储，最后在遍历整体更新
        Map<String,Double> map = new HashMap<String, Double>();
        for(String s : list){
            if(s.indexOf("ID")==-1){
                String[] arr = s.split(" ");
                String linkid = arr[0];
                double cog = Double.parseDouble(arr[5]);
                double co2kg = Double.parseDouble(arr[6]);
                double thcg = Double.parseDouble(arr[7]);
                double noxg = Double.parseDouble(arr[8]);
                double all = cog+co2kg+thcg+noxg;
                String strHexagon = mapRS.get(linkid);
                String[] arr1 = strHexagon.split(";");
                for(String sTemp:arr1){
                    String[] arrTemp = sTemp.split(":");
                    String hexagonid = arrTemp[0];
                    double factor = Double.parseDouble(arrTemp[1]);
                    if(null==map.get(hexagonid)){
                        map.put(hexagonid, all*factor);
                    }else{
                        map.put(hexagonid, map.get(hexagonid)+all*factor);
                    }
                }
            }
        }
        String updateSQL = "update hexagon_touch set pvalue=? where id=?";
        PreparedStatement ps = connection.prepareStatement(updateSQL);
        for(Map.Entry<String, Double> entry : map.entrySet()){
            ps.setDouble(1, entry.getValue());
            ps.setString(2, entry.getKey());
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    /**
     * 生成正方形在指定的范围内
     */
    @Test
    public void testFourPolygon(){
        //获取范围
        DataStore ds = GISDBUtil.ConnPostGis("postgis", "192.168.1.105", "5432", "basedata", "basedata", "basedata");
        List<SimpleFeature> listFeature = GISDBUtil.GetFeaturesByTableName(ds, "traffic_area_middle");
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String insert_sql = "insert into traffic_middle_square_testxj (id,traffic_middle_code,the_geom) values(?,?,ST_GeomFromText(?,4326))";
       try{
            PreparedStatement ps = connection.prepareStatement(insert_sql);
            for(int i=0;i<listFeature.size();i++){
                SimpleFeature sfTemp = listFeature.get(i);
                Point pointTemp = GeoToolsGeometry.GetCentroid((Geometry)sfTemp.getDefaultGeometry());
                Polygon polygonTemp = GeoToolsUtil.CreateSquareByLength(pointTemp.getX(), pointTemp.getY(), (double)1000/1852/60,Math.PI/4);
                ps.setString(1,StringUtil.GetUUIDString());
                ps.setInt(2, Integer.parseInt(sfTemp.getAttribute("code").toString()));
                ps.setString(3, polygonTemp.toText().replace("POLYGON ((", "MULTIPOLYGON(((")+")");
                ps.addBatch();
            }
            ps.executeBatch();
            ds.dispose();
       }catch(Exception e){
           e.printStackTrace();
       }
    }
    
    
    @Test
    public void testFourPoint(){
        //获取范围
        DataStore ds = GISDBUtil.ConnPostGis("postgis", "192.168.1.105", "5432", "basedata", "basedata", "basedata");
        List<SimpleFeature> listFeature = GISDBUtil.GetFeaturesByTableName(ds, "traffic_area_middle");
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String insert_sql = "insert into traffic_area_middle_point (id,name,traffic_middle_code,the_geom) values(?,?,ST_GeomFromText(?,4326))";
       try{
            PreparedStatement ps = connection.prepareStatement(insert_sql);
            for(int i=0;i<listFeature.size();i++){
                SimpleFeature sfTemp = listFeature.get(i);
                Point pointTemp = GeoToolsGeometry.GetCentroid((Geometry)sfTemp.getDefaultGeometry());
                //Polygon polygonTemp = GeoToolsUtil.CreateSquareByLength(pointTemp.getX(), pointTemp.getY(), (double)1000/1852/60);
                ps.setString(1,StringUtil.GetUUIDString());
                ps.setString(2,StringUtil.GetUUIDString());
                ps.setInt(3, Integer.parseInt(sfTemp.getAttribute("code").toString()));
                ps.setString(4, pointTemp.toText());
                ps.addBatch();
            }
            ps.executeBatch();
            ds.dispose();
       }catch(Exception e){
           e.printStackTrace();
       }
    }
}
