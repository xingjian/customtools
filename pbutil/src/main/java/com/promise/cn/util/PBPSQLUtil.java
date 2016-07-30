package com.promise.cn.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**  
 * 功能描述: PGSQL帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月23日 下午3:42:36  
 */
public class PBPSQLUtil {

    /**
     * 根据数据库的不同返回对应的不同映射
     * @param dbType
     * @return
     */
    public static Map<String,String> GetColumnMapping(DBType dbType){
        Map<String,String> retMap = new HashMap<String,String>();
        if(dbType.equals(DBType.PostgreSQL)){
            retMap.put("varchar", "String");
            retMap.put("text", "String");
            retMap.put("int8", "int");
            retMap.put("float8", "double");
            retMap.put("int4", "int");
            retMap.put("float4", "float");
            retMap.put("int2", "int");
            retMap.put("date", "date");
            retMap.put("String", "varchar");
            retMap.put("String", "text");
        }else if(dbType.equals(DBType.Oracle)){
            retMap.put("var", "varchar");
            retMap.put("text", "text");
            retMap.put("int8", "bigint");
            retMap.put("float8", "double precision");
            retMap.put("int4", "integer");
            retMap.put("float4", "real");
            retMap.put("int2", "smallint");
            retMap.put("date", "date");
        }
        return retMap;
    }
    
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
     * 通过sql语句对数据进行copy,前提是表必须存在
     * @param source 数据源链接
     * @param dbType 源数据源类型，为了分页语句使用
     * @param sourceSQL 数据源的查询语句
     * @param des 目标数据源连接
     * @param desSQL 目标执行插入的语句
     * @param pageNum 如果数据量过大的话，会自动分页 。默认5000
     * @param columnType String int double float 
     * @return 执行结果状态
     */
    public static String CopyTable(Connection source,DBType dbType,String sourceSQL,Connection des,String desSQL,int pageNum,String[] columnType) throws Exception{
        ResultSet rs = source.createStatement().executeQuery(sourceSQL);
        PreparedStatement psInsert = des.prepareStatement(desSQL);
        int index = 0;
        while(rs.next()){
            for(int i=0;i<columnType.length;i++){
                String columnStr = columnType[i];
                if(columnStr.trim().toLowerCase().equals("string")){
                    psInsert.setString(i+1, rs.getString(i+1));
                }else if(columnStr.trim().toLowerCase().equals("int")){
                    psInsert.setInt(i+1, rs.getInt(i+1));
                }else if(columnStr.trim().toLowerCase().equals("double")){
                    psInsert.setDouble(i+1, rs.getDouble(i+1));
                }else if(columnStr.trim().toLowerCase().equals("float")){
                    psInsert.setFloat(i+1, rs.getFloat(i+1));
                }else{
                    psInsert.setString(i+1, rs.getString(i+1));
                }
            }
            psInsert.addBatch();
            index++;
            if(index%pageNum==0){
                psInsert.executeBatch();
            }
        }
        psInsert.executeBatch();
        psInsert.close();
        rs.close();
        return "success";
    }
    
    
    /**
     * 
     * @param connection
     * @param tName
     * @return
     */
    public static List<String> GetTableStructure(Connection connection,String tName) throws Exception{
        List<String> ret = new ArrayList<String>();
        DatabaseMetaData dbMeta = connection.getMetaData(); 
        ResultSet colRet = dbMeta.getColumns(null,"%", tName,"%"); 
        while(colRet.next()) { 
            String columnName = colRet.getString("COLUMN_NAME"); 
            String columnType = colRet.getString("TYPE_NAME"); 
            int datasize = colRet.getInt("COLUMN_SIZE"); 
            int digits = colRet.getInt("DECIMAL_DIGITS"); 
            int nullable = colRet.getInt("NULLABLE"); 
        }
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
    
    /**
     * 根据连接获取所有表名
     * @param connection
     * @return
     */
    public static List<String> GetTables(Connection connection){
        List<String> retList = new ArrayList<String>();
        try{
            DatabaseMetaData dmd = connection.getMetaData();
            String[] types   =   {"TABLE"};
            ResultSet rs = dmd.getTables(null, null, null, types);
            while(rs.next()){
                String tableName = rs.getObject("TABLE_NAME").toString();
                retList.add(tableName);
            }  
        }catch(Exception e){
            e.printStackTrace();
        }
        return retList;
    }
    
    /**
     * 生成表的creat 语句
     * @param connection
     * @param dbType 目标数据库类型
     * @param srcTableName 源表名
     * @param desTableName 目标表名 如果为空的话，使用 srcTableName
     * @return
     */
    public static String GetTableDDLCreate(Connection connection,DBType dbType,String srcTableName,String desTableName) throws Exception{
        StringBuffer retSB = new StringBuffer();
        DatabaseMetaData odmd = connection.getMetaData();
        String dataBaseProductName = odmd.getDatabaseProductName();
        desTableName = ((null==desTableName||desTableName.trim().equals(""))?srcTableName:desTableName);
        //PostgreSQL Oracle
        System.out.println(dataBaseProductName);
        ResultSet pkRSet = odmd.getPrimaryKeys(null, null,srcTableName);
        ResultSet rscol = odmd.getColumns(null, null,srcTableName, null);
        ResultSet inset = odmd.getIndexInfo(null, null, srcTableName, false ,true);
        retSB.append("create table "+ ((null==desTableName||desTableName.trim().equals(""))?srcTableName:desTableName)).append(" (");
        if(dataBaseProductName.equals("PostgreSQL")){
            
        }
        while (rscol.next()) {
            String columnName = rscol.getString(4);
            String columnTypeName = rscol.getString(6);
            int columnSize  = rscol.getInt(7);
            System.out.println(columnName +"----"+columnTypeName+"----"+columnSize);
        }
        retSB.append(" )");
        return retSB.toString();
    }
}
