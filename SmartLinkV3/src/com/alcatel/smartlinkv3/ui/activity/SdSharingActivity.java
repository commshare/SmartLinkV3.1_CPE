package com.alcatel.smartlinkv3.ui.activity;


import com.alcatel.smartlinkv3.ui.activity.StorageMainActivity;
import com.alcatel.smartlinkv3.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;



public class SdSharingActivity extends BaseActivity implements OnClickListener{

	
	private RelativeLayout m_layoutStorage = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sd_sharing_view);
		
		m_layoutStorage = (RelativeLayout)findViewById(R.id.layout_storage);
		m_layoutStorage.setOnClickListener(this);
		
	}

	

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {	

		Intent intent =  null;
		switch (v.getId()) {

		case R.id.layout_storage:	
			intent = new Intent(this, StorageMainActivity.class);
			break;
		}	
		
		if(intent != null)
			startActivity(intent);
	
	}

}