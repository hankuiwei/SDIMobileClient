package com.lenovo.sdimobileclient.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.lenovo.sdimobileclient.R;

public class PullActivateLayout extends FrameLayout {
	private final int mActionViewId;
	private final int mToolViewId;
	private final int mTouchSlop;
	private View mActionView;
	private View mToolView;
	private int mActionViewHeight = 0;
	private int mToolViewHeight = 0;
	private final Scroller mScroller;
	private long mLastScroll;
	private final Rect mTempRect = new Rect();
	private View mTargetView;
	private AdapterView<?> mTargetAdapterView;
	private View mScrollbarStoreTarget;
	private boolean mTargetScrollbarEnable;
	private boolean mInTouch = false;
	private boolean mIsMotioned = false;
	private boolean mCancelSended = false;
	private float mLastMotionY;
	private int mMotionScrollY;
	private boolean mEnableStopInAction = true;
	private int mTopState;
	private OnPullStateListener mPullStateListener = null;
	private OnPullListener mActionViewPullListener = null;
	private OnPullListener mToolViewPullListener = null;

	public PullActivateLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullActivateLayout, defStyle, 0);

		this.mActionViewId = a.getResourceId(0, R.id.action_view);
		this.mToolViewId = a.getResourceId(1, R.id.tool_view);
		this.mScroller = new Scroller(context);

		ViewConfiguration configuration = ViewConfiguration.get(context);
		this.mTouchSlop = configuration.getScaledTouchSlop();
	}

	public PullActivateLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PullActivateLayout(Context context) {
		super(context);
		this.mActionViewId = R.id.action_view;
		this.mToolViewId = R.id.tool_view;
		this.mScroller = new Scroller(context);

		ViewConfiguration configuration = ViewConfiguration.get(context);
		this.mTouchSlop = configuration.getScaledTouchSlop();
	}

	private void checkViewId(View view) {
		if (view == null) {
			return;
		}
		if ((this.mActionView == null) && (view.getId() == this.mActionViewId)) {
			this.mActionView = view;
		}

		if ((this.mToolView == null) && (view.getId() == this.mToolViewId))
			this.mToolView = view;
	}

	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		checkViewId(child);
		super.addView(child, index, params);
	}

	protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
		checkViewId(child);
		return super.addViewInLayout(child, index, params, preventRequestLayout);
	}

	public void removeView(View child) {
		if (this.mActionView == child) {
			this.mActionView = null;
		}
		if (this.mToolView == child) {
			this.mToolView = null;
		}
		super.removeView(child);
	}

	public void removeViewInLayout(View view) {
		if (this.mActionView == view) {
			this.mActionView = null;
		}
		if (this.mToolView == view) {
			this.mToolView = null;
		}
		super.removeViewInLayout(view);
	}

	public void removeViewsInLayout(int start, int count) {
		for (int i = start; i < start + count; i++) {
			View view = getChildAt(i);
			if (this.mActionView == view) {
				this.mActionView = null;
			}
			if (this.mToolView == view) {
				this.mToolView = null;
			}
		}
		super.removeViewsInLayout(start, count);
	}

	public void removeViewAt(int index) {
		View view = getChildAt(index);
		if (this.mActionView == view) {
			this.mActionView = null;
		}
		if (this.mToolView == view) {
			this.mToolView = null;
		}
		super.removeViewAt(index);
	}

	public void removeViews(int start, int count) {
		for (int i = start; i < start + count; i++) {
			View view = getChildAt(i);
			if (this.mActionView == view) {
				this.mActionView = null;
			}
			if (this.mToolView == view) {
				this.mToolView = null;
			}
		}
		super.removeViews(start, count);
	}

	public void removeAllViews() {
		this.mActionView = null;
		this.mToolView = null;
		super.removeAllViews();
	}

	public void removeAllViewsInLayout() {
		this.mActionView = null;
		this.mToolView = null;
		super.removeAllViewsInLayout();
	}

	protected void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
		checkViewId(child);
		super.attachViewToParent(child, index, params);
	}

	protected void detachViewFromParent(View child) {
		if (this.mActionView == child) {
			this.mActionView = null;
		}
		if (this.mToolView == child) {
			this.mToolView = null;
		}
		super.detachViewFromParent(child);
	}

	protected void detachViewFromParent(int index) {
		View view = getChildAt(index);
		if (this.mActionView == view) {
			this.mActionView = null;
		}
		if (this.mToolView == view) {
			this.mToolView = null;
		}
		super.detachViewFromParent(index);
	}

	protected void detachViewsFromParent(int start, int count) {
		for (int i = start; i < start + count; i++) {
			View view = getChildAt(i);
			if (this.mActionView == view) {
				this.mActionView = null;
			}
			if (this.mToolView == view) {
				this.mToolView = null;
			}
		}
		super.detachViewsFromParent(start, count);
	}

	protected void detachAllViewsFromParent() {
		this.mActionView = null;
		this.mToolView = null;
		super.detachAllViewsFromParent();
	}

	public void setActionView(View view) {
		if (view != null) {
			view.setId(this.mActionViewId);
			addView(view);
		} else {
			this.mActionView = null;
		}
	}

	public void setToolView(View view) {
		if (view != null) {
			view.setId(this.mToolViewId);
			addView(view);
		} else {
			this.mToolView = null;
		}
	}

	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int b = top;
		if (this.mToolView != null) {
			View v = this.mToolView;
			int t = b - (v.getBottom() - v.getTop());
			v.layout(v.getLeft(), t, v.getRight(), b);
			this.mToolViewHeight = (v.getVisibility() == 0 ? b - t : 0);
			b = t;
		}
		if (this.mActionView != null) {
			View v = this.mActionView;
			int t = b - (v.getBottom() - v.getTop());
			v.layout(v.getLeft(), t, v.getRight(), b);
			this.mActionViewHeight = (v.getVisibility() == 0 ? b - t : 0);
		}
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		float eventFloatY = ev.getY();
		boolean targetOnTop = isOnTargetTop();
		int scrollY = getScrollY();
		int action = ev.getAction();

		if (action == 0) {
			this.mCancelSended = false;
			this.mInTouch = true;
			this.mIsMotioned = false;
			this.mLastMotionY = eventFloatY;
			this.mTargetView = null;
			this.mTargetAdapterView = null;
			View v = findTargetView(this, ev);
			if ((v instanceof AdapterView))
				this.mTargetAdapterView = ((AdapterView<?>) v);
			else {
				this.mTargetView = v;
			}
		}

		switch (action) {
		case 0:
		case 2:
			if ((!this.mIsMotioned) && (scrollY <= 0) && (targetOnTop)) {
				this.mIsMotioned = true;
				this.mLastMotionY = eventFloatY;
				this.mMotionScrollY = scrollY;
			}

			if (!targetOnTop) {
				this.mIsMotioned = false;
			}
			if ((!this.mIsMotioned) || (!targetOnTop))
				break;
			float d = this.mMotionScrollY - eventFloatY + this.mLastMotionY;
			scrollTo(0, (int) d);

			break;
		case 1:
		case 3:
			completeTouch();
		}

		if ((scrollY >= 0) || (!targetOnTop) || (action != 2)) {
			if ((this.mCancelSended) && (action != 1) && (action != 3)) {
				this.mCancelSended = false;
				ev.setAction(0);
			}
		} else if ((!this.mCancelSended) && (action == 2) && (Math.abs(this.mMotionScrollY - scrollY) > this.mTouchSlop)) {
			this.mCancelSended = true;
			ev.setAction(3);
			super.dispatchTouchEvent(ev);
		}

		if (!this.mCancelSended) {
			super.dispatchTouchEvent(ev);
		}

		if (!this.mIsMotioned) {
			this.mLastMotionY = eventFloatY;
		}
		return true;
	}

	public void scrollTo(int x, int y) {
		int destY = Math.min(y, 0);

		int newState = 0;
		if ((destY < 0) && (this.mToolViewHeight > 0)) {
			newState |= 1;
		}
		if ((destY < this.mToolViewHeight) && (this.mActionViewHeight > 0)) {
			newState |= 2;
		}
		if (destY < -this.mActionViewHeight - this.mToolViewHeight) {
			newState |= 4;
		}

		if (newState != this.mTopState) {
			if (this.mPullStateListener != null) {
				if (((this.mTopState & 0x4) == 0) && ((newState & 0x4) != 0))
					this.mPullStateListener.onPullOut();
				else if (((this.mTopState & 0x4) != 0) && ((newState & 0x4) == 0)) {
					this.mPullStateListener.onPullIn();
				}
			}

			if (this.mActionViewPullListener != null) {
				if (((this.mTopState & 0x2) == 0) && ((newState & 0x2) != 0))
					this.mActionViewPullListener.onShow();
				else if (((this.mTopState & 0x2) != 0) && ((newState & 0x2) == 0)) {
					this.mActionViewPullListener.onHide();
				}
			}

			if (this.mToolViewPullListener != null) {
				if (((this.mTopState & 0x1) == 0) && ((newState & 0x1) != 0))
					this.mToolViewPullListener.onShow();
				else if (((this.mTopState & 0x1) != 0) && ((newState & 0x1) == 0)) {
					this.mToolViewPullListener.onHide();
				}
			}

			this.mTopState = newState;
		}

		setVerticalScrollBarEnabled(destY < 0);

		if ((destY < 0) && (this.mScrollbarStoreTarget == null)) {
			if (this.mTargetView != null) {
				this.mScrollbarStoreTarget = this.mTargetView;
				this.mTargetScrollbarEnable = this.mTargetView.isVerticalScrollBarEnabled();
				this.mTargetView.setVerticalScrollBarEnabled(false);
			}
			if (this.mTargetAdapterView != null) {
				this.mScrollbarStoreTarget = this.mTargetAdapterView;
				this.mTargetScrollbarEnable = this.mTargetAdapterView.isVerticalScrollBarEnabled();
				this.mTargetAdapterView.setVerticalScrollBarEnabled(false);
			}
		}
		if ((destY >= 0) && (this.mScrollbarStoreTarget != null)) {
			this.mScrollbarStoreTarget.setVerticalScrollBarEnabled(this.mTargetScrollbarEnable);
			this.mScrollbarStoreTarget = null;
			setVerticalScrollBarEnabled(false);
		}

		super.scrollTo(x, destY);
	}

	public void computeScroll() {
		if (this.mScroller.computeScrollOffset()) {
			int oldY = getScrollY();
			int scrollY = this.mScroller.getCurrY();

			scrollTo(0, scrollY);

			if (oldY != scrollY) {
				onScrollChanged(0, scrollY, 0, oldY);
			}
			if (scrollY > 0) {
				this.mScroller.abortAnimation();
			}

			postInvalidate();
		}
	}

	private View findTargetView(ViewGroup group, MotionEvent ev) {
		float xf = ev.getX();
		float yf = ev.getY();
		float scrolledXFloat = xf + group.getScrollX();
		float scrolledYFloat = yf + group.getScrollY();
		Rect frame = this.mTempRect;
		int orgAction = ev.getAction();

		ev.setAction(0);

		int scrolledXInt = (int) scrolledXFloat;
		int scrolledYInt = (int) scrolledYFloat;

		int count = group.getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			View child = group.getChildAt(i);
			if ((child.getVisibility() == 0) || (child.getAnimation() != null)) {
				child.getHitRect(frame);
				if (!frame.contains(scrolledXInt, scrolledYInt))
					continue;
				float xc = scrolledXFloat - child.getLeft();
				float yc = scrolledYFloat - child.getTop();
				ev.setLocation(xc, yc);

				if (!child.dispatchTouchEvent(ev)) {
					continue;
				}
				ev.setAction(3);
				child.dispatchTouchEvent(ev);

				ev.setAction(orgAction);
				ev.setLocation(xf, yf);
				return child;
			}
		}

		ev.setAction(orgAction);
		ev.setLocation(xf, yf);
		return null;
	}

	public void setEnableStopInActionView(boolean enable) {
		this.mEnableStopInAction = enable;
	}

	public void setOnActionPullListener(OnPullListener listener) {
		this.mActionViewPullListener = listener;
	}

	public void setOnToolPullListener(OnPullListener listener) {
		this.mToolViewPullListener = listener;
	}

	public void setOnPullStateChangeListener(OnPullStateListener listener) {
		this.mPullStateListener = listener;
	}

	private boolean isOnTargetTop() {
		if (this.mTargetView != null) {
			return this.mTargetView.getScrollY() <= 0;
		}
		if (this.mTargetAdapterView != null) {
			int first = this.mTargetAdapterView.getFirstVisiblePosition();
			View firstView = null;
			if ((first == 0) && ((firstView = this.mTargetAdapterView.getChildAt(0)) != null)) {
				return firstView.getTop() >= 0;
			}
			return false;
		}

		return true;
	}

	private void completeTouch() {
		this.mInTouch = false;
		int y = getScrollY();
		int toolTop = -this.mToolViewHeight;
		int top = toolTop - this.mActionViewHeight;
		if (y >= 0) {
			return;
		}

		if (y < top)
			snapToActionViewTop();
		else if ((!this.mEnableStopInAction) && (y < toolTop) && (y > top)) {
			hideActionView();
		} else if ((y <= toolTop * 0.6F) && (y > toolTop)) {
			snapToToolViewTop();
		} else if (y > toolTop * 0.6F) {
			snapToToolViewBottom();
		}
		// else {
		// smoothScrollTo(-this.mToolViewHeight - this.mActionViewHeight);
		// }
	}

	protected int computeVerticalScrollRange() {
		int scrollRange = getHeight();
		if (this.mTargetView != null) {
			scrollRange = Math.max(scrollRange, this.mTargetView.getHeight());
		}
		if (this.mTargetAdapterView != null) {
			int itemCount = this.mTargetAdapterView.getCount();
			int count = this.mTargetAdapterView.getChildCount();

			scrollRange = Math.max(scrollRange, Math.max(1, itemCount - count) * getHeight());
		}

		int scrollY = getScrollY();
		if (scrollY < 0) {
			scrollRange = (int) (scrollRange - scrollY * getHeight() * 0.1D);
		}

		return scrollRange;
	}

	protected int computeVerticalScrollOffset() {
		return Math.max(0, super.computeVerticalScrollOffset());
	}

	public void hideActionView() {
		int y = getScrollY();
		int bottom = -this.mToolViewHeight;
		if ((y >= bottom) || (this.mInTouch)) {
			return;
		}

		smoothScrollTo(bottom);
	}

	public void snapToActionViewTop() {
		int y = getScrollY();
		int top = -this.mToolViewHeight - this.mActionViewHeight;
		if ((y >= top) || (this.mInTouch)) {
			return;
		}
		smoothScrollTo(top);
		if (this.mActionViewPullListener != null)
			this.mActionViewPullListener.onSnapToTop();
	}

	public void snapToToolViewTop() {
		int y = getScrollY();
		int top = -this.mToolViewHeight;
		if ((y >= 0) || (this.mInTouch)) {
			return;
		}
		smoothScrollTo(top);
		if (this.mToolViewPullListener != null)
			this.mToolViewPullListener.onSnapToTop();
	}

	public void snapToToolViewBottom() {
		int y = getScrollY();
		if ((y >= 0) || (this.mInTouch)) {
			return;
		}
		smoothScrollTo(0);
	}

	public boolean isPullOut() {
		return getScrollY() < -this.mActionViewHeight - this.mToolViewHeight;
	}

	public final void smoothScrollBy(int dy) {
		long duration = AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll;
		if (duration > 250L) {
			this.mScroller.startScroll(0, getScrollY(), 0, dy);
			invalidate();
		} else {
			if (!this.mScroller.isFinished()) {
				this.mScroller.abortAnimation();
			}
			scrollBy(0, dy);
		}
		this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
	}

	public final void smoothScrollTo(int y) {
		smoothScrollBy(y - getScrollY());
	}

	public static abstract interface OnPullListener {
		public abstract void onSnapToTop();

		public abstract void onShow();

		public abstract void onHide();
	}

	public static abstract interface OnPullStateListener {
		public abstract void onPullOut();

		public abstract void onPullIn();
	}
}