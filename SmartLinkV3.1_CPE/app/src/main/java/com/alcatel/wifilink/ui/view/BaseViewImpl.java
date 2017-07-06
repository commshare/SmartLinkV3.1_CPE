package com.alcatel.wifilink.ui.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

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

    void setViewGroupVisibility(ViewGroup view, final int visibility) {
        final int count = view.getChildCount();
        for (int i = 0; i < count; ++i) {
            View temp = view.getChildAt(i);
            temp.setVisibility(visibility);
        }
        view.setVisibility(visibility);
    }
}
