package com.promise.gistool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.gistool.util.GISCoordinateTransform;
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
    
    /**
     * 测试切图服务wms
     */
    @Test
    public void testCreateMercator3857Cache(){
        //115.55460357666  39.4103164672852  117.384056091309  40.6935348510742
        String wmsurl = "http://192.168.1.105:8888/geoserver/buscity/wms?service=WMS&version=1.1.0&request=GetMap&layers=buscity:navigationline&styles=&width=256&height=256&srs=EPSG:900913&TRANSPARENT=true&FORMAT=image/png"; 
        int threadNum = 3;
        double[] dArr1 = GISCoordinateTransform.From84To900913(115.55460357666,39.4103164672852);
        double[] dArr2 = GISCoordinateTransform.From84To900913(117.384056091309,40.6935348510742);
        double minX = dArr1[0]; 
        double minY = dArr1[1];
        double maxX = dArr2[0]; 
        double maxY = dArr2[1]; 
        String filePath = "d://mapcache//wms"; 
        int zLevel = 12; 
        String format = "png"; 
        List<String> list = MapServerUtil.CreateMercator3857Cache(wmsurl, threadNum, minX, minY, maxX, maxY, filePath, zLevel, format,null);
        PrintUtil.PrintObject(list);
    }
    
    @Test
    public void testGetBBoxStrs(){
        //12705075.4672818444669246673583984375  3593920.262830469  12765584.334795720875263214111328125  3551594.282544668
        String wmsurl = "http://localhost:8080/geoserver/wmsserver?version=1.3.0&layers=busline"; 
        double minX = 12705075.4672818444669246673583984375; 
        double minY = 3551594.282544668;
        double maxX = 12765584.334795720875263214111328125; 
        double maxY = 3593920.262830469; 
        int zLevel = 12; 
        List<String> result = MapServerUtil.GetWMSURLSByExtent(wmsurl,zLevel, minX, minY, maxX, maxY, 256);
        for(String str:result){
            System.out.println(str);
        }
    }
    
    /**
     * 测试下周指定范围的高德地图
     */
    @Test
    public void testGetGDTileByExtent(){
        //115.55460357666  39.4103164672852  117.384056091309  40.6935348510742
        int threadNum = 3;
        double[] dArr1 = GISCoordinateTransform.From84To900913(115.55460357666,39.4103164672852);
        double[] dArr2 = GISCoordinateTransform.From84To900913(117.384056091309,40.6935348510742);
        double minX = dArr1[0]; 
        double minY = dArr1[1];
        double maxX = dArr2[0]; 
        double maxY = dArr2[1]; 
        String filePath = "d://gdmapcache//image"; 
        int zLevel = 12; 
        String format = "png"; 
        Map<String,List<String>> list = MapServerUtil.GetGDTileByExtent(null, threadNum, minX, minY, maxX, maxY, filePath, zLevel, format,"image");
        //List<String> list = MapServerUtil.GetGDTileVectorURLListByExtent(null, threadNum, minX, minY, maxX, maxY, zLevel, format);
        PrintUtil.PrintObject(list);
    }
    
    
    @Test
    public void testCreateMercatorBaiDuCache(){
        //115.55460357666  39.4103164672852  117.384056091309  40.6935348510742
        String wmsurl = "http://localhost:8080/geoserver/beijingmap/wms?service=WMS&version=1.3.0&request=GetMap&layers=beijingmap:beijingmapgroup&styles=&width=256&height=256&srs=EPSG:900913&TRANSPARENT=true&FORMAT=image/png"; 
        int threadNum = 3;
        double[] dArr1 = GISCoordinateTransform.From84To900913(115.38424,39.43097);
        double[] dArr2 = GISCoordinateTransform.From84To900913(117.5,41.06083);
        double minX = dArr1[0]; 
        double minY = dArr1[1];
        double maxX = dArr2[0]; 
        double maxY = dArr2[1]; 
        String filePath = "d://mapcache//wms"; 
        int zLevel = 16; 
        String format = "png"; 
        List<String> list = MapServerUtil.CreateMercator3857Cache(wmsurl, threadNum, minX, minY, maxX, maxY, filePath, zLevel, format,null);
        PrintUtil.PrintObject(list.size());
    }
    
    
    @Test
    public void testCreateMercatorBaiDuCacheBlack(){
        //115.55460357666  39.4103164672852  117.384056091309  40.6935348510742
        String wmsurl = "http://localhost:8080/geoserver/beijingmap/wms?service=WMS&version=1.3.0&request=GetMap&layers=beijingmap:beijingmapgroup_black&styles=&width=256&height=256&srs=EPSG:900913&TRANSPARENT=true&FORMAT=image/png"; 
        int threadNum = 3;
        double[] dArr1 = GISCoordinateTransform.From84To900913(115.38424,39.43097);
        double[] dArr2 = GISCoordinateTransform.From84To900913(117.5,41.06083);
        double minX = dArr1[0]; 
        double minY = dArr1[1];
        double maxX = dArr2[0]; 
        double maxY = dArr2[1];
        List<String> list = PBFileUtil.ReadFileByLine("d:\\beijingtile.txt");
        Map<String,String> filter = new HashMap<String, String>();
        for(String str : list){
            str.split(".");
            filter.put(str.split("\\.")[0], str);
        }
        String filePath = "F:\\gitworkspace\\opengis\\mapofflinestudy\\src\\main\\webapp\\tiles\\cus_beijing_baidu_black"; 
        String format = "png"; 
        for(int i=7;i<=16;i++){
            List<String> listtemp = MapServerUtil.CreateMercator3857Cache(wmsurl, threadNum, minX, minY, maxX, maxY, filePath, i, format,filter);
            PrintUtil.PrintObject(listtemp.size());
        }
        
        
       
    }
    
    
    
    @Test
    public void findNullPng(){
        String filesPath = "";
        File file = new File(filesPath);
        List<String> result = new ArrayList<String>();
        File[] fileList = file.listFiles();
        for(File fTemp : fileList){
            if(fTemp.isDirectory()){
                
            }
        }
        
    }
}
