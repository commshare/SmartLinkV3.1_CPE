package com.alcatel.wifilink.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class SquareLayout extends TextView{

	public SquareLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public SquareLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);
		setMeasuredDimension(measuredHeight, measuredHeight);

	}
	
	private int measureHeight(int measureSpec) {

		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// Default size if no limits are specified.

		int result = 500;
		if (specMode == MeasureSpec.AT_MOST) {

			// Calculate the ideal size of your
			// control within this maximum size.
			// If your control fills the available
			// space return the outer bound.

			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {

			// If your control can fit within these bounds return that
			// value.
			result = specSize;
		}

		return result;
	}

	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// Default size if no limits are specified.
		int result = 500;
		if (specMode == MeasureSpec.AT_MOST) {
			// Calculate the ideal size of your control
			// within this maximum size.
			// If your control fills the available space
			// return the outer bound.
			result = specSize;
		}

		else if (specMode == MeasureSpec.EXACTLY) {
			// If your control can fit within these bounds return that
			// value.

			result = specSize;
		}

		return result;
	}

}
