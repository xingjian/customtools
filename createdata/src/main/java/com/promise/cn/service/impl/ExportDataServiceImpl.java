/** @文件名: ExportDataServiceImpl.java @创建人：邢健  @创建日期： 2013-3-5 下午4:09:09 */

package com.promise.cn.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
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
	public List<TableVO> getTableVOListByDCC(DataConnectConfigVO dcc,String tableName) {
		Connection con = DBUtil.GetDataConection(dcc);
		List<TableVO> reList = new ArrayList<TableVO>();
		Statement st;
		try {
			st = con.createStatement();
			String sql = "";
			if(DBUtil.CheckDataSourceType(dcc).equals("1")){//oracle TABLE_NAME(VARCHAR2)
				sql = "select table_name from all_tables where owner='"+dcc.getUserName().toUpperCase()+"'";
				if(null!=tableName&&!tableName.equals("")){
					sql += " table_name='"+tableName+"'"; 
				}
			}else if(DBUtil.CheckDataSourceType(dcc).equals("2")){//mysql Tables_in_pbsoft(VARCHAR)
				sql = "show tables";//后期修正单表 where Tables_in_pbsoft
			}else if(DBUtil.CheckDataSourceType(dcc).equals("3")){//sqlserver name(nvarchar)
				sql = "select name from dbo.sysobjects t where xtype = 'U'";
				if(null!=tableName&&!tableName.trim().equals("")){
					sql += " and name like '%"+tableName+"%'"; 
				}
			}
			System.out.println(sql);
			ResultSet rs = st.executeQuery(sql);
		    // 输出数据
			if(DBUtil.CheckDataSourceType(dcc).equals("3")){
			    while (rs.next()){
			    	TableVO t = new TableVO();
			        t.setName(rs.getString(1));
			        t.setColumnStr("*");//日后解析列
			        t.setId(rs.getString(1));
			        reList.add(t);
			    }  
			}
		    rs.close();
		    st.close();
		    con.close(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reList;
	}

	
	@Override
	public boolean exportDataToExcel(DataConnectConfigVO dcc, TableVO table,String url, boolean isMultiFile) {
		if(isMultiFile){
			
		}
		return false;
	}

	
}
