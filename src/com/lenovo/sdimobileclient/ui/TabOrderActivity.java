
package com.lenovo.sdimobileclient.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.foreveross.cache.CacheManager;
import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.OrderCounts;
import com.lenovo.sdimobileclient.api.OrderInfo;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.data.AlarmAlert;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout.OnPullListener;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout.OnPullStateListener;
import com.lenovo.sdimobileclient.utility.AlarmTask;
import com.lenovo.sdimobileclient.utility.Utils;
import com.zhy.http.okhttp.OkHttpUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 工单列表
 * 
 * @author zhangshaofang
 * 
 */
public class TabOrderActivity extends Fragment
		implements OnItemClickListener, OnScrollListener, OnPullListener, OnPullStateListener, Constants, OnClickListener {

	private static final int CALLBACK_INIT = 1;
	private static final int CALLBACK_APPEND = 2;
	private static final int CALLBACK_HISTORY = 3;
	private ApiDataAdapter<OrderInfo> mOrderAdapter;
	private ListView mListView;
	private View mWaitView;
	private Uri mUri = Uri.parse(URL_ORDERLIST).buildUpon().appendQueryParameter(PARAM_PAGECOUNT, String.valueOf(PAGE_COUNT)).build();

	/* Views, widgets, animations & drawables */
	private PullActivateLayout mPullLayout;
	private TextView mActionText;
	private TextView mTimeText;
	private View mProgress;
	private View mActionImage;

	private Animation mRotateUpAnimation;
	private Animation mRotateDownAnimation;
	/* Variable */
	private boolean mInLoading = false;

	private TabOrderNewActivity mRootFragmentActivity;
	private View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mHttpHelper = OkHttpHelper.getInstance(mRootFragmentActivity);
		mView = inflater.inflate(R.layout.order_list, null);
		Activity activity = getActivity();
		mRootFragmentActivity = (TabOrderNewActivity) activity;
		initView();

		// Uri uri = mRootFragmentActivity.getIntent().getData();
		int order_state = getArguments().getInt("order_state");
		// if (uri != null) {// 如果uri不为空，则为工单历史记录
		// mOrderUri = uri;
		// mNextId = 1;
		// mLastUri =
		// mOrderUri.buildUpon().appendQueryParameter(PARAM_PAGEINDEX,
		// String.valueOf(mNextId)).build();
		// // NetworkPath path = new Netpath(mLastUri.toString());
		// // CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// // path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// // mCacheManager.load(CALLBACK_HISTORY, params, this);
		// mLoadId = CALLBACK_HISTORY;
		// OkHttpHelper.load(mLastUri.toString(), mCallBack,
		// TabOrderActivity.class);

		// } else {
		initData(order_state);
		// }
		return mView;
	}

	/**
	 * 数据加载解析
	 */
	public int mLoadId = -1;
	public int errorcount = 0;
	OkHttpStringCallback mCallBack = new OkHttpStringCallback(mRootFragmentActivity) {

		@Override
		protected void LoadError() {
			mRootFragmentActivity.showProgress(mWaitView, false);
			mPullLayout.hideActionView();
		};

		@Override
		public void onResponse(String result) {

			/**
			 * 登录成功，保存用户账号信息及工程师信息
			 */

			mSafe = mHttpHelper.isSuccessResult(result, (Context) mRootFragmentActivity);

			if (!mSafe) {
				mRootFragmentActivity.showProgress(mWaitView, false);
				return;
			}

			switch (mLoadId) {
			case CALLBACK_HISTORY: {// 工单历史记录
				mWaitView.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
				mOrderAdapter.clear();
				RootData rootData;
				rootData = (RootData) new RootData(result);

				List<OrderInfo> orderInfos = rootData.getArrayData(OrderInfo.class);
				if (orderInfos != null) {
					mOrderAdapter.add(orderInfos);
				}
				mNoList.setVisibility(orderInfos.size() == 0 ? View.VISIBLE : View.GONE);
			}
				break;
			case CALLBACK_INIT:// 首次获取

				mListView.setVisibility(View.VISIBLE);
				mOrderAdapter.clear();
			case CALLBACK_APPEND:// 翻页

				dataLoaded();
				mListView.setVisibility(View.VISIBLE);
				mWaitView.setVisibility(View.GONE);
				RootData rootData;
				rootData = (RootData) new RootData(result);

				try {
					String totalNumber = rootData.getJson().getString("TotalNumber");
					int counts = Integer.parseInt(totalNumber);
					String currentIndex = Uri.parse(mLastUri.toString()).getQueryParameter(PARAM_PAGEINDEX);
					int index = Integer.parseInt(currentIndex);
					if (index * PAGE_COUNT < counts) {
						mNextId = ++index;
						mHasData = true;
					} else {
						mNextId = -1;
						mHasData = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				OrderCounts orderCounts = rootData.getData(OrderCounts.class);
				if (orderCounts != null)
					mRootFragmentActivity.initViewCount(orderCounts);
				List<OrderInfo> orderInfos = rootData.getArrayData(OrderInfo.class);

				/**
				 * 之前在本地 这是提醒闹钟 功能已经取消
				 * 
				 * if (!TextUtils.isEmpty(orderState)) { int state =
				 * Integer.parseInt(orderState); /** 检查是否为待联系列表，设置提醒
				 * 
				 * boolean isAlert = false; if (state == ORDER_STATE_UNCONTACT)
				 * { AlarmTask.cancleAlarmTask(mRootFragmentActivity); List
				 * <OrderInfo> infos = orderInfos; for (OrderInfo orderInfo :
				 * infos) { if (!isAlert) { if
				 * (!TextUtils.isEmpty(orderInfo.PreTime)) { AlarmAlert
				 * alarmAlert = AlarmAlert.queryByOrderID(mRootFragmentActivity,
				 * orderInfo.OrderID, orderInfo.PreTime); if (alarmAlert ==
				 * null) { isAlert = true;
				 * AlarmTask.setAlarmTask(mRootFragmentActivity,
				 * orderInfo.OrderID, orderInfo.PreTime); } } }
				 * 
				 * } }
				 * 
				 * }
				 */
				if (orderInfos != null) {
					mOrderAdapter.add(orderInfos);
				}
				if (errorcount != 1 && mOrderAdapter.getCount() == 0) {
					errorcount++;

					onClick(mWaitView.findViewById(R.id.btn_retry));

				} else {
					int count = mOrderAdapter.getCount();
					mNoList.setVisibility(mOrderAdapter.getCount() == 0 ? View.VISIBLE : View.GONE);

					mTimeText.setVisibility(View.VISIBLE);

					mActionText.setVisibility(View.VISIBLE);

				}
				mOrderAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}

		}
	};

	private Uri mLastUri;
	private Uri mOrderUri;
	private int mOrderState;

	/**
	 * 初始化工单列表
	 * 
	 * @param state
	 *            工单类型
	 */
	private void initData(int state) {

		// mCacheManager.cancel(this);
		mOrderState = state;
		mNextId = 1;
		mOrderAdapter.clear();
		mRootFragmentActivity.showProgress(mWaitView, true);
		Uri uri = mUri;
		mOrderUri = uri.buildUpon().appendQueryParameter(PARAM_ORDERSTATE, String.valueOf(state)).build();
		mLastUri = mOrderUri.buildUpon().appendQueryParameter(PARAM_PAGEINDEX, String.valueOf(mNextId)).build();

		// NetworkPath path = new Netpath(mLastUri.toString());
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_INIT, params, this);
		mLoadId = CALLBACK_INIT;
		mHttpHelper.load(mLastUri.toString(), mCallBack, mRootFragmentActivity);
	}

	// TODO
	@Override
	public void onResume() {
		super.onResume();

		boolean today = Utils.isToday(System.currentTimeMillis());
		mHttpHelper.cancle(MainActivity.class);
		if (needRefrash) {

			if (today) {

				mListView.setVisibility(View.GONE);
				mWaitView.setVisibility(View.VISIBLE);
				mRootFragmentActivity.showProgress(mWaitView, true);
				startLoading();

			} else {
				Utils.clearEngineerInfo(mRootFragmentActivity);
				mHttpHelper.cancle(mRootFragmentActivity.getClass());
				Intent intent = new Intent(mRootFragmentActivity, LoginActivity.class);
				startActivity(intent);
				mRootFragmentActivity.finish();
			}
		}
	}

	/**
	 * 分页获取
	 */
	private void appendOrderListData() {
		mLastUri = mOrderUri.buildUpon().appendQueryParameter(PARAM_PAGEINDEX, String.valueOf(mNextId)).build();
		// NetworkPath path = new Netpath(mLastUri.toString());
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_APPEND, params, this);
		//
		mLoadId = CALLBACK_APPEND;
		mHttpHelper.load(mLastUri.toString(), mCallBack, mRootFragmentActivity);

	}

	/**
	 * 页面初始化
	 */
	private void initView() {
		mListView = (ListView) mView.findViewById(android.R.id.list);
		mNoList = mView.findViewById(R.id.tv_nolist);
		mListView.setVisibility(View.GONE);
		mListView.setDivider(new ColorDrawable(mRootFragmentActivity.getResources().getColor(R.color.transparent)));
		mListView.setDividerHeight(13);
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);
		// mWaitView =
		// getActivity().getLayoutInflater().inflate(R.layout.progress, null);
		mWaitView = mView.findViewById(R.id.wait_progress);
		mWaitView.setVisibility(View.VISIBLE);
		mWaitView.findViewById(R.id.btn_retry).setOnClickListener(this);
		// mListView.addFooterView(mWaitView);
		mOrderAdapter = new ApiDataAdapter<OrderInfo>(mRootFragmentActivity);
		mListView.setAdapter(mOrderAdapter);

		/**
		 * 下拉刷新
		 */
		mRotateUpAnimation = AnimationUtils.loadAnimation(mRootFragmentActivity, R.anim.rotate_up);
		mRotateDownAnimation = AnimationUtils.loadAnimation(mRootFragmentActivity, R.anim.rotate_down);

		mPullLayout = (PullActivateLayout) mView.findViewById(R.id.pull_container);
		mPullLayout.setOnActionPullListener(this);
		mPullLayout.setOnPullStateChangeListener(this);

		mProgress = mView.findViewById(android.R.id.progress);
		mActionImage = mView.findViewById(android.R.id.icon);
		mActionText = (TextView) mView.findViewById(R.id.pull_note);
		mTimeText = (TextView) mView.findViewById(R.id.refresh_time);

		mTimeText.setText(R.string.note_not_update);
		mActionText.setText(R.string.note_pull_down);
	}

	@Override
	public void onClick(View v) {
		// mCacheManager.cancel(this);
		switch (v.getId()) {
		case R.id.btn_retry:
			initData(mOrderState);
			break;
		default:
			break;
		}
	}

	public static int INTOORDERDETIL = 9001;
	protected static boolean needRefrash = true;

	/**
	 * 点击某工单进入，该工单处理页面
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object obj = parent.getItemAtPosition(position);
		if (obj instanceof OrderInfo) {
			OrderInfo orderInfo = (OrderInfo) obj;
			Intent intent = new Intent(mRootFragmentActivity.getParent(), OrderActivity.class);

			// TODO
			intent.putExtra("orderId", orderInfo.OrderID);
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			startActivity(intent);

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		switch (requestCode) {
		case 120:

			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	private int mNextId = 0;
	private boolean mSafe;
	private boolean mHasData;
	private View mNoList;
	private OkHttpHelper mHttpHelper;

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
			mRootFragmentActivity.showProgress(mWaitView, true);
			appendOrderListData();
		}
	}

	public void dataLoaded(int id, CacheParams params, ICacheInfo result) {
		/*
		 * mSafe = ActivityUtils.prehandleNetworkData(mRootFragmentActivity,
		 * this, id, params, result, false); if (!mSafe) {
		 * mRootFragmentActivity.showProgress(mWaitView, false); return; }
		 * switch (id) { case CALLBACK_HISTORY: {// 工单历史记录
		 * mWaitView.setVisibility(View.GONE);
		 * mListView.setVisibility(View.VISIBLE); mOrderAdapter.clear();
		 * RootData rootData = (RootData) result.getData(); List<OrderInfo>
		 * orderInfos = rootData.getArrayData(OrderInfo.class); if (orderInfos
		 * != null) { mOrderAdapter.add(orderInfos); }
		 * mNoList.setVisibility(orderInfos.size() == 0 ? View.VISIBLE :
		 * View.GONE); } break; case CALLBACK_INIT:// 首次获取
		 * 
		 * mListView.setVisibility(View.VISIBLE); mOrderAdapter.clear(); case
		 * CALLBACK_APPEND:// 翻页
		 * 
		 * NetworkPath path = params.path; Uri uri = Uri.parse(path.url);
		 * 
		 * dataLoaded(); mListView.setVisibility(View.VISIBLE);
		 * mWaitView.setVisibility(View.GONE); RootData rootData = (RootData)
		 * result.getData(); try { String totalNumber =
		 * rootData.getJson().getString("TotalNumber"); int counts =
		 * Integer.parseInt(totalNumber);
		 * 
		 * String currentIndex =
		 * Uri.parse(params.path.url).getQueryParameter(PARAM_PAGEINDEX); int
		 * index = Integer.parseInt(currentIndex); if ((index + 1) * PAGE_COUNT
		 * < counts) { mNextId = ++index; mHasData = true; } else { mNextId =
		 * -1; mHasData = false; } } catch (JSONException e) {
		 * e.printStackTrace(); } OrderCounts orderCounts =
		 * rootData.getData(OrderCounts.class); if (orderCounts != null)
		 * mRootFragmentActivity.initViewCount(orderCounts); List<OrderInfo>
		 * orderInfos = rootData.getArrayData(OrderInfo.class); String
		 * orderState = uri.getQueryParameter(PARAM_ORDERSTATE); if
		 * (!TextUtils.isEmpty(orderState)) { int state =
		 * Integer.parseInt(orderState); /* 检查是否为待联系列表，设置提醒
		 *
		 * boolean isAlert = false; if (state == ORDER_STATE_UNCONTACT) {
		 * AlarmTask.cancleAlarmTask(mRootFragmentActivity); List<OrderInfo>
		 * infos = orderInfos; for (OrderInfo orderInfo : infos) { if (!isAlert)
		 * { if (!TextUtils.isEmpty(orderInfo.PreTime)) { AlarmAlert alarmAlert
		 * = AlarmAlert.queryByOrderID(mRootFragmentActivity, orderInfo.OrderID,
		 * orderInfo.PreTime); if (alarmAlert == null) { isAlert = true;
		 * AlarmTask.setAlarmTask(mRootFragmentActivity, orderInfo.OrderID,
		 * orderInfo.PreTime); } } }
		 * 
		 * } }
		 * 
		 * } if (orderInfos != null) { mOrderAdapter.add(orderInfos); }
		 * 
		 * mNoList.setVisibility(mOrderAdapter.getCount() == 0 ? View.VISIBLE :
		 * View.GONE);
		 * 
		 * mTimeText.setVisibility(View.VISIBLE);
		 * 
		 * mActionText.setVisibility(View.VISIBLE); break;
		 * 
		 * default: break; }
		 */}

	/**
	 * 下拉刷新，数据加载完成调用
	 */
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

			mTimeText.setText(getString(R.string.note_update_at, DateFormat.getTimeFormat(mRootFragmentActivity).format(new Date(System.currentTimeMillis()))));
		}
	}

	@Override
	public void onDestroyView() {

		mHttpHelper.cancle(mRootFragmentActivity.getClass());
		mLoadId = -1;
		super.onDestroyView();
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
	public void onPullIn() {
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_down);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateDownAnimation);
		}

	}

	/**
	 * 下拉刷新
	 */
	private void startLoading() {

		mNoList.setVisibility(View.GONE);
		if (!mInLoading) {
			mInLoading = true;
			mPullLayout.setEnableStopInActionView(true);
			mActionImage.clearAnimation();
			mActionImage.setVisibility(View.GONE);

			mTimeText.setVisibility(View.GONE);
			mProgress.setVisibility(View.GONE);

			mActionText.setVisibility(View.GONE);

			mActionText.setText(R.string.note_pull_loading);

			initData(mOrderState);
		}
	}

	@Override
	public void onSnapToTop() {
		startLoading();

	}

	@Override
	public void onShow() {

	}

	@Override
	public void onHide() {

	}

	public void showProgress(TabOrderNewActivity tabOrderNewActivity) {

		mWaitView = tabOrderNewActivity.getLayoutInflater().inflate(R.layout.progress, null);
		mWaitView.setVisibility(View.VISIBLE);
		tabOrderNewActivity.showProgress(mWaitView, true);
	}
}
