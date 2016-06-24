package com.lenovo.sdimobileclient.ui;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.OrderCounts;
import com.viewpagerindicator.TabPageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class TabOrderNewActivity extends RootFragmentActivity {

	protected String[] CONTENT = new String[] { "处理中工单", "服务完工单" };
	private ViewPager pager;

	private TabPageIndicator indicator;

	private ApproveFragmentAdapter mAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initSearchBtn();

		pager = (ViewPager) findViewById(R.id.pager);
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		mAdapter = new ApproveFragmentAdapter(getSupportFragmentManager(), CONTENT);
		pager.setAdapter(mAdapter);
		indicator.setViewPager(pager);

	}

	/**
	 * 刷新工单列表
	 * 
	 * @param orderCounts
	 */
	public void initViewCount(OrderCounts orderCounts) {
		// CONTENT = new String[] { getString(R.string.label_uncontact,
		// orderCounts.Uncontact), getString(R.string.label_contacted,
		// orderCounts.Undoor),
		// getString(R.string.label_dooring, orderCounts.Dooring),
		// getString(R.string.label_tobefinished, orderCounts.ToBeFinished),
		// getString(R.string.label_finish, orderCounts.Finish) };
		mAdapter.setTabArray(CONTENT);
		mAdapter.notifyDataSetChanged();
		indicator.notifyDataSetChanged();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.order_list_new;
	}

	@Override
	protected void notifyView() {

		TabOrderActivity item = (TabOrderActivity) mAdapter.getItem(0);

		item.showProgress(this);
	}

	/**
	 * 工单切换卡页面配置
	 * 
	 * @author shaofang zhang
	 *
	 */
	class ApproveFragmentAdapter extends FragmentPagerAdapter {
		private String[] tabArray;

		public void setTabArray(String[] array) {
			tabArray = array;
		}

		public ApproveFragmentAdapter(FragmentManager fm, String[] array) {
			super(fm);
			tabArray = array;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return generateFragment(TabOrderActivity.class, ORDER_STATE_UNFINISH);
			}
			// else if (position == 1) {
			// return generateFragment(TabOrderActivity.class,
			// ORDER_STATE_CONTACTED);
			// } else if (position == 2) {
			// return generateFragment(TabOrderActivity.class,
			// ORDER_STATE_DOORING);
			// } else if (position == 3) {
			// return generateFragment(TabOrderActivity.class,
			// ORDER_STATE_TOBEFINISHED);
			// }
			else if (position == 1) {
				return generateFragment(TabOrderActivity.class, ORDER_STATE_FINISHED);
			} else {
				return null;
			}
		}

		private Fragment generateFragment(Class<? extends Fragment> clazz, int state) {
			try {
				Fragment fragment = (Fragment) clazz.newInstance();

				Bundle args = new Bundle();
				args.putInt("order_state", state);
				fragment.setArguments(args);
				return fragment;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabArray[position % tabArray.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return tabArray.length;
		}
	}
}
