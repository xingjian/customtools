package com.promise.pbutil;

import org.junit.Test;

import com.promise.cn.util.EncryptUtil;

/**  
 * 功能描述: 加密类测试类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年2月16日 下午5:00:45  
 */
public class EncryptUtilTest {

    @Test
    public void testEncryptUtil() throws Exception{
        String username = EncryptUtil.encrypt("xingjian@tongtusoft.com.cn");
        String pawwd = EncryptUtil.encrypt("000000@xj");
        System.out.println(username);
        System.out.println(pawwd);
    }
    
}
