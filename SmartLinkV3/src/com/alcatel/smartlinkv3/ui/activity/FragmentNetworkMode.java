package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class FragmentNetworkMode extends Fragment implements OnClickListener{
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
        return inflater.inflate(R.layout.fragment_network_mode, container, false);  
    }  

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
