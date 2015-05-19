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
