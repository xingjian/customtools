/** @文件名: MainUI.java @创建人：邢健  @创建日期： 2013-8-6 下午3:58:43 */
package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;

import javax.crypto.Cipher;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * @类名: MainUI.java
 * @包名: ui
 * @描述: TODO
 * @作者: xingjian xingjian@yeah.net
 * @日期:2013-8-6 下午3:58:43
 * @版本: V1.0
 */
public class MainUI {

	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	private JPanel northPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private JLabel label1 = new JLabel("机器名称:");
	private JLabel label2 = new JLabel("开始时间:");
	private JLabel label3 = new JLabel("结束时间:");
	private JLabel labelNotice = new JLabel("许可路径只写目录,目录必须要存在,注意应该使用单斜杠！");
	private JLabel label4 = new JLabel("许可路径:");
	private JTextField jtfMName = new JTextField(20);
	private JTextField lpath = new JTextField(20);
	private JButton btn = new JButton("生成");
	private JButton btnClose = new JButton("重置");
	private DateChooserJButton sbtn = new DateChooserJButton();
	private DateChooserJButton ebtn = new DateChooserJButton();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");

	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setSize(300, 210);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("许可生成器(xingjian@yeah.net)");
			jFrame.setFont(getFont("微软雅黑", Font.BOLD, 16));
			jFrame.setIconImage(getImage("/image/home.gif"));
			setCenter(jFrame);
			initJFrame();
		}
		return jFrame;
	}

	public void initJFrame() {
		jFrame.add(getNorthPanel(), BorderLayout.NORTH);
		jFrame.add(getCenterPanel(), BorderLayout.CENTER);
		jFrame.add(getSourthPanel(), BorderLayout.SOUTH);
	}

	public JPanel getNorthPanel() {
		northPanel.setLayout(new FlowLayout(7));
		northPanel.add(label1);
		northPanel.add(jtfMName);
		return northPanel;
	}

	public JPanel getCenterPanel() {
		centerPanel.setLayout(new GridLayout(4, 1));
		JPanel jp1 = new JPanel(new FlowLayout(7));
		JPanel jp2 = new JPanel(new FlowLayout(7));
		JPanel jp3 = new JPanel(new FlowLayout(7));
		JPanel jp4 = new JPanel(new FlowLayout(7));
		label1.setFont(getFont("微软雅黑", Font.BOLD, 12));
		label2.setFont(getFont("微软雅黑", Font.BOLD, 12));
		label3.setFont(getFont("微软雅黑", Font.BOLD, 12));
		label4.setFont(getFont("微软雅黑", Font.BOLD, 12));
		labelNotice.setFont(getFont("微软雅黑", Font.BOLD, 11));
		labelNotice.setForeground(Color.red);
		jp4.add(labelNotice);
		btn.setFont(getFont("微软雅黑", Font.BOLD, 11));
		btnClose.setFont(getFont("微软雅黑", Font.BOLD, 11));
		sbtn.setFont(getFont("微软雅黑", Font.BOLD, 12));
		ebtn.setFont(getFont("微软雅黑", Font.BOLD, 12));
		lpath.setText("C:\\");
		jp1.add(label2);
		jp1.add(sbtn);
		jp2.add(label3);
		jp2.add(ebtn);
		jp3.add(label4);
		jp3.add(lpath);
		centerPanel.add(jp1);
		centerPanel.add(jp2);
		centerPanel.add(jp3);
		centerPanel.add(jp4);
		return centerPanel;
	}

	public JPanel getSourthPanel() {
		FlowLayout fl = new FlowLayout();
		fl.setHgap(40);
		fl.setAlignment(FlowLayout.CENTER);
		southPanel.setLayout(fl);
		southPanel.add(btn);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean  result = createLicence(jtfMName.getText(),sbtn.getText(),ebtn.getText(),lpath.getText());
				String message = "生成机器名为:"+jtfMName.getText()+"山洪许可文件成功！";
				if(!result){
					message = "生成机器名为:"+jtfMName.getText()+"山洪许可文件失败！";
				}
				JOptionPane.showMessageDialog(jContentPane, message, "提示信息",JOptionPane.DEFAULT_OPTION);
			}
		});
		southPanel.add(btnClose);
		return southPanel;
	}

	/**
	 * 生成许可文件
	 * 
	 * @return
	 */
	public boolean createLicence(String mName, String sTime, String eTime,String path) {
		String pass = "xingjian";
		boolean result = true;
		String lineceStr = mName + "," + sTime + "," + eTime;
		byte[] s;
		try {
			s = encryptByPrivateKey(lineceStr.getBytes(), path, "floodwarn",pass);
			FileOutputStream out = new FileOutputStream(new File(path+ mName + ".lic"));
			out.write(s);
			out.close();
			out.close();
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		
		return result;
	}

	public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath,
			String alias, String password) throws Exception {
		// 取得私钥
		PrivateKey privateKey = getPrivateKeyByKeyStore(alias,password);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	private static PrivateKey getPrivateKeyByKeyStore(String alias, String password) throws Exception {
		// 获得密钥库
		KeyStore ks = getKeyStore(password);
		// 获得私钥
		return (PrivateKey) ks.getKey(alias, password.toCharArray());
	}

	private static KeyStore getKeyStore(String password)
			throws Exception {
		// 实例化密钥库
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		// 获得密钥库文件流
		InputStream is = MainUI.class.getClassLoader().getResourceAsStream("floodwarnkeystore");
		// 加载密钥库
		ks.load(is, password.toCharArray());
		// 关闭密钥库文件流
		is.close();
		return ks;
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

	public static Font getFont(String fontType, int fontStyle, int fontSize) {
		return new Font(fontType, fontStyle, fontSize);
	}

	public static void setCenter(Component component) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int) (toolkit.getScreenSize().getWidth() - component
				.getWidth()) / 2;
		int y = (int) (toolkit.getScreenSize().getHeight() - component
				.getHeight()) / 2;
		component.setLocation(x, y);
	}

	public static Image getImage(String imageURL) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		return toolkit.createImage(MainUI.class.getResource(imageURL));
	}

}
