package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

public class FragmentProfileManagement extends Fragment implements OnClickListener{
	
	private ListView m_profile_list;
	private ImageButton m_add_profile = null;
	private ImageButton m_delete_profile = null;
	private SettingNetworkActivity m_parent_activity = null;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
		View rootView = inflater.inflate(R.layout.fragment_profile_management, container, false);  
		initUi(rootView);
        return rootView;
    }
	
	private void initUi(View rootView){
		
		m_parent_activity = (SettingNetworkActivity) getActivity();
		m_parent_activity.setAddAndDeleteVisibility(View.VISIBLE);
		
		m_parent_activity.changeTitlebar(R.string.setting_network_profile_management);
		
		m_add_profile = (ImageButton) getActivity().findViewById(R.id.tv_titlebar_add);
		m_delete_profile = (ImageButton) getActivity().findViewById(R.id.tv_titlebar_delete);
		m_add_profile.setOnClickListener(this);
		m_delete_profile.setOnClickListener(this);
		
		m_profile_list = (ListView) rootView.findViewById(R.id.profile_list);
		ProfileListAdapter adapter = new ProfileListAdapter(getActivity(), getProfileList());
		m_profile_list.setAdapter(adapter);
	}
	
	private List<String> getProfileList(){
		List<String> data = new ArrayList<String>();
		data.add("");
		data.add("Default");
		return data;
	}
	
	private void goToDetailProfile(){
		m_parent_activity.setAddAndDeleteVisibility(View.GONE);
		m_parent_activity.showFragmentProfileManagementDetail();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

private class ProfileListAdapter extends BaseAdapter{
		
		private LayoutInflater m_inflater;
		private List<String> m_data;
		private int m_selected_position = -1;
		private RadioButton m_selected_button;
		
		public ProfileListAdapter(Context context, List<String> data){
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
			holder.m_profile_title.setText(m_data.get(position));
			holder.m_profile_radiobutton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					if(position != m_selected_position && m_selected_button != null){
						m_selected_button.setChecked(false);
					}
					m_selected_position = position;
					m_selected_button = (RadioButton) view;
					
					goToDetailProfile();
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
}