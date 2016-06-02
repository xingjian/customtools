package com.promise.gistool;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBMathUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月30日 上午11:01:14  
 */
public class BaseDataTest {

    
    @Test
    public void testXNingqxShapeToPostGis(){
        String shapePath = "G:\\项目文档\\西宁交通\\gis\\xining\\xningqx.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "xiningtaffic", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "xningqx", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testShapeToPostGis(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\地图\\2014地图\\14S-G_beijing\\beijingshape\\POIbeijing.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "sw_navigation", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "poibeijing", Point.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testShapeToPostGisNew(){
        String shapePath = "G:\\项目文档\\公交都市\\通道数据\\beijingqx_disolve.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "192.168.1.105", "5432", "basedata", "basedata", "basedata");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "beijingborder", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testDBFToPostGis(){
        String dbfPath = "G:\\项目文档\\公交都市\\giss数据\\地图\\2014地图\\14S-G_beijing\\beijingshape\\PNamebeijing.dbf";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "sw_navigation", "postgis", "postgis");
        List<String> str = ConversionUtil.GetDBFAttributes(dbfPath, "GBK");
        ConversionUtil.DBFToPostGIS(dbfPath, "GBK", "pnamebeijing", dataStore);
        PrintUtil.PrintObject(str);
    }
    
    @Test
    public void testJoinBeijingQX() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select name,ST_AsText(the_geom) wkt from beijingqx where name in ('东城区','崇文区')";
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
        
        String update_sql = "update beijingqx set the_geom=st_geometryfromtext(?,4326) where name=?";
        PreparedStatement ps = connection.prepareStatement(update_sql);
        ps.setString(1, (joinGeom.toText()+")").replace("POLYGON", "MULTIPOLYGON("));
        ps.setString(2, "东城区");
        ps.addBatch();
        ps.executeBatch();
    }
    
    /**
     * 根据绘制好的shape,写入数据库表bus_channel
     */
    @Test
    public void insertBusChannel() throws Exception{
        String path = "G:\\项目文档\\公交都市\\通道数据\\";
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String insertSQL = "insert into bus_channel (id,name,direction,code,the_geom) values (?,?,?,?,st_geometryfromtext(?,4326))";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        Map<String,String> map = new HashMap<String, String>();
        map.put("liangkuanglu_shangxiang", "两广路");
        map.put("changanjie_shuangxiang", "长安街");
        map.put("pingandajie_shuangxiang", "平安大街");
        map.put("jingkai_shuangxiang", "京开高速");
        map.put("jga_shuangxiang", "京港澳高速");
        map.put("xdwl_shuangxiang", "西大望路");
        map.put("jingzang_shuangxiang", "京藏高速");
        map.put("sanhuan_shuangxiang", "三环路");
        map.put("zgcdj_shuangxiang", "中关村大街");
        map.put("jingtong_shuangxiang", "京通快速路");
        map.put("sihuan_shuangxiang", "四环辅路");
        map.put("erhuan_shuangxiang", "二环路");
        int code = 1;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String shapePath = path+key+".shp";
            SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures(shapePath, "GBK");
            SimpleFeatureIterator sfi = sfc.features();
            int direction = 1;
            while(sfi.hasNext()){
                SimpleFeature sf = sfi.next();
                String wkts = sf.getDefaultGeometry().toString();
                ps.setString(1, StringUtil.GetUUIDString());
                ps.setString(2, value);
                ps.setInt(3, direction);
                ps.setString(4, code+"");
                ps.setString(5,GISCoordinateTransform.From84To02(GISCoordinateTransform.From54To84(wkts)));
                direction = 2;
                ps.addBatch();
            }
            code++;
        }
        ps.executeBatch();
    }
    
    @Test
    public void test11111() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String querySQL = "select t1.id,t2.pre_station_distance from busstation_distance_temp t1 left join busstation t2 on t1.e_stationid=t2.id";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(querySQL);
       
        String updateSQL = "update busstation_distance_temp set distance=? where id=?";
        PreparedStatement ps = connection.prepareStatement(updateSQL);
        int i=0;
        while(rs.next()){
            String id = rs.getString(1);
            double lineCode = rs.getDouble(2);
            ps.setString(2, id);
            ps.setDouble(1, lineCode);
            ps.addBatch();
            i++;
            if(i%5000==0){
                ps.executeBatch();
            }
        }
        ps.executeBatch();
    }
    
    @Test
    public void testInsertDeBaseBusDistance() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String insertSQL = "INSERT INTO de_base_bus_distance(id, linecode, onstationid, onstationorder, offstationid, offstationorder, distance) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        String querySQL_BusLine = "select id,linecode from busline";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(querySQL_BusLine);
        Map<String,Integer> map1 = new HashMap<String, Integer>();
        while(rs.next()){
            String lineID = rs.getString(1);
            int lineCode = rs.getInt(1);
            map1.put(lineID, lineCode);
        }
        rs.close();
        for (Map.Entry<String,Integer> entry : map1.entrySet()) {
            String buslineID = entry.getKey();
            int lineCode = entry.getValue();
            String sql1 = "select id,buslineid,index,stationid,pre_station_distance from busstation where buslineid='"+buslineID+"' order by index";
            Statement statementTemp = connection.createStatement();
            ResultSet rsTemp = statementTemp.executeQuery(sql1);
            List<String[]> list = new ArrayList<String[]>();
            int i = 0;
            while(rsTemp.next()){
                String bsId = rsTemp.getString(1);
                String buslineid = rsTemp.getString(2);
                int s_index = rsTemp.getInt(3);
               // int stationid = 
            }
            
        }

        
    }
    
    public void testHandleResult(){
        
    }
    
    @Test
    public void testBusStationTrafficBig() throws Exception{
        String url = "jdbc:postgresql://123.117.25.216:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select code from huanmian";
        Statement statement = connection.createStatement();
        
        String sqlUpdata = "update busstation set ring_area_code = ? where id in (select b.id from huanmian a,busstation b where  1=1"+ 
                    " and ST_Within(b.the_geom, a.the_geom) and a.code = ?)";
        PreparedStatement ps = connection.prepareStatement(sqlUpdata);
        ResultSet rs = statement.executeQuery(sql);
        while(rs.next()){
            String code = rs.getString(1);
            System.out.println(" start code ==== "+ code);
            ps.setString(1, code);
            ps.setInt(2, Integer.parseInt(code));
            ps.executeUpdate();
            System.out.println(" end code ==== "+ code);
        }
    }
    
    @Test
    public void testCalcStastionDistance(){
        try{
        GeoToolsGeometry gtg = new GeoToolsGeometry();
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        //把需要的导航数据加载到内存中
        Map<String,NavigationObject> navigationMap = new HashMap<String, NavigationObject>();
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql_query_nl = "select t1.navigationid,ST_AsText(t2.the_geom) wkt,t2.direction from (select navigationid from buslinelink group by navigationid"+
                ") t1 left join navigationline t2 on t1.navigationid = t2.id";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql_query_nl);
        while(rs.next()){
            String navigationid = rs.getString(1);
            String wkt = rs.getString(2);
            String direct = rs.getString(3);
            NavigationObject no = new NavigationObject(navigationid, direct, wkt);
            navigationMap.put(navigationid, no);
        }
        
        //把需要的公交站点加载到内存中
        String sql_query_bsl = "select busstationid,ST_AsText(the_geom) wkt from busstationlink";
        Statement statement0 = connection.createStatement();
        ResultSet rs0 = statement0.executeQuery(sql_query_bsl);
        Map<String,String> busstationlinkMap = new HashMap<String, String>();
        while(rs0.next()){
            String busstationid = rs0.getString(1);
            String wkt = rs0.getString(2);
            busstationlinkMap.put(busstationid, wkt);
        }
        
        //
        String sqlQ = "select s_stationid,e_stationid,linkids,id from busstation_distance_2016_03_01 where distance >0";
        Statement statement1 = connection.createStatement();
        ResultSet rs1 = statement1.executeQuery(sqlQ);
        String updataBSDistance = "update busstation_distance_2016_03_01 set the_geom=st_geometryfromtext(?,4326) where id=?";
        PreparedStatement psBusStation = connection.prepareStatement(updataBSDistance);
        int index = 0;
        while(rs1.next()){
            String s_stationid = rs1.getString(1);
            String e_stationid = rs1.getString(2);
            String linkids = rs1.getString(3);
            String id = rs1.getString(4);
            String multilineWkt = "MULTILINESTRING(";
            String[] linkidArr = linkids.split(",");
            System.out.println(linkids);
            if(linkidArr.length>1){
                String s_nameWkt = busstationlinkMap.get(s_stationid);
                NavigationObject s_namelinkWkt = navigationMap.get(linkidArr[0]);
                String e_nameWkt = busstationlinkMap.get(e_stationid);
                NavigationObject e_namelinkWkt = navigationMap.get(linkidArr[linkidArr.length-1]);
                if(null==s_nameWkt){
                    System.out.println(s_stationid);//8d81de06fc9447dd880bd24ce2e90d0f
                    return;
                }
                List<String> wktList = pointSplitLine(s_nameWkt, s_namelinkWkt);
                MultiLineString subline1 = gtg.createMLineByWKT(wktList.get(0));
                MultiLineString subline2 = gtg.createMLineByWKT(wktList.get(1));
                MultiLineString naviLine = gtg.createMLineByWKT(navigationMap.get(linkidArr[1]).wkt);
                double dis1 = gtg.distanceGeo(subline1, naviLine);
                double dis2 = gtg.distanceGeo(subline2, naviLine);
                List<String> startString = null;
                if(dis1>dis2){//取dis2 wktList(1)
                    startString = getXYByWkt(wktList.get(1));
                }else{//取dis1 wktList(2)
                    startString = getXYByWkt(wktList.get(0));
                }
                String startStringwkt="(";
                for(int k=0;k<startString.size();k++){
                    if(k==startString.size()-1){
                        startStringwkt +=startString.get(k)+"),";
                    }else{
                        startStringwkt +=startString.get(k)+",";
                    }
                }
                multilineWkt +=startStringwkt;
                
                if(linkidArr.length>2){
                    for(int i=1;i<linkidArr.length-1;i++){
                        String middleLineWkt="(";
                        NavigationObject navigationWkt = navigationMap.get(linkidArr[i]);
                        List<String> middStrList = getXYByWkt(navigationWkt.wkt);
                        for(int u = 0;u<middStrList.size();u++){
                            if(u==middStrList.size()-1){
                                middleLineWkt += middStrList.get(u)+"),";
                            }else{
                                middleLineWkt += middStrList.get(u)+",";
                            }
                            
                        }
                        multilineWkt +=middleLineWkt;
                    }
                }
                
                List<String> wktList1 = pointSplitLine(e_nameWkt, e_namelinkWkt);
                MultiLineString subline3 = gtg.createMLineByWKT(wktList1.get(0));
                MultiLineString subline4 = gtg.createMLineByWKT(wktList1.get(1));
                MultiLineString naviLine1 = gtg.createMLineByWKT(navigationMap.get(linkidArr[linkidArr.length-2]).wkt);
                double dis3 = gtg.distanceGeo(subline3, naviLine1);
                double dis4 = gtg.distanceGeo(subline4, naviLine1);
                List<String> endString = null;
                
                if(dis3>dis4){//取dis2 wktList(1)
                    endString = getXYByWkt(wktList1.get(1));
                }else{//取dis1 wktList(2)
                    endString = getXYByWkt(wktList1.get(0));
                }
                
                String endStringwkt="(";
                for(int h=0;h<endString.size();h++){
                    if(h==endString.size()-1){
                        endStringwkt +=endString.get(h)+")";
                    }else{
                        endStringwkt +=endString.get(h)+",";
                    }
                } 
                multilineWkt +=endStringwkt;
                multilineWkt +=")";
                
                psBusStation.setString(1, multilineWkt);
                psBusStation.setString(2, id);
                psBusStation.addBatch();
                index++;
                System.out.println("Index value ==== "+ index);
                if(index%10000==0){
                    psBusStation.executeBatch();
                }
            }
            
        }
        psBusStation.executeBatch();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //220722198209035620 
    public List<String> pointSplitLine(String pointWkt,NavigationObject lineWkt){
        List<String> retList = new ArrayList<String>();
        List<String> pointList = getXYByWkt(pointWkt);
        String pointStr = pointList.get(0);
        String[] xyArr = pointStr.split(" ");
        double pointx = Double.parseDouble(xyArr[0]);
        double pointy = Double.parseDouble(xyArr[1]);
        List<String> linePointList =getXYByWkt(lineWkt.wkt);
        WKTObject[] arrTemp = new WKTObject[linePointList.size()];
        if(linePointList.size()==2){//线只有2个点表示的话，就直接将点插入
            String multilineWkt1 = "MULTILINESTRING(";
            multilineWkt1 +="("+linePointList.get(0)+","+pointStr+"))";
            String multilineWkt2 = "MULTILINESTRING(";
            multilineWkt2 +="("+pointStr+","+linePointList.get(1)+"))";
            retList.add(0,multilineWkt1);
            retList.add(1,multilineWkt2);
        }else{//一个线多个点组成，先求最近距离，将点插入到中间
            for(int i=0;i<linePointList.size();i++){
                String[] xyArrLine = linePointList.get(i).split(" ");
                double pointx2 = Double.parseDouble(xyArrLine[0]);
                double pointy2 = Double.parseDouble(xyArrLine[1]);
                double dis = PBMathUtil.Distance(pointx, pointy, pointx2, pointy2);
                arrTemp[i] = new WKTObject(i,dis);
            }
            Arrays.sort(arrTemp, new Comparator(){
                @Override
                public int compare(Object o1, Object o2) {
                    WKTObject w1 = (WKTObject)o1;
                    WKTObject w2 = (WKTObject)o2;
                    return (w1.distance < w2.distance ? -1 : (w1.distance == w2.distance ? 0 : 1));
                }
            });
            int index1 = arrTemp[0].index;
            int index2 = arrTemp[1].index;
            int a = index1;
            if(index1<index2){
                a = index2;
            }
            String multilineWkt3 = "MULTILINESTRING((";
            for(int j = 0;j<a;j++){
                multilineWkt3 +=linePointList.get(j)+",";
            }
            multilineWkt3 +=pointStr+"))";
            retList.add(0, multilineWkt3);
            
            String multilineWkt4 = "MULTILINESTRING(("+pointStr+",";
            for(int j = a;j<linePointList.size();j++){
                if(j==linePointList.size()-1){
                    multilineWkt4 +=linePointList.get(j)+"))";
                }else{
                    multilineWkt4 +=linePointList.get(j)+",";
                }
            }
            retList.add(1, multilineWkt4);
        }
        return retList;
    }
    
    /**
     * 抽取wkt当中的xy对
     * @param wkt
     * @return
     */
    public List<String> getXYByWkt(String wkt){
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        try{
            Matcher matcher = pattern.matcher(wkt);
            int i=0;
            while(matcher.find()){
                result.add(i, matcher.group());
                i++;
            } 
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(wkt);
        }
        return result;
    }
    
    /**
     * 测试3点画圆
     */
    @Test
    public void testCreateCircle(){
        double xa = 115.904032294708;
        double ya = 40.3900589071082;
        double xb = 117.56336536464;
        double yb = 40.1482798370384;
        double xc = 116.288229173176;
        double yc = 38.8698316035186;
        double[] result = PBMathUtil.CreateCircleByPoint(xa, ya, xb, yb, xc, yc);
        Polygon pResult = GeoToolsGeometry.createCircle(result[0], result[1], result[2], 64);
        System.out.println(pResult.toText());
    }
    
    @Test
    public void testFrontIntersection(){
        double xa = 115.904032294708;
        double ya = 40.3900589071082;
        double xb = 117.56336536464;
        double yb = 40.1482798370384;
        double[] result = PBMathUtil.CalcFrontIntersection(xa, ya, xb, yb, Math.PI/2, Math.PI/6);
        Point point = GeoToolsGeometry.createPoint(result[0], result[1]);
        System.out.println(point.toText());
    }
    
    @Test
    public void tesArea3tShapeToPostGis(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\四维行政区划\\beijingqx_new.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "localhost", "5432", "sw_navigation", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "beijingqx", MultiPolygon.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void tesZhanWeiShapeToPostGis(){
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\给交委数据\\北京_公交站位_font_point.shp";
        DataStore dataStore = GISDBUtil.ConnPostGis("postgis", "ttyjbj.ticp.net", "5432", "basedata", "basedata", "basedata!@#&*(");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "busstation_platform", Point.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void mergeShapeFileGeometry() throws Exception{
        String pgurl = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, username, passwd);
        String insertSQL = "insert into bus_channel_centerline (id,channelid,name,the_geom) values (?,?,?,st_geometryfromtext(?,4326))";
        PreparedStatement ps = connectionPG.prepareStatement(insertSQL);
        Map<String,String> map = new HashMap<String,String>();
        map.put("all.shp","安立路");
        map.put("byl_channel.shp","北苑路");
        map.put("cnj_channel.shp","长安街");
        map.put("cyl_channel.shp","朝阳路");
        map.put("fcl_channel.shp","阜成路");
        map.put("fsl_channel.shp","阜石路");
        map.put("jcdegs_channel.shp","机场第二高速");
        map.put("jcgs_channel.shp","京承高速");
        map.put("jgags_channel.shp","京港澳高速");
        map.put("jkgs_channel.shp","京开高速");
        map.put("jml_channel.shp","京密路");
        map.put("jtksl_channel.shp","京通快速路");
        map.put("jzgs_channel.shp","京藏高速");
        map.put("lgl_channel.shp","两广路");
        map.put("lsl_channel.shp","莲石路");
        map.put("nzzl_channel.shp","南中轴路");
        map.put("padj_channel.shp","平安大街");
        map.put("wqhl_channel.shp","万泉河路");
        map.put("xdwl_channel.shp","西大望路");
        map.put("zgcdj_channel.shp","中关村大街");
        map.put("zzyl_channel.shp","紫竹院路");
        String filePath = "G:\\项目文档\\公交都市\\giss数据\\合并通道中心线\\";
        File fileDic = new File(filePath);
        File[] fileArr = fileDic.listFiles();
        for(File file : fileArr){
            if(file.getName().endsWith(".shp")){
                System.out.println(file.getName());
                SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures(filePath+file.getName(), "GBK");
                SimpleFeatureIterator sfi = sfc.features();
                String channelid = StringUtil.GetUUIDString();
                while(sfi.hasNext()){
                    SimpleFeature sf = sfi.next();
                    Geometry geom = (Geometry)sf.getDefaultGeometry();
                    String id = StringUtil.GetUUIDString();
                    ps.setString(1, id);
                    ps.setString(2, channelid);
                    if(file.getName().equals("bus_channel_centerline.shp")){
                        ps.setString(2, StringUtil.GetUUIDString());
                        if(null==sf.getAttribute("name")){
                            ps.setString(3, file.getName());
                        }else{
                            ps.setString(3, sf.getAttribute("name").toString());
                        }
                    }else{
                        ps.setString(3, map.get(file.getName()));
                    }
                    ps.setString(4, geom.toText());
                    ps.addBatch();
                    
                }
            }
        }
        ps.executeBatch();
    }
    
    @Test
    public void testMergeBuslinechannelcenterline() throws Exception{
        String sql = "select name,st_astext(the_geom) from bus_channel_centerline";
        String pgurl = "jdbc:postgresql://localhost:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(pgurl, username, passwd);
        ResultSet rs = connectionPG.createStatement().executeQuery(sql);
        Map<String,List<String>> map= new HashMap<String,List<String>>();
        while(rs.next()){
            String nameStr = rs.getString(1);
            String wktStr = rs.getString(2);
            if(null==map.get(nameStr)){
                List<String> list = new ArrayList<String>();
                list.add(wktStr);
                map.put(nameStr, list);
            }else{
                map.get(nameStr).add(wktStr);
            }
        }
        rs.close();
        String inserSQL = "insert into bus_channel_centerline_merge (name,the_geom) values (?,st_geometryfromtext(?,4326))";
        PreparedStatement ps = connectionPG.prepareStatement(inserSQL);
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> wkts = entry.getValue();
            List<Geometry> listGeo = new ArrayList<Geometry>();
            for(String wltStr : wkts){
                listGeo.add(GeoToolsGeometry.createGeometrtyByWKT(wltStr));
            }
            Geometry unionGeo = null;
            if(null!=listGeo.get(0)){
              unionGeo = listGeo.get(0);
              for(int i=1;i<listGeo.size();i++){
                unionGeo = unionGeo.union(listGeo.get(i));
              }
            }
            ps.setString(1, entry.getKey());
            ps.setString(2, unionGeo.toText());
            ps.addBatch();
        }
        ps.executeBatch();
        DBConnection.CloseConnection(connectionPG);
    }
    
    /**
     * 处理公交站点adcd为空的情况
     */
    @Test
    public void handleBustationAdcd() throws Exception{
        String sql = "select t1.code,st_astext(t2.the_geom) from beijingqx t1 left join beijingqx_new t2 on t1.name=t2.name";
        String urlttyj = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        ResultSet rs = connection.createStatement().executeQuery(sql);
        Map<String, Geometry> map1 = new HashMap<String, Geometry>();
        while(rs.next()){
            String adcdCode = rs.getString(1);
            String wkt = rs.getString(2);
            System.out.println(adcdCode);
            System.out.println(wkt);
            map1.put(adcdCode, GeoToolsGeometry.createGeometrtyByWKT(wkt));
        }
        String updateSQL = "update busstation set adcdcode=? where id=?";
        PreparedStatement ps = connection.prepareStatement(updateSQL);
        String sql2 = "select  id,st_astext(the_geom) from busstation where adcdcode is null";
        ResultSet rs2 = connection.createStatement().executeQuery(sql2);
        while(rs2.next()){
            String id = rs2.getString(1);
            String wkt = rs2.getString(2);
            Geometry point = GeoToolsGeometry.createGeometrtyByWKT(wkt);
            for (Map.Entry<String, Geometry> entry : map1.entrySet()) {
                String adcdTemp = entry.getKey();
                
                Geometry geom = entry.getValue();
                if(geom.contains(point) ||geom.intersects(point)){
                    ps.setString(1, adcdTemp);
                    ps.setString(2, id);
                    ps.addBatch();
                    break;
                }
               
            }
        }
        ps.executeBatch();
    }
}

