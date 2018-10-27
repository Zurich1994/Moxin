package com.heu.moxin.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heu.moxin.R;
import com.heu.moxin.listener.ShakeListener;
import com.heu.moxin.listener.ShakeListener.OnShakeListener;
import com.heu.moxin.task.ShakeTask;

public class Find_FindFragment extends RefreshableFragement {
	public Find_FindFragment() throws SingletonException {
		super();
	}

	public static final String TAG = "Find_FindFragment";
	private ShakeListener mShakeListener;

	@Override
	public void onResume() {
		Log.e(TAG, "onResume");
		mShakeListener.start();
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.e(TAG, "onPause");
		mShakeListener.stop();
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_find_find, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mShakeListener = new ShakeListener(getActivity());
		mShakeListener.setOnShakeListener(new OnShakeListener() {

			@Override
			public void onShake() {
				ShakeTask st = new ShakeTask(getActivity());
				st.execute();
				mShakeListener.stop();
			}
		});
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void onHideChanged(boolean h) {
		if (mShakeListener != null) {
			if (h) {
				mShakeListener.stop();
			} else {
				mShakeListener.start();
			}
		}
	}

}
