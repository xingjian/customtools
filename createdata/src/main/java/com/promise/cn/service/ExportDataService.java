/** @文件名: ExportDataService.java @创建人：邢健  @创建日期： 2013-3-5 下午4:08:37 */

package com.promise.cn.service;

import java.util.List;

import com.promise.cn.vo.DataConnectConfigVO;
import com.promise.cn.vo.TableVO;

/**   
 * @类名: ExportDataService.java 
 * @包名: com.promise.cn.service 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-3-5 下午4:08:37 
 * @版本: V1.0   
 */
public interface ExportDataService {

	public List<TableVO> getTableVOListByDCC(DataConnectConfigVO dcc);
}
