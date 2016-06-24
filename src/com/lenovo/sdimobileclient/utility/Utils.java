package com.lenovo.sdimobileclient.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.data.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * 工具类，常用方法
 * 
 * @author zhangshaofang
 * 
 */
public class Utils implements Constants {

	public static final String DATE_TIME_FORMAT = "MM-dd HH:mm";
	public static final String FILE_NAME_TIME_FORMAT = "yyyyMMddHHmm";
	public static final String FILE_NAME_TIMES_FORMAT = "yyyyMMddHHmmss";
	public static final String DATE_TIME_FORMAT_TODAY = "今天 HH:mm";
	public static final String DATE_TIME_FORMAT_YESTERDAY = "昨天 HH:mm";
	public static final String DATE_TIME_FORMAT_TDBY = "前天 HH:mm";

	public static boolean isMobileNO(String mobiles) {

		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");

		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static String getPrintSize(long size) { // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size < 1024) {
			return String.valueOf(size) + "B";
		} else {
			size = size / 1024;
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// //因为还没有到达要使用另一个单位的时候
		// //接下去以此类推
		if (size < 1024) {
			return String.valueOf(size) + "KB";
		} else {
			size = size / 1024;
		}
		if (size < 1024) {
			// //因为如果以MB为单位的话，要保留最后1位小数，
			// //因此，把此数乘以100之后再取余
			size = size * 100;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
		} else {
			// //否则如果要以GB为单位的，先除于1024再作同样的处理
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
		}
	}

	/**
	 * 时间格式化
	 * 
	 * @param times
	 * @return
	 */
	public static String dateFormat(long times) {
		long current = System.currentTimeMillis();
		Date date = new Date(times);
		Date curr = new Date(current);
		String dateformat = DATE_TIME_FORMAT;
		int y = curr.getYear() - date.getYear();
		int m = curr.getMonth() - date.getMonth();
		int t = curr.getDate() - date.getDate();
		if (y == 0 && m == 0) {
			if (t == 0) {
				dateformat = DATE_TIME_FORMAT_TODAY;
			} else if (t == 1) {
				dateformat = DATE_TIME_FORMAT_YESTERDAY;
			} else if (t == 2) {
				dateformat = DATE_TIME_FORMAT_TDBY;
			}
		}
		SimpleDateFormat dateformat1 = new SimpleDateFormat(dateformat);
		String dateStr = dateformat1.format(date);
		return dateStr;
	}

	public static final String FORMAT_DATE = "yyyy-MM-dd";
	public static final String FORMAT_TIME = "HH:mm";
	public static final String FORMAT_TODAY = "今天";
	public static final String FORMAT_YESTERDAY = "昨天 ";
	public static final String FORMAT_AFTER = "明天";

	/**
	 * 时间格式化 获取日期
	 * 
	 * @param times
	 * @return
	 */
	public static String getDate(long times) {
		long current = System.currentTimeMillis();
		Date date = new Date(times);
		Date curr = new Date(current);
		String dateformat = FORMAT_DATE;
		int y = curr.getYear() - date.getYear();
		int m = curr.getMonth() - date.getMonth();
		int t = curr.getDate() - date.getDate();
		if (y == 0 && m == 0) {
			if (t == 0) {
				dateformat = FORMAT_TODAY;
			} else if (t == 1) {
				dateformat = FORMAT_YESTERDAY;
			} else if (t == -1) {
				dateformat = FORMAT_AFTER;
			}
		}
		SimpleDateFormat dateformat1 = new SimpleDateFormat(dateformat);
		String dateStr = dateformat1.format(date);
		return dateStr;
	}

	/**
	 * 时间格式化
	 * 
	 * @param times
	 * @return
	 */
	public static String getTime(long times) {
		Date date = new Date(times);
		String dateformat = FORMAT_TIME;
		SimpleDateFormat dateformat1 = new SimpleDateFormat(dateformat);
		String dateStr = dateformat1.format(date);
		return dateStr;
	}

	public static int getTipSpit(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int tipSpit = preferences.getInt(PREF_TIP_SPIT, 15);
		return tipSpit;
	}

	public static void saveTipSpit(Context context, int tipSpit) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putInt(PREF_TIP_SPIT, tipSpit);
		editor.commit();
	}

