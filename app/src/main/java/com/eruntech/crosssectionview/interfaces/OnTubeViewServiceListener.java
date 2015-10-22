package com.eruntech.crosssectionview.interfaces;

import java.util.List;

import com.eruntech.crosssectionview.valueobjects.HoleInXml;
import com.eruntech.crosssectionview.valueobjects.TubePadInXml;

public interface OnTubeViewServiceListener
{
	public void onTubeViewDataGot(TubePadInXml tubePadData, List<HoleInXml> holesData);
}
