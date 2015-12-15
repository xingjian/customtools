/**文件名: PBMathUtil.java 创建人：邢健  创建日期： 2013-12-4 上午8:54:28 */

package com.promise.cn.util;


/**   
 * 类名: PBMathUtil.java 
 * 包名: com.promise.cn.util 
 * 描述: 数学常用 
 * 作者: xingjian xingjianyeah.net   
 * 日期:2013-12-4 上午8:54:28 
 * 版本: V1.0   
 */
public class PBMathUtil {

	/**
	 * 四舍五入
	 * @param d
	 * @param n 小数点后的位数 -n小数点前的位数
	 * @return
	 */
	public static double DoubleRound(double d, int n) {
		d = d * Math.pow(10, n);
		d += 0.5d;
		d = (long)d;
		d = d / Math.pow(10d, n);
		return d;
	}

	/**
	 * 计算2点距离 勾股定理
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double Distance(double x1,double y1,double x2,double y2){
	    return Math.sqrt(Math.pow(Math.abs((x1-x2)),2)+Math.pow(Math.abs((y1-y2)),2));
	}
	

	/**
     * 求高
     * x,y 到 x1,y1,x2,y2的高
     * @return value
     */
    public static double GetHeight(double x,double y,double x1,double y1,double x2,double y2){
        double a = Math.sqrt(((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)));
        double b = Math.sqrt(((x-x1)*(x-x1)+(y-y1)*(y-y1)));
        double c = Math.sqrt(((x-x2)*(x-x2)+(y-y2)*(y-y2)));
        double p = (a+b+c)/2;
        double s = Math.sqrt(p*(p-a)*(p-b)*(p-c));
        return 2*s/a;        
    }
    
    /**
     * @根据两点求出垂线过第三点的直线的交点 
     * @param pt1 直线上的第一个点 
     * @param pt2 直线上的第二个点 
     * @param pt3 垂线上的点 
     * @return 返回点到直线的垂直交点坐标 
     */
    public static double[] GetVerticalPoint(double x0,double y0,double x1,double y1,double x2,double y2){
        //当平行与y轴
        if(x1 == x2){
            return new double[]{x1,y0};
        }
        //平行与x轴
        if(y1 == y2){
            return new double[]{x0,y1};
        }
        double k = (y2-y1)/(x2-x1);
        double nx = (k*k*x1+k*(y0-y1)+x0)/(k*k+1);
        double ny = k*(nx-x1)+y1;
        return new double[]{nx,ny};
    }
    
    /**
     * 线段打断
     * 线段AB,输入距离在线段内插入一个点C,并将线段分成两部分
     * 求内差点C的坐标 
     * @param xa a坐标x
     * @param xb b坐标x
     * @param ya a坐标y
     * @param yb b坐标y
     * @param distance c到b的长度
     * @return double数组
     */
    public static double[] SplitSegmentByLength(double xa,double xb,double ya,double yb,double distance){
        double length = Math.sqrt(Math.pow((xb-xa), 2)+Math.pow((yb-ya), 2));
        if(length<distance){
            return null;
        }
        double xc = xa+(xb-xa)*distance/length;
        double yc = ya+(yb-ya)*distance/length;
        return new double[]{xc,yc};
    }
    
    
    /**
     * 已有线段c1c2,以线段为极轴
     * 输入角度af和长度distance
     * @param c1
     * @param c2
     * @param dis
     * @return
     */
    public static double[] PolarCoordinates(double x1,double y1,double x2,double y2, double dis,double af){
        double l = PBMathUtil.Distance(x1, y1, x2, y2);
        double dx = x2 - x1;
        double dy = y2 - y1;
        dx = dx*Math.cos(Math.PI*af/180) - dy*Math.sin(Math.PI*af/180);
        dy = dx*Math.sin(Math.PI*af/180) + dy*Math.cos(Math.PI*af/180);
        double px = x1 + dx*dis/l;
        double py = y1 + dy*dis/l;
        return new double[]{px,py};
    }
    
    /**
     * 三点画圆
     * @param xa a x值
     * @param ya a y值
     * @param xb b x值
     * @param yb b y值
     * @param xc c x值
     * @param yc c y值
     * @return 返回圆心坐标和半径 result[0] result[1] 中心坐标 result[3] 半径值
     */
    public static double[] CreateCircleByPoint(double xa,double ya,double xb,double yb,double xc,double yc){
        double[] result = new double[3];
        double A = xb - xa;
        double B = yb - ya;
        double C = xc - xa;
        double D = yc - ya;
        double E = A*(xa+xb)+B*(ya+yb);
        double F = C*(xa+xc)+D*(ya+yc);
        double G = 2*(A*(yc-yb)-B*(xc-xb));
        double xp = (D*E - B*F)/G;
        double yp = (A*F - C*E)/G;
        double r = Math.sqrt(Math.pow((xa-xp),2)+Math.pow((ya-yp),2));
        result[0] = xp;
        result[1] = yp;
        result[2] = r;
        return result;
    }
    
    /**
     * 前面交汇
     * 在三角形ABP,已知点  A B的坐标为xa,ya,xb,yb,并且知道角PAB,PBA
     * java里面没有余切 1/Math.tan(theta)
     * @param xa
     * @param ya
     * @param xb
     * @param yb
     * @param anglePAB
     * @param anglePBA
     * @return 返回p点坐标
     */
    public static double[] CalcFrontIntersection(double xa,double ya,double xb,double yb,double anglePAB,double anglePBA){
        double[] result = new double[2];
        result[0] = (xa*(1/Math.tan(anglePBA)) + xb*(1/Math.tan(anglePAB)) -ya + yb)/((1/Math.tan(anglePAB))+(1/Math.tan(anglePBA)));
        result[1] = (ya*(1/Math.tan(anglePBA)) + yb*(1/Math.tan(anglePAB)) + xa - xb)/((1/Math.tan(anglePAB))+(1/Math.tan(anglePBA)));
        return result;
    }
}
