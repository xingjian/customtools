package com.promise.gistool.util;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.tongtu.nomap.core.transform.BeijingToGis84;
import com.tongtu.nomap.core.transform.Gis84ToCehui;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

/**  
 * 功能描述: GIS常用坐标转换
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月7日 上午10:34:31  
 */
public class GISCoordinateTransform {

    public static DecimalFormat df = new DecimalFormat( "0.0000000");
    
    /**
     * 投影坐标转换84坐标
     * @param x x坐标
     * @param y y坐标
     * @return
     */
    public static double[] From900913To84(double x,double y){
        double[] r = new double[2];
        double lon = (x / 20037508.34) * 180;
        double lat = (y / 20037508.34) * 180;
        lat = 180/Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);
        r[0] = lon;
        r[1] = lat; 
        return r;
    }
    /**
     * 84坐标转换投影坐标
     * @param x x坐标
     * @param y y坐标
     * @return
     */
    public static double[] From84To900913(double x,double y){
        double[] r = new double[2];
        double lon = x *20037508.34/180;
        double lat = Math.log(Math.tan((90+y)*Math.PI/360))/(Math.PI/180);
        lat = lat *20037508.34/180;
        r[0] = lon;
        r[1] = lat; 
        return r;
    }
    
    /**
     * 将wkt900913转换wkt84
     * @param wkt900913
     * @return
     */
    public static String From900913To84(String wkt900913){
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String wktCopy= wkt900913;
        Matcher matcher = pattern.matcher(wkt900913);
        while(matcher.find()){
            String temp = wkt900913.substring(matcher.start(),matcher.end());
            String[] xyArrTemp = temp.split(" ");
            double x_double = Double.parseDouble(xyArrTemp[0]);
            double y_double = Double.parseDouble(xyArrTemp[1]);
            double[] wgs84XYArr = From900913To84(x_double, y_double);
            wktCopy = wktCopy.replaceAll(temp, wgs84XYArr[0]+" "+wgs84XYArr[1]);
        }
        return wktCopy;
    }
    
    /**
     * 将wkt84转换wkt900913
     * @param wkt84
     * @return
     */
    public static String From84To900913(String wkt84){
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String wktCopy= wkt84;
        Matcher matcher = pattern.matcher(wkt84);
        while(matcher.find()){
            String temp = wkt84.substring(matcher.start(),matcher.end());
            String[] xyArrTemp = temp.split(" ");
            double x_double = Double.parseDouble(xyArrTemp[0]);
            double y_double = Double.parseDouble(xyArrTemp[1]);
            double[] wgs900913XYArr = From84To900913(x_double, y_double);
            wktCopy = wktCopy.replaceFirst(temp, df.format(wgs900913XYArr[0])+" "+df.format(wgs900913XYArr[1]));
        }
        return wktCopy;
    }
    
    
    /**
     * 几何84坐标转换google投影坐标
     * @param geo
     * @return
     */
    public static Geometry From84To900913(Geometry geo){
        String geoWkt = geo.toText();
        try {
            String strTemp = From84To900913(geoWkt);
            return GeoToolsGeometry.createGeometrtyByWKT(strTemp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 北京54坐标转换成84坐标
     * 目前只支持北京范围
     * @param wkt54
     * @return
     */
    public static String From54To84(String wkt54){
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String wktCopy= wkt54;
        Matcher matcher = pattern.matcher(wkt54);
        while(matcher.find()){
            String temp = wkt54.substring(matcher.start(),matcher.end());
            String[] xyArrTemp = temp.split(" ");
            double x_double = Double.parseDouble(xyArrTemp[0]);
            double y_double = Double.parseDouble(xyArrTemp[1]);
            double[] wgs84XYArr = BeijingToGis84.transSingle(x_double, y_double);
            wktCopy = wktCopy.replaceFirst(temp, wgs84XYArr[0]+" "+wgs84XYArr[1]);
        }
        return wktCopy;
    }
    
    /**
     * 84坐标转换成02坐标
     * 目前只支持北京范围
     * @param wkt84
     * @return
     */
    public static String From84To02(String wkt84){
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        String wktCopy= wkt84;
        Matcher matcher = pattern.matcher(wkt84);
        while(matcher.find()){
            String temp = wkt84.substring(matcher.start(),matcher.end());
            String[] xyArrTemp = temp.split(" ");
            double x_double = Double.parseDouble(xyArrTemp[0]);
            double y_double = Double.parseDouble(xyArrTemp[1]);
            double[] wgs84XYArr = Gis84ToCehui.transform(x_double, y_double);
            wktCopy = wktCopy.replaceFirst(temp, wgs84XYArr[0]+" "+wgs84XYArr[1]);
        }
        return wktCopy;
    }
    
    /**
     * transform beijing54 to 02 坐标是转换了，但.prj文件需要自己修改成02的
     * @param inputShapeFile
     * @param outputShapleFile
     * @param charSet
     * @return
     * @throws Exception
     */
    public static String From54To02(String inputShapeFile,String outputShapleFile,String charSet) throws Exception{
        //GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137,298.257223563]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]]
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        String result = "success";
        ShapefileDataStore inputSDS = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(inputShapeFile).toURI().toURL());
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures(inputShapeFile,charSet);
        Map<String, Serializable> params = new HashMap<String, Serializable>();  
        params.put( ShapefileDataStoreFactory.URLP.key,new File(outputShapleFile).toURI().toURL() );
        params.put( ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, (Serializable)Boolean.TRUE );
        ShapefileDataStore outSDS = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
        outSDS.createSchema(inputSDS.getSchema());  
        outSDS.setCharset(Charset.forName(charSet));
        //设置Writer  
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = outSDS.getFeatureWriter(outSDS.getTypeNames()[0], Transaction.AUTO_COMMIT);
        SimpleFeatureIterator sfi = sfc.features();
        while(sfi.hasNext()){
            SimpleFeature readFeature = sfi.next();
            Geometry gTemp = (Geometry)readFeature.getDefaultGeometry();
            String wkt = From54To84(gTemp.toText());
            wkt = From84To02(wkt);
            SimpleFeature writeFeature = writer.next();
            writeFeature.setAttributes(readFeature.getAttributes());
            writeFeature.setDefaultGeometry(GeoToolsGeometry.createGeometrtyByWKT(wkt));
        }
        writer.write();
        writer.close();
        inputSDS.dispose();
        outSDS.dispose();
        String outputShapleFileStr = outputShapleFile.substring(0,outputShapleFile.lastIndexOf(".shp"))+".prj";
        File f = new File(outputShapleFileStr);
        FileWriter fw =  new FileWriter(f);
        fw.write("GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.017453292519943295]]");
        fw.close();
        return result;
    }
    
    /**
     * transform beijing54 to 84 坐标
     * @param inputShapeFile
     * @param outputShapleFile
     * @param charSet
     * @return
     * @throws Exception
     */
    public static String From54To84(String inputShapeFile,String outputShapleFile,String charSet) throws Exception{
        //GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137,298.257223563]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]]
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        String result = "success";
        ShapefileDataStore inputSDS = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(inputShapeFile).toURI().toURL());
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures(inputShapeFile,charSet);
        Map<String, Serializable> params = new HashMap<String, Serializable>();  
        params.put( ShapefileDataStoreFactory.URLP.key,new File(outputShapleFile).toURI().toURL() );
        params.put( ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, (Serializable)Boolean.TRUE );
        ShapefileDataStore outSDS = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
        outSDS.createSchema(inputSDS.getSchema());  
        outSDS.setCharset(Charset.forName(charSet));
        //设置Writer  
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = outSDS.getFeatureWriter(outSDS.getTypeNames()[0], Transaction.AUTO_COMMIT);
        SimpleFeatureIterator sfi = sfc.features();
        while(sfi.hasNext()){
            SimpleFeature readFeature = sfi.next();
            Geometry gTemp = (Geometry)readFeature.getDefaultGeometry();
            String wkt = From54To84(gTemp.toText());
            SimpleFeature writeFeature = writer.next();
            writeFeature.setAttributes(readFeature.getAttributes());
            writeFeature.setDefaultGeometry(GeoToolsGeometry.createGeometrtyByWKT(wkt));
        }
        writer.write();
        writer.close();
        inputSDS.dispose();
        outSDS.dispose();
        String outputShapleFileStr = outputShapleFile.substring(0,outputShapleFile.lastIndexOf(".shp"))+".prj";
        File f = new File(outputShapleFileStr);
        FileWriter fw =  new FileWriter(f);
        fw.write("GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.017453292519943295]]");
        fw.close();
        return result;
    }
    
    /**
     * transform beijing84 to 02 
     * @param inputShapeFile
     * @param outputShapleFile
     * @param charSet
     * @return
     * @throws Exception
     */
    public static String From84To02(String inputShapeFile,String outputShapleFile,String charSet) throws Exception{
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        String result = "success";
        ShapefileDataStore inputSDS = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(inputShapeFile).toURI().toURL());
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures(inputShapeFile,charSet);
        Map<String, Serializable> params = new HashMap<String, Serializable>();  
        params.put( ShapefileDataStoreFactory.URLP.key,new File(outputShapleFile).toURI().toURL() );
        params.put( ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, (Serializable)Boolean.TRUE );
        ShapefileDataStore outSDS = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
        outSDS.createSchema(inputSDS.getSchema());  
        outSDS.setCharset(Charset.forName(charSet));
        //设置Writer  
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = outSDS.getFeatureWriter(outSDS.getTypeNames()[0], Transaction.AUTO_COMMIT);
        SimpleFeatureIterator sfi = sfc.features();
        while(sfi.hasNext()){
            SimpleFeature readFeature = sfi.next();
            Geometry gTemp = (Geometry)readFeature.getDefaultGeometry();
            String wkt = From84To02(gTemp.toText());
            SimpleFeature writeFeature = writer.next();
            writeFeature.setAttributes(readFeature.getAttributes());
            writeFeature.setDefaultGeometry(GeoToolsGeometry.createGeometrtyByWKT(wkt));
        }
        writer.write();
        writer.close();
        inputSDS.dispose();
        outSDS.dispose();
        return result;
    }
}
