package com.lenovo.sdimobileclient.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.data.AlarmAlert;
import com.lenovo.sdimobileclient.utility.Utils;

/**
 * 设置提醒
 * 
 * @author zhangshaofang
 * 
 */
public class SettingActivity extends RootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private TextView mTimeTv;
	private TextView mModeTv;
	private int mFun;
	private int mTime;

	private void initView() {
		initBackBtn();
		findViewById(R.id.btn_set_time).setOnClickListener(this);
		findViewById(R.id.btn_set_tip).setOnClickListener(this);
		findViewById(R.id.btn_delete_msg).setOnClickListener(this);
		mTimeTv = (TextView) findViewById(R.id.tv_time);
		mModeTv = (TextView) findViewById(R.id.tv_mode);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int tipSpit = preferences.getInt(PREF_TIP_SPIT, 15);
		mTime = tipSpit / 15 - 1;

		int bell = preferences.getInt(PREF_TIP_BELL, 0);
		int shock = preferences.getInt(PREF_TIP_SHOCK, 0);
		mTimeTv.setText(tipSpit + "分钟");
		if (bell == 0 && shock != 0) {
			mModeTv.setText("震动");
			mFun = 1;
		} else if (bell != 0 && shock == 0) {
			mModeTv.setText("响铃");
			mFun = 0;
		} else {
			mFun = 2;
			mModeTv.setText("震动+响铃");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_set_time:
			showDialog(DLG_TIP_TIMES);
			break;
		case R.id.btn_set_tip:
			showDialog(DLG_TIP_MODE);
			break;
		case R.id.btn_delete_msg:
			showDialog(DLG_TIP_DELETE);
			break;
		default:
			super.onClick(v);
			break;
		}

	}

	private static final int DLG_TIP_TIMES = 2001;
	private static final int DLG_TIP_MODE = 2002;
	private static final int DLG_TIP_DELETE = 2003;

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		/**
		 * 设置提醒时间对话框
		 */
		case DLG_TIP_TIMES: {
			Builder builder = new android.app.AlertDialog.Builder(getRootActivity(this));
			builder.setTitle("提醒时间");
			builder.setSingleChoiceItems(R.array.tip_times, mTime, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String hoddy = getResources().getStringArray(R.array.tip_times)[which];
					Utils.saveTipSpit(getApplicationContext(), (which + 1) * 15);
					mTimeTv.setText(hoddy);
					dialog.dismiss();
				}
			});
			dialog = builder.create();
		}
			break;
		case DLG_TIP_MODE: {
			/**
			 * 设置提醒方式对话框
			 */
			Builder builder = new android.app.AlertDialog.Builder(this);
			builder.setTitle("提醒方式");
			builder.setSingleChoiceItems(R.array.tip_mode, mFun, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String hoddy = getResources().getStringArray(R.array.tip_mode)[which];
					int bell = 0;
					int shock = 0;
					switch (which) {
					case 0:
						bell = 1;
						break;
					case 1:
						shock = 1;
						break;
					case 2:
						bell = 1;
						shock = 1;
						break;
					}
					Utils.saveTipFunction(getApplicationContext(), bell, shock);
					mModeTv.setText(hoddy);
					dialog.dismiss();
				}
			});
			dialog = builder.create();
		}
			break;
		case DLG_TIP_DELETE:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("清楚全部提醒记录？");
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			AlertDialog result = builder.create();
			result = builder.create();
			result.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					new AlarmAlert().deleteAll(getApplicationContext());

				}
			});
			result.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (DialogInterface.OnClickListener) null);
			result.setOwnerActivity(this);
			result.setCancelable(false);
			dialog = result;
			break;
		default:
			dialog = super.onCreateDialog(id);
			break;
		}
		return dialog;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.setting;
	}
}
