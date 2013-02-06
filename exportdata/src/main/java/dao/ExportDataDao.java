/** @文件名: ExportDataDao.java @创建人：邢健  @创建日期： 2013-2-5 下午1:18:56 */

package dao;

import java.util.List;

import util.DataSourceType;

import bean.ADPlanB;

/**   
 * @类名: ExportDataDao.java 
 * @包名: dao.impl 
 * @描述: 接口 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-2-5 下午1:18:56 
 * @版本: V1.0   
 */
public interface ExportDataDao {
	public boolean exportBlob(String fileName,String fileType);
	public boolean testConnection(DataSourceType dst,String url,String userName,String password);
	public List<ADPlanB> getAllADPlanB(DataSourceType dst,String url,String userName,String password,String tableName,String columName);
	public boolean exportWord(DataSourceType dst,String url,String userName,String password,String tableName,String columName,String exportURL);
}
