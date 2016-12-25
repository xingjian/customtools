package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Geometry;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月23日 下午5:05:58  
 */
public class RoadSectionTest {

    @Test
    public void testOracleToPostGIS() throws Exception{
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj", "buscity", "admin123ttyj7890uiop");
        String querySQL = "select uuid,unirowid,sname,ename,streetname,intime,json from park.roadsection";
        Statement statement = connectOracle.createStatement();
        System.out.println("adfasdfasdfasdf");
        String url = "jdbc:postgresql://localhost:5432/roadsection";
        String username = "postgis";
        String passwd = "postgis";
        Connection connectPostgres = DBConnection.GetPostGresConnection(url, username, passwd);
        String insertSQL = "insert into roadsection_cl (uuid,unirowid,sname,ename,streetname,intime,the_geom) values (?,?,?,?,?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connectPostgres.prepareStatement(insertSQL);
        
        ResultSet rs = statement.executeQuery(querySQL);
        while(rs.next()){
            String uuid = rs.getString(1);
            String unirowid = rs.getString(2);
            String sname = rs.getString(3);
            String ename = rs.getString(4);
            String streetname = rs.getString(5);
            String intime = rs.getString(6);
            String json = rs.getString(7);
            Geometry geom = GeoToolsGeometry.ArcgisJSONToGeometry(json);
            
            ps.setString(1, uuid);
            ps.setString(2, unirowid);
            ps.setString(3, sname);
            ps.setString(4, ename);
            ps.setString(5, streetname);
            ps.setString(6, intime);
            ps.setString(7, geom.toText());
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    @Test
    public void fileter900Roadsection() throws Exception{
        Map<String,String> map = new HashMap<String,String>();
        List<String> list = POIExcelUtil.ReadXLS("G:\\项目文档\\微波路段\\微波路段路段梳理表900.xls", "#", 2, 907, 1, 5);
        String url = "jdbc:postgresql://localhost:5432/roadsection";
        String username = "postgis";
        String passwd = "postgis";
        Connection connectPostgres = DBConnection.GetPostGresConnection(url, username, passwd);
        List<CLData> listResult = new ArrayList<CLData>();
        for(String s:list){
            String[] arr = s.split("#");
            String rowid = arr[4];
            String sql = "select ST_AsText(ST_Centroid(st_astext(the_geom))) point_wkt,ST_AsText(the_geom) line_wkt from roadsection_cl where unirowid='"+rowid+"' limit 1 ";
            ResultSet rs = connectPostgres.createStatement().executeQuery(sql);
            if(rs.next()){
                String wkt_point = rs.getString(1);
                String wkt_line = rs.getString(2);
                CLData clData = new CLData();
                clData.setCode(rowid);
                clData.setStartName(arr[1]);
                clData.setEndName(arr[2]);
                clData.setRing(arr[0]);
                clData.setRingType(arr[3]);
                clData.setPointWKT(wkt_point);
                clData.setLineWKT(wkt_line);
                listResult.add(clData);
                
            }else{
                System.out.println(rowid);
            }
            rs.close();
        }
        POIExcelUtil.ExportDataByList(listResult, "d:\\roadsection906.xls");
    }
    
    @Test
    public void testReadShapeFileFeatures() throws Exception{
        String shapePath = "G:\\项目文档\\TOCC\\gis\\bjroad_cityroad\\KSL.shp";
        List<String> attributes = ConversionUtil.GetShapeAttributes(shapePath, "GBK");
        PrintUtil.PrintObject(attributes);
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures(shapePath, "GB2312");
        SimpleFeatureIterator sfi = sfc.features();
        while(sfi.hasNext()){
            SimpleFeature sf = sfi.next();
            System.out.println(GeoToolsGeometry.FeatureToJSON(sf));
            int c = GeoToolsGeometry.getGeoJSONXYCount(GeoToolsGeometry.FeatureToJSON(sf));
            System.out.println(c);
            Geometry g1 = (Geometry)sf.getDefaultGeometry();
            String s1 = g1.toText();
            int c1 = GeoToolsGeometry.getWktXYCount(s1);
            System.out.println(c1);
            s1 = GISCoordinateTransform.From54To84(s1);
            System.out.println(s1);
            s1 = GISCoordinateTransform.From84To02(s1);
            System.out.println(s1);
        }
    }
    
    @Test
    public void testExportRoadsection() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/roadsection";
        String username = "postgis";
        String passwd = "postgis";
        String sql = "select uuid,unirowid,sname,ename,streetname ,ST_AsText(ST_Centroid(st_astext(the_geom))) point_wkt,ST_AsText(the_geom) line_wkt from roadsection_cl_point_bnk20161019";
        Connection connectPostgres = DBConnection.GetPostGresConnection(url, username, passwd);
        POIExcelUtil.ExportDataBySQL(sql, connectPostgres, "d:\\zh.xls");
    }
    
}
