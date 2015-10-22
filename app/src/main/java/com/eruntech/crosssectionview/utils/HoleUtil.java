package com.eruntech.crosssectionview.utils;

import android.content.Context;

import com.eruntech.crosssectionview.drawobjects.Hole;
import com.eruntech.crosssectionview.valueobjects.HoleInDataGrid;
import com.eruntech.crosssectionview.valueobjects.HoleInXml;

/**
 * 处理管孔对象的各种类，包含{@link Hole}类和{@link HoleInXml}类以及{@link HoleInDataGrid}类
 * @author 覃远逸
 */
public class HoleUtil
{
	/**
	 * 将{@link HoleInXml}类转化为{@link Hole}类
	 * @param holeInXml 待转化的{@link HoleInXml}类
	 * @param context 应用程序上下文
	 * @return 转化好的{@link Hole}类
	 */
	public static Hole holeInXmlToHole(HoleInXml holeInXml, Context context)
	{
		Hole hole = new Hole(context);

		hole.id = holeInXml.getHoleId();
		hole.status = holeInXml.getStatus();
		hole.setOwner(holeInXml.getOwner());// hole.owner = holeInXml.getOwner();
		hole.material = holeInXml.getMaterial();
		hole.indexId = holeInXml.getPipId();

		if (holeInXml.getChildIds() != null) {
			final int childHoleCount = holeInXml.getChildIds().length;
			Hole[] childHoles = new Hole[childHoleCount];
			for (int i = 0; i < childHoleCount; ++i) {
				childHoles[i] = new Hole(context);
				childHoles[i].id = Long.parseLong(holeInXml.getChildIds()[i]);
				childHoles[i].parentId = hole.id;
			}
			hole.setChildHoles(childHoles);
		}


		return hole;
	}

	public static HoleInDataGrid holeToHoleInDataGrid(Hole hole)
	{
		String index = hole.indexId;
		String owner = hole.getOwner();
		String status = hole.status;
		String material = hole.material;

		HoleInDataGrid holeInDataGrid = new HoleInDataGrid(index, owner, status, material);
		holeInDataGrid.id = hole.id;

		return holeInDataGrid;
	}
}
