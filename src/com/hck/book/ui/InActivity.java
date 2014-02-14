package com.hck.book.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hck.book.adapter.FileDapter;
import com.hck.book.helper.BookDB;
import com.hck.book.util.LogUtil;
import com.hck.book.vo.BookInfo;
import com.hck.book.vo.BookVo;
import com.hck.date.FinalDate;
import com.hck.test.R;
import com.wxd.bookreader.manager.BookManager;
/**
 * �ļ��ĵ���
 * 
 * @author
 * 
 */
public class InActivity extends DefaultActivity implements OnClickListener {
	protected static final String TAG = "InActivity";
	protected ListView lvImportBookList;
	protected TextView tt ;
	protected ImageView im ;
	protected String name[];
	protected String path[];
	protected int num[];
	protected ArrayList<String> list;
	protected Set<String> set;
	protected Map<String, Integer> parentmap;
	protected Map<String, Integer> filesToImport;
	protected ArrayList<Map<String, Object>> aList;
	protected int a;
	protected int i;
	protected Boolean b = true;	
	protected ArrayList<String> paths = null;
	private TextView all;
	private int Image[] = { R.drawable.ok1, R.drawable.no1 };
	private Button aaaa;
	private PopupWindow mPopupWindow;
	private View popunwindwow;
	private BookDB localbook;
	private HashMap<String, ArrayList<BookVo>> importedBooks;
	protected Boolean ok = false;
	protected ProgressDialog mpDialog = null;
	private ArrayList<BookInfo> insertList = new ArrayList<BookInfo>();	
	
	protected AlertDialog ab;
	private BookManager bookManager = new BookManager();
	
	private Thread InThread = new Thread() {
		@Override
		public void run() {
			Looper.prepare();
			File sdpath = Environment.getExternalStorageDirectory();
			try {
				printAllFile(sdpath);
			} catch (Exception e) {
				Log.e(TAG, "InThread error", e);
			}
			// �����̷߳�����Ϣ
			mHandler.sendEmptyMessage(1);
		}
	};
	
	private Thread updateThread = new Thread() {
		File sdpath = Environment.getExternalStorageDirectory();
		@Override
		public void run() {
			Log.i("hck", "thrunrun");
			try {
				printAllFile(sdpath);
			} catch (Exception e) {
				Log.e(TAG, "updateThread error", e);
			}
			// �����̷߳�����Ϣ
			mHandler.sendEmptyMessage(2);
			mHandler.removeCallbacks(updateThread);
		}
	};

