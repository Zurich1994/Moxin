package com.heu.moxin.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.activity.ChatActivity;
import com.heu.moxin.activity.SearchFriendActivity;
import com.heu.moxin.adapter.ContactAdapter;
import com.heu.moxin.bean.User;
import com.heu.moxin.widget.Sidebar;

public class SubContactFragment extends RefreshableFragement {
	public SubContactFragment() throws SingletonException {
		super();
	}

	private ListView listView;
	private InputMethodManager inputMethodManager;
	private List<String> blackList;
	private LayoutInflater infalter;
	private Sidebar sidebar;
	private ContactAdapter adapter;
	private TextView tv_total;
	private boolean hidden;
	private EditText query;
	private ImageButton clearSearch;
	public static final String TAG = "SubContactFragment";

	@SuppressLint("InflateParams")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = (ListView) getView().findViewById(R.id.list);

		// 黑名单列表
		blackList = EMContactManager.getInstance().getBlackListUsernames();
		contactList = new ArrayList<User>();
		// 获取设置contactlist
		getContactList();
		infalter = LayoutInflater.from(getActivity());
		View headView = infalter.inflate(R.layout.item_contact_list_header,
				null);
		RelativeLayout rl_add = (RelativeLayout) headView
				.findViewById(R.id.addfriend);
		rl_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(),
						SearchFriendActivity.class));
			}
		});
		listView.addHeaderView(headView);
		View footerView = infalter.inflate(R.layout.item_contact_list_footer,
				null);
		listView.addFooterView(footerView);
		sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
		sidebar.setListView(listView);

		adapter = new ContactAdapter(getActivity(), R.layout.item_contact_list,
				contactList);
		final String st2 = getResources().getString(
				R.string.Cant_chat_with_yourself);
		listView.setAdapter(adapter);
		listView.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				hideSoftKeyboard();
				return false;
			}

		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position != 0 && position != contactList.size() + 1) {

					User user = contactList.get(position - 1);
					String username = user.getPhone();
					/*
					 * startActivity(new Intent(getActivity(),
					 * UserInfoActivity.class).putExtra("hxid", username)
					 * .putExtra("nick", user.getNick()) .putExtra("avatar",
					 * user.getAvatar()) .putExtra("sex", user.getSex()));
					 */
					// 待实现
					if (username.equals(MoxinApplication.getInstance()
							.getUserName()))
						Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT)
								.show();
					else {
						// 进入聊天页面
						Intent intent = new Intent(getActivity(),
								ChatActivity.class);
						// it is single chat
						intent.putExtra("userId", username);
						intent.putExtra("userNick", user.getUsernick());
						startActivity(intent);
					}
				}
			}
		});

		tv_total = (TextView) footerView.findViewById(R.id.tv_total);
		tv_total.setText(String.valueOf(contactList.size()) + "位联系人");

		// 搜索框
		query = (EditText) getView().findViewById(R.id.query);
		String strSearch = getResources().getString(R.string.search);
		query.setHint(strSearch);
		// 搜索框中清除button
		clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
		query.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
				if (s.length() > 0) {
					clearSearch.setVisibility(View.VISIBLE);
				} else {
					clearSearch.setVisibility(View.INVISIBLE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		clearSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				query.getText().clear();
				hideSoftKeyboard();
			}
		});

		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	private List<User> contactList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.subfragment_contact, container, false);
	}

	private void getContactList() {
		contactList.clear();
		Map<String, User> users = MoxinApplication.getInstance()
				.getContactList();
		Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, User> entry = iterator.next();
			// 过滤黑名单好友
			if (!blackList.contains(entry.getKey()))
				contactList.add(entry.getValue());
		}
		// 排序
		Collections.sort(contactList, new Comparator<User>() {

			@Override
			public int compare(User lhs, User rhs) {
				return lhs.getUsernick().compareTo(rhs.getUsernick());
			}
		});
	}

	// 刷新ui
	public void refresh() {
		try {
			Log.e(TAG, "refresh");
			// 可能会在子线程中调到这方法
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					getContactList();
					adapter.notifyDataSetChanged();
					tv_total.setText(String.valueOf(contactList.size())
							+ "位联系人");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

	void hideSoftKeyboard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
