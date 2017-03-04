package com.promise.gistool.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.StringUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述: 矢量切片帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年2月17日 上午10:52:45  
 */
public class PBTileStacheUtil {

  //分辨率数组，与级别相对应，即一个级别对应一个分辨率，分辨率表示当前级别下单个像素代表的地理长度
    public static final double[][] ResMercator = {{156543.03392800014, 591657527.591555},
         {78271.516963999937, 295828763.79577702}, {39135.758482000092, 147914381.89788899},
         {19567.879240999919, 73957190.948944002}, {9783.9396204999593, 36978595.474472001},
         {4891.9698102499797, 18489297.737236001}, {2445.9849051249898, 9244648.8686180003},
         {1222.9924525624949, 4622324.4343090001}, {611.49622628138, 2311162.217155},
         {305.748113140558, 1155581.108577},{152.874056570411, 577790.554289},
         {76.4370282850732, 288895.277144}, {38.2185141425366, 144447.638572},
         {19.1092570712683, 72223.819286}, {9.55462853563415, 36111.909643},
         {4.7773142679493699, 18055.954822}, {2.3886571339746849, 9027.9774109999998},
         {1.1943285668550503, 4513.9887049999998}, {0.59716428355981721, 2256.994353},
         {0.29858214164761665, 1128.4971760000001}};
    
    public static final double[] BoundMecrator = {-20037508.3427892, -20037508.3427892, 20037508.3427892, 20037508.3427892};
    public static int ImageWidth = 256;
    public static int ImageHeight = 256;
    
    /**
     * 读取切片配置文件
     * 格式：
     * {
     *    "name": "pbtilestache-beijing",
     *    "path": "d://pbtilestache-beijing",
     *    "description": "demo",
     *    "shapefile": "E://tmp//gjzd.shp",
     *    "xmin":135:,
     *    "ymin":135:,
     *    "xmax":135:,
     *    "ymax":135:,
     *    "zoom":11 12 13
     *  }
     * @param filePath 文件路径
     * @return json对象
     */
    public static JSONObject GetConfigJSONFile(String filePath){
        return PBFileUtil.ReadJSONFile(filePath);
    }
    
    
    
