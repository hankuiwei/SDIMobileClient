/**
 * 
 */
package com.lenovo.sdimobilecllient.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Mr.Wang
 * 
 *         增加 检查textView 在singleline= true 的时候.是否有隐藏内容
 *
 */
public class SingleLineTextView extends TextView {

	public SingleLineTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SingleLineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SingleLineTextView(Context context) {
		super(context);
	}

	private int getAvailableWidth() {
		return getWidth() - getPaddingLeft() - getPaddingRight();
	}

	/**
	 * 检查是否有隐藏内容
	 * 
	 * @return
	 */
	public boolean isOverFlowed() {
		Paint paint = getPaint();
		float width = paint.measureText(getText().toString());
		int availableWidth = getAvailableWidth();
		
		if (width > getAvailableWidth())
			return true;
		return false;
	}

}
