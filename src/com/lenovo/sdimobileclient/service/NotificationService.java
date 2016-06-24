package com.lenovo.sdimobileclient.service;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.data.PushConfig;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service {

	// 获取消息线程

	// 点击查看
	private Intent messageIntent = null;
	private PendingIntent messagePendingIntent = null;

	// 通知栏消息
	private int messageNotificationID = 1000;
	private Notification messageNotification = null;
	private NotificationManager messageNotificatioManager = null;

	private String mContent;

	private String mTitle;

	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			mContent = intent.getStringExtra("content");
			mTitle = intent.getStringExtra("title");

			// 初始化
			messageNotification = new Notification();
			messageNotification.icon = R.drawable.logo128;
			messageNotification.tickerText = "服务交付： " + "您有新短消息，请注意查收！";
			messageNotification.defaults = Notification.DEFAULT_SOUND;
			messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			messageIntent = new Intent(PushConfig.ACTION_MSG);
			messagePendingIntent = PendingIntent.getActivity(this, 0, messageIntent, 0);

			// 更新通知栏

			messageNotification.setLatestEventInfo(getApplicationContext(), mTitle, mContent, messagePendingIntent);
			messageNotification.flags |= Notification.FLAG_AUTO_CANCEL;
			messageNotificatioManager.notify(messageNotificationID, messageNotification);

			// 每次通知完，通知ID递增一下，避免消息覆盖掉
			messageNotificationID++;
		}

		return super.onStartCommand(intent, flags, startId);
	}



	@Override
	public void onDestroy() {

		super.onDestroy();
	}

}