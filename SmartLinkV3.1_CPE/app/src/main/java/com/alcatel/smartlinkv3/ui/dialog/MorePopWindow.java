package com.alcatel.smartlinkv3.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.activity.UsageSettingActivity;
import com.alcatel.smartlinkv3.utils.DataUtils;

public class MorePopWindow extends PopupWindow implements OnClickListener {
    private View conentView;
    LinearLayout m_usage_setting;
    LinearLayout m_clear_history;
    private OnClearUsageRecord onClearUsageRecord;

    public MorePopWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.more_popup_dialog, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        this.setContentView(conentView);
        this.setWidth(w / 2 + 50);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        //this.setAnimationStyle(R.style.AnimationPreview);

        m_usage_setting = (LinearLayout) conentView.findViewById(R.id.usage_setting_layout);
        m_usage_setting.setOnClickListener(this);

        m_clear_history = (LinearLayout) conentView.findViewById(R.id.clear_history_layout);
        m_clear_history.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        String msgRes = null;
        switch (v.getId()) {
            case R.id.usage_setting_layout:
                intent.setClass(v.getContext(), UsageSettingActivity.class);
                v.getContext().startActivity(intent);
                this.dismiss();
                break;
            case R.id.clear_history_layout:
                // clear the record
                clearUsageRecord();
                this.dismiss();
                break;
        }
    }

    /* **** clearUsageRecord **** */
    private void clearUsageRecord() {
        API.get().setUsageRecordClear(DataUtils.getCurrent(), new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {

            }
        });
    }

    public interface OnClearUsageRecord{
        void clearRecord();
    }
    
    public void setOnClearUsageRecord(OnClearUsageRecord onClearUsageRecord){
        this.onClearUsageRecord = onClearUsageRecord;
    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, -16);
        } else {
            this.dismiss();
        }
    }
}
