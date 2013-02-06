/** @文件名: MainUI.java @创建人：邢健  @创建日期： 2013-2-5 下午1:26:21 */

package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import util.DataSourceType;
import util.ExportDataUtil;
import bean.ADPlanB;
import dao.ExportDataDao;
import dao.impl.ExportDataDaoImpl;

/**   
 * @类名: MainUI.java 
 * @包名: ui 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-2-5 下午1:26:21 
 * @版本: V1.0   
 */
public class MainUI {

	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	private JLabel userNameLabel = new JLabel(" 用户:");
	private JLabel pwdLabel = new JLabel(" 密码:");
	private JLabel urlLabel = new JLabel(" 路径:");
	private JLabel tableNameLabel = new JLabel(" 表名:");
	private JLabel columNameLabel = new JLabel(" 列名:");
	private JTextField userNameText = new JTextField(16);
	private JTextField pwdText = new JTextField(16);
	private JTextField tableNameText = new JTextField(16);
	private JTextField columNameText = new JTextField(16);
	private JTextField urlText = new JTextField(38);
	private JTextField exportURL = new JTextField(30);
	private JPanel northPanel = null;
	private JPanel sourthPanel = null;
	private JButton testButton = new JButton("测试连接");
	private JButton conButton = new JButton("打开连接");
	private JButton exportButton = new JButton("导出");
	private ExportDataDao edd = new ExportDataDaoImpl();
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private String[] tableHeaderStr = {"planID", "id", "title", "pubtm"};
	private ADPlanBTableModel tableModel = null;
	
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setSize(496, 500);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("SQLSERVER2008导出Blob程序(xingjian@yeah.net)");
			jFrame.setFont(ExportDataUtil.getFont("微软雅黑", Font.BOLD, 16));
			jFrame.setIconImage(ExportDataUtil.getImage("/images/download.png"));
			ExportDataUtil.setCenter(jFrame);
			initJFrame();
		}
		return jFrame;
	}
	
	private void initJFrame(){
		jFrame.add(getNorthPanel(), BorderLayout.NORTH);
		jFrame.add(getCenterPanel(), BorderLayout.CENTER);
		jFrame.add(getSourthPanel(), BorderLayout.SOUTH);
		initNorthPanelData();
	}
	
	private JScrollPane getCenterPanel(){
		if(null==jScrollPane){
			jScrollPane = new JScrollPane();
			jScrollPane.setBorder(new TitledBorder(null,"预案信息",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,ExportDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			
			jTable = new JTable();
			tableModel = new ADPlanBTableModel(null, tableHeaderStr);
			jTable = new JTable(tableModel);
			//行高 
			jTable.setRowHeight(25);  
	        //设置表格列宽
			jTable.getColumn("planID").setMaxWidth(50);
			jTable.getColumn("title").setMaxWidth(200);
			jTable.getColumn("id").setMaxWidth(120);
			jTable.getColumn("pubtm").setMaxWidth(130);
	        //设置表格内部字体
			jTable.setFont(ExportDataUtil.getFont("微软雅黑", Font.BOLD, 12));
	        //设置表头字体
			jTable.getTableHeader().setFont(ExportDataUtil.getFont("微软雅黑", Font.PLAIN, 12));
	        //设置选择模式,使其能选择一行或多行
			jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        //设置列不可拖动
			jTable.getTableHeader().setReorderingAllowed(false);
			DefaultTableCellRenderer render = new DefaultTableCellRenderer();
	        render.setHorizontalAlignment(JLabel.CENTER);
	        jTable.getColumn("planID").setCellRenderer(render);
	        jTable.getColumn("id").setCellRenderer(render);
	        jTable.getColumn("title").setCellRenderer(render);
	        jTable.getColumn("pubtm").setCellRenderer(render);
	        jScrollPane.setViewportView(jTable);
		}
		return jScrollPane;
	}
	
	private JPanel getSourthPanel(){
		if(null==sourthPanel){
			sourthPanel = new JPanel();
			sourthPanel.setBorder(new TitledBorder(null,"预案导出",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,ExportDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel jl1 = new JLabel("输出目录:");
			jp1.add(jl1);
			jp1.add(exportURL);
			jp1.add(exportButton);
			exportButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean boo = edd.exportWord(DataSourceType.SQLSERVER, urlText.getText(), userNameText.getText(), pwdText.getText(), tableNameText.getText(), columNameText.getText(),exportURL.getText());
					String message = "导出成功！";
					if(!boo){
						message = "导出失败！";
					}
					JOptionPane.showMessageDialog(jContentPane, message, "提示信息",JOptionPane.DEFAULT_OPTION);
				}
			});
			sourthPanel.add(jp1);
		}
		return sourthPanel;
	}
	
	private void initNorthPanelData(){
		userNameText.setText("floodwarn");
		pwdText.setText("floodwarn");
		urlText.setText("jdbc:sqlserver://10.10.142.135:1433;databaseName=floodwarn_xingjian");
		tableNameText.setText("AD_Plan_B");
		columNameText.setText("FILECONTENT");
		exportURL.setText("c:\\floodwarncity\\exportword\\");
	}
	
	private JPanel getNorthPanel(){
		if(null==northPanel){
			GridLayout gl = new GridLayout(4,1);
			gl.setVgap(-5);
			northPanel = new JPanel(gl);
			JPanel jp1 = new JPanel(new GridLayout(1,2));
			JPanel jp9 = new JPanel(new GridLayout(1,2));
			JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp6 = new JPanel(new FlowLayout(FlowLayout.CENTER));
			JPanel jp7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp8 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			jp6.add(testButton);
			testButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean boo = edd.testConnection(DataSourceType.SQLSERVER, urlText.getText(), userNameText.getText(), pwdText.getText());
					String message = "测试连接成功！";
					if(!boo){
						message = "测试连接失败！";
					}
					JOptionPane.showMessageDialog(jContentPane, message, "提示信息",JOptionPane.DEFAULT_OPTION);
				}
			});
			jp6.add(conButton);
			conButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					List<ADPlanB> list = edd.getAllADPlanB(DataSourceType.SQLSERVER, urlText.getText(), userNameText.getText(), pwdText.getText(),tableNameText.getText(),columNameText.getText());
					tableModel.dccList = list;
					jTable.updateUI();
				}
			});
			jp2.add(userNameLabel);
			jp2.add(userNameText);
			jp3.add(pwdLabel);
			jp3.add(pwdText);
			jp7.add(tableNameLabel);
			jp7.add(tableNameText);
			jp8.add(columNameLabel);
			jp8.add(columNameText);
			
			jp1.add(jp2);
			jp1.add(jp3);
			jp9.add(jp7);
			jp9.add(jp8);
			jp5.add(urlLabel);
			jp5.add(urlText);
			northPanel.add(jp1);
			northPanel.add(jp9);
			northPanel.add(jp5);
			northPanel.add(jp6);
			northPanel.setBorder(new TitledBorder(null,"连接信息",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,ExportDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
		}
		return northPanel;
	}
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainUI application = new MainUI();
				application.getJFrame().setVisible(true);
				application.getJFrame().setResizable(false);
			}
		});
	}

}
