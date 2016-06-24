package com.lenovo.sdimobileclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 开机启动广播
 * 
 * @author zhangshaofang
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(BOOT_ACTION)) {
			registerMsgListener(context);
		}
	}

	public static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
	public static final String MSG_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	private void registerMsgListener(Context context) {
		try {
			IntentFilter filter = new IntentFilter(MSG_ACTION);
			filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);// 设置优先级最大
			SMSReceiver recevier = new SMSReceiver();
			context.getApplicationContext().registerReceiver(recevier, filter);
		} catch (Exception e) {
		}
	}
}
