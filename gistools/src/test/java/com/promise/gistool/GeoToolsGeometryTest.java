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
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.promise.gistool.util.GeoToolsUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.gml2.GMLWriter;

/**  
 * 功能描述:GeoToolsGeometry测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月12日 上午11:47:51  
 */
public class GeoToolsGeometryTest {

    public GeoToolsGeometry gtg = new GeoToolsGeometry();
    
    @Test
    public void testGetXYByWkt(){
        String str = "MULTIPOLYGON(((116.034602615 40.4626854890001,116.034492188 40.4626025220001,116.034324938 40.462544345,116.034161115 40.4625366990001,116.033920114 40.4625315820001,116.033605745 40.4625521740001,116.033372318)))";
        List<String> result = gtg.getXYByWkt(str);
        PrintUtil.PrintObject(result);
    }
    
    @Test
    public void testCreatePointGeoJSON() throws Exception{
        String json = "{\"type\":\"Point\",\"coordinates\":[100.1,0.1]}";
        Point point = GeoToolsGeometry.createPointGeoJSON(json);
        System.out.println(point.toText());
        SimpleFeature sf = GeoToolsGeometry.JSONToFeature(json);
        String geoJSONTemp = GeoToolsGeometry.FeatureToJSON(sf);
        Point pointTemp = GeoToolsGeometry.createPointGeoJSON(geoJSONTemp);
        System.out.println(geoJSONTemp);
        System.out.println(pointTemp.getX() +"-----"+pointTemp.getY());
    }
    
    @Test
    public void testFeatureToJSON() throws Exception{
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures("G:\\项目文档\\公交都市\\giss数据\\自行车网点数据2015-3\\自行车网点1503_Project.shp", "GBK");
        SimpleFeatureIterator sfi = sfc.features();
        while(sfi.hasNext()){
            SimpleFeature sfTemp = sfi.next();
            String geoJSONTemp = GeoToolsGeometry.FeatureToJSON(sfTemp);
            System.out.println("before===="+geoJSONTemp);
            SimpleFeature sfNew = GeoToolsGeometry.JSONToFeature(geoJSONTemp);
            String geoJSONTemp1 = GeoToolsGeometry.FeatureToJSON(sfNew);
            System.out.println("after===="+geoJSONTemp1);
            Point mls = GeoToolsGeometry.createPointGeoJSON(geoJSONTemp1);
            
            System.out.println(mls.toText()+"----"+mls.getX());
            GMLWriter gmlw = new GMLWriter();
            System.out.println(gmlw.write(mls));
            break;
        }
    }
    
    @Test
    public void testRoateLineSegment() throws Exception{
        //st_geomfromgeojson ST_AsGeoJson
        String url = "jdbc:postgresql://localhost:5432/test_gis";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String querySQL = "select id,ST_AsGeoJson(the_geom) geojson from test_linestring where id='1'";
        String insertSQL = "insert into test_linestring (id,the_geom) values (?,st_geomfromtext(?,4326))";
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        ResultSet rs = statement.executeQuery(querySQL);
        while(rs.next()){
            String id = rs.getString(1);
            String geojson = rs.getString(2);
            LineString ls = GeoToolsGeometry.createLineGeoJSON(geojson);
            Coordinate[] arrTemp = ls.getCoordinates();
            Coordinate c1 = arrTemp[0];
            Coordinate c2 = arrTemp[1];
            double af = 300;
            double dis = 0.0006;
            Coordinate c3 = GeoToolsUtil.RoateLineSegment(c1, c2, af, dis);
            LineString ls1 = GeoToolsGeometry.createLine(new Coordinate[]{c1,c3});
            ps.setString(1, id+"_4");
            ps.setString(2, ls1.toText());
            ps.addBatch();
        }
        ps.executeBatch();
    }
}
