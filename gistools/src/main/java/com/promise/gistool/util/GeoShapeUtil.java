package com.promise.gistool.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.v1_1.OGCConfiguration;
import org.geotools.referencing.CRS;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.SAXException;

import com.promise.cn.util.PBFileUtil;
import com.promise.cn.util.POIExcelUtil;
import com.promise.cn.util.StringUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述: GeoTools shape工具类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年9月3日 下午9:08:23  
 */
public class GeoShapeUtil {

    private static FilterFactory2  ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
    
    /**
     * 获取shape文件feature集合
     * @param shapePath shape文件路径
     * @param charSet 读取shape文件编码
     * @return SimpleFeatureCollection
     */
    public static SimpleFeatureCollection ReadShapeFileFeatures(String shapePath,String charSet){
        SimpleFeatureCollection sfc = null;
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore sds = null;
        try {
            sds = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(shapePath).toURI().toURL());
            sds.setCharset(Charset.forName(charSet));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            sfc = featureSource.getFeatures();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            sds.dispose();
        }
        return sfc;
    }
    
    /**
     * 获取FeatureId
     * @param fs
     * @return
     */
    public static Set<FeatureId> GetFeatureId(FeatureSource<SimpleFeatureType, SimpleFeature> fs){
        Set<FeatureId> fids = new HashSet<FeatureId>();
        try {
            FeatureIterator<SimpleFeature> itertor = fs.getFeatures().features();
            while (itertor.hasNext()) {  
                SimpleFeature feature = itertor.next();  
                fids.add(feature.getIdentifier());  
            }  
            itertor.close(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fids;
    }
    
    /**
     * 使用feature id 作为过滤条件
     * @param fs
     * @param fids
     * @return
     * @throws IOException
     */
    public static FeatureCollection FilterByFid(FeatureSource<SimpleFeatureType, SimpleFeature> fs,Set<FeatureId> fids) throws IOException {  
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());  
        Filter filt = (Filter) ff.id(fids);  
        FeatureCollection col = fs.getFeatures(filt);
        return col;  
    }
    
    /**
     * 相等过滤
     * @param fs
     * @param columnName 字段名称
     * @param value 字段值
     * @return
     * @throws IOException
     */
    public static FeatureCollection CompareFilter(FeatureSource<SimpleFeatureType, SimpleFeature> fs,String columnName,String value) throws IOException {  
        Filter left = ff.equals(ff.property( columnName ), ff.literal( value ));
        FeatureCollection col = fs.getFeatures(left);  
        return col;  
    }
    
    /**
     * 
     * @param fs
     * @param sql sql过滤
     * @return
     * Filter f = CQL.toFilter("ATTR1 < 10 AND ATTR2 < 2 OR ATTR3 > 10"); 
     * Filter f = CQL.toFilter("NAME = 'New York' "); 
     * Filter f = CQL.toFilter("NAME LIKE 'New%' "); 
     * Filter f = CQL.toFilter("NAME IS NULL"); 
     * Filter f = CQL.toFilter("DATE BEFORE 2006-11-30T01:30:00Z"); 
     * Filter f = CQL.toFilter("NAME DOES-NOT-EXIST"); 
     * Filter f = CQL.toFilter("QUANTITY BETWEEN 10 AND 20"); 
     * Filter f = CQL.toFilter("CROSSES(SHAPE, LINESTRING(1 2, 10 15))"); 
     * Filter f = CQL.toFilter("BBOX(SHAPE, 10,20,30,40)"); 
     * Expression e = CQL.toExpression("NAME"); 
     * Expression e = CQL.toExpression("QUANTITY * 2"); 
     * Expression e = CQL.toExpression("strConcat(NAME, 'suffix')"); 
     * List filters = CQL.toFilterList("NAME IS NULL;BBOX(SHAPE, 10,20,30,40);INCLUDE"); 
     * @throws CQLException
     * @throws IOException
     */
    public static FeatureCollection FilterCQL(FeatureSource<SimpleFeatureType, SimpleFeature> fs,String sql) throws CQLException, IOException{  
        return fs.getFeatures(CQL.toFilter(sql));  
    }
    
    /**
     * 通过配置xml来实现过滤格式如下：
     * <Filter xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
     *      xmlns:ogc="http://www.opengis.net/ogc"  
     *      xmlns="http://www.opengis.net/ogc"  
     *      xsi:schemaLocation="http://www.opengis.net/ogc filter.xsd">  
     *  <PropertyIsEqualTo>  
     *          <PropertyName>NAME</PropertyName>  
     *          <Literal>13路上行(火车站-市工商局)</Literal>  
     *  </PropertyIsEqualTo>  
     *</Filter>
     * @param fs
     * @param fileName
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static FeatureCollection FilterXML(FeatureSource<SimpleFeatureType, SimpleFeature> fs,String fileName) throws IOException, SAXException, ParserConfigurationException{  
        Configuration configuration = new OGCConfiguration();  
        Parser parser = new Parser(configuration);
        InputStream xml = ClassLoader.getSystemResourceAsStream(fileName);  
        Filter filter = (Filter) parser.parse( xml );  
        FeatureCollection col = fs.getFeatures(filter);  
        return col;  
    }
    
    /**
     * 注意首先要备份原始的shape文件，并坐标要求相同
     * 将指定的simplefeature集合追加到指定的shapefile
     * @param list simplefeature集合
     * @param shapePath 合并的shape文件
     * @param fileds 要把feature里面的哪些字段合并到指定的shapefile中 
     * fields 字符串格式 columnname1:columnname2
     * @param charSet读取shapefile编码格式
     * @return 结果状态
     */
    public static boolean AppendFeatureToShapeFile(List<SimpleFeature> list, String shapePath,List<String> fileds,String charSet){
        boolean retBoo = false;
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore sds = null;
        try {
            sds = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(shapePath).toURI().toURL());
            sds.setCharset(Charset.forName(charSet));
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = sds.getFeatureWriter(sds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            for(SimpleFeature sf : list){
                SimpleFeature feature = writer.next();
                for(String filedStr : fileds){
                    String[] strArr = filedStr.split(":");
                    feature.setAttribute(strArr[1],sf.getAttribute(strArr[0]));
                }
            }
            writer.write();
            writer.close();
            retBoo = true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            sds.dispose();
        }
        return retBoo;
    }
    
    /**
     * 根据文本文件生成shape wkt json 文件
     * @param path txt 路径  'F:\\xy.txt'
     * @param splitChar 文件每行分隔 除了点要素分隔符可以使用逗号，其它类型要更换分隔符号
     * @param crs 坐标系
     * @param encoding 文件编码
     * @param attriDesc 属性描述 空间字段名称必须为the_geom 类型geometry-wkt geometry-json
     * String[] attriDesc = new String[]{"gid:int","type:String","lockid:double","street:String","the_geom:geometry-wkt"};
     * @param topath  'F:\\export.shp'
     * @param geometry type 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
     * @return 结果状态
     */
    public static boolean CreateShapeByTxt(String txtpath,String splitChar,String crs,String encoding,String[] attriDesc,String topath,String geometryType){
        boolean ret = false;
        try {
            //创建shape文件对象
            File file = new File(topath);
            Map<String, Serializable> params = new HashMap<String, Serializable>();  
            params.put( ShapefileDataStoreFactory.URLP.key, file.toURI().toURL() );
            params.put( ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, (Serializable)Boolean.TRUE );
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params); 
            //定义图形信息和属性信息  
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder(); 
            CoordinateReferenceSystem sourceCRS = CRS.decode(crs);
            tb.setName("shapefile");
            tb.setCRS(sourceCRS);
            Map<Integer,String> map = new HashMap<Integer,String>();
            Map<String,String> mapTemp = new HashMap<String,String>();
            for(int i=0;i<attriDesc.length;i++){
                String[] arrTemp = attriDesc[i].split(":");
                String columnLabel = arrTemp[0];
                String columnType = arrTemp[1];
                if(columnLabel.length()>10){
                    if(i>9){
                        columnLabel = columnLabel.substring(0, 8)+i;
                    }else{
                        columnLabel = columnLabel.substring(0, 9)+i;
                    }
                }
                if(!columnLabel.trim().toLowerCase().equals("the_geom")){
                    if(columnType.toLowerCase().equals("double")){
                        tb.add(columnLabel, Double.class);
                    }else if(columnType.toLowerCase().equals("int")){
                        tb.add(columnLabel, Integer.class);
                    }else if(columnType.toLowerCase().equals("float")){
                        tb.add(columnLabel, Float.class);
                    }else{
                        tb.add(columnLabel, String.class);
                    }
                }
                map.put(i, columnLabel);
                mapTemp.put(columnLabel, columnType);
            }
            if(geometryType.equals("1")){tb.add("the_geom", Point.class);}
            if(geometryType.equals("2")){tb.add("the_geom", MultiPoint.class);}
            if(geometryType.equals("3")){tb.add("the_geom", LineString.class);}
            if(geometryType.equals("4")){tb.add("the_geom", MultiLineString.class);}
            if(geometryType.equals("5")){tb.add("the_geom", Polygon.class);}
            if(geometryType.equals("6")){tb.add("the_geom", MultiPolygon.class);}
            ds.createSchema(tb.buildFeatureType());  
            ds.setCharset(Charset.forName(encoding));
            List<String> listTxtLine = PBFileUtil.ReadFileByLine(txtpath);
            //设置Writer  
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            for(String str:listTxtLine){
                String[] strArr = str.split(splitChar);
                SimpleFeature feature = writer.next();
                for(int i=0;i<strArr.length;i++){
                    String labelName = map.get(i);
                    String labelType = mapTemp.get(labelName);
                    String dataStr = strArr[i];
                    if(labelType.toLowerCase().equals("double")){
                        feature.setAttribute(labelName,Double.parseDouble(dataStr));
                    }else if(labelType.toLowerCase().equals("int")){
                        feature.setAttribute(labelName,Integer.parseInt(dataStr));
                    }else if(labelType.toLowerCase().equals("float")){
                        feature.setAttribute(labelName,Float.parseFloat(dataStr));
                    }else if(labelType.toLowerCase().equals("string")){
                        feature.setAttribute(labelName,dataStr);
                    }else if(labelType.toLowerCase().equals("geometry-wkt")){
                        feature.setAttribute(labelName,GeoToolsGeometry.createGeometrtyByWKT(dataStr));
                    }else if(labelType.toLowerCase().equals("geometry-json")){
                        feature.setAttribute(labelName,GeoToolsGeometry.CreateGeometrtyByJSON(dataStr));
                    }
                }
            }
            writer.write();  
            writer.close();  
            ds.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    /**
     * 将空间表数据导出shape 只支持WGS84 字符串字段不能超过253，列名不能超过10(非中文)
     * @param connection 数据库的连接
     * @param sql 执行的sql语句 几何字段必须为wkt 目前只支持wkt eg:select *,ST_AsText(the_geom) wkt from traffic_area_middle_54
     * @param shapeFilePath 保存的文件路径
     * @param geometry type 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
     * @return 程序结果状态
     */
    public static String ExportTableToShape(Connection connection,String sql,String shapeFilePath,String encoding,String geometryType,String crs){
        try {
            //创建shape文件对象
            File file = new File(shapeFilePath);  
            Map<String, Serializable> params = new HashMap<String, Serializable>();  
            params.put( ShapefileDataStoreFactory.URLP.key, file.toURI().toURL() );
            params.put( ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, (Serializable)Boolean.TRUE );
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params); 
            //定义图形信息和属性信息  
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder(); 
            CoordinateReferenceSystem sourceCRS = CRS.decode(crs);
            tb.setCRS(sourceCRS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            tb.setName("shapefile"); 
            for(int i=1;i<=columnCount;i++){
                int columnType = rsmd.getColumnType(i);
                String columnLabel = rsmd.getColumnLabel(i);
                if(columnLabel.length()>10){
                    if(i>9){
                        columnLabel = columnLabel.substring(0, 8)+i;
                    }else{
                        columnLabel = columnLabel.substring(0, 9)+i;
                    }
                }
                if(!columnLabel.trim().toLowerCase().equals("the_geom")&&!columnLabel.trim().toLowerCase().equals("wkt")){
                    if(columnType==Types.DOUBLE){
                        tb.add(columnLabel, Double.class);
                    }else if(columnType==Types.INTEGER){
                        tb.add(columnLabel, Integer.class);
                    }else if(columnType==Types.FLOAT){
                        tb.add(columnLabel, Float.class);
                    }else{
                        //列宽
                        int precision = rsmd.getPrecision(i);
                        tb.add(columnLabel, String.class);
                    }
                }
            }
            if(geometryType.equals("1")){tb.add("the_geom", Point.class);}
            if(geometryType.equals("2")){tb.add("the_geom", MultiPoint.class);}
            if(geometryType.equals("3")){tb.add("the_geom", LineString.class);}
            if(geometryType.equals("4")){tb.add("the_geom", MultiLineString.class);}
            if(geometryType.equals("5")){tb.add("the_geom", Polygon.class);}
            if(geometryType.equals("6")){tb.add("the_geom", MultiPolygon.class);}
            ds.createSchema(tb.buildFeatureType());  
            ds.setCharset(Charset.forName(encoding));
            //设置Writer  
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            while(resultSet.next()){
                SimpleFeature feature = writer.next();
                for(int i=1;i<=columnCount;i++){
                    int columnType = rsmd.getColumnType(i);
                    String columnLabel = rsmd.getColumnLabel(i);
                    if(columnLabel.length()>10){
                        if(i>9){
                            columnLabel = columnLabel.substring(0, 8)+i;
                        }else{
                            columnLabel = columnLabel.substring(0, 9)+i;
                        }
                    }
                    if(columnLabel.trim().toLowerCase().equals("wkt")){
                        if(null!=resultSet.getString(i)&&!resultSet.getString(i).trim().equals("")){
                            feature.setAttribute("the_geom",GeoToolsGeometry.createGeometrtyByWKT(resultSet.getString(i))); 
                        }
                    }else{
                        if(columnType==Types.NUMERIC||columnType==Types.DOUBLE||columnType==Types.FLOAT){
                            feature.setAttribute(columnLabel,resultSet.getDouble(i));
                        }else if(columnType==Types.INTEGER){
                            feature.setAttribute(columnLabel,resultSet.getInt(i));
                        }else if(columnType!=Types.OTHER){
                            String strResult = resultSet.getString(i);
                            if(null==strResult){strResult="";}
                            int byteLength = StringUtil.GetWordCountCode(strResult, encoding);
                            if(byteLength>253){
                                strResult = StringUtil.SubstringByByte(strResult, 253, encoding);
                            }
                            feature.setAttribute(columnLabel,strResult);
                        }
                    }
                    
                }
            }
            writer.write();  
            writer.close();  
            ds.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
    
    /**
     * 将对象集合转换到shape
     * @param listObject 通过类的反射
     * @param shapeFilePath shape文件路径
     * @param encoding shape文件编码
     * @param geometryType 几何字段类型 1点2多点3线 4多线5面 6多面
     * @param crs 坐标信息  EPSG:4326
     * @param geomName 几何字段信息 一般设置成wkt
     * @return
     */
    public static <T> String ListObjectToShapeFile(List<T> listObject,String shapeFilePath,String encoding,String geometryType,String geomName,String crs){
        String result = "success";
        try{
            //创建shape文件对象
            File file = new File(shapeFilePath);  
            Map<String, Serializable> params = new HashMap<String, Serializable>();  
            params.put( ShapefileDataStoreFactory.URLP.key, file.toURI().toURL() );
            params.put( ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, (Serializable)Boolean.TRUE );
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params); 
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            CoordinateReferenceSystem sourceCRS = CRS.decode(crs);
            tb.setCRS(sourceCRS);
            tb.setName("shapefile");
            Field[] fds = null;
            Class clazz = null;
            Map<String,String> map = new HashMap<String, String>();
            for (Object object : listObject){
                //获取集合中的对象类型
                if (null == clazz) {
                    clazz = object.getClass();
                    //获取他的字段数组
                    fds = clazz.getDeclaredFields();
                    for(int i=0;i<fds.length;i++){  
                        if(!fds[i].getName().toString().toLowerCase().trim().equals(geomName.toLowerCase().trim())){
                            String columnLabel = fds[i].getName().toString();
                            if(columnLabel.length()>10){
                              if(i>9){
                                  columnLabel = columnLabel.substring(0, 8)+i;
                              }else{
                                  columnLabel = columnLabel.substring(0, 9)+i;
                              }
                            }
                            tb.add(columnLabel, String.class);
                            map.put(columnLabel, "get"+POIExcelUtil.ChangeFirstUpper(fds[i].getName()));
                        }else{
                            if(geometryType.equals("1")){tb.add("the_geom", Point.class);}
                            if(geometryType.equals("2")){tb.add("the_geom", MultiPoint.class);}
                            if(geometryType.equals("3")){tb.add("the_geom", LineString.class);}
                            if(geometryType.equals("4")){tb.add("the_geom", MultiLineString.class);}
                            if(geometryType.equals("5")){tb.add("the_geom", Polygon.class);}
                            if(geometryType.equals("6")){tb.add("the_geom", MultiPolygon.class);}
                            map.put("the_geom","get"+POIExcelUtil.ChangeFirstUpper(geomName));
                        }
                    }
                    ds.createSchema(tb.buildFeatureType());  
                    ds.setCharset(Charset.forName(encoding));
                }
            }
            //设置Writer  
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            for (Object object : listObject) {
                SimpleFeature feature = writer.next();
                for(Map.Entry<String, String> entry : map.entrySet()){
                    Method metd = clazz.getMethod(entry.getValue(), null);
                    if(entry.getKey().trim().equals("the_geom")){
                        feature.setAttribute(entry.getKey(),GeoToolsGeometry.createGeometrtyByWKT(metd.invoke(object, null).toString()));
                    }else{
                        feature.setAttribute(entry.getKey(),metd.invoke(object, null));
                    }
                }
            }
            writer.write();  
            writer.close();  
            ds.dispose();
        }catch(Exception e){
            e.printStackTrace();
            result = "failture";
        }
        return result;
    }
    
    
    /**
     * shape数据格式转换成excel
     * 空间字段转换成wkt
     * @param shapeFilePath
     * @param excelPath
     * @param sfEncoding shapefile编码
     * @param includeGeom 是否包括空间字段wkt true 包括wkt false 不包括
     */
    public static boolean ExportShapeToExcel(String shapeFilePath,String excelPath,String sfEncoding,boolean includeGeom){
        try{
            SimpleFeatureCollection sfc = ReadShapeFileFeatures(shapeFilePath,sfEncoding);
            SimpleFeatureIterator sfi = sfc.features();
            SimpleFeatureType ft= sfc.getSchema();
            String[] attArr = null;
            if(!includeGeom){//不包括空间字段
                attArr = new String[ft.getAttributeCount()-1];
                int j = 0;
                for (int i = 0; i < ft.getAttributeCount(); i++) {
                    AttributeType at = ft.getType(i);
                    String cLabel = at.getName().toString();
                    if(!ConstantUtil.IsGeometryTypeKey(cLabel)){
                        attArr[j] = cLabel;
                        j++;
                    }
                }
            }else{
                attArr = new String[ft.getAttributeCount()];
                for (int i = 0; i < ft.getAttributeCount(); i++) {
                    AttributeType at = ft.getType(i);
                    attArr[i] = at.getName().toString();
                }
            }
            
            int nColumn = attArr.length;
            HSSFWorkbook workbook = new HSSFWorkbook();  
            HSSFSheet sheet = workbook.createSheet();  
            workbook.setSheetName(0,"export1");  
            HSSFRow row= sheet.createRow(0); 
            HSSFCell cell;
            for(int i=0;i<nColumn;i++){  
                cell = row.createCell(i);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
                cell.setCellValue(attArr[i]);  
            }
            int iRow=1;
            while(sfi.hasNext()){
                SimpleFeature feature = sfi.next();
                row= sheet.createRow(iRow);
                for(int j=0;j<nColumn;j++){
                    cell = row.createCell(j);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    String attArrStr = attArr[j];
                    if(ConstantUtil.IsGeometryTypeKey(attArrStr)){
                        cell.setCellValue(((Geometry)feature.getDefaultGeometry()).toText());
                    }else{
                        if(null==feature.getAttribute(attArr[j]).toString()){
                            cell.setCellValue("");
                        }else{
                            cell.setCellValue(feature.getAttribute(attArr[j]).toString());
                        }
                    }
                }
                iRow++;
                //写入各条记录，每条记录对应Excel中的一行
                FileOutputStream fOut = new FileOutputStream(excelPath);
                workbook.write(fOut);
                fOut.flush();
                fOut.close();
                workbook.close();
            } 
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        
        
    }
    
    /**
     * shape数据格式转换成txt
     * 空间字段转换成wkt
     * @param shapeFilePath
     * @param txtPath
     * @param sfEncoding shapefile编码
     * @param includeGeom 是否包括空间字段wkt true 包括wkt false 不包括
     * @param splitChar 分隔符
     */
    public static boolean ExportShapeToTxt(String shapeFilePath,String txtPath,String sfEncoding,boolean includeGeom,String splitChar){
        try{
            SimpleFeatureCollection sfc = ReadShapeFileFeatures(shapeFilePath,sfEncoding);
            SimpleFeatureIterator sfi = sfc.features();
            SimpleFeatureType ft= sfc.getSchema();
            List<String> exportList = new ArrayList<String>();
            String[] attArr = null;
            if(!includeGeom){//不包括空间字段
                attArr = new String[ft.getAttributeCount()-1];
                int j = 0;
                for (int i = 0; i < ft.getAttributeCount(); i++) {
                    AttributeType at = ft.getType(i);
                    String cLabel = at.getName().toString();
                    if(!ConstantUtil.IsGeometryTypeKey(cLabel)){
                        attArr[j] = cLabel;
                        j++;
                    }
                }
            }else{
                attArr = new String[ft.getAttributeCount()];
                for (int i = 0; i < ft.getAttributeCount(); i++) {
                    AttributeType at = ft.getType(i);
                    attArr[i] = at.getName().toString();
                }
            }
            
            String title="";
            int nColumn = attArr.length;
            for(int i=0;i<nColumn;i++){  
                title = title+attArr[i]+splitChar;
            }
            while(sfi.hasNext()){
                SimpleFeature feature = sfi.next();
                String rowStr="";
                for(int j=0;j<nColumn;j++){
                    String attArrStr = attArr[j];
                    if(ConstantUtil.IsGeometryTypeKey(attArrStr)){
                        rowStr = rowStr+((Geometry)feature.getDefaultGeometry()).toText()+splitChar;
                    }else{
                        if(null==feature.getAttribute(attArr[j]).toString()||"".equals(feature.getAttribute(attArr[j]).toString())){
                            rowStr = rowStr+"Null"+splitChar;
                        }else{
                            rowStr = rowStr+feature.getAttribute(attArr[j]).toString()+splitChar;
                        }
                    }
                }
                exportList.add(rowStr);
            } 
            PBFileUtil.WriteListToTxt(exportList, txtPath, true);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    
//    public static boolean CreatePointByTxt(String txtpath,String splitChar,String crs,String encoding,String[] attriDesc,String topath){
//        boolean ret = false;
//        try {
//            //创建shape文件对象
//            File file = new File(topath);
//            Map<String, Serializable> params = new HashMap<String, Serializable>();  
//            params.put( ShapefileDataStoreFactory.URLP.key, file.toURI().toURL() );
//            params.put( ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, (Serializable)Boolean.TRUE );
//            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params); 
//            //定义图形信息和属性信息  
//            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder(); 
//            CoordinateReferenceSystem sourceCRS = CRS.decode(crs);
//            tb.setName("shapefile");
//            tb.setCRS(sourceCRS);
//            Map<Integer,String> map = new HashMap<Integer,String>();
//            Map<String,String> mapTemp = new HashMap<String,String>();
//            for(int i=0;i<attriDesc.length;i++){
//                String[] arrTemp = attriDesc[i].split(":");
//                String columnLabel = arrTemp[0];
//                String columnType = arrTemp[1];
//                if(columnLabel.length()>10){
//                    if(i>9){
//                        columnLabel = columnLabel.substring(0, 8)+i;
//                    }else{
//                        columnLabel = columnLabel.substring(0, 9)+i;
//                    }
//                }
//                if(!columnLabel.trim().toLowerCase().equals("the_geom")){
//                    if(columnType.toLowerCase().equals("double")){
//                        tb.add(columnLabel, Double.class);
//                    }else if(columnType.toLowerCase().equals("int")){
//                        tb.add(columnLabel, Integer.class);
//                    }else if(columnType.toLowerCase().equals("float")){
//                        tb.add(columnLabel, Float.class);
//                    }else{
//                        tb.add(columnLabel, String.class);
//                    }
//                }
//                map.put(i, columnLabel);
//                mapTemp.put(columnLabel, columnType);
//            }
//            tb.add("the_geom", Point.class);
//            
//            ds.createSchema(tb.buildFeatureType());  
//            ds.setCharset(Charset.forName(encoding));
//            List<String> listTxtLine = PBFileUtil.ReadFileByLine(txtpath);
//            //设置Writer  
//            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
//            for(String str:listTxtLine){
//                String[] strArr = str.split(splitChar);
//                SimpleFeature feature = writer.next();
//                for(int i=0;i<strArr.length;i++){
//                    String labelName = map.get(i);
//                    String labelType = mapTemp.get(labelName);
//                    String dataStr = strArr[i];
//                    if(labelType.toLowerCase().equals("double")){
//                        feature.setAttribute(labelName,Double.parseDouble(dataStr));
//                    }else if(labelType.toLowerCase().equals("int")){
//                        feature.setAttribute(labelName,Integer.parseInt(dataStr));
//                    }else if(labelType.toLowerCase().equals("float")){
//                        feature.setAttribute(labelName,Float.parseFloat(dataStr));
//                    }else if(labelType.toLowerCase().equals("string")){
//                        feature.setAttribute(labelName,dataStr);
//                    }else if(labelType.toLowerCase().equals("geometry-wkt")){
//                        feature.setAttribute(labelName,GeoToolsGeometry.createGeometrtyByWKT(dataStr));
//                    }else if(labelType.toLowerCase().equals("geometry-json")){
//                        feature.setAttribute(labelName,GeoToolsGeometry.CreateGeometrtyByJSON(dataStr));
//                    }
//                }
//            }
//            writer.write();  
//            writer.close();  
//            ds.dispose();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ret;
//    }
}
