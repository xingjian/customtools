/*@文件名: PolicyDialog.java  @创建人: 邢健   @创建日期: 2011-10-25 下午03:05:36*/
package com.promise.cn.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JTextArea;
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
@SuppressWarnings("all")
public class PolicyDialog extends JDialog{

	public JPanel jp1,jp2,jp3,jp4,jp5,jp6,jp7,northPanel,centerPanel,southPanel;
	public PolicyVO policyVO;
	private JComboBox jcb;
	private JComboBox jcb_jp5;
	private JComboBox jcb_jp6;
	private JComboBox jcb_jp7;
	private JComboBox jcb_jp71;
	private String[] policyType; 
	public JLabel label1 = new JLabel("策略名称:");
	public JTextField textField = new JTextField(12);
	public JLabel label2 = new JLabel("策略类型:");
	public CardLayout cardLayout;
	public JButton okBtn,exitBtn;
	public JLabel label1_jp1,label2_jp1;
	public JTextField maxTextField_jp1,minTextField_jp1;
	public JLabel label1_jp2,label2_jp2,label3_jp2;
	public JTextField maxTextField_jp2,minTextField_jp2,countTextField;
	public JLabel label1_jp3,label2_jp3,label3_jp3;
	public JLabel label1_jp4;
	public JLabel label1_jp5,label2_jp5,label3_jp5;
	public JTextField jtf1_jp5,jtf2_jp5,jtf3_jp5;
	public JLabel label1_jp6,label2_jp6,label3_jp6,label4_jp6;
	public JTextField jtf1_jp6,jtf2_jp6,jtf3_jp6;
	public JLabel label1_jp7,label2_jp7,label3_jp7;
	public JTextField jtf1_jp7,jtf2_jp7,jtf3_jp7;
	public JTextArea jta_jp4;
	public JTextField strJTextField,lengthJTextField;
	public String[] siteJComboBoxItems = new String[2];
	public String[] jcbItems_jp5 = new String[2];
	public String[] jcbItems_jp6 = new String[2];
	public String[] jcbItems_jp7 = new String[2];
	public String[] jcbItems_jp71 = new String[3];
	public JComboBox siteJComboBox = null;
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
		siteJComboBox = new JComboBox(siteJComboBoxItems);
		jcbItems_jp5[0] = CreateDataUtil.DESCENDTYPE_ASC;
		jcbItems_jp5[1] = CreateDataUtil.DESCENDTYPE_DESC;
		jcb_jp5 = new JComboBox(jcbItems_jp5);
		jcbItems_jp6[0] = CreateDataUtil.DESCENDTYPE_ASC;
		jcbItems_jp6[1] = CreateDataUtil.DESCENDTYPE_DESC;
		jcb_jp6 = new JComboBox(jcbItems_jp6);
		jcbItems_jp71[0] = CreateDataUtil.DATA_HOUR;
		jcbItems_jp71[1] = CreateDataUtil.DATA_MINUTE;
		jcbItems_jp71[2] = CreateDataUtil.DATA_SECOND;
		jcb_jp71 = new JComboBox(jcbItems_jp71);
		jcbItems_jp7[0] = CreateDataUtil.DESCENDTYPE_ASC;
		jcbItems_jp7[1] = CreateDataUtil.DESCENDTYPE_DESC;
		jcb_jp7 = new JComboBox(jcbItems_jp7);
		jcb_jp71.setMaximumSize(new Dimension(60,20)); 
		jcb_jp71.setMinimumSize(new Dimension(60,20)); 
		jcb_jp71.setPreferredSize(new Dimension(60,20)); 
		jcb_jp7.setMaximumSize(new Dimension(135,20)); 
		jcb_jp7.setMinimumSize(new Dimension(135,20)); 
		jcb_jp7.setPreferredSize(new Dimension(135,20));
		jcb_jp6.setMaximumSize(new Dimension(135,20)); 
		jcb_jp6.setMinimumSize(new Dimension(135,20)); 
		jcb_jp6.setPreferredSize(new Dimension(135,20));
		siteJComboBox.setMaximumSize(new Dimension(135,20)); 
		siteJComboBox.setMinimumSize(new Dimension(135,20)); 
		siteJComboBox.setPreferredSize(new Dimension(135,20));
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
		}else if(policyVO.getType().equals(CreateDataUtil.RANDOMINT_AB)){
			jcb.setSelectedIndex(1);
			textField.setText(policyVO.getName());
			minTextField_jp1.setText(policyVO.getInitValue());
			maxTextField_jp1.setText(policyVO.getEndValue());
		}else if(policyVO.getType().equals(CreateDataUtil.RANDOMSTRING)){
			jcb.setSelectedIndex(2);
			textField.setText(policyVO.getName());
			strJTextField.setText(policyVO.getValue());
			lengthJTextField.setText(policyVO.getStrLength());
			siteJComboBox.setSelectedItem(policyVO.getSiteStr());
		}else if(policyVO.getType().equals(CreateDataUtil.RANDOMDOUBLE_AB)){
			jcb.setSelectedIndex(0);
			textField.setText(policyVO.getName());
			minTextField_jp2.setText(policyVO.getInitValue());
			maxTextField_jp2.setText(policyVO.getEndValue());
			countTextField.setText(policyVO.getNumberDecimal());
		}else if(policyVO.getType().equals(CreateDataUtil.CONSTANTVALUE)){
			jcb.setSelectedItem(CreateDataUtil.CONSTANTVALUE);
			textField.setText(policyVO.getName());
			jta_jp4.setText(policyVO.getValue());
		}else if(policyVO.getType().equals(CreateDataUtil.DESCEND_INT)){
			jcb.setSelectedItem(CreateDataUtil.DESCEND_INT);
			textField.setText(policyVO.getName());
			jtf1_jp5.setText(policyVO.getValue());
			jtf3_jp5.setText(policyVO.getStepValue());
			if(policyVO.isDesc()){
				jcb_jp5.setSelectedItem(CreateDataUtil.DESCENDTYPE_ASC);
			}else {
				jcb_jp5.setSelectedItem(CreateDataUtil.DESCENDTYPE_DESC);
			}
		}else if(policyVO.getType().equals(CreateDataUtil.DESCEND_DOUBLE)){
			jcb.setSelectedItem(CreateDataUtil.DESCEND_DOUBLE);
			textField.setText(policyVO.getName());
			jtf1_jp6.setText(policyVO.getValue());
			jtf2_jp6.setText(policyVO.getNumberDecimal());
			jtf3_jp6.setText(policyVO.getStepValue());
			if(policyVO.isDesc()){
				jcb_jp6.setSelectedItem(CreateDataUtil.DESCENDTYPE_ASC);
			}else {
				jcb_jp6.setSelectedItem(CreateDataUtil.DESCENDTYPE_DESC);
			}
		}else if(policyVO.getType().equals(CreateDataUtil.DESCEND_DATE)){
			jcb.setSelectedItem(CreateDataUtil.DESCEND_DATE);
			textField.setText(policyVO.getName());
			jtf1_jp7.setText(policyVO.getValue());
			jtf3_jp7.setText(policyVO.getStepValue());
			if(policyVO.isDesc()){
				jcb_jp7.setSelectedItem(CreateDataUtil.DESCENDTYPE_ASC);
			}else {
				jcb_jp7.setSelectedItem(CreateDataUtil.DESCENDTYPE_DESC);
			}
			if(policyVO.getStepValueUnit().equals(CreateDataUtil.UNIT_HOUR)){
				jcb_jp71.setSelectedItem(CreateDataUtil.DATA_HOUR);
			}else if(policyVO.getStepValueUnit().equals(CreateDataUtil.UNIT_MINUTE)){
				jcb_jp71.setSelectedItem(CreateDataUtil.DATA_MINUTE);
			}if(policyVO.getStepValueUnit().equals(CreateDataUtil.UNIT_SECOND)){
				jcb_jp71.setSelectedItem(CreateDataUtil.DATA_SECOND);
			}
			jcb_jp71.setSelectedItem(policyVO.getStepValueUnit());
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
					}else if(type.equals(CreateDataUtil.DESCEND_INT)){
						String value = jtf1_jp5.getText();
						String stepValue = jtf3_jp5.getText();
						String descName = jcb_jp5.getSelectedItem().toString();
						boolean descBoolean = true;
						if(descName.equals(CreateDataUtil.DESCENDTYPE_DESC)){
							descBoolean = false;
						}
						policyVO.setValue(value);
						policyVO.setDesc(descBoolean);
						policyVO.setStepValue(stepValue);
					}else if(type.equals(CreateDataUtil.DESCEND_DOUBLE)){
						String value = jtf1_jp6.getText();
						String stepValue = jtf3_jp6.getText();
						String descName = jcb_jp6.getSelectedItem().toString();
						boolean descBoolean = true;
						if(descName.equals(CreateDataUtil.DESCENDTYPE_DESC)){
							descBoolean = false;
						}
						policyVO.setValue(value);
						policyVO.setDesc(descBoolean);
						policyVO.setStepValue(stepValue);
						policyVO.setNumberDecimal(jtf2_jp6.getText());
					}else if(type.equals(CreateDataUtil.DESCEND_DATE)){
						String value = jtf1_jp7.getText();
						String stepValue = jtf3_jp7.getText();
						String descName = jcb_jp7.getSelectedItem().toString();
						String stepUnitName = jcb_jp71.getSelectedItem().toString();
						boolean descBoolean = true;
						String stepUnitValue = CreateDataUtil.UNIT_HOUR;
						if(stepUnitName.equals(CreateDataUtil.DATA_HOUR)){
							stepUnitValue = CreateDataUtil.UNIT_HOUR;
						}else if(stepUnitName.equals(CreateDataUtil.DATA_MINUTE)){
							stepUnitValue = CreateDataUtil.UNIT_MINUTE;
						}else if(stepUnitName.equals(CreateDataUtil.DATA_SECOND)){
							stepUnitValue = CreateDataUtil.UNIT_SECOND;
						}
						if(descName.equals(CreateDataUtil.DESCENDTYPE_DESC)){
							descBoolean = false;
						}
						policyVO.setValue(value);
						policyVO.setDesc(descBoolean);
						policyVO.setStepValue(stepValue);
						policyVO.setStepValueUnit(stepUnitValue);
					}else if(type.equals(CreateDataUtil.CONSTANTVALUE)){
						policyVO.setValue(jta_jp4.getText().trim());
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
	
	//随机策略Int
	public JPanel getJPanel_One(){
		if(jp1==null){
			jp1 = new JPanel(new GridLayout(2,1));
			jp1.setBorder(new TitledBorder(null,"随机策略Int",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			label1_jp1 = new JLabel("最小值:");
			label1_jp1.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2_jp1 = new JLabel("最大值:");
			label2_jp1.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			maxTextField_jp1 = new JTextField(12);
			minTextField_jp1 = new JTextField(12);
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
	
	//随机策略Double
	public JPanel getJPanel_Two(){
		if(jp2==null){
			jp2 = new JPanel(new GridLayout(3,1));
			jp2.setBorder(new TitledBorder(null,"随机策略Double",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			label1_jp2 = new JLabel("最小值:");
			label1_jp2.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2_jp2 = new JLabel("最大值:");
			label2_jp2.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label3_jp2 = new JLabel("小数位:");
			label3_jp2.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			maxTextField_jp2 = new JTextField(12);
			minTextField_jp2 = new JTextField(12);
			countTextField = new JTextField(12);
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
	
	//随机策略String
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
			strJTextField = new JTextField(12);
			lengthJTextField = new JTextField(12);
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
	
	//固定字符串
	public JPanel getJPanel_Four(){
		if(jp4==null){
			jp4 = new JPanel(new BorderLayout());
			jp4.setBorder(new TitledBorder(null,"固定数值或字符串",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			label1_jp4 = new JLabel("固定数值(多个值使用逗号分开):");
			label1_jp4.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			jta_jp4 = new JTextArea();
			jp4.add(label1_jp4,BorderLayout.NORTH);
			jp4.add(jta_jp4,BorderLayout.CENTER);
		}
		return jp4;
	}
	
	//递增或递减Int
	public JPanel getJPanel_Five(){
		if(jp5==null){
			jp5 = new JPanel(new GridLayout(3,1));
			jp5.setBorder(new TitledBorder(null,"递增或递减(Int)",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			JPanel jp5_temp1 = new JPanel();
			JPanel jp5_temp2 = new JPanel();
			JPanel jp5_temp3 = new JPanel();
			jtf1_jp5 = new JTextField(12);
			jtf2_jp5 = new JTextField(12);
			jtf3_jp5 = new JTextField(12);
			label1_jp5 = new JLabel("初 始 值 :");
			label2_jp5 = new JLabel("排序方式:");
			label3_jp5 = new JLabel("步 长 值 :");
			label1_jp5.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2_jp5.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label3_jp5.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			jcb_jp5.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			jp5_temp3.add(label3_jp5);
			jp5_temp3.add(jtf3_jp5);
			jp5_temp1.add(label1_jp5);
			jp5_temp1.add(jtf1_jp5);
			jp5_temp2.add(label2_jp5);
			jp5_temp2.add(jcb_jp5);
			jp5.add(jp5_temp1);
			jp5.add(jp5_temp3);
			jp5.add(jp5_temp2);
		}
		return jp5;
	}
	
	//递增或递减double
	public JPanel getJPanel_Six(){
		if(jp6==null){
			jp6 = new JPanel(new GridLayout(4,1));
			jp6.setBorder(new TitledBorder(null,"递增或递减(Double)",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			JPanel jp6_temp1 = new JPanel();
			JPanel jp6_temp2 = new JPanel();
			JPanel jp6_temp3 = new JPanel();
			JPanel jp6_temp4 = new JPanel();
			jtf1_jp6 = new JTextField(12);
			jtf2_jp6 = new JTextField(12);
			jtf3_jp6 = new JTextField(12);
			label1_jp6 = new JLabel("初 始 值 :");
			label4_jp6 = new JLabel("小 数 位 :");
			label2_jp6 = new JLabel("排序方式:");
			label3_jp6 = new JLabel("步 长 值 :");
			label1_jp6.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2_jp6.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label3_jp6.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label4_jp6.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			jcb_jp6.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			jp6_temp3.add(label3_jp6);
			jp6_temp3.add(jtf3_jp6);
			jp6_temp1.add(label1_jp6);
			jp6_temp1.add(jtf1_jp6);
			jp6_temp2.add(label2_jp6);
			jp6_temp2.add(jcb_jp6);
			jp6_temp4.add(label4_jp6);
			jp6_temp4.add(jtf2_jp6);
			jp6.add(jp6_temp1);
			jp6.add(jp6_temp3);
			jp6.add(jp6_temp2);
			jp6.add(jp6_temp4);
		}
		return jp6;
	}

	//获取日期面板
	public JPanel getJPanel_Seven(){
		if(jp7==null){
			jp7 = new JPanel(new GridLayout(4,1));
			jp7.setBorder(new TitledBorder(null,"递增或递减(Date)",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,CreateDataUtil.getFont("微软雅黑", Font.PLAIN, 12),Color.BLUE));
			JPanel jp7_temp1 = new JPanel();
			JPanel jp7_temp2 = new JPanel();
			JPanel jp7_temp3 = new JPanel();
			JPanel jp7_temp4 = new JPanel();
			jtf1_jp7 = new JTextField(12);
			jtf2_jp7 = new JTextField(12);
			jtf3_jp7 = new JTextField(6);
			label1_jp7 = new JLabel("开始日期:");
			label2_jp7 = new JLabel("排序方式:");
			label3_jp7 = new JLabel("步 长 值 :");
			JLabel formatLabel = new JLabel("yyyy-MM-dd hh:mm:ss");
			JLabel formatLabel2 = new JLabel("日期格式:");
			formatLabel.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			formatLabel2.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label1_jp7.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label2_jp7.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			label3_jp7.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			jcb_jp7.setFont(CreateDataUtil.getFont("微软雅黑", Font.BOLD, 12));
			jp7_temp3.add(label3_jp7);
			jp7_temp3.add(jtf3_jp7);
			jp7_temp3.add(jcb_jp71);
			jp7_temp1.add(label1_jp7);
			jp7_temp1.add(jtf1_jp7);
			jp7_temp4.add(formatLabel2);
			jp7_temp4.add(formatLabel);
			jp7_temp2.add(label2_jp7);
			jp7_temp2.add(jcb_jp7);
			jp7.add(jp7_temp1);
			jp7.add(jp7_temp3);
			jp7.add(jp7_temp2);
			jp7.add(jp7_temp4);
		}
		return jp7;
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
						}else if(itemStr.equals(CreateDataUtil.CONSTANTVALUE)){
							cardLayout.show(centerPanel, "jp4");
						}else if(itemStr.equals(CreateDataUtil.DESCEND_INT)){
							cardLayout.show(centerPanel, "jp5");
						}else if(itemStr.equals(CreateDataUtil.DESCEND_DOUBLE)){
							cardLayout.show(centerPanel, "jp6");
						}else if(itemStr.equals(CreateDataUtil.DESCEND_DATE)){
							cardLayout.show(centerPanel, "jp7");
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
			centerPanel.add("jp4", getJPanel_Four());
			centerPanel.add("jp5", getJPanel_Five());
			centerPanel.add("jp6", getJPanel_Six());
			centerPanel.add("jp7", getJPanel_Seven());
		}
		return centerPanel;
	}
	
}
