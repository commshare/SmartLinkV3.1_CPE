package com.alcatel.smartlinkv3.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.CPEConfig;

public class SettingShareActivity extends BaseActivity implements OnClickListener{
	
		private TextView m_tv_title = null;
		private ImageButton m_ib_back=null;
		private TextView m_tv_back=null;
		private TextView m_tv_done;
		private FrameLayout m_fl_titlebar;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.activity_setting_share);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
			controlTitlebar();
		}
		
		private void controlTitlebar(){
			m_tv_title = (TextView)findViewById(R.id.tv_title_title);
			m_tv_title.setText(R.string.setting_sharing);
			//back button and text
			m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
			m_tv_back = (TextView)findViewById(R.id.tv_title_back);
			
			m_fl_titlebar = (FrameLayout)findViewById(R.id.fl_edit_or_done);
			m_fl_titlebar.setVisibility(View.VISIBLE);
			
			m_tv_done = (TextView)findViewById(R.id.tv_titlebar_done);
			m_tv_done.setVisibility(View.GONE);
			
			findViewById(R.id.tv_titlebar_edit).setVisibility(View.GONE);
			
			m_ib_back.setOnClickListener(this);
			m_tv_back.setOnClickListener(this);
			m_tv_done.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int nID = v.getId();
			switch (nID) {
			case R.id.tv_title_back:
			case R.id.ib_title_back:
				SettingShareActivity.this.finish();
				break;
			
			default:
				break;
			}
		}
}
