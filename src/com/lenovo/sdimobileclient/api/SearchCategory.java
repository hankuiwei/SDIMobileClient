package com.lenovo.sdimobileclient.api;

public class SearchCategory extends AbsApiData{

	public String sId;
	public String sName;
	public SearchCategory(String sId, String sName) {
		this.sId = sId;
		this.sName = sName;
	}
	
}
