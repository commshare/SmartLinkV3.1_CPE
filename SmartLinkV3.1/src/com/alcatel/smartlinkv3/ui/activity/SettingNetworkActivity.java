package com.alcatel.smartlinkv3.ui.activity;

import java.util.List;
import java.util.Stack;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BaseManager;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.profile.HttpGetProfileList.ProfileItem;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.HttpMethodUti;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SettingNetworkActivity extends BaseFragmentActivity implements OnClickListener{
	
	public final String TAG_FRAGMENT_NETWORK_MODE = "FRAGMENT_NETWORK_MODE";
	public final String TAG_FRAGMENT_NETWORK_SELECTION = "FRAGMENT_NETWORK_SELECTION";
	public final String TAG_FRAGMENT_PROFILE_MANAGEMENT = "FRAGMENT_NETWORK_MANAGEMENT";
	public final String TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL = "FRAGMENT_NETWORK_MANAGEMENT_DETAIL";
	
	//////////////////////////////profile tags start/////////////////////////////////
	public final String TAG_OPERATION = "operation";
	public final String TAG_OPERATION_EDIT_PROFILE = "edit_profile";
	public final String TAG_OPERATION_ADD_PROFILE = "add_profile";
	public final String TAG_PROFILE_NAME = "name";
	public final String TAG_APN = "apn";
	public final String TAG_USER_NAME = "usre_name";
	public final String TAG_PASSWORD = "password";
	public final String TAG_DEFAULT = "default";
	public final String TAG_PROFILE_ID = "profile_id";
	public final String TAG_IS_PREDEFINE = "is_predefine";
	public final String TAG_AUTH_TYPE = "auth_type";
	public final String TAG_DAIL_NUMBER = "dial_number";
	public final String TAG_IP_ADDRESS = "ip_address";
//////////////////////////////profile tags end/////////////////////////////////
	
	private FragmentNetworkSelection m_fragment_network_selection = null;
	private FragmentProfileManagement m_fragment_profile_management = null;
	private FragmentProfileManagementDetail m_fragment_profile_management_detail = null;
	
	private Stack<String> m_fragment_tag_stack = null;
	
	private TextView m_tv_done = null;
	private TextView m_tv_edit = null;
	private FrameLayout m_edit_or_done_container = null;
	
	
	private TextView m_tv_title = null;
	private ImageButton m_ib_back=null;
	private TextView m_tv_back=null;
	private FrameLayout m_network_mode_container=null;
	private FrameLayout m_network_selection_container=null;
	private FrameLayout m_network_profile_management=null;
	private LinearLayout m_level_one_menu=null;
	private LinearLayout m_add_and_delete_container = null;
	
	private TextView m_add_profile;
	private TextView m_delete_profile;
	private List<ProfileItem> m_profile_list_data = null;
	
	private FragmentManager m_fragment_manager;
	private FragmentTransaction m_transaction;
	
	private RelativeLayout m_waiting_circle;
	private TextView m_mode_desc;
	private TextView m_selection_desc;
	private TextView m_selected_profile;
	
	private IntentFilter m_get_network_setting_filter;
	private IntentFilter m_set_network_setting_filter;
	private NetworkSettingReceiver m_network_setting_receiver;
	
	
	private RadioGroup m_network_mode_radiogroup = null;
	private final int MODE_ERROR = -1;
	private int m_current_mode = MODE_ERROR;
	private final int MODE_AUTO = 0;
	private final int MODE_2G_ONLY = 1;
	private final int MODE_3G_ONLY = 2;
	private final int MODE_LTE_ONLY = 3;
	private int m_current_network_selection_mode = -1;
	
	private RadioButton mode_auto = null;
	private RadioButton mode_2g_only = null;
	private RadioButton mode_3g_only = null;
	private RadioButton mode_lte_only = null;

	private boolean m_delete_menu = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().setBackgroundDrawable(null);
		setContentView(R.layout.activity_setting_network);
		getWindow().setBackgroundDrawable(null);
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
		
		m_get_network_setting_filter = new IntentFilter(MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST);
		m_set_network_setting_filter = new IntentFilter(MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST);
		m_get_network_setting_filter.addAction(MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST);
		m_set_network_setting_filter.addAction(MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST);
		m_network_setting_receiver = new NetworkSettingReceiver();
		
		m_network_mode_container = (FrameLayout) findViewById(R.id.network_mode);
		m_network_selection_container = (FrameLayout) findViewById(R.id.network_selection);
		m_network_profile_management = (FrameLayout) findViewById(R.id.network_profile_management);
		m_selected_profile = (TextView)findViewById(R.id.network_selected_profile);
		
		m_mode_desc = (TextView)findViewById(R.id.network_mode_desc);
		m_selection_desc = (TextView)findViewById(R.id.network_selection_desc);
		
		m_waiting_circle = (RelativeLayout) findViewById(R.id.waiting_progressbar);
		
		m_network_mode_container.setOnClickListener(this);
		m_network_selection_container.setOnClickListener(this);
		m_network_profile_management.setOnClickListener(this);
		
		m_level_one_menu = (LinearLayout) findViewById(R.id.level_one_menu);
		m_fragment_manager = this.getSupportFragmentManager();
		
		m_add_and_delete_container = (LinearLayout)findViewById(R.id.fl_add_and_delete);
		m_add_and_delete_container.setVisibility(View.GONE);
		
		m_add_profile = (TextView) findViewById(R.id.tv_titlebar_add);
		m_add_profile.setOnClickListener(this);
		m_delete_profile = (TextView) findViewById(R.id.tv_titlebar_delete);
		m_delete_profile.setOnClickListener(this);
		
		
		m_edit_or_done_container = (FrameLayout) findViewById(R.id.fl_edit_or_done);
		m_edit_or_done_container.setVisibility(View.GONE);
		m_tv_edit = (TextView)findViewById(R.id.tv_titlebar_edit);
		m_tv_edit.setVisibility(View.GONE);
		m_tv_done = (TextView)findViewById(R.id.tv_titlebar_done);
		m_tv_done.setVisibility(View.VISIBLE);
		m_tv_done.setOnClickListener(this);
		
		m_fragment_tag_stack = new Stack<String>();
		
		m_fragment_network_selection = new FragmentNetworkSelection();
		m_fragment_profile_management = new FragmentProfileManagement();
		m_fragment_profile_management_detail = new FragmentProfileManagementDetail();
		
		
		
		
		
		changeTitlebar(R.string.setting_network_mode);
		m_network_mode_radiogroup = (RadioGroup)findViewById(R.id.setting_network_mode);
		m_current_mode = BusinessMannager.getInstance().getNetworkManager().getNetworkMode();
		m_current_network_selection_mode = BusinessMannager.getInstance().getNetworkManager().getNetworkSelection();
		
		mode_auto = (RadioButton)findViewById(R.id.mode_auto);
		mode_2g_only = (RadioButton)findViewById(R.id.mode_2g);
		mode_3g_only = (RadioButton)findViewById(R.id.mode_3g);
		mode_lte_only = (RadioButton)findViewById(R.id.mode_lte);
		
		m_network_mode_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				
				switch(checkedId){
				case R.id.mode_auto:
					UserSetNetworkMode(MODE_AUTO);
					break;
				case R.id.mode_2g:
					UserSetNetworkMode(MODE_2G_ONLY);
					break;
				case R.id.mode_3g:
					UserSetNetworkMode(MODE_3G_ONLY);
					break;
				case R.id.mode_lte:
					UserSetNetworkMode(MODE_LTE_ONLY);
					break;
				default:
					break;
				}
			}
			
		});
		
