package com.promise.gistool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.promise.cn.util.POIExcelUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年4月23日 上午10:04:45  
 */
public class XLBusGML {

    private String headerFile;
    private String envelopeFile;
    private String contentFile;
    private String outputFile;
    
    public <T> String createDataFile(List<T> listObject){
        String ret = "success";
        try {
            File output = new File(outputFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            String envelopeContent = getFileString(this.envelopeFile);
            String header = getFileString(this.headerFile);
            String content = getFileString(this.contentFile);
            writer.write(header,0,header.length());
            writer.write(envelopeContent,0,envelopeContent.length());
            
            Field[] fds = null;
            Class clazz = null;
            Map<String,String> map = new HashMap<String, String>();
            for (Object object : listObject){
                //获取集合中的对象类型
                if (null == clazz) {
                    clazz = object.getClass();
                    //获取他的字段数组
                    fds = clazz.getDeclaredFields();
                    for(int i=0;i<fds.length;i++){  
                        String columnLabel = fds[i].getName().toString();
                        map.put(columnLabel, "get"+POIExcelUtil.ChangeFirstUpper(columnLabel));
                    }
                }
                String c = content;
                for(Map.Entry<String, String> entry : map.entrySet()){
                    Method metd = clazz.getMethod(entry.getValue(), null);
                    String attributeName = entry.getKey();
                    String attributeValue = metd.invoke(object, null).toString();
                    c = c.replace("${"+attributeName+"}",attributeValue);
                }
                writer.write(c,0,c.length());
            }
            writer.write("</gml:FeatureCollection>",0,24);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            ret = "failture";
            e.printStackTrace();
        }
        return ret;
    }
    
    public String getFileString(String fileName) throws Exception{
        InputStream m = new FileInputStream(new File(fileName));
        BufferedReader reader = new BufferedReader(new InputStreamReader(m, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();        
    }
    
    public String getHeaderFile() {
        return headerFile;
    }

    public void setHeaderFile(String headerFile) {
        this.headerFile = headerFile;
    }

    public String getEnvelopeFile() {
        return envelopeFile;
    }

    public void setEnvelopeFile(String envelopeFile) {
        this.envelopeFile = envelopeFile;
    }

    public String getContentFile() {
        return contentFile;
    }

    public void setContentFile(String contentFile) {
        this.contentFile = contentFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public static void main(String[] args) {

    }

}
