package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.platform.comapi.map.r;
import com.foreveross.cache.CacheManager;
import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.Netpath;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.R.id;
import com.lenovo.sdimobileclient.api.Machine;
import com.lenovo.sdimobileclient.api.Material;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.api.Warranty;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.utility.Utils;
import com.lenovo.sdimobilecllient.camer.MipcaActivityCapture;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主机及保修查询
 * 
 * @author zhangshaofang
 * 
 */
public class HostSearchActivity extends RootActivity implements OnItemClickListener, TextWatcher {

	public enum SearchType {
		HOST, BOX
	}

	private static final int CALLBACK_SEARCH_HOST = 1;
	private static final int CALLBACK_SEARCH_BOX = 2;
	private View mActivonView;
	private SearchType mSearchType = SearchType.HOST;
	private OkHttpHelper mHttpHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharepreferecence = getSharedPreferences("SDImobile", Activity.MODE_PRIVATE);
		mEditor = mSharepreferecence.edit();
		mHttpHelper = OkHttpHelper.getInstance(this);
		initView();
		initData();
		hideInputMethod();
	}

	private void putString(String string) {

		mEditor.putString(string, "ProductNo" + System.currentTimeMillis()).commit();

	}

	private Map<String, String> getAllValue() {

		return (Map<String, String>) mSharepreferecence.getAll();

	}

	private EditText mSearchTv;
	private ApiDataAdapter<Material> mMaterialAdapter;
	// private View mBtnFilter;
	private LayoutInflater mInflater;
	private View mWarrInfoView;
	private View mBtnSearch;
	private View mScanHost;
	private boolean isVisibility;
	private EditText mSearchSectionTv;
	private View mScanSection;
	private String mCectioncode;
	private View mBtnFilter;
	private DrawerLayout mDrawerLayout;
	private CheckBox mAllCheckBox;

	private void initView() {
		mMaterCategoryAdapter = new ApiDataAdapter<Material>(this, true);
		initBackBtn();

		mLayAllCheck = findViewById(R.id.lay_cb_all);
		mAllCheckBox = (CheckBox) findViewById(R.id.cb_all);
		mLayAllCheck.setOnClickListener(this);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

		findViewById(R.id.cancle).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				mDrawerLayout.closeDrawer(GravityCompat.END);
			}
		});

		findViewById(R.id.confirm).setOnClickListener(this);

		mInflater = getLayoutInflater();
		mSeachHost = findViewById(R.id.btn_search_host);
		mScanHost = findViewById(R.id.btn_scan_host);
		mScanHost.setOnClickListener(this);
		mScanSection = findViewById(R.id.btn_scan_section);
		mScanSection.setOnClickListener(this);
		mWarinfoLayout = (LinearLayout) findViewById(R.id.warinfo_layout);
		mWarrInfoView = findViewById(R.id.warinfo_view);
		mSearchTv = (EditText) findViewById(R.id.et_search);
		mSearchSectionTv = (EditText) findViewById(R.id.et_search_section);
		mSearchTv.addTextChangedListener(this);

		mBtnHostinfo = (Button) findViewById(R.id.btn_host_info);
		mBtnHostinfo.setOnClickListener(this);

		mBtnBox = (Button) findViewById(R.id.btn_box);
		mBtnBox.setOnClickListener(this);

		mBtnCheckBox = (Button) findViewById(R.id.btn_check_box);
		mBtnCheckBox.setOnClickListener(this);

		mBtnCheck = findViewById(R.id.btn_check);
		mBtnCheck.setOnClickListener(this);
		mActivonView = findViewById(R.id.action_view);
		findViewById(R.id.rl_war).setOnClickListener(this);
		mCheckBox = findViewById(R.id.ll2);
		mBtnSearch = findViewById(R.id.btn_search_host);
		mBtnSearch.setOnClickListener(this);

		mBtnFilter = findViewById(R.id.btn_filter_box);
		mBtnFilter.setOnClickListener(this);

		ListView listView = (ListView) findViewById(android.R.id.list);
		mMaterialAdapter = new ApiDataAdapter<Material>(this);
		listView.setAdapter(mMaterialAdapter);
		mMyList = (ListView) findViewById(R.id.my_list);
		mProNo = new ArrayList<String>();
		Map<String, String> strings = getAllValue();
		for (Map.Entry<String, String> entry : strings.entrySet()) {
			mProNo.add(entry.getKey());
		}
		arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_textview);
		mMyList.setAdapter(arrayAdapter);
		mMyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {

				mSearchTv.setText(mFinalProNo.get(paramInt));
			}
		});
	}

	/**
	 * 初始化数据，检查是否传有主机序列号，有则直接搜索
	 */
	private void initData() {
		int type = getIntent().getIntExtra("type", 0);
		if (type != 0) {
			mSearchType = SearchType.BOX;
			onClick(findViewById(R.id.btn_box));
		}

		String host_id = getIntent().getStringExtra("host_id");
		if (!TextUtils.isEmpty(host_id)) {
			mSearchTv.setText(host_id);
			mSearchTv.setSelection(host_id.length());
			doSearch(mSearchType, mSearchTv.getText().toString());
		}

		String stringExtra = getIntent().getStringExtra("result");

		if (TextUtils.equals(stringExtra, "doscan")) {
			onClick(findViewById(R.id.btn_scan_host));
		}

	}

	/**
	 * 隐藏键盘
	 */
	public void hideInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm != null) {
			// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			imm.hideSoftInputFromWindow(mSearchTv.getWindowToken(), 0);
		}
	}

	private static final int BOX_FILTER = 10090;
	private static final int REQUEST_CODE_CONTACED_HOST = 5001;
	private static final int REQUEST_CODE_CONTACED_SECTION = 5002;

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

		case R.id.confirm:

		{
			List<Material> materialList = mMaterCategoryAdapter.getSelectData();
			List<Material> tmpList = new ArrayList<Material>();
			for (Material material : mMaterialList) {
				for (Material m : materialList) {
					if (m.MaterialCategory.equals(material.MaterialCategory)) {
						tmpList.add(material);
					}
				}
			}
			mMaterialAdapter.clear();
			mMaterialAdapter.add(tmpList);

		}
			mDrawerLayout.closeDrawer(GravityCompat.END);

			break;
		case R.id.btn_filter_box:
			showdrawer();

			// showDialog(BOX_FILTER);
			break;
		case R.id.btn_search_host:
			doSearch(mSearchType, mSearchTv.getText().toString());
			break;
		case R.id.rl_war:
			ImageView imv_war_info = (ImageView) findViewById(R.id.imv_war_info);
			// findViewById(R.id.rl_war).setVisibility(View.GONE);
			// v.setVisibility(View.GONE);
			if (!isVisibility) {
				mWarrInfoView.setVisibility(View.VISIBLE);

				imv_war_info.setImageResource(R.drawable.rettop);
				isVisibility = true;
			} else {
				mWarrInfoView.setVisibility(View.GONE);
				imv_war_info.setImageResource(R.drawable.ext);
				isVisibility = false;
			}
			break;

		case R.id.lay_cb_all: {

			mAllCheckBox.setChecked(!mAllCheckBox.isChecked());

			if (mAllCheckBox.isChecked()) {
				List<Material> materialList = mMaterCategoryAdapter.getSelectData();
				materialList.clear();
				materialList.addAll(mOriSelectedList);
			} else {
				List<Material> materialList = mMaterCategoryAdapter.getSelectData();
				materialList.clear();
			}
			mMaterCategoryAdapter.notifyDataSetChanged();
		}
			break;
		case R.id.btn_host_info:

			mBtnHostinfo.setTextColor(getResources().getColor(R.color.blue));
			mBtnBox.setTextColor(getResources().getColor(R.color.black));
			mBtnCheckBox.setTextColor(getResources().getColor(R.color.black));

			findViewById(R.id.check_result).setVisibility(View.GONE);
			mSearchTv.setHint("输入序列号");
			mSearchType = SearchType.HOST;
			findViewById(R.id.host_info).setVisibility(View.VISIBLE);
			findViewById(R.id.box_info).setVisibility(View.GONE);
			mActivonView.setBackgroundResource(R.drawable.new_tab3_01);
			// mBtnFilter.setTag(mBtnFilter.getVisibility());
			mSeachHost.setVisibility(View.VISIBLE);
			mBtnCheck.setVisibility(View.GONE);
			mScanHost.setVisibility(View.GONE);
			// mBtnFilter.setVisibility(View.GONE);
			mCheckBox.setVisibility(View.GONE);
			break;
		case R.id.btn_box:
			mBtnHostinfo.setTextColor(getResources().getColor(R.color.black));
			mBtnBox.setTextColor(getResources().getColor(R.color.blue));
			mBtnCheckBox.setTextColor(getResources().getColor(R.color.black));
			findViewById(R.id.check_result).setVisibility(View.GONE);
			mSearchTv.setHint("输入或扫描主机号");
			mSeachHost.setVisibility(View.VISIBLE);
			mScanHost.setVisibility(View.VISIBLE);
			// Object vis = mBtnFilter.getTag();
			// if (vis != null) {
			// int visibility = (Integer) vis;
			// mBtnFilter.setVisibility(visibility);
			// }
			mSearchType = SearchType.BOX;

			findViewById(R.id.box_info).setVisibility(View.VISIBLE);
			findViewById(R.id.host_info).setVisibility(View.GONE);
			mCheckBox.setVisibility(View.GONE);
			mBtnCheck.setVisibility(View.GONE);
			mActivonView.setBackgroundResource(R.drawable.new_tab3_02);
			break;
		case R.id.btn_check_box:

			mBtnHostinfo.setTextColor(getResources().getColor(R.color.black));
			mBtnBox.setTextColor(getResources().getColor(R.color.black));
			mBtnCheckBox.setTextColor(getResources().getColor(R.color.blue));

			findViewById(R.id.host_info).setVisibility(View.GONE);
			findViewById(R.id.box_info).setVisibility(View.GONE);
			mSearchTv.setHint("输入或扫描主机号");

			mScanHost.setVisibility(View.VISIBLE);
			mBtnCheck.setVisibility(View.VISIBLE);
			mBtnSearch.setVisibility(View.GONE);
			mCheckBox.setVisibility(View.VISIBLE);
			mActivonView.setBackgroundResource(R.drawable.new_tab3_03);
			break;
		case R.id.btn_check:
			if (TextUtils.isEmpty(mSearchTv.getText().toString().trim()) || TextUtils.isEmpty(mSearchSectionTv.getText().toString().trim())) {
				Utils.showToast(this, "主机号或部件条码为空");
			} else {
				checkHostCode(mSearchTv.getText().toString().trim(), mSearchSectionTv.getText().toString().trim());
			}

			break;
		case R.id.btn_scan_host:
			scanCode(REQUEST_CODE_CONTACED_HOST, "扫描主机条码");
			break;
		case R.id.btn_scan_section:
			scanCode(REQUEST_CODE_CONTACED_SECTION, "扫描部件条码");
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	/**
	 * 调用摄像头 二维码
	 */
	public void scanCode(int requestCode, String title) {
		Intent intent = new Intent(this, MipcaActivityCapture.class);
		intent.putExtra("title", title);
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 二维码扫描返回
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CONTACED_HOST:

			if (resultCode == RESULT_OK && data != null) {
				String code = data.getStringExtra("code");
				mSearchTv.setText(code);

			}
			break;
		case REQUEST_CODE_CONTACED_SECTION:

			if (resultCode == RESULT_OK && data != null) {
				String code = data.getStringExtra("code");
				mSearchSectionTv.setText(code);

			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	/**
	 * 检查主机号和装箱单是否匹配
	 * 
	 * @param string2
	 * @param string
	 */
	private void checkHostCode(String hostcode, String sectioncode) {
		// TODO
		mCectioncode = sectioncode;
		flag = true;
		doSearch(SearchType.BOX, hostcode);
		doSearch(SearchType.HOST, hostcode);

	}

	/**
	 * show check result
	 * 
	 * @param sectioncode
	 */
	private void show(String sectioncode) {
		flag = false;
		if (mMaterialList == null || mMaterialList.size() == 0) {

			Utils.showToast(this, "主机号输入不正确");

		} else {

			for (Material i : mMaterialList) {

				if (i != null && i.MaterialCode.equalsIgnoreCase(sectioncode)) {

					LinearLayout check_result = (LinearLayout) findViewById(R.id.check_result);
					check_result.setVisibility(View.VISIBLE);
					check_result.removeAllViews();

					TextView tv_result = (TextView) findViewById(R.id.tv_result);
					if (tv_result != null) {
						tv_result.setVisibility(View.GONE);
					}

					LinearLayout host_item = (LinearLayout) mInflater.inflate(R.layout.check_host_item, null);

					TextView noTv = (TextView) host_item.findViewById(R.id.tv_product_no);
					TextView nameTv = (TextView) host_item.findViewById(R.id.tv_product_name);
					TextView dateTv = (TextView) host_item.findViewById(R.id.tv_product_date);
					noTv.setText(mHostinfo.ProductNo);
					putString(mHostinfo.ProductNo);
					nameTv.setText(mHostinfo.MachineName);
					dateTv.setText(mHostinfo.ProductDate);
					((TextView) host_item.findViewById(R.id.cate_name)).setText(i.MaterialCategory);
					((TextView) host_item.findViewById(R.id.part_num)).setText(i.MaterialID);
					((TextView) host_item.findViewById(R.id.part_code)).setText(i.MaterialCode);
					((TextView) host_item.findViewById(R.id.part_name)).setText(i.MaterialName);
					host_item.setVisibility(View.VISIBLE);

					check_result.addView(host_item);

					return;
				}

			}
			findViewById(R.id.check_result).setVisibility(View.VISIBLE);
			TextView tv_result = (TextView) findViewById(R.id.tv_result);
			tv_result.setText("部件与主机不匹配");

		}
	}

	private ApiDataAdapter<Material> mMaterCategoryAdapter;

	private View mLayAllCheck;

	private void showdrawer() {

		ListView listView = (ListView) findViewById(R.id.drawer_filter_list);
		listView.setOnItemClickListener(this);

		listView.setAdapter(mMaterCategoryAdapter);

		mDrawerLayout.openDrawer(GravityCompat.END);

	}

	/**
	 * 弹出分类过滤对话框
	 */
	@Override
	public Dialog onCreateDialog(int id) {
		Dialog result = null;
		switch (id) {
		case BOX_FILTER:

			AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
			View view = getLayoutInflater().inflate(R.layout.box_category, null);
			view.findViewById(R.id.btn_next).setVisibility(View.GONE);
			mLayAllCheck = (CheckBox) view.findViewById(R.id.cb_all);

			// mAllCheckBox.setOnCheckedChangeListener(mChangeListener);
			mLayAllCheck.setOnClickListener(this);
			ListView listView = (ListView) view.findViewById(android.R.id.list);
			listView.setOnItemClickListener(this);

			listView.setAdapter(mMaterCategoryAdapter);
			builder.setView(view);
			AlertDialog dialog = builder.create();
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, getText(R.string.btn_sure), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					List<Material> materialList = mMaterCategoryAdapter.getSelectData();
					List<Material> tmpList = new ArrayList<Material>();
					for (Material material : mMaterialList) {
						for (Material m : materialList) {
							if (m.MaterialCategory.equals(material.MaterialCategory)) {
								tmpList.add(material);
							}
						}
					}
					mMaterialAdapter.clear();
					mMaterialAdapter.add(tmpList);
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

	/**
	 * 对话框，过滤数据初始化
	 */
	@Override
	public void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case BOX_FILTER:
			List<Material> materialList = mMaterCategoryAdapter.getSelectData();

			mAllCheckBox.setChecked(materialList.size() == mOriSelectedList.size());
			break;
		default:
			super.onPrepareDialog(id, dialog);
			break;
		}

	}

	/**
	 * 搜索
	 * 
	 * @param type
	 *            类型
	 * @param keyword
	 *            主机号
	 */
	private void doSearch(SearchType type, String keyword) {
		mMyList.setVisibility(View.GONE);

		if (TextUtils.isEmpty(keyword)) {
			Toast.makeText(this, "主机编号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		showDialog(DLG_DATA_LOADING);
		hideInputMethod();
		String url = null;

		switch (type) {
		case HOST:
			callbackId = CALLBACK_SEARCH_HOST;
			url = URL_SEARCHHOST;
			findViewById(R.id.machine_layout).setVisibility(View.GONE);
			findViewById(R.id.rl_war).setVisibility(View.GONE);
			mWarrInfoView.setVisibility(View.GONE);
			break;
		case BOX:
			mMaterialAdapter.clear();
			// mBtnFilter.setVisibility(View.GONE);
			url = URL_SEARCHBOX;
			callbackId = CALLBACK_SEARCH_BOX;
			break;
		}
		Uri uri = Uri.parse(url).buildUpon().appendQueryParameter(PARAM_PRODUCTNO, keyword).build();

		mHttpHelper.load(uri.toString(), mCallback, this);

		// NetworkPath path = new Netpath(uri.toString());
		// CacheParams params = new CacheParams(path);
		// mCacheManager.load(callbackId, params, this);
	}

	OkHttpStringCallback mCallback = new OkHttpStringCallback(this) {

		@Override
		public void onResponse(String result) {
			dismisDialog(DLG_DATA_LOADING);
			boolean safe = mHttpHelper.isSuccessResult(result, HostSearchActivity.this);
			if (!safe) {
				return;
			}

			switch (callbackId) {
			case CALLBACK_SEARCH_HOST: {

				RootData rootData = new RootData(result);
				Machine hostInfo = rootData.getData(Machine.class);

				mHostinfo = hostInfo;
				if (hostInfo != null) {
					if (!flag) {
						initView(hostInfo);
					}
				} else {

					Utils.showToast(HostSearchActivity.this, "未找到该主机信息");
					// Toast.makeText(this, "未找到该主机信息",
					// Toast.LENGTH_LONG).show();
				}
				List<Warranty> warrantyList = rootData.getArrayData(Warranty.class);
				if (warrantyList != null) {

					if (!flag) {
						initView(warrantyList);

					} else {
						check_result++;
					}

				} else {
					findViewById(R.id.rl_war).setVisibility(View.GONE);
				}

				if (check_result == 1) {

					show(mCectioncode);
					check_result = -1;
				}

			}
				break;
			case CALLBACK_SEARCH_BOX: {

				findViewById(R.id.layout_filter_box).setVisibility(View.VISIBLE);
				RootData rootData = (RootData) new RootData(result);
				mMaterialList = rootData.getArrayData(Material.class);
				if (mMaterialList != null && !mMaterialList.isEmpty()) {
					// mBtnFilter.setVisibility(View.VISIBLE);
					mMaterialAdapter.clear();
					Map<String, List<Material>> marterialMap = new HashMap<String, List<Material>>();
					List<Material> tmpList = new ArrayList<Material>();
					for (Material material : mMaterialList) {
						if (marterialMap.containsKey(material.MaterialCategory)) {
							List<Material> materials = marterialMap.get(material.MaterialCategory);
							material.spitvis = false;
							materials.add(material);
						} else {
							List<Material> materials = new ArrayList<Material>();
							material.spitvis = true;
							materials.add(material);
							tmpList.add(material);
							marterialMap.put(material.MaterialCategory, materials);
						}
					}

					if (!flag) {
						mOriSelectedList = tmpList;
						mMaterialAdapter.add(mMaterialList);
						List<Material> materialList = mMaterCategoryAdapter.getSelectData();
						materialList.clear();
						materialList.addAll(mOriSelectedList);
						mMaterCategoryAdapter.clear();

						mMaterCategoryAdapter.add(materialList);
					} else {
						check_result++;
					}
				} else {
					Utils.showToast(HostSearchActivity.this, "未找到该装箱单信息");
					// Toast.makeText(this, "未找到该装箱单信息",
					// Toast.LENGTH_LONG).show();
				}

				if (check_result == 1) {

					show(mCectioncode);
					check_result = -1;
				}

			}
				break;
			}
		}
	};

	/**
	 * 初始化主机界面
	 * 
	 * @param hostInfo
	 *            主机实体
	 */
	private LinearLayout mWarinfoLayout;

	private void initView(Machine hostInfo) {

		findViewById(R.id.machine_layout).setVisibility(View.VISIBLE);
		findViewById(R.id.rl_war).setVisibility(View.VISIBLE);
		mWarrInfoView.setVisibility(View.GONE);
		TextView noTv = (TextView) findViewById(R.id.tv_product_no);
		TextView nameTv = (TextView) findViewById(R.id.tv_product_name);
		TextView dateTv = (TextView) findViewById(R.id.tv_product_date);
		TextView scandateTv = (TextView) findViewById(R.id.tv_scandate);
		TextView warTv = (TextView) findViewById(R.id.tv_warranty_endate);
		TextView onsiteTv = (TextView) findViewById(R.id.tv_onsiteendate);
		noTv.setText(hostInfo.ProductNo);
		putString(hostInfo.ProductNo);
		nameTv.setText(hostInfo.MachineName);
		dateTv.setText(hostInfo.ProductDate);
		scandateTv.setText(hostInfo.ScanDate);
		warTv.setText(hostInfo.PartEndDate);
		onsiteTv.setText(hostInfo.OnSiteEndDate);
	}

	@Override
	public void finish() {
		// if (mCacheManager != null)
		// mCacheManager.cancel(this);

		mHttpHelper.cancle(HostSearchActivity.class);
		super.finish();
	}

	/**
	 * 初始化保修信息
	 * 
	 * @param warranty
	 */
	private void initView(List<Warranty> list) {
		mWarinfoLayout.removeAllViews();
		for (Warranty warranty : list) {
			View view = mInflater.inflate(R.layout.warranty, null);
			TextView nameTv = (TextView) view.findViewById(R.id.tv_war_name);
			TextView desTv = (TextView) view.findViewById(R.id.tv_war_des);
			TextView startTv = (TextView) view.findViewById(R.id.tv_war_startdate);
			TextView endTv = (TextView) view.findViewById(R.id.tv_war_enddate);
			TextView siteStartTv = (TextView) view.findViewById(R.id.tv_war_sitestartdate);
			TextView siteEndTv = (TextView) view.findViewById(R.id.tv_war_siteendate);
			nameTv.setText(warranty.WarrantyName);
			desTv.setText(warranty.WarrantyDecription);
			startTv.setText(warranty.PartStartDate);
			endTv.setText(warranty.PartEndDate);
			siteStartTv.setText(warranty.OnSiteStartDate);
			siteEndTv.setText(warranty.OnSiteEndDate);
			mWarinfoLayout.addView(view);
		}

	}

	@Override
	protected int getContentViewId() {
		return R.layout.search_host;
	}

	@Override
	protected void notifyView() {
		showDialog(DLG_DATA_LOADING);
	}

	/**
	 * 装箱单数组
	 */
	private List<Material> mMaterialList;
	private List<Material> mOriSelectedList;
	private View mCheckBox;
	private View mSeachHost;
	private View mBtnCheck;
	private SharedPreferences mSharepreferecence;
	private Editor mEditor;
	private ListView mMyList;
	private ArrayAdapter<String> arrayAdapter;
	private ArrayList<String> mProNo;
	private ArrayList<String> mFinalProNo;
	private Machine mHostinfo;
	private boolean flag = false;;

	private int check_result = -1;
	private Button mBtnHostinfo;
	private Button mBtnBox;
	private Button mBtnCheckBox;
	private int callbackId = 0;

	/*
	 * @Override public void dataLoaded(int id, CacheParams params, ICacheInfo
	 * cacheInfo) { dismisDialog(DLG_DATA_LOADING); boolean safe =
	 * ActivityUtils.prehandleNetworkData(this, this, id, params, cacheInfo,
	 * true); if (!safe) { return; } switch (id) { case CALLBACK_SEARCH_HOST: {
	 * 
	 * RootData rootData = (RootData) cacheInfo.getData(); Machine hostInfo =
	 * rootData.getData(Machine.class);
	 * 
	 * mHostinfo = hostInfo; if (hostInfo != null) { if (!flag) {
	 * initView(hostInfo); } } else { Toast.makeText(this, "未找到该主机信息",
	 * Toast.LENGTH_LONG).show(); } List<Warranty> warrantyList =
	 * rootData.getArrayData(Warranty.class); if (warrantyList != null) {
	 * 
	 * if (!flag) { initView(warrantyList);
	 * 
	 * } else { check_result++; }
	 * 
	 * } else { findViewById(R.id.rl_war).setVisibility(View.GONE); }
	 * 
	 * if (check_result == 1) {
	 * 
	 * show(mCectioncode); check_result = -1; }
	 * 
	 * } break; case CALLBACK_SEARCH_BOX: {
	 * 
	 * findViewById(R.id.layout_filter_box).setVisibility(View.VISIBLE);
	 * RootData rootData = (RootData) cacheInfo.getData(); mMaterialList =
	 * rootData.getArrayData(Material.class); if (mMaterialList != null &&
	 * !mMaterialList.isEmpty()) { // mBtnFilter.setVisibility(View.VISIBLE);
	 * mMaterialAdapter.clear(); Map<String, List<Material>> marterialMap = new
	 * HashMap<String, List<Material>>(); List<Material> tmpList = new
	 * ArrayList<Material>(); for (Material material : mMaterialList) { if
	 * (marterialMap.containsKey(material.MaterialCategory)) { List<Material>
	 * materials = marterialMap.get(material.MaterialCategory); material.spitvis
	 * = false; materials.add(material); } else { List<Material> materials = new
	 * ArrayList<Material>(); material.spitvis = true; materials.add(material);
	 * tmpList.add(material); marterialMap.put(material.MaterialCategory,
	 * materials); } }
	 * 
	 * if (!flag) { mOriSelectedList = tmpList;
	 * mMaterialAdapter.add(mMaterialList); List<Material> materialList =
	 * mMaterCategoryAdapter.getSelectData(); materialList.clear();
	 * materialList.addAll(mOriSelectedList); mMaterCategoryAdapter.clear();
	 * 
	 * mMaterCategoryAdapter.add(materialList); } else { check_result++; } }
	 * else { Toast.makeText(this, "未找到该装箱单信息", Toast.LENGTH_LONG).show(); }
	 * 
	 * if (check_result == 1) {
	 * 
	 * show(mCectioncode); check_result = -1; }
	 * 
	 * } break; } }
	 */

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object object = parent.getItemAtPosition(position);
		if (object instanceof Material) {
			Material material = (Material) object;
			List<Material> materialList = mMaterCategoryAdapter.getSelectData();
			boolean constans = false;
			for (Material m : materialList) {
				if (m.MaterialCategory.equals(material.MaterialCategory)) {
					constans = true;
				}
			}
			if (constans) {
				materialList.remove(material);
			} else {
				materialList.add(material);
			}
			mAllCheckBox.setChecked(materialList.size() == mOriSelectedList.size());
			mMaterCategoryAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
		// TODO Auto-generated method stub

	}

	/**
	 * 内容改变监听
	 */
	@Override
	public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
		if (mProNo != null && mProNo.size() > 0) {
			mMyList.setVisibility(View.VISIBLE);
			mFinalProNo = new ArrayList<String>();

			for (String prono : mProNo) {
				if (paramCharSequence.toString().trim().length() > 0 && prono.contains(paramCharSequence.toString().trim())) {
					mFinalProNo.add(prono);
				}

			}

			if (!(mFinalProNo.size() > 0)) {

				mMyList.setVisibility(View.GONE);
			}
			arrayAdapter.clear();
			arrayAdapter.addAll(mFinalProNo);

			arrayAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void afterTextChanged(Editable paramEditable) {
		// TODO Auto-generated method stub

	}

}
