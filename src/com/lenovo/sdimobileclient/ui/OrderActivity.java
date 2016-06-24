package com.lenovo.sdimobileclient.ui;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.google.gson.Gson;
import com.google.zxing.qrcode.encoder.QRCode;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Attachment;
import com.lenovo.sdimobileclient.api.AttachmentTypes;
import com.lenovo.sdimobileclient.api.CustomerPhone;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.api.OrderInfo;
import com.lenovo.sdimobileclient.api.OrderOperations;
import com.lenovo.sdimobileclient.api.OrderTask;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.api.SourceOption;
import com.lenovo.sdimobileclient.api.TaskHistory;
import com.lenovo.sdimobileclient.api.TaskSource;
import com.lenovo.sdimobileclient.data.Attach;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.service.UploadAttachService;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.ui.adapter.bean.ComeTypes;
import com.lenovo.sdimobileclient.ui.adapter.bean.CometypesBean;
import com.lenovo.sdimobileclient.ui.dialog.Check1Dialog;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout;
import com.lenovo.sdimobileclient.utility.AlarmTask;
import com.lenovo.sdimobileclient.utility.Utils;
import com.lenovo.sdimobilecllient.camer.MipcaActivityCapture;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class OrderActivity extends RootActivity implements OnGetGeoCoderResultListener {

	private static final int CALLBACK_ORDER = 1;
	private static final int CALLBACK_SUBMIT_MA_UNCONTACT = 2;
	private static final int CALLBACK_SUBMIT_CHECKHOST = 3;
	private static final int CALLBACK_SIGNARRIVED = 4;
	private static final int CALLBACK_RAPIR_RESULT = 5;
	private static final int CALLBACK_TOBEFINISHED = 6;
	private static final int CALLBACK_ORDERTASKSOURCES = 7;
	private static final int CALLBACK_UNMA_SUBMIT = 8;
	private static final int CALLBACK_AMOUNT = 9;

	private static final int CALLBACK_GETQR = 11;

	private static final int CALLBACK_SEND_ORDER = 10;
	private static final int DLG_PROCESS = 5001;
	private static final int DLG_USER = 5002;
	private static final int DLG_SOURCEOPTIONS = 5003;

	public static final int RAPIR_RESULT_REPAIRED = 1;
	public static final int RAPIR_RESULT_UNREPAIR = 2;
	public static final int RAPIR_RESULT_UNPROCESS = 3;
	public static final int RAPIR_RESULT_REDICET = 4;
	public static final int RAPIR_RESULT_DOORING = 5;
	public static final int REQUEST_CODE_CONTACED_HOST = 301;
	public static final int REQUEST_CODE_ORDERPROGRESS = 302;
	private static final int REQUESTCODE_ADDREPLACE = 3001;

	private static final int REQUESTCODE_CHECK1DIALOG = 4001;
	private static final int DLG_DOSUBMIT = 6001;

	private LayoutInflater mInflater;
	private View mHeaderView;
	// private View mWaitView;
	// private CacheManager mCacheManager;
	private OrderInfo mOrderInfo;
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

	private ArrayList<TaskHistory> mHistoryList;
	private TextView mBtnNext;
	private TextView mMyselectEdit = null;
	private ComeTypes bean3;
	private boolean newlcation = true;
	private OkHttpHelper mHttpHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBackBtn();
		mHttpHelper = OkHttpHelper.getInstance(this);
		/**
		 * 取消消息提醒
		 */
		AlarmTask.cancleAlarmTask(this);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);// 进入页面禁止滑出
		// mCacheManager = getAppliction().getCacheManager();
		mInflater = LayoutInflater.from(this);

		initView();
		init();
	}

	/**
	 * 获取工单详情
	 * 
	 * 先是上面的内容
	 * 
	 * 不包含包含可不信息按钮单并不包括历史维修
	 * 
	 */
	private void init() {
		mWaitProgress = findViewById(R.id.wait_progress);
		mWaitProgress.setVisibility(View.VISIBLE);
		// showProgress(mWaitView, true);

		OrderInfo orderInfo = (OrderInfo) getIntent().getSerializableExtra("order");

		if (orderInfo == null) {

			String orderId = getIntent().getStringExtra("orderId");
			EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(this);
			Uri uri = Uri.parse(URL_ORDERINFO).buildUpon().appendQueryParameter(PARAM_ORDERID, String.valueOf(orderId))
					.appendQueryParameter(PARAM_ENGINEER, enginnerInfo.EngineerID).build();
			// NetworkPath path = new Netpath(uri.toString());
			// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
			// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
			// mCacheManager.load(CALLBACK_ORDER, params, this);
			mLoadId = CALLBACK_ORDER;
			mHttpHelper.load(uri.toString(), mCallback, this);

		} else {

			newlcation = false;
			initView(orderInfo);
		}

	}

	private View mBtnSendOrder;

	private void initView() {

		replaceView = findViewById(R.id.btn_replace);
		replaceView.setOnClickListener(this);
		unReplaceView = findViewById(R.id.btn_unreplace);
		unReplaceView.setOnClickListener(this);
		attachView = findViewById(R.id.btn_attachment);
		attachView.setOnClickListener(this);
		/**
		 * TODO 发票逻辑和pc 不一致. 已gone.
		 */
		invoiceView = findViewById(R.id.btn_invoice);
		invoiceView.setOnClickListener(this);
		lineReplace = findViewById(R.id.line_replace);
		lineUnReplace = findViewById(R.id.line_unreplace);
		lineAttach = findViewById(R.id.line_attach);
		lineInvoice = findViewById(R.id.line_invoice);

		findViewById(R.id.btn_box_m).setOnClickListener(this);
		findViewById(R.id.btn_repair_search).setOnClickListener(this);
		findViewById(R.id.btn_text).setOnClickListener(this);
		mRightMenu = (LinearLayout) findViewById(R.id.right);
		/**
		 * 下拉刷新
		 */

		mRotateUpAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_up);
		mRotateDownAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_down);

		mPullLayout = (PullActivateLayout) findViewById(R.id.pull_container);

		mProgress = findViewById(android.R.id.progress);
		mActionImage = findViewById(android.R.id.icon);
		mActionText = (TextView) findViewById(R.id.pull_note);
		mTimeText = (TextView) findViewById(R.id.refresh_time);

		mTimeText.setText(R.string.note_not_update);
		mActionText.setText(R.string.note_pull_down);

		mListView = (ListView) findViewById(android.R.id.list);
		mHeaderView = mInflater.inflate(R.layout.orderinfo_view, null);
		mHeaderView.setVisibility(View.GONE);
		mListView.addHeaderView(mHeaderView);
		View btnNextView = findViewById(R.id.layout_btn_next);
		mBtnSendOrder = btnNextView.findViewById(R.id.btn_send_order);
		mBtnSendOrder.setOnClickListener(this);
		mBtnNext = (TextView) btnNextView.findViewById(R.id.btn_next);
		mBtnNext.setVisibility(View.GONE);

		mBtnNext.setOnClickListener(this);
		mApiDataAdapter = new ApiDataAdapter<OrderInfo>(this);

		mListView.setAdapter(mApiDataAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				SparseArray<View> viewMap = (SparseArray<View>) view.getTag();
				if (viewMap != null && viewMap.size() != 0) {
					View view2 = viewMap.get(R.id.tv_taskname);
					mMyselectEdit = (TextView) viewMap.get(R.id.tv_select_sourceoption);

					if (null != view2) {
						ComeTypes bean = (ComeTypes) view2.getTag();
						if (null != bean) {
							mSourceOptions = bean.TabControls;

							showDialog(DLG_SOURCEOPTIONS);
						}
					}

					if (bean3 == null) {
						bean3 = new ComeTypes();
						bean3.type = "30";

						bean3.rule = 4;
						bean3.position = position;
						// view2.setTag(1, bean3.rule);
					}
				}

			}

		});
	}

	private int mLoadId = -1;
	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {
			dataLoaded();
			boolean safe = mHttpHelper.isSuccessResult(result, OrderActivity.this);

			switch (mLoadId) {
			case CALLBACK_GETQR:
				mWaitProgress.setVisibility(View.GONE);
				break;
			case CALLBACK_SUBMIT_MA_UNCONTACT:
			case CALLBACK_SIGNARRIVED:
			case CALLBACK_RAPIR_RESULT:
			case CALLBACK_TOBEFINISHED:
			case CALLBACK_UNMA_SUBMIT:
			case CALLBACK_AMOUNT:
				dismisDialog(DLG_SENDING);
				mBtnNext.setEnabled(true);
				break;
			case CALLBACK_SEND_ORDER:
				dismisDialog(DLG_SENDING);
				break;
			case CALLBACK_SUBMIT_CHECKHOST:
				dismisDialog(DLG_CHECKHOST);
				mBtnCheckHost.setEnabled(true);
				break;
			case CALLBACK_ORDERTASKSOURCES:
				mWaitProgress.setVisibility(View.GONE);
				// showProgress(mTaskWaitView, false);
				break;
			case CALLBACK_ORDER:
				// showProgress(mWaitView, false);
				mWaitProgress.setVisibility(View.GONE);
				break;
			default:
				break;
			}

			if (!safe) {
				return;
			}
			switch (mLoadId) {
			case CALLBACK_SEND_ORDER:

				Utils.showToast(OrderActivity.this, "补发工单");
				break;
			case CALLBACK_AMOUNT:
				finish();
				break;

			case CALLBACK_GETQR: {

				RootData rootData = new RootData(result);

				String s = rootData.ResultMsg;

				try {
					JSONObject jsonObject = new JSONObject(s);
					String s1 = jsonObject.get("data").toString();
					JSONObject jsonObject1 = new JSONObject(s1);
					String Qrcode = jsonObject1.get("url").toString();

					Log.e("qrc", Qrcode);

					if (!TextUtils.isEmpty(Qrcode)) {
						findViewById(R.id.QRweb).setVisibility(View.VISIBLE);
						loadQRCode(Qrcode);

					} else {
						findViewById(R.id.QRweb).setVisibility(View.GONE);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

				break;
			case CALLBACK_ORDER: {

				mRootData = new RootData(result);
				OrderInfo orderInfo = mRootData.getData(OrderInfo.class);
				mOrderOperations = mRootData.getData(OrderOperations.class);
				mAttachTypes = mRootData.getArrayData(AttachmentTypes.class);

				initDrawer();
				initView(orderInfo);
				newlcation = true;

			}
				break;
			case CALLBACK_SUBMIT_MA_UNCONTACT:

				OrderActivity.this.setResult(Activity.RESULT_OK, new Intent());

				Utils.showToast(OrderActivity.this, "提交成功");
				finish();
				break;
			case CALLBACK_SUBMIT_CHECKHOST: {
				try {
					JSONObject jsonObject = new JSONObject(result);

					String VerifyResult = jsonObject.getString("VerifyResult");
					if (TextUtils.equals(VerifyResult, "001")) {

						Utils.showToast(OrderActivity.this, "验证通过");
						String ProductSN = jsonObject.getString("ProductSN");
						mContactedBarCodeTv.setText(ProductSN);
					} else {
						Utils.showToast(OrderActivity.this, "实物主机号与工单不符，请核对后再维修！");
						mContactedBarCodeTv.setText("");
					}

				} catch (Exception e) {
				}
			}
				break;
			case CALLBACK_SIGNARRIVED: {
				try {
					JSONObject json = new JSONObject(result);

					String msg = (String) json.get("WarningMsg");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				OrderActivity.this.setResult(Activity.RESULT_OK, new Intent());
				mApiDataAdapter.clearall();
				ClearAll();
				startLoading();

			} // finish();
				break;
			case CALLBACK_RAPIR_RESULT:
				finish();
				break;
			case CALLBACK_TOBEFINISHED:

				finish();
				break;
			case CALLBACK_ORDERTASKSOURCES: {

				// mTaskWaitView.setVisibility(View.GONE);
				mWaitProgress.setVisibility(View.GONE);
				// result 这个是 下面是数据的集合. 我们可以从中找到下面的框有哪几种
				// 现在需要找到这个数据是从哪个接口 下载的json

				RootData rootData = new RootData(result);
				List<OrderTask> orderTasks = rootData.getArrayData(OrderTask.class);
				if (orderTasks != null && !orderTasks.isEmpty()) {
					OrderTask orderTask = orderTasks.get(0);
					// if (!mTypeMA)
					// setTitle(orderTask.TaskID);
					List<TaskSource> taskSources = orderTask.getArrayData(TaskSource.class);

					// 获取到工单进度的信息a-mHistoryList
					mHistoryList = orderTask.getArrayData(TaskHistory.class);
					mOrderTask = orderTask;

					// 显示工单进度按钮
					if (mHistoryList != null && !mHistoryList.isEmpty()) {
						if (mBtnOrderProcess != null)
							mBtnOrderProcess.setVisibility(View.VISIBLE);
					}

					if (taskSources != null && !taskSources.isEmpty()) {
						mBtnNext.setVisibility(View.VISIBLE);
						TaskSource taskSource = taskSources.get(0);
						mTaskSource = taskSource;
						JSONObject json = taskSource.getJson();
						String string = json.toString();
						Gson gson = new Gson();
						CometypesBean fromJson = gson.fromJson(string, CometypesBean.class);

						mComeTypes = fromJson.ComeTypes;
						// 把要显示的数据填充到集合里面去
						if (null != mApiDataAdapter) {
							mApiDataAdapter.clear();
						}
						ComeTypes comeTypes = new ComeTypes();
						comeTypes.type = "501";
						comeTypes.Come = orderTask.TaskName;
						mComeTypes.add(0, comeTypes);
						mApiDataAdapter.addData(mComeTypes);

						mListView.smoothScrollToPosition(mApiDataAdapter.getCount() - 1);
						// 115 待维修
						// 120 维修中
						// 140 待取机
						// 130 待通知
						// 190 待服务完
						// 60 已分派
						// 65 已联系
						// 70 上门中
						// 80 服务中
						// 330 站端已接收
						// 335 待站端接收
						// 200 服务完

						if (mState != 40 && mState != 60 && mState != 65 && mState != 70 && mState != 80 && mState != 115 && mState != 120 && mState != 130
								&& mState != 140 && mState != 190 && mState != 335 && mState != 330) {

							// mBtnNext.setVisibility(View.GONE);

							mBtnNext.setTextColor(getResources().getColor(R.color.order_textcolor));
							mBtnNext.setText("当前状态工单已转至协调员在PC端处理！");
							mBtnNext.setEnabled(false);
						}
						String OrderTypeName = mOrderInfo.OrderTypeName;

						if (OrderTypeName.contains("MA") && TextUtils.equals(orderTask.TaskName, "回填上门结果")) {
							// to-do
							View Changge = OrderActivity.this.findViewById(R.id.change_layout);
							Changge.setVisibility(View.VISIBLE);

							findViewById(R.id.change).setOnClickListener(OrderActivity.this);
							findViewById(R.id.unchange).setOnClickListener(OrderActivity.this);
						}

					} else if (mState == 200) {

						mBtnNext.setVisibility(View.GONE);
						GetQRCode();
					} else {
						mBtnNext.setVisibility(View.VISIBLE);
						mBtnNext.setText("服务完");
					}

				}

			}
				break;
			case CALLBACK_UNMA_SUBMIT:
				finish();
				break;
			}
		}

	};

	private boolean mTypeMA;
	private LinearLayout mOrderLayout;
	private int mState;

	/**
	 * 初始化工单详情
	 * 
	 * @param orderInfo
	 */
	private void initView(OrderInfo orderInfo) {

		mTaskDesc = orderInfo.TaskDescriptions;
		setTitle(orderInfo.OrderID);
		findViewById(R.id.title).setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				TextView textView = (TextView) v;

				ClipboardManager cm = (ClipboardManager) getSystemService(OrderActivity.this.CLIPBOARD_SERVICE);

				cm.setText(textView.getText().toString());

				Utils.showToast(OrderActivity.this, "内容\"" + textView.getText().toString() + "\"已复制到剪贴板");

				return false;
			}
		});

		mBtnSendOrder.setVisibility(View.GONE);
		initRightBtn();

		mWaitProgress.setVisibility(View.GONE);
		mHeaderView.setVisibility(View.VISIBLE);
		mOrderInfo = orderInfo;

		ApiDataAdapter<OrderInfo> orderAdapter = new ApiDataAdapter<OrderInfo>(this);
		mOrderLayout = (LinearLayout) mHeaderView.findViewById(R.id.order_layout);
		mOrderLayout.removeAllViews();
		orderInfo.IsOrderList = false;
		mOrderView = orderAdapter.getItemView(orderInfo);

		View findViewById = mOrderView.findViewById(R.id.order_host_fault);

		if (null != findViewById)
			findViewById.setOnClickListener(this);
		mOrderView.setOnTouchListener(mOnTouchListener);
		mOrderLayout.addView(mOrderView);
		if (orderInfo.AttachCount != 0)
			mOrderView.findViewById(R.id.order_count).setOnClickListener(this);
		if (orderInfo.RepairTimes != 0) {
			mOrderView.findViewById(R.id.rapir_count).setOnClickListener(this);
		}
		mSign = false;
		mFault = false;
		/**
		 * 获取工单类型，根据类型初始化相应页面
		 */

		mTypeMA = !TextUtils.isEmpty(mOrderInfo.OrderTypeName) && TextUtils.equals(mOrderInfo.OrderTypeName, "MA标准");
		if (!TextUtils.isEmpty(orderInfo.OrderStateID))

			mState = Integer.parseInt(orderInfo.OrderStateID);

		if (newlcation) {
			getGeocoder();
		}

		initServeingData();
		initUnMaProcess(orderInfo);
		if (mState == ORDER_STATE_UNCONTACT) {

			showCustomer(isShowCustomer);
		}
		initListHist();
	}

	private void initListHist() {

		if (newlcation) {

			mSoListHists = new ArrayList<OrderInfo>();

			if (mOrderInfo != null) {
				JSONObject json = mOrderInfo.getJson();
				Gson gson = new Gson();

				try {
					JSONArray jsonArray = json.getJSONArray("SOListHist");

					for (int i = 0; i < jsonArray.length(); i++) {

						JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
						JSONObject st = (JSONObject) jsonObject.get("OrderDetail");
						OrderInfo order = gson.fromJson(st.toString(), OrderInfo.class);
						mSoListHists.add(order);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}
	}

	/**
	 * 服务中
	 */
	private void initServeingData() {
		if (!TextUtils.isEmpty(mOrderInfo.CurrentTaskCode)) {
			if (mOrderInfo.CurrentTaskCode.equals("611")) {
				mFault = true;
				mBtnNext.setText("提交");
				mBtnNext.setVisibility(View.VISIBLE);
			}
		} else if (mState == 200) {

		}

	}

	/**
	 * 初始化非MA流程页面
	 * 
	 * @param orderInfo
	 */
	private void initUnMaProcess(OrderInfo orderInfo) {

		View orderCusProView = mInflater.inflate(R.layout.order_customer_progress, null);
		TextView customerName = (TextView) orderCusProView.findViewById(R.id.btn_customer_name);
		customerName.setText(orderInfo.CustomerName);
		orderCusProView.findViewById(R.id.btn_customer_info).setOnClickListener(this);
		orderCusProView.findViewById(R.id.btn_order_details).setOnClickListener(this);
		mBtnOrderProcess = orderCusProView.findViewById(R.id.btn_order_process);
		mBtnOrderProcess.setOnClickListener(this);
		mBtnOrderProcess.setVisibility(View.GONE);
		mOrderLayout.addView(orderCusProView);

		// mWaitProgress = mInflater.inflate(R.layout.progress, null);
		mWaitProgress.setVisibility(View.VISIBLE);
		mWaitProgress.findViewById(R.id.btn_retry).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadOrderTaskSources();
			}
		});
		// mOrderLayout.addView(mTaskWaitView);
		loadOrderTaskSources();
	}

	private View mBtnOrderProcess;
	private View mTaskWaitView;

	/**
	 * 非MA时调用，获取工单处理操作
	 * 
	 * 下面任务的调取
	 * 
	 */
	private void loadOrderTaskSources() {
		mWaitProgress.setVisibility(View.VISIBLE);
		// showProgress(mTaskWaitView, true);
		EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(this);
		Uri uri = Uri.parse(URL_ORDERTASKSOURCES).buildUpon().appendQueryParameter(PARAM_ORDERID, mOrderInfo.OrderID)
				.appendQueryParameter(PARAM_ENGINEER, enginnerInfo.EngineerID).build();
		// NetworkPath path = new Netpath(uri.toString());
		// CacheParams params = new CacheParams(path);
		// mCacheManager.load(CALLBACK_ORDERTASKSOURCES, params, this);
		mLoadId = CALLBACK_ORDERTASKSOURCES;
		mHttpHelper.load(uri.toString(), mCallback, this);
	}

	private TextView mContactedBarCodeTv;
	private View mBtnCheckHost;

	private boolean mSign;
	private boolean mFault;
	private EditText mFaultResult;
	private EditText mHostNum;
	private String mCheck2;
	private ComeTypes mWriteNamedata;
	private boolean isShowCustomer = false;
	private ArrayList<TaskHistory> HistoryList;
	private boolean flag = false;

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// TODO 提交流程
		case R.id.btn_next:

			showDialog(DLG_DOSUBMIT);

			break;
		case R.id.btn_order_left: { // 上一个
			Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
			intent.putExtra("orderId", mOrderInfo.P_OrderID);
			startActivity(intent);
			overridePendingTransition(R.anim.in_to_left, R.anim.out_from_right);
			finish();
		}
			break;
		case R.id.btn_order_right: {// 下一个
			Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
			intent.putExtra("orderId", mOrderInfo.N_OrderID);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			finish();
		}
			break;
		// 换件
		case R.id.change:
		case R.id.btn_replace: {
			mDrawerLayout.closeDrawer(GravityCompat.END);
			Intent intent = new Intent(this, ReplaceActivity.class);
			intent.putExtra("orderId", mOrderInfo.OrderID);
			intent.putExtra("ProductSN", mOrderInfo.ProductSN);
			if (mOrderOperations != null) {
				intent.putExtra("replace", mOrderOperations.Reservation_Change_Add);
				intent.putExtra("unreplace", mOrderOperations.Reservation_Unchange_Add);
			}

			startActivityForResult(intent, REQUESTCODE_ADDREPLACE);
		}
			break;
		// 非换件
		case R.id.unchange:
		case R.id.btn_unreplace: {
			mDrawerLayout.closeDrawer(GravityCompat.END);
			Intent intent = new Intent(this, UnReplaceActivity.class);
			intent.putExtra("orderId", mOrderInfo.OrderID);
			if (mOrderOperations != null) {
				intent.putExtra("replace", mOrderOperations.Reservation_Change_Add);
				intent.putExtra("unreplace", mOrderOperations.Reservation_Unchange_Add);
			}
			startActivity(intent);
		}
			break;
		// 上传附件
		case R.id.btn_attachment: {
			mDrawerLayout.closeDrawer(GravityCompat.END);
			Intent intent = new Intent(this, AttachUploadActivity.class);
			if (mAttachTypes != null) {
				intent.putParcelableArrayListExtra("attach_types", mAttachTypes);
			}
			intent.putExtra("orderId", mOrderInfo.OrderID);
			startActivity(intent);
		}
			break;
		// 发票
		case R.id.btn_invoice: {
			mDrawerLayout.closeDrawer(GravityCompat.END);
			Intent intent = new Intent(this, InvoiceUploadActivity.class);
			intent.putExtra("orderId", mOrderInfo.OrderID);
			startActivity(intent);
		}
			break;
		case R.id.btn_order_info:
			mDrawerLayout.closeDrawer(GravityCompat.END); {
			if (mRootData != null) {
				Intent intent = new Intent(this, OrderInfoActivity.class);
				intent.putExtra("host_id", mOrderInfo.ProductSN);
				intent.putExtra("host_type", mOrderInfo.ProductType);
				intent.putExtra("rootData", mRootData.getJson().toString());
				startActivity(intent);
			}
		}
			break;
		case R.id.btn_send_order:
			// xxxxxx
			// Toast.makeText(this, "补发工单", Toast.LENGTH_SHORT).show();
			sendOrder();
			break;
		case R.id.btn_box_m: {
			mDrawerLayout.closeDrawer(GravityCompat.END);
			Intent intent = new Intent(this, HostSearchActivity.class);
			intent.putExtra("type", 1);
			intent.putExtra("host_id", mOrderInfo.ProductSN);
			startActivity(intent);
		}
			break;
		case R.id.btn_repair_search: {
			mDrawerLayout.closeDrawer(GravityCompat.END);
			Intent intent = new Intent(this, HostSearchActivity.class);
			intent.putExtra("host_id", mOrderInfo.ProductSN);
			startActivity(intent);
		}
			break;
		case R.id.btn_text: {
			mDrawerLayout.closeDrawer(GravityCompat.END);
			Intent intent = new Intent(this, TabSupportActivity.class);
			intent.putExtra("host_id", mOrderInfo.ProductType);
			startActivity(intent);
		}
			break;
		case R.id.btn_check:
			// checkHostCode();
			break;
		case R.id.btn_read_code: {

			// scanCode();
		}
			break;
		case R.id.btn_retry:
			init();
			break;
		case R.id.btn_order_process: {

			if (mHistoryList != null && !mHistoryList.isEmpty()) {
				Intent intent = new Intent(this, OrderProgressActivity.class);
				HistoryList = (ArrayList) mHistoryList;
				intent.putExtra("Task", mOrderTask);
				intent.putExtra("SOTaskID", mOrderInfo.LastSOTaskID);
				intent.putExtra("OrderID", mOrderInfo.OrderID);
				intent.putExtra("State", mState);

				intent.putParcelableArrayListExtra("HistoryList", HistoryList);

				startActivityForResult(intent, REQUEST_CODE_ORDERPROGRESS);
			} else {
				Utils.showToast(this, "暂无工单进度");
			}
		}
			// showDialog(DLG_PROCESS);
			break;
		case R.id.btn_cancle:
			dismisDialog(DLG_PROCESS);
			break;

		/**
		 * 查看换上件信息
		 */

		case R.id.rapir_count: {
			Intent intent = new Intent(this, CheckReplaceActivity.class);
			intent.putExtra("orderId", mOrderInfo.OrderID);
			intent.putExtra("ProductSN", mOrderInfo.ProductSN);
			if (mOrderOperations != null) {
				intent.putExtra("replace", mOrderOperations.Reservation_Change_Add);
				intent.putExtra("unreplace", mOrderOperations.Reservation_Unchange_Add);
			}
			startActivity(intent);
		}
			break;
		case R.id.order_count: {
			Intent intent = new Intent(this, AttachmentListActivity.class);
			intent.putExtra("orderId", mOrderInfo.OrderID);
			ArrayList<Attachment> attachments = mOrderInfo.getArrayData(Attachment.class);
			intent.putParcelableArrayListExtra("attachs", attachments);
			startActivity(intent);
		}
			break;
		case R.id.btn_phone: {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + mOrderInfo.ContactPhone));
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			startActivity(intent);
		}
			break;
		case R.id.btn_customer_info:
		case R.id.btn_more:
			showCustomer(isShowCustomer);

			break;
		// case R.id.btn_cancle_u:
		// dismisDialog(DLG_USER);
		// break;
		case R.id.btn_msg: {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("smsto:" + mOrderInfo.ContactPhone));
			startActivity(intent);

		}
			break;
		case R.id.btn_address: {

			/**
			 * 调启网页版百度地图
			 */

			if (getAppliction().getCoordinate() == null) {
				Utils.showToast(this, "暂时无法获取您的位置,请检查定位服务是否打开");
				break;
			}
			LatLng ptStart = new LatLng(getAppliction().getCoordinate().getLatitude(), getAppliction().getCoordinate().getLongitude());

			RouteParaOption para = new RouteParaOption().startPoint(ptStart).endName(mOrderInfo.CustomerAddress);

			try {
				BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, this);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
			break;
		case R.id.step_4_radio_one:
			resultView(v);
			break;
		case R.id.step_4_radio_two:
			resultView(v);
			break;
		case R.id.step_4_radio_three:
			resultView(v);
			break;
		case R.id.step_4_radio_four:
			resultView(v);
			break;
		case R.id.step_4_radio_five:
			resultView(v);
			break;
		case R.id.btn_order_details:
			Intent intent = new Intent(this, OrderInfoActivity.class);
			intent.putExtra("customerInfo", mOrderInfo);

			ArrayList<CustomerPhone> arrayData = mOrderInfo.getArrayData(CustomerPhone.class);
			intent.putExtra("solisthist", mSoListHists);
			intent.putExtra("customerPhones", arrayData);
			intent.putExtra("Task", mOrderTask);
			intent.putExtra("mHistoryList", mHistoryList);

			startActivity(intent);

			break;

		case R.id.btn_right: {
			mDrawerLayout.openDrawer(GravityCompat.END);
		}
			break;
		case R.id.btn_back:

			finish();
			break;

		case R.id.order_host_fault:

			TextView view = (TextView) v;

			view.setSingleLine(flag);

			flag = !flag;
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	private void doSubmit() {
		if (mComeTypes != null && bean3 != null && mComeTypes.contains(bean3)) {
			submitUnContactData();
		} else {
			submitServeingResult();
		}
	}

	private View replaceView = null;
	private View unReplaceView = null;
	private View attachView = null;
	private View invoiceView = null;
	private View lineReplace, lineUnReplace, lineAttach, lineInvoice;
	protected LinearLayout mRightMenu;

	private void initDrawer() {
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		if (mOrderOperations != null) {
			if (mOrderOperations.ReservationRecord_View) {
				replaceView.setVisibility(View.VISIBLE);
				lineReplace.setVisibility(View.VISIBLE);
				unReplaceView.setVisibility(View.VISIBLE);
				lineUnReplace.setVisibility(View.VISIBLE);
			} else {
				replaceView.setVisibility(View.GONE);
				lineReplace.setVisibility(View.GONE);
				unReplaceView.setVisibility(View.GONE);
				lineUnReplace.setVisibility(View.GONE);
			}
			if (mOrderOperations.Attachment_Normal_Upload) {
				attachView.setVisibility(View.VISIBLE);
				lineAttach.setVisibility(View.VISIBLE);
			} else {
				attachView.setVisibility(View.GONE);
				lineAttach.setVisibility(View.GONE);
			}
			if (mOrderOperations.Attachment_Invoice_Upload) {
				invoiceView.setVisibility(View.GONE);
				lineInvoice.setVisibility(View.GONE);
			} else {
				invoiceView.setVisibility(View.GONE);
				lineInvoice.setVisibility(View.GONE);
			}
		}

	}

	private void showCustomer(boolean isshow) {
		if (mCustomerDetail == null) {
			mCustomerDetail = findViewById(R.id.order_user);
		}

		if (!isshow) {
			isShowCustomer = true;

			TextView findViewById = (TextView) findViewById(R.id.btn_customer_name);
			findViewById.setText("");
			ImageView ivImageView = (ImageView) findViewById(R.id.imv_showcustomer);
			ivImageView.setImageResource(R.drawable.rettop);
			;
			mCustomerDetail.setVisibility(View.VISIBLE);
			TextView nameTv = (TextView) mCustomerDetail.findViewById(R.id.order_customer);
			TextView addressTv = (TextView) mCustomerDetail.findViewById(R.id.tv_address);
			TextView cusLevel = (TextView) mCustomerDetail.findViewById(R.id.tv_cusLevel);
			cusLevel.setText(mOrderInfo.CustomerLevel);

			addressTv.setText(
					TextUtils.isEmpty(mOrderInfo.CompanyName) ? mOrderInfo.CustomerAddress : mOrderInfo.CompanyName + " " + mOrderInfo.CustomerAddress);
			mCustomerDetail.findViewById(R.id.btn_address).setOnClickListener(this);
			nameTv.setText(mOrderInfo.CustomerName);
			LinearLayout phoneLayout = (LinearLayout) mCustomerDetail.findViewById(R.id.phone_layout);
			phoneLayout.removeAllViews();
			List<CustomerPhone> customerPhones = mOrderInfo.getArrayData(CustomerPhone.class);
			if (customerPhones != null) {
				LayoutInflater inflater = getLayoutInflater();
				for (final CustomerPhone phone : customerPhones) {
					if (!TextUtils.isEmpty(phone.Phone)) {
						View view = inflater.inflate(R.layout.customer_phone, null);
						phoneLayout.addView(view);
						TextView typeTv = (TextView) view.findViewById(R.id.tv_phonetype);
						TextView phoneTv = (TextView) view.findViewById(R.id.tv_phone);
						phoneTv.setText(phone.Phone);
						view.findViewById(R.id.btn_phone).setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phone.Phone));
								intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
								startActivity(intent);
							}
						});
						boolean mobile = Utils.isMobileNO(phone.Phone);
						if (!mobile) {
							typeTv.setText("客户电话");
						} else {
							typeTv.setText("客户手机");
							View msgView = view.findViewById(R.id.btn_msg);
							msgView.setVisibility(View.VISIBLE);
							msgView.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent();
									intent.setAction(Intent.ACTION_SENDTO);
									intent.setData(Uri.parse("smsto:" + phone.Phone));
									startActivity(intent);
								}
							});
						}
					}
				}
			}
		} else {
			isShowCustomer = false;
			((ImageView) findViewById(R.id.imv_showcustomer)).setImageResource(R.drawable.ext);

			TextView findViewById = (TextView) findViewById(R.id.btn_customer_name);
			findViewById.setText(mOrderInfo.CustomerName);

			mCustomerDetail.setVisibility(View.GONE);
		}
	}

	private LatLng location;

	private void getGeocoder() {

		String city = getAppliction().getCity();

		if (city == null) {
			return;
		}
		GeoCodeOption geoCodeOption = new GeoCodeOption().address(mOrderInfo.CustomerAddress).city(getAppliction().getCity());
		geocoder.geocode(geoCodeOption);

	}

	/**
	 * 提交
	 */
	private void submitServeingResult() {

		mWriteNamedata = mApiDataAdapter.getmWriteNamedata();

		mDate = mApiDataAdapter.getmDate();
		mAmount = mApiDataAdapter.getmAmount();
		mSourceOption = mApiDataAdapter.getmSourceOption();
		mNoteTv = mApiDataAdapter.getmNoteTv();
		mHostNum = mApiDataAdapter.getmHostNum();
		mFaultResult = mApiDataAdapter.getmFaultResult();
		mCheck2 = mApiDataAdapter.getmCheck2();
		mLastRView = (TextView) mApiDataAdapter.getmLstRView();
		// List<NameValuePair> postValues = new ArrayList<NameValuePair>();

		HashMap<String, String> hashMap = new HashMap<String, String>();
		if (!TextUtils.isEmpty(mOrderTask.TaskName) && (mOrderTask.TaskName.contains("离开现场") || mOrderTask.TaskName.contains("到达现场"))) {
			// postValues.add(new ParamPair(PARAM_ONSITEPOSITION,
			// getAppliction().getLocation()));
			hashMap.put(PARAM_ONSITEPOSITION, getAppliction().getLocation() == null ? "" : getAppliction().getLocation());
			String string = getAppliction().getAddrStr();
			// postValues.add(new ParamPair(PARAM_ADDRESS, string));
			hashMap.put(PARAM_ADDRESS, string == null ? "" : string);
			if (location != null) {
				LatLng latLng = new LatLng(getAppliction().getCoordinate().getLatitude(), getAppliction().getCoordinate().getLongitude());
				double distance = DistanceUtil.getDistance(latLng, location);
				// postValues.add(new ParamPair(PARAM_DISTENCE, distance));
				hashMap.put(PARAM_DISTENCE, distance + "0 ");
			}

		}
		// postValues.add(new ParamPair(PARAM_ORDERID, mOrderInfo.OrderID));
		hashMap.put(PARAM_ORDERID, mOrderInfo.OrderID);
		EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(this);
		// postValues.add(new ParamPair(PARAM_ENGINEER,
		// enginnerInfo.EngineerID));
		// postValues.add(new ParamPair("NextTaskCode",
		// mOrderInfo.CurrentTaskCode));
		hashMap.put(PARAM_ENGINEER, enginnerInfo.EngineerID);
		hashMap.put("NextTaskCode", mOrderInfo.CurrentTaskCode);
		if (mLastRView != null) {
			// postValues.add(new ParamPair("Result",
			// mLastRView.getText().toString().trim()));

			hashMap.put("Result", mLastRView.getText().toString().trim());
		}
		if (mDate != null) {

			if (!identificationRule(mApiDataAdapter.getmDatedata(), mDate)) {
				return;
			}
			// postValues.add(new ParamPair("Result", mDate));
			hashMap.put("Result", mDate);
		}
		if (mNoteTv != null) {
			if (!identificationRule(mApiDataAdapter.getmNoteTvdata(), mNoteTv)) {
				return;
			}
			// postValues.add(new ParamPair(PARAM_REMARK, mNoteTv.getText()));
			hashMap.put(PARAM_REMARK, mNoteTv.getText().toString());
		}
		if (mAmount != null) {
			if (!identificationRule(mApiDataAdapter.getmAmountdata(), mAmount.getText().toString())) {

				return;
			}
			// postValues.add(new ParamPair("Result",
			// mAmount.getText().toString()));
			hashMap.put("Result", mAmount.getText().toString());
		}
		if (mSourceOption != null) {
			// if(!identificationRule(mApiDataAdapter.getmDatedata(),
			// mDate)){return;}
			// postValues.add(new ParamPair("Result", mSourceOption.ID));
			hashMap.put("Result", mSourceOption.ID);

		}
		if (mHostNum != null) {
			if (!identificationRule(mApiDataAdapter.getmHostNumdata(), mHostNum.getText())) {
				return;
			}
			// postValues.add(new ParamPair("Result", mHostNum.getText()));

			hashMap.put("Result", mHostNum.getText().toString());

		}
		if (mFaultResult != null) {
			if (!identificationRule(mApiDataAdapter.getmFaultResultdata(), mFaultResult.getText())) {
				return;
			}
			// postValues.add(new ParamPair("Result", mFaultResult.getText()));
			hashMap.put("Result", mFaultResult.getText().toString());

		}
		if (mCheck2 != null) {
			if (!identificationRule(mApiDataAdapter.getmCheck2data(), mCheck2)) {
				return;
			}
			// postValues.add(new ParamPair("Result", mCheck2));
			hashMap.put("Result", mCheck2);

		}
		showDialog(DLG_SENDING);

		if (mWriteNamedata != null && mWriteNamedata.type.equals("130")) {
			mSignBitmap = mApiDataAdapter.getmSignBitmap();
			if (!submitSignPic()) {
				return;
			}
		}
		mBtnNext.setEnabled(false);
		// NetworkPath path = new Netpath(URL_CONFIRMSERVEING, postValues);
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_SIGNARRIVED, params, this);
		mLoadId = CALLBACK_SIGNARRIVED;
		mHttpHelper.load(URL_CONFIRMSERVEING, mCallback, hashMap, this);
	}

	/**
	 * 到达现场验证主机,调用摄像头 二维码
	 */
	public void scanCode(String title) {
		Intent intent = new Intent(this, MipcaActivityCapture.class);

		intent.putExtra("title", title);

		startActivityForResult(intent, REQUEST_CODE_CONTACED_HOST);
	}

	private View mRapirResultView;

	/**
	 * 二维码扫描返回
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case REQUESTCODE_CHECK1DIALOG: {

			mSourceOption = mSourceOptions.get(resultCode);
			Integer oId = Integer.parseInt(mSourceOption.ID);
			mSlectPosition = resultCode;
			mCheckposition = resultCode;
			if (oId == 10702 || oId == 10701) {

				if (!mComeTypes.contains(bean3)) {

					mComeTypes.add(bean3.position, bean3);
					mApiDataAdapter.clear();
					mApiDataAdapter.addData(mComeTypes);

				}

			} else {
				mApiDataAdapter.clearmContactReason();
				mComeTypes.remove(bean3);
				mApiDataAdapter.clear();
				mApiDataAdapter.addData(mComeTypes);
			}
		}
			break;

		case REQUEST_CODE_CONTACED_HOST:

			mContactedBarCodeTv = mApiDataAdapter.getmContactedBarCodeTv();
			if (resultCode == RESULT_OK && data != null) {
				String code = data.getStringExtra("code");
				mContactedBarCodeTv.setText(code);

				checkHostCode();
			}
			break;
		case REQUESTCODE_ADDREPLACE:

			if (resultCode == RESULT_OK) {
				showDialog(DLG_DATA_LOADING);
				startLoading();
			}
			break;
		case REQUEST_CODE_ORDERPROGRESS:

			if (resultCode == RESULT_OK) {
				startLoading();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	private TextView mLastRView;

	private void resultView(View v) {
		if (mRapirResultView != null)
			mRapirResultView.setSelected(false);
		v.setSelected(true);
		mRapirResultView = v;
	}

	private String mContactReason;

	private String mDate;
	private EditText mAmount;

	/**
	 * 
	 * 提交待联系
	 */
	private void submitUnContactData() {

		String uri = null;

		EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(this);
		// List<NameValuePair> postValues = new ArrayList<NameValuePair>();

		HashMap<String, String> hashMap = new HashMap<String, String>();
		// 去adapter 设置数据
		Integer oId = -1;
		for (ComeTypes temp : mComeTypes) {
			if (temp.type.equals("30")) {

				// postValues.clear();

				hashMap.clear();
				// postValues.add(new ParamPair(PARAM_TASKID,
				// mOrderInfo.CurrentTaskCode));
				hashMap.put(PARAM_TASKID, mOrderInfo.CurrentTaskCode);

				oId = Integer.parseInt(mSourceOption.ID);
				mDate = mApiDataAdapter.getmDate();

				mSourceOption = mApiDataAdapter.getmSourceOption();
				mContactReason = mApiDataAdapter.getmContactReason();
				// checkmDate(mDate);
				if (!identificationRule(mApiDataAdapter.getmResionDatedata(), mDate)) {
					return;
				}

				oId = Integer.parseInt(mSourceOption.ID);
				if (10701 == oId || 10702 == oId) {
					if (TextUtils.isEmpty(mDate)) {
						Toast.makeText(this, "请填写最新预约时间", Toast.LENGTH_SHORT).show();
						return;
					}
					if (TextUtils.isEmpty(mContactReason)) {
						Toast.makeText(this, "请选择改约原因", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				uri = URL_PUTCONTACTRESULT;
				// postValues.add(new ParamPair(PARAM_CONTACTRESULT, oId));
				// postValues.add(new ParamPair(PARAM_PRETIME, mDate));
				// postValues.add(new ParamPair(PARAM_REASON, mContactReason));
				hashMap.put(PARAM_CONTACTRESULT, oId + "");
				hashMap.put(PARAM_PRETIME, mDate);
				hashMap.put(PARAM_REASON, mContactReason);

			} else if (temp.type.equals("40")) {

				// postValues.add(new ParamPair("NextTaskCode",
				// mOrderInfo.CurrentTaskCode));
				hashMap.put("NextTaskCode", mOrderInfo.CurrentTaskCode);
				uri = URL_CONFIRMSERVEING;
				mDate = mApiDataAdapter.getmDate();
				if (!identificationRule(mApiDataAdapter.getmDatedata(), mDate)) {
					return;
				}
				if (TextUtils.isEmpty(mDate)) {
					Toast.makeText(this, "请填写最新预约时间", Toast.LENGTH_SHORT).show();
					return;
				}
				// postValues.add(new ParamPair("Result", mDate));
				hashMap.put("Result", mDate);

			}

			else if (temp.type.endsWith("20")) {
				// postValues.add(new ParamPair("NextTaskCode",
				// mOrderInfo.CurrentTaskCode));
				SourceOption getmSourceOption = mApiDataAdapter.getmSourceOption();
				// postValues.add(new ParamPair("Result",
				// getmSourceOption.Name));
				hashMap.put("NextTaskCode", mOrderInfo.CurrentTaskCode);
				hashMap.put("Result", getmSourceOption.Name);
				uri = URL_CONFIRMSERVEING;
			}
		}

		showDialog(DLG_SENDING);

		// postValues.add(new ParamPair(PARAM_ORDERID, mOrderInfo.OrderID));
		//
		// postValues.add(new ParamPair(PARAM_ENGINEER,
		// enginnerInfo.EngineerID));
		if (mNoteTv != null)
			hashMap.put(PARAM_REMARK, mNoteTv.getText().toString());
		// postValues.add(new ParamPair(PARAM_REMARK, mNoteTv.getText()));

		hashMap.put(PARAM_ORDERID, mOrderInfo.OrderID);
		hashMap.put(PARAM_ENGINEER, enginnerInfo.EngineerID);

		// NetworkPath path = new Netpath(uri, postValues);
		// mBtnNext.setEnabled(false);
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_SUBMIT_MA_UNCONTACT, params, this);
		mLoadId = CALLBACK_SUBMIT_MA_UNCONTACT;
		mHttpHelper.load(uri.toString(), mCallback, hashMap, this);
	}

	/**
	 * 检查提交数据合法性
	 * 
	 * @param getmResionDatedata
	 * @param object
	 * @return
	 */
	private boolean identificationRule(ComeTypes getmResionDatedata, Object object) {

		boolean flag = true;

		if (getmResionDatedata == null) {

			return true;
		}
		switch (getmResionDatedata.rule) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		// 必填
		case 3:
			if (object instanceof CharSequence) {
				if (TextUtils.isEmpty((CharSequence) object)) {
					Utils.showToast(OrderActivity.this, "有必填项为空");
					dismisDialog(DLG_SENDING);
					flag = false;
					return flag;
				} else if (getmResionDatedata.type.equals("120") && !checkAmount("^[0-9]\\d*$", "请检查巡检数量")) {
					flag = false;
					return flag;
				}
			}
			break;
		// 检查时间
		case 4:
			if (object instanceof String) {
				return checkmDate((String) object);
			}
			break;
		// 正整数
		case 5:
			if (checkAmount("^[1-9]\\d*$", "输入金额的格式有误")) {

				flag = false;
				return flag;
			}

			break;
		case 6:

			break;

		default:
			break;
		}

		return flag;

	}

	private boolean checkmDate(String mDate) {
		Date date = null;
		try {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			date = simpleDateFormat.parse(mDate);
			long time = date.getTime();
			long currentTimeMillis = System.currentTimeMillis();
			if (time + 60 * 1000 < currentTimeMillis) {

				Utils.showToast(this, "输入时间小于当前时间");
				dismisDialog(DLG_SENDING);
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			Utils.showToast(this, "输入时间有误");
			return false;
		}
		return true;
	}

	/**
	 * 补发工单
	 */
	private void sendOrder() {
		showDialog(DLG_SENDING);
		EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(this);
		// List<NameValuePair> postValues = new ArrayList<NameValuePair>();

		HashMap<String, String> hashMap = new HashMap<String, String>();

		// postValues.add(new ParamPair(PARAM_ORDERID, mOrderInfo.OrderID));
		// postValues.add(new ParamPair(PARAM_ENGINEER,
		// enginnerInfo.EngineerID));

		hashMap.put(PARAM_ORDERID, mOrderInfo.OrderID);
		hashMap.put(PARAM_ENGINEER, enginnerInfo.EngineerID);
		// NetworkPath path = new Netpath(URL_SENDORDER, postValues);
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_SEND_ORDER, params, this);
		mLoadId = CALLBACK_SEND_ORDER;
		mHttpHelper.load(URL_SENDORDER, mCallback, hashMap, this);
	}

	/**
	 * 验证主机
	 */
	public void checkHostCode() {

		mContactedBarCodeTv = mApiDataAdapter.getmContactedBarCodeTv();
		mBtnCheckHost = mApiDataAdapter.getmBtnCheckHost();

		String code = mContactedBarCodeTv.getText().toString();
		if (TextUtils.isEmpty(code)) {
			Toast.makeText(this, "条码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		mBtnCheckHost.setEnabled(false);
		EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(this);
		// List<NameValuePair> postValues = new ArrayList<NameValuePair>();
		HashMap<String, String> hashMap = new HashMap<String, String>();

		hashMap.put(PARAM_ENGINEER, enginnerInfo.EngineerID);
		hashMap.put(PARAM_PRODUCTSN, mOrderInfo.ProductSN);
		hashMap.put(PARAM_ORDERID, mOrderInfo.OrderID);
		hashMap.put("OProductSN", code);

		// postValues.add(new ParamPair(PARAM_ENGINEER,
		// enginnerInfo.EngineerID));
		// postValues.add(new ParamPair(PARAM_PRODUCTSN, mOrderInfo.ProductSN));
		// postValues.add(new ParamPair(PARAM_ORDERID, mOrderInfo.OrderID));
		// postValues.add(new ParamPair("OProductSN ", code));
		// NetworkPath path = new Netpath(URL_VERIFYMACHINE, postValues);

		if (code.equalsIgnoreCase(mOrderInfo.ProductSN)) {
			Toast.makeText(this, "验证通过", Toast.LENGTH_SHORT).show();
			mContactedBarCodeTv.setText(code);
			mBtnCheckHost.setEnabled(true);
			return;
		}
		showDialog(DLG_CHECKHOST);
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_SUBMIT_CHECKHOST, params, this);
		mLoadId = CALLBACK_SUBMIT_CHECKHOST;
		mHttpHelper.load(URL_VERIFYMACHINE, mCallback, hashMap, this);
	}

	/**
	 * 检查收费金额
	 */
	private boolean checkAmount(String regex, CharSequence text) {

		boolean flag = true;
		if (!TextUtils.isEmpty(mAmount.getText().toString())) {
			if (!Pattern.compile(regex).matcher(mAmount.getText().toString()).matches()) {
				Toast.makeText(this, "輸入的金額錯誤", Toast.LENGTH_SHORT).show();
				dismisDialog(DLG_SENDING);
				mBtnNext.setEnabled(true);
				flag = false;
			}
		}

		return flag;

	}

	/**
	 * 提交客户签名图片
	 */
	private boolean submitSignPic() {

		if (mSignBitmap == null) {
			Toast.makeText(this, "尚未签名", Toast.LENGTH_SHORT).show();

			return false;
		}
		ArrayList<Attach> attachs = new ArrayList<Attach>();
		File file = Utils.generatorFileFromBitmap(this, mSignBitmap);

		Attach attach = new Attach("客户签名", mOrderInfo.OrderID, Utils.getEnginnerInfo(this).EngineerNumber, file.getAbsolutePath(), Attach.ATTCH_CUSTO, "客户签名",
				"", "");
		attach.fileType = Attach.FILETYPE_SIGNATURE;
		int id = (int) attach.insert(this);
		attach._id = id;
		attachs.add(attach);
		// finish();
		Toast.makeText(getApplicationContext(), "签名图片已提交到后台上传", Toast.LENGTH_SHORT).show();

		Intent intent = new Intent(OrderActivity.this, UploadAttachService.class);
		intent.putParcelableArrayListExtra("attachs", attachs);
		startService(intent);
		return true;
	}

	private OrderTask mOrderTask;
	private TaskSource mTaskSource;
	private TextView mSelectTv;
	private EditText mNoteTv;

	/**
	 * 选择日期
	 */
	private TextView mDateTv;
	/** 维修结果 */
	private SourceOption mSourceOption;
	/** 可选择的维修结果 */
	private List<SourceOption> mSourceOptions;
	private ArrayList<AttachmentTypes> mAttachTypes;
	private View mDateView;
	private View mReasionView;
	private RootData mRootData;

	/**
	 * 填充数据
	 */
	/*
	 * @Override public void dataLoaded(int id, CacheParams params, ICacheInfo
	 * result) { dataLoaded(); boolean safe =
	 * ActivityUtils.prehandleNetworkData(this, this, id, params, result, true);
	 * switch (id) { case CALLBACK_SUBMIT_MA_UNCONTACT: case
	 * CALLBACK_SIGNARRIVED: case CALLBACK_RAPIR_RESULT: case
	 * CALLBACK_TOBEFINISHED: case CALLBACK_UNMA_SUBMIT: case CALLBACK_AMOUNT:
	 * dismisDialog(DLG_SENDING); mBtnNext.setEnabled(true); break; case
	 * CALLBACK_SEND_ORDER: dismisDialog(DLG_SENDING); break; case
	 * CALLBACK_SUBMIT_CHECKHOST: dismisDialog(DLG_CHECKHOST);
	 * mBtnCheckHost.setEnabled(true); break; case CALLBACK_ORDERTASKSOURCES:
	 * mWaitProgress.setVisibility(View.GONE); // showProgress(mTaskWaitView,
	 * false); break; case CALLBACK_ORDER: // showProgress(mWaitView, false);
	 * mWaitProgress.setVisibility(View.GONE); break; default: break; } if
	 * (!safe) { return; } switch (id) { case CALLBACK_SEND_ORDER:
	 * Toast.makeText(this, "补发工单", Toast.LENGTH_SHORT).show(); break; case
	 * CALLBACK_AMOUNT: finish(); break; case CALLBACK_ORDER: {
	 * 
	 * mRootData = (RootData) result.getData(); OrderInfo orderInfo =
	 * mRootData.getData(OrderInfo.class); mOrderOperations =
	 * mRootData.getData(OrderOperations.class); mAttachTypes =
	 * mRootData.getArrayData(AttachmentTypes.class);
	 * 
	 * initDrawer(); initView(orderInfo); newlcation = true;
	 * 
	 * } break; case CALLBACK_SUBMIT_MA_UNCONTACT:
	 * 
	 * this.setResult(Activity.RESULT_OK, new Intent()); Toast.makeText(this,
	 * "提交成功", Toast.LENGTH_SHORT).show(); finish(); // break; case
	 * CALLBACK_SUBMIT_CHECKHOST: { RootData rootData = (RootData)
	 * result.getData(); JSONObject jsonObject = rootData.getJson(); try {
	 * String VerifyResult = jsonObject.getString("VerifyResult"); if
	 * (TextUtils.equals(VerifyResult, "001")) { Toast.makeText(this, "验证通过",
	 * Toast.LENGTH_SHORT).show(); String ProductSN =
	 * jsonObject.getString("ProductSN");
	 * mContactedBarCodeTv.setText(ProductSN); } else { Toast.makeText(this,
	 * "实物主机号与工单不符，请核对后再维修！", Toast.LENGTH_SHORT).show();
	 * mContactedBarCodeTv.setText(""); }
	 * 
	 * } catch (Exception e) { } } break; case CALLBACK_SIGNARRIVED: { RootData
	 * rootData = (RootData) result.getData();
	 * 
	 * JSONObject json = rootData.getJson(); try { String msg = (String)
	 * json.get("WarningMsg"); } catch (JSONException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * this.setResult(Activity.RESULT_OK, new Intent()); startLoading();
	 * 
	 * mApiDataAdapter.clearall(); ClearAll();
	 * 
	 * } // finish(); break; case CALLBACK_RAPIR_RESULT: finish(); break; case
	 * CALLBACK_TOBEFINISHED:
	 * 
	 * finish(); break; case CALLBACK_ORDERTASKSOURCES: {
	 * 
	 * // mTaskWaitView.setVisibility(View.GONE);
	 * mWaitProgress.setVisibility(View.GONE); // result 这个是 下面是数据的集合.
	 * 我们可以从中找到下面的框有哪几种 // 现在需要找到这个数据是从哪个接口 下载的json
	 * 
	 * RootData rootData = (RootData) result.getData(); List<OrderTask>
	 * orderTasks = rootData.getArrayData(OrderTask.class); if (orderTasks !=
	 * null && !orderTasks.isEmpty()) { OrderTask orderTask = orderTasks.get(0);
	 * // if (!mTypeMA) // setTitle(orderTask.TaskID); List<TaskSource>
	 * taskSources = orderTask.getArrayData(TaskSource.class);
	 * 
	 * // 获取到工单进度的信息a-mHistoryList mHistoryList =
	 * orderTask.getArrayData(TaskHistory.class); mOrderTask = orderTask;
	 * 
	 * // 显示工单进度按钮 if (mHistoryList != null && !mHistoryList.isEmpty()) { if
	 * (mBtnOrderProcess != null) mBtnOrderProcess.setVisibility(View.VISIBLE);
	 * }
	 * 
	 * if (taskSources != null && !taskSources.isEmpty()) {
	 * mBtnNext.setVisibility(View.VISIBLE); TaskSource taskSource =
	 * taskSources.get(0); mTaskSource = taskSource; JSONObject json =
	 * taskSource.getJson(); String string = json.toString(); Gson gson = new
	 * Gson(); CometypesBean fromJson = gson.fromJson(string,
	 * CometypesBean.class);
	 * 
	 * mComeTypes = fromJson.ComeTypes; // 把要显示的数据填充到集合里面去 if (null !=
	 * mApiDataAdapter) { mApiDataAdapter.clear(); } ComeTypes comeTypes = new
	 * ComeTypes(); comeTypes.type = "501"; comeTypes.Come = orderTask.TaskName;
	 * mComeTypes.add(0, comeTypes); mApiDataAdapter.addData(mComeTypes);
	 * 
	 * mListView.smoothScrollToPosition(mApiDataAdapter.getCount() - 1); // 115
	 * 待维修 // 120 维修中 // 140 待取机 // 130 待通知 // 190 待服务完 // 60 已分派 // 65 已联系 //
	 * 70 上门中 // 80 服务中 // 330 站端已接收 // 335 待站端接收 // 200 服务完
	 * 
	 * if (mState != 40 && mState != 60 && mState != 65 && mState != 70 &&
	 * mState != 80 && mState != 115 && mState != 120 && mState != 130 && mState
	 * != 140 && mState != 190 && mState != 335 && mState != 330) {
	 * 
	 * // mBtnNext.setVisibility(View.GONE);
	 * 
	 * mBtnNext.setTextColor(getResources().getColor(R.color.order_textcolor));
	 * mBtnNext.setText("当前状态工单已转至协调员在PC端处理！"); mBtnNext.setEnabled(false); }
	 * 
	 * } else if (mState == 200) { mBtnNext.setVisibility(View.GONE); } else {
	 * mBtnNext.setVisibility(View.VISIBLE); mBtnNext.setText("服务完"); }
	 * 
	 * } } break; case CALLBACK_UNMA_SUBMIT: finish(); break; } }
	 */
	private void ClearAll() {

		mSlectPosition = 0;
		mCheckposition = -1;
		mDate = null;
		mAmount = null;
		mSourceOption = null;
		mNoteTv = null;
		mHostNum = null;
		mFaultResult = null;
		mCheck2 = null;
		mLastRView = null;
		mApiDataAdapter.clear();
	}

	/**
	 * 选中条目的Name
	 */
	private int mSlectPosition = 0;
	private int mCheckposition = -1;

	/**
	 * 对话框的弹出
	 */
	@Override
	public Dialog onCreateDialog(int id, Bundle bundle) {
		Dialog dialog = null;
		switch (id) {
		case DLG_SOURCEOPTIONS: { // 非MA 单选

			String[] optionArray = new String[mSourceOptions.size()];
			for (int i = 0; i < mSourceOptions.size(); i++) {
				optionArray[i] = mSourceOptions.get(i).Name;
			}

			Intent intent = new Intent(this, Check1Dialog.class);

			intent.putExtra("checkposition", mCheckposition);
			intent.putExtra("data", optionArray);
			intent.putExtra("title", mOrderTask.TaskName);
			startActivityForResult(intent, REQUESTCODE_CHECK1DIALOG);

		}
			break;
		case DLG_SOURCECHECK1: {

			List<SourceOption> check1 = mApiDataAdapter.getmsoureCheck1();

			Collections.sort(check1, new Comparator<SourceOption>() {

				@Override
				public int compare(SourceOption paramT1, SourceOption paramT2) {

					return paramT1.Name.compareTo(paramT2.Name);
				}
			});

			// 下拉二级多选
			final String[] optionArray1 = new String[check1.size()];

			if (mCheck1Results == null)

			{
				mCheck1Results = new boolean[check1.size()];
				for (int i = 0; i < check1.size(); i++) {
					optionArray1[i] = check1.get(i).Name;
					mCheck1Results[i] = false;
				}
			} else

			{
				for (int i = 0; i < check1.size(); i++) {
					optionArray1[i] = check1.get(i).Name;
				}
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			AlertDialog result = builder.setTitle(mOrderTask.TaskName).setMultiChoiceItems(optionArray1, mCheck1Results, new OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					// TODO Auto-generated method stub
					mCheck1Results[which] = isChecked;
					// dialog.dismiss();
				}
			}).setPositiveButton("确认", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					StringBuilder stringBuilder = new StringBuilder();

					for (int i = 0; i < mCheck1Results.length; i++) {
						if (mCheck1Results[i]) {
							stringBuilder.append(optionArray1[i]);
						}
					}
					mApiDataAdapter.notifyDataSetChanged();
				}
			}).create();

			dialog = result;

		}
			break;
		case DLG_SOURCECHECK2: {
			ArrayList<String> check2 = bundle.getStringArrayList("check2");
			// 下拉二级多选
			final String[] optionArray2 = new String[check2.size()];

			mCheck2Results = null;
			mCheck2Results = new boolean[check2.size()];
			for (int i = 0; i < check2.size(); i++) {
				optionArray2[i] = check2.get(i);
				mCheck2Results[i] = false;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog result = builder.setTitle(mOrderTask.TaskName).setMultiChoiceItems(optionArray2, mCheck2Results, new OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					mCheck2Results[which] = isChecked;
				}
			}).setPositiveButton("确认", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					StringBuilder stringBuilder = new StringBuilder();

					for (int i = 0; i < mCheck2Results.length; i++) {
						if (mCheck2Results[i]) {
							stringBuilder.append(optionArray2[i]);
						}
					}
					mApiDataAdapter.notifyDataSetChanged();
				}
			}).create();
			dialog = result;
		}
			break;
		case DLG_PROCESS:

		{// 工单处理步骤
			Dialog builder = new Dialog(this, R.style.selectorDialog);
			int proId = mTypeMA ? R.layout.ma_order_process : R.layout.order_process;
			View view = getLayoutInflater().inflate(proId, null);
			view.findViewById(R.id.btn_cancle).setOnClickListener(this);
			builder.setContentView(view);
			dialog = builder;
		}
			break;
		case DLG_USER:

		{ // 客戶详细信息
			Dialog builder = new Dialog(this, R.style.selectorDialog);
			// View dView = getLayoutInflater().inflate(R.layout.order_user,
			// null);
			View dView = findViewById(R.id.order_user);
			TextView nameTv = (TextView) dView.findViewById(R.id.order_customer);
			TextView addressTv = (TextView) dView.findViewById(R.id.tv_address);
			addressTv.setText(
					TextUtils.isEmpty(mOrderInfo.CompanyName) ? mOrderInfo.CustomerAddress : mOrderInfo.CompanyName + " " + mOrderInfo.CustomerAddress);
			dView.findViewById(R.id.btn_address).setOnClickListener(this);
			nameTv.setText(mOrderInfo.CustomerName);
			LinearLayout phoneLayout = (LinearLayout) dView.findViewById(R.id.phone_layout);
			phoneLayout.removeAllViews();
			List<CustomerPhone> customerPhones = mOrderInfo.getArrayData(CustomerPhone.class);
			if (customerPhones != null) {
				LayoutInflater inflater = getLayoutInflater();
				for (final CustomerPhone phone : customerPhones)

				{
					if (!TextUtils.isEmpty(phone.Phone)) {
						View view = inflater.inflate(R.layout.customer_phone, null);
						phoneLayout.addView(view);
						TextView typeTv = (TextView) view.findViewById(R.id.tv_phonetype);
						TextView phoneTv = (TextView) view.findViewById(R.id.tv_phone);
						phoneTv.setText(phone.Phone);
						view.findViewById(R.id.btn_phone).setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phone.Phone));
								intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
								startActivity(intent);
							}
						});
						boolean mobile = Utils.isMobileNO(phone.Phone);
						if (!mobile) {
							typeTv.setText("客户电话");
						} else {
							typeTv.setText("客户手机");
							View msgView = view.findViewById(R.id.btn_msg);
							msgView.setVisibility(View.VISIBLE);
							msgView.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent();
									intent.setAction(Intent.ACTION_SENDTO);
									intent.setData(Uri.parse("smsto:" + phone.Phone));
									startActivity(intent);
								}
							});
						}
					}
				}

			}

			// dView.findViewById(R.id.btn_cancle_u).setOnClickListener(this);
			builder.setContentView(dView);
			dialog = builder;
		}
			break;

		case DLG_DOSUBMIT:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			AlertDialog result = builder.setTitle("确认提交").setMessage("您确定要提交吗?").setPositiveButton("确认", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					doSubmit();

					dialog.dismiss();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
				}
			}).create();
			dialog = result;
			break;
		default:
			dialog = super.onCreateDialog(id);
			break;

		}
		return dialog;
	}

	private void loadQRCode(String qrcode) {

		WebView webView = (WebView) findViewById(R.id.w);

		WebSettings webSettings = webView.getSettings();
		webSettings.setUseWideViewPort(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.loadUrl(qrcode);
		webView.setInitialScale(50);
		webView.setHorizontalScrollBarEnabled(false);//水平不显示
		webView.setVerticalScrollBarEnabled(false); //垂直不显示
		// findViewById(R.id.web_layout).setOnClickListener(this);

	}

	private void GetQRCode() {

		Uri uri = Uri.parse(URL_GETQRCODE).buildUpon().appendQueryParameter(PARAM_ORDERID, String.valueOf(mOrderInfo.OrderID)).build();
		mLoadId = CALLBACK_GETQR;
		mWaitProgress.setVisibility(View.VISIBLE);
		mHttpHelper.load(uri.toString(), mCallback, this);

	}

	/**
	 * 工单详情数据解析
	 */
	@Override
	public void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		switch (id) {
		case DLG_SOURCEOPTIONS: {
			String[] optionArray = new String[mSourceOptions.size()];
			for (int i = 0; i < mSourceOptions.size(); i++) {
				optionArray[i] = mSourceOptions.get(i).Name;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog result = builder.setTitle(mOrderTask.TaskName).setSingleChoiceItems(optionArray, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					mSourceOption = mSourceOptions.get(which);
					mSelectTv.setText(mSourceOption.Name);
				}
			}).create();
			dialog = result;
		}
			break;
		case DLG_SOURCECHECK2: {
			ArrayList<String> check2 = bundle.getStringArrayList("check2");

			// 下拉二级多选
			final String[] optionArray2 = new String[check2.size()];

			mCheck2Results = null;
			mCheck2Results = new boolean[check2.size()];
			for (int i = 0; i < check2.size(); i++) {
				optionArray2[i] = check2.get(i);
				mCheck2Results[i] = false;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog result = builder.setTitle(mOrderTask.TaskName).setMultiChoiceItems(optionArray2, mCheck2Results, new OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					mCheck2Results[which] = isChecked;
				}
			}).setPositiveButton("确认", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					StringBuilder stringBuilder = new StringBuilder();

					for (int i = 0; i < mCheck2Results.length; i++) {
						if (mCheck2Results[i]) {
							stringBuilder.append(optionArray2[i]);
						}
					}
					mApiDataAdapter.notifyDataSetChanged();
				}
			}).create();
			dialog = result;
		}

			removeDialog(DLG_SOURCECHECK2);
			break;
		default:
			super.onPrepareDialog(id, dialog);
			break;
		}
	}

	/**
	 * 
	 * 处理上面的左右滑动
	 * 
	 */
	private float StartY;
	private float startX;
	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				StartY = event.getY();
				mOrderView.getParent().requestDisallowInterceptTouchEvent(true);
				break;

			case MotionEvent.ACTION_UP:

				if (startX - event.getX() > 200) {
					if (!TextUtils.isEmpty(mOrderInfo.N_OrderID)) {
						Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
						intent.putExtra("orderId", mOrderInfo.N_OrderID);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						finish();
					} else {
						// Toast.makeText(getApplicationContext(),
						// "暂无下一条工单记录",
						// Toast.LENGTH_SHORT).show();
					}
				} else if (event.getX() - startX > 200) {
					if (!TextUtils.isEmpty(mOrderInfo.P_OrderID)) {
						Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
						intent.putExtra("orderId", mOrderInfo.P_OrderID);
						startActivity(intent);
						overridePendingTransition(R.anim.in_to_left, R.anim.out_from_right);
						finish();
					} else {
						// Toast.makeText(getApplicationContext(),
						// "暂无上一条工单记录",
						// Toast.LENGTH_SHORT).show();
					}
				}

				break;
			}
			return true;
		}
	};

	private void startLoading() {
		if (!mInLoading) {
			mInLoading = true;
			mPullLayout.setEnableStopInActionView(true);
			mActionImage.clearAnimation();
			mActionImage.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
			mActionText.setText(R.string.note_pull_loading);
			init();
		}
	}

	/**
	 * 加载完成设置 本次加载时间, 现在当前页面不允许手动刷新.
	 * 
	 * 取消
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

			mTimeText.setText(getString(R.string.note_update_at, DateFormat.getTimeFormat(this).format(new Date(System.currentTimeMillis()))));
		}
	}

	protected void showProgress(View progressLayout, boolean show) {
		if (show) {
			progressLayout.findViewById(R.id.error_footer).setVisibility(View.GONE);
			progressLayout.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
		} else {
			progressLayout.findViewById(R.id.error_footer).setVisibility(View.VISIBLE);
			progressLayout.findViewById(R.id.progressBar).setVisibility(View.GONE);
		}
	}

	public interface DateListener {
		public void date(String date);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.order_info;
	}

	private Bitmap mSignBitmap;
	private ImageView ivSign;
	private TextView tvSign;
	private ApiDataAdapter<OrderInfo> mApiDataAdapter;
	private List<ComeTypes> mComeTypes;
	private boolean[] mCheck1Results = null;
	private boolean[] mCheck2Results;
	private View mCustomerDetail;
	private View mWaitProgress;
	private DrawerLayout mDrawerLayout;
	private String mTaskDesc;
	private ArrayList<OrderInfo> mSoListHists;
	private ListView mListView;
	private View mOrderView;

	@Override
	public void finish() {
		// if (mCacheManager != null)
		// mCacheManager.cancel(this);

		TabOrderActivity.needRefrash = true;
		mHttpHelper.cancle(OrderActivity.class);
		super.finish();
	}

	public int getmSlectPosition() {

		return mSlectPosition;
	}

	public boolean[] getmCheck1Result() {
		return mCheck1Results;
	}

	public boolean[] getmCheck2Result() {
		return mCheck2Results;
	}

	public CharSequence getTaskDesc() {

		return mTaskDesc;
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult paramGeoCodeResult) {
		location = paramGeoCodeResult.getLocation();

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult paramReverseGeoCodeResult) {
	}
}
