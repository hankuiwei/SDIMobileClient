package com.lenovo.sdimobileclient.ui.view;

import java.io.File;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ZoomControls;

public class ViewArea extends FrameLayout { // 前面说了ViewArea是一个布局，
											// 所以这里当然要继承一个布局了。LinearLayout也可以
	private int imgDisplayW;
	private int imgDisplayH;
	private ImageView touchView;
	private static final int ZOOMCONTROL_MAX = 3;
	private int mCurrentZoom;

	public ViewArea(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ViewArea(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewArea(Context context) {
		super(context);
	}

	// resId为图片资源id
	public ViewArea(Context context, ZoomControls zoomControls) {
		super(context);
		imgDisplayW = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();// 这里的宽高要和xml中的LinearLayout大小一致，如果要指定大小。xml中
																								// LinearLayout的宽高一定要用dip像素单位，因为这里的宽高是像素，用px会有误差！
		imgDisplayH = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight() - 150;
		int version = android.os.Build.VERSION.SDK_INT;
		zoomControls.setIsZoomInEnabled(true);
		zoomControls.setIsZoomOutEnabled(true);
		if (version >= 7) {
			touchView = new TouchView(context, imgDisplayW, imgDisplayH);
			zoomControls.setVisibility(View.GONE);
		} else {
			final ImageTouchView imageTouchView = new ImageTouchView(context, imgDisplayW, imgDisplayH);
			touchView = imageTouchView;
			zoomControls.setOnZoomInClickListener(new OnClickListener() {
				public void onClick(View v) {
					++mCurrentZoom;
					imageTouchView.setScale(ImageTouchView.SCALE, ImageTouchView.BIGGER);
					if (mCurrentZoom >= ZOOMCONTROL_MAX) {
						v.setEnabled(false);
					}
				}
			});

			zoomControls.setOnZoomOutClickListener(new OnClickListener() {
				public void onClick(View v) {
					--mCurrentZoom;
					imageTouchView.setScale(ImageTouchView.SCALE, ImageTouchView.SMALLER);
					if (mCurrentZoom + ZOOMCONTROL_MAX <= 0) {
						v.setEnabled(false);
					}
				}
			});

		}
		this.addView(touchView);
	}

	public void setImageDrawable(String url) {

		File file = new File(url);

		Picasso.with(getContext()).load(file).config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(touchView);
		touchView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT));
		touchView.setScaleType(ImageView.ScaleType.FIT_CENTER);

	}
}