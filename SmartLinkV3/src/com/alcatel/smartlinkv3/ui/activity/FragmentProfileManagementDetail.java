package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

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
	private String APN = "";
	private String UserName = "";
	private String Password = "";
	private int Default = -1;
	private int ProfileID = -1;
	private int IsPredefine = -1;
	private int AuthType = -1;
	private String DailNumber = "";
	private String IPAddress = "";
	
	
	
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
		m_parent_activity.changeTitlebar(R.string.setting_network_profile_management_profile_detail);
		
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
		
		
		
		
		
		
		
		
		
		if(operation == m_parent_activity.TAG_OPERATION_EDIT_PROFILE){
			refreshInfo();
		}
		
	}
	
	private void refreshInfo(){
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
		m_edit_dial_number.setHint(DailNumber);
		m_edit_apn.setHint(APN);
		m_edit_user_name.setHint(UserName);
		m_edit_password.setHint(Password);
		
		switch(AuthType){
		case 0:
			m_protocol_displayer.setText(R.string.setting_network_profile_management_protocol_none);
			break;
		case 1:
			m_protocol_displayer.setText(R.string.setting_network_profile_management_auth_type_pap);
			break;
		case 2:
			m_protocol_displayer.setText(R.string.setting_network_profile_management_auth_type_chap);
			break;
		case 3:
			m_protocol_displayer.setText(R.string.setting_network_profile_management_auth_type_pap_and_chap);
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nID = v.getId();
		switch (nID) {
		case R.id.profile_protocal:
			m_profile_detail_container.setVisibility(View.GONE);
			m_protocol_list.setVisibility(View.VISIBLE);
			break;
		case R.id.profile_set_default:
			if(m_is_default == false){
				//m_switch_icon.setBackground(getResources().getDrawable(R.drawable.pwd_switcher_on));
				m_switch_icon.setBackgroundResource(R.drawable.pwd_switcher_on);
				m_is_default = true;
			}
			else if(m_is_default == true){
				//m_switch_icon.setBackground(getResources().getDrawable(R.drawable.pwd_switcher_off));
				m_switch_icon.setBackgroundResource(R.drawable.pwd_switcher_off);
				m_is_default = false;
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
			TextView selected_text = (TextView) group.findViewById(checkedId);
			
			((TextView)m_root_view.findViewById(R.id.edit_protocal)).setText(selected_text.getText());
			
			m_protocol_list.setVisibility(View.GONE);
			m_profile_detail_container.setVisibility(View.VISIBLE);
		}
	}

}
