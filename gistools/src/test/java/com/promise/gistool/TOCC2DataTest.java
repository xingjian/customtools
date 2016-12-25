package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年7月13日 下午2:32:44  
 */
public class TOCC2DataTest {
    
    @Test
    public void testParkWKTTONorm_scope()throws Exception{
        String urlOracle1 = "jdbc:oracle:thin:@10.212.138.116:1521:tocc";
        String usernameOracle1 = "toccdb";
        String passwdOracle1 = "toccdb";
        Connection connectionOracle1 = DBConnection.GetOracleConnection(urlOracle1, usernameOracle1, passwdOracle1);
        String sql1 = "select PARKING_NAME,GPS_LONGITUDE,GPS_LATITUDE from PARK_INFO_VIEW where DATATIME='201606'";
        ResultSet rs = connectionOracle1.createStatement().executeQuery(sql1);
        Map<String,String> map = new HashMap<String,String>();
        while(rs.next()){
            String parkName = rs.getString(1);
            String longitude = rs.getString(2);
            String lattiude = rs.getString(3);
            if(null!=longitude&&null!=lattiude){
                Point point = GeoToolsGeometry.createPoint(Double.parseDouble(longitude), Double.parseDouble(lattiude));
                map.put(parkName, point.toText());
            }
        }
        
        
        
        String urlOracle = "jdbc:oracle:thin:@10.212.138.116:1521:tocc";
        String usernameOracle = "indicator";
        String passwdOracle = "indicator";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        String sqlQuery = "select id,name from norm_scope where id like '000e%'";
        ResultSet rs1 = connectionOracle.createStatement().executeQuery(sqlQuery);
        String update = "update norm_scope set geom=? where id=?";
        PreparedStatement ps = connectionOracle.prepareStatement(update);
        while(rs1.next()){
            String id = rs1.getString(1);
            String name = rs1.getString(2);
            if(null!=map.get(name)){
                String wkt = map.get(name);
                ps.setString(1, wkt);
                ps.setString(2, id);
                ps.addBatch();
            }
        }
        ps.executeBatch();
    }
    
    /**
     * 拷贝parkarea到本地postgres数据库
     */
    @Test
    public void testCopyParkArea() throws Exception{
        String urlOracle = "jdbc:oracle:thin:@10.212.140.210:1521:parking";
        String usernameOracle = "new_park";
        String passwdOracle = "new_park";
        Connection connectionOLE = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        String pgurl = "jdbc:postgresql://ttyjbj.ticp.net:5432/tocc_park";
        String pgusername = "toccpark";
        String pgpasswd = "toccpark";
        Connection connectionPG = DBConnection.GetOracleConnection(pgurl, pgusername, pgpasswd);
        
        String sql = "select id,smail_area_code,park_code,park_name,park_type,park_geom,state,user_code,kind_type from park_area";
        ResultSet rs = connectionOLE.createStatement().executeQuery(sql);
        String inserSQL = "INSERT INTO park_area( id, smail_area_code, park_code, park_name, park_type, park_geom, "+
            "state, user_code, kind_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement psInsert = connectionPG.prepareStatement(inserSQL);
        int count = 0;
        while(rs.next()){
            String id = rs.getString(1);
            String smail_area_code = rs.getString(2);
            String park_code = rs.getString(3);
            String park_name = rs.getString(4);
            String park_type = rs.getString(5);
            String park_geom = rs.getString(6);
            int state = rs.getInt(7);
            String user_code = rs.getString(8);
            String kind_type = rs.getString(9);
            psInsert.setString(1, id);
            psInsert.setString(2, smail_area_code);
            psInsert.setString(3, park_code);
            psInsert.setString(4, park_name);
            psInsert.setString(5, park_type);
            psInsert.setString(6, park_geom);
            psInsert.setInt(7, state);
            psInsert.setString(8, user_code);
            psInsert.setString(9, kind_type);
            psInsert.addBatch();
            count++;
            if(count%5000==0){
                psInsert.executeBatch();
            }
        }
        psInsert.executeBatch();
    }
    
    /**
     * 导入佳琪提供西城区的路侧停车场（说是非常准确的数据，有毛博士提供的）
     */
    @Test
    public void importParkPoint(){
        String shapePath = "G:\\项目文档\\停车场\\西城停车位\\西城停车位\\02坐标\\Park_point.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("ttyjbj.ticp.net", "5432", "tocc_park", "toccpark", "toccpark","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "park_point", Point.class, "EPSG:4326");
        System.out.println(result);
    }
    
    /**
     * 将京通检测器数据和机场高速的检测器29个生成数据到指定表中
     */
    @Test
    public void importJCQData() throws Exception{
        String issertSQL = "INSERT INTO roadsection_cl_point(uuid, unirowid, sname, ename, streetname, intime, geom) VALUES (?, ?, ?, ?, ?, ?, ST_GeomFromText(?,4326))";
        String pgurl = "jdbc:postgresql://localhost:5432/tocc2";
        String pgusername = "postgres";
        String pgpasswd = "postgres";
        //ID,AREANAME,SCODE,STYPE,PILENO,PILENUM,LINENAME,LINECODE,POSITION,LONGITUDE,LATITUDE,DESCREPTION,REMARK,ROADTYPE,LONGX,LATIY,SXX
        //402881f7456efc0f01456efc46ea0009,,623110009,交通流量采集设备,K10+880,10880,机场高速,S12,VD9,12973718.37,4869640.512,,,1,12973712.81,4869653.688,all
        Connection connectionPG = DBConnection.GetOracleConnection(pgurl, pgusername, pgpasswd);
        PreparedStatement ps = connectionPG.prepareStatement(issertSQL);
        List<String> list = PBFileUtil.ReadCSVFile("D:\\sensor.csv", "GBK");
        for(String str:list){
            String[] arr = str.split(",");
            String id = arr[0];
            String scode = arr[2];
            String sname = arr[4];
            String ename = "";
            String streetName = arr[6];
            String intime = "2016-11-29";
            String x1 = arr[9];
            String y1 = arr[10];
            String x2 = arr[14];
            String y2 = arr[15];
            double[] arrXY = GISCoordinateTransform.From900913To84(Double.parseDouble(x1), Double.parseDouble(y1));
            Point point = GeoToolsGeometry.createPoint(arrXY[0], arrXY[1]);
            ps.setString(1, id);
            ps.setString(2, scode);
            ps.setString(3, sname);
            ps.setString(4, ename);
            ps.setString(5, streetName);
            ps.setString(6, intime);
            ps.setString(7, point.toText());
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
}
