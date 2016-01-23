package com.promise.gistool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



public class ExportStationDistanceNew {



  public boolean heightVersionFlag = false;

  private Connection conn_flow = null;

  private static Connection conn = null;

  public void init() throws Exception {
    Class.forName("oracle.jdbc.OracleDriver");
    String url = "jdbc:oracle:thin:@182.92.183.85:1521:orcl";
    conn = DriverManager.getConnection(url, "ie", "xssdd_ttyj_xd22ws");
  }

  public void close() throws Exception {
    if(conn != null){
      conn.close();
      conn = null;
    }
  }

  public void init_flow() throws Exception {
    Class.forName("org.postgresql.Driver");
    //String url = "jdbc:postgresql://123.116.104.249:5432/buscity";
    String url = "jdbc:postgresql://192.168.1.105:5432/basedata";
    //conn_flow = DriverManager.getConnection(url, "buscity", "bs789&*(");
    conn_flow = DriverManager.getConnection(url, "basedata", "basedata");
  }

  public void close_flow() throws Exception {
    if(conn_flow != null){
      conn_flow.close();
      conn_flow = null;
    }
  }

  /***********************************************************/
  //--------------------区域统计数
  /***********************************************************/

  private List<String[]> readHuanMian(){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select id,name,ST_Area(ST_Transform(the_geom,900913)) area,'5' as type,'环线' as areatype,ST_x(ST_Centroid(the_geom)) x,ST_y(ST_Centroid(the_geom)) y,code ");
    sqlBuf.append(" from huanmian ");
    //sqlBuf.append(" where code = 6");
    sqlBuf.append(" order by code ");
    //
    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[14];
        bo[0] = UUID.randomUUID().toString().replace("-", "");
        bo[1] = rs.getString("id");
        bo[2] = rs.getString("code");
        bo[3] = rs.getString("name");
        bo[4] = rs.getString("area");
        bo[5] = rs.getString("type");
        bo[6] = rs.getString("x");
        bo[7] = rs.getString("y");
        bo[8] = "";
        bo[9] = "";
        bo[10] = "";
        bo[11] = "";
        bo[12] = "";
        bo[13] = rs.getString("areatype");
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    return list;
  }

  private List<String[]> readBjQx(){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select id,name,ST_Area(ST_Transform(the_geom,900913)) area,'4' as type,'行政区' as areatype,ST_x(ST_Centroid(the_geom)) x,ST_y(ST_Centroid(the_geom)) y,code ");
    sqlBuf.append(" from beijingqx ");
    //sqlBuf.append(" where code = 1 ");
    sqlBuf.append(" order by code ");
    //
    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[14];
        bo[0] = UUID.randomUUID().toString().replace("-", "");
        bo[1] = rs.getString("id");
        bo[2] = rs.getString("code");
        bo[3] = rs.getString("name");
        bo[4] = rs.getString("area");
        bo[5] = rs.getString("type");
        bo[6] = rs.getString("x");
        bo[7] = rs.getString("y");
        bo[8] = "";
        bo[9] = "";
        bo[10] = "";
        bo[11] = "";
        bo[12] = "";
        bo[13] = rs.getString("areatype");
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    return list;
  }

  private List<String[]> readBigArea(){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select idd,name,shape_area area,'3' as type,'交通大区' as areatype,ST_x(ST_Centroid(the_geom)) x,ST_y(ST_Centroid(the_geom)) y,code ");
    sqlBuf.append(" from traffic_area_big_54 ");
    //sqlBuf.append(" where code = 1 ");
    sqlBuf.append(" order by code ");
    //
    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[14];
        bo[0] = UUID.randomUUID().toString().replace("-", "");
        bo[1] = rs.getString("idd");
        bo[2] = rs.getString("code");
        bo[3] = rs.getString("name");
        bo[4] = rs.getString("area");
        bo[5] = rs.getString("type");
        bo[6] = rs.getString("x");
        bo[7] = rs.getString("y");
        bo[8] = "";
        bo[9] = "";
        bo[10] = "";
        bo[11] = "";
        bo[12] = "";
        bo[13] = rs.getString("areatype");
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    return list;
  }


  private List<String[]> readMiddleArea(){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select id,shape_area area,'2' as type,'交通中区' as areatype,ST_x(ST_Centroid(the_geom)) x,ST_y(ST_Centroid(the_geom)) y,code ");
    sqlBuf.append(" from traffic_area_middle_54 ");
    //sqlBuf.append(" where code = 1 ");
    sqlBuf.append(" order by code ");
    //
    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[14];
        bo[0] = UUID.randomUUID().toString().replace("-", "");
        bo[1] = rs.getString("id");
        bo[2] = rs.getString("code");
        bo[3] = "";
        bo[4] = rs.getString("area");
        bo[5] = rs.getString("type");
        bo[6] = rs.getString("x");
        bo[7] = rs.getString("y");
        bo[8] = "";
        bo[9] = "";
        bo[10] = "";
        bo[11] = "";
        bo[12] = "";
        bo[13] = rs.getString("areatype");
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    return list;
  }


  private List<String[]> readSmallArea(){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select id,shape_area area,'1' as type,'交通小区' as areatype,ST_x(ST_Centroid(the_geom)) x,ST_y(ST_Centroid(the_geom)) y,code ");
    sqlBuf.append(" from traffic_area_small_54 ");
    //sqlBuf.append(" where code = 1 ");
    sqlBuf.append(" order by code ");
    //
    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[14];
        bo[0] = UUID.randomUUID().toString().replace("-", "");
        bo[1] = rs.getString("id");
        bo[2] = rs.getString("code");
        bo[3] = "";
        bo[4] = rs.getString("area");
        bo[5] = rs.getString("type");
        bo[6] = rs.getString("x");
        bo[7] = rs.getString("y");
        bo[8] = "";
        bo[9] = "";
        bo[10] = "";
        bo[11] = "";
        bo[12] = "";
        bo[13] = rs.getString("areatype");
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    return list;
  }

