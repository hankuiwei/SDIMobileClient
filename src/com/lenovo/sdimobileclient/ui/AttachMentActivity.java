package com.lenovo.sdimobileclient.ui;

import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.data.Attach;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout.OnPullListener;
import com.lenovo.sdimobileclient.ui.view.PullActivateLayout.OnPullStateListener;

/**
 * 附件列表
 * 
 * @author zhangshaofang
 * 
 */
public class AttachMentActivity extends RootActivity implements OnPullListener, OnPullStateListener {

	private ApiDataAdapter<Attach> mAttachAdapter;
	private View mActivonView;
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

	private int mType;

	/**
	 * 获取附件列表
	 * 
	 * @param type
	 *            if true 已完成 else false 待上传
	 */
	private List<Attach> mAttachs;

	private void initData(int type) {

		mAttachs = Attach.queryAttachSuccess(this, type);
		mHandler.sendEmptyMessage(1);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			mAttachAdapter.clear();
			mAttachAdapter.add(mAttachs);
			dataLoaded();
		};
	};
	private TextView mHostInfo;
	private TextView mBox;

	private void loadData(int type) {
		mType = type;
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				initData(mType);
			}
		});
	}

	private void initView() {
		initBackBtn();
		mActivonView = findViewById(R.id.action_view);
		mHostInfo = (TextView) findViewById(R.id.btn_host_info);

		mHostInfo.setOnClickListener(this);

		mBox = (TextView) findViewById(R.id.btn_box);
		mBox.setOnClickListener(this);
		ListView listView = (ListView) findViewById(android.R.id.list);
		mAttachAdapter = new ApiDataAdapter<Attach>(this);
		listView.setAdapter(mAttachAdapter);

		mRotateUpAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_up);
		mRotateDownAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_down);

//		mPullLayout = (PullActivateLayout) findViewById(R.id.pull_container);
//		mPullLayout.setOnActionPullListener(this);
//		mPullLayout.setOnPullStateChangeListener(this);

		mProgress = findViewById(android.R.id.progress);
		mActionImage = findViewById(android.R.id.icon);
		mActionText = (TextView) findViewById(R.id.pull_note);
		mTimeText = (TextView) findViewById(R.id.refresh_time);

//		mTimeText.setText(R.string.note_not_update);
//		mActionText.setText(R.string.note_pull_down);

		onClick(findViewById(R.id.btn_host_info));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_host_info:
			mBox.setTextColor(getResources().getColor(R.color.slidemenu_text));
			mHostInfo.setTextColor(getResources().getColor(R.color.blue));
			mActivonView.setBackgroundResource(R.drawable.new_tab_02);
			mType = 0;
			startLoading();
			break;
		case R.id.btn_box:

			mBox.setTextColor(getResources().getColor(R.color.blue));
			mHostInfo.setTextColor(getResources().getColor(R.color.slidemenu_text));
			mActivonView.setBackgroundResource(R.drawable.new_tab_01);
			mType = 1;
			startLoading();
			break;
		default:
			super.onClick(v);
			break;
		}
	}

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
//		if (!mInLoading) {
//			mInLoading = true;
////			mPullLayout.setEnableStopInActionView(true);
//			mActionImage.clearAnimation();
//			mActionImage.setVisibility(View.GONE);
//			mProgress.setVisibility(View.VISIBLE);
//			mActionText.setText(R.string.note_pull_loading);
			loadData(mType);
//		}
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
		return R.layout.attachmentlist;
	}

}
