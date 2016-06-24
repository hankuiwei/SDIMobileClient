
package com.lenovo.sdimobileclient.utility;

import org.apache.http.impl.auth.UnsupportedDigestAlgorithmException;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * DES加密类
 * 
 * @author zhangshaofang
 *
 */
public final class DES {
	public static final String INIT_KEY = "SDIMOBIL";

	private static final String LOG_TAG = "DES";
	private static final String ALGORITHM = "DES";
	private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5PADDING";
	private static final SecretKeyFactory KEY_FACTORY;
	private static final Cipher CIPHER;
	private static final int KEY_SIZE;
	private static final int IV_SIZE;
	private static final int BLOCK_SIZE;

	/**
	 * 初始化秘钥
	 */
	static {
		try {
			KEY_FACTORY = SecretKeyFactory.getInstance(ALGORITHM);
			CIPHER = Cipher.getInstance(CIPHER_ALGORITHM);
			KEY_SIZE = 8;
			IV_SIZE = KEY_SIZE;
			BLOCK_SIZE = CIPHER.getBlockSize();
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOG_TAG, "Get DES Digest failed.");
			throw new UnsupportedDigestAlgorithmException(CIPHER_ALGORITHM, e);
		} catch (NoSuchPaddingException e) {
			Log.e(LOG_TAG, "Get DES Digest failed.");
			throw new IllegalArgumentException(CIPHER_ALGORITHM, e);
		}
	}

	private DES() {
	}

	private static byte[] adjust(byte[] input, int size, byte fill) {
		byte[] output = new byte[size];

		System.arraycopy(input, 0, output, 0, Math.min(input.length, size));
		for (int i = input.length; i < size; i++) {
			output[i] = fill;
		}
		return output;
	}

	/**
	 * 获取秘钥
	 * 
	 * @param keyString
	 * @return
	 */
	private static Key generateKey(String keyString) {
		Key result = null;
		byte[] key = adjust(keyString.getBytes(), KEY_SIZE, (byte) 0);

		try {
			DESKeySpec keySpec = new DESKeySpec(key);
			result = KEY_FACTORY.generateSecret(keySpec);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Get key failed. Use NULL.");
		}

		return result;
	}

	private static byte[] cyrpt(int mode, Key key, byte[] data, IvParameterSpec iv) {
		byte[] result;
		try {
			CIPHER.init(mode, key, iv);
			result = CIPHER.doFinal(data);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Encoding failed, use source data to instead.");
			result = data;
		}
		return result;
	}

	/**
	 * 加密
	 * 
	 * @param keyString
	 *            加密密码
	 * @param source
	 *            待加密数据
	 * @return 加密数据
	 */
	public static String encyrpt(String keyString, String source) {
		if (source == null) {
			return null;
		}
		Key key = generateKey(keyString);
		IvParameterSpec iv = new IvParameterSpec(adjust(keyString.getBytes(), IV_SIZE, (byte) 0));

		byte[] data = source.getBytes();
		byte[] encData = cyrpt(Cipher.ENCRYPT_MODE, key, data, iv);
		encData = Base64.encode(encData);

		return new String(encData);
	}

	/**
	 * 解密
	 * 
	 * @param keyString
	 *            解密密码
	 * @param source
	 *            待解密数据
	 * @return 元数据
	 */
	public static String decyrpt(String keyString, String source) {
		if (source == null) {
			return null;
		}
		Key key = generateKey(keyString);
		IvParameterSpec iv = new IvParameterSpec(adjust(keyString.getBytes(), IV_SIZE, (byte) 0));

		byte[] data = Base64.decode(source);
		byte[] encData = cyrpt(Cipher.DECRYPT_MODE, key, data, iv);

		String string = "";

		try {
			string = new String(encData, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return string;

	}

	private static boolean cyrpt(int mode, String keyString, InputStream in, OutputStream out) {
		Key key = generateKey(keyString);
		IvParameterSpec iv = new IvParameterSpec(adjust(keyString.getBytes(), IV_SIZE, (byte) 0));
		boolean result = true;

		try {
			CIPHER.init(mode, key, iv);

			byte[] buffer = new byte[BLOCK_SIZE];
			byte[] outBuffer = null;
			while (in.read(buffer) >= 0) {
				outBuffer = CIPHER.update(buffer);
				out.write(outBuffer);
			}
			outBuffer = CIPHER.doFinal();
			if (outBuffer.length > 0) {
				out.write(outBuffer);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Encoding failed, use source data to instead.");
			result = false;
		}

		return result;
	}

	public static boolean encyrpt(String keyString, InputStream in, OutputStream out) {
		return cyrpt(Cipher.ENCRYPT_MODE, keyString, in, out);
	}

	public static boolean decyrpt(String keyString, InputStream in, OutputStream out) {
		return cyrpt(Cipher.DECRYPT_MODE, keyString, in, out);

	}
}
