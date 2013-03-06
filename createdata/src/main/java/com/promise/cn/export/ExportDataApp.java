/** @文件名: ExportDataApp.java @创建人：邢健  @创建日期： 2013-3-5 上午10:12:01 */

package com.promise.cn.export;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.promise.cn.model.JTableComboBoxEditor;
import com.promise.cn.model.TableConfigVOModel;
import com.promise.cn.model.TableVOModel;
import com.promise.cn.service.CreateDataService;
import com.promise.cn.service.ExportDataService;
import com.promise.cn.service.impl.ExportDataServiceImpl;
import com.promise.cn.util.CreateDataUtil;
import com.promise.cn.vo.DataConnectConfigVO;
import com.promise.cn.vo.TableVO;

/**   
 * @类名: ExportDataApp.java 
 * @包名: com.promise.cn.appcompent 
 * @描述: 导出 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-3-5 上午10:12:01 
 * @版本: V1.0   
 */
@SuppressWarnings("all")
public class ExportDataApp extends JPanel {

	private JPanel northPanel,centerPanel,southPanel;
	private JComboBox jComboBox;
	public DefaultComboBoxModel dcbm = null;
	public List<DataConnectConfigVO> tableData = null;
	private JLabel label1 = new JLabel("数据源:");
	private JCheckBox jcb = new JCheckBox("单表");
	private JTextField jtf = new JTextField(15);
	private JButton jbLoadTable = new JButton("加载表信息");
	private JButton testConnect_JButton = null;
	private CreateDataService cds;
	private JTable jtable = new JTable();
	private TableVOModel tvo = null;
	private String[] tableHeader = {"序号","表名","操作"};
	private JScrollPane jScrollPane = new JScrollPane();
	private List<TableVO> tableVOList = new ArrayList<TableVO>();
	private TableVOModel dtm;
	private JCheckBox jcbExport = new JCheckBox("多文件");
	private JButton jbtExport = new JButton("导出Excel");
	private JFileChooser jfc=new JFileChooser();//文件选择器 
	private JTextField pathTextField = new JTextField(20);;
	private JButton selectPathBtn = new JButton("Excel路径");
	private JButton deleBtn = new JButton("删除一行");
	private ExportDataService eds = new ExportDataServiceImpl();
	
	public ExportDataApp(List<DataConnectConfigVO> tableData,CreateDataService cds){
		this.tableData = tableData;
		this.cds = cds;
		this.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
		this.setLayout(new BorderLayout());
		initPanel();
		this.add(northPanel,BorderLayout.NORTH);
		this.add(centerPanel,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);
	}
	
	public void initPanel(){
		northPanel = new JPanel();
		centerPanel = new JPanel(new BorderLayout());
		southPanel = new JPanel();
		initNorthPanel();
		initCenterPanel();
		initSouthPanel();
	}
	
	public void initSouthPanel(){
		southPanel.setBorder(new TitledBorder(null,"导出设置(多文件表示每个表会生成一个文件)",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
		southPanel.setBackground(Color.white);
		southPanel.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		flowLayout.setHgap(22);
		flowLayout.setVgap(10);
		southPanel.setLayout(flowLayout);
		southPanel.add(jcbExport);
		JLabel jl = new JLabel("路径:");
		southPanel.add(jl);
		southPanel.add(pathTextField);
		selectPathBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
		selectPathBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showJSCDialog();
			}
		});
		southPanel.add(selectPathBtn);
		southPanel.add(jbtExport);
	}
	
	public void initNorthPanel(){
		northPanel.setBorder(new TitledBorder(null,"数据源和表(单表表示只加载文本框指定的表)",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
		northPanel.setBackground(Color.white);
		northPanel.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
		label1.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		flowLayout.setHgap(22);
		flowLayout.setVgap(10);
		northPanel.setLayout(flowLayout);
		northPanel.add(label1);
		northPanel.add(getJComboBox());
		initJCB();
		northPanel.add(jcb);
		northPanel.add(jtf);
		jbLoadTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DataConnectConfigVO ccvTemp = (DataConnectConfigVO)jComboBox.getSelectedItem();
				eds.getTableVOListByDCC(ccvTemp);
			}
		});
		
		northPanel.add(jbLoadTable);
		northPanel.add(getTestConnectJButton());
	}
	
	private void initJCB(){
		jcb.setSelected(true);
		jcb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtf.setEditable(jcb.isSelected());
			}
		});
	}
	
	/**
	 *弹出路径窗口 
	 */
	public void showJSCDialog(){
		int intRetVal = jfc.showSaveDialog(this);
		 if(intRetVal == JFileChooser.APPROVE_OPTION){ 
		    pathTextField.setText(jfc.getSelectedFile().getPath()); 
		 } 
	}
	
	
	public void initCenterPanel(){
		dtm = new TableVOModel(tableVOList,tableHeader);
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
		jtable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer());
		jScrollPane.setViewportView(jtable);
		centerPanel.add(jScrollPane,BorderLayout.CENTER);
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
		FlowLayout fl = new FlowLayout(FlowLayout.CENTER);
		JPanel jpTemp = new JPanel();
		jpTemp.setLayout(fl);
		jpTemp.add(deleBtn);
		centerPanel.add(jpTemp,BorderLayout.SOUTH);
	}
	
	public JComboBox getJComboBox(){
		if(jComboBox==null){
			jComboBox = new JComboBox();
			dcbm = new DefaultComboBoxModel(tableData.toArray());
			jComboBox.setModel(dcbm);
		}
		return jComboBox;
	}
	
	/**
	 * 测试连接按钮
	 * @return
	 */
	public JButton getTestConnectJButton(){
		if(testConnect_JButton == null){
			testConnect_JButton = new JButton("测试连接");
			testConnect_JButton.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 13));
			testConnect_JButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					testConnect_JButtonClickHandle();
				}
			});
		}
		return testConnect_JButton;
	}
	
	/**
	 * 测试数据库连接
	 */
	public void testConnect_JButtonClickHandle(){
		DataConnectConfigVO ccvTemp = (DataConnectConfigVO)jComboBox.getSelectedItem();
		boolean booTemp = cds.checkDataConnectVO(ccvTemp);
		String message = "测试连接成功！";
		if(!booTemp){
			message = "测试连接失败！";
		}
		JOptionPane.showMessageDialog(this, message, "提示信息",JOptionPane.DEFAULT_OPTION);
	}
}
