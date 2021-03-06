package com.promise.gistool;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoToolsGeometry;
import com.tongtu.nomap.core.transform.BeijingToGis84;
import com.tongtu.nomap.core.transform.CoordinateConvert;

/**  
 * 功能描述: 坐标转换测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月11日 上午11:52:57  
 */
public class GISCoordinateTransformTest {

    @Test
    public void testFrom84To900913(){
        double[] ret = GISCoordinateTransform.From84To900913(116.397066,39.9172);
        BigDecimal bd = new BigDecimal(ret[0]);
        double[] ret1 = GISCoordinateTransform.From84To900913(116.373583984375,39.9260503472222);
        double[] ret2 = GISCoordinateTransform.From84To900913(116.502667643229,39.8742363823785);
        double dis = GeoToolsGeometry.distanceGeo(GeoToolsGeometry.createPoint(ret1[0],ret1[1]), GeoToolsGeometry.createPoint(ret2[0],ret2[1]));
        System.out.println(dis);
    }
    @Test
    public void testFrom84To900913Second(){
        double[] ret = GISCoordinateTransform.From84To900913(101.778534,36.616347);
        BigDecimal bd = new BigDecimal(ret[0]);
        System.out.println(bd+","+ret[1]);
    }
    
    @Test
    public void testFrom54To02ShapeFile(){
        String inputShapeFile = "C:\\Users\\xingjian\\Desktop\\080305\\080305_划线车位_夜间.shp";
        String outputShapleFile = "C:\\Users\\xingjian\\Desktop\\080305\\080305_划线车位_夜间04.shp";
        try {
           String result = GISCoordinateTransform.From54To02(inputShapeFile, outputShapleFile, "GBK");
           System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testFrom84To02ShapeFile(){
        String inputShapeFile = "G:\\项目文档\\停车场\\excelgis\\tccline_tab.shp";
        String outputShapleFile = "G:\\tccline_tab.shp";
        try {
           String result = GISCoordinateTransform.From84To02(inputShapeFile, outputShapleFile, "GBK");
           System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testJAVA(){
        String inputShapeFile = "G:\\项目文档\\微波路段\\城市道路84+北京地方\\城市道路84-全属性\\道路\\道路全属性-按技术等级\\主干路_Project.shp";
        String outputShapleFile = "G:\\项目文档\\微波路段\\城市道路84+北京地方\\城市道路84-全属性\\道路\\道路全属性-按技术等级\\ZGL02.shp";
        String outputShapleFileStr = outputShapleFile.substring(0,outputShapleFile.lastIndexOf(".shp"))+".prj";
        System.out.println(outputShapleFileStr);
    }
    
    
    @Test
    public void testFrom54To84ShapeFile(){
        String[] strArr = new String[]{"石景山_划线车位_日间","石景山_划线车位_夜间","石景山_随意车位_夜间","石景山_随意停车_日间","石景山_有效通行宽度_日间","石景山_有效通行宽度_夜间"};
        String inputShapeFile = "G:\\项目文档\\停车场\\合并数据(54坐标转84坐标)\\合并数据\\";
        String outputShapleFile = "d:\\cp\\";
        try {
            for(String str : strArr){
                System.out.println(str);
                String result = GISCoordinateTransform.From54To84(inputShapeFile+str+".shp", outputShapleFile+str+".shp", "GBK");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 测试西宁坐标转换
     * @throws Exception
     */
    @Test
    public void conver02To84() throws Exception {
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String url = "jdbc:postgresql://localhost:5432/xiningtaffic";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String tableName = "xiningnew2";
        String sql_update = "update "+tableName+" set the_geom=st_geometryfromtext(?,4326) where gid=?";
        String sql_query = "select gid, st_astext(geom) from "+tableName;
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_update);
        ResultSet rs = statement.executeQuery(sql_query);
        int i=1;
        while(rs.next()){
            int id = rs.getInt(1);
            String wkt = rs.getString(2);
            System.out.println(id +"-----"+i);
            String wktCopy= wkt;
            Matcher matcher = pattern.matcher(wkt);
            while(matcher.find()){
                String temp = wkt.substring(matcher.start(),matcher.end());
                String[] xyArrTemp = temp.split(" ");
                double x_double = Double.parseDouble(xyArrTemp[0]);
                double y_double = Double.parseDouble(xyArrTemp[1]);
                double[] cehuiDouble = CoordinateConvert.gcj2WGSExactly(y_double,x_double);
                System.out.println(x_double+"----"+y_double+"***"+cehuiDouble[0]+"----"+cehuiDouble[1]);
                System.out.println(temp +":"+cehuiDouble[1]+" "+cehuiDouble[0]);
                wktCopy = wktCopy.replaceAll(temp, cehuiDouble[1]+" "+cehuiDouble[0]);
            }
            ps.setString(1, wktCopy);
            ps.setInt(2, id);
           ps.addBatch();
            i++;
       }
        ps.executeBatch();
    }
    
    
    public void test84to02Point(){
        double[] xy = new double[]{116.300928,39.883084};
    }
    
    @Test
    public void test02to900913Point(){
//        double[] ret = GISCoordinateTransform.From84To900913(113.70041,31.36125);
//        double[] ret1 = GISCoordinateTransform.From84To900913(115.08081,29.96664);
//        double[] ret = GISCoordinateTransform.From84To900913(114.13163479739977,30.700547250000003);
//        double[] ret1 = GISCoordinateTransform.From84To900913(114.67519520260022,30.37306275);
//        BigDecimal bd = new BigDecimal(ret[0]);
//        BigDecimal bd1 = new BigDecimal(ret1[0]);
//        System.out.println(bd+"  "+ret[1]);
//        System.out.println(bd1+"  "+ret1[1]);
        double[] wgs84XYArr = BeijingToGis84.transSingle(534845, 398628);
        System.out.println(wgs84XYArr[0]+"----"+wgs84XYArr[1]);
        System.out.println(wgs84XYArr);
    }
    
    
    @Test
    public void testFrom84To02ShapeFile1(){
        String inputShapeFile = "D:\\2017_01_12_武汉公交基础数据_new\\2017_01_12_武汉公交基础数据\\wh_busline_4326_new.shp";
        String outputShapleFile = "G:\\公交线路_02.shp";
        try {
           String result = GISCoordinateTransform.From84To02(inputShapeFile, outputShapleFile, "GBK");
           System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 程磊坐标转换测试
     * @throws Exception
     */
    @Test
    public void test11111() throws Exception{
        String pgurl = "jdbc:postgresql://localhost:5432/sw_navigation";
        String pgusername = "postgis";
        String pgpasswd = "postgis";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, pgusername, pgpasswd);
        String sql1 = "select fid,\"GPSLongtit\",\"GPSLatitud\" from busstation_clwh";
        String upsql = "update busstation_clwh set x02=?,y02=? where fid=?";
        ResultSet rs = connectionPG.createStatement().executeQuery(sql1);
        while(rs.next()){
            int fid = rs.getInt(1);
            String x02 = rs.getString(2);
            String y02 = rs.getString(3);
            double[] dd = GISCoordinateTransform.From84To02(Double.parseDouble(x02), Double.parseDouble(y02));
        }
    }
    
}
