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
	 * �Ƿ���ʾ��������Ĭ�ϲ���ʾ
	 */
	protected boolean isDrawTitle(){
		return false;
	}
	
	/**
	 * �Ƿ�Ϊȫ����Ĭ��Ϊȫ����ʾ
	 * @return
	 */
	protected boolean isFullScreen(){
		return true;
	}
	
}
