package com.lenovo.sdimobileclient.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class ImageTouchView extends ImageView {
	static final int NONE = 0;// 表示当前没有状态
	static final int DRAG = 1; // 表示当前处于移动状态
	static final int BIGGER = 3; // 表示放大图片
	static final int SMALLER = 4; // 表示缩小图片
	public static final float SCALE = 0.5f; // 缩放因子

	private int screenW;// 下面两句图片的移动范围，及ViewArea的范围，也就是linearLayout的范围，也就是屏幕方位（都是填满父控件属性）
	private int screenH;

	private int start_x;// 开始触摸点
	private int start_y;
	private int stop_x;// 结束触摸点
	private int stop_y;

    public ImageTouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageTouchView(Context context) {
		super(context);
	}

	public ImageTouchView(Context context, int w, int h)// 这里传进来的w，h就是图片的移动范围
	{
		super(context);
		this.setPadding(0, 0, 0, 0);
		screenW = w;
		screenH = h;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) { // MotionEvent.ACTION_MASK
										// 表示多点触控事件
		case MotionEvent.ACTION_DOWN:
			stop_x = (int) event.getRawX();// 表示相对于屏幕左上角为原点的坐标
			stop_y = (int) event.getRawY();// 同上
			start_x = stop_x - this.getLeft();// 用(int)
												// event.getX();一样,表示相对于当前点击Widget（控件）左上角的坐标，这里就是相对于自定义imageView左上角的坐标.建议用前者，如果不是全屏拖动，而是指定范围内，一样适用！
			start_y = stop_y - this.getTop();// //用(int)
												// event.getY();一样,this.getTop()表示其顶部相对于父控件的距离
			break;
		case MotionEvent.ACTION_UP:

			int disX = 0;
			int disY = 0;
			if (getHeight() <= screenH)//
			{
				if (this.getTop() < 0) {
					disY = getTop();
					// layout(left , top, right,bottom)函数表示设置view的位置。
					this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());

				} else if (this.getBottom() >= screenH) {
					disY = getHeight() - screenH + getTop();
					this.layout(this.getLeft(), screenH - getHeight(), this.getRight(), screenH);
				}
			} else {
				int Y1 = getTop();
				int Y2 = getHeight() - screenH + getTop();
				if (Y1 > 0) {
					disY = Y1;
					this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
				} else if (Y2 < 0) {
					disY = Y2;
					this.layout(this.getLeft(), screenH - getHeight(), this.getRight(), screenH);
				}
			}
			if (getWidth() <= screenW) {
				if (this.getLeft() < 0) {
					disX = getLeft();
					this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
				} else if (this.getRight() > screenW) {
					disX = getWidth() - screenW + getLeft();
					this.layout(screenW - getWidth(), this.getTop(), screenW, this.getBottom());
				}
			} else {
				int X1 = getLeft();
				int X2 = getWidth() - screenW + getLeft();
				if (X1 > 0) {
					disX = X1;
					this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
				} else if (X2 < 0) {
					disX = X2;
					this.layout(screenW - getWidth(), this.getTop(), screenW, this.getBottom());
				}

			}
			// 如果图片缩放到宽高任意一个小于100，那么自动放大，直到大于100.
			while (getHeight() < 100 || getWidth() < 100) {

				setScale(SCALE, BIGGER);
			}
			// 根据disX和disY的偏移量采用移动动画回弹归位，动画时间为500毫秒。
			if (disX != 0 || disY != 0) {
                TranslateAnimation trans = new TranslateAnimation(disX, 0, disY, 0);
				trans.setDuration(500);
				this.startAnimation(trans);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y - start_y + this.getHeight());
			stop_x = (int) event.getRawX();
			stop_y = (int) event.getRawY();

			break;
		}
		return true;
	}

	public void setScale(float temp, int flag) {

		if (flag == BIGGER) {
			this.setFrame(this.getLeft() - (int) (temp * this.getWidth()), this.getTop() - (int) (temp * this.getHeight()), this.getRight()
					+ (int) (temp * this.getWidth()), this.getBottom() + (int) (temp * this.getHeight()));
		} else if (flag == SMALLER) {
			temp = temp / 2;
			this.setFrame(this.getLeft() + (int) (temp * this.getWidth()), this.getTop() + (int) (temp * this.getHeight()), this.getRight()
					- (int) (temp * this.getWidth()), this.getBottom() - (int) (temp * this.getHeight()));
		}
	}

	public void setPosition(int left, int top, int right, int bottom) {
		this.layout(left, top, right, bottom);
	}

}
