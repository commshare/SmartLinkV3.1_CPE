package com.alcatel.smartlinkv3.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

public class FragmentProfileManagementDetail extends Fragment implements OnClickListener{
	
	private SettingNetworkActivity m_parent_activity = null;
	private LinearLayout m_protocol_selection = null;
	private RadioGroup m_protocol_list = null;
	private LinearLayout m_profile_detail_container = null;
	private FrameLayout m_default_switcher = null;
	private TextView m_switch_icon = null;
	private View m_root_view = null;
	
	private static boolean m_is_default = false;
	
	private LinearLayout m_profile_name_container;
	private EditText m_edit_profile_name;
	
	private LinearLayout m_dial_number_container;
	private EditText m_edit_dial_number;
	
	private LinearLayout m_apn_container;
	private EditText m_edit_apn;
	
	private LinearLayout m_user_name_container;
	private EditText m_edit_user_name;
	
	private LinearLayout m_password_container;
	private EditText m_edit_password;
	
	private LinearLayout m_protocol_container;
	private TextView m_protocol_displayer;
	
	private String operation = "";
	private String ProfileName = "";
	private String APN = "3GNET";
	private String UserName = "";
	private String Password = "";
	private int Default = -1;
	private int ProfileID = -1;
	private int IsPredefine = -1;
	private int AuthType = -1;
	private String DailNumber = "*99#";
	private String IPAddress = "";
	
	private int NewAuthType = -1;
	
	private IntentFilter m_set_default_profile_filter;
	private ProfileDetailReceiver m_profile_receiver;
	
	private RelativeLayout m_progress_bar;
	
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
		View rootView = inflater.inflate(R.layout.fragment_profile_management_detail, container, false);
		initUi(rootView);
		m_root_view = rootView;
		
