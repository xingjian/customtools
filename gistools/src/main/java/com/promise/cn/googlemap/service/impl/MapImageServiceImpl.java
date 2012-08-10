/** @文件名: MapImageServiceImpl.java @创建人：邢健  @创建日期： 2012-8-7 上午11:44:04 */

package com.promise.cn.googlemap.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.promise.cn.googlemap.service.MapImageService;
import com.promise.cn.googlemap.utils.GoogleMapInitConfig;
import com.promise.cn.googlemap.utils.GoogleMapUtils;
import com.promise.cn.googlemap.vo.FileMessage;

/**   
 * @类名: MapImageServiceImpl.java 
 * @包名: com.promise.cn.googlemap.service.impl 
 * @描述: MapImageService接口实现类 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2012-8-7 上午11:44:04 
 * @版本: V1.0   
 */
@SuppressWarnings("all")
public class MapImageServiceImpl implements MapImageService {

	
	private GoogleMapInitConfig gmc;
	   /**
	   * 合并已经下载的图像。
	   * @param sourceFolder String 源图片目录
	   * @param targetFolder String 结果存放目录
	   * @param totalRowCount int 总共有多少行
	   * @param totalColumnCount int 总共有多少列
	   */
	@Override
	public void mergeImage(String sourceFolder, String targetFolder,int totalRowCount, int totalColumnCount,int startX,int startY) {
		 BufferedImage imageNew = new BufferedImage(totalColumnCount*256,totalRowCount*256,BufferedImage.TYPE_INT_RGB);     
		 ArrayList<FileMessage> fmList = getFileMessageList(sourceFolder,".png");
         if(null!=fmList&&fmList.size()>0){
	    	 for(FileMessage fmTemp:fmList){
	    		 String fileName = fmTemp.getFileName();
	    		 String[] xyArray = fileName.substring(0,fileName.indexOf(".")).split("-");
	    		 int row = Integer.parseInt(xyArray[0])-startX;
	    		 int column = Integer.parseInt(xyArray[1])-startY;
	    		 try {
	    			 BufferedImage outputImg = ImageIO.read(new File(fmTemp.getPath()));
	    			 int[] imageArrayOne = new int[256 * 256];
	    			 imageArrayOne = outputImg.getRGB(0, 0, 256, 256, imageArrayOne,0, 256);
	    			 imageNew.setRGB(row*256,column*256,256, 256, imageArrayOne, 0, 256);
				 } catch (IOException e) {
					e.printStackTrace();
				 }   
	    	 }
	    	 writeImageLocalFile(targetFolder+"\\mergeImage.png",imageNew);
	     }
	}
	/**
	 * 根据url获取文件
	 * @param dir
	 * @return
	 */
	public ArrayList<FileMessage> getFileMessageList(String dir,String fileType){
		File fs[] = null;
		File f = new File(dir);
		if (f != null && f.isDirectory()){
			fs = f.listFiles();
			if (fs == null){
				return null;
			}
			for (int i = 0; i < fs.length; i++){
				for (int j = 0; j < fs.length - i - 1; j++)
					if ((new Date(fs[j].lastModified())).getTime() < (new Date(fs[j + 1].lastModified())).getTime())
					{
						File temp = fs[j];
						fs[j] = fs[j + 1];
						fs[j + 1] = temp;
					}
			}
			ArrayList<FileMessage> fmList = new ArrayList<FileMessage>();
			for (int i = 0; i < fs.length; i++){
				FileMessage fm = new FileMessage();
				if (fs[i].getName().indexOf(fileType) == -1)
					continue;
				fm.setFileName(fs[i].getName());
				fm.setType(fileType);
				fm.setPath(fs[i].getPath());
				fm.setFileDate((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(fs[i].lastModified())));
				fmList.add(fm);
			}
			return fmList;
		} else{
			return null;
		}
	}
	
