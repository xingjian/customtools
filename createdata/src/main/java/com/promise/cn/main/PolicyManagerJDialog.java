/*@文件名: PolicyManagerJDialog.java  @创建人: 邢健   @创建日期: 2011-10-26 上午08:59:27*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import com.promise.cn.customcomponent.CustomTable;
import com.promise.cn.model.JTableComboBoxEditor;
import com.promise.cn.model.PolicyManagerTableModel;
import com.promise.cn.util.CreateDataUtil;
import com.promise.cn.vo.PolicyVO;

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
	private CustomTable jtable = null;
	private JButton addBtn,deleteBtn,editBtn,saveBtn;
	private String[] jtableHeader = {"名称","类型"};
	private String[] policyType = {"randomDouble[a,b]","randomInt[a,b]","randomString"};
	private PolicyManagerTableModel pmtm = null;
	private List<PolicyVO> policyList = new ArrayList<PolicyVO>();
	
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
			pmtm = new PolicyManagerTableModel(policyList,jtableHeader);
			jtable = new CustomTable(pmtm);
			jtable.setRowHeight(25);  
	        //设置表格内部字体
			jtable.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
	        //设置表头字体
			jtable.getTableHeader().setFont(CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12));
	        //设置选择模式,使其能选择一行或多行
			jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        //设置列不可拖动
			jtable.getColumnModel().getColumn(1).setCellEditor(new JTableComboBoxEditor(policyType));
			jtable.getTableHeader().setReorderingAllowed(false);
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(jtable);
		}
		return jScrollPane;
	}
	
	
	public JPanel getJPanelSouth(){
		if(jpanelSouth==null){
			jpanelSouth = new JPanel();
			jpanelSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			addBtn = new JButton("增加策略");
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					PolicyDialog policyDialog = new PolicyDialog(policyType,"增加策略");
					policyDialog.setLocationRelativeTo(getJScrollPane());
					policyDialog.setVisible(true);
					policyDialog.setResizable(false);
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
			jpanelSouth.setBorder(new TitledBorder(null,"策略操作",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
		}
		return jpanelSouth;
	}
	
}
