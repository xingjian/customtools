package com.promise.gistool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBCrawlerUtil;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PBMathUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月6日 下午1:38:32  
 */
public class HubeiDataTest {

    @Test
    public void importRhubeiShaple(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\Rhubei.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "Rhubei", MultiLineString.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testShapeToPostGISDHSJWuhan(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\Rwuhan.shp";
        String url = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\武汉tocc\\gisdata\\properties\\navigationline.properties");
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","navigationline_wuhan",mapping);
        Assert.assertEquals("success", result);
    }
    
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入导航数据(湖北)
     */
    @Test
    public void testShapeToPostGISDHSJHuBei(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\Rhubei.shp";
        String url = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\武汉tocc\\gisdata\\properties\\navigationline.properties");
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","navigationline",mapping);
        Assert.assertEquals("success", result);
    }
    
    @Test
    public void importBPhubeiShaple(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\BPhubei.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "BPhubei", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void importBNhubeiShaple(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\BNhubei.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "BNhubei", Point.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void importDhubeiShaple(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\Dhubei.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "Dhubei", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void importBLhubeiShaple(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\BLhubei.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "BLhubei", MultiLineString.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void importBPLhubeiDBF(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\BPLhubei.dbf";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.DBFToPostGIS(shapePath, "GBK","BPLhubei",dataStore);
        System.out.println(result);
    }
    
    @Test
    public void importBUPhubeiDBF(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\BUPhubei.dbf";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.DBFToPostGIS(shapePath, "GBK","BUPhubei",dataStore);
        System.out.println(result);
    }
    
    @Test
    public void importR_NamehubeiDBF(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\R_Namehubei.dbf";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.DBFToPostGIS(shapePath, "GBK","R_Namehubei",dataStore);
        System.out.println(result);
    }
    
    @Test
    public void importR_LNamehubeiDBF(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\R_LNamehubei.dbf";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.DBFToPostGIS(shapePath, "GBK","R_LNamehubei",dataStore);
        System.out.println(result);
    }
    
    @Test
    public void importFNamehubeiDBF(){
        String shapePath = "G:\\项目文档\\武汉tocc\\gisdata\\hubeishape\\FNamehubei.dbf";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "hubei", "postgis", "postgis");
        String result = ConversionUtil.DBFToPostGIS(shapePath, "GBK","FNamehubei",dataStore);
        System.out.println(result);
    }
    
    @Test
    public void checkWuHan() throws Exception{
        String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql1 = "select st_astext(the_geom) from wuhanalltemp t1";
        String sql2 = "select id,st_astext(the_geom) from navigationline_wuhan";
        ResultSet rs0 = connection.createStatement().executeQuery(sql1);
        rs0.next();
        String wktStrAll = rs0.getString(1);
        Geometry geomALL = GeoToolsGeometry.createGeometrtyByWKT(wktStrAll);
        ResultSet rs1 = connection.createStatement().executeQuery(sql2);
        String updateSQL = "update navigationline_wuhan set isvalid=? where id=?";
        PreparedStatement ps = connection.prepareStatement(updateSQL);
        int count = 0;
        while(rs1.next()){
            String id = rs1.getString(1);
            String wktStrSub = rs1.getString(2);
            Geometry geomsub = GeoToolsGeometry.createGeometrtyByWKT(wktStrSub);
            if(GeoToolsGeometry.isContains(geomALL, geomsub)||GeoToolsGeometry.isIntersects(geomALL, geomsub)){
                ps.setString(1, "1");
                ps.setString(2, id);
                ps.addBatch();
                count++;
                if(count%5000==0){
                   ps.executeBatch();
                   System.out.println("commit count = "+count);
                }
            }
        }
        ps.executeBatch();
    }
    
    
    @Test
    public void randomRoadStatusWuHan() throws Exception{
        String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String sql2 = "select id from navigationline_wuhan";
        ResultSet rs1 = connection.createStatement().executeQuery(sql2);
        String updateSQL = "update navigationline_wuhan set state=? where id=?";
        PreparedStatement ps = connection.prepareStatement(updateSQL);
        int count = 0;
        while(rs1.next()){
            String id = rs1.getString(1);
            ps.setString(1, PBMathUtil.GetRandomInt(1, 6)+"");
            ps.setString(2, id);
            ps.addBatch();
            count++;
            if(count%5000==0){
                ps.executeBatch();
                System.out.println(count);
            }
        }
        ps.executeBatch();
    }
    
    
    @Test
    public void testGeneratorChannel() throws Exception{
        String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String insertsql = "insert into wh_channel (id,name,the_geom) values (?,?,st_geometryfromtext(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insertsql);
        String url = "http://ditu.amap.com/service/poiInfo?query_type=TQUERY&city=420100&keywords=%E9%9B%84%E6%A5%9A%E5%A4%A7%E9%81%93&pagesize=20&pagenum=1&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&div=PC1000&addr_poi_merge=true&is_classify=true&geoobj=114.287096%7C30.490493%7C114.45189%7C30.527615";
        String result = PBCrawlerUtil.GetByString(url);
        JSONObject jb = JSONObject.fromObject(result);
        JSONArray ja = jb.getJSONArray("data");
        for(int i=0;i<ja.size();i++){
            JSONObject jsonTemp = JSONObject.fromObject(ja.get(i));
            if("polyline".equals(jsonTemp.get("type"))){
                JSONArray ja1 = jsonTemp.getJSONArray("list");
                LineString[] lsArr = new LineString[ja1.size()];
                for(int j=0;j<ja1.size();j++){
                    JSONObject jsonTemp1 = JSONObject.fromObject(ja1.get(j));
                    JSONArray ja2 = jsonTemp1.getJSONArray("path");
                    Coordinate[] coords = new Coordinate[ja2.size()];
                    for(int a=0;a<ja2.size();a++){
                        JSONObject jsonTemp2 = JSONObject.fromObject(ja2.get(a));
                        coords[a] = new Coordinate(Double.parseDouble(jsonTemp2.get("lng").toString()),Double.parseDouble(jsonTemp2.get("lat").toString()));
                    }
                    LineString lsTemp = GeoToolsGeometry.createLine(coords);
                    lsArr[j] = lsTemp;
                }
                MultiLineString msTemp = GeoToolsGeometry.createMLine(lsArr);
                ps.setString(1,StringUtil.GetUUIDString());
                ps.setString(2,"雄楚大道");
                ps.setString(3,msTemp.toText());
                ps.addBatch();
                ps.executeBatch();
            }
        }
    }
    
    @Test
    public void testGetBuslineJSON(){
        try {
            List<String> list1 = PBFileUtil.ReadCSVFile("G:\\项目文档\\武汉tocc\\gisdata\\t.csv", "utf-8");
            for(String str1:list1){
                String str2 = URLEncoder.encode(str1,"UTF-8");
                String url = "http://ditu.amap.com/service/poiInfo?query_type=TQUERY&city=420100&keywords="+str2+"&pagesize=20&pagenum=1&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&div=PC1000&addr_poi_merge=true&is_classify=true&geoobj=114.18943%7C30.470054%7C114.51902%7C30.544298";
                String result = PBCrawlerUtil.GetByString(url);
                PBFileUtil.WriteStringToTxt(result, "G:\\项目文档\\武汉tocc\\gisdata\\json\\"+str1+".txt");
                Thread.sleep(1000*10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testGeneratorBusLine(){
        try{
            String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
            String username = "postgis";
            String passwd = "postgis";
            Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
            String insertsql = "INSERT INTO wh_busline("+
               "id, busid, name, air, areacode, auto, basic_price, bounds, company, "+
                "end_time, front_name, front_spell, ic_card, key_name, length, "+
               " start_time, stime, terminal_name, terminal_spell, total_price, "+
               " the_geom) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, st_geometryfromtext(?,4326))";
            String insertBusstation = "INSERT INTO wh_busstation(id, code, x, y, name, index, the_geom,buslinecode,buslineid) VALUES (?, ?, ?, ?, ?, ?, st_geometryfromtext(?,4326),?,?)";
            PreparedStatement psBusStation = connection.prepareStatement(insertBusstation);
            PreparedStatement ps = connection.prepareStatement(insertsql);
            String url = "http://ditu.amap.com/service/poiInfo?query_type=TQUERY&city=420100&keywords=15%E8%B7%AF&pagesize=20&pagenum=1&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&div=PC1000&addr_poi_merge=true&is_classify=true&geoobj=114.18943%7C30.470054%7C114.51902%7C30.544298";
            String result = PBCrawlerUtil.GetByString(url);
            JSONObject jb = JSONObject.fromObject(result);
            
            JSONObject jb1 = JSONObject.fromObject(jb.getString("busData"));
            Set<String> setKeys = jb1.keySet();
            Iterator<String> iterator = setKeys.iterator();
            while(iterator.hasNext()){
                String buslineCode = iterator.next();
                String buslineid = StringUtil.GetUUIDString();
                JSONArray ja1 = jb1.getJSONArray(buslineCode);
                for(int i=0;i<ja1.size();i++){
                    JSONObject jsonTemp = JSONObject.fromObject(ja1.get(i));
                    JSONArray ja2 = jsonTemp.getJSONArray("list");
                    if("polyline".equals(jsonTemp.get("type"))){
                        JSONObject jsonTemp4 = JSONObject.fromObject(ja2.get(0));
                        String frontName = jsonTemp4.getString("front_name");
                        String areacode = jsonTemp4.getString("areacode");
                        String name = jsonTemp4.getString("name");
                        String air = jsonTemp4.getString("air");
                        String auto = jsonTemp4.getString("auto");
                        String basic_price = jsonTemp4.getString("basic_price");
                        String bounds = jsonTemp4.getString("bounds");
                        String company = jsonTemp4.getString("company");
                        String end_time = jsonTemp4.getString("end_time");
                        String front_spell = jsonTemp4.getString("front_spell");
                        String ic_card = jsonTemp4.getString("ic_card");
                        String key_name = jsonTemp4.getString("key_name");
                        String length = jsonTemp4.getString("length");
                        String start_time = jsonTemp4.getString("start_time");
                        String stime = jsonTemp4.getString("stime");
                        String terminal_name = jsonTemp4.getString("terminal_name");
                        String terminal_spell = jsonTemp4.getString("terminal_spell");
                        String total_price = jsonTemp4.getString("total_price");
                        JSONArray ja3 = jsonTemp4.getJSONArray("path");
                        Coordinate[] coords = new Coordinate[ja3.size()];
                        for(int w=0;w<ja3.size();w++){
                             JSONObject jsonTemp5 = JSONObject.fromObject(ja3.get(w));
                             coords[w] = new Coordinate(Double.parseDouble(jsonTemp5.get("lng").toString()),Double.parseDouble(jsonTemp5.get("lat").toString()));
                        }
                        LineString lsTemp = GeoToolsGeometry.createLine(coords);
                        LineString[] lsArr = new LineString[1];
                        lsArr[0] = lsTemp;
                        MultiLineString msTemp = GeoToolsGeometry.createMLine(lsArr);
                        ps.setString(1, buslineid);
                        ps.setString(2, buslineCode);
                        ps.setString(3, name);
                        ps.setString(4, air);
                        ps.setString(5, areacode);
                        ps.setString(6, auto);
                        ps.setDouble(7, Double.parseDouble(basic_price));
                        ps.setString(8, bounds);
                        ps.setString(9, company);
                        ps.setString(10, end_time);
                        ps.setString(11, frontName);
                        ps.setString(12, front_spell);
                        ps.setString(13, ic_card);
                        ps.setString(14, key_name);
                        ps.setDouble(15, Double.parseDouble(length));
                        ps.setString(16, start_time);
                        ps.setString(17, stime);
                        ps.setString(18, terminal_name);
                        ps.setString(19, terminal_spell);
                        ps.setDouble(20, Double.parseDouble(total_price));
                        ps.setString(21, msTemp.toText());
                        ps.addBatch();
                        ps.executeBatch();
                    }else if("marker".equals(jsonTemp.get("type"))){
                       for(int m=0;m<ja2.size();m++){
                           JSONObject jsonTemp2 = JSONObject.fromObject(ja2.get(m));
                           String idbusstation = StringUtil.GetUUIDString();
                           String code = jsonTemp2.get("id")+"";
                           String name = jsonTemp2.get("name")+"";
                           int sequence = Integer.parseInt(jsonTemp2.get("sequence")+"");
                           JSONObject jsonTemp3 = JSONObject.fromObject(jsonTemp2.get("location"));
                           String lat = jsonTemp3.get("lat")+"";
                           String lng = jsonTemp3.get("lng")+"";
                           psBusStation.setString(1, idbusstation);
                           psBusStation.setString(2, code);
                           psBusStation.setDouble(3, Double.parseDouble(lng));
                           psBusStation.setDouble(4, Double.parseDouble(lat));
                           psBusStation.setString(5, name);
                           psBusStation.setInt(6, sequence);
                           String wktPoint = GeoToolsGeometry.createPoint(Double.parseDouble(lng), Double.parseDouble(lat)).toText();
                           psBusStation.setString(7, wktPoint);
                           psBusStation.setString(8, buslineCode);
                           psBusStation.setString(9, buslineid);
                           psBusStation.addBatch();
                       }
                      psBusStation.executeBatch();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    @Test
    public void saveGPSData(){
        try{
            String urlttyj = "jdbc:postgresql://192.168.1.101:5432/hubei";
            String username = "postgis";
            String passwd = "postgis";
            Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
            String filePath = "G:\\项目文档\\武汉tocc\\gisdata\\WHTaxiDataPart1\\gpstxt\\d01w300.txt";
            String insertSQL = "insert into gpsdata (column1,dtimestr,column2,column3,x1,y1,x2,y2,column4) values (?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(insertSQL);
            List<String> list = PBFileUtil.ReadFileByLine(filePath);
            int countIndex = 0;
            for(String s:list){
                String[] sArr = s.split(",");
                String c1 = sArr[0];
                String dtimestr = sArr[1];
                String c2 = sArr[3];
                String c3 = sArr[4];
                double x1 = Double.parseDouble(sArr[15]);
                double y1 = Double.parseDouble(sArr[16]);
                double x2 = Double.parseDouble(sArr[17]);
                double y2 = Double.parseDouble(sArr[18]);
                String c4 = sArr[18];
                ps.setString(1, c1);
                ps.setString(2, dtimestr);
                ps.setString(3, c2);
                ps.setString(4, c3);
                ps.setDouble(5, x1);
                ps.setDouble(6, y1);
                ps.setDouble(7, x2);
                ps.setDouble(8, y2);
                ps.setString(9, c4);
                ps.addBatch();
                countIndex++;
                if(countIndex%5000==0){
                    ps.executeBatch();
                    System.out.println(countIndex);
                }
            }
            ps.executeBatch();
            System.out.println(countIndex);
        }catch(Exception e){
            e.printStackTrace();
        }
       
    }
    
    
    @Test
    public void testGeneratorBusLineJSON(){
        try{
            String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
            String username = "postgis";
            String passwd = "postgis";
            Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
            String insertsql = "INSERT INTO wh_busline_new("+
               "id, busid, name, air, areacode, auto, basic_price, bounds, company, "+
                "end_time, front_name, front_spell, ic_card, key_name, length, "+
               " start_time, stime, terminal_name, terminal_spell, total_price, "+
               " the_geom) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, st_geometryfromtext(?,4326))";
            String insertBusstation = "INSERT INTO wh_busstation_new(id, code, x, y, name, index, the_geom,buslinecode,buslineid) VALUES (?, ?, ?, ?, ?, ?, st_geometryfromtext(?,4326),?,?)";
            PreparedStatement psBusStation = connection.prepareStatement(insertBusstation);
            PreparedStatement ps = connection.prepareStatement(insertsql);
            
            List<String> list1 = PBFileUtil.ReadCSVFile("G:\\项目文档\\武汉tocc\\gisdata\\t.csv", "utf-8");
            for(String str1:list1){
                if(str1.equals("电车1路")||str1.equals("电车8")){
                    continue;
                }
                List<String> s = PBFileUtil.ReadFileByLine("G:\\项目文档\\武汉tocc\\gisdata\\json\\"+str1+".txt");
                JSONObject jb = JSONObject.fromObject(s.get(0));
                System.out.println(str1);
                JSONObject jb1 = JSONObject.fromObject(jb.getString("busData"));
                Set<String> setKeys = jb1.keySet();
                Iterator<String> iterator = setKeys.iterator();
                while(iterator.hasNext()){
                    String buslineCode = iterator.next();
                    String buslineid = StringUtil.GetUUIDString();
                    JSONArray ja1 = jb1.getJSONArray(buslineCode);
                    for(int i=0;i<ja1.size();i++){
                        JSONObject jsonTemp = JSONObject.fromObject(ja1.get(i));
                        JSONArray ja2 = jsonTemp.getJSONArray("list");
                        if("polyline".equals(jsonTemp.get("type"))){
                            JSONObject jsonTemp4 = JSONObject.fromObject(ja2.get(0));
                            String frontName = jsonTemp4.getString("front_name");
                            String areacode = jsonTemp4.getString("areacode");
                            String name = jsonTemp4.getString("name");
                            String air = jsonTemp4.getString("air");
                            String auto = jsonTemp4.getString("auto");
                            String basic_price = jsonTemp4.getString("basic_price");
                            String bounds = jsonTemp4.getString("bounds");
                            String company = jsonTemp4.getString("company");
                            String end_time = jsonTemp4.getString("end_time");
                            String front_spell = jsonTemp4.getString("front_spell");
                            String ic_card = jsonTemp4.getString("ic_card");
                            String key_name = jsonTemp4.getString("key_name");
                            String length = jsonTemp4.getString("length");
                            String start_time = jsonTemp4.getString("start_time");
                            String stime = jsonTemp4.getString("stime");
                            String terminal_name = jsonTemp4.getString("terminal_name");
                            String terminal_spell = jsonTemp4.getString("terminal_spell");
                            String total_price = jsonTemp4.getString("total_price");
                            JSONArray ja3 = jsonTemp4.getJSONArray("path");
                            Coordinate[] coords = new Coordinate[ja3.size()];
                            for(int w=0;w<ja3.size();w++){
                                 JSONObject jsonTemp5 = JSONObject.fromObject(ja3.get(w));
                                 coords[w] = new Coordinate(Double.parseDouble(jsonTemp5.get("lng").toString()),Double.parseDouble(jsonTemp5.get("lat").toString()));
                            }
                            LineString lsTemp = GeoToolsGeometry.createLine(coords);
                            LineString[] lsArr = new LineString[1];
                            lsArr[0] = lsTemp;
                            MultiLineString msTemp = GeoToolsGeometry.createMLine(lsArr);
                            ps.setString(1, buslineid);
                            ps.setString(2, buslineCode);
                            ps.setString(3, name);
                            ps.setString(4, air);
                            ps.setString(5, areacode);
                            ps.setString(6, auto);
                            if(null==basic_price||basic_price.trim().equals("")){
                                ps.setDouble(7, 0.0);
                            }else{
                                ps.setDouble(7, Double.parseDouble(basic_price));
                            }
                            
                            ps.setString(8, bounds);
                            ps.setString(9, company);
                            ps.setString(10, end_time);
                            ps.setString(11, frontName);
                            ps.setString(12, front_spell);
                            ps.setString(13, ic_card);
                            ps.setString(14, key_name);
                            ps.setDouble(15, Double.parseDouble(length));
                            ps.setString(16, start_time);
                            ps.setString(17, stime);
                            ps.setString(18, terminal_name);
                            ps.setString(19, terminal_spell);
                            if(null==total_price||total_price.trim().equals("")){
                                ps.setDouble(20, 0.0);
                            }else{
                                ps.setDouble(20, Double.parseDouble(total_price));
                            }
                            
                            ps.setString(21, msTemp.toText());
                            ps.addBatch();
                            ps.executeBatch();
                        }else if("marker".equals(jsonTemp.get("type"))){
                           for(int m=0;m<ja2.size();m++){
                               JSONObject jsonTemp2 = JSONObject.fromObject(ja2.get(m));
                               String idbusstation = StringUtil.GetUUIDString();
                               String code = jsonTemp2.get("id")+"";
                               String name = jsonTemp2.get("name")+"";
                               int sequence = Integer.parseInt(jsonTemp2.get("sequence")+"");
                               JSONObject jsonTemp3 = JSONObject.fromObject(jsonTemp2.get("location"));
                               String lat = jsonTemp3.get("lat")+"";
                               String lng = jsonTemp3.get("lng")+"";
                               psBusStation.setString(1, idbusstation);
                               psBusStation.setString(2, code);
                               psBusStation.setDouble(3, Double.parseDouble(lng));
                               psBusStation.setDouble(4, Double.parseDouble(lat));
                               psBusStation.setString(5, name);
                               psBusStation.setInt(6, sequence);
                               String wktPoint = GeoToolsGeometry.createPoint(Double.parseDouble(lng), Double.parseDouble(lat)).toText();
                               psBusStation.setString(7, wktPoint);
                               psBusStation.setString(8, buslineCode);
                               psBusStation.setString(9, buslineid);
                               psBusStation.addBatch();
                           }
                          psBusStation.executeBatch();
                        }
                    }
                }
            }
            
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    @Test
    public void testGetKeLiu() throws Exception{
        String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        List<String> list = PBFileUtil.ReadFileByLine("D:\\ss.txt");
        Statement state = connection.createStatement();
        int count = 0;
        for(String s:list){
            String sql1 = s.replace("BUSCITYNEW.", "").toLowerCase();
            state.addBatch(sql1);
            count++;
            if(count%5000==0){
                state.executeBatch();
                System.out.println(count);
            }
        }
        state.executeBatch();
        System.out.println(count);
    }
    
    @Test
    public void testGetKeLiuNew() throws Exception{
        String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        ResultSet rs1 = connection.createStatement().executeQuery("select name from wh_busline_new");
        List<String> listName = new ArrayList<String>();
        while(rs1.next()){
            String name  = rs1.getString(1);
            listName.add(name);
        }
        rs1.close();
        String sql = "select scope from busline_keliu group by scope limit 760";
        String update = "update busline_keliu set flag='1' , scope=? where scope=?";
        PreparedStatement psupdate = connection.prepareStatement(update);
        Statement state = connection.createStatement();
        ResultSet rs = state.executeQuery(sql);
        int count1 = 0;
        while(rs.next()){
            
            psupdate.setString(1, listName.get(count1));
            psupdate.setString(2, rs.getString(1));
            count1++;
            psupdate.addBatch();
            if(count1%20==0){
                psupdate.executeBatch(); 
                System.out.println(count1);
            }
        }
        psupdate.executeBatch(); 
        System.out.println(count1);
    }
    
    @Test
    public void testCopybusline_keliuToSQLSSERVER() throws Exception{
        String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        
        String urlSQL = "jdbc:sqlserver://192.168.1.101:1433;databaseName=toccdb";
        String usernamesql = "toccdb";
        String passwdsql = "toccdb";
        
        String sqlQuery = "select scope,dat,value from busline_keliu";
        String insertSQL = "insert into busline_keliu (buslinename,dat,value) values (?,?,?)";
        
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        Connection connectionsqlserver = DBConnection.getSQLServerConnection(urlSQL, usernamesql, passwdsql);
        PreparedStatement ps = connectionsqlserver.prepareStatement(insertSQL);
        ResultSet rs1 = connection.createStatement().executeQuery(sqlQuery);
        int count = 0;
        while(rs1.next()){
            count++;
            ps.setString(1, rs1.getString(1));
            ps.setString(2, rs1.getString(2));
            ps.setInt(3,rs1.getInt(3));
            ps.addBatch();
            if(count%5000==0){
               ps.executeBatch(); 
               System.out.println(count);
            }
        }
        ps.executeBatch(); 
        System.out.println(count);
    }
    
    
    @Test
    public void testCopybusline_keliuToSQLSSERVER3() throws Exception{
        String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        
        String urlSQL = "jdbc:sqlserver://192.168.1.104:1433;databaseName=toccdb";
        String usernamesql = "toccdb";
        String passwdsql = "toccdb";
        
        String sqlQuery = "select id, busid, name, air, areacode, auto, basic_price, bounds, company, end_time, front_name, front_spell, ic_card, key_name, length, start_time, stime, terminal_name, terminal_spell, total_price from wh_busline_new";
        String insertSQL = "insert into wh_busline (id, busid, name, air, areacode, auto, basic_price, bounds, company, end_time, front_name, front_spell, ic_card, key_name, length, start_time, stime, terminal_name, terminal_spell, total_price) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        Connection connectionsqlserver = DBConnection.getSQLServerConnection(urlSQL, usernamesql, passwdsql);
        PreparedStatement ps = connectionsqlserver.prepareStatement(insertSQL);
        ResultSet rs1 = connection.createStatement().executeQuery(sqlQuery);
        int count = 0;
        while(rs1.next()){
            count++;
            ps.setString(1, rs1.getString(1));
            ps.setString(2, rs1.getString(2));
            ps.setString(3,rs1.getString(3));
            ps.setString(4, rs1.getString(4));
            ps.setString(5, rs1.getString(5));
            ps.setString(6, rs1.getString(6));
            ps.setFloat(7, Float.parseFloat(rs1.getString(7)));
            ps.setString(8, rs1.getString(8));
            ps.setString(9, rs1.getString(9));
            ps.setString(10, rs1.getString(10));
            ps.setString(11, rs1.getString(11));
            ps.setString(12, rs1.getString(12));
            ps.setString(13, rs1.getString(13));
            ps.setString(14, rs1.getString(14));
            ps.setFloat(15, Float.parseFloat(rs1.getString(15)));
            ps.setString(16, rs1.getString(16));
            ps.setString(17, rs1.getString(17));
            ps.setString(18, rs1.getString(18));
            ps.setString(19, rs1.getString(19));
            ps.setFloat(20, Float.parseFloat(rs1.getString(20)));
            ps.addBatch();
        }
        ps.executeBatch(); 
        System.out.println(count);
    }
    
    
    @Test
    public void testCopybusline_keliuToSQLSSERVER1() throws Exception{
        String urlSQL = "jdbc:sqlserver://192.168.1.104:1433;databaseName=toccdb";
        String usernamesql = "toccdb";
        String passwdsql = "toccdb";
        String insertSQL = "insert into taxi_cash_tmp4 (sumyunying,sumshouru,summil,day,carnum,avgyunying,avgshouru,avgmil,sumtime,avgtime,type) values (?,?,?,?,?,?,?,?,?,?,?)";
        Connection connectionsqlserver = DBConnection.getSQLServerConnection(urlSQL, usernamesql, passwdsql);
        PreparedStatement ps = connectionsqlserver.prepareStatement(insertSQL);
        
        int count = 0;
        List<String> list = PBFileUtil.ReadFileByLine("D:\\333.txt");
        for(String str:list){
            String[] arr = str.split(",");
            count++;
            ps.setInt(1, Integer.parseInt(arr[0]));
            ps.setFloat(2, Float.parseFloat(arr[1]));
            ps.setFloat(3,Float.parseFloat(arr[2]));
            ps.setString(4, arr[3]);
            ps.setFloat(5, Float.parseFloat(arr[4]));
            ps.setFloat(6,Float.parseFloat(arr[5].substring(0, 8)));
            ps.setFloat(7, Float.parseFloat(arr[6].substring(0, 8)));
            ps.setFloat(8, Float.parseFloat(arr[7].substring(0, 8)));
            ps.setFloat(9,Float.parseFloat(arr[8].substring(0, 13)));
            ps.setFloat(10, Float.parseFloat(arr[9].substring(0, 8)));
            ps.setString(11, arr[10]);
            ps.addBatch();
        }
        System.out.println(count);
        ps.executeBatch(); 
    }
    
    @Test
    public void testRandomJCQ() throws Exception{
        String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        String query = "select id from navigationline_wuhan where version='1'";
        String update = "update navigationline_wuhan set basename=? where id=?";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        PreparedStatement ps = connection.prepareStatement(update);
        ResultSet rs1 = connection.createStatement().executeQuery(query);
        int jcqcount;
        int count = 0;
        while(rs1.next()){
            String id = rs1.getString(1);
            count++;
            if(count%7==0){
                ps.setString(1, "jcq");
                ps.setString(2, id);
                ps.addBatch();
            }
        }
        ps.executeBatch();
    }
    
    @Test
    public void testUpdateName() throws Exception{
        String sql1 = "select id,idlink from navigationline_wuhan";
        String sql2 = "select \"ID\",\"Route_ID\" from \"R_LNamehubei\"";
        String sql3 = "select \"Route_ID\",\"PathName\" from \"R_Namehubei\" where \"Language\"='1'";
        String sql4 = "update navigationline_wuhan set pathname=? where idlink=?";
        String urlttyj = "jdbc:postgresql://localhost:5432/hubei";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        ResultSet rs1 = connection.createStatement().executeQuery(sql1);
        ResultSet rs2 = connection.createStatement().executeQuery(sql2);
        ResultSet rs3 = connection.createStatement().executeQuery(sql3);
        PreparedStatement ps = connection.prepareStatement(sql4);
        Map<String,String> map1 = new HashMap<String,String>();
        Map<String,String> map2 = new HashMap<String,String>();
        Map<String,String> map3 = new HashMap<String,String>();
        while(rs1.next()){
            map1.put(rs1.getString(2), rs1.getString(1));
        }
        rs1.close();
        while(rs2.next()){
            map2.put(rs2.getString(1), rs2.getString(2));
        }
        rs2.close();
        while(rs3.next()){
            map3.put(rs3.getString(1), rs3.getString(2));
        }
        rs3.close();
        int count = 0;
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            String keyStr = entry.getKey();
            String valueStr = entry.getValue();
            String pathName = map3.get(map2.get(keyStr));
            if(null!=pathName){
                ps.setString(1, pathName);
                ps.setString(2, keyStr);
                ps.addBatch();
            }
            count++;
            if(count%5000==0){
                ps.executeBatch();
            }
        }
        ps.executeBatch();
        System.out.println(count);
    }
    
    
    @Test
    public void saveGPSData2Road(){
        try{
            String urlttyj = "jdbc:postgresql://192.168.1.104:5432/hubei";
            String username = "postgis";
            String passwd = "postgis";
            Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
            String filePath = "G:\\项目文档\\武汉tocc\\gisdata\\WHTaxiDataPart1\\gpstxt\\d01w324.txt";
            String insertSQL = "insert into gpsdata (column1,dtimestr,column2,column3,x1,y1,x2,y2,column4) values (?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(insertSQL);
            List<String> list = PBFileUtil.ReadFileByLine(filePath);
            int countIndex = 0;
            for(String s:list){
                if(null==s||s.trim().equals("")){
                    continue;
                }
                String[] sArr = s.split(",");
                String c1 = sArr[0];
                String dtimestr = sArr[1];
                if(sArr.length<5){
                    continue;
                }
                
                String c2 = sArr[3];
               
                String c3 = sArr[4];
                if(c3.equals("051113110800")||c3.equals("051113111903")){
                    double x1 = Double.parseDouble(sArr[15]);
                    double y1 = Double.parseDouble(sArr[16]);
                    double x2 = Double.parseDouble(sArr[17]);
                    double y2 = Double.parseDouble(sArr[18]);
                    String c4 = sArr[18];
                    ps.setString(1, c1);
                    ps.setString(2, dtimestr);
                    ps.setString(3, c2);
                    ps.setString(4, c3);
                    ps.setDouble(5, x1);
                    ps.setDouble(6, y1);
                    ps.setDouble(7, x2);
                    ps.setDouble(8, y2);
                    ps.setString(9, c4);
                    ps.addBatch();
                    countIndex++;
                    if(countIndex%5000==0){
                        ps.executeBatch();
                        System.out.println(countIndex);
                    }
                }
            }
            ps.executeBatch();
            System.out.println(countIndex);
        }catch(Exception e){
            e.printStackTrace();
        }
       
    }
    
    @Test
    public void saveGPSData2Road4(){
        try{
            String hhs = "00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23";
            String filePath = "G:\\项目文档\\武汉tocc\\gisdata\\WHTaxiDataPart1\\gpstxt\\d01w3";
            String[] arr = hhs.split(",");
            List<String> list1 = new ArrayList<String>();
            List<String> list2 = new ArrayList<String>();
            for(int i=0;i<arr.length;i++){
                String subStr = arr[i];
                String pathfile = filePath+subStr+".txt";
                System.out.println(pathfile);
                List<String> list = PBFileUtil.ReadFileByLine(pathfile);
                for(String s:list){
                    if(null==s||s.trim().equals("")){
                        continue;
                    }
                    String[] sArr = s.split(",");
                    if(sArr.length<5){
                        continue;
                    }
                    
                    String c3 = sArr[4];
                    if(c3.equals("051113110800")){
                        list1.add(s);
                    }else if(c3.equals("051113111903")){
                        list2.add(s);
                    }
                }
            }
            PBFileUtil.WriteListToTxt(list1, "d:\\051113110800.txt",true);
            PBFileUtil.WriteListToTxt(list2, "d:\\051113111903.txt",true);
            
        }catch(Exception e){
            e.printStackTrace();
        }
       
    }
    
    
    @Test
    public void saveGPSData2Road5(){
        try{
            DecimalFormat df = new DecimalFormat( "0.000 ");
            DecimalFormat df1 = new DecimalFormat( "0.0000 ");
            String hhs = "00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23";
            String filePath = "G:\\项目文档\\武汉tocc\\gisdata\\WHTaxiDataPart1\\gpstxt\\d01w3";
            String[] arr = hhs.split(",");
            List<String> list1 = new ArrayList<String>();
            Map<String,String> map = new HashMap<String,String>();
            for(int i=0;i<arr.length;i++){
                String subStr = arr[i];
                String pathfile = filePath+subStr+".txt";
                System.out.println(pathfile);
                List<String> list = PBFileUtil.ReadFileByLine(pathfile);
                for(String s:list){
                    if(null==s||s.trim().equals("")){
                        continue;
                    }
                    String[] sArr = s.split(",");
                    if(sArr.length<21){
                        continue;
                    }
                    String c3 = sArr[4];
                    if(null==map.get(c3)){
                        if(sArr[17].length()<4){
                            continue;  
                        }
                        double x2 = Double.parseDouble(sArr[17].substring(0, 3)+"."+sArr[17].substring(3, sArr[17].length()));
                        double y2 = Double.parseDouble(sArr[18].substring(0, 2)+"."+sArr[18].substring(2, sArr[18].length()));
                        double[] dd = GISCoordinateTransform.From84To900913(x2, y2);
                        map.put(c3, s+PBMathUtil.DoubleRound(dd[0], 2)+","+PBMathUtil.DoubleRound(dd[1], 4));
                        list1.add(s+df.format(PBMathUtil.DoubleRound(dd[0], 2))+","+df1.format(PBMathUtil.DoubleRound(dd[1], 4)));
                    }
                    
                }
            }
            PBFileUtil.WriteListToTxt(list1, "d:\\taxi_ids.txt",true);
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
       
    }
    
    @Test
    public void testReCreateFile(){
        List<String> list = PBFileUtil.ReadFileByLine("G:\\项目文档\\出租车系统\\gpssocket\\d01w318.txt");
        List<String> listG = new ArrayList<String>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean status = true;
        for(String str:list){
            String[] sArr = str.split(",");
            if(sArr.length<21){
                continue;
            }
            String taxeCode = sArr[4];
            long ddd = (Long.parseLong(sArr[0]))+3600*24*180;
            String dtime = formatter.format(ddd*1000);
            double x2 = Double.parseDouble(sArr[17].substring(0, 3)+"."+sArr[17].substring(3, sArr[17].length()));
            double y2 = Double.parseDouble(sArr[18].substring(0, 2)+"."+sArr[18].substring(2, sArr[18].length()));
            String speed = sArr[10];
            String degree = sArr[9];
            String stat = "";
            status = !status;
            if(status){
                stat="1";
            }else{
                stat="0";
            }
            listG.add(taxeCode+","+dtime+","+x2+","+y2+","+speed+","+degree+","+stat);
        }
        PBFileUtil.WriteListToTxt(listG, "G:\\项目文档\\出租车系统\\gpssocket\\d01w318new.txt", true);
        
    }
    
    @Test
    public void testGenTaxiShape(){
        List<String> list = PBFileUtil.ReadFileByLine("d:\\taxi_ids.txt");
        List<TaxiGPS> listAll = new ArrayList<TaxiGPS>();
        for(String str:list){
            String[] arr = str.split(",");
            String id = arr[4];
            String positionti = StringUtil.GetDateString(null, null);
            double x2 = Double.parseDouble(arr[17].substring(0, 3)+"."+arr[17].substring(3, arr[17].length()));
            double y2 = Double.parseDouble(arr[18].substring(0, 2)+"."+arr[18].substring(2, arr[18].length()));
            TaxiGPS tt = new TaxiGPS();
            tt.setId(id);
            tt.setPositionti(positionti);
            tt.setCompany(GeoToolsGeometry.createPoint(x2, y2).toText());
            listAll.add(tt);
        }
        GeoShapeUtil.ListObjectToShapeFile(listAll,"G:\\项目文档\\武汉tocc\\gisdata\\gpsshape\\gps1.shp","GBK","1","company","EPSG:4326");
    }
    
    
    @Test
    public void testReCreateFile11(){
        List<String> list = PBFileUtil.ReadFileByLine("G:\\项目文档\\武汉tocc\\gisdata\\WHTaxiDataPart1\\gpstxt\\d01w318.txt");
        List<String> listG = new ArrayList<String>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean status = true;
        for(String str:list){
            String[] sArr = str.split(",");
            if(sArr.length<21){
                continue;
            }
            String taxeCode = sArr[4];
            long ddd = (Long.parseLong(sArr[0]))+3600*24*180;
            String dtime = formatter.format(ddd*1000);
            double x2 = Double.parseDouble(sArr[15].substring(0, 3)+"."+sArr[15].substring(3, sArr[15].length()));
            double y2 = Double.parseDouble(sArr[16].substring(0, 2)+"."+sArr[16].substring(2, sArr[16].length()));
            String speed = sArr[10];
            String degree = sArr[9];
            String stat = "";
            status = !status;
            if(status){
                stat="1";
            }else{
                stat="0";
            }
            listG.add(taxeCode+","+dtime+","+x2+","+y2+","+speed+","+degree+","+stat);
        }
        PBFileUtil.WriteListToTxt(listG, "G:\\项目文档\\出租车系统\\gpssocket\\d01w318new2.txt", true);
        
    }
    
    
    @Test
    public void bufferBusstation(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //1441036800000
        //1441036800
        Date currentTime = new Date(1441036800000L);
        System.out.println(formatter.format(currentTime));
        System.out.println(System.currentTimeMillis());
        DecimalFormat df = new DecimalFormat("0.00000");
        System.out.println(StringUtil.GetDateString("yyyy",1441036800000L));;
        double d = 1267333555555553.74414444444444444864d;
        BigDecimal bd = new BigDecimal(d);
        bd.setScale(5, RoundingMode.FLOOR);
        System.out.println(formatter.format(System.currentTimeMillis()));
    }
}
