package com.promise.gistool;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISDBUtil;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年5月18日 下午4:34:01  
 */
public class GSGXDataTest {

    @Test
    public void importShapeToPostGisOfRoadStatus236(){
        String shapePath = "G:\\项目文档\\国省干线\\高速匹配\\gs236_84\\GS236_84.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS( "localhost", "5432", "sw_navigation", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "roadstatus236", MultiLineString.class, "EPSG:4326");
    }
    
    @Test
    public void importShapeToPostGisOfSWLine(){
        String shapePath = "G:\\项目文档\\国省干线\\高速匹配\\与高速路段对应的四维道路图\\Export_Output_2.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("localhost", "5432", "sw_navigation", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "roadstatus_sw", MultiLineString.class, "EPSG:4326");
    }
    
    @Test
    public void importShapeToPostGisOfPark080305(){
        String shapePath = "G:\\项目文档\\停车场\\普查小车shape图层\\石景山区84坐标勘测图数据\\080305\\080305_划线车位_夜间.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("localhost", "5432", "park", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "sjs_080305_park", MultiPolygon.class, "EPSG:4326");
    }
    
    
    @Test
    public void importShapeToPostGisOfTrafficArea080305(){
        String shapePath = "D:\\park_area_line_sjs\\park_area_polygon_sjs.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS( "localhost", "5432", "park", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "park_area_polygon_sjs", MultiPolygon.class, "EPSG:4326");
    }
    
    @Test
    public void importShapeToPostGisOfParkfsq(){
        String shapePath = "G:\\项目文档\\停车场\\普查小车shape图层\\石景山区84坐标勘测图数据\\080305\\080305_划线车位_夜间.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("postgis", "localhost", "5432", "park", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "sjs_080305_park", MultiPolygon.class, "EPSG:4326");
    }
    
    
    @Test
    public void importShapeToPostGisOfTrafficArea0fsq(){
        String shapePath = "D:\\park_area_polygon_fsq.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("postgis", "localhost", "5432", "park", "postgis", "postgis");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "park_area_polygon_fsq", MultiPolygon.class, "EPSG:4326");
    }
    
    @Test
    public void generatorRef(){
        
    }
}
