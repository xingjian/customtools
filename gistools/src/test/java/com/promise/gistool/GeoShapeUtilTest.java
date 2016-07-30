package com.promise.gistool;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.promise.cn.util.DBConnection;
import com.promise.cn.util.POIExcelUtil;
import com.promise.gistool.util.ConversionUtil;
import com.promise.gistool.util.GISDBUtil;
import com.promise.gistool.util.GeoShapeUtil;
import com.promise.gistool.util.GeoToolsGeometry;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述: GeoShapeUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月9日 下午1:43:14  
 */
public class GeoShapeUtilTest {

    @Test
    public void testAppendFeatureToShapeFile(){
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures("F:\\gitworkspace\\customtools\\gistools\\src\\main\\resources\\huanxian1.shp", "GBK");
        SimpleFeatureIterator sfi = sfc.features();
        List<SimpleFeature> list = new ArrayList<SimpleFeature>();
        while(sfi.hasNext()){
            list.add(sfi.next());
        }
        List<String> fileds = new ArrayList<String>();
        fileds.add("the_geom:the_geom");
        GeoShapeUtil.AppendFeatureToShapeFile(list, "F:\\gitworkspace\\customtools\\gistools\\src\\main\\resources\\huanxian2.shp", fileds, "GBK");
    }
    
    @Test
    public void testExportTableToShape(){
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://192.168.1.105:5432/buscity", "buscity", "bs789&*(");
        String sql_query = "select buslineid,st_astext(the_geom) wkt from buslinelink where buslineid in ("+
                "select buslineid from buslinelink_merge t1 where t1.id in ("+
                "select ref_id from td_line_station_ref_wc t2 where t2.tdname='四环辅路'))";
        String shapeFilePath = "d:\\4ringbuslinelink.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
        
    }
    @Test
    public void testExportTableToShapeChannelFL(){
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://ttyjbj.ticp.net:5432/basedata", "basedata", "basedata!@#&*(");
        String sql_query = "select id,navigationid,channelid, st_astext(the_geom) wkt from busline_channel_link where channelid in (select id from busline_channel where ldmc like '%辅路%')";
        String shapeFilePath = "d:\\tongdaofulu.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
    }
    @Test
    public void testCreateShapeByTxt(){
        String txtpath = "F:\\xy.txt";
        String splitChar = ";";
        String crs = "EPSG:4326";
        String encoding = "GBK";
        //geometry type 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
        String geometryType = "1";
        String topath = "F:\\export.shp";
        String[] attriDesc = new String[]{"gid:int","netid:String","areaid:String","name:String","type:String","lockid:double","starttime:double","site:String","status:double","mark:String","street:String",
                "x:double","y:double", "bjx:double", "bjy:double","the_geom:geometry-wkt"};
        GeoShapeUtil.CreateShapeByTxt(txtpath, splitChar, crs, encoding, attriDesc, topath, geometryType);
    }
    
