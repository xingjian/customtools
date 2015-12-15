package com.promise.gistool.util;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.PBMathUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述: 自定义GeoTools工具类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月2日 下午12:26:57  
 */
public class GeoToolsUtil {

    /**
     * 根据矩形绘制六边形，获取各个六边形的中点坐标
     * @param xmin
     * @param ymin
     * @param xmax
     * @param ymax
     * @return
     */
    public static List<Polygon> CreateHexagonsByExtents(double xmin,double ymin,double xmax,double ymax,double radius){
        int index = 0;
        List<Polygon> retList = new ArrayList<Polygon>();
        double xTemp = 0;
        double yTemp = 0;
        int yInt = (int)((ymax-ymin)/(Math.sqrt(3)*radius))+1;
        int xInt = (int)((xmax-xmin)/(1.5*radius))+1;
        for(int i=0;i<xInt;i++){
            xTemp = xmin + i*1.5*radius;
            for(int j=0;j<yInt;j++){
                if(i%2==0){
                    yTemp = ymax - j*(Math.sqrt(3)*radius);
                }else{
                    yTemp = ymax - j*(Math.sqrt(3)*radius) - ((Math.sqrt(3))/2)*radius;
                }
                Polygon polygon = GeoToolsGeometry.createCircle(xTemp, yTemp, radius, 6);
                retList.add(index, polygon);
                index++;
            }
        }
        return retList;
    }
    
    /**
     * 根据中心点绘制正方形
     * @param x
     * @param y
     * @param length 边长
     * @return
     */
    public static Polygon CreateSquareByLength(double x,double y,double length){
        Polygon polygon = GeoToolsGeometry.createCircle(x, y, length/Math.sqrt(2), 4);
        return polygon;
    }
    
    /**
     * 根据中心点绘制正方形
     * @param x
     * @param y
     * @param length 边长
     * @return
     */
    public static Polygon CreateSquareByLength(double x,double y,double length,double rotate){
        Polygon polygon = GeoToolsGeometry.createCircle(x, y, length/Math.sqrt(2), 4,rotate);
        return polygon;
    }
    
    /**
     * 要素的切割
     * @param listSF listSF被切割对象
     * @param listG 切割的轮廓 一般是面要素
     * @return
     */
    public static List<SimpleFeature> ClipGeometry(List<SimpleFeature> listSF,List<SimpleFeature> listG ) throws Exception{
        List<SimpleFeature> retList = new ArrayList<SimpleFeature>();
        //将几何对象合并形成轮廓对象
        Geometry g1 = null;
        for(SimpleFeature sf:listG){
            if(null==g1){
                g1=(Geometry)sf.getDefaultGeometry();
            }else{
                g1 = GeoToolsGeometry.union(g1, (Geometry)sf.getDefaultGeometry());
            }
        }
        if(null!=g1){
           for(SimpleFeature sf : listSF){
               Geometry g = g1.intersection((Geometry)sf.getDefaultGeometry());
               if(!g.isEmpty()){
                   retList.add(sf);
               }
           }
        }
        return retList;
    }
    
    /**
     * 延长线坐标
     * 点离right近
     * @param left
     * @param right
     * @param dis
     * @return
     */
    public static Coordinate VectorExtendLine(Coordinate left,Coordinate right,double dis){
        double left_right_dis = PBMathUtil.Distance(left.x, left.y, right.x, right.y);
        double x = right.x + (right.x-left.x)*dis/left_right_dis;
        double y = right.y + (right.y-left.y)*dis/left_right_dis;
        return new Coordinate(x,y);
    }
    
    /**
     * 经纬度距离求算
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double GetDistanceWGS84(double x1,double y1,double x2,double y2){
        double radx1 = Math.toRadians(x1);
        double radx2 = Math.toRadians(x2);
        double a = radx1 - radx2;
        double b = Math.toRadians(y1)-Math.toRadians(y2);
        double s = 2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radx1)*Math.cos(radx2)*Math.pow(Math.sin(b/2),2)));
        s = s * 6378137.0;
        return s;
    }
    
    /**
     * 向量加法
     * @param left
     * @param right
     * @return
     */
    public static Coordinate VectorAdd(Coordinate left,Coordinate right){
        return new Coordinate(left.x+right.x, left.y+right.y);
    }
    
