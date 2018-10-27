package com.heu.moxin.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.heu.moxin.bean.User;

public class MeDao {
	public static final String TAG = "MeDao";
	public static final String TABLE_NAME = "me";
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

	public MeDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
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
				user.setUsernick(nick);
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
}
