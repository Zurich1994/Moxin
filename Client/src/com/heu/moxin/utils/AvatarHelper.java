package com.heu.moxin.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Base64;
import android.util.Log;

import com.heu.moxin.Constant;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

public class AvatarHelper {
	public static final int PNG = 2;
	public static final int JPEG = 1;

	/**
	 * 将base64字节数组转换为bitmap
	 */
	public static Bitmap Base642Bitmap(byte[] base64) {
		// 将base64字节数组转换为普通的字节数组
		byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
		// 用BitmapFactory创建bitmap
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	/**
	 * 将base64字节数组转换为bitmap
	 */
	public static Bitmap Base642Bitmap(byte[] base64, Options options) {
		// 将base64字节数组转换为普通的字节数组
		byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
		// 用BitmapFactory创建bitmap
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}

	/**
	 * 将bitmap转换为base64字节数组
	 */
	public static byte[] Bitmap2Base64(Bitmap bitmap, int type) {
		try {
			// 先将bitmap转换为普通的字节数组
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (type == 1) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} else if (type == 2) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			}
			out.flush();
			out.close();
			byte[] buffer = out.toByteArray();
			// 将普通字节数组转换为base64数组
			byte[] encode = Base64.decode(buffer, Base64.DEFAULT);
			return encode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将bitmap转换为base64字节数组
	 */
	public static String Bitmap2Base64String(Bitmap bitmap, int type) {
		ByteArrayOutputStream out = null;
		try {
			// 先将bitmap转换为普通的字节数组
			out = new ByteArrayOutputStream();
			if (type == 1) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} else if (type == 2) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			}
			out.flush();
			byte[] buffer = out.toByteArray();
			// 将普通字节数组转换为base64数组
			String encode = Base64.encodeToString(buffer, Base64.DEFAULT);
			return encode;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * 上传头像
	 */
	public static void upload(Bitmap bitmap, String phone) {
		try {
			Configuration config = new Configuration.Builder()
					.chunkSize(256 * 1024) // 分片上传时，每片的大小。 默认 256K
					.putThreshhold(512 * 1024) // 启用分片上传阀值。默认 512K
					.connectTimeout(10) // 链接超时。默认 10秒
					.responseTimeout(60) // 服务器响应超时。默认 60秒
					.build();
			// 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
			UploadManager uploadManager = new UploadManager(config);
			String key = "img/avatar/" + phone + ".png";
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(Constant.URL_GET_TOKEN + "?KEY="
					+ key);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			byte[] result = EntityUtils.toByteArray(entity);
			String token = new String(result, "UTF-8");// <从服务端SDK获取>;
			uploadManager.put(Bitmap2Bytes(bitmap), key, token,
					new UpCompletionHandler() {

						@Override
						public void complete(String key, ResponseInfo info,
								JSONObject response) {
							Log.e("complete", info.toString());

						}
					}, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Bitmap转byte[]
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}
