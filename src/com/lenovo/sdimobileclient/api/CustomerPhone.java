package com.lenovo.sdimobileclient.api;

import java.io.Serializable;

/**
 * 客户联系方式
 * 
 * @author zhangshaofang
 *
 */
public class CustomerPhone extends AbsApiData implements Serializable {

	/**
	 * 电话号码
	 */
	public String Phone;
	/**
	 * 号码类型（手机或座机）
	 */
	public String Type;
}
