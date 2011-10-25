package com.promise.cn.service;

import java.awt.FlowLayout;   
import java.awt.event.ActionEvent;   
import java.awt.event.ActionListener;   
  
import javax.swing.JButton;   
import javax.swing.JFileChooser;   
import javax.swing.JFrame;   
  
public class JFileChooserTest {   
       
    JFrame jf = new JFrame();   
    JButton jb1 = new JButton("选择");   
    JButton jb2 = new JButton("打开");   
    JButton jb3 = new JButton("保存");   
    JFileChooser jfc = new JFileChooser();   
       
    public JFileChooserTest(){   
        jf.setVisible(true);   
        jf.setBounds(200, 20, 950, 650);   
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
           
        jf.add(jb1);   
        jf.add(jb2);   
        jf.add(jb3);   
           
        jf.setLayout(new FlowLayout());   
           
        jb1.addActionListener(new ActionListener() {   
               
            @Override  
            public void actionPerformed(ActionEvent e) {   
                //选择对话框   
                jfc.showDialog(jf, "选择，哈哈哈……");   
            }   
        });   
        jb2.addActionListener(new ActionListener() {   
               
            @Override  
            public void actionPerformed(ActionEvent e) {   
                //打开对话框   
                jfc.showOpenDialog(jf);   
            }   
        });   
        jb3.addActionListener(new ActionListener() {   
               
            @Override  
            public void actionPerformed(ActionEvent e) {   
                //保存对话框   
                jfc.showSaveDialog(jf);   
            }   
        });   
           
    }   
       
    public static void main(String[] args) {   
        new JFileChooserTest();   
    }   
  
} 