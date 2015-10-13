package com.promise.gistool;

import java.util.List;

import org.junit.Test;

import com.promise.cn.util.PrintUtil;
import com.promise.gistool.util.GeoToolsGeometry;

/**  
 * 功能描述:GeoToolsGeometry测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月12日 上午11:47:51  
 */
public class GeoToolsGeometryTest {

    public GeoToolsGeometry gtg = new GeoToolsGeometry();
    
    @Test
    public void testGetXYByWkt(){
        String str = "MULTIPOLYGON(((116.034602615 40.4626854890001,116.034492188 40.4626025220001,116.034324938 40.462544345,116.034161115 40.4625366990001,116.033920114 40.4625315820001,116.033605745 40.4625521740001,116.033372318)))";
        List<String> result = gtg.getXYByWkt(str);
        PrintUtil.PrintObject(result);
    }
}
