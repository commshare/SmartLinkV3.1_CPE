package com.alcatel.wifilink.ui.home.helper.sms;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.sms.SMSContactList;
import com.alcatel.wifilink.model.sms.SmsInitState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

import java.util.Set;

/**
 * Created by qianli.ma on 2017/6/17.
 */

public class SmsCountHelper {

    private static Activity activity;
    private static TextView tv;

    /**
     * 显示主页消息数量
     *
     * @param tv
     */
    public static void setSmsCount(Activity activity, TextView tv) {
        SmsCountHelper.activity = activity;
        // check the init state
        API.get().getSmsInitState(new MySubscriber<SmsInitState>() {
            @Override
            protected void onSuccess(SmsInitState result) {
                if (result.getState() == Cons.SMS_COMPLETE) {
                    getSmsContactList(tv);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });

    }

    private static void getSmsContactList(TextView mTvSmsCount) {
        API.get().getSMSContactList(0, new MySubscriber<SMSContactList>() {
            @Override
            protected void onSuccess(SMSContactList result) {
                activity.runOnUiThread(() -> {
                    // caculate the sms count
                    int unReadCount = 0;
                    for (SMSContactList.SMSContact smsContact : result.getSMSContactList()) {

                        // 到缓冲区去查看是否有缓冲的未读短信数量
                        int unreadCache = getUnreadCache(smsContact.getContactId());
                        int unreadCount = smsContact.getUnreadCount();
                        // 取得两者当中数量不为0的那一个值
                        if (unreadCache > 0) {
                            unReadCount += unreadCache;
                        } else if (unreadCount > 0) {
                            unReadCount += unreadCount;
                        } else {
                            unReadCount += unreadCount;
                        }
                    }


                    // show sms ui according the count
                    if (unReadCount <= 0) {
                        mTvSmsCount.setVisibility(View.GONE);
                    } else if (unReadCount < 10) {
                        mTvSmsCount.setVisibility(View.VISIBLE);
                        mTvSmsCount.setText(String.valueOf(unReadCount));
                        int nDrawable = R.drawable.tab_sms_new;
                        mTvSmsCount.setBackgroundResource(nDrawable);
                    } else {
                        mTvSmsCount.setVisibility(View.VISIBLE);
                        mTvSmsCount.setText("");
                        int nDrawable = R.drawable.tab_sms_new_9_plus;
                        mTvSmsCount.setBackgroundResource(nDrawable);
                    }
                });

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });
    }

    /**
     * 获取缓冲区里的未读短信
     *
     * @param contactId
     * @return
     */
    public static int getUnreadCache(long contactId) {
        int unreadCache = 0;
        Set<Long> contactIds = HomeActivity.smsUnreadMap.keySet();
        for (Long id : contactIds) {
            if (id == contactId) {
                unreadCache = HomeActivity.smsUnreadMap.get(id);
            }
        }
        return unreadCache;
    }

}
