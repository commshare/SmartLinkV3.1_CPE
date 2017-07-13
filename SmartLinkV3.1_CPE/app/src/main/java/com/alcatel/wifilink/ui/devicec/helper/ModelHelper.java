package com.alcatel.wifilink.ui.devicec.helper;

import com.alcatel.wifilink.model.device.other.BlockModel;
import com.alcatel.wifilink.model.device.other.ConnectModel;
import com.alcatel.wifilink.model.device.response.BlockList;
import com.alcatel.wifilink.model.device.response.ConnectedList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qianli.ma on 2017/6/24.
 */

public class ModelHelper {

    public static List<ConnectModel> getConnectModel(ConnectedList result) {
        List<ConnectModel> connectModels = new ArrayList<>();
        List<ConnectedList.Device> connectedList = result.getConnectedList();
        if (connectedList.size() > 0) {
            for (ConnectedList.Device device : connectedList) {
                connectModels.add(new ConnectModel(device, false));
            }
        }

        return connectModels;
    }

    public static List<BlockModel> getBlockModel(BlockList result) {
        List<BlockModel> blockModels = new ArrayList<>();
        List<BlockList.BlockDevice> blockList = result.getBlockList();
        if (blockList.size() > 0) {
            for (BlockList.BlockDevice block : blockList) {
                blockModels.add(new BlockModel(block, false));
            }
        }
        return blockModels;
    }
}
