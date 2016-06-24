package com.lenovo.sdimobileclient.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ListView;

import com.lenovo.sdimobileclient.data.Account;
import com.lenovo.sdimobileclient.data.AlarmAlert;
import com.lenovo.sdimobileclient.data.Attach;

/**
 * 数据库管理
 * 
 * @author zhangshaofang
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "lenovo_services.db", null, 4);
	}

	/**
	 * 数据库初始化，建表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Account.createTable());
		db.execSQL(Attach.createTable());
		db.execSQL(AlarmAlert.createTable());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			
			
			db.execSQL("ALTER TABLE attach ADD categorydesc STRING ;");
		}
	}
}
