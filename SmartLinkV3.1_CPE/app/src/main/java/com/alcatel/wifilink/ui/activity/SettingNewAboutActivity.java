package com.alcatel.wifilink.ui.activity;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.common.CommonUtil;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingNewAboutActivity  extends BaseActivity implements OnClickListener{
	
	private TextView m_tv_title = null;
	private ImageButton m_ib_title_back = null;
	private TextView m_tv_title_back = null;
	
	private TextView m_app_version = null;
//	private FrameLayout m_feedback = null;
	private FrameLayout m_official_website = null;
	private LinearLayout m_menu_container = null;
	
//	private FragmentAboutFeedback m_fragment_feedback = null;
//	private FragmentManager m_fragment_manager;
//	private FragmentTransaction m_transaction;
	
	private boolean m_is_feedback_shown;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_about_new);
		getWindow().setBackgroundDrawable(null);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		//control title bar
		controlTitlebar();
		//create controls
		//app version
		initUi();

	}
	
	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_about);
		m_ib_title_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_title_back = (TextView)findViewById(R.id.tv_title_back);
		m_ib_title_back.setOnClickListener(this);
		m_tv_title_back.setOnClickListener(this);
	}
	
	private void initUi(){
		m_app_version = (TextView)findViewById(R.id.device_about_app_version);
		
//		m_feedback = (FrameLayout)findViewById(R.id.device_about_feedback);
//		m_feedback.setOnClickListener(this);
		
		m_official_website = (FrameLayout)findViewById(R.id.device_about_official_website);
		m_official_website.setOnClickListener(this);
		
		String strVersionString = BusinessManager.getInstance().getAppVersion();
		String strTemp = getString(R.string.setting_about_version) + " " + strVersionString;
		m_app_version.setText(strTemp);
		
		m_menu_container = (LinearLayout) findViewById(R.id.device_about_menu_container);
		
//		m_fragment_manager = getFragmentManager();
//		m_fragment_feedback = new FragmentAboutFeedback();
		
		m_is_feedback_shown = false;
		updateMenuVisibility(View.VISIBLE);
	}
	
	public void updateMenuVisibility(final int visibility){
		m_menu_container.setVisibility(visibility);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.tv_title_back:
		case R.id.ib_title_back:
			this.onBackPressed();
			break;
		case R.id.device_about_official_website:
			CommonUtil.openWebPage(this, "http://www.alcatelonetouch.com");
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed(){
		if(m_is_feedback_shown){
			updateMenuVisibility(View.VISIBLE);
			m_is_feedback_shown = false;
		}
		super.onBackPressed();
	}
	
	@Override
	protected void onResume(){
		m_bNeedBack = false;
		super.onResume();
	}

}
