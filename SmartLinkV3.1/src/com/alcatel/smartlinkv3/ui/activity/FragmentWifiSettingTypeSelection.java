package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.ENUM.SecurityMode;
import com.alcatel.smartlinkv3.common.ENUM.WEPEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WPAEncryption;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FragmentWifiSettingTypeSelection extends Fragment{
	
	private SettingWifiActivity m_parent_activity;
	private RadioGroup m_wifi_type_selection;
	private RadioGroup m_wifi_type_wpa_type;
	private RadioGroup m_wifi_type_wep_type;
	
	private int m_security_mode = 0;
	private int m_mode_type = -1;
	
	private boolean m_isFirstIn;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
		View rootView = inflater.inflate(R.layout.fragment_wifi_type_selection, container, false);
		initUi(rootView);
        return rootView;
    }
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		m_parent_activity.setContentVisibility(View.VISIBLE);
		m_parent_activity.setDoneButtonVisibility(View.VISIBLE);
		m_parent_activity.setBackButtonVisibility(View.VISIBLE);
		m_parent_activity.setTypeSelectionFragmentVisible(false);
	}
	
	private void initUi(View view){
		m_parent_activity = (SettingWifiActivity) getActivity();
		m_isFirstIn = true;
		m_wifi_type_selection = (RadioGroup)(view.findViewById(R.id.setting_network_wifi_type_selection));
		m_wifi_type_wpa_type = (RadioGroup)(view.findViewById(R.id.setting_network_wifi_wpa_type));
		m_wifi_type_wep_type = (RadioGroup)(view.findViewById(R.id.setting_network_wifi_wep_type));
		
		m_wifi_type_wpa_type.setVisibility(View.GONE);
		m_wifi_type_wep_type.setVisibility(View.GONE);
		
		m_wifi_type_selection.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch(checkedId){
				case R.id.setting_network_wifi_type_wep:
					m_wifi_type_wep_type.setVisibility(View.VISIBLE);
					m_wifi_type_wpa_type.setVisibility(View.GONE);
					m_security_mode = SecurityMode.antiBuild(SecurityMode.WEP);
					if(m_mode_type > 1){
						m_wifi_type_wep_type.clearCheck();
						m_mode_type = 0;
					}
					m_parent_activity.setWifiMode(m_security_mode, m_mode_type);
					break;
				case R.id.setting_network_wifi_type_wpa:
					m_wifi_type_wep_type.setVisibility(View.GONE);
					m_wifi_type_wpa_type.setVisibility(View.VISIBLE);
					m_security_mode = SecurityMode.antiBuild(SecurityMode.WPA);
					m_parent_activity.setWifiMode(m_security_mode, m_mode_type);
					break;
				case R.id.setting_network_wifi_type_wpa2:
					m_wifi_type_wep_type.setVisibility(View.GONE);
					m_wifi_type_wpa_type.setVisibility(View.VISIBLE);
					m_security_mode = SecurityMode.antiBuild(SecurityMode.WPA2);
					m_parent_activity.setWifiMode(m_security_mode, m_mode_type);
					break;
				case R.id.setting_network_wifi_type_wpa_or_wpa2:
					m_wifi_type_wep_type.setVisibility(View.GONE);
					m_wifi_type_wpa_type.setVisibility(View.VISIBLE);
					m_security_mode = SecurityMode.antiBuild(SecurityMode.WPA_WPA2);
					m_parent_activity.setWifiMode(m_security_mode, m_mode_type);
					break;
				default:
					break;
				}
			}
			
		});
		
		m_wifi_type_wpa_type.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(!m_isFirstIn){
					switch(checkedId){
					case R.id.setting_wifi_tkip:
						m_mode_type = WPAEncryption.antiBuild(WPAEncryption.TKIP);
						m_parent_activity.setWifiMode(m_security_mode, m_mode_type);
//						m_parent_activity.onBackPressed();
						break;
					case R.id.setting_wifi_aes:
						m_mode_type = WPAEncryption.antiBuild(WPAEncryption.AES);
						m_parent_activity.setWifiMode(m_security_mode, m_mode_type);
//						m_parent_activity.onBackPressed();
						break;
					case R.id.setting_wifi_auto:
						m_mode_type = WPAEncryption.antiBuild(WPAEncryption.AUTO);
						m_parent_activity.setWifiMode(m_security_mode, m_mode_type);
//						m_parent_activity.onBackPressed();
						break;
					default:
						break;
					}
				}
			}
			
		});
		
		m_wifi_type_wep_type.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(!m_isFirstIn){
					switch(checkedId){
					case R.id.setting_wifi_open:
						m_mode_type = WEPEncryption.antiBuild(WEPEncryption.Open);
						m_parent_activity.setWifiMode(m_security_mode, m_mode_type);
//						m_parent_activity.onBackPressed();
						break;
					case R.id.setting_wifi_share:
						m_mode_type = WEPEncryption.antiBuild(WEPEncryption.Share);
						m_parent_activity.setWifiMode(m_security_mode, m_mode_type);
//						m_parent_activity.onBackPressed();
						break;
					default:
						break;
					}
				}
			}
			
		});
		
		m_security_mode = getArguments().getInt("Security_Mode");
		m_mode_type = getArguments().getInt("Mode_Type");
//		RadioButton checkWifiType;
		if(m_security_mode > 0){
//			switch(m_security_mode){
//			case 1://WEP
//				checkWifiType = (RadioButton) m_wifi_type_selection.getChildAt(0);
//				checkWifiType.setChecked(true);
//				break;
//			case 2:
//				checkWifiType = (RadioButton) m_wifi_type_selection.getChildAt(2);
//				checkWifiType.setChecked(true);
//				break;
//			}
			RadioButton checkWifiType = (RadioButton) m_wifi_type_selection.getChildAt(m_security_mode*2 - 2);
			checkWifiType.setChecked(true);
			if(m_security_mode == SecurityMode.antiBuild(SecurityMode.WEP)){
				RadioButton checkWepType = (RadioButton) m_wifi_type_wep_type.getChildAt((m_mode_type + 1) * 2 - 2);
				checkWepType.setChecked(true);
			}
			else{
				RadioButton checkWpaType = (RadioButton) m_wifi_type_wpa_type.getChildAt((m_mode_type + 1) * 2 - 2);
				checkWpaType.setChecked(true);
			}
		}
		
		m_isFirstIn = false;
	}
}
