package com.lenovo.sdimobileclient.ui;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.foreveross.cache.CacheManager;
import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.Netpath;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.api.OrderCounts;
import com.lenovo.sdimobileclient.api.OrderInfo;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout;
import com.lenovo.sdimobileclient.utility.Utils;

import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 工单 搜索列表
 * 
 * @author Administrator
 *
 */
public class TabOrderSearchActivity extends RootActivity implements OnItemClickListener, OnScrollListener {

	private static final int CALLBACK_SEARCH = 0;
	private static final int CALLBACK_APPEND = 1;
	private EnginnerInfo mEnginnerInfo;
	private EditText mSearchTv;
	private View mBtnSearch;
	private ListView mListView;
	private Animation mRotateUpAnimation;
	private Object mRotateDownAnimation;
	private PullActivateLayout mPullLayout;
	private View mProgress;
	private View mActionImage;
	private TextView mActionText;
	private TextView mTimeText;
	private View mWaitView;
	private ApiDataAdapter<OrderInfo> mOrderAdapter;
	private int mNextId;
	private Uri mSearchUri;
	private boolean mSafe;
	private boolean mInLoading;
	private int mloadId;
	private OkHttpHelper mHttpHelper;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mHttpHelper = OkHttpHelper.getInstance(this);
		mHttpHelper.cancle(MainActivity.class);
		initBackBtn();
		mEnginnerInfo = Utils.getEnginnerInfo(this);

		initView();
	}

	private void initView() {
		mSearchTv = (EditText) findViewById(R.id.et_search);
		mBtnSearch = findViewById(R.id.btn_search_product);
		mBtnSearch.setOnClickListener(this);

		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);

		mWaitView = getLayoutInflater().inflate(R.layout.progress, null);
		mWaitView.setVisibility(View.GONE);
		mWaitView.findViewById(R.id.btn_retry).setOnClickListener(this);

		mOrderAdapter = new ApiDataAdapter<OrderInfo>(this);
		mListView.setAdapter(mOrderAdapter);

		mListView.addFooterView(mWaitView);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_search_product: {
			doSearch(mSearchTv.getText().toString());
		}
			break;
		case R.id.btn_back: {
			finish();
		}
			break;

		default:
			break;
		}

	}

	private void doSearch(String mSearchString) {
		mHttpHelper.cancle(TabOrderSearchActivity.class);
		if (TextUtils.isEmpty(mSearchString)) {
			Toast.makeText(this, "查询内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mBtnSearch.setEnabled(false);

		Uri uri = Uri.parse(URL_ALLORDERLIST).buildUpon().appendQueryParameter(PARAM_PAGECOUNT, String.valueOf(PAGE_COUNT)).build();
		mOrderAdapter.clear();
		showProgress(mWaitView, true);
		mNextId = 0;

		mSearchUri = uri.buildUpon().appendQueryParameter(PARAM_QUERYCONDITION, mSearchString).build();

		mLastUri = mSearchUri.buildUpon().appendQueryParameter(PARAM_PAGEINDEX, String.valueOf(mNextId)).build();
		// NetworkPath path = new Netpath(mLastUri.toString());
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_SEARCH, params, this);
		mloadId = CALLBACK_SEARCH;
		mHttpHelper.load(mLastUri.toString(), mCallback, this);

	}

	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {
			mBtnSearch.setEnabled(true);
			boolean safe = mHttpHelper.isSuccessResult(result, TabOrderSearchActivity.this);

			if (!mSafe) {
				showProgress(mWaitView, false);
				return;
			}
			switch (mloadId) {
			case CALLBACK_SEARCH: {
				mWaitView.setVisibility(View.GONE);
				RootData rootData = new RootData(result);
				mOrderAdapter.clear();
				try {
					String totalNumber = rootData.getJson().getString("TotalNumber");
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				List<OrderInfo> orderInfos = rootData.getArrayData(OrderInfo.class);
				if (orderInfos != null) {
					mOrderAdapter.add(orderInfos);
				}
				if (orderInfos.size() == 0) {
					Utils.showToast(getApplicationContext(), "没有查到符合条件的工单");
				}
			}
				break;

			case CALLBACK_APPEND: {

				dataLoaded();
				mWaitView.setVisibility(View.GONE);
				RootData rootData = new RootData(result);
				try {
					String totalNumber = rootData.getJson().getString("TotalNumber");
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				List<OrderInfo> orderInfos = rootData.getArrayData(OrderInfo.class);

				if (orderInfos != null) {
					mOrderAdapter.add(orderInfos);
				}
			}
				break;
			default:
				break;
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

	@Override
	protected int getContentViewId() {
		return R.layout.order_search_list;
	}

	private boolean mHasData;
	private Uri mLastUri;

	/**
	 * 工单列表滑动监听
	 */
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
			showProgress(mWaitView, true);
			appendOrderListData();
		}
	}

	private void appendOrderListData() {
		mLastUri = mSearchUri.buildUpon().appendQueryParameter(PARAM_PAGEINDEX, String.valueOf(mNextId)).build();
		// NetworkPath path = new Netpath(mLastUri.toString());
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_APPEND, params, this);
		mloadId = CALLBACK_APPEND;
		mHttpHelper.load(mLastUri.toString(), mCallback, this);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object obj = parent.getItemAtPosition(position);
		if (obj instanceof OrderInfo) {
			OrderInfo orderInfo = (OrderInfo) obj;
			Intent intent = new Intent(this, OrderActivity.class);

			// TODO
			intent.putExtra("orderId", orderInfo.OrderID);
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			startActivity(intent);
		}
	}

}
