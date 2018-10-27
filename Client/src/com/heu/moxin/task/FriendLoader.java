package com.heu.moxin.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;

public abstract class FriendLoader implements Runnable {
	public static String TAG = "FriendLoader";
	public static final int FOUND = 0;
	public static final int NOTFOUND = 1;
	public static final int ERROR = 2;
	public WeakReference<Context> wr_context;
	public String phoneList;
	public Handler handler;

	public FriendLoader(String phoneList, Context context, Handler handler) {
		this.phoneList = phoneList;
		this.wr_context = new WeakReference<Context>(context);
		this.handler = handler;
	}

}

