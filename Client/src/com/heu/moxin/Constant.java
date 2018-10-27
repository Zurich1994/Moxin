package com.heu.moxin;

/**
 * 此为全局常量类
 * 
 */
public class Constant {
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String ACCOUNT_REMOVED = "account_removed";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String URL_NearBy = "http://moxin.coding.io/servlet/NearByServlet";
	public static final String URL_Friend = "http://moxin.coding.io/servlet/QueryUsersByNameServlet";
	public static final String URL_Compatibility_Friend = "http://moxin.coding.io/servlet/QueryUsersByNameOrPhoneServlet";
	//public static final String URL_Upload_Avatar = "http://moxin.coding.io/servlet/UploadAvatarServlet?";
	//public static final String URL_Download_Avatar = "http://moxin.coding.io/servlet/DownloadAvatarServlet?";
	public static final String URL_Download_Avatar = "http://7xjww1.com1.z0.glb.clouddn.com/";
	public static final String URL_SIGNUP = "http://moxin.coding.io/servlet/SignUpServlet";
	public static final String URL_UpdateUserInfo = "http://moxin.coding.io/servlet/UpdateUserInfoServlet";
	public static final String URL_GET_TOKEN = "http://moxin.coding.io/servlet/TokenServlet";
	public static final String AVATAR_PATH = MoxinApplication.getInstance()
			.getFilesDir() + "/avatar/";
	public static final String[] GAMES_NAME = { "\"找你妹\"", "\"一个都不能死\"",
			"\"最强电灯泡\"", "\"看你有多色\"", "\"降温摇可乐\"" };
}
