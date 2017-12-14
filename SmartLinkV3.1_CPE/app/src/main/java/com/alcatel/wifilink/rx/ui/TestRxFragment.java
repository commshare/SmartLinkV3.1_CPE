package com.alcatel.wifilink.rx.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by qianli.ma on 2017/12/12 0012.
 */

public class TestRxFragment extends Fragment {
    
    
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        HomeRxActivity activity = (HomeRxActivity) getActivity();
        textView.setText("hello test");
        textView.setOnClickListener(v -> {
            activity.fraHelpers.transfer(activity.clazz[0]);
        });
        return textView;
    }
}
