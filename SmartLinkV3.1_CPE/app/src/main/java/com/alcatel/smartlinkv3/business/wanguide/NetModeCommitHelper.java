package com.alcatel.smartlinkv3.business.wanguide;

import android.content.Context;

import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.WanManager;

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
        WanManager wanManager = BusinessManager.getInstance().getWanManager();
        wanManager.setOnWanSettingListener(isSuccess -> {
             /*请求成功后--> 发送标记 + WAN口信息(非必要)--> 跳转到状态页*/
            getStatusBean(setStatus(statusBean, isSuccess));
        });
        wanManager.setWanSetting(statusBean.getWanInfo());/* 提交 */
    }

    /**
     * H1.修改原有的statusbean的成功状态
     *
     * @param statusBean 原对象
     * @param isSuccess  是否成功
     * @return 状态修改后的对象
     */
    private StatusBean setStatus(StatusBean statusBean, boolean isSuccess) {
        if (isSuccess) {
            statusBean.setStatus(1);
        } else {
            statusBean.setStatus(0);
        }
        return statusBean;
    }


}
