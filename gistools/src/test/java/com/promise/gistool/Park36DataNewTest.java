package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.POIExcelUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年9月6日 下午1:14:04  
 */
public class Park36DataNewTest {

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
        String sqlQuery_Oracle = "select x, y, rownum_, id_, district, roadname, roadid, roadsecnam, roadsecid, roaddirect, parkspids, equipment, parkspide, parkspnums from PARK_POINT_NEW_TWO";
        String insert_pg = "INSERT INTO park_roadsection_new( x, y, rownum_, district, roadname, roadid, roadsecnam, roadsecid, roaddirect, parkspids,equipment, parkspide, parkspnums,id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            int parkspnums = rs_oracle.getInt("parkspnums");
            String equipment = rs_oracle.getString("equipment");
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
            ps_pg.setString(11,equipment);
            ps_pg.setString(12,parkspide);
            ps_pg.setInt(13,parkspnums);
            ps_pg.setString(14,id);
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
        String sqlQuery_Oracle = "select x, y,id_,parkid,frontflag,backflag,type  from PARKSPACE_POINT_NEW";
        String insert_pg = "INSERT INTO park_roadsection_point_new( x, y, id, parkid, frontflag,backflag,type) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "select x, y, id, parkid,type from park_roadsection_point_new";
        String sql2 = "update park_roadsection_point_new set the_geom=st_geometryfromtext(?,4326) where parkid='PK141117101608'";
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
        String sql2 = "select id from park_roadsection_new ";
        ResultSet rs2 = connectionPG.createStatement().executeQuery(sql2);
        String sql3 = "update park_roadsection_new set the_geom=st_geometryfromtext(?,4326) where id=?";
        PreparedStatement ps = connectionPG.prepareStatement(sql3);
        while(rs2.next()){
            String parkid = rs2.getString(1);
            String sql = "select id,st_x(the_geom),st_y(the_geom) from park_roadsection_point_new where parkid='"+parkid+"' order by parkid,id";
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
//        String pgurl = "jdbc:postgresql://localhost:5432/park";
//        String pgusername = "basedata";
//        String pgpasswd = "basedata";
        String pgurl = "jdbc:postgresql://10.212.140.210:5432/park_gis";
        String pgusername = "postgres";
        String pgpasswd = "postgres";
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
    
}
