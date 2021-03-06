/*@文件名: TableConfigPanel.java  @创建人: 邢健   @创建日期: 2011-10-24 下午02:44:13*/
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
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import com.promise.cn.model.JTableComboBoxEditor;
import com.promise.cn.model.TableConfigVOModel;
import com.promise.cn.service.CreateDataService;
import com.promise.cn.util.CreateDataUtil;
import com.promise.cn.vo.PolicyVO;
import com.promise.cn.vo.TableConfigVO;

/**   
 * @类名: TableConfigPanel.java 
 * @包名: com.promise.cn.main 
 * @描述: 表格配置页面 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-24 下午02:44:13 
 * @版本 V1.0   
 */
@SuppressWarnings("all")
public class TableConfigPanel extends JPanel {

	public JTable jtable = null;
	private TableConfigVOModel dtm = null;
	private String[] tableHeader = {"字段名","类型","策略名称"};
	private JScrollPane jScrollPane = new JScrollPane();
	private List<TableConfigVO> tableConfigVOList = new ArrayList<TableConfigVO>();
	private JPanel jpanelSouth,jpanelCenter,jpanelCenter_South;
	private JButton okBtn,addBtn,deleBtn,selectPathBtn;
	private JCheckBox checkBox;
	private JTextField sqlTextPathTextField;
	private JFileChooser jfc=new JFileChooser();//文件选择器 
	public CreateDataService cds;
	public CreateDataMainApplication cdma;
	public List<PolicyVO> policyList = new ArrayList<PolicyVO>();
	
	public TableConfigPanel(CreateDataMainApplication cdma,CreateDataService cds){
		this.cdma = cdma;
		this.cds = cds;
		this.policyList = cdma.policyList;
		initPanel();
	}
	
	/**
	 * 初始化面板
	 */
	public void initPanel(){
		this.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
		this.setLayout(new BorderLayout());
		this.add(getCenterJPanel(), BorderLayout.CENTER);
		this.add(getJPanelSouth(),BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
	/**
	 * 获取中间面板
	 * @return
	 */
	public JPanel getCenterJPanel(){
		if(jpanelCenter == null){
			jpanelCenter = new JPanel();
			jpanelCenter.setLayout(new BorderLayout());
			jpanelCenter.setBorder(new TitledBorder(null,"表格配置",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			dtm = new TableConfigVOModel(tableConfigVOList,tableHeader);
			jtable = new JTable(dtm);
			jtable.setRowHeight(25);  
	        //设置表格内部字体
			jtable.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
	        //设置表头字体
			jtable.getTableHeader().setFont(CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12));
	        //设置选择模式,使其能选择一行或多行
			jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        //设置列不可拖动
			jtable.getTableHeader().setReorderingAllowed(false);
			jtable.getColumnModel().getColumn(2).setCellEditor(new JTableComboBoxEditor(getNewPolicyList()));
			jScrollPane.setViewportView(jtable);
			jpanelCenter.add(jScrollPane,BorderLayout.CENTER);
			jpanelCenter.add(getJpanelCenter_South(),BorderLayout.SOUTH);
		}
		return jpanelCenter;
	}
	
	
	public void updateJTableColumn(){
		jtable.getColumnModel().getColumn(2).setCellEditor(new JTableComboBoxEditor(getNewPolicyList()));
		jtable.updateUI();
	}
	
	/**
	 * 获取最新策略
	 */
	public String[] getNewPolicyList(){
		List<PolicyVO> listTemp = cds.getAllPolicyVO();
		String[] retString = new String[listTemp.size()];
		for(int i=0;i<listTemp.size();i++){
			retString[i]=listTemp.get(i).getName();
		}
		return retString;
	}
	
	
	/**
	 *弹出路径窗口 
	 */
	public void showJSCDialog(){
		int intRetVal = jfc.showSaveDialog(this);
		 if(intRetVal == JFileChooser.APPROVE_OPTION){ 
		    sqlTextPathTextField.setText(jfc.getSelectedFile().getPath()); 
		 } 
	}
	
	/**
	 * 开始生成sql语句并执行
	 */
	public void okBtnClickHandle(){
		if(checkBox.isSelected()){
			if(sqlTextPathTextField.getText().trim().equals("")){
				JOptionPane.showMessageDialog(this, "请选择sql语句保存路径文件！");
			}else{
				cdma.createInsertSqlByList(dtm.tcVOList, sqlTextPathTextField.getText(),policyList);
			}
		}else{
			cdma.createInsertSqlByList(dtm.tcVOList, sqlTextPathTextField.getText(),policyList);
		}
	}
	
	/**
	 * 按钮面板
	 */
	public JPanel getJPanelSouth(){
		if(jpanelSouth==null){
			FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);
			jpanelSouth = new JPanel();
			jpanelSouth.setBorder(new TitledBorder(null,"提交配置",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			jpanelSouth.setLayout(flowLayout);
			selectPathBtn = new JButton("选择文件");
			selectPathBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			selectPathBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showJSCDialog();
				}
			});
			okBtn = new JButton("生成SQL文件");
			okBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					okBtnClickHandle();
				}
			});
			checkBox = new JCheckBox();
			checkBox.setSelected(true);
			checkBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					sqlTextPathTextField.setEditable(checkBox.isSelected());
					selectPathBtn.setEnabled(checkBox.isSelected());
				}
			});
			sqlTextPathTextField = new JTextField(30);
			flowLayout.setHgap(10);
			jpanelSouth.add(checkBox);
			jpanelSouth.add(sqlTextPathTextField);
			jpanelSouth.add(selectPathBtn);
			jpanelSouth.add(okBtn);
		}
		return jpanelSouth;
	}
	
	/**
	 * 返回按钮面板
	 */
	public JPanel getJpanelCenter_South(){
		if(jpanelCenter_South == null){
			jpanelCenter_South = new JPanel();
			jpanelCenter_South.setLayout(new FlowLayout(FlowLayout.CENTER));
			addBtn = new JButton("增加一行");
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TableConfigVO tcvTemp = new TableConfigVO();
					tcvTemp.setPolicyName(policyList.get(0).getName());
					tableConfigVOList.add(tcvTemp);
					jtable.updateUI();
				}
			});
			deleBtn = new JButton("删除一行");
			deleBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(jtable.getSelectedRow()!=-1){
						dtm.deleteSelectRow(jtable.getSelectedRow());
						if(dtm.tcVOList.size()>0){
							jtable.addRowSelectionInterval(0, 0);
						}
						jtable.updateUI();
					}
				}
			});
			addBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			deleBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			jpanelCenter_South.add(addBtn);
			jpanelCenter_South.add(deleBtn);
		}
		return jpanelCenter_South;
	}
}
