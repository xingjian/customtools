package com.promise.gistool;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.DBConnection;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;

/**  
 * 功能描述: GeoShapeUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月9日 下午1:43:14  
 */
public class GeoShapeUtilTest {

    @Test
    public void testAppendFeatureToShapeFile(){
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures("F:\\gitworkspace\\customtools\\gistools\\src\\main\\resources\\huanxian1.shp", "GBK");
        SimpleFeatureIterator sfi = sfc.features();
        List<SimpleFeature> list = new ArrayList<SimpleFeature>();
        while(sfi.hasNext()){
            list.add(sfi.next());
        }
        List<String> fileds = new ArrayList<String>();
        fileds.add("the_geom:the_geom");
        GeoShapeUtil.AppendFeatureToShapeFile(list, "F:\\gitworkspace\\customtools\\gistools\\src\\main\\resources\\huanxian2.shp", fileds, "GBK");
    }
    
    @Test
    public void testExportTableToShape(){
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        String sql_query = "select buslineid,st_astext(the_geom) wkt from buslinelink where buslineid in ("+
                "select buslineid from buslinelink_merge t1 where t1.id in ("+
                "select ref_id from td_line_station_ref_wc t2 where t2.tdname='四环辅路'))";
        String shapeFilePath = "d:\\4ringbuslinelink.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testCreateShapeByTxt(){
        String txtpath = "F:\\xy.txt";
        String splitChar = ";";
        String crs = "EPSG:4326";
        String encoding = "GBK";
        //geometry type 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
        String geometryType = "1";
        String topath = "F:\\export.shp";
        String[] attriDesc = new String[]{"gid:int","netid:String","areaid:String","name:String","type:String","lockid:double","starttime:double","site:String","status:double","mark:String","street:String",
                "x:double","y:double", "bjx:double", "bjy:double","the_geom:geometry-wkt"};
        GeoShapeUtil.CreateShapeByTxt(txtpath, splitChar, crs, encoding, attriDesc, topath, geometryType);
    }
    
    @Test
    public void testReadShapeFileFeatures() throws Exception{
        String shapePath = "G:\\项目文档\\微波路段\\城市道路84+北京地方\\城市道路全属性_北京地方\\北京地方全属性\\Road_LN_02.shp";
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures(shapePath, "GBK");
        SimpleFeatureIterator sfi = sfc.features();
        while(sfi.hasNext()){
            SimpleFeature sf = sfi.next();
            System.out.println(GeoToolsGeometry.FeatureToJSON(sf));
        }
    }
    
    
    /**
     * 导出四环的公交车路链
     */
    @Test
    public void testExportTableToShapeKJF(){
        String url = "jdbc:postgresql://localhost:5432/mobile";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql_query = "select t1.id,t1.qxm,t1.fid,st_astext(t1.the_geom) wkt,t2.count from zxcxzhzhydflqc t1 left join result_home201409_result5h1 t2 on t1.fid=t2.fid";
        String shapeFilePath = "d:\\result_home201409_result5h2.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","6","EPSG:4326");
        System.out.println(result);
    }
}
