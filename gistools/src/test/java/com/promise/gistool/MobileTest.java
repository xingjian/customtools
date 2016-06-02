package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.gistool.util.ConversionUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月9日 上午9:42:34  
 */
public class MobileTest {

    @Test
    public void testCreate5HData() throws Exception{
        String url = "jdbc:postgresql://192.168.1.205:5432/mobile";
        String username = "mobile";
        String passwd = "adminxxssdeee998";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        
        String sql = "select uuid,workx,worky from temp_workcode201409_result5h";
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(20);
        ResultSet rs = statement.executeQuery(sql);
        String sql_update = "update temp_workcode201409_result5h set the_geom=st_geometryfromtext(?,4326) where uuid=?";
        PreparedStatement ps = connection.prepareStatement(sql_update);
        while(rs.next()){
            String uuid = rs.getString(1);
            String workx = rs.getString(2);
            String worky = rs.getString(3);
       }
        ps.executeBatch();
    }
    
    @Test
    public void testMercatorTo84(){
        String url = "jdbc:postgresql://192.168.1.205:5432/mobile";
        String username = "mobile";
        String passwd = "adminxxssdeee998";
        String shapePath = "G:\\项目文档\\手机信令\\gis数据\\zxcxzhzhydflqc.shp";
        List<String> attributes = ConversionUtil.GetShapeAttributes(shapePath, "GBK");
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\手机信令\\gis数据\\zxcxzhzhydflqc.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","zxcxzhzhydflqc",mapping);
        //上面是导入
        //update zxcxzhzhydflqc set the_geom=ST_Transform(the_geom,4326)
    }
    
    @Test
    public void testPatchPhonePoint() throws Exception{
        String tableName = "home201409_result5h";
        String sqlQuery = "select fid from zxcxzhzhydflqc order by fid";
        String insertResult = "insert into result_home201409_result5h (fid,result) values(?,?)";
        String sql = "select b.fid,count(*) from "+tableName+" a,zxcxzhzhydflqc b where ST_Within(a.the_geom, b.the_geom) and b.fid=? group by b.fid";
        String url = "jdbc:postgresql://192.168.1.205:5432/mobile";
        String username = "mobile";
        String passwd = "adminxxssdeee998";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        PreparedStatement psQuery = connection.prepareStatement(sqlQuery);
        PreparedStatement ps = connection.prepareStatement(sql);
        PreparedStatement psInsert = connection.prepareStatement(insertResult);
        ResultSet rs1 = psQuery.executeQuery();
        while(rs1.next()){
            String fid = rs1.getString(1);
            System.out.println("计算zxcxzhzhydflqc fid===="+fid);
            ps.setInt(1, Integer.parseInt(fid));
            ResultSet rs2 = ps.executeQuery();
            while(rs2.next()){
                int fidInt = rs2.getInt(1);
                int count = rs2.getInt(2);
                System.out.println(fidInt+"   "+count);
                psInsert.setInt(1, fidInt);
                psInsert.setInt(2, count);
                psInsert.addBatch();
            }
        }
        psInsert.executeBatch();
    }
    
    @Test
    public void testDateStr(){
        Calendar cal = Calendar.getInstance(); 
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        System.out.println(hour);
    }
}
