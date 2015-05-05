package com.alcatel.smartlinkv3.ui.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.alcatel.smartlinkv3.R;

public class FragmentNetworkSelection extends Fragment implements OnClickListener{
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
        return inflater.inflate(R.layout.fragment_network_selection, container, false);  
    }  

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
