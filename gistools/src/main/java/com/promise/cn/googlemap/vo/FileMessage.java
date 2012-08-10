/*@文件名: FileMessage.java  @创建人: 邢健   @创建日期: 2012-3-12 下午03:52:58*/
package com.promise.cn.googlemap.vo;

import java.io.Serializable;

/**   
 * @类名: FileMessage.java 
 * @包名: com.promise.cn.googlemap.vo 
 * @描述: TODO(用一句话描述该文件做什么) 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2012-3-12 下午03:52:58 
 * @版本 V1.0   
 */
@SuppressWarnings("all")
public class FileMessage implements Serializable{
	
	private String fileName;
	
	private String fileDate;
	
	private String type;
	
	private String path;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDate() {
		return fileDate;
	}

	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}
