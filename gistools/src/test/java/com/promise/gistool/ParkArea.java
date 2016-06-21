package com.promise.gistool;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年4月5日 下午6:22:03  
 */
public class ParkArea {
    //id,small_area_code,park_code,park_name,park_type,park_geom
    public String id;
    public String smallArea;
    public String parkCode;
    public String parkName;
    public String park_type;
    public String park_geom;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSmallArea() {
        return smallArea;
    }
    public void setSmallArea(String smallArea) {
        this.smallArea = smallArea;
    }
    public String getParkCode() {
        return parkCode;
    }
    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }
    public String getParkName() {
        return parkName;
    }
    public void setParkName(String parkName) {
        this.parkName = parkName;
    }
    public String getPark_type() {
        return park_type;
    }
    public void setPark_type(String park_type) {
        this.park_type = park_type;
    }
    public String getPark_geom() {
        return park_geom;
    }
    public void setPark_geom(String park_geom) {
        this.park_geom = park_geom;
    }
    
}
