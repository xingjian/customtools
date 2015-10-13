package com.promise.gistool.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

/**  
 * 功能描述:GeoTools几何对象
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年8月26日 上午10:15:02  
 */
public class GeoToolsGeometry {

    public static GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
    
    /**
     * create Coordinate
     * @param x
     * @param y
     * @return
     */
    public Coordinate coordinate(double x,double y){  
        return new Coordinate(x,y);  
    }
    
    /**
     * 构造点对象 109.013388, 32.715519
     * @param x
     * @param y
     * @return
     */
    public static Point createPoint(double x,double y){  
            Coordinate coord = new Coordinate(x, y);  
            Point point = gf.createPoint( coord );  
            return point;  
    }
    
    /**
     * POINT (109.013388 32.715519)
     * @param wktString
     * @return
     * @throws ParseException
     */
    public static Point createPointByWKT(String wktString) throws ParseException{  
            WKTReader reader = new WKTReader( gf );  
            Point point = (Point) reader.read(wktString);  
            return point;  
    }
    
    /**
     * MULTIPOINT(109.013388 32.715519,119.32488 31.435678)
     * @param wktString
     * @return
     * @throws ParseException
     */
    public static MultiPoint createMulPointByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        MultiPoint mpoint = (MultiPoint) reader.read(wktString);  
        return mpoint;  
    }
    
    /**
     * createLine
     * @param coords
     * @return
     */
    public static LineString createLine(Coordinate[] coords){  
        LineString line = gf.createLineString(coords);  
        return line;  
    }
    
    /**
     * LINESTRING(0 0, 2 0)
     * @param wktString
     * @return
     * @throws ParseException
     */
    public static LineString createLineByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        LineString line = (LineString) reader.read(wktString);  
        return line;  
    }  
    
    /**
     *  mline
     * @param lineStrings
     * @return
     */
    public MultiLineString createMLine(LineString[] lineStrings){  
        MultiLineString ms = gf.createMultiLineString(lineStrings);  
        return ms;  
    }
    
    /**
     * MULTILINESTRING((0 0, 2 0),(1 1,2 2))
     * @return
     * @throws ParseException
     */
    public MultiLineString createMLineByWKT(String wktString)throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        MultiLineString line = (MultiLineString) reader.read(wktString);
        return line;  
    }
    
    /**
     * POLYGON((20 10, 30 0, 40 10, 30 20, 20 10))
     * @param wktString
     * @return
     * @throws ParseException
     */
    public Polygon createPolygonByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        Polygon polygon = (Polygon) reader.read(wktString);  
        return polygon;  
    }
    
    /**
     * 创建一个园
     * @param x
     * @param y
     * @param RADIUS
     * @return
     */
    public Polygon createCircle(double x, double y, final double RADIUS){  
        final int SIDES = 32;//圆上面的点个数  
        Coordinate coords[] = new Coordinate[SIDES+1];  
        for( int i = 0; i < SIDES; i++){  
            double angle = ((double) i / (double) SIDES) * Math.PI * 2.0;  
            double dx = Math.cos( angle ) * RADIUS;  
            double dy = Math.sin( angle ) * RADIUS;  
            coords[i] = new Coordinate( (double) x + dx, (double) y + dy );  
        }  
        coords[SIDES] = coords[0];  
        LinearRing ring = gf.createLinearRing( coords );  
        Polygon polygon = gf.createPolygon( ring, null );  
        return polygon;  
    }
    
    /**
     * MULTIPOLYGON(((40 10, 30 0, 40 10, 30 20, 40 10),(30 10, 30 0, 40 10, 30 20, 30 10)))
     * @return
     * @throws ParseException
     */
    public MultiPolygon createMulPolygonByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        MultiPolygon mpolygon = (MultiPolygon) reader.read(wktString);  
        return mpolygon;  
    }
    
    /**
     * 至少一个公共点(相交)
     * 几何形状至少有一个共有点（区别于脱节）
     * @param g1
     * @param g2
     * @return
     */
    public boolean isIntersects(Geometry g1,Geometry g2){
        return g1.intersects(g2);
    }
    
    /**
     * 几何形状没有共有的点。
     * @param g1
     * @param g2
     * @return
     */
    public boolean isDisjoint(Geometry g1,Geometry g2){
        return g1.disjoint(g2);
    }
    
    /**
     * 几何形状共享一些但不是所有的内部点。
     * @param g1
     * @param g2
     * @return
     */
    public boolean isCrosses(Geometry g1,Geometry g2){
        return g1.crosses(g2);
    }
    
    /**
     * 几何形状g1的线都在几何形状g2内部。
     * @param g1
     * @param g2
     * @return
     */
    public boolean isWithin(Geometry g1,Geometry g2){
        return g1.within(g2);
    }
    
    /**
     * 几何形状g1是否包含g2
     * @param g1
     * @param g2
     * @return
     */
    public boolean isContains(Geometry g1,Geometry g2){
        return g1.contains(g2);
    }
    
    /**
     * 计算wkt格式有多少对xy
     * @param wkt
     * @return
     */
    public int getWktXYCount(String wkt){
        int retCount = 0;
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(wkt);
        while(matcher.find()){
            retCount++;
        }
        return retCount;
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
    
    /**
     * 返回(A)与(B)中距离最近的两个点的距离 
     * @param a
     * @param b
     * @return
     */
    public double distanceGeo(Geometry a,Geometry b){  
        return a.distance(b);  
    }
    
    /**
     * 缓冲区(如果负责参考BufferOp)
     * @param geo
     * @param radius
     * @return
     */
    public Geometry buffer(Geometry geo,double radius){
        return geo.buffer(radius);
    }
    
    /**
     * 判断是否重叠
     * 几何形状拓扑上相等。
     * @param geo1
     * @param geo2
     * @return
     */
    public boolean isOverlap(Geometry geo1,Geometry geo2){
        return geo1.equals(geo2);
    }
    
    /**
     * 判断是否接触
     * 几何形状有至少一个公共的边界点，但是没有内部点。
     * @param geo1
     * @param geo2
     * @return
     */
    public boolean isTouchs(Geometry geo1,Geometry geo2){
        return geo1.touches(geo2);
    }
    
    /**
     * 返回两个几何对象的交集
     * @param geo1
     * @param geo2
     * @return
     */
    public Geometry intersection(Geometry geo1,Geometry geo2){
        return geo1.intersection(geo2);
    }
    
    /**
     * geo1,geo2形状的对称差异分析就是位于geo1中或者geo2中但不同时在geo1,geo2中的所有点的集合 
     * (相当于交集之外的)
     * @param geo1
     * @param geo2
     * @return
     */
    public Geometry symDifference(Geometry geo1,Geometry geo2){
        return geo1.symDifference(geo2);
    }
    
    /**
     * 几何对象合并
     * @param geo1
     * @param geo2
     * @return
     */
    public Geometry union(Geometry geo1,Geometry geo2){
        return geo1.union(geo2);
    }
    
    /**
     * 在geo1几何对象中有的，但是geo2几何对象中没有
     * @param geo1
     * @param 
     * @return
     */
    public Geometry difference(Geometry geo1,Geometry geo2){
        return geo1.difference(geo2);
    }
    
    /**
     * 包含几何形体的所有点的最小凸壳多边形（外包多边形）
     * @param geo
     * @return
     */
    public Geometry convexHull(Geometry geo){
        return geo.convexHull();
    }
    
    /**
     * 合并线 线路合并，线路之间不产生有交点
     * union会产生交点
     * @param wkts
     * @return
     */
    public Collection<Geometry> mergerLines(List<String> wkts){
        Collection<Geometry> collect = null;
        try {
            LineMerger lineMerger = new LineMerger();
            List<Geometry> geoList = new ArrayList<Geometry>();
            WKTReader reader = new WKTReader(gf);  
            for(int i=0;i<wkts.size();i++){
                geoList.add(i, reader.read(wkts.get(i)));
            }
            lineMerger.add(geoList);
            collect = lineMerger.getMergedLineStrings();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return collect;
    }
}
