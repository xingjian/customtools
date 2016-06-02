package com.promise.pbutil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**  
 * 功能描述: java 日期测试类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月5日 上午11:40:59  
 */
public class JavaDateTest {

    /**
     * 测试日期相减
     */
    @Test
    public void testDateReduce() throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        Calendar cal = Calendar.getInstance();
        System.out.println(format.format(cal.getTime()));
        cal.add(Calendar.MINUTE, -5);
        int day=cal.getActualMaximum(Calendar.DAY_OF_MONTH);    
        System.out.println(day); 
        int minute=cal.get(Calendar.MINUTE);
        Date dateCreate = cal.getTime();
        System.out.println(format.format(dateCreate));
    }
    
    
    public static void main(String[] args) throws Exception{
        Socket socket = new Socket("172.24.186.135", 9999);
        OutputStream socketOut = socket.getOutputStream();  
        socketOut.write("ttyj".toString().getBytes());
        while(true){
            InputStream socketInput = socket.getInputStream();  
            BufferedReader buffer = new BufferedReader(new InputStreamReader(socketInput));  
            String data = null;  
            while ((data=buffer.readLine())!=null) {  
                System.out.println(data);
            } 
        }
    }
}
