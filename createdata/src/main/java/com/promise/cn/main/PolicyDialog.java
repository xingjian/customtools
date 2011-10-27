/*@文件名: PolicyDialog.java  @创建人: 邢健   @创建日期: 2011-10-25 下午03:05:36*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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

	public JPanel jp1,jp2,jp3,northPanel,centerPanel,southPanel;
	public PolicyVO policyVO;
	private JComboBox jcb;
	private String[] policyType; 
	public JLabel label1 = new JLabel("策略名称:");
	public JTextField textField = new JTextField(10);
	public JLabel label2 = new JLabel("策略类型:");
	public CardLayout cardLayout;
	public JButton okBtn,exitBtn;
	public JLabel label1_jp1,label2_jp1;
	public JTextField maxTextField_jp1,minTextField_jp1;
	public JLabel label1_jp2,label2_jp2,label3_jp2;
	public JTextField maxTextField_jp2,minTextField_jp2,countTextField;
	public JLabel label1_jp3,label2_jp3,label3_jp3;
	public JTextField strJTextField,lengthJTextField;
	public String[] siteJComboBoxItems = new String[2];
	public JComboBox siteJComboBox = new JComboBox(siteJComboBoxItems);
	public PolicyManagerJDialog pmjd;
	public String status;
	
	public PolicyDialog(PolicyManagerJDialog pmjd,String titleName,PolicyVO policyVO,String status){
		this.pmjd = pmjd;
		this.status = status;
		this.policyType = pmjd.policyType;
		this.policyVO = policyVO;
		this.setTitle(titleName);
		init();
		this.setIconImage(CreateDataUtil.getImage("../images/tubiao.png"));
		this.setSize(300,300);
		this.setResizable(false);
		this.setModal(true);
	}
	
	public void initData(){
		siteJComboBoxItems[0] = CreateDataUtil.SITETYPE_LEFT;
		siteJComboBoxItems[1] = CreateDataUtil.SITETYPE_RIGHT;
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		initData();
		this.setLayout(new BorderLayout());
		this.add(getNorthPanel(), BorderLayout.NORTH);
		this.add(getCenterPanel(), BorderLayout.CENTER);
		this.add(getSouthPanel(),BorderLayout.SOUTH);
		if(policyVO.getType()==null){
			jcb.setSelectedIndex(0);
		}else if(policyVO.getType().equals("randomInt[a,b]")){
			jcb.setSelectedIndex(1);
		}else if(policyVO.getType().equals("randomString")){
			jcb.setSelectedIndex(2);
		}
		
	}
	
	public JPanel getSouthPanel(){
		if(southPanel==null){
			southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			okBtn = new JButton("确定");
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					policyVO.setName(textField.getText());
					String type = jcb.getSelectedItem().toString();
					policyVO.setType(type);
					if(type.equals(CreateDataUtil.RANDOMDOUBLE_AB)){
						String initValue = minTextField_jp2.getText();
						String endValue = maxTextField_jp2.getText();
						String numberDecimal = countTextField.getText();
						policyVO.setInitValue(initValue);
						policyVO.setEndValue(endValue);
						policyVO.setNumberDecimal(numberDecimal);
					}else if(type.equals(CreateDataUtil.RANDOMINT_AB)){
						String initValue = minTextField_jp1.getText();
						String endValue = maxTextField_jp1.getText();
						policyVO.setInitValue(initValue);
						policyVO.setEndValue(endValue);
					}else if(type.equals(CreateDataUtil.RANDOMSTRING)){
						String value = strJTextField.getText();
						String valueLength = lengthJTextField.getText();
						String siteType = siteJComboBox.getSelectedItem().toString();
						policyVO.setValue(value);
						policyVO.setStrLength(valueLength);
						policyVO.setSiteStr(siteType);
					}
					if(status.equals("add")){
						pmjd.policyList.add(policyVO);
					}
					pmjd.jtable.updateUI();
					dispose();
				}
			});
			exitBtn = new JButton("取消");
			okBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			exitBtn.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			exitBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			southPanel.add(okBtn);
			southPanel.add(exitBtn);
		}
		return southPanel;
	}
	
	public JPanel getJPanel_One(){
		if(jp1==null){
			jp1 = new JPanel(new GridLayout(2,1));
			jp1.setBorder(new TitledBorder(null,"随机策略Int",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			label1_jp1 = new JLabel("最大值:");
			label1_jp1.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2_jp1 = new JLabel("最小值:");
			label2_jp1.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			maxTextField_jp1 = new JTextField(10);
			minTextField_jp1 = new JTextField(10);
			JPanel jp1_temp1 = new JPanel();
			JPanel jp1_temp2 = new JPanel();
			jp1_temp1.add(label1_jp1);
			jp1_temp1.add(minTextField_jp1);
			jp1_temp2.add(label2_jp1);
			jp1_temp2.add(maxTextField_jp1);
			jp1.add(jp1_temp1);
			jp1.add(jp1_temp2);
		}
		return jp1;
	}
	
	public JPanel getJPanel_Two(){
		if(jp2==null){
			jp2 = new JPanel(new GridLayout(3,1));
			jp2.setBorder(new TitledBorder(null,"随机策略Double",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			label1_jp2 = new JLabel("最大值:");
			label1_jp2.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2_jp2 = new JLabel("最小值:");
			label2_jp2.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label3_jp2 = new JLabel("小数位:");
			label3_jp2.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			maxTextField_jp2 = new JTextField(10);
			minTextField_jp2 = new JTextField(10);
			countTextField = new JTextField(10);
			JPanel jp2_temp1 = new JPanel();
			JPanel jp2_temp2 = new JPanel();
			JPanel jp2_temp3 = new JPanel();
			jp2_temp1.add(label1_jp2);
			jp2_temp1.add(minTextField_jp2);
			jp2_temp2.add(label2_jp2);
			jp2_temp2.add(maxTextField_jp2);
			jp2_temp3.add(label3_jp2);
			jp2_temp3.add(countTextField);
			jp2.add(jp2_temp1);
			jp2.add(jp2_temp2);
			jp2.add(jp2_temp3);
		}
		return jp2;
	}
	
	public JPanel getJPanel_Three(){
		if(jp3==null){
			jp3 = new JPanel(new GridLayout(3,1));
			jp3.setBorder(new TitledBorder(null,"随机策略String",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			JPanel jp3_temp1 = new JPanel();
			JPanel jp3_temp2 = new JPanel();
			JPanel jp3_temp3 = new JPanel();
			label1_jp3 = new JLabel("字  符  串:");
			label2_jp3 = new JLabel("追加位置:");
			label3_jp3 = new JLabel("字符长度:");
			label1_jp3.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2_jp3.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			strJTextField = new JTextField(10);
			lengthJTextField = new JTextField(10);
			label3_jp3.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			jp3_temp3.add(label3_jp3);
			jp3_temp3.add(lengthJTextField);
			jp3_temp1.add(label1_jp3);
			jp3_temp1.add(strJTextField);
			jp3_temp2.add(label2_jp3);
			jp3_temp2.add(siteJComboBox);
			jp3.add(jp3_temp1);
			jp3.add(jp3_temp3);
			jp3.add(jp3_temp2);
		}
		return jp3;
	}
	
	public JPanel getNorthPanel(){
		if(northPanel==null){
			jcb = new JComboBox(policyType);
			jcb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == jcb){
						String itemStr = jcb.getSelectedItem().toString();
						if(itemStr.equals(CreateDataUtil.RANDOMDOUBLE_AB)){
							cardLayout.show(centerPanel, "jp2");
						}else if(itemStr.equals(CreateDataUtil.RANDOMINT_AB)){
							cardLayout.show(centerPanel, "jp1");
						}else if(itemStr.equals(CreateDataUtil.RANDOMSTRING)){
							cardLayout.show(centerPanel, "jp3");
						}
					}
				}
			});
			northPanel = new JPanel(new GridLayout(2,1));
			JPanel jpNorth_temp1 = new JPanel();
			JPanel jpNorth_temp2 = new JPanel();
			jpNorth_temp1.add(label1);
			jpNorth_temp1.add(textField);
			jpNorth_temp2.add(label2);
			jpNorth_temp2.add(jcb);
			label1.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			northPanel.add(jpNorth_temp1);
			northPanel.add(jpNorth_temp2);
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
