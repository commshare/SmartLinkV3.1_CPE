package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class FragmentNetworkMode extends Fragment implements OnClickListener{
	
	private SettingNetworkActivity m_parent_activity = null;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
		View rootView = inflater.inflate(R.layout.fragment_network_mode, container, false);  
		initUi(rootView);
		
        return rootView;
    }
	
	private void initUi(View rootView){
		m_parent_activity = (SettingNetworkActivity) getActivity();
		m_parent_activity.changeTitlebar(R.string.setting_network_mode);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
