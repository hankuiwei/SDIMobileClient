package com.lenovo.sdimobileclient.data;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * 提醒
 * 
 * @author zhangshaofang
 * 
 */
public class AlarmAlert extends AbsData {
	public static final String TABLE_NAME = "alerttable";
	private static final String TYPE = "type";
	private static final String BELL = "bell";
	private static final String SHOCK = "shock";
	private static final String MSG = "msg";
	private static final String ORDERID = "orderId";
	private static final String TIPTIME = "TIPTIME";
	/**
	 * 提醒类型
	 */
	public int type;
	/**
	 * 响铃
	 */
	public int bell;
	/**
	 * 震动
	 */
	public int shock;
	/**
	 * 提醒时间
	 */
	public long timestamp;
	/**
	 * 内容
	 */
	public String msg;
	/**
	 * 工单Id
	 */
	public String orderId;
	public String tipTime;

	public AlarmAlert() {
	}

	public AlarmAlert(int type, int bell, int shock, String msg, String orderId, String tipTime) {
		this.type = type;
		this.bell = bell;
		this.shock = shock;
		this.msg = msg;
		this.orderId = orderId;
		this.tipTime = tipTime;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public static String createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(TABLE_NAME).append("(").append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,").append(TYPE).append(" INTEGER, ")
				.append(BELL).append(" INTEGER, ").append(SHOCK).append(" INTEGER, ").append(MSG).append(" STRING, ").append(ORDERID).append(" STRING, ")
				.append(TIPTIME).append(" STRING, ").append(TIMESTAMP).append(" LONG ").append(");");
		return sb.toString();
	}

	public static ArrayList<AlarmAlert> queryAlarmAlert(Context context) {
		ArrayList<AlarmAlert> accounts = new ArrayList<AlarmAlert>();
		Uri uri = getContentUri(AlarmAlert.class, context);
		ContentResolver resolver = context.getContentResolver();
		String sortOrder = TIMESTAMP + " desc";
		Cursor cursor = resolver.query(uri, null, null, null, sortOrder);
		while (cursor.moveToNext()) {
			AlarmAlert account = new AlarmAlert();
			setValues(cursor, account);
			accounts.add(account);
		}
		cursor.close();
		return accounts;
	}

	public static AlarmAlert queryByID(Context context, int id) {
		Uri uri = ContentUris.withAppendedId(getContentUri(AlarmAlert.class, context), id);
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		AlarmAlert product = null;
		if (cursor.moveToFirst()) {
			product = new AlarmAlert();
			setValues(cursor, product);
			cursor.close();
		}
		return product;
	}
	/**
	 * 返回最近一次工单提醒
	 * @param context
	 * @param orderId
	 * @param tipTime
	 * @return
	 */
	public static AlarmAlert queryByOrderID(Context context, String orderId, String tipTime) {
		Uri uri = getContentUri(AlarmAlert.class, context);
		String selection = ORDERID + " = " + orderId + " and " + TIPTIME + " = '" + tipTime+"'";
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, null, selection, null, null);
		AlarmAlert product = null;
		if (cursor.moveToFirst()) {
			product = new AlarmAlert();
			setValues(cursor, product);
			cursor.close();
		}
		return product;
	}

	public ContentValues putValues() {
		ContentValues values = new ContentValues();
		values.put(TYPE, type);
		values.put(BELL, bell);
		values.put(SHOCK, shock);
		values.put(MSG, msg);
		values.put(ORDERID, orderId);
		values.put(TIPTIME, tipTime);
		values.put(TIMESTAMP, System.currentTimeMillis());
		return values;
	}

	public static AlarmAlert setValues(Cursor cursor, AlarmAlert alert) {
		alert._id = cursor.getInt(cursor.getColumnIndex(_ID));
		alert.type = cursor.getInt(cursor.getColumnIndex(TYPE));
		alert.bell = cursor.getInt(cursor.getColumnIndex(BELL));
		alert.shock = cursor.getInt(cursor.getColumnIndex(SHOCK));
		alert.msg = cursor.getString(cursor.getColumnIndex(MSG));
		alert.orderId = cursor.getString(cursor.getColumnIndex(ORDERID));
		alert.tipTime = cursor.getString(cursor.getColumnIndex(TIPTIME));
		alert.timestamp = cursor.getLong(cursor.getColumnIndex(TIMESTAMP));
		return alert;
	}
}
