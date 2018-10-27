package com.heu.moxin.bean;

import android.annotation.SuppressLint;

import com.easemob.chat.EMContact;
import com.easemob.util.HanziToPinyin;
import com.heu.moxin.Constant;

public class User extends EMContact {

	@Override
	public String toString() {
		return "User [unreadMsgCount=" + unreadMsgCount + ", header=" + header
				+ ", age=" + age + ", usernick=" + usernick + ", sex=" + sex
				+ ", phone=" + phone + ", mid=" + mid + ", region=" + region
				+ ", avatar=" + avatar + ", sign=" + sign + ", beizhu="
				+ beizhu + "]";
	}

	private int unreadMsgCount;// 未读消息数
	private String header;
	private String age; // 年龄
	private String usernick;// 昵称
	private String sex;// 性别
	private String phone;// 电话 登录名
	private String mid;// 陌信id
	private String region;// 地区
	private String avatar;// 头像
	private String sign;// 签名
	private String beizhu;// 备注

	public String getHeader() {
		return header;
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	@SuppressLint("DefaultLocale")
	public void setUserHearder(String username) {
		String headerName = null;
		headerName = username;
		headerName = headerName.trim();
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			header = "";
		} else if (Character.isDigit(headerName.charAt(0))) {
			header = "#";
		} else {
			setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char head = getHeader().toLowerCase().charAt(0);
			if (head < 'a' || head > 'z') {
				header = "#";
			}
		}
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getUsernick() {
		return usernick;
	}

	public void setUsernick(String usernick) {
		this.usernick = usernick;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		return getUsername().equals(((User) o).getUsername());
	}

}
