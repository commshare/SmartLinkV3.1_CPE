package com.alcatel.smartlinkv3.ui.home.helper.sms;

import android.view.View;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.common.ENUM.SMSInit;
import com.alcatel.smartlinkv3.ui.home.allsetup.HomeActivity;

/**
 * Created by qianli.ma on 2017/6/17.
 */

public class SmsCountHelper {

    /**
     * 显示主页消息数量
     *
     * @param tv
     */
    public static void setSmsCount(TextView tv) {
        int nNewSmsCount = BusinessManager.getInstance().getSMSInit() == SMSInit.Initing ? -1 : BusinessManager.getInstance().getNewSmsNumber();
        TextView mTvSmsCount = HomeActivity.mTvHomeMessageCount;
        if (nNewSmsCount <= 0) {
            mTvSmsCount.setVisibility(View.GONE);
        } else if (nNewSmsCount < 10) {
            mTvSmsCount.setVisibility(View.VISIBLE);
            mTvSmsCount.setText(String.valueOf(nNewSmsCount));
            int nDrawable = R.drawable.tab_sms_new;
            mTvSmsCount.setBackgroundResource(nDrawable);
        } else {
            mTvSmsCount.setVisibility(View.VISIBLE);
            mTvSmsCount.setText("");
            int nDrawable = R.drawable.tab_sms_new_9_plus;
            mTvSmsCount.setBackgroundResource(nDrawable);
        }
    }

}
