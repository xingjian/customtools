package com.promise.gistool;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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

import org.apache.poi.ss.usermodel.CellStyle;

import com.promise.cn.util.StringUtil;







public class ExportBusLineCSVGD {


  private static CellStyle style;
  private static CellStyle style2;

  public boolean heightVersionFlag = false;

  private Connection conn_flow = null;


  

 

  public void init_flow() throws Exception {
    Class.forName("org.postgresql.Driver");
    String url = "jdbc:postgresql://10.212.140.212:5432/basedata";
    conn_flow = DriverManager.getConnection(url, "basedata", "basedata");
  }

  public void close_flow() throws Exception {
    if(conn_flow != null){
      conn_flow.close();
      conn_flow = null;
    }
  }

  private static String BUSSTATION_DISTANCE_TABLE = "busstation_distance_1";

  //==============================

  private List<String> readLinesCode(){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select linecode from busline ");
    sqlBuf.append(" where id = uuidversion and linecode > 0 group by linecode order by linecode ");

    List<String> list = new ArrayList<String>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        list.add(rs.getString("linecode"));
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
      e.printStackTrace();
    }
    return list;
  }
  
  private List<String> readBusLinesByLineCode(String code){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select id,name,arrow,linename,linecode,ykt_sxx,state,isvalid from busline ");
    sqlBuf.append(" where linecode = "+code);
    sqlBuf.append("   and isvalid = '1'");
    sqlBuf.append("  order by arrow ");

    
    List<String> list = new ArrayList<String>();
    String[] bo = null;
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
        e.printStackTrace();
    }
    return list;
  }
  
  
  private List<String> readBusLinesByLineName(String name){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append("select * from busline where 1=1 and name like '"+name+"(%' ");
    sqlBuf.append(" order by arrow ");

    List<String> list = new ArrayList<String>();
    String[] bo = null;
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
        e.printStackTrace();
    }
    return list;
  }



  private Map<String,String[]> readBusLineLinksMap(String buslineid){
    StringBuffer sqlBuf = new StringBuffer();

    sqlBuf.append("select t.*,");
    sqlBuf.append(" c.busstationid,d.name,ST_X(c.the_geom) p_x,ST_Y(c.the_geom) p_y,st_line_locate_point(ST_GeometryN(t.the_geom,1),c.the_geom)*100 ratio ");
    sqlBuf.append("from( ");
    sqlBuf.append(" select a.navigationid,b.direction,b.snodeid,b.enodeid,b.mapid,b.idlink,b.kind,b.pathname,ST_Length(ST_Transform(a.the_geom,900913)) length,");
    sqlBuf.append("  ST_X(st_startpoint(ST_GeometryN(a.the_geom,1))) s_p_x,ST_Y(st_startpoint(ST_GeometryN(a.the_geom,1))) s_p_y, ");
    sqlBuf.append("  ST_X(st_endpoint(ST_GeometryN(a.the_geom,1))) e_p_x,ST_Y(st_endpoint(ST_GeometryN(a.the_geom,1))) e_p_y,a.the_geom ");
    sqlBuf.append(" from buslinelink a,navigationline b ");
    sqlBuf.append(" where 1=1 ");
    sqlBuf.append("   and a.navigationid = b.id ");
    sqlBuf.append("   and buslineid = '"+buslineid+"' ");
    sqlBuf.append(") t ");

    sqlBuf.append(" left join busstationlink c on c.linkid = t.navigationid and c.busstationid in(select id from busstation where buslineid='"+buslineid+"') ");
    sqlBuf.append(" left join busstation d on d.id = c.busstationid ");
    sqlBuf.append("");


    Map<String,String[]> list = new HashMap<String,String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[19];
        bo[1] = rs.getString("navigationid");
        bo[2] = rs.getString("length");
        bo[3] = rs.getString("direction");
        //--
        bo[4] = rs.getString("mapid");
        bo[5] = rs.getString("idlink");
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

        //---
        bo[6] = s_node_id+"";
        bo[7] = e_node_id+"";
        bo[8] = rs.getString("s_p_x");
        bo[9] = rs.getString("s_p_y");
        bo[10] = rs.getString("e_p_x");
        bo[11] = rs.getString("e_p_y");

        bo[12] = rs.getString("busstationid");
        bo[13] = rs.getString("name");
        bo[14] = rs.getString("p_x");
        bo[15] = rs.getString("p_y");
        bo[16] = rs.getString("ratio");
        bo[17] = rs.getString("pathname");
        bo[18] = rs.getString("kind");

        list.put(rs.getString("navigationid"), bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
        e.printStackTrace();
    }
    return list;
  }



  //=============工大csv数据更新=====================

  private List<String[]> readBusLineStationDistance(String buslineid){
    StringBuffer sqlBuf = new StringBuffer();
    sqlBuf.append(" select b.arrow,a.buslineid,a.s_stationid,a.e_stationid,a.s_stationname,a.e_stationname,a.index,a.distance,a.linkids ");
    sqlBuf.append(" from "+BUSSTATION_DISTANCE_TABLE+" a,busline b ");
    sqlBuf.append(" where a.buslineid = b.id ");
    sqlBuf.append(" and a.buslineid ='"+buslineid+"' ");
    sqlBuf.append(" order by b.arrow,index;");

    List<String[]> list = new ArrayList<String[]>();
    String[] bo = null;
    try{
      Statement stmt = conn_flow.createStatement();
      String sql = sqlBuf.toString();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {  
        bo = new String[9];
        bo[0] = rs.getString("arrow");
        bo[1] = rs.getString("buslineid");
        bo[2] = rs.getString("s_stationid");
        bo[3] = rs.getString("e_stationid");
        bo[4] = rs.getString("s_stationname");
        bo[5] = rs.getString("e_stationname");
        bo[6] = rs.getString("index");
        bo[7] = rs.getString("distance");
        bo[8] = rs.getString("linkids");
        list.add(bo);
      }
      rs.close();
      stmt.close();
    } catch(Exception e){
        e.printStackTrace();
    }
    return list;
  }



  private List<BusLineBean> buildGDCSVData(String buslineid,String lineName,String lineCode){

    List<String[]> list = readBusLineStationDistance(buslineid);
    Map<String,String[]> link_map = readBusLineLinksMap(buslineid);

    String[] bo;
    BusLineBean linkBean;
    //
    Map<String,String> tempMap = new HashMap<String,String>();
    List<BusLineBean> list_route = new ArrayList<BusLineBean>();

    int orderIndex = 0;
    int list_len = list.size();
    for(int i=0;i<list_len;i++){
      bo = list.get(i);
      String arrow = bo[0];
      String routeLinks = bo[8];
      Integer busstion_distance = Double.valueOf(bo[7]).intValue();
      //log.info("--busstion_distance=="+busstion_distance);
      if(!routeLinks.equals("")){
        String[] routes = routeLinks.split(",");
        int route_len = routes.length;
        if(i<list_len-1){
          route_len--;
        }

        for(int k=0;k<route_len;k++){
          String router_id = routes[k];
          String[] linkInfo = link_map.get(router_id);
          if(!tempMap.containsKey(router_id) && linkInfo != null){
            tempMap.put(router_id, router_id);
            orderIndex++;
            String mapid = linkInfo[4];
            String idlink = linkInfo[5];
            String direction = linkInfo[3];
            String length = linkInfo[2];
            String s_node_id = linkInfo[6];
            String e_node_id = linkInfo[7];

            String s_p_x = linkInfo[8];
            String s_p_y = linkInfo[9];
            String e_p_x = linkInfo[10];
            String e_p_y = linkInfo[11];

            String busstation_id = linkInfo[12];
            String busstation_name = linkInfo[13];
            String busstation_x = linkInfo[14];
            String busstation_y = linkInfo[15];
            String busstation_ratio = linkInfo[16];
            String path_name = linkInfo[17];
            String path_kind = linkInfo[18];
            //
            String[] roadType2 = new String[] { 
                "b.kind like '00%' or b.kind like '01%'", 
                "b.kind like '02%' or b.kind like '03%'", 
                "b.kind like '04%' or b.kind like '06%' or b.kind like '08%'" 
            };

            int arc_level = 0;
            if(path_kind.indexOf("00") !=-1 || path_kind.indexOf("01") !=-1){
              arc_level = 1;
            }
            if(path_kind.indexOf("02") !=-1 || path_kind.indexOf("03") !=-1){
              arc_level = 2;
            }
            if(path_kind.indexOf("04") !=-1 || path_kind.indexOf("06") !=-1 || path_kind.indexOf("08") !=-1){
              arc_level = 3;
            }

            linkBean = new BusLineBean();
            linkBean.setRoadLineID(lineName);
            linkBean.setRouteCode(lineCode);
            linkBean.setArcSartID(mapid+s_node_id);
            linkBean.setArcID(mapid+idlink);
            linkBean.setArcEndID(mapid+e_node_id);
            linkBean.setArcPathName(path_name);
            linkBean.setStationName(busstation_name);

            if(busstation_name!=null){
              linkBean.setStationLength(busstion_distance);
              linkBean.setStationlongitude(Double.parseDouble(busstation_x));
              linkBean.setStationlatitude(Double.parseDouble(busstation_y));
              linkBean.setRatio((int)Double.parseDouble(busstation_ratio));
            }else{
              linkBean.setRatio(-100);
              linkBean.setStationLength(null);
            }

            //--特殊处理
            if(i==list_len-1 && k ==route_len-1){
              linkBean.setStationLength(null);
            }

            linkBean.setArcLen((int)Double.parseDouble(length));


            linkBean.setIntDirection(Integer.parseInt(arrow)+1);
            linkBean.setIntOrder(orderIndex);

            linkBean.setSlongitude(Double.parseDouble(s_p_x));
            linkBean.setSlatitude(Double.parseDouble(s_p_y));
            linkBean.setElongitude(Double.parseDouble(e_p_x));
            linkBean.setElatitude(Double.parseDouble(e_p_y));
            linkBean.setArcLevel(arc_level);
            list_route.add(linkBean);
          }

        }//end for k
      }
    }//end for i

    /*
    for(int i=0;i<list_route.size();i++){
      linkBean = list_route.get(i);
      log.info("-ArcID="+linkBean.getArcID()+";Order="+linkBean.getIntOrder()+";Len="+linkBean.getArcLen()+
          ";Direction="+linkBean.getIntDirection()+";Station="+linkBean.getStationName()+";Ratio="+linkBean.getRatio()+
          ";Stationlon="+linkBean.getStationlongitude()+";Stationlat="+linkBean.getStationlatitude()+";StationLength="+linkBean.getStationLength()+";pathname="+linkBean.getArcPathName());

    }

    log.info("--link_map.size=="+link_map.size());
    log.info("--list_route.size=="+list_route.size());
     */
    return list_route;
  }

  private void insertOracleDbByBuslineDistance(List<BusLineBean> distance_list,String lineCode) throws Exception{
    PreparedStatement ps = null;
    PreparedStatement pstm = null;
    try {
      
      String sql = "DELETE FROM DAT_BUSLINEINFO_TEST WHERE ROUTECODE = ?";
      pstm = conn_flow.prepareStatement(sql);
      pstm.setString(1, lineCode);
      pstm.executeUpdate();
      pstm.close();
      //
      
      String batch_sql = "INSERT INTO DAT_BUSLINEINFO_TEST(INTORDER,STRBUSLINE,ROUTECODE,INTSTARTPOINTID,INTPOLYLINEID,INTENDPOINTID,INTDIRECTION,"
          + "ARCPATHNAME,STRBUSSTATION,STATIONSTART,ARCLEN,SLONGITUDE,SLATITUDE,ELONGITUDE,ELATITUDE,STATIONLONGITUDE,STATIONLATITUDE,STATIONLENGTH,ARCLEVEL,ID"
          + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

      ps = conn_flow.prepareStatement(batch_sql);
      conn_flow.setAutoCommit(false);
      BusLineBean bo;
      for(int i=0,len=distance_list.size();i<len;i++){
        bo = distance_list.get(i);
        //log.info(bo.getRoadLineID()+","+bo.getRouteCode()+","+bo.getRatio()+","+bo.getStationlongitude()+","+bo.getStationlatitude());
        ps.setInt(1, bo.getIntOrder());
        ps.setString(2, bo.getRoadLineID());
        ps.setString(3, bo.getRouteCode());
        ps.setLong(4, Long.parseLong(bo.getArcSartID()));
        ps.setLong(5, Long.parseLong(bo.getArcID()));
        ps.setLong(6, Long.parseLong(bo.getArcEndID()));
        ps.setInt(7, bo.getIntDirection());
        ps.setString(8, bo.getArcPathName());
        ps.setString(9, bo.getStationName());
        ps.setInt(10, bo.getRatio()==null?-100:bo.getRatio());
        ps.setInt(11, bo.getArcLen());

        ps.setString(12, bo.getSlongitude()==null?null:bo.getSlongitude().toString());
        ps.setString(13, bo.getSlatitude()==null?null:bo.getSlatitude().toString());
        ps.setString(14, bo.getElongitude()==null?null:bo.getElongitude().toString());
        ps.setString(15, bo.getElatitude()==null?null:bo.getElatitude().toString());
        ps.setString(16, bo.getStationlongitude()==null?null:bo.getStationlongitude().toString());
        ps.setString(17, bo.getStationlatitude()==null?null:bo.getStationlatitude().toString());
        ps.setString(18, bo.getStationLength()==null?null:bo.getStationLength().toString());
        ps.setInt(19, bo.getArcLevel());
        ps.setString(20, StringUtil.GetUUIDString());
        
        //
        ps.addBatch();
        if(i%500==0){
          ps.executeBatch();
          conn_flow.commit();  
          ps.clearBatch();
          //log.info("---"+i+"----");
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


  public void updateBusLineKind(String routeCode) throws Exception {
    Statement stm = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {

      String sql = " SELECT * FROM DAT_BUSLINEINFO_TEST WHERE 1=1 AND ROUTECODE='"+routeCode+"' ORDER BY ROUTECODE,INTORDER ";
      pstm = conn_flow.prepareStatement(sql);
      rs = pstm.executeQuery();

      stm = conn_flow.createStatement();
      String strBusLine = "";
      String strStationName = "";
      String id = "";
      int a = 0;
      int b = 0;
      int c = 0;
      int d = 0;
      while (rs.next()) {

        if (!strBusLine.equals(rs.getString("routecode"))) {
          if (!strBusLine.equals("")) {
            stm.executeBatch();
            strStationName = "";
            id = "null";
          }
          strBusLine = rs.getString("routecode");
        }

        if (rs.getString("STRBUSSTATION") != null && !rs.getString("STRBUSSTATION").equals("")) {

          if (!strStationName.equals("")) {
            Integer[] dataArray = new Integer[] { a, b, c, d };
            Integer[] levelArray = new Integer[] { 1, 2, 3, 4 };
            for (int i = 0; i < dataArray.length; i++) {
              int temp = dataArray[i];
              int levelTemp = levelArray[i];
              if (dataArray[i] > dataArray[i + 1]) {
                dataArray[i] = dataArray[i + 1];
                dataArray[i + 1] = temp;
                levelArray[i] = levelArray[i + 1];
                levelArray[i + 1] = levelTemp;
              }
              if ((i + 2) == dataArray.length) {
                break;
              }
            }

            sql = "UPDATE DAT_BUSLINEINFO_TEST SET INTKIND=" + levelArray[3] + " where id='" + id+"'";
            stm.addBatch(sql);
            a = 0;
            b = 0;
            c = 0;
            d = 0;
          }
          strStationName = rs.getString("STRBUSSTATION");
          id = rs.getString("ID");
        }

        if (rs.getInt("ARCLEVEL") == 1) {
          a++;
        } else if (rs.getInt("ARCLEVEL") == 2) {
          b++;
        } else if (rs.getInt("ARCLEVEL") == 3) {
          c++;
        } else if (rs.getInt("ARCLEVEL") == 4) {
          d++;
        }
      }
      stm.executeBatch();

      rs.close();
      stm.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {

    }

  }

  public void expBusLineInfo(String expFilePath,String lineCode) throws Exception {
    File file = new File(expFilePath);
    file.mkdirs();

    File f = new File(expFilePath + "line_" + lineCode + ".csv");
    if(f.exists()){
      f.delete();
    }
    
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn_flow.createStatement();
      String sql = "select a.ID,a.INTORDER,a.STRBUSLINE,a.INTSTARTPOINTID,a.INTPOLYLINEID,a.INTENDPOINTID,a.INTDIRECTION,a.STRBUSSTATION,a.INTDISTANCETOSTART,a.ARCLEN,a.SLONGITUDE,a.SLATITUDE,a.ELONGITUDE,a.ELATITUDE,a.STATIONLONGITUDE,a.STATIONLATITUDE,a.STATIONLENGTH,a.ROUTECODE,a.INTKIND,a.STATIONSTART "
          + " from DAT_BUSLINEINFO_TEST a where 1=1 and a.ROUTECODE='"+lineCode+"' order by a.ROUTECODE,a.INTORDER";
      rs = stm.executeQuery(sql);

      String routeCode = "";
      PrintWriter pw = null;
      while (rs.next()) {
        if (!routeCode.equals(rs.getString("ROUTECODE"))) {
          if (pw != null)
            pw.close();

          routeCode = rs.getString("ROUTECODE");
          System.out.println(routeCode);
          pw = new PrintWriter(new FileWriter(expFilePath + "line_" + rs.getString(18) + ".csv"));
          pw.println("ID ,INTORDER ,STRBUSLINE ,INTSTARTPOINTID ,INTPOLYLINEID ,INTENDPOINTID ,INTDIRECTION ,STRBUSSTATION ,STATIONSTART ,ARCLEN ,SLONGITUDE ,SLATITUDE ,ELONGITUDE ,ELATITUDE ,STATIONLONGITUDE ,STATIONLATITUDE ,STATIONLENGTH ,INTKIND");
        }

        pw.print(rs.getString("ID") == null ? "," : rs.getString("ID") + ",");
        pw.print(rs.getString("INTORDER") == null ? "," : rs.getString("INTORDER") + ",");
        pw.print(rs.getString("STRBUSLINE") == null ? "," : rs.getString("STRBUSLINE") + ",");
        pw.print(rs.getString("INTSTARTPOINTID") == null ? "," : rs.getString("INTSTARTPOINTID") + ",");
        pw.print(rs.getString("INTPOLYLINEID") == null ? "," : rs.getString("INTPOLYLINEID") + ",");
        pw.print(rs.getString("INTENDPOINTID") == null ? "," : rs.getString("INTENDPOINTID") + ",");
        pw.print(rs.getString("INTDIRECTION") == null ? "," : rs.getString("INTDIRECTION") + ",");
        pw.print(rs.getString("STRBUSSTATION") == null ? "," : rs.getString("STRBUSSTATION") + ",");
        pw.print(rs.getString("STATIONSTART") == null ? "," : rs.getString("STATIONSTART") + ",");
        pw.print(rs.getString("ARCLEN") == null ? "," : rs.getString("ARCLEN") + ",");
        pw.print(rs.getString("SLONGITUDE") == null ? "," : rs.getString("SLONGITUDE") + ",");
        pw.print(rs.getString("SLATITUDE") == null ? "," : rs.getString("SLATITUDE") + ",");
        pw.print(rs.getString("ELONGITUDE") == null ? "," : rs.getString("ELONGITUDE") + ",");
        pw.print(rs.getString("ELATITUDE") == null ? "," : rs.getString("ELATITUDE") + ",");
        pw.print(rs.getString("STATIONLONGITUDE") == null ? "," : rs.getString("STATIONLONGITUDE") + ",");
        pw.print(rs.getString("STATIONLATITUDE") == null ? "," : rs.getString("STATIONLATITUDE") + ",");
        pw.print(rs.getString("STATIONLENGTH") == null ? "," : rs.getString("STATIONLENGTH") + ",");
        pw.println(rs.getString("INTKIND") == null ? "" : rs.getString("INTKIND"));
      }
      pw.close();

      rs.close();
      stm.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {}

  }


  private void impGDBuslineData(String lineName,String lineCode,String buslineid_1,String buslineid_2) throws Exception{

    List<BusLineBean> list_arrow_1 = buildGDCSVData(buslineid_1, lineName, lineCode);
    
    List<BusLineBean> list_arrow_2 = new ArrayList<BusLineBean>();
    
    if(buslineid_2 != null){
      list_arrow_2 = buildGDCSVData(buslineid_2, lineName, lineCode);
    }

    List<BusLineBean> list_route = new ArrayList<BusLineBean>();
    for(int i=0;i<list_arrow_1.size();i++){
      BusLineBean bo = list_arrow_1.get(i);
      list_route.add(bo);
    }
    
    int prefix_order = list_arrow_1.size();
    for(int i=0;i<list_arrow_2.size();i++){
      BusLineBean bo = list_arrow_2.get(i);
      int new_order = prefix_order + bo.getIntOrder();
      bo.setIntOrder(new_order);
      list_route.add(bo);
    }

    insertOracleDbByBuslineDistance(list_route,lineCode);
  }


  public static void main(String[] args) throws Exception {
    String expFilePath = "D:\\buscity_csv_dev\\";
    
    ExportBusLineCSVGD tool = new ExportBusLineCSVGD();
    tool.init_flow();
    
    BUSSTATION_DISTANCE_TABLE = "busstation_distance_2016_05_04";
    
    //--
    String buslineid_1;
    String buslineid_2;
    
    List<String> list = tool.readLinesCode();
    int len = list.size();
    for(int i=0;i<len;i++){
    //for(int i=0;i<10;i++){
      
      String linecode = list.get(i);
      buslineid_1 = null;
      buslineid_2 = null;
      List<String> bus_lines = tool.readBusLinesByLineCode(linecode);
      
      if(bus_lines.size() > 0){
        buslineid_1 = bus_lines.get(0);
        if(bus_lines.size() == 2){
          buslineid_2 = bus_lines.get(1);
        }
        System.out.println(len+"["+i+"]."+linecode+"=="+buslineid_1+","+buslineid_2);
        tool.impGDBuslineData(linecode,linecode,buslineid_1,buslineid_2);
        tool.updateBusLineKind(linecode);
        //--
        tool.expBusLineInfo(expFilePath, linecode);
      }
    }

    tool.close_flow();
    
  }

}
