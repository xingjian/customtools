/*@文件名: DataSourceTableModel.java  @创建人: 邢健   @创建日期: 2011-10-21 下午02:04:52*/
package com.promise.cn.model;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.promise.cn.vo.DataConnectConfigVO;

/**   
 * @类名: DataSourceTableModel.java 
 * @包名: com.promise.cn.model 
 * @描述: TODO(用一句话描述该文件做什么) 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-21 下午02:04:52 
 * @版本 V1.0   
 */
public class DataSourceTableModel implements TableModel {

	public List<DataConnectConfigVO> dccList;
	
	public String[] tableHeader;
	
	public DataSourceTableModel(List<DataConnectConfigVO> dccList,String[] tableHeader){
		this.tableHeader = tableHeader;
		this.dccList = dccList;
	}
	
	@Override
	public int getRowCount() {
		return dccList.size();
	}

	@Override
	public int getColumnCount() {
		return tableHeader.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return tableHeader[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public void deleteSelectRow(int rowIndex){
		dccList.remove(rowIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DataConnectConfigVO dccTemp = dccList.get(rowIndex);
		String retStr = "";
		if(columnIndex==0){
			retStr = dccTemp.getName();
		}else if(columnIndex==1){
			retStr = dccTemp.getUserName();
		}else if(columnIndex==2){
			retStr = dccTemp.getPassword();
		}else if(columnIndex==3){
			retStr = dccTemp.getDriverClassName();
		}else if(columnIndex==4){
			retStr = dccTemp.getUrl();
		}
		return retStr;
	}
	
	/**
	 * 通过rowIndex返回DataConnectConfigVO
	 * @param rowIndex
	 * @return
	 */
	public DataConnectConfigVO getDataConnectConfigVOByRow(int rowIndex) {
		DataConnectConfigVO dccTemp = dccList.get(rowIndex);
		return dccTemp;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		dccList.add(rowIndex, (DataConnectConfigVO)aValue);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {

	}

	@Override
	public void removeTableModelListener(TableModelListener l) {

	}

}
