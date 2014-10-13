package com.alcatel.ui.view;

import java.util.ArrayList;
import java.util.Map;

import com.alcatel.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;


public class ViewUsage extends BaseViewImpl {
	private static final String TAG = ViewUsage.class.getSimpleName();


	public ViewUsage(Context context) {
		super(context);
		init();
	}

	@Override
	protected void init() {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_usage,null);
	}

	@Override
	public void onResume() {}

	@Override
	public void onPause() {}

	@Override
	public void onDestroy() {}
    
}

