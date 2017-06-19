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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SmsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<SMSSummaryItem> m_smsContactMessagesLstData;

    public SmsAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public SmsAdapter(Context context, ArrayList<SMSSummaryItem> m_smsContactMessagesLstData) {
        this.context = context;
        this.m_smsContactMessagesLstData = m_smsContactMessagesLstData;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return m_smsContactMessagesLstData.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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


        SMSSummaryItem smsItem = m_smsContactMessagesLstData.get(position);
        holder.number.setText(smsItem.strNumber);
        holder.content.setText(smsItem.strSummaryContent);

        int nUnreadNum = smsItem.nUnreadNumber;
        if (nUnreadNum == 0) {
            holder.unreadImage.setVisibility(View.INVISIBLE);
        } else {
            holder.unreadImage.setVisibility(View.VISIBLE);
        }

        Date summaryDate = DataUti.formatDateFromString(smsItem.strSummaryTime);

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
        switch (smsItem.enumSmsType) {
            case SentFailed:
                holder.sentFailedImage.setVisibility(View.VISIBLE);
                holder.totalcount.setVisibility(View.VISIBLE);
                holder.totalcount.setText(String.valueOf(smsItem.nCount));
                holder.count.setVisibility(View.INVISIBLE);
                break;
            case Draft:
                holder.sentFailedImage.setVisibility(View.INVISIBLE);
                holder.totalcount.setVisibility(View.VISIBLE);
                holder.totalcount.setTextColor(context.getResources().getColor(R.color.color_grey));
                holder.totalcount.setText(String.format(context.getString(R.string.sms_list_view_draft), smsItem.nCount));
                holder.count.setVisibility(View.INVISIBLE);
                break;
            default:
                holder.sentFailedImage.setVisibility(View.INVISIBLE);
                holder.totalcount.setVisibility(View.VISIBLE);
                holder.totalcount.setText(String.valueOf(smsItem.nCount));
                holder.count.setVisibility(View.INVISIBLE);
                holder.count.setText(String.format(context.getResources().getString(R.string.sms_unread_num), smsItem.nUnreadNumber));
                break;
        }

        return convertView;
    }

}
