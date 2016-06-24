package com.lenovo.sdimobileclient.receiver;

import com.lenovo.sdimobileclient.ui.AlarmAlertActivity1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
/**
 * 定时闹钟广播
 * @author zhangshaofang
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int hour = intent.getIntExtra("hour", 0);
		int minute = intent.getIntExtra("minute", 0);
		int type = intent.getIntExtra("type", 0);
		int tip_spit = intent.getIntExtra("tip_spit", 0);
		String time = intent.getStringExtra("time");
		String orderId = intent.getStringExtra("orderId");
		Intent i = new Intent(context, AlarmAlertActivity1.class);
		Bundle bundleRet = new Bundle();
		bundleRet.putString("STR_CALLER", "");
		bundleRet.putInt("hour", hour);
		bundleRet.putString("time", time);
		bundleRet.putInt("minute", minute);
		bundleRet.putInt("type", type);
		bundleRet.putInt("tip_spit", tip_spit);
		bundleRet.putString("orderId", orderId);
		i.putExtras(bundleRet);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

}
