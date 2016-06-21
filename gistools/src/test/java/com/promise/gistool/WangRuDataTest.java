package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;

import com.promise.cn.util.DBConnection;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月21日 下午3:35:57  
 */
public class WangRuDataTest {

    @Test
    public void testUpdateTrafficMiddleAreaName() throws Exception{
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        String urlOracle = "jdbc:oracle:thin:@ttyjwh.wicp.net:1522:ttyj";
        String usernameOracle = "buscity";
        String passwdOracle = "admin123ttyj7890uiop";
        Connection connectionOracle = DBConnection.GetOracleConnection(urlOracle, usernameOracle, passwdOracle);
        
        String updateSQL = "update traffic_area_middle set name=? where code=?";
        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
        String querySQL = "select name,code from de_cfg_area t where t.type=2";
        ResultSet rs = connectionOracle.createStatement().executeQuery(querySQL);
        while(rs.next()){
            String name = rs.getString(1);
            String code = rs.getString(2);
            psUpdate.setString(1, name);
            psUpdate.setInt(2, Integer.parseInt(code));
            psUpdate.addBatch();
        }
        psUpdate.executeBatch();
    }
}