	private Handler mHandler = new Handler() {
		// �������̷߳�������Ϣ��ͬʱ����UI
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				insert();
				importedBooks = bookManager.getImportedBooksGroupByFolder();
				show("a");
				mpDialog.dismiss();
			} else if (msg.what == 2) {
				flu();
			}
		}
	};
	
	/**
	 * ����SD��,�����ݴ���insertList
	 */
	public void printAllFile(File f) {
		if (f.isFile()) {
			if (f.toString().endsWith(".txt")) {
				BookInfo info = new BookInfo();
				info.parent=f.getParent();
				info.path= f.toString();
				info.type = BookInfo.BOOK_TYPE_IMPORT;
				info.now = "0";				
				insertList.add(info);
			}
		}
		if (f.isDirectory()) {			
			File[] f1 = f.listFiles();
			if (f1==null) {
				return;
			}
			int len = f1.length;
			for (int i = 0; i < len; i++) {
				printAllFile(f1[i]);
			}			
		}

	}
	
	/**
	 * ������д�����ݿ�
	 */
	public void insert() { 
		bookManager.insertBooks(insertList);
		mPopupWindow.dismiss();		
	}
	
	/**
	 * �жϲ���ʾ����,��ʾ����ɨ��ĵ�����
	 *
	 */
	public void show(String p) {
		Log.i("hck", "show");
		Set<String> importedBookSet = importedBooks.keySet();
		if (p.equals("a")) {
			aList = new ArrayList<Map<String, Object>>();
			name = new String[importedBookSet.size()];
			paths = new ArrayList<String>();
			i = 0;
			filesToImport.clear();
			Iterator<String> it = importedBookSet.iterator();
			while (it.hasNext()) {
				a = 0;
				paths.add((String) it.next());
				Map<String, Object> map = new HashMap<String, Object>();
				File f1 = new File(paths.get(i));
				name[i] = f1.getName();
				map.put("icon", R.drawable.cartoon_folder);
				if (name[i].length() > 8) {
					map.put("name", name[i].substring(0, 8) + "...");
				} else {
					map.put("name", name[i]);
				}
				map.put("num", importedBooks.get(paths.get(i)).size());
				aList.add(map);
				i = i + 1;
			}
			all.setText(null);
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
			setDate(1);
		} else {
			aList = new ArrayList<Map<String, Object>>();
			ArrayList<BookVo> al = importedBooks.get(paths.get(Integer.parseInt(p)));
			paths = new ArrayList<String>();
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("icon", R.drawable.back);
			map2.put("name", "������һ��");
			map2.put("num", null);
			paths.add("a");
			aList.add(map2);
			for (int i = 0; i < al.size(); i++) {
				paths.add(al.get(i).getOwen());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("icon", R.drawable.my_fiction);
				File file = new File(al.get(i).getOwen());
				if (file.getName().substring(0, file.getName().length() - 4)
						.length() > 8) {
					map.put("name", file.getName().substring(0, 8) + "...");
				} else {
					map.put("name",
							file.getName().substring(0,
									file.getName().length() - 4));
				}
				map.put("num", "��ʽ��txt");
				// ��¼�������ݵ���ʽ
				if (al.get(i).getLocal() == 0) {
					map.put("imChoose", Image[1]);
				} else if (al.get(i).getLocal() == 1) {
					map.put("imChoosezz", "�ѵ���");
				}
				aList.add(map);
			}
			all.setText("ȫѡ");
			setDate(2);
		}
	}
	/**
	 * popupwindow�ĵ���
	 */
	public void pop() {
		mPopupWindow.showAtLocation(findViewById(R.id.main11), Gravity.BOTTOM,
				0, 0);
		aaaa = (Button) popunwindwow.findViewById(R.id.aaaa);// ȷ�ϵ��밴ť
		aaaa.setBackgroundResource(R.drawable.popin);
		aaaa.setText("ȷ�ϵ���(" + String.valueOf(filesToImport.size()) + ")");
		aaaa.setOnClickListener(this);
	}

	public void setDate(int a) {
		Log.i("hck", "setDate");
		FileDapter	aDapter=new FileDapter(this, aList,a);
		lvImportBookList.setAdapter(aDapter);
		Log.i("hck", "adpter");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.import_books);
			
		lvImportBookList = (ListView) findViewById(R.id.lv_import_book_list);	
		all = (TextView) findViewById(R.id.all);
		all.setVisibility(View.GONE);
		
		localbook = new BookDB(this);
		
		popunwindwow = this.getLayoutInflater().inflate(R.layout.popwindow,	null);
		mPopupWindow = new PopupWindow(popunwindwow, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
	
		filesToImport = new HashMap<String, Integer>();// ��¼���׼��������ļ�
		parentmap = new HashMap<String, Integer>();
		set = new HashSet<String>();
		list = new ArrayList<String>();
		// �������ݿ�lackbook ������ݿ�Ϊ�տ����߳� ����SD�� ����ֱ�Ӵ����ݿ�����ȡ��ʾ
		if (bookManager.getImportedBooksGroupByFolder().isEmpty()) {			
			// �����ļ�
			LogUtil.debug("׼�������ļ���");
			showProgressDialog("���Ժ�......");
			InThread.start();
		} else {
			LogUtil.debug("׼��ˢ���ļ���");
			// ˢ���ļ�
			showProgressDialog("���Ժ�.....");
			updateThread.start();			
		}
		
		
		// �������ļ�ʱlistview�ڵĵ���¼�
		lvImportBookList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				String p = paths.get(position);
				all.setVisibility(View.GONE);
				// �ص���Ŀ¼
				if (p.equals("a")) {
					all.setVisibility(View.GONE);
					show("a");
				} else {
					all.setVisibility(View.VISIBLE);
					File file = new File(p);
					String s = file.getParent();
					if (file.isFile()) {
						if (importedBooks.get(s).get(position - 1).getLocal() == 0) {
							if (!filesToImport.containsKey(p)) {
								Map<String, Object> map1 = aList.get(position);
								map1.put("imChoose", Image[0]);
								setDate(2);
								filesToImport.put(p, position);
								if (!mPopupWindow.isShowing()) {
									pop();
									aaaa.setText("ȷ�ϵ���("
											+ String.valueOf(filesToImport.size())
											+ ")");
								}
								aaaa.setText("ȷ�ϵ���("
										+ String.valueOf(filesToImport.size()) + ")");
							} else {
								Map<String, Object> map1 = aList.get(position);
								map1.put("imChoose", Image[1]);
								setDate(2);
								filesToImport.remove(p);
								if (filesToImport.isEmpty()) {
									mPopupWindow.dismiss();
								}
								aaaa.setText("ȷ�ϵ���("
										+ String.valueOf(filesToImport.size()) + ")");
							}
						}
					} else {
						show(String.valueOf(position));
					}
				}
			}
		});
		// ȫѡ����ѡ�Ĵ���
		all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (b) {
					int length = paths.size();
					for (int i = 1; i < length; i++) {
						File file = new File(paths.get(i));
						String s = file.getParent();
						if (file.isFile()) {
							if (importedBooks.get(s).get(i - 1).getLocal() == 0) {

								if (!filesToImport.containsKey(paths.get(i))) {
									Map<String, Object> map1 = aList.get(i);
									map1.put("imChoose", Image[0]);
									setDate(2);
									filesToImport.put(paths.get(i), i);
									if (!mPopupWindow.isShowing()) {
										pop();
										aaaa.setText("ȷ�ϵ���("
												+ String.valueOf(filesToImport.size())
												+ ")");
									}
									aaaa.setText("ȷ�ϵ���("
											+ String.valueOf(filesToImport.size())
											+ ")");
								}
							}
						}
					}
					all.setText("��ѡ");
					b = false;
				} else {
					int length = paths.size();
					for (int i = 1; i < length; i++) {
						File file = new File(paths.get(i));
						String s = file.getParent();
						if (file.isFile()) {
							if (importedBooks.get(s).get(i - 1).getLocal() == 0) {
								if (filesToImport.containsKey(paths.get(i))) {
									Map<String, Object> map1 = aList.get(i);
									map1.put("imChoose", Image[1]);
									filesToImport.remove(paths.get(i));
								} else {
									Map<String, Object> map1 = aList.get(i);
									map1.put("imChoose", Image[0]);
									filesToImport.put(paths.get(i), i);
								}
							}
						}
					}
					setDate(2);
					if (filesToImport.isEmpty()) {
						mPopupWindow.dismiss();
					}
					if (aaaa!=null) {
						aaaa.setText("ȷ�ϵ���(" + String.valueOf(filesToImport.size()) + ")");
					}
					all.setText("ȫѡ");
					b = true;
				}
			}
		});
	}
	/**
	 * ˢ�µķ���
	 */
	public void flu() {
		ArrayList<HashMap<String, String>> dbList = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = localbook.getReadableDatabase();

		//�����ݿ��е�����������ӵ�dbList
		String col[] = { "parent", "path" };
		Cursor cur = db.query(FinalDate.DATABASE_TABKE, col, null, null, null, null, null);
		while (cur.moveToNext()) {
			HashMap<String, String> dbMap = new HashMap<String, String>();
			String s1 = cur.getString(cur.getColumnIndex("path"));
			String s = cur.getString(cur.getColumnIndex("parent"));
			dbMap.put("parent", s);
			dbMap.put("path", s1);
			dbList.add(dbMap);
		}
		
		
		//������SD���õ���insertList�� dbList���бȽ� �����д���
		for (int i = 0; i < dbList.size(); i++) {
			if (insertList.size() == 0) {
				SQLiteDatabase db1 = localbook.getWritableDatabase();
				db1.delete(FinalDate.DATABASE_TABKE, "path='" + dbList.get(i).get("path")+ " and type=0'", null);
				db1.close();
			} else {
				//���ѡ��Ĵ�������鼮�Ѿ����룬���ѡ�����Ƴ�
				for (int j = 0; j < insertList.size(); j++) {
					if (insertList.get(j).parent.equals(dbList.get(i).get("parent"))
						&& insertList.get(j).path.equals(dbList.get(i).get("path"))) {							
							insertList.remove(j);
							j = j - 1;
							break;
					} 
				}
			}			
		}
		
		db.close();
		insert();
		importedBooks = bookManager.getImportedBooksGroupByFolder();
		show("a");		
		try {
			mpDialog.dismiss();
		} catch (Exception e) {
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// ���밴ť
		case R.id.aaaa:
			SQLiteDatabase db = localbook.getWritableDatabase();
			Set<String> setIn = filesToImport.keySet();
			Iterator<?> it = setIn.iterator();
			while (it.hasNext()) {
				try {
					String path = (String) it.next();
					File f = new File(path);
					String parentFolder = f.getParent();
					importedBooks.get(parentFolder).get(filesToImport.get(path) - 1).setLocal(1);// ���õ���״̬
					Map<String, Object> map = aList.get(filesToImport.get(path));
					map.remove("imChoose");
					map.put("imChoosezz", "�ѵ���");
					setDate(2);
					ContentValues values = new ContentValues();
					values.put("type", 1);// keyΪ�ֶ�����valueΪֵ
					Cursor cur = db.query(FinalDate.DATABASE_TABKE, new String[]{"path"}, "type=2", null, null, null, null);
					Log.i("hck","InActivity11: "+cur.getCount());
					db.update(FinalDate.DATABASE_TABKE, values, "path=?", new String[] { path });// �޸�״̬Ϊͼ�鱻�ѱ�����
					
					//db.insert(FinalDate.DATABASE_TABKE, "path", values);// �޸�״̬Ϊͼ�鱻�ѱ�����
				} catch (SQLException e) {
					Log.e(TAG, "R.id.aaaa onclick-> SQLException error", e);
				} catch (Exception e) {
					Log.e(TAG, "R.id.aaaa onclick-> Exception error", e);
				}
			}
			db.close();
			mPopupWindow.dismiss();
			break;
		}
	}
	

	/**
	 * ��д���˰�ť ��ҳ����˵��������
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent it = new Intent();
			it.setClass(InActivity.this, BookListActivity2.class);
			it.putExtra("nol", "l");
			startActivity(it);
			this.finish();
		}
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("hck", "onpase");
		if (mpDialog!=null) {
			mpDialog.dismiss();
		}
	}
	private void showProgressDialog(String msg) {
		mpDialog = new ProgressDialog(InActivity.this);
		mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
		mpDialog.setMessage(msg);
		mpDialog.setIndeterminate(false);// ���ý������Ƿ�Ϊ����ȷ
		mpDialog.setCancelable(true);// ���ý������Ƿ���԰��˻ؼ�ȡ��
		mpDialog.show();
	}
	public void back(View view)
	{
		Intent intent = new Intent();
		intent.setClass(InActivity.this, BookListActivity2.class);
		intent.putExtra("nol", "l");
		startActivity(intent);
		this.finish();
	}

	@Override
	protected boolean isFullScreen() {
		return false;
	}	
}