package com.example.popup_vs_dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class MainActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ImageView img1 = (ImageView)this.findViewById(R.id.img1);
		img1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				usePopup(img1);
			}
		});
		
		final ImageView img2 = (ImageView)this.findViewById(R.id.img2);
		img2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				useDialog(img2);
			}
		});
	}
	
	private void usePopup(final ImageView anchor){
		//参考： http://www.cnblogs.com/sw926/p/3230659.html
		LayoutInflater mInflater = LayoutInflater.from(this);
	 	ViewGroup rootView = (ViewGroup)mInflater.inflate(R.layout.menu, null);
	 	rootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		final PopupWindow popup = new PopupWindow(this);
		//setContentView之前一定要设置宽高，否则不显示
		popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		//去掉默认的背景 
		popup.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		popup.setContentView(rootView);
		//点击空白处的时候PopupWindow会消失
		popup.setTouchable(true); 
		popup.setOutsideTouchable(true);
		//如果focusable为false，在一个Activity弹出一个PopupWindow，按返回键，由于PopupWindow没有焦点，会直接退出Activity。如果focusable为true，PopupWindow弹出后，所有的触屏和物理按键都有PopupWindows处理。
		popup.setFocusable(true);
		//计算弹框位置
		int[] xy = calcPopupXY(rootView,anchor);
		//不用任何gravity，使用绝对的(x,y)坐标
		popup.showAtLocation((View)anchor.getParent(),Gravity.NO_GRAVITY, xy[0], xy[1]);
	}
	
	private void useDialog(final ImageView anchor){
		LayoutInflater mInflater = LayoutInflater.from(this);
	 	ViewGroup rootView = (ViewGroup)mInflater.inflate(R.layout.menu, null);
	 	rootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		Dialog dialog = new Dialog(this);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		//去掉默认的背景,下面两个都可以
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		//dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		//http://stackoverflow.com/questions/12348405/dialog-is-bigger-than-expected-when-using-relativelayout
		//dialog默认都是有title的
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题，否则会影响高度计算，一定要在setContentView之前调用，终于明白有一个设置theme的构造函数的目的了
		dialog.setContentView(rootView);
	
		//计算弹框位置
		int[] xy = calcPopupXY(rootView,anchor);
		//gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL.
		//参考: http://www.cnblogs.com/angeldevil/archive/2012/03/31/2426242.html
		dialog.getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
		params.x = xy[0];
		params.y = xy[1];
		
		dialog.show();
	}
	
	private int[] calcPopupXY(View rootView, View anchor){
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
    	int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
    	rootView.measure(w, h);  
    	int popupWidth = rootView.getMeasuredWidth();
    	int popupHeight = rootView.getMeasuredHeight();
    	Rect anchorRect = getViewAbsoluteLocation(anchor);
		int x = anchorRect.left + (anchorRect.right - anchorRect.left)/2 - popupWidth / 2;
		int y = anchorRect.top - popupHeight;
		return new int[]{x,y};
	}
	
    public static Rect getViewAbsoluteLocation(View view){
    	if(view == null){
    		return new Rect();
    	}
		// 获取View相对于屏幕的坐标
		int[] location = new int[2] ;
		view.getLocationOnScreen(location);//这是获取相对于屏幕的绝对坐标，而view.getLocationInWindow(location); 是获取window上的相对坐标，本例中只有一个window，二者等价
		// 获取View的宽高
		int width = view.getMeasuredWidth();
		int height = view.getMeasuredHeight();
		// 获取View的Rect
		Rect rect = new Rect();
		rect.left = location[0];
		rect.top = location[1];
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		return rect;
	}
}
