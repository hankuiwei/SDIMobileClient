package com.lenovo.sdimobileclient.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.data.Attach;
import com.lenovo.sdimobileclient.service.UploadAttachService;
import com.lenovo.sdimobileclient.utility.FileUtil;
import com.lenovo.sdimobileclient.utility.Utils;

/**
 * 添加附件 发票
 * 
 * @author zhangshaofang
 * 
 */
public class InvoiceUploadActivity extends RootActivity {

	private LinearLayout mAttachLayout;
	private String mOrderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBackBtn();
		mOrderId = getIntent().getStringExtra("orderId");
		initView();
		addAttach();
	}

	private void initView() {
		// mAttachLayout = (LinearLayout) findViewById(R.id.attach_layout);
		findViewById(R.id.btn_save_attach).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			addAttach();
			break;
		case R.id.btn_save_attach:
			save();
			break;
		case R.id.btn_back:
			if (emptyData()) {
				finish();
			} else {
				showDialog(DLG_UNSAVE);
			}
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	public class VideoFile {
		public Uri file;

		public VideoFile(Uri file) {
			this.file = file;
		}

	}

	/**
	 * 保存附件
	 */
	private void save() {
		// int childCount = mAttachLayout.getChildCount();
		ArrayList<Attach> attachs = new ArrayList<Attach>();
		// for (int i = 0; i < childCount; i++) {
		// View view = mAttachLayout.getChildAt(i);
		TextView nameTv = (TextView) findViewById(R.id.et_invoice_code);
		TextView dateTv = (TextView) mDateLayout.findViewById(R.id.tv_attchtype);
		String name = nameTv.getText().toString();
		String date = dateTv.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, "发票号码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(date)) {
			Toast.makeText(this, "购机日期不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		Object object = mImageView.getTag();
		if (object instanceof File) {
			File file = (File) object;
			EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(this);
			if (file != null) {
				Attach attach = new Attach(nameTv.getText().toString(), mOrderId, enginnerInfo.EngineerNumber, file.getAbsolutePath(), Attach.ATTCH_INVOICE,
						null, dateTv.getText().toString(), null);
				attach.fileType = Attach.FILETYPE_INVOICE;
				int id = (int) attach.insert(this);
				attach._id = id;
				attachs.add(attach);
			}
		} else {
			Toast.makeText(this, "图片不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		// }
		finish();
		Toast.makeText(getApplicationContext(), "已提交到后台上传", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(InvoiceUploadActivity.this, UploadAttachService.class);
		intent.putParcelableArrayListExtra("attachs", attachs);
		startService(intent);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !emptyData()) {
			showDialog(DLG_UNSAVE);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private boolean emptyData() {
		boolean result = true;
		// int childCount = mAttachLayout.getChildCount();
		// for (int i = 0; i < childCount; i++) {
		// View view = mAttachLayout.getChildAt(i);
		TextView nameTv = (TextView) findViewById(R.id.et_invoice_code);
		TextView dateTv = (TextView) findViewById(R.id.tv_attchtype);
		String name = nameTv.getText().toString();
		String date = dateTv.getText().toString();
		if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(date)) {
			result = false;
		}
		View image = findViewById(R.id.icon);
		Object object = image.getTag();
		if (object instanceof File) {
			result = false;
		}
		// }
		return result;
	}

	private ImageView mImageView;
	private View mIconPlayView;
	private TextView mDateTv;
	private static final int DATE_DIALOG = 3001;
	private View mDateLayout;

	/**
	 * 添加附件
	 */
	private void addAttach() {
		// final View view = getLayoutInflater().inflate(R.layout.invoice,
		// null);
		mDateLayout = findViewById(R.id.btn_category);
		findViewById(R.id.invoice).setVisibility(View.VISIBLE);

		TextView category = (TextView) findViewById(R.id.tv_category);
		category.setText("购机日期");

		mDateLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG);

			}
		});
		// view.findViewById(R.id.et_date).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// mDateTv = (TextView) v;
		// showDialog(DATE_DIALOG);
		// }
		// });
		mImageView = (ImageView) findViewById(R.id.icon);
		mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIconPlayView = findViewById(R.id.ic_play);
				ActionHelper.openActivityForPickImage(InvoiceUploadActivity.this, null, MAX_WIDTH, MAX_HEIGHT, false, false, false, REQUEST_CODE_GET_IMAGE);
			}
		});

		mDateTv = (TextView) mDateLayout.findViewById(R.id.tv_attchtype);

		// mAttachLayout.addView(view);
	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog result = null;
		switch (id) {
		case DATE_DIALOG:
			Calendar c = Calendar.getInstance();
			result = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {

					mDateTv.setText(year + "年" + (month + 1) + "月" + dayOfMonth + "日");
				}
			}, c.get(Calendar.YEAR), // 传入年份
					c.get(Calendar.MONTH), // 传入月份
					c.get(Calendar.DAY_OF_MONTH) // 传入天数
			);
			break;

		default:
			result = super.onCreateDialog(id);
			break;
		}
		return result;
	}

	/**
	 * 获取图片回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_GET_IMAGE:
			if (resultCode == RESULT_OK && data != null) {
				Bitmap bitmap = data.getParcelableExtra("data");
				Uri fileUri = data.getData();
				setImage(bitmap, fileUri);
			}
			if (resultCode == PicturePickerActivity.RESULT_CODE_AUDIO && data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					ContentResolver cr = getContentResolver();
					Bitmap bitmap = getVideoThumbnail(cr, uri);
					// FILETYPE_VIDEO
					mImageView.setImageBitmap(bitmap);
					mIconPlayView.setVisibility(View.VISIBLE);
					VideoFile videoFile = new VideoFile(uri);
					mImageView.setTag(videoFile);
				}
			}
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Cursor cursor = cr.query(uri, new String[] { MediaStore.Video.Media._ID }, null, null, null);

		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		cursor.moveToFirst();
		String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));

		if (videoId == null) {
			return null;
		}
		cursor.close();
		long videoIdLong = Long.parseLong(videoId);
		bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong, Images.Thumbnails.MICRO_KIND, options);

		return bitmap;
	}

	/**
	 * 设置图片
	 * 
	 * @param uri
	 */
	private void setImage(Bitmap bmp, Uri fileUri) {
		ContentResolver cr = getContentResolver();
		if (fileUri != null) {
			File file = FileUtil.getTempFile(this);
			if (FileUtil.copy(cr, fileUri, file)) {
				mImageView.setTag(file);
			}
		}
		if (bmp != null) {
			mImageView.setImageBitmap(bmp);

		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.attach;
	}
}
