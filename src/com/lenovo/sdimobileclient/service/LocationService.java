package com.lenovo.sdimobileclient.service;

import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.foreveross.cache.CacheManager;
import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.Netpath;
import com.lenovo.sdimobileclient.LenovoServicesApplication;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.api.SystemNotification;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.network.UrlAttr;
import com.lenovo.sdimobileclient.ui.ActivityUtils;
import com.lenovo.sdimobileclient.ui.OrderActivity;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;

/**
 * 定位服务，及坐标上传
 * 
 * @author zhangshaofang
 * 
 */
public class LocationService extends Service implements Callback {
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MMLocationListener();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LenovoServicesApplication application = (LenovoServicesApplication) getApplication();

		mHttpHelper = OkHttpHelper.getInstance(this);

		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数

		initLocation();
	}

	private void initLocation() {

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000 * 60 * 5;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
		mLocationClient.start();

	}

	@Override
	public void onDestroy() {
		if (mLocationClient != null)
			mLocationClient.stop();
		super.onDestroy();
	}

	class MMLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(LocationService.this);

			long currentTimeMillis = System.currentTimeMillis();

			String time = Utils.getTime(currentTimeMillis);

			int compareTo = time.compareTo("08:00");
			int compareTo2 = time.compareTo("20:00");

			int locType = location.getLocType();

			LenovoServicesApplication application = (LenovoServicesApplication) getApplication();
			String lo = location.getLatitude() + "," + location.getLongitude();
			if (TextUtils.equals("4.9E-324,4.9E-324", lo)) {
				lo = lo + "locType" + locType;
			}
			application.setCoordinate(location);
			application.setCity(location.getCity());
			application.setLocation(lo);
			application.setAddrstr(location.getAddrStr());
			if (location != null && enginnerInfo != null && compareTo > 0 && compareTo2 < 0) {

				Uri uri = Uri.parse(UrlAttr.URL_UPLOADLOCATION).buildUpon().appendQueryParameter(UrlAttr.PARAM_LOCATION, lo)
						.appendQueryParameter(UrlAttr.PARAM_ENGINEER, enginnerInfo.EngineerNumber).build();

				mHttpHelper.load(uri.toString(), mCallback, LocationService.this);

				// NetworkPath path = new Netpath(uri.toString());
				// CacheParams params = new
				// CacheParams(CacheParams.PRIORITY_NORMAL, path,
				// CacheParams.PRIORITY_LOWEST);
				// mCacheManager.load(0, params, LocationService.this);

			}
		}

	}

	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {
			// if (notifications != null) {
			// for (SystemNotification notification : notifications) {
			// AlarmAlert alarmAlert = new AlarmAlert(110, 0, 0,
			// notification.Notification, "", "");
			// alarmAlert.insert(getApplicationContext());
			// }
			// }
		}
	};
	private CacheManager mCacheManager;
	private OkHttpHelper mHttpHelper;

	@Override
	public void dataLoaded(int arg0, CacheParams arg1, ICacheInfo arg2) {

	}

}
