/*@文件名: PolicyManagerTableModel.java  @创建人: 邢健   @创建日期: 2011-10-26 上午09:41:06*/
package com.promise.cn.model;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.promise.cn.vo.DataConnectConfigVO;
import com.promise.cn.vo.PolicyVO;
import com.promise.cn.vo.TableConfigVO;

/**   
 * @类名: PolicyManagerTableModel.java 
 * @包名: com.promise.cn.model 
 * @描述: 策略表格Model
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-26 上午09:41:06 
 * @版本 V1.0   
 */
public class PolicyManagerTableModel implements TableModel {

	public List<PolicyVO> policyVOList;
	
	public String[] tableHeader;
	
	public PolicyManagerTableModel(List<PolicyVO> policyVOList,String[] tableHeader){
		this.policyVOList = policyVOList;
		this.tableHeader = tableHeader;
	}
	
	@Override
	public int getRowCount() {
		return policyVOList.size();
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
		if(columnIndex==0){
			return String.class;
		}else{
			return JTableComboBoxEditor.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(rowIndex==-1){
			return false;
		}else{
			return false;
		}
	}

	/**
	 * 删除一行
	 */
	public void deleteSelectRow(int rowIndex){
		policyVOList.remove(rowIndex);
	}
	
	/**
	 * 通过rowIndex返回DataConnectConfigVO
	 * @param rowIndex
	 * @return
	 */
	public PolicyVO getPolicyVOByRow(int rowIndex) {
		PolicyVO policyVO = policyVOList.get(rowIndex);
		return policyVO;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PolicyVO policyVO = policyVOList.get(rowIndex);
		String retStr = "";
		if(columnIndex==0){
			retStr = policyVO.getName();
		}else if(columnIndex==1){
			retStr = policyVO.getType();
		}
		return retStr;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PolicyVO policyVO = policyVOList.get(rowIndex);
		if(columnIndex==0){
			policyVO.setName(aValue.toString());
		}else if(columnIndex==1){
			policyVO.setType(aValue.toString());
		}
		
	}

	@Override
	public void addTableModelListener(TableModelListener l) {

	}

	@Override
	public void removeTableModelListener(TableModelListener l) {

	}

}
