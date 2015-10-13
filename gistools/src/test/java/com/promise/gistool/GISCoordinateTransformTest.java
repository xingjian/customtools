package com.promise.gistool;

import java.math.BigDecimal;

import org.junit.Test;

import com.promise.gistool.util.GISCoordinateTransform;

/**  
 * 功能描述: 坐标转换测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月11日 上午11:52:57  
 */
public class GISCoordinateTransformTest {

    @Test
    public void testFrom84To900913(){
        double[] ret = GISCoordinateTransform.From84To900913(116.397066,39.9172);
        BigDecimal bd = new BigDecimal(ret[0]);
        
        System.out.println(bd.toString()+","+Double.toString(ret[1]));
    }
}
