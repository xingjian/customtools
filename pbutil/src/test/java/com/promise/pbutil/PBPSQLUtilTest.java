package com.promise.pbutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.DBType;
import com.promise.cn.util.PBPSQLUtil;

/**  
 * 功能描述:PBPSQLUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月23日 下午4:09:24  
 */
public class PBPSQLUtilTest {

    @Test
    public void testIsTableExist(){
        Connection connectionOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@182.92.183.85:1521:orcl", "buscity", "admin123ttyj7890uiop");
        boolean boo11 = PBPSQLUtil.IsTableExist(connectionOracle, "DE_CFG_DM");
        boolean boo12 = PBPSQLUtil.IsTableExist(connectionOracle, "DE_CFG_DM_BAK");
        Connection connectionPG = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        boolean boo21 = PBPSQLUtil.IsTableExist(connectionPG, "spatial_ref_sys");
        boolean boo22 = PBPSQLUtil.IsTableExist(connectionPG, "spatial_ref_sys_bak");
        System.out.println("connectionOracle DE_CFG_DM :"+boo11);
        System.out.println("connectionOracle DE_CFG_DM_BAK :"+boo12);
        System.out.println("connectionPG spatial_ref_sys :"+boo21);
        System.out.println("connectionPG spatial_ref_sys_bak :"+boo22);
    }
    
