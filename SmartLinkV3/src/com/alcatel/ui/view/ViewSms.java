package com.alcatel.ui.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alcatel.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewSms extends BaseViewImpl implements OnClickListener ,OnItemClickListener {

	
	
	public ViewSms(Context context) {
		super(context);
		m_context = context;
		init();
	}
	
	@Override
    protected void init()
    {
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_sms, null);
    }	
	
	@Override
	public void onResume() {}
	
	@Override
	public void onPause() {}
	
	@Override
	public void onDestroy() {}

	public void onClick(View v) {}
	

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {}
}
