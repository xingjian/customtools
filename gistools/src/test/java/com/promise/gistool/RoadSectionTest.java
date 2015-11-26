package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PrintUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月23日 下午5:05:58  
 */
public class RoadSectionTest {

    @Test
    public void testOracleToPostGIS() throws Exception{
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@182.92.183.85:1521:orcl", "park", "ADMIN123");
        String querySQL = "select uuid,unirowid,sname,ename,streetname,intime,json from roadsection";
        Statement statement = connectOracle.createStatement();
        
        String url = "jdbc:postgresql://localhost:5432/roadsection";
        String username = "postgis";
        String passwd = "postgis";
        Connection connectPostgres = DBConnection.GetPostGresConnection(url, username, passwd);
        String insertSQL = "insert into roadsection (uuid,unirowid,sname,ename,streetname,intime,the_geom) values (?,?,?,?,?,?,ST_GeomFromText(?,4326))";
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
            List<String> list = GeoToolsGeometry.getXYByGeoJSON(json);
            LineString[] lineString = new LineString[1];
            Coordinate[] coordinate = new Coordinate[list.size()];
            for(int i=0;i<list.size();i++){
                String str1 = list.get(i);
                String[] xyArr = str1.split(",");
                coordinate[i] = new Coordinate(Double.parseDouble(xyArr[0]), Double.parseDouble(xyArr[1]));
            }
            lineString[0] = GeoToolsGeometry.createLine(coordinate);
            MultiLineString  ms = GeoToolsGeometry.createMLine(lineString);
            ps.setString(1, uuid);
            ps.setString(2, unirowid);
            ps.setString(3, sname);
            ps.setString(4, ename);
            ps.setString(5, streetname);
            ps.setString(6, intime);
            ps.setString(7, ms.toText());
            ps.addBatch();
        }
        ps.executeBatch();
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
    
    
    
}
