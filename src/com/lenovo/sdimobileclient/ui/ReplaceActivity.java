package com.lenovo.sdimobileclient.ui;

import java.util.HashMap;
import java.util.List;

import com.foreveross.cache.CacheManager;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Change;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.utility.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReplaceActivity extends RootActivity {

	private static final int CALLBACK_LOADREPLACE = 2001;
	private static final int CALLBACK_DELETE_REPLACEDATA = 2002;
	private static final int CALLBACK_REFRESH = 2003;
	private LinearLayout mReplaceLayout;
	private LayoutInflater mInflater;
	private String mOrderId;
	private boolean mReplaceEdit;
	private View mBtnAddView;
	private View mWaitView;
	private String ProductSN;
	private int mLoadID = -1;
	private OkHttpHelper mHttpHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBackBtn();
		mHttpHelper = OkHttpHelper.getInstance(this);
		mOrderId = getIntent().getStringExtra("orderId");
		ProductSN = getIntent().getStringExtra("ProductSN");
		mBtnAddView = findViewById(R.id.btn_add);
		mBtnAddView.setOnClickListener(this);
		mView = findViewById(R.id.tv_desc);
		mReplaceEdit = getIntent().getBooleanExtra("replace", false);
		mReplaceLayout = (LinearLayout) findViewById(R.id.replace_hasReplaced);
		mInflater = LayoutInflater.from(this);
		mWaitView = findViewById(R.id.progress);
		mWaitView.setVisibility(View.VISIBLE);
		mWaitView.findViewById(R.id.btn_retry).setOnClickListener(this);
		// findViewById(R.id.unreplace_layout).setVisibility(View.GONE);
		mReplaceLayout.setVisibility(View.VISIBLE);
		loadReplaceInfo(CALLBACK_LOADREPLACE);

	}

	private void loadReplaceInfo(int callbackId) {
		mWaitView.setVisibility(View.VISIBLE);
		// Uri uri =
		// Uri.parse(URL_CHANGE_SOURCES).buildUpon().appendQueryParameter(PARAM_ORDERID,
		// mOrderId)
		// .appendQueryParameter(PARAM_ENGINEER,
		// Utils.getEnginnerInfo(this).EngineerNumber).build();
		// NetworkPath path = new Netpath(uri.toString());
		// CacheParams params = new CacheParams(path);
		// mCacheManager.load(callbackId, params, this);

		mLoadID = callbackId;

		Uri uri = Uri.parse(URL_CHANGE_MESSAGE).buildUpon().appendQueryParameter(PARAM_ORDERID, mOrderId)
				.appendQueryParameter(PARAM_ENGINEER, Utils.getEnginnerInfo(this).EngineerNumber).build();
		mHttpHelper.load(uri.toString(), mCallback, this);

	}

	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {

			dismisDialog(DLG_SENDING);
			dismisDialog(DLG_DATA_LOADING);
			boolean safe = mHttpHelper.isSuccessResult(result, ReplaceActivity.this);
			if (!safe) {
				if (mLoadID == CALLBACK_LOADREPLACE)
					showProgress(mWaitView, false);
				return;
			}

			switch (mLoadID) {

			case CALLBACK_REFRESH:
			case CALLBACK_LOADREPLACE:
				mView.setVisibility(View.VISIBLE);
				mWaitView.setVisibility(View.GONE);

				RootData rootData = new RootData(result);
				List<Change> changes = rootData.getArrayData(Change.class);
				mReplaceLayout.removeAllViews();
				if (changes != null && !changes.isEmpty()) {
					mBtnAddView.setVisibility(View.VISIBLE);
					for (Change change : changes) {
						
						
						change.mReplaceEdit = mReplaceEdit;
						addChangeView(change);
					}
				} else {
					mView.setVisibility(View.GONE);
					// TODO
					Intent intent = new Intent(ReplaceActivity.this, AddReplaceActivity.class);
					intent.putExtra("orderId", mOrderId);
					intent.putExtra("ProductSN", ProductSN);
					intent.putExtra("needrefresh", mNeedRefrash);
					startActivityForResult(intent, REQUESTCODE_NO_REPLACED);
				}
				break;
			case CALLBACK_DELETE_REPLACEDATA:

				mNeedRefrash = true;
				if (mReplaceLayout != null)
					mReplaceLayout.removeView(mDeleteView);

				if (mReplaceLayout.getChildCount() == 0) {
					mView.setVisibility(View.GONE);
				}

				Utils.showToast(ReplaceActivity.this, "删除成功");
				break;
			default:
				break;
			}

		}
	};

	private static final int REQUESTCODE_ADDREPLACE = 3001;
	private static final int REQUESTCODE_NO_REPLACED = 4001;

	private boolean mNeedRefrash = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add: {
			Intent intent = new Intent(this, AddReplaceActivity.class);
			intent.putExtra("orderId", mOrderId);
			intent.putExtra("ProductSN", ProductSN);
			intent.putExtra("needrefresh", mNeedRefrash);
			startActivityForResult(intent, REQUESTCODE_ADDREPLACE);
		}
			break;
		case R.id.btn_retry:
			retryLoadData();
			break;
		case R.id.btn_back:
			if (emptyData()) {
				finish();
			} else {
				showDialog(DLG_UNSAVE);
			}
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_ADDREPLACE:
			if (resultCode == RESULT_OK) {
				showDialog(DLG_DATA_LOADING);

				loadReplaceInfo(CALLBACK_REFRESH);
			}
			break;
		case REQUESTCODE_NO_REPLACED:

			if (resultCode == RESULT_CANCELED) {

				finish();
			} else if (resultCode == RESULT_OK) {

				showDialog(DLG_DATA_LOADING);
				loadReplaceInfo(CALLBACK_REFRESH);
			}

			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	@Override
	protected void notifyView() {
		if (mCallbackId == CALLBACK_LOADREPLACE) {
			showProgress(mWaitView, true);
		} else if (mCallbackId == CALLBACK_REFRESH) {
			showDialog(DLG_DATA_LOADING);
		} else if (mCallbackId == CALLBACK_DELETE_REPLACEDATA) {
			showDialog(DLG_SENDING);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !emptyData()) {
			showDialog(DLG_UNSAVE);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private boolean emptyData() {
		boolean result = true;
		return result;
	}

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
		v.findViewById(R.id.btn_delete).setVisibility(mReplaceEdit ? View.VISIBLE : View.GONE);
		v.findViewById(R.id.btn_delete).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				deleteChange(change);
				mDeleteView = v;
			}
		});
		mReplaceLayout.addView(v);
	}

	private void deleteChange(Change change) {
		showDialog(DLG_SENDING);

		HashMap<String, String> hashMap = new HashMap<String, String>();
		// postValues.add(new ParamPair(PARAM_ORDERID, mOrderId));
		// postValues.add(new ParamPair(PARAM_ENGINEER,
		// Utils.getEnginnerInfo(this).EngineerNumber));
		// postValues.add(new ParamPair(PARAM_CHANGEINFOID,
		// change.ChangeRecID));

		hashMap.put(PARAM_ORDERID, mOrderId);
		hashMap.put(PARAM_ENGINEER, Utils.getEnginnerInfo(this).EngineerNumber);
		hashMap.put(PARAM_CHANGEINFOID, change.ChangeRecID);
		// NetworkPath path = new Netpath(URL_CHANGE_DELETE, postValues);
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_DELETE_REPLACEDATA, params, this);

		mLoadID = CALLBACK_DELETE_REPLACEDATA;
		mHttpHelper.load(URL_CHANGE_DELETE, mCallback, hashMap, this);
	}

	private View mView;

	/*
	 * @Override public void dataLoaded(int id, CacheParams params, ICacheInfo
	 * result) {
	 * 
	 * dismisDialog(DLG_SENDING); dismisDialog(DLG_DATA_LOADING); boolean safe =
	 * ActivityUtils.prehandleNetworkData(this, this, id, params, result, true);
	 * if (!safe) { if (id == CALLBACK_LOADREPLACE) showProgress(mWaitView,
	 * false); return; } switch (id) { case CALLBACK_REFRESH: case
	 * CALLBACK_LOADREPLACE: mView.setVisibility(View.VISIBLE);
	 * mWaitView.setVisibility(View.GONE); RootData rootData = (RootData)
	 * result.getData(); mReplaceInfo = rootData.getJson().toString();
	 * List<Change> changes = rootData.getArrayData(Change.class);
	 * mReplaceLayout.removeAllViews(); if (changes != null &&
	 * !changes.isEmpty()) { mBtnAddView.setVisibility(View.VISIBLE); for
	 * (Change change : changes) { addChangeView(change); } } else {
	 * mView.setVisibility(View.GONE); // TODO Intent intent = new Intent(this,
	 * AddReplaceActivity.class); intent.putExtra("replace", mReplaceInfo);
	 * intent.putExtra("orderId", mOrderId); intent.putExtra("ProductSN",
	 * ProductSN); intent.putExtra("needrefresh", mNeedRefrash);
	 * startActivityForResult(intent, REQUESTCODE_NO_REPLACED); } break; case
	 * CALLBACK_DELETE_REPLACEDATA:
	 * 
	 * mNeedRefrash = true; if (mReplaceLayout != null)
	 * mReplaceLayout.removeView(mDeleteView);
	 * 
	 * if (mReplaceLayout.getChildCount() == 0) {
	 * mView.setVisibility(View.GONE); } Toast.makeText(this, "删除成功",
	 * Toast.LENGTH_SHORT).show(); break; default: break; } }
	 */
	@Override
	public void finish() {
		/*
		 * if (mCacheManager != null) { mCacheManager.cancel(this); }
		 */

		mHttpHelper.cancle(ReplaceActivity.class);

		super.finish();
	}
}
