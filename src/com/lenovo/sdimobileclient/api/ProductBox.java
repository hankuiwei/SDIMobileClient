package com.lenovo.sdimobileclient.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductBox extends AbsApiData {

	public String ChangeRecID;
	public String MaterialNo;
	public String SPDesc;
	public String SPSN;
	public String SPTypeDesc;
	public String YakuanPrice;

	public ProductBox() {
	}

	public ProductBox(String json) {
		try {
			parser(new JSONObject(json));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
