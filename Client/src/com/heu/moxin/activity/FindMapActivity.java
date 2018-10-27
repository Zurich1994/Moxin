package com.heu.moxin.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.heu.moxin.R;
import com.heu.moxin.bean.MapUser;

public class FindMapActivity extends FragmentActivity {

	protected static final String TAG = "FindMapActivity";
	private LocationMode mCurrentMode;
	private BitmapDescriptor mCurrentMarker;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private double latitude;
	private double longitude;
	private float radius;
	private List<Marker> mark = new ArrayList<Marker>();
	private List<BitmapDescriptor> BDescriptor = new ArrayList<BitmapDescriptor>();
	private List<MapUser> users = null;
	private LatLng latlng;
	// UI相关
	boolean isFirstLoc = true;// 是否首次定位
	private BitmapDescriptor bdm;
	private BitmapDescriptor bdf;
	private BitmapDescriptor bdn;
	private RelativeLayout mMarkerInfoLy;
	private MapUser mapUser;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_location);
		oninit();
		startMap();
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		List<MapUser> list = (List<MapUser>) b.get("list");
		int i;
		if (list != null) {
			users = list;
			for (i = 0; i < list.size(); i++) {
				if (list.get(i).getGender() == "male")
					BDescriptor.add(bdm);
				else if (list.get(i).getGender() == "female")
					BDescriptor.add(bdf);
				else
					BDescriptor.add(bdn);
			}
			for (i = 0; i < list.size(); i++) {
				latlng = new LatLng(list.get(i).getLatitude(), list.get(i)
						.getLongitude());
				mark.add(
						i,
						(Marker) (mBaiduMap.addOverlay(new MarkerOptions()
								.position(latlng).icon(BDescriptor.get(i))
								.zIndex(5))));
			}
		}
	}

	public void startMap() {
		SharedPreferences share = getApplication().getSharedPreferences(
				"baidulocate", Context.MODE_PRIVATE);
		if ("".equals(share.getString("lon", ""))
				|| "".equals(share.getString("lat", ""))) {
			return;
		} else {
			longitude = Double.parseDouble(share.getString("lon", null));
			latitude = Double.parseDouble(share.getString("lat", null));
			radius = share.getFloat("radius", 0);
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(radius)
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(latitude).longitude(longitude)
					.build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(latitude, longitude);
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLngZoom(ll, 18);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}

	public Bitmap CreatNewBitmap(String text) {
		Bitmap bmp = Bitmap.createBitmap(120, 150, Config.ARGB_4444);
		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(bmp, 0, 0, null);
		TextPaint textpaint = new TextPaint();
		textpaint.setAntiAlias(true);
		textpaint.setTextSize(16.0F);
		textpaint.setColor(Color.BLUE);
		StaticLayout layout = new StaticLayout(text, textpaint,
				bmp.getWidth() - 8, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
		canvas.translate(6, 40);
		layout.draw(canvas);
		return bmp;
	}

	public void showMark(BitmapDescriptor bitmap) {
		// 定义Maker坐标点
		LatLng point = new LatLng(latitude, longitude);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
	}

	public void oninit() {
		mCurrentMode = LocationMode.NORMAL;
		mark = new ArrayList<Marker>();
		bdm = BitmapDescriptorFactory.fromResource(R.drawable.blue_flag);
		bdf = BitmapDescriptorFactory.fromResource(R.drawable.pink_flag);
		bdn = BitmapDescriptorFactory.fromResource(R.drawable.green_flag);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		initMapClickEvent();
		initMarkerClickEvent();
	}

	private void initMapClickEvent() {
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				mMarkerInfoLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();

			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	private void initMarkerClickEvent() {
		// 对Marker的点击
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				// 获得marker中的数据
				MapUser user = null;
				for (int i = 0; i < mark.size(); i++) {
					if (mark.get(i).equals(marker)) {
						if (users != null) {
							user = users.get(i);
						}
					}
				}
				InfoWindow mInfoWindow = null;
				OnInfoWindowClickListener listener = null;
				// 生成一个TextView用户在地图中显示InfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setBackgroundResource(R.drawable.location_tips);
				location.setPadding(30, 20, 30, 50);
				location.setText(user.getName());
				location.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// v.setVisibility(v.GONE);

					}
				});
				// 将marker所在的经纬度的信息转化成屏幕上的坐标
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// 为弹出的InfoWindowlistener添加监听
				listener = new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						LatLng ll = marker.getPosition();
						LatLng llNew = new LatLng(ll.latitude + 0.005,
								ll.longitude + 0.005);
						marker.setPosition(llNew);
						mBaiduMap.hideInfoWindow();
					}
				};
				LatLng lll = marker.getPosition();
				mInfoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(location), lll, -47, listener);
				// 显示InfoWindow
				mBaiduMap.showInfoWindow(mInfoWindow);
				// 设置详细信息布局为可见
				mMarkerInfoLy.setVisibility(View.VISIBLE);
				// 根据商家信息为详细信息布局设置信息
				popupInfo(mMarkerInfoLy, user);
				Log.e("find", "map");
				return true;
			}
		});
	}

	/**
	 * 复用弹出面板mMarkerLy的控件
	 * 
	 * @author zhy
	 * 
	 */
	private static class ViewHolder {
		TextView tag1;
		TextView tag2;
		TextView tag3;
		TextView autograph;
		TextView city;
		TextView name;
		TextView dude;
		Button play;
	}

	/**
	 * 根据info为布局上的控件设置信息
	 * 
	 * @param mMarkerInfo2
	 * @param info
	 */
	protected void popupInfo(RelativeLayout mMarkerLy, MapUser user) {
		ViewHolder viewHolder = null;
		if (mMarkerLy.getTag() == null) {
			viewHolder = new ViewHolder();
			viewHolder.tag1 = (TextView) mMarkerLy.findViewById(R.id.text1);
			viewHolder.tag2 = (TextView) mMarkerLy.findViewById(R.id.text2);
			viewHolder.tag3 = (TextView) mMarkerLy.findViewById(R.id.text3);
			viewHolder.name = (TextView) mMarkerLy.findViewById(R.id.indv_name);
			viewHolder.autograph = (TextView) mMarkerLy
					.findViewById(R.id.text4);
			viewHolder.city = (TextView) mMarkerLy.findViewById(R.id.indv_city);
			viewHolder.dude = (TextView) mMarkerLy
					.findViewById(R.id.indv_friend);
			viewHolder.play = (Button) mMarkerLy.findViewById(R.id.play_game);
			viewHolder.play.setOnClickListener(button_listener);
			mMarkerLy.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) mMarkerLy.getTag();
		}
		viewHolder.name.setText(user.getName());
		mapUser = user;
		if (user.getAutograph() != null && user.getAutograph() != "")
			viewHolder.autograph.setText(user.getAutograph());
	}

	private Button.OnClickListener button_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			Log.e(TAG, "phone11 : " + mapUser.getPhone());
			intent.putExtra("username", mapUser.getName());
			intent.putExtra("phone", mapUser.getPhone());
			intent.setClass(FindMapActivity.this, GameActivity.class);
			startActivity(intent);
			finish();
		}

	};

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {

		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
		if (BDescriptor != null)
			for (int j = 0; j < BDescriptor.size(); j++)
				BDescriptor.get(j).recycle();
	}

}
