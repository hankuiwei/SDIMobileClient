
package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import com.foreveross.cache.CacheParams;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.Netpath;
import com.foreveross.cache.network.ParamPair;
import com.lenovo.lsf.push.PushSDK;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.EnginnerInfo;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.api.UserAccount;
import com.lenovo.sdimobileclient.data.Account;
import com.lenovo.sdimobileclient.data.Config;
import com.lenovo.sdimobileclient.data.PushConfig;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
import com.lenovo.sdimobileclient.utility.DES;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * 登录
 * 
 * @author zhangshaofang
 * 
 */
public class LoginActivity extends RootActivity implements OnItemClickListener {

	private static final int CALLBACK_LOGIN = 1;

	private EditText mUsernameTv;
	private EditText mPasswordTv;
	private CheckBox mAutoCb;
	private List<UserAccount> mUserAccounts;
	private ApiDataAdapter<UserAccount> mUserAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setTitle(getString(R.string.title_login));

		mOkHeler = mOkHeler.getInstance(this);
		initView();
	}

	/**
	 * 初始化登录界面
	 */
	private void initView() {

		mUsernameTv = (EditText) findViewById(R.id.et_username);
		final View delete = findViewById(R.id.btn_user_select);
		delete.setOnClickListener(this);
		mUsernameTv.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {

				if (paramCharSequence.toString().trim().length() > 0) {
					delete.setVisibility(View.VISIBLE);
				} else {

					delete.setVisibility(View.GONE);

				}

			}

			@Override
			public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {

			}

			@Override
			public void afterTextChanged(Editable paramEditable) {

			}
		});
		mPasswordTv = (EditText) findViewById(R.id.et_password);
		mPasswordTv.setOnKeyListener(new OnKeyListener() {// 密码输入框添加按键监听，输入完密码，点击回车，进入登陆验证

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					onClick(findViewById(R.id.btn_login));
				}
				return false;
			}
		});
		mAutoCb = (CheckBox) findViewById(R.id.cb_auto);

		mAutoCb.setChecked(true);
		findViewById(R.id.btn_login).setOnClickListener(this);

		boolean loginInfo = Utils.getLoginInfo(this, "isLogin");

		// Utils.showToast(this, "已经登录过了");

		List<Account> accounts = Account.queryAccount(this);
		if (!accounts.isEmpty()) {
			Account acc = accounts.get(0);
			if (acc.auto && Utils.isToday(acc.timestamp) && !TextUtils.isEmpty(acc.username) && !TextUtils.isEmpty(acc.password) && loginInfo) {

				enterSystem();
			} else {
				Utils.saveLoginInfo(this, "isLogin", false);
				initData();
			}

		} else {
			Utils.saveLoginInfo(this, "isLogin", false);
			initData();
			// Utils.showToast(this, "还没登陆过");
		}
	}

	@Override
	public Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			for (Activity activity : getAppliction().getActivities()) {
				activity.finish();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 初始化登录数据，显示最近一次登陆的用户账号
	 */
	private void initData() {
		List<Account> accounts = Account.queryAccount(this);
		if (!accounts.isEmpty()) {
			Account acc = accounts.get(0);
			if (acc.auto && Utils.isToday(acc.timestamp) && !TextUtils.isEmpty(acc.username) && !TextUtils.isEmpty(acc.password)) {
				mUsernameTv.setText(acc.username);
				mPasswordTv.setText(acc.password);
				mAutoCb.setChecked(true);
				onClick(findViewById(R.id.btn_login));
			} else {
				mUsernameTv.setText(acc.username);
				mPasswordTv.setSelected(true);
				mUserAccounts = new ArrayList<UserAccount>();
				for (Account account : accounts) {
					UserAccount userAccount = new UserAccount(account._id, account.username, account.password, account.auto, account.timestamp);
					mUserAccounts.add(userAccount);
				}
			}
		} else {
			findViewById(R.id.btn_user_select).setVisibility(View.GONE);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			if (isValued()) {
				doLogin();
			}
			break;
		case R.id.btn_user_select:

			mUsernameTv.setText("");
			mPasswordTv.setText("");
			// showAccountList();
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	protected void notifyView() {
		showDialog(DLG_DATA_LOADING);
	}

	private PopupWindow mAccountPop;

	/**
	 * 弹出历史登录账号列表
	 */
	private void showAccountList() {
		if (mAccountPop == null) {
			View accountView = getLayoutInflater().inflate(R.layout.account_list, null);
			if (mUserAccounts != null) {
				ListView accListView = (ListView) accountView.findViewById(R.id.account_list);
				accListView.setOnItemClickListener(this);
				mUserAdapter = new ApiDataAdapter<UserAccount>(this);
				accListView.setAdapter(mUserAdapter);
				mUserAdapter.add(mUserAccounts);
			}
			mAccountPop = new PopupWindow(accountView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			mAccountPop.setAnimationStyle(R.style.PopupAnimation);
			mAccountPop.setFocusable(true);
			mAccountPop.setTouchable(true);
			mAccountPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pop));
			mAccountPop.update();
		}
		if (!mAccountPop.isShowing()) {
			mAccountPop.showAsDropDown(findViewById(R.id.user_view), 0, 0);
		}
	}

	/**
	 * 校验用户名和密码是否符合规范
	 * 
	 * @return
	 */
	private boolean isValued() {
		boolean result = true;
		String username = mUsernameTv.getText().toString();
		String password = mPasswordTv.getText().toString();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, R.string.tip_username_null, Toast.LENGTH_SHORT).show();
			result = false;
		} else if (TextUtils.isEmpty(password)) {
			Toast.makeText(this, R.string.tip_password_null, Toast.LENGTH_SHORT).show();
			result = false;
		} else if (password.length() < PWD_MIN_LENGTH) {
			Toast.makeText(this, R.string.tip_password_length, Toast.LENGTH_SHORT).show();
			result = false;
		}
		return result;
	}

	/**
	 * 执行登录，调用接口
	 */
	private void doLogin() {
		showDialog(DLG_DATA_LOADING);
		String username = mUsernameTv.getText().toString();
		String password = mPasswordTv.getText().toString();
		List<NameValuePair> postValues = new ArrayList<NameValuePair>();
		Config config = Utils.getSystemConfig(this);
		String key = DES.decyrpt(DES.INIT_KEY, config.Key);
		username = DES.encyrpt(key, username);
		password = DES.encyrpt(key, password);
		postValues.add(new ParamPair(PARAM_USERNAME, username));
		postValues.add(new ParamPair(PARAM_PASSWORD, password));
		NetworkPath path = new Netpath(URL_LOGIN, postValues);
		CacheParams params = new CacheParams(CacheParams.PRIORITY_NORMAL, path, CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);
		// mCacheManager.load(CALLBACK_LOGIN, params, this);

		mHashMap = new HashMap<String, String>();
		mHashMap.put(PARAM_USERNAME, username);
		mHashMap.put(PARAM_PASSWORD, password);
		mOkHeler.load(URL_LOGIN, new MyStringCallback(this), mHashMap, this);

	}

	class MyStringCallback extends OkHttpStringCallback {

		public MyStringCallback(Context context) {
			super(context);
		}

		@Override
		public void onResponse(String result) {

			dismisDialog(DLG_DATA_LOADING);
			/**
			 * 登录成功，保存用户账号信息及工程师信息
			 */

			boolean successResult = mOkHeler.isSuccessResult(result, LoginActivity.this);

			if (!successResult) {
				return;
			}

			String username = null;
			String password = null;
			username = mHashMap.get(PARAM_USERNAME);
			password = mHashMap.get(PARAM_PASSWORD);

			Config config = Utils.getSystemConfig(LoginActivity.this);
			String key = DES.decyrpt(DES.INIT_KEY, config.Key);
			username = DES.decyrpt(key, username);
			password = DES.decyrpt(key, password);
			List<Account> accounts = Account.queryAccount(LoginActivity.this);
			if (!accounts.isEmpty())

			{
				Account a = null;
				for (Account account : accounts) {
					if (username.equals(account.username)) {
						a = account;
					}
				}
				if (a != null) {
					a.password = mAutoCb.isChecked() ? password : "";
					a.auto = mAutoCb.isChecked();
					a.update(LoginActivity.this);
				} else {
					a = new Account(username, mAutoCb.isChecked() ? password : "", mAutoCb.isChecked());
					a.insert(LoginActivity.this);
				}
			} else

			{
				Account account = new Account(username, mAutoCb.isChecked() ? password : "", mAutoCb.isChecked());
				account.insert(LoginActivity.this);
			}

			RootData rootData;
			rootData = new RootData(result);
			try {
				Integer time = (Integer) rootData.getJson().get("SetTime");
				Utils.saveTipSpit(getApplicationContext(), time);
				if (time == 180) {
					StopPush();
				} else {
					startPush();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			EnginnerInfo enginnerInfo = rootData.getData(EnginnerInfo.class);
			Utils.saveEngineerInfo(LoginActivity.this, enginnerInfo);
			Utils.saveLoginInfo(LoginActivity.this, "isLogin", true);
			enterSystem();

		}

	}

	private boolean flag;

	private HashMap<String, String> mHashMap;

	private OkHttpHelper mOkHeler;

	private void enterSystem() {
		if (flag) {
			return;
		}
		flag = true;

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void StopPush() {
		Intent intent = new Intent();
		intent.putExtra("sid", PushConfig.SID);// 10081
		PushSDK.unregister(this, intent);

	}

	private void startPush() {
		Intent intent = new Intent();
		intent.putExtra("sid", PushConfig.SID);// 10081
		intent.putExtra("receiver_name", PushConfig.RECEIVER);

		// intent.putExtra("username", engineerID);
		// intent.putExtra("push_level", 1);
		intent.putExtra("realtime_level", 1);
		PushSDK.setInitStatus(getApplicationContext(), true);

		PushSDK.register(this, intent);

	}

	@Override
	public void finish() {
		// if (mCacheManager != null)
		// mCacheManager.cancel(this);

		mOkHeler.cancle(LoginActivity.class);

		super.finish();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.login;
	}

	@Override
	public void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DLG_DATA_LOADING:
			ProgressDialog progDialog = (ProgressDialog) dialog;
			progDialog.setMessage(getString(R.string.login_loading));
			break;

		default:
			super.onPrepareDialog(id, dialog);
			break;
		}

	}

	/**
	 * 网络回调
	 */
	// @Override
	// public void dataLoaded(int id, CacheParams params, ICacheInfo result) {
	// dismisDialog(DLG_DATA_LOADING);
	// boolean safe = ActivityUtils.prehandleNetworkData(this, this, id, params,
	// result, true);
	// if (!safe) {
	// return;
	// }
	// switch (id) {
	// case CALLBACK_LOGIN:
	// /**
	// * 登录成功，保存用户账号信息及工程师信息
	// */
	// NetworkPath path = params.path;
	// List<NameValuePair> postValues = path.postValues;
	// String username = null;
	// String password = null;
	// for (NameValuePair pair : postValues) {
	// if (pair.getName().equals(PARAM_USERNAME)) {
	// username = pair.getValue();
	// } else if (pair.getName().equals(PARAM_PASSWORD)) {
	// password = pair.getValue();
	// }
	// }
	// Config config = Utils.getSystemConfig(this);
	// String key = DES.decyrpt(DES.INIT_KEY, config.Key);
	// username = DES.decyrpt(key, username);
	// password = DES.decyrpt(key, password);
	// List<Account> accounts = Account.queryAccount(this);
	// if (!accounts.isEmpty()) {
	// Account a = null;
	// for (Account account : accounts) {
	// if (username.equals(account.username)) {
	// a = account;
	// }
	// }
	// if (a != null) {
	// a.password = mAutoCb.isChecked() ? password : "";
	// a.auto = mAutoCb.isChecked();
	// a.update(this);
	// } else {
	// a = new Account(username, mAutoCb.isChecked() ? password : "",
	// mAutoCb.isChecked());
	// a.insert(this);
	// }
	// } else {
	// Account account = new Account(username, mAutoCb.isChecked() ? password :
	// "", mAutoCb.isChecked());
	// account.insert(this);
	// }
	// RootData rootData = (RootData) result.getData();
	// EnginnerInfo enginnerInfo = rootData.getData(EnginnerInfo.class);
	// Utils.saveEngineerInfo(this, enginnerInfo);
	// Utils.saveLoginInfo(this, "isLogin", true);
	// enterSystem();
	// startPush();
	// break;
	// default:
	// break;
	// }
	//
	// }

	/**
	 * 用户账号列表点击
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object object = parent.getItemAtPosition(position);
		if (object instanceof UserAccount) {
			UserAccount account = (UserAccount) object;
			mUsernameTv.setText(account.username);
			if (!TextUtils.isEmpty(account.password) && account.auto && Utils.isToday(account.timestamp)) {
				mPasswordTv.setText(account.password);
			} else {
				mPasswordTv.setText("");
			}
			mAutoCb.setChecked(account.auto);
			mAccountPop.dismiss();
		}
	}
}
