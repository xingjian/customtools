package com.promise.pbutil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.promise.cn.util.PBCrawlerUtil;

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
}
