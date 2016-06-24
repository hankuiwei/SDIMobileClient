
package com.lenovo.sdimobileclient.ui;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabWidget;

/**
 * 自定义tabwidget
 * 
 * @author zhangshaofang
 *
 */
public class AdvancedTabWidget extends TabWidget {

	private int mSelectedTab = 0;
	private AdvancedTabHost mTabHost = null;

	public AdvancedTabWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AdvancedTabWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AdvancedTabWidget(Context context) {
		super(context);
	}

	public void setCurrentTab(int index) {
		super.setCurrentTab(index);
		if (index < 0 || index >= getTabCount()) {
			return;
		}

		mSelectedTab = index;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (mTabHost == null) {
			super.onFocusChange(v, hasFocus);
			return;
		}

		if (v == this) {
			super.onFocusChange(v, hasFocus);
			return;
		}

		if (hasFocus) {
			int numTabs = getTabCount();
			for (int i = 0; i < numTabs; i++) {
				if (getChildTabViewAt(i) == v) {
					if (mSelectedTab == i) {
						mTabHost.setIgnoreFocus(true);
					}
					break;
				}
			}
		}
		super.onFocusChange(v, hasFocus);
		mTabHost.setIgnoreFocus(false);
	}

	void setTabHost(AdvancedTabHost advancedTabHost) {
		mTabHost = advancedTabHost;
	}

}
