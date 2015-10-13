package com.promise.gistool.util;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**  
 * 功能描述: GeoTools shape工具类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月3日 下午9:08:23  
 */
public class GeoShapeUtil {

    /**
     * 获取shape文件feature集合
     * @param shapePath shape文件路径
     * @param charSet 读取shape文件编码
     * @return SimpleFeatureCollection
     */
    public static SimpleFeatureCollection ReadShapeFileFeatures(String shapePath,String charSet){
        SimpleFeatureCollection sfc = null;
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore sds = null;
        try {
            sds = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(shapePath).toURI().toURL());
            sds.setCharset(Charset.forName(charSet));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            sfc = featureSource.getFeatures();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            sds.dispose();
        }
        return sfc;
    }
    
    /**
     * 注意首先要备份原始的shape文件，并坐标要求相同
     * 将指定的simplefeature集合追加到指定的shapefile
     * @param list simplefeature集合
     * @param shapePath 合并的shape文件
     * @param fileds 要把feature里面的哪些字段合并到指定的shapefile中 
     * fields 字符串格式 columnname1:columnname2
     * @param charSet读取shapefile编码格式
     * @return 结果状态
     */
    public static boolean AppendFeatureToShapeFile(List<SimpleFeature> list, String shapePath,List<String> fileds,String charSet){
        boolean retBoo = false;
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore sds = null;
        try {
            sds = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(shapePath).toURI().toURL());
            sds.setCharset(Charset.forName(charSet));
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = sds.getFeatureWriter(sds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            for(SimpleFeature sf : list){
                SimpleFeature feature = writer.next();
                for(String filedStr : fileds){
                    String[] strArr = filedStr.split(":");
                    feature.setAttribute(strArr[1],sf.getAttribute(strArr[0]));
                }
            }
            writer.write();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            sds.dispose();
        }
        return true;
    }
    
}
