package com.promise.gistool;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.cn.util.PBFileUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GISDBUtil;
import com.tongtu.nomap.core.transform.BeijingToGis84;
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
    
    @Test
    public void testCl(){
        List<String> list = PBFileUtil.ReadFileByLine("d:\\1111.txt");
        List<String> result = new ArrayList<String>();
        for(String s:list){
            String[] arr = s.split(",");
            double x = Double.parseDouble(arr[2]);
            double y = Double.parseDouble(arr[3]);
            double[] wgs84XYArr = BeijingToGis84.transSingle(x, y);
            double[] sss = GISCoordinateTransform.From84To02(wgs84XYArr[0], wgs84XYArr[1]);
            result.add(arr[0]+","+arr[1]+","+sss[0]+","+sss[1]);
        }
        PBFileUtil.WriteListToTxt(result, "d:\\2222.txt", true);
    }
}
