package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foreveross.cache.CacheManager;
import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.Netpath;
import com.google.gson.Gson;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.BorrowOrderInfo;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout.OnPullListener;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout.OnPullStateListener;
import com.lenovo.sdimobileclient.utility.Utils;

import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 备件查询
 * 
 * @author zhangshaofang
 * 
 */
public class TabSpareActivity extends RootActivity implements OnScrollListener, OnPullListener, OnPullStateListener, OnClickListener {

	private static final int CALLBACK_SPARE_INIT = 1001;
	private static final int CALLBACK_SPARE_APPEND = 1002;
	private Uri mUri = Uri.parse(URL_SPARELIST).buildUpon().appendQueryParameter("PageSize", String.valueOf(PAGE_COUNT)).build();
	private LayoutInflater mInflater;
	private ListView mListView;

	private int mNextId = 1;
	private boolean mSafe;
	private View mWaitView;
	private ApiDataAdapter<BorrowOrderInfo> mSpareAdapter;
	private boolean mHasData;
	private boolean mInLoading;
	private PullActivateLayout mPullLayout;
	private View mActionImage;
	private View mProgress;
	private Animation mRotateUpAnimation;
	private TextView mActionText;
	private TextView mTimeText;
	private Uri mLastUri;
	private View mListWaitView;
	private Animation mRotateDownAnimation;
	private int mloadId;
	private OkHttpHelper mHttpHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(this);
		mHttpHelper = OkHttpHelper.getInstance(this);
		mHttpHelper.cancle(MainActivity.class);

		initView();
		init();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.sqare_lv);
		mWaitView = findViewById(R.id.wait_progress);
		mWaitView.setVisibility(View.VISIBLE);

		mWaitView.findViewById(R.id.btn_retry).setOnClickListener(this);

		mListView.setOnScrollListener(this);
		mSpareAdapter = new ApiDataAdapter<BorrowOrderInfo>(this);
		mListView.setAdapter(mSpareAdapter);
		mListWaitView = getLayoutInflater().inflate(R.layout.progress, null);
		mListView.addFooterView(mListWaitView);
		mWaitView.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);

		/**
		 * 下拉刷新
		 */

		mRotateUpAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_up);
		mRotateDownAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_down);

		mPullLayout = (PullActivateLayout) findViewById(R.id.pull_container);
		mPullLayout.setOnActionPullListener(this);
		mPullLayout.setOnPullStateChangeListener(this);

		mProgress = findViewById(android.R.id.progress);
		mActionImage = findViewById(android.R.id.icon);
		mActionText = (TextView) findViewById(R.id.pull_note);
		mTimeText = (TextView) findViewById(R.id.refresh_time);

		mTimeText.setText(R.string.note_not_update);
		mActionText.setText(R.string.note_pull_down);
	}

	private void init() {

		EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(this);
		mLastUri = mUri.buildUpon().appendQueryParameter(PARAM_PAGEINDEX, String.valueOf(mNextId)).build();
		// NetworkPath path = new Netpath(mLastUri.toString());
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_SPARE_INIT, params, this);

		mloadId = CALLBACK_SPARE_INIT;
		mHttpHelper.load(mLastUri.toString(), mCallback, new HashMap<String, String>(), MainActivity.class, this);
	}

	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {

			mSafe = mHttpHelper.isSuccessResult(result, TabSpareActivity.this);

			if (!mSafe) {
				showProgress(mWaitView, false);
				return;
			}

			switch (mloadId) {
			case CALLBACK_SPARE_INIT:

				mListView.setVisibility(View.VISIBLE);
				mSpareAdapter.clear();
			case CALLBACK_SPARE_APPEND:// 翻页
				dataLoaded();
				mListView.setVisibility(View.VISIBLE);
				mListWaitView.setVisibility(View.GONE);
				mWaitView.setVisibility(View.GONE);
				RootData rootData = new RootData(result);
				List<BorrowOrderInfo> borrowOrderInfos = new ArrayList<BorrowOrderInfo>();
				try {
					String totalNumber = rootData.getJson().getString("PageCount");
					int counts = Integer.parseInt(totalNumber);
					String currentIndex = mLastUri.getQueryParameter(PARAM_PAGEINDEX);
					int index = Integer.parseInt(currentIndex);
					if ((index + 1) * PAGE_COUNT < counts) {
						mNextId = ++index;
						mHasData = true;
					} else {
						mNextId = -1;
						mHasData = false;
					}

					JSONObject json = rootData.getJson();
					Gson js = new Gson();
					JSONArray jsonArray = json.getJSONArray("BorrowOrderInfo");
					for (int i = 0; i < jsonArray.length(); i++) {
						Object object = jsonArray.get(i);
						String string = object.toString();
						BorrowOrderInfo fromJson = js.fromJson(string, BorrowOrderInfo.class);
						borrowOrderInfos.add(fromJson);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (!borrowOrderInfos.isEmpty()) {

					mSpareAdapter.add(borrowOrderInfos);
				} else {
					mListView.setVisibility(View.GONE);
					findViewById(R.id.tv_desc_sqare).setVisibility(View.VISIBLE);
				}

			}
		}
	};

	private void dataLoaded() {
		if (mInLoading) {
			mInLoading = false;
			mPullLayout.setEnableStopInActionView(false);
			mPullLayout.hideActionView();
			mActionImage.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);

			if (mPullLayout.isPullOut()) {
				mActionText.setText(R.string.note_pull_refresh);
				mActionImage.clearAnimation();
				mActionImage.startAnimation(mRotateUpAnimation);
			} else {
				mActionText.setText(R.string.note_pull_down);
			}

			mTimeText.setText(getString(R.string.note_update_at, DateFormat.getTimeFormat(this).format(new Date(System.currentTimeMillis()))));
		}
	}

	private void appendOrderListData() {
		mLastUri = mUri.buildUpon().appendQueryParameter(PARAM_PAGEINDEX, String.valueOf(mNextId)).build();
		// NetworkPath path = new Netpath(mLastUri.toString());
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_SPARE_APPEND, params, this);
		mloadId = CALLBACK_SPARE_APPEND;
		mHttpHelper.load(mLastUri.toString(), mCallback, new HashMap<String, String>(), MainActivity.class, this);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean flag = false;
		if (mLastUri != null) {
			String startId = mLastUri.getQueryParameter(PARAM_PAGEINDEX);
			if (!TextUtils.isEmpty(startId)) {
				int sId = Integer.valueOf(startId);
				if (sId == mNextId) {
					flag = true;
				}
			}
		}
		if (!flag && mSafe && mHasData && view.getLastVisiblePosition() >= (view.getCount() - 2)) {
			mListWaitView.setVisibility(View.VISIBLE);
			appendOrderListData();
		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.tabsqare_beijian;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPullOut() {
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_refresh);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateUpAnimation);
		}
	}

	@Override
	public void onClick(View v) {

		mHttpHelper.cancle(TabSpareActivity.class);
		switch (v.getId()) {
		case R.id.btn_retry:
			init();
			break;
		default:
			break;
		}
	}

	@Override
	public void onPullIn() {
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_down);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateDownAnimation);
		}

	}

	private void startLoading() {
		showProgress(mWaitView, true);
		if (!mInLoading) {
			mInLoading = true;
			mPullLayout.setEnableStopInActionView(true);
			mActionImage.clearAnimation();
			mActionImage.setVisibility(View.GONE);

			mProgress.setVisibility(View.GONE);
			mActionText.setText(R.string.note_pull_loading);

			init();
		}
	}

	@Override
	public void onSnapToTop() {
		startLoading();

	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHide() {
		// TODO Auto-generated method stub

	}

}
