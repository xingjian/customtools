/** @文件名: DataSourceType.java @创建人：邢健  @创建日期： 2013-2-5 上午10:25:21 */

package util;

/**   
 * @类名: DataSourceType.java 
 * @包名: util 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-2-5 上午10:25:21 
 * @版本: V1.0   
 */
public enum DataSourceType {
	ORACLE("oracle"),SQLSERVER("sqlserver"),MYSQL("mysql");
	
	private String type;
	
	private DataSourceType(String type){
		this.type = type;
	}
}
