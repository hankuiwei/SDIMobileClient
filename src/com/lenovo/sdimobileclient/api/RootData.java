package com.lenovo.sdimobileclient.api;

import java.io.Serializable;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.network.FileInfo;

public class RootData extends AbsApiData implements ICacheInfo {

	private long expired = -1;
	private int status = -1;
	private int handle;
	public FileInfo fileInfo;
	private String Result;
	public String ResultMsg;
	public String Key;
	public String SysTime;
	public int BoxVisible;

	public RootData() {
	}

	public RootData(String  json) {
		try {
			parser(new JSONObject(json));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RootData(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	@Override
	public boolean isCorrectData() {
		return isValid();
	}

	public String getResult() {
		return Result;
	}

	public int getStatus() {
		return TextUtils.isEmpty(Result) ? 0 : Integer.parseInt(Result);
	}

	@Override
	public boolean isErrorData() {
		return TextUtils.isEmpty(Result) || !Result.equals("001");
	}

	@Override
	public long getExpiry() {
		return expired;
	}

	@Override
	public int getHandleMode() {
		return handle;
	}

	@Override
	public Object getData() {
		return this;
	}

	void addData(AbsApiData data) {
		HashMap<Class<? extends AbsApiData>, AbsApiData> datas = getDatas();
		if (data.isValid()) {
			datas.put(data.getClass(), data);
		}
	}

	@Override
	public boolean isCache() {
		return false;
	}

	@Override
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	@Override
	public boolean isSuccessData() {
		return !TextUtils.isEmpty(Result) && Result.equals("001");
	}
}
