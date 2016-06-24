package com.lenovo.sdimobileclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.lenovo.sdimobileclient.Constants;

/**
 * 附件上传调用封装
 * 
 * @author zhangshaofang
 * 
 */
public class ActionHelper implements Constants {
	private static final String MIME_TYPE_IMAGE = "image/*";

	public static void openActivityForPickImage(Activity activity, Uri uri, int width, int height, boolean getBitmap, boolean doFaceDetection, boolean fixSize,
			int requestCode) {
		Intent intent = new Intent(activity, PicturePickerActivity.class);
		intent.setType(MIME_TYPE_IMAGE);

		Bundle extras = new Bundle();
		if (uri != null) {
			extras.putParcelable(MediaStore.EXTRA_OUTPUT, uri);
		}
		extras.putInt("outputX", 100);
		extras.putInt("outputY", 100);
		extras.putBoolean("return-data", getBitmap);
		extras.putBoolean("noFaceDetection", !doFaceDetection);
		extras.putBoolean("fix-size", fixSize);

		intent.putExtras(extras);
		requestCode = requestCode == 0 ? REQUEST_CODE_GET_IMAGE : requestCode;
		activity.startActivityForResult(intent, requestCode);
	}
}
