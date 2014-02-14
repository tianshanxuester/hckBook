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
 * �ı��ļ������
 * @author ����
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
		
		//��ʼ��popupMenu
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
	 * ��ʾ��Ŀ¼�б�
	 *
	 */
	private final class ListSubFoldersTask extends AsyncTask<String, Integer, Map<String,Object>>{

		@Override
		protected Map<String,Object> doInBackground(String... subFolder) {
			File targetFile = new File(currentFilePath+"/"+subFolder[0]);
		
			//������һ��
			if(FLAG_PREVIOUS_LEVEL.equals(subFolder[0])){
				targetFile = new File(currentFilePath);
				targetFile = new File(targetFile.getParent());
			}
			
			File[] subFiles = null;
			if(targetFile != null && targetFile.isDirectory()){
				subFiles = targetFile.listFiles(new TxtFileFilter());
			}
			
			Log.d("wxd test","�õ�"+targetFile+"�µ�����Ŀ¼��"+subFiles);
			
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
				Toast.makeText(TxtFileExplorerActivity.this, "�ļ���Ϊ�գ�", Toast.LENGTH_SHORT).show();
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
				//������һ��
				new ListSubFoldersTask().execute(FLAG_PREVIOUS_LEVEL);
			}else{
				Toast.makeText(getApplicationContext(), "�ϼ�Ŀ¼Ϊ�գ�", Toast.LENGTH_SHORT).show();
			}
		}
		return true;
	}

	private String getDefaultPath(){
		//����չ��
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}else{
			return "/";
		}
	}
	
	/**
	 * �����ļ���������¼�
	 */
	private class FileItemClickLinstener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,	long arg3) {
			ListFileItemVO vo = (ListFileItemVO) adapter.getItem(position);
			Log.d("wxd test","���·����"+vo.file);
	
			if(vo.file.isDirectory()){
				new ListSubFoldersTask().execute(vo.file.getName());
			}else{
				//��������ļ� do nothing
				Log.d("wxd test","����ļ���"+vo.file.getAbsolutePath());
				if(vo.isSelected){//ȡ��ѡ��
					vo.isSelected = !vo.isSelected;
					filesToImport.remove(vo.file);
				}else{
					//ѡ��
					vo.isSelected = !vo.isSelected;
					filesToImport.add(vo.file);
				}
				
				//��ʾ���밴ť
				if(filesToImport.size() == 0){
					popupWindow.dismiss();
				}else{
					pop();
				}
				
				//�����б�
				adapter.notifyDataSetChanged();
			}			
		}		
	}

	@Override
	protected boolean isFullScreen() {
		return false;
	}	
	
	/**
	 * popupwindow�ĵ���
	 */
	public void pop() {
		btnImportBooks.setText("ȷ�ϵ���(" + String.valueOf(filesToImport.size()) + ")");
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
