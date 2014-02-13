package com.hck.book.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.hck.book.vo.BookInfo;
import com.hck.date.FinalDate;
import com.hck.test.R;
import com.wxd.bookreader.manager.BookManager;

public class LodingActivity extends DefaultActivity {

	private ImageView imageView;	
	private SharedPreferences sp;
	private Editor editor;	
	
	BookManager bookManager = new BookManager();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FinalDate.context=this;
		sp = getSharedPreferences("book", Context.MODE_PRIVATE);
		if (sp.getLong("time", 0) == 0) {
			editor = sp.edit();
			editor.putLong("time", System.currentTimeMillis());
			editor.commit();
			FinalDate.tme=System.currentTimeMillis();
		}
		else {
			FinalDate.tme=sp.getLong("time", 0);
		}
		if (sp.getBoolean("isFirst", true)) {
			FinalDate.isFirst=true;
			editor = sp.edit();
			editor.putBoolean("isFirst", false);
			editor.commit();
		}
		else {
			FinalDate.isFirst=false;
		}
		
		
		setContentView(R.layout.loding);
		imageView = (ImageView) findViewById(R.id.loding_im);	 
		
		new AsyncSetApprove().execute();
	}
	
 
	class AsyncSetApprove extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			File path = getFilesDir();
		
			String[] strings = getResources().getStringArray(R.array.bookid);// 获取assets目录下的文件列表
			for (int i = 0; i < strings.length; i++) {
				try {
					FileOutputStream out = new FileOutputStream(path + "/"+ strings[i]);
					BufferedInputStream bufferedIn = new BufferedInputStream(getResources().openRawResource(R.raw.book0 + i));
					BufferedOutputStream bufferedOut = new BufferedOutputStream(out);
					byte[] data = new byte[2048];
					int length = 0;
					while ((length = bufferedIn.read(data)) != -1) {
						bufferedOut.write(data, 0, length);
					}
					// 将缓冲区中的数据全部写出
					bufferedOut.flush();
					// 关闭流
					bufferedIn.close();
					bufferedOut.close();
					
					sp.edit().putBoolean("isInit", true).commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			ArrayList<HashMap<String, String>> insertList = new ArrayList<HashMap<String, String>>();
			File[] f1 = path.listFiles();
			int len = f1.length;
			for (int i = 0; i < len; i++) {
				if (f1[i].isFile()) {
					if (f1[i].toString().contains(".txt")) {
						HashMap<String, String> insertMap = new HashMap<String, String>();
						insertMap.put("parent", f1[i].getParent());
						insertMap.put("path", f1[i].toString());
						insertList.add(insertMap);
					}
				}
			}
			
			bookManager.deleteInvalidBook();			

			for (int i = 0; i < insertList.size(); i++) {
				try {
					if (insertList.get(i) != null) {
						BookInfo book = new BookInfo();
						book.parent = insertList.get(i).get("parent");
						book.path = insertList.get(i).get("path");
						book.type = "2";
						book.now = "0";
						bookManager.insertBook(book);
					}
				} catch (SQLException e) {
					Log.e("hck", "setApprove SQLException", e);
				} catch (Exception e) {
					Log.e("hck", "setApprove Exception", e);
				}
			}
			
			sp.edit().putBoolean("isInit", true).commit();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			startActivity(new Intent(LodingActivity.this,BookListActivity2.class));
			LodingActivity.this.finish();
		}
	}	
}