/** @文件名: DataSourceFactoryTest.java @创建人：邢健  @创建日期： 2013-2-5 上午11:59:15 */

package test;

import java.sql.Connection;

import org.junit.Test;

import util.DataSourceFactory;
import util.DataSourceType;

/**   
 * @类名: DataSourceFactoryTest.java 
 * @包名: test 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-2-5 上午11:59:15 
 * @版本: V1.0   
 */
public class DataSourceFactoryTest {

	@Test
	public void testGetConnection(){
		String url = "jdbc:sqlserver://172.18.18.34:1433;databaseName=floodwarncity";
		Connection con = DataSourceFactory.getConnection(DataSourceType.SQLSERVER, url, "floodwarn", "floodwarn");
		con.toString();
	}

}
