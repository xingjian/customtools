package com.promise.cn.util;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**  
 * 功能描述:基于java mail进行封装的类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年12月22日 上午11:07:42  
 */
public class PBMailUtil {

    /**
     * 构造发送邮件的参数
     * @param protocol eg: smtp 
     * @param host eg:smtp.163.com
     * @param auth eg:true
     * @param mailUserName xingjian@yeah.net
     * @param mailPasswd  
     * @return
     */
    public static Properties CreateProperties(String protocol,String host,String auth,String mailUserName,String mailPasswd){
        // 参数配置
        Properties props = new Properties();
        //使用的协议（JavaMail规范要求）
        props.setProperty("mail.transport.protocol", protocol);
        // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.host", host);
        // 请求认证，参数名称与具体实现有关
        props.setProperty("mail.smtp.auth", auth);
        props.setProperty("sendmail.username", mailUserName);
        props.setProperty("sendmail.passwd", mailPasswd);
        return props;
    }
    
    /**
     * 创建一封只包含文本的简单邮件
     * @param session 和服务器交互的会话
     * @param sendMail 发件人
     * @param receiveMails 接收人
     * @param ccMails 抄送人
     * @param bccMails 密送人
     * @param subject 主题
     * @param content 内容
     * @return
     * @throws Exception
     */
    public static MimeMessage CreateMimeMessage(Session session,InternetAddress sendMail,List<InternetAddress> receiveMails,List<InternetAddress> ccMails,List<InternetAddress> bccMails,String subject,String content) throws Exception {
        //创建一封邮件
        MimeMessage message = new MimeMessage(session);
        //From: 发件人 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
        message.setFrom(sendMail);
        //To: 收件人（可以增加多个收件人、抄送、密送）
        for(InternetAddress recipientTemp: receiveMails){
            message.addRecipient(MimeMessage.RecipientType.TO, recipientTemp);
        }
        //Cc: 抄送（可选）
        if(null!=ccMails){
            for(InternetAddress recipientCCTemp: ccMails){
                message.addRecipient(MimeMessage.RecipientType.CC, recipientCCTemp);
            } 
        }
        //Bcc: 密送（可选）
        if(null!=bccMails){
            for(InternetAddress recipientBCCTemp: bccMails){
                message.addRecipient(MimeMessage.RecipientType.BCC, recipientBCCTemp);
            }  
        }
        //Subject: 邮件主题
        message.setSubject(subject, "UTF-8");
        //Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");
        //设置发件时间
        message.setSentDate(new Date());
        //保存设置
        message.saveChanges();
        return message;
    }
    /**
     * 发送一封只包含文本的简单邮件
     * @param props 必须包含mail.transport.protocol(使用的协议)、mail.host(发件人的邮箱的 SMTP 服务器地址)、mail.smtp.auth、sendmail.username、sendmail.passwd
     * @param session 和服务器交互的会话
     * @param sendMail 发件人
     * @param receiveMails 接收人
     * @param ccMails 抄送人
     * @param bccMails 密送人
     * @param subject 主题
     * @param content 内容
     * @return
     * @throws Exception
     */
    public static String SendEmailText(Properties props,InternetAddress sendMail,List<InternetAddress> receiveMails,List<InternetAddress> ccMails,List<InternetAddress> bccMails,String subject,String content) throws Exception{
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);// 设置为debug模式, 可以查看详细的发送 log
        //创建一封邮件
        MimeMessage message = CreateMimeMessage(session, sendMail, receiveMails,ccMails,bccMails,subject,content);
        return SendMail(message,props);
    }
    
    /**
     * 创建一个带附件的email
     * @param session 和服务器交互的回话
     * @param sendMail 发送人的email
     * @param receiveMails 接收人的email地址
     * @param ccMails 抄送人的email地址
     * @param bccMails 密送人的email地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param fileDataSources 邮件附件内容
     * @return
     * @throws Exception
     */
    public static MimeMessage CreateMimeMessageAttachment(Session session,InternetAddress sendMail,List<InternetAddress> receiveMails,List<InternetAddress> ccMails,List<InternetAddress> bccMails,String subject,String content,List<File> files) throws Exception {
        //创建一封邮件
        MimeMessage message = new MimeMessage(session);
        //From: 发件人 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
        message.setFrom(sendMail);
        //To: 收件人（可以增加多个收件人、抄送、密送）
        for(InternetAddress recipientTemp: receiveMails){
            message.addRecipient(MimeMessage.RecipientType.TO, recipientTemp);
        }
        //Cc: 抄送（可选）
        if(null!=ccMails){
            for(InternetAddress recipientCCTemp: ccMails){
                message.addRecipient(MimeMessage.RecipientType.CC, recipientCCTemp);
            } 
        }
        //Bcc: 密送（可选）
        if(null!=bccMails){
            for(InternetAddress recipientBCCTemp: bccMails){
                message.addRecipient(MimeMessage.RecipientType.BCC, recipientBCCTemp);
            }  
        }
        //Subject: 邮件主题
        message.setSubject(subject, "UTF-8");
        //Content: 邮件正文
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(content, "text/html;charset=UTF-8");
        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(text);
        if(null!=files&&files.size()>0){
            for(File fileTemp:files){
                MimeBodyPart attachment = new MimeBodyPart();
                DataHandler dh2 = new DataHandler(new FileDataSource(fileTemp));
                attachment.setDataHandler(dh2);
                attachment.setFileName(MimeUtility.encodeText(dh2.getName()));
                mm.addBodyPart(attachment);
            }
        }
        mm.setSubType("mixed");
        message.setContent(mm);
        //设置发件时间
        message.setSentDate(new Date());
        //保存设置
        message.saveChanges();
        return message;
    }
    /**
     * 发送一封本和附件的邮件
     * @param props 必须包含mail.transport.protocol(使用的协议)、mail.host(发件人的邮箱的 SMTP 服务器地址)、mail.smtp.auth、sendmail.username、sendmail.passwd
     * @param session 和服务器交互的会话
     * @param sendMail 发件人
     * @param receiveMails 接收人
     * @param ccMails 抄送人
     * @param bccMails 密送人
     * @param subject 主题
     * @param content 内容
     * @return
     * @throws Exception
     */
    public static String SendEmailAttachment(Properties props,InternetAddress sendMail,List<InternetAddress> receiveMails,List<InternetAddress> ccMails,List<InternetAddress> bccMails,String subject,String content,List<File> files) throws Exception{
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);// 设置为debug模式, 可以查看详细的发送 log
        //创建一封邮件
        MimeMessage message = CreateMimeMessageAttachment(session, sendMail, receiveMails,ccMails,bccMails,subject,content,files);
        return SendMail(message,props);
    }
    
    /**
     * 发送邮件
     * @param mm 邮件对象
     * @param props 邮件配置 必须包含mail.transport.protocol(使用的协议)、mail.host(发件人的邮箱的 SMTP 服务器地址)、mail.smtp.auth、sendmail.username、sendmail.passwd
     * @return
     */
    public static String SendMail(MimeMessage mm,Properties props) throws Exception{
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);// 设置为debug模式, 可以查看详细的发送 log
        // 根据 Session获取邮件传输对象
        Transport transport = session.getTransport();
        //使用 邮箱账号 和 密码 连接邮件服务器
        //这里认证的邮箱必须与 mm 中的发件人邮箱一致，否则报错
        transport.connect(props.getProperty("sendmail.username"), props.getProperty("sendmail.passwd"));
        //发送邮件, 发到所有的收件地址, mm.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(mm, mm.getAllRecipients());
        //关闭连接
        transport.close();
        return "success";
    }
}
