package com.promise.gistool;

import org.junit.Test;

import com.promise.gistool.util.GeoShapeUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月17日 下午4:53:52  
 */
public class MaYueTest {

    @Test
    public void exportShape(){
        //GeoShapeUtil.ExportShapeToExcel("D:\\my\\cgl.shp", "d:\\zgl.xls", "GBK", false);
        GeoShapeUtil.ExportShapeToTxt("D:\\my\\cgl.shp", "d:\\zgl.txt", "GBK", false,",");
    }
}
