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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.bean.User;
import com.heu.moxin.dao.MeDao;
import com.heu.moxin.dao.UserDao;

public class DetailChangeActivity extends Activity {

	private TextView title;
	private EditText edit;
	private int idcode;
	String item;
	Button save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_new_age);
		Intent intent = getIntent();
		item = intent.getStringExtra("class");
		if (item.contains("signature"))
			idcode = 1;
		else if (item.contains("age"))
			idcode = 2;
		else if (item.contains("region"))
			idcode = 3;
		else if (item.contains("nickname"))
			idcode = 4;
		else
			idcode = 0;
		Log.e("DetailChange", String.valueOf(idcode));
		title = (TextView) findViewById(R.id.title);
		edit = (EditText) findViewById(R.id.edit);
		switch (idcode) {
		case 1:
			title.setText("更改签名");
			edit.setHint("如果爱自己是个追求终身浪漫的过程，请从签名开始雕琢");
			break;
		case 2:
			break;
		case 3:
			title.setText("更改地区");
			edit.setHint("我宅在");
			break;
		case 4:
			title.setText("更改昵称");
			edit.setHint("不要迷恋哥，哥只是个传说");
			break;
		default:
			Log.e("DetailChange", "error");
		}

		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MeDao dao = new MeDao(DetailChangeActivity.this);
				User user = null;
				Log.e(String.valueOf(idcode), "idcode");

				user = dao.getContact(MoxinApplication.getInstance()
						.getUserName());
				switch (idcode) {
				case 1:
					user.setSign(edit.getText().toString());
					dao.saveContact(user);
					break;
				case 2:
					user.setAge(edit.getText().toString());
					dao.saveContact(user);
					// user =
					// dao.getContact(MoxinApplication.getInstance().getUserName());
					// Log.e(user.getAge()+"age","age");
					break;
				case 3:
					user.setRegion(edit.getText().toString());
					dao.saveContact(user);
					break;
				case 4:
					user.setUsername(edit.getText().toString());
					dao.saveContact(user);
					break;
				default:
					Log.e("DetailChange", "error");
				}
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
								.getUsername()));
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
