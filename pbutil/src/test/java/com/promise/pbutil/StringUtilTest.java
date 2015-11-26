package com.promise.pbutil;

import org.junit.Test;

import com.promise.cn.util.StringUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月4日 下午12:09:19  
 */
public class StringUtilTest {

    @Test
    public void testSubstringByByte(){
        String s = "2015年03月02日01时22分，当事人周德高驾驶冀F903G7号车，在东城区北京站被检查人员示证检查，经查:该当事人由北京站载客二人去东直门，驾驶员与乘客议价30元车费，尚未收取违法所得。该车�";
        String result1 = StringUtil.SubstringByByte(s, 253, "UTF-8");
        String result2 = StringUtil.SubstringByByte(s, 253, "GBK");
        System.out.println(result1);
        System.out.println(result2);
        int len1 = StringUtil.GetWordCountCode(result2, "GBK");
        int len2 = StringUtil.GetWordCountCode(result1, "UTF-8");
        System.out.println(len1);
        System.out.println(len2);
    }
    
    @Test
    public void testIsChineseChar(){
        int minute = 19;
        System.out.println(minute / 5);
        if (minute % 5 != 0){
            minute = minute / 5 * 5;
        }
        System.out.println(minute);
        System.out.println(Math.sin(Math.PI*30/180));
    }
}
