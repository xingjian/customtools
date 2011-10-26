/*@文件名: PolicyManagerJDialog.java  @创建人: 邢健   @创建日期: 2011-10-26 上午08:59:27*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import com.promise.cn.util.CreateDataUtil;

/**   
 * @类名: PolicyManagerJDialog.java 
 * @包名: com.promise.cn.main 
 * @描述: 策略管理面板 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-26 上午08:59:27 
 * @版本 V1.0   
 */
public class PolicyManagerJDialog extends JDialog {

	private JScrollPane jScrollPane = null;
	private JPanel jpanelSouth = null;
	private JTable jtable = null;
	private JButton addBtn,deleteBtn,editBtn,saveBtn;
	
	public PolicyManagerJDialog(){
		init();
		this.setIconImage(CreateDataUtil.getImage("../images/tubiao.png"));
		this.setSize(600, 400);
	}
	
	/**
	 * 初始化方法
	 */
	public void init(){
		this.setTitle("策略管理");
		this.setLayout(new BorderLayout());
		this.add(getJPanelSouth(),BorderLayout.SOUTH);
		this.add(getJScrollPane(),BorderLayout.CENTER);
	}
	
	/**
	 * init jScrollPane
	 * @return
	 */
	public JScrollPane getJScrollPane(){
		if(jScrollPane==null){
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}
	
	/**
	 * init jtable
	 * @return
	 */
	public JTable getJTable(){
		if(jtable==null){
			jtable = new JTable();
		}
		return jtable;
	}
	
	public JPanel getJPanelSouth(){
		if(jpanelSouth==null){
			jpanelSouth = new JPanel();
			jpanelSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			addBtn = new JButton("增加策略");
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				}
			});
			editBtn = new JButton("编辑策略");
			editBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				}
			});
			deleteBtn = new JButton("删除策略");
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				}
			});
			saveBtn = new JButton("保存策略");
			saveBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				}
			});
			addBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
			editBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
			deleteBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
			saveBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
			jpanelSouth.add(addBtn);
			jpanelSouth.add(editBtn);
			jpanelSouth.add(deleteBtn);
			jpanelSouth.add(saveBtn);
			jpanelSouth.setBorder(new TitledBorder(null,"策略列表",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
		}
		return jpanelSouth;
	}
	
}
