package com.lenovo.sdimobileclient.ui;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;


public class AdvancedTabHost extends TabHost {
	private LocalActivityManager mLocalActivityManager;
	private TabSelectChangeListener mSelectChangeListener;
	private boolean mIgnoreFocus = false;

	public AdvancedTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AdvancedTabHost(Context context) {
		super(context);
	}

	public void setup(LocalActivityManager activityGroup) {
		super.setup(activityGroup);
		mLocalActivityManager = activityGroup;
		TabWidget tabWidget = getTabWidget();
		if (tabWidget instanceof AdvancedTabWidget) {
			((AdvancedTabWidget) tabWidget).setTabHost(this);
		}
	}

	@Override
	public void setCurrentTab(int index) {
		if (mSelectChangeListener != null) {
			mSelectChangeListener.selectChange(this, index);
		}
		super.setCurrentTab(index);
	}

	public void setSelectChangeListener(TabSelectChangeListener l) {
		mSelectChangeListener = l;
	}
	/**
	 * tab切换监听
	 * @author zhangshaofang
	 *
	 */
	public final static class TabSelectChangeListener {
		private void selectChange(AdvancedTabHost tabHost, int index) {
			if (index == tabHost.getCurrentTab() && !tabHost.mIgnoreFocus
					&& tabHost.mLocalActivityManager != null) {
				Activity activity = tabHost.mLocalActivityManager
						.getCurrentActivity();
				if (activity instanceof TabGroup) {
					((TabGroup) activity).checkIndex();
				}
			}
		}
	}

	void setIgnoreFocus(boolean ignore) {
		mIgnoreFocus = ignore;
	}
	/**
	 * tab切换和二次点击
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		final boolean handled = super.dispatchKeyEvent(event);

		// unhandled key ups change focus to tab indicator for embedded
		// activities
		// when there is nothing that will take focus from default focus
		// searching

		View view = getCurrentView();
		if (!handled && (event.getAction() == KeyEvent.ACTION_DOWN)
				&& (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN)
				&& (view.hasFocus())
				&& (view.findFocus().focusSearch(View.FOCUS_DOWN) == null)) {
			getTabWidget().getChildTabViewAt(getCurrentTab()).requestFocus();
			playSoundEffect(SoundEffectConstants.NAVIGATION_UP);
			return true;
		}
		return handled;
	}
}
