package com.heu.moxin.fragment;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.adapter.NewFriendsAdapter;
import com.heu.moxin.bean.InviteMessage;
import com.heu.moxin.dao.InviteMessageDao;

public class SubFriendVerifyFragment extends RefreshableFragement {
	public SubFriendVerifyFragment() throws SingletonException {
		super();
	}

	public static final String TAG = "SubFriendVerifyFragment";

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			onResume();
		} else {
			onPause();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MoxinApplication.getInstance().setUnreadContactCnt(0);
		refresh();
	}

	private ListView listview;
	private NewFriendsAdapter adapter;
	private List<InviteMessage> msgs;
	private InviteMessageDao dao;
	private TextView noVerifyTv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView");
		return inflater.inflate(R.layout.subfragment_friend_verify, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		listview = (ListView) getView().findViewById(R.id.verify_listview);
		noVerifyTv = (TextView) getView().findViewById(R.id.no_verify);
		dao = new InviteMessageDao(getActivity());
		msgs = dao.getMessagesList();
		adapter = new NewFriendsAdapter(getActivity(), msgs);
		listview.setAdapter(adapter);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void refresh() {
		msgs = dao.getMessagesList();
		Log.e(TAG, "refresh:" + msgs.size());
		adapter.notifyDataSetChanged();
		if (msgs.size() <= 0) {
			Log.e(TAG, "tv");
			noVerifyTv.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		} else {
			noVerifyTv.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
		}
	}

}
