package com.lenovo.sdimobileclient.network;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Error;
import com.lenovo.sdimobileclient.ui.ActivityUtils;
import com.lenovo.sdimobileclient.ui.IEncActivity;
import com.lenovo.sdimobileclient.ui.RootActivity;
import com.lenovo.sdimobileclient.ui.RootFragmentActivity;
import com.lenovo.sdimobileclient.utility.Utils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import okhttp3.Call;

public abstract class OkHttpStringCallback extends StringCallback {

	private Context mContext;

	public OkHttpStringCallback(Context context) {

		this.mContext = context;
	}

	@Override
	public void onError(Call arg0, Exception errorlog) {
		String errmsg = errorlog.getMessage();

		if (TextUtils.equals(errmsg, "Canceled")) {
			
			return;
			
		}

		Error error = null;
		IEncActivity enActivity = null;
		Activity activity = null;
		if (mContext instanceof Activity) {
			activity = (Activity) mContext;
			activity = ActivityUtils.getRootActivity(activity);
			if (activity instanceof IEncActivity) {
				enActivity = (IEncActivity) activity;
			} else {
				enActivity = (IEncActivity) mContext;
			}
		}

		if (enActivity instanceof RootActivity) {

			((RootActivity) enActivity).dismisDialog(Utils.DLG_DATA_LOADING);

		} else if (enActivity instanceof RootFragmentActivity) {
			((RootFragmentActivity) enActivity).dismisDialog(Utils.DLG_DATA_LOADING);
		}

		if (mContext != null && mContext instanceof IEncActivity) {

			boolean network = Utils.isNetwork(mContext);

			if (!network) {
				error = new Error(Error.ERROR_NETWORK, mContext.getString(R.string.errmsg_nonetwork));
			} else {
				error = new Error(Error.ERROR_UNKNOW, mContext.getString(R.string.errmsg_network_error));
			}
			enActivity.errorData(error);
			enActivity.showDialog(Utils.DLG_NETWORK_ERROR);
		}
		LoadError();
	}

	protected void LoadError() {

	}

	@Override
	public abstract void onResponse(String result);

}
