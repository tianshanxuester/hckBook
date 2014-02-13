package com.hck.book.util;

import android.util.Log;

public class LogUtil {
	private static final String TAG = "wxd test";	
	
	public static void debug(String info){
		Log.d(TAG, info);
	}
}
