/*@文件名: DataSourceManagerJDialog.java  @创建人: 邢健   @创建日期: 2011-10-20 下午04:52:21*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.promise.cn.event.DataEvent;
import com.promise.cn.event.DataEventListener;
import com.promise.cn.event.DataEventManager;
import com.promise.cn.model.DataSourceTableModel;
import com.promise.cn.service.CreateDataService;
import com.promise.cn.util.CreateDataUtil;
import com.promise.cn.vo.DataConnectConfigVO;

/**   
 * @类名: DataSourceManagerJDialog.java 
 * @包名: com.promise.cn.main 
 * @描述: 数据源管理对话框 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-20 下午04:52:21 
 * @版本 V1.0   
 */
public class DataSourceManagerJDialog extends JDialog {

	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private String[] tableHeaderStr = {"Name", "UserName", "PWD", "DriverClassName","URL"};
	private DataSourceTableModel tableModel = null;
	private JButton testConnect_JButton = null;
	private JButton saveConnect_JButton = null;
	private JButton editConnect_JButton = null;
	private JButton deleteConnect_JButton = null;
	private JButton addConnect_JButton = null;
	private JPanel south_JPanel = null;
	private DataEventManager dem = new DataEventManager();
	private List<DataConnectConfigVO> tableData = new ArrayList<DataConnectConfigVO>();
	private DataEventListener dataEventListener;
	private CreateDataService cds;
	
	/**
	 * 构造函数
	 */
	public DataSourceManagerJDialog() {
	}
	
	/**
	 * north_JPanel
	 * @return
	 */
	public JPanel getNorthJPanel(){
		if(south_JPanel == null){
			south_JPanel = new JPanel();
			south_JPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			south_JPanel.add(getTestConnectJButton());
			south_JPanel.add(getAddConnectJButton());
			south_JPanel.add(getEditConnectJButton());
			south_JPanel.add(getSaveConnectJButton());
			south_JPanel.add(getDeleteConnectJButton());
		}
		return south_JPanel;
	}
	/**
	 * 测试连接按钮
	 * @return
	 */
	public JButton getTestConnectJButton(){
		if(testConnect_JButton == null){
			testConnect_JButton = new JButton("测试连接");
			testConnect_JButton.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
		}
		return testConnect_JButton;
	}
	
	/**
	 * 保存连接按钮
	 * @return
	 */
	public JButton getSaveConnectJButton(){
		if(saveConnect_JButton == null){
			saveConnect_JButton = new JButton("保存连接");
			saveConnect_JButton.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
			saveConnect_JButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveDataConnectConifgVO();
				}
			});
		}
		return saveConnect_JButton;
	}
	/**
	 * 保存数据连接配置
	 */
	public void saveDataConnectConifgVO(){
		boolean isSave = cds.saveDataConnectVOList(tableModel.dccList);
		if(isSave){
			
		}
	}
	
	/**
	 * 测试连接按钮
	 * @return
	 */
	public JButton getDeleteConnectJButton(){
		if(deleteConnect_JButton == null){
			deleteConnect_JButton = new JButton("删除连接");
			deleteConnect_JButton.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
		}
		return deleteConnect_JButton;
	}
	
	/**
	 * 测试连接按钮
	 * @return
	 */
	public JButton getEditConnectJButton(){
		if(editConnect_JButton == null){
			editConnect_JButton = new JButton("编辑连接");
			editConnect_JButton.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
		}
		return editConnect_JButton;
	}
	
	public DataEventListener getDataEventListener(){
		if(dataEventListener == null){
			dataEventListener = new DataEventListener() {
				@Override
				public void dataEvent(DataEvent dataEvent) {
					tableModel.dccList.add((DataConnectConfigVO)dataEvent.data);
					jTable.updateUI();
				}
			};
		}
		return dataEventListener;
	}
	
	/**
	 * add连接按钮
	 * @return
	 */
	public JButton getAddConnectJButton(){
		if(addConnect_JButton == null){
			addConnect_JButton = new JButton("增加连接");
			addConnect_JButton.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
			addConnect_JButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DataSourceVOView dsvv = new DataSourceVOView();
					dsvv.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dsvv.addWindowListener(new WindowAdapter() {
						 public void windowClosed(WindowEvent e) {
							 dem.removeDataEventListener(getDataEventListener()); 
						 }
					});
					dsvv.setDataEventManager(dem);
					dem.addDataEventListener(getDataEventListener());
					dsvv.setSize(400, 200);
					dsvv.setLocationRelativeTo(getContentPane());
					dsvv.setVisible(true);
					dsvv.setResizable(false);
				}
			});
		}
		return addConnect_JButton;
	}

	/**
	 * This method initializes this
	 * 
	 */
	public void initialize() {
		this.setTitle("数据源管理");
		tableData = cds.getAllDataConnectConfigVO();
		this.setLayout(new BorderLayout());
		this.add(getJScrollPane(), BorderLayout.CENTER);
		this.add(getNorthJPanel(),BorderLayout.SOUTH);
		this.setSize(700, 400);
		this.setIconImage(CreateDataUtil.getImage("../images/tubiao.png"));
	}


	/**
	 * This method initializes jScrollPane	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes jTable	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			tableModel = new DataSourceTableModel(tableData, tableHeaderStr);
			jTable = new JTable(tableModel);
			//行高 
			jTable.setRowHeight(25);  
	        //设置表格列宽
			jTable.getColumn("Name").setMaxWidth(150);
			jTable.getColumn("UserName").setMaxWidth(100);
			jTable.getColumn("PWD").setMaxWidth(100);
	        //设置表格内部字体
			jTable.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
	        //设置表头字体
			jTable.getTableHeader().setFont(CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12));
	        //设置选择模式,使其能选择一行或多行
			jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        //设置列不可拖动
			jTable.getTableHeader().setReorderingAllowed(false);
			DefaultTableCellRenderer render = new DefaultTableCellRenderer();
	        render.setHorizontalAlignment(JLabel.CENTER);
	        jTable.getColumn("Name").setCellRenderer(render);
	        jTable.getColumn("UserName").setCellRenderer(render);
	        jTable.getColumn("PWD").setCellRenderer(render);
	        jTable.getColumn("DriverClassName").setCellRenderer(render);
	        jTable.getColumn("URL").setCellRenderer(render);
		}
		return jTable;
	}
	
	public CreateDataService getCds() {
		return cds;
	}

	public void setCds(CreateDataService cds) {
		this.cds = cds;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
