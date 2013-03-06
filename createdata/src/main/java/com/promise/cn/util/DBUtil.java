/** @文件名: DBUtil.java @创建人：邢健  @创建日期： 2013-3-5 下午4:26:54 */

package com.promise.cn.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.promise.cn.vo.DataConnectConfigVO;

/**   
 * @类名: DBUtil.java 
 * @包名: com.promise.cn.util 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-3-5 下午4:26:54 
 * @版本: V1.0   
 */
public class DBUtil {

	/**
	 * 检验数据源的类型 1 oralce 2 mysql 3 sqlserver
	 * @return
	 */
	public static String CheckDataSourceType(DataConnectConfigVO dcc){
		String str = dcc.getUrl();
		if(str.indexOf("oracle")!=-1){
			return "1";
		}else if(str.indexOf("mysql")!=-1){
			return "2";
		}else if(str.indexOf("sqlserver")!=-1){
			return "3";
		}else{
			return null;
		}
	}
	
	public static Connection GetDataConection(DataConnectConfigVO dcc){
		Connection connect = null;
		try {
			Class.forName(dcc.getDriverClassName());
			connect = DriverManager.getConnection(dcc.getUrl(),dcc.getUserName(),dcc.getPassword());
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return connect;
	}
}
