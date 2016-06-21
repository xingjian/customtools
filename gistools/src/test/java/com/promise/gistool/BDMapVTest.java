package com.promise.gistool;

import java.sql.Connection;
import java.sql.ResultSet;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiLineString;

/**  
 * 功能描述: 百度mapv数据生成
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月1日 下午10:44:20  
 */
public class BDMapVTest {

    @Test
    public void testCreateMapVData() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String querySQL = "select count,st_astext(the_geom) from buslineheatmap where substring(kind,0,3) in ('00','01','02','03','04')";
        ResultSet rs = connection.createStatement().executeQuery(querySQL);
        int maxCount = 0;
        StringBuffer sbTemp = new StringBuffer();
        sbTemp.append("var driveData = [");
        while(rs.next()){
            sbTemp.append("{\"geo\":[");
            String wkt = rs.getString(2);
            MultiLineString  mlTemp = GeoToolsGeometry.createMLineByWKT(wkt);
            Coordinate[] points = mlTemp.getCoordinates();
            for(int i=0;i<points.length;i++){
                Coordinate coor = points[i];
                double[] arr =GISCoordinateTransform.From84ToBD09(coor.x, coor.y);
                if(i==points.length-1){
                    sbTemp.append("["+arr[0]+","+arr[1]+"]");
                }else{
                    sbTemp.append("["+arr[0]+","+arr[1]+"],");
                }
            }
            
            int count = rs.getInt(1);
            if(count>maxCount){
                maxCount = count;
            }
            sbTemp.append("],");
            sbTemp.append("\"count\":\""+count+"\"},");
        }
        sbTemp.append("]");
        PBFileUtil.WriteStringToTxt(sbTemp.toString(), "d:\\buslinedata.js");
    }
    
    @Test
    public void testCreateMapVDataBusLineChannel() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String querySQL = "select st_astext(the_geom),buslinecount from busline_channel_geometry where linetype='1'";
        ResultSet rs = connection.createStatement().executeQuery(querySQL);
        StringBuffer sbTemp = new StringBuffer();
        int maxCount = 0;
        sbTemp.append("var driveData = [");
        while(rs.next()){
            sbTemp.append("{\"geo\":[");
            String wkt = rs.getString(1);
            MultiLineString  mlTemp = GeoToolsGeometry.createMLineByWKT(wkt);
            Coordinate[] points = mlTemp.getCoordinates();
            for(int i=0;i<points.length;i++){
                Coordinate coor = points[i];
                double[] arr =GISCoordinateTransform.From84ToBD09(coor.x, coor.y);
                if(i==points.length-1){
                    sbTemp.append("["+arr[0]+","+arr[1]+"]");
                }else{
                    sbTemp.append("["+arr[0]+","+arr[1]+"],");
                }
                
            }
            int count = rs.getInt(2);
            if(count>maxCount){
                maxCount = count;
            }
            sbTemp.append("],");
            sbTemp.append("\"count\":"+count+"},");
            
        }
        sbTemp.append("]");
        System.out.println(maxCount);
        PBFileUtil.WriteStringToTxt(sbTemp.toString(), "d:\\buslinechanneldata.js");
    }
}
