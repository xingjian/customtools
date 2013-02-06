/** @文件名: ADPlanBTableModel.java @创建人：邢健  @创建日期： 2013-2-5 下午5:52:14 */

package ui;

import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import bean.ADPlanB;

/**   
 * @类名: ADPlanBTableModel.java 
 * @包名: ui 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-2-5 下午5:52:14 
 * @版本: V1.0   
 */
public class ADPlanBTableModel implements TableModel {

	public List<ADPlanB> dccList;
	
	public String[] tableHeader;
	
	public ADPlanBTableModel(List<ADPlanB> dccList,String[] tableHeader){
		this.tableHeader = tableHeader;
		this.dccList = dccList;
	}
	
	@Override
	public int getRowCount() {
		if(null!=dccList){
			return dccList.size();
		}
		return 0;
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
		ADPlanB dccTemp = dccList.get(rowIndex);
		String retStr = "";
		if(columnIndex==0){
			retStr = dccTemp.getPlanID()+"";
		}else if(columnIndex==1){
			retStr = dccTemp.getId();
		}else if(columnIndex==2){
			retStr = dccTemp.getName();
		}else if(columnIndex==3){
			retStr = dccTemp.getPubTime();
		}
		return retStr;
	}
	
	/**
	 * 通过rowIndex返回DataConnectConfigVO
	 * @param rowIndex
	 * @return
	 */
	public ADPlanB getDataConnectConfigVOByRow(int rowIndex) {
		ADPlanB dccTemp = dccList.get(rowIndex);
		return dccTemp;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		dccList.add(rowIndex, (ADPlanB)aValue);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {

	}

	@Override
	public void removeTableModelListener(TableModelListener l) {

	}

}