    @Test
    public void testReadShapeFileFeatures() throws Exception{
        String shapePath = "G:\\项目文档\\微波路段\\城市道路84+北京地方\\城市道路全属性_北京地方\\北京地方全属性\\Road_LN_02.shp";
        SimpleFeatureCollection sfc = GeoShapeUtil.ReadShapeFileFeatures(shapePath, "GBK");
        SimpleFeatureIterator sfi = sfc.features();
        while(sfi.hasNext()){
            SimpleFeature sf = sfi.next();
            System.out.println(GeoToolsGeometry.FeatureToJSON(sf));
        }
    }
    
    
    /**
     * 导出四环的公交车路链
     */
    @Test
    public void testExportTableToShapeKJF(){
        String url = "jdbc:postgresql://localhost:5432/mobile";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String sql_query = "select t1.id,t1.qxm,t1.fid,st_astext(t1.the_geom) wkt,t2.count from zxcxzhzhydflqc t1 left join result_home201409_result5h1 t2 on t1.fid=t2.fid";
        String shapeFilePath = "d:\\result_home201409_result5h2.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","6","EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testExportTrainLine(){
        String sql = "select buslineid,st_astext(the_geom) wkt from buslinelink where buslineid in (select id from busline where sygs='地铁')";
        String url = "jdbc:postgresql://192.168.1.105:5432/buscity";
        String username = "buscity";
        String passwd = "bs789&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String shapeFilePath = "d:\\buslinelink_subline.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testExportBusstation(){
        String sql = "select t1.id,t1.busstationid,st_astext(t1.the_geom) wkt,t1.linkid,t2.name from ("+
                    " select * from busstationlink where busstationid in (select id from busstation where buslineid in (select id from busline where isvalid='1')) "+
                       " ) t1 left join busstation t2 on t1.busstationid=t2.id";
        String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
        String username = "basedata";
        String passwd = "basedata";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String shapeFilePath = "d:\\busstationlink0110.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql, shapeFilePath,"GBK","1","EPSG:4326");
        System.out.println(result);
    }
    
    
    @Test
    public void testShapeToPostGis(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\给交委数据\\北京_公交站位_font_point.shp";
        DataStore dataStore = GISDBUtil.GetDataStoreFromPostGIS( "localhost", "5432", "pollutionreduction", "postgis", "postgis","public");
        String result = ConversionUtil.ShapeToPostGIS(shapePath, dataStore, "GBK", "busstation_zhanwei", Point.class, "EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testExportShapeToExcel(){
        String shapePath = "G:\\项目文档\\公交都市\\giss数据\\给交委数据\\北京_公交站位_font_point.shp";
        String excelPath = "D:\\北京_公交站位.xls";
        boolean result = GeoShapeUtil.ExportShapeToExcel(shapePath, excelPath, "GBK", false);
        System.out.println(result);
    }
    
    @Test
    public void testExportExcelBusstationzhanwei(){
        String sql = "SELECT fid, \"标注\", \"名称\", \"DATAID\", \"途经车次\", \"站序号\", \"节点序号\", \"分公司\", \"REGIONALIS\", \"RINGPOSITI\", \"ISINMAINRO\", \"DIRECT\", \"STAPOS\", \"IFLED\", \"IFVIDEO\",  st_x(the_geom),st_y(the_geom) FROM busstation_zhanwei";
        String url = "jdbc:postgresql://localhost:5432/pollutionreduction";
        String username = "postgis";
        String passwd = "postgis";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String excelPath = "d:\\北京公交站位.xls";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    @Test
    public void testData_wxh6(){
        String url = "jdbc:postgresql://ttyjbj.ticp.net:5432/basedata";
        String username = "basedata";
        String passwd = "basedata!@#&*(";
        Connection connection = DBConnection.GetPostGresConnection(url, username, passwd);
        String excelPath = "d:\\busstation_adcd_no.xls";
        String sql = "select tjcc as label, name,adcd_code adcdcode,stationid,adcd_name from busstation_not_adcd order by tjcc,stationid asc";
       String result = POIExcelUtil.ExportDataBySQL(sql, connection, excelPath);
    }
    
    /**
     * 北航线路图形数据
     */
    @Test
    public void testExportTableToShapeBH(){
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://ttyjbj.ticp.net:5432/basedata", "basedata", "basedata!@#&*(");
        String sql_query = "select t2.name,t2.runtime, t2.price, t2.chargemode, t2.wrsp, t2.length, t2.monthlyticket, t2.ybc, t2.gfc, t2.sygs, t2.sj, t2.fcjg, t2.qbj, t2.dzj, t2.twoway, t2.arrow, t2.xlbh, t2.wz, t2.rx, t2.dzgj, t2.qt, t2.dd, t2.ssbb,t2.linename, t2.linecode,ST_AsText(t1.the_geom) wkt"
                +" from buslinelink t1,busline t2 where 1=1 and t1.buslineid = t2.id and t1.buslineid in("
                +" select a.buslineid from busstation a,busline b,busstationlink c"
                +" where 1=1 and a.id = c.busstationid"
                +" and a.buslineid = b.id and b.state = '5'"
                +" and ST_Within(c.the_geom,st_geometryfromtext('MULTIPOLYGON(((116.37294010228365 39.90772508304188,116.41894535130709 39.908646821293196,116.41915992802826 39.9006157601075,116.37345508641451 39.89939595726065,116.37294010228365 39.90772508304188)))',4326)) "
                +" and ("
                +" (starttime <= to_date('2015-01-01', 'YYYY-MM-DD') and endtime >= to_date('2015-01-31', 'YYYY-MM-DD')) or (starttime <= to_date('2015-05-01', 'YYYY-MM-DD') and endtime >= to_date('2015-05-31', 'YYYY-MM-DD'))"
                +"))";
        String shapeFilePath = "d:\\bh_busline.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testExportTableToShapeTJ(){
        String sql_query = "select t2.name,t2.runtime, t2.price, t2.chargemode, t2.wrsp, t2.length, t2.monthlyticket, t2.ybc, t2.gfc, t2.sygs, t2.sj, t2.fcjg, t2.qbj, t2.dzj, t2.twoway, t2.arrow, t2.xlbh, t2.wz, t2.rx, t2.dzgj, t2.qt, t2.dd, t2.ssbb,t2.linename, t2.linecode,ST_AsText(t1.the_geom) wkt"
                +" from buslinelink t1,busline t2" 
                +" where 1=1"
                +" and t1.buslineid = t2.id"
                +" and t1.buslineid in("
                +"   select a.buslineid"
                +"   from busstation a,busline b"
                +"   where 1=1"
                +"   and a.buslineid = b.id"
                +"   and b.state = '5'"
                +"   and a.name in ('火器营','长春桥','车道沟桥','地铁慈寿寺站','西钓鱼台','公主坟','莲花桥','六里桥西','西局','前泥洼','丰台站','地铁首经贸站','纪家庙','草桥','地铁角门西站')"
                +"   and ("
                +"    (starttime <= to_date('2015-10-16', 'YYYY-MM-DD') and endtime >= to_date('2015-10-16', 'YYYY-MM-DD')) or (starttime <= to_date('2015-10-23', 'YYYY-MM-DD') and endtime >= to_date('2015-10-23', 'YYYY-MM-DD'))"
                +"   )"
                +" )";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://ttyjbj.ticp.net:5432/basedata", "basedata", "basedata!@#&*(");
        String shapeFilePath = "d:\\TJ_busline.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testExportTableShapeGD(){
        String sql_query = "select t2.name,t2.runtime, t2.price, t2.chargemode, t2.wrsp, t2.length, t2.monthlyticket, t2.ybc, t2.gfc, t2.sygs, t2.sj, t2.fcjg, t2.qbj, t2.dzj, t2.twoway, t2.arrow, t2.xlbh, t2.wz, t2.rx, t2.dzgj, t2.qt, t2.dd, t2.ssbb,t2.linename, t2.linecode,ST_AsText(t1.the_geom) wkt"
                    +" from buslinelink t1,busline t2 "
                    +" where 1=1 and t2.isvalid='1'"
                    +" and t1.buslineid = t2.id";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://ttyjbj.ticp.net:5432/basedata", "basedata", "basedata!@#&*(");
        String shapeFilePath = "d:\\GD_busline.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testExportTableShapeGD11(){
        String sql_query = "SELECT t2.id, t2.name, t2.runtime, t2.price,"+ 
               " t2.chargemode, t2.wrsp, t2.length, t2.monthlyticket, t2.ybc, t2.gfc, t2.sygs, t2.sj, "+
               " t2.fcjg, t2.qbj, t2.dzj, t2.twoway, t2.linenumber, t2.linenum, t2.arrow, t2.xlbh, t2.wz, "+
               " t2.rx, t2.dzgj, t2.qt, t2.dd, t2.ssbb, t2.version, t2.linename, t2.linecode, t2.starttime, t2.endtime, t2.uuidversion,"+
               " t2.ykt_sxx, t2.sygscode, t2.xllx_code, t2.lineno,ST_AsText(t1.the_geom) wkt"+
               " from buslinelink t1,busline t2 "+
               " where 1=1"+
               " and t1.buslineid = t2.id"+
               " and t2.state = '5'"+
               " and t2.starttime <= to_date('2015-12-01', 'YYYY-MM-DD') and t2.endtime > to_date('2015-12-31', 'YYYY-MM-DD')"+
               " order by t2.name";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://ttyjbj.ticp.net:5432/basedata", "basedata", "basedata!@#&*(");
        String shapeFilePath = "d:\\GD_busline_201512.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
    }
    
    @Test
    public void testExportTableShapeNotInCHanel(){
        String sql_query = "select id,channelid,name,st_astext(the_geom) wkt from busline_channel_section t1 where t1.channelid not in (select id from busline_channel)";
        Connection connection = DBConnection.GetPostGresConnection("jdbc:postgresql://ttyjbj.ticp.net:5432/basedata", "basedata", "basedata!@#&*(");
        String shapeFilePath = "d:\\busline_channel_section_not.shp";
        String result = GeoShapeUtil.ExportTableToShape(connection, sql_query, shapeFilePath,"GBK","4","EPSG:4326");
        System.out.println(result);
    }
    
}
