package com.promise.cn.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**  
 * 功能描述: 获取常用数据库连接对象
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年8月26日 上午9:17:16  
 */
public class DBConnection {
    
    /**
     * postgres数据库连接对象
     * @param url jdbc:postgresql://127.0.0.1:5432/postgis
     * @param username postgis
     * @param passwd postgis
     * @return 数据库连接对象
     */
    public static Connection GetPostGresConnection(String url,String username,String passwd){
        try {
           Class.forName("org.postgresql.Driver");
           return DriverManager.getConnection(url, username,passwd);
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       } 
   }
    
    
    /**
     * oralce 数据库连接队形
     * @param url jdbc:oracle:thin:@182.92.183.85:1521:orcl
     * @param username ttyj_tocc
     * @param passwd admin123ttyj7890uiop
     * @return 数据库连接对象
     */
    public static Connection GetOracleConnection(String url,String username,String passwd){
         try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(url, username,passwd);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } 
    }
    
    /**
     * 获取mysql数据库连接
     * @param url jdbc:mysql://localhost:3306/webtestexample
     * @param username root
     * @param passwd xingjian
     * @return 数据库连接对象
     */
     public static Connection GetMySQLConnection(String url,String username,String passwd){
         try {
              Class.forName("com.mysql.jdbc.Driver");
              return DriverManager.getConnection(url,username,passwd);
         } catch (Exception e) {
              e.printStackTrace();
              return null;
         }
     }
     
     /**
      * 获取sqlserver数据库连接
      * @param url jdbc:sqlserver://172.18.18.34:1433;databaseName=floodwarncity
      * @param userName floodwarn
      * @param password floodwarn
      * @return 数据库连接对象
      */
     public static Connection getSQLServerConnection (String url,String userName,String password){
         try {
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             return DriverManager.getConnection(url,userName,password);
         }catch(Exception e) {
             e.printStackTrace();
             return null;
         }
     }
     
     /**
      * 关闭数据库连接
      * @param connection
      */
     public static void CloseConnection(Connection connection){
         if(null!=connection){
             try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
         }
     }
     
     /**
      * 关闭 Statement
      * @param st
      */
     public static void CloseStatement(Statement st){
         if(null!=st){
             try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
         }
     }
     
     /**
      * 执行insert update delete语句
      * @param connection 数据库连接对象
      * @param sql 要执行的sql语句
      * @param isClose 连接是否自动关闭  true自动关闭  false不关闭 需要调用 CloseConnection() 手都关闭
      * @return 返回执行结果状态
      */
     public static String executeSQL(Connection connection,String sql,boolean isClose){
         try {
            Statement st = connection.createStatement();
            st.execute(sql);
            CloseStatement(st);
            if(isClose){
                CloseConnection(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return "success";
     }
}
