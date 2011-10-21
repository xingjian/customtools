/*@文件名: DataConnectPanel.java  @创建人: 邢健   @创建日期: 2011-10-20 下午04:05:32*/
package com.promise.cn.main;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

/**   
 * @类名: DataConnectPanel.java 
 * @包名: com.promise.cn.main 
 * @描述: 数据连接页面 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-20 下午04:05:32 
 * @版本 V1.0   
 */
public class DataConnectPanel extends JPanel {

	private JComboBox jComboBox;
	private JTable jTable;
	
	public DataConnectPanel(){
		this.setName("连接属性");
		this.setLayout(new BorderLayout());
		initPanel();
	}
	
	/**
	 * 初始化面板
	 */
	public void initPanel(){
		this.add(getJComboBox(), BorderLayout.NORTH);
		this.add(getJTable(), BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	public JComboBox getJComboBox(){
		if(jComboBox==null){
			jComboBox = new JComboBox();
		}
		return jComboBox;
	}
	
	public JTable getJTable(){
		if(jTable==null){
			jTable = new JTable();
		}
		return jTable;
	}
}
