package com.lenovo.sdimobileclient.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.lenovo.sdimobileclient.data.Account;
import com.lenovo.sdimobileclient.data.AlarmAlert;
import com.lenovo.sdimobileclient.data.Attach;

/**
 * 客户端数据库访问接口
 * 
 * @author zhangshaofang
 *
 */
public class LenovoServicesProvider extends ContentProvider {
	private static final String[] TABLENAMES = new String[] { Account.TABLE_NAME, Attach.TABLE_NAME, AlarmAlert.TABLE_NAME };
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static String sAuthority = null;
	private static Uri sContentUri = null;
	private static final String LOG_TAG = "ShoppingHelperProvider";
	private static final int SHOPPING = 1;
	private static final int SHOPPING_ID = 2;
	private DBOpenHelper dbOpenHelper;

	@Override
	public boolean onCreate() {
		dbOpenHelper = new DBOpenHelper(this.getContext());
		init(getContext());
		return true;
	}

	synchronized private static void init(Context c) {
		if (!TextUtils.isEmpty(sAuthority)) {
			return;
		}
		ProviderInfo info = null;
		ProviderInfo[] providerInfos = null;
		try {
			providerInfos = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_PROVIDERS).providers;
		} catch (NameNotFoundException e) {
			Log.w(LOG_TAG, "Failed get Package info.", e);
		}

		if (providerInfos != null) {
			String className = LenovoServicesProvider.class.getName();
			for (int i = 0; i < providerInfos.length; i++) {
				if (className.equals(providerInfos[i].name)) {
					info = providerInfos[i];
					break;
				}
			}
		}
		if (info == null) {
			throw new IllegalArgumentException("Not found the definition for this Provider in AndroidManifest.xml.");
		}

		sAuthority = info.authority;
		if (TextUtils.isEmpty(sAuthority)) {
			return;
		}
		sContentUri = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + sAuthority);
		UriMatcher matcher = sURIMatcher;
		for (int i = 0; i < TABLENAMES.length; i++) {
			matcher.addURI(sAuthority, TABLENAMES[i], SHOPPING);
			matcher.addURI(sAuthority, TABLENAMES[i] + "/#", SHOPPING_ID);
		}
	}

	public String getType(Uri uri) {
		final int match = sURIMatcher.match(uri);
		String ret = null;
		switch (match) {
		case SHOPPING:
			ret = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + sAuthority;
			break;
		case SHOPPING_ID:
			ret = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + sAuthority;
			break;
		default:
			throw new IllegalArgumentException("this is an unknown Uri:" + uri);
		}
		return ret;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Uri insertUri = null;
		String tableName = uri.getLastPathSegment();
		switch (sURIMatcher.match(uri)) {
		case SHOPPING:
			long rowid = db.insert(tableName, null, values);
			insertUri = ContentUris.withAppendedId(uri, rowid);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		case SHOPPING_ID:
			throw new IllegalArgumentException("Can't support this Uri:" + uri);
		default:
			throw new IllegalArgumentException("this is an unknown Uri:" + uri);
		}
		return insertUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor result = null;
		String tableName = uri.getLastPathSegment();
		switch (sURIMatcher.match(uri)) {
		case SHOPPING:
			result = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case SHOPPING_ID:
			tableName = subTableName(uri);
			long rowid = ContentUris.parseId(uri);
			String where = BaseColumns._ID + "=" + rowid;
			if (TextUtils.isEmpty(selection)) {
				selection = where;
				// where += " and " + selection;
			}

			result = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("this is an unknown Uri:" + uri);
		}
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int num = 0;
		String tableName = subTableName(uri);
		switch (sURIMatcher.match(uri)) {
		case SHOPPING:
			num = db.update(tableName, values, selection, selectionArgs);
			break;
		case SHOPPING_ID:
			long rowid = ContentUris.parseId(uri);
			String where = BaseColumns._ID + "=" + rowid;
			if (!TextUtils.isEmpty(selection)) {
				where += " and " + selection;
			}
			num = db.update(tableName, values, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("this is an unknown Uri:" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return num;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int num = 0;
		String tableName = subTableName(uri);
		switch (sURIMatcher.match(uri)) {
		case SHOPPING:
			num = db.delete(tableName, selection, selectionArgs);
			break;
		case SHOPPING_ID:
			long rowid = ContentUris.parseId(uri);
			String where = BaseColumns._ID + "=" + rowid;
			if (!TextUtils.isEmpty(selection)) {
				where += " and " + selection;
			}
			num = db.delete(tableName, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("This is an unknown Uri:" + uri);
		}
		// getContext().getContentResolver().notifyChange(uri, null);
		return num;
	}

	public static Uri getContentUri(Context c) {
		init(c);
		return sContentUri;
	}

	public static String subTableName(Uri uri) {
		String path = uri.getPath();
		int first = path.indexOf("/");
		int end = path.lastIndexOf("/");
		String tableName = path.substring(first + 1, end == first ? path.length() : end);
		return tableName;
	}

	public static String[] getTables() {
		return TABLENAMES;
	}
}
