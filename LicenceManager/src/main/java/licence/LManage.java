/** @文件名: LManage.java @创建人：邢健  @创建日期： 2013-8-5 下午1:48:37 */
package licence;

import java.io.InputStream;
import java.net.InetAddress;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;

/**
 * @类名: LManage.java
 * @包名: licence
 * @描述: TODO
 * @作者: xingjian xingjian@yeah.net
 * @日期:2013-8-5 下午1:48:37
 * @版本: V1.0
 */
public class LManage {

	private static boolean offenChcek = false;
	private static boolean flag = false;
	private static int times = 5;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时");
	static {
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			InputStream input = LManage.class.getClassLoader().getResourceAsStream(hostname+".lic");
			byte[] v = new byte[128];
			input.read(v, 0, 128);
			String liceneStr = new String(decryptByPublicKey(v),"gbk");
			String[] sArray = liceneStr.split(",");
			String s1 = sArray[0];
			String s2 = sArray[1];
			String s3 = sArray[2];
			Date currentDate = new Date();
			Date sDate = sdf.parse(s2);
			Date eDate = sdf.parse(s3);
			if(hostname.equals(s1)){
				//验证日期有效性
				if(currentDate.before(sDate)||currentDate.after(eDate)){
					flag = false;
					System.out.println("系统提示：东华软件山洪许可校验失败！");
					closeJVM();
				}else{//日期快临近提醒
					flag = true;
					System.out.println("系统提示：东华软件山洪许可校验通过！");
					System.out.println("系统提示：有效期"+s2+"-"+s3);
					long r = eDate.getTime()- currentDate.getTime();
					double r1 = r/(1000*60*60*24);
					if(r1<15){
						System.out.println("系统提示：许可快过期请尽快联系东华软化股份公司进行许可更换！");
					}
				}
			}else{
				//机器名称不一致
				flag = false;
				System.out.println("系统提示：东华软件山洪许可校验失败！");
				closeJVM();
			}
		} catch (Exception e) {
			System.out.println("系统提示：东华软件山洪许可校验失败！");
			closeJVM();
		}
	}

	public static void closeJVM(){
		for(int i=times;i>0;i--){
			try {
				Thread.sleep(1000);
				System.out.println("系统提示: "+i+" 秒后系统关闭.......");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.exit(-1);
	}
	
	public static String dePublicKey(byte[] input, PublicKey key) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, key);
	    return new String(cipher.doFinal(input));
	}
	
	private static Certificate getCertificate()throws Exception {   
        // 实例化证书工厂   
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");   
        // 取得证书文件流   
        InputStream in = LManage.class.getClassLoader().getResourceAsStream("floodwarn.cer"); 
        // 生成证书   
        Certificate certificate = certificateFactory.generateCertificate(in);   
        // 关闭证书文件流   
        in.close();   
        return certificate;   
    } 
	
	 private static PublicKey getPublicKeyByCertificate()throws Exception {   
	        // 获得证书   
	        Certificate certificate = getCertificate();   
	        // 获得公钥   
	        return certificate.getPublicKey();   
	}
	
	public static byte[] decryptByPublicKey(byte[] data)   
            throws Exception {   
        // 取得公钥   
        PublicKey publicKey = getPublicKeyByCertificate();   
        // 对数据加密   
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());   
        cipher.init(Cipher.DECRYPT_MODE, publicKey);   
        return cipher.doFinal(data);   
    } 
	
	
	public static void check() {
		if (!flag) {
			System.out.println("系统提示：东华软件山洪许可校验失败！");
			closeJVM();
		}else{
			System.out.println("系统提示：东华软件山洪许可校验通过！");
		}
	}

	public static void main(String[] args){
//		check();
	}
	
	public static boolean isOffenChcek() {
		return offenChcek;
	}

	public static void setOffenChcek(boolean offenChcek) {
		LManage.offenChcek = offenChcek;
	}
}
