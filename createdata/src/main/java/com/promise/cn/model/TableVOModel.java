/** @文件名: TableVOModel.java @创建人：邢健  @创建日期： 2013-3-5 下午2:33:08 */

package com.promise.cn.model;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.promise.cn.vo.TableConfigVO;
import com.promise.cn.vo.TableVO;

/**   
 * @类名: TableVOModel.java 
 * @包名: com.promise.cn.model 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-3-5 下午2:33:08 
 * @版本: V1.0   
 */
public class TableVOModel implements TableModel {

	public List<TableVO> tcVOList;
	
	public String[] tableHeader;
	
	public TableVOModel(List<TableVO> tcVOList,String[] tableHeader){
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
		TableVO dccTemp = tcVOList.get(rowIndex);
		String retStr = "";
		if(columnIndex==0){
			retStr = dccTemp.getId()+"";
		}else if(columnIndex==1){
			retStr = dccTemp.getName();
		}
		return retStr;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		TableVO dccTemp = tcVOList.get(rowIndex);
		if(columnIndex==0){
			dccTemp.setId(aValue.toString());
		}else if(columnIndex==1){
			dccTemp.setName(aValue.toString());
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {

	}

	@Override
	public void removeTableModelListener(TableModelListener l) {

	}

}