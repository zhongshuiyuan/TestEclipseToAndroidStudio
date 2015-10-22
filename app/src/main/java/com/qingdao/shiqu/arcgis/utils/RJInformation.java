package com.qingdao.shiqu.arcgis.utils;

import java.util.List;

public class RJInformation {

	int nodeid;
	int equid;
	String equName;
	List<Fiber> fibers;
	public List<Fiber> getFibers() {
		return fibers;
	}
	public void setFibers(List<Fiber> fibers) {
		this.fibers = fibers;
	}
	public int getNodeid() {
		return nodeid;
	}
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	public int getEquid() {
		return equid;
	}
	public void setEquid(int equid) {
		this.equid = equid;
	}
	public String getEquName() {
		return equName;
	}
	public void setEquName(String equName) {
		this.equName = equName;
	}
	
}
