/*@文件名: DataSourceVOView.java  @创建人: 邢健   @创建日期: 2011-10-21 上午10:25:26*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.promise.cn.event.DataEventManager;
import com.promise.cn.util.CreateDataUtil;
import com.promise.cn.vo.DataConnectConfigVO;

/**   
 * @类名: DataSourceVOView.java 
 * @包名: com.promise.cn.main 
 * @描述: 数据源界面 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-21 上午10:25:26 
 * @版本 V1.0   
 */
@SuppressWarnings("all")
public class DataSourceVOView extends JDialog {
	private DataConnectConfigVO dcc;
	private JButton okBtn = new JButton("确定");
	private JButton exitBtn = new JButton("取消");
	private JPanel centerPanel = new JPanel(new GridLayout(5,1));
	private JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JLabel nameLabel = new JLabel("Name：");
	private JLabel userNameLabel = new JLabel("UserName：");
	private JLabel pwdLabel = new JLabel("PWD：");
	private JLabel driverClassNameLabel = new JLabel("DriverClassName：");
	private JLabel urlLabel = new JLabel("URL：");
	private JTextField nameText = new JTextField(35);
	private JTextField userNameText = new JTextField(35);
	private JTextField pwdText = new JTextField(35);
	private JTextField driverClassNameText = new JTextField(35);
	private JTextField urlText = new JTextField(35);
	public DataEventManager dataEventManager = null;
	public JLabel typeJLabel = new JLabel("type");
	public String[] typesJComboBoxItems = new String[5];
	public JComboBox typeJComboBox = null;
	/**
	 * 构造函数
	 */
	public DataSourceVOView(DataConnectConfigVO dcc) {
		this.dcc = dcc;
		initData();
		init();
		initBtnEvent();
		this.setModal(true);
	}

	/**
	 * 初始化数据
	 */
	public void initData(){
		typesJComboBoxItems[0] = CreateDataUtil.DATABASETYPE_ORACLE;
		typesJComboBoxItems[1] = CreateDataUtil.DATABASETYPE_MYSQL;
		typesJComboBoxItems[2] = CreateDataUtil.DATABASETYPE_POSTGRESQL;
		typesJComboBoxItems[3] = CreateDataUtil.DATABASETYPE_SQLSERVER;
		typesJComboBoxItems[4] = CreateDataUtil.DATABASETYPE_DB2;
		typeJComboBox = new JComboBox(typesJComboBoxItems);
	}
	
	public void initBtnEvent(){
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchDataEvent();
			}
		});
		exitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	/**
	 * 纷发事件
	 */
	public void dispatchDataEvent(){
		if(dataEventManager!=null){
			dcc.setName(nameText.getText());
			dcc.setUserName(userNameText.getText());
			dcc.setPassword(pwdText.getText());
			dcc.setUrl(urlText.getText());
			dcc.setDriverClassName(driverClassNameText.getText());
			dataEventManager.fireDataEvent(dcc);
		}
	}
	
	public DataEventManager getDataEventManager() {
		return dataEventManager;
	}

	public void setDataEventManager(DataEventManager dataEventManager) {
		this.dataEventManager = dataEventManager;
	}
	
	public void init(){
		this.setTitle("连接属性信息");
		this.setIconImage(CreateDataUtil.getImage("../images/download.png"));
		okBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
		exitBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
		southPanel.add(okBtn);
		southPanel.add(exitBtn);
		
		JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel jp3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel jp4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel jp5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		nameLabel.setMaximumSize(new Dimension(125,20)); 
		nameLabel.setMinimumSize(new Dimension(125,20)); 
		nameLabel.setPreferredSize(new Dimension(125,20));
		
		userNameLabel.setMaximumSize(new Dimension(125,20)); 
		userNameLabel.setMinimumSize(new Dimension(125,20)); 
		userNameLabel.setPreferredSize(new Dimension(125,20));
		
		pwdLabel.setMaximumSize(new Dimension(125,20)); 
		pwdLabel.setMinimumSize(new Dimension(125,20)); 
		pwdLabel.setPreferredSize(new Dimension(125,20));
		
		driverClassNameLabel.setMaximumSize(new Dimension(125,20)); 
		driverClassNameLabel.setMinimumSize(new Dimension(125,20)); 
		driverClassNameLabel.setPreferredSize(new Dimension(125,20));
		
		urlLabel.setMaximumSize(new Dimension(125,20)); 
		urlLabel.setMinimumSize(new Dimension(125,20)); 
		urlLabel.setPreferredSize(new Dimension(125,20));
		
		
		jp1.add(nameLabel);
		jp1.add(nameText);
		jp2.add(userNameLabel);
		jp2.add(userNameText);
		jp3.add(pwdLabel);
		jp3.add(pwdText);
		jp4.add(driverClassNameLabel);
		jp4.add(driverClassNameText);
		jp5.add(urlLabel);
		jp5.add(urlText);
		centerPanel.add(jp1);
		centerPanel.add(jp2);
		centerPanel.add(jp3);
		centerPanel.add(jp4);
		centerPanel.add(jp5);
		this.add(centerPanel,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);
		if(dcc!=null){
			nameText.setText(dcc.getName());
			userNameText.setText(dcc.getUserName());
			pwdText.setText(dcc.getPassword());
			urlText.setText(dcc.getUrl());
			driverClassNameText.setText(dcc.getDriverClassName());
		}else{
			dcc = new DataConnectConfigVO();
			dcc.setStatus("add");
		}
	}
}
