package com.heu.moxin.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.bean.User;
import com.heu.moxin.dao.MeDao;

public class GenderChangeActivity extends Activity {
	public static final String TAG = "GenderChangeActivity";
	private TextView title;
	private RadioGroup gender;
	private RadioButton male;
	private RadioButton female;
	private int idcode;
	String item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_new_gender);
		title = (TextView) findViewById(R.id.title);
		gender = (RadioGroup) findViewById(R.id.gender);
		male = (RadioButton) findViewById(R.id.male);
		female = (RadioButton) findViewById(R.id.female);
		title.setText("更改性别");
		Button save;
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MeDao dao = new MeDao(GenderChangeActivity.this);
				User user = null;
				Log.e(TAG, MoxinApplication.getInstance().getUserName());
				user = dao.getContact(MoxinApplication.getInstance()
						.getUserName());
				if (male.isChecked())
					user.setSex("male");
				else
					user.setSex("female");
				dao.saveContact(user);
				// user =
				// dao.getContact(MoxinApplication.getInstance().getUserName());
				// Log.e(user.getSex(),"gender");
				final User usert = user;
				new Thread(new Runnable() {

					@Override
					public void run() {
						// 调用服务器注册方法
						HttpClient client = new DefaultHttpClient();
						HttpPost request = new HttpPost(URI
								.create(Constant.URL_UpdateUserInfo));
						List<NameValuePair> list = new ArrayList<NameValuePair>();
						list.add(new BasicNameValuePair("id", usert.getMid()));
						list.add(new BasicNameValuePair("nickname", usert
								.getUsernick()));
						list.add(new BasicNameValuePair("password",
								MoxinApplication.getInstance().getPassword()));
						list.add(new BasicNameValuePair("gender", usert
								.getSex()));
						list.add(new BasicNameValuePair("email", "1@qq.com"));
						list.add(new BasicNameValuePair("avatar", "username="
								+ usert.getPhone() + ".png"));
						list.add(new BasicNameValuePair("age", usert.getAge()));
						list.add(new BasicNameValuePair("area", usert
								.getRegion()));
						list.add(new BasicNameValuePair("signature", usert
								.getSign()));
						list.add(new BasicNameValuePair("hobbies", ""));
						list.add(new BasicNameValuePair("labels", ""));
						list.add(new BasicNameValuePair("experience", "0"));
						try {
							UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(
									list, "UTF-8");
							request.setEntity(requestEntity);
							client.execute(request);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
				finish();
			}
		});

	}
	
	public void back(View view) {
		finish();
	}

}