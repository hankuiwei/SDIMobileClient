package com.lenovo.sdimobileclient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.foreveross.cache.CacheManager;
import com.foreveross.cache.utility.ImageCacheManager;
import com.lenovo.sdimobileclient.network.SimpleConfiguration;
import com.lenovo.sdimobileclient.service.LocationService;
import com.lenovo.sdimobileclient.utility.CrashHandler;
import com.lenovo.sdimobileclient.utility.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import okhttp3.OkHttpClient;

/**
 * @ClassName: ForossApplication
 * @author ZhangShaofang
 * @Description: TODO
 */
public class LenovoServicesApplication extends Application implements Constants {
	/**
	 * 缓存管理
	 */
	private CacheManager mCacheManager;
	/**
	 * 图片缓存管理
	 * 
	 * private ImageCacheManager mImageCacheManager;
	 */
	/**
	 * Activity 堆栈
	 */
	private List<Activity> mActivities;
	/**
	 * 百度地图key
	 */
	private static Context mContext;
	private List<PendingIntent> mAlarmPendingList;
	private String mLocation;
	private String mAddrStr;
	private BDLocation mCoordinate;
	private String mCity;

	@Override
	public void onCreate() {
		super.onCreate();
		initEngineManager(this);
		initTip();
		mContext = this;
		mActivities = new ArrayList<Activity>();
		this.startService();

		CrashHandler.getInstance().init(this);

	}

	public void setLocation(String location) {
		mLocation = location;
	}

	public void setCoordinate(BDLocation location) {
		mCoordinate = location;
	}

	public void setCity(String lo) {
		mCity = lo;
	}

	public String getCity() {
		return mCity;

	}

	public void setAddrstr(String location) {
		mAddrStr = location;
	}

	public String getLocation() {
		return mLocation;
	}

	public BDLocation getCoordinate() {
		return mCoordinate;
	}

	public String getAddrStr() {
		return mAddrStr;
	}

	private void initTip() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int tipSpit = preferences.getInt(PREF_TIP_SPIT, 0);
		int bell = preferences.getInt(PREF_TIP_BELL, 0);
		int shock = preferences.getInt(PREF_TIP_SHOCK, 0);
		if (tipSpit == 0)
			Utils.saveTipSpit(this, 15);
		if (bell == 0 && shock == 0)
			Utils.saveTipFunction(this, 1, 1);
	}

	public void addAlarmPendingIntent(PendingIntent pendingIntent) {
		if (mAlarmPendingList == null) {
			mAlarmPendingList = new ArrayList<PendingIntent>();
		}
		mAlarmPendingList.add(pendingIntent);
	}

	public List<PendingIntent> getAlarmPendingIntent() {
		return mAlarmPendingList;
	}

	public List<Activity> getActivities() {
		return mActivities;
	}

	/**
	 * 绑定定位服务
	 */
	public void startService() {
		Intent intent = new Intent(this, LocationService.class);
		getApplicationContext().bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
	}

	/**
	 * 解除绑定，停止服务
	 */
	public void stopService() {
		unbindService(mConnection);
	}

	/**
	 * 绑定服务，服务停止之后，回调监听
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			startService();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};

	/**
	 * 初始化百度地图
	 * 
	 * @param context
	 */
	public void initEngineManager(Context context) {
		// if (mBMapManager == null) {
		SDKInitializer.initialize(getApplicationContext());// 因此我们建议该方法放在Application的初始化方法中
		//// }
		// if (!mBMapManager.init(MAP_KEY, new MyGeneralListener())) {
		// Toast.makeText(this, "BMapManager 初始化错误!", Toast.LENGTH_LONG).show();
		// }
	}

	/**
	 * 常用事件监听，用来处理通常的网络错误，授权验证错误等
	 * 
	 * @author zhangshaofang
	 * 
	 */
	// public static class MyGeneralListener implements MKGeneralListener {
	//
	// @Override
	// public void onGetNetworkState(int iError) {
	// if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
	// Toast.makeText(mContext, "您的网络出错啦！", Toast.LENGTH_LONG).show();
	// } else if (iError == MKEvent.ERROR_NETWORK_DATA) {
	// Toast.makeText(mContext, "输入正确的检索条件！", Toast.LENGTH_LONG).show();
	// }
	// // ...
	// }
	//
	// @Override
	// public void onGetPermissionState(int iError) {
	// if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
	// // 授权Key错误：
	// Toast.makeText(mContext, "Key验证失败", Toast.LENGTH_LONG).show();
	// }
	// }
	// }

	/**
	 * 初始化缓存管理者
	 * 
	 * @return
	 */
	public CacheManager getCacheManager() {
		if (mCacheManager == null) {
			Context c = getApplicationContext();
			mCacheManager = CacheManager.getInstance(c, new SimpleConfiguration(c));
		}
		return mCacheManager;
	}

	/**
	 * 初始化图片缓存管理者
	 * 
	 * @return
	 * 
	 * 		public ImageCacheManager getImageCacheManager() { if
	 *         (mImageCacheManager == null) { mImageCacheManager = new
	 *         ImageCacheManager(getCacheManager()); Resources resources =
	 *         getResources(); mImageCacheManager.setDefault(DEFAULT_ITEM,
	 *         resources.getDrawable(R.drawable.ic_launcher)); } return
	 *         mImageCacheManager; }
	 */
	/**
	 * 应用停止方法 注销地图管理
	 * 
	 * @Override public void onTerminate() { if (mBMapManager != null) {
	 *           mBMapManager.destroy(); mBMapManager = null; }
	 *           super.onTerminate(); }
	 */
}