    /**
     * 向量减法
     * @param left
     * @param right
     * @return
     */
    public static Coordinate VectorSubtraction(Coordinate left,Coordinate right){
        return new Coordinate(left.x-right.x, left.y-right.y);
    }
    
    public static double VectorMultiplication(Coordinate left,Coordinate right){
        return left.x*right.x + left.y*right.y;
    }
    
    public static Coordinate VectorMultiplication(Coordinate left,double value){
        return new Coordinate(left.x*value, left.y*value);
    }
    
    public static double VectorMOperator(Coordinate left,Coordinate right){
        return left.x*right.y - left.y*right.x;
    }
    
    /**
     * 做平行线
     * @param distance
     * @param coords
     * @return
     */
    public static List<Coordinate> ParallelLine(double dist,Coordinate[] pList){
        //单位化两顶点向量差
        List<Coordinate> dpList  = new ArrayList<Coordinate>(); //边向量dpList［i＋1］－ dpLIst［i］ 在 initDPList函数当中计算后赋值
        List<Coordinate> ndpList = new ArrayList<Coordinate>(); //单位化的边向量， 在initNDPList函数当中计算后肤质，实际使用的时候，完全可以用dpList来保存他们
        List<Coordinate> newList = new ArrayList<Coordinate>(); //新的折线顶点，在compute函数当中，赋值
        for(int i=0;i<pList.length;++i){
            dpList.add(i, VectorSubtraction(pList[i==pList.length-1 ? 0: i+1],pList[i]));
        }
        for(int j=0;j<dpList.size();j++){
            ndpList.add(j,VectorMultiplication(dpList.get(j) , ( 1.0 /Math.sqrt(VectorMultiplication(dpList.get(j),dpList.get(j))))));
        }
        //开始计算新顶点
        int count = pList.length;
        for(int a=0;a<count;++a){
            int startIndex = a==0 ? count-1 : a-1;
            int endIndex   = a;
            double sina = VectorMOperator(ndpList.get(startIndex),ndpList.get(endIndex));
            double length = dist / sina;
            Coordinate vector = VectorSubtraction(ndpList.get(endIndex),ndpList.get(startIndex));
            newList.add(a,VectorAdd(pList[a],VectorMultiplication(vector,length)));
        }
        return newList;
    }
    
    
    /**
     * 做平行线
     * @param distance
     * @param coords 数组3个 按顺序放
     * @return
     */
    public static Coordinate ParallelLineMiddle(double dist,Coordinate[] pList){
        Coordinate a = pList[0];
        Coordinate b = pList[1];
        Coordinate c = pList[2];
        double af = Math.atan2(c.x - b.x,c.y-b.y);
        double bt = Math.atan2(b.x - a.x,b.y-c.y);
        if(af<0){
            af = af+2*Math.PI;
        }
        if(bt<0){
            bt = bt+2*Math.PI;
        }
        double k = (bt-af-Math.PI)/2;
        double r = af +k;
        double d = dist/Math.sin(k);
        double xp = b.x + d*Math.cos(r);
        double yp = b.y + d*Math.sin(r);
        Coordinate coor = new Coordinate(xp,yp);
        return coor;
    }
    
    /**
     * 线段旋转求极坐标值
     * @param ac
     * @param bc
     * @param rotate
     * @param d
     * @return
     */
    public static Coordinate RoateLineSegment(Coordinate ac, Coordinate bc, double rotate,double d){
        double[] result = PBMathUtil.PolarCoordinates(ac.x, ac.y, bc.x, bc.y, d, rotate);
        return new Coordinate(result[0],result[1]);
    }
    
}
