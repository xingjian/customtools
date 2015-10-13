package com.promise.pbutil;

import org.junit.Test;

import com.promise.cn.util.PBMathUtil;

/**  
 * 功能描述:PBMathUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月12日 上午11:37:50  
 */
public class PBMathUtilTest {

    @Test
    public void testDistance(){
        double result = PBMathUtil.Distance(0, 0, 4, 3);
        System.out.println(result);
    }
}
