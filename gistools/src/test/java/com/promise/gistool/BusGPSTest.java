package com.promise.gistool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.PBMathUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年4月23日 上午10:30:36  
 */
public class BusGPSTest {

    @Test
    public void createGMLXLBus() throws Exception{
        String sql = "select * from gj_car_bh where xlmc in('102','106','110')";
        Connection connectOracle = DBConnection.GetOracleConnection("jdbc:oracle:thin:@10.212.140.211:1521:buscity", "buscitynew", "admin123ttyj7890uiop");
        ResultSet rs = connectOracle.createStatement().executeQuery(sql);
        List<XLBusGPS> list = new ArrayList<XLBusGPS>();
        while(rs.next()){
            String id = rs.getString(4);
            XLBusGPS obj = new XLBusGPS();
            obj.setMzl(PBMathUtil.GetRandomDouble(0.0, 1.5, 1));
            obj.setId(id);
            list.add(obj);
        }
        rs.close();
        String headerFile = "G:\\项目文档\\TTGIS\\模版\\xlbusheader.xml";
        String envelopeFile = "G:\\项目文档\\TTGIS\\模版\\xlbusenvelope.xml";
        String contentFile = "G:\\项目文档\\TTGIS\\模版\\xlbuscontent.xml";
        String outputFile = "G:\\项目文档\\TTGIS\\模版\\xlbus.gml";
        XLBusGML xlBusGML = new XLBusGML();
        xlBusGML.setContentFile(contentFile);
        xlBusGML.setEnvelopeFile(envelopeFile);
        xlBusGML.setHeaderFile(headerFile);
        xlBusGML.setOutputFile(outputFile);
        String result = xlBusGML.createDataFile(list);
        System.out.println(result);
    }
    
    
    @Test
    public void createGMLTaxi() throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<TaxiGPS> list = new ArrayList<TaxiGPS>();
        List<String> listStr = PBFileUtil.ReadFileByLine("d:\\taxi_ids.txt");
        for(String str:listStr){
            String[] arr = str.split(",");
            TaxiGPS tt = new TaxiGPS();
            tt.setId(arr[4]);
            tt.setCompany(arr[3]);
            Date currentTime = new Date(Long.parseLong(arr[0])*1000);
            tt.setPositionti(formatter.format(currentTime));
            tt.setX(arr[21]);
            tt.setY(arr[22]);
            tt.setSpeed(arr[10]);
            list.add(tt);
        }
        String headerFile = "G:\\项目文档\\TTGIS\\模版\\xlbusheader.xml";
        String envelopeFile = "G:\\项目文档\\TTGIS\\模版\\xlbusenvelope.xml";
        String contentFile = "G:\\项目文档\\TTGIS\\模版\\xlbuscontent_hubei.xml";
        String outputFile = "G:\\项目文档\\TTGIS\\模版\\xlbus_hubei.gml";
        XLBusGML xlBusGML = new XLBusGML();
        xlBusGML.setContentFile(contentFile);
        xlBusGML.setEnvelopeFile(envelopeFile);
        xlBusGML.setHeaderFile(headerFile);
        xlBusGML.setOutputFile(outputFile);
        String result = xlBusGML.createDataFile(list);
        System.out.println(result);
    }
}
