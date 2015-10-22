package com.qingdao.shiqu.arcgis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * ************************************
 *
 *      作  者： 潘跃瑞 
 *
 *      功  能： Assets数据操作
 *
 *      时  间：  2014-10-30
 *
 *      版  权：  青岛盛天科技有限公司 
 *
 ***************************************
 */
public class AssetHelper {
	private static final String TAG = "AssetHelper";

	public static void CopyAsset(Context ctx, File path, String filename)
			throws IOException {
		AssetManager assetManager = ctx.getAssets();
		InputStream in = null;
		OutputStream out = null;

		try {
			in = assetManager.open(filename);
			out = new FileOutputStream(path.toString() + "/" + filename);
			copyFile(in, out);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			throw e;
		} finally {
			// Reclaim resources
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.flush();
				out.close();
				out = null;
			}
		}
	}

	static private void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
