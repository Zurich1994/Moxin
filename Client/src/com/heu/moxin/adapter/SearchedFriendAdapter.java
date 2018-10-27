package com.heu.moxin.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.bean.User;
import com.heu.moxin.dialog.AlertDialog;
import com.heu.moxin.utils.LoadUserAvatar;
import com.heu.moxin.utils.LoadUserAvatar.ImageDownloadedCallBack;

public class SearchedFriendAdapter extends BaseAdapter {
	private List<User> list;
	private LayoutInflater inflater;
	private Context context;
	private ProgressDialog progressDialog;

	public SearchedFriendAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		progressDialog = new ProgressDialog(context);
		list = new ArrayList<User>();
	}

	public void setList(List<User> list) {
		this.list.clear();
		this.list.addAll(list);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public User getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		ImageView imageView;
		TextView textView;
		Button button;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_search_friend, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.avatar);
			holder.button = (Button) convertView.findViewById(R.id.indicator);
			holder.textView = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final User user = getItem(position);
		holder.textView.setText(user.getUsernick());
		holder.button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addContact(user, "");
				// 待实现
			}
		});
		showUserAvatar(holder.imageView, user.getAvatar());
		return convertView;
	}

	/**
	 * 加载头像
	 * 
	 * @param imageView
	 * @param avatar
	 */
	private static void showUserAvatar(ImageView imageView, String avatar) {
		if (avatar == null || avatar.equals(""))
			return;
		final String url_avatar = Constant.URL_Download_Avatar + avatar;
		imageView.setTag(url_avatar);
		if (url_avatar != null && !url_avatar.equals("")) {
			LoadUserAvatar.getInstance().loadImage(imageView,
					Constant.AVATAR_PATH, url_avatar,
					new ImageDownloadedCallBack() {
						@Override
						public void onImageDownloaded(ImageView imageView,
								Bitmap bitmap) {
							if (imageView.getTag() == url_avatar) {
								imageView.setImageBitmap(bitmap);
							}
						}

					});
		}
	}

	/**
	 * 添加contact
	 * 
	 * @param user
	 */
	public void addContact(final User user, final String myreason) {
		if (MoxinApplication.getInstance().getUserName()
				.equals(user.getUsername())) {
			String str = context.getResources().getString(
					R.string.not_add_myself);
			context.startActivity(new Intent(context, AlertDialog.class)
					.putExtra("msg", str));
			return;
		}

		if (MoxinApplication.getInstance().getContactList()
				.containsKey(user.getUsername())) {
			String strin = context.getResources().getString(
					R.string.This_user_is_already_your_friend);
			context.startActivity(new Intent(context, AlertDialog.class)
					.putExtra("msg", strin));
			return;
		}

		String stri = context.getResources().getString(
				R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {

				try {
					// demo写死了个reason，实际应该让用户手动填入
					String s = context.getResources().getString(
							R.string.Add_a_friend);

					// 在reason封装请求者的昵称/头像/时间等信息，在通知中显示
					String name = user.getUsernick();
					String avatar = user.getAvatar();
					long time = System.currentTimeMillis();
					String myreason_temp = myreason;
					if (myreason == null || myreason.equals("")) {
						myreason_temp = s;
					}
					String reason = name + "66split88" + avatar + "66split88"
							+ String.valueOf(time) + "66split88"
							+ myreason_temp;

					EMContactManager.getInstance().addContact(
							user.getPhone(), reason);
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = context.getResources().getString(
									R.string.send_successful);
							Toast.makeText(context, s1, Toast.LENGTH_SHORT)
									.show();
						}
					});
				} catch (final Exception e) {
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = context.getResources().getString(
									R.string.Request_add_buddy_failure);
							Toast.makeText(context, s2 + e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}
}
