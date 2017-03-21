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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.profile.HttpGetProfileList.ProfileItem;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import java.util.List;

public class FragmentProfileManagement extends Fragment implements OnClickListener{
	
	private ListView m_profile_list;
//	private ImageButton m_add_profile = null;
//	private ImageButton m_delete_profile = null;
	private SettingNetworkActivity m_parent_activity = null;
	private RelativeLayout m_progress_bar;
	
	private List<ProfileItem> m_profile_list_data = null;
	
	private IntentFilter m_fragment_get_profile_list_request_filter;
	private IntentFilter m_fragment_delete_profile__request_filter;
	private GetProfileListReceiver m_fragment_get_profile_list_request_receiver;
	
	
	private ProfileListAdapter m_adapter;
	private int     selected_deleted_position = -1;
    private boolean isDeleting                = false;
	
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
		m_fragment_delete_profile__request_filter = new IntentFilter(MessageUti.PROFILE_DELETE_PROFILE_REQUEST);
		m_fragment_delete_profile__request_filter.addAction(MessageUti.PROFILE_DELETE_PROFILE_REQUEST);
		
		m_parent_activity = (SettingNetworkActivity) getActivity();
		m_parent_activity.setAddAndDeleteVisibility(View.VISIBLE);
		
		m_parent_activity.changeTitlebar(R.string.setting_network_profile_list);
		
		m_progress_bar = (RelativeLayout) rootView.findViewById(R.id.waiting_loading_profile_progressbar);
		m_progress_bar.setVisibility(View.VISIBLE);

		getActivity().registerReceiver(m_fragment_get_profile_list_request_receiver, m_fragment_get_profile_list_request_filter);
		getActivity().registerReceiver(m_fragment_get_profile_list_request_receiver, m_fragment_delete_profile__request_filter);  
		
		m_profile_list = (ListView) rootView.findViewById(R.id.profile_list);
		BusinessManager.getInstance().getProfileManager().startGetProfileList(null);
	}
	
	public void goToDetailProfile(Bundle data){
		m_parent_activity.setAddAndDeleteVisibility(View.GONE);
		m_parent_activity.showFragmentProfileManagementDetail(data);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

    public void deleteProfile() {
        if (selected_deleted_position < 0){
            isDeleting = false;
            m_adapter.notifyDataSetChanged();
            return;
        }
        m_progress_bar.setVisibility(View.VISIBLE);
        int ProfileId = m_profile_list_data.get(selected_deleted_position).ProfileID;
        DataValue data = new DataValue();
        data.addParam("profile_id", ProfileId);
        BusinessManager.getInstance().getProfileManager().startDeleteProfile(data);
    }

    public ProfileListAdapter getAdapter() {
        if (m_adapter != null){
            return m_adapter;
        }else{
            return null;
        }
    }

    public boolean getIsDeleting (){
        return isDeleting;
    }

    public void setIsDeleting (boolean isDeleting){
        this.isDeleting = isDeleting;
    }

    public void setDeletePosition(int deletePosition){
        selected_deleted_position = deletePosition;
    }

    public class ProfileListAdapter extends BaseAdapter{
		
		private LayoutInflater m_inflater;
		private List<ProfileItem> m_data;
		private int m_selected_position = -1;

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
				holder.m_profile_container = (RelativeLayout) convertView.findViewById(R.id.profile_management_list_item_container);
				holder.m_profile_check = (ImageView) convertView.findViewById(R.id.profile_management_list_item_check);
				holder.m_profile_delete_check = (ImageView) convertView.findViewById(R.id.profile_management_list_item_delete_check);
				holder.m_profile_title = (TextView) convertView.findViewById(R.id.profile_desc);
				holder.m_profile_arrow = (ImageView) convertView.findViewById(R.id.profile_management_list_item_arrow);
				holder.m_profile_radiobutton = (TextView) convertView.findViewById(R.id.profile_management_list_item);
				holder.m_profile_divider = (ImageView) convertView.findViewById(R.id.profile_management_list_item_divider);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
            //是否为删除模式
            if (((SettingNetworkActivity)getActivity()).isDeleteMode()){//删除模式
                holder.m_profile_check.setVisibility(View.INVISIBLE);
                holder.m_profile_delete_check.setVisibility(View.VISIBLE);
                holder.m_profile_arrow.setVisibility(View.INVISIBLE);

                //是否选中删除
                if (m_profile_list_data.get(position).IsDeleteCheck){
                    holder.m_profile_delete_check.setImageResource(R.drawable.general_ic_check);
                }else{
                    holder.m_profile_delete_check.setImageResource(R.drawable.general_ic_uncheck);
                }
            }else{//普通模式
                holder.m_profile_check.setVisibility(View.VISIBLE);
                holder.m_profile_delete_check.setVisibility(View.INVISIBLE);
                holder.m_profile_arrow.setVisibility(View.VISIBLE);

                //是否打勾
                if (m_data.get(position).Default == 1){
                    holder.m_profile_check.setVisibility(View.VISIBLE);
                }else{
                    holder.m_profile_check.setVisibility(View.INVISIBLE);
                }
            }
            //是否为预设的项
			holder.m_profile_title.setText(m_data.get(position).Default == 1 ? "Default" : "");

            //最后一行不显示分割线
            if (position ==m_data.size() - 1){
                holder.m_profile_divider.setVisibility(View.GONE);
            }else{
                holder.m_profile_divider.setVisibility(View.VISIBLE);
            }
			holder.m_profile_radiobutton.setText(m_data.get(position).ProfileName);
			holder.m_profile_container.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
//					if(position != m_selected_position && m_selected_button != null){
//						m_selected_button.setChecked(false);
//					}
					m_selected_position = position;
                    //普通模式
					if(!m_parent_activity.isDeleteMode()){
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
                    //删除模式
					else{
						if(m_profile_list_data.get(position).IsPredefine == 1 && m_profile_list_data.get(position).Default == 0){
                            for (int i = 0; i < m_profile_list_data.size(); i++) {
                                if (i == position){
                                    m_profile_list_data.get(i).IsDeleteCheck = true;
                                }else {
                                    m_profile_list_data.get(i).IsDeleteCheck = false;
                                }
                            }

                            selected_deleted_position = position;
						}
						else if(m_profile_list_data.get(position).IsPredefine == 0){
							String strInfo = getString(R.string.setting_network_profile_management_cannot_delete_predefined_profile);
							Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
						}
						else if(m_profile_list_data.get(position).Default == 1){
							String strInfo = getString(R.string.setting_network_profile_management_cannot_delete_default_profile);
							Toast.makeText(getActivity(), strInfo, Toast.LENGTH_SHORT).show();
						}
					}

                    notifyDataSetChanged();

				}
			});
			
