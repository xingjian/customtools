package com.promise.gistool.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileHeader;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.promise.cn.util.StringUtil;
import com.vividsolutions.jts.geom.GeometryFactory;

/**  
 * 功能描述:转换类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月1日 下午3:15:58  
 */
public class ConversionUtil {

    /**
     * excel格式转换到shp
     * @param excelFilePath excel路径
     * @param shapeFilePath shp路径
     * @return 转换结果
     */
    public static String ExcelToShape(String excelFilePath,String shapeFilePath){
        String result = "fail";
        return result;
    }
    /**
     * Excel转换成PostGIS表中
     * @param excelFilePath excel路径
     * @param connection PostGIS数据库连接对象
     * @return 转换结果
     */
    public static String ExcelToPostGIS(String excelFilePath,Connection connection){
        String result = "fail";
        return result;
    }
    /**
     * shape数据导入PostGIS SQL语句方式
     * @param shapePath shape文件路径
     * @param connection PostGIS数据库连接对象,必须安装PostGis
     * @param charSet 读取shape编码设置
     * @param tableName 表名
     * @return 转换结果
     */
    public static String ShapeToPostGIS(String shapePath,Connection connection,String charSet,String tableName,Map<String,String> mapping){
        String result = "success";
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(shapePath).toURI().toURL());
            sds.setCharset(Charset.forName(charSet));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = featureSource.getFeatures().features();
            //生成insert语句和对应关系
            String insertSQL = "insert into "+tableName+" (";
            int columnCount = 0;
            int theGeomIndex=0;
            boolean findGeom = true;
            //shapeColumnName和insert问号索引
            Map<String,Integer> map1 = new HashMap<String, Integer>();
            //table列名和类型对应关系
            Map<Integer,String> map2 = new HashMap<Integer, String>();
            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                String key = entry.getKey().toLowerCase();
                String[] array = entry.getValue().split(":");
                insertSQL = insertSQL+" "+array[0]+",";
                if(findGeom&&(key.equals("the_geom")||key.equals("geometry"))){
                    theGeomIndex = columnCount;
                    findGeom = false;
                }
                columnCount++;
                map1.put(key, columnCount);
                map2.put(columnCount, array[1].trim());
            }
            insertSQL = insertSQL.substring(0, insertSQL.length()-1)+" ) values(";
            for(int i=0;i<columnCount;i++){
                if(i!=theGeomIndex){
                    insertSQL+="?,";
                }else{
                    insertSQL+="ST_GeomFromText(?,4326),";
                }
            }
            insertSQL = insertSQL.substring(0, insertSQL.length()-1)+")";
            PreparedStatement ps = connection.prepareStatement(insertSQL);
            int rowIndex = 0;
            while(itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                Iterator<Property> it = feature.getProperties().iterator();
                while(it.hasNext()) {
                    Property pro = it.next();
                    String columnNameShape = pro.getName().toString();
                    Integer index = map1.get(columnNameShape.toLowerCase());
                    String columType = map2.get(index);
                    if(columType.trim().toLowerCase().equals("string")){
                        ps.setString(index, pro.getValue().toString());
                    }else if(columType.trim().toLowerCase().equals("int")){
                        ps.setInt(index, Integer.parseInt(pro.getValue().toString()));
                    }else if(columType.trim().toLowerCase().equals("double")){
                        ps.setDouble(index, Double.parseDouble(pro.getValue().toString()));
                    }else if(columType.trim().toLowerCase().equals("float")){
                        ps.setFloat(index, Float.parseFloat(pro.getValue().toString()));
                    }else if(columType.trim().toLowerCase().equals("geometry")){
                        ps.setString(index, pro.getValue().toString());
                    }else if(columType.trim().toLowerCase().equals("uuid")){
                        ps.setString(index,StringUtil.GetUUIDString());
                    }else{
                        ps.setString(index,columType.trim());
                    }
                }
                ps.addBatch();
                rowIndex++;
                if(rowIndex%5000==0){
                    ps.executeBatch();
                }
           } 
           ps.executeBatch();
           ps.close();
           connection.close();
           itertor.close(); 
           sds.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 通过dataStore导入shape文件
     * 目前不能使用,因为获取的几何对象类型有出入,比如line变成多线，面变成多面
     * @param shapePath shape路径
     * @param dataStore dataStore对象 请参考ConnPostGis方法获取
     * @param charSet 读取shape文件编码设置
     * @param tableName 表名
     * @return 运行结果状态
     */
    public static String ShapeToPostGIS(String shapePath,DataStore dataStore,String charSet,String tableName){
        String result = "success";
        String createTableResult = GISDBUtil.CreateTableSchema(tableName,shapePath,dataStore,"GBK");
        if("success".equals(createTableResult)){
            Transaction tran = new DefaultTransaction("add");
            try {
                SimpleFeatureSource sfs = dataStore.getFeatureSource(tableName);
                SimpleFeatureType sft = dataStore.getSchema(tableName);
                FeatureStore featureStore = (FeatureStore) sfs;
                SimpleFeatureCollection features = GeoShapeUtil.ReadShapeFileFeatures(shapePath, charSet);
                featureStore.setTransaction(tran);
                featureStore.addFeatures(features);
                tran.commit();
                dataStore.dispose();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    tran.rollback();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return result;
    } 
    
    /**
     * 读取shape属性信息
     * C (Character) -> String
     * N (Numeric)   -> Integer or Long or Double (depends on field's decimal count and fieldLength)
     * F (Floating)  -> Double
     * L (Logical)   -> Boolean
     * D (Date)      -> java.util.Date (Without time)
     * @param shapePath shape路径
     * @param charSet 编码设置
     * @return 属性信息 格式 列名,类型,长度
     */
    public static List<String> GetShapeAttributes(String shapePath,String charSet){
        List<String> attributes = new ArrayList<String>();
        DbaseFileReader reader = null;
        try {
            reader = new DbaseFileReader(new ShpFiles(shapePath),false,Charset.forName(charSet));
            DbaseFileHeader header = reader.getHeader();
            int numFields = header.getNumFields();
            for (int i=0; i<numFields; i++) {
                String title = header.getFieldName(i);
                char fieldType = header.getFieldType(i);
                int fieldLength = header.getFieldLength(i);
                attributes.add(title+","+fieldType+","+fieldLength);
            }
            ShpFiles sf = new ShpFiles(shapePath);    
            ShapefileReader sfr = new ShapefileReader(sf,false, false, new GeometryFactory());
            ShapefileHeader sfh = sfr.getHeader();
            attributes.add("the_geom"+","+sfh.getShapeType().name);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {reader.close();} catch (Exception e) {}
            }
        }
        return attributes;
    }
    
    /**
     * 将指定路径shape 导入到 指定的dataStore
     * @param shapePath shape文件路径
     * @param dataStore shape存储目标
     * @param charSet 读取shape编码设置
     * @return 返回运行结果状态
     */
    public static String ShapeToPostGIS(String shapePath,DataStore dataStore,String charSet){
        String result = "success";
        ShapefileDataStore shpDataStore = null;
        Transaction tran = new DefaultTransaction("add");
        try {
            shpDataStore = new ShapefileDataStore(new File(shapePath).toURI().toURL());
            shpDataStore.setCharset(Charset.forName(charSet));
            String typeName = shpDataStore.getTypeNames()[0];
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;  
            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>) shpDataStore.getFeatureSource(typeName);  
            FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures();  
            SimpleFeatureType schema = features.getSchema();
            dataStore.createSchema(schema);
            SimpleFeatureSource sfs = dataStore.getFeatureSource(typeName);
            FeatureStore featureStore = (FeatureStore) sfs;
            featureStore.setTransaction(tran);
            featureStore.addFeatures(features);
            tran.commit();
            shpDataStore.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            result = "fail";
        }  
       
        return result;
    }
}
