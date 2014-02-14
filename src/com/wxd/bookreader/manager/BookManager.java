package com.wxd.bookreader.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hck.book.helper.BookDB;
import com.hck.book.ui.MyApplication;
import com.hck.book.util.LogUtil;
import com.hck.book.vo.BookInfo;
import com.hck.book.vo.BookVo;
import com.hck.date.FinalDate;

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
	
	public HashMap<String, ArrayList<BookVo>> getImportedBooksGroupByFolder(){

		SQLiteDatabase db = this.bookDAO.getReadableDatabase();
		String col[] = {"parent"};
		//取出所有用户导入的书籍的父路径
		Cursor cur = db.queryWithFactory(null, true, FinalDate.DATABASE_TABKE, col,	"type<>2", null, null, null, null, null);
		ArrayList<String> arraylist1 = new ArrayList<String>();

		HashMap<String, ArrayList<BookVo>> map1 = new HashMap<String, ArrayList<BookVo>>();
		while (cur.moveToNext()) {
			String s1 = cur.getString(cur.getColumnIndex("parent"));
			arraylist1.add(s1);
		}		
		
		LogUtil.debug("导入书籍的父路径集合："+arraylist1);
		
		//取出父路径下面所有的书籍
		String col1[] = { "path", "type" };
		for (int i = 0; i < arraylist1.size(); i++) {
			ArrayList<BookVo> arraylist2 = new ArrayList<BookVo>();
			Cursor cur1 = db.query(FinalDate.DATABASE_TABKE, col1,"parent = '" + arraylist1.get(i) + "'", null, null, null,null);
			while (cur1.moveToNext()) {
				String s2 = cur1.getString(cur1.getColumnIndex("path"));
				int s3 = cur1.getInt(cur1.getColumnIndex("type"));
				BookVo bookvo = new BookVo(s2, s3);
				arraylist2.add(bookvo);
				map1.put(arraylist1.get(i), arraylist2);
			}
		}
		
		cur.close();
		db.close();		
		return map1;	
	}
}