package com.lenovo.sdimobileclient.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.foreveross.cache.utility.FileUtils;
import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.utility.FileUtil;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 照片获取
 * 
 * @author zhangshaofang
 * 
 */
public class PicturePickerActivity extends Activity implements OnCancelListener, Constants, OnDismissListener {
	private static final String MIME_TYPE_IMAGE = "image/*";
	private static final int DLG_CUSTOM_BASE = 2001;
	private static final int REQUEST_CODE_FIRST_CUSTUM = 4001;
	private static final int DLG_SELECT_PICKER = DLG_CUSTOM_BASE + 1;
	private static final int DLG_WAITING = DLG_CUSTOM_BASE + 2;

	private static final int REQUEST_CODE_FROM_ALBUM = REQUEST_CODE_FIRST_CUSTUM + 1;
	private static final int REQUEST_CODE_FROM_CAMERA = REQUEST_CODE_FIRST_CUSTUM + 2;
	private static final int REQUEST_CODE_CROP_IMAGE = REQUEST_CODE_FIRST_CUSTUM + 3;
	private static final int REQUEST_CODE_FROM_AUDIO = REQUEST_CODE_FIRST_CUSTUM + 4;
	public static final int RESULT_CODE_AUDIO = 1001;

	private static final String TEMP_FILENAME = "temp_pic.jpg";

	private Uri mSaveUri;
	private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
	private int mOutputWidth;
	private int mOutputHeight;
	private boolean mOutputBitmap = true;
	private boolean mFixSize = true;
	private boolean mDismiss;
	private File mTempFile;
	private Bitmap mBitmap;

	private final Handler mHandler = new Handler();

	protected int getContentViewId() {
		return R.layout.page_translucent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (extras != null) {
			mSaveUri = extras.getParcelable(MediaStore.EXTRA_OUTPUT);

			String outputFormatString = extras.getString("outputFormat");
			if (outputFormatString != null) {
				try {
					mOutputFormat = Bitmap.CompressFormat.valueOf(outputFormatString);
				} catch (Throwable t) {
					Log.w(LOG_TAG, "outputFormat value is not correct. Value:" + outputFormatString);
				}
			}

			mOutputWidth = extras.getInt("outputX");
			mOutputHeight = extras.getInt("outputY");

			mFixSize = extras.getBoolean("fix-size", true);
			mOutputBitmap = extras.getBoolean("return-data", true);
		}
		showDialog(DLG_SELECT_PICKER);
	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog result = null;

		Context context = ActivityUtils.getRootActivity(this);
		switch (id) {
		case DLG_SELECT_PICKER: {
			AlertDialog dialog = new AlertDialog.Builder(context).setTitle("设置").setItems(R.array.picture_setting, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDismiss = true;
					switch (which) {
					case 0:
						openActivityForPickImageFromCamera();
						break;
					case 1:
						openActivityForPickImageFromAlbum();
						break;
					case 2:
						openActivityForVideoFromAlbum();
						break;
					}
				}
			}).create();
			dialog.setOnCancelListener(this);
			dialog.setOnDismissListener(this);
			result = dialog;
			break;
		}
		case DLG_WAITING:
			ProgressDialog dialog = new ProgressDialog(context);
			dialog.setTitle("");
			dialog.setMessage(getString(R.string.data_loading));
			dialog.setIndeterminate(false);

			dialog.setCancelable(true);
			dialog.setOnCancelListener(this);

			result = dialog;

