package com.hck.book.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hck.book.vo.BookInfo;
import com.hck.test.R;
import com.wxd.bookreader.manager.BookManager;
import com.wxd.test.bookreader.adapter.ListFileAdapter;
import com.wxd.test.bookreader.vo.ListFileItemVO;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 文本文件浏览器
 * @author 王旭东
 *
 */
@SuppressLint("DefaultLocale")
public class TxtFileExplorerActivity extends DefaultActivity  {

	ListView lv_fileList;
	ListFileAdapter adapter;

	private String currentFilePath = "";
	protected List<File> filesToImport = new ArrayList<File>();
	
	private final String FLAG_PREVIOUS_LEVEL = "..";
	private final String ROOT_PATH = "/";
	
	private BookManager bookManager =new BookManager();
	
	private TextView tvTitle ;

	private Button btnGoBack;
	private Button btnImportBooks;
	
	private PopupWindow popupWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_books2);
		lv_fileList = (ListView) findViewById(R.id.list);
		
		currentFilePath = getDefaultPath();
		new ListSubFoldersTask().execute("");
		
		this.btnGoBack = (Button) findViewById(R.id.btn_goBack);
		this.btnGoBack.setOnClickListener(new GoBackLinstener());
		
		this.tvTitle = (TextView) findViewById(R.id.tv_title);
		
		adapter = new ListFileAdapter(new ArrayList<ListFileItemVO>(),getLayoutInflater());
		lv_fileList.setAdapter(adapter);
		lv_fileList.setOnItemClickListener(new FileItemClickLinstener());
		updateTitle();
		
		//初始化popupMenu
		View popupView = getLayoutInflater().inflate(R.layout.popwindow2,	null);
		popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		btnImportBooks = (Button) popupView.findViewById(R.id.btn_import_books);
		btnImportBooks.setOnClickListener(new ImportBooksLinstener());
	}
	
	private final class GoBackLinstener implements OnClickListener{
		@Override
		public void onClick(View v) {
			startActivity(new Intent(TxtFileExplorerActivity.this, BookListActivity2.class));
			TxtFileExplorerActivity.this.finish();
		}
	}
	
	private  class TxtFileFilter implements FileFilter{

		@SuppressLint("DefaultLocale")
		@Override
		public boolean accept(File file) {
			
			if(file.isDirectory()) return true;
			
			String name = file.getName();
			if(name.toLowerCase(Locale.getDefault()).endsWith("txt")){
				return true;
			}else{
				return false;
			}		
		}
	}
	
	/**
	 * 显示子目录列表
	 *
	 */
	private final class ListSubFoldersTask extends AsyncTask<String, Integer, Map<String,Object>>{

		@Override
		protected Map<String,Object> doInBackground(String... subFolder) {
			File targetFile = new File(currentFilePath+"/"+subFolder[0]);
		
			//返回上一级
			if(FLAG_PREVIOUS_LEVEL.equals(subFolder[0])){
				targetFile = new File(currentFilePath);
				targetFile = new File(targetFile.getParent());
			}
			
			File[] subFiles = null;
			if(targetFile != null && targetFile.isDirectory()){
				subFiles = targetFile.listFiles(new TxtFileFilter());
			}
			
			Log.d("wxd test","得到"+targetFile+"下的所有目录："+subFiles);
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("subFiles", subFiles);
			map.put("targetFile", targetFile);
			return map;
		}

		@Override
		protected void onPostExecute(Map<String,Object> result) {
			List<ListFileItemVO> currentFileList  = new ArrayList<ListFileItemVO>();
			
			File[] subFiles = (File[]) result.get("subFiles");
			if(subFiles == null){
				Toast.makeText(TxtFileExplorerActivity.this, "文件夹为空！", Toast.LENGTH_SHORT).show();
			}else{
				currentFilePath = ((File) result.get("targetFile")).getAbsolutePath();
				for(File file:subFiles){
					ListFileItemVO vo = new ListFileItemVO();
					vo.file = file;
					vo.isImported = false;
					vo.isSelected = false;
					currentFileList.add(vo);
				}	
				adapter.changeContent(currentFileList);
				updateTitle();
			}
		}
	}
	
	private void updateTitle(){
		this.tvTitle.setText(currentFilePath);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			
			if(popupWindow.isShowing()){
				popupWindow.dismiss();
				return true;
			}
			
			if(!ROOT_PATH.equals(this.currentFilePath)){
				//返回上一级
				new ListSubFoldersTask().execute(FLAG_PREVIOUS_LEVEL);
			}else{
				Toast.makeText(getApplicationContext(), "上级目录为空！", Toast.LENGTH_SHORT).show();
			}
		}
		return true;
	}

	private String getDefaultPath(){
		//有扩展卡
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}else{
			return "/";
		}
	}
	
	/**
	 * 处理文件被点击的事件
	 */
	private class FileItemClickLinstener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,	long arg3) {
			ListFileItemVO vo = (ListFileItemVO) adapter.getItem(position);
			Log.d("wxd test","点击路径："+vo.file);
	
			if(vo.file.isDirectory()){
				new ListSubFoldersTask().execute(vo.file.getName());
			}else{
				//点击的是文件 do nothing
				Log.d("wxd test","点击文件："+vo.file.getAbsolutePath());
				if(vo.isSelected){//取消选中
					vo.isSelected = !vo.isSelected;
					filesToImport.remove(vo.file);
				}else{
					//选中
					vo.isSelected = !vo.isSelected;
					filesToImport.add(vo.file);
				}
				
				//显示导入按钮
				if(filesToImport.size() == 0){
					popupWindow.dismiss();
				}else{
					pop();
				}
				
				//更新列表
				adapter.notifyDataSetChanged();
			}			
		}		
	}

	@Override
	protected boolean isFullScreen() {
		return false;
	}	
	
	/**
	 * popupwindow的弹出
	 */
	public void pop() {
		btnImportBooks.setText("确认导入(" + String.valueOf(filesToImport.size()) + ")");
		popupWindow.showAtLocation(findViewById(R.id.main11), Gravity.BOTTOM,0, 0);
	}
	
	private final class ImportBooksLinstener implements OnClickListener{

		@Override
		public void onClick(View v) {
			List<BookInfo> books = new ArrayList<BookInfo>();
			for(File file:filesToImport){
				BookInfo info = new BookInfo();
				info.parent = file.getParent();
				info.path = file.getPath();
				info.type = "1";
				info.now = "0";
				books.add(info);				
			}
			bookManager.insertBooks(books);
			popupWindow.dismiss();
		}
	}
}
