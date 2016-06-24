
package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.utility.RandomMaker;

public class TabGroup extends ActivityGroup implements AnimationListener {
	private static final String LOG_TAG = "TabGroup";

	public static final String EXTRA_INTENT = "intent";
	public static final String EXTRA_GO_OUT = "go_out";

	public static final String STATE_KEY = "com.m_bao.ui.TabGroup:state";
	public static final String STATE_HISTORY = "history";
	public static final String STATE_ID_HISTORY = "id_history";

	private LocalActivityManager mLocalActivityManager;

	public static class HistoryData implements Parcelable {
		Intent mIntent;
		int mRequestCode;

		public HistoryData(Intent intent, int requestCode) {
			mIntent = intent;
			mRequestCode = requestCode;
		}

		@Override
		public int describeContents() {
			return (mIntent != null) ? mIntent.describeContents() : 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(mRequestCode);
			if (mIntent != null) {
				mIntent.writeToParcel(dest, flags);
			}
		}

		public static final Parcelable.Creator<HistoryData> CREATOR = new Parcelable.Creator<HistoryData>() {
			public HistoryData createFromParcel(Parcel in) {
				return new HistoryData(in);
			}

			public HistoryData[] newArray(int size) {
				return new HistoryData[size];
			}
		};

		private HistoryData(Parcel in) {
			readFromParcel(in);
		}

		public void readFromParcel(Parcel in) {
			mRequestCode = in.readInt();
			mIntent = Intent.CREATOR.createFromParcel(in);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isChild()) {
			if (isCurentFirst()) {
				return getParent().onKeyDown(keyCode, event);
			} else {
				Activity activity = mLocalActivityManager.getCurrentActivity();
				return activity.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onSearchRequested() {
		boolean result;
		Activity parent = getParent();
		if (parent != null) {
			result = parent.onSearchRequested();
		} else {
			result = super.onSearchRequested();
		}
		return result;
	}

	private ArrayList<String> mIdHistory;
	private ArrayList<HistoryData> mHistory;

	private FrameLayout mLayout;
	private View mUpView;
	private View mDownView;

	private Animation mSlideInAnimation;
	private Animation mSlideOutAnimation;

	private int mDpi;
	private FrameLayout.LayoutParams mLayoutParams;

	public TabGroup() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Bundle state = null;
		if (savedInstanceState != null) {
			state = savedInstanceState.getBundle(STATE_KEY);
		}
		super.onCreate(state);
		mLocalActivityManager = getLocalActivityManager();

		setContentView(R.layout.tab_group);
		mLayout = (FrameLayout) findViewById(R.id.mainLayout);
		mLayout.setBackgroundColor(0xFF000000);
		mUpView = findViewById(R.id.upLayout);
		mDownView = findViewById(R.id.downLayout);

		mSlideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in);
		mSlideInAnimation.setAnimationListener(this);
		mSlideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out);
		mSlideOutAnimation.setAnimationListener(this);

		mDpi = getResources().getDisplayMetrics().densityDpi;

		mLayoutParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

		if (state == null) {
			mIdHistory = new ArrayList<String>();
			mHistory = new ArrayList<HistoryData>();

			Intent intent = getIntent().getParcelableExtra(EXTRA_INTENT);
			if (intent != null) {
				addChild(intent, -1);
			}
		} else {
			mIdHistory = state.getStringArrayList(STATE_ID_HISTORY);
			mHistory = state.getParcelableArrayList(STATE_HISTORY);
			String id = mIdHistory.get(mIdHistory.size() - 1);
			Intent intent = mHistory.get(mHistory.size() - 1).mIntent;
			Window w = mLocalActivityManager.startActivity(id, intent);
			View v = w.getDecorView();

			mLayout.addView(v, mLayoutParams);
			v.requestFocus();
			if (mLayout.getChildCount() > 1) {
				mLayout.removeViewAt(0);
			}
		}
	}

	@Override
	public void startActivityFromChild(Activity child, Intent intent, int requestCode) {
		if (intent.getBooleanExtra(EXTRA_GO_OUT, false)) {
			// super.startActivityFromChild(child, intent, requestCode);
			startActivityForResult(intent, requestCode);
			// getParent().startActivityForResult(intent, requestCode);
		} else {
			addChild(intent, requestCode);
		}
	}

