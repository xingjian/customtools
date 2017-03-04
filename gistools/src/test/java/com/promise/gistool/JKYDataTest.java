package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoToolsUtil;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年2月13日 下午12:54:42  
 */
public class JKYDataTest {

    @Test
    public void importZhengZhouCity(){
        String shapePath = "C:\\Users\\xingjian\\Desktop\\中国行政区划数据shapefiles格式\\zhengzhouchengqu.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS( "localhost", "5432", "sw_navigation", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "UTF-8", "zhengzhouchengqu", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    
    
    @Test
    public void testCreateSquareByExtents() throws Exception{
        //获取范围  横、纵坐标最小值 113.444，34.5986 ：横、纵坐标最大值 113.86，34.9642
        double minx = 113.444;
        double miny = 34.5986;
        double maxx = 113.86;
        double maxy = 34.9642;
       
        String url = "jdbc:postgresql://localhost:5432/jkydata";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String insert_sql = "insert into zz_traffic_area_big (id,the_geom) values(?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insert_sql);
        //纬度1度 = 大约111km 
        //纬度1分 = 大约1.85km 
        //纬度1秒 = 大约30.9m
        //1英里= 63360 米
        //1米=1/1852 海里
        //1海里= 1/60度
        //如果要进行具体的运算，需要进行一下单位换算，比如要求一个500米的范围，那么应该是
        //500*1/1852*1/60（度）
        List<Polygon> list = GeoToolsUtil.CreateSquareByExtents(minx,miny,maxx,maxy,(double)4000/1852/60);
        for(int i=0;i<list.size();i++){
            Polygon pTemp = list.get(i);
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setString(2, pTemp.toText());
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
}
