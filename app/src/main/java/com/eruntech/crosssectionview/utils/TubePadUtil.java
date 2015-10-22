package com.eruntech.crosssectionview.utils;

import android.content.Context;

import com.eruntech.crosssectionview.drawobjects.TubePad;
import com.eruntech.crosssectionview.valueobjects.TubePadInXml;

/**
 * 处理管块对象的各种类，包含{@link TubePad}和{@link TubePadInXml}类
 * @author 覃远逸
 */
public class TubePadUtil
{
	/**
	 * 将{@link TubePadInXml}类转化为{@link TubePad}类
	 * @param tubePadInXml 需要转化的{@link TubePadInXml}类
	 * @param context 应用程序上下文
	 * @return 转化好的{@link TubePad}类
	 */
	public static TubePad tubePadInXmlToTubePad(TubePadInXml tubePadInXml, Context context)
	{
		TubePad tubePad = new TubePad(context);

		tubePad.addressA = tubePadInXml.getBeginWellAddress();
		tubePad.addressZ = tubePadInXml.getEndWellAddress();
		tubePad.setRowNum(tubePadInXml.getRowCount());
		tubePad.setColumnNum(tubePadInXml.getColumnCount());

		return tubePad;
	}
}
