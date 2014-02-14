package com.wxd.test.bookreader.vo;

import java.io.File;

/**
 * �ļ��б���Ŀ��ֵ����
 * 
 * @author ����
 *
 */
public class ListFileItemVO {
	/** �ļ�*/
	public File  file;
	/** �Ƿ��Ѿ�����*/
	public 	boolean isImported;
	
	/** �Ƿ�ѡ��*/
	public 	boolean isSelected =false;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ListFileItemVO other = (ListFileItemVO) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
}
