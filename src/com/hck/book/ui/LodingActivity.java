package com.hck.book.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.hck.book.vo.BookInfo;
import com.hck.date.FinalDate;
import com.hck.test.R;
import com.wxd.bookreader.manager.BookManager;

public class LodingActivity extends DefaultActivity {

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
		new AsyncSetApprove().execute();
	}	
 
	class AsyncSetApprove extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			
			//删除内部图书
			bookManager.deleteInnerBook();
			
			File path = getFilesDir();
		
			//将应用程序自带的txt电子书复制到手机本地目录中
			String[] strings = getResources().getStringArray(R.array.bookid);
			for (int i = 0; i < strings.length; i++) {
				try {
					File file = new File(path + "/"+ strings[i]);
					FileOutputStream out = new FileOutputStream(file);
					BufferedInputStream bufferedIn = new BufferedInputStream(getResources().openRawResource(R.raw.book0 + i));
					BufferedOutputStream bufferedOut = new BufferedOutputStream(out);
					byte[] data = new byte[2048];
					int length = 0;
					while ((length = bufferedIn.read(data)) != -1) {
						bufferedOut.write(data, 0, length);
					}				
					// 关闭流
					bufferedIn.close();
					bufferedOut.close();					
					
					//将书籍信息放入到数据库中					
					BookInfo book = new BookInfo();
					book.parent = file.getParent();
					book.path = file.toString();
					book.type = BookInfo.BOOK_TYPE_INNER;
					book.now = "0";					
					bookManager.insertBook(book);
					
					sp.edit().putBoolean("isInit", true).commit();
				} catch (Exception e) {
					e.printStackTrace();
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