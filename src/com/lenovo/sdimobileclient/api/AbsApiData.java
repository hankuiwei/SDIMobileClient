package com.lenovo.sdimobileclient.api;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * api 数据结构基类，封装了运用java反射和映射技术解析json基于api类赋值
 * 
 * @author zhangshaofang
 *
 */
public abstract class AbsApiData {
	/**
	 * 存放 JsonObject api
	 */
	private HashMap<Class<? extends AbsApiData>, AbsApiData> mDatas;
	/**
	 * 存放json数组api
	 */
	private HashMap<Class<? extends AbsApiData>, ArrayList<? extends AbsApiData>> mArrays;
	private JSONObject mJson;

	protected AbsApiData() {

	}

	/**
	 * json 解析
	 * 
	 * @param json
	 * @throws JSONException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	protected void parser(JSONObject json) throws JSONException, IllegalArgumentException, IllegalAccessException {
		mJson = json;
		Iterator<?> keyIterator = json.keys();
		ArrayList<String> keyList = new ArrayList<String>();
		while (keyIterator.hasNext()) {
			String key = (String) keyIterator.next();
			keyList.add(key);
		}

		Field[] fields = getClass().getDeclaredFields();
		HashMap<String, Field> fieldMap = new HashMap<String, Field>();
		HashSet<String> fieldNames = new HashSet<String>();
		for (Field field : fields) {
			String name = field.getName();
			name = name.substring(name.lastIndexOf(".") + 1);
			fieldNames.add(name);
			fieldMap.put(name, field);

		}

		for (String key : keyList) {
			Object object = json.get(key);
			if (object instanceof JSONObject) {
				AbsApiData data = JsonDataFactory.getData(key, (JSONObject) object);
				if (data != null) {
					getDatas().put(data.getClass(), data);
				} else {
					Log.w(JsonDataFactory.LOG_TAG, "A JSONObject not be parser:\n" + object);
				}
			} else if (object instanceof JSONArray) {
				ArrayList<AbsApiData> array = JsonDataFactory.getDataArray(key, (JSONArray) object);
				if (array != null) {
					getArrayDatas().put(JsonDataFactory.getArrayClass(key), array);
				} else {
					Log.w(JsonDataFactory.LOG_TAG, "A JSONArray not be parser:\nKey:" + key + ", JSON:" + object);
				}
			} else {
				Field field = fieldMap.get(key);
				if (field != null) {
					if ((field.getType() == boolean.class || field.getType() == Boolean.class) && !(object instanceof Boolean)) {
						object = json.getInt(key) != 0;
					} else if ((field.getType() == long.class || field.getType() == Long.class) && !(object instanceof Long)) {
						object = json.getLong(key);
					}
					if (object == JSONObject.NULL) {
						object = null;
					}
					field.setAccessible(true);
					field.set(this, object);
				}
			}
		}
	}

	/**
	 * 数据校验
	 * 
	 * @return
	 */
	public boolean isValid() {
		return true;
	}

	/**
	 * 校验url
	 * 
	 * @param url
	 * @return
	 */
	protected boolean isUrl(String url) {
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			// TODO should return false;
			return true;
		}
	}

	public HashMap<Class<? extends AbsApiData>, AbsApiData> getDatas() {
		if (mDatas == null) {
			mDatas = new HashMap<Class<? extends AbsApiData>, AbsApiData>();
		}
		return mDatas;
	}

	protected HashMap<Class<? extends AbsApiData>, ArrayList<? extends AbsApiData>> getArrayDatas() {
		if (mArrays == null) {
			mArrays = new HashMap<Class<? extends AbsApiData>, ArrayList<? extends AbsApiData>>();
		}
		return mArrays;
	}

	private void typeWarning(Object value, Class<? extends AbsApiData> c, ClassCastException e) {
		StringBuilder sb = new StringBuilder();
		sb.append("Try get a ");
		sb.append(c.getName());
		sb.append(", but value was a ");
		sb.append(value.getClass().getName());
		sb.append(".");
		Log.w(JsonDataFactory.LOG_TAG, sb.toString());
		Log.w(JsonDataFactory.LOG_TAG, "Attempt to cast generated internal exception:", e);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbsApiData> T getData(Class<T> c) {
		HashMap<Class<? extends AbsApiData>, AbsApiData> datas = getDatas();
		AbsApiData data = datas.get(c);
		if (data == null) {
			return null;
		}
		try {
			return (T) data;
		} catch (ClassCastException e) {
			typeWarning(data, c, e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends AbsApiData> ArrayList<T> getArrayData(Class<T> c) {
		HashMap<Class<? extends AbsApiData>, ArrayList<? extends AbsApiData>> arrays = getArrayDatas();
		ArrayList<? extends AbsApiData> list = arrays.get(c);
		if (list == null) {
			return null;
		}
		try {
			return (ArrayList<T>) list;
		} catch (ClassCastException e) {
			typeWarning(list, c, e);
			return null;
		}
	}

	public JSONObject getJson() {
		return mJson;
	}
}
