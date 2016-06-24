package com.lenovo.sdimobileclient.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.lenovo.sdimobileclient.provider.LenovoServicesProvider;

public abstract class AbsData implements BaseColumns {

	public static final int STATE_SYNC = 1;
	public static final int STATE_LOCAL = 2;
	// public static final int STATE_SYNCING = 3;

	protected static String TABLE_NAME = null;
	protected static final String TIMESTAMP = "timestamp";
	protected static final String STATE = "state";
	protected static Uri sContentUri = null;
	public int _id = -1;
	public long timestamp;
	public int state;
	public AbsData() {
	}

	public static Uri getContentUri(Class<? extends AbsData> c, Context context) {
		try {
			AbsData absData = c.newInstance();
			TABLE_NAME = absData.getTableName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sContentUri = Uri.withAppendedPath(
				LenovoServicesProvider.getContentUri(context), TABLE_NAME);
		return sContentUri;
	}

	public long insert(Context context) {
		Uri uri = getContentUri(getClass(), context);
		ContentResolver resolver = context.getContentResolver();
		timestamp = System.currentTimeMillis();
		ContentValues values = putValues();
		long rowId = -1;
		try {
			Uri result = resolver.insert(uri, values);
			
			rowId = ContentUris.parseId(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowId;
	}

	public void update(Context context) {
		if (_id == -1) {
			return;
		}
		Uri uri = ContentUris.withAppendedId(
				getContentUri(getClass(), context), _id);
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = putValues();
		resolver.update(uri, values, null, null);
	}

	public void delete(Context context) {
		Uri uri = ContentUris.withAppendedId(
				getContentUri(getClass(), context), _id);
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(uri, null, null);
	}
	public void deleteAll(Context context) {
		Uri uri = getContentUri(getClass(), context);
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(uri, null, null);
	}

	protected abstract ContentValues putValues();

	public abstract String getTableName();
}
