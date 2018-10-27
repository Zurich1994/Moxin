package com.heu.moxin.utils;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片异步加载类
 * 
 * @author Leslie.Fang
 * 
 */
public class LoadUserAvatar {
	private final static int OK = 111;
	private final static String TAG = "LoadUserAvatar";
	// 一级内存缓存基于 LruCache
	private BitmapCache bitmapCache;
	// 二级文件缓存
	private FileUtil fileUtil;
	// 线程池
	private ExecutorService threadPools = null;

	private LoadUserAvatar() {
		threadPools = Executors.newCachedThreadPool();
		bitmapCache = new BitmapCache();
	}

	private static class LoadUserAvatarHolder {
		private static final LoadUserAvatar INSTANCE = new LoadUserAvatar();

	}

	public static LoadUserAvatar getInstance() {
		return LoadUserAvatarHolder.INSTANCE;
	}

	static class MyHandler extends Handler {
		WeakReference<LoadUserAvatar> wr_la;

		public MyHandler(LoadUserAvatar la) {
			wr_la = new WeakReference<LoadUserAvatar>(la);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == OK && wr_la.get().imageDownloadedCallBack != null) {
				Bitmap bitmap = (Bitmap) msg.obj;
				wr_la.get().imageDownloadedCallBack.onImageDownloaded(
						wr_la.get().imageView, bitmap);
			}
		}
	}

	private ImageDownloadedCallBack imageDownloadedCallBack;
	private ImageView imageView;

	/**
	 * 
	 * @param imageView
	 * @param local_image_path
	 * @param imageUrl
	 * @param imageDownloadedCallBack
	 * @return Bitmap
	 */
	public Bitmap loadImage(final ImageView imageView,
			final String local_image_path, final String imageUrl,
			final ImageDownloadedCallBack imageDownloadedCallBack) {
		fileUtil = new FileUtil(local_image_path);
		final String filename = imageUrl
				.substring(imageUrl.lastIndexOf("/") + 1);
		final String filepath = fileUtil.getAbsolutePath() + filename;
		Log.e(TAG, filepath);
		// 先从内存中拿
		Bitmap bitmap = bitmapCache.getBitmap(imageUrl);

		if (bitmap != null) {
			Log.i(TAG, "image exists in memory");
			imageDownloadedCallBack.onImageDownloaded(imageView, bitmap);
		} else if (fileUtil.isBitmapExists(filename)) {// 从文件中找
			Log.i(TAG, "image exists in file" + filename);
			bitmap = BitmapFactory.decodeFile(filepath);
			// 先缓存到内存
			bitmapCache.putBitmap(imageUrl, bitmap);
			imageDownloadedCallBack.onImageDownloaded(imageView, bitmap);
		} else if (imageUrl != null && !imageUrl.equals("")) {// 内存和文件中都没有再从网络下载
			this.imageDownloadedCallBack = imageDownloadedCallBack;
			this.imageView = imageView;
			final Handler handler = new MyHandler(this);
			Runnable thread = new Runnable() {
				@Override
				public void run() {
					String url = imageUrl.replace("username=", "img/avatar/");
					Log.i(TAG, Thread.currentThread().getName() + " is running, url = " + url);
					//username=phone.png --> img/avatar/phone.png
					InputStream inputStream = HTTPService.getInstance()
							.getStream(url);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = false;
					options.inSampleSize = 5; // width，hight设为原来的十分一
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null, options);
					// 图片下载成功后缓存并执行回调刷新界面
					if (bitmap != null) {

						// 先缓存到内存
						bitmapCache.putBitmap(imageUrl, bitmap);
						// 缓存到文件系统
						fileUtil.saveBitmap(filename, bitmap);

						Message msg = new Message();
						msg.what = OK;
						msg.obj = bitmap;
						handler.sendMessage(msg);

					}
				}
			};
			threadPools.execute(thread);
		}
		return bitmap;
	}
	/**
	 * 
	 * @param imageView
	 * @param local_image_path
	 * @param imageUrl
	 * @param imageDownloadedCallBack
	 * @return Bitmap
	 */
	public void loadImage(final ImageView imageView,
			final String local_image_path, final String imageUrl, final int size,
			final ImageDownloadedCallBack imageDownloadedCallBack) {
		fileUtil = new FileUtil(local_image_path);
		final String filename = imageUrl
				.substring(imageUrl.lastIndexOf("/") + 1);
		final String filepath = fileUtil.getAbsolutePath() + filename;
		Log.e(TAG, filepath);
		// 先从内存中拿
		Bitmap bitmap = bitmapCache.getBitmap(imageUrl);

		if (bitmap != null) {
			Log.i(TAG, "image exists in memory");
			imageDownloadedCallBack.onImageDownloaded(imageView, bitmap);
		} else if (fileUtil.isBitmapExists(filename)) {// 从文件中找
			Log.i(TAG, "image exists in file" + filename);
			bitmap = BitmapFactory.decodeFile(filepath);
			// 先缓存到内存
			bitmapCache.putBitmap(imageUrl, bitmap);
			imageDownloadedCallBack.onImageDownloaded(imageView, bitmap);
		} else if (imageUrl != null && !imageUrl.equals("")) {// 内存和文件中都没有再从网络下载
			this.imageDownloadedCallBack = imageDownloadedCallBack;
			this.imageView = imageView;
			final Handler handler = new MyHandler(this);
			Runnable thread = new Runnable() {
				@Override
				public void run() {
					String url = imageUrl.replace("username=", "img/avatar/");
					Log.i(TAG, Thread.currentThread().getName() + " is running, url = " + url);
					//username=phone.png --> img/avatar/phone.png
					InputStream inputStream = HTTPService.getInstance()
							.getStream(url);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = false;
					options.inSampleSize = 1; // width，hight设为原来的十分一
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null, options);
					// 图片下载成功后缓存并执行回调刷新界面
					if (bitmap != null) {

						// 先缓存到内存
						bitmapCache.putBitmap(imageUrl, bitmap);
						// 缓存到文件系统
						fileUtil.saveBitmap(filename, bitmap);

						Message msg = new Message();
						msg.what = OK;
						msg.obj = bitmap;
						handler.sendMessage(msg);

					}
				}
			};
			threadPools.execute(thread);
		}
	}
	/**
	 * 图片下载完成回调接口
	 * 
	 */
	public interface ImageDownloadedCallBack {
		void onImageDownloaded(ImageView imageView, Bitmap bitmap);
	}

}
