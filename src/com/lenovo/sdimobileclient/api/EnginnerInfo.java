package com.lenovo.sdimobileclient.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 工程师信息
 */
public class EnginnerInfo extends AbsApiData {

	/**
	 * 工程师id
	 */
	public String EngineerID;
	/**
	 * 姓名
	 */
	public String EngineerName;
	/**
	 * 工程师编号
	 */
	public String EngineerNumber;

	public EnginnerInfo() {
	}

	public EnginnerInfo(JSONObject jsonObject) {
		try {
			parser(jsonObject);
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
}
