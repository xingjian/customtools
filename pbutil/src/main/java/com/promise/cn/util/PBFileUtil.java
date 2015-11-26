package com.promise.cn.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**  
 * 功能描述: 文件帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年8月27日 下午1:04:52  
 */
public class PBFileUtil {

    /**
     * 读取文件按行读取去除两边空格之后,将每行加入到集合list当中
     * 目前支持txt文档
     * @param filePath 文件路径
     * @return 文件集合
     */
    public static List<String> ReadFileByLine(String filePath){
        List<String> list = new ArrayList<String>();
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while((s = br.readLine())!=null){
                if(s.trim()!=""){
                    list.add(s);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * 将字符串写入文本文件
     * @param content 字符串内容
     * @param filePath 文件路径
     * @return
     */
    public static String WriteStringToTxt(String content,String filePath){
        String result = "";
        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
        FileWriter writer;
        try {
            writer = new FileWriter(filePath, true);
            writer.write(content);  
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }  
       
        return result;
    }
    
    /**
     * 将指定Properties文件转换到Map<String,String>
     * @param filePath 文件路径
     * @return Map<String,String>
     */
    public static Map<String,String> ReadPropertiesFile(String filePath){
        Map<String,String> result = new HashMap<String,String>();
        Properties prop = new Properties();
        try{
            InputStream in = new BufferedInputStream (new FileInputStream(filePath));
            //解决读取中文问题
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            prop.load(bf);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key=it.next();
                result.put(key,prop.getProperty(key));
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
