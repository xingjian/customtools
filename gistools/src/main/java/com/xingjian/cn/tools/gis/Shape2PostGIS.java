/**@Title: Shape2PostGIS.java @author promisePB xingjian@yeah.net @date 2010-12-6 下午06:15:15 */

package com.xingjian.cn.tools.gis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;

/**   
 * @Title: Shape2PostGIS.java 
 * @Package com.xingjian.cn.tools.gis 
 * @Description: shape文件数据转换到postgis 
 * @author promisePB xingjian@yeah.net   
 * @date 2010-12-6 下午06:15:15 
 * @version V1.0   
 */

public class Shape2PostGIS extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel centerPane = null;
	private JPanel southPane = null;
	private JPanel northPane = null;
	private JTable jtable;
	private JButton clearBtn,changeBtn,setBinPathBtn,setTempPathBtn,addBtn,deleBtn;
	private JLabel binPathLabel,tempPathLabel,dataSetLabel,dataUserLabel;
	private JTextField binPathText,tempPathText,dataSetText,dataUserText;
	String[] codes = { "GBK", "UTF-8", "GB2312"};
	private JFileChooser chooser,chooserShap;
	private List<String> sqlFileName = new ArrayList<String>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Shape2PostGIS thisClass = new Shape2PostGIS();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public Shape2PostGIS() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Shape2PostGIS主程序");
		this.setLayout(new BorderLayout());
		this.add(getCenterPane(),BorderLayout.CENTER);
		this.add(getNorhtPane(),BorderLayout.NORTH);
		this.add(getSouthPane(),BorderLayout.SOUTH);
		this.setSize(500, 600);
		this.setResizable(false);
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}
	
	/**
	 * 获取中间面板
	 * @return
	 */
	private JPanel getCenterPane(){
		if(centerPane == null){
			centerPane = new JPanel();
			centerPane.setLayout(new BorderLayout());
			centerPane.setBorder(new TitledBorder("注意表名不要用中文"));
			jtable = new JTable(new TableModel2Shape());
			jtable.getTableHeader().setReorderingAllowed(false);
	        JComboBox com = new JComboBox(codes);
	        TableColumnModel tcm = jtable.getColumnModel();
	        tcm.getColumn(0).setPreferredWidth(30);
	        tcm.getColumn(1).setPreferredWidth(150);
	        tcm.getColumn(2).setPreferredWidth(30);
	        tcm.getColumn(3).setCellEditor(new DefaultCellEditor(com));
			
			JScrollPane jsp = new JScrollPane(jtable);
			
			deleBtn = new JButton("删除");
			deleBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					deleData();
					jtable.updateUI();
				}
			});
			addBtn = new JButton("添加");
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addData();
					jtable.updateUI();
				}
			});
			clearBtn = new JButton("清空");
			clearBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					clearData();
					jtable.updateUI();
				}
			});
			changeBtn = new JButton("运行");
			changeBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					runBatCommands();
				}
			});
			FlowLayout fl = new FlowLayout(FlowLayout.CENTER);
			fl.setHgap(5);
			JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			tempPanel.add(addBtn);
			tempPanel.add(deleBtn);
			tempPanel.add(clearBtn);
			tempPanel.add(changeBtn);
			centerPane.add(jsp,BorderLayout.CENTER);
			centerPane.add(tempPanel,BorderLayout.SOUTH);
		}
		return centerPane;
	}
	
	/**
	 * 开始执行批处理
	 */
	public void runBatCommands(){
		StringBuffer sb=new StringBuffer();
		String binPathStr = binPathText.getText();
		Process process = null;
		TableModel2Shape tms = (TableModel2Shape)jtable.getModel();
		sqlFileName.clear();
		String pathOld = (this.getClass().getClassLoader().getResource("").getPath()).substring(1);
		String path = pathOld+"shape2postgis.bat";
		for(int i=0;i<tms.getRowCount();i++){
			if(tms.getValueAt(i, 0).toString()=="true"){
				String arg1 = "\""+binPathStr+"\\shp2pgsql.exe"+"\"";
				String arg2 = "\""+tms.getValueAt(i, 3)+"\"";
				String arg3 = tms.getValueAt(i, 1).toString();
				String arg4 = tms.getValueAt(i, 2).toString();
				String arg5 = tempPathText.getText()+"\\"+tms.getValueAt(i, 2)+".sql";
			try {
				process = Runtime.getRuntime().exec(path+" "+arg1+" "+arg2+" "+arg3+" "+arg4+" "+arg5);
				sqlFileName.add(arg5);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			};
		}
		runSqlScript();
	}
	
	/**
	 * 执行sql脚本
	 */
	public void runSqlScript(){
		Process process = null;
		String binPathStr = binPathText.getText();
		String pathOld = (this.getClass().getClassLoader().getResource("").getPath()).substring(1);
		String path = pathOld+"runpostgresql.bat";
		for(int i=0;i<sqlFileName.size();i++){
			String arg1 = "\""+binPathStr+"\\pgsql.exe"+"\"";
			String arg2 = dataSetText.getText();
			String arg3 = sqlFileName.get(i);
			String arg4 = dataUserText.getText();
			try {
				//09 16 18 26 30 31 14 process = Runtime.getRuntime().exec(path+" "+arg1+" "+arg2+" "+arg3+" "+arg4);
			process = Runtime.getRuntime().exec("");	
			} catch (Exception e) {
					e.printStackTrace();
			}
				
		};
	}
	
	/**
	 * 表格增加一条数据
	 */
	public void addData(){
		chooserShap = new JFileChooser();
		chooserShap.setDialogTitle("请选择shpe文件");
		chooserShap.setMultiSelectionEnabled(true);
		chooserShap.addChoosableFileFilter(new FileNameExtensionFilter("*.shp","shp"));
		int result = chooserShap.showOpenDialog(this);
		chooserShap.setVisible(true);
		File[] selectedFiles = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			TableModel2Shape tms = (TableModel2Shape)jtable.getModel();
		    selectedFiles = chooserShap.getSelectedFiles();
		    if(selectedFiles!=null){
		    	for(int i=0;i<selectedFiles.length;i++){
		    		String sourcePath = selectedFiles[i].getAbsolutePath();
		    		int end = selectedFiles[i].getName().indexOf(".");
		    		String tableName = selectedFiles[i].getName().substring(0, end);
		    		tms.addRow(sourcePath, true, "GBK", tableName);
			    }
		    }
		} 
		
	}
	
	/**
	 * 表格增加一条数据
	 */
	public void deleData(){
		TableModel2Shape tms = (TableModel2Shape)jtable.getModel();
		for(int i=0;i<tms.getRowCount();i++){
			if(tms.getValueAt(i, 0).toString()=="true"){
				tms.removeRow(i);
				i--;
			};
		}
	}
	
	/**
	 * 清空
	 */
	public void clearData(){
		TableModel2Shape tms = (TableModel2Shape)jtable.getModel();
		for(int i=0;i<tms.getRowCount();i++){
				tms.removeRow(i);
				i--;
		}
	}
	
	/**
	 * 获取southPane面板
	 * @return
	 */
	private JPanel getSouthPane(){
		if(southPane == null){
			southPane = new JPanel();
			southPane.setBorder(new TitledBorder("注意事项"));
			JLabel noticeLabel = new JLabel("使用该程序，请确认您的机器已经安装了Postgresql和PostGIS!");
			southPane.add(noticeLabel);
		}
		return southPane;
	}
	
	/**
	 * setBtn action
	 */
	public void getSetBinPath(){
		chooser = new JFileChooser(); 
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			binPathText.setText( chooser.getSelectedFile().getPath()) ;
		    chooser.setVisible(false);
		}
	}
	
	/**
	 * tempBtn action
	 */
	public void getTempPath(){
		chooser = new JFileChooser(); 
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			tempPathText.setText( chooser.getSelectedFile().getPath()) ;
		    chooser.setVisible(false);
		}
	}
	
	/**
	 * 获取northPane面板
	 * @return
	 */
	private JPanel getNorhtPane(){
		if(northPane == null){
			northPane = new JPanel();
			northPane.setBorder(new TitledBorder("路径设置和Bin程序设置"));
			northPane.setLayout(new GridLayout(3,1));
			
			JPanel jp1 = new JPanel();
			JPanel jp2 = new JPanel();
			JPanel jp3 = new JPanel();
			jp1.setLayout(new FlowLayout(FlowLayout.LEFT));
			jp2.setLayout(new FlowLayout(FlowLayout.LEFT));
			jp3.setLayout(new FlowLayout(FlowLayout.LEFT));
			
			binPathLabel = new JLabel("Bin   目录:");
			binPathText = new JTextField(30);
			binPathText.setText("D:\\Program Files\\PostgreSQL\\9.0\\bin");
			binPathText.setEditable(false);
			setBinPathBtn = new JButton("设置");
			setBinPathBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getSetBinPath();
				}
			});
			jp1.add(binPathLabel);
			jp1.add(binPathText);
			jp1.add(setBinPathBtn);
			tempPathLabel = new JLabel("临时目录:");
			tempPathText = new JTextField(30);
			tempPathText.setEditable(false);
			setTempPathBtn = new JButton("设置");
			setTempPathBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getTempPath();
				}
			});
			jp2.add(tempPathLabel);
			jp2.add(tempPathText);
			jp2.add(setTempPathBtn);
			dataSetLabel = new JLabel("数据库:");
			dataUserLabel = new JLabel("用户名:");
			dataSetText = new JTextField(10);
			dataUserText = new JTextField(10);
			jp3.add(dataSetLabel);
			jp3.add(dataSetText);
			jp3.add(dataUserLabel);
			jp3.add(dataUserText);
			northPane.add(jp1);
			northPane.add(jp2);
			northPane.add(jp3);
			
		}
		return northPane;
	}
	

}
