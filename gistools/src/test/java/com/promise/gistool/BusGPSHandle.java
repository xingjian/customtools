package com.promise.gistool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.GISCoordinateTransform;

/**  
 * 功能描述: 处理公交GPS数据,专门提供公交专用道数据抽取
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年10月28日 下午4:47:33  
 */
public class BusGPSHandle {

    public static List<String> busLineCodes = new ArrayList<String>();
    public static Map<String,String> configMap = new HashMap<String, String>();
    public String lineCodes;
    //原始gps文件路径
    public String gpsPath;
    //生成结果文件
    public String txtPath;
    //1 work  0 not work
    public String dayFlag;
    public String timeValue;
    //1 tongtu  0 datacenter
    public String gpsformat;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒"); 
    public SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
    public Map<String,File> files = new HashMap<String,File>();
    public Map<String,List<GPSMessage>> listGPSMap = new HashMap<String,List<GPSMessage>>();

    /**
     * 读取gps的文件 通途自己备份的格式
     * @param filePath
     */
    public void readGPSFileByPath(String filePath){
        try{
            System.out.println(sdf.format(new Date())+" 开始加载"+filePath+"......");
            BufferedReader br= new BufferedReader(new FileReader(filePath));
            String line = "";
            System.out.println(sdf.format(new Date())+" 加载完成,开始遍历......");
            Calendar calendar = Calendar.getInstance();
            String[] timeValueArr = timeValue.split(";");
            String[] timeArr1 = timeValueArr[0].split(",");//工作日 早
            String[] timeArr2 = timeValueArr[1].split(",");//工作日 晚
            String[] timeArr3 = timeValueArr[2].split(",");//非工作日 早
            String[] timeArr4 = timeValueArr[3].split(",");//非工作日 晚
            //加载车辆自编号和线路号的映射关系
            List<String> carCodeList = PBFileUtil.ReadFileByLine(BusGPSHandle.class.getResource("/").getPath()+"/cardmapping.txt");
            Map<String,String> cardMap = new HashMap<String,String>();
            for(String carMess : carCodeList){
                String[] arr = carMess.split(",");
                cardMap.put(arr[1], arr[2]);
            }
            while ((line=br.readLine())!=null) {
                
                if(!line.trim().equals("")){
                    String[] gpsStrArr = line.split(",");
                    if(gpsStrArr.length<13){
                        continue;
                    }
                    String stemp = gpsStrArr[3];
                    if(null!=cardMap.get(gpsStrArr[1])){
                        stemp = cardMap.get(gpsStrArr[1]);
                    }
                    if(busLineCodes.contains(stemp)){
                        //车次号 时间 经度 纬度
                        String busCode = gpsStrArr[1];
                        String timeStr = gpsStrArr[5];
                        Date dateTemp = sdf1.parse(timeStr);
                        calendar.setTime(dateTemp);
                        int hour=calendar.get(Calendar.HOUR_OF_DAY);
                        String longitdudeStr = gpsStrArr[6];
                        String latitudeStr = gpsStrArr[7];
                        if(null==longitdudeStr||null==latitudeStr||longitdudeStr.trim().equals("")||latitudeStr.trim().equals("")){
                            continue;
                        }
                        try{
                            double b1 = Double.parseDouble(longitdudeStr);
                            double b2 = Double.parseDouble(latitudeStr);
                            if(b1<114||b1>118||b2>42||b2<38){
                                continue;
                            }
                            
                        }catch(Exception e){
                            continue;
                        }
                        String fileName = gpsStrArr[3]+"_"+busCode+"_GPS_CH.txt";
                        if(dayFlag.trim().equals("1")){//工作日
                            if(hour>=Integer.parseInt(timeArr1[0])&&hour<Integer.parseInt(timeArr1[1])){//早高峰
                                GPSMessage gpsTemp = new GPSMessage();
                                gpsTemp.setDate(dateTemp);
                                gpsTemp.setLatitude(latitudeStr);
                                gpsTemp.setLineCode(stemp);
                                gpsTemp.setLineNum(busCode);
                                gpsTemp.setLongitude(longitdudeStr);
                                insertGPSMessageToList("0",gpsTemp);
                            }else if(hour>=Integer.parseInt(timeArr2[0])&&hour<Integer.parseInt(timeArr2[1])){//晚高峰
                                GPSMessage gpsTemp = new GPSMessage();
                                gpsTemp.setDate(dateTemp);
                                gpsTemp.setLatitude(latitudeStr);
                                gpsTemp.setLineCode(stemp);
                                gpsTemp.setLineNum(busCode);
                                gpsTemp.setLongitude(longitdudeStr);
                                insertGPSMessageToList("1",gpsTemp);
                            }
                        }else if(dayFlag.trim().equals("0")){//非工作日
                            if(hour>=Integer.parseInt(timeArr3[0])&&hour<Integer.parseInt(timeArr3[1])){//早高峰
                                GPSMessage gpsTemp = new GPSMessage();
                                gpsTemp.setDate(dateTemp);
                                gpsTemp.setLatitude(latitudeStr);
                                gpsTemp.setLineCode(stemp);
                                gpsTemp.setLineNum(busCode);
                                gpsTemp.setLongitude(longitdudeStr);
                                insertGPSMessageToList("0",gpsTemp);
                            }else if(hour>=Integer.parseInt(timeArr4[0])&&hour<Integer.parseInt(timeArr4[1])){//晚高峰
                                GPSMessage gpsTemp = new GPSMessage();
                                gpsTemp.setDate(dateTemp);
                                gpsTemp.setLatitude(latitudeStr);
                                gpsTemp.setLineCode(stemp);
                                gpsTemp.setLineNum(busCode);
                                gpsTemp.setLongitude(longitdudeStr);
                                insertGPSMessageToList("1",gpsTemp);
                            }
                        }
                        
                    } 
                }
            }
            System.out.println(sdf.format(new Date())+" 处理完成,准备生成文件......");
            br.close();
            
            for (Map.Entry<String, List<GPSMessage>> entry : listGPSMap.entrySet()) {
                String key = entry.getKey();
                List<GPSMessage> listSub = entry.getValue();
                String[] arrTemp = key.split(":");
                
                //0 早高峰  1 晚高峰
                if(arrTemp[0].equals("0")){
                    writeGPSMessageToFile((txtPath+"//070000_090000//VEH_GPS//"+arrTemp[1]),listSub);
                }else if(arrTemp[0].equals("1")){
                    writeGPSMessageToFile((txtPath+"//170000_190000//VEH_GPS//"+arrTemp[1]),listSub);
                }
            }
            System.out.println(sdf.format(new Date())+" 处理完成.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   /**
    * 将GPSMessage 写入文件
    */
    public void writeGPSMessageToFile(String filePath,List<GPSMessage> list){
        Collections.sort(list); 
        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
        FileWriter writer;
        try {
            writer = new FileWriter(filePath, true);
            for(GPSMessage gpsTemp:list){
                //1,$SH01,0,2016/09/24_06:59:44,116.354042,39.853065,116.360229,39.854423,16762,300快内,1,4,50#
                double[] xy = GISCoordinateTransform.From84To02(Double.parseDouble(gpsTemp.getLongitude()), Double.parseDouble(gpsTemp.getLatitude()));
                String xNew = StringUtil.FormatDoubleStr("0.000000", xy[0]);
                String yNew = StringUtil.FormatDoubleStr("0.000000", xy[1]);
                String content = "1,$SH01,0,"+sdf2.format(gpsTemp.getDate())+","+gpsTemp.getLongitude()+","+gpsTemp.getLatitude()+","+xNew+","+yNew+","+gpsTemp.getLineNum()+","+gpsTemp.getLineCode()+",1,4,50#"+"\n";
                writer.write(content);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }
    
    /**
     * 0 早高峰  1 晚高峰
     */
    public void insertGPSMessageToList(String timeType,GPSMessage gpsMessage){
        List<GPSMessage> listTemp = listGPSMap.get(timeType+":"+gpsMessage.getLineCode()+"_"+gpsMessage.getLineNum()+"_GPS_CH.txt");
        if(null==listTemp){
            listTemp = new ArrayList<BusGPSHandle.GPSMessage>();
            listTemp.add(gpsMessage);
            listGPSMap.put(timeType+":"+gpsMessage.getLineCode()+"_"+gpsMessage.getLineNum()+"_GPS_CH.txt", listTemp);
        }else{
            listTemp.add(gpsMessage);
        }
    }
    
    /**
     * 读取gps的文件 数据中心的格式
     * 格式不一样，文件数量也不一样
     * @param filePath
     */
    public void readGPSFileByPathDataCenter(String filePath){
        try{
            System.out.println(sdf.format(new Date())+" 开始加载"+filePath+"......");
            File fileAll = new File(filePath);
            Calendar calendar = Calendar.getInstance();
            String[] timeValueArr = timeValue.split(";");
            String[] timeArr1 = timeValueArr[0].split(",");//工作日 早
            String[] timeArr2 = timeValueArr[1].split(",");//工作日 晚
            String[] timeArr3 = timeValueArr[2].split(",");//非工作日 早
            String[] timeArr4 = timeValueArr[3].split(",");//非工作日 晚
            int index = 0;
            for(File file:fileAll.listFiles()){
                BufferedReader br= new BufferedReader(new FileReader(filePath+"//"+file.getName()));
                String line = "";
                System.out.println(sdf.format(new Date())+" 加载完成,"+index+"----"+file.getName()+",开始遍历......");
               
                while ((line=br.readLine())!=null) {
                    //215,,XL,9134,,运通101,2,2016-09-26 16:59:58,116.210683,39.953997,44,96,24
                    //215,,BAIBUS,12031,,300内,1,2016-09-26 16:59:47,116.343675,39.96631,0,0,
                    if(!line.trim().equals("")){
                        String[] gpsStrArr = line.split(",");
                        if(gpsStrArr.length<13){
                            continue;
                        }
                        String stemp = gpsStrArr[5];
                        if(busLineCodes.contains(stemp)){
                            //车次号 时间 经度 纬度
                            String busCode = gpsStrArr[3];
                            String timeStr = gpsStrArr[7];
                            Date dateTemp = sdf1.parse(timeStr);
                            calendar.setTime(dateTemp);
                            int hour=calendar.get(Calendar.HOUR_OF_DAY);
                            String longitdudeStr = gpsStrArr[8];
                            String latitudeStr = gpsStrArr[9];
                            if(null==longitdudeStr||null==latitudeStr||longitdudeStr.trim().equals("")||latitudeStr.trim().equals("")){
                                continue;
                            }
                            try{
                                double b1 = Double.parseDouble(longitdudeStr);
                                double b2 = Double.parseDouble(latitudeStr);
                                if(b1<114||b1>118||b2>42||b2<38){
                                    continue;
                                }
                                
                            }catch(Exception e){
                                continue;
                            }
                            String fileName = gpsStrArr[5]+"_"+busCode+"_GPS_CH.txt";
                            if(dayFlag.trim().equals("1")){//工作日
                                if(hour>=Integer.parseInt(timeArr1[0])&&hour<Integer.parseInt(timeArr1[1])){//早高峰
                                    GPSMessage gpsTemp = new GPSMessage();
                                    gpsTemp.setDate(dateTemp);
                                    gpsTemp.setLatitude(latitudeStr);
                                    gpsTemp.setLineCode(stemp);
                                    gpsTemp.setLineNum(busCode);
                                    gpsTemp.setLongitude(longitdudeStr);
                                    insertGPSMessageToList("0",gpsTemp);
                                }else if(hour>=Integer.parseInt(timeArr2[0])&&hour<Integer.parseInt(timeArr2[1])){//晚高峰
                                    GPSMessage gpsTemp = new GPSMessage();
                                    gpsTemp.setDate(dateTemp);
                                    gpsTemp.setLatitude(latitudeStr);
                                    gpsTemp.setLineCode(stemp);
                                    gpsTemp.setLineNum(busCode);
                                    gpsTemp.setLongitude(longitdudeStr);
                                    insertGPSMessageToList("1",gpsTemp);
                                }
                            }else if(dayFlag.trim().equals("0")){//非工作日
                                if(hour>=Integer.parseInt(timeArr3[0])&&hour<Integer.parseInt(timeArr3[1])){//早高峰
                                    GPSMessage gpsTemp = new GPSMessage();
                                    gpsTemp.setDate(dateTemp);
                                    gpsTemp.setLatitude(latitudeStr);
                                    gpsTemp.setLineCode(stemp);
                                    gpsTemp.setLineNum(busCode);
                                    gpsTemp.setLongitude(longitdudeStr);
                                    insertGPSMessageToList("0",gpsTemp);
                                }else if(hour>=Integer.parseInt(timeArr4[0])&&hour<Integer.parseInt(timeArr4[1])){//晚高峰
                                    GPSMessage gpsTemp = new GPSMessage();
                                    gpsTemp.setDate(dateTemp);
                                    gpsTemp.setLatitude(latitudeStr);
                                    gpsTemp.setLineCode(stemp);
                                    gpsTemp.setLineNum(busCode);
                                    gpsTemp.setLongitude(longitdudeStr);
                                    insertGPSMessageToList("1",gpsTemp);
                                }
                            }
                            
                        } 
                    }
                }
                
                br.close();
                System.out.println(sdf.format(new Date())+" 处理完成,"+index+"----"+file.getName());
            }
            System.out.println(sdf.format(new Date())+" 处理完成,准备生成文件......");
            for (Map.Entry<String, List<GPSMessage>> entry : listGPSMap.entrySet()) {
                String key = entry.getKey();
                List<GPSMessage> listSub = entry.getValue();
                String[] arrTemp = key.split(":");
                
                //0 早高峰  1 晚高峰
                if(arrTemp[0].equals("0")){
                    writeGPSMessageToFile((txtPath+"//070000_090000//VEH_GPS//"+arrTemp[1]),listSub);
                }else if(arrTemp[0].equals("1")){
                    writeGPSMessageToFile((txtPath+"//170000_190000//VEH_GPS//"+arrTemp[1]),listSub);
                }
            }
            System.out.println(sdf.format(new Date())+" 处理完成.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 启动程序开始
     */
    public String startHandle(){
        String result = "";
        readPropertiesFile(BusGPSHandle.class.getResource("/").getPath()+"/config.properties");
        gpsPath = configMap.get("gpsPath");
        lineCodes = configMap.get("lineCodes");
        busLineCodes = Arrays.asList(lineCodes.split(","));
        dayFlag = configMap.get("dayFlag");
        timeValue = configMap.get("timeValue");
        txtPath = configMap.get("txtPath");
        gpsformat = configMap.get("gpsformat");
        File file = new File(txtPath);
        if(!file.exists()){
            file.mkdir();
        }
        File fileZGF = new File(txtPath+"//070000_090000//VEH_GPS");
        File fileWGF = new File(txtPath+"//170000_190000//VEH_GPS");
        if(!fileZGF.exists()){
            fileZGF.mkdirs();
        }
        
        if(!fileWGF.exists()){
            fileWGF.mkdirs();
        }
        if(gpsformat.trim().equals("1")){
            readGPSFileByPath(gpsPath);
        }else{
            readGPSFileByPathDataCenter(gpsPath);
        }
        
        return result;
    }
    
    
    public void readPropertiesFile(String filePath){
        System.out.println("加载配置文件config.properties,请确保和当前执行程序在同一物理位置！");
        Properties prop = new Properties();
        try{
            InputStream in = new BufferedInputStream (new FileInputStream(filePath));
            //解决读取中文问题
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            prop.load(bf);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key=it.next();
                configMap.put(key,prop.getProperty(key));
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        BusGPSHandle busGPSHandle = new BusGPSHandle();
        System.out.println(busGPSHandle.startHandle());
    }

    class GPSMessage implements Comparable<GPSMessage>{
        private String lineCode;
        private String lineNum;
        private Date date;
        private String longitude;
        private String latitude;
        
        
        @Override
        public int compareTo(GPSMessage o) {
            int flag = this.getDate().compareTo(o.getDate());
            return flag;
        }

        public String getLineCode() {
            return lineCode;
        }

        public void setLineCode(String lineCode) {
            this.lineCode = lineCode;
        }

        public String getLineNum() {
            return lineNum;
        }

        public void setLineNum(String lineNum) {
            this.lineNum = lineNum;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
        
        
        
    }
}
