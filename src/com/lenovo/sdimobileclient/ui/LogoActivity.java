package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import com.foreveross.cache.network.ParamPair;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Apk;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.receiver.LockScreenReceiver;
import com.lenovo.sdimobileclient.utility.DES;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class LogoActivity extends RootActivity {
	private static final int CALLBACK_SYSTEM_PARAM = 1;
	// private View mWaitView;
	private static final int REQUEST_CODE_LOCK = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mWaitView = findViewById(R.id.progress);
		mHttpHelper = OkHttpHelper.getInstance(this);
		checkActive();
	}

	/**
	 * 检查应用锁屏是否激活
	 */
	private void checkActive() {
		DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName mDeviceAdminSample = new ComponentName(this, LockScreenReceiver.class);
		boolean active = mDevicePolicyManager.isAdminActive(mDeviceAdminSample);
		if (!active) {// 未激活 跳转设备激活授权界面
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "欢迎使用联想服务交付系统，该系统包含远程锁屏功能，以便在您的手机丢失后，可通过站端发送命令对您遗失的设备进行锁屏，首次安装时，需要您初始激活。");
			startActivityForResult(intent, REQUEST_CODE_LOCK);
		} else {

			init();

		}
	}

	/**
	 * 应用初始化 获取系统参数 Apk信息 系统时间 密钥
	 */
	@SuppressWarnings("deprecation")
	private void init() {

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String device_id = null;
		if (tm != null) {
			device_id = tm.getDeviceId();
		}
		if (TextUtils.isEmpty(device_id)) {
			device_id = UNKNOW_DEVICE_ID;
		}
		// showProgress(mWaitView, true);

		HashMap<String, String> hashMap = new HashMap<String, String>();

		List<NameValuePair> postValues = new ArrayList<NameValuePair>();
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		int h = d.getHeight();
		int w = d.getWidth();
		int o = d.getOrientation();
		int screenHeight = o == 0 ? h : w;
		int screenWidth = o == 0 ? w : h;
		String enDeviceId = DES.encyrpt(DES.INIT_KEY, device_id);
		postValues.add(new ParamPair(PARAM_IMEI, enDeviceId));

		hashMap.put(PARAM_IMEI, enDeviceId);

		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(getPackageName(), 0);
			String version = info.versionName;
			float curVersion = Float.parseFloat(version);
			postValues.add(new ParamPair(PARAM_APK_VERSION, curVersion));
			hashMap.put(PARAM_APK_VERSION, curVersion + "");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		postValues.add(new ParamPair(PARAM_DRIVERNAME, Build.MODEL));
		postValues.add(new ParamPair(PARAM_SCREEN_WIDTH, screenWidth));
		postValues.add(new ParamPair(PARAM_SCREEN_HEIGHT, screenHeight));

		hashMap.put(PARAM_DRIVERNAME, Build.MODEL);
		hashMap.put(PARAM_SCREEN_WIDTH, screenWidth + "");
		hashMap.put(PARAM_SCREEN_HEIGHT, screenHeight + "");

		DisplayMetrics metrics = new DisplayMetrics();
		metrics.setToDefaults();
		postValues.add(new ParamPair(PARAM_SCREEN_DPI, metrics.densityDpi));
		postValues.add(new ParamPair(PARAM_SEARCHTYPE, "0"));
		postValues.add(new ParamPair(PARAM_SDK, Build.VERSION.SDK));

		hashMap.put(PARAM_SCREEN_DPI, metrics.densityDpi + "");
		hashMap.put(PARAM_SEARCHTYPE, "0");
		hashMap.put(PARAM_SDK, Build.VERSION.SDK);

		// NetworkPath path = new Netpath(URL_SYSTEM_PARAMS, postValues);
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_SYSTEM_PARAM, params, this);

		mHttpHelper.load(URL_SYSTEM_PARAMS, new MyStringCallback(this), hashMap, this);

	}

	class MyStringCallback extends OkHttpStringCallback {

		public MyStringCallback(Context context) {
			super(context);
		}

		@Override
		public void onResponse(String result) {

			dismisDialog(DLG_DATA_LOADING);
			/**
			 * 登录成功，保存用户账号信息及工程师信息
			 */

			boolean successResult = mHttpHelper.isSuccessResult(result, LogoActivity.this);

			if (!successResult) {
				return;
			}

			RootData rootData = new RootData(result);

			Utils.saveSystemKeyAndTime(LogoActivity.this, rootData.Key, rootData.SysTime);

			Apk apk = rootData.getData(Apk.class);
			if (apk != null && !TextUtils.isEmpty(apk.DownLoadUrl)) {
				mApk = apk;
				showDialog(DLG_NEW_VERSION);
			} else {
				enterSystem();
			}

		}

	}

	/**
	 * 返回当前显示界面
	 */
	@Override
	protected int getContentViewId() {
		return R.layout.logo;
	}

	/**
	 * 响应点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_retry:
			init();
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	public void finish() {
		// if (mCacheManager != null)
		// mCacheManager.cancel(this);

		mHttpHelper.cancle(LogoActivity.class);
		super.finish();
	}

	/**
	 * 网络连接失败，点击重试时，界面提示，显示数据加载中
	 */
	@Override
	protected void notifyView() {
		// showProgress(mWaitView, true);
	}

	private void enterSystem() {
		mHandler.sendEmptyMessageDelayed(MSG_ENTER_SYSTEM, 0);
	}

	/**
	 * 进入设备激活页面，用户激活反馈
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_LOCK: {
			DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			ComponentName mDeviceAdminSample = new ComponentName(this, LockScreenReceiver.class);
			boolean active = mDevicePolicyManager.isAdminActive(mDeviceAdminSample);
			if (!active) {
				Toast.makeText(this, R.string.tip_avtived_failed, Toast.LENGTH_SHORT).show();
			}
			init();
		}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	private Apk mApk;

	// public void dataLoaded(int id, CacheParams params, ICacheInfo result) {
	// boolean safe = ActivityUtils.prehandleNetworkData(this, this, id, params,
	// result, true);
	// if (!safe) {
	// // mWaitView.findViewById(R.id.btn_retry).setOnClickListener(this);
	// // showProgress(mWaitView, false);
	// return;
	// }
	// RootData rootData = (RootData) result.getData();
	// Utils.saveSystemKeyAndTime(this, rootData.Key, rootData.SysTime);
	//
	// Apk apk = rootData.getData(Apk.class);
	// if (apk != null && !TextUtils.isEmpty(apk.DownLoadUrl)) {
	// mApk = apk;
	// showDialog(DLG_NEW_VERSION);
	// } else {
	//
	// enterSystem();
	// }
	// }

	private void downloaderApk(String apk_url, String description) {
		UpdateManager manager = new UpdateManager(LogoActivity.this);
		// 检查软件更新
		manager.checkUpdate(apk_url);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ENTER_SYSTEM: {
				Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
				break;
			default:
				break;
			}

		};
	};
	private static final int DLG_NEW_VERSION = 30008;
	private static final int DLG_APK_LOADING = 30009;
	private OnClickListener mDialogListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				downloaderApk(mApk.DownLoadUrl, mApk.VersionDescription);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				enterSystem();
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog result = null;
		switch (id) {

		case DLG_NEW_VERSION: {
			AlertDialog.Builder builder = new AlertDialog.Builder(getRootActivity(this));
			View view = getLayoutInflater().inflate(R.layout.loading, null);
			builder.setView(view);
			TextView verTv = (TextView) view.findViewById(R.id.tv_version);
			verTv.setText("版本号:" + mApk.CurrentVersion);
			TextView desTv = (TextView) view.findViewById(R.id.tv_des);
			desTv.setText(mApk.VersionDescription);
			TextView dateTv = (TextView) view.findViewById(R.id.tv_date);
			dateTv.setText("发布时间:" + mApk.ReleaseDate);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			AlertDialog r = builder.create();
			r.setTitle("发现新版本");
			r.setCancelable(false);
			r.setButton(DialogInterface.BUTTON_POSITIVE, getText(R.string.btn_sure), mDialogListener);
			r.setButton(DialogInterface.BUTTON_NEGATIVE, getText(R.string.btn_Cancel), mDialogListener);
			result = r;
		}
			break;
		case DLG_APK_LOADING:
			ProgressDialog progDialog = new ProgressDialog(getRootActivity(this));
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(true);
			progDialog.setMessage(getString(R.string.label_loading));
			result = progDialog;
			break;
		default:
			result = super.onCreateDialog(id);
			break;
		}
		return result;
	};

	private static final int MSG_ENTER_SYSTEM = 2;
	private OkHttpHelper mHttpHelper;
}