//		refresButton();
		
		
	}
	
	public void showFragmentNetworkSelection(){
		showFragment(m_fragment_network_selection, TAG_FRAGMENT_NETWORK_SELECTION);
	}
	
	public void showFragmentProfileManagement(){
		showFragment(m_fragment_profile_management, TAG_FRAGMENT_PROFILE_MANAGEMENT);
	}
	
	public void showFragmentProfileManagementDetail(Bundle data){
		addToFragmentTagStack(TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL);
		m_level_one_menu.setVisibility(View.GONE);
		m_transaction= m_fragment_manager.beginTransaction();
		m_fragment_profile_management_detail.setArguments(null);
		m_fragment_profile_management_detail.setArguments(data);
		m_transaction.replace(R.id.setting_network_content, m_fragment_profile_management_detail, TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL);
		m_transaction.addToBackStack(null);
		m_transaction.commit();
		m_edit_or_done_container.setVisibility(View.VISIBLE);
	}
	
	public void showAddFragmentProfileManagementDetail(){
		
		Bundle dataBundle = new Bundle();
		dataBundle.putString(TAG_OPERATION, TAG_OPERATION_ADD_PROFILE);
		m_fragment_profile_management_detail.setArguments(dataBundle);
		showFragment(m_fragment_profile_management_detail, TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL);
		m_edit_or_done_container.setVisibility(View.VISIBLE);
	}
	
	private void showFragment(Fragment menu, String fragmentTag){
		addToFragmentTagStack(fragmentTag);
		m_level_one_menu.setVisibility(View.GONE);
		m_transaction= m_fragment_manager.beginTransaction();
		menu.setArguments(null);
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
	
	public void setEditOrDoneVisibility(final int visibility){
		m_edit_or_done_container.setVisibility(visibility);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		ConnectStatusModel internetConnState = BusinessMannager.getInstance().getConnectStatus();
		switch (nID) {
		case R.id.tv_title_back:
		case R.id.ib_title_back:
			this.onBackPressed();
			break;
		case R.id.network_mode:
//			showFragment(m_fragment_network_mode, TAG_FRAGMENT_NETWORK_MODE);
//			BusinessMannager.getInstance().getProfileManager().startAddNewProfile(null);
//			BusinessMannager.getInstance().getProfileManager().startDeleteProfile(null);
			if(internetConnState.m_connectionStatus == ConnectionStatus.Disconnected)
				m_network_mode_radiogroup.setVisibility(View.VISIBLE);
			else if(internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting){
				String strInfo = getString(R.string.setting_network_try_again);
				Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			}
			else{
				String strInfo = getString(R.string.setting_network_disconnect_first);
				Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.network_selection:
			 if(BusinessMannager.getInstance().getSimStatus().m_SIMState == SIMState.Accessable){
				 if(internetConnState.m_connectionStatus == ConnectionStatus.Disconnected)
						showFragment(m_fragment_network_selection, TAG_FRAGMENT_NETWORK_SELECTION);
					else if(internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting){
						String strInfo = getString(R.string.setting_network_try_again);
						Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
					}
					else{
						String strInfo = getString(R.string.setting_network_disconnect_first);
						Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
					}
			 }
			 else if(BusinessMannager.getInstance().getSimStatus().m_SIMState == SIMState.PinRequired){
			 	String strInfo = getString(R.string.home_sim_loched);
				Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			 }
			 else if(BusinessMannager.getInstance().getSimStatus().m_SIMState == SIMState.PukRequired){
			 	String strInfo = getString(R.string.home_sim_loched);
				Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			 }
			 else{
			 	String strInfo = getString(R.string.home_sim_not_accessible);
				Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			 }
			break;
		case R.id.network_profile_management:
			if(internetConnState.m_connectionStatus == ConnectionStatus.Disconnected)
				showFragment(m_fragment_profile_management, TAG_FRAGMENT_PROFILE_MANAGEMENT);
			else if(internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting){
				String strInfo = getString(R.string.setting_network_try_again);
				Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			}
			else{
				String strInfo = getString(R.string.setting_network_disconnect_first);
				Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tv_titlebar_add:
			if(m_fragment_tag_stack.size() > 0){
				Log.v("STACKFRAGMENT", m_fragment_tag_stack.peek());
			}
//			setAddAndDeleteVisibility(View.GONE);
			Bundle dataBundle = new Bundle();
			dataBundle.putString(TAG_OPERATION, TAG_OPERATION_ADD_PROFILE);
			showFragmentProfileManagementDetail(dataBundle);
			m_add_and_delete_container.setVisibility(View.GONE);
			break;
		case R.id.tv_titlebar_delete:
			if(m_fragment_tag_stack.size() > 0){
				Log.v("STACKFRAGMENT", m_fragment_tag_stack.peek());
			}
			m_add_and_delete_container.setVisibility(View.GONE);
			m_edit_or_done_container.setVisibility(View.VISIBLE);
			m_tv_title.setText(R.string.setting_network_profile_management_delete_profile);
			m_delete_menu = true;
			break;
		case R.id.tv_titlebar_done:
			if(!m_fragment_tag_stack.isEmpty()){
				String FragmentTag = m_fragment_tag_stack.peek();
				if(FragmentTag.equals(TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL)){
					m_fragment_profile_management_detail.AddOrDeleteProfile();
//					Log.v("AddOrEditProfile", "TEST");
//					this.onBackPressed();
				}
				if(FragmentTag.equals(TAG_FRAGMENT_PROFILE_MANAGEMENT)){
					m_edit_or_done_container.setVisibility(View.INVISIBLE);
					m_add_and_delete_container.setVisibility(View.VISIBLE);
					m_tv_title.setText(R.string.setting_network_profile_management);
					m_delete_menu = false;
				}
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		m_fragment_tag_stack.clear();
		m_fragment_tag_stack = null;
		m_fragment_network_selection = null;
		m_fragment_profile_management = null;
		m_fragment_profile_management_detail = null;
		
		try{
			unregisterReceiver(m_network_setting_receiver);  
		}
		catch(Exception e){
			
		}
		
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		popFragmentTagStack();
		setEditOrDoneVisibility(View.GONE);
		if(!m_fragment_tag_stack.isEmpty()){
			m_level_one_menu.setVisibility(View.GONE);
			String FragmentTag = m_fragment_tag_stack.peek();
			
			if(FragmentTag.equals(TAG_FRAGMENT_PROFILE_MANAGEMENT)){
				changeTitlebar(R.string.setting_network_profile_list);
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
	}
	
	private void UserGetNetworkSetting(){
		registerReceiver(m_network_setting_receiver, m_get_network_setting_filter);  
		registerReceiver(m_network_setting_receiver, m_set_network_setting_filter);  
		BusinessMannager.getInstance().sendRequestMessage(
				MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST, null);
		m_waiting_circle.setVisibility(View.VISIBLE);
		m_network_mode_container.setEnabled(false);
		m_network_selection_container.setEnabled(false);
		m_network_profile_management.setEnabled(false);
	}
	
	public boolean isDeleteMode(){
		return m_delete_menu;
	}
	
	
	private void refresButton(){
		switch(BusinessMannager.getInstance().getNetworkManager().getNetworkMode()){
		case MODE_AUTO:
			mode_auto.setChecked(true);
			
			break;
		case MODE_2G_ONLY:
			
			mode_2g_only.setChecked(true);
			
			break;
		case MODE_3G_ONLY:
			
			mode_3g_only.setChecked(true);
			
			break;
		case MODE_LTE_ONLY:
		
			mode_lte_only.setChecked(true);
			break;
		default:
			break;
		}
	}
	
	private void UserSetNetworkMode(final int mode){
		if(BusinessMannager.getInstance().getNetworkManager().getNetworkSelection() != MODE_ERROR){
			DataValue data = new DataValue();
			data.addParam("network_mode", mode);
			data.addParam("netselection_mode", BusinessMannager.getInstance().getNetworkManager().getNetworkSelection());
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST, data);
		}
		m_network_mode_radiogroup.setVisibility(View.GONE);
		m_waiting_circle.setVisibility(View.VISIBLE);
		m_network_mode_container.setEnabled(false);
		m_network_selection_container.setEnabled(false);
		m_network_profile_management.setEnabled(false);
	}
	
	public void setDeleteProfle(boolean enable){
		m_delete_menu = enable;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
//		BusinessMannager.getInstance().getNetworkManager().GetNetworkSettings(null);
		
//		DataValue data = new DataValue();
//		data.addParam("network_mode", 0);
//		data.addParam("netselection_mode", 0);
//		BusinessMannager.getInstance().sendRequestMessage(
//				MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST, data);
		UserGetNetworkSetting();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.registerReceiver(m_network_setting_receiver, new IntentFilter(MessageUti.PROFILE_GET_PROFILE_LIST_REQUEST));  
		BusinessMannager.getInstance().getProfileManager().startGetProfileList(null);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	private class NetworkSettingReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equalsIgnoreCase(
					MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				
				if (BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() == 0){
					switch(BusinessMannager.getInstance().getNetworkManager().getNetworkMode()){
					
					case 0:
						m_mode_desc.setText("Auto");
						mode_auto.setChecked(true);
						
						break;
					case 1:
						m_mode_desc.setText("2G only");
						
						mode_2g_only.setChecked(true);
						
						break;
					case 2:
						m_mode_desc.setText("3G only");
						
						mode_3g_only.setChecked(true);
						
						break;
					case 3:
						m_mode_desc.setText("4G only");
						
						mode_lte_only.setChecked(true);
						break;
					default:
						m_mode_desc.setText("Error");
						break;
						
					}
					
					switch(BusinessMannager.getInstance().getNetworkManager().getNetworkSelection()){
					case 0:
						m_selection_desc.setText("Auto");
						break;
					case 1:
						m_selection_desc.setText("Manual");
						break;
					default:
						break;
					}
					
//					current_network_mode = BusinessMannager.getInstance().getNetworkManager().getNetworkMode();
//					current_network_selection_mode = BusinessMannager.getInstance().getNetworkManager().getNetworkSelection();
					
					m_network_mode_container.setEnabled(true);
					m_network_selection_container.setEnabled(true);
					m_network_profile_management.setEnabled(true);
					m_waiting_circle.setVisibility(View.GONE);
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					//Log
					String strInfo = getString(R.string.unknown_error);
					Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
				}
				m_waiting_circle.setVisibility(View.GONE);
			}
			
			if (intent.getAction().equalsIgnoreCase(
					MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				
				if (BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() == 0){
					BusinessMannager.getInstance().sendRequestMessage(
							MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST, null);
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					//Log
					String strInfo = getString(R.string.unknown_error);
					Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
				}
				m_waiting_circle.setVisibility(View.GONE);
			}
			
			if (intent.getAction().equalsIgnoreCase(
					MessageUti.PROFILE_GET_PROFILE_LIST_REQUEST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				
				if (BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() == 0){
//					m_network_search_result_list = BusinessMannager.getInstance().getNetworkManager().getNetworkList();
					m_profile_list_data = BusinessMannager.getInstance().getProfileManager().GetProfileListData();
					for(ProfileItem a : m_profile_list_data){
						if(a.Default == 1){
							m_selected_profile.setText(a.ProfileName);
							break;
						}
					}
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					String strInfo = getString(R.string.unknown_error);
					Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
				}
				m_waiting_circle.setVisibility(View.GONE);
			}
			
		}
	}
	
}
