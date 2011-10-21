/*@文件名: CreateDataServiceImpl.java  @创建人: 邢健   @创建日期: 2011-10-20 下午01:23:12*/
package com.promise.cn.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

import com.promise.cn.service.CreateDataService;
import com.promise.cn.vo.DataConnectConfigVO;

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
		return false;
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
		result.append("<datasources>");
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

	@Override
	public List<DataConnectConfigVO> getAllDataConnectConfigVO() {
		return null;
	}

}
