/*@文件名: TableConfigVOModel.java  @创建人: 邢健   @创建日期: 2011-10-24 下午03:10:04*/
package com.promise.cn.model;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.promise.cn.vo.DataConnectConfigVO;
import com.promise.cn.vo.TableConfigVO;

/**   
 * @类名: TableConfigVOModel.java 
 * @包名: com.promise.cn.model 
 * @描述: TODO(用一句话描述该文件做什么) 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-24 下午03:10:04 
 * @版本 V1.0   
 */
public class TableConfigVOModel implements TableModel {

	public List<TableConfigVO> tcVOList;
	
	public String[] tableHeader;
	
	public TableConfigVOModel(List<TableConfigVO> tcVOList,String[] tableHeader){
		this.tableHeader = tableHeader;
		this.tcVOList = tcVOList;
	}
	@Override
	public int getRowCount() {
		return tcVOList.size();
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

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(rowIndex==-1){
			return false;
		}else{
			return true;
		}
		
	}

	/**
	 * 删除一行
	 */
	public void deleteSelectRow(int rowIndex){
		tcVOList.remove(rowIndex);
	}
	
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		TableConfigVO dccTemp = tcVOList.get(rowIndex);
		String retStr = "";
		if(columnIndex==0){
			retStr = dccTemp.getName();
		}else if(columnIndex==1){
			retStr = dccTemp.getType();
		}else if(columnIndex==2){
			retStr = dccTemp.getPolicyName();
		}
		return retStr;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		TableConfigVO dccTemp = tcVOList.get(rowIndex);
		if(columnIndex==0){
			dccTemp.setName(aValue.toString());
		}else if(columnIndex==1){
			dccTemp.setType(aValue.toString());
		}else if(columnIndex==2){
			dccTemp.setPolicyName(aValue.toString());
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {

	}

	@Override
	public void removeTableModelListener(TableModelListener l) {

	}

}
