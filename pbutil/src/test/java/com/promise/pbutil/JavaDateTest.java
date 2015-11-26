package com.promise.pbutil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**  
 * 功能描述: java 日期测试类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月5日 上午11:40:59  
 */
public class JavaDateTest {

    /**
     * 测试日期相减
     */
    @Test
    public void testDateReduce() throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        Calendar cal = Calendar.getInstance();
        System.out.println(format.format(cal.getTime()));
        cal.add(Calendar.MINUTE, -5);
        int day=cal.getActualMaximum(Calendar.DAY_OF_MONTH);    
        System.out.println(day); 
        int minute=cal.get(Calendar.MINUTE);
        Date dateCreate = cal.getTime();
        System.out.println(format.format(dateCreate));
    }
}
