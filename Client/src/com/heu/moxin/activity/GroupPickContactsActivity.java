package com.heu.moxin.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.adapter.ContactAdapter;
import com.heu.moxin.bean.User;
import com.heu.moxin.utils.LoadUserAvatar;
import com.heu.moxin.widget.Sidebar;

public class GroupPickContactsActivity extends BaseActivity {
	private ListView listView;
	/** 是否为一个新建的群组 */
	protected boolean isCreatingNewGroup;
	/** 是否为单选 */
	private boolean isSignleChecked;
	private PickContactAdapter contactAdapter;
	/** group中一开始就有的成员 */
	private List<String> exitingMembers;
	private InputMethodManager inputMethodManager;
	private EditText query;
	private ImageButton clearSearch;
	// 选中用户总数,右上角显示
	int total = 0;
	// 可滑动的显示选中用户的View
	private LinearLayout menuLinerLayout;
	private String groupId;
	private TextView tv_checked;
	// 添加的列表
	private List<String> addList = new ArrayList<String>();

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_group_pick_contacts);

		// String groupName = getIntent().getStringExtra("groupName");
		groupId = getIntent().getStringExtra("groupId");
		if (groupId == null) {// 创建群组
			isCreatingNewGroup = true;
		} else {
			// 获取此群组的成员列表
			EMGroup group = EMGroupManager.getInstance().getGroup(groupId);
			exitingMembers = group.getMembers();
		}
		if (exitingMembers == null)
			exitingMembers = new ArrayList<String>();
		// 获取好友列表
		final List<User> alluserList = new ArrayList<User>();
		for (User user : MoxinApplication.getInstance().getContactList()
				.values()) {
			if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME)
					& !user.getUsername().equals(Constant.GROUP_USERNAME))
				alluserList.add(user);
		}
		// 对list进行排序
		Collections.sort(alluserList, new Comparator<User>() {
			@Override
			public int compare(User lhs, User rhs) {
				return (lhs.getUsername().compareTo(rhs.getUsername()));

			}
		});

		listView = (ListView) findViewById(R.id.list);
		contactAdapter = new PickContactAdapter(this,
				R.layout.row_contact_with_checkbox, alluserList);
		listView.setAdapter(contactAdapter);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View headerView = layoutInflater.inflate(
				R.layout.item_group_list_header, null);
		((Sidebar) findViewById(R.id.sidebar)).setListView(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
				checkBox.toggle();

			}
		});
		listView.addHeaderView(headerView);
		// 搜索框
		query = (EditText) findViewById(R.id.query);
		String strSearch = getResources().getString(R.string.search);
		query.setHint(strSearch);
		// 搜索框中清除button
		clearSearch = (ImageButton) findViewById(R.id.search_clear);
		query.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				contactAdapter.getFilter().filter(s);
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
		tv_checked = (TextView) findViewById(R.id.tv_checked);
		menuLinerLayout = (LinearLayout) findViewById(R.id.linearLayoutMenu);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	/**
	 * 确认选择的members
	 * 
	 * @param v
	 */
	public void save(View v) {
		List<String> list = getToBeAddMembers();
		if (list != null) {
			Intent intent = new Intent().putExtra("newmembers",
					list.toArray(new String[0]));
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/**
	 * 获取要被添加的成员
	 * 
	 * @return
	 */
	private List<String> getToBeAddMembers() {
		if (addList.size() == 0) {
			Toast.makeText(this, "请选择用户", Toast.LENGTH_LONG).show();
			return null;
		}

		return addList;
	}

	/**
	 * adapter
	 */
	private class PickContactAdapter extends ContactAdapter {
		private LayoutInflater layoutInflater;
		private boolean[] isCheckedArray;
		private List<User> list = new ArrayList<User>();
		private Bitmap[] bitmaps;
		private int res;
		private LoadUserAvatar avatarLoader;

		public PickContactAdapter(Context context, int resource,
				List<User> users) {
			super(context, resource, users);
			isCheckedArray = new boolean[users.size()];
			this.list = users;
			bitmaps = new Bitmap[list.size()];
			this.res = resource;
			layoutInflater = LayoutInflater.from(context);
			avatarLoader = LoadUserAvatar.getInstance();
		}

		public Bitmap getBitmap(int position) {
			return bitmaps[position];
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			// if (position > 0) {
			final User user = getItem(position);
			final String username = user.getUsername();
			convertView = layoutInflater.inflate(res, null);
			ImageView iv = (ImageView) convertView.findViewById(R.id.iv_avatar);
			bitmaps[position] = showUserAvatar(iv,
					"username=" + user.getPhone() + ".png");
			;
			// 选择框checkbox
			final CheckBox checkBox = (CheckBox) view
					.findViewById(R.id.checkbox);
			if (exitingMembers != null && exitingMembers.contains(username)) {
				checkBox.setButtonDrawable(R.drawable.checkbox_bg_gray_selector);
			} else {
				checkBox.setButtonDrawable(R.drawable.checkbox_bg_selector);
			}
			if (checkBox != null) {
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// 群组中原来的成员一直设为选中状态
						if (exitingMembers.contains(username)) {
							isChecked = true;
							checkBox.setChecked(true);
						}
						isCheckedArray[position] = isChecked;
						// 如果是单选模式
						if (isSignleChecked && isChecked) {
							for (int i = 0; i < isCheckedArray.length; i++) {
								if (i != position) {
									isCheckedArray[i] = false;
								}
							}
							contactAdapter.notifyDataSetChanged();
						}
						if (isChecked) {
							// 选中用户显示在滑动栏显示
							showCheckImage(contactAdapter.getBitmap(position),
									list.get(position));

						} else {
							// 用户显示在滑动栏删除
							deleteImage(list.get(position));

						}
					}
				});
				// 群组中原来的成员一直设为选中状态
				if (exitingMembers.contains(username)) {
					checkBox.setChecked(true);
					isCheckedArray[position] = true;
				} else {
					checkBox.setChecked(isCheckedArray[position]);
				}
			}
			// }
			return view;
		}
	}

	void hideSoftKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	// 即时显示被选中用户的头像和昵称。

	private void showCheckImage(Bitmap bitmap, User glufineid) {

		if (exitingMembers.contains(glufineid.getUsername()) && groupId != null) {
			return;
		}
		if (addList.contains(glufineid.getUsername())) {
			return;
		}
		total++;

		// 包含TextView的LinearLayout
		// 参数设置
		android.widget.LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
				108, 108, 1);
		View view = LayoutInflater.from(this).inflate(
				R.layout.item_group_header_item, null);
		ImageView images = (ImageView) view.findViewById(R.id.iv_avatar);

		// 设置id，方便后面删除
		view.setTag(glufineid);
		if (bitmap == null) {
			images.setImageResource(R.drawable.default_useravatar);
		} else {
			images.setImageBitmap(bitmap);
		}

		menuLinerLayout.addView(view, menuLinerLayoutParames);
		if (total > 0) {
			tv_checked.setText("确定(" + total + ")");
		} else {
			tv_checked.setText("确定");
		}
		addList.add(glufineid.getUsername());
	}

	private void deleteImage(User glufineid) {
		View view = (View) menuLinerLayout.findViewWithTag(glufineid);

		menuLinerLayout.removeView(view);
		total--;

		addList.remove(glufineid.getUsername());
		if (total > 0) {
			tv_checked.setText("确定(" + total + ")");
		} else {
			tv_checked.setText("确定");
		}

	}

	public void back(View view) {
		finish();
	}
}
