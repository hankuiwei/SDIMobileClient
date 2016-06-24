package com.lenovo.sdimobileclient.ui.view;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * 在使用时需要注意，如果是需要在ProgressBar后面放背景图的，先要调用方法setBackground 然后再调用show方法
 * 
 * @author
 *
 */
@SuppressLint("HandlerLeak")
public abstract class ProgressDialogHandle {
	private Context mContext;
	private Dialog mDialog;
	private static final String THREAD_NAME = "dataprocess_thread";
	private static final int UPDATE = 1;
	protected static final int DISMISS = 2;
	protected static final int INVALIDEUSER = 3;
	private HttpURLConnection conn;
	private Bitmap backgound;

	public ProgressDialogHandle(Context context) {
		this.mContext = context;
	}

	public ProgressDialogHandle(Context context, HttpURLConnection conn) {
		this.mContext = context;
		this.conn = conn;
	}

	Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE:

				break;
			case DISMISS:
				try {
					/* 执行这里的时候，判断对话框是否已经存在，不然直接执行dismiss可能会出现异常 */
					if (mDialog != null && mDialog.isShowing()) {
						mDialog.dismiss();
					}
				} catch (Exception e) {

				} finally {
					updateUI();
				}
				break;
			default:
				break;
			}

		}
	};

	public void dismiss() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	public void setBackground(Bitmap backgound) {
		this.backgound = backgound;
	}

	public abstract void handleData() throws JSONException, IOException, Exception;

	public abstract String initialContent();

	public abstract void updateUI();
}
