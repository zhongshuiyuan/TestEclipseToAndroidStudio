package com.qingdao.shiqu.arcgis.interfaces;

import java.io.InputStream;
import java.util.List;

import com.qingdao.shiqu.arcgis.utils.RJInformation;


public interface RJParser {

	/**
	 * 解析输入流，dedao
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public List<RJInformation> parse(InputStream is)throws Exception;
	public String serialize(List<RJInformation> books) throws Exception;
}
