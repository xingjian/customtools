/** @文件名: PatchRainDataUI.java @创建人：邢健  @创建日期： 2013-7-10 下午12:58:55 */

package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import util.DataSourceFactory;
import util.DataSourceType;
import util.ExportDataUtil;
import bean.FldSysinfoReal;
import bean.Station;

/**   
 * @类名: PatchRainDataUI.java 
 * @包名: ui 
 * @描述: 处理补报数据 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-7-10 下午12:58:55 
 * @版本: V1.0   
 */
public class PatchRainDataUI {
	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	private JLabel userNameLabel = new JLabel(" 用户:");
	private JLabel pwdLabel = new JLabel(" 密码:");
	private JLabel userNameDDBLabel = new JLabel(" 用户:");
	private JLabel pwdDDBLabel = new JLabel(" 密码:");
	private JLabel urlLabel = new JLabel(" 路径:");
	private JLabel urlTempLabel = new JLabel(" 路径:");
	private JLabel tableNameLabel = new JLabel(" 表名:");
	private JLabel tableNameDDBLabel = new JLabel(" 表名:");
	private JLabel columNameLabel = new JLabel(" 时间:");
	private JLabel runTimeLabel = new JLabel(" 频率:");
	private JTextField userNameText = new JTextField(15);
	private JPasswordField pwdText = new JPasswordField(15);
	private JTextField userNameDDBText = new JTextField(15);
	private JPasswordField pwdDDBText = new JPasswordField(15);
	private JTextField tableNameText = new JTextField(15);
	private JTextField tableNameDDBText = new JTextField(15);
	private JTextField columNameText = new JTextField(15);
	private JTextField runTimeText = new JTextField(15);
	private JTextField urlText = new JTextField(55);
	private JTextField urlTempText = new JTextField(55);
	private JPanel northPanel = null;
	private JButton conButton = new JButton("开始运行");
	private JButton clearButton = new JButton("清空控制台");
	private JScrollPane jScrollPane = null;
	private JTextArea jTextArea = null;
	private JLabel logLabel = new JLabel(" 日志:");
	private JTextField logDirText = new JTextField(15);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private List<Station> listStation = null;
	private Connection connection;
	private Connection connectionTemp;
	private int count = 1;
	private boolean flag = true;
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setSize(670, 600);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("东华山洪-处理雨情补报(xingjian@yeah.net)");
			jFrame.setFont(ExportDataUtil.getFont("微软雅黑", Font.BOLD, 16));
			jFrame.setIconImage(ExportDataUtil.getImage("/images/rain.png"));
			ExportDataUtil.setCenter(jFrame);
			initJFrame();
		}
		return jFrame;
	}
	
	private void initJFrame(){
		jFrame.add(getNorthPanel(), BorderLayout.NORTH);
		jFrame.add(getCenterPanel(), BorderLayout.CENTER);
		initNorthPanelData();
	}
	
	private JScrollPane getCenterPanel(){
		if(null==jScrollPane){
			jScrollPane = new JScrollPane();
			jScrollPane.setBorder(new TitledBorder(null,"控制台信息",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,ExportDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			jTextArea = new JTextArea();
			jTextArea.setEditable(false);
	        jScrollPane.setViewportView(jTextArea);
		}
		return jScrollPane;
	}
	
	private void initNorthPanelData(){
		userNameText.setText("floodwarn");
		userNameDDBText.setText("floodwarn");
		pwdText.setText("floodwarn");
		pwdDDBText.setText("floodwarn");
		runTimeText.setText("5");
		urlText.setText("jdbc:sqlserver://10.10.142.135:1433;databaseName=floodwarn");
		urlTempText.setText("jdbc:sqlserver://172.18.18.34:1433;databaseName=floodwarn");
		tableNameText.setText("ST_PPTN_R");
		tableNameDDBText.setText("ST_PPTN_R_TEMP");
		columNameText.setText("2880");
		logDirText.setText("c:\\floodwarn\\patchlog\\");
	}
	
	/**
	 * 处理雨量补发数据 add by xingjian 2013-07-10 AM
	 */
	public void resultPatchData(){
		setLogText("调用resultPatchData()方法"+count+"次。");
		for(Station station:listStation) {
			String sstcd = station.getCode();
			FldSysinfoReal fldTemp = getLastUpataTime(sstcd);
			if(fldTemp!=null){//不等于空可以补发，如果为空不用补发
			   Date eDate = fldTemp.getTm();
			   long time = eDate.getTime()- Integer.parseInt(columNameText.getText())*60*1000;
			   Date sDate = new Date(time);
			   String enddate = sdf.format(eDate);
			   String startdate = sdf.format(sDate);
			   String sql = "select * from "+tableNameDDBText.getText()+" t where stcd='"+sstcd+"' and convert(varchar(100),t.tm,120)>'"+startdate+"' and convert(varchar(100),t.tm,120)<'"+enddate+"' order by tm";
			   try {
				   Statement st = connectionTemp.createStatement();
				   ResultSet rs = st.executeQuery(sql);
					while(rs.next()) {
							double drp=rs.getDouble("drp");
							double intv = rs.getDouble("intv");
							double pdr = rs.getDouble("pdr");
							double dyp = rs.getDouble("dyp");
							Date tm = rs.getDate("tm");
							String wth = rs.getString("wth");
							String qStr = "select * from ST_PPTN_R where stcd='"+sstcd+"' and tm='"+sdf.format(tm)+"'";
							Statement st2 = connection.createStatement();
							ResultSet rs2 = st2.executeQuery(qStr);
							if(!rs2.next()){
								String insert = "insert into ST_PPTN_R (stcd,drp,pdr,dyp,intv,tm,wth) values('"+sstcd+"',"+drp+","+pdr+","+dyp+","+intv+",'"+sdf.format(tm)+"',"+wth+")";
								st2.execute(insert);
								setLogText(insert);
							}
					   }
				}catch (Exception e) {
					setLogText(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		count++;
	}
	
	private JPanel getNorthPanel(){
		if(null==northPanel){
			GridLayout gl = new GridLayout(3,1);
			gl.setVgap(1);
			northPanel = new JPanel(gl);
			
			JPanel jp1Top1 = new JPanel(new GridLayout(2,1));
			JPanel jp1Top2 = new JPanel(new GridLayout(2,1));
			
			
			JPanel jp1 = new JPanel(new GridLayout(1,3));
			JPanel jp1DDB = new JPanel(new GridLayout(1,3));
			JPanel jp9 = new JPanel(new GridLayout(2,2));
			JPanel jpin = new JPanel(new GridLayout(1,3));
			JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp2DDB = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp3DDB = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp13 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp6 = new JPanel(new FlowLayout(FlowLayout.CENTER));
			JPanel jp7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp7DDB = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp8 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp10 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel jp12 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			jp6.add(conButton);
			conButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					connection = DataSourceFactory.getConnection(DataSourceType.SQLSERVER, urlText.getText(), userNameText.getText(), pwdText.getText());
					connectionTemp = DataSourceFactory.getConnection(DataSourceType.SQLSERVER, urlTempText.getText(), userNameDDBText.getText(), pwdDDBText.getText());
					initStation();
					Timer timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run() {
							resultPatchData();
						}
					}, 10000, Integer.parseInt(runTimeText.getText())*60*1000);
				}
			});
			jp6.add(clearButton);
			clearButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jTextArea.setText("");
				}
			});
			jp2.add(userNameLabel);
			jp2.add(userNameText);
			jp3.add(pwdLabel);
			jp3.add(pwdText);
			jp2DDB.add(userNameDDBLabel);
			jp2DDB.add(userNameDDBText);
			jp3DDB.add(pwdDDBLabel);
			jp3DDB.add(pwdDDBText);
			jp7.add(tableNameLabel);
			jp7.add(tableNameText);
			jp7DDB.add(tableNameDDBLabel);
			jp7DDB.add(tableNameDDBText);
			jp8.add(runTimeLabel);
			jp8.add(runTimeText);
			jp1.add(jp2);
			jp1.add(jp3);
			jp1.add(jp7);
			jp1DDB.add(jp2DDB);
			jp1DDB.add(jp3DDB);
			jp1DDB.add(jp7DDB);
			jp10.add(logLabel);
			jp10.add(logDirText);
			jpin.add(jp8);
			jpin.add(jp12);
			jpin.add(jp10);
			jp9.add(jpin);
			jp9.add(jp6);
			jp9.setBorder(new TitledBorder(null,"应用配置信息",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,ExportDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			
			jp12.add(columNameLabel);
			jp12.add(columNameText);
			jp5.add(urlLabel);
			jp5.add(urlText);
			jp13.add(urlTempLabel);
			jp13.add(urlTempText);
			
			jp1Top1.add(jp1);
			jp1Top1.add(jp5);
			jp1Top1.setBorder(new TitledBorder(null,"Floodwarn数据库配置信息",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,ExportDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			jp1Top2.setBorder(new TitledBorder(null,"中间表数据库配置信息",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,ExportDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			jp1Top2.add(jp1DDB);
			jp1Top2.add(jp13);
			
			northPanel.add(jp1Top1);
			northPanel.add(jp1Top2);
			northPanel.add(jp9);
		}
		return northPanel;
	}
	
	//设置日志
	public void setLogText(String s){
		if(flag){
			jTextArea.setText(sdf.format(new java.util.Date())+":"+s);
			flag = false;
		}else{
			jTextArea.setText(jTextArea.getText()+"\n"+sdf.format(new java.util.Date())+":"+s);
		}
		writeLogFile(sdf.format(new java.util.Date())+":"+s);
	}
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	//写日志文件
	public void writeLogFile(String content){
		File fileDir = new File(logDirText.getText());
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
		try {
			FileWriter writer = new FileWriter(logDirText.getText()+sdf1.format(new java.util.Date())+".txt", true); 
			writer.write(content+"\r\n"); 
			writer.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}
	
	/**
	 * 初始化测站
	 * @throws Exception
	 */
	public void initStation(){
		listStation = new ArrayList<Station>();
		try{
			String sql = "select stcd,stnm from ST_STBPRP_B t where t.sttp='PP' or t.sttp='RP' or t.sttp='ZP' order by t.stcd asc";
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if(null!=rs){
				while(rs.next()){
					String stcd = rs.getString(1);
					String stnm = rs.getString(2);
					Station station = new Station();
					station.setCode(stcd);
					station.setName(stnm);
					listStation.add(station);
				}
			}
			rs.close();
			st.close();
		}catch(Exception e){
			setLogText(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public FldSysinfoReal getLastUpataTime(String stcd){
		try{
			String sql = "select id,tableid,tm,sourceType from fld_sysinfo_real t where t.tableid='"+stcd+"' and t.sourceType='"+1+"'";
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);
			FldSysinfoReal fldSysinfoReal = null;
			if(null!=rs){
				while(rs.next()){
					fldSysinfoReal = new FldSysinfoReal();
					String id = rs.getString(1);
					String tableid = rs.getString(2);
					Date tm = rs.getDate(3);
					String sourceType = rs.getString(4);
					fldSysinfoReal.setId(id);
					fldSysinfoReal.setTableid(tableid);
					fldSysinfoReal.setTm(tm);
					fldSysinfoReal.setSourceType(sourceType);
				}
			}
			rs.close();
			st.close();
			return fldSysinfoReal;
		}catch(Exception e){
			setLogText(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PatchRainDataUI application = new PatchRainDataUI();
				application.getJFrame().setVisible(true);
				application.getJFrame().setResizable(false);
			}
		});
	}
}
