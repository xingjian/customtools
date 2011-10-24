/*@文件名: TestCreateDataService.java  @创建人: 邢健   @创建日期: 2011-10-24 上午09:28:39*/
package com.promise.cn.service;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.promise.cn.service.impl.CreateDataServiceImpl;
import com.promise.cn.vo.DataConnectConfigVO;

/**   
 * @类名: TestCreateDataService.java 
 * @包名: com.promise.cn.service 
 * @描述: CreateDataService测试
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-24 上午09:28:39 
 * @版本 V1.0   
 */
public class TestCreateDataService extends TestCase{

	private CreateDataService cds;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cds = new CreateDataServiceImpl();
	}
	
	/**
	 * 测试读取配置文件，封装对象
	 */
	@Test
	public void testGetAllDataConnectConfigVO(){
		cds.getAllDataConnectConfigVO();
	}
	
	/**
	 * 测试数据库连接正确性
	 */
	@Test
	public void testCheckDataConnectVO(){
		DataConnectConfigVO dccTemp = new DataConnectConfigVO();
		dccTemp.setName("testOracle");
		dccTemp.setDriverClassName("oracle.jdbc.OracleDriver");
		dccTemp.setPassword("sde");
		dccTemp.setUrl("jdbc:oracle:thin:@172.18.18.203:1521:emdb");
		dccTemp.setUserName("sde");
		boolean booTemp = cds.checkDataConnectVO(dccTemp);
		Assert.assertTrue(booTemp);
	}
}
