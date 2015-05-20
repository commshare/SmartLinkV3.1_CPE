package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FragmentNetworkMode extends Fragment implements OnClickListener{
	
	private SettingNetworkActivity m_parent_activity = null;
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
		m_network_mode_radiogroup = (RadioGroup) rootView.findViewById(R.id.setting_network_mode);
		m_current_mode = BusinessMannager.getInstance().getNetworkManager().getNetworkMode();
		m_current_network_selection_mode = BusinessMannager.getInstance().getNetworkManager().getNetworkSelection();
		
		mode_auto = (RadioButton) rootView.findViewById(R.id.mode_auto);
		mode_2g_only = (RadioButton) rootView.findViewById(R.id.mode_2g);
		mode_3g_only = (RadioButton) rootView.findViewById(R.id.mode_3g);
		mode_lte_only = (RadioButton) rootView.findViewById(R.id.mode_lte);
		
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
		
		refresButton();
	}
	
	private void refresButton(){
		switch(m_current_mode){
		case MODE_AUTO:
			mode_auto.setChecked(true);
			mode_2g_only.setChecked(false);
			mode_3g_only.setChecked(false);
			mode_lte_only.setChecked(false);
			break;
		case MODE_2G_ONLY:
			mode_auto.setChecked(false);
			mode_2g_only.setChecked(true);
			mode_3g_only.setChecked(false);
			mode_lte_only.setChecked(false);
			break;
		case MODE_3G_ONLY:
			mode_auto.setChecked(false);
			mode_2g_only.setChecked(false);
			mode_3g_only.setChecked(true);
			mode_lte_only.setChecked(false);
			break;
		case MODE_LTE_ONLY:
			mode_auto.setChecked(false);
			mode_2g_only.setChecked(false);
			mode_3g_only.setChecked(false);
			mode_lte_only.setChecked(true);
			break;
		default:
			break;
		}
	}
	
	private void UserSetNetworkMode(final int mode){
		if(m_current_network_selection_mode != MODE_ERROR){
			DataValue data = new DataValue();
			data.addParam("network_mode", mode);
			data.addParam("netselection_mode", m_current_network_selection_mode);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST, data);
		}
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
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
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
