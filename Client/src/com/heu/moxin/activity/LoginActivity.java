package com.heu.moxin.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.heu.moxin.Constant;
import com.heu.moxin.DemoHXSDKHelper;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.bean.User;
import com.heu.moxin.task.ContactFriendLoader;
import com.heu.moxin.task.FriendLoader;
import com.heu.moxin.task.MineFriendLoader;
import com.heu.moxin.utils.CommonUtils;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends Activity {
	public static final String TAG = "LoginActivity";
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText etu;
	private EditText etp;
	private String currentUsername;
	private String currentPassword;
	private ProgressDialog dialog;
	InputMethodManager manager;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				etu.clearFocus();
				etp.clearFocus();
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 已登录时直接进入
		if (DemoHXSDKHelper.getInstance().isLogined()) {
			// autoLogin = true;
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		etu = (EditText) findViewById(R.id.username);
		etp = (EditText) findViewById(R.id.password);
		dialog = new ProgressDialog(LoginActivity.this);
		dialog.setCanceledOnTouchOutside(false);
		// 账号变更时清空密码
		etu.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				etp.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		// 取出默认账号用户名
		if (MoxinApplication.getInstance().getUserName() != null) {
			etu.setText(MoxinApplication.getInstance().getUserName());
		}

		Button btn = (Button) findViewById(R.id.login_btn);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.setMessage("正在登录...");
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.show();
				if (!CommonUtils.isNetWorkConnected(LoginActivity.this)) {
					Toast.makeText(LoginActivity.this,
							R.string.network_isnot_available,
							Toast.LENGTH_SHORT).show();
					Log.e("login", "network_isnot_available");
					dialog.dismiss();
					return;
				}
				currentUsername = etu.getText().toString().trim();
				currentPassword = etp.getText().toString().trim();

				if (TextUtils.isEmpty(currentUsername)) {
					Toast.makeText(LoginActivity.this,
							R.string.User_name_cannot_be_empty,
							Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if (TextUtils.isEmpty(currentPassword)) {
					Toast.makeText(LoginActivity.this,
							R.string.Password_cannot_be_empty,
							Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				// 调用sdk登陆方法登陆聊天服务器
				EMChatManager.getInstance().login(currentUsername,
						currentPassword, new EMCallBack() {

							@Override
							public void onSuccess() {

								// 登陆成功，保存用户名密码
								MoxinApplication.getInstance().setUserName(
										currentUsername);
								MoxinApplication.getInstance().setPassword(
										currentPassword);
								Log.e(TAG, "currentUsername:" + currentUsername);
								Thread t = new Thread(new MineFriendLoader(
										currentUsername, LoginActivity.this));
								t.start();
								runOnUiThread(new Runnable() {
									public void run() {
										dialog.setMessage(getString(R.string.list_is_for));
									}
								});
								try {
									// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
									// ** manually load all local groups and
									// conversations in case we are auto login
									EMGroupManager.getInstance()
											.loadAllGroups();
									EMChatManager.getInstance()
											.loadAllConversations();
									// 处理好友和群组
									processContactsAndGroups();
									runOnUiThread(new Runnable() {
										public void run() {
											startActivity(new Intent(
													LoginActivity.this,
													MainActivity.class));
											dialog.dismiss();
											finish();
										}
									});

								} catch (Exception e) {
									e.printStackTrace();
									// 取好友或者群聊失败，不让进入主页面
									runOnUiThread(new Runnable() {
										public void run() {
											dialog.dismiss();
											MoxinApplication.getInstance()
													.logout(null);
											Toast.makeText(
													getApplicationContext(),
													R.string.login_failure_failed,
													Toast.LENGTH_SHORT).show();
										}
									});
									return;
								}

							}

							@Override
							public void onProgress(int progress, String status) {
							}

							@Override
							public void onError(final int code,
									final String message) {

								runOnUiThread(new Runnable() {
									public void run() {
										dialog.dismiss();
										Toast.makeText(
												getApplicationContext(),
												getString(R.string.Login_failed)
														+ message,
												Toast.LENGTH_SHORT).show();
									}
								});
							}
						});
			}
		});
		TextView reg = (TextView) findViewById(R.id.reg_btn);
		reg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	private void processContactsAndGroups() throws EaseMobException {
		// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
		List<String> usernames = EMContactManager.getInstance()
				.getContactUserNames();
		System.out.println("----------------" + usernames.toString());
		EMLog.d("roster", "contacts size: " + usernames.size());
		/*
		 * Map<String, User> userlist = new HashMap<String, User>(); String
		 * totaluser = usernames.get(0); for (int i = 1; i < usernames.size();
		 * i++) { final String split = "66split88"; totaluser += split +
		 * usernames.get(i); } totaluser =
		 * totaluser.replace(Constant.NEW_FRIENDS_USERNAME, ""); totaluser =
		 * totaluser.replace(Constant.GROUP_USERNAME, ""); for (String username
		 * : usernames) { User user = new User();
		 * 
		 * userlist.put(username, user); }
		 * 
		 * // 存入内存 MoxinApplication.getInstance().setContactList(userlist); //
		 * 存入db UserDao dao = new UserDao(LoginActivity.this); List<User> users
		 * = new ArrayList<User>(userlist.values()); dao.saveContactList(users);
		 */
		StringBuffer phones = new StringBuffer();
		for (int i = 0; i < usernames.size() - 1; i++) {
			phones.append(usernames.get(i) + ",");
		}
		if (usernames.size() > 0)
			phones.append(usernames.get(usernames.size() - 1));
		new Thread(new ContactFriendLoader(phones.toString(), this, null))
				.start();
		// 获取黑名单列表
		List<String> blackList = EMContactManager.getInstance()
				.getBlackListUsernamesFromServer();
		// 保存黑名单
		EMContactManager.getInstance().saveBlackList(blackList);

		// 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
		EMGroupManager.getInstance().getGroupsFromServer();
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	@SuppressLint("DefaultLocale")
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsernick();
		}
		headerName = headerName.trim();
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
}
