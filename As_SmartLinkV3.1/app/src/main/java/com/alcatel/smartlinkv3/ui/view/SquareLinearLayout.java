package com.alcatel.smartlinkv3.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SquareLinearLayout extends ImageView{

	public SquareLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public SquareLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = View.MeasureSpec.getSize(widthMeasureSpec);
		int height = View.MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, width);
	}
}

