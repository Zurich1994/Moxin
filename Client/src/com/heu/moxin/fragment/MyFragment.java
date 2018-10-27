package com.heu.moxin.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.activity.setAboutActivity;
import com.heu.moxin.activity.setDetailActivity;
import com.heu.moxin.activity.setMsgActivity;
import com.heu.moxin.bean.User;
import com.heu.moxin.dao.MeDao;
import com.heu.moxin.dialog.LogoutDialog;
import com.heu.moxin.utils.LoadUserAvatar;
import com.heu.moxin.utils.LoadUserAvatar.ImageDownloadedCallBack;

public class MyFragment extends RefreshableFragement {
	public static final String TAG = "MyFragment";

	public MyFragment() throws SingletonException {
		super();
	}

	/*
	 * private Button person; private Button chat; private Button remind;
	 * private Button exit;
	 */
	private LinearLayout lid;
	private LinearLayout lremind;
	private LinearLayout labout;
	private LinearLayout lexit;
	private ImageView avatar;
	private TextView tv;
	private TextView mid;
	public static final int MEVIEW = 3;
	public static final int FRAGMENTSIZE = 8;
	public static final int DETAILVIEW = 4;
	public static final int EXITVIEW = 5;
	public static final int MSGVIEW = 6;
	public static final int ABOUTVIEW = 7;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_setting, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		oninit();
		lid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(), setDetailActivity.class);
				getActivity().startActivity(in);
			}
		});

		labout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(), setAboutActivity.class);
				startActivity(in);
			}
		});
		lremind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(getActivity(), setMsgActivity.class);
				startActivity(in);
			}
		});
		lexit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().startActivity(
						new Intent(getActivity(), LogoutDialog.class));
			}
		});
	}

	/*
	 * protected void showCustomDialog() { final Dialog dialog = new
	 * Dialog(getActivity().getApplicationContext());
	 * dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	 * dialog.setContentView(R.layout.logout_actionsheet); Button button_exit =
	 * (Button) dialog.findViewById(R.id.btn_exit); Button button_cancel =
	 * (Button) dialog.findViewById(R.id.btn_cancel);
	 * button_exit.setOnClickListener(new View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * EMChatManager.getInstance().logout(); Intent intent = new
	 * Intent(getActivity(), LoginActivity.class);
	 * getActivity().startActivity(intent); dialog.dismiss();
	 * getActivity().finish(); } }); button_cancel.setOnClickListener(new
	 * View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { dialog.dismiss(); } });
	 * dialog.show(); }
	 */

	public void oninit() {
		lid = (LinearLayout) getView().findViewById(R.id.ll_black_list);
		lremind = (LinearLayout) getView().findViewById(R.id.ll_black_remind);
		labout = (LinearLayout) getView().findViewById(R.id.ll_black_about);
		lexit = (LinearLayout) getView().findViewById(R.id.ll_black_exit);
		tv = (TextView) getView().findViewById(R.id.text);
		mid = (TextView) getView().findViewById(R.id.tv_user_nickname);
		MeDao medao = new MeDao(getActivity());
		User user = medao.getContact(MoxinApplication.getInstance()
				.getUserName());
		Log.e(TAG, "user : " + user);
		tv.setText(user.getUsernick());
		mid.setText(user.getMid());
		avatar = (ImageView) getView().findViewById(R.id.imag);
	}

	@Override
	public void onResume() {
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
				});
		super.onResume();
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}
}
