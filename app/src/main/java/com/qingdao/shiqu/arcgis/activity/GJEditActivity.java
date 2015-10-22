package com.qingdao.shiqu.arcgis.activity;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.qingdao.shiqu.arcgis.R;
import com.qingdao.shiqu.arcgis.adapter.GJPictureAdapter;
import com.qingdao.shiqu.arcgis.sqlite.DoAction;

import Eruntech.BirthStone.Core.Parse.Data;
import Eruntech.BirthStone.Core.Parse.DataCollection;
import Eruntech.BirthStone.Core.Parse.DataTable;
import Eruntech.BirthStone.Core.Sqlite.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GJEditActivity extends Activity {
	public final static int CONSULT_DOC_PICTURE = 1000;  
	public final static int CONSULT_DOC_CAMERA = 1001;  


	private int SELECT_PICTURE = 1;  

	private ImageView iv;
	private EditText et;
	private Button more,add,seeall;
	private SQLiteDatabase db;
	private Bitmap bmp = null;
	private ListView gjlist;
	private String PATH = "";
	ArrayAdapter<String> adp;
	private String itemName;
	GJPictureAdapter gpa;
	List<String> names;
	List<String> paths;
	String ID;
//	final String NAME;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		db = new SQLiteDatabase(GJEditActivity.this);
		setContentView(R.layout.gjedit_activity);
		iv = (ImageView)findViewById(R.id.jpic);
		more = (Button)findViewById(R.id.more);
		add = (Button)findViewById(R.id.add);
		et = (EditText)findViewById(R.id.jtype);
		seeall = (Button)findViewById(R.id.seeall);
		gjlist = (ListView)findViewById(R.id.gjlist);
		setClick();
		setItemLongPress();
		handler.post(update_thread);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自动生成的方法存根
		if (resultCode == RESULT_OK) {  
			/*if(data != null){
				if(data.hasExtra("data")){  
					 bmp = data.getParcelableExtra("data");  
					//得到bitmap后的操作  
					} 

			}
			else{*/
				Uri uri = data.getData();
				ContentResolver cr = this.getContentResolver();
				try {
					if (bmp != null)// 如果不释放的话，不断取图片，将会内存不够
						bmp.recycle();
					bmp = BitmapFactory.decodeStream(cr.openInputStream(uri));
					Uri originalUri = data.getData();        //获得图片的uri 
					//获取图片路径
					String[] proj = {MediaStore.Images.Media.DATA};
					Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					PATH = cursor.getString(column_index);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i("path", PATH);
				iv.setImageBitmap(bmp);
//			}
			
		} else {
			Toast.makeText(GJEditActivity.this, "请重新选择图片", Toast.LENGTH_SHORT).show();
		}  
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void getGJNameAndPath(){
		names = new ArrayList<String>();
		paths = new ArrayList<String>();
		DataTable rst = db.executeTable("aqBS_GJTypeQuery", null);
		for(int i=0;i<rst.size();i++){
			DataCollection dc = rst.next();
			names.add(dc.get("gjtypeid").Value.toString()+":"+dc.get("gjtypename").Value.toString());
			paths.add(dc.get("GJTypeImg").Value.toString());
		}
		adp = new ArrayAdapter<String>(GJEditActivity.this, android.R.layout.simple_list_item_1, names);
		gpa = new GJPictureAdapter(this, names, paths);
	}

	Handler handler = new Handler() ;  
	//要用handler来处理多线程可以使用runnable接口，这里先定义该接口
	//线程中运行该接口的run函数
	Runnable update_thread = new Runnable()
	{
		public void run()
		{
			getGJNameAndPath();
			gjlist.setAdapter(gpa);
		}
	};
	/**
	 * set listener
	 */
	public void setClick(){
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent intent = new Intent();  
				// 开启Pictures画面Type设定为image   
				intent.setType("image/*");  
				// 使用Intent.ACTION_GET_CONTENT这个Action   
				intent.setAction(Intent.ACTION_GET_CONTENT);   
				// 取得相片后返回本画面   
				startActivityForResult(intent, 1);  
			}
		});
		more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);                    //自带的浏览文件Activity
				intent.addCategory(Intent.CATEGORY_OPENABLE);             
				intent.setType("image/*");                                 //这是到达该image路径下，的所有文件，默认为内存卡的
				startActivityForResult(Intent.createChooser(intent,"选择图片"), SELECT_PICTURE);
			}
		});
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				if(add.getText().equals("添加")){
					if(DoAction.IsGJNameExist(GJEditActivity.this, et.getText().toString())){
						Toast.makeText(GJEditActivity.this, "该管井名称已存在，请修改名称！", Toast.LENGTH_SHORT).show();
						return;
					}
					DoAction.addGJType(GJEditActivity.this, et.getText().toString(), PATH);
					/*DataCollection pramas = new DataCollection();
					pramas.add(new Data("gjtypeid", null));
					pramas.add(new Data("gjtypename",et.getText().toString()));
					if(PATH.equals("")){
						Toast.makeText(GJEditActivity.this, "图片错误，请重新选择！", Toast.LENGTH_SHORT).show();
						return;
					}
					pramas.add(new Data("gjtypeimg", PATH));
					db.execute("aqBS_GJTypeAdd", pramas);*/
					handler.post(update_thread);
				}else if(add.getText().equals("修改")){
					DoAction.UpdateGDTypeInfo(GJEditActivity.this, ID, et.getText().toString(),PATH);
					add.setText("添加");
					et.setText("");
					handler.post(update_thread);
				}
				
			}
		});
		seeall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				/*getGJNameAndPath();
				gjlist.setAdapter(gpa);*/
				handler.post(update_thread);
			}
		});
	}
	/**
	 * 说	明：设置ListVew中Item长按的响应
	 */
	private void setItemLongPress(){
		gjlist.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View view,
					ContextMenuInfo menuInfo) {
				// TODO 自动生成的方法存根
				TextView tv = (TextView)view.findViewById(R.id.gjtv);
				itemName = (String) tv.getText();
				menu.add(0,0,0,"编辑");
				menu.add(0,1,0,"删除");
			}
		});

	}
	/* （非 Javadoc）
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 * the long press result
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) { 

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
				.getMenuInfo(); 
		info.targetView.findViewById(android.R.id.text1);
		String[] ids = itemName.split(":");

		switch (item.getItemId()) { 
		case 0: 
			// 修改操作 
			add.setText("修改");
			/*DoAction.UpdateGDTypeInfo(GJEditActivity.this, ids[0], ids[1]);
			add.setText("添加");*/
			ID = ids[0];
			et.setText(ids[1]);
			String path = DoAction.getGJPathByName(GJEditActivity.this, ids[1]);
			File file = new File(path);
			if(file.exists()){
			       Bitmap bm = BitmapFactory.decodeFile(path);
			       iv.setImageBitmap(DoAction.compressImage(bm,20));
			}
			break; 

		case 1: 
			// 删除操作 
			DoAction.DeleteGDType(GJEditActivity.this, ids[0]);
			break; 

		case 2: 
			// 删除ALL操作 
			break; 

		default: 
			break; 
		} 

		return super.onContextItemSelected(item); 
	} 
	/**
	 * no use
	 * @param c
	 * 			cursor
	 * @param iconIndex
	 * 			index
	 * @return
	 * 			convent bitmap
	 */
	public Bitmap getIconFromCursor(Cursor c, int iconIndex) { 
		byte[] data = c.getBlob(iconIndex); 
		try {

			Log.v("BitmapFactory.decodeByteArray.length000:", "BitmapFactory.decodeByteArray.length");
			Log.v("BitmapFactory.decodeByteArray.length:", BitmapFactory.decodeByteArray(data, 0, data.length).getWidth()+"");
			Log.v("BitmapFactory.decodeByteArray.length111:", "BitmapFactory.decodeByteArray.length");

			return BitmapFactory.decodeByteArray(data, 0, data.length); 
		} catch (Exception e) { 
			return null; 
		}
	}

}
