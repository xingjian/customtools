package com.promise.gistool;

import java.math.BigDecimal;

import org.junit.Test;

import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoToolsGeometry;

/**  
 * 功能描述: 坐标转换测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月11日 上午11:52:57  
 */
public class GISCoordinateTransformTest {

    @Test
    public void testFrom84To900913(){
        double[] ret = GISCoordinateTransform.From84To900913(116.397066,39.9172);
        BigDecimal bd = new BigDecimal(ret[0]);
        double[] ret1 = GISCoordinateTransform.From84To900913(116.373583984375,39.9260503472222);
        double[] ret2 = GISCoordinateTransform.From84To900913(116.502667643229,39.8742363823785);
        double dis = GeoToolsGeometry.distanceGeo(GeoToolsGeometry.createPoint(ret1[0],ret1[1]), GeoToolsGeometry.createPoint(ret2[0],ret2[1]));
        System.out.println(dis);
    }
    @Test
    public void testFrom84To900913Second(){
        double[] ret = GISCoordinateTransform.From84To900913(101.778534,36.616347);
        BigDecimal bd = new BigDecimal(ret[0]);
        System.out.println(bd+","+ret[1]);
    }
    
    @Test
    public void testFrom54To02ShapeFile(){
        String inputShapeFile = "G:\\项目文档\\微波路段\\城市道路84+北京地方\\城市道路全属性_北京地方\\北京地方全属性\\Road_LN_03.shp";
        String outputShapleFile = "G:\\项目文档\\微波路段\\城市道路84+北京地方\\城市道路全属性_北京地方\\北京地方全属性\\Road_LN_04.shp";
        try {
           String result = GISCoordinateTransform.From84To02(inputShapeFile, outputShapleFile, "GBK");
           System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testFrom84To02ShapeFile(){
        String inputShapeFile = "G:\\项目文档\\节能减排\\gis\\data\\wgs1984\\traffic1.shp";
        String outputShapleFile = "G:\\项目文档\\节能减排\\gis\\data\\wgs1984\\traffic13_02.shp";
        try {
           String result = GISCoordinateTransform.From84To02(inputShapeFile, outputShapleFile, "GBK");
           System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testJAVA(){
        String inputShapeFile = "G:\\项目文档\\微波路段\\城市道路84+北京地方\\城市道路84-全属性\\道路\\道路全属性-按技术等级\\主干路_Project.shp";
        String outputShapleFile = "G:\\项目文档\\微波路段\\城市道路84+北京地方\\城市道路84-全属性\\道路\\道路全属性-按技术等级\\ZGL02.shp";
        String outputShapleFileStr = outputShapleFile.substring(0,outputShapleFile.lastIndexOf(".shp"))+".prj";
        System.out.println(outputShapleFileStr);
    }
    
}
