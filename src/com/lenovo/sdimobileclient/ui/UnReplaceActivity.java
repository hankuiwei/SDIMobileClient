package com.lenovo.sdimobileclient.ui;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.foreveross.cache.CacheManager;
import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.Netpath;
import com.foreveross.cache.network.ParamPair;
import com.google.gson.Gson;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Action;
import com.lenovo.sdimobileclient.api.MatelialClass;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.api.SearchCategory;
import com.lenovo.sdimobileclient.api.SparePartsExt;
import com.lenovo.sdimobileclient.api.Unchange;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UnReplaceActivity extends RootActivity implements OnItemClickListener {

	// private static final int DLG_ADD_REPLACE = 2001;
	private LinearLayout mReplaceLayout;
	private LinearLayout mUnReplaceLayout;
	private LayoutInflater mInflater;

	private enum ReplaceInfo {
		replace, unreplace
	}

	private ReplaceInfo mReplaceInfo = ReplaceInfo.replace;
	private String mOrderId;
	private boolean mReplaceEdit;
	private boolean mUnReplaceEdit;
	private View mBtnAddView;
	private View mWaitView;
	private View mActionVIew;
	private OkHttpHelper mHttpHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHttpHelper = OkHttpHelper.getInstance(this);
		mBtnSubmit = findViewById(R.id.btn_next);
		mBtnSubmit.setVisibility(View.VISIBLE);
		initBackBtn();
		mOrderId = getIntent().getStringExtra("orderId");
		mBtnAddView = findViewById(R.id.btn_add);
		mBtnAddView.setOnClickListener(this);
		mReplaceEdit = getIntent().getBooleanExtra("replace", false);
		mUnReplaceEdit = getIntent().getBooleanExtra("unreplace", false);
		if (DEBUG)
			mUnReplaceEdit = true;

		mReplaceLayout = (LinearLayout) findViewById(R.id.replace_layout);
		mUnReplaceLayout = (LinearLayout) findViewById(R.id.unreplace_layout);
		mUnReplaceLayout.setVisibility(View.GONE);
		mInflater = LayoutInflater.from(this);
		mWaitView = findViewById(R.id.progress);
		mWaitView.findViewById(R.id.btn_retry).setOnClickListener(this);
		mReplaceInfo = ReplaceInfo.unreplace;
		mReplaceLayout.setVisibility(View.GONE);

		mSptypeTv = (TextView) findViewById(R.id.unreplace_category);
		mActionTv = (TextView) findViewById(R.id.unreplace_action);
		mSptypeView = findViewById(R.id.un_btn_sptype);
		// spTypeView.setTag(view);
		// if (unchange == null)
		mSptypeView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DLG_UNREPLACE_SPTYPE);
			}
		});
		addUnReplaceView(null);
		loadUnReplaceInfo();

	}

	private static final int CALLBACK_LOADUNREPLACE = 1;
	private static final int CALLBACK_LOADREPLACE = 4;
	private boolean mUnReplaceLoad = false;
	private boolean mReplaceLoad = false;
	private int mLoadId = -1;

	private void loadUnReplaceInfo() {
		if (mUnReplaceLoad) {
			return;
		}
		mUnReplaceLoad = true;
		Uri uri = Uri.parse(URL_UNCHANGE_SOURCES).buildUpon().appendQueryParameter(PARAM_ORDERID, mOrderId)
				.appendQueryParameter(PARAM_ENGINEER, Utils.getEnginnerInfo(this).EngineerNumber).build();
		mLoadId = CALLBACK_LOADUNREPLACE;
		mHttpHelper.load(uri.toString(), mCallBack, this);
	}

	private void loadReplaceInfo() {
		if (mReplaceLoad) {
			return;
		}
		mReplaceLoad = true;
		Uri uri = Uri.parse(URL_CHANGE_SOURCES).buildUpon().appendQueryParameter(PARAM_ORDERID, mOrderId)
				.appendQueryParameter(PARAM_ENGINEER, Utils.getEnginnerInfo(this).EngineerNumber).build();
		NetworkPath path = new Netpath(uri.toString());
		mLoadId = CALLBACK_LOADREPLACE;
		mHttpHelper.load(uri.toString(), mCallBack, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			// addUnReplaceView(null);
			break;
		case R.id.btn_next:
			// TODO xxxxxxxxxx
			switch (mReplaceInfo) {
			case replace:

				break;
			case unreplace:

				submitUnReplaceData();
				break;
			}
			break;
		case R.id.btn_retry:
			retryLoadData();
			break;
		case R.id.btn_host_info:
			mBtnAddView.setVisibility(View.GONE);
			mReplaceInfo = ReplaceInfo.replace;
			mUnReplaceLayout.setVisibility(View.GONE);
			mReplaceLayout.setVisibility(View.VISIBLE);
			loadReplaceInfo();
			break;
		case R.id.btn_box:
			mBtnAddView.setVisibility(View.GONE);
			mReplaceInfo = ReplaceInfo.unreplace;
			mUnReplaceLayout.setVisibility(View.VISIBLE);
			mReplaceLayout.setVisibility(View.GONE);
			loadUnReplaceInfo();
			break;
		case R.id.btn_back:
			if (emptyData()) {
				finish();
			} else {
				showDialog(DLG_UNSAVE);
			}
			break;
		case R.id.cb_all: {
			if (mAllCheckBox.isChecked()) {
				List<SearchCategory> materialList = mSearchAdapter.getSelectData();
				materialList.clear();
				materialList.addAll(mSearchAdapter.getmObjects());
			} else {
				List<SearchCategory> materialList = mSearchAdapter.getSelectData();
				materialList.clear();
			}
			mSearchAdapter.notifyDataSetChanged();
		}
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	protected void notifyView() {
		if (mCallbackId == CALLBACK_LOADUNREPLACE) {
			showProgress(mWaitView, true);
		} else if (mCallbackId == CALLBACK_SUBMIT_UNREPLACEDATA) {
			mBtnSubmit.setEnabled(false);
			showDialog(DLG_SENDING);
		} else if (mCallbackId == CALLBACK_DELETE_UNREPLACEDATA) {
			showDialog(DLG_SENDING);
		}
	}

	private static final int CALLBACK_SUBMIT_UNREPLACEDATA = 2;
	private static final int CALLBACK_DELETE_UNREPLACEDATA = 3;

	private void submitUnReplaceData() {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		StringBuilder types = new StringBuilder();
		StringBuilder actions = new StringBuilder();
		if (mCcsend == 1) {

			ccsendflag = true;

			for (SparePartsExt irem : mSparePartsExtList) {

				types.append(irem.SPType).append("&");

				if (irem.Action.contains("&nbsp;")) {

					irem.Action = irem.Action.replace("&nbsp;", "");

				}

				actions.append(irem.Action).append("&");

			}
			types.deleteCharAt(types.length() - 1);
			actions.deleteCharAt(actions.length() - 1);

		} else {

			Object obj = mUnReplaceView.getTag();
			if (obj == null) {
				Toast.makeText(this, "请选择部件大类和动作", Toast.LENGTH_SHORT).show();
				return;
			} else if (obj instanceof UnReplaceData) {
				UnReplaceData unReplaceData = (UnReplaceData) obj;
				types.append(unReplaceData.category);

				actions.append(URLEncoder.encode(unReplaceData.subCategory.replace(",", ";")));

			}

		}

		hashMap.put(PARAM_ORDERID, mOrderId);
		hashMap.put(PARAM_ENGINEER, Utils.getEnginnerInfo(this).EngineerNumber);
		hashMap.put(PARAM_SPTYPES, types.toString());
		hashMap.put(PARAM_ACTIONS, actions.toString());
		showDialog(DLG_SENDING);
		mBtnSubmit.setEnabled(false);
		mLoadId = CALLBACK_SUBMIT_UNREPLACEDATA;
		mHttpHelper.load(URL_UNCHANGE_ADD, mCallBack, hashMap, this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Dialog onCreateDialog(int id) {
		Dialog result = null;
		switch (id) {
		case DLG_UNREPLACE_SPTYPE: {

			final Dialog dialog = new Dialog(this, R.style.dialog);

			View inflate = mInflater.inflate(R.layout.dialog, null);

			TextView dialogtitle = (TextView) inflate.findViewById(R.id.tv_title);
			dialogtitle.setVisibility(View.VISIBLE);
			dialogtitle.setText("部件大类/动作");

			ListView findViewById = (ListView) inflate.findViewById(android.R.id.list);

			findViewById.setAdapter(new ArrayAdapter(this, R.layout.item_lv_diaog, mMatelialArray));

			findViewById.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {

					String selected = mMatelialArray[paramInt];
					mSptypeTv.setText(selected);
					MatelialClass matelialClass = mMatelialClasses.get(paramInt);
					Object obj = mSptypeTv.getTag();
					MatelialClass tmp = null;
					if (obj != null && obj instanceof MatelialClass) {
						tmp = (MatelialClass) obj;
					}
					if (tmp == null || !tmp.equals(matelialClass)) {
						mSptypeTv.setTag(matelialClass);
						mActionTv.setText("");
						mActionTv.setTag(null);
						mSptypeView.setTag(null);
					}
					dialog.dismiss();
				}
			});

			dialog.setContentView(inflate);
			dialog.setCanceledOnTouchOutside(true);
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();

			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.height = (int) (display.getHeight() * 0.5); // 高度设置为屏幕的0.8
			lp.width = (int) (LayoutParams.MATCH_PARENT);
			dialog.getWindow().setGravity(Gravity.BOTTOM);
			dialog.getWindow().setAttributes(lp);
			result = dialog;
		}
			break;
		default:
			result = super.onCreateDialog(id);
			break;
		}
		return result;
	}

	private CheckBox mAllCheckBox;
	private ApiDataAdapter<SearchCategory> mSearchAdapter;

	private Dialog createSubDialog() {
		Activity activity = getRootActivity(this);

		final Dialog dialog = new Dialog(activity, R.style.dialog);

		View view = getLayoutInflater().inflate(R.layout.box_category, null);
		mAllCheckBox = (CheckBox) view.findViewById(R.id.cb_all);
		mAllCheckBox.setOnClickListener(this);
		TextView titleTv = (TextView) view.findViewById(R.id.cate_title);
		titleTv.setText("动作/任务");
		ListView listView = (ListView) view.findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		listView.setAdapter(mSearchAdapter);
		dialog.setContentView(view);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(true);
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.height = (int) (LayoutParams.WRAP_CONTENT); // 高度设置为屏幕的0.8
		lp.width = (int) (LayoutParams.MATCH_PARENT);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.getWindow().setAttributes(lp);
		view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View paramView) {

				List<SearchCategory> categories = mSearchAdapter.getSelectData();
				StringBuilder sBuilder = new StringBuilder();
				StringBuilder sIdBuilder = new StringBuilder();

				if (categories != null) {
					for (SearchCategory sc : categories) {
						sBuilder.append(sc.sName).append(",");
						sIdBuilder.append(sc.sId).append(",");
					}
					if (categories.isEmpty())
						categories = null;
				}
				if (!TextUtils.isEmpty(sBuilder)) {
					sBuilder.deleteCharAt(sBuilder.length() - 1);
					sIdBuilder.deleteCharAt(sIdBuilder.length() - 1);
				}
				mActionTv.setText(sBuilder);
				mActionTv.setTag(categories);
				if (categories != null && !categories.isEmpty()) {
					UnReplaceData unReplaceData = new UnReplaceData(mMatelialClass.SPType, sBuilder.toString());
					mUnReplaceView.setTag(unReplaceData);
				} else {
					mUnReplaceView.setTag(null);
				}

				dialog.dismiss();

			}

		});
		List<SearchCategory> materialList = mSearchAdapter.getSelectData();
		mAllCheckBox.setChecked(materialList.size() == mSearchAdapter.getCount());
		return dialog;
	}

	private void addReplaceView() {
		final View view = mInflater.inflate(R.layout.replace, null);
		view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mReplaceLayout.removeView(view);
			}
		});
		mReplaceLayout.addView(view);
	}

	private TextView mSptypeTv;
	private TextView mActionTv;
	private static final int DLG_UNREPLACE_SPTYPE = 6001;
	private View mSptypeView;
	private View mUnReplaceView;
	private MatelialClass mMatelialClass;

	private void addUnReplaceView(Unchange unchange) {
		mUnReplaceLayout.setVisibility(View.VISIBLE);

		mBtnSubmit.setVisibility(View.VISIBLE);

		if (mCcsend == 1) {

			if (mSparePartsExtList.isEmpty()) {
				findViewById(R.id.all_layout).setVisibility(View.GONE);
				findViewById(R.id.tv_NoUnreplace).setVisibility(View.VISIBLE);
				mBtnSubmit.setVisibility(View.GONE);

			} else {

				LinearLayout lay_addUnreplace = (LinearLayout) findViewById(R.id.lay_ccsend);
				lay_addUnreplace.removeAllViews();
				for (SparePartsExt item : mSparePartsExtList) {

					View inflate = mInflater.inflate(R.layout.item_add_unreplace, null);
					TextView category = (TextView) inflate.findViewById(R.id.unreplace_category);
					TextView action = (TextView) inflate.findViewById(R.id.unreplace_action);
					category.setText(item.SPTypeDesc);
					action.setText(item.Action);

					lay_addUnreplace.addView(inflate);
				}
			}
		} else if (mCcsend == 2) {
			findViewById(R.id.tv_NoUnreplace).setVisibility(View.VISIBLE);

			mUnReplaceLayout.setVisibility(View.GONE);

			mBtnSubmit.setVisibility(View.GONE);

		} else

		{
			View actView = findViewById(R.id.un_btn_action);
			mUnReplaceView = findViewById(R.id.un_btn_action);
			if (unchange == null)
				actView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// View actionView = (View) v.getTag();

						View sptypeTv = findViewById(R.id.unreplace_category);
						Object obj = sptypeTv.getTag();
						if (obj != null && obj instanceof MatelialClass) {
							MatelialClass matelialClass = (MatelialClass) obj;
							mMatelialClass = matelialClass;
							mActionList = matelialClass.getArrayData(Action.class);
							if (mActionList != null) {
								mActionTv = (TextView) v.findViewById(R.id.unreplace_action);

								List<SearchCategory> categories = new ArrayList<SearchCategory>();
								for (int i = 0; i < mActionList.size(); i++) {
									Action action = mActionList.get(i);
									categories.add(new SearchCategory(action.Code, action.Value));
								}
								mSearchAdapter = new ApiDataAdapter<SearchCategory>(UnReplaceActivity.this, true);
								// List<SearchCategory> materialList =
								// mSearchAdapter.getSelectData();
								// materialList.addAll(categories);
								Object o = mActionTv.getTag();
								mSearchAdapter.getSelectData().clear();
								if (o != null) {
									List<SearchCategory> searchCategories = (List<SearchCategory>) o;
									if (searchCategories != null) {
										for (SearchCategory category : searchCategories) {
											for (SearchCategory s : categories)
												if (category.sId.equals(s.sId) && category.sName.equals(s.sName)) {
													mSearchAdapter.getSelectData().add(s);
												}
										}
									}
								}
								mSearchAdapter.add(categories);
								createSubDialog().show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "尚未选择部件大类", Toast.LENGTH_SHORT).show();
						}
					}
				});
			if (unchange != null) {
				View inflate = mInflater.inflate(R.layout.unreplace_replaced, null);
				((TextView) inflate.findViewById(R.id.replace_category)).setText(unchange.SPTypeDesc);

				if (unchange.ActionDesc.contains("&nbsp;")) {

					unchange.ActionDesc = unchange.ActionDesc.replace("&nbsp;", "");

				}
				((TextView) inflate.findViewById(R.id.replace_action)).setText(unchange.ActionDesc);

				View btn_unrepl_delete = inflate.findViewById(R.id.btn_unrepl_delete);

				btn_unrepl_delete.setTag(unchange);

				btn_unrepl_delete.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View paramView) {

						mDelReplaceView = (View) paramView.getParent();
						deleteUnreplace((Unchange) paramView.getTag());

					}
				});
				mReplaceLayout.addView(inflate);
			}
			if (unchange == null) {
				if (mTipView != null) {
					mUnReplaceLayout.removeView(mTipView);
				}
				mBtnSubmit.setVisibility(View.VISIBLE);
				mBtnSubmit.setOnClickListener(this);
			}
		}
	}

	boolean ccsendflag = false;
	private View mDelReplaceView;

	private void deleteUnreplace(Unchange unchange) {
		showDialog(DLG_SENDING);
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put(PARAM_ORDERID, mOrderId);
		hashMap.put(PARAM_ENGINEER, Utils.getEnginnerInfo(this).EngineerNumber);
		hashMap.put(PARAM_UNCHANGEINFOID, unchange.ID);

		mLoadId = CALLBACK_DELETE_UNREPLACEDATA;
		mHttpHelper.load(URL_UNCHANGE_ADD, mCallBack, hashMap, this);
	}

	private View mBtnSubmit;

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
		int count = mUnReplaceLayout.getChildCount();
		if (mUnchanges != null && !mUnchanges.isEmpty())
			if (count - 1 > mUnchanges.size())
				result = false;
		return result;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.replace_layout;
	}

	private List<MatelialClass> mMatelialClasses;
	private String[] mMatelialArray;
	private List<Action> mActionList;
	private List<Unchange> mUnchanges;
	private View mTipView;
	private ArrayList<SparePartsExt> mSparePartsExtList;
	private int mCcsend;
	int ii = 0;

	OkHttpStringCallback mCallBack = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {
			dismisDialog(DLG_SENDING);
			boolean safe = mHttpHelper.isSuccessResult(result, UnReplaceActivity.this);

			if (!safe) {
				mBtnSubmit.setEnabled(true);

				if (mLoadId == CALLBACK_LOADUNREPLACE)
					showProgress(mWaitView, false);
				return;
			}
			switch (mLoadId) {
			case CALLBACK_LOADREPLACE:
				mWaitView.setVisibility(View.GONE);
				mReplaceEdit = getIntent().getBooleanExtra("replace", false);
				mUnReplaceEdit = getIntent().getBooleanExtra("unreplace", false);

				break;
			case CALLBACK_LOADUNREPLACE:
				mBtnAddView.setVisibility(View.GONE);
				mWaitView.setVisibility(View.GONE);
				RootData rootData = new RootData(result);
				mUnchanges = rootData.getArrayData(Unchange.class);

				if (mUnchanges != null && !mUnchanges.isEmpty()) {
					mReplaceLayout.setVisibility(View.VISIBLE);
					for (Unchange unchange : mUnchanges) {

						addUnReplaceView(unchange);
					}
				} else {
					mReplaceLayout.setVisibility(View.GONE);
					// mTipView =
					// getLayoutInflater().inflate(R.layout.tip_layout,
					// null);
					// TextView tipTv = (TextView)
					// mTipView.findViewById(R.id.tip);
					// tipTv.setText("当前工单没有相应非换件信息");
					// mUnReplaceLayout.addView(mTipView,
					// mUnReplaceLayout.getChildCount() - 1);
				}
				mUnReplaceLayout.setVisibility(View.VISIBLE);
				mMatelialClasses = rootData.getArrayData(MatelialClass.class);
				Gson gson = new Gson();
				mSparePartsExtList = new ArrayList<SparePartsExt>();
				try {
					mCcsend = rootData.getJson().getInt("CCSend");
					JSONArray jsonArray = rootData.getJson().getJSONArray("SparePartsExt");

					for (int i = 0; i < jsonArray.length(); i++) {
						String string = jsonArray.get(i).toString();
						SparePartsExt SparePartsExt = gson.fromJson(string, SparePartsExt.class);

						if (SparePartsExt.Action.contains("&nbsp;")) {

							SparePartsExt.Action = SparePartsExt.Action.replace("&nbsp;", "");

						}
						mSparePartsExtList.add(SparePartsExt);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (mCcsend != 0) {

					addUnReplaceView(null);
				}

				if (mMatelialClasses != null) {
					mMatelialArray = new String[mMatelialClasses.size()];
					for (int i = 0; i < mMatelialClasses.size(); i++) {
						MatelialClass matelialClass = mMatelialClasses.get(i);
						mMatelialArray[i] = matelialClass.SPTypeDesc;
					}
				}
				break;
			case CALLBACK_SUBMIT_UNREPLACEDATA:
				// Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
				Utils.showToast(UnReplaceActivity.this, "添加成功");
				ccsendflag = false;
				finish();

				break;
			case CALLBACK_DELETE_UNREPLACEDATA:
				if (mUnReplaceLayout != null)
					mReplaceLayout.removeView(mDelReplaceView);
				// Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();

				Utils.showToast(UnReplaceActivity.this, "删除成功");

				break;
			default:
				break;
			}
		}
	};

	public class UnReplaceData {
		public String category;
		public String subCategory;

		public UnReplaceData(String category, String subCategory) {
			this.category = category;
			this.subCategory = subCategory;
		}
	}

	@Override
	public void finish() {

		mCcsend = -1;

		mHttpHelper.cancle(UnReplaceActivity.class);
		// if (mCacheManager != null)
		// mCacheManager.cancel(this);
		super.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object object = parent.getItemAtPosition(position);
		if (object instanceof SearchCategory) {
			SearchCategory category = (SearchCategory) object;
			List<SearchCategory> materialList = mSearchAdapter.getSelectData();
			if (materialList.contains(category)) {
				materialList.remove(category);
			} else {
				materialList.add(category);
			}
			mAllCheckBox.setChecked(materialList.size() == mSearchAdapter.getCount());
			mSearchAdapter.notifyDataSetChanged();
		}

	}
}
