package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.AttachmentTypes;
import com.lenovo.sdimobileclient.api.Change;
import com.lenovo.sdimobileclient.api.ChangeHistory;
import com.lenovo.sdimobileclient.api.CustomerPhone;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.api.OrderInfo;
import com.lenovo.sdimobileclient.api.OrderTask;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.api.TaskHistory;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.utility.Utils;
import com.squareup.picasso.Picasso.LoadedFrom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

/**
 * 工单详情
 * 
 * @author zhangshaofang
 * 
 */
public class OrderInfoActivity extends RootActivity {

	private OrderInfo mOrderInfo;
	private TextView mTvShow;
	private TextView mHostfault;
	private TextView mSolition;
	private boolean showDetail;
	private View mBtnOrderProcess;
	private boolean isShowCustomer;
	private View mCustomerDetail;
	private ArrayList<CustomerPhone> mCustomerPhones;
	private ArrayList<TaskHistory> mHistoryList;
	private ArrayList<TaskHistory> HistoryList;
	private OrderTask mOrderTask;
	private RelativeLayout mDetail;
	private ListView mListView;
	private boolean remarkflag = false;
	private boolean faultSolutionflag = false;
	private LayoutInflater mInflater;
	private View mWaitProgress;
	private EnginnerInfo mEnginnerInfo;
	private OkHttpHelper mHttpHelper;
	private ArrayList<ChangeHistory> mSOlistHist = new ArrayList<ChangeHistory>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEnginnerInfo = Utils.getEnginnerInfo(this);

