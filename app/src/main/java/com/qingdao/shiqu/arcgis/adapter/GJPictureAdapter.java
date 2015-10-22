package com.qingdao.shiqu.arcgis.adapter;

import java.io.File;
import java.util.List;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.GDColorAdapter.ViewHold;
import com.qingdao.shiqu.arcgis.sqlite.DoAction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GJPictureAdapter extends BaseAdapter {
	Context context;
	List<String> names;
	List<String> paths;

	public GJPictureAdapter(Context context, List<String> names,
			List<String> paths) {
		super();
		this.context = context;
		this.names = names;
		this.paths = paths;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return names.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO 自动生成的方法存根
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO 自动生成的方法存根
		return arg0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO 自动生成的方法存根
		ViewHold hold;
		if(view == null){
			hold = new ViewHold();
			view = LayoutInflater.from(context).inflate(R.layout.gjpictureadatpter, null);
			view.setTag(hold);
		}else
			hold = (ViewHold)view.getTag();
		hold.name = (TextView)view.findViewById(R.id.gjtv);
		hold.name.setText(names.get(arg0));
		hold.iv = (ImageView)view.findViewById(R.id.gjiv);
		File file = new File(paths.get(arg0));
		if(file.exists()){
		       Bitmap bm = BitmapFactory.decodeFile(paths.get(arg0));
		       hold.iv.setImageBitmap(DoAction.compressImage(bm,20)/*bm*/);
		}
		return view;
	}
	static class ViewHold{
		public ImageView iv;
		public TextView name;
	}

}
