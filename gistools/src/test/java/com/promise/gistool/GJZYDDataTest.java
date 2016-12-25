package com.promise.gistool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISDBUtil;
import com.vividsolutions.jts.geom.MultiLineString;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年10月20日 下午12:28:05  
 */
public class GJZYDDataTest {
    
    /**
     * 导入三环数据到数据库
     */
    @Test
    public void importSanHuan(){
        String shapePath = "G:\\项目文档\\公交都市\\公交专用道\\gis\\sanhuan_zyd.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS("192.168.1.105", "5432", "basedata", "basedata", "basedata","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "sanhuan_zyd", MultiLineString.class, "EPSG:4326");
        System.out.println(result);
    }
    
    /**
     * 根据snode和enode区分内外环
     */
    @Test
    public void exportSanHuanLinks() throws Exception{
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select idlink,snodeid,enodeid,direction from sanhuan_zyd";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        Map<String,Link> mapLink1 = new HashMap<String,Link>();
        Map<String,Link> mapLink2 = new HashMap<String,Link>();
        String startNodeID = "";
        while(rs.next()){
            String idlink = rs.getString(1);
            
            String snodeid = rs.getString(2);
            String enodeid = rs.getString(3);
            String direction = rs.getString(4);
            
            Link linkTemp = new Link();
            linkTemp.setLinkid(idlink);
            if(direction.equals("3")){
                linkTemp.setSnodeid(enodeid);
                linkTemp.setEnodeid(snodeid);
            }else{
                linkTemp.setSnodeid(snodeid);
                linkTemp.setEnodeid(enodeid);
            }
            if(startNodeID.equals("")){
                startNodeID = linkTemp.getSnodeid();
            }
            mapLink1.put(linkTemp.getSnodeid(), linkTemp);
        }
        boolean flag = true;
        
        String s_id = startNodeID;
        while(flag){
            Link linkTemp = mapLink1.get(s_id);
            String s_idTemp = s_id;
            mapLink2.put(s_id, linkTemp);
            s_id = linkTemp.getEnodeid();
            System.out.println(s_idTemp+"("+linkTemp.getLinkid()+")------"+s_id);
            mapLink1.remove(linkTemp);
            if(null==mapLink1.get(s_id)){
                flag=false;
            }
        }
        System.out.println("内外环分类完成。");
//        System.out.println("maplink1---------");
//        for (Map.Entry<String, Link> entry : mapLink1.entrySet()) {
//            System.out.println(entry.getKey() + "--->" + entry.getValue().getEnodeid());
//        }
//        System.out.println("maplink2---------");
//        for (Map.Entry<String, Link> entry : mapLink2.entrySet()) {
//            System.out.println(entry.getKey() + "--->" + entry.getValue().getEnodeid());
//        }
    }
}
