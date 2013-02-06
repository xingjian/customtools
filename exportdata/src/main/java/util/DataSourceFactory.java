package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceFactory {
	
	/**
	 * 获取数据库连接
	 * @param dst
	 * @param url
	 * jdbc:microsoft:sqlserver://localhost:1433;databaseName=pubs
	 * @param userName
	 * @param password
	 * @return
	 */
	public static Connection getConnection(DataSourceType dst,String url,String userName,String password){
		Connection connect = null;
		try {
			if(dst==DataSourceType.SQLSERVER){
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			}
			connect = DriverManager.getConnection(url,userName,password);
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return connect;
	}
}
