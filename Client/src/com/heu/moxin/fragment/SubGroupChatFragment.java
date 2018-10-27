package com.heu.moxin.fragment;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.heu.moxin.R;
import com.heu.moxin.activity.ChatActivity;
import com.heu.moxin.activity.NewGroupActivity;
import com.heu.moxin.adapter.GroupAdapter;

public class SubGroupChatFragment extends RefreshableFragement {

	public SubGroupChatFragment() throws SingletonException {
		super();
	}

	private ListView groupListView;
	protected List<EMGroup> grouplist;
	private GroupAdapter groupAdapter;
	private InputMethodManager inputMethodManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.subfragment_groupchat, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		grouplist = EMGroupManager.getInstance().getAllGroups();
		groupListView = (ListView) getView().findViewById(R.id.list);
		groupAdapter = new GroupAdapter(getActivity(), 1, grouplist);
		groupListView.setAdapter(groupAdapter);
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == groupAdapter.getCount() - 1) {
					// 新建群聊
					startActivity(new Intent(getActivity(),
							NewGroupActivity.class));
				} else {

					// 进入群聊
					Intent intent = new Intent(getActivity(),
							ChatActivity.class);
					// it is group chat
					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					intent.putExtra("groupId",
							groupAdapter.getItem(position).getGroupId());
					startActivityForResult(intent, 0);
				}
			}

		});
		groupListView.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getActivity().getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(
								getActivity().getCurrentFocus()
										.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void refresh() {
		grouplist = EMGroupManager.getInstance().getAllGroups();
		groupAdapter = new GroupAdapter(getActivity(), 1, grouplist);
		groupListView.setAdapter(groupAdapter);
		groupAdapter.notifyDataSetChanged();
	}


}
