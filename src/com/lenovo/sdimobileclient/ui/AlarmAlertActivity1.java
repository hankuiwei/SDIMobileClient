package com.lenovo.sdimobileclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.data.AlarmAlert;
import com.lenovo.sdimobileclient.service.AlarmKlaxonService;
import com.lenovo.sdimobileclient.utility.AlarmTask;

/**
 * 定时提醒
 * 
 * @author zhangshaofang
 * 
 */
public class AlarmAlertActivity1 extends Activity implements Constants {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		int hour = bundle.getInt("hour");
		int minute = bundle.getInt("minute");
		int type = bundle.getInt("type");
		int tip_spit = bundle.getInt("tip_spit");
		String time = bundle.getString("time");
		final String orderId = bundle.getString("orderId");
		String title = null;
		String content = null;
		switch (type) {
		case AlarmTask.ALARMTASK_TYPE_RESPONE_DUETO:
			title = "到期提醒";
			content = "将在" + tip_spit + "分钟到达预约时间,";
			break;
		case AlarmTask.ALARMTASK_TYPE_RESPONE_DUETO_NOW:
			title = "到期提醒";
			content = "将在" + hour + "小时" + minute + "分钟到达预约时间,";
			break;
		case AlarmTask.ALARMTASK_TYPE_RESPONE_BEYOND:
			title = "超期提醒";
			content = "已超出预约上门时间" + tip_spit + "分钟,";
			break;
		case AlarmTask.ALARMTASK_TYPE_RESPONE_BEYOND_NOW:
			title = "超期提醒";
			content = "已超出预约上门时间" + hour + "小时" + minute + "分钟,";
			break;
		}
		String tip = "工单编号为" + orderId + content + "请及时到达现场";
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int bell = preferences.getInt(PREF_TIP_BELL, 0);
		int shock = preferences.getInt(PREF_TIP_SHOCK, 0);
		AlarmAlert alarmAlert = new AlarmAlert(type, bell, shock, tip, orderId, time);
		alarmAlert.insert(this);
		/**
		 * 跳出的闹铃警示
		 */
		AlertDialog.Builder builder = new AlertDialog.Builder(AlarmAlertActivity1.this);
		builder.setCancelable(true);
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();

			}
		});
		builder.setIcon(R.drawable.ic_launcher).setTitle(title).setMessage(tip).setPositiveButton("查看工单", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				AlarmAlertActivity1.this.finish();
				Intent intent = new Intent(AlarmAlertActivity1.this, OrderActivity.class);
				intent.putExtra("orderId", orderId);
				startActivity(intent);
			}
		}).show();
		Intent intent = new Intent(this, AlarmKlaxonService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
		}

		public void onServiceDisconnected(ComponentName name) {
		}
	};
}
