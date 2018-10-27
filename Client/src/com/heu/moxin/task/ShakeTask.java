package com.heu.moxin.task;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.activity.FindMapActivity;
import com.heu.moxin.bean.MapUser;

public class ShakeTask extends AsyncTask<Void, Integer, List<MapUser>> {
	public static final String TAG = "ShakeTask";
	private static final int SERVER_ERROR = 1;
	private static final int NETWORK_ERROR = 2;
	private static final int CANCELLED = 3;
	private static final int NOGUY_ERROR = 4;
	private boolean successed = true;

	@Override
	protected void onCancelled() {
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(List<MapUser> result) {
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
		if (successed) {
			Intent intent = new Intent(context, FindMapActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("list", (Serializable) result);
			intent.putExtras(b);
			intent.setAction("ACTION_GET");
			context.startActivity(intent);
		}
		super.onPostExecute(result);
	}

	private Context context;
	// 定位相关
	private LocationClient mLocClient;

	private SharedPreferences share;

	public ShakeTask(Context context) {
		this.context = context;
	}

	@Override
	protected List<MapUser> doInBackground(Void... params) {
		String CodeString = null;
		int i = 0;
		List<MapUser> list = null;
		try {
			do {
				byte[] result = httpClientPost(Constant.URL_NearBy);
				if (result != null) {
					CodeString = new String(result, "UTF-8");
					Log.e(TAG, "CodeString:" + CodeString);
					if (CodeString.indexOf("[]") != -1 && i == 2)
						publishProgress(NOGUY_ERROR);
					list = MapUsers(CodeString);
				}
				i++;
				if (i >= 3) {
					Log.e(TAG, "CodeString:" + CodeString);
					if (CodeString == null) {
						publishProgress(NETWORK_ERROR);
					} else if (CodeString.indexOf("[]") == -1 && i == 1)
						publishProgress(SERVER_ERROR); // Notify your activity
														// that
														// you had canceled the
														// task
					return null; // don't forget to terminate this method
				}
			} while (list == null);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			publishProgress(NETWORK_ERROR);
		}
		return list;
	}

	@Override
	protected void onPreExecute() {
		share = context.getSharedPreferences("baidulocate",
				Context.MODE_PRIVATE);
		if (share.getFloat("radius", 0) == 0
				|| share.getString("lon", null) == null
				|| share.getString("lat", null) == null) {
			Log.e("startmap", "nulltask");
			return;
		}
	}

	public byte[] httpClientPost(String url) {
		byte[] result = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(URI.create(url));
			// 创建请求体
			List<NameValuePair> pairList = new ArrayList<NameValuePair>();
			if (share.getString("lat", null) != null
					&& share.getString("lon", null) != null) {
				NameValuePair pair1 = new BasicNameValuePair("latitude",
						String.valueOf(share.getString("lat", null)));
				NameValuePair pair2 = new BasicNameValuePair("longitude",
						String.valueOf(share.getString("lon", null)));
				NameValuePair pair3 = new BasicNameValuePair("phone",
						MoxinApplication.getInstance().getUserName());
				pairList.add(pair1);
				pairList.add(pair2);
				pairList.add(pair3);
			}
			UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(
					pairList, "UTF-8");
			request.setEntity(requestEntity);
			HttpResponse resp = client.execute(request);
			int code = resp.getStatusLine().getStatusCode();
			Log.e(TAG, "code : " + code);
			if (code == 200) {
				HttpEntity resEntity = resp.getEntity();
				result = EntityUtils.toByteArray(resEntity);
			} else {
				publishProgress(SERVER_ERROR);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<MapUser> MapUsers(String codeString) {

		try {
			JSONArray jsonArray;
			if (codeString == null)
				return null;
			else
				jsonArray = new JSONArray(codeString);
			Log.e(TAG, codeString);
			int iSize = jsonArray.length();
			if (iSize == 0)
				return null;
			List<MapUser> MapUsers = new ArrayList<MapUser>();
			for (int i = 0; i < iSize; i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				MapUser MapUser = new MapUser();
				MapUser.setName(jsonObj.getString("u_nickname"));
				MapUser.setId(jsonObj.getString("u_id"));
				MapUser.setLatitude(jsonObj.getString("u_latitude"));
				MapUser.setLongitude(jsonObj.getString("u_longitude"));
				MapUser.setGender(jsonObj.getString("u_gender"));
				MapUser.setAutograph(jsonObj.getString("signature"));
				MapUser.setPhone(jsonObj.getString("u_phone"));
				String label = jsonObj.getString("labels");
				String[] labels = label.split(",");
				List<String> labelist = new ArrayList<String>();
				for (i = 0; i < labels.length; i++) {
					labelist.add(i, labels[i]);
				}
				MapUser.setLabel(labelist);
				MapUsers.add(MapUser);
			}
			return MapUsers;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... errorCode) {
		successed = false;
		switch (errorCode[0]) {
		case CANCELLED:
			Toast.makeText(context, "您以取消了该操作", Toast.LENGTH_LONG).show();
			break;
		case NETWORK_ERROR:
			Toast.makeText(context, "网络连接异常", Toast.LENGTH_LONG).show();
			break;
		case SERVER_ERROR:
			Toast.makeText(context, "服务器异常", Toast.LENGTH_LONG).show();
			break;
		case NOGUY_ERROR:
			Toast.makeText(context, "啊哦，附近竟然没人？", Toast.LENGTH_LONG).show();
			break;
		}
	}

}
