/*@文件名: CreateDataService.java  @创建人: 邢健   @创建日期: 2011-10-20 下午01:22:19*/
package com.promise.cn.service;

import java.util.List;

import com.promise.cn.vo.DataConnectConfigVO;
import com.promise.cn.vo.PolicyVO;
import com.promise.cn.vo.TableConfigVO;

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
	/**
	 * 通过TableConfigVO集合创建sql,并写入指定文件
	 */
	public boolean createInsertSqlByList(List<TableConfigVO> list,String path,String tableName,int count);
	/**
	 * 批量保存策略
	 */
	public boolean savePolicyVOList(List<PolicyVO> policyVOList);
	/**
	 * 批量获取策略
	 */
	public List<PolicyVO> getAllPolicyVO();
	/**
	 * 通过Name返回PolicyVO
	 */
	public PolicyVO getPolicyByName(String name,List<PolicyVO> list);
	/**
	 * 通过数据源获取表
	 */
	public List<String> getTablesByDataSource(DataConnectConfigVO dccv);
}
