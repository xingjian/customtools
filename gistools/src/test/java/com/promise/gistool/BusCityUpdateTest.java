package com.promise.gistool;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.StringUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述: 更新公交都市gis数据和公交线路数据（根据于海涛部门提供的导航数据和公交线路数据）
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年10月21日 下午2:21:01  
 */
public class BusCityUpdateTest {

    /**
     * 更新于海涛提供的导航数据到数据库中
     */
    @Test
    public void importNavigationLine() throws Exception{
        String shapePath = "G:\\项目文档\\公交都市\\于海涛导航\\2014-beijing\\navigation_road_bj.shp";
        String dateStr = StringUtil.GetDateString("yyyyMMddhhmm", new Date());
        String ip = "192.168.1.105";
        String port = "5432";
        String dataBaseName = "basedata";
        String userName = "basedata";
        String password = "basedata";
        String updateTableName = "alter table if exists navigation_road_bj  rename to navigation_road_bj"+dateStr;
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://"+ip+":"+port+"/"+dataBaseName, userName, password);
        //备份表数据
        Statement statement = connection.createStatement();
        statement.execute(updateTableName);
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS(ip, port, dataBaseName, userName, password,"public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "navigation_road_bj", MultiLineString.class, "EPSG:4326");
        System.out.println(result);
    }
    
    
    /**
     * 更新于海涛提供的node_road_bj到数据库中
     */
    @Test
    public void importNodeRoadBJ() throws Exception{
        String shapePath = "G:\\项目文档\\公交都市\\于海涛导航\\2014-beijing\\node_road_bj.shp";
        String dateStr = StringUtil.GetDateString("yyyyMMddhhmm", new Date());
        String ip = "192.168.1.105";
        String port = "5432";
        String dataBaseName = "basedata";
        String userName = "basedata";
        String password = "basedata";
        String updateTableName = "alter table if exists node_road_bj  rename to node_road_bj"+dateStr;
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://"+ip+":"+port+"/"+dataBaseName, userName, password);
        //备份表数据
        Statement statement = connection.createStatement();
        statement.execute(updateTableName);
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS(ip, port, dataBaseName, userName, password,"public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "node_road_bj", Point.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void createLineByPoints(){
       List<String> list = PBFileUtil.ReadFileByLine("G:\\项目文档\\公交都市\\公交专用道\\数据\\886.txt");
       List<BusLineSegment> listBLS = new ArrayList<BusLineSegment>();
       int index = 0;
       for(String s:list){
           String[] arr = s.split(",");
           BusLineSegment bls = new BusLineSegment();
           bls.setIndex(index);
           index++;
           bls.setLineCode(arr[0]);
           bls.setLineNum(arr[4]);
           bls.setSpoint(arr[9]);
           bls.setEpoint(arr[10]);
           bls.setWkt("MULTILINESTRING(("+arr[9]+","+arr[10]+"))");
           listBLS.add(bls);
       }
       String crs = "EPSG:4326";
       String encoding = "GBK";
       //geometry type 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
       String geometryType = "4";
       String[] attriDesc = new String[]{"lineCode:String","lineNum:String","spoint:String","epoint:String", "the_geom:geometry-wkt"};
       GeoShapeUtil.ListObjectToShapeFile(listBLS, "G:\\项目文档\\公交都市\\公交专用道\\数据\\temp\\bls.shp", encoding, geometryType, "wkt", crs);
       
       
       
       for(String str:list){
           String[] arr = str.split(",");
           String point1str = arr[9];
           String point2str = arr[10];
           System.out.println(point1str+"----"+point2str);
           
       }
    }
}
