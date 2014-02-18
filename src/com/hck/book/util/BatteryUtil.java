package com.hck.book.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BatteryUtil {
	/** 电量百分比 */
	private String levelPercent;
	
	public BatteryUtil(Context ctx){
		 //注册一个广播接受者
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		ctx.registerReceiver(batteryChangedReceiver, intentFilter);
	}
	
	 private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
					int level = intent.getIntExtra("level", 0);
					int scale = intent.getIntExtra("scale", 100);
					levelPercent =  level * 100 / scale + "%";
				}
			}
		};
		
	/**
	 * 得到电池电量百分比	
	 * @return
	 */
	public String getBatteryLevelPercent(){
		return levelPercent;
	}
}
