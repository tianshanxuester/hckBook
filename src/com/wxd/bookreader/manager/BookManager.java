package com.wxd.bookreader.manager;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.hck.book.helper.BookDB;
import com.hck.book.ui.MyApplication;
import com.hck.book.util.LogUtil;
import com.hck.book.vo.BookInfo;

public class BookManager {
	private BookDB bookDAO;
	
	public BookManager(){
		this.bookDAO = MyApplication.bookDB;
	}
	
	/**
	 * 删除应用程序自带书籍
	 */
	public void deleteInnerBook(){
		SQLiteDatabase db = this.bookDAO.getWritableDatabase();
		db.delete(BookDB.TABLE_NAME_BOOKS, "type='" + BookInfo.BOOK_TYPE_INNER + "'", null);
		db.close();
	}
	
	/**
	 * 插入多条图书信息
	 */
	public void insertBooks(List<BookInfo> books){
		LogUtil.debug("MyApplication.bookDB="+MyApplication.bookDB);
		SQLiteDatabase db = this.bookDAO.getWritableDatabase();		 
		for(int i =0; i<books.size();i++){		
			BookInfo book = books.get(i);
			String sql1 = "insert into " + BookDB.TABLE_NAME_BOOKS
					+ "(parent,path, type,now,ready)"
					+ " values('" + book.parent + "','" + book.path
					+ "',"+book.type+","+book.now+",null" + ");";
			db.execSQL(sql1);
		}
		db.close();
	}
	
	/**
	 * 插入1条图书信息
	 */
	public void insertBook(BookInfo book){
		List<BookInfo> innerBookList = new ArrayList<BookInfo>();
		innerBookList.add(book);
		this.insertBooks(innerBookList);
	}	
}