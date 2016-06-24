
package com.lenovo.sdimobileclient.utility;

import android.os.Build;
/**
 * 移植javaBase64
 * @author zhangshaofang
 *
 */
public class Base64 {
	/**
	 * 转码
	 * @param encData
	 * @return
	 */
	public static byte[] encode(byte[] encData) {
		if (Build.VERSION.SDK_INT >= 8) {
			byte[] temp = JavaCalls.callStaticMethod("android.util.Base64", "encode", encData, 0);
			//byte[] temp = android.util.Base64.encode(encData, android.util.Base64.DEFAULT);
			byte[] result = temp;
			if (temp[temp.length - 1] == '\n') {
				result = new byte[temp.length - 1];
				System.arraycopy(temp, 0, result, 0, temp.length - 1);
			}
			return result;
		} else {
			return JavaCalls.callStaticMethod("org.bouncycastle.util.encoders.Base64", "encode",
					encData);
			//return org.bouncycastle.util.encoders.Base64.encode(encData);
		}
	}
	/**
	 * 解码
	 * @param decData
	 * @return
	 */
	public static byte[] decode(String decData) {
		if (Build.VERSION.SDK_INT >= 8) {
			return JavaCalls.callStaticMethod("android.util.Base64", "decode", decData, 0);
			//return android.util.Base64.decode(decData, android.util.Base64.DEFAULT);
		} else {
			return JavaCalls.callStaticMethod("org.bouncycastle.util.encoders.Base64", "decode",
					decData);
			//return org.bouncycastle.util.encoders.Base64.decode(decData);
		}
	}

}
