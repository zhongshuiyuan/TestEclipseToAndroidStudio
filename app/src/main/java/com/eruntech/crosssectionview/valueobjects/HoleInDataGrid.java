package com.eruntech.crosssectionview.valueobjects;

/**
 * 管孔数据类，用于储存需要显示在表格的管孔数据
 * @author 覃远逸
 */
public class HoleInDataGrid
{
	/** 编号，例如：1-1，1-2，2-1，2-1等 **/
	public final int INDEX = 0;
	/** 产权，例如：有线，移动，联通，电信等 **/
	public final int OWNER = 1;
	/** 管孔状态，例如：空管孔等 **/
	public final int STATUS = 2;
	/** 管孔材质，例如：七孔梅花管，PVC100管等 **/
	public final int MATERIAL = 3;
	/** 管孔属性 **/
	public String[] attribute = new String[4];
	/** 管孔对象ID **/
	public long id = 0;
	/** 管孔所在的行的序数 **/
	private int rowIndex = 0;
	/** 管孔所在的列的序数 **/
	private int columnIndex = 0;

	public HoleInDataGrid(String index, String owner, String status, String material)
	{
		attribute[INDEX] = index;
		attribute[OWNER] = owner;
		attribute[STATUS] = status;
		attribute[MATERIAL] = material;

		// 计算管孔的行序号和列序号
		// 注意：如果是表头，attribute[INDEX] = "编号"
		// attribute[INDEX] = "1-2" 形如XX-XX
		String[] indexs = attribute[INDEX].split("-");
		if (indexs.length == 1) {
			// 表头，设置最小的序号以便排在第一
			rowIndex = 0;
			columnIndex = 0;
		} else if (indexs.length == 2) {
			// 列表项
			rowIndex = Integer.parseInt(indexs[0]);
			columnIndex = Integer.parseInt(indexs[1]);
		} else {
			throw new RuntimeException("管孔编号格式错误，应该形如X-X, XX-X, XX-XX等");
		}

	}

	/**
	 * 获取管孔所在的行的序数
	 * @return
	 */
	public int getRowIndex()
	{
		return this.rowIndex;
	}

	/**
	 * 获取管孔所在的列的序数
	 * @return
	 */
	public int getColumnIndex()
	{
		return this.columnIndex;
	}
}
