package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingNetworkActivity extends BaseActivity implements OnClickListener{
	
	private TextView m_tv_title = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private FrameLayout m_network_mode=null;
	private FrameLayout m_network_selection=null;
	private FrameLayout m_network_profile_management=null;
	private LinearLayout m_level_one_menu=null;
	
	private FragmentManager m_fragment_manager;
	FragmentTransaction m_transaction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_network);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		
		controlTitlebar();
		initUi();
	}
	
	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_network);
		//back button and text
		m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_back = (TextView)findViewById(R.id.tv_title_back);
		m_ib_back.setOnClickListener(this);
		m_tv_back.setOnClickListener(this);
	}
	
	private void initUi(){
		m_network_mode = (FrameLayout) findViewById(R.id.network_mode);
		m_network_selection = (FrameLayout) findViewById(R.id.network_selection);
		m_network_profile_management = (FrameLayout) findViewById(R.id.network_profile_management);
		
		m_network_mode.setOnClickListener(this);
		m_network_selection.setOnClickListener(this);
		m_network_profile_management.setOnClickListener(this);
		
		m_level_one_menu = (LinearLayout) findViewById(R.id.level_one_menu);
		m_fragment_manager = getFragmentManager();
	}
	
	private void popFragment(Fragment menu){
		m_level_one_menu.setVisibility(View.GONE);
		m_transaction= m_fragment_manager.beginTransaction();
		m_transaction.replace(R.id.setting_network_content, menu);
		m_transaction.addToBackStack(null);
		m_transaction.commit();
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
		case R.id.network_mode:
			popFragment(new FragmentNetworkMode());
			break;
		case R.id.network_selection:
			popFragment(new FragmentNetworkSelection());
			break;
		case R.id.network_profile_management:
			popFragment(new FragmentProfileManagement());
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		m_level_one_menu.setVisibility(View.VISIBLE);
	}
}