	public static void saveTipFunction(Context context, int bell, int shock) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putInt(PREF_TIP_BELL, bell);
		editor.putInt(PREF_TIP_SHOCK, shock);
		editor.commit();
	}

	/**
	 * 检查时间是否是今天
	 * 
	 * @param times
	 * @return
	 */
	public static boolean isToday(long times) {
		boolean isToday = false;
		long current = System.currentTimeMillis();
		Date date = new Date(times);
		Date curr = new Date(current);
		int y = curr.getYear() - date.getYear();
		int m = curr.getMonth() - date.getMonth();
		int t = curr.getDate() - date.getDate();
		if (y == 0 && m == 0) {
			if (t == 0) {
				isToday = true;
			}
		}
		return isToday;
	}

	/**
	 * 保存key和服务器时间
	 */
	public static void saveSystemKeyAndTime(Context context, String key, String times) {
		long server_time = dateFormatTimes(times);
		long client_time = System.currentTimeMillis();
		long spit = server_time - client_time;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putString(PREF_SYSTEM_KEY, key);
		editor.putLong(PREF_SYSTEM_TIME, spit);
		editor.putLong(PREF_CLIENT_TIME, client_time);
		editor.commit();
	}

	public static void saveEngineerInfo(Context context, EnginnerInfo enginnerInfo) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putString(PREF_ENGINEERINFO, enginnerInfo.getJson().toString());
		editor.commit();
	}

	public static void clearEngineerInfo(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putString(PREF_ENGINEERINFO, "");
		editor.commit();
	}

	public static EnginnerInfo getEnginnerInfo(Context context) {
		EnginnerInfo enginnerInfo = null;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String jsonString = preferences.getString(PREF_ENGINEERINFO, "");
		if (!TextUtils.isEmpty(jsonString)) {
			try {
				JSONObject jsonObject = new JSONObject(jsonString);
				enginnerInfo = new EnginnerInfo(jsonObject);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return enginnerInfo;
	}

	/**
	 * 把登录状态保存下来
	 * 
	 * @param context
	 * @param key
	 * @param isLogin
	 */
	public static void saveLoginInfo(Context context, String key, boolean isLogin) {
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

		Editor edit = defaultSharedPreferences.edit();

		edit.putBoolean(key, isLogin);

		edit.commit();

	}

	/**
	 * 获取当前的登录状态
	 * 
	 * @param context
	 * @param key
	 * @return
	 */

	public static boolean getLoginInfo(Context context, String key) {

		boolean isLogin = false;

		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		isLogin = defaultSharedPreferences.getBoolean(key, false);

		return isLogin;

	}

	/**
	 * 获取系统配置 key 和 系统时间差
	 * 
	 * @param context
	 * @return
	 */
	public static Config getSystemConfig(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String key = preferences.getString(PREF_SYSTEM_KEY, "");
		long spit = preferences.getLong(PREF_SYSTEM_TIME, 0);
		long client_time = preferences.getLong(PREF_CLIENT_TIME, 0);
		boolean isToday = isToday(client_time);
		Config config = null;
		if (!TextUtils.isEmpty(key) && isToday)
			config = new Config(key, spit);
		return config;
	}

	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";

	/**
	 * 时间格式化
	 * 
	 * @param times
	 * @return
	 */
	public static long dateFormatTimes(String times) {
		DateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
		Date date = null;
		try {
			date = sdf.parse(times);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	private static final String DATE_TIME = "yyyyMMddHHmmss";

	/**
	 * 时间格式化
	 * 
	 * @param times
	 * @return
	 */
	public static String dateFormatForKey(long times) {
		Date date = new Date(times);
		SimpleDateFormat dateformat1 = new SimpleDateFormat(DATE_TIME);
		String dateStr = dateformat1.format(date);
		return dateStr;
	}

	public static String dateFormatPre(long times) {
		Date date = new Date(times);
		SimpleDateFormat dateformat1 = new SimpleDateFormat(TIME_FORMAT);
		String dateStr = dateformat1.format(date);
		return dateStr;
	}

	public static String datetimeFormatPre(long times) {
		Date date = new Date(times);
		SimpleDateFormat dateformat1 = new SimpleDateFormat(FILE_NAME_TIME_FORMAT);
		String dateStr = dateformat1.format(date);
		return dateStr;
	}

	public static String datetimesFormatPre(long times) {
		Date date = new Date(times);
		SimpleDateFormat dateformat1 = new SimpleDateFormat(FILE_NAME_TIMES_FORMAT);
		String dateStr = dateformat1.format(date);
		return dateStr;
	}

	/**
	 * 压缩图片
	 * 
	 * @param context
	 * @param is
	 * @param uri
	 * @return
	 */
	public static Bitmap getBitpMap(Context context, InputStream is, Uri uri) {
		ParcelFileDescriptor pfd;
		try {
			pfd = context.getContentResolver().openFileDescriptor(uri, "r");
		} catch (IOException ex) {
			return null;
		}
		java.io.FileDescriptor fd = pfd.getFileDescriptor();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 10;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fd, null, options);
		options.inSampleSize = computeSampleSize(options, 800);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		Bitmap sourceBitmap = BitmapFactory.decodeStream(is, null, options);
		return sourceBitmap;
	}

	static int computeSampleSize(BitmapFactory.Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}
		// if (VERBOSE)
		return candidate;
	}

	/**
	 * 把集合 Map 存到sp中
	 * 
	 * @param context
	 * @param key
	 * @param datas
	 */
	public static void saveInfo(Context context, String key, Map<String, String> datas) {
		JSONArray mJsonArray = new JSONArray();
		Map<String, String> itemMap = datas;
		Iterator<Entry<String, String>> iterator = itemMap.entrySet().iterator();

		JSONObject object = new JSONObject();

		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			try {
				object.put(entry.getKey(), entry.getValue());
			} catch (JSONException e) {

			}
		}
		mJsonArray.put(object);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString(key, mJsonArray.toString());
		editor.commit();
	}

	/**
	 * 从sp 中 取出集合
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static Map<String, String> getInfo(Context context, String key) {
		Map<String, String> itemMap = null;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String result = sp.getString(key, "");
		try {
			JSONArray array = new JSONArray(result);
			for (int i = 0; i < array.length(); i++) {
				JSONObject itemObject = array.getJSONObject(i);
				itemMap = new HashMap<String, String>();
				JSONArray names = itemObject.names();
				if (names != null) {
					for (int j = 0; j < names.length(); j++) {
						String name = names.getString(j);
						String value = itemObject.getString(name);
						itemMap.put(name, value);
					}
				}
			}
		} catch (JSONException e) {

		}

		return itemMap;
	}

	/**
	 * 获取加密信息
	 */
	public static String getToken(Context context) {
		Config config = getSystemConfig(context);
		long clientTime = System.currentTimeMillis();
		String serverTime = dateFormatForKey(clientTime + config.spit);
		String key = DES.decyrpt(DES.INIT_KEY, config.Key);
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String device_id = null;
		if (tm != null) {
			device_id = tm.getDeviceId();
		}
		if (TextUtils.isEmpty(device_id)) {
			device_id = UNKNOW_DEVICE_ID;
		}
		return DES.encyrpt(key, serverTime + "," + device_id);
	}

	private static final int FILE_SIZE = 1024 * 1024 * 10;
	private static Toast toast;

	/**
	 * 将bitmap转换成文件
	 * 
	 * @param context
	 * @param bitmap
	 * @return
	 */
	public static File generatorFileFromBitmap(Context context, Bitmap bitmap) {
		System.gc();
		File file = new File(context.getCacheDir(), System.currentTimeMillis() + ".png");
		try {
			file.createNewFile();
			FileOutputStream outStream = new FileOutputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			byte[] mPicdat = bos.toByteArray();
			outStream.write(mPicdat);
			outStream.close();
			if (file.length() > FILE_SIZE) {
				bitmap = Bitmap.createScaledBitmap(bitmap, 640, 960, false);
				BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
				createFileFromDrawable(context, bd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 将drawable转换成文件
	 * 
	 * @param context
	 * @param draw
	 * @return
	 */
	public static File createFileFromDrawable(Context context, Drawable draw) {
		if (draw == null) {
			return null;
		}
		Bitmap bitmap = getBitmapFromDrawable(draw);
		File file = new File(context.getCacheDir(), System.currentTimeMillis() + ".png");
		try {
			file.createNewFile();
			FileOutputStream outStream = new FileOutputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			byte[] mPicdat = bos.toByteArray();
			outStream.write(mPicdat);
			outStream.close();
			if (file.length() > FILE_SIZE) {
				bitmap = Bitmap.createScaledBitmap(bitmap, 640, 960, false);
				BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
				file = createFileFromDrawable(context, bd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
		}
		return file;
	}

	/**
	 * showToast
	 * 
	 * @param context
	 * @param s
	 */
	public static void showToast(Context context, CharSequence s) {

		if (toast == null) {
			toast = Toast.makeText(context, s, 0);
		}
		toast.setText(s);
		toast.show();
	}

	public static void resizePicker(FrameLayout tp) {
		List<NumberPicker> npList = findNumberPicker(tp);
		for (NumberPicker np : npList) {
			resizeNumberPicker(np);
		}
	}

	private static List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;
		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}
		return npList;
	}

	private static void resizeNumberPicker(NumberPicker np) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, RadioGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 0, 10, 0);
		np.setLayoutParams(params);
	}

	/**
	 * drawable 转换成bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	public static boolean isNetwork(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {

			return true;
		} else if (gprs == State.CONNECTED || gprs == State.CONNECTING) {

			return true;
		}
		return false;
	}

	/**
	 * 网络已经连接，然后去判断是wifi连接还是GPRS连接
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {

			return true;
		}
		return false;
	}

}
