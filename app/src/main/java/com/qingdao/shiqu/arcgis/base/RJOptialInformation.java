package com.qingdao.shiqu.arcgis.base;

public class RJOptialInformation {

	private int equID;			//设备ID
	private int OptialfiberID;	//光纤ID
	private boolean isLeft;		//是否在左边
	private boolean isRight;	//是否在右边
	private String color;		//颜色名称，蓝
	public int getEquID() {
		return equID;
	}
	public void setEquID(int equID) {
		this.equID = equID;
	}
	public int getOptialfiberID() {
		return OptialfiberID;
	}
	public void setOptialfiberID(int optialfiberID) {
		OptialfiberID = optialfiberID;
	}
	public boolean isLeft() {
		return isLeft;
	}
	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}
	public boolean isRight() {
		return isRight;
	}
	public void setRight(boolean isRight) {
		this.isRight = isRight;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
}
