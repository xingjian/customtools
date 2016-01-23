package com.promise.cn.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**  
 * 功能描述: PGSQL帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月23日 下午3:42:36  
 */
public class PBPSQLUtil {

    /**
     * 复制表
     * 支持跨数据库
     * @param source
     * @param des
     * @return
     */
    public static String CopyTable(Connection source,String sourceTName,Connection des,String desTName){
        String ret = "success";
        boolean boo1 = IsTableExist(source,sourceTName);
        if(boo1){
            boolean boo2 = IsTableExist(des,desTName);
            if(boo2){//如果存在的话，验证表结构，并插入数据
                
            }else{//创建表，并插入数据
                
            }
        }else{
            ret = sourceTName+" is not exist.";
        }
        return ret;
    }
    
    /**
     * 
     * @param connection
     * @param tName
     * @return
     */
    public static List<String> GetTableStructure(Connection connection,String tName){
        List<String> ret = new ArrayList<String>();
        return ret;
    }
    
    /**
     * 判断表是否存在
     * @return 
     */
    public static boolean IsTableExist(Connection connection,String tName){
        boolean boo = false;
        try {
            DatabaseMetaData dmd = connection.getMetaData();
            //%"就是表示*的意思，也就是任意所有
            ResultSet tableRet = dmd.getTables(null, "%",tName,new String[]{"TABLE"});
            boo = tableRet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return boo;
    }
}