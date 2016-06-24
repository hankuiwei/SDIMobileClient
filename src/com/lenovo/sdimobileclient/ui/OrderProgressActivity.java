package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.api.OrderTask;
import com.lenovo.sdimobileclient.api.TaskHistory;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderProgressActivity extends RootActivity {

	private static final int DIALOG_REVOKETASK = 1001;
	private LayoutInflater mInflater;
	private ArrayList<TaskHistory> mHistoryList;
	private OrderTask mOrderTask;
	private EnginnerInfo mEnginnerInfo;
	private String mSOTaskID;
	private String mOrderID;
	private int mState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initBackBtn();

		mEnginnerInfo = Utils.getEnginnerInfo(this);
		Intent intent = getIntent();
		mHistoryList = intent.getParcelableArrayListExtra("HistoryList");
		mOrderTask = (OrderTask) intent.getSerializableExtra("Task");
		mSOTaskID = intent.getStringExtra("SOTaskID");
		mOrderID = intent.getStringExtra("OrderID");
		mState = intent.getIntExtra("State", -1);
		mInflater = LayoutInflater.from(this);
		initView();
	}

	private void initView() {

		LinearLayout layout = (LinearLayout) findViewById(R.id.order_process_layout);

		for (int i = 0; i < mHistoryList.size(); i++) {
			View view = null;
			switch (i) {
			case 0:
				view = mInflater.inflate(R.layout.order_process_item_first, null);
				break;
			default:
				view = mInflater.inflate(R.layout.order_process_item, null);
				View btn_delete = view.findViewById(R.id.btn_delete);
				btn_delete.setVisibility(View.GONE);
				btn_delete.setOnClickListener(this);
				break;
			}

			TextView process_tag = (TextView) view.findViewById(R.id.process_tag);
			// process_tag.setText("完成");
			ImageView process_state = (ImageView) view.findViewById(R.id.process_state);
			process_state.setImageResource(R.drawable.step0);
			TextView process_name = (TextView) view.findViewById(R.id.process_name);
			TextView process_taskback = (TextView) view.findViewById(R.id.taskback);

			String start = mHistoryList.get(i).Name.substring(0, mHistoryList.get(i).Name.indexOf("("));
			String end = mHistoryList.get(i).Name.substring(mHistoryList.get(i).Name.indexOf("("));
			String end1 = end.substring(end.indexOf(" "));

			String end2 = "( " + mEnginnerInfo.EngineerID + end1;
			TextView process_time = (TextView) view.findViewById(R.id.process_time);
			process_time.setText(end2);
			process_name.setText(start);
			process_taskback.setText(TextUtils.isEmpty(mHistoryList.get(i).TaskBack) ? "" : mHistoryList.get(i).TaskBack);
			layout.addView(view);

			if (i == mHistoryList.size() - 1 && mState != 200) {
				View btn_delete = view.findViewById(R.id.btn_delete);
				btn_delete.setVisibility(View.VISIBLE);
				btn_delete.setOnClickListener(this);
			}

		}
		View view = mInflater.inflate(R.layout.order_process_item, null);
		ImageView process_state = (ImageView) view.findViewById(R.id.process_state);
		process_state.setImageResource(R.drawable.step1);
		TextView process_name = (TextView) view.findViewById(R.id.process_name);
		process_name.setText(mOrderTask.TaskName);
		ImageView iv_verline = (ImageView) findViewById(R.id.iv_verLine);
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

		process_state.measure(w, h);
		view.measure(w, h);

		int width = process_state.getMeasuredWidth();
		int height = view.getMeasuredHeight();

		float density = getResources().getDisplayMetrics().density;

		iv_verline.setPadding((int) (width / (density)), (int) (height / (2 * density)), 0, (int) (height / (2 * density)));

		TextView process_taskback = (TextView) view.findViewById(R.id.taskback);
		TextView process_time = (TextView) view.findViewById(R.id.process_time);
		// btn_delete

		View btn_delete = view.findViewById(R.id.btn_delete);
		btn_delete.setVisibility(view.GONE);

		view.findViewById(R.id.process_time).setVisibility(View.GONE);
		process_taskback.setVisibility(View.INVISIBLE);
		if (!TextUtils.isEmpty(mOrderTask.TaskName)) {
			layout.addView(view);
		} else {
			view.setVisibility(View.GONE);
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_delete:

			showDialog(DIALOG_REVOKETASK);

			break;
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_REVOKETASK:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			AlertDialog result = builder.setTitle("确认撤销").setMessage("您确定要撤销吗?").setPositiveButton("确认", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					RevokeTask();

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

	private void RevokeTask() {

		Uri build = Uri.parse(URL_REVOKETASK).buildUpon().appendQueryParameter(PARAM_ENGINEER, mEnginnerInfo.EngineerID)
				.appendQueryParameter(PARAM_ORDERID, mOrderID).appendQueryParameter("SOTaskID", mSOTaskID).build();

		OkHttpHelper.getInstance(this).load(build.toString(), new OkHttpStringCallback(OrderProgressActivity.this) {

			@Override
			public void onResponse(String result) {

				boolean successResult = OkHttpHelper.getInstance(OrderProgressActivity.this).isSuccessResult(result, OrderProgressActivity.this);
				if (!successResult) {
					return;
				}

				setResult(RESULT_OK);
				finish();
			}
		}, OrderProgressActivity.this);

	}

	@Override
	protected int getContentViewId() {
		return R.layout.order_taskprogress;
	}

}
