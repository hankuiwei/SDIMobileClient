
package com.lenovo.sdimobileclient.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lenovo.sdimobileclient.LenovoServicesApplication;
import com.lenovo.sdimobileclient.service.LocationService;
import com.lenovo.sdimobileclient.service.NotificationService;
import com.lenovo.sdimobileclient.service.UploadAttachService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE;
	// 程序的Context对象
	private LenovoServicesApplication mContext;

	// 保证只有一个CrashHandler实例
	private CrashHandler() {

	}

	// 获取CrashHandler实例 ,单例模式
	public static CrashHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CrashHandler();
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = (LenovoServicesApplication) context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 *            异常信息
	 * @return true 如果处理了该异常信息;否则返回false.
	 */
	public boolean handleException(Throwable ex) {
		if (ex == null || mContext == null)
			return false;
		final String crashReport = getCrashReport(mContext, ex);
		new Thread() {
			public void run() {
				Looper.prepare();
				File file = save2File(crashReport);
				// sendAppCrashReport(mContext, crashReport, file);

				Intent intent = new Intent(mContext, LocationService.class);
				Intent intent1 = new Intent(mContext, UploadAttachService.class);
				Intent intent2 = new Intent(mContext, NotificationService.class);
				mContext.stopService(intent);
				mContext.stopService(intent2);
				mContext.stopService(intent1);
				List<Activity> activities = mContext.getActivities();
				for (Activity activity : activities) {
					activity.finish();
				}
				mContext.getActivities().clear();
				mContext.stopService();
				ActivityManager manager = (ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);
				manager.killBackgroundProcesses(mContext.getPackageName());
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
				Looper.loop();
			}

		}.start();
		return true;
	}

	private File save2File(String crashReport) {
		// 用于格式化日期,作为日志文件名的一部分
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = dateFormat.format(new Date());
		String fileName = "crash" + time + -+System.currentTimeMillis() + ".txt";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 存储路径，是sd卡的crash文件夹
				File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash");
				if (!dir.exists())
					dir.mkdir();
				File file = new File(dir, fileName);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(crashReport.toString().getBytes());
				fos.close();
				return file;
			} catch (Exception e) {
				// sd卡存储，记得加上权限，不然这里会抛出异常
				Log.i("Show", "save2File error:" + e.getMessage());
			}
		}
		return null;
	}

	private void sendAppCrashReport(final Context context, final String crashReport, final File file) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_info).setTitle("抱歉").setMessage("服务异常")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(1);
						dialog.dismiss();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(1);
					}
				});

		AlertDialog dialog = builder.create();
		// 需要的窗口句柄方式，没有这句会报错的
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}

	/**
	 * 获取APP崩溃异常报告
	 * 
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Context context, Throwable ex) {
		PackageInfo pinfo = getPackageInfo(context);
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "(" + pinfo.versionCode + " )");
		exceptionStr.append("Android:" + android.os.Build.VERSION.RELEASE + " (" + android.os.Build.MODEL + " )");
		exceptionStr.append("Exception:" + ex.getMessage() + "");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "");
		}
		return exceptionStr.toString();
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	private PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

}