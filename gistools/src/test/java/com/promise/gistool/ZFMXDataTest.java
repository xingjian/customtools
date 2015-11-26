package com.promise.gistool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.tongtu.nomap.core.transform.Gis84ToCehui;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述: 执法明细
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月25日 下午5:52:49  
 */
public class ZFMXDataTest {

    /**
     * 并提取城8区插入到bjqx_six
     */
    @Test
    public void testGetBeijingSixADCD() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/postgis";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        
        String url1 = "jdbc:postgresql://localhost:5432/zfsj";
        String username1 = "postgis";
        String passwd1 = "postgis";
        Connection connection1 = DBConnection.GetPostGresConnection(url1, username1, passwd1);
        
        
        String sql = "select name,ST_AsText(geom) wkt from beijingqx where name in ('东城区','西城区','海淀区','朝阳区','丰台区','石景山区','宣武区','崇文区')";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        
        String insert_sql = "insert into bjqx_six (id,name,the_geom) values(?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection1.prepareStatement(insert_sql);
        
        while(rs.next()){
            String name = rs.getString(1);
            String wkt = rs.getString(2);
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setString(2, name);
            ps.setString(3, wkt);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    /**
     * 并提取城区插入到bjqx
     */
    @Test
    public void testGetBeijingADCD() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/postgis";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        
        String url1 = "jdbc:postgresql://localhost:5432/zfsj";
        String username1 = "postgis";
        String passwd1 = "postgis";
        Connection connection1 = DBConnection.GetPostGresConnection(url1, username1, passwd1);
        
        
        String sql = "select name,ST_AsText(geom) wkt from beijingqx";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        
        String insert_sql = "insert into bjqx (id,name,the_geom) values(?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection1.prepareStatement(insert_sql);
        
        while(rs.next()){
            String name = rs.getString(1);
            String wkt = rs.getString(2);
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setString(2, name);
            ps.setString(3, wkt);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    /**
     * 合并行政区域
     * 东城区，西城区，海淀区，朝阳区，丰台区，石景山区
     * 宣武区合并到西城区 崇文区合并到东城区
     */
    @Test
    public void testJoinBeijingQX() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select name,ST_AsText(the_geom) wkt from bjqx where name in ('西城区','宣武区')";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        
        List<String> wktList = new ArrayList<String>();
        
        while(rs.next()){
            String name = rs.getString(1);
            String wkt = rs.getString(2);
            System.out.println(name + wkt);
            wktList.add(wkt);
        }
        Geometry joinGeom = GeoToolsGeometry.UnionManyGeo(wktList);
        
        String update_sql = "update bjqx set the_geom=st_geometryfromtext(?,4326) where name=?";
        PreparedStatement ps = connection.prepareStatement(update_sql);
        ps.setString(1, (joinGeom.toText()+")").replace("POLYGON", "MULTIPOLYGON("));
        ps.setString(2, "西城区");
        ps.addBatch();
        ps.executeBatch();
    }
    
    /**
     * 复制执法明细表
     */
    @Test
    public void testCopyZFMX() throws  Exception{
        String url = "jdbc:postgresql://localhost:5432/postgis";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String querySQL = "select id, chk_id, chk_check_date, chk_case_type, chk_trade_type, chk_enforce_name,"+ 
            "chk_person, chk_idcard, chk_driver_code, chk_vehicleid, chk_veh_color, "+
            "chk_veh_type, chk_addr, chk_district, chk_unit_name, chk_addr_code, "+
            "chk_deal_state, chk_transid, chk_peccancy_type, chk_punish, chk_peccancy_des,"+ 
            "createdate, updatedate, chk_action_name, chk_type_name, up_flag, "+
            "chk_insert_date, chk_dwbh, chk_jasj, chk_xwbm, chk_sftj, chk_ppjy, "+
            "chk_ajzt, ychdsj, chk_cfdx, chk_wfxzbh, jcr1, jcr2, x, y, ST_AsText(the_geom), "+
            "bjqy, value from zfmx where bjqy in ('东城区','西城区','海淀区','朝阳区','丰台区','石景山区','宣武区','崇文区')";
        String insertSQL = "INSERT INTO zfmx_data("+
            "id, chk_id, chk_check_date, chk_case_type, chk_trade_type, chk_enforce_name,"+ 
            "chk_person, chk_idcard, chk_driver_code, chk_vehicleid, chk_veh_color, "+
            "chk_veh_type, chk_addr, chk_district, chk_unit_name, chk_addr_code, "+
            "chk_deal_state, chk_transid, chk_peccancy_type, chk_punish, chk_peccancy_des,"+ 
            "createdate, updatedate, chk_action_name, chk_type_name, up_flag, "+
            "chk_insert_date, chk_dwbh, chk_jasj, chk_xwbm, chk_sftj, chk_ppjy, "+
            "chk_ajzt, ychdsj, chk_cfdx, chk_wfxzbh, jcr1, jcr2, x, y, the_geom, "+
            "bjqy, value)"+
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromText(?,4326), ?, ?);";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(querySQL);
        
        String url1 = "jdbc:postgresql://localhost:5432/zfsj";
        String username1 = "postgis";
        String passwd1 = "postgis";
        Connection connection1 = DBConnection.GetPostGresConnection(url1, username1, passwd1);
        PreparedStatement ps = connection1.prepareStatement(insertSQL);
        while(rs.next()){
            ps.setString(1, rs.getString(1)); 
            ps.setString(2, rs.getString(2));
            ps.setString(3, rs.getString(3));
            ps.setString(4, rs.getString(4));
            ps.setString(5, rs.getString(5));
            ps.setString(6, rs.getString(6)); 
            ps.setString(7, rs.getString(7));
            ps.setString(8, rs.getString(8));
            ps.setString(9, rs.getString(9));
            ps.setString(10, rs.getString(10));
            ps.setString(11, rs.getString(11)); 
            ps.setString(12, rs.getString(12));
            ps.setString(13, rs.getString(13));
            ps.setString(14, rs.getString(14));
            ps.setString(15, rs.getString(15));
            ps.setString(16, rs.getString(16)); 
            ps.setString(17, rs.getString(17));
            ps.setString(18, rs.getString(18));
            ps.setString(19, rs.getString(19));
            ps.setString(20, rs.getString(20));
            ps.setString(21, rs.getString(21)); 
            ps.setString(22, rs.getString(22));
            ps.setString(23, rs.getString(23));
            ps.setString(24, rs.getString(24));
            ps.setString(25, rs.getString(25));
            ps.setString(26, rs.getString(26)); 
            ps.setString(27, rs.getString(27));
            ps.setString(28, rs.getString(28));
            ps.setString(29, rs.getString(29));
            ps.setString(30, rs.getString(30));
            ps.setString(31, rs.getString(31)); 
            ps.setString(32, rs.getString(32));
            ps.setString(33, rs.getString(33));
            ps.setString(34, rs.getString(34));
            ps.setString(35, rs.getString(35));
            ps.setString(36, rs.getString(36)); 
            ps.setString(37, rs.getString(37));
            ps.setString(38, rs.getString(38));
            ps.setString(39, rs.getString(39));
            ps.setString(40, rs.getString(40));
            ps.setString(41, rs.getString(41)); 
            ps.setString(42, rs.getString(42));
            ps.setInt(43, rs.getInt(43));
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    /**
     * 导入执法区域
     */
    @Test
    public void testShapeToPostGISGXQ(){
        String shapePath = "G:\\项目文档\\执法数据空间分析\\数据\\guanxiaqu.shp";
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\执法数据空间分析\\数据\\guanxiaqu.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","guanxiaqu",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 导入终点区域
     */
    @Test
    public void testShapeToPostGISZDQY(){
        String shapePath = "G:\\项目文档\\执法数据空间分析\\数据\\重点区域\\执法重点区域.shp";
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\执法数据空间分析\\数据\\zdqy.properties");
        PrintUtil.PrintObject(mapping);
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "UTF-8","zdqy",mapping);
        Assert.assertEquals("success", result);
    }
    
    /**
     * 终点区域有多少个执法明细
     */
    @Test
    public void testZDQYAndZFMX() throws Exception {
        String query ="select name, ST_AsText(the_geom) from zdqy";
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while(rs.next()){
            String name = rs.getString(1);
            String wkt = rs.getString(2);
            Statement statement2 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query2 = "select * from zfmx_data t1 where ST_Within(t1.the_geom,ST_GeomFromText('"+wkt+"',4326) )='t'";
            ResultSet rs2 = statement2.executeQuery(query2);
            rs2.last();
            System.out.println(name + "------"+ rs2.getRow());
        }
    }
    
    @Test
    public void testImportFoToPostGIS() throws Exception{
        String foPath = "F:\\eclipsejeeworkspace\\com.tongtu.zfmx\\fo";
        List<String> list = PBFileUtil.ReadFileByLine(foPath);
        PrintUtil.PrintObject(list);
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String insertSQL = "insert into dbscan_fo_zfmx1026_2015_01tousu_13_14 (id,value,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        Iterator<String> iter = list.iterator();
        while(iter.hasNext()){
            String str = iter.next();
            String[] arr = str.split(",");
            Point point = GeoToolsGeometry.createPoint(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setInt(2, Integer.parseInt(arr[2]));
            ps.setString(3, point.toText());
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    //@Test
    public void conver84To02() throws Exception {
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String tableName = "bjqx";
        String sql_update = "update "+tableName+" set the_geom=st_geometryfromtext(?,4326) where id=?";
        String sql_query = "select id, st_astext(the_geom) from "+tableName;
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql_update);
        ResultSet rs = statement.executeQuery(sql_query);
        while(rs.next()){
            String id = rs.getString(1);
            String wkt = rs.getString(2);
            String wktCopy= wkt;
            Matcher matcher = pattern.matcher(wkt);
            while(matcher.find()){
                String temp = wkt.substring(matcher.start(),matcher.end());
                String[] xyArrTemp = temp.split(" ");
                double x_double = Double.parseDouble(xyArrTemp[0]);
                double y_double = Double.parseDouble(xyArrTemp[1]);
                double[] cehuiDouble = Gis84ToCehui.transform(x_double,y_double);
                wktCopy = wktCopy.replaceAll(temp, cehuiDouble[0]+" "+cehuiDouble[1]);
            }
            ps.setString(1, wktCopy);
            ps.setString(2, id);
            ps.addBatch();
       }
        ps.executeBatch();
    }
    
    /**
     * 获取excel表头生成字符串
     */
    //@Test
    public void testCreateTableString(){
        List<String> list = POIExcelUtil.GetExcelRowPinYin("G:\\项目文档\\执法数据空间分析\\文档\\2014年违章数据.xls", 0, 0);
        PrintUtil.PrintObject(list);
    }
    
    @Test
    public void testInsertZFMX1026() throws Exception{
        String insertSQL = "INSERT INTO zfmx1026_2015(id, anjianlaiyuan, xingyeleibie, zhifadadui, zhuzhi, cheliangpaihao, "+
            "cheshenyanse, cheshenfuse, cheliangxinghao, danweibianhao, danweimingcheng, "+
            "farendaibiao, weizhangxingwei, weifaqingxing, yanzhongweizhang, "+
            "yibanweizhang, jianchashijian, jianchadidian, weizhangshiyou, "+
            "jieanshijian, yicigouduishijian, chulizhuangtai, lujishijian, "+
            "chakouriqi, cheliangsuoyouren, dingcheruanjian,the_geom)"+
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?,  ?, ?, ?, ?,  ?, ?, ?,ST_GeomFromText(?,4326))";
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        List<String> list = POIExcelUtil.ReadXLS("G:\\项目文档\\执法数据空间分析\\文档\\2015年1-9月（含经纬度）.xls", "&&", 2,20724,1,27);
        int count = 0;
        for(String str:list){
            count++;
            String[] strArr = str.split("&&");
            ps.setString(1, StringUtil.GetUUIDString());
            ps.setString(2, strArr[0]);
            ps.setString(3, strArr[1]);
            ps.setString(4, strArr[2]);
            ps.setString(5, strArr[3]);
            ps.setString(6, strArr[4]);
            ps.setString(7, strArr[5]);
            ps.setString(8, strArr[6]);
            ps.setString(9, strArr[7]);
            ps.setString(10, strArr[8]);
            ps.setString(11, strArr[9]);
            ps.setString(12, strArr[10]);
            ps.setString(13, strArr[11]);
            ps.setString(14, strArr[12]);
            ps.setString(15, strArr[13]);
            ps.setString(16, strArr[14]);
            ps.setString(17, strArr[15]);
            ps.setString(18, strArr[16]);
            ps.setString(19, strArr[17]);
            ps.setString(20, strArr[18]);
            ps.setString(21, strArr[19]);
            ps.setString(22, strArr[20]);
            ps.setString(23, strArr[21]);
            ps.setString(24, strArr[22]);
            ps.setString(25, strArr[23]);
            ps.setString(26, strArr[24]);
            String x = strArr[25];
            String y =strArr[26];
            Point point = GeoToolsGeometry.createPoint(Double.parseDouble(x), Double.parseDouble(y));
            if(x.equals("")||x.equals("0")||y.equals("")||y.equals("0")){
                
            }else{
                ps.setString(27, point.toText());
                ps.addBatch();
            }
            if(count%5000==0){
                ps.executeBatch();
            }
        }
        ps.executeBatch();
    }
    
   // @Test
    public void testUpdatezfmx1026_2015_sixADCD() throws Exception{
        String queryADCD = "select * from bjqx_six_02";
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        Statement statement1 = connection.createStatement();
        String updateSQL = "update zfmx1026_2015_six set adcd=? where id=?";
        PreparedStatement ps = connection.prepareStatement(updateSQL);
        ResultSet rs1 = statement1.executeQuery(queryADCD);
        while(rs1.next()){
            String id = rs1.getString(1);
            String name = rs1.getString(2);
            Statement statement2 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String querySQL = "select id from zfmx1026_2015_six t1 where ST_Within(t1.the_geom,ST_GeomFromText((select ST_AsText(the_geom) from bjqx_six_02 where id='"+id+"'),4326) )='t'";
            ResultSet rs2 = statement2.executeQuery(querySQL);
            while(rs2.next()){
                String idTemp = rs2.getString(1);
                ps.setString(1, name);
                ps.setString(2, idTemp);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    
    /**
     * 导入终点区域
     */
    //@Test
    public void testZFMX20151026ShapeToPostGIS(){
        String shapePath = "G:\\项目文档\\执法数据空间分析\\数据\\zfmx1027_2015.shp";
        String url = "jdbc:postgresql://localhost:5432/zfsj";
        String username = "postgis";
        String passwd = "postgis";
        //List<String> attributes = ConversionUtil.GetShapeAttributes(shapePath, "UTF-8");
        //PrintUtil.PrintObject(attributes);
        Map<String,String> mapping = PBFileUtil.ReadPropertiesFile("G:\\项目文档\\执法数据空间分析\\数据\\zfmx1026_2015_six.properties");
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String result = ConversionUtil.ShapeToPostGIS(shapePath, connection, "GBK","zfmx1026_2015_six",mapping);
        Assert.assertEquals("success", result);
    }
}
