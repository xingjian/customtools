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
import com.promise.cn.util.PBMathUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年1月6日 上午10:24:24  
 */
public class BusCityDataTest2 {

    /**
     * 按照公交线路打断100
     */
    @Test
    public void testHundredBusLine() throws Exception{
        GeoToolsGeometry gtg = new GeoToolsGeometry();
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String querySQL = "select t1.navigationid,ST_AsText(t2.the_geom) wkt,t2.direction,t2.snodeid,t2.enodeid from (select navigationid from buslinelink group by navigationid"
                    +") t1 left join navigationline t2 on t1.navigationid = t2.id";
        Statement statement = connection.createStatement();
        ResultSet rs1 = statement.executeQuery(querySQL);
        Map<String,NavigationObject> map1 = new HashMap<String,NavigationObject>();
        while(rs1.next()){
            String navigationid = rs1.getString(1);
            String wktStr = rs1.getString(2);
            String direction = rs1.getString(3);
            String snode = rs1.getString(4);
            String enode = rs1.getString(5);
            NavigationObject nObject = new NavigationObject();
            nObject.setId(navigationid);
            nObject.setEnode(enode);
            nObject.setSnode(snode);
            nObject.setWkt(wktStr);
            nObject.setGeometry(GeoToolsGeometry.createGeometrtyByWKT(wktStr));
            nObject.setDirect(direction);
            map1.put(navigationid, nObject);
        }
        //5534a40c66a44f8fae27348c2878cf39 653路 五路居-北苑家园
        //String okbuslinesql = "select buslineid,buslinename from busstation_distance_2015_12_21  where buslineid not in(select buslineid from busstation_distance_2015_12_21 where distance = 0 group by buslineid ) group by buslineid,buslinename";
        String okbuslinesql = "select buslineid,buslinename from busstation_distance_2015_12_21  where buslineid not in(select buslineid from busstation_distance_2015_12_21 where distance = 0  group by buslineid ) and buslineid='eed2c6102f96467b839de5d7a17b13f7' group by buslineid,buslinename ";
        Statement statementall = connection.createStatement();
        ResultSet rsall = statementall.executeQuery(okbuslinesql);
        int uuuu = 1;
        while(rsall.next()){
            //String buslineid = "5534a40c66a44f8fae27348c2878cf39";
            String buslineName = rsall.getString("buslinename");
            String buslineid = rsall.getString("buslineid");
            System.out.println("start----"+uuuu+"----"+buslineName+"----"+buslineid);
            String querySQL1 = "select * from busstation_distance_2015_12_21 where buslineid='"+buslineid+"' order by buslineid,index";
            String querySQL2 = "select t1.id,t1.name,t1.index,t2.linkid,ST_AsText(t2.the_geom) wkt from busstation t1 left join busstationlink t2 on t1.id = t2.busstationid where t1.buslineid='"+buslineid+"' order by t1.index";
            Statement statement1 = connection.createStatement();
            ResultSet rs2 = statement1.executeQuery(querySQL1);
            Statement statement2 = connection.createStatement();
            ResultSet rs3 = statement2.executeQuery(querySQL2);
            StringBuffer sbf = new StringBuffer();
            while(rs2.next()){
                String linkids = rs2.getString("linkids")+",";
                sbf.append(linkids);
            }
            String firstLink = null;
            String firstWKT = null;
            String endLink = null;
            String endWKT = null;
            while(rs3.next()){
                if(rs3.isFirst()){
                    firstLink = rs3.getString("linkid");
                    firstWKT = rs3.getString("wkt");
                }
                if(rs3.isLast()){
                    endLink = rs3.getString("linkid");
                    endWKT = rs3.getString("wkt");
                }
            }
            String[] linkArr = sbf.toString().split(",");
            List<String> points = new ArrayList<String>();
            int pointsIndex = 0;
            Map<String,String> mapPoint= new HashMap<String, String>();
            points.add(pointsIndex,GeoToolsGeometry.getXYByWkt(firstWKT).get(0));
            pointsIndex++;
            mapPoint.put(GeoToolsGeometry.getXYByWkt(firstWKT).get(0), GeoToolsGeometry.getXYByWkt(firstWKT).get(0));
            String forward = "";
            for(int i=0;i<linkArr.length;i++){
                NavigationObject naviTemp = map1.get(linkArr[i]);
                if(i==0&&firstLink.equals(naviTemp.getId())){
                    List<String> wktList = pointSplitLine(firstWKT,naviTemp);
                    MultiLineString subline1 = gtg.createMLineByWKT(wktList.get(0));
                    MultiLineString subline2 = gtg.createMLineByWKT(wktList.get(1));
                    MultiLineString naviLine = gtg.createMLineByWKT(map1.get(linkArr[1]).wkt);
                    double dis1 = gtg.distanceGeo(subline1, naviLine);
                    double dis2 = gtg.distanceGeo(subline2, naviLine);
                    if(dis1<dis2){
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(subline1.toText());
                        for(String s1:lTemp1){
                            if(!mapPoint.containsKey(s1)){
                                mapPoint.put(s1, s1);
                                points.add(pointsIndex, s1);
                                pointsIndex++;
                            }
                        }
                    }else{
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(subline2.toText());
                        for(String s1:lTemp1){
                            if(!mapPoint.containsKey(s1)){
                                mapPoint.put(s1, s1);
                                points.add(pointsIndex, s1);
                                pointsIndex++;
                            }
                            
                        }
                    }
                    forward = points.get(pointsIndex);
                }else if(i==linkArr.length-1&&endLink.equals(linkArr[linkArr.length-1])){
                    List<String> wktList = pointSplitLine(endWKT,naviTemp);
                    MultiLineString subline1 = gtg.createMLineByWKT(wktList.get(0));
                    MultiLineString subline2 = gtg.createMLineByWKT(wktList.get(1));
                    MultiLineString naviLine = gtg.createMLineByWKT(map1.get(linkArr[linkArr.length-2]).wkt);
                    double dis1 = gtg.distanceGeo(subline1, naviLine);
                    double dis2 = gtg.distanceGeo(subline2, naviLine);
                    if(dis1<dis2){
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(subline1.toText());
                        for(String s1:lTemp1){
                            if(!mapPoint.containsKey(s1)){
                                mapPoint.put(s1, s1);
                                mapPoint.put(s1, s1);
                                points.add(pointsIndex, s1);
                                pointsIndex++;
                            }
                            
                        }
                    }else{
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(subline2.toText());
                        for(String s1:lTemp1){
                            if(!mapPoint.containsKey(s1)){
                                mapPoint.put(s1, s1);
                                points.add(pointsIndex, s1);
                                pointsIndex++;
                            }
                        }
                    }
                }else{
                    if(null!=forward){
                        String[] arrFor = forward.split(" ");
                        List<String> lTemp1 = GeoToolsGeometry.getXYByWkt(naviTemp.wkt);
                        String[] arrFor1 = lTemp1.get(0).split(" ");
                        String[] arrFor2 = lTemp1.get(lTemp1.size()-1).split(" ");
                        Point pointForward = GeoToolsGeometry.createPoint(Double.parseDouble(arrFor[0]), Double.parseDouble(arrFor[1]));
                        Point pointForward1 = GeoToolsGeometry.createPoint(Double.parseDouble(arrFor1[0]), Double.parseDouble(arrFor1[1]));
                        Point pointForward2 = GeoToolsGeometry.createPoint(Double.parseDouble(arrFor2[0]), Double.parseDouble(arrFor2[1]));
                        
                        double d1 = GeoToolsGeometry.distanceGeo(pointForward, pointForward1);
                        double d2 = GeoToolsGeometry.distanceGeo(pointForward, pointForward2);
                        if(d1<d2){
                            for(String s1:lTemp1){
                                if(!mapPoint.containsKey(s1)){
                                    mapPoint.put(s1, s1);
                                    points.add(pointsIndex, s1);
                                    pointsIndex++;
                                }
                            } 
                        }else{
                            for(int j=lTemp1.size()-1;j>=0;j--){
                                String s1 = lTemp1.get(j);
                                if(!mapPoint.containsKey(s1)){
                                    mapPoint.put(s1, s1);
                                    points.add(pointsIndex, s1);
                                    pointsIndex++;
                                }
                            }
                        }
                        forward = points.get(pointsIndex);
                    }
                }
                
            }
            System.out.println(points.size());
            //开始计算个点之间的位置关系
            String[] resultArr = new String[points.size()];
            resultArr[0] = mapPoint.get(GeoToolsGeometry.getXYByWkt(firstWKT).get(0));
            points.remove(0);
            int m = 1;
            int count = points.size();
            for(int a=0;a<=count-1;a++){
//                int iii = calcDisMin(resultArr[a],points);
//                resultArr[m] = points.get(iii);
//                m++;
//                points.remove(iii);
            }
            //System.out.println(resultArr.length);
            
            String insertSQL = "insert into busline_point (buslineid,buslinename,index,the_geom) values (?,?,?,ST_GeomFromText(?,4326))";
            PreparedStatement ps = connection.prepareStatement(insertSQL);
            for(int p=0;p<resultArr.length;p++){
                ps.setString(1, buslineid);
                ps.setString(2,buslineName);
                ps.setInt(3, p);
                String[] arrtt = resultArr[p].split(" ");
                String wkt = GeoToolsGeometry.createPoint(Double.parseDouble(arrtt[0]), Double.parseDouble(arrtt[1])).toText();
                ps.setString(4, wkt);
                ps.addBatch();
            }
            ps.executeBatch();
            int sum = 100;
            double target = 100;
            String resultStr=resultArr[0];
            for(int w=0;w<resultArr.length-1;w++){
                String wkt1 = resultArr[w];
                String wkt2 = resultArr[w+1];
                String[] arrtt = wkt1.split(" ");
                String[] arrtt2 = wkt2.split(" ");
                double[] d1 = GISCoordinateTransform.From84To900913(Double.parseDouble(arrtt[0]), Double.parseDouble(arrtt[1]));
                double[] d2 = GISCoordinateTransform.From84To900913(Double.parseDouble(arrtt2[0]), Double.parseDouble(arrtt2[1]));
                Point p1 = GeoToolsGeometry.createPoint(d1[0],d1[1]);
                Point p2 = GeoToolsGeometry.createPoint(d2[0],d2[1]);
                double dis = GeoToolsGeometry.distanceGeo(p1, p2);
//                double dis = PBMathUtil.Distance(d1[0],d1[1],d2[0], d2[1]);
                if(dis>target){
                    double[] d3 = PBMathUtil.SplitSegmentByLength(d1[0],d2[0],d1[1], d2[1], target);
                    double[] d33 = GISCoordinateTransform.From900913To84(d3[0],d3[1]);
                    resultStr = resultStr+","+d33[0]+" "+d33[1];
                    dis = dis-target;
                    while(dis>sum){
                        double[] dt = PBMathUtil.SplitSegmentByLength(d3[0],d2[0],d3[1], d2[1], sum);
                        double[] dtt = GISCoordinateTransform.From900913To84(dt[0],dt[1]);
                        resultStr = resultStr+","+dtt[0]+" "+dtt[1];
                        d3 = dt;
                        dis = dis-sum;
                    }
                    target = sum - dis;
                }else if(dis==target){
                    resultStr = resultStr+","+wkt2;
                    target = sum;
                    if(w+1==resultArr.length-1){
                        break;
                    }
                }else{
                    target = target - dis;
                    if(w+1==resultArr.length-1){
                        resultStr = resultStr+","+wkt2;
                        break;
                    }
                }
            }
            
            String[] resultArr11 = resultStr.split(",");
            
            String insertSQL11 = "insert into buslinesplit100point (id,buslineid,buslinename,stationflag,direct,index,the_geom) values (?,?,?,?,?,?,ST_GeomFromText(?,4326))";
            PreparedStatement ps11 = connection.prepareStatement(insertSQL11);
            for(int p=0;p<resultArr11.length;p++){
              ps11.setString(1, StringUtil.GetUUIDString());
              ps11.setString(2, buslineid);
              ps11.setString(3, buslineName);
              if(p==0){
                  ps11.setString(4, "1");
              }else if(p==resultArr11.length-1){
                  ps11.setString(4, "2");
              }else{
                  ps11.setString(4, "0");
              }
              ps11.setString(5, "");
              ps11.setInt(6, p+1);
              String[] arrtt = resultArr11[p].split(" ");
              String wkt = GeoToolsGeometry.createPoint(Double.parseDouble(arrtt[0]), Double.parseDouble(arrtt[1])).toText();
              ps11.setString(7, wkt);
              ps11.addBatch();
            }
            ps11.executeBatch();
            System.out.println("end----"+uuuu+"----"+buslineName+"----"+buslineid);
            uuuu++;
        }
        
        
    }
    
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
        Matcher matcher = pattern.matcher(wkt);
        int i=0;
        while(matcher.find()){
            result.add(i, matcher.group());
            i++;
        }
        return result;
    }
}
