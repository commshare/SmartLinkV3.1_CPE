package com.alcatel.smartlinkv3.ui.view;

import com.alcatel.smartlinkv3.R;

import android.content.Context;
import android.view.LayoutInflater;

public class ViewMicroSD extends BaseViewImpl{
	
	@Override
	protected void init(){
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_microsd,
				null);
	}

	public ViewMicroSD(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

}
