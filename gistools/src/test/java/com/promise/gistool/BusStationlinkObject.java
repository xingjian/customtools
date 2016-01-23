package com.promise.gistool;

import com.vividsolutions.jts.geom.Geometry;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年12月31日 下午1:52:09  
 */
public class BusStationlinkObject {
    public String id;
    public String wkt;
    public Geometry geometry;
    public int index;
    public String linkid;
    public String name;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getWkt() {
        return wkt;
    }
    public void setWkt(String wkt) {
        this.wkt = wkt;
    }
    public Geometry getGeometry() {
        return geometry;
    }
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getLinkid() {
        return linkid;
    }
    public void setLinkid(String linkid) {
        this.linkid = linkid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    
}
