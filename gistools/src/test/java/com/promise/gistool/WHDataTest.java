package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBMathUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISDBUtil;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年1月9日 上午11:44:55  
 */
public class WHDataTest {

    @Test
    public void testExportStation(){
        String filePath = "G:\\项目文档\\武汉tocc\\gisdata\\wuhanshape\\wuhan_busstation.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("localhost", "5432", "sw_navigation", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(filePath, dataStore, "GBK", "wh_busstation", Point.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void genRandowRltData() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/sw_navigation";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select id from wh_busstation";
        String updateSQL = "update wh_busstation set rltdata=? where id=?";
        PreparedStatement ps = connection.prepareStatement(updateSQL);
        ResultSet rs = connection.createStatement().executeQuery(sql);
        while(rs.next()){
            String id = rs.getString(1);
            ps.setInt(1, PBMathUtil.GetRandomInt(10, 60));
            ps.setString(2, id);
            ps.addBatch();
        }
        ps.executeBatch();
    }
}
