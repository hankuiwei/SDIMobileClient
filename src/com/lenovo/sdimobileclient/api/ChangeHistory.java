package com.lenovo.sdimobileclient.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeHistory extends AbsApiData {

	public String ChangeRecID;
	public String DownMaterialNo;
	public String DownMaterialNoDesc;
	public String DownPartsSN;
	public String ESDMaterialNo;
	public String StatusDesc;
	public String SwapCategoryDesc;
	public String UpMaterialNo;
	public String UpMaterialNoDesc;
	public String UpPartsSN;
	public String CreateTime;
	public boolean mReplaceEdit;

	public ChangeHistory() {

	}

	public ChangeHistory(String json) {
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
}
