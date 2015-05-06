package com.alcatel.smartlinkv3.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

public class FragmentNetworkSelection extends Fragment implements OnClickListener{
	
	ListView m_network_list;
	LinearLayout m_network_list_conainer;
	RadioButton m_auto_mode;
	RadioButton m_manual_mode;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
		View rootView = inflater.inflate(R.layout.fragment_network_selection, container, false); 
		
		initUi(rootView);
        
		
        return rootView;
    }
	
	private void initUi(View view){
		m_network_list = (ListView) view.findViewById(R.id.network_list);
        NetworkListAdapter m_adapter = new NetworkListAdapter(getActivity(), getNetworkList());
        m_network_list.setAdapter(m_adapter);
        
        m_network_list_conainer = (LinearLayout) view.findViewById(R.id.network_list_container);
        
        m_auto_mode = (RadioButton) view.findViewById(R.id.mode_auto);
        m_manual_mode = (RadioButton) view.findViewById(R.id.mode_manual);
        
        if(m_auto_mode.isChecked() == true){
        	m_network_list_conainer.setVisibility(View.GONE);
        }
        if(m_manual_mode.isChecked() == true){
        	m_network_list_conainer.setVisibility(View.VISIBLE);
        }
        
        m_auto_mode.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				// TODO Auto-generated method stub
				if(checked == true){
					m_network_list_conainer.setVisibility(View.GONE);
				}
				else if(checked == false){
					m_network_list_conainer.setVisibility(View.VISIBLE);
				}
			}
        	
        });
	}
	
	private List<String> getNetworkList(){
		List<String> data = new ArrayList<String>();
		data.add("Available");
		data.add("Available");
		data.add("Available");
		data.add("Available");
		data.add("Available");
		data.add("Available");
		data.add("Available");
		data.add("Available");
		data.add("Available");
		data.add("Available");
		data.add("Available");
		data.add("Forbidden");
		data.add("Forbidden");
		data.add("Forbidden");
		data.add("Forbidden");
		data.add("Forbidden");
		data.add("Forbidden");
		return data;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private class NetworkListAdapter extends BaseAdapter{
		
		private LayoutInflater m_inflater;
		private List<String> m_data;
		private int m_selected_position = -1;
		private RadioButton m_selected_button;
		
		public NetworkListAdapter(Context context, List<String> data){
			this.m_inflater = LayoutInflater.from(context);
			this.m_data = data;
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
			holder.m_network_title.setText(m_data.get(position));
			holder.m_network_radiobutton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					if(position != m_selected_position && m_selected_button != null){
						m_selected_button.setChecked(false);
					}
					m_selected_position = position;
					m_selected_button = (RadioButton) view;
				}
			});
			
			if(m_selected_position != position){
				holder.m_network_radiobutton.setChecked(false);
			}
			else{
				holder.m_network_radiobutton.setChecked(true);
				if(m_selected_button != null && holder.m_network_radiobutton != m_selected_button){
					m_selected_button = holder.m_network_radiobutton;
				}
			}
			return convertView;
		}
		
		public final class ViewHolder{
			TextView m_network_title;
			RadioButton m_network_radiobutton;
		}
		
	}
}
