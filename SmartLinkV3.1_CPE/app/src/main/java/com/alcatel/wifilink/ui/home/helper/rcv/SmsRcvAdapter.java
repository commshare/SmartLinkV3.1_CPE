package com.alcatel.wifilink.ui.home.helper.rcv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.wlan.AP;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.model.sms.SMSContactList;
import com.alcatel.wifilink.model.sms.SmsSingle;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.sms.SmsCountHelper;
import com.alcatel.wifilink.ui.sms.activity.SmsDetailActivity;
import com.alcatel.wifilink.utils.OtherUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.alcatel.wifilink.ui.home.helper.cons.Cons.DRAFT;
import static com.alcatel.wifilink.ui.home.helper.cons.Cons.SENT_FAILED;
import static com.alcatel.wifilink.ui.home.helper.cons.Cons.UNREAD;

public class SmsRcvAdapter extends RecyclerView.Adapter<SmsHolder> {

    private Context context;
    private SMSContactList smsContactListTotal;
    private List<SMSContactList.SMSContact> smsContactList;
    private OnRcvLongClickListener onRcvLongClickListener;

    public SmsRcvAdapter(Context context, SMSContactList smsContactListTotal) {
        this.context = context;
        this.smsContactListTotal = smsContactListTotal;
    }

    public void notifys(SMSContactList smsContactList) {
        this.smsContactListTotal = smsContactList;
        notifyDataSetChanged();
    }

    @Override
    public SmsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SmsHolder(LayoutInflater.from(context).inflate(R.layout.item_sms_update, parent, false));
    }

    @Override
    public void onBindViewHolder(SmsHolder holder, int position) {
        // TOAT: 总方法汇聚
        if (smsContactListTotal != null) {
            // sort by date
            smsContactList = this.smsContactListTotal.getSMSContactList();
            // Collections.sort(smsContactList, new SmsDateSort());
            // set ui
            setSmsPoint(holder, position);// set sms point
            setPhoneNum(holder, position);// set telphone
            setSmsCount(holder, position);// set sms count
            setSmsContent(holder, position);// set sms simple content
            // setSmsSendFailed(holder, position);// set sms send failed logo (keep it)
            setSmsDate(holder, position);// set date
            setSmsClick(holder, position);// to sms detail
            setSmsLongClick(holder, position);// to show delete pop
        }
    }

    @Override
    public int getItemCount() {
        return smsContactListTotal != null ? smsContactListTotal.getSMSContactList().size() : 0;
    }

    /* **** setSmsPoint **** */
    private void setSmsPoint(SmsHolder holder, int position) {
        SMSContactList.SMSContact smsContact = smsContactList.get(position);
        int smsType = smsContact.getSMSType();
        // 查看缓冲区是否有当前contactid对应的未读短信数量
        int unreadCache = SmsCountHelper.getUnreadCache(smsContact.getContactId());
        // 以下4种情况均需要显示对应的点
        boolean pointShow = smsType == UNREAD || smsType == SENT_FAILED || smsType == DRAFT || unreadCache > 0;
        holder.iv_smsPoint.setVisibility(pointShow ? VISIBLE : GONE);
        if (smsType == UNREAD) {
            holder.iv_smsPoint.setImageResource(R.drawable.sms_point_unread);
        } else if (smsType == DRAFT) {
            holder.iv_smsPoint.setImageResource(R.drawable.sms_edit);
        } else if (smsType == SENT_FAILED) {
            holder.iv_smsPoint.setImageResource(R.drawable.sms_prompt);
        } else if (unreadCache > 0) {
            holder.iv_smsPoint.setImageResource(R.drawable.sms_point_unread);
        }
    }

    /* **** setPhoneNum **** */
    private void setPhoneNum(SmsHolder holder, int position) {
        SMSContactList.SMSContact smsContact = smsContactList.get(position);
        List<String> phoneNumber = smsContact.getPhoneNumber();
        String phone = OtherUtils.stitchPhone(context, phoneNumber);
        holder.tv_smsPhone.setText(phone);
    }

    /* **** setSmsCount **** */
    private void setSmsCount(SmsHolder holder, int position) {
        SMSContactList.SMSContact smsContact = smsContactList.get(position);
        holder.tv_smsCount.setText(String.valueOf(smsContact.getTSMSCount()));
    }

    /* **** setSmsContent **** */
    private void setSmsContent(SmsHolder holder, int position) {
        SMSContactList.SMSContact smsContact = smsContactList.get(position);
        holder.tv_smsContent.setText(smsContact.getSMSContent());
    }

    /* **** setSmsDate **** */
    private void setSmsDate(SmsHolder holder, int position) {
        SMSContactList.SMSContact smsContact = smsContactList.get(position);
        String date = OtherUtils.transferDate(smsContact.getSMSTime());
        holder.tv_smsDate.setText(date);
    }

    /* **** setSmsClick **** */
    private void setSmsClick(SmsHolder holder, int position) {
        SMSContactList.SMSContact smsContact = smsContactList.get(position);
        holder.rl_sms.setOnClickListener(v -> {
            // 设置为已读
            setReaded(smsContact);
            // 跳转
            EventBus.getDefault().postSticky(smsContact);
            ChangeActivity.toActivity(context, SmsDetailActivity.class, false, false, false, 0);
        });
    }

    /* **** setSmsLongClick **** */
    private void setSmsLongClick(SmsHolder holder, int position) {
        holder.rl_sms.setOnLongClickListener(v -> {
            if (onRcvLongClickListener != null) {
                onRcvLongClickListener.getPosition(position);
            }
            return true;
        });
    }

    /* **** setSmsSendFailed **** */
    private void setSmsSendFailed(SmsHolder holder, int position) {
        SMSContactList.SMSContact smsContact = smsContactList.get(position);
        holder.iv_smsSendFailed.setVisibility(smsContact.getSMSType() == Cons.SENT_FAILED ? VISIBLE : GONE);
    }


    /* 调用此方法, 路由器自动设置为已读 */
    private void setReaded(SMSContactList.SMSContact smsContact) {
        // 清空缓冲区短信未读数量
        HomeActivity.smsUnreadMap.put(smsContact.getContactId(), 0);
        // 调用此接口的目的是为了告知路由器该ID下的短信已读
        API.get().getSingleSMS(smsContact.getSMSId(), new MySubscriber<SmsSingle>() {
            @Override
            protected void onSuccess(SmsSingle result) {

            }
        });
    }

    public interface OnRcvLongClickListener {
        void getPosition(int position);
    }

    public void setOnRcvLongClickListener(OnRcvLongClickListener onRcvLongClickListener) {
        this.onRcvLongClickListener = onRcvLongClickListener;
    }

}
