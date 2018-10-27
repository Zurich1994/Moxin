package com.heu.moxin.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.util.HanziToPinyin;
import com.heu.moxin.Constant;
import com.heu.moxin.bean.User;

public class UserDao {
	public static final String TAG = "UserDao";
	public static final String TABLE_NAME = "users";
	public static final String COLUMN_NAME_ID = "username";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_NAME_SEX = "sex";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	public static final String COLUMN_NAME_SIGN = "sign";
	public static final String COLUMN_NAME_TEL = "tel";
	public static final String COLUMN_NAME_MID = "mid";
	public static final String COLUMN_NAME_REGION = "region";
	public static final String COLUMN_NAME_BEIZHU = "beizhu";
	public static final String COLUMN_NAME_IS_STRANGER = "is_stranger";
	public static final String PREF_TABLE_NAME = "pref";
	public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
	public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";
	public static final String COLUMN_NAME_AGE = "age";
	private DbOpenHelper dbHelper;

	public UserDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<User> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (User user : contactList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, user.getPhone());
				if (user.getUsernick() != null) {
					values.put(COLUMN_NAME_NICK, user.getUsernick());
				}
				if (user.getBeizhu() != null) {
					values.put(COLUMN_NAME_BEIZHU, user.getBeizhu());
				}
				if (user.getPhone() != null) {
					values.put(COLUMN_NAME_TEL, user.getPhone());
				}
				if (user.getSex() != null) {
					values.put(COLUMN_NAME_SEX, user.getSex());
				}
				if (user.getAvatar() != null) {
					values.put(COLUMN_NAME_AVATAR, user.getAvatar());
				}
				if (user.getSign() != null) {
					values.put(COLUMN_NAME_SIGN, user.getSign());
				}
				if (user.getMid() != null) {
					values.put(COLUMN_NAME_MID, user.getMid());
				}
				if (user.getAge() != null) {
					values.put(COLUMN_NAME_AGE, user.getAge());
				}
				if (user.getRegion() != null) {
					values.put(COLUMN_NAME_REGION, user.getRegion());
				}
				db.replace(TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public Map<String, User> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_AVATAR));
				String tel = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_TEL));
				String sign = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_SIGN));
				String age = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_AGE));
				String sex = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_SEX));
				String region = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_REGION));
				String beizhu = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_BEIZHU));
				String mid = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_MID));
				User user = new User();
				user.setUsername(username);
				user.setUsernick(nick);
				user.setBeizhu(beizhu);
				user.setMid(mid);
				user.setRegion(region);
				user.setAge(age);
				user.setSex(sex);
				user.setSign(sign);
				user.setPhone(tel);
				user.setAvatar(avatar);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getUsernick())) {
					headerName = user.getUsernick();
				} else {
					headerName = user.getUsername();
				}
				if (TextUtils.isEmpty(headerName))
					continue;
				if (username.equals(Constant.NEW_FRIENDS_USERNAME)
						|| username.equals(Constant.GROUP_USERNAME)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance()
							.get(headerName.substring(0, 1)).get(0).target
							.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}

	/**
	 * 删除一个联系人
	 * 
	 * @param username
	 */
	public void deleteContact(String username) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?",
					new String[] { username });
		}
	}

	/**
	 * 保存一个联系人
	 * 
	 * @param user
	 */
	public void saveContact(User user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, user.getPhone());
		if (user.getUsernick() != null) {
			values.put(COLUMN_NAME_NICK, user.getUsernick());
		}
		if (user.getBeizhu() != null) {
			values.put(COLUMN_NAME_BEIZHU, user.getBeizhu());
		}
		if (user.getPhone() != null) {
			values.put(COLUMN_NAME_TEL, user.getPhone());
		}
		if (user.getSex() != null) {
			values.put(COLUMN_NAME_SEX, user.getSex());
		}
		if (user.getAge() != null) {
			values.put(COLUMN_NAME_AGE, user.getAge());
		}
		if (user.getAvatar() != null) {
			values.put(COLUMN_NAME_AVATAR, user.getAvatar());
		}
		if (user.getSign() != null) {
			values.put(COLUMN_NAME_SIGN, user.getSign());
		}
		if (user.getMid() != null) {
			values.put(COLUMN_NAME_MID, user.getMid());
		}
		if (user.getRegion() != null) {
			values.put(COLUMN_NAME_REGION, user.getRegion());
		}
		if (db.isOpen()) {
			long r = db.replace(TABLE_NAME, null, values);
			Log.e(TAG, "数据库执行结果" + r);
		}
	}

	/**
	 * 取出一个联系人
	 * 
	 * @param user
	 */
	public User getContact(String phone) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// Map<String, User> users = new HashMap<String, User>();
		User users = null;
		if (db.isOpen()) {
			String sql = "select * from " + TABLE_NAME + " where "
					+ COLUMN_NAME_ID + " = '" + phone + "'"/*
															 * + " desc"
															 */;
			Log.e(TAG, "sql : " + sql);
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				String username = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_AVATAR));
				String tel = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_TEL));
				String sign = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_SIGN));
				String sex = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_SEX));
				String age = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_AGE));
				String region = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_REGION));
				String beizhu = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_BEIZHU));
				String mid = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_MID));
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				user.setBeizhu(beizhu);
				user.setMid(mid);
				user.setRegion(region);
				user.setAge(age);
				user.setSex(sex);
				user.setSign(sign);
				user.setPhone(tel);
				user.setAvatar(avatar);
				users = user;
			}
			cursor.close();
		}
		if (users == null)
			Log.e(TAG, "null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!->" + phone);
		return users;
	}

	public void setDisabledGroups(List<String> groups) {
		setList(COLUMN_NAME_DISABLED_GROUPS, groups);
	}

	public List<String> getDisabledGroups() {
		return getList(COLUMN_NAME_DISABLED_GROUPS);
	}

	public void setDisabledIds(List<String> ids) {
		setList(COLUMN_NAME_DISABLED_IDS, ids);
	}

	public List<String> getDisabledIds() {
		return getList(COLUMN_NAME_DISABLED_IDS);
	}

	private void setList(String column, List<String> strList) {
		StringBuilder strBuilder = new StringBuilder();

		for (String hxid : strList) {
			strBuilder.append(hxid).append("$");
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(column, strBuilder.toString());

			db.update(PREF_TABLE_NAME, values, null, null);
		}
	}

	private List<String> getList(String column) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select " + column + " from "
				+ PREF_TABLE_NAME, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}

		String strVal = cursor.getString(0);
		if (strVal == null || strVal.equals("")) {
			return null;
		}

		cursor.close();
		String[] array = strVal.split("$");

		if (array != null && array.length > 0) {
			List<String> list = new ArrayList<String>();
			for (String str : array) {
				list.add(str);
			}

			return list;
		}

		return null;
	}
	
	/**
	 * 获取全部list
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public Map<String, User> getAllList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_AVATAR));
				String tel = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_TEL));
				String sign = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_SIGN));
				String age = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_AGE));
				String sex = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_SEX));
				String region = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_REGION));
				String beizhu = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_BEIZHU));
				String fxid = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_MID));
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				user.setBeizhu(beizhu);
				user.setMid(fxid);
				user.setRegion(region);
				user.setAge(age);
				user.setSex(sex);
				user.setSign(sign);
				user.setPhone(tel);
				user.setAvatar(avatar);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getUsernick())) {
					headerName = user.getUsernick();
				} else {
					headerName = user.getUsername();
				}
				if (TextUtils.isEmpty(headerName))
					continue;
				if (username.equals(Constant.NEW_FRIENDS_USERNAME)
						|| username.equals(Constant.GROUP_USERNAME)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance()
							.get(headerName.substring(0, 1)).get(0).target
							.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}
}
