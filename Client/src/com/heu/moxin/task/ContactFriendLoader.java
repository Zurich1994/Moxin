package com.heu.moxin.task;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.bean.User;
import com.heu.moxin.dao.UserDao;

public class ContactFriendLoader extends FriendLoader {
	public static final String TAG = "ContactFriendLoader";
	public ContactFriendLoader(String phoneList, Context context,
			Handler handler) {
		super(phoneList, context, handler);
	}

	@Override
	public void run() {
		Message msg = Message.obtain();
		Map<String, User> map = null;
		try {
			String url = Constant.URL_Friend;
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(URI.create(url));
			List<NameValuePair> pairList = new ArrayList<NameValuePair>();
			pairList.add(new BasicNameValuePair("phones", phoneList));
			UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(
					pairList, "UTF-8");
			request.setEntity(requestEntity);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			byte[] result = EntityUtils.toByteArray(entity);
			String json = new String(result, "UTF-8");
			Log.e(TAG, "json " + json);
			JSONObject jo = JSONObject.parseObject(json);
			JSONArray ja = jo.getJSONArray("list");
			map = new HashMap<String, User>();
			List<User> users = new ArrayList<User>();
			if (ja != null) {
				for (int i = 0; i < ja.size(); i++) {
					JSONObject j = (JSONObject) ja.getJSONObject(i);
					User user = new User();
					user.setAvatar(j.getString("avatar"));
					user.setPhone(j.getString("phone"));
					user.setUsernick(j.getString("nickname"));
					user.setSign(j.getString("signature"));
					user.setSex(j.getString("gender"));
					user.setMid(j.getString("id"));
					user.setRegion(j.getString("area"));
					user.setUserHearder(j.getString("nickname"));
					users.add(user);
					map.put(j.getString("phone"), user);
				}
				msg.what = FOUND;
			} else {
				msg.what = NOTFOUND;
			}
			msg.obj = users;
			Map<String, User> userlist = MoxinApplication.getInstance()
					.getContactList();
			userlist.putAll(map);
			// 存入内存
			MoxinApplication.getInstance().setContactList(userlist);
			// 存入db
			UserDao dao = new UserDao(wr_context.get());
			dao.saveContactList(users);
		} catch (NullPointerException e) {
			msg.what = NOTFOUND;
			e.printStackTrace();
		} catch (Exception e) {
			msg.what = ERROR;
			e.printStackTrace();
		}
		if (handler != null) {
			handler.sendMessage(msg);
		}
	}

}
