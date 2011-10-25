/*@文件名: CreateDataServiceImpl.java  @创建人: 邢健   @创建日期: 2011-10-20 下午01:23:12*/
package com.promise.cn.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.promise.cn.service.CreateDataService;
import com.promise.cn.vo.DataConnectConfigVO;
import com.promise.cn.vo.TableConfigVO;

/**   
 * @类名: CreateDataServiceImpl.java 
 * @包名: com.promise.cn.service.impl 
 * @描述: 构造数据服务对象接口实现类 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-20 下午01:23:12 
 * @版本 V1.0   
 */
public class CreateDataServiceImpl implements CreateDataService {

	/**
	 * 保存数据配置对象到data_connect.xml
	 */
	@Override
	public boolean saveDataConnectVO(DataConnectConfigVO dccVO) {
		try {
			String filePath = CreateDataServiceImpl.class.getResource("/data_connect.xml").getPath();
			XMLWriter writer = new XMLWriter(new FileWriter(filePath));
			Document document = null;
			document = DocumentHelper.parseText(dccVO.toString());
			writer.write(document);
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}

	/**
	 * 验证数据配置对象正确性
	 */
	@Override
	public boolean checkDataConnectVO(DataConnectConfigVO dccVO) {
		Connection connect = null;
		try {
			Class.forName(dccVO.getDriverClassName());
			connect = DriverManager.getConnection(dccVO.getUrl(),dccVO.getUserName(),dccVO.getPassword());
			if(connect!=null){
				if(!connect.isClosed()){
					connect.close();
				}
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除数据配置对象
	 */
	@Override
	public boolean deleteDataConnectVO(DataConnectConfigVO dccVO) {
		return false;
	}

	/**
	 * 编辑数据配置对象
	 */
	@Override
	public boolean editDataConnectVO(DataConnectConfigVO dccVO) {
		return false;
	}

	@Override
	public boolean saveDataConnectVOList(List<DataConnectConfigVO> dccVOList) {
		StringBuffer result = new StringBuffer();
		result.append("<datasources>"+"\n");
		for(int i=0;i<dccVOList.size();i++){
			result.append(dccVOList.get(i).toString());
		}
		result.append("</datasources>");
		String filePath = CreateDataServiceImpl.class.getResource("/data_connect.xml").getPath();
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileWriter(filePath));
			Document document = null;
			document = DocumentHelper.parseText(result.toString());
			writer.write(document);
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 解析data_connnect.xml,转换成DataConnectConfigVO集合
	 */
	@SuppressWarnings({"all"})
	@Override
	public List<DataConnectConfigVO> getAllDataConnectConfigVO() {
		List<DataConnectConfigVO> dccList = new ArrayList<DataConnectConfigVO>();
		 SAXReader reader = new SAXReader();
		 String filePath = CreateDataServiceImpl.class.getResource("/data_connect.xml").getPath();
         try {
			Document  document = reader.read(new File(filePath));
			Element rootElm = document.getRootElement();
			List nodes = rootElm.elements("datasource");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				   Element elm = (Element) it.next();
				   String name = elm.elementText("name");
				   String userName = elm.elementText("userName");
				   String password = elm.elementText("password");
				   String driverClassName = elm.elementText("driverClassName");
				   String url = elm.elementText("url");
				   DataConnectConfigVO dccTemp = new DataConnectConfigVO();
				   dccTemp.setName(name);
				   dccTemp.setUrl(url);
				   dccTemp.setUserName(userName);
				   dccTemp.setPassword(password);
				   dccTemp.setDriverClassName(driverClassName);
				   dccList.add(dccTemp);
				}

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return dccList;
	}
	
	/**
	 * 通过
	 */
	@Override
	public boolean createSqlByList(List<TableConfigVO> list, String path,String tableName,int count) {
		File file  = null;
		FileOutputStream fos = null;
		FileChannel outputChannel = null;
		if(null!=path&&!path.equals("")){
			file = new File(path);
			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}   
			outputChannel = fos.getChannel();   
		}
		
		for(Iterator<TableConfigVO> iterator = list.iterator();iterator.hasNext();){
			TableConfigVO tcvTemp = iterator.next();
			tcvTemp.setTableName(tableName);
			ByteBuffer bb = ByteBuffer.wrap("test".getBytes());   
			try {
				outputChannel.write(bb);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

}
