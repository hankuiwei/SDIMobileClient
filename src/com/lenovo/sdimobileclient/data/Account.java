package com.lenovo.sdimobileclient.data;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
/**
 * 账号实体
 * @author zhangshaofang
 *
 */
public class Account extends AbsData {
	public static final String TABLE_NAME = "account";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String AUTO = "auto";
	public String username;
	public String password;
	public boolean auto;

	public Account() {
	}

	public Account(String username, String password,boolean auto) {
		this.username = username;
		this.password = password;
		this.auto = auto;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public static String createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(TABLE_NAME).append("(").append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,").append(USERNAME)
				.append(" STRING, ").append(PASSWORD).append(" STRING, ").append(AUTO).append(" INTEGER, ").append(TIMESTAMP).append(" LONG ").append(");");
		return sb.toString();
	}

	public static ArrayList<Account> queryAccount(Context context) {
		ArrayList<Account> accounts = new ArrayList<Account>();
		Uri uri = getContentUri(Account.class, context);
		ContentResolver resolver = context.getContentResolver();
		String sortOrder = TIMESTAMP + " desc";
		Cursor cursor = resolver.query(uri, null, null, null, sortOrder);
		while (cursor.moveToNext()) {
			Account account = new Account();
			setValues(cursor, account);
			accounts.add(account);
		}
		cursor.close();
		return accounts;
	}

	public static Account queryByID(Context context, int id) {
		Uri uri = ContentUris.withAppendedId(getContentUri(Account.class, context), id);
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		Account product = null;
		if (cursor.moveToFirst()) {
			product = new Account();
			setValues(cursor, product);
			cursor.close();
		}
		return product;
	}

	public ContentValues putValues() {
		ContentValues values = new ContentValues();
		values.put(USERNAME, username);
		values.put(PASSWORD, password);
		values.put(AUTO, auto ? 1 : 0);
		values.put(TIMESTAMP, System.currentTimeMillis());
		return values;
	}

	public static Account setValues(Cursor cursor, Account account) {
		account._id = cursor.getInt(cursor.getColumnIndex(_ID));
		account.username = cursor.getString(cursor.getColumnIndex(USERNAME));
		account.password = cursor.getString(cursor.getColumnIndex(PASSWORD));
		account.auto = cursor.getInt(cursor.getColumnIndex(AUTO)) != 0;
		account.timestamp = cursor.getLong(cursor.getColumnIndex(TIMESTAMP));
		return account;
	}
}
