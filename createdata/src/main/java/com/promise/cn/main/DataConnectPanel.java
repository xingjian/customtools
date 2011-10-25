/*@文件名: DataConnectPanel.java  @创建人: 邢健   @创建日期: 2011-10-20 下午04:05:32*/
package com.promise.cn.main;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.promise.cn.util.CreateDataUtil;
import com.promise.cn.vo.DataConnectConfigVO;

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
	private JComboBox jComboBoxTables;
	public JTextField jTextFieldTableName;
	public JTextField jTextFieldDataCount;
	private JTable jTable;
	private JLabel label1 = new JLabel("数据源:");
	private JLabel label2 = new JLabel("表名:");
	private JLabel label3 = new JLabel("数据量(条):");
	public List<DataConnectConfigVO> tableData = null; 
	public DefaultComboBoxModel dcbm = null; 
	
	public DataConnectPanel(List<DataConnectConfigVO> tableData){
		this.tableData = tableData;
		this.setName("连接配置属性");
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		flowLayout.setHgap(22);
		flowLayout.setVgap(10);
		this.setLayout(flowLayout);
		initPanel();
	}
	
	/**
	 * 初始化面板
	 */
	public void initPanel(){
		jTextFieldTableName = new JTextField("table name");
		jTextFieldDataCount = new JTextField();
		jTextFieldDataCount.setColumns(15);
		jTextFieldTableName.setHorizontalAlignment(JTextField.CENTER);
		jTextFieldTableName.setColumns(15);
		label1.setFont(CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12));
		label2.setFont(CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12));
		label3.setFont(CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12));
		this.add(label1);
		this.add(getJComboBox());
		this.add(label2);
		this.add(jTextFieldTableName);
		this.add(label3);
		this.add(jTextFieldDataCount);
		this.setBackground(Color.white);
		this.setBorder(new TitledBorder(null,"数据源和表",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
		this.setVisible(true);
	}
	
	public JComboBox getjComboBoxTables(){
		if(jComboBoxTables==null){
			jComboBoxTables = new JComboBox();
		}
		return jComboBoxTables;
	}
	
	/**
	 * 返回数据源名称
	 * @return
	 */
	public String[] getDataSourceName(){
		String[] names = new String[tableData.size()];
		if(tableData==null||tableData.size()==0){
			
		}
		else{
			for(int i=0;i<tableData.size();i++){
				String name  = tableData.get(i).getName();
				names[i] = name;
			}
		}
		return names;
	}
	
	public JComboBox getJComboBox(){
		if(jComboBox==null){
			jComboBox = new JComboBox();
			dcbm = new DefaultComboBoxModel(getDataSourceName());
			jComboBox.setModel(dcbm);
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
