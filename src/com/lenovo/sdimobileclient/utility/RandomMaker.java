
package com.lenovo.sdimobileclient.utility;


import java.util.Random;

public class RandomMaker {
	private static Random sRandom;
	private static byte[] sTemp;

	private static Random getRandomSeed() {
		if (sRandom == null) {
			sRandom = new Random();
			sTemp = new byte[6];
		}
		return sRandom;
	}

	public static String getString() {
		Random r = getRandomSeed();
		r.nextBytes(sTemp);
		
		return new String(Base64.encode(sTemp));
	}
}
