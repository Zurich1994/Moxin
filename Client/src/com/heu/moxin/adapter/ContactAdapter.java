/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heu.moxin.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easemob.util.EMLog;
import com.heu.moxin.Constant;
import com.heu.moxin.R;
import com.heu.moxin.bean.User;
import com.heu.moxin.utils.LoadUserAvatar;
import com.heu.moxin.utils.LoadUserAvatar.ImageDownloadedCallBack;

/**
 * 简单的好友Adapter实现
 *
 */
public class ContactAdapter extends ArrayAdapter<User> implements
		SectionIndexer {
	private static final String TAG = "ContactAdapter";
	List<String> list;
	List<User> userList;
	List<User> copyUserList;
	private LayoutInflater layoutInflater;
	private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;
	private int res;
	private MyFilter myFilter;
	private boolean notiyfyByFilter;

	public ContactAdapter(Context context, int resource, List<User> objects) {
		super(context, resource, objects);
		this.res = resource;
		this.userList = objects;
		copyUserList = new ArrayList<User>();
		copyUserList.addAll(objects);
		layoutInflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {
		ImageView iv_avatar;// 头像
		TextView nameTextview;// 名字
		TextView tvHeader;// 拼音头
		View view_temp;// 线

		@Override
		public String toString() {
			return "ViewHolder [iv_avatar=" + iv_avatar + ", nameTextview="
					+ nameTextview + ", tvHeader=" + tvHeader + ", view_temp="
					+ view_temp + "]";
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(res, null);
			holder.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_avatar);
			holder.nameTextview = (TextView) convertView
					.findViewById(R.id.tv_name);
			holder.tvHeader = (TextView) convertView.findViewById(R.id.header);
			holder.view_temp = convertView.findViewById(R.id.view_temp);
			convertView.setTag(holder);
			Log.e(TAG, "holdern : " + holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			Log.e(TAG, "holderh : " + holder);
		}

		User user = getItem(position);
		if (user == null)
			Log.d("ContactAdapter", position + "");
		// 设置nick，demo里不涉及到完整user，用username代替nick显示
		String nickname = user.getUsernick();
		String phone = user.getPhone();
		String header = user.getHeader();
		String useravatar = "username=" + phone + ".png";
		if (position == 0 || header != null
				&& !header.equals(getItem(position - 1).getHeader())) {
			if ("".equals(header)) {
				holder.tvHeader.setVisibility(View.GONE);
				if (holder.view_temp != null) {
					holder.view_temp.setVisibility(View.GONE);
				}
			} else {
				holder.tvHeader.setVisibility(View.VISIBLE);
				holder.tvHeader.setText(header);
				if (holder.view_temp != null)
					holder.view_temp.setVisibility(View.VISIBLE);
			}
		} else {
			holder.tvHeader.setVisibility(View.GONE);
			if (holder.view_temp != null)
				holder.view_temp.setVisibility(View.GONE);
		}

		// 显示申请与通知item
		Log.e(TAG, "user : " + user);
		holder.nameTextview.setText(nickname);
		// 设置用户头像
		holder.iv_avatar.setImageResource(R.drawable.default_useravatar);
		showUserAvatar(holder.iv_avatar, useravatar);
		return convertView;
	}

	@Override
	public User getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	public int getPositionForSection(int section) {
		return positionOfSection.get(section);
	}

	public int getSectionForPosition(int position) {
		return sectionOfPosition.get(position);
	}

	@Override
	public Object[] getSections() {
		positionOfSection = new SparseIntArray();
		sectionOfPosition = new SparseIntArray();
		int count = getCount();
		list = new ArrayList<String>();
		list.add(getContext().getString(R.string.search_header));
		positionOfSection.put(0, 0);
		sectionOfPosition.put(0, 0);
		for (int i = 0; i < count; i++) {

			String letter = getItem(i).getHeader();
			System.err.println("contactadapter getsection getHeader:" + letter
					+ " name:" + getItem(i).getUsernick());
			int section = list.size() - 1;
			if (list.get(section) != null && !list.get(section).equals(letter)) {
				list.add(letter);
				section++;
				positionOfSection.put(section, i + 1);
			}
			sectionOfPosition.put(i + 1, section);
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public Filter getFilter() {
		if (myFilter == null) {
			myFilter = new MyFilter(userList);
		}
		return myFilter;
	}

	private class MyFilter extends Filter {
		List<User> mOriginalList = null;

		public MyFilter(List<User> myList) {
			this.mOriginalList = myList;
		}

		@Override
		protected synchronized FilterResults performFiltering(
				CharSequence prefix) {
			FilterResults results = new FilterResults();
			if (mOriginalList == null) {
				mOriginalList = new ArrayList<User>();
			}
			EMLog.d(TAG, "contacts original size: " + mOriginalList.size());
			EMLog.d(TAG, "contacts copy size: " + copyUserList.size());

			if (prefix == null || prefix.length() == 0) {
				results.values = copyUserList;
				results.count = copyUserList.size();
			} else {
				String prefixString = prefix.toString();
				final int count = mOriginalList.size();
				final ArrayList<User> newValues = new ArrayList<User>();
				for (int i = 0; i < count; i++) {
					final User user = mOriginalList.get(i);
					String username = user.getUsernick();
					if (username.startsWith(prefixString)) {
						newValues.add(user);
					} else {
						final String[] words = username.split(" ");
						final int wordCount = words.length;

						// Start at index 0, in case valueText starts with
						// space(s)
						for (int k = 0; k < wordCount; k++) {
							if (words[k].startsWith(prefixString)) {
								newValues.add(user);
								break;
							}
						}
					}
				}
				results.values = newValues;
				results.count = newValues.size();
			}
			EMLog.d(TAG, "contacts filter results size: " + results.count);
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected synchronized void publishResults(CharSequence constraint,
				FilterResults results) {
			userList.clear();
			userList.addAll((List<User>) results.values);
			EMLog.d(TAG, "publish contacts filter results size: "
					+ results.count);
			if (results.count > 0) {
				notiyfyByFilter = true;
				notifyDataSetChanged();
				notiyfyByFilter = false;
			} else {
				notifyDataSetInvalidated();
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (!notiyfyByFilter) {
			copyUserList.clear();
			copyUserList.addAll(userList);
		}
	}

	protected Bitmap showUserAvatar(ImageView imageView, String avatar) {
		Bitmap bitmap = null;
		if (avatar == null || avatar.equals(""))
			return null;
		final String url_avatar = Constant.URL_Download_Avatar + avatar;
		Log.e(TAG, url_avatar);
		imageView.setTag(url_avatar);
		if (url_avatar != null && !url_avatar.equals("")) {
			 bitmap = LoadUserAvatar.getInstance().loadImage(imageView,
					Constant.AVATAR_PATH, url_avatar,
					new ImageDownloadedCallBack() {

						@Override
						public void onImageDownloaded(ImageView imageView,
								Bitmap bitmap) {
							if (imageView.getTag() == url_avatar) {
								imageView.setImageBitmap(bitmap);
								notifyDataSetChanged();
							}
						}

					});

		}
		return bitmap;
	}
}
