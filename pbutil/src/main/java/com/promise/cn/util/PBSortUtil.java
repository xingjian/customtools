/** 文件名: PBSortUtil.java 创建人：邢健  创建日期： 2013-12-3 下午3:03:01 */

package com.promise.cn.util;



/**   
 * 类名: PBSortUtil.java 
 * 包名: com.promise.cn.util 
 * 描述: 排序算法 
 * 作者: xingjian xingjianyeah.net   
 * 日期:2013-12-3 下午3:03:01 
 * 版本: V1.0   
 */
public class PBSortUtil {

	/**
	 * 二分法
	 * @param array
	 * @param key 目标值
	 * @return
	 */
	public static int[] BanarySort(int[] array, int key) {
		int[] result = new int[2];
		int low = 0;
		int hight = array.length - 1;
		int mid = 0;
		int midVal = 0;
		while (true) {
			mid = (low + hight) >>> 1;
			midVal = array[mid];
			if (midVal > key) {
				hight = mid;
				if (array[hight] >= key && array[hight - 1] <= key) {
					result[0] = array[hight - 1];
					result[1] = array[hight];
					break;
				}
			} else if (midVal < key) {
				low = mid;
				if (array[low] <= key && key <= array[low + 1]) {
					result[0] = array[low];
					result[1] = array[low+1];
					break;
				}
			} else {
				if (mid % 2 == 0) {
					result[0] = array[mid - 1];
					result[1] = array[mid];
				} else {
					result[0] = array[mid];
					result[1] = array[mid+1];
				}
				break;
			}
		}
		return result;
	}
}
