package com.promise.gistool;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.promise.cn.util.PBFileUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.PBTileStacheUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年2月17日 下午3:00:42  
 */
public class PBTileStacheTest {

    @Test
    public void testGetConfigJSONFile(){
        String filePath = "F:\\gitworkspace\\customtools\\gistools\\src\\main\\resources\\pbtilestacheconfig.json";
        JSONObject jsonObject = PBTileStacheUtil.GetConfigJSONFile(filePath);
        String name = jsonObject.getString("name");
        String path = jsonObject.getString("path");
        String description = jsonObject.getString("description");
        String shapefile = jsonObject.getString("shapefile");
        String xmin = jsonObject.getString("xmin");
        String ymin = jsonObject.getString("ymin");
        String xmax = jsonObject.getString("xmax");
        String ymax = jsonObject.getString("ymax");
        String zoom = jsonObject.getString("zoom");
        System.out.println(name);
        System.out.println(path);
        System.out.println(description);
        System.out.println(shapefile);
        System.out.println(xmin);
        System.out.println(ymin);
        System.out.println(xmax);
        System.out.println(ymax);
        System.out.println(zoom);
    }
    
    
    @Test
    public void testShapeToGeoJSON(){
        ConversionUtil.ShapeToGeoJSON("d:\\test1.geojson", "G://项目文档//公交都市//giss数据//四维行政区划//beijingqx_new.shp", "UTF-8");
    }
    
    @Test
    public void testGenVectorTileFileByShape(){
        String filePath = "F:\\gitworkspace\\customtools\\gistools\\src\\main\\resources\\pbtilestacheconfig.json";
        JSONObject jsonObject = PBFileUtil.ReadJSONFile(filePath);
        String name = jsonObject.getString("name");
        String path = jsonObject.getString("path");
        String description = jsonObject.getString("description");
        String shapefile = jsonObject.getString("shapefile");
        String xmin = jsonObject.getString("xmin");
        String ymin = jsonObject.getString("ymin");
        String xmax = jsonObject.getString("xmax");
        String ymax = jsonObject.getString("ymax");
        String zoom = jsonObject.getString("zoom");
        System.out.println(name);
        System.out.println(path);
        System.out.println(description);
        System.out.println(shapefile);
        System.out.println(xmin);
        System.out.println(ymin);
        System.out.println(xmax);
        System.out.println(ymax);
        System.out.println(zoom);
        String result = PBTileStacheUtil.GenVectorTileFileByShape(shapefile, Double.parseDouble(xmin), Double.parseDouble(ymin), Double.parseDouble(xmax), Double.parseDouble(ymax), 13, path, "UTF-8",null,"EPSG:4326",true);
        System.out.println(result);
    }
}
