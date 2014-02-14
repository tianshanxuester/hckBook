package com.wxd.test.bookreader.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hck.test.R;
import com.wxd.test.bookreader.vo.ListFileItemVO;

public class ListFileAdapter extends BaseAdapter{
	private List<ListFileItemVO> fileList ;
	private LayoutInflater inflater;
	public ListFileAdapter(List<ListFileItemVO> currentFileList,LayoutInflater inflater){
		this.fileList = currentFileList;
		this.inflater = inflater;
	}
	
	/**
	 * 列表内容发生变化
	 * @param currentFileList  变化后的列表内容
	 */
	public void changeContent(List<ListFileItemVO> currentFileList){
		this.fileList = currentFileList;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return fileList.size();
	}

	@Override
	public Object getItem(int index) {
		return fileList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View v, ViewGroup parent) {
		if(v == null){
			 v = inflater.inflate(R.layout.item_import2, parent,false);
		}
		ListFileItemVO vo = fileList.get(index);
		TextView tvFileName = (TextView) v.findViewById(R.id.tv_file_name);
		tvFileName.setText(vo.file.getName());
		
		ImageView selectIcon = (ImageView) v.findViewById(R.id.img_select_btn);
		ImageView imgFileIcon = (ImageView) v.findViewById(R.id.img_file_icon);
		if(vo.file.isDirectory()){
			imgFileIcon.setImageResource(R.drawable.cartoon_folder);
			selectIcon.setVisibility(View.GONE);
		}else{
			imgFileIcon.setImageResource(R.drawable.my_fiction);
			selectIcon.setVisibility(View.VISIBLE);
			if(!vo.isImported){
				if(vo.isSelected){
					selectIcon.setImageResource(R.drawable.ok1);
				}else{
					selectIcon.setImageResource(R.drawable.no1);
				}				
			}
		}
		
		return v;
	}
	
}