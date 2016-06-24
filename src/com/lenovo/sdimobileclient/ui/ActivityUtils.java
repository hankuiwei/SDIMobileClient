package com.lenovo.sdimobileclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.FileInfo;
import com.foreveross.cache.utility.NetworkHelpers;
import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Error;
import com.lenovo.sdimobileclient.api.RootData;

/**
 * Activity 工具类
 * 
 * @author zhangshaofang
 * 
 */
public class ActivityUtils implements Constants {
	/**
	 * 网络返回数据检查
	 * 
	 * @param context
	 *            上下文
	 * @param callback
	 *            回调方法
	 * @param id
	 *            回调Id
	 * @param params
	 *            网络请求参数
	 * @param cacheInfo
	 *            网络返回数据信息
	 * @param needNotify
	 *            是否需要通知，弹出错误对话框
	 * @return
	 */
	public static boolean prehandleNetworkData(Context context, Callback callback, int id, CacheParams params, ICacheInfo cacheInfo, boolean needNotify) {
		boolean result = true;
		IEncActivity enActivity = null;
		Activity activity = null;
		if (context instanceof Activity) {
			activity = (Activity) context;
			activity = getRootActivity(activity);
			if (activity instanceof IEncActivity) {
				enActivity = (IEncActivity) activity;
			} else {
				enActivity = (IEncActivity) context;
			}
			enActivity.notifyReloadByErrDlg(id, params, callback);
		}
		/**
		 * 网络检查
		 */
		result = NetworkHelpers.isNetworkAvailable(context);
		if (cacheInfo == null) {
			if (needNotify && enActivity != null) {
				NetworkPath path = params.path;
				FileInfo fileInfo = new FileInfo(null, path.url, path.postValues, null);
				Error error = null;
				if (!result) {
					error = new Error(Error.ERROR_NETWORK, context.getString(R.string.errmsg_nonetwork), fileInfo);
				} else {
					error = new Error(Error.ERROR_UNKNOW, context.getString(R.string.errmsg_network_error), fileInfo);
				}
				enActivity.errorData(error);
				enActivity.showDialog(DLG_NETWORK_ERROR);
			}
			result = false;
		} else {
			if (cacheInfo.isErrorData()) {
				RootData rootData = (RootData) cacheInfo.getData();
				if (needNotify && enActivity != null) {
					Error error = null;
					if (!result) {
						NetworkPath path = params.path;
						FileInfo fileInfo = new FileInfo(null, path.url, path.postValues, "Network is not available");
						error = new Error(Error.ERROR_NETWORK, context.getString(R.string.errmsg_nonetwork), fileInfo);
					} else {
						String errorCode = rootData.getStatus() == 0 ? Error.ERROR_NETWORK : rootData.getResult();
						String errorMsg = TextUtils.isEmpty(rootData.ResultMsg) ? context.getString(R.string.errmsg_network_error) : rootData.ResultMsg;
						error = new Error(errorCode, errorMsg, rootData.getFileInfo());
					}
					enActivity.errorData(error);
					enActivity.showDialog(DLG_NETWORK_ERROR);
				}
				result = false;
			}
		}
		if (result) {
			RootData rootData = (RootData) cacheInfo.getData();
			try {
				String msg = rootData.getJson().getString("WarningMsg");
				if (!TextUtils.isEmpty(msg))
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
			}
		}

		return result;
	}

	/**
	 * 获取activity 最顶层父类
	 * 
	 * @param activity
	 * @return
	 */
	public static Activity getRootActivity(Activity activity) {
		Activity parent = activity == null ? null : activity.getParent();
		if (parent != null) {
			return getRootActivity(parent);
		}
		return activity;
	}

	public static AlertDialog onCreateDialog(Activity a, int id) {
		AlertDialog result = null;
		Activity activity = getRootActivity(a);
		switch (id) {
		/**
		 * 数据加载
		 */
		case DLG_DATA_LOADING: {
			ProgressDialog progDialog = new ProgressDialog(activity);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(true);
			progDialog.setMessage(activity.getString(R.string.data_loading));
			result = progDialog;
		}
			break;
		/**
		 * 等待
		 */
		case DLG_LOADING: {
			ProgressDialog progDialog = new ProgressDialog(activity);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(true);
			progDialog.setMessage(activity.getString(R.string.data_waiting));
			result = progDialog;
		}
			break;
		/**
		 * 发送
		 */
		case DLG_SENDING: {
			ProgressDialog progDialog = new ProgressDialog(activity);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(true);
			progDialog.setMessage(activity.getString(R.string.data_sending));
			result = progDialog;
		}
			break;
		case DLG_CHECKHOST: {
			ProgressDialog progDialog = new ProgressDialog(activity);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(true);
			progDialog.setMessage(activity.getString(R.string.data_checking));
			result = progDialog;
		}
			break;
		case DLG_LOGOUT: {
			ProgressDialog progDialog = new ProgressDialog(activity);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(true);
			progDialog.setMessage(activity.getString(R.string.data_logout));
			result = progDialog;
		}
			break;
		}
		return result;
	}

}
