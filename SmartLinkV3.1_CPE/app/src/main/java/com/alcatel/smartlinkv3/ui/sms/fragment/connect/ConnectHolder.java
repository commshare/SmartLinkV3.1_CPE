package com.alcatel.smartlinkv3.ui.sms.fragment.connect;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

/**
 * Created by qianli.ma on 2017/6/30.
 */

public class ConnectHolder extends RecyclerView.ViewHolder {
    
    public Button blockBtn;
    public ImageView icon;
    public TextView deviceNameTextView;
    public EditText deviceNameEditView;
    public RelativeLayout deviceNameLayout;
    public ImageView modifyDeviceName;
    public TextView ip;
    public TextView mac;
    public TextView host;

    public ConnectHolder(View convertView) {
        super(convertView);
        blockBtn = (Button) convertView.findViewById(R.id.block_button);
        icon = (ImageView) convertView.findViewById(R.id.icon);
        deviceNameTextView = (TextView) convertView.findViewById(R.id.device_description_textview);
        deviceNameEditView = (EditText) convertView.findViewById(R.id.device_description_editview);
        deviceNameLayout = (RelativeLayout) convertView.findViewById(R.id.device_name_layout);
        modifyDeviceName = (ImageView) convertView.findViewById(R.id.edit_image);
        ip = (TextView) convertView.findViewById(R.id.ip);
        mac = (TextView) convertView.findViewById(R.id.mac);
        host = (TextView) convertView.findViewById(R.id.host);
    }
}
