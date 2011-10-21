/*@文件名: CreateDataService.java  @创建人: 邢健   @创建日期: 2011-10-20 下午01:22:19*/
package com.promise.cn.service;

import java.util.List;

import com.promise.cn.vo.DataConnectConfigVO;

/**   
 * @类名: CreateDataService.java 
 * @包名: com.promise.cn.service 
 * @描述: 创建服务对象接口 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-20 下午01:22:19 
 * @版本 V1.0   
 */
public interface CreateDataService {

	/**
	 * 保存数据连接对象
	 * @param dccVO
	 * @return
	 */
	public boolean saveDataConnectVO(DataConnectConfigVO dccVO);
	/**
	 * 验证数据配置连接正确性
	 */
	public boolean checkDataConnectVO(DataConnectConfigVO dccVO);
	/**
	 * 删除配置连接
	 * @param dccVO
	 * @return
	 */
	public boolean deleteDataConnectVO(DataConnectConfigVO dccVO);
	/**
	 * 编辑配置连接对象
	 * @param dccVO
	 * @return
	 */
	public boolean editDataConnectVO(DataConnectConfigVO dccVO);
	/**
	 * 批量保存数据连接对象
	 * @param dccVO
	 * @return
	 */
	public boolean saveDataConnectVOList(List<DataConnectConfigVO> dccVOList);
	/**
	 * 批量获取数据源连接
	 */
	public List<DataConnectConfigVO> getAllDataConnectConfigVO();
}
