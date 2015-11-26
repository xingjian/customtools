package com.promise.pbutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

import com.promise.cn.util.DBConnection;

/**  
 * 功能描述: 数据库连接测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月17日 下午2:20:30  
 */
public class DBConnectionTest {

    @Test
    public void testGetOracleConnection(){
        Connection connect = DBConnection.GetOracleConnection("jdbc:oracle:thin:@182.92.183.85:1521:orcl", "mobile", "adminxxssdeee998");
        System.out.println(connect);
    }
    
    @Test
    public void testCreate5HData() throws Exception{
        String url = "jdbc:postgresql://192.168.1.205:5432/mobile";
        String username = "mobile";
        String passwd = "adminxxssdeee998";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select uuid,workx,worky from temp_workcode201409_result5h";
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        statement.setFetchDirection(ResultSet.FETCH_REVERSE);
        ResultSet rs = statement.executeQuery(sql);
        while(rs.next()){
            String uuid = rs.getString(1);
            String workx = rs.getString(2);
            String worky = rs.getString(3);
            System.out.println(uuid);
       }
    }
}
