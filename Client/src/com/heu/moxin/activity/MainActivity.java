package com.heu.moxin.activity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.heu.moxin.Constant;
import com.heu.moxin.DemoHXSDKHelper;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.bean.InviteMessage;
import com.heu.moxin.bean.InviteMessage.InviteMesageStatus;
import com.heu.moxin.bean.User;
import com.heu.moxin.dao.InviteMessageDao;
import com.heu.moxin.dao.UserDao;
import com.heu.moxin.fragment.Find_FindFragment;
import com.heu.moxin.fragment.RefreshableFragement;
import com.heu.moxin.task.ContactFriendLoader;
import com.heu.moxin.task.StrangerFriendLoader;
import com.heu.moxinlib.controller.HXSDKHelper;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity implements OnClickListener,
		EMEventListener {
	protected static final String TAG = "MainActivity";
	public static final int FINDVIEW = 0;
	public static final int CHATVIEW = 1;
	public static final int CONTACTVIEW = 2;
	public static final int MEVIEW = 3;
	public static final int FRAGMENTSIZE = 4;
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private RefreshableFragement[] fragments;
	private RelativeLayout tab_find;
	private RelativeLayout tab_chat;
	private RelativeLayout tab_contact;
	private RelativeLayout tab_me;
	private TextView mChatTV;
	private TextView mContactTV;
	private TextView mFind_FindTV;
	private TextView mMeTV;
	private Drawable mChatImg_normal;
	private Drawable mChatImg_selected;
	private Drawable mContactImg_normal;
	private Drawable mContactImg_selected;
	private Drawable mFind_FindImg_normal;
	private Drawable mFind_FindImg_selected;
	private Drawable mMeImg_normal;
	private Drawable mMeImg_selected;

	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;
	private boolean isConflictDialogShow;
	private Builder conflictBuilder;
	private Builder accountRemovedBuilder;
	private boolean isAccountRemovedDialogShow;
	// 未读消息textview
	private TextView unreadLabel;
	// 未读通讯录textview
	public TextView unreadAddressLable;
	private InviteMessageDao inviteMessageDao;
	private UserDao userDao;
	private MyGroupChangeListener myGroupChangeListener;
	private MyContactListener myContactListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED,
						false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			MoxinApplication.getInstance().logout(null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}

		setContentView(R.layout.activity_main);

		initView();
		MobclickAgent.updateOnlineConfig(this);
		inviteMessageDao = new InviteMessageDao(this);
		userDao = new UserDao(this);

		myGroupChangeListener = new MyGroupChangeListener();
		// 注册群聊相关的listener
		EMGroupManager.getInstance().addGroupChangeListener(
				myGroupChangeListener);
		myContactListener = new MyContactListener();
		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(myContactListener);
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();

	}

	@Override
	protected void onDestroy() {
		EMContactManager.getInstance().removeContactListener();
		EMGroupManager.getInstance().removeGroupChangeListener(
				myGroupChangeListener);
		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
		}

		// unregister this event listener when this activity enters the
		// background
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper
				.getInstance();
		sdkHelper.pushActivity(this);

		// register the event listener when enter the foreground
		EMChatManager
				.getInstance()
				.registerEventListener(
						this,
						new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
	}

	/***
	 * 好友变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			List<String> list = null;
			try {
				list = EMContactManager.getInstance().getContactUserNames();
			} catch (EaseMobException e) {
				e.printStackTrace();
			}
			if (list != null && list.size() != 0) {
				StringBuffer sb = new StringBuffer(list.get(0));
				for (int i = 1; i < list.size(); i++) {
					sb.append(",");
					sb.append(list.get(i));
				}
				new Thread(new ContactFriendLoader(sb.toString(),
						MainActivity.this, refreshHandler)).start();
			}
			// 刷新ui

			if (mViewPager.getCurrentItem() == CONTACTVIEW)
				fragments[CONTACTVIEW].refresh();

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Map<String, User> localUsers = MoxinApplication.getInstance()
					.getContactList();
			for (String username : usernameList) {
				Log.e(TAG, username + "被删除");
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessageDao.deleteMessage(username);
			}
			runOnUiThread(new Runnable() {
				public void run() {
					// 如果正在与此用户的聊天页面
					String st10 = getResources().getString(
							R.string.have_you_removed);
					if (ChatActivity.activityInstance != null
							&& usernameList
									.contains(ChatActivity.activityInstance
											.getToChatUsername())) {
						Toast.makeText(
								MainActivity.this,
								ChatActivity.activityInstance
										.getToChatUsername() + st10,
								Toast.LENGTH_SHORT).show();
						ChatActivity.activityInstance.finish();
					}
					updateUnreadLabel();
					// 刷新ui

					if (fragments[CONTACTVIEW].isAdded())
						fragments[CONTACTVIEW].refresh();
					if (fragments[CHATVIEW].isAdded())
						fragments[CHATVIEW].refresh();

				}
			});

		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			List<InviteMessage> msgs = inviteMessageDao.getMessagesList();

			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null
						&& inviteMessage.getFrom().equals(username)) {
					inviteMessageDao.deleteMessage(username);
					MoxinApplication.getInstance().MinusUnreadContactCnt();
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			Log.d(TAG, username + "请求加你为好友,reason: " + reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			MoxinApplication.getInstance().AddUnreadContactCnt();
			notifyNewIviteMessage(msg, false);

		}

		@Override
		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessageDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(username + "同意了你的好友请求");
			Log.e(TAG, username + "同意了你的好友请求");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg, true);

		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能,demo未实现
			Log.d(username, username + "拒绝了你的好友请求");
		}

	}

	/**
	 * MyGroupChangeListener
	 */
	private class MyGroupChangeListener implements GroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName,
				String inviter, String reason) {

			// 被邀请
			String st3 = getResources().getString(
					R.string.Invite_you_to_join_a_group_chat);
			User user = MoxinApplication.getInstance().getContactList()
					.get(inviter);
			if (user != null) {
				EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
				msg.setChatType(ChatType.GroupChat);
				msg.setFrom(inviter);
				msg.setTo(groupId);
				msg.setMsgId(UUID.randomUUID().toString());
				msg.addBody(new TextMessageBody(user.getUsernick() + st3));
				msg.setAttribute("useravatar", user.getAvatar());
				msg.setAttribute("nickname", user.getUsernick());
				// 保存邀请消息
				EMChatManager.getInstance().saveMessage(msg);
				// 提醒新消息
				EMNotifier.getInstance(getApplicationContext())
						.notifyOnNewMsg();
			}
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (mViewPager.getCurrentItem() == CHATVIEW)
						fragments[CHATVIEW].refresh();
					// if (CommonUtils.getTopActivity(MainActivity.this).equals(
					// GroupsActivity.class.getName())) {
					// GroupsActivity.instance.onResume();
					// }
				}
			});

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter,
				String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee,
				String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						// if (CommonUtils.getTopActivity(MainActivity.this)
						// .equals(GroupsActivity.class.getName())) {
						// GroupsActivity.instance.onResume();
						// }
					} catch (Exception e) {
						EMLog.e(TAG, "refresh exception " + e.getMessage());
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					if (mViewPager.getCurrentItem() == CHATVIEW)
						fragments[CHATVIEW].refresh();
					// if (CommonUtils.getTopActivity(MainActivity.this).equals(
					// GroupsActivity.class.getName())) {
					// GroupsActivity.instance.onResume();
					// }
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName,
				String applyer, String reason) {
			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg, false);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName,
				String accepter) {
			String st4 = getResources().getString(
					R.string.Agreed_to_your_group_chat_application);
			// 加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + st4));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (mViewPager.getCurrentItem() == CHATVIEW)
						fragments[CHATVIEW].refresh();
					// if (CommonUtils.getTopActivity(MainActivity.this).equals(
					// GroupsActivity.class.getName())) {
					// GroupsActivity.instance.onResume();
					// }
				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName,
				String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	public void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		MoxinApplication.getInstance().logout(null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								accountRemovedBuilder = null;
								finish();
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
							}
						});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.e(TAG,
						"---------color userRemovedBuilder error"
								+ e.getMessage());
			}

		}

	}

	/**
	 * 显示帐号在别处登录dialog
	 */
	public void showConflictDialog() {
		isConflictDialogShow = true;
		MoxinApplication.getInstance().logout(null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								conflictBuilder = null;
								finish();
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
							}
						});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG,
						"---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	public void initView() {
		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mChatTV = (TextView) findViewById(R.id.chat);
		mContactTV = (TextView) findViewById(R.id.contact);
		mFind_FindTV = (TextView) findViewById(R.id.find_find);
		mMeTV = (TextView) findViewById(R.id.me);
		unreadLabel = (TextView) findViewById(R.id.unread_chat_number);
		unreadAddressLable = (TextView) findViewById(R.id.unread_contact_number);
		tab_chat = (RelativeLayout) findViewById(R.id.tab_chat);
		tab_contact = (RelativeLayout) findViewById(R.id.tab_contact);
		tab_find = (RelativeLayout) findViewById(R.id.tab_find_find);
		tab_me = (RelativeLayout) findViewById(R.id.tab_me);
		tab_chat.setOnClickListener(this);
		tab_contact.setOnClickListener(this);
		tab_find.setOnClickListener(this);
		tab_me.setOnClickListener(this);

		initDrawable();

		fragments = new RefreshableFragement[FRAGMENTSIZE];
		fragments[FINDVIEW] = RefreshableFragement
				.getInstance("Find_FindFragment");
		fragments[CHATVIEW] = RefreshableFragement
				.getInstance("ConversationFragment");
		fragments[CONTACTVIEW] = RefreshableFragement
				.getInstance("ContactFragment");
		fragments[MEVIEW] = RefreshableFragement.getInstance("MyFragment");

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return FRAGMENTSIZE;
			}

			@Override
			public Fragment getItem(int position) {
				return fragments[position];
			}
		};
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(FINDVIEW);// 设置当前显示标签页为第一页
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				resetTextView();
				((Find_FindFragment) fragments[FINDVIEW]).onHideChanged(true);
				switch (position) {
				case FINDVIEW:
					((Find_FindFragment) fragments[FINDVIEW])
							.onHideChanged(false);
					mFind_FindTV.setCompoundDrawables(null,
							mFind_FindImg_selected, null, null);
					break;
				case CHATVIEW:
					mChatTV.setCompoundDrawables(null, mChatImg_selected, null,
							null);
					break;
				case CONTACTVIEW:
					mContactTV.setCompoundDrawables(null, mContactImg_selected,
							null, null);
					break;
				case MEVIEW:
					mMeTV.setCompoundDrawables(null, mMeImg_selected, null,
							null);
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		updateUnreadLabel();
		updateUnreadAddressLable();
	}

	protected void initDrawable() {
		mChatImg_normal = getResources().getDrawable(R.drawable.chat_normal);
		mChatImg_selected = getResources()
				.getDrawable(R.drawable.chat_selected);
		mContactImg_normal = getResources().getDrawable(
				R.drawable.contact_normal);
		mContactImg_selected = getResources().getDrawable(
				R.drawable.contact_selected);
		mFind_FindImg_normal = getResources().getDrawable(
				R.drawable.find_find_normal);
		mFind_FindImg_selected = getResources().getDrawable(
				R.drawable.find_find_selected);
		mMeImg_normal = getResources().getDrawable(R.drawable.me_normal);
		mMeImg_selected = getResources().getDrawable(R.drawable.me_selected);

		mChatImg_normal.setBounds(0, 0, mChatImg_normal.getMinimumWidth(),
				mChatImg_normal.getMinimumHeight());
		mChatImg_selected.setBounds(0, 0, mChatImg_selected.getMinimumWidth(),
				mChatImg_selected.getMinimumHeight());
		mContactImg_selected.setBounds(0, 0,
				mContactImg_selected.getMinimumWidth(),
				mContactImg_selected.getMinimumHeight());
		mContactImg_normal.setBounds(0, 0,
				mContactImg_normal.getMinimumWidth(),
				mContactImg_normal.getMinimumHeight());
		mFind_FindImg_normal.setBounds(0, 0,
				mFind_FindImg_normal.getMinimumWidth(),
				mFind_FindImg_normal.getMinimumHeight());
		mFind_FindImg_selected.setBounds(0, 0,
				mFind_FindImg_selected.getMinimumWidth(),
				mFind_FindImg_selected.getMinimumHeight());
		mMeImg_normal.setBounds(0, 0, mMeImg_normal.getMinimumWidth(),
				mMeImg_normal.getMinimumHeight());
		mMeImg_selected.setBounds(0, 0, mMeImg_selected.getMinimumWidth(),
				mMeImg_selected.getMinimumHeight());
	}

	protected void resetTextView() {
		mChatTV.setCompoundDrawables(null, mChatImg_normal, null, null);
		mContactTV.setCompoundDrawables(null, mContactImg_normal, null, null);
		mFind_FindTV.setCompoundDrawables(null, mFind_FindImg_normal, null,
				null);
		mMeTV.setCompoundDrawables(null, mMeImg_normal, null, null);
	}

	static class RefreshHandler extends Handler {
		WeakReference<MainActivity> wr_ma;

		public RefreshHandler(MainActivity ma) {
			wr_ma = new WeakReference<MainActivity>(ma);
		}

		@Override
		public void handleMessage(Message msg) {

			// 刷新联系人
			if (wr_ma.get().fragments[CONTACTVIEW] != null
					&& wr_ma.get().fragments[CONTACTVIEW].isAdded())
				wr_ma.get().fragments[CONTACTVIEW].refresh();
			// 刷新bottom bar消息未读数
			wr_ma.get().updateUnreadAddressLable();
			// 刷新消息
			if (wr_ma.get().fragments[CHATVIEW] != null
					&& wr_ma.get().fragments[CHATVIEW].isAdded())
				wr_ma.get().fragments[CHATVIEW].refresh();
			// 刷新bottom bar消息未读数
			wr_ma.get().updateUnreadLabel();
		}
	}

	private RefreshHandler refreshHandler = new RefreshHandler(this);

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_find_find:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.tab_chat:
			mViewPager.setCurrentItem(1);
			break;
		case R.id.tab_contact:
			mViewPager.setCurrentItem(2);
			break;
		case R.id.tab_me:
			mViewPager.setCurrentItem(3);
			break;
		default:
			break;
		}
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadMsgCountTotal();
				if (count > 0) {
					unreadLabel.setText(String.valueOf(count));
					unreadLabel.setVisibility(View.VISIBLE);
				} else {
					unreadLabel.setVisibility(View.INVISIBLE);
				}
				if (fragments[CHATVIEW] != null
						&& fragments[CHATVIEW].isAdded()) {
					fragments[CHATVIEW].refresh();
				}
			}
		});
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg, boolean savable) {
		// 保存msg
		inviteMessageDao.saveMessage(msg);
		if (savable) {
			new Thread(new ContactFriendLoader(msg.getFrom(), this,
					refreshHandler)).start();
		} else {
			new Thread(new StrangerFriendLoader(msg.getFrom(), this,
					refreshHandler)).start();
		}
	}

	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg, boolean savable) {
		saveInviteMsg(msg, savable);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {

		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					unreadAddressLable.setText(String.valueOf(count));
					unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
				if (fragments[CONTACTVIEW] != null
						&& fragments[CONTACTVIEW].isAdded()) {
					fragments[CONTACTVIEW].refresh();
				}
			}
		});

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		unreadAddressCountTotal = MoxinApplication.getInstance()
				.getUnreadContactCnt();
		return unreadAddressCountTotal;
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				moveTaskToBack(false);
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();

			// 提示新消息
			HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			try {
				updateUnreadAddressLable();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				updateUnreadLabel();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

		case EventOfflineMessage: {
			updateUnreadAddressLable();
			updateUnreadLabel();
			break;
		}

		default:
			break;
		}
	}

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper
				.getInstance();
		sdkHelper.popActivity(this);

		super.onStop();
	}
}
