package com.promise.pbutil;

import java.util.Map;

import org.junit.Test;

import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PrintUtil;

/**  
 * 功能描述:PBFileUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月7日 上午9:33:41  
 */
public class PBFileUtilTest {

    @Test
    public void testReadPropertiesFile(){
        String filePath = "G:\\项目文档\\公交都市\\数据\\busline.properties";
        Map<String,String> map= PBFileUtil.ReadPropertiesFile(filePath);
        PrintUtil.PrintObject(map);
    }
}
