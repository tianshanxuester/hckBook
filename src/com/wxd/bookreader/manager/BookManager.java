package com.wxd.bookreader.manager;

import android.database.sqlite.SQLiteDatabase;

import com.hck.book.helper.BookDB;
import com.hck.book.ui.MyApplication;
import com.hck.book.vo.BookInfo;

public class BookManager {
	private BookDB bookDAO;
	
	public BookManager(){
		this.bookDAO = MyApplication.bookDB;
	}
	
	/**
	 * ɾ����Ч�鱾
	 */
	public void deleteInvalidBook(){
		SQLiteDatabase db = this.bookDAO.getWritableDatabase();
		db.delete(BookDB.TABLE_NAME_BOOKS, "type='" + 2 + "'", null);
		db.close();
	}
	
	/**
	 * ����һ��ͼ����Ϣ
	 */
	public void insertBook(BookInfo book){
		SQLiteDatabase db = this.bookDAO.getWritableDatabase();
		 
		String sql1 = "insert into " + BookDB.TABLE_NAME_BOOKS
				+ "(parent,path, type,now,ready)"
				+ " values('" + book.parent + "','" + book.path
				+ "',"+book.type+","+book.now+",null" + ");";
		db.execSQL(sql1);
		db.close();
	}
}