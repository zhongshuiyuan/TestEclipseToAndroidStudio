package com.qingdao.shiqu.arcgis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SelectableChannel;

import android.R.integer;
import android.content.Context;

import com.esri.android.map.ags.ArcGISLocalTiledLayer;

public class MyArc extends ArcGISLocalTiledLayer 
{
	private String path;
	Context context;
	
	public MyArc(String path) 
	{
		//System.out.println("");
		super(path);
	}
	
	public MyArc(Context context,String path)
	{
		super(path);
		writeFiles();
	}
	
	 private void writeFiles()
	 {
		 try {
			InputStream open = context.getAssets().open("conf.xml");
			File file = new File("/mnt/sdcard/layers/conf.xml");
			OutputStream outputStream = new FileOutputStream(file);
			byte[] buff = new byte[1024];
			int read;
			while ((read = open.read(buff)) != -1) {
				if(read == 0)
				{
					read = open.read();
					if(read < 0)
						break;
					outputStream.write(read);
					continue;
				}
				outputStream.write(buff, 0, read);
			}
			open.close();
			outputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		 String file = "";
	 }

}
