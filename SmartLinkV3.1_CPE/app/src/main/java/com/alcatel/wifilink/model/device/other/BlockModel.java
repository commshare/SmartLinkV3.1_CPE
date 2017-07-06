package com.alcatel.wifilink.model.device.other;

import com.alcatel.wifilink.model.device.response.BlockList;

/**
 * Created by qianli.ma on 2017/6/24.
 */

public class BlockModel {
    public BlockList.BlockDevice block;
    public boolean isEdit;

    public BlockModel(BlockList.BlockDevice block, boolean isEdit) {
        this.block = block;
        this.isEdit = isEdit;
    }

    @Override
    public String toString() {
        return "BlockModel{" + "block=" + block + ", isEdit=" + isEdit + '}';
    }

    public BlockList.BlockDevice getBlock() {
        return block;
    }

    public void setBlock(BlockList.BlockDevice block) {
        this.block = block;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }
}
