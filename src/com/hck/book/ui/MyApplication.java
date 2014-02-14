package com.hck.book.ui;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.hck.book.helper.BookDB;

public class MyApplication extends Application {
	
	public static BookDB bookDB;
	private static Context context;
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("hck", "oncreat");
		context = getApplicationContext();
		initDateBase();		
	}

	private static void initDateBase() {
		bookDB = new BookDB(context);	 
	}
}