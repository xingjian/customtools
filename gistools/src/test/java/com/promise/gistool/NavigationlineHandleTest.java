package com.promise.gistool;

import java.sql.Connection;
import java.sql.ResultSet;

import org.junit.Test;

import com.promise.cn.util.DBConnection;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年5月5日 下午3:14:57  
 */
public class NavigationlineHandleTest {

    /**
     * roadone一级
     * 0x00(高速路) 0x01(都市高速路) 0x02(国道) 0x03(省道) 0x04(线道)
     * roadattribute 不等于'0a','04'
     */
    @Test
    public void testGenRoadOne() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/sw_navigation";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select id,kind from navigationline where substring(kind,0,3) in ('00','01','02','03','04')";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        while(rs.next()){
            String kind = rs.getString("Kind");
            String[] arr = kind.split("\\|");
            for(String strTemp:arr){
                String roadAttribute = strTemp.substring(2, 4);
                System.out.println(roadAttribute);
            }
        }
    }
}
