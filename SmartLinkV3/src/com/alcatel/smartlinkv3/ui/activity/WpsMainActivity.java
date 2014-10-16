package com.alcatel.smartlinkv3.ui.activity;


import com.alcatel.smartlinkv3.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;



public class WpsMainActivity extends BaseActivity implements OnClickListener{

	private RadioGroup mRadioGroup;  
	private RadioButton mRadio0,mRadio1;
	private LinearLayout mWpsPin; 
	private LinearLayout mPbc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wps_view);
		init();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void init(){
        mRadioGroup=(RadioGroup) findViewById(R.id.radioGroup);  
        mRadioGroup.setOnCheckedChangeListener(new RadioButtonOnCheckedChangeListenerImpl()); 
        
        mRadio0 = (RadioButton)findViewById(R.id.radio0);  
        mRadio1 = (RadioButton)findViewById(R.id.radio1); 
        
        mWpsPin=(LinearLayout)findViewById(R.id.include_pin_view);
        mPbc=(LinearLayout)findViewById(R.id.include_pbc_view);
        UpdateUI();

	}
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private class RadioButtonOnCheckedChangeListenerImpl implements OnCheckedChangeListener {  
         @Override  
         public void onCheckedChanged(RadioGroup group, int checkedId) {  

        	 if (checkedId == mRadio0.getId())  
                {  
        		 	mWpsPin.setVisibility(View.GONE);
        		 	mPbc.setVisibility(View.VISIBLE); 
                }  
                else if(checkedId == mRadio1.getId())
                {  
                	mWpsPin.setVisibility(View.VISIBLE);
        		 	mPbc.setVisibility(View.GONE);
                }  
         }  
     } 

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	public void UpdateUI() {
		
	}

}