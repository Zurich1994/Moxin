package com.heu.moxin.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.heu.moxin.Constant;
import com.heu.moxin.MoxinApplication;
import com.heu.moxin.R;
import com.heu.moxin.utils.AvatarHelper;
import com.heu.moxin.utils.FileUtil;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity {
	private EditText phoneEditText;// 手机
	private EditText userNameEditText;// 用户名
	private EditText passwordEditText;// 密码
	private EditText confirmPwdEditText;// 确认密码
	private ImageView switch_avatar;// 头像
	private boolean isSetAvatar;
	private EditText ageEditText;// 年龄
	private RadioGroup genderRadioGroup;
	private String gender;// 性别
	private String IMAGE_NAME = "temp.png";
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		userNameEditText = (EditText) findViewById(R.id.username);
		phoneEditText = (EditText) findViewById(R.id.telephone);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirmpassword);
		switch_avatar = (ImageView) findViewById(R.id.switch_avatar);
		genderRadioGroup = (RadioGroup) findViewById(R.id.gender);
		ageEditText = (EditText) findViewById(R.id.age);
		isSetAvatar = false;

		switch_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		gender = "female";
		genderRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.female:
							gender = "female";
							break;
						case R.id.male:
							gender = "male";
							break;
						default:
							gender = "unkown";
							break;
						}
					}
				});
	}

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (FileUtil.isExternalStorageWritable()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(getTempFile()));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		final String st1 = getResources().getString(
				R.string.User_name_cannot_be_empty);
		final String st12 = getResources().getString(R.string.Phone_cannot_be_empty);
		final String st2 = getResources().getString(
				R.string.Password_cannot_be_empty);
		final String st3 = getResources().getString(
				R.string.Confirm_password_cannot_be_empty);
		final String st4 = getResources()
				.getString(R.string.Two_input_password);
		final String st5 = getResources().getString(R.string.Is_the_registered);
		final String st6 = getResources().getString(
				R.string.Registered_successfully);
		final String st11 = getResources().getString(R.string.null_age);
		final String username = userNameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString().trim();
		final String phone = phoneEditText.getText().toString().trim();
		String age = ageEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, st12, Toast.LENGTH_SHORT).show();
			phoneEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, st1, Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, st2, Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, st3, Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, st4, Toast.LENGTH_SHORT).show();
			return;
		} else if (!TextUtils.isDigitsOnly(age)) {
			Toast.makeText(this, st11, Toast.LENGTH_SHORT).show();
			return;
		} else if (!isSetAvatar) {
			Toast.makeText(this, "请设置头像", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage(st5);
			pd.setCanceledOnTouchOutside(false);
			pd.show();
			final String st7 = getResources().getString(
					R.string.network_anomalies);
			final String st8 = getResources().getString(
					R.string.User_already_exists);
			final String st9 = getResources().getString(
					R.string.registration_failed_without_permission);
			final String st10 = getResources().getString(
					R.string.Registration_failed);
			new Thread(new Runnable() {
				public void run() {
					try {

						// 调用服务器注册方法
						HttpClient client = new DefaultHttpClient();
						HttpPost request = new HttpPost(URI
								.create(Constant.URL_SIGNUP));
						List<NameValuePair> list = new ArrayList<NameValuePair>();
						list.add(new BasicNameValuePair("phone", phone));
						list.add(new BasicNameValuePair("nickname", username));
						list.add(new BasicNameValuePair("password", pwd));
						list.add(new BasicNameValuePair("gender", gender));
						list.add(new BasicNameValuePair("hobbies",
								"hobby1,hobby2"));
						list.add(new BasicNameValuePair("avatar", "username="
								+ phone + ".png"));
						UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(
								list, "UTF-8");
						request.setEntity(requestEntity);
						HttpResponse resp = client.execute(request);
						HttpEntity resEntity = resp.getEntity();
						String result = new String(EntityUtils
								.toByteArray(resEntity), "UTF-8");
						Log.e("signup", result);
						if (!result.startsWith("注册成功")) {
							runOnUiThread(new Runnable() {
								public void run() {
									if (!RegisterActivity.this.isFinishing())
										pd.dismiss();
									Toast.makeText(getApplicationContext(),
											st8, Toast.LENGTH_SHORT).show();
								}
							});
						} else {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (!RegisterActivity.this.isFinishing())
										pd.setMessage("上传头像");
								}
							});
							// 上传头像
							Bitmap photo = null;
							try {
								photo = BitmapFactory.decodeStream(getContentResolver()
										.openInputStream(
												Uri.fromFile(getTempFile())));
								AvatarHelper.upload(photo, phone);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							runOnUiThread(new Runnable() {
								public void run() {
									if (!RegisterActivity.this.isFinishing())
										pd.dismiss();
									// 保存用户名
									MoxinApplication.getInstance().setUserName(
											phone);
									Toast.makeText(getApplicationContext(),
											st6, Toast.LENGTH_SHORT).show();
									finish();
								}
							});
						}
					} catch (UnsupportedEncodingException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								Toast.makeText(getApplicationContext(), st7,
										Toast.LENGTH_SHORT).show();
							}

						});
					} catch (ClientProtocolException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								Toast.makeText(getApplicationContext(), st7,
										Toast.LENGTH_SHORT).show();
							}

						});
					} catch (IOException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								Toast.makeText(getApplicationContext(), st7,
										Toast.LENGTH_SHORT).show();
							}

						});
					}
				}
			}).start();

		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 480);
		intent.putExtra("outputY", 480);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile()));
		intent.putExtra("noFaceDetection", true); // no face detection
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (FileUtil.isExternalStorageWritable()) {
					startPhotoZoom(Uri.fromFile(getTempFile()));
				} else {
					Toast.makeText(RegisterActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
					isSetAvatar = true;
				} else {
					Log.e(TAG, "Avatardatanull!");
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private File getTempFile() {
		if (FileUtil.isExternalStorageWritable()) {
			File f = new File(Environment.getExternalStorageDirectory(),
					IMAGE_NAME);
			return f;
		}
		return null;
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = null;
			try {
				photo = BitmapFactory.decodeStream(getContentResolver()
						.openInputStream(Uri.fromFile(getTempFile())));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			switch_avatar.setImageBitmap(photo);
		}
	}

	public void back(View view) {
		finish();
	}

}
