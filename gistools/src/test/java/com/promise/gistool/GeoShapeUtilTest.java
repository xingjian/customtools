package com.promise.gistool;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.gistool.util.GeoShapeUtil;

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
    
}
