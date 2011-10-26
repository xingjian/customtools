/*@文件名: PolicyDialog.java  @创建人: 邢健   @创建日期: 2011-10-25 下午03:05:36*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

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

	public JPanel jp1,jp2,jp3,southPanel,centerPanel;
	public PolicyVO policyVO;
	private JComboBox jcb;
	public JLabel label1 = new JLabel("类型:");
	public CardLayout cardLayout;
	
	public PolicyDialog(){
		init();
		this.setSize(400,600);
		this.setVisible(true);
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		this.setLayout(new BorderLayout());
		this.add(getSouthPanel(), BorderLayout.SOUTH);
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
	
	public JPanel getSouthPanel(){
		if(southPanel==null){
			southPanel = new JPanel();
		}
		return southPanel;
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