			break;
		default:
			result = super.onCreateDialog(id);
		}
		return result;
	}

	public String getPath(Uri uri) {

		String[] column = { MediaStore.Images.Media.DATA };

		// where id is equal to
		String sel = MediaStore.Images.Media._ID + "=?";

		Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, null, null);

		String filePath = "";

		int columnIndex = cursor.getColumnIndex(column[0]);

		if (cursor.moveToFirst()) {
			filePath = cursor.getString(columnIndex);
		}

		cursor.close();

		return filePath;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		if ((resultCode != RESULT_OK)
				&& (requestCode == REQUEST_CODE_FROM_ALBUM || requestCode == REQUEST_CODE_FROM_CAMERA || requestCode == REQUEST_CODE_CROP_IMAGE)) {
			cancel();
			return;
		}

		switch (requestCode) {
		case REQUEST_CODE_FROM_ALBUM:
		case REQUEST_CODE_FROM_CAMERA: {
			Uri path = null;
			Bitmap data = null;
			if (result != null) {
				path = result.getData();
				final Bundle extras = result.getExtras();
				String path2 = getPath(path);
				if (extras != null) {
					data = (Bitmap) extras.getParcelable(EXTRA_IMAGE_DATA);
					if (data == null) {
						data = (Bitmap) extras.getParcelable("data");
					}
				}
			}

			if (path == null && mTempFile != null) {
				path = Uri.fromFile(mTempFile);
			}
			returnData(data, path);
			break;
		}
		case REQUEST_CODE_FROM_AUDIO: {
			Uri path = null;
			if (result != null) {
				path = result.getData();
			}
			Intent intent = new Intent();
			intent.setData(path);
			setResult(RESULT_CODE_AUDIO, intent);
			finish();
		}
			break;
		case REQUEST_CODE_CROP_IMAGE: {
			if (result == null) {
				cancel();
				return;
			}

			Uri path = null;
			Bitmap bitmap = null;

			final Bundle extras = result.getExtras();
			if (extras != null) {
				mBitmap = bitmap = extras.getParcelable(EXTRA_IMAGE_DATA);
			}

			String action = result.getAction();
			if (!TextUtils.isEmpty(action)) {
				path = Uri.parse(action);
			}
			if (path == null && mTempFile != null) {
				path = Uri.fromFile(mTempFile);
			}

			showDialog(DLG_WAITING);
			if (bitmap == null && path == null) {
				File pfile = new File(TEMP_FILENAME);
				path = Uri.fromFile(pfile);
			}
			final Bitmap b = bitmap;
			final Uri uri = path;

			new Thread() {
				public void run() {
					try {
						returnData(b, uri);
					} catch (Throwable t) {
						cancel();
					} finally {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								dismissDialog(DLG_WAITING);
							}
						});
					}
				}
			}.start();
			break;
		}
		default:
			super.onActivityResult(requestCode, resultCode, result);
		}
	}

	private void returnData(Bitmap bitmap, Uri path) {

		if (bitmap == null && path == null) {
			cancel();
			return;
		}

		if (bitmap == null) {
			try {
				InputStream stream = getContentResolver().openInputStream(path);
				bitmap = Utils.getBitpMap(this, stream, path);

				ContentResolver cr = getContentResolver();
				mTempFile = FileUtil.getTempFile(this);
				FileUtil.copy(cr, path, mTempFile);

			} catch (Exception e) {
			}
		} else {
			path = Uri.fromFile(Utils.generatorFileFromBitmap(this, bitmap));

		}

		if (mTempFile.length() > 1024 * 1024) {

			boolean save = save(this, mTempFile.getAbsolutePath(), getSmallFile(bitmap));
		}

		if (bitmap == null) {
			cancel();
			return;
		}
		Bitmap adjustSize = adjustSize(bitmap);
		saveOutput(adjustSize, Uri.fromFile(mTempFile));
	}

	private Bitmap getSmallFile(final Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		if (mOutputWidth <= 0 || mOutputHeight <= 0) {
			return bitmap;
		}

		int h = bitmap.getHeight();
		int w = bitmap.getWidth();
		if (w <= mOutputWidth && h <= mOutputHeight) {
			return bitmap;
		}
		Bitmap bitmap2 = bitmap;
		float scale;
		int x, y;
		int targetW, targetH;

		scale = 0.5f;

		targetW = (int) (w * scale);
		targetH = (int) (h * scale);

		x = 0;
		y = 0;

		Bitmap croppedImage = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(croppedImage);

		Rect srcRect = new Rect(0, 0, w, h);
		Rect dstRect = new Rect(0, 0, targetW, targetH);
		dstRect.inset(x, y);

		canvas.drawBitmap(bitmap, srcRect, dstRect, null);

		return croppedImage;
	}

	private Bitmap adjustSize(final Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		int h = bitmap.getHeight();
		int w = bitmap.getWidth();
		if (w <= mOutputWidth && h <= mOutputHeight) {
			return bitmap;
		}

		float scale;
		int x, y;
		int targetW, targetH;

		if (mFixSize) {
			targetW = mOutputWidth;
			targetH = mOutputHeight;

			float scaleX = (float) targetW / w;
			float scaleY = (float) targetH / h;
			scale = 0.2f;

			x = (int) ((targetW - w * scale) / 2);
			y = (int) ((targetH - h * scale) / 2);

		} else {
			float scaleX = Math.min(1, (float) mOutputWidth / w);
			float scaleY = Math.min(1, (float) mOutputHeight / h);
			scale = Math.min(scaleX, scaleY);

			targetW = (int) (w * scale);
			targetH = (int) (h * scale);

			x = 0;
			y = 0;
		}

		Bitmap croppedImage = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(croppedImage);

		Rect srcRect = new Rect(0, 0, w, h);
		Rect dstRect = new Rect(0, 0, targetW, targetH);
		dstRect.inset(x, y);

		canvas.drawBitmap(bitmap, srcRect, dstRect, null);
		bitmap.recycle();
		return croppedImage;
	}

	private void cancel() {
		setResult(RESULT_CANCELED);
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		if (!mOutputBitmap && mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;

		}
		// if (mTempFile != null && mTempFile.exists()) {
		// mTempFile.delete();
		// }
		super.onDestroy();
	}

	public boolean save(Context mContext, String path, Bitmap bitmap) {
		DisplayMetrics dm;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (path != null) {
			try {
				File f = new File(path);
				FileOutputStream fos = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
				saveMyBitmap(bitmap);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	private void saveMyBitmap(Bitmap bm) {
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(mTempFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bm.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveOutput(final Bitmap croppedImage, Uri fileUri) {
		// Uri uri = mSaveUri;
		// boolean success = false;
		// OutputStream outputStream = null;
		// try {
		// if (uri == null) {
		// File dir = getCacheDir();
		// File file = new File(dir, "output." + mOutputFormat);
		// String path = FileUtil.chooseUniqueFilename(file.getAbsolutePath());
		// file = new File(path);
		// if (!file.exists()) {
		// file.createNewFile();
		// }
		// uri = Uri.fromFile(file);
		// }
		//
		// outputStream = getContentResolver().openOutputStream(uri);
		// if (outputStream != null) {
		// croppedImage.compress(mOutputFormat, 75, outputStream);
		// }
		// success = true;
		// } catch (IOException ex) {
		// Log.e(LOG_TAG, "Cannot open file: " + uri, ex);
		// } finally {
		// croppedImage.recycle();
		// try {
		// outputStream.close();
		// } catch (Throwable t) {
		// // do nothing
		// }
		// }
		// if (success) {
		Intent intent = new Intent();
		intent.putExtra("data", croppedImage);
		intent.setData(fileUri);
		setResult(RESULT_OK, intent);
		finish();
		// } else {
		// cancel();
		// }
	}

	private void openActivityForPickImageFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(MIME_TYPE_IMAGE);
		startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
	}

	private void openActivityForVideoFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("video/*");
		startActivityForResult(intent, REQUEST_CODE_FROM_AUDIO);
	}

	private void openActivityForPickImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mTempFile = initTempFile();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
		startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
	}

	private File initTempFile() {
		File result = getFileStreamPath(TEMP_FILENAME);
		try {
			result.delete();
			OutputStream stream = openFileOutput(TEMP_FILENAME, Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
			stream.close();
		} catch (Exception e) {
			// do unthing
		}
		return result;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		cancel();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (!mDismiss) {
			cancel();
		}

	}
}
