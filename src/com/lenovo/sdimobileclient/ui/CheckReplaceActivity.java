package com.lenovo.sdimobileclient.ui;

import java.util.List;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Change;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.utility.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckReplaceActivity extends RootActivity {

	private static final int CALLBACK_LOADREPLACE = 2001;
	private LinearLayout mReplaceLayout;
	private LayoutInflater mInflater;
	private String mOrderId;
	private boolean mReplaceEdit;
	private View mWaitView;
	private String ProductSN;
	private OkHttpHelper mHttpHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBackBtn();

		mHttpHelper = OkHttpHelper.getInstance(this);

		mOrderId = getIntent().getStringExtra("orderId");
		ProductSN = getIntent().getStringExtra("ProductSN");
		mView = findViewById(R.id.tv_desc);
		mReplaceEdit = getIntent().getBooleanExtra("replace", false);
		mReplaceLayout = (LinearLayout) findViewById(R.id.replace_hasReplaced);
		mInflater = LayoutInflater.from(this);
		mWaitView = findViewById(R.id.progress);
		mWaitView.setVisibility(View.VISIBLE);
		mWaitView.findViewById(R.id.btn_retry).setOnClickListener(this);
		mReplaceLayout.setVisibility(View.VISIBLE);
		Uri uri = Uri.parse(URL_CHANGE_MESSAGE).buildUpon().appendQueryParameter(PARAM_ORDERID, mOrderId)
				.appendQueryParameter(PARAM_ENGINEER, Utils.getEnginnerInfo(this).EngineerNumber).build();

		mHttpHelper.load(uri.toString(), mCallback, this);
	}

	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		protected void LoadError() {
			dismisDialog(DLG_SENDING);
			dismisDialog(DLG_DATA_LOADING);
		};

		@Override
		public void onResponse(String result) {

			dismisDialog(DLG_SENDING);
			dismisDialog(DLG_DATA_LOADING);
			boolean safe = mHttpHelper.isSuccessResult(result, CheckReplaceActivity.this);
			if (!safe) {
				showProgress(mWaitView, false);
				return;
			}

			mView.setVisibility(View.VISIBLE);
			mWaitView.setVisibility(View.GONE);
			mReplaceInfo = result;
			RootData rootData = new RootData(result);
			List<Change> changes = rootData.getArrayData(Change.class);
			mReplaceLayout.removeAllViews();
			if (changes != null && !changes.isEmpty()) {
				for (Change change : changes) {
					addChangeView(change);
				}
			} else {
				mView.setVisibility(View.GONE);
				// TODO
				Intent intent = new Intent(CheckReplaceActivity.this, AddReplaceActivity.class);
				intent.putExtra("replace", mReplaceInfo);
				intent.putExtra("orderId", mOrderId);
				intent.putExtra("ProductSN", ProductSN);
				intent.putExtra("needrefresh", mNeedRefrash);
				startActivityForResult(intent, REQUESTCODE_NO_REPLACED);
			}
		}
	};

	/*
	 * private void loadReplaceInfo(int callbackId) {
	 * mWaitView.setVisibility(View.VISIBLE); Uri uri =
	 * Uri.parse(URL_CHANGE_SOURCES).buildUpon().appendQueryParameter(
	 * PARAM_ORDERID, mOrderId) .appendQueryParameter(PARAM_ENGINEER,
	 * Utils.getEnginnerInfo(this).EngineerNumber).build();
	 * 
	 * NetworkPath path = new Netpath(uri.toString()); CacheParams params = new
	 * CacheParams(path); mCacheManager.load(callbackId, params, this); }
	 */

	private static final int REQUESTCODE_ADDREPLACE = 3001;
	private static final int REQUESTCODE_NO_REPLACED = 4001;

	private boolean mNeedRefrash = false;

	@Override
	protected int getContentViewId() {
		return R.layout.replace_hasreplaced;
	}

	private View mDeleteView;

	private void addChangeView(final Change change) {
		final View v = mInflater.inflate(R.layout.change, null);
		TextView DownMaterialNo = (TextView) v.findViewById(R.id.DownMaterialNo);
		TextView DownMaterialNoDesc = (TextView) v.findViewById(R.id.DownMaterialNoDesc);
		TextView DownPartsSN = (TextView) v.findViewById(R.id.DownPartsSN);
		TextView SwapCategoryDesc = (TextView) v.findViewById(R.id.SwapCategoryDesc);
		TextView UpMaterialNo = (TextView) v.findViewById(R.id.UpMaterialNo);
		TextView UpMaterialNoDesc = (TextView) v.findViewById(R.id.UpMaterialNoDesc);
		TextView UpPartsSN = (TextView) v.findViewById(R.id.UpPartsSN);
		DownMaterialNo.setText(change.DownMaterialNo);
		DownMaterialNoDesc.setText(change.DownMaterialNoDesc);
		DownPartsSN.setText(change.DownPartsSN);
		SwapCategoryDesc.setText(change.SwapCategoryDesc);
		UpMaterialNo.setText(change.UpMaterialNo);
		UpMaterialNoDesc.setText(change.UpMaterialNoDesc);
		UpPartsSN.setText(change.UpPartsSN);
		v.findViewById(R.id.btn_delete).setVisibility(View.GONE);
		mReplaceLayout.addView(v);
	}

	private String mReplaceInfo;
	private View mView;

	// @Override
	// public void dataLoaded(int id, CacheParams params, ICacheInfo result) {
	// dismisDialog(DLG_SENDING);
	// dismisDialog(DLG_DATA_LOADING);
	// boolean safe = ActivityUtils.prehandleNetworkData(this, this, id, params,
	// result, true);
	// if (!safe) {
	// if (id == CALLBACK_LOADREPLACE)
	// showProgress(mWaitView, false);
	// return;
	// }
	// switch (id) {
	// case CALLBACK_LOADREPLACE:
	// mView.setVisibility(View.VISIBLE);
	// mWaitView.setVisibility(View.GONE);
	// RootData rootData = (RootData) result.getData();
	// mReplaceInfo = rootData.getJson().toString();
	// List<Change> changes = rootData.getArrayData(Change.class);
	// mReplaceLayout.removeAllViews();
	// if (changes != null && !changes.isEmpty()) {
	// for (Change change : changes) {
	// addChangeView(change);
	// }
	// } else {
	// mView.setVisibility(View.GONE);
	// // TODO
	// Intent intent = new Intent(this, AddReplaceActivity.class);
	// intent.putExtra("replace", mReplaceInfo);
	// intent.putExtra("orderId", mOrderId);
	// intent.putExtra("ProductSN", ProductSN);
	// intent.putExtra("needrefresh", mNeedRefrash);
	// startActivityForResult(intent, REQUESTCODE_NO_REPLACED);
	// }
	// break;
	// default:
	// break;
	// }
	// }

	@Override
	public void finish() {
		// if (mCacheManager != null) {
		// mCacheManager.cancel(this);
		// }

		mHttpHelper.cancle(CheckReplaceActivity.class);
		super.finish();
	}
}
