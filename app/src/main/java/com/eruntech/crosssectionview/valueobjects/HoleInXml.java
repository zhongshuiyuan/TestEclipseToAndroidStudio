package com.eruntech.crosssectionview.valueobjects;

/**
 * 管道截面展开图中，通过从服务端获取的管块数据构建出的管孔数据对象类
 * @author 覃远逸
 */
public class HoleInXml
{
	private String holeId;
	public long getHoleId()
	{
		return Long.parseLong(holeId);
	}

	private String status;
	public String getStatus()
	{
		return status;
	}

	private String owner;
	public String getOwner()
	{
		return owner;
	}

	private String material;
	public String getMaterial()
	{
		return material;
	}

	private String pipId;
	public String getPipId()
	{
		return pipId;
	}

	private String childId;
	public String getChildId()
	{
		return childId;
	}
	public void setChildId(String childId)
	{
		this.childId = childId;
		if (!childId.equals("")) {
			this.childIds = childId.split(",");
		}

	}

	private String[] childIds;
	public String[] getChildIds()
	{
		return childIds;
	}

	public HoleInXml(String holeId, String status, String owner,
					 String material, String pipId, String childId)
	{
		this.holeId = holeId;
		this.status = status;
		this.owner = owner;
		this.material = material;
		this.pipId = pipId;
		setChildId(childId);// this.childId = childId;
	}
}
