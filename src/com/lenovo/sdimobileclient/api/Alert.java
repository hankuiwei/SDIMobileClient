package com.lenovo.sdimobileclient.api;

public class Alert {

	/**
	 * 提醒类型
	 */
	public int type;
	/**
	 * 响铃
	 */
	public int bell;
	/**
	 * 震动
	 */
	public int shock;
	/**
	 * 提醒时间
	 */
	public long timestamp;
	/**
	 * 内容
	 */
	public String msg;
	/**
	 * 工单Id
	 */
	public String orderId;
}
