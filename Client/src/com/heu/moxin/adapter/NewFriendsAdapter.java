package com.heu.moxin.adapter;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.heu.moxin.Constant;
import com.heu.moxin.R;
import com.heu.moxin.bean.InviteMessage;
import com.heu.moxin.bean.InviteMessage.InviteMesageStatus;
import com.heu.moxin.dao.InviteMessageDao;
import com.heu.moxin.task.ContactFriendLoader;
import com.heu.moxin.task.FriendLoader;
import com.heu.moxin.utils.LoadUserAvatar;
import com.heu.moxin.utils.LoadUserAvatar.ImageDownloadedCallBack;

public class NewFriendsAdapter extends BaseAdapter {
	Context context;
	List<InviteMessage> msgs;
	private InviteMessageDao messageDao;
	int total = 0;
	private LoadUserAvatar avatarLoader;

	public NewFriendsAdapter(Context context, List<InviteMessage> msgs) {
		this.context = context;
		this.msgs = msgs;
		messageDao = new InviteMessageDao(context);
		avatarLoader = LoadUserAvatar.getInstance();
		total = msgs.size();
	}

	public void setData(List<InviteMessage> msgs) {
		this.msgs = msgs;
	}

	@Override
	public int getCount() {
		return msgs.size();
	}

	@Override
	public InviteMessage getItem(int position) {
		return msgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_newfriendsmsag,
					null);
			holder = new ViewHolder();
			holder.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_avatar);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_reason = (TextView) convertView
					.findViewById(R.id.tv_reason);
			holder.tv_added = (TextView) convertView
					.findViewById(R.id.tv_added);
			holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
			final InviteMessage msg = getItem(total - 1 - position);
			// int msg_id = msg.getId();
			// String userUid = msg.getFrom();
			Log.e("msg", msg.toString());
			if (msg != null) {
				String reason_total = msg.getReason();// nullpointerException
				String[] sourceStrArray = reason_total.split("66split88");
				// 先附初值
				String name = msg.getFrom();
				String avatar = "username=" + msg.getFrom() + ".png";
				String reason = "请求加好友";
				if (sourceStrArray.length == 4) {
					name = sourceStrArray[0];
					avatar = sourceStrArray[1];
					reason = sourceStrArray[3];
				}
				showUserAvatar(holder.iv_avatar, avatar);
				holder.tv_name.setText(name);
				holder.tv_reason.setText(reason);
			}
			if (msg.getStatus() == InviteMesageStatus.AGREED
					|| msg.getStatus() == InviteMesageStatus.BEAGREED) {

				holder.tv_added.setVisibility(View.VISIBLE);
				holder.btn_add.setVisibility(View.GONE);
			} else {
				holder.tv_added.setVisibility(View.GONE);
				holder.btn_add.setVisibility(View.VISIBLE);
				holder.btn_add.setTag(msg);
				holder.btn_add.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						acceptInvitation(holder.btn_add, msg, holder.tv_added);
					}

				});
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}

	private static class ViewHolder {
		ImageView iv_avatar;
		TextView tv_name;
		TextView tv_reason;
		TextView tv_added;
		Button btn_add;

	}

	private void showUserAvatar(ImageView imageView, String avatar) {
		if (avatar == null || avatar.equals("")) {
			return;
		}
		final String url_avatar = Constant.URL_Download_Avatar + avatar;
		imageView.setTag(url_avatar);
		if (url_avatar != null && !url_avatar.equals("")) {
			avatarLoader.loadImage(imageView, Constant.AVATAR_PATH, url_avatar,
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
	 * 同意好友请求或者群申请
	 * 
	 * @param button
	 * @param username
	 */
	private void acceptInvitation(final Button button, final InviteMessage msg,
			final TextView textview) {
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setMessage("正在同意...");
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				// 调用sdk的同意方法
				try {
					if (msg.getGroupId() == null) // 同意好友请求
						EMChatManager.getInstance().acceptInvitation(
								msg.getFrom());
					else
						// 同意加群申请
						EMGroupManager.getInstance().acceptApplication(
								msg.getFrom(), msg.getGroupId());
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							textview.setVisibility(View.VISIBLE);
							button.setEnabled(false);
							button.setVisibility(View.GONE);
							msg.setStatus(InviteMesageStatus.AGREED);
							// 更新db
							ContentValues values = new ContentValues();
							values.put(InviteMessageDao.COLUMN_NAME_STATUS, msg
									.getStatus().ordinal());
							messageDao.updateMessage(msg.getId(), values);

							// 巩固程序,即时将该好友存入好友列表
							
							addFriendToList(msg.getFrom());

						}
					});
				} catch (final Exception e) {
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(context, "同意失败: " + e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					});

				}
			}
		}).start();
	}

	private void addFriendToList(final String name) {
		FriendLoader fl = new ContactFriendLoader(name, context, null);
		new Thread(fl).start();
	}

}