  //
  private int getAreaBikeStationCount(String code,String table){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select a.code, count(*) count_num ");
    sqlBuf.append("  from "+table+" a, bikestation b ");
    sqlBuf.append(" where 1 = 1 ");
    sqlBuf.append("   and ST_Within(b.the_geom, a.the_geom) ");
    sqlBuf.append("   and a.code = "+code);
    sqlBuf.append("  group by a.code ");
    sqlBuf.append("");

    int count_num = 0;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        count_num = rs.getInt("count_num");
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return count_num;
  }

  private int getAreaStationCount(String code,String table){
    StringBuffer sqlBuf = new StringBuffer();
    /*
		sqlBuf.append("select a.code, count(*) count_num ");
    sqlBuf.append("  from "+table+" a, busstation_merge b ");
    sqlBuf.append(" where 1 = 1 ");
    sqlBuf.append("   and ST_Within(b.the_geom, a.the_geom) ");
    sqlBuf.append("   and a.code = "+code);
    sqlBuf.append("  group by a.code ");
    sqlBuf.append("");
     */

    sqlBuf.append("select t.code,count(*) count_num from(");
    sqlBuf.append("select a.code, b.name ");
    sqlBuf.append("  from "+table+" a, busstation b ");
    sqlBuf.append(" where 1 = 1 ");
    sqlBuf.append("   and ST_Within(b.the_geom, a.the_geom) ");
    sqlBuf.append("   and b.isvalid='1' and b.company !='地铁' ");
    sqlBuf.append("   and a.code = "+code);
    sqlBuf.append(" group by a.code,b.name ");
    sqlBuf.append(") t group by t.code ");

    sqlBuf.append("");

    int count_num = 0;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        count_num = rs.getInt("count_num");
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return count_num;
  }


