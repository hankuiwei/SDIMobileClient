package com.lenovo.sdimobileclient.ui;

import java.util.HashMap;
import java.util.List;

import com.lenovo.lsf.push.PushSDK;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.data.Account;
import com.lenovo.sdimobileclient.data.PushConfig;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

/**
 * 更多
 * 
 * @author zhangshaofang
 * 
 */
public class TabMoreActivity extends RootActivity {
	private TextView mTimeTv;
	private int mTime;
	private int mLoadID;
	private OkHttpHelper mHttpHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHttpHelper = OkHttpHelper.getInstance(this);
		
		initView();
	}

	private void initView() {
		mTimeTv = (TextView) findViewById(R.id.tv_time);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int tipSpit = preferences.getInt(PREF_TIP_SPIT, 30);
		int position;
		switch (tipSpit) {
		case 30:

			position = 0;
			break;
		case 45:

			position = 1;
			break;
		case 60:

			position = 2;
			break;
		case 120:

			position = 3;
			break;
		case 180:

			position = 4;
			break;

		default:
			position = 0;
			break;
		}

		mTime = position;

		mTimeTv.setText(getResources().getStringArray(R.array.tip_times)[position]);

		findViewById(R.id.btn_host).setOnClickListener(this);
		findViewById(R.id.more_click_zhuangxiangdan).setOnClickListener(this);
		findViewById(R.id.more_click_fujian).setOnClickListener(this);
		findViewById(R.id.more_click_setting).setOnClickListener(this);
		findViewById(R.id.btn_upload).setOnClickListener(this);
		findViewById(R.id.btn_switch_user).setOnClickListener(this);
	}

	private static final int REQUESTCODE_SETTING = 1001;
	private static final int DLG_TIP_TIMES = 2001;
	private static final int DLG_DOSUBMIT = 3001;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_switch_user:

			showDialog(DLG_DOSUBMIT);

			break;
		case R.id.btn_host: {
			Intent intent = new Intent(this, HostSearchActivity.class);
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			startActivity(intent);
		}
			break;
		case R.id.more_click_zhuangxiangdan: {
			Intent intent = new Intent(this, HostSearchActivity.class);
			intent.putExtra("type", 1);
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			startActivity(intent);
		}
			break;
		case R.id.more_click_fujian:

			Intent intent2 = new Intent(this, AttachMentActivity.class);
			intent2.putExtra(TabGroup.EXTRA_GO_OUT, true);
			startActivity(intent2);
			break;
		case R.id.more_click_setting:

			showDialog(DLG_TIP_TIMES);
			// Intent intent3 = new Intent(this, SettingActivity.class);
			// intent3.putExtra(TabGroup.EXTRA_GO_OUT, true);
			// startActivityForResult(intent3, REQUESTCODE_SETTING);
			break;
		case R.id.btn_upload: {
			Intent intent = new Intent(this, AttachUploadActivity.class);
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			String orderId = "101306179000051";
			intent.putExtra("orderId", orderId);
			startActivity(intent);
		}
		}
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
			// CacheParams params = new CacheParams(path);
			// mCacheManager.load(CALLBACK_LOGOUT, params, this);

			mLoadID = CALLBACK_LOGOUT;
			mHttpHelper.load(URL_LOGOUT, mCallback, hashMap, this);

		} else {
			logOut();
		}

	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {

		case DLG_DOSUBMIT: {

			AlertDialog.Builder builder = new AlertDialog.Builder(getRootActivity(this));

			AlertDialog result = builder.setTitle("确认退出").setMessage("您确定要退出登录吗?").setPositiveButton("确认", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					doLogOut();

					dialog.dismiss();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
				}
			}).create();
			dialog = result;
		}

			break;
		/**
		 * 设置提醒时间对话框
		 */
		case DLG_TIP_TIMES: {
			Builder builder = new AlertDialog.Builder(getRootActivity(this));
			builder.setTitle("提醒时间");
			builder.setSingleChoiceItems(R.array.tip_times, mTime, new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					String hoddy = getResources().getStringArray(R.array.tip_times)[which];
					int time;

					switch (which) {
					case 0:
						time = 30;
						break;
					case 1:
						time = 45;
						break;
					case 2:
						time = 60;
						break;
					case 3:
						time = 120;
						break;
					case 4:
						time = 180;
						break;

					default:
						time = 0;
						break;
					}
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TabMoreActivity.this);
					int tipSpit = preferences.getInt(PREF_TIP_SPIT, 30);

					if (tipSpit == 180 && time != 180) {
						startPush();
					} else if (tipSpit != 180 && time == 180) {
						StopPush();

					}

					Utils.saveTipSpit(getApplicationContext(), time);
					mTimeTv.setText(hoddy);

					Uri build = Uri.parse(URL_PUSHMSG_TIME).buildUpon().appendQueryParameter("SetTime", time + "")
							.appendQueryParameter("Engineer", Utils.getEnginnerInfo(TabMoreActivity.this).EngineerID).build();
					mLoadID = CALLBACK_PUSHMSG;
					mHttpHelper.load(build.toString(), mCallback, TabMoreActivity.this);
					dialog.dismiss();
				}
			});
			dialog = builder.create();
		}
			break;
		}
		return dialog;
	}

	private void startPush() {
		Intent intent = new Intent();
		intent.putExtra("sid", PushConfig.SID);// 10081
		intent.putExtra("receiver_name", PushConfig.RECEIVER);
		intent.putExtra("realtime_level", 1);
		PushSDK.setInitStatus(getApplicationContext(), true);

		PushSDK.register(this, intent);

	}

	private void StopPush() {
		Intent intent = new Intent();
		intent.putExtra("sid", PushConfig.SID);// 10081
		PushSDK.unregister(this, intent);

	}

	private static final int CALLBACK_PUSHMSG = 2;
	private static final int CALLBACK_LOGOUT = 1;

	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {
			dismisDialog(DLG_LOGOUT);
			boolean safe = mHttpHelper.isSuccessResult(result, TabMoreActivity.this);
			if (!safe) {
				return;
			}
			switch (mLoadID) {
			case CALLBACK_LOGOUT:
				logOut();
				break;

			case CALLBACK_PUSHMSG:

				break;

			default:
				break;
			}

		}
	};

	private void logOut() {
		Utils.clearEngineerInfo(this);
		Utils.saveLoginInfo(this, "isLogin", false);
		List<Activity> activities = getAppliction().getActivities();
		for (Activity activity : activities) {
			activity.finish();
		}
		getAppliction().getActivities().clear();
		System.exit(0);

		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		manager.killBackgroundProcesses(getPackageName());
	}

	@Override
	protected void notifyView() {
		showDialog(DLG_LOGOUT);
	}

	@Override
	public void finish() {
		mHttpHelper.cancle(TabMoreActivity.class);
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_SETTING:
			if (resultCode == RESULT_OK) {
				back(this);
			}
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.more;
	}
}
