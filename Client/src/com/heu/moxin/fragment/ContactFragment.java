package com.heu.moxin.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heu.moxin.Constant;
import com.heu.moxin.R;
import com.heu.moxin.activity.MainActivity;

public class ContactFragment extends RefreshableFragement implements
		OnClickListener {
	public ContactFragment() throws SingletonException {
		super();
	}

	public static final String TAG = "ContactFragment";
	private RefreshableFragement[] fragments;
	private RelativeLayout contactlistbtn;
	private RelativeLayout friend_verifybtn;
	private RelativeLayout group_chatbtn;
	private TextView contactlist_tv;
	private TextView friend_verify_tv;
	private TextView group_chat_tv;
	private Drawable Img_normal;
	private Drawable Img_selected;
	private TextView tv_unread;
	public static final int CONTACTLIST = 0;
	public static final int FRIENDVRTIFY = 1;
	public static final int GROUPCHAT = 2;
	public int currentIndex;
	private boolean hidden;
	private int colorNormal;
	private int colorSelected;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)) {
			return;
		}
		init(savedInstanceState);
	}

	protected void init(Bundle savedInstanceState) {
		tv_unread = (TextView) getView().findViewById(R.id.tv_unread);
		fragments = new RefreshableFragement[3];
		fragments[CONTACTLIST] = RefreshableFragement
				.getInstance("SubContactFragment");
		fragments[FRIENDVRTIFY] = RefreshableFragement
				.getInstance("SubFriendVerifyFragment");
		fragments[GROUPCHAT] = RefreshableFragement
				.getInstance("SubGroupChatFragment");
		contactlistbtn = (RelativeLayout) getView().findViewById(
				R.id.re_contactlist);
		contactlistbtn.setOnClickListener(this);
		friend_verifybtn = (RelativeLayout) getView().findViewById(
				R.id.re_friend_verify);
		friend_verifybtn.setOnClickListener(this);
		group_chatbtn = (RelativeLayout) getView().findViewById(
				R.id.re_group_chat);
		group_chatbtn.setOnClickListener(this);
		friend_verify_tv = (TextView) getView().findViewById(
				R.id.tv_friend_verify);
		contactlist_tv = (TextView) getView().findViewById(R.id.tv_contactlist);
		group_chat_tv = (TextView) getView().findViewById(R.id.tv_group_chat);
		Img_normal = getResources().getDrawable(R.color.bar_normal);
		Img_normal.setBounds(0, 0, Img_normal.getMinimumWidth(),
				Img_normal.getMinimumHeight());
		Img_selected = getResources().getDrawable(R.color.bar_selected);
		Img_selected.setBounds(0, 0, Img_selected.getMinimumWidth(),
				Img_selected.getMinimumHeight());
		currentIndex = CONTACTLIST;
		colorNormal = getActivity().getResources().getColor(R.color.bar_normal);
		colorSelected = getActivity().getResources().getColor(
				R.color.bar_selected);
		refresh();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (((MainActivity) getActivity()).isConflict) {
			outState.putBoolean("isConflict", true);
		} else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
			outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
		}
	}

	@Override
	public void onClick(View v) {
		reset();
		FragmentTransaction trx = getChildFragmentManager().beginTransaction();
		switch (v.getId()) {
		case R.id.re_contactlist:
			contactlist_tv.setTextColor(colorSelected);
			if (!fragments[CONTACTLIST].isAdded())
				trx.add(R.id.subfragment, fragments[CONTACTLIST]);
			else
				trx.show(fragments[CONTACTLIST]);
			currentIndex = CONTACTLIST;
			Log.e(TAG, "show--->" + currentIndex);
			break;
		case R.id.re_friend_verify:
			friend_verify_tv.setTextColor(colorSelected);
			if (!fragments[FRIENDVRTIFY].isAdded()) {
				trx.add(R.id.subfragment, fragments[FRIENDVRTIFY]);
				Log.e(TAG, "FRIENDVRTIFYadd");
			} else {
				trx.show(fragments[FRIENDVRTIFY]);
			}
			currentIndex = FRIENDVRTIFY;
			Log.e(TAG, "show--->" + currentIndex);
			break;
		case R.id.re_group_chat:
			group_chat_tv.setTextColor(colorSelected);
			if (!fragments[GROUPCHAT].isAdded())
				trx.add(R.id.subfragment, fragments[GROUPCHAT]);
			else
				trx.show(fragments[GROUPCHAT]);
			currentIndex = GROUPCHAT;
			Log.e(TAG, "show--->" + currentIndex);
			break;
		default:
			break;
		}
		trx.commit();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			onResume();
		} else {
			onPause();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		FragmentTransaction trx = getChildFragmentManager().beginTransaction();
		for (Fragment f : fragments) {
			if (f.isAdded() && !f.isHidden()) {
				trx.hide(f);
			}
		}
		if (!fragments[currentIndex].isAdded())
			trx.add(R.id.subfragment, fragments[currentIndex]);
		else
			trx.show(fragments[currentIndex]);
		trx.commit();
		if (!hidden) {
			refresh();
		}
	}

	public void refresh() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (((MainActivity) getActivity()).unreadAddressLable
						.getVisibility() == View.VISIBLE) {
					tv_unread.setVisibility(View.VISIBLE);
					tv_unread
							.setText(((MainActivity) getActivity()).unreadAddressLable
									.getText());
				} else {
					tv_unread.setVisibility(View.GONE);
				}
				if (fragments != null) {
					for (RefreshableFragement rf : fragments) {
						if (rf != null && rf.isAdded())
							rf.refresh();
					}
				}
			}
		});

	}

	@Override
	public void onPause() {
		FragmentTransaction trx = getChildFragmentManager().beginTransaction();
		/*
		 * for (Fragment f : fragments) { if (!f.isHidden()) trx.hide(f); }
		 */
		trx.hide(fragments[currentIndex]);
		trx.commit();
		super.onPause();
	}

	@SuppressLint("NewApi")
	private void reset() {
		contactlist_tv.setTextColor(colorNormal);
		friend_verify_tv.setTextColor(colorNormal);
		group_chat_tv.setTextColor(colorNormal);
		FragmentTransaction trx = getChildFragmentManager().beginTransaction();
		/*
		 * for (Fragment f : fragments) { if (!f.isHidden()) trx.hide(f); }
		 */
		trx.hide(fragments[currentIndex]);
		trx.commit();
	}

}
