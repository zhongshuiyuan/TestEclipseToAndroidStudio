package com.qingdao.shiqu.arcgis.utils;

public class Fiber {
	String color;
	boolean isrj;
	int startnodeid;
	int endnodeid;
	int rorl;//0表示左边，1表示右边
	String name;
	String vtype;
	public String getVtype() {
		return vtype;
	}
	public void setVtype(String vtype) {
		this.vtype = vtype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRorl() {
		return rorl;
	}
	public void setRorl(int rorl) {
		this.rorl = rorl;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public boolean isIsrj() {
		return isrj;
	}
	public void setIsrj(boolean isrj) {
		this.isrj = isrj;
	}
	public int getStartnodeid() {
		return startnodeid;
	}
	public void setStartnodeid(int startnodeid) {
		this.startnodeid = startnodeid;
	}
	public int getEndnodeid() {
		return endnodeid;
	}
	public void setEndnodeid(int endnodeid) {
		this.endnodeid = endnodeid;
	}
}
