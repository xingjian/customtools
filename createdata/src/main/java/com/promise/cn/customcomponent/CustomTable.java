/*@文件名: CustomTable.java  @创建人: 邢健   @创建日期: 2011-10-26 上午10:46:35*/
package com.promise.cn.customcomponent;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

/**   
 * @类名: CustomTable.java 
 * @包名: com.promise.cn.customcomponent 
 * @描述: 自定义table 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-26 上午10:46:35 
 * @版本 V1.0   
 */
@SuppressWarnings("all")
public class CustomTable extends JTable {

	public int row=-1,col=-1;
	public TableCellEditor editor;

	public CustomTable(TableModel tm){
		super(tm);
	}
	
	public void setComboCell(int r,int c,TableCellEditor ce){
		this.row=r;
		this.col=c;
		this.editor=ce;

	}
	
	@Override
	public TableCellEditor getCellEditor(int r, int column) {
		 if(r==row&&column==col&&editor!=null)
			 return editor;
		 return super.getCellEditor(r, column);
	}


}
