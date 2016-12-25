package com.promise.pbutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoDecompressor;
import org.anarres.lzo.LzoInputStream;
import org.anarres.lzo.LzoLibrary;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import com.hadoop.compression.lzo.LzopCodec;

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
}
