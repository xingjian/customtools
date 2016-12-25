package com.promise.pbutil;

import java.sql.Connection;

import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.POIExcelUtil;

/**  
 * 功能描述:POIExcelUtil 测试类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年11月26日 下午1:59:47  
 */
public class POIExcelUtilTest {

    @Test
    public void exportExcelGDDateByName() throws Exception{
        String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        Connection connectionPG = DBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String sqlQuerySub = "select roadid,roadname,timestr,state,speed from gd_road_status_20161001 where roadname='北六环' order by timestr";
        POIExcelUtil.ExportDataBySQLNew(sqlQuerySub, connectionPG, "d:\\lijingexcels\\北六环.xlsx");
    }
}
