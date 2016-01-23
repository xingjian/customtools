package com.promise.gistool;

import com.vividsolutions.jts.geom.Geometry;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月12日 下午6:54:02  
 */
public class NavigationObject {

    public String id;
    public String direct;
    public String wkt;
    public String snode;
    public String enode;
    public Geometry geometry;
    
    public NavigationObject(String id, String direct, String wkt) {
        super();
        this.id = id;
        this.direct = direct;
        this.wkt = wkt;
    }
    
    public NavigationObject(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    public String getSnode() {
        return snode;
    }

    public void setSnode(String snode) {
        this.snode = snode;
    }

    public String getEnode() {
        return enode;
    }

    public void setEnode(String enode) {
        this.enode = enode;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    
    
}
