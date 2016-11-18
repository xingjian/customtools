package com.promise.gistool.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.wfs.v1_1_0.GeoServerStrategy;

import com.promise.cn.util.PBCrawlerUtil;
import com.promise.cn.util.StringUtil;


/**  
 * 功能描述: 地图服务的帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月27日 上午9:38:10  
 */
public class MapServerUtil {
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
     * 生成缓存图片 基于google命名规则的 支持第四象限的坐标范围
     * @param wmsurl wms 访问路径 并且wkid 3857
     * arcgis http://10.212.137.129:8399/arcgis/services/jtwtraffic/gd_gs/MapServer/WMSServer?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&
     * STYLES=default,default,default,default,default,default,default&TRANSPARENT=true&LAYERS=0,1,2,3,4,5,6&CRS=EPSG:3857&FORMAT=image/png
     * &WIDTH=256&HEIGHT=256
     * geoserver http://192.168.1.105:8888/geoserver/buscity/wms?service=WMS&version=1.1.0&request=GetMap&layers=buscity:navigationline&styles=
     * &width=256&height=256&srs=EPSG:900913&TRANSPARENT=true&FORMAT=image/png
     * @param threadNum 线程数目 目前还没有实现
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     * @parem filePath d://mapservercache
     * @param zLevel 0 - 19
     * @param format 图片格式默认png
     * @return 生成url
     */
    public static List<String> CreateMercator3857Cache(String wmsurl,int threadNum,double minX,double minY,double maxX,double maxY,String filePath,int zLevel,String format){
        List<String> mapURLList = new ArrayList<String>();
        int[] startIndexArr = GetMercatorImageXYBy3857(minX,maxY,zLevel);
        int[] endIndexArr = GetMercatorImageXYBy3857(maxX,minY,zLevel);
        int xStart = startIndexArr[0];
        int yStart = startIndexArr[1];
        int xEnd = endIndexArr[0];
        int yEnd = endIndexArr[1];
        //从左上角(x最小 y最大)循环变量到右下角(x最大 y最小) 
        for(int y=yStart;y<=yEnd;y++){
            for(int x=xStart;x<=xEnd;x++){
                double minxDouble = BoundMecrator[0]+(x*ImageWidth*ResMercator[zLevel][0]);
                double minyDouble = BoundMecrator[3]-((y+1)*ImageWidth*ResMercator[zLevel][0]);
                double maxxDouble = BoundMecrator[0]+((x+1)*ImageWidth*ResMercator[zLevel][0]);
                double maxyDouble = BoundMecrator[3]-y*ImageWidth*ResMercator[zLevel][0];
                String bboxStr = StringUtil.FormatDoubleStr("#.00000000", minxDouble)+","+StringUtil.FormatDoubleStr("#.00000000", minyDouble)+
                        ","+StringUtil.FormatDoubleStr("#.00000000", maxxDouble)+","+StringUtil.FormatDoubleStr("#.00000000", maxyDouble);
                try {
                    PBCrawlerUtil.GetImageByURI(filePath+File.separator+zLevel+File.separator+y, x+"."+format, wmsurl+"&BBOX="+bboxStr);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    mapURLList.add(zLevel+"-"+y+"-"+x+":"+wmsurl+"&BBOX="+bboxStr);
                }
            }
        }
        return mapURLList;
    }
    
    /**
     * 根据指定的范围下载高德地图瓦片 基础底图 影像地图 poi地图 maptype vector 基础地图   image 影像图  poi poi地图(暂未实现)
     * @param gdURL 默认http://webrd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7
     * 影像地图 http://webst01.is.autonavi.com/appmaptile?style=6
     * @param threadNum
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     * @param filePath
     * @param zLevel
     * @param format png
     * @return map集合,下载成功和失败  key success  key error
     */
    public static Map<String,List<String>> GetGDTileByExtent(String gdURL,int threadNum,double minX,double minY,double maxX,double maxY,String filePath,int zLevel,String format,String maptype){
        Map<String,List<String>> result = new HashMap<String,List<String>>();
        List<String> successList = new ArrayList<String>();
        List<String> errorList = new ArrayList<String>();
        if(maptype.trim().equals("vector")&&null==gdURL){
            gdURL = "http://webrd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7";
        }else if(maptype.trim().equals("image")&&null==gdURL){
            gdURL = "http://webst01.is.autonavi.com/appmaptile?style=6";
        }else if(maptype.trim().equals("poi")&&null==gdURL){
            
        }
        int[] startIndexArr = GetMercatorImageXYBy3857(minX,maxY,zLevel);
        int[] endIndexArr = GetMercatorImageXYBy3857(maxX,minY,zLevel);
        int xStart = startIndexArr[0];
        int yStart = startIndexArr[1];
        int xEnd = endIndexArr[0];
        int yEnd = endIndexArr[1];
        //从左上角(x最小 y最大)循环变量到右下角(x最大 y最小) 
        for(int y=yStart;y<=yEnd;y++){
            for(int x=xStart;x<=xEnd;x++){
                try {
                    PBCrawlerUtil.GetImageByURI(filePath+File.separator+zLevel+File.separator+y, x+"."+format, gdURL+"&x="+x+"&y="+y+"&z="+zLevel);
                    successList.add(zLevel+"-"+y+"-"+x+":"+gdURL+"&x="+x+"&y="+y+"&z="+zLevel);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorList.add(zLevel+"-"+y+"-"+x+":"+gdURL+"&x="+x+"&y="+y+"&z="+zLevel);
                } 
            }
        }
        result.put("success", successList);
        result.put("error", errorList);
        return result;
    }
    
