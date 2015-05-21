package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.network.HttpSearchNetworkResult.NetworkItem;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

public class FragmentNetworkSelection extends Fragment implements OnClickListener{
	
	private ListView m_network_list;
	private FrameLayout m_network_list_conainer;
	private RadioGroup m_network_selection;
	private RadioButton m_auto_mode;
	private RadioButton m_manual_mode;
	private TextView m_network_searching_title;
	private NetworkListAdapter m_adapter;
	
	private SettingNetworkActivity m_parent_activity = null;
	
	private IntentFilter m_search_network_result_filter;
	private IntentFilter m_fragment_get_network_setting_filter;
	private IntentFilter m_fragment_set_network_setting_filter;
	private IntentFilter m_fragment_register_network_filter;
	
	private NetworkSearchResultReceiver m_network_search_result_receiver;
	
	private List<NetworkItem> m_network_search_result_list = null;
	
	private RelativeLayout m_waiting_search_result;
	
	private final int SELECTION_MODE_AUTO = 0;
	private final int SELECTION_MODE_MANUAL = 1;
	private final int MODE_ERROR = -1;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
		View rootView = inflater.inflate(R.layout.fragment_network_selection, container, false); 
		
		((SettingNetworkActivity)getActivity()).changeTitlebar(R.string.setting_network_selection);
		initUi(rootView);
        
		
        return rootView;
    }
	
	private void initUi(View view){
		m_search_network_result_filter = new IntentFilter(MessageUti.NETWORK_SEARCH_NETWORK_RESULT_ROLL_REQUSET);
		m_search_network_result_filter.addAction(MessageUti.NETWORK_SEARCH_NETWORK_RESULT_ROLL_REQUSET);
		m_fragment_get_network_setting_filter = new IntentFilter(MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST);
		m_fragment_set_network_setting_filter = new IntentFilter(MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST);
		m_fragment_get_network_setting_filter.addAction(MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST);
		m_fragment_set_network_setting_filter.addAction(MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST);
		m_fragment_register_network_filter = new IntentFilter(MessageUti.NETWORK_REGESTER_NETWORK_REQUEST);
		m_fragment_register_network_filter.addAction(MessageUti.NETWORK_REGESTER_NETWORK_REQUEST);
		
		m_network_search_result_receiver = new NetworkSearchResultReceiver();
		
		m_waiting_search_result = (RelativeLayout) view.findViewById(R.id.network_list_progress_circle);
		m_parent_activity = (SettingNetworkActivity) getActivity();
		
		m_network_list = (ListView) view.findViewById(R.id.network_list);
        
        
        m_network_list_conainer = (FrameLayout) view.findViewById(R.id.network_list_container);
        
        m_network_selection = (RadioGroup)view.findViewById(R.id.setting_network_selection);
        m_auto_mode = (RadioButton) view.findViewById(R.id.mode_auto);
        m_manual_mode = (RadioButton) view.findViewById(R.id.mode_manual);
        
        m_network_searching_title =(TextView)view.findViewById(R.id.network_searching_title);
        
        if(BusinessMannager.getInstance().getNetworkManager().getNetworkSelection() == SELECTION_MODE_MANUAL){
//			UserSetNetworkSelection(SELECTION_MODE_MANUAL);
			m_manual_mode.setChecked(true);
			m_auto_mode.setChecked(false);
			m_network_searching_title.setVisibility(View.VISIBLE);
			m_waiting_search_result.setVisibility(View.VISIBLE);
			BusinessMannager.getInstance().getNetworkManager().startSearchNetworkResult(null);
		}
		else if(BusinessMannager.getInstance().getNetworkManager().getNetworkSelection() == SELECTION_MODE_AUTO){
//			UserSetNetworkSelection(SELECTION_MODE_AUTO);
			m_manual_mode.setChecked(false);
			m_auto_mode.setChecked(true);
			m_network_searching_title.setVisibility(View.GONE);
			m_waiting_search_result.setVisibility(View.GONE);
		}
        
//        if(m_auto_mode.isChecked() == true){
//        	m_network_list_conainer.setVisibility(View.GONE);
//        	m_network_searching_title.setVisibility(View.GONE);
//        }
//        if(m_manual_mode.isChecked() == true){
//        	m_network_list_conainer.setVisibility(View.VISIBLE);
//        	m_network_searching_title.setVisibility(View.VISIBLE);
//        }
        
        m_auto_mode.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				// TODO Auto-generated method stub
				if(checked == true){
//					m_network_list_conainer.setVisibility(View.GONE);
//					m_network_searching_title.setVisibility(View.GONE);
					Log.v("NetworkSearchResult", "auto");
					UserSetNetworkSelection(SELECTION_MODE_AUTO);
					m_network_searching_title.setVisibility(View.GONE);
					BusinessMannager.getInstance().getNetworkManager().stopSearchNetworkResult();
				}
				else if(checked == false){
//					m_network_list_conainer.setVisibility(View.VISIBLE);
//					m_network_searching_title.setVisibility(View.VISIBLE);
					Log.v("NetworkSearchResult", "manual");
					UserSetNetworkSelection(SELECTION_MODE_MANUAL);
					m_network_searching_title.setVisibility(View.VISIBLE);
//					BusinessMannager.getInstance().getNetworkManager().startSearchNetworkResult(null);
				}
			}
        	
        });
        
        getActivity().registerReceiver(m_network_search_result_receiver, m_search_network_result_filter);  
        getActivity().registerReceiver(m_network_search_result_receiver, m_fragment_get_network_setting_filter);  
        getActivity().registerReceiver(m_network_search_result_receiver, m_fragment_set_network_setting_filter);  
        getActivity().registerReceiver(m_network_search_result_receiver, m_fragment_register_network_filter);  
        
	}
	
	private void UserSetNetworkSelection(final int mode){
		if(BusinessMannager.getInstance().getNetworkManager().getNetworkSelection() != MODE_ERROR){
			m_waiting_search_result.setVisibility(View.VISIBLE);
			DataValue data = new DataValue();
			data.addParam("network_mode", BusinessMannager.getInstance().getNetworkManager().getNetworkMode());
			data.addParam("netselection_mode", mode);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST, data);
		}
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
//		m_waiting_search_result.setVisibility(View.GONE);
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
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		getActivity().unregisterReceiver(m_network_search_result_receiver);  
	}
	
	private  class NetworkListAdapter extends BaseAdapter{
		
		private  LayoutInflater m_inflater;
		private  List<NetworkItem> m_data;
		private  int m_selected_position = -1;
		private  RadioButton m_selected_button;
		
		public NetworkListAdapter(Context context, List<NetworkItem> data){
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
				convertView = m_inflater.inflate(R.layout.setting_network_list_item, null);
				holder = new ViewHolder();
				holder.m_network_title = (TextView) convertView.findViewById(R.id.network_desc);
				holder.m_network_radiobutton = (RadioButton) convertView.findViewById(R.id.network_selection_list_item);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.m_network_radiobutton.setText(m_data.get(position).ShortName);
			if(m_data.get(position).State == 0){
				holder.m_network_title.setText("Unknown");
				holder.m_network_radiobutton.setEnabled(false);
			}
			else if(m_data.get(position).State == 1){
				holder.m_network_title.setText("Available");
				holder.m_network_radiobutton.setEnabled(true);
			}
			else if(m_data.get(position).State == 2){
				holder.m_network_title.setText("Selected");
				m_selected_position = position;
				m_selected_button = holder.m_network_radiobutton;
				holder.m_network_radiobutton.setEnabled(false);
			}
			else{
				holder.m_network_title.setText("Forbidden");
				holder.m_network_radiobutton.setEnabled(false);
			}
			
			if(m_selected_position != position){
				holder.m_network_radiobutton.setChecked(false);
			}
			else{
				holder.m_network_radiobutton.setChecked(true);
				if(m_selected_button != null && holder.m_network_radiobutton != m_selected_button){
					m_selected_button = holder.m_network_radiobutton;
				}
			}
			
			
			holder.m_network_radiobutton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					//Log.v("NetworkSearchResult", "" + position);
					if(position != m_selected_position && m_selected_button != null){
						m_selected_button.setChecked(false);
						m_selected_button.setEnabled(true);
					}
					
					BusinessMannager.getInstance().getNetworkManager().startRegisterNetwork(position);
					m_data.get(m_selected_position).State = 1;
					m_selected_position = position;
					m_selected_button = (RadioButton) view;
					m_data.get(position).State = 2;
					m_waiting_search_result.setVisibility(View.VISIBLE);
					NetworkListAdapter.this.notifyDataSetChanged();
				}
			});
			
			holder.m_network_radiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