    /**
     * 生成geojson格式数据，目前支持84坐标,3857(900913)
     * @param shapeFilePath 
     * 如果范围为null,默认读取shape的bound
     * @param minx 
     * @param miny
     * @param maxx
     * @param maxy
     * @param level
     * @param exportPath 文件格式为//z//y//x
     * @param filter 哪些zyx的不用去请求,格式z-y-x
     * @param srid shape文件坐标信息，默认EPSG:4326
     * @param debug true输出过程文件
     * @return
     */
    public static String GenVectorTileFileByShape(String shapeFilePath,double minx,double miny,double maxx,double maxy,int zLevel,String exportPath,String shapeFileCharset,Map<String,String> filter,String srid,boolean debug){
        String result = "success";
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures(shapeFilePath, shapeFileCharset);
        SimpleFeatureIterator sfi = sfc.features();
        List<SimpleFeature> sfList = new ArrayList<SimpleFeature>();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(sfc.getSchema()); 
        while(sfi.hasNext()){
            sfList.add(sfi.next());
        }
        ReferencedEnvelope bounds = sfc.getBounds();
        if(minx==-1||miny==-1||maxx==-1||maxy==-1){
            minx = bounds.getMinX();
            miny = bounds.getMinY();
            maxx = bounds.getMaxX();
            maxy = bounds.getMaxY();
        }
        double minX = minx; 
        double minY = miny;
        double maxX = maxx; 
        double maxY = maxy;
        boolean is4326 = true;
        if(null==srid||srid.trim().toUpperCase().equals("EPSG:4326")){
            double[] dArr1 = GISCoordinateTransform.From84To900913(minx,miny);
            double[] dArr2 = GISCoordinateTransform.From84To900913(maxx,maxy);
            minX = dArr1[0]; 
            minY = dArr1[1];
            maxX = dArr2[0]; 
            maxY = dArr2[1];
        }else if(srid.trim().toUpperCase().equals("EPSG:3857")){
            is4326 = false;
        }
        int[] startIndexArr = MapServerUtil.GetMercatorImageXYBy3857(minX,maxY,zLevel);
        int[] endIndexArr = MapServerUtil.GetMercatorImageXYBy3857(maxX,minY,zLevel);
        int xStart = startIndexArr[0];
        int yStart = startIndexArr[1];
        int xEnd = endIndexArr[0];
        int yEnd = endIndexArr[1];
        List<String> listDebug = new ArrayList<String>();
        
        //从左上角(x最小 y最大)循环变量到右下角(x最大 y最小) 
        for(int y=yStart;y<=yEnd;y++){
            for(int x=xStart;x<=xEnd;x++){
                double minxDouble = BoundMecrator[0]+(x*ImageWidth*ResMercator[zLevel][0]);
                double minyDouble = BoundMecrator[3]-((y+1)*ImageWidth*ResMercator[zLevel][0]);
                double maxxDouble = BoundMecrator[0]+((x+1)*ImageWidth*ResMercator[zLevel][0]);
                double maxyDouble = BoundMecrator[3]-y*ImageWidth*ResMercator[zLevel][0];
                
                String bboxStr = StringUtil.FormatDoubleStr("#.00000000", minxDouble)+","+StringUtil.FormatDoubleStr("#.00000000", minyDouble)+
                        ","+StringUtil.FormatDoubleStr("#.00000000", maxxDouble)+","+StringUtil.FormatDoubleStr("#.00000000", maxyDouble);
                if(debug){
                    listDebug.add(bboxStr);
                }
                try {
                    boolean flag = true;
                    if(null!=filter){
                        if(null!=filter.get(zLevel+"-"+y+"-"+x)){
                            flag = false;
                        }
                    }
                    if(flag){
                        //如果shpae坐标为4326
                        double[] dArr3 = null;
                        double[] dArr4 = null;
                       if(is4326){
                           dArr3 = GISCoordinateTransform.From900913To84(minxDouble,minyDouble);
                           dArr4 = GISCoordinateTransform.From900913To84(maxxDouble,maxyDouble);
                       }else{
                           dArr3 = new double[]{minxDouble,minyDouble};
                           dArr4 = new double[]{maxxDouble,maxyDouble};
                       }
                       Polygon clipPolyson = GeoToolsGeometry.createSquare(dArr3[0],dArr3[1],dArr4[0],dArr4[1]);
                       List<SimpleFeature> sfResultList = new ArrayList<SimpleFeature>();
                       for(int i=0;i<sfList.size();i++){
                           SimpleFeature sfTemp = sfList.get(i);
                           boolean isIntersect = GeoToolsGeometry.isIntersects(clipPolyson, (Geometry)sfTemp.getDefaultGeometry());
                           if(isIntersect){
                               Geometry geoTemp = GeoToolsGeometry.intersection(clipPolyson, (Geometry)sfTemp.getDefaultGeometry());
                               if(!geoTemp.isEmpty()&&geoTemp.isValid()){
                                   SimpleFeature new_sf = featureBuilder.buildFeature(sfTemp.getID(), sfTemp.getAttributes().toArray());
                                   new_sf.setDefaultGeometry(geoTemp);
                                   sfResultList.add(new_sf);
                               }
                           }
                       }
                       if(sfResultList.size()>0){
                           ListFeatureCollection lfc = new ListFeatureCollection(sfc.getSchema(),sfResultList);
                           String jsonStr = GeoToolsGeometry.FeatureToJSON(lfc);
                           PBFileUtil.CreateDir(exportPath+File.separator+zLevel+File.separator+y+File.separator);
                           PBFileUtil.WriteStringToTxt(jsonStr, exportPath+File.separator+zLevel+File.separator+y+File.separator+x+".geojson");
                       }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        if(debug){
            PBFileUtil.CreateDir(exportPath+File.separator+"logs"+File.separator);
            PBFileUtil.WriteListToTxt(listDebug, exportPath+File.separator+"logs"+File.separator+zLevel+".txt", true);
        }
        return result;
    }
    
}
