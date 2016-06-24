package com.lenovo.sdimobileclient.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lenovo.sdimobileclient.LenovoServicesApplication;
import com.lenovo.sdimobileclient.receiver.AlarmReceiver;

/**
 * 定时提醒管理类
 * 
 * @author zhangshaofang
 * 
 */
public class AlarmTask {
	/**
	 * 到期提醒
	 */
	public static final int ALARMTASK_TYPE_RESPONE_DUETO = 1;
	/**
	 * 到期提醒 立刻
	 */
	public static final int ALARMTASK_TYPE_RESPONE_DUETO_NOW = 2;
	/**
	 * 超期提醒
	 */
	public static final int ALARMTASK_TYPE_RESPONE_BEYOND = 3;
	/**
	 * 超期提醒马上
	 */
	public static final int ALARMTASK_TYPE_RESPONE_BEYOND_NOW = 4;

	/**
	 * 设置提醒
	 * @param context
	 * @param orderId 工单id
	 * @param time 工单上门时间
	 */
	public static void setAlarmTask(Context context, String orderId, String time) {
		LenovoServicesApplication lenovoServicesApplication = (LenovoServicesApplication) context.getApplicationContext();
		// TODO 提醒时间间隔，单位分钟
		int tip_spit = Utils.getTipSpit(context);
		AlarmTime alarmTime = getAlarmTime(tip_spit, time);
		if (alarmTime != null) {
			int hour = alarmTime.hour;
			int minute = alarmTime.minute;
			switch (alarmTime.type) {
			case DUETO:
				if (alarmTime.respond != 0) {
					Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, c.getTime().getHours()+hour);
					c.set(Calendar.MINUTE, c.getTime().getMinutes()+minute);
					long times = c.getTimeInMillis();
					Intent intent = new Intent(context, AlarmReceiver.class);
					intent.putExtra("type", ALARMTASK_TYPE_RESPONE_DUETO);
					intent.putExtra("orderId", orderId);
					intent.putExtra("tip_spit", tip_spit);
					intent.putExtra("time", time);
//					PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
//					lenovoServicesApplication.addAlarmPendingIntent(sender);
//					AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//					am.set(AlarmManager.RTC_WAKEUP, times, sender);
//					LogUtils.println("到期提醒---设置上门提醒时间" + hour + ":" + minute);
				} else {
					Intent intent = new Intent(context, AlarmReceiver.class);
					intent.putExtra("hour", hour);
					intent.putExtra("minute", minute);
					intent.putExtra("orderId", orderId);
					intent.putExtra("tip_spit", tip_spit);
					intent.putExtra("time", time);
					intent.putExtra("type", ALARMTASK_TYPE_RESPONE_DUETO_NOW);
//					context.sendBroadcast(intent);
//					LogUtils.println("到期提醒---距离上门时间还有" + hour + "小时" + minute + "分钟");
				}
				break;
			case BEYONDTHE:
				if (alarmTime.respond != 0) {
					Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, c.getTime().getHours()+hour);
					c.set(Calendar.MINUTE, c.getTime().getMinutes()+minute);
					long times = c.getTimeInMillis();
					Intent intent = new Intent(context, AlarmReceiver.class);
					intent.putExtra("type", ALARMTASK_TYPE_RESPONE_BEYOND);
					intent.putExtra("orderId", orderId);
					intent.putExtra("tip_spit", tip_spit);
					intent.putExtra("time", time);
//					PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
//					lenovoServicesApplication.addAlarmPendingIntent(sender);
//					AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//					am.set(AlarmManager.RTC_WAKEUP, times, sender);
//					LogUtils.println("超期提醒---设置上门提醒时间" + hour + ":" + minute);
				} else {
					Intent intent = new Intent(context, AlarmReceiver.class);
					intent.putExtra("hour", hour);
					intent.putExtra("minute", minute);
					intent.putExtra("orderId", orderId);
					intent.putExtra("tip_spit", tip_spit);
					intent.putExtra("time", time);
//					intent.putExtra("type", ALARMTASK_TYPE_RESPONE_BEYOND_NOW);
//					context.sendBroadcast(intent);
//					LogUtils.println("超期提醒---以超过上门时间" + hour + "小时" + minute + "分钟");
				}
				break;
			}
		}
	}

	/**
	 * 取消提醒
	 * @param context
	 */
	public static void cancleAlarmTask(Context context) {
		LenovoServicesApplication lenovoServicesApplication = (LenovoServicesApplication) context.getApplicationContext();
		List<PendingIntent> pendingIntents = lenovoServicesApplication.getAlarmPendingIntent();
		if (pendingIntents != null) {
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			for (PendingIntent pendingIntent : pendingIntents) {
				am.cancel(pendingIntent);
			}
		}
	}

	/**
	 * 时间段截取时分，转换为分钟返回
	 * 
	 * @param times
	 * @return
	 */
	public static int getHourTimeStamp(long times) {
		Date date = new Date(times);
		int h = date.getHours();
		int m = date.getMinutes();
		int timestamp = h * 60 + m;
		return timestamp;
	}

	/**
	 * 获取闹钟提醒时间
	 * 
	 * @param alarm_time
	 *            提醒间隔
	 * @param sdate
	 *            预设提醒时间
	 * @return
	 */
	public static AlarmTime getAlarmTime(int alarm_time, String sdate) {
		long times = Utils.dateFormatTimes(sdate);
		boolean isToday = Utils.isToday(times);
		if (isToday) {
			int minutes = getHourTimeStamp(times);
			Date date = new Date();
			int current_minutes = date.getHours() * 60 + date.getMinutes();
			int spitMinutes = current_minutes - minutes;
			AlarmTime alarmTime = new AlarmTime();
			if (spitMinutes < 0) {
				/**
				 * 到期提醒
				 */
				spitMinutes = Math.abs(spitMinutes);
				alarmTime.type = AlarmType.DUETO;
				if (spitMinutes > alarm_time) {
					int s = spitMinutes - alarm_time;
					int hour = s / 60;
					int minute = s % 60;
					alarmTime.hour = hour;
					alarmTime.minute = minute;
					// 设置定时到期提醒
					alarmTime.respond = AlarmTime.ALARM_RESPOND;
				} else {
					alarmTime.respond = AlarmTime.ALARM_RESPOND_NOW;
					// 立即提醒
					int hour = spitMinutes / 60;
					int minute = spitMinutes % 60;
					alarmTime.hour = hour;
					alarmTime.minute = minute;
				}
			} else if (spitMinutes > 0) {
				alarmTime.type = AlarmType.BEYONDTHE;
				/**
				 * 超期提醒
				 */
				if (spitMinutes < alarm_time) {
					int s = current_minutes + (alarm_time - spitMinutes);
					int hour = s / 60;
					int minute = s % 60;
					alarmTime.hour = hour;
					alarmTime.minute = minute;
					alarmTime.respond = AlarmTime.ALARM_RESPOND;
					// 设置 定时超期提醒
				} else {
					alarmTime.respond = AlarmTime.ALARM_RESPOND_NOW;
					int hour = spitMinutes / 60;
					int minute = spitMinutes % 60;
					alarmTime.hour = hour;
					alarmTime.minute = minute;
					// 立即提醒已超出 超期提醒时间
				}
			} else {
				/**
				 * 到期提醒
				 */
				alarmTime.type = AlarmType.DUETO;
				alarmTime.respond = AlarmTime.ALARM_RESPOND_NOW;
			}
			return alarmTime;
		}
		return null;
	}

	public enum AlarmType {
		DUETO, BEYONDTHE
	}

	/**
	 * 提醒封装类
	 * 
	 * @author zhangshaofang
	 * 
	 */
	public static class AlarmTime {
		/**
		 * 立刻提醒
		 */
		public static final int ALARM_RESPOND_NOW = 0;
		/**
		 * 按设置时间
		 */
		public static final int ALARM_RESPOND = 1;
		/**
		 * 小时
		 */
		public int hour;
		/**
		 * 分钟
		 */
		public int minute;
		/**
		 * 提醒类型
		 */
		public AlarmType type;
		/**
		 * 提醒响应速度
		 */
		public int respond;
	}
}
