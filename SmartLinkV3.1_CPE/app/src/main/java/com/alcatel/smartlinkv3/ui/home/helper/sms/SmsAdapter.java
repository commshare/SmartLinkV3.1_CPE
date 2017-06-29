package com.alcatel.smartlinkv3.ui.home.helper.sms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.DataUti;
import com.alcatel.smartlinkv3.model.sms.SMSContactList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SmsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    // private ArrayList<SMSSummaryItem> m_smsContactMessagesLstData;
    public List<SMSContactList.SMSContact> smsContacts;

    public SmsAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public SmsAdapter(Context context, List<SMSContactList.SMSContact> smsContacts) {
        this.context = context;
        this.smsContacts = smsContacts;
    }

    public void notifity(List<SMSContactList.SMSContact> smsContacts) {
        this.smsContacts = smsContacts;
        notifyDataSetChanged();
    }

    public int getCount() {
        return smsContacts.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public final class ViewHolder {
        public ImageView unreadImage;
        public TextView number;
        public ImageView sentFailedImage;
        public TextView count;
        public TextView totalcount;
        public TextView content;
        public TextView time;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.sms_list_item, null);
            holder.unreadImage = (ImageView) convertView.findViewById(R.id.sms_item_unread_image);
            holder.number = (TextView) convertView.findViewById(R.id.sms_item_number);
            holder.sentFailedImage = (ImageView) convertView.findViewById(R.id.sms_item_send_failed);
            holder.count = (TextView) convertView.findViewById(R.id.sms_item_count);
            holder.totalcount = (TextView) convertView.findViewById(R.id.sms_item_totalcount);
            holder.content = (TextView) convertView.findViewById(R.id.sms_item_content);
            holder.time = (TextView) convertView.findViewById(R.id.sms_item_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        // TOAT: info item
        //SMSSummaryItem smsItem = m_smsContactMessagesLstData.get(position);
        SMSContactList.SMSContact smsContact = smsContacts.get(position);

        holder.number.setText(SmsNumHelper.getNew(smsContact.getPhoneNumber()));
        holder.content.setText(smsContact.getSMSContent());

        int nUnreadNum = smsContact.getUnreadCount();
        if (nUnreadNum == 0) {
            holder.unreadImage.setVisibility(View.INVISIBLE);
        } else {
            holder.unreadImage.setVisibility(View.VISIBLE);
        }

        Date summaryDate = DataUti.formatDateFromString(smsContact.getSMSTime());

        String strTimeText = new String();
        if (summaryDate != null) {
            Date now = new Date();
            if (now.getYear() == summaryDate.getYear() && now.getMonth() == summaryDate.getMonth() && now.getDate() == summaryDate.getDate()) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                strTimeText = format.format(summaryDate);
            } else {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                strTimeText = format.format(summaryDate);
            }
        }
        holder.time.setText(strTimeText);

        //Read, Unread, Sent, SentFailed, Report, Flash,Draft;
        int smsType = smsContact.getSMSType();
        switch (smsType) {
            case 3:
                holder.sentFailedImage.setVisibility(View.VISIBLE);
                holder.totalcount.setVisibility(View.VISIBLE);
                holder.totalcount.setText(String.valueOf(smsContact.getTSMSCount()));
                holder.count.setVisibility(View.INVISIBLE);
                break;
            case 6:
                holder.sentFailedImage.setVisibility(View.INVISIBLE);
                holder.totalcount.setVisibility(View.VISIBLE);
                holder.totalcount.setTextColor(context.getResources().getColor(R.color.color_grey));
                holder.totalcount.setText(String.format(context.getString(R.string.sms_list_view_draft), smsContact.getTSMSCount()));
                holder.count.setVisibility(View.INVISIBLE);
                break;
            default:
                holder.sentFailedImage.setVisibility(View.INVISIBLE);
                holder.totalcount.setVisibility(View.VISIBLE);
                holder.totalcount.setText(String.valueOf(smsContact.getTSMSCount()));
                holder.count.setVisibility(View.INVISIBLE);
                holder.count.setText(String.format(context.getResources().getString(R.string.sms_unread_num), smsContact.getUnreadCount()));
                break;
        }

        return convertView;
    }

}