    /**
     * 根据指定的范围下载高德地图瓦片 基础底图 影像地图 poi地图 maptype vector 基础地图   image 影像图  poi poi地图(暂未实现)
     * @param gdURL 默认http://webrd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7
     * 影像地图 http://webst01.is.autonavi.com/appmaptile?style=6
     * @param threadNum
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     * @param zLevel
     * @param format
     * @return
     */
    public static List<String> GetGDTileURLListByExtent(String gdURL,int threadNum,double minX,double minY,double maxX,double maxY,int zLevel,String format,String maptype){
        List<String> result = new ArrayList<String>();
        if(maptype.trim().equals("vector")&&null==gdURL){
            gdURL = "http://webrd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7";
        }else if(maptype.trim().equals("image")&&null==gdURL){
            gdURL = "http://webst01.is.autonavi.com/appmaptile?style=6";
        }else if(maptype.trim().equals("poi")&&null==gdURL){
            
        }
        int[] startIndexArr = GetMercatorImageXYBy3857(minX,maxY,zLevel);
        int[] endIndexArr = GetMercatorImageXYBy3857(maxX,minY,zLevel);
        int xStart = startIndexArr[0];
        int yStart = startIndexArr[1];
        int xEnd = endIndexArr[0];
        int yEnd = endIndexArr[1];
        //从左上角(x最小 y最大)循环变量到右下角(x最大 y最小) 
        for(int y=yStart;y<=yEnd;y++){
            for(int x=xStart;x<=xEnd;x++){
                result.add(zLevel+"-"+y+"-"+x+":"+gdURL+"&x="+x+"&y="+y+"&z="+zLevel);
            }
        }
        return result;
    }
    
    /**
     * 根据wmsurl和范围 层级生成url集合
     * 支持第四象限的坐标范围
     * 目前只支持投影坐标 900913  3857
     * @param resolution 分辨率
     * @param level 层级
     * @return
     */
    public static List<String> GetWMSURLSByExtent(String wmsurl,int zLevel,double minX,double minY,double maxX,double maxY,int imageSize){
        List<String> result = new ArrayList<String>();
        int[] startIndexArr = GetMercatorImageXYBy3857(minX,maxY,zLevel);
        int[] endIndexArr = GetMercatorImageXYBy3857(maxX,minY,zLevel);
        int xStart = startIndexArr[0];
        int yStart = startIndexArr[1];
        int xEnd = endIndexArr[0];
        int yEnd = endIndexArr[1];
        //从左上角(x最小 y最大)循环变量到右下角(x最大 y最小) 
        for(int y=yStart;y<=yEnd;y++){
            for(int x=xStart;x<=xEnd;x++){
                double minxDouble = BoundMecrator[0]+(x*imageSize*ResMercator[zLevel][0]);
                double minyDouble = BoundMecrator[3]-((y+1)*imageSize*ResMercator[zLevel][0]);
                double maxxDouble = BoundMecrator[0]+((x+1)*imageSize*ResMercator[zLevel][0]);
                double maxyDouble = BoundMecrator[3]-y*imageSize*ResMercator[zLevel][0];
                String bboxStr = StringUtil.FormatDoubleStr("#.00000000", minxDouble)+","+StringUtil.FormatDoubleStr("#.00000000", minyDouble)+
                        ","+StringUtil.FormatDoubleStr("#.00000000", maxxDouble)+","+StringUtil.FormatDoubleStr("#.00000000", maxyDouble);
                result.add(zLevel+"-"+y+"-"+x+":"+wmsurl+"&BBOX="+bboxStr);
            }
        }
        return result;
    }
    
    
    
    /**
     * 根据经纬度坐标信息获取Mercator坐标的图片的索引号
     * @param x 经度
     * @param y 纬度
     * @param z 级别
     * @return 返回索引号的集合
     */
    public static int[] GetMercatorImageXYByWGS1984(double x,double y,int z){
        double[] googleXYDouble = GISCoordinateTransform.From84To900913(x, y);
        return GetMercatorImageXYBy3857(googleXYDouble[0],googleXYDouble[1],z);
    }
    
    /**
     * 根据投影坐标信息获取Mercator坐标的图片的索引号
     * @param x 坐标x
     * @param y 坐标y
     * @param z 级别
     * @return 返回索引号的集合
     */
    public static int[] GetMercatorImageXYBy3857(double x,double y,int z){
        int xtile = (int)Math.floor((x - BoundMecrator[0])/ResMercator[z][0]/256);
        int ytile = (int)Math.floor((BoundMecrator[3]-y)/ResMercator[z][0]/256);
        return new int[]{(int)xtile,(int)ytile};
    }
    
}
