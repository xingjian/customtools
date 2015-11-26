package com.promise.cn.util;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**  
 * 功能描述: 字符串帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年8月26日 上午10:10:38  
 */
public class StringUtil {

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     * @param chinese 汉字
     * @return 拼音
     */
    public static String ConverterToFirstSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    /**
     * 汉字转换位汉语拼音，英文字符不变
     * @param chines汉字
     * @return 拼音
     */
    public static String ConverterToSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }
    
    /**
     * 获取uuid方法
     * @return
     */
    public static String GetUUIDString(){
        String result = UUID.randomUUID().toString().replace("-", "");
        return result;
    }
    
    /**
     * 按特定的编码格式获取长度
     * @param str
     * @param code
     * @return
     * @throws UnsupportedEncodingException
     */
    public static int GetWordCountCode(String str, String code){  
        int length = 0;
        try {
            length = str.getBytes(code).length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }  
        return length;
    }
    
    /**
     * 由于Java是基于Unicode编码的，因此，一个汉字的长度为1，而不是2。 
     * 但有时需要以字节单位获得字符串的长度。例如，“123abc长城”按字节长度计算是10，而按Unicode计算长度是8。 
     * 为了获得10，需要从头扫描根据字符的Ascii来获得具体的长度。如果是标准的字符，Ascii的范围是0至255，如果是汉字或其他全角字符，Ascii会大于255。 
     * 因此，可以编写如下的方法来获得以字节为单位的字符串长度。
     */  
    public static int GetWordCount(String s){  
        int length = 0;  
        for(int i = 0; i < s.length(); i++){  
            int ascii = Character.codePointAt(s, i);  
            if(ascii >= 0 && ascii <=255)  
                length++;  
            else  
                length += 2;  
        }  
        return length;  
          
    }  
    
    /**
     * 按字节截取字符串
     * @param orignal
     * @param count
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String SubstringByByte(String orignal, int bytes,String encoding){   
        String reStr = "";
        try {
            int count = 0;
            if (orignal == null) {
                return "";
            }
            char[] tempChar = orignal.toCharArray();
            for (int i = 0; i < tempChar.length; i++) {
                String s1 = orignal.valueOf(tempChar[i]);
                byte[] b = s1.getBytes(encoding);
                count += b.length;
                if (count <= bytes) {
                    reStr += tempChar[i];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reStr;   
    }
    
    /**
     * 判断是否是中文
     * @param c
     * @return
     */
    public static boolean IsChineseChar(char c){
       return String.valueOf(c).matches("[\\u4E00-\\u9FA5]+");
    }
    
}
