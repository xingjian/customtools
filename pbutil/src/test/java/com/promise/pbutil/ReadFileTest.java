package com.promise.pbutil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import com.hadoop.compression.lzo.LzopCodec;
import com.promise.cn.util.PBFileUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月8日 下午6:27:30  
 */
public class ReadFileTest {

    @Test
    public void testReadFileLZO() throws Exception{
        Configuration conf=new Configuration();    
        conf.set("mapred.job.tracker", "local");    
        conf.set("fs.default.name", "file:///");    
        conf.set("idecs", "copression.lzo.LzoCodec");
        LzopCodec lzo=new LzopCodec(); 
        String line=null;    
        lzo.setConf(conf);    
        InputStream is=lzo.createInputStream(new FileInputStream("G:\\项目文档\\武汉tocc\\gisdata\\WHTaxiDataPart1\\WHTaxiDataPart1\\d=02\\w=4\\00.lzo"));    
        InputStreamReader isr=new InputStreamReader(is);    
        BufferedReader reader=new BufferedReader(isr);    
        List<String> result=new ArrayList<String>();    
        while((line=reader.readLine())!=null){
            System.out.println(line);
            result.add(line);    
        }    
    }
    
    @Test
    public void testReadJSONFile(){
        String filePath = "F:\\gitworkspace\\customtools\\pbutil\\src\\test\\resources\\testjson.json";
        JSONObject jsonObject = PBFileUtil.ReadJSONFile(filePath);
        JSONArray array = jsonObject.getJSONArray("features");
        for(int i=0;i<array.size();i++){
            JSONObject j1 = array.getJSONObject(i);
            System.out.println(j1.get("type"));
            System.out.println(j1.get("properties"));
        }
    }
}
