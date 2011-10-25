/*@文件名: DataSourceVOView.java  @创建人: 邢健   @创建日期: 2011-10-21 上午10:25:26*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
 * @描述: TODO(用一句话描述该文件做什么) 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-21 上午10:25:26 
 * @版本 V1.0   
 */
public class DataSourceVOView extends JDialog {
	private DataConnectConfigVO dcc;
	private JButton okBtn = new JButton("确定");
	private JButton exitBtn = new JButton("取消");
	private JPanel centerPanel = new JPanel(new GridLayout(5,2));
	private JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JLabel nameLabel = new JLabel("Name：");
	private JLabel userNameLabel = new JLabel("UserName：");
	private JLabel pwdLabel = new JLabel("PWD：");
	private JLabel driverClassNameLabel = new JLabel("DriverClassName：");
	private JLabel urlLabel = new JLabel("URL：");
	private JTextField nameText = new JTextField();
	private JTextField userNameText = new JTextField();
	private JTextField pwdText = new JTextField();
	private JTextField driverClassNameText = new JTextField();
	private JTextField urlText = new JTextField();
	public DataEventManager dataEventManager = null;

	/**
	 * 构造函数
	 */
	public DataSourceVOView() {
		init();
		initBtnEvent();
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
			DataConnectConfigVO dccv = new DataConnectConfigVO();
			dccv.setName(nameText.getText());
			dccv.setUserName(userNameText.getText());
			dccv.setPassword(pwdText.getText());
			dccv.setUrl(urlText.getText());
			dccv.setDriverClassName(driverClassNameText.getText());
			dataEventManager.fireDataEvent(dccv);
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
		centerPanel.add(nameLabel);
		centerPanel.add(nameText);
		centerPanel.add(userNameLabel);
		centerPanel.add(userNameText);
		centerPanel.add(pwdLabel);
		centerPanel.add(pwdText);
		centerPanel.add(driverClassNameLabel);
		centerPanel.add(driverClassNameText);
		centerPanel.add(urlLabel);
		centerPanel.add(urlText);
		this.add(centerPanel,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);
		if(dcc!=null){
		}else{
			dcc = new DataConnectConfigVO();
			dcc.setStatus("add");
		}
	}
}
