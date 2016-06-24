package com.lenovo.sdimobileclient.api;

/**
 * 故障信息
 */
public class FailureInfo extends AbsApiData{
	/**
	 * 故障大类
	 */
	public String FailureClass;
	/**
	 * 故障小类
	 */
	public String FailureSubClass;
	/**
	 * 故障现象
	 */
	public String FailureType;
	/**
	 * 故障描述
	 */
	public String FailureDescription;

}
