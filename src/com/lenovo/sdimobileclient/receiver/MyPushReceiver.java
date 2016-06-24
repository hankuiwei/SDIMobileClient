
package com.lenovo.sdimobileclient.receiver;

import com.lenovo.lsf.push.PushMessageReceiver;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.data.PushConfig;
import com.lenovo.sdimobileclient.ui.MsgActivity;
import com.lenovo.sdimobileclient.utility.Utils;
import com.meap.pushptfeedback.PushPtFeedBackAsyncTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.widget.Toast;

public class MyPushReceiver extends PushMessageReceiver {
	private static final String TAG = "PushReceiver." + PushConfig.SID;

	// -------------------------------------------
	// Do not override the onReceive() method !!!
	// -------------------------------------------

	// @Override
	public void onReceiveMsg(final Context context, Intent intent) {
		// 消息的主体
		String body = intent.getStringExtra("body");

		SharedPreferences sp = context.getSharedPreferences("pushdata", Context.MODE_PRIVATE);
		sp.edit().putString("body", body).commit();

		// 如果为false，meap则需要验证sessionKey，只有验证成功，才能反馈消息，此时上传的name没有任何意义，可随意定义，可以为空
		// 如果为true则不需要sessionKey，但是这里需要传一个字段，这个字段必须和上传pt是的字段一样,如："test"
		boolean isSuperFeedBack = true;
		String name = Utils.getEnginnerInfo(context).EngineerID;
		PushPtFeedBackAsyncTask pushPtFeedBackAsyncTask = new PushPtFeedBackAsyncTask() {

			@Override
			protected void onPostExecute(String response) {

				// Toast.makeText(context, response, 0).show();
				Log.i("Https", "body ----response :" + response);
			}
		};
		pushPtFeedBackAsyncTask.executeFeedBack(body, isSuperFeedBack, name, context);

		Log.i(TAG, "body :" + body);
		if (body != null) {
			// 获取body里面的mid
			
			
			String[] split = body.split("！");
			
			if (split.length!=1) {
				
				body = "";
			}
			for (int i = 1; i < split.length; i++) {
				body = body + split[i];

				if (i != split.length - 1) {
					body += "！";
				}

			}

			showNotification(context, split[0], body);
		}
	}

	@Override
	public void onReceivePT(final Context context, Intent intent) {

		// Utils.showToast(context, "pushTicketonReceivePT");
		String result = intent.getStringExtra("result");

		if ("success".equals(result)) {
			final String pushticket = intent.getStringExtra("push_ticket");
			Log.i(TAG, "pushticket : " + pushticket);
			if (pushticket != null) {
				SharedPreferences sp = context.getSharedPreferences("pushdata", Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("pt", pushticket);
				editor.commit();
				Log.i("MainActivity", "pushTicket :" + pushticket);

				// 如果为false，meap则需要验证sessionKey，只有验证成功，才能上传,此时上传的name没有任何意义，可随意定义，可以为空
				// 如果为true则不需要sessionKey，就可以直接将pt传到meap，同时用户需自定义一个字段，用来后台分组推送消息时的字段

				boolean isSuperUpload = true;
				final String engineerID = Utils.getEnginnerInfo(context).EngineerID;
				PushPtFeedBackAsyncTask pushPtFeedBackAsyncTask = new PushPtFeedBackAsyncTask() {

					@Override
					protected void onPostExecute(String response) {

						// Toast.makeText(context, "ptptptptptp " + response + "
						// " + PushConfig.SID + engineerID, 0).show();

						Log.i("Https", "pt ------response :" + response);
					}
				};
				pushPtFeedBackAsyncTask.executeUploadPt(pushticket, isSuperUpload, engineerID, context);
				Log.i("Https", "pt ------engineerID :" + engineerID);
				sp.edit().putString("pt", pushticket).commit();
			}
		}
	}

	int messageNotificationID = 0;
	private Notification notification;

	private void showNotification(Context context, String title, String content) {

		Intent intent = new Intent();
		intent.setAction(PushConfig.SERVICE_MSG);

		intent.putExtra("title", title);
		intent.putExtra("content", content);
		context.startService(intent);

		/**
		 * 
		 * MsgActivity.addListItem("MESSAGE", content); NotificationManager
		 * manager = (NotificationManager)
		 * context.getSystemService(Context.NOTIFICATION_SERVICE); Intent i =
		 * new Intent(PushConfig.ACTION_MSG);
		 * i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		 * Intent.FLAG_ACTIVITY_NEW_TASK); int id = 8848; PendingIntent
		 * pendingIntent2 = PendingIntent.getActivity(context, id, i,
		 * PendingIntent.FLAG_UPDATE_CURRENT); //
		 * 通过Notification.Builder来创建通知，注意API Level // API11之后才支持 // Notification
		 * notify2 = new //
		 * Notification.Builder(context).setSmallIcon(R.drawable.logo128) // //
		 * 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，
		 * 可以使用setLargeIcon(Bitmap // // icon) // .setTicker("服务交付：" +
		 * "您有新短消息，请注意查收！")// 设置在status // // bar上显示的提示文字 //
		 * .setContentTitle(title)// 设置在下拉status // //
		 * bar后Activity，本例子中的NotififyMessage的TextView中显示的标题 //
		 * .setContentText(content)// TextView中显示的详细内容 //
		 * .setContentIntent(pendingIntent2) // 关联PendingIntent ////
		 * .setNumber(1) // //
		 * 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），
		 * 可以指定显示哪一个。 // .getNotification(); // 需要注意build()是在API level
		 * 
		 * if (notification == null) {
		 * 
		 * notification = new Notification(); notification.icon =
		 * R.drawable.logo128; notification.tickerText = "服务交付：" +
		 * "您有新短消息，请注意查收！"; notification.defaults = Notification.DEFAULT_SOUND;
		 * 
		 * }
		 * 
		 * notification.setLatestEventInfo(context, title, content,
		 * pendingIntent2);
		 * 
		 * // 16及之后增加的，在API11中可以使用getNotificatin()来代替 notification.flags |=
		 * Notification.FLAG_AUTO_CANCEL; manager.notify(messageNotificationID,
		 * notification); messageNotificationID++;
		 * 
		 * 
		 */
	}

}