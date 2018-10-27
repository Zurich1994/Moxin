package com.heu.moxin.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenUtil {
	/**
	 * ������Ļ�Ŀ�Ⱥ͸߶� return the width and height of screen
	 * 
	 * @param activity
	 * @return
	 */
	/*public static int[] getScreenWidthHeight(Activity activity) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return new int[] { displayMetrics.widthPixels,
				displayMetrics.heightPixels };
	}*/
	public static int[] getScreenWidthHeight(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return new int[] { displayMetrics.widthPixels,
				displayMetrics.heightPixels };
	}

	/**
	 * ����ֻ�ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dipToPx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * ����ֻ�ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
	 */
	public static int pxToDip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
