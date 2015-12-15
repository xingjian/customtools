package com.promise.gistool.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;

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
    public static Coordinate coordinate(double x,double y){  
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
     * 创建LineString
     * @param list
     * @xysplit 分隔符
     * item string 
     * @type 0 LineString 1 MultiLineString
     */
    public static Geometry createGeometryLine(List<String> list,String xysplit,String type){
        Coordinate[] coords = new Coordinate[list.size()];
        for(int i=0;i<list.size();i++){
            String[] str = list.get(i).split(xysplit);
            coords[i] = coordinate(Double.parseDouble(str[0]),Double.parseDouble(str[1]));
        }
        LineString ls = createLine(coords);
        if(type.trim().equals("1")){
            LineString[] lineStrings = new LineString[1];
            lineStrings[0] = ls;
            return createMLine(lineStrings);
        }else{
            return ls;
        }
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
    public static MultiLineString createMLine(LineString[] lineStrings){  
        MultiLineString ms = gf.createMultiLineString(lineStrings);  
        return ms;  
    }
    
    /**
     * MULTILINESTRING((0 0, 2 0),(1 1,2 2))
     * @return
     * @throws ParseException
     */
    public static MultiLineString createMLineByWKT(String wktString)throws ParseException{  
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
    public static Polygon createPolygonByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        Polygon polygon = (Polygon) reader.read(wktString);  
        return polygon;  
    }

    /**
     * 根据wkt创建几何对象
     * @param wktString
     * @return
     * @throws ParseException
     */
    public static Geometry createGeometrtyByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        return reader.read(wktString);  
    }
    
    /**
     * 根据JSON创建几何对象
     * @param jsonString
     * @return
     * @throws Exception
     */
    public static Geometry CreateGeometrtyByJSON(String jsonString) throws Exception{  
        //1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
        String geoType = GetGeoTypeByJSONString(jsonString);
        if(geoType.equals("1")){
            return createPointGeoJSON(jsonString);
        }else if(geoType.equals("2")){
            return createMultiPointGeoJSON(jsonString);
        }else if(geoType.equals("3")){
            return createLineGeoJSON(jsonString);
        }else if(geoType.equals("4")){
            return createMultiLineGeoJSON(jsonString);
        }else if(geoType.equals("5")){
            return createPolygonGeoJSON(jsonString);
        }else if(geoType.equals("6")){
            return createMultiPolygonGeoJSON(jsonString);
        }
        return null;
    }
    
    /**
     * 创建一个园
     * @param x
     * @param y
     * @param RADIUS
     * @param sides 圆上面的点个数
     * @return
     */
    public static Polygon createCircle(double x, double y, final double RADIUS,int sides){  
        Coordinate coords[] = new Coordinate[sides+1];  
        for( int i = 0; i < sides; i++){  
            double angle = ((double) i / (double) sides) * Math.PI * 2.0;  
            double dx = Math.cos( angle ) * RADIUS;
            double dy = Math.sin( angle ) * RADIUS;
            coords[i] = new Coordinate( (double) x + dx, (double) y + dy );  
        }
        coords[sides] = coords[0];
        LinearRing ring = gf.createLinearRing( coords );
        Polygon polygon = gf.createPolygon( ring, null );
        return polygon;
    }
    
    /**
     * 创建一个园
     * @param x
     * @param y
     * @param RADIUS
     * @param sides 圆上面的点个数
     * @param initAngle 初始点的角度
     * @return
     */
    public static Polygon createCircle(double x, double y, final double RADIUS,int sides,double initAngle){  
        Coordinate coords[] = new Coordinate[sides+1];  
        for( int i = 0; i < sides; i++){  
            double angle = ((double) i / (double) sides) * Math.PI * 2.0+initAngle;  
            double dx = Math.cos( angle ) * RADIUS;
            double dy = Math.sin( angle ) * RADIUS;
            coords[i] = new Coordinate( (double) x + dx, (double) y + dy );  
        }
        coords[sides] = coords[0];
        LinearRing ring = gf.createLinearRing( coords );
        Polygon polygon = gf.createPolygon( ring, null );
        return polygon;
    }
    
    /**
     * MULTIPOLYGON(((40 10, 30 0, 40 10, 30 20, 40 10),(30 10, 30 0, 40 10, 30 20, 30 10)))
     * @return
     * @throws ParseException
     */
    public static MultiPolygon createMulPolygonByWKT(String wktString) throws ParseException{  
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
    public static boolean isIntersects(Geometry g1,Geometry g2){
        return g1.intersects(g2);
    }
    
    /**
     * 几何形状没有共有的点。
     * @param g1
     * @param g2
     * @return
     */
    public static boolean isDisjoint(Geometry g1,Geometry g2){
        return g1.disjoint(g2);
    }
    
    /**
     * 几何形状共享一些但不是所有的内部点。
     * @param g1
     * @param g2
     * @return
     */
    public static boolean isCrosses(Geometry g1,Geometry g2){
        return g1.crosses(g2);
    }
    
    /**
     * 几何形状g1的线都在几何形状g2内部。
     * @param g1
     * @param g2
     * @return
     */
    public static boolean isWithin(Geometry g1,Geometry g2){
        return g1.within(g2);
    }
    
    /**
     * 几何形状g1是否包含g2
     * @param g1
     * @param g2
     * @return
     */
    public static boolean isContains(Geometry g1,Geometry g2){
        return g1.contains(g2);
    }
    
    /**
     * 计算wkt格式有多少对xy
     * @param wkt
     * @return
     */
    public static int getWktXYCount(String wkt){
        int retCount = 0;
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(wkt);
        while(matcher.find()){
            retCount++;
        }
        return retCount;
    }
    
    /**
     * 返回几何对象的质心点
     * @param geom
     * @return
     */
    public static Point GetCentroid(Geometry geom){
        return geom.getCentroid();
    }
    
    /**
     * 抽取wkt当中的xy对
     * @param wkt
     * @return
     */
    public static List<String> getXYByWkt(String wkt){
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
     * 计算Geojson格式有多少对xy
     * @param wkt
     * @return
     */
    public static int getGeoJSONXYCount(String json){
        int retCount = 0;
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?),([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(json);
        while(matcher.find()){
            retCount++;
        }
        return retCount;
    }
    
    /**
     * 抽取Geojson当中的xy对
     * @param wkt
     * @return
     */
    public static List<String> getXYByGeoJSON(String json){
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?),([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(json);
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
    public static double distanceGeo(Geometry a,Geometry b){  
        return a.distance(b);  
    }
    
    /**
     * 缓冲区(如果负责参考BufferOp)
     * @param geo
     * @param radius
     * @return
     */
    public static Geometry buffer(Geometry geo,double radius){
        return geo.buffer(radius);
    }
    
    /**
     * 缓冲区(如果负责参考BufferOp)
     * @param geo
     * @param radius
     * @param quadrantSegments 边数目默认是8
     * @return
     */
    public static Geometry buffer(Geometry geo,double radius,int quadrantSegments){
        return geo.buffer(radius,quadrantSegments);
    }
    
    /**
     * 缓冲区(如果负责参考BufferOp)
     * @param geo
     * @param radius
     * @param quadrantSegments 边数目默认是8
     * BufferOp.CAP_ROUND - (default) a semi-circle 
     * BufferOp.CAP_BUTT - a straight line perpendicular to the end segment 
     * BufferOp.CAP_SQUARE - a half-square 
     * @return
     */
    public static Geometry buffer(Geometry geo,double radius,int quadrantSegments,int endCapStyle){
        return geo.buffer(radius,quadrantSegments,endCapStyle);
    }
    
    
    /**
     * 判断是否重叠
     * 几何形状拓扑上相等。
     * @param geo1
     * @param geo2
     * @return
     */
    public static boolean isOverlap(Geometry geo1,Geometry geo2){
        return geo1.equals(geo2);
    }
    
    /**
     * 判断是否接触
     * 几何形状有至少一个公共的边界点，但是没有内部点。
     * @param geo1
     * @param geo2
     * @return
     */
    public static boolean isTouchs(Geometry geo1,Geometry geo2){
        return geo1.touches(geo2);
    }
    
    /**
     * 返回两个几何对象的交集
     * @param geo1
     * @param geo2
     * @return
     */
    public static Geometry intersection(Geometry geo1,Geometry geo2){
        return geo1.intersection(geo2);
    }
    
    /**
     * geo1,geo2形状的对称差异分析就是位于geo1中或者geo2中但不同时在geo1,geo2中的所有点的集合 
     * (相当于交集之外的)
     * @param geo1
     * @param geo2
     * @return
     */
    public static Geometry symDifference(Geometry geo1,Geometry geo2){
        return geo1.symDifference(geo2);
    }
    
    /**
     * 几何对象合并
     * @param geo1
     * @param geo2
     * @return
     */
    public static Geometry union(Geometry geo1,Geometry geo2){
        return geo1.union(geo2);
    }
    
    /**
     * 根据wkt返回geometry类型 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
     * @param wktString
     * @return
     */
    public static String GetGeoTypeByWKTString(String wktString){
        if(wktString.toLowerCase().indexOf("multipoint")!=-1){
            return "2";
        }else if(wktString.toLowerCase().indexOf("point")!=-1){
            return "1";
        }else if(wktString.toLowerCase().indexOf("multilinestring")!=-1){
            return "4";
        }else if(wktString.toLowerCase().indexOf("linestring")!=-1){
            return "3";
        }else if(wktString.toLowerCase().indexOf("multipolygon")!=-1){
            return "6";
        }else if(wktString.toLowerCase().indexOf("polygon")!=-1){
            return "5";
        }
        return null;
    }
    
    /**
     * 根据json返回geometry类型 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
     * @param wktString
     * @return
     */
    public static String GetGeoTypeByJSONString(String jsonString){
        if(jsonString.toLowerCase().indexOf("multipoint")!=-1){
            return "2";
        }else if(jsonString.toLowerCase().indexOf("point")!=-1){
            return "1";
        }else if(jsonString.toLowerCase().indexOf("multilinestring")!=-1){
            return "4";
        }else if(jsonString.toLowerCase().indexOf("linestring")!=-1){
            return "3";
        }else if(jsonString.toLowerCase().indexOf("multipolygon")!=-1){
            return "6";
        }else if(jsonString.toLowerCase().indexOf("polygon")!=-1){
            return "5";
        }
        return null;
    }
    
    
    /**
     * 多个几何对象合并
     * 传入的的格式为wkt格式集合
     * @param wktList
     * @return
     */
    public static Geometry UnionManyGeo(List<String> wktList) throws Exception{
        Geometry geo = null;
        for(String wkt : wktList){
            String geoType = GetGeoTypeByWKTString(wkt);
            Geometry geo1 = null;
            if(geoType.equals("1")){
                geo1 = createPointByWKT(wkt);
            }else if(geoType.equals("2")){
                geo1 = createMulPointByWKT(wkt);
            }else if(geoType.equals("3")){
                geo1 = createLineByWKT(wkt);
            }else if(geoType.equals("4")){
                geo1 = createMLineByWKT(wkt);
            }else if(geoType.equals("5")){
                geo1 = createPolygonByWKT(wkt);
            }else if(geoType.equals("6")){
                geo1 = createMulPolygonByWKT(wkt);
            }
            if(null==geo){
                geo = geo1;
            }else{
                geo = union(geo, geo1);
            }
        }
        return geo;
    }
    
    /**
     * 在geo1几何对象中有的，但是geo2几何对象中没有
     * @param geo1
     * @param 
     * @return
     */
    public static Geometry difference(Geometry geo1,Geometry geo2){
        return geo1.difference(geo2);
    }
    
    /**
     * 包含几何形体的所有点的最小凸壳多边形（外包多边形）
     * @param geo
     * @return
     */
    public static Geometry convexHull(Geometry geo){
        return geo.convexHull();
    }
    
    /**
     * 合并线 线路合并，线路之间不产生有交点
     * union会产生交点
     * @param wkts
     * @return
     */
    public static Collection<Geometry> mergerLines(List<String> wkts){
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
    
    /**
     * geojson 转换点
     * @param geojson
     * @return
     * @throws IOException
     */
    public static Point createPointGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        Point point = gjson.readPoint(geojson); 
        return point;
    }
    
    /**
     * geojson 转换line
     * @param geojson
     * @return
     * @throws IOException
     */
    public static LineString createLineGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        LineString lineString = gjson.readLine(geojson); 
        return lineString;
    }
    
    /**
     * geojson 转换Polygon
     * @param geojson
     * @return
     * @throws IOException
     */
    public static Polygon createPolygonGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        Polygon polygon = gjson.readPolygon(geojson); 
        return polygon;
    }
    
    /**
     * geojson 转换multiPoint
     * @param geojson
     * @return
     * @throws IOException
     */
    public static MultiPoint createMultiPointGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        MultiPoint multiPoint = gjson.readMultiPoint(geojson); 
        return multiPoint;
    }
    
    /**
     * geojson 转换MultiLineString
     * @param geojson
     * @return
     * @throws IOException
     */
    public static MultiLineString createMultiLineGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        MultiLineString multiLine = gjson.readMultiLine(geojson); 
        return multiLine;
    }
    
    /**
     * geojson 转换MultiPolygon
     * @param geojson
     * @return
     * @throws IOException
     */
    public static MultiPolygon createMultiPolygonGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        MultiPolygon multiPolygon = gjson.readMultiPolygon(geojson); 
        return multiPolygon;
    }
    
    /**
     * Feature对象转换成GeoJSON
     * @param feature
     * @return
     * @throws IOException
     */
    public static String FeatureToJSON(SimpleFeature feature) throws IOException{
        FeatureJSON fjson = new FeatureJSON();
        StringWriter writer = new StringWriter();
        fjson.writeFeature(feature, writer);
        String json = writer.toString();
        return json;
    }
    
    /**
     * GeoJSON对象转换成Feature
     * @param GeoJSON
     * @return
     * @throws IOException
     */
    public static SimpleFeature JSONToFeature(String json) throws IOException{
        FeatureJSON fjson = new FeatureJSON();
        return fjson.readFeature(json);
    }
}
