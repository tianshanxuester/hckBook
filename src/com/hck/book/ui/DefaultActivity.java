package com.hck.book.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class DefaultActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(isDrawTitle()){
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		
		if(isFullScreen()){
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
	
	/**
	 * 是否显示标题栏，默认不显示
	 */
	protected boolean isDrawTitle(){
		return false;
	}
	
	/**
	 * 是否为全屏，默认为全屏显示
	 * @return
	 */
	protected boolean isFullScreen(){
		return true;
	}
	
}
