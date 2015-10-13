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
	 * @param n 小数点后的位数 -n小数点钱的位数
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
	 * 计算2点直接直线距离
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double Distance(double x1,double y1,double x2,double y2){
	    return Math.sqrt(Math.pow(Math.abs((x1-x2)),2)+Math.pow(Math.abs((y1-y2)),2));
	}
}