  private int getAreaRaiWayStationCount(String code,String table){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select t.code,count(*) count_num from(");
    sqlBuf.append("select a.code, b.name ");
    sqlBuf.append("  from "+table+" a, busstation b ");
    sqlBuf.append(" where 1 = 1 ");
    sqlBuf.append("   and ST_Within(b.the_geom, a.the_geom) ");
    sqlBuf.append("   and b.company ='地铁' ");
    sqlBuf.append("   and a.code = "+code);
    sqlBuf.append(" group by a.code,b.name ");
    sqlBuf.append(") t group by t.code ");

    sqlBuf.append("");

    int count_num = 0;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        count_num = rs.getInt("count_num");
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return count_num;
  }


  private int getAreaRaiWayLineCount(String code,String table){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select a.code,count(*) count_num ");
    sqlBuf.append(" from "+table+" a,busline b ");
    sqlBuf.append(" where 1=1 ");
    sqlBuf.append(" and ( ST_Intersects(b.the_geom,a.the_geom) or ST_Within(b.the_geom,a.the_geom) ) ");
    sqlBuf.append(" and b.sygs ='地铁' ");
    sqlBuf.append(" and a.code = "+code);
    sqlBuf.append(" group by a.code ");
    sqlBuf.append("");

    int count_num = 0;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        count_num = rs.getInt("count_num");
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return count_num;
  }

  private int getAreaBusLineCount(String code,String table){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select t.code, count(t.*) count_num");
    sqlBuf.append("  from (select temp.code,");
    sqlBuf.append("               (select temp_2.linename ");
    sqlBuf.append("                  from bus_line_station_ref temp_2 ");
    sqlBuf.append("                 where temp_2.linelabel = ");
    sqlBuf.append("                       (select name from busline where id = temp.buslineid) limit 1) line_name ");
    sqlBuf.append("          from (select a.code, b.buslineid ");
    sqlBuf.append("                  from "+table+" a, buslinelink b ");
    sqlBuf.append("                 where 1 = 1 ");
    sqlBuf.append("                   and ST_Within(b.the_geom, a.the_geom) ");
    sqlBuf.append("                   and a.code = "+code+") temp ");
    sqlBuf.append("         group by code, line_name ");
    sqlBuf.append("        ) t ");
    sqlBuf.append(" group by t.code ");


    int count_num = 0;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        count_num = rs.getInt("count_num");
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return count_num;
  }


  private void insertArea() throws Exception{
    List<String[]> param = new ArrayList<String[]>();
    List<String[]> list = null;
    int len = 0;
    //--big

    list = readBigArea();
    len = list.size();
    for(int i=0;i<len;i++){
      String[] bo = list.get(i);
      String area_id = bo[2];
      int count_station = getAreaStationCount(area_id,"traffic_area_big_54");
      int count_busline = getAreaBusLineCount(area_id,"traffic_area_big_54");
      int count_bikeStation = getAreaBikeStationCount(area_id,"traffic_area_big_54");
      int count_raiwayStation = getAreaRaiWayStationCount(area_id,"traffic_area_big_54");
      int count_raiwayline = getAreaRaiWayLineCount(area_id,"traffic_area_big_54");

      bo[8] = count_station+"";
      bo[9] = count_busline+"";
      bo[10] = count_bikeStation+"";
      bo[11] = count_raiwayStation+"";
      bo[12] = count_raiwayline+"";

      param.add(bo);
    }
    buildOracleAreaInfoSql(param);  
    insertOracleAreaInfo(param);  
    param = new ArrayList<String[]>();
    //--

    list = readMiddleArea();
    len = list.size();
    for(int i=0;i<len;i++){
      String[] bo = list.get(i);
      String area_id = bo[2];
      int count_station = getAreaStationCount(area_id,"traffic_area_middle_54");
      int count_busline = getAreaBusLineCount(area_id,"traffic_area_middle_54");
      int count_bikeStation = getAreaBikeStationCount(area_id,"traffic_area_middle_54");
      int count_raiwayStation = getAreaRaiWayStationCount(area_id,"traffic_area_middle_54");
      int count_raiwayline = getAreaRaiWayLineCount(area_id,"traffic_area_middle_54");

      bo[8] = count_station+"";
      bo[9] = count_busline+"";
      bo[10] = count_bikeStation+"";
      bo[11] = count_raiwayStation+"";
      bo[12] = count_raiwayline+"";
      param.add(bo);
    }
    buildOracleAreaInfoSql(param);  
    insertOracleAreaInfo(param);  
    param = new ArrayList<String[]>();
    //--
    /**/
    list = readSmallArea();
    len = list.size();
    for(int i=0;i<len;i++){
      String[] bo = list.get(i);
      String area_id = bo[2];
      int count_station = getAreaStationCount(area_id,"traffic_area_small_54");
      int count_busline = getAreaBusLineCount(area_id,"traffic_area_small_54");
      int count_bikeStation = getAreaBikeStationCount(area_id,"traffic_area_small_54");
      int count_raiwayStation = getAreaRaiWayStationCount(area_id,"traffic_area_small_54");
      int count_raiwayline = getAreaRaiWayLineCount(area_id,"traffic_area_small_54");

      bo[8] = count_station+"";
      bo[9] = count_busline+"";
      bo[10] = count_bikeStation+"";
      bo[11] = count_raiwayStation+"";
      bo[12] = count_raiwayline+"";
      param.add(bo);
    }
    buildOracleAreaInfoSql(param);  
    insertOracleAreaInfo(param);  
    param = new ArrayList<String[]>();

    list = readBjQx();
    len = list.size();
    for(int i=0;i<len;i++){
      String[] bo = list.get(i);
      String area_id = bo[2];
      int count_station = getAreaStationCount(area_id,"beijingqx");
      int count_busline = getAreaBusLineCount(area_id,"beijingqx");
      int count_bikeStation = getAreaBikeStationCount(area_id,"beijingqx");
      int count_raiwayStation = getAreaRaiWayStationCount(area_id,"beijingqx");
      int count_raiwayline = getAreaRaiWayLineCount(area_id,"beijingqx");

      bo[8] = count_station+"";
      bo[9] = count_busline+"";
      bo[10] = count_bikeStation+"";
      bo[11] = count_raiwayStation+"";
      bo[12] = count_raiwayline+"";      
      param.add(bo);
    }
    buildOracleAreaInfoSql(param);  
    insertOracleAreaInfo(param);  
    param = new ArrayList<String[]>();

    list = readHuanMian();
    len = list.size();
    for(int i=0;i<len;i++){
      String[] bo = list.get(i);
      String area_code = bo[2];
      int count_busline = getAreaBusLineCount(area_code,"huanmian");
      int count_station = getAreaStationCount(area_code,"huanmian");
      int count_bikeStation = getAreaBikeStationCount(area_code,"huanmian");
      int count_raiwayStation = getAreaRaiWayStationCount(area_code,"huanmian");
      int count_raiwayline = getAreaRaiWayLineCount(area_code,"huanmian");			

      bo[8] = count_station+"";
      bo[9] = count_busline+"";
      bo[10] = count_bikeStation+"";
      bo[11] = count_raiwayStation+"";
      bo[12] = count_raiwayline+"";
      param.add(bo);
    }

    buildOracleAreaInfoSql(param);  
    insertOracleAreaInfo(param);  
    param = new ArrayList<String[]>();

    //insertOracleAreaInfo(param);
    //buildOracleAreaInfoSql(param);	
  }

  private void buildOracleAreaInfoSql(List<String[]> list) throws Exception{
    StringBuffer sqlBuf = new StringBuffer();
    String[] bo;
    for(int i=0,len=list.size();i<len;i++){
      bo = list.get(i);
      sqlBuf = new StringBuffer("INSERT INTO BUSCITY.DE_CFG_AREA(ID,NAME,TYPE,AREATYPE,LONGITUDE,LATITUDE,AREASIZE,STATIONNUM,BUSLINENUM,BIKESTATION,SUBWAYSTATION,SUBWAYLINE,CODE) VALUES (");
      sqlBuf.append("'"+bo[0]+"',");
      sqlBuf.append("'"+bo[3]+"',");
      sqlBuf.append(""+bo[5]+",");
      sqlBuf.append("'"+bo[13]+"',");
      sqlBuf.append(""+bo[6]+",");
      sqlBuf.append(""+bo[7]+",");
      sqlBuf.append(""+bo[4]+",");
      sqlBuf.append(""+bo[8]+",");
      sqlBuf.append(""+bo[9]+",");
      sqlBuf.append(""+bo[10]+",");
      sqlBuf.append(""+bo[11]+",");
      sqlBuf.append(""+bo[12]+",");
      sqlBuf.append(""+bo[2]+"");
      sqlBuf.append(");");
      System.out.println(sqlBuf.toString());
    }
  }

  private void insertOracleAreaInfo(List<String[]> list) throws Exception{
    init();

    PreparedStatement ps = null;
    try {
      String batch_sql = "INSERT INTO DE_CFG_AREA(ID,NAME,TYPE,AREATYPE,LONGITUDE,LATITUDE,AREASIZE,STATIONNUM,BUSLINENUM,BIKESTATION,SUBWAYSTATION,SUBWAYLINE,CODE) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
      ps = conn.prepareStatement(batch_sql);
      conn.setAutoCommit(false);
      String[] bo;
      for(int i=0,len=list.size();i<len;i++){
        bo = list.get(i);
        ps.setString(1, bo[0]);
        ps.setString(2, bo[3]);
        ps.setInt(3, Integer.parseInt(bo[5]));
        ps.setString(4, bo[13]);

        ps.setDouble(5, Double.parseDouble(bo[6]));
        ps.setDouble(6, Double.parseDouble(bo[7]));
        ps.setDouble(7, Double.parseDouble(bo[4]));

        ps.setInt(8, Integer.parseInt(bo[8]));
        ps.setInt(9, Integer.parseInt(bo[9]));
        ps.setInt(10, Integer.parseInt(bo[10]));
        ps.setInt(11, Integer.parseInt(bo[11]));
        ps.setInt(12, Integer.parseInt(bo[12]));

        ps.setInt(13, Integer.parseInt(bo[2]));
        //
        ps.addBatch();
        if(i%1000==0){
          ps.executeBatch();
          conn.commit();  
          ps.clearBatch();
        }
      }
      //
      ps.executeBatch();
      conn.commit();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally{
      try {
        if(ps != null){
          ps.close();
          ps = null;
        }
      } catch (SQLException e) {}
    }

    close();
  }


  /***********************************************************/
  //--------------------路链包含公交线路计算
  /***********************************************************/

  private List<String[]> readMergeBuslinelink(){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select navigationid,count(*) busline_sum from buslinelink ");
    sqlBuf.append(" group by navigationid ");
    sqlBuf.append(" order by busline_sum desc ");
    //
    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[4];
        bo[0] = rs.getString("navigationid");
        bo[1] = rs.getString("busline_sum");
        bo[2] = "";
        bo[3] = UUID.randomUUID().toString().replace("-", "");;
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    return list;

  }


  public String readNavigationBusLines(String navigationid){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select navigationid,buslineid from buslinelink ");
    sqlBuf.append(" where navigationid = '"+navigationid+"' ");
    //
    List<String> list = new ArrayList<String>();
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        list.add(rs.getString("buslineid"));
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    StringBuffer buf = new StringBuffer();
    for(int i=0;i<list.size();i++){
      if(i==list.size()-1){
        buf.append(list.get(i));
      }else{
        buf.append(list.get(i)+",");
      }
    }

    return buf.toString();
  }

  private void calculateBusLineStat(){
    List<String[]> list = readMergeBuslinelink();
    int len = list.size();
    for(int i=0;i<len;i++){
      String[] bo = list.get(i);
      String navigationid = bo[0];
      String buslineids = readNavigationBusLines(navigationid);
      bo[2] = buslineids;
    }

    PreparedStatement ps = null;
    try {
      String batch_sql = "INSERT INTO navigation_buslines_temp(id,navigationid,busline_num,busline_ids) values(?,?,?,?)";
      ps = conn_flow.prepareStatement(batch_sql);
      conn_flow.setAutoCommit(false);
      String[] bo;
      for(int i=0;i<len;i++){
        bo = list.get(i);
        ps.setString(1, bo[3]);
        ps.setString(2, bo[0]);
        ps.setInt(3, Integer.parseInt(bo[1]));
        ps.setString(4, bo[2]);
        ps.addBatch();
        if(i%1000==0){
          ps.executeBatch();
          conn_flow.commit();  
          ps.clearBatch();
        }
      }
      //
      ps.executeBatch();
      conn_flow.commit();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally{
      try {
        if(ps != null){
          ps.close();
          ps = null;
        }
      } catch (SQLException e) {}
    }

  }


  /***********************************************************/
  //--------------------公交站点距离计算
  /***********************************************************/

  private List<String[]> readBusLines(){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select id,name,state from busline where 1=1 ");
    sqlBuf.append(" and isvalid = '1' ");
    //sqlBuf.append(" and id='059e376f98c64c4eb4675e9d8eb9492d' ");

    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[2];
        bo[0] = rs.getString("id");
        bo[1] = rs.getString("name");
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return list;
  }


  private List<String[]> readBusLineStations(String buslineid){
    StringBuffer sqlBuf = new StringBuffer();
    /*
    sqlBuf.append(" select a.id,a.name,a.tjcc,a.index,b.arrow,");
    sqlBuf.append(" (select linkid from busstationlink where busstationid=a.id) linkid ");
    sqlBuf.append(" from busstation a,busline b ");
    sqlBuf.append(" where a.buslineid = b.id ");
    sqlBuf.append(" and b.id = '"+buslineid+"' ");
    sqlBuf.append(" order by a.index ");
    */
    
    //---
    sqlBuf.append(" select a.id,a.name,a.tjcc,a.index,b.arrow,c.linkid,st_x(c.the_geom) x,st_y(c.the_geom) y ");
    sqlBuf.append(" from busstation a ");
    sqlBuf.append(" left join busline b on b.id = a.buslineid ");
    sqlBuf.append(" left join busstationlink c on a.id = c.busstationid ");
    sqlBuf.append(" where 1=1 ");
    sqlBuf.append(" and b.id = '"+buslineid+"' ");
    sqlBuf.append(" order by a.index ");

    
    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[8];
        bo[0] = rs.getString("id");
        bo[1] = rs.getString("name");
        bo[2] = rs.getString("tjcc");
        bo[3] = rs.getString("index");
        bo[4] = rs.getString("arrow");
        String linkid =rs.getString("linkid");
        if(linkid != null && !linkid.equals("")){
          bo[5] = linkid;
        } else {
          bo[5] = null;
        }

        bo[6] = rs.getString("x");
        bo[7] = rs.getString("y");
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return list;
  }

  private List<String[]> readBusLineLinksB(String buslineid){
    StringBuffer sqlBuf = new StringBuffer();

    sqlBuf.append("  select t1.id,t.navigationid,ST_Length(ST_Transform(t1.the_geom,900913)) length,t1.direction,t1.snodeid,t1.enodeid from( ");
    sqlBuf.append("    select a.navigationid from buslinelink a where 1=1 ");
    sqlBuf.append("    and a.buslineid = '"+buslineid+"'  ");
    sqlBuf.append("    group by a.navigationid ");
    sqlBuf.append("  ) t,navigationline t1  ");
    sqlBuf.append("  where t.navigationid = t1.id  ");


    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[7];
        bo[0] = rs.getString("id");
        bo[1] = rs.getString("navigationid");
        bo[2] = rs.getString("length");
        bo[3] = rs.getString("direction");
        //---
        String snodeid = rs.getString("snodeid").trim();
        String enodeid = rs.getString("enodeid").trim();
        if(snodeid.length() > 10){
          if(snodeid.startsWith("10") || snodeid.startsWith("20")){
            snodeid = snodeid.substring(1);
          }
        }
        int s_node_id = Integer.parseInt(snodeid);
        //
        if(enodeid.length() > 10){
          if(enodeid.startsWith("10") || enodeid.startsWith("20")){
            enodeid = enodeid.substring(1);
          }
        }
        int e_node_id = Integer.parseInt(enodeid);
        //
        bo[4] = s_node_id+"";
        bo[5] = e_node_id+"";
        bo[6] = "0";
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return list;
  }

  private List<String[]> readBusLineLinks(String buslineid){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("	select a.id,a.navigationid,ST_Length(ST_Transform(a.the_geom,900913)) length,b.direction,b.snodeid,b.enodeid ");
    sqlBuf.append("	from buslinelink a,navigationline b where 1=1 ");
    sqlBuf.append("	and a.navigationid = b.id ");
    sqlBuf.append("	and a.buslineid = '"+buslineid+"' ");
    sqlBuf.append("");

    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[7];
        bo[0] = rs.getString("id");
        bo[1] = rs.getString("navigationid");
        bo[2] = rs.getString("length");
        bo[3] = rs.getString("direction");
        //---
        String snodeid = rs.getString("snodeid").trim();
        String enodeid = rs.getString("enodeid").trim();
        if(snodeid.length() > 10){
          if(snodeid.startsWith("10") || snodeid.startsWith("20")){
            snodeid = snodeid.substring(1);
          }
        }
        int s_node_id = Integer.parseInt(snodeid);
        //
        if(enodeid.length() > 10){
          if(enodeid.startsWith("10") || enodeid.startsWith("20")){
            enodeid = enodeid.substring(1);
          }
        }
        int e_node_id = Integer.parseInt(enodeid);
        //
        bo[4] = s_node_id+"";
        bo[5] = e_node_id+"";
        bo[6] = "0";
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return list;
  }




  public String readBusLineStationMergeSEBBox(String arrow,String s_station_name,String e_station_name){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select ST_AsText(ST_extent(the_geom)) bbox_geom ");
    sqlBuf.append(" from busstation_merge_temp ");
    sqlBuf.append(" where name in ('"+s_station_name+"','"+e_station_name+"')");
    sqlBuf.append(" and arrow='"+arrow+"' ");

    List<String> list = new ArrayList<String>();
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        list.add(rs.getString("bbox_geom"));
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    if(list.size() > 0){
      return list.get(0);
    }

    return null;
  }


  public String calculateStationDistance(String buslineid,String bboxWkt){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select sum(ST_Length(ST_Transform(a.the_geom,900913))) distance ");
    sqlBuf.append(" from buslinelink a,navigationline b where 1=1 ");
    sqlBuf.append(" and a.navigationid = b.id ");
    sqlBuf.append(" and a.buslineid='"+buslineid+"' ");
    sqlBuf.append(" and ST_Intersects(a.the_geom,st_geometryfromtext('"+bboxWkt+"',4326))");

    List<String> list = new ArrayList<String>();
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        list.add(rs.getString("distance"));
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    if(list.size() > 0){
      return list.get(0);
    }

    return "0";
  }


  private String[] getNextLink(Map<String,String> noLinkMap,List<String[]> links,String from_node){
    int len = links.size();
    String[] node = null;
    String[] nex_node = null;
    for(int i=0;i<len;i++){
      node = links.get(i);
      String node_link = node[1];
      String node_length = node[2];
      String s_node = node[4];
      String e_node = node[5];
      String flag = node[6];

      if(!noLinkMap.containsKey(node_link) && flag.equals("0")){

        if(from_node.equals(s_node)){
          node[6] = "1";
          nex_node = new String[3];
          nex_node[0] = node_link;
          nex_node[1] = e_node;
          nex_node[2] = node_length;
          break;
        }

        if(from_node.equals(e_node)){
          node[6] = "1";
          nex_node = new String[3];
          nex_node[0] = node_link;
          nex_node[1] = s_node;
          nex_node[2] = node_length;
          break;
        }
      }
    }

    return nex_node;
  }

  public String[] calculateStationDistanceByLink(boolean closeFlag,Map<String,String> noLinkMap,List<String[]> links,String s_link,String e_link){

    int len = links.size();
    String[] node = null;
    String[] s_node = null;
    String[] e_node = null;
    //--
    for(int i=0;i<len;i++){
      node = links.get(i);
      String linkid = node[1];
      if(linkid.equals(s_link)){
        node[6] = "1";
        s_node = node;
      }else{
        node[6] = "0";
      }
    }
    if(s_node == null){
      return new String[]{"0",""};
    }
    //--
    for(int i=0;i<len;i++){
      node = links.get(i);
      String linkid = node[1];
      if(linkid.equals(e_link)){
        e_node = node;
      }
    }
    if(e_node == null){
      return new String[]{"0",""};
    }

    //--
    boolean flag = false;
    List<String[]> routeLinks = new ArrayList<String[]>();
    String from_node = s_node[5];
    String[] next_node = getNextLink(noLinkMap,links,from_node);

    //
    while(next_node != null){
      routeLinks.add(next_node);
      from_node = next_node[1];
      String next_link = next_node[0];
      if(next_link.equals(e_link)){
        flag = true;
        next_node = null;
      }else{
        next_node = getNextLink(noLinkMap,links,from_node);
      }
    }
    //
    if(!flag){
      for(int i=0;i<len;i++){
        node = links.get(i);
        String linkid = node[1];
        if(linkid.equals(s_link)){
          node[6] = "1";
        }else{
          node[6] = "0";
        }
      }
      //
      routeLinks = new ArrayList<String[]>();
      from_node = s_node[4];
      next_node = getNextLink(noLinkMap,links,from_node);
      while(next_node != null){
        routeLinks.add(next_node);
        from_node = next_node[1];
        String next_link = next_node[0];
        if(next_link.equals(e_link)){
          flag = true;
          next_node = null;
        }else{
          next_node = getNextLink(noLinkMap,links,from_node);
        }
      }
    }

    //--没有连通性
    if(!flag){
      for(int i=0;i<len;i++){
        node = links.get(i);
        String linkid = node[1];
        if(linkid.equals(s_link)){
          node[6] = "1";
        }else{
          node[6] = "0";
        }
      }
      //



    }


    if(flag){
      double sum_len = 0.0;
      int routeLinks_len = routeLinks.size();
      for(int i=0;i<routeLinks_len;i++){
        if(i==routeLinks_len-1){
          sum_len = sum_len + Double.parseDouble(routeLinks.get(i)[2])/2;
        } else {
          sum_len = sum_len + Double.parseDouble(routeLinks.get(i)[2]);
        }
      }

      /*
			for(String[] n:routeLinks){
				sum_len = sum_len + Double.parseDouble(n[2]);
			}
       */

      sum_len = sum_len + Double.parseDouble(s_node[2])/2;

      //--
      StringBuffer linkBuf = new StringBuffer();
      for(int i=0;i<routeLinks_len;i++){
        linkBuf.append(","+routeLinks.get(i)[0]);
      }
      String route_links = s_link + linkBuf.toString();
      return new String[]{sum_len+"",route_links};
    }

    return new String[]{"0",""};
  }

  private void calculateBusLineStationDistance(){

    List<String[]> distance_list = new ArrayList<String[]>();
    List<String[]> router_list = new ArrayList<String[]>();

    List<String[]> busLines = readBusLines();
    int len_1 = busLines.size();
    for(int i=0;i<len_1;i++){
      //for(int i=0;i<10;i++){
      distance_list = new ArrayList<String[]>();
      router_list = new ArrayList<String[]>();
      //
      String[] busline = busLines.get(i);
      String busline_id = busline[0];
      String busline_name = busline[1];
      List<String[]> list_links = readBusLineLinksB(busline_id);
      List<String[]> list_stations = readBusLineStations(busline_id);

      //-判断是否是闭合公交线
      //			String[] first_station = list_stations.get(0);
      //			String[] last_station = list_stations.get(list_stations.size()-1);
      //      String first_linkid = first_station[5];
      //      String last_linkid = last_station[5];

      boolean closeFlag = false;
      //			if(first_linkid.equals(last_linkid)){
      //			  closeFlag = true;
      //			}

      //------

      int len_station = list_stations.size();
      String[] s_station = null;
      String[] e_station = null;
      String[] p_station = null;
      for(int j=0;j<len_station-1;j++){
        s_station = list_stations.get(j);
        e_station = list_stations.get(j+1);
        String distance = "0";
        String routeLinks = "";
        String remark = "";
        //--
        int index = j+1;
        String s_id = s_station[0];
        String e_id = e_station[0];
        String s_name = s_station[1];
        String e_name = e_station[1];
        String s_linkid = s_station[5];
        String e_linkid = e_station[5];
        //--特殊处理
        Map<String,String> noLinkMap = new HashMap<String,String>();
        if(j==0){
          if(len_station > 3){
            p_station = list_stations.get(len_station-2);
            noLinkMap.put(p_station[5], p_station[5]);
          }
        } else {
          p_station = list_stations.get(j-1);
          noLinkMap.put(p_station[5], p_station[5]);
        }

        //--
        if(s_linkid != null && e_linkid != null){
          //
          boolean s_link_connection = this.testConnectivity(list_links, s_linkid);
          boolean e_link_connection = this.testConnectivity(list_links, e_linkid);
          
          if(!s_link_connection || !e_link_connection){
            distance = "0";
            routeLinks = "";
            remark = "";
            if(!s_link_connection){
              remark = "["+s_name+"]站点匹配存在连通性问题;";
            }
            if(!e_link_connection){
              remark = remark + "["+e_name+"]站点匹配存在连通性问题";
            }
            
          } else {
            if(s_linkid.equals(e_linkid)){
              double s_lon = Double.parseDouble(s_station[6]);
              double s_lat = Double.parseDouble(s_station[7]);
              double e_lon = Double.parseDouble(e_station[6]);
              double e_lat = Double.parseDouble(e_station[7]);
              distance = calculateDistance(s_lon,s_lat,e_lon,e_lat)+"";
              routeLinks = s_linkid;
              if(Double.parseDouble(distance) == 0){
                remark = s_name+" - "+e_name+"坐标一样";
              }
            } else {
              String[] distance_info = calculateStationDistanceByLink(closeFlag,noLinkMap,list_links,s_linkid,e_linkid);
              distance = distance_info[0];
              routeLinks = distance_info[1];
              if(Double.parseDouble(distance) == 0){
                remark = "两站间线路匹配有问题";
              }
              /*
              if(!routeLinks.equals("")){
                String[] routes = routeLinks.split(",");
                for(int k=0;k<routes.length;k++){
                  String router_link_id = routes[k];
                  String router_id = UUID.randomUUID().toString().replace("-", "");
                  router_list.add(new String[]{router_id,busline_id,busline_name,s_id,e_id,s_name,e_name,router_link_id});
                }
              }
              */
            }
            
          }//end testConnection
          

        } else {
          remark = "";
          if(s_linkid == null){
            remark = "["+s_name+"]站未匹配到路链上;";
          }
          if(e_linkid == null){
            remark = remark + "["+e_name+"]站未匹配到路链上";
          }
        }

        String id = UUID.randomUUID().toString().replace("-", "");
        distance_list.add(new String[]{id,busline_id,busline_name,s_id,e_id,s_name,e_name,distance,routeLinks,index+"",remark});

      }//end for station

      insertDbByBusstationDistance(distance_list);


    }//end for line

  }

  private void insertDbByBusstationDistance(List<String[]> distance_list){

    PreparedStatement ps = null;
    try {
      String batch_sql = "INSERT INTO busstation_distance_2016_01_07(id,buslineid,buslinename,s_stationid,e_stationid,s_stationname,e_stationname,distance,linkids,index,remark) values(?,?,?,?,?,?,?,?,?,?,?)";
      ps = conn_flow.prepareStatement(batch_sql);
      conn_flow.setAutoCommit(false);
      String[] bo;
      for(int i=0,len=distance_list.size();i<len;i++){
        bo = distance_list.get(i);
        ps.setString(1, bo[0]);
        ps.setString(2, bo[1]);
        ps.setString(3, bo[2]);
        ps.setString(4, bo[3]);
        ps.setString(5, bo[4]);
        ps.setString(6, bo[5]);
        ps.setString(7, bo[6]);
        if(bo[7] == null){
          ps.setDouble(8, 0);
        }else{
          try{
            ps.setDouble(8, Double.parseDouble(bo[7]));
          }catch(Exception ee){
            ps.setDouble(8, 0);
          }
        }
        ps.setString(9, bo[8]);
        ps.setInt(10, Integer.parseInt(bo[9]));
        ps.setString(11, bo[10]);
        //
        ps.addBatch();
        if(i%1000==0){
          ps.executeBatch();
          conn_flow.commit();  
          ps.clearBatch();
        }
      }
      //
      ps.executeBatch();
      conn_flow.commit();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally{
      try {
        if(ps != null){
          ps.close();
          ps = null;
        }
      } catch (SQLException e) {}
    }

  }

  private boolean testConnectivity(List<String[]> list_links,String stationLink){
    boolean connectivity = false;
    for(int i=0;i<list_links.size();i++){
      String[] link_info = list_links.get(i);
      String link = link_info[1];
      if(stationLink != null && stationLink.equals(link)){
        connectivity = true;
        break;
      }
    }
    return connectivity;
  }
  
  private void insertDbByBusstationLink(List<String[]> router_list){

    PreparedStatement ps = null;
    try {
      String batch_sql = "INSERT INTO busstation_router_temp(id,buslineid,buslinename,s_stationid,e_stationid,s_stationname,e_stationname,linkid) values(?,?,?,?,?,?,?,?)";
      ps = conn_flow.prepareStatement(batch_sql);
      conn_flow.setAutoCommit(false);
      String[] bo;
      for(int i=0,len=router_list.size();i<len;i++){
        bo = router_list.get(i);
        ps.setString(1, bo[0]);
        ps.setString(2, bo[1]);
        ps.setString(3, bo[2]);
        ps.setString(4, bo[3]);
        ps.setString(5, bo[4]);
        ps.setString(6, bo[5]);
        ps.setString(7, bo[6]);
        ps.setString(8, bo[7]);

        ps.addBatch();
        if(i%1000==0){
          ps.executeBatch();
          conn_flow.commit();  
          ps.clearBatch();
        }
      }
      //
      ps.executeBatch();
      conn_flow.commit();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally{
      try {
        if(ps != null){
          ps.close();
          ps = null;
        }
      } catch (SQLException e) {}
    }

  }

  
  /** 
   * 计算地球上任意两点(经纬度)距离 
   *  
   * @param long1 
   *            第一点经度 
   * @param lat1 
   *            第一点纬度 
   * @param long2 
   *            第二点经度 
   * @param lat2 
   *            第二点纬度 
   * @return 返回距离 单位：米 
   */  
  private double calculateDistance(double long1, double lat1, double long2, double lat2) {  
      double a, b, R;  
      R = 6378137; // 地球半径  
      lat1 = lat1 * Math.PI / 180.0;  
      lat2 = lat2 * Math.PI / 180.0;  
      a = lat1 - lat2;  
      b = (long1 - long2) * Math.PI / 180.0;  
      double d;  
      double sa2, sb2;  
      sa2 = Math.sin(a / 2.0);  
      sb2 = Math.sin(b / 2.0);  
      d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));  
      return d;  
  }  

  /***********************************************************/
  //--------------------站点合并
  /***********************************************************/

  private List<String> readStationMergeName(String arrow){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select a.name from busstation a, busline b, busstationlink c ");
    sqlBuf.append(" where a.tjcc = b.name ");
    sqlBuf.append(" and a.id = c.busstationid ");
    sqlBuf.append(" and b.arrow = '"+arrow+"' ");
    sqlBuf.append(" and b.state = '4' ");
    sqlBuf.append(" group by a.name ");
    sqlBuf.append(" order by a.name ");
    List<String> list = new ArrayList<String>();
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        list.add(rs.getString("name"));
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return list;
  }

  private String readStationMergeIds(String arrow,String stationName){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select a.id,a.name,a.tjcc,b.arrow ");
    sqlBuf.append("	from busstation a, busline b, busstationlink c,navigationline d ");
    sqlBuf.append("	where a.tjcc = b.name ");
    sqlBuf.append("	and a.id = c.busstationid ");
    sqlBuf.append("	and c.linkid = d.id ");
    sqlBuf.append(" and b.arrow = '"+arrow+"' ");
    sqlBuf.append(" and b.state = '4' ");
    sqlBuf.append(" and a.name = '"+stationName+"' ");

    List<String> list = new ArrayList<String>();
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        list.add(rs.getString("id"));
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    //
    StringBuffer idBuf = new StringBuffer();
    for(int i=0;i<list.size();i++){
      if(i<list.size()-1){
        idBuf.append(list.get(i)+",");
      }else{
        idBuf.append(list.get(i));
      }
    }

    return idBuf.toString();
  }

  private String readStationMergeCenter(String arrow,String stationName){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select ST_AsText(ST_Centroid(ST_Union(c.the_geom))) union_geo ");
    sqlBuf.append(" from busstation a, busline b, busstationlink c ");
    sqlBuf.append(" where a.tjcc = b.name ");
    sqlBuf.append(" and a.id = c.busstationid ");
    sqlBuf.append(" and b.arrow = '"+arrow+"' ");
    sqlBuf.append(" and b.state = '4' ");
    sqlBuf.append(" and a.name = '"+stationName+"' ");

    List<String> list = new ArrayList<String>();
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        list.add(rs.getString("union_geo"));
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    if(list.size() > 0){
      return list.get(0);
    }

    return null;
  }

  private String[] readStationMergeCenterCloseInfo(String arrow,String stationName,String centerWkt){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("	select a.name,a.tjcc,b.arrow,c.linkid,");
    sqlBuf.append("	ST_AsText(ST_ClosestPoint(d.the_geom,st_geometryfromtext('"+centerWkt+"',4326))) close_point,");
    sqlBuf.append("	ST_Distance(ST_Transform(d.the_geom,900913),ST_Transform(st_geometryfromtext('"+centerWkt+"',4326),900913)) distance ");
    sqlBuf.append("	from busstation a, busline b, busstationlink c,navigationline d ");
    sqlBuf.append("	where a.tjcc = b.name ");
    sqlBuf.append("	and a.id = c.busstationid ");
    sqlBuf.append("	and c.linkid = d.id ");
    sqlBuf.append(" and b.arrow = '"+arrow+"' ");
    sqlBuf.append("	and b.state = '4'");
    sqlBuf.append(" and a.name = '"+stationName+"' ");
    sqlBuf.append("	order by distance");

    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[2];
        bo[0] = rs.getString("linkid");
        bo[1] = rs.getString("close_point");
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }

    if(list.size() > 0){
      return list.get(0);
    }

    return null;
  }


  public void insertStationMergeList(){
    List<String[]> list_param = new ArrayList<String[]>();
    String arrow = "0";
    List<String> list_0 = readStationMergeName(arrow);

    for(int i=0;i<list_0.size();i++){
      String stationName = list_0.get(i);
      String busstationids = readStationMergeIds(arrow,stationName);
      String centerWkt = readStationMergeCenter(arrow,stationName);
      String[] closeInfo = readStationMergeCenterCloseInfo(arrow,stationName,centerWkt);
      String linkid = closeInfo[0];
      String closeWkt = closeInfo[1];
      String id = UUID.randomUUID().toString().replace("-", "");
      //--
      list_param.add(new String[]{id,linkid,stationName,busstationids,closeWkt,arrow});
    }

    //--
    arrow = "1";
    List<String> list_1 = readStationMergeName(arrow);

    for(int i=0;i<list_1.size();i++){
      String stationName = list_1.get(i);
      String busstationids = readStationMergeIds(arrow,stationName);
      String centerWkt = readStationMergeCenter(arrow,stationName);
      String[] closeInfo = readStationMergeCenterCloseInfo(arrow,stationName,centerWkt);
      String linkid = closeInfo[0];
      String closeWkt = closeInfo[1];
      String id = UUID.randomUUID().toString().replace("-", "");
      //--
      list_param.add(new String[]{id,linkid,stationName,busstationids,closeWkt,arrow});
    }


    PreparedStatement ps = null;
    try {
      String batch_sql = "INSERT INTO busstation_merge_temp(id,linkid,name,busstationids,the_geom,arrow) values(?,?,?,?,st_geometryfromtext(?,4326),?)";
      ps = conn_flow.prepareStatement(batch_sql);
      conn_flow.setAutoCommit(false);
      String[] bo;
      for(int i=0,len=list_param.size();i<len;i++){
        bo = list_param.get(i);
        ps.setString(1, bo[0]);
        ps.setString(2, bo[1]);
        ps.setString(3, bo[2]);
        ps.setString(4, bo[3]);
        ps.setString(5, bo[4]);
        ps.setString(6, bo[5]);
        ps.addBatch();
        if(i%1000==0){
          ps.executeBatch();
          conn_flow.commit();  
          ps.clearBatch();
        }
      }
      //
      ps.executeBatch();
      conn_flow.commit();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally{
      try {
        if(ps != null){
          ps.close();
          ps = null;
        }
      } catch (SQLException e) {}
    }


  }

  /***********************************************************/
  //--------------------区域公交资源统计
  /***********************************************************/

  private int getAreaBusLineCount_new(String area_code){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select count(t2.*) count_num from( ");
    sqlBuf.append("  select t.area_code,t.area_name,t.buslineid from( ");
    sqlBuf.append("    select a.code area_code,a.name area_name,b.buslineid ");
    sqlBuf.append("    from beijingqx a,buslinelink b,busline c ");
    sqlBuf.append("    where  1=1 ");
    sqlBuf.append("      and b.buslineid = c.id ");
    sqlBuf.append("      and c.isvalid = '1'");
    sqlBuf.append("      and ST_Contains(a.the_geom,b.the_geom) ");
    sqlBuf.append("      and a.code = "+area_code);
    sqlBuf.append("  ) t ");
    sqlBuf.append("  group by t.area_code,t.area_name,t.buslineid ");
    sqlBuf.append(") t2");

    int count_num = 0;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        count_num = rs.getInt("count_num");
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
    }
    return count_num;
  }


  private void statQxBusInfo(){
    List<String[]> list = readBjQx();
    for(int i=0;i<list.size();i++){
      String[] area = list.get(i);
      int line_count = getAreaBusLineCount_new(area[2]);
      String sql = "update beijingqx set buslinenum="+line_count +" where code="+area[2];
    }



  }


  private void navigationAreaChannelInfo(){
    List<String[]> list = readBjQx();
    for(int i=0;i<list.size();i++){
      String[] area = list.get(i);
      String code = area[2];


    }
  }


  public static void main(String[] args) throws Exception {
    ExportStationDistanceNew tool = new ExportStationDistanceNew();
    tool.init_flow();


    tool.calculateBusLineStationDistance();
    //tool.statQxBusInfo();


    tool.close_flow();

  }

}
