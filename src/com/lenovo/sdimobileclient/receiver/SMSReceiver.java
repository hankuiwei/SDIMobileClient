package com.lenovo.sdimobileclient.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

/**
 * 短信广播，监听短信，锁屏
 * 
 * @author zhangshaofang
 * 
 */
public class SMSReceiver extends BroadcastReceiver {
	public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			Object pdus[] = (Object[]) intent.getExtras().get("pdus");
			for (Object pdu : pdus) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
				String sender = message.getOriginatingAddress();
				String body = message.getMessageBody();
//				if (!TextUtils.isEmpty(body) && body.startsWith("#lock#")) {
//					int s = body.lastIndexOf("#");
//					String password = body.substring(s + 1, body.length());
//					DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//					mDevicePolicyManager.lockNow();
//					mDevicePolicyManager.resetPassword(password, 0);
//					this.abortBroadcast();
//					// TODO 回复锁屏状态
//					SmsManager manager = SmsManager.getDefault();
//					manager.sendTextMessage(sender, null, "lock sucess", null, null);
//				}
			}
		}

	}
}
