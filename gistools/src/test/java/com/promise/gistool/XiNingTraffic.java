package com.promise.gistool;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.promise.gistool.util.GeoToolsUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.buffer.BufferOp;

/**  
 * 功能描述: 西宁工作类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月16日 下午5:56:05  
 */
public class XiNingTraffic {

    @Test
    public void testCopyFeature() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/xiningtaffic";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select gid,st_astext(geom) from xningfilter_copy_3m where gid=19455";
        String insertSQL = "insert into xningfilter_copy1_result2 (gid,the_geom) values (?,st_geometryfromtext(?,4326))";
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        ResultSet rs = statement.executeQuery(sql);
        while(rs.next()){
            String gid = rs.getString(1);
            String wkts = rs.getString(2);
            Geometry geom = GeoToolsGeometry.createGeometrtyByWKT(wkts);
            Coordinate[] arrold = geom.getCoordinates();
            Coordinate[] arr = new Coordinate[arrold.length+2];
            for(int i=0;i<arrold.length;i++){
                if(i==0){
                    arr[0] = GeoToolsUtil.VectorExtendLine(arrold[1],arrold[0],0.00026);
                    System.out.println(arr[0].x +"----"+arr[0].y);
                    arr[1] = arrold[0];
                }else if(i==arrold.length-1){
                    arr[arrold.length+1] = GeoToolsUtil.VectorExtendLine(arrold[arrold.length-2],arrold[arrold.length-1],0.00026);
                    System.out.println(arr[arrold.length+1].x +"-******--"+arr[arrold.length+1].y);
                    arr[arrold.length] = arrold[arrold.length-1];
                }else{
                    arr[i+1] = arrold[i];
                }
            }
            for(Coordinate c:arr){
                System.out.println(c.x);
            }
            List<Coordinate> result = GeoToolsUtil.ParallelLine(-0.00006,arrold);
//            result.remove(0);
//            result.remove(result.size()-1);
            ps.setInt(1, Integer.parseInt(gid));
            LineString[] arrLineString = new LineString[1];
            arrLineString[0] = GeoToolsGeometry.createLine((Coordinate[])result.toArray(new Coordinate[result.size()]));
            MultiLineString ms = GeoToolsGeometry.createMLine(arrLineString);
            String wktnew = ms.toText();
            ps.setString(2, wktnew);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    @Test
    public void testCopyFeatureNew() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/xiningtaffic";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select gid,st_astext(geom) from xningfilter_copy_3m where gid=313";
        String insertSQL = "insert into xningfilter_copy1_result2 (gid,the_geom) values (?,st_geometryfromtext(?,4326))";
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        ResultSet rs = statement.executeQuery(sql);
        while(rs.next()){
            String gid = rs.getString(1);
            String wkts = rs.getString(2);
            Geometry geom = GeoToolsGeometry.createGeometrtyByWKT(wkts);
            Geometry buff = GeoToolsGeometry.buffer(geom, 0.00006,8,BufferOp.CAP_SQUARE);
            Geometry interPoint = geom.intersection(buff);//相交点  
            System.out.println(interPoint.toText());//输出 POINT (0 0)
            
            Coordinate[] arrold = geom.getCoordinates();
            Coordinate[] arr = new Coordinate[arrold.length+2];
            for(int i=0;i<arrold.length;i++){
                if(i==0){
                    arr[0] = GeoToolsUtil.VectorExtendLine(arrold[1],arrold[0],0.00006);
                    arr[1] = arrold[0];
                }else if(i==arrold.length-1){
                    arr[arrold.length+1] = GeoToolsUtil.VectorExtendLine(arrold[arrold.length-2],arrold[arrold.length-1],0.00006);
                    arr[arrold.length] = arrold[arrold.length-1];
                }else{
                    arr[i+1] = arrold[i];
                }
            }
            for(Coordinate c:arr){
                System.out.println(c.x);
            }
            List<Coordinate> result = new ArrayList<Coordinate>();
            for(int i=1;i<arr.length-1;i++){
                Coordinate[] arrTemp =  new Coordinate[3];
                arrTemp[0] =  arr[i-1];
                arrTemp[1] =  arr[i];
                arrTemp[2] =  arr[i+1];
                Coordinate subResut = GeoToolsUtil.ParallelLineMiddle(-0.00006, arrTemp);
                result.add(subResut);
            }
            ps.setInt(1, Integer.parseInt(gid));
            LineString[] arrLineString = new LineString[1];
            arrLineString[0] = GeoToolsGeometry.createLine(buff.getCoordinates());//GeoToolsGeometry.createLine((Coordinate[])result.toArray(new Coordinate[result.size()]));
            MultiLineString ms = GeoToolsGeometry.createMLine(arrLineString);
            Coordinate[] resultCoor = ms.getCoordinates();
            List<Coordinate[]> result2 = splitBuffer(resultCoor,arrold[0],arrold[arrold.length-1]);
            String wktnew = ms.toText();
            ps.setString(2, wktnew);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    public List<Coordinate[]> splitBuffer(Coordinate[] resultCoor,Coordinate c1,Coordinate c2){
        List<Coordinate[]> result = new ArrayList<Coordinate[]>();
        Point pc1 = GeoToolsGeometry.createPoint(c1.x, c1.y);
        Point pc2 = GeoToolsGeometry.createPoint(c2.x, c2.y);
        for(int i=0;i<resultCoor.length;i++){
            Coordinate cTemp = resultCoor[i];
            Point pTemp = GeoToolsGeometry.createPoint(cTemp.x, cTemp.y);
            if(pc1.intersects(pTemp)){
                System.out.println("c1 index = "+ i);
            }else if(pc2.intersects(pTemp)){
                System.out.println("c2 index = "+ i);
            }
        }
        return result;
    }
    
    @Test
    public void testCopyFeature_3m() throws Exception{
        //正数 2 负数 3
        String url = "jdbc:postgresql://localhost:5432/xiningtaffic";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "SELECT mapid, id, kind_num, kind, width, direction, toll, const_st, "+
                "undconcrid, snodeid, enodeid, funcclass, length, detailcity, "+
                "through, unthrucrid, ownership, road_cond, special, admincodel,"+ 
                "admincoder, uflag, onewaycrid, accesscrid, speedclass, lanenums2e,"+ 
                "lanenume2s, lanenum, vehcl_type, elevated, structure, usefeecrid,"+ 
                "usefeetype, spdlmts2e, spdlmte2s, spdsrcs2e, spdsrce2s, dc_type, "+
                "nopasscrid, outbancrid, numbancrid, parkflag, st_astext(geom) "+
                "FROM xningfilter_copy_3m";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        //平行线距离原来线的距离
        double dist = 3.0/110000;
        System.out.println(dist);
        //(23769)
        int gidStart = 24000;
        while(rs.next()){
            String mapid = rs.getString(1);
            String id = rs.getString(2);
            String kind_num = rs.getString(3);
            String kind = rs.getString(4);
            String width = rs.getString(5);
            String direction = rs.getString(6);
            String toll = rs.getString(7);
            String const_st = rs.getString(8);
            String undconcrid = rs.getString(9);
            String snodeid = rs.getString(10);
            String enodeid = rs.getString(11);
            String funcclass = rs.getString(12);
            String length = rs.getString(13);
            String detailcity = rs.getString(14);
            String through = rs.getString(15);
            String unthrucrid = rs.getString(16);
            String ownership = rs.getString(17);
            String road_cond = rs.getString(18);
            String special = rs.getString(19);
            String admincodel = rs.getString(20);
            String admincoder = rs.getString(21);
            String uflag = rs.getString(22);
            String onewaycrid = rs.getString(23);
            String accesscrid = rs.getString(24);
            String speedclass = rs.getString(25);
            String lanenums2e = rs.getString(26);
            String lanenume2s = rs.getString(27);
            String lanenum = rs.getString(28);
            String vehcl_type = rs.getString(29);
            String elevated = rs.getString(30);
            String structure = rs.getString(31);
            String usefeecrid = rs.getString(32);
            String usefeetype = rs.getString(33);
            String spdlmts2e = rs.getString(34);
            String spdlmte2s = rs.getString(35);
            String spdsrcs2e = rs.getString(36);
            String spdsrce2s = rs.getString(37);
            String dc_type = rs.getString(38);
            String nopasscrid = rs.getString(39);
            String outbancrid = rs.getString(40);
            String numbancrid = rs.getString(41);
            String parkflag = rs.getString(42);
            String wkts = rs.getString(43);
            
            Geometry geom = GeoToolsGeometry.createGeometrtyByWKT(wkts);
            Coordinate[] arrold = geom.getCoordinates();
            Coordinate[] arr = new Coordinate[arrold.length+2];
            for(int i=0;i<arrold.length;i++){
                if(i==0){
                    arr[0] = GeoToolsUtil.VectorExtendLine(arrold[1],arrold[0],dist);
                    arr[1] = arrold[0];
                }else if(i==arrold.length-1){
                    arr[arrold.length+1] = GeoToolsUtil.VectorExtendLine(arrold[arrold.length-2],arrold[arrold.length-1],dist);
                    arr[arrold.length] = arrold[arrold.length-1];
                }else{
                    arr[i+1] = arrold[i];
                }
            }
            List<Coordinate> result = GeoToolsUtil.ParallelLine(dist,arr);
            result.remove(0);
            result.remove(result.size()-1);
            for(int k=0;k<result.size();k++){
                Coordinate coorTemp = result.get(k);
                if(GeoToolsGeometry.createPoint(coorTemp.x, coorTemp.y).isEmpty()){
                    result.remove(result.get(k));
                }
            }
            LineString[] arrLineString = new LineString[1];
            arrLineString[0] = GeoToolsGeometry.createLine((Coordinate[])result.toArray(new Coordinate[result.size()]));
            MultiLineString ms = GeoToolsGeometry.createMLine(arrLineString);
            String wktnew = ms.toText();
            String tableName = "xningfilter_copy_3m";
            String insertSQL="INSERT INTO "+tableName+"("+
                    "gid, mapid, id, kind_num, kind, width, direction, toll, const_st, "+
                    "undconcrid, snodeid, enodeid, funcclass, length, detailcity, "+
                    "through, unthrucrid, ownership, road_cond, special, admincodel, "+
                    "admincoder, uflag, onewaycrid, accesscrid, speedclass, lanenums2e, "+
                    "lanenume2s, lanenum, vehcl_type, elevated, structure, usefeecrid, "+
                    "usefeetype, spdlmts2e, spdlmte2s, spdsrcs2e, spdsrce2s, dc_type, "+
                    "nopasscrid, outbancrid, numbancrid, parkflag, geom)"+
                    " VALUES ("+gidStart+","+mapid+","+ id+","+ kind_num+","+ kind+","+ width+","+ direction+","+ toll+","+ const_st+","+ 
            undconcrid+","+ snodeid+","+ enodeid+","+ funcclass+","+ length+","+ detailcity+","+ 
            through+","+ unthrucrid+","+ ownership+","+ road_cond+","+ special+","+ admincodel+","+ 
            admincoder+","+ uflag+","+ onewaycrid+","+ accesscrid+","+ speedclass+","+ lanenums2e+","+ 
            lanenume2s+","+ lanenum+","+ vehcl_type+","+ elevated+","+ structure+","+ usefeecrid+","+ 
            usefeetype+","+ spdlmts2e+","+ spdlmte2s+","+ spdsrcs2e+","+ spdsrce2s+","+ dc_type+","+ 
            nopasscrid+","+ outbancrid+","+ numbancrid+","+ parkflag+","+ "st_geometryfromtext('"+wktnew+"',4326));"+"\n";
            gidStart++;
            String filePath = "d:\\"+tableName+".sql";
            File resultFile = new File(filePath);
            if(resultFile.exists()){
                resultFile.createNewFile();
            }
            PBFileUtil.WriteStringToTxt(insertSQL, filePath);
        }
    }
    
    
    @Test
    public void testExportTableToShape(){
        String url = "jdbc:postgresql://localhost:5432/xiningtaffic";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "SELECT mapid, id, kind_num, kind, width, direction, toll, const_st, "+
                "undconcrid, snodeid, enodeid, funcclass, length, detailcity, "+
                "through, unthrucrid, ownership, road_cond, special, admincodel,"+ 
                "admincoder, uflag, onewaycrid, accesscrid, speedclass, lanenums2e,"+ 
                "lanenume2s, lanenum, vehcl_type, elevated, structure, usefeecrid,"+ 
                "usefeetype, spdlmts2e, spdlmte2s, spdsrcs2e, spdsrce2s, dc_type, "+
                "nopasscrid, outbancrid, numbancrid, parkflag, st_astext(geom) wkt "+
                "FROM xningfilter_copy_3m";
        String shapeFilePath = "d:\\xningfilter_copy_3m4841.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
    }
    
    
    @Test
    public void testGetShapeAttributes(){
        //G:\项目文档\西宁交通\西宁双向\xning4841_left_05_round.dbf
        String shapePath = "G:\\项目文档\\西宁交通\\西宁双向\\xning4841_left_05_round.shp";
        List<String> attributes = ConversionUtil.GetShapeAttributes(shapePath, "GBK");
        PrintUtil.PrintObject(attributes);
    }
    
    /**
     * 测试 shape数据导入PostGIS SQL语句方式
     * 导入xningfilter
     */
    @Test
    public void testShapeToPostGISXiNingFilter(){
        String shapePath = "G:\\项目文档\\西宁交通\\西宁双向\\xning4841_right_05_round.shp";
        String url = "jdbc:postgresql://localhost:5432/xiningtaffic";
        String username = "postgis";
        String passwd = "postgis";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\西宁交通\\西宁双向\\xning4841.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","xningfilter4841",mapping);
        Assert.assertEquals("success", result);
    }
    
    @Test
    public void testImportxining_section_20160929(){
        String shapePath = "d:\\xining_section_20160929\\xining_section_20160929.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("localhost", "5432", "xiningtaffic", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK");
        System.out.println(result);
    }
    
    
    @Test
    public void testImportXiNingNan2Shape(){
        String shapePath = "G:\\项目文档\\西宁交通\\西宁双向\\pingxing0114\\xiningnan2_all.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("ttyjbj.ticp.net", "5432", "xiningtraffic", "postgres", "fld789&*(","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK");
        System.out.println(result);
    }
    
    @Test
    public void exportXNGPSShape() throws Exception{
        //车辆类别（如某个公司代码），车牌号，GPS时间，经度，纬度，速度，方向角，车辆状态，转换后经度，转换后纬度。
        List<String> list = PBFileUtil.ReadFileByLine("G:\\项目文档\\西宁交通\\文档\\201604081639");
        List<XININGGPS> gpsList = new ArrayList<XININGGPS>();
        Map<String,String> map = new HashMap<String, String>();
        for(String record : list){
            String[] arr = record.split(",");
            String companyCode = arr[0];
            String carCode = arr[1];
            String time = arr[2];
            double longtidude = Double.parseDouble(arr[3]);
            double latidude = Double.parseDouble(arr[4]);
            if(null==map.get(carCode)){
                XININGGPS gpsmessage = new XININGGPS();
                gpsmessage.setCarCode(carCode);
                gpsmessage.setCompanyCode(companyCode);
                gpsmessage.setTime(time);
                gpsmessage.setLatidude(latidude);
                gpsmessage.setLongtidude(longtidude);
                String wkt = GeoToolsGeometry.createPoint(longtidude, latidude).toText();
                System.out.println(wkt);
                gpsmessage.setWkt(wkt);
                gpsList.add(gpsmessage);
            }
        }
        GeoShapeUtil.ListObjectToShapeFile(gpsList, "d:\\xngps_point.shp", "GBK", "1", "wkt", "EPSG:4326");
    }
    
    
    @Test
    public void testFrom54To02ShapeFile(){
        String inputShapeFile = "d:\\xining_section_20160929\\xining_section_20160929.shp";
        String outputShapleFile = "d:\\xining_section_20160929\\xining_section_20160929_new.shp";
        try {
           String result = GISCoordinateTransform.From84To02(inputShapeFile, outputShapleFile, "GBK");
           System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
 