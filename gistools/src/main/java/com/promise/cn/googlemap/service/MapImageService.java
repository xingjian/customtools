/** @文件名: MapImageService.java @创建人：邢健  @创建日期： 2012-8-7 上午11:36:50 */

package com.promise.cn.googlemap.service;

import java.awt.image.BufferedImage;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.promise.cn.googlemap.vo.FileMessage;

/**   
 * @类名: MapImageService.java 
 * @包名: com.promise.cn.googlemap.service 
 * @描述: 图片处理
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2012-8-7 上午11:36:50 
 * @版本: V1.0   
 */
public interface MapImageService {

	   /**
	   * 合并已经下载的图像。
	   * @param sourceFolder String 源图片目录
	   * @param targetFolder String 结果存放目录
	   * @param totalRowCount int 总共有多少行
	   * @param totalColumnCount int 总共有多少列
	   * @param rowSplit int 行被分成的块数
	   * @param columnSplit int 列被分成的块数
	   */
	  public void mergeImage(String sourceFolder,String targetFolder,int totalRowCount,int totalColumnCount,int startX,int startY);
	  /**
	   * 根据文件路径获取文件
	   * @param dir
	   * @return
	   */
	  public ArrayList<FileMessage> getFileMessageList(String dir,String fileType);
	  /**
	   * 往指定文件里面写二进制流
	   * @param fileName
	   * @param image
	   */
	  public void writeImageLocalFile(String fileName, BufferedImage image);
	  /**
	   * 
	   * @param maxLgtd 最大经度
	   * @param maxLttd 最大纬度
	   * @param minLgtd 最小经度
	   * @param minLttd 最小纬度
	   * @param z 级数(1-18)
	   * @return
	   */
	  public List<String> getGoogleMapImageURLByLgtdAndLttd(double maxLgtd,double maxLttd,double minLgtd,double minLttd,int z);
	  /**
	   * 保存url
	   */
	  public boolean writeGoogleMapImageURLToFile(FileChannel outputChannel,String urlStr);
}
