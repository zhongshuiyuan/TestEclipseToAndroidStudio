package com.eruntech.crosssectionview.valueobjects;

/**
 * 管道截面展开图中，通过从服务端获取的管块数据构建出的管块数据对象类
 * @author 覃远逸
 */
public class TubePadInXml
{
	/** 管块对象的属性总数 **/
	public static int attributeCount = 6;

	private String beginWellAddress;

	public String getBeginWellAddress()
	{
		return beginWellAddress;
	}

	private String endWellAddress;

	public String getEndWellAddress()
	{
		return endWellAddress;
	}

	private String width;

	public String getWidth()
	{
		return width;
	}

	private String height;

	public String getHeight()
	{
		return height;
	}

	private String rowCount;

	public int getRowCount()
	{
		return Integer.parseInt(rowCount);
	}

	private String columnCount;

	public int getColumnCount()
	{
		return Integer.parseInt(columnCount);
	}

	public TubePadInXml(String beginWellAddress, String endWellAddress,
						String width, String height, String columnCount, String rowCount)
	{
		this.beginWellAddress = beginWellAddress;
		this.endWellAddress = endWellAddress;
		this.width = width;
		this.height = height;
		this.columnCount = columnCount;
		this.rowCount = rowCount;
	}
}
