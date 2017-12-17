package com.alcatel.wifilink.business.wanguide;

import android.content.Context;

import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;

/**
 * Created by qianli.ma on 2017/5/27.
 */

public abstract class NetModeCommitHelper {

    private Context context;

    public NetModeCommitHelper(Context context) {
        this.context = context;
    }

    public abstract void getStatusBean(StatusBean statusBean);

    /**
     * 提交
     *
     * @param statusBean
     */
    public void commit(StatusBean statusBean) {
        // 获取WAN口信息并封装
        RX.getInstant().getWanSettings(new ResponseObject<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                getStatusBean(setStatus(statusBean, result.getStatus()));
            }
        });

    }

    /**
     * H1.修改原有的statusbean的成功状态
     *
     * @param statusBean 原对象
     * @param isSuccess  是否成功
     * @return 状态修改后的对象
     */
    private StatusBean setStatus(StatusBean statusBean, int isSuccess) {
        statusBean.setStatus(isSuccess);
        return statusBean;
    }


}
