package com.promise.gistool;

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

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PBMathUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;

/**  
 * 功能描述: db开头表数据生成
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年4月13日 上午11:02:24  
 */
public class BDTableDataGeneTest {

    /**
     * 公交站点合并表 默认最大有效时间设定为2050-01-01
     */
    @Test
    public void testBD_Busstation_Merge() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String username = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, username, passwd);
        
    }
    /**
     * 线路站点区间静态表
     * 根据busline表中valid=1生成
     * 最大有效时间设定为2050-01-01,生成之前将上一次的结束时间进行修改
     * bd_section_busline_station_ref
     * 注意每次执行前请修改表中endtime和valid
     */
    @Test
    public void testBD_Section_Busline_Station_Ref() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, userName, passwd);
        String sql = "select id,label,arrow,linecode from busline where isvalid='1'";
        String insertSQL = "INSERT INTO bd_section_busline_ref(id, dmname, buslineid, linelable, code, linecode, arraw, startcode, startname, startindex, endcode, endname, endindex, starttime," 
            +" endtime, isvalid)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?)";
        PreparedStatement ps = connection.prepareStatement(insertSQL);
        ResultSet rs = connection.createStatement().executeQuery(sql);
        int busLineIndex = 0;
        while(rs.next()){
            String buslineId = rs.getString(1);
            String lable = rs.getString(2);
            String arrow = rs.getString(3);
            String xlbh = rs.getString(4);
            String sqlSub = "select name,index,stationid from busstation where buslineid='"+buslineId+"' order by index";
            ResultSet rsSub = connection.createStatement().executeQuery(sqlSub);
            String p_stationName = "";
            String p_stationIndex = "";
            String p_cardCode = "";
            int index = 0;
            while(rsSub.next()){
                index++;
                String stationName = rsSub.getString(1);
                String stationIndex = rsSub.getString(2);
                String cardCode = rsSub.getString(3);
                if(index>1){
                    ps.setString(1, StringUtil.GetUUIDString());
                    ps.setString(2, p_stationName+"-"+stationName);
                    ps.setString(3, buslineId);
                    ps.setString(4, lable);
                    ps.setString(5, xlbh+"_"+arrow+"_"+cardCode+"_"+stationIndex);
                    ps.setString(6, xlbh);
                    ps.setString(7, arrow);
                    ps.setString(8, p_cardCode);
                    ps.setString(9, p_stationName);
                    ps.setString(10, p_stationIndex);
                    ps.setString(11, cardCode);
                    ps.setString(12, stationName);
                    ps.setString(13, stationIndex);
                    ps.setString(14, "2014-12-01");
                    ps.setString(15, "2050-01-01");
                    ps.setString(16, "1");
                    ps.addBatch();
                }
               p_stationName = stationName;
               p_stationIndex = stationIndex;
               p_cardCode = cardCode;
            }
            ps.executeBatch();
            rsSub.close();
            busLineIndex++;
            System.out.println(busLineIndex+":处理公交线路->"+lable);
        }
    }
    
    /**
     * 抽取wkt当中的xy对
     * @param wkt
     * @return
     */
    public List<String> getXYByWkt(String wkt){
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(wkt);
        int i=0;
        while(matcher.find()){
            result.add(i, matcher.group());
            i++;
        }
        return result;
    }
    
    public List<String> pointSplitLine(String pointWkt,String lineWkt){
        List<String> retList = new ArrayList<String>();
        List<String> pointList = getXYByWkt(pointWkt);
        String pointStr = pointList.get(0);
        String[] xyArr = pointStr.split(" ");
        double pointx = Double.parseDouble(xyArr[0]);
        double pointy = Double.parseDouble(xyArr[1]);
        List<String> linePointList =getXYByWkt(lineWkt);
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
     * 更新bd_section_busline_ref wkt
     * 首先更新busstation_distance_2016_04_14 计算两个站点之间的路链关系 在计算空间，最后根据buslineid sname ename 更新到bd_section_busline_ref
     * 在根据路链进行计算
     */
    @Test
    public void testUpdatebdsectionbuslineref_wkt() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, userName, passwd);
        //初始化navigatiolien数据到内存
        String sqlNavigation = "select id ,st_astext(the_geom) from navigationline where id in (select navigationid from buslinelink where buslineid in (select id from busline where isvalid='1'))";
        Map<String,String> naviMap = new HashMap<String,String>();
        ResultSet rsNav = connection.createStatement().executeQuery(sqlNavigation);
        while(rsNav.next()){
            naviMap.put(rsNav.getString(1), rsNav.getString(2));
        }
        rsNav.close();
        //加载站点和navigationid关系
        String sqlbusstation = "select busstationid,linkid,st_astext(the_geom) from busstationlink where busstationid in (select id from busstation where buslineid in (select id from busline where isvalid='1'))";
        ResultSet rsbusstation = connection.createStatement().executeQuery(sqlbusstation);
        Map<String,String> mapStation = new HashMap<String,String>();
        while(rsbusstation.next()){
            mapStation.put(rsbusstation.getString(1), rsbusstation.getString(3));
        }
        rsbusstation.close();
        String sql = "select id,buslineid,linkids,s_stationid,e_stationid,s_stationname,e_stationname from busstation_distance_2016_05_04 order by buslineid,index";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        int indexnum = 0;
        String updateSQL = "update bd_section_busline_ref set geo_wkt=? where buslineid=? and startname=? and endname=?";
        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
        while(rs.next()){
            indexnum++;
            String id = rs.getString(1);
            String buslineid = rs.getString(2);
            String linkids = rs.getString(3);
            String s_stationid = rs.getString(4);
            String e_stationid = rs.getString(5);
            String startname = rs.getString(6);
            String endname = rs.getString(7);
            System.out.println(indexnum+"->"+buslineid+"-"+startname+"-"+endname);
            String wktResultSet = GeneratorWKT(s_stationid,e_stationid,linkids,naviMap,mapStation);
            if(null!=wktResultSet){
                psUpdate.setString(1, wktResultSet);
                psUpdate.setString(2, buslineid);
                psUpdate.setString(3, startname);
                psUpdate.setString(4, endname);
                psUpdate.addBatch();
                if(indexnum%5000==0){
                    psUpdate.executeBatch();
                    System.out.println(indexnum+"--->"+buslineid+"---"+startname+"---"+endname);
                }
            }
        }
        psUpdate.executeBatch();
        psUpdate.close();
        connection.close();
    }
    
    
    @Test
    public void testHandleQMNTRUEFALSE() throws Exception {
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String sql = "select lxmc,channelname,sectionname,buslineid,s_stationname,e_stationname,ldmc,id from qhm_excel_busline_channel_ref where isvalid='1'";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        String insertSQL = "insert into bd_dm_busline_station_ref (sbsrid,bcsdrid,id) values (?,?,?)";
        PreparedStatement psInert = connection.prepareStatement(insertSQL);
        while(rs.next()){
            String tdname = rs.getString(1);
            String qjname = rs.getString(2);
            String dmname = rs.getString(3);
            String ldmc = rs.getString(7);
            String idd = rs.getString(8);
            String buslineid = rs.getString(4);
            String s_stationname = rs.getString(5);
            String e_stationname = rs.getString(6);
            
            if(ldmc.indexOf("主路")!=-1){
                qjname = qjname+"(主)";
            }else if(ldmc.indexOf("辅路")!=-1){
                qjname = qjname+"(辅)";
            }
            
            String sbsrisql = "select id from bd_channel_section_dm_ref where tdname='"+tdname+"' and qjname='"+qjname+"' and dmname='"+dmname+"'";
            System.out.println(sbsrisql +"-->"+idd);
            ResultSet rs1 = connection.createStatement().executeQuery(sbsrisql);
            rs1.next();
            String sbsrid =  rs1.getString(1);
            rs1.close();
            String bcsdridSQL =  "select id from bd_section_busline_ref where buslineid='"+buslineid+"' and startname='"+s_stationname+"' and endname='"+e_stationname+"'"; //buslineid startname endname
            System.out.println(bcsdridSQL);
            ResultSet rs2 = connection.createStatement().executeQuery(bcsdridSQL);
            while(rs2.next()){
                String bcsdrid =  rs2.getString(1);
                psInert.setString(2, sbsrid);
                psInert.setString(1, bcsdrid);
                psInert.setString(3, StringUtil.GetUUIDString());
                psInert.addBatch();
            }
        }
        psInert.executeBatch();
    }
    
    
    
    
    
    /**
     * 生成站点区间的空间信息
     * @param s_stationid 开始站点id
     * @param e_stationid 结束占地id
     * @param linkids zhan点区间ids
     * @param naviMap 导航路链数据
     * @param mapStation 站点和wkt对应关系
     * @return
     */
    private String GeneratorWKT(String s_stationid, String e_stationid,String linkids, Map<String, String> naviMap, Map<String, String> mapStation) throws Exception {
        GeoToolsGeometry gtg = new GeoToolsGeometry();
        if(null!=linkids&&!(linkids.trim().equals(""))){
            String[] linkidArr = linkids.split(",");
            if(linkidArr.length>1){//处理开始站和结束站不在同一个路链的
                String s_nameWkt = mapStation.get(s_stationid);
                String e_nameWkt = mapStation.get(e_stationid);
                String s_namelinkWkt = naviMap.get(linkidArr[0]);
                String e_namelinkWkt = naviMap.get(linkidArr[linkidArr.length-1]);
                String multilineWkt = "MULTILINESTRING(";
                List<String> wktList = pointSplitLine(s_nameWkt, s_namelinkWkt);
                MultiLineString subline1 = gtg.createMLineByWKT(wktList.get(0));
                MultiLineString subline2 = gtg.createMLineByWKT(wktList.get(1));
                MultiLineString naviLine = gtg.createMLineByWKT(naviMap.get(linkidArr[1]));
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
                        List<String> middStrList = getXYByWkt(naviMap.get(linkidArr[i]));
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
                MultiLineString naviLine1 = gtg.createMLineByWKT(naviMap.get(linkidArr[linkidArr.length-2]));
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
                return multilineWkt;
            }else if(linkidArr.length==1){//同一个路链上
                
            }
        }
        return null;
    }
    /**
     * 更新BD_Section_Busline_Station_Ref adcdnames
     * 根据开始结束站点的行政区划归属来设计
     * 根据busstation adcdname 来设置
     * 还要计算长度update bd_section_busline_ref set seclen=st_length(ST_Transform(st_geometryfromtext(geo_wkt,4326),900913))
     * st_distance(ST_Transform(st_geometryfromtext(geo_wkt,4326),900913),ST_Transform(st_geometryfromtext(geo_wkt,4326),900913))
     */
    @Test
    public void updateBDSectionBuslineStationRefAdcdnames() throws Exception{
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(urlttyj, userName, passwd);
        String sql2 = "select id,buslineid,startindex,endindex from bd_section_busline_ref where isvalid='1'";
        ResultSet rs = connection.createStatement().executeQuery(sql2);
        PreparedStatement ps = connection.prepareStatement("update bd_section_busline_ref set adcdnames=? where id=?");
        String sql1 = "select buslineid, index,adcdname from busstation where buslineid in (select id from busline where isvalid='1')";
        ResultSet rs1 = connection.createStatement().executeQuery(sql1);
        Map<String,String> map1 = new HashMap<String,String>();
        while(rs1.next()){
            String buslineid = rs1.getString(1);
            String index = rs1.getString(2);
            String adcdname = rs1.getString(3);
            map1.put(buslineid+index, adcdname);
        }
        int indexnum=0;
        while(rs.next()){
            indexnum++;
            String id = rs.getString(1);
            String buslineid = rs.getString(2);
            String index = rs.getString(3);
            String endindex = rs.getString(4);
            String adcdname1 = map1.get(buslineid+index);
            String adcdname2 = map1.get(buslineid+endindex);
            ps.setString(2, id);
            String adcdnamesStr="";
            Map<String,String> maptemp = new HashMap<String,String>();
            if(null!=adcdname1){
                maptemp.put(adcdname1,adcdname1);
            }
            if(null!=adcdname2){
                maptemp.put(adcdname2, adcdname2);
            }
            if(maptemp.size()==2){
                ps.setString(1, adcdname1+","+adcdname2);
            }else if(maptemp.size()==1){
                ps.setString(1, maptemp.get(adcdname1));
            }else{
                ps.setString(1, "");
            }
            ps.addBatch();
            if(indexnum%5000==0){
                ps.executeBatch();
            }
            System.out.println(indexnum+"-> ok!");
        }
        ps.executeBatch();
    }
    
    /**
     * 通道，通道区间，通道断面关系表 第一次根据busline_channel busline_channel_section生成
     * 最大有效时间设定为2050-01-01,生成之前将上一次的结束时间进行修改
     * 注意每次执行前请修改表中endtime和valid
     */
    @Test
    public void testBD_Channel_Section_DM_Ref() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        
        String urlttyj = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userNamettyj = "basedata";
        String passwdttyj = "basedata!@#&*(";
        Connection connectionttyj = DBConnection.GetPostGresConnection(urlttyj, userNamettyj, passwdttyj);
        
        
        String sqlTD = "select id, tdlb,lxmc,ldmc,name,dldj,arrowstr from busline_channel order by lxmc";
        String insert = "INSERT INTO bd_channel_section_dm_ref(id, tdname, qjname, dmname, zhufu, fx, starttime, "
            +"endtime, isvalid, tdlb,the_geom) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromText(?,4326))";
        ResultSet rsTD = connectionttyj.createStatement().executeQuery(sqlTD);
        PreparedStatement ps = connection.prepareStatement(insert);
        while(rsTD.next()){
            String channelid = rsTD.getString(1);
            String tdlb = rsTD.getString(2);
            String lxmc = rsTD.getString(3);
            String ldmc = rsTD.getString(4);
            String name = rsTD.getString(5);
            String dldj = rsTD.getString(6);
            String arrowstr = rsTD.getString(7);
            String sqlTDDM = "select id,name,st_astext(the_geom) from busline_channel_section where channelid='"+channelid+"'";
            ResultSet rsTDDM = connectionttyj.createStatement().executeQuery(sqlTDDM);
            while(rsTDDM.next()){
                String id = rsTDDM.getString(1);
                String dmname = rsTDDM.getString(2);
                String dmwkt = rsTDDM.getString(3);
                ps.setString(1, id);
                ps.setString(2, lxmc);
                ps.setString(3, name);
                ps.setString(4, dmname);
                ps.setString(5, dldj);
                ps.setString(6, arrowstr);
                ps.setString(7, "2014-12-01");
                ps.setString(8, "2050-01-01");
                ps.setString(9, "1");
                ps.setString(10, tdlb);
                ps.setString(11, dmwkt);
                ps.addBatch();
            }
        }
        ps.executeBatch();
    }
    
    /**
     * 修改bd_channel_section_dm_ref adcdname
     */
    @Test
    public void testUpdateBDChannelSectionDMRef_ADCDNAME() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String sqlQuery = "select id,st_astext(the_geom) from bd_channel_section_dm_ref where isvalid='1'";
        String sqlQueryBeijingqx = "select name,st_astext(the_geom) from beijingqx";
        String updateSQL = "update bd_channel_section_dm_ref set adcdname=? where id=?";
        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
        ResultSet rsBeijing = connection.createStatement().executeQuery(sqlQueryBeijingqx);
        Map<String,Geometry> map = new HashMap<String,Geometry>();
        while(rsBeijing.next()){
            String name = rsBeijing.getString(1);
            String wkt = rsBeijing.getString(2);
            map.put(name, GeoToolsGeometry.createGeometrtyByWKT(wkt));
        }
        ResultSet rs = connection.createStatement().executeQuery(sqlQuery);
        while(rs.next()){
            String id = rs.getString(1);
            Geometry geom = GeoToolsGeometry.createGeometrtyByWKT(rs.getString(2));
            for (Map.Entry<String, Geometry> entry : map.entrySet()) {
                String adcdname = entry.getKey();
                Geometry geomsub = entry.getValue();
                if(geomsub.intersects(geom)){
                    psUpdate.setString(1, adcdname);
                    psUpdate.setString(2, id);
                    psUpdate.addBatch();
                    break;
                }
            }
        }
        psUpdate.executeBatch();
    }
    
    
    /**
     * 修改busline adcdnames
     */
    @Test
    public void testUpdateBuslineADCDNAME() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String sqlQuery = "select id,adcdname from navigationline where adcdcode is not null";
        ResultSet rsNavigation = connection.createStatement().executeQuery(sqlQuery);
        Map<String,String> navMap = new HashMap<String,String>();
        while(rsNavigation.next()){
            if(null!=rsNavigation.getString(2)){
                navMap.put(rsNavigation.getString(1), rsNavigation.getString(2));
            }
        }
        
        String sqlQueryBusLine = "select id,label from busline";
        String updateSQL = "update busline set adcdnames=? where id=?";
        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
        ResultSet rsBusLine = connection.createStatement().executeQuery(sqlQueryBusLine);
        Map<String,Geometry> map = new HashMap<String,Geometry>();
        int index = 0;
        while(rsBusLine.next()){
            index++;
            String id = rsBusLine.getString(1);
            String label = rsBusLine.getString(2);
            String queryBusline = "select navigationid from buslinelink where buslineid='"+id+"'";
            ResultSet rsSub = connection.createStatement().executeQuery(queryBusline);
            Map<String,String> mapNames = new HashMap<String,String>();
            while(rsSub.next()){
                String navigationid = rsSub.getString(1);
                String name = navMap.get(navigationid);
                if(null!=name&&!name.equals("")&&!name.equals("null")){
                    mapNames.put(name, "1");
                }
                
            }
            String namesstr = "";
            for (Map.Entry<String, String> entry : mapNames.entrySet()) {
                namesstr = namesstr+","+entry.getKey();
            }
            if(namesstr.length()>1){
                namesstr = namesstr.substring(1, namesstr.length());
            }
            psUpdate.setString(1, namesstr);
            psUpdate.setString(2, id);
            psUpdate.addBatch();
            if(index%5000==0){
                psUpdate.executeBatch();
            }
            System.out.println(index+"->"+label+"("+namesstr+")");
        }
        psUpdate.executeBatch();
    }
    
    /**
     * 更新busstation adcdname
     */
    @Test
    public void testUpdateBusstationAdcdName() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String query = "select t1.id,t2.busstationid,st_astext(t2.the_geom) from busstation t1 left join busstationlink t2 on t1.id=t2.busstationid where busstationid is not null";
        String sqlQueryBeijingqx = "select name,st_astext(the_geom) from beijingqx";
        String updateSQL = "update busstation set adcdname=? where id=?";
        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
        ResultSet rsBeijing = connection.createStatement().executeQuery(sqlQueryBeijingqx);
        Map<String,Geometry> map = new HashMap<String,Geometry>();
        while(rsBeijing.next()){
            String name = rsBeijing.getString(1);
            String wkt = rsBeijing.getString(2);
            map.put(name, GeoToolsGeometry.createGeometrtyByWKT(wkt));
        }
        ResultSet rsBusStation = connection.createStatement().executeQuery(query);
        int index = 0;
        while(rsBusStation.next()){
            String id = rsBusStation.getString(1);
            String wkt = rsBusStation.getString(3);
            Geometry geom = GeoToolsGeometry.createGeometrtyByWKT(wkt);
            for (Map.Entry<String, Geometry> entry : map.entrySet()) {
                String adcdname = entry.getKey();
                Geometry geomsub = entry.getValue();
                if(geomsub.contains(geom)||geomsub.intersects(geom)){
                    psUpdate.setString(1, adcdname);
                    psUpdate.setString(2, id);
                    psUpdate.addBatch();
                    index++;
                    System.out.println(index);
                    if(index%5000==0){
                        psUpdate.executeBatch();
                    }
                    break;
                }
            }
        }
        psUpdate.executeBatch();
    }
    
    
    /**
     * 生成通道和公交线路关系表
     */
    @Test
    public void testBD_Line_Station_Ref() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String blcsidSQL = "select id,tdname,qjname,dmname from bd_channel_section_dm_ref where isvalid='1'";//这个表的id和busline_channel_section id一样
        ResultSet sectionRS = connection.createStatement().executeQuery(blcsidSQL);
        String insert1 = "INSERT INTO bd_tddm_busline_station_ref( id, tdname, qjname, dmname, refid, refname, type, starttime,"+ 
                        "endtime, isvalid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(insert1);
        int index = 0;
        while(sectionRS.next()){
            index++;
            String sectionid = sectionRS.getString(1);//
            String tdname = sectionRS.getString(2);
            String qjname = sectionRS.getString(3);
            String dmname = sectionRS.getString(4);
            String sql = "select t3.id,t3.label from ( select buslineid from ("+
                                " select id,buslineid from buslinelink where navigationid in (select navigationid from busline_channel_section_link where sectionid='"+sectionid+"' ) and buslineid in (select id from busline where isvalid='1')"+
                                " ) t1 group by buslineid ) t2 left join busline t3 on t2.buslineid = t3.id order by label";
            ResultSet buslinesRS = connection.createStatement().executeQuery(sql);
            while(buslinesRS.next()){
                String buslineid = buslinesRS.getString(1);
                String buslinelabel = buslinesRS.getString(2);
                ps.setString(1, StringUtil.GetUUIDString());
                ps.setString(2, tdname);
                ps.setString(3, qjname);
                ps.setString(4, dmname);
                ps.setString(5, buslineid);
                ps.setString(6, buslinelabel);
                ps.setString(7, "1");
                ps.setString(8, "2014-12-01");
                ps.setString(9, "2050-01-01");
                ps.setString(10, "1");
                ps.addBatch();
            }
            System.out.println(index+"->"+dmname);
        }
        ps.executeBatch();
        
    }
    
    /**
     * bd_busstation_merge 根据linkid和name 唯一合并
     */
    @Test
    public void testDBusstationmerge() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String sql = "INSERT INTO bd_busstation_merge("+
            " id, name, adcdname, navigationid, starttime, endtime, "+
            " isvalid,the_geom) VALUES (?, ?, ?, ?, ?, ?, ?, ST_GeomFromText(?,4326))";
        String sql2 = "select t1.id,t1.name,st_astext(t2.the_geom),t2.linkid from (select id,name from busstation where buslineid in (select id from busline where isvalid='1'))"+
                "t1 left join busstationlink t2 on t1.id=t2.busstationid order by name";
        String sql3 = "select id,adcdname from navigationline where id in (select linkid from busstationlink group by linkid)";
        Map<String,String> mapNavigation = new HashMap<String, String>();
        ResultSet rs3 = connection.createStatement().executeQuery(sql3);
        PreparedStatement ps = connection.prepareStatement(sql);
        while(rs3.next()){
            mapNavigation.put(rs3.getString(1), rs3.getString(2));
        }
        ResultSet rs2 = connection.createStatement().executeQuery(sql2);
        Map<String,String> mapRS2 = new HashMap<String,String>();
        int index=0;
        while(rs2.next()){
            String id = rs2.getString(1);
            String name = rs2.getString(2);
            String wkt = rs2.getString(3);
            String linkid = rs2.getString(4);
            if(null==mapRS2.get(name+linkid)){
                ps.setString(1, StringUtil.GetUUIDString());
                ps.setString(2, name);
                ps.setString(3, mapNavigation.get(linkid));
                ps.setString(4, linkid);
                ps.setString(5, "2014-12-01");
                ps.setString(6, "2050-01-01");
                ps.setString(7, "1");
                ps.setString(8, wkt);
                ps.addBatch();
                index++;
                if(index%5000==0){
                    ps.executeBatch();
                }
                mapRS2.put(name+linkid,"1");
                System.out.println(index+"->"+name+linkid);
            }
        }
        ps.executeBatch();
    }
    
    /**
     * 更新busstation the_geom
     * @throws Exception
     */
    @Test
    public void testMergeBusstatinGeom() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String sql = "update busstation t1 set the_geom=t2.the_geom from busstationlink t2 where t1.id=t2.busstationid";
        connection.createStatement().execute(sql);
    }
    
    
    /**
     * 合并公交线路几何字段到busline geom
     */
    @Test
    public void testMergeBuslineGeom() throws Exception{
        //String buslineid="9ae8ceb66fa246d891640c0418d67988";
        String buslineid="";
        String sql = "";
        if(buslineid.trim().equals("")){
            sql = "select id from busline";
        }else{
            sql = "select id from busline where id='"+buslineid+"'";
        }
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String userName = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        ResultSet rs = connection.createStatement().executeQuery(sql);
        String updateSQL = "update busline set the_geom=ST_GeomFromText(?,4326) where id=?";
        PreparedStatement psInsert = connection.prepareStatement(updateSQL);
        int indexCount=0;
        while(rs.next()){
            indexCount++;
            String buslineidTemp = rs.getString(1);
            String queryTD = "select buslineid,string_agg(st_astext(the_geom),'%') wkts from buslinelink  where buslineid='"+buslineidTemp+"' group by buslineid";
            ResultSet rssub = connection.createStatement().executeQuery(queryTD);
            while(rssub.next()){
                String wkts = rssub.getString(2);
                String[] wktArr = wkts.split("%");
                List<String> wktList = Arrays.asList(wktArr);
                Geometry result =  GeoToolsGeometry.UnionManyGeo(wktList);
                String wktnew = result.toText();
                psInsert.setString(1, wktnew);
                psInsert.setString(2, buslineidTemp);
                psInsert.addBatch();
                if(indexCount%100==0){
                    psInsert.executeBatch(); 
                }
            }
            psInsert.executeBatch();
            System.out.println(indexCount+"->"+buslineid);
        }
    }
    
    /**
     * 生成通道和公交站点
     */
    @Test
    public void testBD_Line_Station_Ref1() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String blcsidSQL = "select id,tdname,qjname,dmname from bd_channel_section_dm_ref where isvalid='1'";//这个表的id和busline_channel_section id一样
        ResultSet sectionRS = connection.createStatement().executeQuery(blcsidSQL);
        String insert1 = "INSERT INTO bd_tddm_busline_station_ref( id, tdname, qjname, dmname, refid, refname, type, starttime,"+ 
                        "endtime, isvalid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(insert1);
        while(sectionRS.next()){
            String sectionid = sectionRS.getString(1);//
            String tdname = sectionRS.getString(2);
            String qjname = sectionRS.getString(3);
            String dmname = sectionRS.getString(4);
            String sql = "select id,name from bd_busstation_merge where navigationid in (select navigationid from busline_channel_section_link where sectionid='"+sectionid+"' ) and isvalid='1'";
            ResultSet busStationRS = connection.createStatement().executeQuery(sql);
            while(busStationRS.next()){
                String idd = busStationRS.getString(1);
                String stationlabel = busStationRS.getString(2);
                ps.setString(1, StringUtil.GetUUIDString());
                ps.setString(2, tdname);
                ps.setString(3, qjname);
                ps.setString(4, dmname);
                ps.setString(5, idd);
                ps.setString(6, stationlabel);
                ps.setString(7, "0");
                ps.setString(8, "2014-12-01");
                ps.setString(9, "2050-01-01");
                ps.setString(10, "1");
                ps.addBatch();
            }
        }
        ps.executeBatch();
    }
    
    @Test
    public void testUpdateTureQHMEXCEL() throws Exception{
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String userName = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String filePath = "G:\\项目文档\\公交都市\\通道改名\\qhm_excel_busline_channel_ref.xls";
        List<String> list = POIExcelUtil.ReadXLS(filePath, "#", 2, 4771, 1, 4);
        String updatesql = "update qhm_excel_busline_channel_ref set channelname=?,sectionname=?  where id=?";
        PreparedStatement psupdate = connection.prepareStatement(updatesql);
        int index=0;
        for(String s:list){
            String[] arr = s.split("#");
            String id = arr[0];
            String channelname = arr[2];
            String sectionname = arr[3];
           
            psupdate.setString(1, channelname);
            psupdate.setString(2, sectionname);
            psupdate.setString(3, id);
            psupdate.addBatch();
            System.out.println(s);
        }
        psupdate.executeBatch();
    }
    
    @Test
    public void test1111() throws Exception{
        String sql = "select st_astext(the_geom) from busline_channel_section where id in ('f5a50c9fbcd943cd9230c21fccdb40c5','46f0ddfda9554e1688e312c32fdb09fb')";
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String userName = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        ResultSet rs1 = connection.createStatement().executeQuery(sql);
        rs1.next();
        String wkt1 = rs1.getString(1);
        rs1.next();
        String wkt2 = rs1.getString(1);
        System.out.println(wkt1);
        System.out.println(wkt2);
        Geometry g1 = GeoToolsGeometry.createGeometrtyByWKT(wkt1);
        Geometry g2 = GeoToolsGeometry.createGeometrtyByWKT(wkt2);
       
        String update = "update busline_channel_section set the_geom =ST_GeomFromText(?,4326) where id='46f0ddfda9554e1688e312c32fdb09fb'";
        PreparedStatement psupdate = connection.prepareStatement(update);
        psupdate.setString(1,  g1.union(g2).toText());
        psupdate.addBatch();
        psupdate.executeBatch();
    }
    
    
    @Test
    public void testUPdatechannelgeometry() throws Exception{
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        
        String urlttyj = "jdbc:postgresql://localhost:5432/basedata";
        String userNamettyj = "basedata";
        String passwdttyj = "basedata";
        Connection connectionttyj = DBConnection.GetPostGresConnection(urlttyj, userNamettyj, passwdttyj);
        
        String update = "update busline_channel_geometry set kll=? where id=?";
        String sqlTD = "select channelid,data from busline_channel_geometry_data where type='kll' and timestr='201603210915'";
        ResultSet rsTD = connection.createStatement().executeQuery(sqlTD);
        PreparedStatement ps = connectionttyj.prepareStatement(update);
        while(rsTD.next()){
            String channelid = rsTD.getString(1);
            double kll = rsTD.getDouble(2);
            ps.setDouble(1, kll);
            ps.setString(2, channelid);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
    @Test
    public void testHandleQMNTRUEFALSE111() throws Exception {
        String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String userName = "postgres";
        String passwd = "postgres";
        Connection connection = DBConnection.GetPostGresConnection(url, userName, passwd);
        String sql = "select id, lxmc,name,ldmc from busline_channel";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        String insertSQL = "update busline_channel set name=? where id=?";
        PreparedStatement psInert = connection.prepareStatement(insertSQL);
        while(rs.next()){
            String idtemp = rs.getString(1);
            String lxmc = rs.getString(2);
            String name = rs.getString(3);
            String dmname = rs.getString(3);
            String ldmc = rs.getString(4);
            if(ldmc.indexOf("主路")!=-1){
                name = name+"(主)";
            }else if(ldmc.indexOf("辅路")!=-1){
                name = name+"(辅)";
            }
            psInert.setString(1, name);
            psInert.setString(2, idtemp);
            psInert.addBatch();
            
        }
        psInert.executeBatch();
    }
    
    /**
     * navigationlink_busline
     */
    @Test
    public void insertnavigationlink_busline() throws Exception{
        String urlPG = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String sql2 = "delete from navigationlink_busline";
        Statement statement2 = connectionPG.createStatement();
        statement2.addBatch(sql2);
        statement2.executeBatch();
        connectionPG.createStatement().execute("insert into navigationlink_busline select * from navigationline where id in (select navigationid from buslinelink where buslineid in (select id from busline where isvalid='1'))");
    }
    
    
    @Test
    public void testIDTest() throws Exception{
        List<String> list= PBFileUtil.ReadFileByLine("d:\\catalina.out");
        Pattern pattern = Pattern.compile("id[0-9]{1,}");
       // List<IDString> result = new ArrayList<IDString>();
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@10.212.140.211:1521:buscity", "buscitynew", "admin123ttyj7890uiop");
       int index1 = 0;
       int index2 = 0;
        for(String s:list){
            Matcher matcher = pattern.matcher(s);
            while(matcher.find()){
                String sgroup = matcher.group();
                sgroup = sgroup.substring(2, sgroup.length());
                String querySQL = "select * from gj_car_bh where zbh='"+sgroup+"'";
                ResultSet rs = connectOracle.createStatement().executeQuery(querySQL);
                index1++;
               while(rs.next()){
                   index2++;
                   System.out.println(rs.getString(1));
               }
            }
        }
        System.out.println(index1 +"--->"+index2);
        //POIExcelUtil.ExportDataByList(result, "D:\\idexcel.xls");
    }
    
    /**
     * 删除通道区间断面1924
     */
    @Test
    public void deleteChannelsectiondm() throws Exception{
    //京广桥-大黄庄桥    大黄庄桥东-大黄庄桥（东五环）
    //大黄庄桥-双桥东路环岛,大黄庄桥（东五环）-大黄庄桥东
    //北四环内环（四海桥-四元桥）,火器营桥西-六郎庄
    //北四环内环（四海桥-四元桥）,六郎庄-海淀桥西
    //北四环内环（四海桥-四元桥）  四海桥-火器营桥西
    //马甸桥-德胜门桥,德胜门西-德胜门
        String urlPG = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String names = "京广桥-大黄庄桥,大黄庄桥东-大黄庄桥（东五环）;大黄庄桥-双桥东路环岛,大黄庄桥（东五环）-大黄庄桥东;北四环内环（四海桥-四元桥）,火器营桥西-六郎庄;北四环内环（四海桥-四元桥）,六郎庄-海淀桥西;北四环内环（四海桥-四元桥）,四海桥-火器营桥西;马甸桥-德胜门桥,德胜门西-德胜门";
        String[] arr1 = names.split(";");
        Statement statement2 = connectionPG.createStatement();
        for(String str1 : arr1){
            String[] arr2 = str1.split(",");
            String qjname = arr2[0];
            String dmname = arr2[1];
            String sql1 = "select id from busline_channel_section where name in ('"+dmname+"') and channelid in (select id from busline_channel  where name='"+qjname+"')";
           // String del1 = "delete from busline_channel_section_link where sectionid in ("+sql1+")";
            String del2 = "delete from busline_channel_section where name in ('"+dmname+"')";
           // System.out.println(del1); 
            System.out.println(del2);
            
           // statement2.addBatch(del1);
            statement2.addBatch(del2);
        }
        statement2.executeBatch();
    }
    
    /**
     * 根据新增线路触发一系列动作
     */
    @Test
    public void testAddBuslineAction(){
    //bd_busstation_merge 公交站点合并表，合并规则（根据linkid和name 唯一合并）。
        String urlPG = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String buslineid="";
        String sql1 = "select * from bus";
    
    }
    
    
    /**
     * 根据区间名字，修改dm名字
     */
    @Test
    public void testUpdateChannelSectionName(){
        //晋元桥-金安桥(主)  杨庄东-杨庄
        //金安桥-晋元桥(主)  杨庄-杨庄东

        String urlPG = "jdbc:postgresql://10.212.140.212:5432/basedata";
        String usernamePG = "basedata";
        String passwdPG = "basedata";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
    }
    
}
