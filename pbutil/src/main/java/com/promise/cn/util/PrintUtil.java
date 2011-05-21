/**@文件名: PrintUtil.java @作者： promisePB xingjian@yeah.net @日期 2011-5-12 上午09:16:18 */

package com.promise.cn.util;

/**   
 * @类名: PrintUtil.java 
 * @包名: com.promise.cn.util 
 * @描述: 输出信息帮助类 
 * @作者： promisePB xingjian@yeah.net   
 * @日期： 2011-5-12 上午09:16:18 
 * @版本： V1.0   
 */

public class PrintUtil {
	
	/**
	 * 
	 * 功能：输出对象的内容
	 * 描述：对象可以是任何内容
	 * @param object
	 */
	public static void printObject(Object object){
		if(object instanceof int[]){
			int[] temp = (int[]) object;
			String printStr ="";
			for(int i=0;i<temp.length;i++){
				printStr = printStr+temp[i]+",";
			}
			System.out.println(printStr.substring(0, printStr.length()-1));
		}else{
			System.out.println(object.toString());
		}
	}
}
