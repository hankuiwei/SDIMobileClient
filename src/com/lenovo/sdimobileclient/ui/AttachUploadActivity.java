package com.lenovo.sdimobileclient.ui;

import java.io.File;
import java.util.ArrayList;

import android.R.bool;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.AttachmentTypes;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.api.Image;
import com.lenovo.sdimobileclient.data.Attach;
import com.lenovo.sdimobileclient.service.UploadAttachService;
import com.lenovo.sdimobileclient.utility.FileUtil;
import com.lenovo.sdimobileclient.utility.Utils;

/**
 * 添加附件
 * 
 * @author zhangshaofang
 * 
 */
public class AttachUploadActivity extends RootActivity {

	private RelativeLayout mAttachLayout;
	private String mOrderId;
	private String[] mCategoryArray;
	private ArrayList<AttachmentTypes> mAttachmentTypes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View addView = findViewById(R.id.btn_add);
		// addView.setVisibility(View.VISIBLE);
		// addView.setOnClickListener(this);
		mOrderId = getIntent().getStringExtra("orderId");
		mAttachmentTypes = getIntent().getParcelableArrayListExtra("attach_types");
		if (mAttachmentTypes != null) {
			mCategoryArray = new String[mAttachmentTypes.size()];
			for (int i = 0; i < mCategoryArray.length; i++) {
				AttachmentTypes attachmentTypes = mAttachmentTypes.get(i);
				mCategoryArray[i] = attachmentTypes.Name;
			}
		}
		initBackBtn();
		initView();
		addAttach();
	}

	private void initView() {

		et_type = (EditText) findViewById(R.id.et_type);
		mAttachLayout = (RelativeLayout) findViewById(R.id.rl_photo);
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

	@Override
	public void finish() {
		System.gc();
		super.finish();
	}

	private boolean emptyData() {
		boolean result = true;

		TextView desTv = (TextView) findViewById(R.id.et_des);

		TextView cateTv = (TextView) findViewById(R.id.tv_category);
		int childCount = mAttachLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = mAttachLayout.getChildAt(i);
			// TextView nameTv = (TextView) view.findViewById(R.id.et_name);

			// String name = nameTv.getText().toString();
			// String des = desTv.getText().toString();
			Object obj = cateTv.getTag();
			AttachmentTypes attachmentTypes = null;
			if (obj != null) {
				attachmentTypes = (AttachmentTypes) obj;
			}
			// if (!TextUtils.isEmpty(name) || attachmentTypes != null ||
			// !TextUtils.isEmpty(des)) {
			// if (attachmentTypes != null || !TextUtils.isEmpty(des)) {
			if (attachmentTypes != null) {
				result = false;
			}
			View image = findViewById(R.id.icon);
			Object object = image.getTag();
			if (object instanceof File) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !emptyData()) {
			showDialog(DLG_UNSAVE);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
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
	@SuppressWarnings("unused")
	private void save() {
		int childCount = mAttachLayout.getChildCount();
		attachs = new ArrayList<Attach>();
		TextView desTv = (TextView) findViewById(R.id.et_des);
		TextView cateTv = (TextView) findViewById(R.id.tv_category);
		// for (int i = 0; i < childCount; i++) {
		View view = mAttachLayout.getChildAt(0);
		// TextView nameTv = (TextView) view.findViewById(R.id.et_name);

		// String name = nameTv.getText().toString();
		Object obj = cateTv.getTag();

		if (obj == null) {

			Utils.showToast(this, "类型不能为空");

			return;
		}

		AttachmentTypes attachmentType = (AttachmentTypes) obj;
		View image = view.findViewById(R.id.icon);
		Object object = image.getTag();
		if (object instanceof File) {
			File file = (File) object;

			String string = file.getAbsolutePath().toString();

			// nameTv.setText(file.getName());
			EnginnerInfo enginnerInfo = Utils.getEnginnerInfo(this);

			if (file != null) {
				Attach attach = new Attach(et_type.getText().toString().trim() + ".jpg", mOrderId, enginnerInfo.EngineerNumber, file.getAbsolutePath(),
						attachmentType.ID, Attach.ATTCH_NORMAL, desTv == null ? "" : desTv.getText().toString(), attachmentType.Name);
				attach.fileType = Attach.FILETYPE_ATTACH;
				int id = (int) attach.insert(this);
				attach._id = id;
				attachs.add(attach);

			}
		} else {
			Toast.makeText(this, "图片不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		// if (TextUtils.isEmpty(name)) {
		// Toast.makeText(this, "附件名称不能为空", Toast.LENGTH_SHORT).show();
		// return;
		// }
		if (obj == null) {
			Toast.makeText(this, "类型尚未选择", Toast.LENGTH_SHORT).show();
			return;
		}
		// }

		File file = (File) object;
		long length = file.length();

		if (length > 1024 * 1024) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.ic_network_error);
			builder.setTitle("注意");
			builder.setMessage("文件大小超过1mb,是否上传");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					checkNet();
					dialog.dismiss();
				}

			});

			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}

			});
			builder.create();
			builder.show();

		} else {

			checkNet();
		}

	}

	private void checkNet() {
		boolean networkAvailable = Utils.isNetworkAvailable(this);

		if (!networkAvailable) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.ic_network_error);
			builder.setTitle("网络提示信息");
			builder.setMessage("您正在使用您的套餐流量,确定继续上传附件");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					startupload();
					dialog.dismiss();
				}

			});

			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}

			});
			builder.create();
			builder.show();

		} else {
			startupload();
		}
	}

	private void startupload() {
		finish();
		Toast.makeText(getApplicationContext(), "已提交到后台上传", Toast.LENGTH_SHORT).show();

		Intent intent = new Intent(AttachUploadActivity.this, UploadAttachService.class);
		intent.putParcelableArrayListExtra("attachs", attachs);
		startService(intent);

	};

	private ImageView mImageView;
	private View mIconPlayView;
	private View mCategoryTv;
	private static final int DLG_ATTACH_CATEGORY = 3001;
	private ArrayList<Attach> attachs;
	private EditText et_type;

	/**
	 * 添加附件
	 */
	private void addAttach() {
		// final View view = getLayoutInflater().inflate(R.layout.attach, null);
		findViewById(R.id.btn_category).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCategoryTv = v;
				showDialog(DLG_ATTACH_CATEGORY);

			}
		});
		findViewById(R.id.icon).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mImageView = (ImageView) v;
				mIconPlayView = findViewById(R.id.ic_play);
				Object object = v.getTag();
				if (object != null && object instanceof File) {
					File file = (File) object;
					// ArrayList<Image> images = new ArrayList<Image>();
					Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
					intent.putExtra(ImageActivity.EXTRA_IMAGE, file.getAbsolutePath());
					intent.putExtra("showtitle", true);
					startActivityForResult(intent, 501);
				} else {

					ActionHelper.openActivityForPickImage(AttachUploadActivity.this, null, MAX_WIDTH, MAX_HEIGHT, false, false, false, REQUEST_CODE_GET_IMAGE);

				}

			}
		});
		// mAttachLayout.addView(view);
	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog result = null;
		switch (id) {
		case DLG_ATTACH_CATEGORY: {
			AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.more_label_setting)
					.setItems(mCategoryArray, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String cateString = mCategoryArray[which];
							AttachmentTypes types = mAttachmentTypes.get(which);
							TextView attchType = (TextView) mCategoryTv.findViewById(R.id.tv_attchtype);

							attchType.setText(cateString);
							mCategoryTv.findViewById(R.id.tv_category).setTag(types);
						}
					}).create();
			result = dialog;
		}
			break;

		default:
			result = super.onCreateDialog(id);
			break;
		}
		return result;
	}

	/**
	 * 
	 * 获取文件名称
	 * 
	 * @param uri
	 * @return
	 */

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
				File tag = (File) mImageView.getTag();

				et_type.setText(tag.getName());

			}
			if (resultCode == PicturePickerActivity.RESULT_CODE_AUDIO && data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					ContentResolver cr = getContentResolver();
					Bitmap bitmap = getVideoThumbnail(cr, uri);
					mImageView.setImageBitmap(bitmap);
					mIconPlayView.setVisibility(View.VISIBLE);
					VideoFile videoFile = new VideoFile(uri);
					mImageView.setTag(videoFile);
				}
			}
			break;
		case 501:

			if (resultCode == RESULT_CANCELED) {
				mImageView.setTag(null);
				mImageView.setImageResource(R.drawable.cam);
			}

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
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
		if (bmp != null) {
			mImageView.setImageBitmap(bmp);
			if (fileUri != null) {
				File file = FileUtil.getTempFile(this);
				if (FileUtil.copy(cr, fileUri, file)) {
					mImageView.setTag(file);
				}
			}
		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.attach;
	}
}
