package com.alcatel.smartlinkv3.ui.home.helper.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private OnSmsRequestListener onSmsRequestListener;

    

    public interface OnSmsRequestListener {
        void displayUIs();

        void RefreshNewSmsNumbers();

        void getListSmsSummaryDatas();
    }

    public void setOnSmsRequestListener(OnSmsRequestListener onSmsRequestListener) {
        this.onSmsRequestListener = onSmsRequestListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();
        if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET)) {
            if (ok) {
                if (onSmsRequestListener != null) {
                    onSmsRequestListener.displayUIs();
                    onSmsRequestListener.RefreshNewSmsNumbers();
                }

            }
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET)) {
            if (ok) {
                if (onSmsRequestListener != null) {
                    onSmsRequestListener.displayUIs();
                    onSmsRequestListener.RefreshNewSmsNumbers();
                    onSmsRequestListener.getListSmsSummaryDatas();
                }

            }
        }
    }
}
