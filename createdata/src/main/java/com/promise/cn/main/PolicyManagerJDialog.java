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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import com.promise.cn.customcomponent.CustomTable;
import com.promise.cn.model.PolicyManagerTableModel;
import com.promise.cn.service.CreateDataService;
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
	public CustomTable jtable = null;
	private JButton addBtn,deleteBtn,editBtn,saveBtn;
	private String[] jtableHeader = {"名称","类型"};
	public String[] policyType = new String[3];
	private PolicyManagerTableModel pmtm = null;
	public List<PolicyVO> policyList = new ArrayList<PolicyVO>();
	public CreateDataService cds = null;
	
	
	public PolicyManagerJDialog(CreateDataService cds){
		this.cds = cds;
		init();
		this.setIconImage(CreateDataUtil.getImage("../images/tubiao.png"));
		this.setSize(600, 400);
		this.setModal(true);
	}
	
	public void initPolicyType(){
		policyType[0] = CreateDataUtil.RANDOMDOUBLE_AB;
		policyType[1] = CreateDataUtil.RANDOMINT_AB;
		policyType[2] = CreateDataUtil.RANDOMSTRING;
	}
	
	
	/**
	 * 初始化方法
	 */
	public void init(){
		initPolicyType();
		policyList = cds.getAllPolicyVO();
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
			jtable.getTableHeader().setReorderingAllowed(false);
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(jtable);
		}
		return jScrollPane;
	}
	
	/**
	 * add policy
	 */
	public void addPolicyBtnHandle(){
		PolicyVO addPolicy = new PolicyVO();
		PolicyDialog policyDialog = new PolicyDialog(this,"增加策略",addPolicy,"add");
		policyDialog.setLocationRelativeTo(getJScrollPane());
		policyDialog.setVisible(true);
		policyDialog.setResizable(false);
	}
	
	/**
	 * edit policy
	 */
	public void editPolicyBtnHandle(){
		if(jtable.getSelectedRow()!=-1){
			PolicyVO editPolicy = pmtm.getPolicyVOByRow(jtable.getSelectedRow());
			PolicyDialog policyDialog = new PolicyDialog(this,"编辑策略",editPolicy,"edit");
			policyDialog.setLocationRelativeTo(getJScrollPane());
			policyDialog.setVisible(true);
			policyDialog.setResizable(false);
		}else{
			JOptionPane.showMessageDialog(jtable, "请选择一条策略数据！");
		}
		
	}
	
	public JPanel getJPanelSouth(){
		if(jpanelSouth==null){
			jpanelSouth = new JPanel();
			jpanelSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			addBtn = new JButton("增加策略");
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addPolicyBtnHandle();
				}
			});
			editBtn = new JButton("编辑策略");
			editBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editPolicyBtnHandle();
				}
			});
			deleteBtn = new JButton("删除策略");
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(jtable.getSelectedRow()!=-1){
						pmtm.deleteSelectRow(jtable.getSelectedRow());
						if(pmtm.policyVOList.size()>0){
							jtable.addRowSelectionInterval(0, 0);
						}
						jtable.updateUI();
					}
				}
			});
			saveBtn = new JButton("保存策略");
			saveBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean ret = cds.savePolicyVOList(policyList);
					if(ret){
						JOptionPane.showMessageDialog(jScrollPane, "策略保存成功！");
					}else{
						JOptionPane.showMessageDialog(jScrollPane, "策略保存失败！");
					}
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
