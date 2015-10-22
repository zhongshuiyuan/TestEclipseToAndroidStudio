package com.qingdao.shiqu.arcgis;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.esri.core.internal.io.handler.n;


import android.R.string;
import android.util.Base64;
import android.util.Log;

public class Des
{
	private byte[] iv = {1,2,3,4,5,6,7,8};
	private String key = "12345678";
	//解密
	public InputStream decode(InputStream inputStream) throws Exception
	{

		String text = streamToString(inputStream);
		//		String text = "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";
		//text += "7CaTPsCb4iizV3iUqEkOUW6p6/IHZ534X3hzyxnn8h02EKnfHFW9HeqF6TNW+ZRrtgizEv45aXc6jD5zi6zNFqkZMTvOvTJ35lHOH/l6RD0TNVIKZNhlf4RFmIg7JVnvqqzS8yuyutAjfd3yVXAvEjIvR5RBwLl7NJAkUsh6iqtUCds/vRTQHBfj5aEEH3GXB1RpT6FZt7E53sO7JvU17c2htd1r+H2gQpzninY0QAOJC2mVZarl2k8KpnBuhGBYsPJg0uZvPpfu7SGqYdIKQvsHFVuJ0cE3J14KwGxg3YdmPl/VZPeC3jSZqUiTLYO99hmls0kUTp8WQtRewuY5z6KFo0";
		Log.i("Des", text);
//			String[] split = text.
//			text = split[0];
		//	String b = text.substring(0, 10);
		byte[] de = Base64.decode(text, Base64.DEFAULT);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey,zeroIv);
		byte[] doFinal = cipher.doFinal(de);
		return new   ByteArrayInputStream(doFinal);
		//return new String(doFinal);
	}
	private String streamToString(InputStream inputStream)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try
		{
//				while ((line = reader.readLine()) != null)
//				{
//					sb.append(line);
//				}
			sb.append(reader.readLine());
		} catch (IOException e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				inputStream.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
