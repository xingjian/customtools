package com.promise.gistool;

import java.sql.Connection;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.DBType;
import com.promise.cn.util.PBPSQLUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年8月25日 下午7:09:01  
 */
public class KJFDataTest {

    @Test
    public void testCopyKJF() throws Exception{
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@192.168.1.104:1521:orcl", "tocc2", "admin123");
        String sql = "select * from ROUTE_HUANLU_SPEED_0819";
        connectOracle.createStatement().executeQuery(sql);
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
}
