/*@文件名: TestCreateDataUtil.java  @创建人: 邢健   @创建日期: 2011-10-25 下午04:05:46*/
package com.promise.cn.util;

import org.junit.Test;

import junit.framework.TestCase;

/**   
 * @类名: TestCreateDataUtil.java 
 * @包名: com.promise.cn.util 
 * @描述: CreateDataUtil测试类 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-25 下午04:05:46 
 * @版本 V1.0   
 */
public class TestCreateDataUtil extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * 测试随机整型区间数
	 */
	@Test
	public void testGetRandomInt(){
		for(int i=0;i<=100;i++){
			System.out.println(CreateDataUtil.getRandomInt(5, 5));
		}
	}
	
	/**
	 * 测试小数点格式
	 */
	@Test
	public void testGetDecimalPointFormat(){
		System.out.println(CreateDataUtil.getDecimalPointFormat(1));
	}
	
	/**
	 * 测试double区间随机数
	 */
	@Test
	public void testGetRandomDouble(){
		for(int i=0;i<=10000;i++){
			System.out.println(CreateDataUtil.getRandomDouble(0.5, 0.9, 2));
		}
	}
	
	/**
	 * 测试随机字符串
	 */
	@Test
	public void testGetRandomString(){
		for(int i=0;i<=100;i++){
			System.out.println(CreateDataUtil.getRandomString(20));
		}
		for(int i=0;i<=100;i++){
			System.out.println(CreateDataUtil.getRandomString("dhcc", 20, "left"));
		}
		for(int i=0;i<=100;i++){
			System.out.println(CreateDataUtil.getRandomString("dhcc", 20, "right"));
		}
	}
	
	@Test
	public void testGetRemainder(){
		int b = 3;
		for(int a=0;a<=100;a++){
			System.out.println(CreateDataUtil.getRemainder(a,b));
		}
	}
}
