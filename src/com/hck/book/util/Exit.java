package com.hck.book.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Exit implements OnClickListener {
	public Exit(Context context) {
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {			
		System.exit(0);

	}

	public void exit() {
		System.exit(0);
	}
}