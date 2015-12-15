package com.promise.pbutil;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.PrintUtil;

/**  
 * 功能描述:PBFileUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月7日 上午9:33:41  
 */
public class PBFileUtilTest {

    @Test
    public void testReadPropertiesFile(){
        String filePath = "G:\\项目文档\\公交都市\\数据\\busline.properties";
        Map<String,String> map= PBFileUtil.ReadPropertiesFile(filePath);
        PrintUtil.PrintObject(map);
    }
    
    @Test
    public void testExportDataBySQL(){
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql = "select array_to_string(ARRAY(SELECT unnest(array_agg(wkt))),',') wkts,array_to_string(ARRAY(SELECT unnest(array_agg(busstationid)) "+
            "),',') busstationids ,linkid,name from (select rank() OVER (PARTITION BY t1.id ORDER BY t1.id) rank,ST_AsText(t1.the_geom) wkt,t1.id,t1.busstationid,t1.linkid,t2.name from busstationlink  t1 left join busstation t2 on t1.busstationid= t2.id order by name desc) t3 group by linkid,name";
       String excelPath = "d:\\result.xls";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
}
