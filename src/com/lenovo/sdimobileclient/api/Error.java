/**
 * 
 */
package com.lenovo.sdimobileclient.api;

import com.foreveross.cache.network.FileInfo;

/**
 * 错误封装类
 * 
 * @ClassName: Error
 * @author ZhangShaofang
 * @Description: TODO
 */
public class Error {
	public static final String ERROR_NETWORK = "400";
	public static final String ERROR_UNKNOW = "404";
	/**
	 * 账号验证失败
	 */
	public static final String ERROR_ACCOUNT_FAILED = "003";
	/**
	 * 错误编码
	 */
	public String error_code;
	/**
	 * 错误信息
	 */
	public String error_msg;
	/**
	 * debug模式用到，封装，网络请求信息，及异常信息
	 */
	public FileInfo fileinfo;

	public Error() {
	}

	public Error(String error_code, String error_msg, FileInfo fileinfo) {
		this.error_code = error_code;
		this.error_msg = error_msg;
		this.fileinfo = fileinfo;
	}

	public Error(String error_code, String error_msg) {
		this.error_code = error_code;
		this.error_msg = error_msg;
	}

}
