package com.hck.book.vo;

public class BookInfo {
	public String parent;
	public String path;
	public String type;
	public String now;
	public String title;
	
	/**
	 * 图书类型：程序内部自带
	 */
	public static final String BOOK_TYPE_INNER = "2";
	/**
	 * 图书类型：用户外部导入书籍
	 */
	public static final String BOOK_TYPE_IMPORT = "0";
}
