package com.heu.moxin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;

public class setMsgActivity extends Activity {
	private ImageView back;
	private ImageButton notify;
	private ImageButton voice;
	private ImageButton vibration;
	private ImageButton loudspeaker;
	private static boolean state = false;
	private static boolean notify_state = true;
	private static boolean voice_state = true;
	private static boolean vibration_state = true;
	private static boolean loudspeaker_state = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting_message);
		oninit();
		setBackground();
		boolean f = MoxinApplication.isJ_loud();
		state = f;
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				back(v);
			}
		});
		notify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (notify_state == true) {
					notify_state = false;
					notify_pic();
					MoxinApplication.setJ_notify(false);
				} else {
					notify_state = true;
					notify_pic();
					MoxinApplication.setJ_notify(true);
				}
			}
		});
		voice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (voice_state == true) {
					voice_state = false;
					voice_pic();
					MoxinApplication.setJ_voice(false);
				} else {
					voice_state = true;
					voice_pic();
					MoxinApplication.setJ_voice(true);
				}
			}
		});
		vibration.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (vibration_state == true) {
					vibration_state = false;
					vibration_pic();
					MoxinApplication.setJ_vibration(false);
				} else {
					vibration_state = true;
					vibration_pic();
					MoxinApplication.setJ_vibration(true);
				}
			}
		});
		loudspeaker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (loudspeaker_state == true) {
					loudspeaker_state = false;
					loud_pic();
					MoxinApplication.setJ_loud(false);
				} else {
					loudspeaker_state = true;
					loud_pic();
					MoxinApplication.setJ_loud(true);
				}
			}
		});

	}

	public void oninit() {
		notify = (ImageButton) findViewById(R.id.iv_switch_open_notification);
		voice = (ImageButton) findViewById(R.id.iv_switch_open_sound);
		vibration = (ImageButton) findViewById(R.id.iv_switch_open_vibrate);
		loudspeaker = (ImageButton) findViewById(R.id.iv_switch_open_speaker);
	}

	public void setBackground() {
		voice_pic();
		notify_pic();
		vibration_pic();
		loud_pic();
	}

	public void voice_pic() {
		if (voice_state) {
			voice.setImageDrawable(getResources().getDrawable(
					R.drawable.open_icon));
		} else {
			voice.setImageDrawable(getResources().getDrawable(
					R.drawable.close_icon));
		}
	}

	public void notify_pic() {
		if (notify_state) {
			notify.setImageDrawable(getResources().getDrawable(
					R.drawable.open_icon));
		} else {
			notify.setImageDrawable(getResources().getDrawable(
					R.drawable.close_icon));
		}
	}

	public void vibration_pic() {
		if (vibration_state) {
			vibration.setImageDrawable(getResources().getDrawable(
					R.drawable.open_icon));
		} else {
			vibration.setImageDrawable(getResources().getDrawable(
					R.drawable.close_icon));
		}
	}

	public void loud_pic() {
		if (loudspeaker_state) {
			loudspeaker.setImageDrawable(getResources().getDrawable(
					R.drawable.open_icon));
		} else {
			loudspeaker.setImageDrawable(getResources().getDrawable(
					R.drawable.close_icon));
		}
	}

	public void back(View view) {
		finish();
	}
}
