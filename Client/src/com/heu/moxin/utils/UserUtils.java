package com.heu.moxin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.bean.User;
import com.heu.moxin.utils.LoadUserAvatar.ImageDownloadedCallBack;

public class UserUtils {
	/**
	 * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
	 * 
	 * @param username
	 * @return
	 */
	public static User getUserInfo(String username) {
		User user = MoxinApplication.getInstance().getContactList()
				.get(username);
		if (user == null) {
			user = new User();
		}

		if (user != null) {
			// demo没有这些数据，临时填充
			user.setNick(username);
			// user.setAvatar("http://downloads.easemob.com/downloads/57.png");
		}
		return user;
	}

	/**
	 * 设置用户头像
	 * 
	 * @param username
	 */
	public static void setUserAvatar(Context context, String username,
			ImageView imageView) {
		final String fileName = "username=" + username + ".png";
		imageView.setTag(fileName);
		LoadUserAvatar.getInstance().loadImage(imageView, Constant.AVATAR_PATH,
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
	}

}
