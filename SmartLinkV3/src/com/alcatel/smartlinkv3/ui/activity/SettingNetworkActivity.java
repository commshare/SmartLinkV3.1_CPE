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
	
	private final String TAG_FRAGMENT_NETWORK_MODE = "FRAGMENT_NETWORK_MODE";
	private final String TAG_FRAGMENT_NETWORK_SELECTION = "FRAGMENT_NETWORK_SELECTION";
	private final String TAG_FRAGMENT_PROFILE_MANAGEMENT = "FRAGMENT_NETWORK_MANAGEMENT";
	private final String TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL = "FRAGMENT_NETWORK_MANAGEMENT_DETAIL";
	private final String TAG_FRAGMENT_PROFILE_MANAGEMENT_PROTOCAL = "FRAGMENT_NETWORK_MANAGEMENT_PROTOCAL";
	
	private TextView m_tv_title = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private FrameLayout m_network_mode=null;
	private FrameLayout m_network_selection=null;
	private FrameLayout m_network_profile_management=null;
	private LinearLayout m_level_one_menu=null;
	private LinearLayout m_add_and_delete_container = null;
	
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
	
	public void changeTitlebar(int title){
		m_tv_title.setText(title);
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
		
		m_add_and_delete_container = (LinearLayout)findViewById(R.id.fl_add_and_delete);
		m_add_and_delete_container.setVisibility(View.GONE);
	}
	
	private void popFragment(Fragment menu, String fragmentTag){
		m_level_one_menu.setVisibility(View.GONE);
		m_transaction= m_fragment_manager.beginTransaction();
		m_transaction.replace(R.id.setting_network_content, menu, fragmentTag);
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
			popFragment(new FragmentNetworkMode(), TAG_FRAGMENT_NETWORK_MODE);
			break;
		case R.id.network_selection:
			popFragment(new FragmentNetworkSelection(), TAG_FRAGMENT_NETWORK_SELECTION);
			break;
		case R.id.network_profile_management:
			popFragment(new FragmentProfileManagement(), TAG_FRAGMENT_PROFILE_MANAGEMENT);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed(){
		m_level_one_menu.setVisibility(View.VISIBLE);
		changeTitlebar(R.string.setting_network);
		if(m_fragment_manager.findFragmentByTag(TAG_FRAGMENT_PROFILE_MANAGEMENT) != null 
				&& m_add_and_delete_container.getVisibility() == View.VISIBLE){
			m_add_and_delete_container.setVisibility(View.GONE);
		}
		super.onBackPressed();
	}
}
