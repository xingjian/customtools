package com.promise.gistool.util;


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
    
    /**
     * 生成缓存图片 基于google命名规则的
     * @param wmsurl wms 访问路径 并且wkid 3857
     * @param threadNum 线程数目
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     * @param zLevel 0 - 19
     * @param format 图片格式默认png
     * @param oldWkid 默认为4326 或者传入3857
     * @return
     */
    public static int CreateMercator3857Cache(String wmsurl,int threadNum,double minX,double minY,double maxX,double maxY,String filePath,int zLevel,String format,String oldWkid){
        if(null==oldWkid||oldWkid.equals("")){
            oldWkid = "4326";
        }
        int[] startIndex = null;
        int[] endIndex = null;
        if(oldWkid.trim().equals("4326")){
            startIndex = GetMercatorImageXYByWGS1984(minX,maxY,zLevel);
            endIndex = GetMercatorImageXYByWGS1984(maxX,minY,zLevel);
        }else if(oldWkid.trim().equals("3857")){
            startIndex = GetMercatorImageXYBy3857(minX,maxY,zLevel);
            endIndex = GetMercatorImageXYBy3857(maxX,minY,zLevel);
        }
        return 1;
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
