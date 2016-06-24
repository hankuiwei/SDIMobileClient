/**
 * 
 */
package com.lenovo.sdimobileclient.ui;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;

import com.foreveross.cache.CacheManager;
import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.lenovo.sdimobileclient.LenovoServicesApplication;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Error;
import com.lenovo.sdimobileclient.api.OrderOperations;
import com.lenovo.sdimobileclient.network.OkHttpHelper;

/**
 * @ClassName: RootActivity
 * @author ZhangShaofang
 * @Description: TODO
 */
public class RootFragmentActivity extends FragmentActivity implements IEncActivity {
	private HashMap<String, Object> mTempDataMap;
	private Error mError;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());
		getAppliction().getActivities().add(this);
		init();
	}

	/**
	 * 返回键监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Activity parent = getParent();
			if (parent != null && parent instanceof TabGroup && ((TabGroup) parent).isCurentFirst()) {
				return parent.onKeyDown(keyCode, event);
			} else {
				finish();
				return true;
			}
		} else
			return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化titlebar返回按钮
	 */
	protected void initBackBtn() {
		View backView = findViewById(R.id.btn_back);
		if (backView != null) {
			backView.setOnClickListener(this);
			backView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化titlebar菜单按钮
	 */
	protected void initRightBtn() {
		View btnRight = findViewById(R.id.btn_right);
		if (btnRight != null) {
			btnRight.setOnClickListener(this);
			btnRight.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void finish() {
		if (mCacheManager != null)
			mCacheManager.cancel(mCallback);
		super.finish();
	}

	/**
	 * 初始化titlebar保存按钮
	 */
	protected void initSaveBtn() {
		View btnRight = findViewById(R.id.btn_bar_right);
		if (btnRight != null) {
			btnRight.setOnClickListener(this);
			btnRight.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化titlebar搜索按钮
	 */
	protected void initSearchBtn() {
		View btnRight = findViewById(R.id.btn_search);
		if (btnRight != null) {
			btnRight.setOnClickListener(this);
			btnRight.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化设置title
	 */
	private void init() {
		String title = getTitle().toString();
		if (!TextUtils.isEmpty(title)) {
			TextView titleTv = (TextView) findViewById(R.id.title);
			if (titleTv != null)
				titleTv.setText(title);
		}
	}

	/**
	 * 设置title
	 * 
	 * @param title
	 *            页面标题
	 */
	protected void setTitle(String title) {
		if (!TextUtils.isEmpty(title)) {
			TextView titleTv = (TextView) findViewById(R.id.title);
			if (titleTv != null)
				titleTv.setText(title);
		}
	}

	/**
	 * 获取页面layoutId
	 * 
	 * @return
	 */
	protected int getContentViewId() {
		return R.layout.development_view;
	}

	/**
	 * 共用组件事件监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_search: {
			Intent intent = new Intent(this, TabOrderSearchActivity.class);
			startActivity(intent);
		}

			break;

		case R.id.btn_error_info:
			if (mErrorInfoView != null) {
				mErrorInfoView.setVisibility(mErrorInfoView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
			}
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_right: {
			showMenuPop();
		}
			break;
		case R.id.btn_order_info:
			mMenuPop.dismiss(); {
			Intent intent = new Intent(this, OrderInfoActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.btn_box_m: {
			mMenuPop.dismiss();
			Intent intent = new Intent(this, HostSearchActivity.class);
			intent.putExtra("type", 1);
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			startActivity(intent);
		}
			break;
		case R.id.btn_repair_search: {
			mMenuPop.dismiss();
			Intent intent = new Intent(this, HostSearchActivity.class);
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			startActivity(intent);
		}
			break;
		case R.id.btn_text:
			mMenuPop.dismiss();
			finish();
			MainActivity.tabHost.setCurrentTabByTag(MainActivity.TAB_SUPPORT);
			break;
		/*
		 * case R.id.btn_exit: List<Account> accounts =
		 * Account.queryAccount(this); if (!accounts.isEmpty()) { Account acc =
		 * accounts.get(0); acc.auto = false; acc.password = "";
		 * acc.update(this); } back(this); break;
		 */
		}
	}

	/**
	 * 设置等待页面
	 * 
	 * @param progressLayout
	 *            等待页面
	 * @param show
	 *            true 等待页面 false 错误页面
	 */
	protected void showProgress(View progressLayout, boolean show) {

		if (progressLayout == null) {
			return;
		}
		if (progressLayout.getVisibility() != View.VISIBLE)
			progressLayout.setVisibility(View.VISIBLE);
		if (show) {
			progressLayout.findViewById(R.id.error_footer).setVisibility(View.GONE);
			progressLayout.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
		} else {
			progressLayout.findViewById(R.id.error_footer).setVisibility(View.VISIBLE);
			progressLayout.findViewById(R.id.progressBar).setVisibility(View.GONE);
		}
	}

	/**
	 * 工单菜单
	 */
	protected PopupWindow mMenuPop;

	/**
	 * 初始化工单菜单
	 */
	private View replaceView = null;
	private View unReplaceView = null;
	private View attachView = null;
	private View invoiceView = null;
	private View lineReplace, lineUnReplace, lineAttach, lineInvoice;
	protected OrderOperations mOrderOperations;

	private void showMenuPop() {

		if (mMenuPop == null) {
			View menuView = getLayoutInflater().inflate(R.layout.order_menu_view, null);
			menuView.findViewById(R.id.btn_order_info).setOnClickListener(this);
			replaceView = menuView.findViewById(R.id.btn_replace);
			replaceView.setOnClickListener(this);
			unReplaceView = menuView.findViewById(R.id.btn_unreplace);
			unReplaceView.setOnClickListener(this);
			attachView = menuView.findViewById(R.id.btn_attachment);
			attachView.setOnClickListener(this);
			invoiceView = menuView.findViewById(R.id.btn_invoice);
			invoiceView.setOnClickListener(this);
			lineReplace = menuView.findViewById(R.id.line_replace);
			lineUnReplace = menuView.findViewById(R.id.line_unreplace);
			lineAttach = menuView.findViewById(R.id.line_attach);
			lineInvoice = menuView.findViewById(R.id.line_invoice);

			menuView.findViewById(R.id.btn_box_m).setOnClickListener(this);
			menuView.findViewById(R.id.btn_repair_search).setOnClickListener(this);
			menuView.findViewById(R.id.btn_text).setOnClickListener(this);
			mMenuPop = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mMenuPop.setAnimationStyle(R.style.PopupAnimation);
			mMenuPop.setFocusable(true);
			mMenuPop.setTouchable(true);
			mMenuPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_bg));
			mMenuPop.update();
		}
		if (mOrderOperations != null) {
			if (mOrderOperations.ReservationRecord_View) {
				replaceView.setVisibility(View.VISIBLE);
				lineReplace.setVisibility(View.VISIBLE);
				unReplaceView.setVisibility(View.VISIBLE);
				lineUnReplace.setVisibility(View.VISIBLE);
			} else {
				replaceView.setVisibility(View.GONE);
				lineReplace.setVisibility(View.GONE);
				unReplaceView.setVisibility(View.GONE);
				lineUnReplace.setVisibility(View.GONE);
			}
			if (mOrderOperations.Attachment_Normal_Upload) {
				attachView.setVisibility(View.VISIBLE);
				lineAttach.setVisibility(View.VISIBLE);
			} else {
				attachView.setVisibility(View.GONE);
				lineAttach.setVisibility(View.GONE);
			}
			if (mOrderOperations.Attachment_Invoice_Upload) {
				invoiceView.setVisibility(View.VISIBLE);
				lineInvoice.setVisibility(View.VISIBLE);
			} else {
				invoiceView.setVisibility(View.GONE);
				lineInvoice.setVisibility(View.GONE);
			}
		}
		if (!mMenuPop.isShowing()) {
			mMenuPop.showAsDropDown(findViewById(R.id.btn_right), 0, 15);
		}
	}

	/**
	 * 返回
	 * 
	 * @param activity
	 */
	protected void back(Activity activity) {
		Activity parent = activity.getParent();
		if (parent == null) {
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		if (parent instanceof TabGroup) {
			TabGroup group = (TabGroup) parent;
			if (group.isCurentFirst()) {
				back(group);
			} else {
				finish();

			}
		} else if (parent instanceof TabActivity) {
			TabHost tab = ((TabActivity) parent).getTabHost();
			int index = tab.getCurrentTab();
			if (index == 0) {
				back(parent);
			} else {
				parent.finish();
				// tab.setCurrentTab(0);
			}
		} else {
			finish();
		}

	}

	/**
	 * 重新父类方法，创建对话框
	 */
	@Override
	public Dialog onCreateDialog(int id) {
		AlertDialog result = null;
		Activity activity = getRootActivity(this);
		switch (id) {
		/**
		 * 网络错误
		 */
		case DLG_NETWORK_ERROR: {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			View view = getLayoutInflater().inflate(R.layout.err_msg_panel, null);
			builder.setView(view);
			builder.setTitle(R.string.title_error);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			result = builder.create();
			result.setButton(DialogInterface.BUTTON_POSITIVE, getText(R.string.btn_retry), mDialogListener);
			result.setButton(DialogInterface.BUTTON_NEGATIVE, getText(android.R.string.cancel), mDialogListener);
			result.setOwnerActivity(this);
			result.setCancelable(false);
		}
			break;
		case DLG_UNSAVE:
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("注意");
			builder.setMessage("尚未保存,是否退出？");
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			result = builder.create();
			result.setButton(DialogInterface.BUTTON_POSITIVE, "退出", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();

				}
			});
			result.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (DialogInterface.OnClickListener) null);
			result.setOwnerActivity(this);
			result.setCancelable(false);
			break;
		default:
			result = ActivityUtils.onCreateDialog(activity, id);
			break;
		}
		return result;
	}

	/**
	 * 获取activity基类
	 * 
	 * @param activity
	 * @return
	 */
	public static Activity getRootActivity(Activity activity) {
		Activity parent = activity == null ? null : activity.getParent();
		if (parent != null) {
			return getRootActivity(parent);
		}
		return activity;
	}

	/**
	 * 设置错误数据
	 */
	@Override
	public void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DLG_NETWORK_ERROR:
			setDialogError(dialog, mError);
			break;

		default:
			super.onPrepareDialog(id, dialog);
			break;
		}
	}

	private View mErrorInfoView;

	/**
	 * 初始化错误对话框
	 * 
	 * @param dialog
	 * @param error
	 * @return
	 */
	private Dialog setDialogError(Dialog dialog, Error error) {
		TextView errcodeView = (TextView) dialog.findViewById(R.id.errcode);
		TextView errmsgView = (TextView) dialog.findViewById(R.id.errmsg);
		errcodeView.setText(error.error_code);
		errmsgView.setText(error.error_msg);
		View btnErrorView = dialog.findViewById(R.id.btn_error_info);
		btnErrorView.setVisibility(View.GONE);
		if (DEBUG) {
			if (mErrorInfoView != null) {
				mErrorInfoView.setVisibility(View.GONE);
			}
			if (error.fileinfo != null) {
				btnErrorView.setVisibility(View.VISIBLE);
				btnErrorView.setOnClickListener(this);
				mErrorInfoView = dialog.findViewById(R.id.error_info);
				TextView url = (TextView) mErrorInfoView.findViewById(R.id.error_url);
				TextView post = (TextView) mErrorInfoView.findViewById(R.id.error_postvalue);
				TextView exception = (TextView) mErrorInfoView.findViewById(R.id.error_exception);
				url.setText(error.fileinfo.url);
				post.setText(String.valueOf(error.fileinfo.postValues == null ? "" : error.fileinfo.postValues));
				exception.setText(TextUtils.isEmpty(error.fileinfo.exception) ? "" : error.fileinfo.exception);
			}
		}
		return dialog;
	}

	public <T> T getTempData(String key, boolean remove) {
		if (mTempDataMap == null) {
			return null;
		}
		Object obj = remove ? mTempDataMap.remove(key) : mTempDataMap.get(key);
		try {
			return (T) obj;
		} catch (ClassCastException e) {
			Log.w(LOG_TAG, "Class case Exception in getTempData(key)", e);
			return null;
		}
	}

	@Override
	public void addTempData(String key, Object data) {
		if (mTempDataMap == null) {
			mTempDataMap = new HashMap<String, Object>();
		}
		mTempDataMap.put(key, data);
	}

	@Override
	public LenovoServicesApplication getAppliction() {
		return (LenovoServicesApplication) getApplication();
	}

	@Override
	public void errorData(Error error) {
		mError = error;
	}

	public void dismisDialog(int id) {
		try {
			dismissDialog(id);
		} catch (Exception e) {
		}
	}

	private CacheManager mCacheManager;
	private OnClickListener mDialogListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				retryLoadData();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 重试
	 */
	protected void retryLoadData() {
		notifyView();
		
//		OkHttpHelper.loadAgain();
	}

	/**
	 * 子类重新，实现重试过程中页面加载进度
	 */
	protected void notifyView() {

	}

	protected int mCallbackId;
	private CacheParams mCacheParams;
	private Callback mCallback;

	@Override
	public void notifyReloadByErrDlg(int id, CacheParams params, Callback callback) {
		mCallbackId = id;
		mCacheParams = params;
		mCallback = callback;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCacheManager != null) {
			mCacheManager.cancel(mCallback);
		}
	}
}
