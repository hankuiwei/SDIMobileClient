package com.lenovo.sdimobileclient.ui.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.ui.OrderActivity.DateListener;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2013年3月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * 
 *           } });
 * 
 * @version 1.0
 */
public class DateTimePickDialogUtil implements OnDateChangedListener, OnTimeChangedListener, OnClickListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	private Dialog dialog;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	private DateListener mDateListener;
	private TextView title;
	private TextView inputDate;

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialogUtil(Activity activity, String initDateTime, DateListener dateListener) {
		this.activity = activity;
		this.initDateTime = initDateTime;
		mDateListener = dateListener;

	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar = Calendar.getInstance();
		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + "年" + calendar.get(Calendar.MONTH) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
					+ calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
		}

		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);

		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public Dialog dateTimePicKDialog(final TextView inputDate) {
		
		 this.inputDate = inputDate ;
		
		LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.common_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);

		Utils.resizePicker(datePicker);
		Utils.resizePicker(timePicker);
		dateTimeLayout.findViewById(R.id.cancle).setOnClickListener(this);
		dateTimeLayout.findViewById(R.id.setting).setOnClickListener(this);
		title = (TextView) dateTimeLayout.findViewById(R.id.title);
		title.setText(initDateTime);
		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		dialog = new Dialog(activity, R.style.dialog);

		dialog.setContentView(dateTimeLayout);
		dialog.show();
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();

		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.height = (int) (LayoutParams.WRAP_CONTENT); // 高度设置为屏幕的0.8
		lp.width = (int) (LayoutParams.MATCH_PARENT);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.getWindow().setAttributes(lp);

		// ad = new AlertDialog.Builder(activity,
		// R.style.dialog).setTitle(initDateTime).setView(dateTimeLayout)
		// .setPositiveButton("设置", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// mDateListener.date(dateTime);
		// inputDate.setText(dateTime);
		// }
		// }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// // inputDate.setText("");
		// }
		// }).show();

		onDateChanged(null, 0, 0, 0);
		return dialog;
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();

		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

		dateTime = sdf.format(calendar.getTime());
		title.setText(dateTime);
	}

	/**
	 * 实现将初始日期时间2013年03月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// 将初始日期时间2013年03月02日 16:45 拆分成年 月 日 时 分 秒
		String date = spliteString(initDateTime, "日", "index", "front");
		String time = spliteString(initDateTime, "日", "index", "back");

		String yearStr = spliteString(date, "年", "index", "front");
		String monthAndDay = spliteString(date, "年", "index", "back");

		String monthStr = spliteString(monthAndDay, "月", "index", "front");
		String dayStr = spliteString(monthAndDay, "月", "index", "back");

		String hourStr = spliteString(time, ":", "index", "front");
		String minuteStr = spliteString(time, ":", "index", "back");

		int currentYear = Integer.valueOf(yearStr.trim()).intValue();
		int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
		int currentDay = Integer.valueOf(dayStr.trim()).intValue();
		int currentHour = Integer.valueOf(hourStr.trim()).intValue();
		int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

		calendar.set(currentYear, currentMonth, currentDay, currentHour, currentMinute);
		return calendar;
	}

	public static String spliteString(String srcStr, String pattern, String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern);
		} else {
			loc = srcStr.lastIndexOf(pattern);
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc);
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length());
		}
		return result;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.cancle:

			dialog.cancel();
			break;
		case R.id.setting:
			
			mDateListener.date(dateTime);
			inputDate.setText(dateTime);
			dialog.cancel();
			break;

		default:
			break;
		}

	}

}