//			if(m_selected_position != position){
//				holder.m_profile_radiobutton.setChecked(false);
//			}
//			else{
////				holder.m_profile_radiobutton.setChecked(true);
//				if(m_selected_button != null && holder.m_profile_radiobutton != m_selected_button){
//					m_selected_button = holder.m_profile_radiobutton;
//				}
//			}
			return convertView;
		}
		
		public final class ViewHolder{
            RelativeLayout m_profile_container;
            ImageView m_profile_check;
            ImageView m_profile_delete_check;
			TextView m_profile_title;
			ImageView m_profile_arrow;
			TextView m_profile_radiobutton;
			ImageView m_profile_divider;
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
//					m_network_search_result_list = BusinessManager.getInstance().getNetworkManager().getNetworkList();
					m_profile_list_data = BusinessManager.getInstance().getProfileManager().GetProfileListData();
					m_adapter = new ProfileListAdapter(getActivity(), m_profile_list_data);
					m_profile_list.setAdapter(m_adapter);
					m_progress_bar.setVisibility(View.GONE);
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					String strInfo = getString(R.string.unknown_error);
					Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
				}
			}
			
			if (intent.getAction().equalsIgnoreCase(
					MessageUti.PROFILE_DELETE_PROFILE_REQUEST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				
				if (BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() == 0){
					if(selected_deleted_position >=0){
						m_profile_list_data.remove(selected_deleted_position);
						m_adapter.notifyDataSetChanged();
						m_progress_bar.setVisibility(View.GONE);
					}
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					String strInfo = getString(R.string.unknown_error);
					Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
				}

                selected_deleted_position = -1;
                isDeleting = false;
			}
			
		}
		
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		try {
			m_parent_activity.setDeleteProfle(false);
			getActivity().unregisterReceiver(m_fragment_get_profile_list_request_receiver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
}