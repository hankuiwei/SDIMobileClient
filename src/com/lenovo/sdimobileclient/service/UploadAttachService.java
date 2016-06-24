package com.lenovo.sdimobileclient.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import com.baidu.lbsapi.auth.i;
import com.foreveross.cache.CacheManager;
import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.FilePair;
import com.foreveross.cache.network.Netpath;
import com.foreveross.cache.network.ParamPair;
import com.lenovo.sdimobileclient.LenovoServicesApplication;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.data.Attach;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.network.UrlAttr;
import com.lenovo.sdimobileclient.ui.ActivityUtils;
import com.lenovo.sdimobileclient.ui.OrderActivity;
import com.lenovo.sdimobileclient.utility.FileUtil;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

public class UploadAttachService extends Service implements UrlAttr {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private List<Attach> mAttachs;
	private Attach mAttach;
	private OkHttpHelper mHttpHelper;

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		mHttpHelper = OkHttpHelper.getInstance(this);
		if (intent != null)
			mAttachs = intent.getParcelableArrayListExtra("attachs");
		if (!mAttachs.isEmpty()) {
			uploadAttach(mAttachs.remove(0));
		}
	}

	private void uploadAttach(Attach attach) {
		mAttach = attach;
		attach.success = 2;
		attach.update(getApplicationContext());
		File file = null;
		if (attach.fileType == Attach.FILETYPE_VIDEO) {
			ContentResolver cr = getContentResolver();
			file = FileUtil.getTempFile(this);
			FileUtil.copy(cr, Uri.parse(attach.filepath), file);
		} else {
			file = new File(attach.filepath);
		}
		// FilePair filePair = new FilePair(PARAM_IMAGEDATA, file);
		// List<NameValuePair> postValues = new ArrayList<NameValuePair>();

		HashMap<String, String> hashMap = new HashMap<String, String>();
		// postValues.add(new ParamPair(PARAM_ORDERID, attach.orderId)); // 工单编号
		// postValues.add(new ParamPair(PARAM_ENGINEER, attach.engineerId));//
		// 工程师
		hashMap.put(PARAM_ORDERID, attach.orderId);
		hashMap.put(PARAM_ENGINEER, attach.engineerId);

		if (TextUtils.equals(attach.type, Attach.ATTCH_NORMAL)) {
			// postValues.add(new ParamPair(PARAM_FILENAME, attach.name));//
			// 文件名称
			// postValues.add(new ParamPair(PARAM_TYPE, attach.category));//
			// 文件类型

			hashMap.put(PARAM_FILENAME, attach.name);
			hashMap.put(PARAM_TYPE, attach.category);

		} else if (TextUtils.equals(attach.type, Attach.ATTCH_INVOICE)) {
			// postValues.add(new ParamPair(PARAM_FILENAME, attach.name + "&" +
			// attach.description));// 文件描述
			// postValues.add(new ParamPair(PARAM_TYPE, attach.category));//
			// 选择的类型

			hashMap.put(PARAM_FILENAME, attach.name + "&" + attach.description);
			hashMap.put(PARAM_TYPE, attach.category);
		}
		// postValues.add(new ParamPair(PARAM_CATEGORY, attach.type));// 选择的类型
		hashMap.put(PARAM_CATEGORY, attach.type);
		Uri uri = Uri.parse(URL_UPLOADATTACH).buildUpon().build();
		// NetworkPath path = new Netpath(uri.toString(), postValues, filePair);
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_UPLOAD, params, mCallback);

		mHttpHelper.load(uri.toString(), mCallback, hashMap, PARAM_IMAGEDATA, file.getName(), file, this);
	}

	private static final int CALLBACK_UPLOAD = 1;
	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {
			boolean safe = mHttpHelper.isSuccessResult(result, UploadAttachService.this);
			if (!safe) {
				mAttach.success = 0;
				mAttach.update(getApplicationContext());
				Toast.makeText(getApplicationContext(), "附件\"" + mAttach.name + "\"上传失败", Toast.LENGTH_SHORT).show();
				if (!mAttachs.isEmpty()) {
					uploadAttach(mAttachs.remove(0));
				} else {
					onDestroy();
				}
				stopSelf();
				return;
			}
			mAttach.success = 1;
			mAttach.update(getApplicationContext());
			Toast.makeText(getApplicationContext(), "附件\"" + mAttach.name + "\"上传成功", Toast.LENGTH_SHORT).show();
			if (!mAttachs.isEmpty()) {
				uploadAttach(mAttachs.remove(0));
			} else {
				onDestroy();
			}

			stopSelf();
		}
	};

	public void onDestroy() {
		System.gc();
		super.onDestroy();
	};
}