   /**
   * 往指定文件里面写二进制流
   * @param fileName
   * @param image
   */
	public void writeImageLocalFile(String fileName, BufferedImage image) {   
	       if (fileName != null && image != null) {   
	           try {   
	               File file = new File(fileName); 
	               ImageIO.write(image, "png", file);   
	           } catch (IOException e) {   
	               e.printStackTrace();   
	           }   
	       }   
	}
   /**
   * 
   * @param maxLgtd 最大经度
   * @param maxLttd 最大纬度
   * @param minLgtd 最小经度
   * @param minLttd 最小纬度
   * @param z 级数(1-18)
   * @return
   */
	@Override
	public List<String> getGoogleMapImageURLByLgtdAndLttd(double maxLgtd,double maxLttd, double minLgtd, double minLttd, int z) {
		ArrayList<String> retList = new ArrayList<String>();
		FileOutputStream fos = null;
		FileChannel outputChannel = null;
		Date currentDate = new Date();
		File file = new File(gmc.getLogPath()+"\\log"+(new SimpleDateFormat("yyyyMMddHHmmss")).format(currentDate)+".text");
		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}   
		outputChannel = fos.getChannel();
		int[] xyTemp1 = GoogleMapUtils.getXYCodeByLgtdAndLttd(maxLgtd, maxLttd, z);
		int[] xyTemp2 = GoogleMapUtils.getXYCodeByLgtdAndLttd(minLgtd, minLttd, z);
		int i,j,m,n;
		//初始化i,j,m,n
		if(xyTemp1[0]>=xyTemp2[0]){
			i=xyTemp2[0];
			j=xyTemp1[0];
		}else{
			i=xyTemp1[0];
			j=xyTemp2[0];
		}
		if(xyTemp1[1]>=xyTemp2[1]){
			m=xyTemp2[1];
			n=xyTemp1[1];
		}else{
			m=xyTemp1[1];
			n=xyTemp2[1];
		}
		int serverListSize = gmc.getGoogleServerList().size();
		int currentServerIndex = 0;
		for(int a=i;a<=j;a++){
			for(int b=m;b<=n;b++){
				String serverHeader = gmc.getGoogleServerList().get(currentServerIndex);
				String webURL=serverHeader+"&x="+a+"&y="+b+"&z="+z+"";
				writeGoogleMapImageURLToFile(outputChannel,webURL);
				URL url;
				try {
					url = new URL(webURL);
					//new Proxy(Proxy.Type.HTTP, new InetSocketAddress(s[0],Integer.parseInt(s[1])))
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/534.36 (KHTML, like Gecko) Chrome/13.0.766.0 Safari/534.36");
			        conn.setRequestMethod("GET");
			        conn.setConnectTimeout(5*1000);
			        InputStream inStream =  conn.getInputStream();//通过输入流获取图片数据
			        byte[] data = readInputStream(inStream);//得到图片的二进制数据
			        File ImageFile = new File(gmc.getDownLoadPath()+"\\"+a+"-"+b+".png");//存到本地硬盘名为
			        FileOutputStream  outstream = new FileOutputStream(ImageFile);
			        outstream.write(data);
			        outstream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				currentServerIndex++;
				if(currentServerIndex>=serverListSize){
					currentServerIndex = 0;
				}
			}
		}
		try {
			outputChannel.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mergeImage(gmc.getDownLoadPath(),gmc.getDownLoadPath(),n-m+1,j-i+1,i,m);
		return retList;
	}
	/**
	 * 往指定文件里面写
	 */
	@Override
	public boolean writeGoogleMapImageURLToFile(FileChannel outputChannel,String urlStr){
		ByteBuffer bb = ByteBuffer.wrap((urlStr+"\n").getBytes());   
		try {
			outputChannel.write(bb);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public byte[] readInputStream(InputStream instream) throws Exception{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[]  buffer = new byte[1204];
		int len = 0;
		while ((len = instream.read(buffer)) != -1){
		    outStream.write(buffer,0,len);
		}
		instream.close();
		return outStream.toByteArray();        
	}
	
	public void setGoogleMapInitConfig(GoogleMapInitConfig gmc) {
		this.gmc = gmc;
	}
	
}
