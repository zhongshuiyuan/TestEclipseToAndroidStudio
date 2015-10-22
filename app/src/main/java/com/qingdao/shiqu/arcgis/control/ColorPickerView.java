package com.qingdao.shiqu.arcgis.control;

import com.qingdao.shiqu.arcgis.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//import com.style.colorpicker.R;
//import com.style.colorpicker.ColorPickerView.onColorChangedListener;

public class ColorPickerView extends FrameLayout {
	
	private ImageView iv_color_range;//棰滆壊閫夋嫨鐩�
	
	private ImageView iv_color_picker;//棰滆壊閫夋嫨鍣�

	private RelativeLayout rl_root;//鏍瑰竷灞�
	
	private int range_radius ;//鍦嗙洏鍗婂緞
	private int picker_radius;//棰滆壊閫夋嫨鍣ㄥ崐寰�
	private int centreX;//鍦嗙洏涓績X鍧愭爣
	private int centreY;//鍦嗙洏涓績Y鍧愭爣
	private int picker_centreX;//棰滆壊閫夋嫨鍣ㄤ腑蹇僗鍧愭爣
	private int picker_centreY;//棰滆壊閫夋嫨鍣ㄤ腑蹇僘鍧愭爣
	private Bitmap bitmap;//棰滆壊閫夋嫨鐩樺浘鐗�
	private onColorChangedListener colorChangedListener;//棰滆壊鍙樻崲鐩戝惉
	public ColorPickerView(Context context) {
		super(context); 
		
	}

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs); 
		init(context); 
	}
	

	/**
	 * 
	 * init:(鍒濆鍖�). <br/>
	 * @author msl
	 * @param context
	 * @since 1.0
	 */
	private void init(Context context){
		View view = LayoutInflater.from(context).inflate(R.layout.color_picker, this);
		iv_color_range = (ImageView) view.findViewById(R.id.iv_color_range);
		iv_color_picker = (ImageView) view.findViewById(R.id.iv_color_picker);
		rl_root = (RelativeLayout)view.findViewById(R.id.rl_root);
		//閫夋嫨鍣ㄨЕ鎽哥洃鍚�		
		iv_color_picker.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY;//涓婃瑙︽懜鍧愭爣	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int ea=event.getAction(); 
				if(range_radius==0){//鏈垵濮嬪寲
					range_radius =iv_color_range.getWidth()/2;//鍦嗙洏鍗婂緞
					picker_radius = iv_color_picker.getWidth()/2;//閫夋嫨鍣ㄥ崐寰�
					centreX =iv_color_range.getRight()-range_radius;
					centreY =iv_color_range.getBottom()-iv_color_range.getHeight()/2; 
				    bitmap =((BitmapDrawable)iv_color_range.getDrawable()).getBitmap();//鑾峰彇鍦嗙洏鍥剧墖
				}
				switch(ea){
				case MotionEvent.ACTION_DOWN://鎸変笅
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					getParent().requestDisallowInterceptTouchEvent(true);//閫氱煡鐖舵帶浠跺嬁鎷︽埅鏈帶浠秚ouch浜嬩欢
					break;
				case MotionEvent.ACTION_MOVE://鎷栧姩
					//鎷栧姩璺濈
					int dx =(int)event.getRawX() - lastX;
					int dy =(int)event.getRawY() - lastY;	
					//鐩稿浜庣埗鎺т欢鐨勬柊鍧愭爣
					int left = v.getLeft() + dx;
					int top = v.getTop() + dy;
					int right = v.getRight() + dx;
					int bottom = v.getBottom() + dy;	
					//閫夋嫨鍣ㄥ渾蹇冨潗鏍�
					picker_centreX = right-picker_radius;
					picker_centreY = bottom-picker_radius;

					//閫夋嫨鍣ㄥ渾蹇冧笌鍦嗙洏鍦嗗績璺濈
					float diff = FloatMath.sqrt((centreY-picker_centreY)*(centreY-picker_centreY)+(centreX-picker_centreX)*
							(centreX-picker_centreX))+picker_radius/2;//涓や釜鍦嗗績璺濈+棰滆壊閫夋嫨鍣ㄥ崐寰�
					//鍦ㄨ竟璺濆唴锛屽垯鎷栧姩
					if(diff<=range_radius){
						v.layout(left, top, right, bottom);
						int pixel = bitmap.getPixel(picker_centreX-iv_color_range.getLeft(), picker_centreY-iv_color_range.getTop());//鑾峰彇閫夋嫨鍣ㄥ渾蹇冨儚绱�
						if(colorChangedListener!=null){//璇诲彇棰滆壊
							colorChangedListener.colorChanged(Color.red(pixel), Color.blue(pixel), Color.green(pixel));
						}
						Log.d("TAG", "radValue="+Color.red(pixel)+"  blueValue="+Color.blue(pixel)+"  greenValue"+Color.green(pixel));
						lastX = (int) event.getRawX();
						lastY = (int) event.getRawY();
					}
					getParent().requestDisallowInterceptTouchEvent(true);//閫氱煡鐖舵帶浠跺嬁鎷︽埅鏈帶浠秚ouch浜嬩欢
					break;
				default:
					break;
				}
				return false;
			}
		});
		
	
	
	}
	
	
	
    public onColorChangedListener getColorChangedListener() {
		return colorChangedListener;
	}

	public void setColorChangedListener(onColorChangedListener colorChangedListener) {
		this.colorChangedListener = colorChangedListener;
	}



	/**
	 * 棰滆壊鍙樻崲鐩戝惉鎺ュ彛
	 */
	public interface onColorChangedListener{
    	public void colorChanged(int red,int blue,int green);
    }
	 
	



}
