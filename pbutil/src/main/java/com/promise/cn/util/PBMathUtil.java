/** @文件名: PBMathUtil.java @创建人：邢健  @创建日期： 2013-12-4 上午8:54:28 */

package com.promise.cn.util;

/**   
 * @类名: PBMathUtil.java 
 * @包名: com.promise.cn.util 
 * @描述: 数学常用 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-12-4 上午8:54:28 
 * @版本: V1.0   
 */
public class PBMathUtil {

	/**
	 * 四舍五入
	 * @param d
	 * @param n 小数点后的位数 -n小数点钱的位数
	 * @return
	 */
	private static double DoubleRound(double d, int n) {
		d = d * Math.pow(10, n);
		d += 0.5d;
		d = (long)d;
		d = d / Math.pow(10d, n);
		return d;
	}
	
	public PBMathUtil() {
	}

}
