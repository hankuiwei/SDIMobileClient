package com.lenovo.sdimobileclient.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ImageActivity extends RootActivity implements OnClickListener {
	private ImageView zoomView;
	private Context ctx;
	private GestureDetector gestureDetector;
	public static final String EXTRA_IMAGE = Constants.PACKAGE_NAME + ".image";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_picture_preview);
		getIntent().getBooleanExtra("showtitle", true);
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_dele).setOnClickListener(this);
		findViewById(R.id.image_title).setVisibility(getIntent().getBooleanExtra("showtitle", true) ? View.VISIBLE : View.GONE);

		ctx = this;
		zoomView = (ImageView) findViewById(R.id.zoom_view);

		String filepath = getIntent().getStringExtra(EXTRA_IMAGE);

		/* 大图的下载地址 */
		// final String url = getIntent().getStringExtra("url");

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		final int widthPixels = metrics.widthPixels;
		final int heightPixels = metrics.heightPixels;
		File bigPicFile = new File(filepath);
		// if (bigPicFile.exists()) {/* 如果已经下载过了,直接从本地文件中读取 */
		zoomView.setImageBitmap(zoomBitmap(BitmapFactory.decodeFile(filepath), widthPixels, heightPixels));
		// }
		gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				float x = e2.getX() - e1.getX();
				if (x > 0) {
					prePicture();
				} else if (x < 0) {

					nextPicture();
				}
				return true;
			}
		});
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Activity parent = getParent();
			if (parent != null && parent instanceof TabGroup && ((TabGroup) parent).isCurentFirst()) {
				return parent.onKeyDown(keyCode, event);
			} else {
				
				setResult(RESULT_OK);
				finish();
				return true;
			}
		} else
			return super.onKeyDown(keyCode, event);
	}

	protected void nextPicture() {
		// TODO Auto-generated method stub

	}

	protected void prePicture() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		super.onResume();
		// recycle();
	}

	public void recycle() {
		if (zoomView != null && zoomView.getDrawable() != null) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) zoomView.getDrawable();
			if (bitmapDrawable != null && bitmapDrawable.getBitmap() != null && !bitmapDrawable.getBitmap().isRecycled())

			{
				bitmapDrawable.getBitmap().recycle();
			}
		}
	}

	public Bitmap getBitMapFromUrl(String url) {
		Bitmap bitmap = null;
		URL u = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.disconnect();
		}
		return bitmap;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	/**
	 * Resize the bitmap
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		if (bitmap == null)
			return bitmap;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		if (scaleWidth < scaleHeight) {
			matrix.postScale(scaleWidth, scaleWidth);
		} else {
			matrix.postScale(scaleHeight, scaleHeight);
		}
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	public static String getLocalPath(String url) {
		String fileName = "temp.png";
		if (url != null) {
			if (url.contains("/")) {
				fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
			}
			if (fileName != null && fileName.contains("&")) {
				fileName = fileName.replaceAll("&", "");
			}
			if (fileName != null && fileName.contains("%")) {
				fileName = fileName.replaceAll("%", "");
			}
			// if (fileName != null && fileName.contains("?")) {
			// fileName = fileName.replaceAll("?", "");
			// }
		}
		return Environment.getExternalStorageDirectory() + "/" + fileName;
	}

	/**
	 * Save image to the SD card
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static void savePhotoToSDCard(Bitmap photoBitmap, String fullPath) {
		if (checkSDCardAvailable()) {
			File file = new File(fullPath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			File photoFile = new File(fullPath);
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
						fileOutputStream.flush();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally {
				try {
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					// if (photoBitmap != null && !photoBitmap.isRecycled()) {
					// photoBitmap.recycle();
					// }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_back:

			setResult(RESULT_OK);

			finish();
			break;
		case R.id.btn_dele:

			setResult(RESULT_CANCELED);

			finish();
			break;

		default:
			break;
		}

	}

}
