package com.lenovo.sdimobileclient.network;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Error;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.ui.ActivityUtils;
import com.lenovo.sdimobileclient.ui.IEncActivity;
import com.lenovo.sdimobileclient.utility.DES;
import com.lenovo.sdimobileclient.utility.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;
import okhttp3.OkHttpClient;

public class OkHttpHelper {

	private static final String TOKEN_KEY = "12345678";
	private static OkHttpUtils httpUtils;
	private static String token;
	private static String OldUrl;
	private static Callback<?> OldCallback;
	private static Class tag;
	private static Context mContext;
	private static OkHttpHelper mHttpHelper;

	public static OkHttpHelper getInstance(Context context) {
		mContext = context;
		if (httpUtils == null) {

			OkHttpClient okHttpClient = new OkHttpClient.Builder()
					// .addInterceptor(new LoggerInterceptor("TAG"))
					.connectTimeout(15000L, TimeUnit.MILLISECONDS).readTimeout(15000L, TimeUnit.MILLISECONDS)
					// 其他配置
					.build();

			httpUtils = OkHttpUtils.getInstance(okHttpClient);
			// httpUtils = OkHttpUtils.getInstance();

		}

		if (mHttpHelper == null) {

			mHttpHelper = new OkHttpHelper();
		}
		return mHttpHelper;
	}

	public void load(String url, Callback<?> callback, Context context) {

		load(url, callback, new HashMap<String, String>(), context);
	}

	public void load(String url, Callback<?> callback, Map<String, String> params, Context context) {

		tag = context.getClass();

		load(url, callback, params, tag, context);
	}

	public void load(String url, Callback<?> callback, Map<String, String> params, Class clazz, Context context) {

		if (mContext == null) {
			mContext = context;
		}
		boolean flag = checkToken(context);
		Uri build = null;
		if (flag) {
			build = Uri.parse(url).buildUpon().appendQueryParameter("Token", Utils.getToken(mContext)).appendQueryParameter("SourceFlag", "MOBILE").build();
		} else {
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			String device_id = null;
			if (tm != null) {
				device_id = tm.getDeviceId();
			}
			if (TextUtils.isEmpty(device_id)) {
				device_id = Utils.UNKNOW_DEVICE_ID;
			}
			String oldtoken = Utils.datetimesFormatPre(System.currentTimeMillis()) + "," + device_id;
			token = DES.encyrpt(TOKEN_KEY, oldtoken);
			build = Uri.parse(url).buildUpon().appendQueryParameter("Token", token).appendQueryParameter("SourceFlag", "MOBILE").build();
		}

		Utils.saveInfo(mContext, "params", params);
		OldUrl = url;

		OldCallback = callback;

		if (build == null || clazz == null || callback == null || httpUtils == null) {
			return;
		}
		if (params.isEmpty()) {
			httpUtils.post().url(build.toString()).tag(clazz).build().execute(callback);
		} else {
			httpUtils.post().url(build.toString()).params(params).tag(clazz).build().execute(callback);
		}
	}

	private boolean checkToken(Context context) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String key = preferences.getString(Utils.PREF_SYSTEM_KEY, "");
		long spit = preferences.getLong(Utils.PREF_SYSTEM_TIME, 0);
		long client_time = preferences.getLong(Utils.PREF_CLIENT_TIME, 0);
		boolean isToday = Utils.isToday(client_time);
		if (TextUtils.isEmpty(key) || spit == 0 || !isToday) {
			return false;
		}

		return true;
	}

	public void load(String url, Callback<?> callback, Map<String, String> params, String Key, String filename, File file, Context context) {

		boolean flag = checkToken(context);
		Uri build = null;
		if (flag) {

			build = Uri.parse(url).buildUpon().appendQueryParameter("Token", Utils.getToken(mContext)).appendQueryParameter("SourceFlag", "MOBILE").build();
		} else {
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			String device_id = null;
			if (tm != null) {
				device_id = tm.getDeviceId();
			}
			if (TextUtils.isEmpty(device_id)) {
				device_id = Utils.UNKNOW_DEVICE_ID;
			}
			String oldtoken = Utils.datetimesFormatPre(System.currentTimeMillis()) + "," + DES.decyrpt(DES.INIT_KEY, device_id);
			token = DES.encyrpt(TOKEN_KEY, oldtoken);
			build = Uri.parse(url).buildUpon().appendQueryParameter("Token", token).appendQueryParameter("SourceFlag", "MOBILE").build();
		}
		tag = context.getClass();

		OldUrl = url;

		OldCallback = callback;
		Utils.saveInfo(mContext, "params", params);

		httpUtils.post().url(build.toString()).params(params).addFile(Key, filename, file).tag(tag).build().execute(callback);

	}

	public void cancle(Class clazz) {

		httpUtils.cancelTag(clazz);

	}

	public void loadAgain() {

		load(OldUrl, OldCallback, Utils.getInfo(mContext, "params"), mContext);
	}

	public boolean isSuccessResult(String result, Context context) {
		boolean isSuccessResult = false;

		IEncActivity enActivity = null;
		Activity activity = null;
		if (context instanceof Activity) {
			activity = (Activity) context;
			activity = ActivityUtils.getRootActivity(activity);
			if (activity instanceof IEncActivity) {
				enActivity = (IEncActivity) activity;
			} else {
				enActivity = (IEncActivity) context;
			}
		}

		/**
		 * 检查返回值是否是 001 不是就不是正常的返回值 需要弹出 dialog.
		 */
		RootData rootData = new RootData(result);
		String res = rootData.getResult();
		isSuccessResult = TextUtils.equals(res, "001");

		/**
		 * 如果不是正常数据 弹出窗口
		 */
		if (!isSuccessResult) {

			Error error = null;
			String errorCode = rootData.getStatus() == 0 ? Error.ERROR_NETWORK : rootData.getResult();
			String errorMsg = TextUtils.isEmpty(rootData.ResultMsg) ? context.getString(R.string.errmsg_network_error) : rootData.ResultMsg;
			error = new Error(errorCode, errorMsg, rootData.getFileInfo());
			enActivity.errorData(error);
			enActivity.showDialog(ActivityUtils.DLG_NETWORK_ERROR);
		}

		if (isSuccessResult) {
			try {
				String msg = rootData.getJson().getString("WarningMsg");
				if (!TextUtils.isEmpty(msg))
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
			}
		}

		return isSuccessResult;

	}

}
