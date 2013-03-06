/** @文件名: ExportDataServiceImpl.java @创建人：邢健  @创建日期： 2013-3-5 下午4:09:09 */

package com.promise.cn.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.promise.cn.service.ExportDataService;
import com.promise.cn.util.DBUtil;
import com.promise.cn.vo.DataConnectConfigVO;
import com.promise.cn.vo.TableVO;

/**   
 * @类名: ExportDataServiceImpl.java 
 * @包名: com.promise.cn.service.impl 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-3-5 下午4:09:09 
 * @版本: V1.0   
 */
public class ExportDataServiceImpl implements ExportDataService{

	@Override
	public List<TableVO> getTableVOListByDCC(DataConnectConfigVO dcc) {
		Connection con = DBUtil.GetDataConection(dcc);
		List<TableVO> reList = new ArrayList<TableVO>();
		Statement st;
		try {
			st = con.createStatement();
			String sql = "";
			if(DBUtil.CheckDataSourceType(dcc).equals("1")){//oracle TABLE_NAME(VARCHAR2)
				sql = "select table_name from all_tables where owner='"+dcc.getUserName().toUpperCase()+"'";
			}else if(DBUtil.CheckDataSourceType(dcc).equals("2")){//mysql Tables_in_pbsoft(VARCHAR)
				sql = "show tables";
			}else if(DBUtil.CheckDataSourceType(dcc).equals("3")){//sqlserver name(nvarchar)
				sql = "select * from dbo.sysobjects t where xtype = 'U'";
			}
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData(); 
			int columnCount = rsmd.getColumnCount();
			// 输出列名   
		    for (int i=1; i<=columnCount; i++){   
		        System.out.print(rsmd.getColumnName(i));   
		        System.out.print("(" + rsmd.getColumnTypeName(i) + ")");   
		        System.out.print(" | ");   
		    }   
		    System.out.println();   
		    // 输出数据
		    while (rs.next()){
		        for (int i=1; i<=columnCount; i++){
		            System.out.print(rs.getString(i) + " | ");
		        }   
		        System.out.println();   
		    }   
		    rs.close();
		    st.close();
		    con.close(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
