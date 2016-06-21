package com.promise.gistool;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;

/**  
 * 功能描述: 将通道多线的图生成单线
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年4月15日 下午2:03:27  
 */
public class HandleChannelTest {

    /**
     * 读取线的点
     * @throws Exception
     */
    @Test
    public void test1() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String channelName = "机场第二高速";
        String sql = "select name,st_astext(the_geom) from bus_channel_centerline_merge where name='"+channelName+"'";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        String insert = "insert into channel_point (id,name,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insert);
        int idIndex=1;
        while(rs.next()){
            String name = rs.getString(1);
            String wkt = rs.getString(2);
            MultiLineString ms = GeoToolsGeometry.createMLineByWKT(wkt);
            Coordinate[] points = ms.getCoordinates();
            for(Coordinate coor:points){
                Point point = GeoToolsGeometry.createPoint(coor.x, coor.y);
                ps.setString(1, idIndex+"");
                ps.setString(2, name);
                ps.setString(3, point.toText());
                idIndex++;
                ps.addBatch();
            }
        }
        ps.executeBatch();
    }
    
    
    
    /**
     * 合并点
     * @throws Exception
     */
    @Test
    public void test2() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);//58-170  170-557  557-873  873-988 1-58
        String channelName = "京通快速路辅路";//71 701 903  326  573
        String sql = "select st_astext(the_geom) from jtksl_point order by st_x(the_geom)";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        String insert = "insert into channel_mulitlinestring_split (name,the_geom) values (?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insert);
        int idIndex=0;
        List<Coordinate> list = new ArrayList<Coordinate>();
        while(rs.next()){
            String wkt = rs.getString(1);
            Point point  = GeoToolsGeometry.createPointByWKT(wkt);
            list.add(idIndex, new Coordinate(point.getX(),point.getY()));
            idIndex++;
        }
        Coordinate[] coorArr = new Coordinate[list.size()];
        for(int i=0;i<list.size();i++){
            coorArr[i] = list.get(i);
        }
        
        LineString lineString = GeoToolsGeometry.createLine(coorArr);
        ps.setString(1, channelName);
        ps.setString(2, GeoToolsGeometry.createMLine(new LineString[]{lineString}).toText());
        ps.addBatch();
        ps.executeBatch();
    }
    
    
    @Test
    public void recreateIndex() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String channelName = "南中轴路";
        String sql = "select id,st_astext(the_geom) from channel_point where name='"+channelName+"' order by st_y(the)";
        ResultSet rs = connection.createStatement().executeQuery(sql);
    }
    
    
    /**
     * 合并点
     * @throws Exception
     */
    @Test
    public void test3() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String channelName = "机场第二高速";
        String sql = "select id, st_astext(the_geom) from channel_point_jcdegs where type='0' order by st_y(the_geom)";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        String insert = "update channel_point_jcdegs set indexnum=? where id=?";
        PreparedStatement ps = connection.prepareStatement(insert);
        int idIndex=178;
        while(rs.next()){
            String idTemp = rs.getString(1);
            ps.setInt(1,idIndex);
            ps.setString(2, idTemp);
            idIndex++;
            ps.addBatch();
        }
        
        
        ps.executeBatch();
    }
    
    
    /**
     * 读取线的点
     * @throws Exception
     * 补录北苑路的情况
     */
    @Test
    public void test4() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String channelName = "阜成路1";
        String sql = "select name, st_astext(the_geom) from fcltemp";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        String insert = "insert into channel_point (id,name,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insert);
        int idIndex=1;
        while(rs.next()){
            String name = rs.getString(1);
            String wkt = rs.getString(2);
            MultiLineString ms = GeoToolsGeometry.createMLineByWKT(wkt);
            Coordinate[] points = ms.getCoordinates();
            for(Coordinate coor:points){
                Point point = GeoToolsGeometry.createPoint(coor.x, coor.y);
                ps.setString(1, idIndex+"");
                ps.setString(2, channelName);
                ps.setString(3, point.toText());
                idIndex++;
                ps.addBatch();
            }
        }
        ps.executeBatch();
    }
    
    
    /**
     * 修正京通快速路城内段
     * @throws Exception
     */
    @Test
    public void test1_repairjtksl() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select id,name,st_astext(the_geom) from bus_channel_geometry where id='c978c200906e430b82b9e3893b4d2b5f'";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        String insert = "insert into channel_point (id,name,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insert);
        int idIndex=1;
        while(rs.next()){
            String name = rs.getString(1);
            String wkt = rs.getString(2);
            MultiLineString ms = GeoToolsGeometry.createMLineByWKT(wkt);
            Coordinate[] points = ms.getCoordinates();
            for(Coordinate coor:points){
                Point point = GeoToolsGeometry.createPoint(coor.x, coor.y);
                ps.setString(1, idIndex+"");
                ps.setString(2, "京通快速路城内调整");
                ps.setString(3, point.toText());
                idIndex++;
                ps.addBatch();
            }
        }
        ps.executeBatch();
    }
    
    
    @Test
    public void test1_repairjtksl111() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sqlq = "select idlink,kindnum,kind from navigationlink_busline";
        String sql = "update navigationlink_busline set roadtype=? where idlink=?";
        connection.createStatement().execute("update navigationlink_busline set roadtype=null");
        ResultSet rs = connection.createStatement().executeQuery(sqlq);
        PreparedStatement ps = connection.prepareStatement(sql);
        while(rs.next()){
            String linkid = rs.getString(1);
            String kind = rs.getString(3);
            //String[] kindArr = kind.split("|");
            String str01 = kind.substring(0, 2);
            String str02 = kind.substring(2, 4);
            if(str01.equals("00")||str01.equals("01")||str01.equals("02")){
                if(!str02.equals("0a")&&!str02.equals("0b")&&!str02.equals("05")&&!str02.equals("04")&&!str02.equals("01")){
                    ps.setString(1, "1");
                    ps.setString(2, linkid);
                    ps.addBatch();
                }
               
            }
        }
        ps.executeBatch();
    }
    
    
    @Test
    public void receviceSocketGPS() throws Exception{
        Socket socket = new Socket("172.24.186.135", 9999);
        FileWriter fileWrite =  new FileWriter("G:\\项目文档\\公交都市\\gpsdata\\172-24-186-135-72.txt", true);
        int index = 0;
        int maxIndex = 9999;
        int indexFile = 73;
        boolean run = true;
        //创建一个流套接字并将其连接到指定主机上的指定端口号  
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("askdata:e1665817ef8fa0406e219b2299cb4ccfa49084899b14c6e4");
        while (run) {
            String str = in.readLine();
            fileWrite.write(str+"\n");
            index++;
            if(index>maxIndex){
                fileWrite.close();
                fileWrite = new FileWriter("G:\\项目文档\\公交都市\\gpsdata\\172-24-186-135-"+indexFile+".txt", true);
                indexFile++;
                index = 0;
            }
        }
    }
    
    @Test
    public void receviceSocketGPS11() throws Exception{
        Socket socket = socket = new Socket("172.24.186.135", 9999);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        socket.getOutputStream().write("askdata:e1665817ef8fa0406e219b2299cb4ccfa49084899b14c6e4\n".getBytes());
        while (true) {    
            //创建一个流套接字并将其连接到指定主机上的指定端口号  
            byte[] buffer = new byte[input.available()];
            if(buffer.length != 0){
                // 读取缓冲区
                input.read(buffer);
                // 转换字符串
                String three = new String(buffer);
                System.out.println("内容=" + three);
            }
        }    
    }
    
    
    @Test
    public void testchannel_zcl() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        String urllocalhost = "jdbc:postgresql://localhost:5432/basedata";
        String usernamelocalhost = "basedata";
        String passwdlocalhost = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        Connection connectionlocalhost = DBConnection.GetPostGresConnection(urllocalhost, usernamelocalhost, passwdlocalhost);
        String channelName = "机场第二高速";
        String sql = "select st_astext(st_union(the_geom)) from busline_channel_section where channelid= '20090b1c14a5427389f9ba303f76a30c'";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        String insert = "insert into channel_point (id,name,the_geom) values (?,?,ST_GeomFromText(?,4326))";
        PreparedStatement ps = connectionlocalhost.prepareStatement(insert);
        int idIndex=1;
        while(rs.next()){
            String name = "知春路";
            String wkt = rs.getString(1);
            MultiLineString ms = GeoToolsGeometry.createMLineByWKT(wkt);
            Coordinate[] points = ms.getCoordinates();
            for(Coordinate coor:points){
                Point point = GeoToolsGeometry.createPoint(coor.x, coor.y);
                ps.setString(1, idIndex+"");
                ps.setString(2, name);
                ps.setString(3, point.toText());
                idIndex++;
                ps.addBatch();
            }
        }
        ps.executeBatch();
    }
    
    
    
    /**
     * 合并点
     * @throws Exception
     */
    @Test
    public void test2_zcl() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        
        String urllocalhost = "jdbc:postgresql://localhost:5432/basedata";
       
        Connection connectionlocalhost = DBConnection.GetPostGresConnection(urllocalhost, username, passwd);
        
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);//58-170  170-557  557-873  873-988 1-58
        String channelName = "京通快速路辅路";//71 701 903  326  573
        String sql = "select st_astext(the_geom) from channel_point where name='知春路' order by st_x(the_geom)";
        ResultSet rs = connectionlocalhost.createStatement().executeQuery(sql);
        String insert = "update busline_channel_geometry set the_geom=ST_GeomFromText(?,4326) where id='20090b1c14a5427389f9ba303f76a30c'";
        PreparedStatement ps = connection.prepareStatement(insert);
        int idIndex=0;
        List<Coordinate> list = new ArrayList<Coordinate>();
        while(rs.next()){
            String wkt = rs.getString(1);
            Point point  = GeoToolsGeometry.createPointByWKT(wkt);
            list.add(idIndex, new Coordinate(point.getX(),point.getY()));
            idIndex++;
        }
        Coordinate[] coorArr = new Coordinate[list.size()];
        for(int i=0;i<list.size();i++){
            coorArr[i] = list.get(i);
        }
        LineString lineString = GeoToolsGeometry.createLine(coorArr);
        ps.setString(1, GeoToolsGeometry.createMLine(new LineString[]{lineString}).toText());
        ps.addBatch();
        ps.executeBatch();
    }
    
    @Test
    public void testHandleTrafficStationRef() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select id,st_astext(the_geom) from traffic_samll_bq6";
        String update = "update traffic_samll_bq6 set geom_unionstation=ST_GeomFromText(?,4326) where id=?";
        PreparedStatement ps1 = connection.prepareStatement(update);
        ResultSet rs = connection.createStatement().executeQuery(sql);
        int index = 0;
        while(rs.next()){
            index++;
            String id = rs.getString(1);
            String wkt = rs.getString(2);
            String sqlquery = "select st_astext(bthe_geom) from traffic_bq6samll_station_ref where id='"+id+"'";
            ResultSet rs1 = connection.createStatement().executeQuery(sqlquery);
            List<String> listWkt = new ArrayList<String>();
            while(rs1.next()){
                listWkt.add(rs1.getString(1));
            }
            if(listWkt.size()>0){
                Geometry geomUnion = GeoToolsGeometry.UnionManyGeo(listWkt);
                System.out.println(geomUnion.getGeometryType());
                if(geomUnion.getGeometryType().equals("Polygon")){
                    Polygon[] pArr = new Polygon[1];
                    pArr[0] = (Polygon)geomUnion;
                    geomUnion = GeoToolsGeometry.createMultiPolygon(pArr);
                }
                //Geometry result = GeoToolsGeometry.intersection(geomUnion, GeoToolsGeometry.createGeometrtyByWKT(wkt));
                ps1.setString(1, geomUnion.toText());
                ps1.setString(2, id);
                ps1.addBatch();
                System.out.println("union index->"+index +" ,id--->"+id);
            }
            
        }
        ps1.executeBatch();
    }
    
    /**
     * 求相交
     * @throws Exception
     */
    @Test
    public void testHandleTrafficStationRef2() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select id,st_astext(the_geom),st_astext(geom_unionstation) from traffic_samll_bq6";
        String update = "update traffic_samll_bq6 set geom_intersection_station=ST_GeomFromText(?,4326) where id=?";
        PreparedStatement ps1 = connection.prepareStatement(update);
        ResultSet rs = connection.createStatement().executeQuery(sql);
        int index = 0;
        while(rs.next()){
            index++;
            String id = rs.getString(1);
            String wkt = rs.getString(2);
            String wkt2 = rs.getString(3);
            if(null!=wkt2&&!wkt2.trim().equals("")){
                try{
                    Geometry g1 = GeoToolsGeometry.createGeometrtyByWKT(wkt2);
                    Geometry g2 =GeoToolsGeometry.createGeometrtyByWKT(wkt);
                    System.out.println(g1.isValid());
                    if(!g2.isValid()){
                        g2 = GeoToolsGeometry.buffer(g2, 0);
                    }
                    Geometry result = GeoToolsGeometry.intersection(g1,g2);
                    if(result.getGeometryType().equals("Polygon")){
                        Polygon[] pArr = new Polygon[1];
                        pArr[0] = (Polygon)result;
                        result = GeoToolsGeometry.createMultiPolygon(pArr);
                    }
                    ps1.setString(1, result.toText());
                    ps1.setString(2, id);
                    ps1.addBatch();
                }catch(TopologyException e){
                    System.out.println("union index->"+index +" ,id--->"+id);
                    //e.printStackTrace();
                }
                
            }
        }
        ps1.executeBatch();
    }
    
    
    /**
     * 计算各个行政区划和500覆盖率
     * @throws Exception
     */
    @Test
    public void testHandleTrafficStationRef3() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select id,st_astext(the_geom),st_astext(geom_unionstation) from traffic_samll_bq6";
        String update = "update traffic_samll_bq6 set geom_intersection_station=ST_GeomFromText(?,4326) where id=?";
        PreparedStatement ps1 = connection.prepareStatement(update);
        ResultSet rs = connection.createStatement().executeQuery(sql);
        int index = 0;
        while(rs.next()){
            index++;
            String id = rs.getString(1);
            String wkt = rs.getString(2);
            String wkt2 = rs.getString(3);
            if(null!=wkt2&&!wkt2.trim().equals("")){
                try{
                    Geometry g1 = GeoToolsGeometry.createGeometrtyByWKT(wkt2);
                    Geometry g2 =GeoToolsGeometry.createGeometrtyByWKT(wkt);
                    System.out.println(g1.isValid());
                    if(!g2.isValid()){
                        g2 = GeoToolsGeometry.buffer(g2, 0);
                    }
                    Geometry result = GeoToolsGeometry.intersection(g1,g2);
                    if(result.getGeometryType().equals("Polygon")){
                        Polygon[] pArr = new Polygon[1];
                        pArr[0] = (Polygon)result;
                        result = GeoToolsGeometry.createMultiPolygon(pArr);
                    }
                    ps1.setString(1, result.toText());
                    ps1.setString(2, id);
                    ps1.addBatch();
                }catch(TopologyException e){
                    System.out.println("union index->"+index +" ,id--->"+id);
                    //e.printStackTrace();
                }
                
            }
        }
        ps1.executeBatch();
    }
}
