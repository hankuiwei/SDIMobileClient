package com.lenovo.sdimobileclient.api;

/**
 * 故障现象
 * 
 * @author zhangshaofang
 * 
 */
public class BreakDownInfo extends AbsApiData {

	public String Code;
	public String Value;

	public BreakDownInfo() {
	}

	public BreakDownInfo(String code, String value) {
		Code = code;
		Value = value;
	}

}
