package com.promise.gistool;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PrintUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述: 公交gps数据处理
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年10月30日 下午6:10:14  
 */
public class BusGPSDataHandle {

    /**
     * 将公交专用道的抽取的gps的数据插入到PostGIS
     */
    @Test
    public void testInsertFilterGPSToPostGIS() throws Exception{
        String url = "jdbc:postgresql://localhost:5432/test_gis";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String gpsTxtPath = "D:\\1111\\";
        File fileDir = new File(gpsTxtPath);
        File[] files = fileDir.listFiles();
        int index=1;
        String sqlInsert = "INSERT INTO busgps73016(linename, linecode, remark1, remark2, remark3, timestr, old_longtitude, old_latitude, par_longtitude, par_latitude, remark4, remark5, remark6, remark7, remark8,index) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";
        PreparedStatement ps = connection.prepareStatement(sqlInsert);
        for(File file:files){
            List<String> listTemp = PBFileUtil.ReadFileByLine(gpsTxtPath+"\\"+file.getName());
            String fileName = file.getName();
            String[] strArrFile = fileName.substring(0, fileName.lastIndexOf(".")).split("_");
            ps.setString(1, strArrFile[0]);
            ps.setString(2, strArrFile[1]);
            for(String strTemp:listTemp){
                String[] arrTemp = strTemp.split(",");
                ps.setString(3, arrTemp[0]);
                ps.setString(4, arrTemp[1]);
                ps.setString(5, arrTemp[2]);
                ps.setString(6, arrTemp[3]);
                ps.setDouble(7, Double.parseDouble(arrTemp[4]));
                ps.setDouble(8, Double.parseDouble(arrTemp[5]));
                ps.setDouble(9, Double.parseDouble(arrTemp[6]));
                ps.setDouble(10, Double.parseDouble(arrTemp[7]));
                ps.setString(11, arrTemp[8]);
                ps.setString(12, arrTemp[9]);
                ps.setString(13, arrTemp[10]);
                ps.setString(14, arrTemp[11]);
                ps.setString(15, arrTemp[12]);
                ps.setInt(16,index++);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    
    public static List<String> calcDistance(List<String> list, double allDistance){
        
        List<String> result = new ArrayList<String>();
        double sum = 0.0;
        for(String str:list){
            String[] strArr = str.split(",");
            sum +=Double.parseDouble(strArr[6]);
        }
        
        for(String str:list){
            String[] strArr = str.split(",");
            double dd = Double.parseDouble(strArr[6])*allDistance*1000/sum;
            result.add(strArr[0]+","+strArr[1]+","+strArr[2]+","+strArr[3]+","+strArr[4]+","+strArr[5]+","
                    +StringUtil.FormatDoubleStr("0.00000", dd)+","+strArr[7]+","+strArr[8]+","+strArr[9]+","+strArr[10]);
        }
        PrintUtil.PrintObject(result);
        return result;
    }
    
    @Test
    public void testCalcLinkDistance() throws Exception{
        File file = new File("G:\\项目文档\\公交都市\\transitlaneLineFold\\");
        for(File file1:file.listFiles()){
            
        
        String fileName = file1.getName().split("\\.")[0];
        String filePath = "G:\\项目文档\\公交都市\\transitlaneLineFold\\"+fileName+".csv";
        String exportPath="G:\\项目文档\\公交都市\\transitlaneLineFold\\"+fileName+".txt";
        List<String> list = PBFileUtil.ReadCSVFile(filePath, "UTF-8");
        String sql = "select idlink,length from navigationlink_busline";
        String url = "jdbc:postgresql://localhost:5432/test_gis";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        ResultSet rs = connection.createStatement().executeQuery(sql);
        Map<String,Double> map = new HashMap<String,Double>();
        while(rs.next()){
            String idlink = rs.getString(1);
            String lengthStr = rs.getString(2);
            map.put(idlink, Double.parseDouble(lengthStr));
        }
        List<String> writeList = new ArrayList<String>();
        String currLinkID = "";
        double currLinkIDLength = 0;
        List<String> list1 = null;
        int index = 0;
        for(String str:list){
            index++;
            String[] strArr = str.split(",");
            if(index==list.size()){
                if(!currLinkID.equals(strArr[4])){
                    currLinkID = strArr[4];
                    currLinkIDLength = map.get(currLinkID);
                    list1 = new ArrayList<String>();
                    list1.add(str);
                    List<String> result = calcDistance(list1,currLinkIDLength);
                    writeList.addAll(result);
                }else if(currLinkID.equals(strArr[4])){
                    list1.add(str);
                    List<String> result = calcDistance(list1,currLinkIDLength);
                    writeList.addAll(result);
                }
                break;
            }
            
            if("".equals(currLinkID)){
                currLinkID = strArr[4];
                currLinkIDLength = map.get(currLinkID);
                list1 = new ArrayList<String>();
                list1.add(str);
            }else if(!currLinkID.equals(strArr[4])){
                List<String> result = calcDistance(list1,currLinkIDLength);
                writeList.addAll(result);
                currLinkID = strArr[4];
                currLinkIDLength = map.get(currLinkID);
                list1 = new ArrayList<String>();
                list1.add(str);
            }else if(currLinkID.equals(strArr[4])){
                list1.add(str);
            }
        }
        PrintUtil.PrintObject(writeList);    
        PBFileUtil.WriteListToTxt(writeList, exportPath, true);
        }
    }
    
    
    @Test
    public void testCalcLinkDistance1(){
        String filePath = "G:\\项目文档\\公交都市\\transitlaneLineFold\\line_886_1_new.txt";
        List<String> list = PBFileUtil.ReadFileByLine(filePath);
        double sumDistance = 0.0;
        double sumDistance1 = 0.0;
        Map<String,String> map = new HashMap<String,String>();
        for(String str:list){
            String[] strArr = str.split(",");
            String xy1 = strArr[9];
            String xy2 = strArr[10];
            if(strArr[2].equals("马甸桥-健翔桥")){
                System.out.println(strArr[6]);
                sumDistance = sumDistance + Double.parseDouble(strArr[6]);
                sumDistance1 += getDistance84(Double.parseDouble(xy1.split(" ")[0]),Double.parseDouble(xy1.split(" ")[1]),Double.parseDouble(xy2.split(" ")[0]),Double.parseDouble(xy2.split(" ")[1]));
                if(null==map.get(strArr[4])){
                    map.put(strArr[4], "");
                }
            }
        }
        System.out.println(sumDistance+"----"+sumDistance1);
    }
    
    
    
    @Test
    public void testInsertTxt() throws Exception{
        //872,1,马甸桥-健翔桥,马甸桥-健翔桥1,50173913,1,8.99690,116.3805 39.96828,116.37788 39.98997,116.3805 39.96828,116.38051 39.96836
        String url = "jdbc:postgresql://localhost:5432/test_gis";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String gpsTxtPath = "D:\\2222\\";
        File fileDir = new File(gpsTxtPath);
        File[] files = fileDir.listFiles();
        int index=1;
        String sqlInsert = "INSERT INTO busgpsLineCSV(linecode,linename, old_longtitude, old_latitude, par_longtitude, par_latitude,index,remark1) VALUES (?, ?,?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(sqlInsert);
        for(File file:files){
            List<String> listTemp = PBFileUtil.ReadFileByLine(gpsTxtPath+"\\"+file.getName());
            String fileName = file.getName();
            for(String strTemp:listTemp){
                String[] arrTemp = strTemp.split(",");
                String sStr = arrTemp[9];
                String eStr = arrTemp[10];
                String[] ssArr = sStr.split(" ");
                String[] eeArr = eStr.split(" ");
                
                ps.setString(1, arrTemp[0]);
                ps.setString(2, arrTemp[2]);
                ps.setDouble(3, Double.parseDouble(ssArr[0]));
                ps.setDouble(4, Double.parseDouble(ssArr[1]));
                ps.setDouble(5, Double.parseDouble(eeArr[0]));
                ps.setDouble(6, Double.parseDouble(eeArr[1]));
                ps.setInt(7,index++);
                ps.setString(8,arrTemp[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    
    
    @Test
    public void testDistance84(){
        double dd = getDistance84(116.35833,39.85322,116.35693,39.85283);
        System.out.println(dd);
    }
    
    public static double getDistance84(double x1,double y1,double x2,double y2){
        double lon1 = x1;
        double lat1 = y1;
        double lon2 = x2;
        double lat2 = y2;
        
        double a = 6378137;
        //double a = 6370986;
        double b = 6356752.3142;
        
        double f = 1 / 298.257223563;
        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
        double lambda = L;
        double lambdaP = 0.0;
        double iterLimit = 100;
        double cosSqAlpha = 0.0;
        double cos2SigmaM = 0.0;
        double sinSigma = 0.0;
        double sinLambda = 0.0;
        double cosLambda = 0.0;
        double cosSigma = 0.0;
        double sigma = 0.0;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if(sinSigma == 0)
                return 0;
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        }while (Math.abs(lambda-lambdaP) > (1e-12) && --iterLimit>0);
        if(iterLimit == 0) {
            return -1.0;
        }
        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double  A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double  B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double  deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double  s = b * A * (sigma - deltaSigma);
        double  fwdAz = Math.atan2(cosU2 * sinLambda, cosU1 * sinU2 - sinU1 * cosU2 * cosLambda);
        double  revAz = Math.atan2(cosU1 * sinLambda, -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda);
        return s;        
    }
}
