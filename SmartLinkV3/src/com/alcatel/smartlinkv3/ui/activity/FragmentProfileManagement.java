package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.ProfileManager;
import com.alcatel.smartlinkv3.business.network.HttpSearchNetworkResult.NetworkItem;
import com.alcatel.smartlinkv3.business.profile.HttpGetProfileList.ProfileItem;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

public class FragmentProfileManagement extends Fragment implements OnClickListener{
	
	private ListView m_profile_list;
//	private ImageButton m_add_profile = null;
//	private ImageButton m_delete_profile = null;
	private SettingNetworkActivity m_parent_activity = null;
	private RelativeLayout m_progress_bar;
	
	private List<ProfileItem> m_profile_list_data = null;
	
	private IntentFilter m_fragment_get_profile_list_request_filter;
	private GetProfileListReceiver m_fragment_get_profile_list_request_receiver;
	
	private ProfileListAdapter m_adapter;
	
//	private NetworkSearchResultReceiver m_network_search_result_receiver;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
		View rootView = inflater.inflate(R.layout.fragment_profile_management, container, false);  
		initUi(rootView);
        return rootView;
    }
	
	private void initUi(View rootView){
		
		m_fragment_get_profile_list_request_receiver = new GetProfileListReceiver();
		m_fragment_get_profile_list_request_filter = new IntentFilter(MessageUti.PROFILE_GET_PROFILE_LIST_REQUEST);
		m_fragment_get_profile_list_request_filter.addAction(MessageUti.PROFILE_GET_PROFILE_LIST_REQUEST);
		
		m_parent_activity = (SettingNetworkActivity) getActivity();
		m_parent_activity.setAddAndDeleteVisibility(View.VISIBLE);
		
		m_parent_activity.changeTitlebar(R.string.setting_network_profile_management);
		
		m_progress_bar = (RelativeLayout) rootView.findViewById(R.id.waiting_loading_profile_progressbar);
		m_progress_bar.setVisibility(View.VISIBLE);
		
//		m_add_profile = (ImageButton) getActivity().findViewById(R.id.tv_titlebar_add);
//		m_delete_profile = (ImageButton) getActivity().findViewById(R.id.tv_titlebar_delete);
//		m_add_profile.setOnClickListener(this);
//		m_delete_profile.setOnClickListener(this);
		
		getActivity().registerReceiver(m_fragment_get_profile_list_request_receiver, m_fragment_get_profile_list_request_filter);  
		
		m_profile_list = (ListView) rootView.findViewById(R.id.profile_list);
		BusinessMannager.getInstance().getProfileManager().startGetProfileList(null);
	}
	
	private List<String> getProfileList(){
		List<String> data = new ArrayList<String>();
		data.add("");
		data.add("Default");
		return data;
	}
	
	public void goToDetailProfile(Bundle data){
		m_parent_activity.setAddAndDeleteVisibility(View.GONE);
		m_parent_activity.showFragmentProfileManagementDetail(data);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

private class ProfileListAdapter extends BaseAdapter{
		
		private LayoutInflater m_inflater;
		private List<ProfileItem> m_data;
		private int m_selected_position = -1;
		private RadioButton m_selected_button;
		
		public ProfileListAdapter(Context context, List<ProfileItem> data){
			m_inflater = LayoutInflater.from(context);
			m_data = data;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return m_data.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return m_data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView == null){
				convertView = m_inflater.inflate(R.layout.setting_profile_management_profile_item, null);
				holder = new ViewHolder();
				holder.m_profile_title = (TextView) convertView.findViewById(R.id.profile_desc);
				holder.m_profile_radiobutton = (RadioButton) convertView.findViewById(R.id.profile_management_list_item);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.m_profile_title.setText(m_data.get(position).Default == 1 ? "Default" : "");
			holder.m_profile_radiobutton.setText(m_data.get(position).ProfileName);
			holder.m_profile_radiobutton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					if(position != m_selected_position && m_selected_button != null){
						m_selected_button.setChecked(false);
					}
					m_selected_position = position;
					m_selected_button = (RadioButton) view;
					ProfileItem profileData = m_data.get(position);
					Bundle dataBundle = new Bundle();
					dataBundle.putString(m_parent_activity.TAG_OPERATION, m_parent_activity.TAG_OPERATION_EDIT_PROFILE);
					dataBundle.putString(m_parent_activity.TAG_PROFILE_NAME, profileData.ProfileName);
					dataBundle.putString(m_parent_activity.TAG_APN, profileData.APN);
					dataBundle.putString(m_parent_activity.TAG_USER_NAME, profileData.UserName);
					dataBundle.putString(m_parent_activity.TAG_DAIL_NUMBER, profileData.DailNumber);
					dataBundle.putString(m_parent_activity.TAG_PASSWORD, profileData.Password);
					dataBundle.putInt(m_parent_activity.TAG_DEFAULT, profileData.Default);
					dataBundle.putInt(m_parent_activity.TAG_PROFILE_ID, profileData.ProfileID);
					dataBundle.putInt(m_parent_activity.TAG_IS_PREDEFINE, profileData.IsPredefine);
					dataBundle.putInt(m_parent_activity.TAG_AUTH_TYPE, profileData.AuthType);
					goToDetailProfile(dataBundle);
				}
			});
			
			if(m_selected_position != position){
				holder.m_profile_radiobutton.setChecked(false);
			}
			else{
				holder.m_profile_radiobutton.setChecked(true);
				if(m_selected_button != null && holder.m_profile_radiobutton != m_selected_button){
					m_selected_button = holder.m_profile_radiobutton;
				}
			}
			return convertView;
		}
		
		public final class ViewHolder{
			TextView m_profile_title;
			RadioButton m_profile_radiobutton;
		}
	}


	private class GetProfileListReceiver extends BroadcastReceiver{
	
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
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
					m_adapter = new ProfileListAdapter(getActivity(), m_profile_list_data);
					m_profile_list.setAdapter(m_adapter);
					m_progress_bar.setVisibility(View.GONE);
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					
				}
			}
			
		}
		
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		try {
			getActivity().unregisterReceiver(m_fragment_get_profile_list_request_receiver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
}