		mHttpHelper = OkHttpHelper.getInstance(OrderInfoActivity.this);
		initBackBtn();
		initData();
		// initview();

	}

	private void initData() {
		mOrderInfo = (OrderInfo) getIntent().getSerializableExtra("customerInfo");
		mCustomerPhones = (ArrayList<CustomerPhone>) getIntent().getSerializableExtra("customerPhones");

		mCustomerPhones = (ArrayList<CustomerPhone>) (mCustomerPhones == null ? mOrderInfo.CustomerPhones : mCustomerPhones);
		mHistoryList = (ArrayList<TaskHistory>) getIntent().getSerializableExtra("mHistoryList");
		mOrderTask = (OrderTask) getIntent().getSerializableExtra("Task");

		mWaitProgress = findViewById(R.id.wait_progress);
		mWaitProgress.setVisibility(View.VISIBLE);

		Uri build = Uri.parse(URL_CHANGE_HISTORIES).buildUpon().appendQueryParameter(PARAM_ENGINEER, mEnginnerInfo.EngineerID)
				.appendQueryParameter(PARAM_ORDERID, mOrderInfo.OrderID).build();

		mHttpHelper.load(build.toString(), new OkHttpStringCallback(OrderInfoActivity.this) {

			@Override
			public void onResponse(String result) {
				mWaitProgress.setVisibility(View.GONE);
				boolean safe = mHttpHelper.isSuccessResult(result, OrderInfoActivity.this);
				if (!safe) {
					return;
				}
				try {
					Gson gson = new Gson();
					JSONObject jsonObject2 = new JSONObject(result);
					JSONArray jsonArray = jsonObject2.getJSONArray("ChangeHistory");

					for (int i = 0; i < jsonArray.length(); i++) {

						JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
						String string = jsonObject.toString();

						ChangeHistory changeHistory = new ChangeHistory(string);
						mSOlistHist.add(changeHistory);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (mSOlistHist != null && !mSOlistHist.isEmpty()) {
					for (ChangeHistory i : mSOlistHist) {
						i.mReplaceEdit = false;
					}
				}

				initview();

			}
		}, OrderInfoActivity.this);

	}

	@SuppressWarnings("unchecked")
	private void initview() {
		mInflater = LayoutInflater.from(this);
		mListView = (ListView) findViewById(R.id.historyList);

		mTvShow = (TextView) findViewById(R.id.showall);
		// mTvShow.setOnClickListener(this);
		showDetail = false;
		isShowCustomer = false;
		View orderCusProView = findViewById(R.id.customer_progress);

		/**
		 * 备件整体状态
		 */
		// TextView PartsStatus = (TextView)
		// findViewById(R.id.order_PartsStatus);
		TextView Time1 = (TextView) findViewById(R.id.order_Time1);
		TextView Time4 = (TextView) findViewById(R.id.order_Time4);
		TextView CallBack = (TextView) findViewById(R.id.order_CallBack);

		// PartsStatus.setText(mOrderInfo.PartsStatus);
		Time1.setText(mOrderInfo.Time1);
		Time4.setText(mOrderInfo.Time4);
		CallBack.setText(mOrderInfo.CallBack);

		if (mOrderInfo.OrderTypeName.contains("服务器")) {

			findViewById(R.id.callbackph).setVisibility(View.VISIBLE);
			TextView CallBackNum = (TextView) findViewById(R.id.order_CallBack_num);

			CallBackNum.setVisibility(View.VISIBLE);

			CallBackNum.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View paramView) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + "4008101155"));
					intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
					startActivity(intent);
				}
			});
		} else {
			findViewById(R.id.callbackph).setVisibility(View.GONE);
			findViewById(R.id.order_CallBack_num).setVisibility(View.GONE);
		}

		if (mOrderInfo.SOType.equals("1")) {
			mDetail = (RelativeLayout) findViewById(R.id.detail);
			findViewById(R.id.detail).setVisibility(View.VISIBLE);
			findViewById(R.id.detail_cus).setVisibility(View.GONE);
			findViewById(R.id.tv1).setVisibility(View.VISIBLE);
			// PartsStatus.setVisibility(View.VISIBLE);

			// findViewById(R.id.beijian).setVisibility(View.VISIBLE);

		} else {
			mDetail = (RelativeLayout) findViewById(R.id.detail_cus);
			findViewById(R.id.detail).setVisibility(View.GONE);
			findViewById(R.id.detail_cus).setVisibility(View.VISIBLE);
			// findViewById(R.id.beijian).setVisibility(View.GONE);
			// PartsStatus.setVisibility(View.GONE);

			findViewById(R.id.tv1).setVisibility(View.GONE);
		}

		mHostfault = (TextView) mDetail.findViewById(R.id.order_host_fault);
		mSolition = (TextView) findViewById(R.id.order_Solution);

		mSolition.setOnClickListener(this);

		mDetail.removeView(mDetail.findViewById(R.id.line2));
		mSolition.setText(mOrderInfo.Solution);

		TextView customerName = (TextView) orderCusProView.findViewById(R.id.btn_customer_name);
		customerName.setText(mOrderInfo.CustomerName);
		orderCusProView.findViewById(R.id.btn_customer_info).setOnClickListener(this);
		orderCusProView.findViewById(R.id.btn_order_details).setVisibility(View.GONE);
		mBtnOrderProcess = orderCusProView.findViewById(R.id.btn_order_process);
		mBtnOrderProcess.setOnClickListener(this);

		if (mSOlistHist != null) {
			findViewById(R.id.tv_history).setVisibility(mSOlistHist.isEmpty() ? View.GONE : View.VISIBLE);
		} else {
			findViewById(R.id.tv_history).setVisibility(View.GONE);
		}

		initView(mOrderInfo);
	}

	private void initView(OrderInfo orderInfo) {

		OrderInfo order = (OrderInfo) orderInfo;
		// TextView codeTv = (TextView) viewMap.get(R.id.order_code);
		TextView hostTv = (TextView) mDetail.findViewById(R.id.order_host);
		TextView typeTv = (TextView) mDetail.findViewById(R.id.order_type);
		TextView hostfaulttv = (TextView) mDetail.findViewById(R.id.order_host_fault);
		TextView subtypetv = (TextView) mDetail.findViewById(R.id.order_subtype);

		TextView hostCodeTv = (TextView) mDetail.findViewById(R.id.order_host_code);
		// TextView customerTv = (TextView)
		// viewMap.get(R.id.order_customer);
		TextView orderStateTv = (TextView) mDetail.findViewById(R.id.order_state);
		TextView dateTv = (TextView) mDetail.findViewById(R.id.order_date);

		TextView paidancount = (TextView) mDetail.findViewById(R.id.order_paidancount);
		TextView order_remark = (TextView) mDetail.findViewById(R.id.order_remark);

		if (null != hostfaulttv) {
			hostfaulttv.setText(orderInfo.FailureDescription);
			hostfaulttv.setOnClickListener(this);
		}
		if (null != subtypetv)
			subtypetv.setText(orderInfo.ServiceName);
		if (null != hostTv)
			hostTv.setText(orderInfo.ProductSN);

		String stateName = orderInfo.OrderStateName;
		if (null != orderStateTv)
			orderStateTv.setText(stateName);
		if (null != typeTv)
			typeTv.setText(orderInfo.OrderTypeName);
		if (null != hostCodeTv)
			hostCodeTv.setText(orderInfo.ProductType);
		if (null != paidancount)
			paidancount.setText(orderInfo.CurrentNum);
		if (null != order_remark) {
			order_remark.setText(orderInfo.Remark);
			order_remark.setOnClickListener(this);
		}
		if (!TextUtils.isEmpty(order.PreTime)) {
			dateTv.setText(Utils.getDate(Utils.dateFormatTimes(order.PreTime)) + "  " + Utils.getTime(Utils.dateFormatTimes(order.PreTime)));
		} else {
			dateTv.setText("暂无");
		}
		// if (order.AttachCount != 0) {
		TextView attchcount = (TextView) findViewById(R.id.order_count);
		attchcount.setVisibility(View.INVISIBLE);

		// }
		// if (order.RepairTimes != 0) {
		TextView repair = (TextView) findViewById(R.id.rapir_count);
		repair.setVisibility(View.INVISIBLE);
		repair.setText(order.RepairTimes + "");

		// }
		if (mSOlistHist != null && !mSOlistHist.isEmpty()) {
			ApiDataAdapter<ChangeHistory> apiDataAdapter = new ApiDataAdapter<ChangeHistory>(this);
			View inflate = mInflater.inflate(R.layout.change, null);
			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			inflate.measure(w, h);
			int height = inflate.getMeasuredHeight();
			mListView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height * mSOlistHist.size()));
			mListView.setAdapter(apiDataAdapter);

			apiDataAdapter.add(mSOlistHist);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.order_Solution:
			if (showDetail) {
				mTvShow.setText("收起全部");
				mSolition.setSingleLine(true);

				if (mHostfault != null) {

					mHostfault.setSingleLine(true);
				}
				showDetail = false;
			} else {
				mTvShow.setText("展开全部");
				mSolition.setSingleLine(false);

				if (mHostfault != null) {

					mHostfault.setSingleLine(true);
				}
				showDetail = true;
			}
			break;
		case R.id.btn_customer_info:

			showCustomer(isShowCustomer);

			break;
		case R.id.btn_order_process: {

			if (mHistoryList != null && !mHistoryList.isEmpty()) {
				Intent intent = new Intent(this, OrderProgressActivity.class);
				HistoryList = (ArrayList) mHistoryList;
				intent.putExtra("Task", mOrderTask);

				intent.putParcelableArrayListExtra("HistoryList", HistoryList);

				startActivity(intent);
			} else {
				Utils.showToast(this, "暂无工单进度");
			}
		}
			// showDialog(DLG_PROCESS);
			break;

		case R.id.order_remark: {

			TextView view = (TextView) v;

			view.setSingleLine(remarkflag);
			remarkflag = !remarkflag;
		}
			break;

		case R.id.order_host_fault: {

			TextView view = (TextView) v;

			view.setSingleLine(faultSolutionflag);
			faultSolutionflag = !faultSolutionflag;
		}
			break;
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}
	}

	private void showCustomer(boolean isshow) {
		if (mCustomerDetail == null) {
			mCustomerDetail = findViewById(R.id.order_user);
		}

		if (!isshow) {
			isShowCustomer = true;
			((ImageView) findViewById(R.id.imv_showcustomer)).setImageResource(R.drawable.rettop);
			;
			mCustomerDetail.setVisibility(View.VISIBLE);
			TextView nameTv = (TextView) mCustomerDetail.findViewById(R.id.order_customer);
			TextView addressTv = (TextView) mCustomerDetail.findViewById(R.id.tv_address);
			TextView cusLevel = (TextView) mCustomerDetail.findViewById(R.id.tv_cusLevel);
			addressTv.setText(
					TextUtils.isEmpty(mOrderInfo.CompanyName) ? mOrderInfo.CustomerAddress : mOrderInfo.CompanyName + " " + mOrderInfo.CustomerAddress);
			mCustomerDetail.findViewById(R.id.btn_address).setOnClickListener(this);
			nameTv.setText(mOrderInfo.CustomerName);
			cusLevel.setText(mOrderInfo.CustomerLevel);
			LinearLayout phoneLayout = (LinearLayout) mCustomerDetail.findViewById(R.id.phone_layout);
			phoneLayout.removeAllViews();
			// List<CustomerPhone> customerPhones =
			// mOrderInfo.getArrayData(CustomerPhone.class);
			if (mCustomerPhones != null) {
				LayoutInflater inflater = getLayoutInflater();
				for (final CustomerPhone phone : mCustomerPhones) {
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
			mCustomerDetail.setVisibility(View.GONE);
		}

		// TODO Auto-generated method stub

	}

	@Override
	protected int getContentViewId() {
		return R.layout.order;
	}
}
