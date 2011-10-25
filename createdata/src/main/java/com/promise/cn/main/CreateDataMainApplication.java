/*@文件名: CreateDataMainApplication.java  @创建人: 邢健   @创建日期: 2011-10-20 下午01:06:21*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.promise.cn.service.CreateDataService;
import com.promise.cn.service.impl.CreateDataServiceImpl;
import com.promise.cn.util.CreateDataUtil;
import com.promise.cn.vo.DataConnectConfigVO;
import com.promise.cn.vo.TableConfigVO;

/**   
 * @类名: CreateDataMainApplication.java 
 * @包名: com.promise.cn.main 
 * @描述: 模拟数据主程序 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-20 下午01:06:21 
 * @版本 V1.0   
 */
public class CreateDataMainApplication {

	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu menu1 = null;
	private JMenu menu2 = null;
	private JMenu menu3 = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem cutMenuItem = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem pasteMenuItem = null;
	private JMenuItem connectManagerItem = null;
	private JDialog aboutDialog = null;
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	
	private DataConnectPanel dcp;
	private DataSourceManagerJDialog dmd;
	private List<DataConnectConfigVO> tableData = new ArrayList<DataConnectConfigVO>();
	private CreateDataService cds = new CreateDataServiceImpl();
	private TableConfigPanel tcp = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CreateDataMainApplication application = new CreateDataMainApplication();
				application.getJFrame().setVisible(true);
				application.getJFrame().setResizable(false);
			}
		});
	}

	/**
	 * This method initializes jFrame
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		initData();
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(1000, 600);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("模拟数据程序");
			jFrame.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 16));
			CreateDataUtil.setCenter(jFrame);
			jFrame.setIconImage(CreateDataUtil.getImage("../images/home.gif"));
			initJFrame();
		}
		return jFrame;
	}

	private void initData(){
		tableData = cds.getAllDataConnectConfigVO();
	}
	
	/**
	 * 初始化Jframe
	 */
	private void initJFrame(){
		jFrame.add(getDataConnectPanel(), BorderLayout.NORTH);
		jFrame.add(getTableConfigPanel(), BorderLayout.CENTER);
	}
	
	public TableConfigPanel getTableConfigPanel(){
		if(tcp==null){
			tcp = new TableConfigPanel(this);
			tcp.cds = cds;
		}
		return tcp;
	}
	
	/**
	 * 创建sql
	 * @param list
	 * @param path
	 */
	public void createSqlByList(List<TableConfigVO> list,String path){
		String tableName = dcp.jTextFieldTableName.getText();
		int count  = Integer.parseInt(dcp.jTextFieldDataCount.getText());
		cds.createSqlByList(list, path,tableName,count);
	}
	
	/**
	 * This method initializes jContentPane
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
	 * This method initializes jJMenuBar	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getMenu1());
			jJMenuBar.add(getMenu2());
			jJMenuBar.add(getMenu3());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMenu1() {
		if (menu1 == null) {
			menu1 = new JMenu();
			menu1.setText("数据连接");
			menu1.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			menu1.add(getConnectManagerItem());
			menu1.add(getExitMenuItem());
		}
		return menu1;
	}

	/**
	 * This method initializes jMenu	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMenu2() {
		if (menu2 == null) {
			menu2 = new JMenu();
			menu2.setText("窗口");
			menu2.add(getCutMenuItem());
			menu2.add(getCopyMenuItem());
			menu2.add(getPasteMenuItem());
		}
		return menu2;
	}

	/**
	 * This method initializes jMenu	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMenu3() {
		if (menu3 == null) {
			menu3 = new JMenu();
			menu3.setText("系统帮助");
			menu3.add(getAboutMenuItem());
		}
		return menu3;
	}

	/**
	 * This method initializes jMenuItem	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("退出");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("关于系统");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();
					aboutDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutDialog	
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(getJFrame(), true);
			aboutDialog.setTitle("About");
			aboutDialog.setContentPane(getAboutContentPane());
		}
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new BorderLayout());
			aboutContentPane.add(getAboutVersionLabel(), BorderLayout.CENTER);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutVersionLabel	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getAboutVersionLabel() {
		if (aboutVersionLabel == null) {
			aboutVersionLabel = new JLabel();
			aboutVersionLabel.setText("Version 1.0");
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}

	/**
	 * This method initializes jMenuItem	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setText("Cut");
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					Event.CTRL_MASK, true));
		}
		return cutMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setText("Copy");
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					Event.CTRL_MASK, true));
		}
		return copyMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));
		}
		return pasteMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getConnectManagerItem() {
		if (connectManagerItem == null) {
			connectManagerItem = new JMenuItem();
			connectManagerItem.setText("连接管理");
			connectManagerItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DataSourceManagerJDialog dmdTemp = getDataSourceManagerJDialog();
					dmdTemp.setLocationRelativeTo(getJFrame());
					dmdTemp.setVisible(true);
					dmdTemp.setResizable(false);
				}
			});
		}
		return connectManagerItem;
	}
	
	private DataConnectPanel getDataConnectPanel(){
		if(dcp == null){
			dcp = new DataConnectPanel(tableData);
		}
		return dcp;
	}
	
	private DataSourceManagerJDialog getDataSourceManagerJDialog(){
		if(dmd == null){
			dmd = new DataSourceManagerJDialog();
			dmd.setCds(cds);
			dmd.tableData = tableData;
			dmd.initialize();
		}
		return dmd;
	}

}
