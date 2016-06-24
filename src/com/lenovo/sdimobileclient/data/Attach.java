package com.lenovo.sdimobileclient.data;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 附件实体
 * 
 * @author zhangshaofang
 * 
 */
public class Attach extends AbsData implements Parcelable {
	public static final String TABLE_NAME = "attach";
	private static final String NAME = "name";
	private static final String ORDER_ID = "order_id";
	private static final String ENGINEER_ID = "engineer_id";
	private static final String FILEPATH = "filepath";
	private static final String CATEGORY = "category";
	private static final String DESCRIPTION = "description";
	private static final String FILETYPE = "filetype";
	private static final String SUCCESS = "success";
	private static final String TYPE = "type";
	public static final int FILETYPE_VIDEO = 1;
	public static final int FILETYPE_INVOICE = 4;
	public static final int FILETYPE_ATTACH = 2;
	public static final int FILETYPE_SIGNATURE = 3;
	public static final String ATTCH_CUSTO = "3";
	public static final String ATTCH_NORMAL = "2";
	public static final String ATTCH_INVOICE = "1";
	public String name;
	public String orderId;
	public String engineerId;
	public String filepath;
	public String category;
	public String description;
	public int fileType;
	public int success;
	public String type;
	public int itype;
	public String FileID;
	public String categorydesc;

	public Attach() {
	}

	/**
	 * 
	 * @param name
	 *            附件名
	 * @param orderId
	 *            工单id
	 * @param engineerId
	 *            工程师id
	 * @param filepath
	 *            文件路径
	 * @param category
	 *            附件基本类型
	 * @param type
	 *            附件具体类型(这个是客户选的)
	 * @param description
	 *            附件描述
	 */
	public Attach(String name, String orderId, String engineerId, String filepath, String category, String type, String description, String categorydesc) {
		this.name = name;
		this.orderId = orderId;
		this.engineerId = engineerId;
		this.filepath = filepath;
		this.category = category;
		this.description = description;
		this.type = type;
		this.categorydesc = categorydesc;
	}

	/**
	 * 
	 * @param name
	 *            文件名
	 * @param orderId
	 *            工单id
	 * @param fileId
	 * 
	 *            文件编号
	 * @param success
	 * 
	 * 
	 * 
	 * @param type
	 *            附件类型
	 */
	public Attach(String name, String orderId, String type, int success) {
		this.name = name;
		this.orderId = orderId;
		this.success = success;
		itype = 1;
		this.type = type;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public static String createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(TABLE_NAME).append("(").append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,").append(NAME).append(" STRING, ")
				.append(ORDER_ID).append(" STRING, ").append(ENGINEER_ID).append(" STRING, ").append(FILETYPE).append(" INTEGER, ").append(FILEPATH)
				.append(" STRING, ").append(CATEGORY).append(" STRING, ").append(DESCRIPTION).append(" STRING, ").append(SUCCESS).append(" INTEGER, ")
				.append(TIMESTAMP).append(" LONG, ").append(TYPE).append(" STRING, ").append("categorydesc").append(" STRING ").append(");");
		return sb.toString();
	}

	public static ArrayList<Attach> queryAttachSuccess(Context context, int success) {
		ArrayList<Attach> accounts = new ArrayList<Attach>();
		Uri uri = getContentUri(Attach.class, context);
		ContentResolver resolver = context.getContentResolver();
		String sortOrder = TIMESTAMP + " desc";
		String selection = SUCCESS + "=" + success;
		if (success == 0) {
			selection = SUCCESS + "=" + success + " or " + SUCCESS + "=" + 2;
		}
		/**
		 * aa
		 */
		Cursor cursor = resolver.query(uri, null, selection, null, sortOrder);
		while (cursor.moveToNext()) {
			Attach attach = new Attach();
			setValues(cursor, attach);
			accounts.add(attach);
		}
		cursor.close();
		return accounts;
	}

	public static Attach queryByID(Context context, int id) {
		Uri uri = ContentUris.withAppendedId(getContentUri(Attach.class, context), id);
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		Attach attach = null;
		if (cursor.moveToFirst()) {
			attach = new Attach();
			setValues(cursor, attach);
			cursor.close();
		}
		return attach;
	}

	public ContentValues putValues() {
		ContentValues values = new ContentValues();
		values.put(NAME, name);
		values.put(ORDER_ID, orderId);
		values.put(ENGINEER_ID, engineerId);
		values.put(FILETYPE, fileType);
		values.put(FILEPATH, filepath);
		values.put(CATEGORY, category);
		values.put(DESCRIPTION, description);
		values.put(SUCCESS, success);
		values.put(TIMESTAMP, System.currentTimeMillis());
		values.put(TYPE, type);
		values.put("categorydesc", categorydesc);
		return values;
	}

	public static Attach setValues(Cursor cursor, Attach attach) {
		attach._id = cursor.getInt(cursor.getColumnIndex(_ID));
		attach.name = cursor.getString(cursor.getColumnIndex(NAME));
		attach.orderId = cursor.getString(cursor.getColumnIndex(ORDER_ID));
		attach.engineerId = cursor.getString(cursor.getColumnIndex(ENGINEER_ID));
		attach.fileType = cursor.getInt(cursor.getColumnIndex(FILETYPE));
		attach.filepath = cursor.getString(cursor.getColumnIndex(FILEPATH));
		attach.category = cursor.getString(cursor.getColumnIndex(CATEGORY));
		attach.description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
		attach.success = cursor.getInt(cursor.getColumnIndex(SUCCESS));
		attach.timestamp = cursor.getLong(cursor.getColumnIndex(TIMESTAMP));
		attach.type = cursor.getString(cursor.getColumnIndex(TYPE));
		attach.categorydesc = cursor.getString(cursor.getColumnIndex("categorydesc"));
		return attach;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(name);
		dest.writeString(orderId);
		dest.writeString(engineerId);
		dest.writeString(filepath);
		dest.writeString(category);
		dest.writeString(description);
		dest.writeInt(fileType);
		dest.writeInt(success);
		dest.writeString(type);
		dest.writeString(categorydesc);

	}

	public static final Parcelable.Creator<Attach> CREATOR = new Parcelable.Creator<Attach>() {
		public Attach createFromParcel(Parcel in) {
			return new Attach(in);
		}

		public Attach[] newArray(int size) {
			return new Attach[size];
		}
	};

	private Attach(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		_id = in.readInt();
		name = in.readString();
		orderId = in.readString();
		engineerId = in.readString();
		filepath = in.readString();
		category = in.readString();
		description = in.readString();
		fileType = in.readInt();
		success = in.readInt();
		type = in.readString();
		categorydesc = in.readString();
	}
}