	@Override
	public void finishActivityFromChild(Activity child, final int requestCode) {
		int index = -1;
		final int count = mHistory.size();
		for (int i = 0; i < count; i++) {
			if (requestCode == mHistory.get(i).mRequestCode) {
				index = i;
				break;
			}
		}

		if (index == -1 || count <= 1) {
			super.finishActivityFromChild(child, requestCode);
		} else if (index == count - 1) {
			moveTo(index - 1, child);
		} else {
			String id = mIdHistory.get(index);
			mLocalActivityManager.destroyActivity(id, true);
			mIdHistory.remove(index);
			mHistory.remove(index);
		}
	}

	@Override
	public void finishFromChild(Activity child) {
		int size = mHistory.size();
		if (size <= 1 || !moveTo(size - 2, child)) {
			super.finishFromChild(child);
		}
	}

	private void addChild(Intent intent, int requestCode) {
		//Log.e("addIntent", intent.toString());
		String id = RandomMaker.getString();
		mIdHistory.add(id);
		mHistory.add(new HistoryData(intent, requestCode));
		Window w = mLocalActivityManager.startActivity(id, intent);
		if (w == null) {
			//mIdHistory.remove(mIdHistory.size() - 1);
			//mHistory.remove(mHistory.size() - 1);
			return;
		}
		View v = w.getDecorView();

		View oldChild = mLayout.getChildAt(0);
		Bitmap cache = null;
		if (oldChild != null) {
			mLayout.setDrawingCacheEnabled(true);
			cache = mLayout.getDrawingCache(true);
			if (cache != null) {
				cache = cache.copy(Config.ARGB_8888, false);
				cache.setDensity(mDpi);
				Drawable d = new BitmapDrawable(cache);

				mDownView.setVisibility(View.VISIBLE);
				mDownView.setBackgroundDrawable(d);
			} else {
				mLayout.setDrawingCacheEnabled(false);
			}
		}

		mLayout.addView(v, mLayoutParams);
		v.requestFocus();

		if (oldChild != null) {
			if (mLayout.getChildCount() > 1) {
				mLayout.removeViewAt(0);
			}
			if (cache != null) {
				mLayout.startAnimation(mSlideInAnimation);
			}
		}
	}

