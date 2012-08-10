/** @文件名: ApplicationMainFrame.java @创建人：邢健  @创建日期： 2012-8-9 上午9:43:48 */

package com.promise.cn.googlemap.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import com.promise.cn.googlemap.service.MapImageService;
import com.promise.cn.googlemap.service.impl.MapImageServiceImpl;
import com.promise.cn.googlemap.utils.GoogleMapInitConfig;

/**   
 * @类名: ApplicationMainFrame.java 
 * @包名: com.promise.cn.googlemap.main 
 * @描述: 图形界面入口
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2012-8-9 上午9:43:48 
 * @版本: V1.0   
 */
@SuppressWarnings("all")
public class ApplicationMainFrame extends JFrame {

	private JPanel jContentPane = null;
	private	GoogleMapInitConfig gmc = new GoogleMapInitConfig();
	private MapImageServiceImpl mapImageService = new MapImageServiceImpl();
	private JLabel label_XMAX = new JLabel("XMax:");
	private JLabel label_YMAX = new JLabel("YMax:");
	private JLabel label_XMIN = new JLabel("Xmin:");
	private JLabel label_YMIN = new JLabel("Ymin:");
	private JLabel label_Z = new JLabel("Z:");
	private JTextField jtf_XMAX = new JTextField(9);
	private JTextField jtf_YMAX = new JTextField(9);
	private JTextField jtf_XMIN = new JTextField(9);
	private JTextField jtf_YMIN = new JTextField(9);
	private JTextField jtf_Z = new JTextField(4);
	private JButton jb_start = new JButton("开始");
	//构造函数
	public ApplicationMainFrame(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setContentPane(getJContentPane());
		this.setTitle("地图图片下载-1.0   xingjian@yeah.net");
		this.setFont(getFont("微软雅黑", Font.BOLD, 16));
		setCenter(this);
		this.setIconImage(getImage("../image/home.gif"));
		initJFrame();
	}
	
	//加载配置文件
	public void loadConfig(){
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("googlemap/googlemapconfig.properties");    
	    Properties p = new Properties(); 
	    try {    
	       p.load(inputStream);    
	    } catch (IOException e1) {    
	       e1.printStackTrace();    
	    }
	    gmc.setMapType(p.getProperty("mapType"));
	    gmc.setDownLoadPath(p.getProperty("workPath"));
	    gmc.setLogPath(p.getProperty("workPath"));
	    gmc.setErrorPath(p.getProperty("workPath"));
	    ArrayList<String> listServer = new ArrayList<String>();
	    listServer.add(0, p.getProperty("server1"));
	    listServer.add(1, p.getProperty("server2"));
	    listServer.add(2, p.getProperty("server3"));
	    listServer.add(3, p.getProperty("server4"));
	    gmc.setGoogleServerList(listServer);
	    //读取代理上网(syj crawler.proxy)
		FileInputStream fis;
		try {
			fis = new FileInputStream(this.getClass().getResource("./../../../../../googlemap/proxy/spys.ru-20120809.proxy").getPath());
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			ArrayList<String> proxyList = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				proxyList.add(line);
			}
			br.close();
			isr.close();
			fis.close();
			gmc.setProxyNetWorkList(proxyList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mapImageService.setGoogleMapInitConfig(gmc);
		//40.136891,115.827484 39.610978,116.908264 北京坐标
		jtf_XMAX.setText("116.908264");
		jtf_YMAX.setText("40.136891");
		jtf_XMIN.setText("115.827484");
		jtf_YMIN.setText("39.610978");
		jtf_Z.setText("10");
	}
	
	//初始化方法
	public void initJFrame(){
		loadConfig();
		this.add(getNorthPanel(), BorderLayout.NORTH);
	}
	
	//初始化北部面板
	public JPanel getNorthPanel(){
		JPanel northPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		flowLayout.setHgap(10);
		flowLayout.setVgap(10);
		northPanel.setLayout(flowLayout);
		northPanel.setBorder(new TitledBorder(null,"初始值",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
		northPanel.add(label_XMAX);
		northPanel.add(jtf_XMAX);
		northPanel.add(label_YMAX);
		northPanel.add(jtf_YMAX);
		northPanel.add(label_XMIN);
		northPanel.add(jtf_XMIN);
		northPanel.add(label_YMIN);
		northPanel.add(jtf_YMIN);
		northPanel.add(label_Z);
		northPanel.add(jtf_Z);
		northPanel.add(jb_start);
		jb_start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double xmax = Double.parseDouble(jtf_XMAX.getText());
				double ymax = Double.parseDouble(jtf_YMAX.getText());
				double xmin = Double.parseDouble(jtf_XMIN.getText());
				double ymin = Double.parseDouble(jtf_YMIN.getText());
				int z = Integer.parseInt(jtf_Z.getText());
				mapImageService.getGoogleMapImageURLByLgtdAndLttd(xmax, ymax, xmin, ymin, z);
			}
		});
		return northPanel;
	}
	
	//中心面板
	public JPanel getCenterPanel(){
		JPanel centerPanel = new JPanel();
		return centerPanel;
	}
	
	//组件居中
	public void setCenter(Component component){
		Toolkit toolkit = Toolkit.getDefaultToolkit(); 
		int x = (int)(toolkit.getScreenSize().getWidth()-component.getWidth())/2; 
		int y = (int)(toolkit.getScreenSize().getHeight()-component.getHeight())/2; 
		component.setLocation(x, y); 
	}
	
	//获取Image对象
	public Image getImage(String imageURL){
		Toolkit toolkit = Toolkit.getDefaultToolkit(); 
		return toolkit.createImage(ApplicationMainFrame.class.getResource(imageURL));
	}
	
	//获取字体对象
	public Font getFont(String fontType,int fontStyle,int fontSize){
		return new Font(fontType,fontStyle,fontSize);
	}
	
	//jContentPane
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}
	
	/**
	 * 入口函数
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ApplicationMainFrame application = new ApplicationMainFrame();
				application.setVisible(true);
				application.setResizable(false);
			}
		});
	}

}
