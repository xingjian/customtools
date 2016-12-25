package com.promise.pbutil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.InternetAddress;

import org.junit.Test;

import com.promise.cn.util.PBMailUtil;

/**  
 * 功能描述: PBMailUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年12月22日 下午1:18:10  
 */
public class PBMailUtilTest {

    /**
     * 测试发送邮件
     * @throws Exception
     */
    @Test
    public void testSendMain() throws Exception{
        Properties props = PBMailUtil.CreateProperties("smtp", "smtp.tongtusoft.com.cn", "true", "xingjian@tongtusoft.com.cn", "000000@xj");
        InternetAddress sendMail = new InternetAddress("xingjian@tongtusoft.com.cn","工作邮箱","UTF-8");
        InternetAddress receiveMail = new InternetAddress("xingjian@yeah.net","工作邮箱yeah","UTF-8");
        InternetAddress receiveMail1 = new InternetAddress("xingjian@outlook.com","工作邮箱outlook","UTF-8");
        InternetAddress receiveMail2 = new InternetAddress("xingjian888@vip.qq.com","工作邮箱QQ","UTF-8");
        List<InternetAddress> receiveMails = new ArrayList<InternetAddress>();
        receiveMails.add(receiveMail);
        receiveMails.add(receiveMail1);
        receiveMails.add(receiveMail2);
        PBMailUtil.SendEmailText(props, sendMail, receiveMails, null, null, "java程序发的邮件11", "this is test11 mail!");
    }
    
    /**
     * 测试发送带附件邮件
     * @throws Exception
     */
    @Test
    public void testSendMainAttachment() throws Exception{
        Properties props = PBMailUtil.CreateProperties("smtp", "smtp.tongtusoft.com.cn", "true", "xingjian@tongtusoft.com.cn", "000000@xj");
        InternetAddress sendMail = new InternetAddress("xingjian@tongtusoft.com.cn","工作邮箱","UTF-8");
        InternetAddress receiveMail = new InternetAddress("xingjian@yeah.net","工作邮箱yeah","UTF-8");
        InternetAddress receiveMail1 = new InternetAddress("xingjian@outlook.com","工作邮箱outlook","UTF-8");
        InternetAddress receiveMail2 = new InternetAddress("xingjian888@vip.qq.com","工作邮箱QQ","UTF-8");
        List<InternetAddress> receiveMails = new ArrayList<InternetAddress>();
        receiveMails.add(receiveMail);
        receiveMails.add(receiveMail1);
        receiveMails.add(receiveMail2);
        List<File> list = new ArrayList<File>();
        File file1 = new File("D:\\二期停车资源数据目录20160909.xlsx");
        File file2 = new File("D:\\西宁卡口经纬度.csv");
        File file3 = new File("D:\\驾照.jpg");
        File file4 = new File("D:\\sfz_f.jpg");
        list.add(file1);
        list.add(file2);
        list.add(file3);
        list.add(file4);
        PBMailUtil.SendEmailAttachment(props, sendMail, receiveMails, null, null, "java程序发的邮件11", "this is test11 mail!",list);
    }
    
}
