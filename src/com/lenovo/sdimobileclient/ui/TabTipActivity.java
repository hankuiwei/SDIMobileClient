package com.lenovo.sdimobileclient.ui;

import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.data.AlarmAlert;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout.OnPullListener;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout.OnPullStateListener;

/**
 * 提醒及系统通知列表
 * 
 * @author zhangshaofang
 * 
 */
public class TabTipActivity extends RootActivity implements OnPullListener, OnPullStateListener {

	private ApiDataAdapter<AlarmAlert> mAlertAdapter;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}
	private boolean mEdit;
	private TextView mActionTv;

	private void initView() {
		mActionTv = (TextView) findViewById(R.id.btn_bar_right);
		mActionTv.setOnClickListener(this);
		mActionTv.setVisibility(View.VISIBLE);
		mActionTv.setText("编辑");
		mEdit = false;
		mAlertAdapter = new ApiDataAdapter<AlarmAlert>(this);
		ListView listView = (ListView) findViewById(android.R.id.list);

		listView.setAdapter(mAlertAdapter);
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

	private void initData() {
		mAlertAdapter.clear();
		List<AlarmAlert> alerts = AlarmAlert.queryAlarmAlert(this);
		mAlertAdapter.add(alerts);
		dataLoaded();

	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bar_right:
			mEdit = !mEdit;
			mAlertAdapter.setEdit(mEdit);
			mActionTv.setText(mEdit ? "取消" : "编辑");
			mAlertAdapter.notifyDataSetChanged();
			break;

		default:
			super.onClick(v);
			break;
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

	private void startLoading() {
		if (!mInLoading) {
			mInLoading = true;
			mPullLayout.setEnableStopInActionView(true);
			mActionImage.clearAnimation();
			mActionImage.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
			mActionText.setText(R.string.note_pull_loading);
			initData();
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

	@Override
	protected int getContentViewId() {
		return R.layout.tip;
	}

}
