package com.heu.moxin.activity;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.heu.moxin.Constant;
import com.heu.moxin.R;

public class GameActivity extends Activity {
	public static final String TAG = "GameActivity";
	private WebView webview;
	private String username;
	private String phone;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game);
		this.username = getIntent().getStringExtra("username");
		Log.e(TAG, "phone1 : " + phone);
		this.phone = getIntent().getStringExtra("phone");
		webview = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowFileAccess(true);// 设置允许访问文件数据
		webSettings.setUseWideViewPort(true);// 关键点
		webSettings.setLoadWithOverviewMode(true);
		Random r = new Random();
		int i = r.nextInt(5) + 1;
		webview.loadUrl("file:///android_asset/" + i + "/index.html");
		Log.e("game", "start");
		webview.addJavascriptInterface(this, "uploadscore");
	}

	@JavascriptInterface
	public void clicked(final int i, final double g) {
		// 进入聊天页面
		Intent intent = new Intent(this, ChatActivity.class);
		// it is single chat
		intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
		Log.e(TAG, "phone2 : " + phone);
		intent.putExtra("userId", phone);
		intent.putExtra("userNick", username);
		intent.putExtra("game_result", "您好," + username + "我在找一找游戏"
				+ Constant.GAMES_NAME[i - 1] + "中获得了" + g + "的好成绩！");
		startActivity(intent);
		finish();
	}

	private MediaPlayer mp = null;

	@JavascriptInterface
	public void play(String name) {
		if (mp != null) {
			mp.release();
			mp = null;
		}
		if ("fizzing".equals(name))
			mp = MediaPlayer.create(GameActivity.this, R.raw.fizzing);
		else if ("shake".equals(name))
			mp = MediaPlayer.create(GameActivity.this, R.raw.shake);
		if (mp != null)
			mp.start();
		else
			Log.e("media", "null");
	}

	@Override
	protected void onDestroy() {
		/*
		 * mp.release(); mp = null;
		 */
		super.onDestroy();
	}
}
