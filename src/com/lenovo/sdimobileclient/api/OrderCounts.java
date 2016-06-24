package com.lenovo.sdimobileclient.api;
/**
 * 工单数量
 */
public class OrderCounts extends AbsApiData {
	
	/**
	 * 待联系
	 */
	public int Uncontact;	
	/**
	 * 待上门
	 */
	public int Undoor;	
	/**
	 * 上门中
	 */
	public int Dooring;	
	/**
	 * 待完成
	 */
	public int ToBeFinished;	
	/**
	 * 服务完
	 */
	public int Finish;
}
