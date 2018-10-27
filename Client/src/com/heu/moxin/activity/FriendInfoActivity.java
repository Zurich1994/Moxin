package com.heu.moxin.activity;

import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.bean.User;
import com.heu.moxin.dao.MeDao;
import com.heu.moxin.dao.UserDao;
import com.heu.moxin.utils.LoadUserAvatar;
import com.heu.moxin.utils.LoadUserAvatar.ImageDownloadedCallBack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FriendInfoActivity extends Activity {

	ImageView avatar = null;
	TextView mid = null;
	TextView gender = null;
	TextView nickname = null;
	TextView labels = null;
	TextView signature = null;
	TextView area = null;
//	ImageView back = null;
	String username =null;
	User user = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_friend_info);
		oninit();
		Intent intent = getIntent();
		username = intent.getStringExtra("userName");
		if(username != null && !(username.contains( MoxinApplication.getInstance().getUserName())||MoxinApplication.getInstance().getUserName().contains(username))){
			UserDao dao = new UserDao (this);
			user = new User();
			user = dao.getContact(username);
			if(user != null){
			if (user.getMid() != null && !user.getMid().equals("")){
				mid.setText( user.getMid());
			}
			if (user.getSex() != null && !user.getSex().equals("")){
				gender.setText( user.getSex());
			}
			if (user.getUsernick() != null &&! user.getUsernick().equals("") ){
				nickname.setText( user.getUsernick());
			}
			if (user.getSign() != null&& !user.getSign().equals("")){
				labels.setText( user.getSign());
			}
			if (user.getBeizhu() != null &&!user.getBeizhu().equals("")){
				signature.setText( user.getBeizhu());
			}
			if (user.getRegion() != null&&!user.getRegion().equals("")){
				area.setText( user.getRegion());
			}
			}
			final String fileName = "username="
					+ username + ".png";
			avatar.setTag(fileName);
			LoadUserAvatar.getInstance().loadImage(avatar, Constant.AVATAR_PATH,
					Constant.URL_Download_Avatar + fileName, 1,
					new ImageDownloadedCallBack() {

						@Override
						public void onImageDownloaded(ImageView imageView,
								Bitmap bitmap) {
							if (imageView.getTag() == fileName) {
								imageView.setImageBitmap(bitmap);
							}
						}
					});
		}else if(username == null){
			Toast.makeText(getApplicationContext(), "数据库异常", Toast.LENGTH_LONG).show();
			finish();
		}else{
			MeDao dao = new MeDao(this);
			user = new User();
			if(user != null){
			Log.e("happy" , "bird");
			user = dao.getContact(MoxinApplication.getInstance().getUserName());
			if (user.getMid() != null && !user.getMid().equals("")){
				mid.setText( user.getMid());
			}
			if (user.getSex() != null && !user.getSex().equals("")){
				gender.setText( user.getSex());
			}
			if (user.getUsernick() != null && !user.getUsernick().equals("") ){
				nickname.setText( user.getUsernick());
			}
			if (user.getSign() != null&& !user.getSign().equals("")){
				labels.setText( user.getSign());
			}
			if (user.getBeizhu() != null &&!user.getBeizhu().equals("")){
				signature.setText( user.getBeizhu());
			}
			if (user.getRegion() != null&&!user.getRegion().equals("")){
				area.setText( user.getRegion());
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
					});}
		}
		/*back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				back(v);
			}
		});*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void oninit() {

		avatar = (ImageView) findViewById(R.id.tu);
		mid = (TextView) findViewById(R.id.tv_mid);
		gender = (TextView) findViewById(R.id.tv_gender);
		nickname = (TextView) findViewById(R.id.tv_nickname);
		labels = (TextView) findViewById(R.id.tv_labels);
		signature = (TextView) findViewById(R.id.tv_user_signature);
		area = (TextView) findViewById(R.id.tv_area);
	//	back = (ImageView) findViewById(R.id.back);
	}
	public void back(View view) {
		finish();
	}

}
