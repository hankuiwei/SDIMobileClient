/**
 * 
 */
package com.lenovo.sdimobileclient.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.foreveross.cache.CacheManager;
import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.utility.AlarmTask;
import com.lenovo.sdimobileclient.utility.Utils;

/**
 * 测试类
 * 
 * @author zhangshaofang
 * 
 */
public class TestActivity extends RootActivity {
	private static final int CALLBACK_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(android.R.id.button1).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case android.R.id.button1:
			String date = Utils.dateFormatPre(System.currentTimeMillis() - 17 * 60 * 1000);
			AlarmTask.setAlarmTask(this, "23842894038209483", date);
			TextView test = (TextView) findViewById(R.id.tv_test);
			test.setText(date);
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.test;
	}

}