//					if(isChecked){
//						TextView tv = (TextView) m_network_list.getChildAt(position).findViewById(R.id.network_desc);
//						Log.v("NetworkSearchResult", "Checked position" + position + " " + tv.getText().toString());
//						tv.setText("Selected");
//					}
//					else{
//						TextView tv = (TextView) m_network_list.getChildAt(position).findViewById(R.id.network_desc);
//						Log.v("NetworkSearchResult", "Unchecked position" + position + " " + tv.getText().toString());
//						tv.setText("Available");
//					}
//					NetworkListAdapter.this.notifyDataSetChanged();
				}
			});
			return convertView;
		}
		
		public final class ViewHolder{
			TextView m_network_title;
			RadioButton m_network_radiobutton;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	private class NetworkSearchResultReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equalsIgnoreCase(
					MessageUti.NETWORK_SEARCH_NETWORK_RESULT_ROLL_REQUSET)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				
				if (BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() == 0){
					m_network_search_result_list = BusinessMannager.getInstance().getNetworkManager().getNetworkList();
					if(m_network_search_result_list != null){
						m_adapter = new NetworkListAdapter(m_parent_activity, m_network_search_result_list);
				        m_network_list.setAdapter(m_adapter);
				        
				        m_waiting_search_result.setVisibility(View.GONE);
					}
						
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					
				}
			}
			
			
			if (intent.getAction().equalsIgnoreCase(
					MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST)) {
				Log.v("NetworkSearchResult", "Yes");
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				
				if (BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() == 0){
					if(BusinessMannager.getInstance().getNetworkManager().getNetworkSelection() == SELECTION_MODE_MANUAL){
						BusinessMannager.getInstance().getNetworkManager().startSearchNetworkResult(null);
					}
					else if(BusinessMannager.getInstance().getNetworkManager().getNetworkSelection() == SELECTION_MODE_AUTO){
						m_waiting_search_result.setVisibility(View.GONE);
					}
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					//Log
				}
			}
			
			if (intent.getAction().equalsIgnoreCase(
					MessageUti.NETWORK_REGESTER_NETWORK_REQUEST)) {
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
						BaseResponse.RESPONSE_OK);
				
				String strErrorCode = intent
						.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				
				if (BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() == 0){
					m_waiting_search_result.setVisibility(View.GONE);
				}
				else if(BaseResponse.RESPONSE_OK == nResult
						&& strErrorCode.length() > 0){
					
				}
			}
			
		}
		
	}
}
