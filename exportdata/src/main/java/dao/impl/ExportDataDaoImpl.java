/** @文件名: ExportDataDaoImpl.java @创建人：邢健  @创建日期： 2013-2-5 下午1:23:59 */

package dao.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import util.DataSourceFactory;
import util.DataSourceType;
import bean.ADPlanB;
import dao.ExportDataDao;

/**   
 * @类名: ExportDataDaoImpl.java 
 * @包名: dao.impl 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-2-5 下午1:23:59 
 * @版本: V1.0   
 */
public class ExportDataDaoImpl implements ExportDataDao {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdff = new SimpleDateFormat("yyyyMMddHHmmss");
	@Override
	public boolean exportBlob(String fileName, String fileType) {
		return false;
	}

	@Override
	public boolean testConnection(DataSourceType dst,String url,String userName,String password) {
		Connection  con = DataSourceFactory.getConnection(dst,url,userName,password);
		if(null!=con){
			return true;
		}
		return false;
	}

	@Override
	public List<ADPlanB> getAllADPlanB(DataSourceType dst,String url,String userName,String password,String tableName,String columName) {
		Connection  con = DataSourceFactory.getConnection(dst,url,userName,password);
		List<ADPlanB> list = new ArrayList<ADPlanB>();
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select adcd,title,pubtm,planID from "+tableName);
			if(null!=rs){
				while(rs.next()){
					String adcd = rs.getString(1);
					String title = rs.getString(2);
					Date date = rs.getDate(3);
					String dateStr = sdf.format(date);
					int planID = rs.getInt(4);
					ADPlanB adp = new ADPlanB();
					adp.setId(adcd);
					adp.setName(title);
					adp.setPubTime(dateStr);
					adp.setPlanID(planID);
					list.add(adp);
				}
			}
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean exportWord(DataSourceType dst, String url, String userName,
			String password, String tableName, String columName,String exportURL) {
		Connection  con = DataSourceFactory.getConnection(dst,url,userName,password);
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select adcd,title,pubtm,planID,FILECONTENT from "+tableName);
			if(null!=rs){
				while(rs.next()){
					String title = rs.getString(2);
					File fileDir = new File(exportURL);
					if (!fileDir.exists()) {
						fileDir.mkdirs();
					}
					File file = new File(exportURL + title+ ".doc");
					InputStream is = rs.getBinaryStream(5);
					if(null!=is){
						DataInputStream in = new DataInputStream(is);
						DataOutputStream out = new DataOutputStream(
								new FileOutputStream(file));
						int c = 0;
						byte buffer[] = new byte[4096];
						while ((c = in.read(buffer, 0, buffer.length)) != -1) {
							out.write(buffer, 0, c);
						}
						out.flush();
						out.close();
						is.close();
					}
				}
			}
			st.close();
			con.close();
			return true;
			}catch (Exception e) {
				e.printStackTrace();
			}
		return false;
	}

}
