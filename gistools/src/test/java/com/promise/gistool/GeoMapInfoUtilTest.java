package com.promise.gistool;

import java.io.File;

import org.junit.Test;

import com.promise.gistool.util.GeoMapInfoUtil;

/**  
 * 功能描述: GeoMapInfoUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月3日 下午1:59:29  
 */
public class GeoMapInfoUtilTest {

    @Test
    public void testReadMapInfoFile() throws Exception{
        File tifFile = new File("G:\\项目文档\\手机信令\\gis数据\\R_Namebeijing.mif");
        GeoMapInfoUtil.ReadMapInfoFile(tifFile);
    }
}
