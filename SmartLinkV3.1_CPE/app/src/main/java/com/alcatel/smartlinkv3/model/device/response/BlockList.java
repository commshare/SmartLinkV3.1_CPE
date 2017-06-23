package com.alcatel.smartlinkv3.model.device.response;

import java.util.List;

/**
 * Created by qianli.ma on 2017/6/22.
 */

public class BlockList {

    public List<BlockDevice> BlockListpub;

    @Override
    public String toString() {
        return "BlockList{" + "BlockListpub=" + BlockListpub + '}';
    }

    public List<BlockDevice> getBlockListpub() {
        return BlockListpub;
    }

    public void setBlockListpub(List<BlockDevice> blockListpub) {
        BlockListpub = blockListpub;
    }

    public class BlockDevice {
        public int id;
        public String DeviceName;
        public String MacAddress;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDeviceName() {
            return DeviceName;
        }

        public void setDeviceName(String deviceName) {
            DeviceName = deviceName;
        }

        public String getMacAddress() {
            return MacAddress;
        }

        public void setMacAddress(String macAddress) {
            MacAddress = macAddress;
        }

        @Override
        public String toString() {
            return "BlockDevice{" + "id=" + id + ", DeviceName='" + DeviceName + '\'' + ", MacAddress='" + MacAddress + '\'' + '}';
        }
    }

}
