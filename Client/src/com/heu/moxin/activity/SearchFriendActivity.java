package com.heu.moxin.activity;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.heu.moxin.R;
import com.heu.moxin.adapter.SearchedFriendAdapter;
import com.heu.moxin.bean.User;
import com.heu.moxin.dialog.AlertDialog;
import com.heu.moxin.task.FriendLoader;
import com.heu.moxin.task.StrangerFriendLoader;

public class SearchFriendActivity extends BaseActivity {
	private InputMethodManager inputMethodManager;
	private ImageButton clearSearch;
	private EditText query;
	private String name;
	private MyHandler myHandler;
	private ListView listview;
	private SearchedFriendAdapter adapter;

	static class MyHandler extends Handler {
		WeakReference<Activity> weakReference;

		public MyHandler(Activity activity) {
			this.weakReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FriendLoader.FOUND:
				((SearchFriendActivity) weakReference.get()).listview
						.setVisibility(View.VISIBLE);
				@SuppressWarnings("unchecked")
				List<User> list = (List<User>) msg.obj;
				((SearchFriendActivity) weakReference.get()).adapter
						.setList(list);
				((SearchFriendActivity) weakReference.get()).adapter
						.notifyDataSetChanged();
				break;
			case FriendLoader.NOTFOUND:
				Toast.makeText(weakReference.get(), "没有这个用户",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(weakReference.get(), "服务器异常", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_friend);
		ImageView v = (ImageView) findViewById(R.id.back);
		listview = (ListView) findViewById(R.id.search_list);
		adapter = new SearchedFriendAdapter(this);
		listview.setAdapter(adapter);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		clearSearch = (ImageButton) findViewById(R.id.search_clear);
		query = (EditText) findViewById(R.id.query);
		String strSearch = getResources().getString(R.string.search);
		query.setHint(strSearch);
		query.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
				listview.setVisibility(View.GONE);
				hideSoftKeyboard();
			}
		});
		myHandler = new MyHandler(this);
	}

	private void hideSoftKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 查找contact
	 * 
	 * @param v
	 */
	public void searchContact(View v) {
		name = query.getText().toString();
		if (TextUtils.isEmpty(name)) {
			String st = getResources().getString(
					R.string.Please_enter_a_username);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
					st));
			return;
		}
		FriendLoader fl = new StrangerFriendLoader(name, this, myHandler);
		new Thread(fl).start();
		// 服务器存在此用户，显示此用户和添加按钮
	}

}
