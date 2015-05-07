package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FragmentProfileManagementDetail extends Fragment implements OnClickListener{
	
	private static SettingNetworkActivity m_parent_activity = null;
	private static LinearLayout m_protocol_selection = null;
	private static RadioGroup m_protocol_list = null;
	private static LinearLayout m_profile_detail_container = null;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
		View rootView = inflater.inflate(R.layout.fragment_profile_management_detail, container, false);
		initUi(rootView);
		
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
		default:
			break;
		}
	}
	
	private class ProtocolOnCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			// TODO Auto-generated method stub
			m_protocol_list.setVisibility(View.GONE);
			m_profile_detail_container.setVisibility(View.VISIBLE);
		}
	}

}
