package com.lenovo.sdimobileclient.ui;

import android.os.Bundle;

import com.lenovo.sdimobileclient.R;

/**
 * 待开发界面
 * 
 * @author zhangshaofang
 * 
 */
public class DevelopActivity extends RootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("工单处理");
		initBackBtn();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.development_view;
	}
}
