/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heu.moxin.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.heu.moxinlib.controller.HXSDKHelper;

//需要重新建表
public class DbOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 7;
	private static DbOpenHelper instance;
	private static final String ME_TABLE_CREATE = "CREATE TABLE "
			+ MeDao.TABLE_NAME + " (" + MeDao.COLUMN_NAME_NICK + " TEXT, "
			+ MeDao.COLUMN_NAME_AVATAR + " TEXT, " + MeDao.COLUMN_NAME_BEIZHU
			+ " TEXT, " + MeDao.COLUMN_NAME_MID + " TEXT, "
			+ MeDao.COLUMN_NAME_REGION + " TEXT, " + MeDao.COLUMN_NAME_SEX
			+ " TEXT, " + MeDao.COLUMN_NAME_AGE + " TEXT, "
			+ MeDao.COLUMN_NAME_SIGN + " TEXT, " + MeDao.COLUMN_NAME_TEL
			+ " TEXT, " + MeDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
	private static final String TOPUSER_TABLE_CREATE = "CREATE TABLE "
			+ TopUserDao.TABLE_NAME + " (" + TopUserDao.COLUMN_NAME_TIME
			+ " TEXT, " + TopUserDao.COLUMN_NAME_IS_GOUP + " TEXT, "
			+ TopUserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

	private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
			+ UserDao.TABLE_NAME + " (" + UserDao.COLUMN_NAME_NICK + " TEXT, "
			+ UserDao.COLUMN_NAME_AVATAR + " TEXT, "
			+ UserDao.COLUMN_NAME_BEIZHU + " TEXT, " + UserDao.COLUMN_NAME_MID
			+ " TEXT, " + UserDao.COLUMN_NAME_REGION + " TEXT, "
			+ UserDao.COLUMN_NAME_SEX + " TEXT, " + UserDao.COLUMN_NAME_AGE
			+ " TEXT, " + UserDao.COLUMN_NAME_SIGN + " TEXT, "
			+ UserDao.COLUMN_NAME_TEL + " TEXT, " + UserDao.COLUMN_NAME_ID
			+ " TEXT PRIMARY KEY);";

	private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
			+ InviteMessageDao.TABLE_NAME + " ("
			+ InviteMessageDao.COLUMN_NAME_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ InviteMessageDao.COLUMN_NAME_FROM + " TEXT, "
			+ InviteMessageDao.COLUMN_NAME_GROUP_ID + " TEXT, "
			+ InviteMessageDao.COLUMN_NAME_GROUP_Name + " TEXT, "
			+ InviteMessageDao.COLUMN_NAME_REASON + " TEXT, "
			+ InviteMessageDao.COLUMN_NAME_STATUS + " INTEGER, "
			+ InviteMessageDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
			+ InviteMessageDao.COLUMN_NAME_TIME + " TEXT); ";

	private static final String CREATE_PREF_TABLE = "CREATE TABLE "
			+ UserDao.PREF_TABLE_NAME + " ("
			+ UserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
			+ UserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

	private DbOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
	}

	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}

	private static String getUserDatabaseName() {
		return HXSDKHelper.getInstance().getHXId() + "_glufine.db";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ME_TABLE_CREATE);
		db.execSQL(USERNAME_TABLE_CREATE);
		db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
		db.execSQL(TOPUSER_TABLE_CREATE);
		db.execSQL(CREATE_PREF_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			db.execSQL(CREATE_PREF_TABLE);
		}
		if (oldVersion <= 6) {
			db.execSQL(ME_TABLE_CREATE);
		}
	}

	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		if (instance != null) {
			try {
				SQLiteDatabase db = instance.getWritableDatabase();
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			instance = null;
		}
	}

}
