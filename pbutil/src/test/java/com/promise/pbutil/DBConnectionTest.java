package com.promise.pbutil;

import java.sql.Connection;

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
}
