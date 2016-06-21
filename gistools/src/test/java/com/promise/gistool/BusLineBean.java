package com.promise.gistool;

public class BusLineBean implements Cloneable{
	
	private String roadLineID; //路名
	private String routeCode;  //路编号
	
	private String arcSartID;
	private String arcID;
	private String arcEndID;
	
	private String arcPathName;
	
	private String stationName;
	private Integer stationLength; 
  private Double stationlongitude;
  private Double stationlatitude;
	
	
	private Integer ratio;
	private Integer arcLen;
	
	private Integer intDirection;
	private Integer intOrder;
	
	private Double slongitude;
	private Double slatitude;
	private Double elongitude;
	private Double elatitude;
	
	private Integer intKind;
	private Integer arcLevel;

	public Integer getStationLength() {
		return stationLength;
	}
	public void setStationLength(Integer stationLength) {
		this.stationLength = stationLength;
	}
	public Integer getIntOrder() {
		return intOrder;
	}
	public void setIntOrder(Integer intOrder) {
		this.intOrder = intOrder;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getArcID() {
		return arcID;
	}
	public void setArcID(String arcID) {
		this.arcID = arcID;
	}
	public Double getSlongitude() {
		return slongitude;
	}
	public void setSlongitude(Double slongitude) {
		this.slongitude = slongitude;
	}
	public Double getSlatitude() {
		return slatitude;
	}
	public void setSlatitude(Double slatitude) {
		this.slatitude = slatitude;
	}
	public Double getElongitude() {
		return elongitude;
	}
	public void setElongitude(Double elongitude) {
		this.elongitude = elongitude;
	}
	public Double getElatitude() {
		return elatitude;
	}
	public void setElatitude(Double elatitude) {
		this.elatitude = elatitude;
	}
	public Integer getIntDirection() {
		return intDirection;
	}
	public void setIntDirection(Integer intDirection) {
		this.intDirection = intDirection;
	}
	public Integer getArcLen() {
		return arcLen;
	}
	public void setArcLen(Integer arcLen) {
		this.arcLen = arcLen;
	}
	public String getRoadLineID() {
		return roadLineID;
	}
	public void setRoadLineID(String roadLineID) {
		this.roadLineID = roadLineID;
	}
	public Integer getRatio() {
		return ratio;
	}
	public void setRatio(Integer ratio) {
		this.ratio = ratio;
	}
	
	public BusLineBean clone(){
		BusLineBean o = null;
    	try{
       	o = (BusLineBean)super.clone();
       }catch(CloneNotSupportedException e){
       	e.printStackTrace();
       }
       return o;
	}
	public String getRouteCode() {
		return routeCode;
	}
	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}
	public void setIntKind(Integer intKind) {
		this.intKind = intKind;
	}
	public Integer getIntKind() {
		return intKind;
	}
  public String getArcSartID() {
    return arcSartID;
  }
  public void setArcSartID(String arcSartID) {
    this.arcSartID = arcSartID;
  }
  public String getArcEndID() {
    return arcEndID;
  }
  public void setArcEndID(String arcEndID) {
    this.arcEndID = arcEndID;
  }
  public Double getStationlongitude() {
    return stationlongitude;
  }
  public void setStationlongitude(Double stationlongitude) {
    this.stationlongitude = stationlongitude;
  }
  public Double getStationlatitude() {
    return stationlatitude;
  }
  public void setStationlatitude(Double stationlatitude) {
    this.stationlatitude = stationlatitude;
  }
  public String getArcPathName() {
    return arcPathName;
  }
  public void setArcPathName(String arcPathName) {
    this.arcPathName = arcPathName;
  }
  public Integer getArcLevel() {
    return arcLevel;
  }
  public void setArcLevel(Integer arcLevel) {
    this.arcLevel = arcLevel;
  }
	
	
}
