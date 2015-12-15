package com.promise.gistool;

import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述:ConversionUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月2日 上午8:59:15  
 */
@SuppressWarnings("all")
public class ConversionUtilTest {

    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     */
    @Test
    public void testShapeToPostGIS(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\cl\\huanmian.shp";
        String url = "jdbc:postgresql://127.0.0.1:5432/postgis";
        String username = "postgis";
        String passwd = "postgis";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\giss数据\\cl\\huanmian.properties");
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","huanmian",mapping);
        Assert.assertEquals("success", result);
    }
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入导航数据
     */
    @Test
    public void testShapeToPostGISDHSJ(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\cl\\beijingroad.shp";
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\数据\\navigationline.properties");
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","navigationline",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入导航数据(河北)
     */
    @Test
    public void testShapeToPostGISDHSJHeBei(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\地图\\导航shape\\navigationline_hebei.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\数据\\navigationline.properties");
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","navigationline_hebei",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入导航数据(天津)
     */
    @Test
    public void testShapeToPostGISDHSJTianjin(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\地图\\导航shape\\navigationline_tianjin.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\数据\\navigationline.properties");
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","navigationline_tianjin",mapping);
        Assert.assertEquals("success", result);
    }
    
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入公交线路
     */
    @Test
    public void testShapeToPostGISBusLine(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\cl\\busline.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\数据\\busline.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","busline",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入北京环线54坐标的
     */
    @Test
    public void testShapeToPostGISBeijingHX(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\导航环线\\beijinghuanxian54.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\数据\\beijinghuanxian54.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","beijinghuanxian",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入公交测站
     */
    @Test
    public void testShapeToPostGISBusStation(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\cl\\busstation.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\数据\\busstation.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","busstation",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入交通小区
     */
    @Test
    public void testShapeToPostGISTrafficSmall(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\TAZ_BusMetroStopLines\\TAZ_BusMetroStopLines\\TAZ1911_BusMetroStopsLines.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\数据\\trafficsmall.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","traffic_area_small_54",mapping);
        Assert.assertEquals("success", result);
    }
    
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入交通中区
     */
    @Test
    public void testShapeToPostGISTrafficMiddle(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\TAZ_BusMetroStopLines\\TAZ_BusMetroStopLines\\TAZ389_BusMetroStopsLines.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\数据\\trafficmiddle.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","traffic_area_middle_54",mapping);
        Assert.assertEquals("success", result);
    }
    
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入交通大区
     */
    @Test
    public void testShapeToPostGISTrafficBig(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\TAZ_BusMetroStopLines\\TAZ_BusMetroStopLines\\TAZ60_BusMetroStopsLines.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\数据\\trafficbig.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "UTF-8","traffic_area_big_54",mapping);
        Assert.assertEquals("success", result);
    }
    
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入环线区域
     */
    @Test
    public void testShapeToPostGISHuanMian(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\TAZ_BusMetroStopLines\\TAZ_BusMetroStopLines\\环面.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\giss数据\\cl\\huanmian.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","huanmian",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入北京区县
     */
    @Test
    public void testShapeToPostGISBeijingQX(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\TAZ_BusMetroStopLines\\TAZ_BusMetroStopLines\\beijingqx.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\giss数据\\cl\\qx.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","beijingqx",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入北京环线
     */
    @Test
    public void testShapeToPostGISHuanxian(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\beijing\\beijingringarea.shp";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\公交都市\\giss数据\\cl\\huanmian.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","huanmian",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试 shape数据导入 geotools方式
     */
    @Test
    public void testShapeToPostGIS2(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\cl\\环面.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "postgis", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK","beijinghuanmian1",MultiPolygon.class,"");
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试 读取shape属性字段方法
     */
    @Test
    public void testGetShapeAttributes(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\导航环线\\beijinghuanxian54.shp";
        List<String> attributes = ConversionUtil.GetShapeAttributes(shapePath, "GBK");
        PrintUtil.PrintObject(attributes);
    }
    
    /**
     * 测试 shape文件创建表，并导入表
     */
    @Test
    public void testCreateTableSchema2(){
        String tableName = "rbeijing_polylinetest11";
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\地图\\导航shape\\shp\\Rbeijing_polyline.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "postgis", "postgis", "postgis");
        String result = GISDBUtil.CreateTableSchema(tableName,shapePath,dataStore,"GBK");
        String resultInsert = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK",tableName,MultiLineString.class,"");
        PrintUtil.PrintObject(result);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 测试shape导入postgis
     */
    @Test
    public void testShapeToPostGIS3(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\cl\\busline.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "192.168.1.105", "5432", "busycity", "postgres", "admin123");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK");
    }
    
    /**
     * 测试 创建表 点 线 面 多点 多线 多面
     */
    @Test
    public void testCreateTableSchema(){
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "127.0.0.1", "5432", "postgis", "postgis", "postgis");
        //创建point类型
        Map<String,Class> columns = new HashMap<String, Class>();
        columns.put("c1", Integer.class);
        columns.put("c2", String.class);
        columns.put("c3", Double.class);
        columns.put("c4", Date.class);
        columns.put("c5", Boolean.class);
        columns.put("the_geom", Point.class);
        String result = GISDBUtil.CreateTableSchema("test1", dataStore, columns);
        //创建线LineString类型
        Map<String,Class> columns1 = new HashMap<String, Class>();
        columns1.put("c1", Integer.class);
        columns1.put("c2", String.class);
        columns1.put("c3", Double.class);
        columns1.put("c4", Date.class);
        columns1.put("c5", Boolean.class);
        columns1.put("the_geom", LineString.class);
        String result1 = GISDBUtil.CreateTableSchema("test2", dataStore, columns1);
        //创建面Polygon
        Map<String,Class> columns2 = new HashMap<String, Class>();
        columns2.put("c1", Integer.class);
        columns2.put("c2", String.class);
        columns2.put("c3", Double.class);
        columns2.put("c4", Date.class);
        columns2.put("c5", Boolean.class);
        columns2.put("the_geom", Polygon.class);
        String result2 = GISDBUtil.CreateTableSchema("test3", dataStore, columns2);
        System.out.println(result +" >>> "+result1 +" >>> "+ result2);
      //创建MultiPoint类型
        Map<String,Class> columns3 = new HashMap<String, Class>();
        columns3.put("c1", Integer.class);
        columns3.put("c2", String.class);
        columns3.put("c3", Double.class);
        columns3.put("c4", Date.class);
        columns3.put("c5", Boolean.class);
        columns3.put("the_geom", MultiPoint.class);
        String result3 = GISDBUtil.CreateTableSchema("test4", dataStore, columns3);
        //创建线MultiLineString类型
        Map<String,Class> columns4 = new HashMap<String, Class>();
        columns4.put("c1", Integer.class);
        columns4.put("c2", String.class);
        columns4.put("c3", Double.class);
        columns4.put("c4", Date.class);
        columns4.put("c5", Boolean.class);
        columns4.put("the_geom", MultiLineString.class);
        String result4 = GISDBUtil.CreateTableSchema("test5", dataStore, columns4);
        //创建面MultiPolygon
        Map<String,Class> columns5 = new HashMap<String, Class>();
        columns5.put("c1", Integer.class);
        columns5.put("c2", String.class);
        columns5.put("c3", Double.class);
        columns5.put("c4", Date.class);
        columns5.put("c5", Boolean.class);
        columns5.put("the_geom", MultiPolygon.class);
        String result5 = GISDBUtil.CreateTableSchema("test6", dataStore, columns5);
        System.out.println(result3 +" >>> "+ result4 + " >>> "+ result5);
    }
    
    /**
     * 测试GISDBUtil--GetFeaturesByTableName方法
     */
    @Test
    public void testGetFeaturesByTableName(){
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "buscity", "postgis", "postgis");
        List<SimpleFeature> list = GISDBUtil.GetFeaturesByTableName(dataStore, "buslinelink");
        System.out.println(list.size());
    }
    
    /**
     * 测试GISDBUtil--GetFeaturesByTableName方法
     */
    @Test
    public void testGetAttributeByTableName(){
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "buscity", "postgis", "postgis");
        List<String> list = GISDBUtil.GetAttributeByTableName(dataStore, "buslinelink");
        PrintUtil.PrintObject(list);
    }
    
    @Test
    public void testWriteGeoJSONFile(){
        File file = new File("D:\\my.geojson");
        String shapePath = "G:\\项目文档\\手机信令\\gis数据\\zxcxzhzhydflqc.shp";
        SimpleFeatureCollection features = GeoShapeUtil.ReadShapeFileFeatures(shapePath, "UTF-8");
        ConversionUtil.WriteGeoJSONFile(features, file);
    }
    
    @Test
    public void testShapeToPostGis(){
        String shapePath = "G:\\项目文档\\节能减排\\gis\\data\\wgs1984\\traffic3.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "pollutionreduction", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "traffic3", MultiLineString.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testDBFToPostGis(){
        String dbfPath = "G:\\项目文档\\公交都市\\giss数据\\地图\\2014地图\\14S-G_beijing\\beijingshape\\PNamebeijing.dbf";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "sw_navigation", "postgis", "postgis");
        List<String> str = ConversionUtil.GetDBFAttributes(dbfPath, "GBK");
        ConversionUtil.DBFToPostGIS(dbfPath, "GBK", "pnamebeijing", dataStore);
        PrintUtil.PrintObject(str);
    }
}
