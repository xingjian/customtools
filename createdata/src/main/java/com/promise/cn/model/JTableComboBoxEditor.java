/*@文件名: JTableComboBoxEditor.java  @创建人: 邢健   @创建日期: 2011-10-26 上午09:34:22*/
package com.promise.cn.model;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**   
 * @类名: JTableComboBoxEditor.java 
 * @包名: com.promise.cn.model 
 * @描述: jtable下拉框 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-26 上午09:34:22 
 * @版本 V1.0   
 */
public class JTableComboBoxEditor extends DefaultCellEditor {

	public JTableComboBoxEditor(String[] items) {
		super(new JComboBox(items));
	}

}
