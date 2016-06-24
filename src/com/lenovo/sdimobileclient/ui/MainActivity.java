
package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.LenovoServicesApplication;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.data.Account;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 应用主界面
 * 
 * @author zhangshaofang
 * 
 */
public class MainActivity extends TabActivity implements Constants {
	public static TabHost tabHost;
	public static final String TAB_HOME = "tabHome";
	public static final String TAB_PROCESS = "tabProcess";
	public static final String TAB_ORDER = "tabOrder";
	public static final String TAB_SUPPORT = "tabSupport";
	public static final String TAB_MORE = "tabMore";
	public long mLastBack;
	private OkHttpHelper mHttpHelper;
	public static final int MIN_BACK_DURATION = 5 * 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		((LenovoServicesApplication) getApplication()).getActivities().add(this);
		mHttpHelper = OkHttpHelper.getInstance(this);

		init();

	}

	/**
	 * 初始化切换标签页
	 */
	private void init() {

		tabHost = getTabHost();
		ArrayList<MainActivity.TabItem> tabItems = new ArrayList<MainActivity.TabItem>();
		initTabItems(tabItems);
		// 初始化tabhost
		for (TabItem t : tabItems) {
			TabSpec spec = tabHost.newTabSpec(t.tabname);
			spec.setIndicator(t.view);
			spec.setContent(t.intent);
			tabHost.addTab(spec);
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String paramString) {

				TabOrderActivity.needRefrash = false;

			}
		});

	}

	// 主界面底部button
	private void initTabItems(ArrayList<MainActivity.TabItem> tabItems) {
		LayoutInflater inflate = LayoutInflater.from(getApplicationContext());

		// 工单
		tabItems.add(new TabItem(TAB_ORDER, getView(inflate, R.drawable.ic_tab_order, R.string.tab_order, true), TabOrderNewActivity.class));
		// 提醒
		// tabItems.add(new TabItem(TAB_PROCESS, getView(inflate,
		// R.drawable.ic_tab_process, R.string.tab_process_tixing, true),
		// TabTipActivity.class));
		// 备件
		tabItems.add(new TabItem(TAB_PROCESS, getView(inflate, R.drawable.ic_tab_process, R.string.tab_process_beijian, true), TabSpareActivity.class));

		// 空占位
		tabItems.add(new TabItem(null, getView(inflate, R.drawable.ic_null, R.string.tab_process_null, false), TabTipActivity.class));
		// 知识
		tabItems.add(new TabItem(TAB_SUPPORT, getView(inflate, R.drawable.ic_tab_support, R.string.tab_support_knowledge, true), TabSupportActivity.class));
		// 更多
		tabItems.add(new TabItem(TAB_MORE, getView(inflate, R.drawable.ic_tab_more, R.string.tab_more, true), TabMoreActivity.class));

		findViewById(R.id.scan_code).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doScan();
			}
		});
	}

	private void doScan() {
		Intent intent = new Intent(this, HostSearchActivity.class);
		intent.putExtra("result", "doscan");
		intent.putExtra("title", "扫描主机条码");
		startActivity(intent);
	}

	/**
	 * 加载页面
	 * 
	 * @param inflater
	 * @param IconId
	 *            标签图片id
	 * @param strId
	 *            标签名称id
	 * @param divVisible
	 *            是否显示
	 * @return
	 */
	private View getView(LayoutInflater inflater, int IconId, int strId, boolean divVisible) {
		View tabIndicator = inflater.inflate(R.layout.tab_indicator, null, false);

		final TextView tv = (TextView) tabIndicator.findViewById(android.R.id.title);
		TextPaint tp = tv.getPaint();
		tp.setFakeBoldText(true);
		tv.setText(strId);
		final ImageView iconView = (ImageView) tabIndicator.findViewById(android.R.id.icon);
		iconView.setImageResource(IconId);
		tabIndicator.setVisibility(divVisible ? View.VISIBLE : View.INVISIBLE);

		return tabIndicator;
	}

	protected void back(Activity activity) {
		Activity parent = activity.getParent();
		if (parent == null) {
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		if (parent instanceof TabGroup) {
			TabGroup group = (TabGroup) parent;
			if (group.isCurentFirst()) {
				back(group);
			} else {
				finish();

			}
		} else if (parent instanceof TabActivity) {
			TabHost tab = ((TabActivity) parent).getTabHost();
			int index = tab.getCurrentTab();
			if (index == 0) {
				back(parent);
			} else {
				parent.finish();
				// tab.setCurrentTab(0);
			}
		} else {
			finish();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Bundle b = ((LenovoServicesApplication)
		// getApplication()).getRedirectParam();
		// if (b == null) {
		// return;
		// }
		//
		// String tag = b.getString(EXTRA_TAG);
		// if (TextUtils.isEmpty(tag)) {
		// return;
		// }
		// getTabHost().setCurrentTabByTag(tag);
		//
		// Intent intent = b.getParcelable(EXTRA_INTENT);
		// if (intent == null) {
		// return;
		// }
		// Activity child = getLocalActivityManager().getCurrentActivity();
		// child.startActivityFromChild(null, intent, -1);
	}

	/**
	 * 监听返回按键，5秒内，连续两次返回键则退出应用
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long current = System.currentTimeMillis();
			if (current - mLastBack >= MIN_BACK_DURATION) {
				Activity activity = getCurrentActivity();
				if (activity != null && activity instanceof TabGroup) {
					TabGroup tabGroup = (TabGroup) activity;
					if (tabGroup.isCurentFirst()) {
						mLastBack = current;
						Toast.makeText(this, R.string.title_exit, MIN_BACK_DURATION).show();
						return true;
					} else {
						return tabGroup.onKeyDown(keyCode, event);
					}
				}
			} else {

				moveTaskToBack(true);
				// for (Activity activity : ((LenovoServicesApplication)
				// getApplication()).getActivities()) {
				// activity.finish();
				// }
				// ((LenovoServicesApplication)
				// getApplication()).getActivities().clear();
				// System.exit(0);
				// ((LenovoServicesApplication) getApplication()).onTerminate();
				// ActivityManager manager = (ActivityManager)
				// getSystemService(ACTIVITY_SERVICE);
				// manager.killBackgroundProcesses(getPackageName());
				// doLogOut();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private static final int CALLBACK_LOGOUT = 1;

	@Override
	protected Dialog onCreateDialog(int id) {
		return ActivityUtils.onCreateDialog(this, id);
	}

	private void doLogOut() {
		showDialog(DLG_LOGOUT);
		List<Account> accounts = Account.queryAccount(this);
		if (!accounts.isEmpty()) {
			Account acc = accounts.get(0);
			acc.auto = false;
			acc.password = "";
			acc.update(this);

			HashMap<String, String> hashMap = new HashMap<String, String>();
			// postValues.add(new ParamPair(PARAM_USERNAME, acc.username));

			hashMap.put(PARAM_USERNAME, acc.username);

			// NetworkPath path = new Netpath(URL_LOGOUT, postValues);
			// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
			// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
			// mCacheManager.load(CALLBACK_LOGOUT, params, this);

			mHttpHelper.load(URL_LOGOUT, mCallback, hashMap, this);

		} else {
			logOut();
		}

	}

	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);
		// ZSF: This part use to start the feature: when click second times
		// should reset TabGroup
		TabHost tabHost = getTabHost();
		if (tabHost instanceof AdvancedTabHost) {
			((AdvancedTabHost) tabHost).setSelectChangeListener(new AdvancedTabHost.TabSelectChangeListener());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);

		// ZSF: This use to work around a android bug. If there is't an id in
		// first tab with the same as others, we can not do this.

		LocalActivityManager localActivityManager = getLocalActivityManager();
		if (localActivityManager != null) {
			Activity activity = localActivityManager.getActivity(TAB_HOME);
			if (activity instanceof TabGroup) {
				((TabGroup) activity).onRestoreInstanceState(state);
			}
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// ZSF: This use to work around a android bug. If there is't an id in
		// first tab with the same as others, we can not do this.

		LocalActivityManager localActivityManager = getLocalActivityManager();

		if (localActivityManager != null) {
			Activity activity = localActivityManager.getActivity(TAB_HOME);
			if (activity instanceof TabGroup) {
				((TabGroup) activity).onSaveInstanceState(outState);
			}
		}

		super.onSaveInstanceState(outState);
	}

	/**
	 * 标签页实体
	 * 
	 * @author zhangshaofang
	 * @return
	 * 
	 */

	public class TabItem {
		private String tabname;
		private View view;
		private Intent intent;

		public TabItem(String tabname, View view, Class<? extends Activity> activity) {
			this.tabname = tabname;
			this.view = view;
			this.intent = getIntent(MainActivity.this, activity);
		}
	}

	public Intent getIntent(Context context, Class<? extends Activity> c) {
		Intent i = new Intent(context, TabGroup.class);
		Intent targetIntent = new Intent(context, c);
		i.putExtra(TabGroup.EXTRA_INTENT, targetIntent);
		return i;
	}

	private void dismisDialog(int id) {
		try {

			dismissDialog(id);
		} catch (Exception e) {
		}
	}

	private void logOut() {
		Utils.clearEngineerInfo(this);
		for (Activity activity : ((LenovoServicesApplication) getApplication()).getActivities()) {
			activity.finish();
		}
		((LenovoServicesApplication) getApplication()).getActivities().clear();
		System.exit(0);
		((LenovoServicesApplication) getApplication()).onTerminate();
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		manager.killBackgroundProcesses(getPackageName());
		back(this);
	}

	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {
			dismisDialog(DLG_LOGOUT);
			boolean safe = mHttpHelper.isSuccessResult(result, MainActivity.this);

			if (safe) {
				return;
			}
			logOut();

		}
	};

	/*
	 * @Override public void dataLoaded(int id, CacheParams params, ICacheInfo
	 * cachehInfo) { dismisDialog(DLG_LOGOUT); logOut(); // boolean safe =
	 * ActivityUtils.prehandleNetworkData(this, this, id, // params, cachehInfo,
	 * false); // if (!safe) { // return; // } // switch (id) { // case
	 * CALLBACK_LOGOUT: // // break; // // default: // break; // }
	 * 
	 * }
	 */}
