package com.promise.pbutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
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
}
