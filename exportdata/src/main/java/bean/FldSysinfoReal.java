package bean;

import java.sql.Date;

public class FldSysinfoReal {
	//主键
	private String id;
    //监测表名ID	
	private String tableid;
	//数据最新更新时间
	private Date tm;
	private String sourceType;
	
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public Date getTm() {
		return tm;
	}
	public void setTm(Date tm) {
		this.tm = tm;
	}
}
