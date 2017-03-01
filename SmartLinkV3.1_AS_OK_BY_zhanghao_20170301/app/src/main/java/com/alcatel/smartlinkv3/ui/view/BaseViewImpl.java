package com.alcatel.smartlinkv3.ui.view;

import android.content.Context;
import android.view.View;

public abstract class BaseViewImpl {
	protected Context m_context;
    protected View m_view;
    
    public BaseViewImpl(Context context) {
        m_context = context;
    }
    
    protected void init(){}
    
    public abstract void onResume();
    public abstract void onPause();
    public abstract void onDestroy();
	
    public View getView()
    {
        return m_view;
    }
}
