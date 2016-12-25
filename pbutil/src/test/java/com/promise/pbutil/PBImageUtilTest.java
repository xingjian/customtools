package com.promise.pbutil;

import org.junit.Test;

import com.promise.cn.util.PBImageUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年11月9日 下午2:48:21  
 */
public class PBImageUtilTest {

    @Test
    public void testCutImage() throws Exception{
        String fileSrc = "G:\\项目文档\\TOCC\\tocc截图\\tocc截图\\监测报告\\数据管理.PNG";
        PBImageUtil.CutImage(fileSrc, "G:\\项目文档\\TOCC\\tocc截图\\tocc截图\\监测报告\\"+"数据管理Cut.PNG", 100, 100, 600, 600, "png");
    }
    
    @Test
    public void testMergeImage() throws Exception{
        int xCount = 1559 - 1540+1;
        int yCount = 3383-3362+1;
        String[][] files = new String[xCount][yCount];
        for(int i=1540;i<=1559;i++){
            for(int j=3362;j<=3383;j++){
                files[i-1540][j-3362] = "D:\\mapcache\\wms\\12\\"+i+"\\"+j+".png";
            } 
        }
        PBImageUtil.MergeImage(files, "D:\\mapcache\\wms\\12"+"merge.PNG");
    }
    
    
    @Test
    public void testOverlapImage() throws Exception{
        PBImageUtil.OverlapImage("D:\\gdmapcache\\image\\12merge.PNG", "D:\\mapcache\\wms\\12merge.PNG", "D:\\ddddd.png",1.0f);
    }
}
