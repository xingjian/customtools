package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsUtil;
import com.vividsolutions.jts.geom.Geometry;
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
}
