package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.POIExcelUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GISDBUtil;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年1月14日 下午12:50:14  
 */
public class ParkDataTest {

    @Test
    public void testInsertTrafficSmallToPostGIS(){
        String shapePath = "G:\\项目文档\\停车场\\交通小区151019\\traffic_area_small_84.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "park", "basedata", "basedata");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "traffic_area_small_84", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testInsertBeijingqxToPostGIS(){
        String shapePath = "G:\\项目文档\\停车场\\行政区划\\beijingqx.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "park", "basedata", "basedata");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "beijingqx", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testUpdate_traffic_area_small_84() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/park";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@182.92.183.85:1521:orcl", "PARK", "ADMIN123");
        String queryPG = "select fid,st_asgeojson json from traffic_area_small_84_new";
        String updateOracle = "update traffic_area_small_84_new set st_asgeojs=? where fid=?";
        Statement statement = connection.createStatement();
        PreparedStatement ps = connectOracle.prepareStatement(updateOracle);
        ResultSet rs = statement.executeQuery(queryPG);
        while(rs.next()){
             String fid = rs.getString(1);
             String json = rs.getString(2);
             ps.setString(1, json);
             ps.setString(2, fid);
             ps.addBatch();
        }
        ps.executeBatch();
    }
    
    @Test
    public void testFrom54To02ShapeFile(){
        String inputShapeFile = "G:\\项目文档\\停车场\\080305\\080305_wz_rj.shp";
        String outputShapleFile = "G:\\项目文档\\停车场\\080305\\080305_wz_rj_02.shp";
        try {
           String result = GISCoordinateTransform.From54To02(inputShapeFile, outputShapleFile, "GBK");
           System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertTCWToPostGIS(){
        String shapePath = "G:\\项目文档\\停车场\\080305\\080305_hx_rj_02.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "park", "basedata", "basedata");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "080305_hx_rj_02", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    @Test
    public void testInsertBeijingqxPostGIS(){
        String shapePath = "G:\\项目文档\\停车场\\行政区划\\beijingqx.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "park", "basedata", "basedata");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "beijingqx_chouxi", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void exportBeiJingqxChouxi(){
        String url = "jdbc:postgresql://localhost:5432/park";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select name,st_astext(the_geom) wkt, st_asgeojson(the_geom) json from beijingqx_chouxi";
        String excelPath = "D:\\beijingqx_chouxi.xls";
        POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
}
