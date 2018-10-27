package com.heu.moxin.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.activity.MainActivity;
import com.heu.moxin.utils.ScreenUtil;

public class ShakeListener implements SensorEventListener {

	// rock
	private Vibrator mVibrator;
	private final int VIBRATOR_TIME = 300;
	private boolean haveShaked;
	private boolean haveStarted;
	// animation
	private TranslateAnimation mOriginalTranslateUpwards;
	private TranslateAnimation mOriginalTranslateDownwards;
	private AnimationSet mNewInfoAnimationSet;

	private View mAnimationView;
	private int mUpwardsHeight;
	private int mDownwardsHeight;
	private int mDownwardsDuration = 2000;
	private int mNewInfoAnimationDuration = 300;
	String TAG = "ShakeListener";
	// 速度阈值，当摇晃速度达到这值后产生作用
	private static final int SPEED_SHRESHOLD = 3500;
	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 70;
	// 传感器管理器
	private SensorManager sensorManager;
	// 传感器
	private Sensor sensor;
	// 重力感应监听器
	private OnShakeListener onShakeListener;
	// 上下文
	private Context mContext;
	// 手机上一个位置时重力感应坐标
	private float lastX;
	private float lastY;
	private float lastZ;
	// 上次检测时间
	private long lastUpdateTime;

	// 构造器
	public ShakeListener(Context c) {
		// 获得监听对象
		mContext = c;
		haveStarted = false;
	}

	// 开始
	public synchronized void start() {
		if (!haveStarted) {
			Log.e(TAG, "start");
			// 获得传感器管理器
			sensorManager = (SensorManager) mContext
					.getSystemService(Context.SENSOR_SERVICE);
			mVibrator = (Vibrator) mContext
					.getSystemService(Context.VIBRATOR_SERVICE);
			mAnimationView = ((MainActivity) mContext)
					.findViewById(R.id.rock_layout);
			if (sensorManager != null) {
				// 获得重力传感器
				sensor = sensorManager
						.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			}
			// 注册
			if (sensor != null) {
				sensorManager.registerListener(this, sensor,
						SensorManager.SENSOR_DELAY_GAME);
			}
			haveStarted = true;
			haveShaked = false;
		}
	}

	// 停止检测
	public synchronized void stop() {
		if (haveStarted) {
			Log.e(TAG, "stop");
			sensorManager.unregisterListener(this);
			haveStarted = false;
		}
	}

	// 设置重力感应监听器
	public void setOnShakeListener(OnShakeListener listener) {
		onShakeListener = listener;
	}

	// 重力感应器感应获得变化数据
	public synchronized void onSensorChanged(SensorEvent event) {
		if (!haveShaked && haveStarted) {

			// 现在检测时间
			long currentUpdateTime = System.currentTimeMillis();
			// 两次检测的时间间隔
			long timeInterval = currentUpdateTime - lastUpdateTime;
			// 判断是否达到了检测时间间隔
			if (timeInterval < UPTATE_INTERVAL_TIME)
				return;
			// 现在的时间变成last时间
			lastUpdateTime = currentUpdateTime;
			// 获得x,y,z坐标
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			// 获得x,y,z的变化值
			float deltaX = x - lastX;
			float deltaY = y - lastY;
			float deltaZ = z - lastZ;
			// 将现在的坐标变成last坐标
			lastX = x;
			lastY = y;
			lastZ = z;
			double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
					* deltaZ)
					/ timeInterval * 10000;
			// 达到速度阀值，发出提示
			if (speed >= SPEED_SHRESHOLD) {
				haveShaked = true;
				Log.e(TAG, "shake");
				if (MoxinApplication.isJ_vibration()) {
					// 连续震动时间
					mVibrator.vibrate(VIBRATOR_TIME);
				}
				
				// 间歇震动时间
				// mVibrator.vibrate(new long[] { 300, 200, 300, 200 }, -1);

				initAnimation();

				// 启动摇动处理的效果
				if (mAnimationView != null)
					mAnimationView.startAnimation(mOriginalTranslateUpwards);
				
				onShakeListener.onShake();
			}
		}
	}

	private void initAnimation() {

		int[] viewLocation = new int[2];
		// 获取当前view的坐标
		if (mAnimationView != null)
			mAnimationView.getLocationOnScreen(viewLocation);
		// 获取当前屏幕的宽度和高度
		int[] screenWidthHeight = ScreenUtil.getScreenWidthHeight(mContext);

		// real rest height
		// mDownwardsHeight = screenWidthHeight[1] - viewLocation[1];

		// 稍微向上移动一点距离
		mUpwardsHeight = screenWidthHeight[1] / 30;

		// 向下移动距离
		mDownwardsHeight = screenWidthHeight[1];

		// 向上移动效果
		if (mOriginalTranslateUpwards == null) {
			mOriginalTranslateUpwards = new TranslateAnimation(0, 0, 0,
					-mUpwardsHeight);
			mOriginalTranslateUpwards.setDuration(mDownwardsDuration / 10);
			mOriginalTranslateUpwards.setFillBefore(true);
			mOriginalTranslateUpwards.setFillEnabled(true);
			mOriginalTranslateUpwards.setFillAfter(true);
		}

		// 向下移动效果
		if (mOriginalTranslateDownwards == null) {
			mOriginalTranslateDownwards = new TranslateAnimation(0, 0,
					-mUpwardsHeight, mDownwardsHeight);
			mOriginalTranslateDownwards.setDuration(mDownwardsDuration);
			mOriginalTranslateUpwards.setFillBefore(true);
			mOriginalTranslateDownwards.setFillEnabled(true);
			mOriginalTranslateDownwards.setFillAfter(true);
		}

		// 透明渐变、移动的效果
		if (mNewInfoAnimationSet == null) {
			AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
			alphaAnimation.setDuration(mNewInfoAnimationDuration);
			alphaAnimation.setFillAfter(true);

			TranslateAnimation translateAnimation = new TranslateAnimation(
					screenWidthHeight[0] / 3, 0, -screenWidthHeight[1] / 6, 0);
			translateAnimation.setDuration(mNewInfoAnimationDuration);
			translateAnimation.setFillAfter(true);

			mNewInfoAnimationSet = new AnimationSet(false);
			mNewInfoAnimationSet.addAnimation(alphaAnimation);
			mNewInfoAnimationSet.addAnimation(translateAnimation);

			// 动画事件处理
			mNewInfoAnimationSet.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
				}
			});
		}

		// 动画事件处理
		mOriginalTranslateUpwards.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (mAnimationView != null)
					mAnimationView.startAnimation(mOriginalTranslateDownwards);
			}
		});

		// 动画事件处理
		mOriginalTranslateDownwards
				.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						mAnimationView.startAnimation(mNewInfoAnimationSet);
					}
				});
	}

	// 摇晃监听接口
	public interface OnShakeListener {
		public void onShake();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}