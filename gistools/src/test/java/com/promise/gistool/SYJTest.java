package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.GeoShapeUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月11日 下午4:08:42  
 */
public class SYJTest {

    /**
     *导入点变shape 
     */
    @Test
    public void testInsertPointByTxt(){
        String url = "jdbc:postgresql://localhost:5432/mobile";
        String username = "postgis";
        String passwd = "postgis";
        String insertSQL = "insert into syj_point_test(id,x,y,count,dataType,geometry) values (?,?,?,?,?,ST_GeomFromText(?,4326))";
        String txtpath = "C:\\Users\\xingjian\\Desktop\\points.txt";
        String splitChar = ";";
        String crs = "EPSG:4326";
        String encoding = "GBK";
        //geometry type 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
        String geometryType = "1";
        String topath = "F:\\points.shp";
        String[] attriDesc = new String[]{"the_geom:geometry-wkt"};
        GeoShapeUtil.CreateShapeByTxt(txtpath, splitChar, crs, encoding, attriDesc, topath, geometryType);
    }
    
    @Test
    public void testInsertPointByTxtOne() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/mobile";
        String username = "postgis";
        String passwd = "postgis";
        String insertSQL = "insert into syj_point_test(id,x,y,count,dataType,the_geom) values (?,?,?,?,?,ST_GeomFromText(?,4326))";
        String txtpath = "C:\\Users\\xingjian\\Desktop\\result-1358.txt";
        List<String> list = PBFileUtil.ReadFileByLine(txtpath);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        for(String str:list){
            String[] arr = str.split(",");
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setDouble(2,Double.parseDouble(arr[0]));
            ps.setDouble(3,Double.parseDouble(arr[1]));
            ps.setInt(4, 0);
            ps.setString(5, arr[2]);
            ps.setString(6, "Point("+arr[0]+" "+arr[1]+")");
            ps.addBatch();
        }
        ps.executeBatch();
    }
}
