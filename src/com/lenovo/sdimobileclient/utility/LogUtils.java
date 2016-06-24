package com.lenovo.sdimobileclient.utility;

import com.lenovo.sdimobileclient.Constants;
/**
 * log manager
 * @author zhangshaofang
 *
 */
public class LogUtils {

	public static void println(String log) {
		if (Constants.DEBUG)
			System.out.println(log);
	}
}
