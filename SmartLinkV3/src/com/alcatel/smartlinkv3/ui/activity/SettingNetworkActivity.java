package com.alcatel.smartlinkv3.ui.activity;

import java.util.Stack;

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
	
	public static final String TAG_FRAGMENT_NETWORK_MODE = "FRAGMENT_NETWORK_MODE";
	public static final String TAG_FRAGMENT_NETWORK_SELECTION = "FRAGMENT_NETWORK_SELECTION";
	public static final String TAG_FRAGMENT_PROFILE_MANAGEMENT = "FRAGMENT_NETWORK_MANAGEMENT";
	public static final String TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL = "FRAGMENT_NETWORK_MANAGEMENT_DETAIL";
	
	private static FragmentNetworkMode m_fragment_network_mode = null;
	private static FragmentNetworkSelection m_fragment_network_selection = null;
	private static FragmentProfileManagement m_fragment_profile_management = null;
	private static FragmentProfileManagementDetail m_fragment_profile_management_detail = null;
	
	private static Stack<String> m_fragment_tag_stack = null;
	
	private static TextView m_tv_title = null;
	private static ImageButton m_ib_back=null;
	private static TextView m_tv_back=null;
	private static FrameLayout m_network_mode=null;
	private static FrameLayout m_network_selection=null;
	private static FrameLayout m_network_profile_management=null;
	private static LinearLayout m_level_one_menu=null;
	private static LinearLayout m_add_and_delete_container = null;
	
	private static FragmentManager m_fragment_manager;
	private static FragmentTransaction m_transaction;
	
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
		
		m_fragment_tag_stack = new Stack<String>();
		
		m_fragment_network_mode = new FragmentNetworkMode();
		m_fragment_network_selection = new FragmentNetworkSelection();
		m_fragment_profile_management = new FragmentProfileManagement();
		m_fragment_profile_management_detail = new FragmentProfileManagementDetail();
	}
	
	public void showFragmentNetworkMode(){
		showFragment(m_fragment_network_mode, TAG_FRAGMENT_NETWORK_MODE);
	}
	
	public void showFragmentNetworkSelection(){
		showFragment(m_fragment_network_selection, TAG_FRAGMENT_NETWORK_SELECTION);
	}
	
	public void showFragmentProfileManagement(){
		showFragment(m_fragment_profile_management, TAG_FRAGMENT_PROFILE_MANAGEMENT);
	}
	
	public void showFragmentProfileManagementDetail(){
		showFragment(m_fragment_profile_management_detail, TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL);
	}
	
	private void showFragment(Fragment menu, String fragmentTag){
		addToFragmentTagStack(fragmentTag);
		m_level_one_menu.setVisibility(View.GONE);
		m_transaction= m_fragment_manager.beginTransaction();
		m_transaction.replace(R.id.setting_network_content, menu, fragmentTag);
		m_transaction.addToBackStack(null);
		m_transaction.commit();
	}
	
	public FragmentManager getSettingNetworkFragmentManager(){
		return m_fragment_manager;
	}
	
	public String getCurrentFragmentTag(){
		return m_fragment_tag_stack.peek();
	}
	
	public void addToFragmentTagStack(String fragmentTag){
		m_fragment_tag_stack.push(fragmentTag);
	}
	
	public void popFragmentTagStack(){
		if(!m_fragment_tag_stack.isEmpty())
			m_fragment_tag_stack.pop();
	}
	
	public void setAddAndDeleteVisibility(final int visibility){
		m_add_and_delete_container.setVisibility(visibility);
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
			showFragment(m_fragment_network_mode, TAG_FRAGMENT_NETWORK_MODE);
			break;
		case R.id.network_selection:
			showFragment(m_fragment_network_selection, TAG_FRAGMENT_NETWORK_SELECTION);
			break;
		case R.id.network_profile_management:
			showFragment(m_fragment_profile_management, TAG_FRAGMENT_PROFILE_MANAGEMENT);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed(){
		popFragmentTagStack();
		if(!m_fragment_tag_stack.isEmpty()){
			m_level_one_menu.setVisibility(View.GONE);
			String FragmentTag = m_fragment_tag_stack.peek();
			
			if(FragmentTag.equals(TAG_FRAGMENT_PROFILE_MANAGEMENT)){
				changeTitlebar(R.string.setting_network_profile_management);
				setAddAndDeleteVisibility(View.VISIBLE);
			}
			else if(FragmentTag.equals(TAG_FRAGMENT_NETWORK_SELECTION)){
				changeTitlebar(R.string.setting_network_selection);
				setAddAndDeleteVisibility(View.GONE);
			}
			else if(FragmentTag.equals(TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL)){
				changeTitlebar(R.string.setting_network_profile_management_profile_detail);
				setAddAndDeleteVisibility(View.GONE);
			}
			else if(FragmentTag.equals(TAG_FRAGMENT_NETWORK_MODE)){
				changeTitlebar(R.string.setting_network_mode);
				setAddAndDeleteVisibility(View.GONE);
			}
		}
		else{
			m_level_one_menu.setVisibility(View.VISIBLE);
			changeTitlebar(R.string.setting_network);
			setAddAndDeleteVisibility(View.GONE);
		}
		super.onBackPressed();
	}
}