        return rootView;  
    }
	
	private void initUi(View rootView){
		m_parent_activity = (SettingNetworkActivity) getActivity();
		
		m_profile_receiver = new ProfileDetailReceiver();
		m_set_default_profile_filter = new IntentFilter(MessageUti.PROFILE_SET_DEFAULT_PROFILE_REQUEST);
		m_set_default_profile_filter.addAction(MessageUti.PROFILE_SET_DEFAULT_PROFILE_REQUEST);
		getActivity().registerReceiver(m_profile_receiver, m_set_default_profile_filter);  
		
		
		m_protocol_selection = (LinearLayout) rootView.findViewById(R.id.profile_protocal);
		m_protocol_selection.setOnClickListener(this);
		
		m_protocol_list = (RadioGroup) rootView.findViewById(R.id.profile_protocol_selection);
		m_protocol_list.setOnCheckedChangeListener(new ProtocolOnCheckedChangeListener());
		m_protocol_list.setVisibility(View.GONE);
		
		m_profile_detail_container = (LinearLayout) rootView.findViewById(R.id.profile_detail_container);
		m_profile_detail_container.setVisibility(View.VISIBLE);
		
		m_default_switcher = (FrameLayout) rootView.findViewById(R.id.profile_set_default);
		m_default_switcher.setOnClickListener(this);
		
		m_switch_icon = (TextView) rootView.findViewById(R.id.btn_default_switch);
		
		operation = getArguments().getString(m_parent_activity.TAG_OPERATION);
		
		m_profile_name_container = (LinearLayout) rootView.findViewById(R.id.profile_detail_name);
		m_edit_profile_name = (EditText) rootView.findViewById(R.id.edit_profile);
		
		m_dial_number_container = (LinearLayout) rootView.findViewById(R.id.profile_detail_dial_number);
		m_edit_dial_number = (EditText) rootView.findViewById(R.id.edit_dial_number);
		
		m_apn_container = (LinearLayout) rootView.findViewById(R.id.profile_detail_apn);
		m_edit_apn = (EditText) rootView.findViewById(R.id.edit_apn);
		
		m_user_name_container = (LinearLayout) rootView.findViewById(R.id.profile_detail_user_name);
		m_edit_user_name = (EditText) rootView.findViewById(R.id.edit_user_name);
		
		m_password_container = (LinearLayout) rootView.findViewById(R.id.profile_password);
		m_edit_password = (EditText) rootView.findViewById(R.id.edit_password);
		
		m_protocol_container = (LinearLayout) rootView.findViewById(R.id.profile_protocal);
		m_protocol_displayer = (TextView) rootView.findViewById(R.id.edit_protocal);
		
		m_progress_bar = (RelativeLayout) rootView.findViewById(R.id.profile_waiting_progressbar);
		m_progress_bar.setVisibility(View.GONE);
		refreshInfo();
		
	}
	
	private void refreshInfo(){
		
		if(operation == m_parent_activity.TAG_OPERATION_EDIT_PROFILE){
			m_parent_activity.changeTitlebar(R.string.setting_network_profile_management_profile_detail);
			m_default_switcher.setVisibility(View.VISIBLE);
			
			ProfileName = getArguments().getString(m_parent_activity.TAG_PROFILE_NAME);
			APN = getArguments().getString(m_parent_activity.TAG_APN);
			UserName = getArguments().getString(m_parent_activity.TAG_USER_NAME);
			Password = getArguments().getString(m_parent_activity.TAG_PASSWORD);
			Default = getArguments().getInt(m_parent_activity.TAG_DEFAULT);
			ProfileID = getArguments().getInt(m_parent_activity.TAG_PROFILE_ID);
			IsPredefine = getArguments().getInt(m_parent_activity.TAG_IS_PREDEFINE);
			AuthType = getArguments().getInt(m_parent_activity.TAG_AUTH_TYPE);
			DailNumber = getArguments().getString(m_parent_activity.TAG_DAIL_NUMBER);
			
			m_edit_profile_name.setHint(ProfileName);
			m_edit_profile_name.setText(ProfileName.toCharArray() , 0, ProfileName.length());
			m_edit_dial_number.setHint(DailNumber);
			m_edit_dial_number.setText(DailNumber.toCharArray(), 0, DailNumber.length());
//			m_edit_dial_number.setEnabled(false);
			m_edit_apn.setHint(APN);
			m_edit_apn.setText(APN.toCharArray(), 0, APN.length());
			m_edit_user_name.setHint(UserName);
			m_edit_user_name.setText(UserName.toCharArray(), 0, UserName.length());
			m_edit_password.setHint(Password);
			m_edit_password.setText(Password.toCharArray(), 0, Password.length());
			
			if(Default == 1){
				m_switch_icon.setBackgroundResource(R.drawable.general_btn_on);
				m_is_default = true;
			}
			else{
				m_is_default = false;
			}
			
			switch(AuthType){
			case 0:
				m_protocol_displayer.setText(R.string.setting_network_profile_management_protocol_none);
//				m_selected_protocol = 0;
				break;
			case 1:
				m_protocol_displayer.setText(R.string.setting_network_profile_management_auth_type_pap);
//				m_selected_protocol = 1;
				break;
			case 2:
				m_protocol_displayer.setText(R.string.setting_network_profile_management_auth_type_chap);
//				m_selected_protocol = 2;
				break;
			case 3:
				m_protocol_displayer.setText(R.string.setting_network_profile_management_auth_type_pap_and_chap);
//				m_selected_protocol = 3;
				break;
			default:
				break;
			}
			
			if(IsPredefine == 0 || Default == 1){
				m_edit_profile_name.setEnabled(false);
				m_edit_dial_number.setEnabled(false);
				m_edit_apn.setEnabled(false);
				m_edit_user_name.setEnabled(false);
				m_edit_password.setEnabled(false);
				m_protocol_selection.setEnabled(false);
//				m_default_switcher.setEnabled(false);
			}
		}
		else{
			m_parent_activity.changeTitlebar(R.string.setting_network_add_profile);
			m_default_switcher.setVisibility(View.GONE);
			
			ProfileName = "";
			APN = "3GNET";
			UserName = "";
			Password = "";
			Default = -1;
			ProfileID = -1;
			IsPredefine = -1;
			AuthType = -1;
			DailNumber = "*99#";
			
			m_edit_dial_number.setText(DailNumber.toCharArray(), 0, DailNumber.length());
//			m_edit_dial_number.setEnabled(false);
			
			m_edit_profile_name.setHint("Profile Name");
			m_edit_profile_name.setText(ProfileName);
//			m_edit_apn.setHint(APN);
			m_edit_apn.setText(null);
			m_edit_user_name.setHint("User Name");
			m_edit_user_name.setText(UserName.toCharArray(), 0, UserName.length());
			m_edit_password.setHint("Password");
			m_edit_password.setText(Password.toCharArray(), 0, Password.length());
		}
		
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		refreshInfo();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.profile_protocal:
			m_profile_detail_container.setVisibility(View.GONE);
			m_protocol_list.setVisibility(View.VISIBLE);
			m_parent_activity.setEditOrDoneVisibility(View.GONE);
			break;
		case R.id.profile_set_default:
			if(m_is_default == false){
				//m_switch_icon.setBackground(getResources().getDrawable(R.drawable.pwd_switcher_on));
				m_progress_bar.setVisibility(View.VISIBLE);
				DataValue data = new DataValue();
				data.addParam("profile_id", ProfileID);
				BusinessManager.getInstance().getProfileManager().startSetDefaultProfile(data);
			}
			else if(m_is_default == true){
				String strInfo = getString(R.string.setting_network_profile_management_already_default);
				Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	
	private class ProtocolOnCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			switch(checkedId){
			case R.id.protocol_none:
//				m_selected_protocol = 0;
				NewAuthType = 0;
				break;
			case R.id.protocol_tcp:
//				m_selected_protocol = 1;
				NewAuthType = 1;
				break;
			case R.id.protocol_udp:
//				m_selected_protocol = 2;
				NewAuthType = 2;
				break;
			case R.id.protocol_tcp_or_udp:
//				m_selected_protocol = 3;
				NewAuthType = 3;
				break;
			default:
				break;
			}
			
			TextView selected_text = (TextView) group.findViewById(checkedId);
			
			((TextView)m_root_view.findViewById(R.id.edit_protocal)).setText(selected_text.getText());
			
			m_protocol_list.setVisibility(View.GONE);
			m_profile_detail_container.setVisibility(View.VISIBLE);
			m_parent_activity.setEditOrDoneVisibility(View.VISIBLE);
		}
	}
	
	private boolean isChanged(String t_ProfileName, String DialNumber, String t_APN, String t_UserName, String t_Password, int t_AuthType){
		if(!t_ProfileName.equals(ProfileName))
			return true;
		if(!t_APN.equals(APN))
			return true;
		if(!t_UserName.equals(UserName))
			return true;
		if(!t_Password.equals(Password))
			return true;
		if(t_AuthType != AuthType && NewAuthType != -1)
			return true;
		if(!DialNumber.equalsIgnoreCase(DailNumber))
			return true;
		
		return false;
	}
	
	public void AddOrDeleteProfile(){
		if(operation == m_parent_activity.TAG_OPERATION_ADD_PROFILE){
			String profileName = m_edit_profile_name.getText().toString();
			if(profileName.length() == 0){
//				Log.v("AddOrEditProfile", "empty");
				String strInfo = getString(R.string.setting_network_profile_input_profile_name);
				Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
				return;
			}
			if(profileName.contains(Character.toString('/')) || profileName.contains(Character.toString('\\')) ||profileName.contains(Character.toString(':')) ||profileName.contains(Character.toString('*')) ||profileName.contains(Character.toString('?')) ||
				profileName.contains(Character.toString('"')) ||profileName.contains(Character.toString('[')) ||profileName.contains(Character.toString(']')) ||profileName.contains(Character.toString('(')) ||profileName.contains(Character.toString(')')) ||
				profileName.contains(Character.toString('<')) ||profileName.contains(Character.toString('>')) ||profileName.contains(Character.toString('|')) ||profileName.contains(Character.toString('{')) ||profileName.contains(Character.toString('}')) ||
				profileName.contains(Character.toString(';')) ||profileName.contains(Character.toString('\'')) ||profileName.contains(Character.toString(',')) ||profileName.contains(Character.toString('.')) ||profileName.contains(Character.toString('~')) ||profileName.contains(Character.toString('`')) ){
				String strInfo = getString(R.string.setting_network_profile_error_profile_name);
				Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
				return;
			}
			String dialNumber = m_edit_dial_number.getText().toString();
			if(dialNumber.length() >24){
				String strInfo = getString(R.string.setting_network_profile_dial_number_wrong);
				Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
				return;
			}
			if(dialNumber.length() == 0){
				String strInfo = getString(R.string.setting_network_profile_dial_number_empty);
				Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
				return;
			}
			String APN = m_edit_apn.getText().toString();
//			if(APN.length() == 0){
//				APN = "3GNET";
//			}
			String userName = m_edit_user_name.getText().toString();
			if(userName.length() == 0){
				userName = "";
			}
			String passWord = m_edit_password.getText().toString();
			if(passWord.length() == 0){
				passWord = "";
			}
			int auth_type = NewAuthType == -1 ? 0 : NewAuthType;
//			Log.v("AddOrEditProfile", Integer.toString(auth_type));
			
			DataValue data = new DataValue();
			data.addParam("profile_name", profileName);
			data.addParam("apn", APN);
			data.addParam("user_name", userName);
			data.addParam("password", passWord);
			data.addParam("auth_type", auth_type);
			data.addParam("dial_number", dialNumber);
			BusinessManager.getInstance().getProfileManager().startAddNewProfile(data);
		}
		else if(operation == m_parent_activity.TAG_OPERATION_EDIT_PROFILE){
			int profileID = ProfileID;
			if(profileID < 0)
				return;
			String profileName = m_edit_profile_name.getText().toString();
			if(profileName.length() == 0){
				String strInfo = getString(R.string.setting_network_profile_input_profile_name);
				Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
				return;
			}
			if(profileName.contains(Character.toString('/')) || profileName.contains(Character.toString('\\')) ||profileName.contains(Character.toString(':')) ||profileName.contains(Character.toString('*')) ||profileName.contains(Character.toString('?')) ||
				profileName.contains(Character.toString('"')) ||profileName.contains(Character.toString('[')) ||profileName.contains(Character.toString(']')) ||profileName.contains(Character.toString('(')) ||profileName.contains(Character.toString(')')) ||
				profileName.contains(Character.toString('<')) ||profileName.contains(Character.toString('>')) ||profileName.contains(Character.toString('|')) ||profileName.contains(Character.toString('{')) ||profileName.contains(Character.toString('}')) ||
				profileName.contains(Character.toString(';')) ||profileName.contains(Character.toString('\'')) ||profileName.contains(Character.toString(',')) ||profileName.contains(Character.toString('.')) ||profileName.contains(Character.toString('~')) ||profileName.contains(Character.toString('`')) ){
				String strInfo = getString(R.string.setting_network_profile_error_profile_name);
				Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
				return;
			}
			String dialNumber = m_edit_dial_number.getText().toString();
			if(dialNumber.length() >24){
				String strInfo = getString(R.string.setting_network_profile_dial_number_wrong);
				Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
				return;
			}
			String APN = m_edit_apn.getText().toString();
			if(APN.length() == 0){
				APN = "3GNET";
			}
			String userName = m_edit_user_name.getText().toString();
			if(userName.length() == 0){
				userName = "";
			}
			String passWord = m_edit_password.getText().toString();
			if(passWord.length() == 0){
				passWord = "";
			}
			int auth_type = NewAuthType == -1 ? 0 : NewAuthType;
//			Log.v("AddOrEditProfile", Integer.toString(auth_type));
			
			if(isChanged(profileName, dialNumber, APN, userName, passWord, auth_type)){
				DataValue data = new DataValue();
				data.addParam("profile_id", profileID);
				data.addParam("profile_name", profileName);
				data.addParam("apn", APN);
				data.addParam("user_name", userName);
				data.addParam("password", passWord);
				data.addParam("auth_type", auth_type);
				data.addParam("dial_number", dialNumber);
				BusinessManager.getInstance().getProfileManager().startEditProfile(data);
			}
		}
		m_parent_activity.onBackPressed();
		
	}
	
	private class ProfileDetailReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
			Boolean ok = response != null && response.isOk();
			if (intent.getAction().equalsIgnoreCase(MessageUti.PROFILE_SET_DEFAULT_PROFILE_REQUEST)) {
				if (ok){
					m_is_default = true;
					Default = 1;
					m_switch_icon.setBackgroundResource(R.drawable.general_btn_on);
					m_edit_profile_name.setEnabled(false);
					m_edit_dial_number.setEnabled(false);
					m_edit_apn.setEnabled(false);
					m_edit_user_name.setEnabled(false);
					m_edit_password.setEnabled(false);
					m_protocol_selection.setEnabled(false);
					m_progress_bar.setVisibility(View.GONE);
				} else {
					String strInfo = getString(R.string.unknown_error);
					Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
				}
			}
			
			if (intent.getAction().equalsIgnoreCase(MessageUti.PROFILE_ADD_NEW_PROFILE_REQUEST)) {
				if (!ok){
					String strInfo = getString(R.string.unknown_error);
					Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
				}
			}
			
			if (intent.getAction().equalsIgnoreCase(MessageUti.PROFILE_EDIT_PROFILE_REQUEST)) {
				if (!ok){
					String strInfo = getString(R.string.unknown_error);
					Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
				}
			}
		}
		
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		try{
			getActivity().unregisterReceiver(m_profile_receiver);  
		}
		catch(Exception e){
			
		}
	}

}
