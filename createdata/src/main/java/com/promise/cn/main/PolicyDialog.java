/*@文件名: PolicyDialog.java  @创建人: 邢健   @创建日期: 2011-10-25 下午03:05:36*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.promise.cn.util.CreateDataUtil;
import com.promise.cn.vo.PolicyVO;

/**   
 * @类名: PolicyDialog.java 
 * @包名: com.promise.cn.main 
 * @描述: 测量窗口 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-25 下午03:05:36 
 * @版本 V1.0   
 */
public class PolicyDialog extends JDialog{

	public JPanel jp1,jp2,jp3,northPanel,centerPanel;
	public PolicyVO policyVO;
	private JComboBox jcb;
	private String[] policyType; 
	public JLabel label1 = new JLabel("策略名称:");
	public JTextField textField = new JTextField(10);
	public JLabel label2 = new JLabel("策略类型:");
	public CardLayout cardLayout;
	
	public PolicyDialog(String[] policyType,String titleName){
		this.policyType = policyType;
		this.setTitle(titleName);
		init();
		this.setIconImage(CreateDataUtil.getImage("../images/tubiao.png"));
		this.setSize(500,300);
		this.setModal(true);
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		this.setLayout(new BorderLayout());
		this.add(getNorthPanel(), BorderLayout.NORTH);
		this.add(getCenterPanel(), BorderLayout.CENTER);
	}
	
	public JPanel getJPanel_One(){
		if(jp1==null){
			jp1 = new JPanel();
		}
		return jp1;
	}
	
	public JPanel getJPanel_Two(){
		if(jp2==null){
			jp2 = new JPanel();
		}
		return jp2;
	}
	
	public JPanel getJPanel_Three(){
		if(jp3==null){
			jp3 = new JPanel();
		}
		return jp3;
	}
	
	public JPanel getNorthPanel(){
		if(northPanel==null){
			jcb = new JComboBox(policyType);
			northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			northPanel.add(label1);
			northPanel.add(textField);
			northPanel.add(label2);
			northPanel.add(jcb);
			label1.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			northPanel.setBorder(new TitledBorder(null,"名称和类型",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
		}
		return northPanel;
	}
	
	public JPanel getCenterPanel(){
		if(centerPanel==null){
			centerPanel = new JPanel();
			cardLayout = new CardLayout();
			centerPanel.setLayout(cardLayout);
			centerPanel.add("jp1", getJPanel_One());
			centerPanel.add("jp2", getJPanel_Two());
			centerPanel.add("jp3", getJPanel_Three());
		}
		return centerPanel;
	}
}
