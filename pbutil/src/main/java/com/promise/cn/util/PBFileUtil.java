package com.promise.cn.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.hadoop.compression.lzo.LzopCodec;

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
     * 将字符串写入文本文件
     * @param content 字符串内容
     * @param filePath 文件路径
     * @param appendEnter 是否追加回车换行
     * @return
     */
    public static String WriteListToTxt(List<String> list,String filePath,boolean appendEnter){
        String result = "";
        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
        FileWriter writer;
        try {
            writer = new FileWriter(filePath, true);
            for(String content:list){
                if(appendEnter){
                    writer.write(content+"\n");
                }else{
                    writer.write(content);
                }
                
            }
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
    
    /**
     * 读取csv文件，返回List<String>集合
     * @param filePath csv文件路径
     * @param encoding 读取文件的编码
     * @return
     */
    public static List<String> ReadCSVFile(String filePath,String encoding){
        List<String> result = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis,encoding);
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            while((s = br.readLine())!=null){
                if(s.trim()!=""){
                    result.add(s);
                }
            }
            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 读取LzoFile并将结果存放到集合list
     * @param lzoFilePath
     * @param conf
     * @return list
     */
    public static List<String> ReadLzoFileToList(String lzoFilePath,Configuration conf) {
        LzopCodec lzo = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        List<String> result = null;
        String line = null;
        try {
            if(null==conf){
                conf=new Configuration();    
                conf.set("mapred.job.tracker", "local");    
                conf.set("fs.default.name", "file:///");    
                conf.set("idecs", "copression.lzo.LzoCodec");
                conf.set("io.compression.codecs", "com.hadoop.compression.lzo.LzoCodec");
            }
            lzo = new LzopCodec();
            lzo.setConf(conf);
            is = lzo.createInputStream(new FileInputStream(lzoFilePath));
            isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            result = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 读取lzo格式文件内容到txt文件
     * @param lzoFilePath
     * @param txtFilePath
     * @param isAddEnter 是否增加换行符
     * @return
     */
    public static String ReadLZOFileToTxtFile(String lzoFilePath,String txtFilePath,Configuration conf,boolean isAddEnter){
        String result = "success";
        try{
            if(null==conf){
                conf=new Configuration();    
                conf.set("mapred.job.tracker", "local");    
                conf.set("fs.default.name", "file:///");    
                conf.set("idecs", "copression.lzo.LzoCodec");
                conf.set("io.compression.codecs", "com.hadoop.compression.lzo.LzoCodec");
            }
            LzopCodec lzo=new LzopCodec(); 
            String line=null;    
            lzo.setConf(conf);    
            InputStream is=lzo.createInputStream(new FileInputStream(lzoFilePath));    
            InputStreamReader isr=new InputStreamReader(is);    
            BufferedReader reader=new BufferedReader(isr);    
            FileWriter writer = new FileWriter(txtFilePath, true);
            while((line=reader.readLine())!=null){
                if(isAddEnter){
                    writer.write(line+"\n");
                }else{
                    writer.write(line);
                }
                
            }
            writer.close();
            reader.close();
            isr.close();
            is.close();
        }catch(Exception e){
            e.printStackTrace();
            result = "failture";
        }
        return result;
    }
    
    /**
     * 写数据到lzo格式文件
     * @param destLzoFilePath
     * @param conf
     * @param datas
     */
    public static void WriteToLZOFile(String destLzoFilePath,Configuration conf,byte[] datas){  
        LzopCodec lzo=null;  
        OutputStream out=null;  
        try {  
            if(null==conf){
                conf=new Configuration();    
                conf.set("mapred.job.tracker", "local");    
                conf.set("fs.default.name", "file:///");    
                conf.set("idecs", "copression.lzo.LzoCodec");
                conf.set("io.compression.codecs", "com.hadoop.compression.lzo.LzoCodec");
            }
            lzo=new LzopCodec();  
            lzo.setConf(conf);  
            out=lzo.createOutputStream(new FileOutputStream(destLzoFilePath));  
            out.write(datas);  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{  
          try {  
            if(out!=null){  
              out.close();  
            }  
          } catch (IOException e) {  
            e.printStackTrace();  
          }  
        }  
    }
   
    /**
     * 查询指定目录下以endName结尾的文件
     * @param findPath
     * @param endName
     * @return 返回结果,每个结果为文件的完整路径
     */
    public static List<String> FindFilesByEndName(String findPath,String endName){
       List<String> result = new ArrayList<String>();
       File file = new File(findPath);
       if(file.isFile()){
           String fileNameTemp = file.getName();
           if(fileNameTemp.endsWith(endName)){
               result.add(file.getAbsolutePath());
           }
           return result;
       }else{
           File[] fileArray= file.listFiles();
           if(null==fileArray){
             return result;
           }
           for (int i = 0; i < fileArray.length; i++) {// 如果是个目录
                if (fileArray[i].isDirectory()) {
                    //递归调用
                    List<String> resultTemp = FindFilesByEndName(fileArray[i].getAbsolutePath(), endName);
                    result.addAll(resultTemp);
                    //如果是文件
                } else if (fileArray[i].isFile()) {
                    //如果是以endName结尾的文件
                    if (fileArray[i].getName().endsWith(endName)) {
                        //保存文件完整路径
                        result.add(fileArray[i].getAbsolutePath());
                    }
                }
           }
       }
       return result;
    } 
    
    /**
     * 通过sql导出数据到txt
     * @param sql
     * @param conect
     * @param splitStr
     * @param appendEnter
     * @return
     */
    public static String ExportDataBySQL(String sql,Connection connect,String txtPath,String splitStr,boolean appendEnter){
        String result = "success";
        try{
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int nColumn = rsmd.getColumnCount();
            FileWriter writer;
            try {
                writer = new FileWriter(txtPath, true);
                while(resultSet.next()){
                    String content = "";
                    for(int i=1;i<=nColumn;i++){
                        if(i==nColumn){
                            content = content + resultSet.getString(i);
                        }else{
                            content = content + resultSet.getString(i)+splitStr;
                        }
                    }
                    if(appendEnter){
                        writer.write(content+"\r\n");
                    }else{
                        writer.write(content);
                    }
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }  
          resultSet.close();
          statement.close();
        }catch(Exception e){
            result = "error";
            e.printStackTrace();
        }
        return result;
    }
    
    
    /**
     * 关闭流
     * @param closeables
     */
    public static void CloseQuietly(Closeable... closeables) {
        if(closeables != null) {
            for(Closeable closeable : closeables) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
