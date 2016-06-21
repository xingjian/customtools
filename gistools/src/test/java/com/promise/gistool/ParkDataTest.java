package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.data.DataStore;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.POIExcelUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.tongtu.nomap.core.transform.CoordinateConvert;
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
    //
    //select name,st_astext(the_geom) from beijingqx_new
    @Test
    public void exportBeiJingqxChouxi(){
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
//        String sql = "select name,st_astext(the_geom) wkt, st_asgeojson(the_geom) json from beijingqx_chouxi";
        String sql = "SELECT fid, \"TDLB\", \"LXMC\", \"LDMC\", \"NAME\", \"DLDJ\", \"LINK_FID\" FROM busline_channel_link_ref";
        String excelPath = "D:\\busline_channel_link_ref.xls";
        POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    @Test
    public void testInsertEmission1(){
        String shapePath = "D:\\排放通道路链关系\\busline_channel_emission.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "ttyjbj.ticp.net", "5432", "emission", "emission", "emission");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "busline_channel_link_ref", MultiLineString.class, "EPSG:4326");
        System.out.println(result);
    }
    
    
    @Test
    public void exportbuslinechannel(){
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select id,tdlb,lxmc,ldmc,name,dldj,arrow from busline_channel";
        String excelPath = "D:\\busline_channel.xls";
        POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    @Test
    public void copyParkOracleToPostgis_PARK_POINT_NEW() throws Exception{
        String pgurl = "jdbc:postgresql://10.212.140.210:5432/park_gis";
        String pgusername = "postgres";
        String pgpasswd = "postgres";
        String urlOracle = "jdbc:oracle:thin:@10.212.140.210:1521:parking";
        String usernameOracle = "roadside_parking";
        String passwdOracle = "roadside_parking";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
        Connection connectionOLE = DBConnection.GetPostGresConnection(urlOracle, usernameOracle, passwdOracle);
        String sqlQuery_Oracle = "select x, y, rownum_, id_, district, roadname, roadid, roadsecnam, roadsecid, roaddirect, parkspids, parkspide, sht_parkspnums, jgj_parkspnums,  gis_parkspnums, sht, jgj, gis, memo from PARK_POINT_NEW";
        String insert_pg = "INSERT INTO park_roadsection( x, y, rownum_, district, roadname, roadid, roadsecnam, roadsecid, roaddirect, parkspids, parkspide, sht_parkspnums, jgj_parkspnums,  gis_parkspnums, sht, jgj, gis, memo,id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?)";
        ResultSet rs_oracle = connectionOLE.createStatement().executeQuery(sqlQuery_Oracle);
        PreparedStatement ps_pg = connectionPG.prepareStatement(insert_pg);
        while(rs_oracle.next()){
            double x = rs_oracle.getDouble("x");
            double y = rs_oracle.getDouble("y");
            int rownum = rs_oracle.getInt("rownum_");
            String district = rs_oracle.getString("district");
            String roadname = rs_oracle.getString("roadname");
            int roadid = rs_oracle.getInt("roadid");
            String roadsecnam = rs_oracle.getString("roadsecnam");
            int roadsecid = rs_oracle.getInt("roadsecid");
            String roaddirect = rs_oracle.getString("roaddirect");
            String parkspids = rs_oracle.getString("parkspids");
            String parkspide = rs_oracle.getString("parkspide");
            int sht_parkspnums = rs_oracle.getInt("sht_parkspnums");
            int jgj_parkspnums = rs_oracle.getInt("jgj_parkspnums");
            int gis_parkspnums = rs_oracle.getInt("gis_parkspnums");
            int sht = rs_oracle.getInt("sht");
            int jgj = rs_oracle.getInt("jgj");
            int gis = rs_oracle.getInt("gis");
            String memo = rs_oracle.getString("memo");
            String id = rs_oracle.getString("id_");
            ps_pg.setDouble(1, x);
            ps_pg.setDouble(2, y);
            ps_pg.setInt(3,rownum);
            ps_pg.setString(4,district);
            ps_pg.setString(5,roadname);
            ps_pg.setInt(6,roadid);
            ps_pg.setString(7,roadsecnam);
            ps_pg.setInt(8,roadsecid);
            ps_pg.setString(9,roaddirect);
            ps_pg.setString(10,parkspids);
            ps_pg.setString(11,parkspide);
            ps_pg.setInt(12,sht_parkspnums);
            ps_pg.setInt(13,jgj_parkspnums);
            ps_pg.setInt(14,gis_parkspnums);
            ps_pg.setInt(15,sht);
            ps_pg.setInt(16,jgj);
            ps_pg.setInt(17,gis);
            ps_pg.setString(18,memo);
            ps_pg.setString(19,id);
            ps_pg.addBatch();
        }
        ps_pg.executeBatch();
    }
    
    
    @Test
    public void copyParkOracleToPostgis_PARKSPACE_POINT() throws Exception{
        String pgurl = "jdbc:postgresql://10.212.140.210:5432/park_gis";
        String pgusername = "postgres";
        String pgpasswd = "postgres";
        String urlOracle = "jdbc:oracle:thin:@10.212.140.210:1521:parking";
        String usernameOracle = "roadside_parking";
        String passwdOracle = "roadside_parking";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
        Connection connectionOLE = DBConnection.GetPostGresConnection(urlOracle, usernameOracle, passwdOracle);
        String sqlQuery_Oracle = "select x, y,id_,parkid,frontflag,backflag,type  from PARKSPACE_POINT";
        String insert_pg = "INSERT INTO park_roadsection_point( x, y, id, parkid, frontflag,backflag,type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        ResultSet rs_oracle = connectionOLE.createStatement().executeQuery(sqlQuery_Oracle);
        PreparedStatement ps_pg = connectionPG.prepareStatement(insert_pg);
        while(rs_oracle.next()){
            double x = rs_oracle.getDouble("x");
            double y = rs_oracle.getDouble("y");
            String id = rs_oracle.getString("id_");
            String parkspid = rs_oracle.getString("parkid");
            int frontflag = rs_oracle.getInt("frontflag");
            int backflag = rs_oracle.getInt("backflag");
            int type = rs_oracle.getInt("type");
            ps_pg.setDouble(1, x);
            ps_pg.setDouble(2, y);
            ps_pg.setString(3,id);
            ps_pg.setString(4,parkspid);
            ps_pg.setInt(5,frontflag);
            ps_pg.setInt(6,backflag);
            ps_pg.setInt(7,type);
            ps_pg.addBatch();
        }
        ps_pg.executeBatch();
    }
    
    @Test
    public void generatorParkPoint_PARKSPACE_POINT() throws Exception{
        String pgurl = "jdbc:postgresql://10.212.140.210:5432/park_gis";
        String pgusername = "postgres";
        String pgpasswd = "postgres";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
        String sql = "select x, y, id, parkid,type from park_roadsection_point";
        String sql2 = "update park_roadsection_point set the_geom=st_geometryfromtext(?,4326) where id=?";
        ResultSet rs = connectionPG.createStatement().executeQuery(sql);
        PreparedStatement ps = connectionPG.prepareStatement(sql2);
        while(rs.next()){
            double x = rs.getDouble(1);
            double y = rs.getDouble(2);
            String id = rs.getString(3);
            int type = rs.getInt(5);
            Point p = null;
            if(type==1){//需要坐标转换
                double[] dArr1 = GISCoordinateTransform.From84To02(x,y);
                p = GeoToolsGeometry.createPoint(dArr1[0], dArr1[1]);
            }else{
                p = GeoToolsGeometry.createPoint(x, y);
            }
            
            String wkt = p.toText();
            ps.setString(1, wkt);
            ps.setString(2, id);
            ps.addBatch();
        }
        ps.executeBatch();
        
    }
    
    /**
     * 根据停车场的点连成线
     * @throws Exception
     */
    @Test
    public void generatorParkPoint_Park_Roadsection() throws Exception{
        String pgurl = "jdbc:postgresql://10.212.140.210:5432/park_gis";
        String pgusername = "postgres";
        String pgpasswd = "postgres";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
        String sql2 = "select id from park_roadsection ";
        ResultSet rs2 = connectionPG.createStatement().executeQuery(sql2);
        String sql3 = "update park_roadsection set the_geom=st_geometryfromtext(?,4326) where id=?";
        PreparedStatement ps = connectionPG.prepareStatement(sql3);
        while(rs2.next()){
            String parkid = rs2.getString(1);
            String sql = "select id,st_x(the_geom),st_y(the_geom) from park_roadsection_point where parkid='"+parkid+"' order by parkid,id";
            ResultSet rs = connectionPG.createStatement().executeQuery(sql);
            List<Coordinate> list = new ArrayList<Coordinate>();
            int index = 0;
            
            while(rs.next()){
                String id = rs.getString(1);
                double x = rs.getDouble(2);
                double y = rs.getDouble(3);
                list.add(index, GeoToolsGeometry.coordinate(x, y));
                index++;
            }
            Coordinate[] arrayCoor = new Coordinate[list.size()]; 
            for(int i=0;i<list.size();i++){
                arrayCoor[i] = list.get(i);
            }
            LineString lineString = GeoToolsGeometry.createLine(arrayCoor);
            MultiLineString mLine = GeoToolsGeometry.createMLine(new LineString[]{lineString});
            ps.setString(1, mLine.toText());
            ps.setString(2,parkid);
            ps.addBatch();
            rs.close();
        }
        ps.executeBatch();
    }
    
    @Test
    public void exportXY() throws Exception{
        String sql = "select t1.id,t1.code,t1.park_code,t2.name ,t1.json_geom,t1.data_time from park_places t1 left join park_infos t2 on t1.park_code=t2.code order by t1.park_code,t1.code";
        String pgurl = "jdbc:postgresql://localhost:5432/park";
        String pgusername = "basedata";
        String pgpasswd = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
        ResultSet rs = connectionPG.createStatement().executeQuery(sql);
        JSONParser parser = new JSONParser();
        List<ParkPlaces> list = new ArrayList<ParkPlaces>();
        while(rs.next()){
            String id = rs.getString(1);
            String code = rs.getString(2);
            String park_code = rs.getString(3);
            String parkinfoname = rs.getString(4);
            String jsongeom = rs.getString(5);
            String datatime = rs.getString(6);
            JSONObject jsonObject = (JSONObject)parser.parse(jsongeom);
            ParkPlaces pp = new ParkPlaces();
            pp.setCode(code);
            pp.setParkInfoName(null==parkinfoname?"":parkinfoname);
            pp.setDataTime(datatime);
            pp.setId(id);
            pp.setParkCode(park_code);
            pp.setX(null==jsonObject.get("x")?"":jsonObject.get("x").toString());
            pp.setY(null==jsonObject.get("y")?"":jsonObject.get("y").toString());
            list.add(pp);
        }
        POIExcelUtil.ExportDataByList(list, "d:\\park_spaces.xls");
    }
    
    @Test
    public void testGeneratorArcgisJSONToShape() throws Exception{
        String pgurl = "jdbc:postgresql://localhost:5432/park";
        String pgusername = "postgres";
        String pgpasswd = "postgres";
        Connection connectionOLE = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
//        String sql = "select id,smail_area_code,park_code,park_name,park_type,park_geom from park_area where park_code like '13%'";
        String sql = "select id,smail_area_code,park_code,park_name,park_type,park_geom from park_area";
        ResultSet rs = connectionOLE.createStatement().executeQuery(sql);
        List<ParkArea> listPolygon = new ArrayList<ParkArea>();
        List<ParkArea> listLine = new ArrayList<ParkArea>();
        while(rs.next()){
            if(null!=rs.getString(6)){
                Geometry geom1 = GeoToolsGeometry.ArcgisJSONToGeometry(rs.getString(6));
                ParkArea pa = new ParkArea();
                pa.setId(rs.getString(1));
                pa.setSmallArea(rs.getString(2));
                pa.setParkCode(rs.getString(3));
                pa.setParkName(rs.getString(4));
                pa.setPark_type(rs.getString(5));
                pa.setPark_geom(geom1.toText());
                if(geom1 instanceof MultiPolygon){
                    listPolygon.add(pa);
                }else if(geom1 instanceof MultiLineString){ 
                    listLine.add(pa);
                }  
            }
            
        }
        System.out.println(listPolygon.size());
        System.out.println(listLine.size());
        GeoShapeUtil.ListObjectToShapeFile(listLine,"d:\\park_area_line_fsq.shp","GBK","4","park_geom","EPSG:4326");
        GeoShapeUtil.ListObjectToShapeFile(listPolygon,"d:\\park_area_polygon_fsq.shp","GBK","6","park_geom","EPSG:4326");
    }
    
    @Test
    public void transformXYOfsjs_080305_park_84() throws Exception{
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String pgurl = "jdbc:postgresql://localhost:5432/park";
        String pgusername = "postgis";
        String pgpasswd = "postgis";
        String tableName = "sjs_080305_park_84";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
        String sql_update = "update "+tableName+" set the_geom=st_geometryfromtext(?,4326) where fid=?";
        String sql_query = "select fid, st_astext(the_geom) from "+tableName;
        Statement statement = connectionPG.createStatement();
        PreparedStatement ps = connectionPG.prepareStatement(sql_update);
        ResultSet rs = statement.executeQuery(sql_query);
        int i=1;
        while(rs.next()){
            Integer id = rs.getInt(1);
            String wkt = rs.getString(2);
            System.out.println(id +"-----"+i);
            String wktCopy= wkt;
            Matcher matcher = pattern.matcher(wkt);
            while(matcher.find()){
                String temp = wkt.substring(matcher.start(),matcher.end());
                String[] xyArrTemp = temp.split(" ");
                double x_double = Double.parseDouble(xyArrTemp[0]);
                double y_double = Double.parseDouble(xyArrTemp[1]);
                
                double[] cehuiDouble = GISCoordinateTransform.From84To02(x_double,y_double);
                System.out.println(x_double+"----"+y_double+"***"+cehuiDouble[0]+"----"+cehuiDouble[1]);
                System.out.println(temp +":"+cehuiDouble[1]+" "+cehuiDouble[0]);
                wktCopy = wktCopy.replaceAll(temp, cehuiDouble[0]+" "+cehuiDouble[1]);
            }
            ps.setString(1, wktCopy);
            ps.setInt(2, id);
           ps.addBatch();
            i++;
       }
        ps.executeBatch();
        
    }
    
    
    @Test
    public void transformXYOfpark_area_polygon_sjs_080305_84() throws Exception{
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String pgurl = "jdbc:postgresql://localhost:5432/park";
        String pgusername = "postgis";
        String pgpasswd = "postgis";
        String tableName = "park_area_polygon_fsq_131802_84";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
        String sql_update = "update "+tableName+" set the_geom=st_geometryfromtext(?,4326) where fid=?";
        String sql_query = "select fid, st_astext(the_geom) from "+tableName;
        Statement statement = connectionPG.createStatement();
        PreparedStatement ps = connectionPG.prepareStatement(sql_update);
        ResultSet rs = statement.executeQuery(sql_query);
        int i=1;
        while(rs.next()){
            Integer id = rs.getInt(1);
            String wkt = rs.getString(2);
            System.out.println(id +"-----"+i);
            String wktCopy= wkt;
            Matcher matcher = pattern.matcher(wkt);
            while(matcher.find()){
                String temp = wkt.substring(matcher.start(),matcher.end());
                String[] xyArrTemp = temp.split(" ");
                double x_double = Double.parseDouble(xyArrTemp[0]);
                double y_double = Double.parseDouble(xyArrTemp[1]);
                
                double[] cehuiDouble = GISCoordinateTransform.From02To84(y_double,x_double);
                System.out.println(x_double+"----"+y_double+"***"+cehuiDouble[0]+"----"+cehuiDouble[1]);
                System.out.println(temp +":"+cehuiDouble[1]+" "+cehuiDouble[0]);
                wktCopy = wktCopy.replaceAll(temp, cehuiDouble[1]+" "+cehuiDouble[0]);
            }
            ps.setString(1, wktCopy);
            ps.setInt(2, id);
           ps.addBatch();
            i++;
       }
        ps.executeBatch();
        
    }
    
    @Test
    public void importShapeToPostGis1(){
        String shapePath = "G:/项目文档/停车场/建筑物/building080205.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "park", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "building080205", MultiPolygon.class, "EPSG:4326");
    }
    
    @Test
    public void importShapeToPostGisBuilding(){
        String shapePath = "G:\\项目文档\\停车场\\建筑物\\building.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "park", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "building", MultiPolygon.class, "EPSG:4326");
    }
    
    
    @Test
    public void exportJTXQBH(){
        String url = "jdbc:postgresql://localhost:5432/park";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select \"NO\",\"CODE\" from traffic_area_small_84 where \"CODE\"!=''";
        String excelPath = "D:\\traffic_area_small_zt.xls";
        POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    /**
     * 根据交通小区编号来导出响应的图层数据信息
     */
    @Test
    public void exportShapeByTrafficAraeCode(){
        String[] trafficAreaCodeArr = new String[]{"80903","80104"};
        String pgurl = "jdbc:postgresql://localhost:5432/park";
        String pgusername = "postgis";
        String pgpasswd = "postgis";
        boolean isExportTraffic = true;
        boolean isBuilding = true;
        boolean isParkPlace = true;
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
        String sqlQuery1 = "select \"NO\",st_astext(the_geom) wkt from traffic_area_small_84 where \"NO\"=";
        String sqlQuery2 = "select id,\"smallArea\",\"parkCode\",\"parkName\",\"park_type\",st_astext(the_geom) wkt from park_area_polygon_sjs where \"smallArea\"=";
        String sqlQuery3 = "select b.fid, b.\"ID\",st_astext(b.the_geom) wkt from traffic_area_small_84 a,building b where ST_Contains(a.the_geom,b.the_geom) and a.\"NO\"=";
        String shapeFilePath = "G:\\项目文档\\停车场\\速通石景山\\20160603\\";
        
        for(String codeTemp : trafficAreaCodeArr){
            String codeTempStr = "0"+ codeTemp;
            //exprot traffic_area_small
            if(isExportTraffic){
                String sqlQueryTemp = sqlQuery1+codeTemp;
                String result = GeoShapeUtil.ExportTableToShape(connectionPG, sqlQueryTemp, shapeFilePath+"sjc_"+codeTempStr+".shp","GBK","6","EPSG:4326");
                System.out.println("traffic_area_small:"+codeTemp+"exprot result is"+result);
            }
            //export park_area_polygon_sjs 
            if(isParkPlace){
                String sqlQueryTemp2 = sqlQuery2+"'"+codeTempStr+"'";
                String result2 = GeoShapeUtil.ExportTableToShape(connectionPG, sqlQueryTemp2, shapeFilePath+"sjc_tcc_"+codeTempStr+".shp","GBK","6","EPSG:4326");
                System.out.println("park_area_polygon_sjs:"+codeTemp+"exprot result is"+result2);
            }
            //export building
            if(isBuilding){
                String sqlQueryTemp3 = sqlQuery3+codeTemp;
                String result3 = GeoShapeUtil.ExportTableToShape(connectionPG, sqlQueryTemp3, shapeFilePath+"sjc_building_"+codeTempStr+".shp","GBK","6","EPSG:4326");
                System.out.println("building:"+codeTemp+"exprot result is"+result3);
            }
            
        }
    }
}
