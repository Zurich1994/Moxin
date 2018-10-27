package com.heu.moxin.task;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heu.moxin.Constant;
import com.heu.moxin.bean.User;
import com.heu.moxin.dao.MeDao;

public class MineFriendLoader extends FriendLoader {
	public static final String TAG = "MineFriendLoader";
	public MineFriendLoader(String phoneList, Context context) {
		super(phoneList, context, null);
	}

	@Override
	public void run() {
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
			if (ja != null) {
				JSONObject j = (JSONObject) ja.getJSONObject(0);
				User user = new User();
				user.setAvatar(j.getString("avatar"));
				user.setPhone(j.getString("phone"));
				user.setUsernick(j.getString("nickname"));
				user.setSign(j.getString("signature"));
				user.setSex(j.getString("gender"));
				user.setMid(j.getString("id"));
				user.setRegion(j.getString("area"));
				user.setUserHearder(j.getString("nickname"));
				Log.e(TAG, "user " + user);
				// 存入db
				MeDao dao = new MeDao(wr_context.get());
				dao.saveContact(user);
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
