package com.alcatel.wifilink.ui.sms.fragment.block;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.device.other.BlockModel;
import com.alcatel.wifilink.model.device.response.BlockList;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.sms.allsetup.ActivityDeviceManager;
import com.alcatel.wifilink.ui.sms.helper.FragmentDeviceEnum;

import java.util.List;

/**
 * Created by qianli.ma on 2017/6/30.
 */

public class BlockAdapter extends RecyclerView.Adapter<BlockHolder> {

    public ActivityDeviceManager activity;
    private List<BlockModel> blockModelList;
    String m_strEditString = new String();

    public BlockAdapter(Activity activity, List<BlockModel> blockModelList) {
        this.activity = (ActivityDeviceManager) activity;
        this.blockModelList = blockModelList;
    }

    public void notifys(List<BlockModel> blockModelList) {
        this.blockModelList = blockModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return blockModelList != null ? blockModelList.size() : 0;
    }

    @Override
    public BlockHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BlockHolder(LayoutInflater.from(activity).inflate(R.layout.device_manage_block_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BlockHolder holder, int position) {
        BlockModel blockModel = blockModelList.get(position);
        BlockList.BlockDevice block = blockModel.block;

        final String displayName = block.DeviceName;
        holder.deviceName.setText(displayName);
        String mac = block.MacAddress;
        holder.mac.setText(String.format(activity.getString(R.string.device_manage_mac), mac));
        // Click the unblock button
        holder.unblockBtn.setOnClickListener(v -> setDeviceUnlock(displayName, mac, position));
    }

    /* setDeviceUnlock */
    private void setDeviceUnlock(String strDeviceName, String strMac, int position) {
        API.get().setDeviceUnblock(strDeviceName, strMac, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                blockModelList.remove(position);
                if (blockModelList.size() <= 0) {// if block name is empty then go to connect ui
                    activity.toFragment(FragmentDeviceEnum.CONNECT);
                }
                // refresh ui
                notifys(blockModelList);
            }
        });
    }
}
