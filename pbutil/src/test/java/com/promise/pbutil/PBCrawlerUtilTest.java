package com.promise.pbutil;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.promise.cn.util.PBCrawlerUtil;
import com.promise.cn.util.PBFileUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月13日 下午1:31:17  
 */
public class PBCrawlerUtilTest {

    @Test
    public void testGetByString() throws Exception{
        String url = "http://ditu.amap.com/service/poiInfo?query_type=TQUERY&city=420100&keywords=%E9%9B%84%E6%A5%9A%E5%A4%A7%E9%81%93&pagesize=20&pagenum=1&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&div=PC1000&addr_poi_merge=true&is_classify=true&geoobj=114.287096%7C30.490493%7C114.45189%7C30.527615";
        String result = PBCrawlerUtil.GetByString(url);
        JSONObject jb = JSONObject.fromObject(result);
        JSONArray ja = jb.getJSONArray("data");
        for(int i=0;i<ja.size();i++){
            JSONObject jsonTemp = JSONObject.fromObject(ja.get(i));
            if("polyline".equals(jsonTemp.get("type"))){
                JSONArray ja1 = jsonTemp.getJSONArray("list");
                for(int j=0;j<ja1.size();j++){
                    JSONObject jsonTemp1 = JSONObject.fromObject(ja1.get(i));
                    System.out.println(jsonTemp1);
                }
            }
        }
    }
    
    @Test
    public void testGetImageByURI(){
        String filePath = "d:";
        String fileName = "test.png";
        String uri = "http://ttyjbj.ticp.net:8888/geoserver/buscity/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&STYLES&LAYERS=buscity%3Anavigationline&SRS=EPSG%3A4326&WIDTH=768&HEIGHT=538&BBOX=115.4146423339845%2C39.31309509277345%2C117.5240173339845%2C40.79075622558595";
        try {
            String result = PBCrawlerUtil.GetImageByURI(filePath, fileName, uri);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testGetImageByURIBusCity(){
        String filePath = "D:\\buscitytxb";
        List<String> list = PBFileUtil.ReadFileByLine("d:\\buscitytxb.txt");
        String uri = "http://www.bjbus.com/ext/saleimg/";
        try {
            for(String imageName:list){
                String result = PBCrawlerUtil.GetImageByURI(filePath, imageName, uri+imageName);
                System.out.println(imageName+":"+result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
