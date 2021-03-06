package com.heu.moxin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.heu.moxin.DemoHXSDKHelper;
import com.heu.moxin.R;

/**
 * 开屏页
 *
 */
public class SplashActivity extends Activity {
	@Override
	protected void onStart() {
		new Thread(new Runnable() {
			public void run() {
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					// ** 免登陆情况 加载所有本地群和会话
					// 不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					// 加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					long costTime = System.currentTimeMillis() - start;
					// 等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// 进入主页面
					startActivity(new Intent(SplashActivity.this,
							MainActivity.class));
					finish();
				} else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this,
							LoginActivity.class));
					finish();
				}
			}
		}).start();
		super.onStart();
	}

	private static final int sleepTime = 2000;
	private RelativeLayout rootLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);

		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

}
