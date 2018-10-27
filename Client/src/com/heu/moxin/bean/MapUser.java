package com.heu.moxin.bean;

import java.io.Serializable;
import java.util.List;

public class MapUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double longitude;
	private String name;//nicheng
	private String id;
	private double latitude;
	private String gender;
	private List<String> label;
	private String autograph;
	private String phone;
	private int friend;
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getFriend() {
		return friend;
	}

	public void setFriend(int friend) {
		this.friend = friend;
	}

	public List<String> getLabel() {
		return label;
	}

	public void setLabel(List<String> label) {
		this.label = label;
	}

	public String getAutograph() {
		return autograph;
	}

	public void setAutograph(String autograph) {
		this.autograph = autograph;
	}


	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = Double.valueOf(latitude).doubleValue();
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = Double.valueOf(longitude).doubleValue();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
