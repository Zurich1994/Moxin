package com.heu.moxin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.bean.User;
import com.heu.moxin.dao.MeDao;
import com.heu.moxin.utils.LoadUserAvatar;
import com.heu.moxin.utils.LoadUserAvatar.ImageDownloadedCallBack;

public class setDetailActivity extends Activity {
	private ImageView back;
	private ImageView avatar;
	private TextView nickname;
	private TextView age;
	private TextView gender;
	private TextView area;
	private TextView occp;
	private TextView signature;
	private TextView ideal;
	private RelativeLayout lpic;
	private RelativeLayout lnickname;
	private RelativeLayout lmid;
	private RelativeLayout lgender;
	private RelativeLayout lage;
	private RelativeLayout lregion;
	private RelativeLayout lsignature;
	private RelativeLayout lremark;
	private SharedPreferences clients;
	private User user = null;
	@Override
	protected void onResume() {
		MeDao dao = new MeDao(this);
		user = dao.getContact(MoxinApplication.getInstance().getUserName());
		if (user == null) {
			Log.e("setdetail", "setdetail");
		} else {
			if (user.getUsername() != null)
				nickname.setText(user.getUsernick());
			// age.setText(user.getAge());
			age.setText(user.getAge());
			area.setText(user.getRegion());
			gender.setText(user.getSex());
			signature.setText(user.getSign());
		}
		final String fileName = "username="
				+ MoxinApplication.getInstance().getUserName() + ".png";
		avatar.setTag(fileName);
		LoadUserAvatar.getInstance().loadImage(avatar, Constant.AVATAR_PATH,
				Constant.URL_Download_Avatar + fileName,
				new ImageDownloadedCallBack() {

					@Override
					public void onImageDownloaded(ImageView imageView,
							Bitmap bitmap) {
						if (imageView.getTag() == fileName) {
							imageView.setImageBitmap(bitmap);
						}
					}
				});
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting_detail);
		oninit();
		// clients =
		// getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
		// if(clients.getString("name", "未命名")!=null){
		// Log.e(MoxinApplication.getInstance().getUserName()
		lnickname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(setDetailActivity.this,
						DetailChangeActivity.class);
				in.putExtra("class", "nickname");
				startActivity(in);
			}
		});
		lage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(setDetailActivity.this,
						DetailChangeActivity.class);
				in.putExtra("class", "age");
				startActivity(in);
			}
		});
		lsignature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(setDetailActivity.this,
						DetailChangeActivity.class);
				in.putExtra("class", "signature");
				startActivity(in);
			}
		});
		lregion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(setDetailActivity.this,
						DetailChangeActivity.class);
				in.putExtra("class", "region");
				startActivity(in);
			}
		});
		lgender.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(setDetailActivity.this,
						GenderChangeActivity.class);
				startActivity(in);
			}
		});
		Log.e(MoxinApplication.getInstance().getUserName(), "setdetailss");
	}

	public void oninit() {

		avatar = (ImageView) findViewById(R.id.iv_user_avatar);
		// if(LoadUserAvatars.getBmap()!=null)
		// avatar.setImageBitmap(LoadUserAvatar.getInstance().loadImage(imageView,
		// local_image_path, imageUrl, imageDownloadedCallBack))
		// else
		// Log.e("null pic","fragment_detail");
		nickname = (TextView) findViewById(R.id.tv_user_nickname);
		gender = (TextView) findViewById(R.id.tv_user_gender);
		age = (TextView) findViewById(R.id.tv_user_age);
		area = (TextView) findViewById(R.id.tv_user_area);
		signature = (TextView) findViewById(R.id.tv_user_signature);
		lpic = (RelativeLayout) findViewById(R.id.rl_user_pic);
		lnickname = (RelativeLayout) findViewById(R.id.rl_user_nickname);
		lmid = (RelativeLayout) findViewById(R.id.rl_user_mid);
		lgender = (RelativeLayout) findViewById(R.id.rl_user_gender);
		lage = (RelativeLayout) findViewById(R.id.rl_user_age);
		lregion = (RelativeLayout) findViewById(R.id.rl_user_area);
		lsignature = (RelativeLayout) findViewById(R.id.rl_user_sign);
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				back(v);
			}
		});
	}
	
	public void back(View view) {
		finish();
	}

}
