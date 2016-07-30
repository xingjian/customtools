package com.promise.gistool;

import org.junit.Test;

import com.promise.cn.util.PrintUtil;
import com.promise.gistool.util.MapServerUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年7月7日 上午5:31:11  
 */
public class MapServerUtilTest {

    @Test
    public void testGetMercatorImageXYByWGS1984(){
        int[] result = MapServerUtil.GetMercatorImageXYByWGS1984(116.211382,39.894239, 16);
        PrintUtil.PrintObject(result);
        
    }
}
