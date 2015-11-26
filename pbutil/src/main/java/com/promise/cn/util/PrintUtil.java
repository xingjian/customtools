/**文件名: PrintUtil.java 作者： promisePB xingjianyeah.net 日期 2011-5-12 上午09:16:18 */
package com.promise.cn.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**   
 * 类名: PrintUtil.java 
 * 包名: com.promise.cn.util 
 * 描述: 输出信息帮助类 
 * 作者： promisePB xingjianyeah.net   
 * 日期： 2011-5-12 上午09:16:18 
 * 版本： V1.0   
 */
@SuppressWarnings("all")
public class PrintUtil {
	
	/**
	 * 功能：输出对象的内容
	 * 描述：对象可以是任何内容
	 * @param object
	 */
	public static void PrintObject(Object object){
		if(object instanceof int[]){
			int[] temp = (int[]) object;
			String printStr ="";
			for(int i=0;i<temp.length;i++){
				printStr = printStr+temp[i]+",";
			}
			System.out.println(printStr.substring(0, printStr.length()-1));
		}else if(object instanceof List){
		    List list = (List)object;
		    for(int i=0;i<list.size();i++){
                System.out.println(list.get(i).toString());
            }
		}else if(object instanceof Map){
		    Map map = (Map)object;
		    Set<String> set = map.keySet();
		    Iterator<String> iterator = set.iterator();
		    while (iterator.hasNext()) {
		        String str = iterator.next();
		        System.out.println(str + " : " + map.get(str));
		    }
		}
		else{
			System.out.println(object.toString());
		}
	}
	
	/**
	 * 输出文件内容
	 * @param filePath
	 */
	public static void PrintFileByLine(String filePath){
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while((s = br.readLine())!=null){
                if(s.trim()!=""){
                    System.out.println(s);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
