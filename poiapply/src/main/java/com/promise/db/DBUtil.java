/** @文件名: DBUtil.java @创建人：邢健  @创建日期： 2014-5-11 下午3:28:23 */
package com.promise.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @类名: DBUtil.java
 * @包名: com.promise.adcd.util
 * @描述: TODO
 * @作者: xingjian xingjian@yeah.net
 * @日期:2014-5-11 下午3:28:23
 * @版本: V1.0
 */
public class DBUtil {

	public static Connection connect = null;
	public static String username = "root";
	public static String password = "000000";
	public static String url = "jdbc:mysql://localhost:3386/test";

	public static Connection connectSQLServer = null;
	public static String usernameSQLServer = "floodwarn";
	public static String passwordSQLServer = "floodwarn";
	public static String urlSQLServer = "jdbc:sqlserver://172.18.18.35:1433;DatabaseName=floodwarnprovince";
	
	
	public static Connection GetMySQLConnection() {
		if (connect == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connect = DriverManager.getConnection(url, username, password);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return connect;
	}
	
	public static Connection GetSQLServerConnection() {
		if (connectSQLServer == null) {
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				connectSQLServer = DriverManager.getConnection(urlSQLServer, usernameSQLServer, passwordSQLServer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return connectSQLServer;
	}

}
