package com.heu.moxin;

import java.util.Map;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Vibrator;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.EMCallBack;
import com.heu.moxin.bean.User;

public class MoxinApplication extends Application {

	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
	public static Context applicationContext;
	private static MoxinApplication instance;
	public static StringBuffer lat = null;
	public static StringBuffer lon = null;
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	public Vibrator mVibrator;
	public BDLocation bdlocation;
	public SharedPreferences sharedPreferences;
	public Editor editor;
	private static boolean onshake;
	private boolean isOpenLocation = false;
	private static boolean j_vibration = true;
	private static boolean j_voice = true;
	private static boolean j_notify = true;
	private static boolean j_loud = true;

	public static boolean isOnshake() {
		return onshake;
	}

	public static void setOnshake(boolean onshake) {
		MoxinApplication.onshake = onshake;
	}

	public static boolean isJ_voice() {
		return j_voice;
	}

	public static void setJ_voice(boolean j_voice) {
		MoxinApplication.j_voice = j_voice;
	}

	public static boolean isJ_notify() {
		return j_notify;
	}

	public static void setJ_notify(boolean j_notify) {
		MoxinApplication.j_notify = j_notify;
	}

	public static boolean isJ_loud() {
		return j_loud;
	}

	public static void setJ_loud(boolean j_loud) {
		MoxinApplication.j_loud = j_loud;
	}

	public static boolean isJ_vibration() {
		return j_vibration;
	}

	public static void setJ_vibration(boolean j_vibration) {
		MoxinApplication.j_vibration = j_vibration;
	}

	public int getUnreadContactCnt() {
		return sharedPreferences.getInt("UnreadContactCnt", 0);
	}

	public void setUnreadContactCnt(int unreadContactCnt) {
		// 借用连天奇的SharedPreferences
		editor.putInt("UnreadContactCnt", unreadContactCnt);
		editor.commit();
	}

	public void AddUnreadContactCnt() {
		// 借用连天奇的SharedPreferences
		editor.putInt("UnreadContactCnt",
				sharedPreferences.getInt("UnreadContactCnt", 0) + 1);
		editor.commit();
	}

	public void MinusUnreadContactCnt() {
		// 借用连天奇的SharedPreferences
		int unread = sharedPreferences.getInt("UnreadContactCnt", 0);
		if (unread > 1) {
			editor.putInt("UnreadContactCnt", unread - 1);
			editor.commit();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		applicationContext = this;
		instance = this;
		hxSDKHelper.onInit(applicationContext);
		sharedPreferences = getSharedPreferences("baidulocate",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		SDKInitializer.initialize(this);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(30000); // 30秒一次定位
		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.setLocOption(option);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(getApplicationContext());
		mVibrator = (Vibrator) getApplicationContext().getSystemService(
				Service.VIBRATOR_SERVICE);
		startLocation();
	}

	public static MoxinApplication getInstance() {
		return instance;
	}

	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
		Map<String, User> map = hxSDKHelper.getContactList();
		return map;

	}

	/**
	 * 开始定位
	 */
	public void startLocation() {
		if (!isOpenLocation) {
			mLocationClient.start();
			isOpenLocation = true;
		}
	}

	/**
	 * 结束定位
	 */
	public void stopLocation() {
		if (isOpenLocation) {
			mLocationClient.stop();
			isOpenLocation = false;
		}
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名（环信）
	 *
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名（环信）
	 *
	 * @param user
	 */
	public void setUserName(String phone) {
		hxSDKHelper.setHXId(phone);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(emCallBack);
		stopLocation();
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			bdlocation = location;
			editor.putString("lon", String.valueOf(location.getLongitude()));
			editor.putString("lat", String.valueOf(location.getLatitude()));
			editor.putFloat("radius", location.getRadius());
			editor.commit();

		}

	}
}