	private boolean moveTo(int index, Activity from) {
		int size = mHistory.size();
		if (size - 2 < index || index < 0) {
			return false;
		}

		Activity activity = mLocalActivityManager.getCurrentActivity();
		HistoryData curent = mHistory.get(size - 1);
		String id = mIdHistory.get(index);
		HistoryData history = mHistory.get(index);
		Intent intent = history.mIntent;
		Window w = mLocalActivityManager.startActivity(id, intent);

		int resultCode = Activity.RESULT_OK;
		Intent data = null;
		Activity destActivity = null;
		boolean canReturn = false;
		if (from != null && curent.mRequestCode != -1) {
			destActivity = mLocalActivityManager.getCurrentActivity();

			if (activity != null && destActivity != null && destActivity instanceof ITabGroupChild) {
				Intent i = activity.getIntent();
				Bundle bundle = i == null ? null : i.getBundleExtra(EXTRA_RESULT);
				if (bundle != null && bundle.containsKey(EXTRA_RESULT_RESULTCODE)) {
					resultCode = bundle.getInt(EXTRA_RESULT_RESULTCODE, Activity.RESULT_OK);
					data = bundle.getParcelable(EXTRA_RESULT_DATA);
					canReturn = true;
				} else {
					Log.w(LOG_TAG,
							"Not found result info, please use TabGroup.setResult() instead of setResult().");
				}

				// ActivityRecord r = new ActivityRecord();
				// r.activity = destActivity;
				// ArrayList<ResultInfo> results = new ArrayList<ResultInfo>();
				// results.add(new ResultInfo(null, history.mRequestCode,
				// resultCode, data));
				// ActivityThread.currentActivityThread().deliverResults(r,
				// results);
			} else {
				Log.w(LOG_TAG, "Can't push result to targetActivity.");
			}
		}

		View v = w.getDecorView();

		mLayout.setDrawingCacheEnabled(true);
		Bitmap cache = mLayout.getDrawingCache(true);
		if (cache != null) {
			try {
				cache = cache.copy(Config.ARGB_8888, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
			cache.setDensity(mDpi);
			Drawable d = new BitmapDrawable(cache);
			mUpView.setVisibility(View.VISIBLE);
			mUpView.setBackgroundDrawable(d);
		} else {
			mLayout.setDrawingCacheEnabled(false);
		}
		ViewGroup layout = (ViewGroup) (v.getParent() != null ? v.getParent() : null);
		if (layout != null)
			layout.removeView(v);
		mLayout.addView(v, mLayoutParams);
		v.requestFocus();
		if (mLayout.getChildCount() > 1) {
			mLayout.removeViewAt(0);
		}

		if (cache != null) {
			mUpView.startAnimation(mSlideOutAnimation);
		}

		for (int i = size - 1; i > index; i--) {
			id = mIdHistory.get(i);
			mLocalActivityManager.destroyActivity(id, true);
			mIdHistory.remove(i);
			mHistory.remove(i);
		}

		if (canReturn && destActivity != null && destActivity instanceof ITabGroupChild) {
			((ITabGroupChild) destActivity).onActivityResult(curent.mRequestCode, resultCode, data);
		}
		return true;
	}

	public void checkIndex() {
		int size = mHistory.size();
		if (size - 2 < 0) {
			return;
		}

		moveTo(0, null);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Bundle state = new Bundle();

		state.putStringArrayList(STATE_ID_HISTORY, mIdHistory);
		state.putParcelableArrayList(STATE_HISTORY, mHistory);

		outState.putBundle(STATE_KEY, state);
		super.onSaveInstanceState(state);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		Bundle state = savedInstanceState.getBundle(STATE_KEY);
		super.onRestoreInstanceState(state);

		// mIdHistory = state.getStringArrayList(STATE_ID_HISTORY);
		// mHistory = state.getParcelableArrayList(STATE_HISTORY);
		//
		// String id = mIdHistory.get(mIdHistory.size() - 1);
		// Intent intent = mHistory.get(mHistory.size() - 1);
		// Window w = mLocalActivityManager.startActivity(id, intent);
		// View v = w.getDecorView();
		//
		// mLayout.addView(v, mLayoutParams);
		// v.requestFocus();
		// if (mLayout.getChildCount() > 1) {
		// mLayout.removeViewAt(0);
		// }
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mUpView.setVisibility(View.GONE);
		mUpView.setBackgroundDrawable(null);
		mDownView.setVisibility(View.GONE);
		mDownView.setBackgroundDrawable(null);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
		mLayout.setDrawingCacheEnabled(false);
	}

	public boolean isCurentFirst() {
		return mHistory.size() <= 1;
	}

	private static final String EXTRA_RESULT = Constants.PACKAGE_NAME + ".activity_result";
	private static final String EXTRA_RESULT_RESULTCODE = "resultCode";
	private static final String EXTRA_RESULT_DATA = "data";

	public static void setResult(Activity activity, int resultCode, Intent data) {
		activity.setResult(resultCode, data);
		Intent intent = activity.getIntent();

		Bundle bundle = intent.getBundleExtra(EXTRA_RESULT);
		if (bundle == null) {
			bundle = new Bundle();
			intent.putExtra(EXTRA_RESULT, bundle);
		} else {
			bundle.clear();
		}
		bundle.putInt(EXTRA_RESULT_RESULTCODE, resultCode);
		bundle.putParcelable(EXTRA_RESULT_DATA, data);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("TabGroup", "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);

		Activity child = getCurrentActivity();
		if (child instanceof ITabGroupChild) {
			((ITabGroupChild) child).onActivityResult(requestCode, resultCode, data);
		}
	}

	public static Context getContext(Context context) {
		if (context != null && context instanceof Activity) {
			Activity parent = ((Activity) context).getParent();
			context = parent == null ? context : parent;
		}
		return context;
	}
}
