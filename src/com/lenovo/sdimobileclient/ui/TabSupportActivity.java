package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.cache.CacheManager;
import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.Netpath;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.api.KnowledgeInfo;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.api.SearchCategory;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.utility.Utils;

/**
 * 技术通报查询和其他文章查询
 * 
 * @author zhangshaofang
 * 
 */
public class TabSupportActivity extends RootActivity implements OnItemClickListener, OnScrollListener {

	private enum SearchType {
		KNOWLEDGE, OTHER
	}

	private static final int CALLBACK_SEARCH = 1;
	private ListView mListView;
	private LinearLayout mBtnLayout;
	private TextView mBtnCategory;
	private ApiDataAdapter<KnowledgeInfo> mKnowledgeAdapter;

	private View mWaitView;
	private SearchType mSearchType = SearchType.KNOWLEDGE;
	private EnginnerInfo mEnginnerInfo;
	private TextView mPageIndexTv;
	private ApiDataAdapter<SearchCategory> mSearchAdapter;
	private OkHttpHelper mHttpHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHttpHelper = OkHttpHelper.getInstance(this);
		mHttpHelper.cancle(MainActivity.class);
		mEnginnerInfo = Utils.getEnginnerInfo(this);
		mPageIndexTv = (TextView) findViewById(R.id.tv_pageindex);
		initView();
		hideInputMethod();
		List<SearchCategory> categories = new ArrayList<SearchCategory>();
		categories.add(new SearchCategory("1", "常见问题"));
		categories.add(new SearchCategory("2", "维修案例"));
		categories.add(new SearchCategory("3", "操作指导"));
		mSearchAdapter = new ApiDataAdapter<SearchCategory>(this, true);
		List<SearchCategory> materialList = mSearchAdapter.getSelectData();
		materialList.addAll(categories);
		mSearchAdapter.add(categories);
	}

	private EditText mSearchTv;
	private View mBtnSearch;

	private void initView() {
		mBtnLayout = (LinearLayout) findViewById(R.id.btn_layout);
		mSearchTv = (EditText) findViewById(R.id.et_search);
		mBtnCategory = (TextView) findViewById(R.id.btn_category);
		findViewById(R.id.btn_technology).setOnClickListener(this);
		findViewById(R.id.btn_othertext).setOnClickListener(this);
		mBtnCategory.setOnClickListener(this);
		mBtnSearch = findViewById(R.id.btn_search_product);
		mBtnSearch.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.support_list);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mWaitView = getLayoutInflater().inflate(R.layout.progress, null);
		mWaitView.setVisibility(View.GONE);
		mWaitView.findViewById(R.id.btn_retry).setOnClickListener(this);
		mListView.addFooterView(mWaitView);
		mKnowledgeAdapter = new ApiDataAdapter<KnowledgeInfo>(this);
		mListView.setAdapter(mKnowledgeAdapter);
		String host_id = getIntent().getStringExtra("host_id");
		if (!TextUtils.isEmpty(host_id)) {
			initBackBtn();
			mSearchTv.setText(host_id);
			mSearchTv.setSelection(host_id.length());
			doSearch(mSearchType, mSearchTv.getText().toString());
		}
	}

	/**
	 * 对话框，过滤数据初始化
	 */
	@Override
	public void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case BOX_FILTER:
			List<SearchCategory> materialList = mSearchAdapter.getSelectData();
			mAllCheckBox.setChecked(materialList.size() == mSearchAdapter.getCount());
			break;
		default:
			super.onPrepareDialog(id, dialog);
			break;
		}
	}

	public void hideInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(mSearchTv.getWindowToken(), 0);
		}
	}

	PopupWindow mCategory;

	private void showPopwindou() {

		if (mMenuPop == null) {
			View popwindow = getLayoutInflater().inflate(R.layout.suport_popwindow, null);

			mTv_all = (TextView) popwindow.findViewById(R.id.tv_all);
			mTv_all.setOnClickListener(this);

			mTv_common = (TextView) popwindow.findViewById(R.id.tv_common);
			mTv_common.setOnClickListener(this);

			mTv_direct = (TextView) popwindow.findViewById(R.id.tv_direct);
			mTv_direct.setOnClickListener(this);

			mTv_case = (TextView) popwindow.findViewById(R.id.tv_case);
			mTv_case.setOnClickListener(this);

			mMenuPop = new PopupWindow(popwindow, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mMenuPop.setAnimationStyle(R.style.PopupAnimation);
			mMenuPop.setFocusable(true);
			mMenuPop.setTouchable(true);
			mMenuPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_bg));
			mMenuPop.update();
		}
		if (!mMenuPop.isShowing()) {
			mMenuPop.showAsDropDown(findViewById(R.id.btn_category), 0, 0);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_category:
			// showDialog(BOX_FILTER);
			showPopwindou();
			break;
		case R.id.btn_search_product:
			doSearch(mSearchType, mSearchTv.getText().toString());
			break;
		case R.id.btn_technology: {

			mHasData = false;
			mSearchType = SearchType.KNOWLEDGE;
			mBtnLayout.setBackgroundResource(R.drawable.new_tab_02);
			mBtnCategory.setVisibility(View.GONE);

			mHttpHelper.cancle(TabSpareActivity.class);
			mWaitView.setVisibility(View.GONE);
			mKnowledgeAdapter.clear();
			// mSearchTv.setText("");
			mBtnSearch.setEnabled(true);
			String keyword = mSearchTv.getText().toString();
			if (!TextUtils.isEmpty(keyword)) {
				doSearch(mSearchType, keyword);
			}
		}
			;
			break;
		case R.id.btn_othertext:
			mHasData = false;
			mSearchType = SearchType.OTHER;
			mBtnLayout.setBackgroundResource(R.drawable.new_tab_01);
			mBtnCategory.setVisibility(View.VISIBLE);
			mHttpHelper.cancle(TabSpareActivity.class);
			mKnowledgeAdapter.clear();
			mWaitView.setVisibility(View.GONE);
			mBtnSearch.setEnabled(true);
			String keyword = mSearchTv.getText().toString();
			if (!TextUtils.isEmpty(keyword)) {
				doSearch(mSearchType, keyword);
			}
			mSearchTv.invalidate();
			break;
		case R.id.btn_retry:
			retryLoadData();
			break;

		case R.id.tv_all:
			mBtnCategory.setText(mTv_all.getText());
			mTv_all.setBackgroundColor(getResources().getColor(R.color.popwindow));
			mTv_case.setBackgroundColor(getResources().getColor(R.color.transparent));
			mTv_common.setBackgroundColor(getResources().getColor(R.color.transparent));
			mTv_direct.setBackgroundColor(getResources().getColor(R.color.transparent));
			mMenuPop.dismiss();
			break;
		case R.id.tv_common:
			mBtnCategory.setText(mTv_common.getText());
			mTv_common.setBackgroundColor(getResources().getColor(R.color.popwindow));
			mTv_case.setBackgroundColor(getResources().getColor(R.color.transparent));
			mTv_all.setBackgroundColor(getResources().getColor(R.color.transparent));
			mTv_direct.setBackgroundColor(getResources().getColor(R.color.transparent));
			mMenuPop.dismiss();
			break;
		case R.id.tv_direct:
			mBtnCategory.setText(mTv_direct.getText());
			mTv_direct.setBackgroundColor(getResources().getColor(R.color.popwindow));
			mTv_case.setBackgroundColor(getResources().getColor(R.color.transparent));
			mTv_common.setBackgroundColor(getResources().getColor(R.color.transparent));
			mTv_all.setBackgroundColor(getResources().getColor(R.color.transparent));
			mMenuPop.dismiss();
			break;
		case R.id.tv_case:
			mBtnCategory.setText(mTv_case.getText());
			mTv_case.setBackgroundColor(getResources().getColor(R.color.popwindow));
			mTv_all.setBackgroundColor(getResources().getColor(R.color.transparent));
			mTv_common.setBackgroundColor(getResources().getColor(R.color.transparent));
			mTv_direct.setBackgroundColor(getResources().getColor(R.color.transparent));
			mMenuPop.dismiss();
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
		mBtnSearch.setEnabled(false);
		showProgress(mWaitView, true);
	}

	private Uri mSearchUri;
	private Uri mLastUri;
	private int mLoadID;

	private void doSearch(SearchType type, String productNo) {
		mHttpHelper.cancle(MainActivity.class);
		if (TextUtils.isEmpty(productNo)) {
			Toast.makeText(this, "查询内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mBtnSearch.setEnabled(false);
		hideInputMethod();
		String url = URL_SEARCHOTHER;
		String t = null;
		switch (type) {
		case KNOWLEDGE:
			t = "技术通报";
			break;
		case OTHER:

			String trim = mBtnCategory.getText().toString().trim();

			List<SearchCategory> tmpCategorys = mSearchAdapter.getSelectData();
			if (tmpCategorys == null || tmpCategorys.isEmpty()) {
				tmpCategorys = mSearchAdapter.getmObjects();
			}
			StringBuilder sb = new StringBuilder();
			for (SearchCategory category : tmpCategorys) {
				sb.append(category.sName).append(";");
			}
			sb.deleteCharAt(sb.length() - 1);

			t = TextUtils.equals(trim, "全部") ? sb.toString() : trim;
			break;
		}

		Uri uri = Uri.parse(url).buildUpon().appendQueryParameter(PARAM_PAGECOUNT, String.valueOf(PAGE_COUNT)).build();
		mKnowledgeAdapter.clear();
		showProgress(mWaitView, true);
		mNextId = 0;
		mSearchUri = uri.buildUpon().appendQueryParameter(PARAM_QUERYCONTENT, productNo).appendQueryParameter(PARAM_QUERYTYPE, t)
				.appendQueryParameter(PARAM_ENGINEER, mEnginnerInfo.EngineerNumber).build();
		mLastUri = mSearchUri.buildUpon().appendQueryParameter(PARAM_PAGEINDEX, String.valueOf(mNextId)).build();
		// NetworkPath path = new Netpath(mLastUri.toString());
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		mLoadID = CALLBACK_SEARCH;
		mHttpHelper.load(mLastUri.toString(), mCallback, new HashMap<String, String>(), MainActivity.class, this);
		// mCacheManager.load(CALLBACK_SEARCH, params, this);
	}

	private void appendData() {
		showProgress(mWaitView, true);
		mLastUri = mSearchUri.buildUpon().appendQueryParameter(PARAM_PAGEINDEX, String.valueOf(mNextId)).build();
		// NetworkPath path = new Netpath(mLastUri.toString());
		// CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL,
		// path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_SEARCH, params, this);

		mLoadID = CALLBACK_SEARCH;

		mHttpHelper.load(mLastUri.toString(), mCallback, new HashMap<String, String>(), MainActivity.class, this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object object = parent.getItemAtPosition(position);
		if (object instanceof KnowledgeInfo) {
			KnowledgeInfo info = (KnowledgeInfo) object;
			Intent intent = new Intent(this, ProcessInfoActivity.class);
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			String token = Utils.getToken(this);
			Uri uri = Uri.parse(URL_KNOWINFODETAIL).buildUpon().appendQueryParameter(PARAM_KEYCODE, info.KnowledgeID).appendQueryParameter(PARAM_TOKEN, token)
					.appendQueryParameter(PARAM_SOURCEFLAG, SOURCEFLAG).build();
			intent.putExtra("url", uri.toString());
			intent.putExtra("title", "知识详情");
			startActivity(intent);
		} else if (object instanceof SearchCategory) {
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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState != 0 && mTotalNumber != 0) {
			mPageIndexTv.setVisibility(View.VISIBLE);
		} else {
			mPageIndexTv.setVisibility(View.GONE);
		}
	}

	private int mNextId = 0;
	private boolean mSafe;
	private boolean mHasData;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean flag = false;
		if (mLastUri != null) {
			String startId = mLastUri.getQueryParameter(PARAM_PAGEINDEX);
			if (!TextUtils.isEmpty(startId)) {
				int sId = Integer.valueOf(startId);
				if (sId == mNextId) {
					flag = true;
				}
			}
		}
		if (!flag && mSafe && mHasData && view.getLastVisiblePosition() >= (view.getCount() - 2)) {
			appendData();
		}
		int firstPosition = view.getFirstVisiblePosition();
		int lastPosition = view.getLastVisiblePosition();
		float middlePosition = (firstPosition + lastPosition) / 2f;
		int y = mTotalNumber - (mTotalNumber / PAGE_COUNT) * PAGE_COUNT;
		int s = y > 0 ? 1 : 0;
		int allPage = mTotalNumber / PAGE_COUNT + s;
		int p = (firstPosition + 1) / PAGE_COUNT + 1;
		if (middlePosition > p * PAGE_COUNT)
			p += 1;
		if (lastPosition == mTotalNumber)
			p = allPage;
		mPageIndexTv.setText(p + "/" + allPage);
	}

	private int mTotalNumber;

	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		protected void LoadError() {
			mBtnSearch.setEnabled(true);
			mWaitView.setVisibility(View.GONE);
		}

		@Override
		public void onResponse(String result) {
			mBtnSearch.setEnabled(true);
			mSafe = mHttpHelper.isSuccessResult(result, TabSupportActivity.this);

			if (!mSafe) {
				showProgress(mWaitView, false);
				return;
			}
			switch (mLoadID) {
			case CALLBACK_SEARCH:
				mWaitView.setVisibility(View.GONE);
				RootData rootData = new RootData(result);
				try {
					String totalNumber = rootData.getJson().getString("TotalNumber");
					mTotalNumber = Integer.parseInt(totalNumber);
					String currentIndex = mLastUri.getQueryParameter(PARAM_PAGEINDEX);
					int index = Integer.parseInt(currentIndex);
					if ((index + 1) * PAGE_COUNT < mTotalNumber) {
						mNextId = ++index;
						mHasData = true;
					} else {
						mNextId = -1;
						mHasData = false;
					}
					List<KnowledgeInfo> knowledgeInfos = rootData.getArrayData(KnowledgeInfo.class);
					if (knowledgeInfos != null)
						mKnowledgeAdapter.add(knowledgeInfos);
					if (knowledgeInfos == null && index == 1 || mTotalNumber == 0) {

						Utils.showToast(TabSupportActivity.this, "未找到相关内容");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;

			default:
				break;
			}
		}
	};

	private static final int BOX_FILTER = 11090;
	private CheckBox mAllCheckBox;
	private TextView mTv_all;
	private TextView mTv_common;
	private TextView mTv_direct;
	private TextView mTv_case;

	/**
	 * 弹出分类过滤对话框
	 */
	@Override
	public Dialog onCreateDialog(int id) {
		Dialog result = null;
		switch (id) {
		case BOX_FILTER:
			Activity activity = getRootActivity(this);
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			View view = getLayoutInflater().inflate(R.layout.box_category, null);
			mAllCheckBox = (CheckBox) view.findViewById(R.id.cb_all);
			// mAllCheckBox.setOnCheckedChangeListener(mChangeListener);
			mAllCheckBox.setOnClickListener(this);
			TextView titleTv = (TextView) view.findViewById(R.id.cate_title);
			titleTv.setText("搜索分类");
			ListView listView = (ListView) view.findViewById(android.R.id.list);
			listView.setOnItemClickListener(this);
			listView.setAdapter(mSearchAdapter);
			builder.setView(view);
			AlertDialog dialog = builder.create();
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, getText(R.string.btn_sure), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// SearchCategory category =
					// mSearchAdapter.getmSearchCategory();
					dialog.dismiss();
				}

			});
			result = dialog;
			break;
		default:
			result = super.onCreateDialog(id);
			break;
		}
		return result;
	}

	@Override
	public void finish() {
		mHttpHelper.cancle(TabSpareActivity.class);
		super.finish();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.support;
	}
}