    @Test
    public void testInsertQHSF() throws Exception{
        Connection connect = DBConnection.GetOracleConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ttsoft", "ttsoft");
        String insertSQL = "insert into test_V_EXPORT_TABLE1 (id,stime,name,speed,levelInt) values(?,?,?,?,?)";
        PreparedStatement ps = connect.prepareStatement(insertSQL);
        for(int i=2000;i<4000;i++){
            ps.setString(1, i+"");
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setString(3, i+""+i);
            ps.setDouble(4, i*0.5);
            ps.setInt(5, i);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    @Test
    public void testQHSF() throws Exception{
        Calendar cal = Calendar.getInstance();
        Connection connect = DBConnection.GetOracleConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ttsoft", "ttsoft");
        String querySQL = "select id,stime,name,speed,levelInt from test_V_EXPORT_TABLE1 where stime between ? - 1 / 24  and ? - 1 / 24 / 12";
        PreparedStatement ps = connect.prepareStatement(querySQL);
        ps.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
        ps.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while(rs.next()){
            count++;
            String s1 = rs.getString(1);
            String s2 = rs.getString(2);
            System.out.println(s1+"---"+s2);
        }
        System.out.println("count ==== "+count);
    }
    
    @Test
    public void testGetTableStructure() throws Exception{
        Connection connectionOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@10.212.140.210:1521:parking", "new_park", "new_park");
        Connection connectionPG = DBConnection.GetPostGresConnection("jdbc:postgresql://localhost:5432/basedata", "basedata", "basedata");
        PBPSQLUtil.GetTableStructure(connectionOracle, "PARK_AREA");
        
    }
    
    @Test
    public void testCopyTable() throws Exception{
        String sql1 = "INSERT INTO test_linestring( id, name, typestr) VALUES (?, ?, ?)";
        String sql2 = "select id,name,linenumber from busline";
        Connection connectionPG = DBConnection.GetPostGresConnection("jdbc:postgresql://localhost:5432/test_gis", "postgres", "postgres");
        Connection connectionPG1 = DBConnection.GetPostGresConnection("jdbc:postgresql://ttyjbj.ticp.net:5432/basedata", "basedata", "basedata");
        String result = PBPSQLUtil.CopyTable(connectionPG1, DBType.PostgreSQL, sql2, connectionPG, sql1, 5000, new String[]{"String","String","String"});
        System.out.println(result);
    }
    
    @Test
    public void testGetTableDDLCreate() throws Exception{
       // Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://localhost:5432/basedata", "basedata", "basedata");
       //PBPSQLUtil.GetTableDDLCreate(connection, DBType.PostgreSQL, "busline", "pbbusline");
        Connection connectionOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj", "buscity", "admin123ttyj7890uiop");
        PBPSQLUtil.GetTableDDLCreate(connectionOracle, DBType.PostgreSQL, "ACAA02_AFC_SYSTEM_BODY_HISTORY","BUSLINE_CHANNEL");
    }
    
    @Test
    public void testCopyTableParkInfo() throws Exception{
        String sql1 = "INSERT INTO parkinfo( kind_type, type, code, name, area, area_code, trafficcode, addr,   charge, day_c, night_c, day_o, night_o, day, night, num) VALUES (?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?)";
        String sql2 = "SELECT kind_type, type, code, name, area, area_code, trafficcode, addr, charge, day_c, night_c, day_o, night_o, day, night, num FROM parkinfo";
        String urlOracle = "jdbc:oracle:thin:@10.212.140.210:1521:parking";
        String usernameOracle = "new_park";
        String passwdOracle = "new_park";
        Connection connectionOLE = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        String pgurl = "jdbc:postgresql://ttyjbj.ticp.net:5432/tocc_park";
        String pgusername = "toccpark";
        String pgpasswd = "toccpark";
        Connection connectionPG = DBConnection.GetOracleConnection(pgurl, pgusername, pgpasswd);
        String result = PBPSQLUtil.CopyTable(connectionOLE, DBType.Oracle, sql2, connectionPG, sql1, 5000, new String[]{"String","String","String","String","String","String","String","String","int","int","int","int","int","int","int","int"});
        System.out.println(result);
    }
    
    
    @Test
    public void testCopyTableParkPoint() throws Exception{
        String sql1 = "INSERT INTO parkpoint( kind_type, trafficcode, code, name, area, addr, num, charge,  pointcode, direction, nature, x, y) VALUES (?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?)";
        String sql2 = "SELECT kind_type, trafficcode, code, name, area, addr, num, charge,  pointcode, direction, nature, x, y FROM parkpoint";
        String urlOracle = "jdbc:oracle:thin:@10.212.140.210:1521:parking";
        String usernameOracle = "new_park";
        String passwdOracle = "new_park";
        Connection connectionOLE = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        String pgurl = "jdbc:postgresql://ttyjbj.ticp.net:5432/tocc_park";
        String pgusername = "toccpark";
        String pgpasswd = "toccpark";
        Connection connectionPG = DBConnection.GetOracleConnection(pgurl, pgusername, pgpasswd);
        String result = PBPSQLUtil.CopyTable(connectionOLE, DBType.Oracle, sql2, connectionPG, sql1, 5000, new String[]{"String","String","String","String","String","String","int","int","String","String","String","double","double"});
        System.out.println(result);
    }
    
    
    @Test
    public void testCopyTabletb_integrate_park_info() throws Exception{
        String sql1 = "INSERT INTO tb_integrate_park_info( area, memo_no, id, parking_name, paking_type, unit_name, code,   park_name, park_address, community_name, street_name, pc_type, "
            +" land_type, whehereornot_opening, whehereornot_charge, existing_park_count," 
            +" ex_type, gps_longitude, gps_latitude) VALUES (?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?,  ?, ?, ?, ?,  ?, ?, ?)";
        String sql2 = "SELECT area, memo_no, id, parking_name, paking_type, unit_name, code, "+
       "park_name, park_address, community_name, street_name, pc_type, "+
       "land_type, whehereornot_opening, whehereornot_charge, existing_park_count, "+
       "ex_type, gps_longitude, gps_latitude FROM tb_integrate_park_info";
        String urlOracle = "jdbc:oracle:thin:@10.212.138.190:1521:toccdb2";
        String usernameOracle = "tocc2";
        String passwdOracle = "admin123";
        Connection connectionOLE = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        String pgurl = "jdbc:postgresql://ttyjbj.ticp.net:5432/tocc_park";
        String pgusername = "toccpark";
        String pgpasswd = "toccpark";
        Connection connectionPG = DBConnection.GetOracleConnection(pgurl, pgusername, pgpasswd);
        String result = PBPSQLUtil.CopyTable(connectionOLE, DBType.Oracle, sql2, connectionPG, sql1, 5000, new String[]{"String","String","String","String","String","String","String","String","String","String","String","String","String","String","String","String","String","String","String"});
        System.out.println(result);
    }
    
}
