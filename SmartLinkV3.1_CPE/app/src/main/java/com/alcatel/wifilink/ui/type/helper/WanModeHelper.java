package com.alcatel.wifilink.ui.type.helper;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.wanguide.NetModeUiItemHelper;
import com.alcatel.wifilink.business.wanguide.WanInfo;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

/**
 * Created by qianli.ma on 2017/7/21.
 */

public class WanModeHelper {


    private final static String MTU = "1492";
    private static OnNetModeItemListener onNetModeItemListener;
    private Context context;
    private WanSettingsResult wanInfo;


    public WanModeHelper(Context context) {
        this.context = context;
    }

    /**
     * 根据类型切换索引(固件返回的索引需要对应UI的索引)
     *
     * @param wanInfo
     * @return
     */
    public int transferIndex(WanSettingsResult wanInfo) {
        int type = wanInfo.getConnectType();
        int index = 0;
        switch (type) {
            case Cons.DHCP:
                index = 0;
                break;
            case Cons.PPPOE:
                index = 1;
                break;
            case Cons.STATIC:
                index = 2;
                break;
        }
        return index;
    }

    /**
     * P.设置主选项组UI [DHCP|PPPOE|STATIC_IP]
     *
     * @param wanInfo WAN口对象--> 用于获取类型
     * @param rbs     主选项卡数组
     * @return 更新[被选]标记的主选项卡数组
     */
    public RadioButton[] setNetModeUi(WanSettingsResult wanInfo, RadioButton... rbs) {
        int index = transferIndex(wanInfo);
        // 根据输入索引进行设置
        for (int i = 0; i < rbs.length; i++) {
            if (i == index) {
                rbs[i].setChecked(true);
            } else {
                rbs[i].setChecked(false);
            }
        }
        // 显示UI
        for (RadioButton rb : rbs) {
            if (rb.isChecked()) {
                rb.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_radio_on_normal, 0);
            }
        }

        return rbs;
    }

    /**
     * P.设置选项组点击事件以及初始化上次记录
     *
     * @param rbs        选项组
     * @param mllNetMode 待变容器
     * @param wanInfo    WAN口信息
     */
    public void setOnClickAndInit(RadioButton[] rbs, LinearLayout mllNetMode, WanSettingsResult wanInfo) {
        this.wanInfo = wanInfo;
        initUi(mllNetMode, wanInfo);// 初始化UI
        // TOGO: WAN口模式的切换监听(当前屏蔽,根据后期业务需求开启|屏蔽)
        //setClick(rbs, mllNetMode);// 设置点击切换
    }


    /* -------------------------- 设置子项选项卡监听器 --------------------------*/
    public interface OnNetModeItemListener {
        /**
         * @param itemEd 返回当前主选项卡中对应的组元素
         */
        void getValue(EditText... itemEd);
    }

    public void setOnNetModeItemListener(OnNetModeItemListener onNetModeItemListener) {
        this.onNetModeItemListener = onNetModeItemListener;
    }

    
    /* ****************************************************** HELPER ******************************************************/

    /**
     * A1.初始化UI
     *
     * @param mllNetMode
     * @param wanInfo
     */
    private void initUi(LinearLayout mllNetMode, WanSettingsResult wanInfo) {
        int anInt = wanInfo.getConnectType();
        mllNetMode.removeAllViews();
        mllNetMode.setVisibility(View.GONE);
        switch (anInt) {
            case Cons.DHCP:// DHCP
                mllNetMode.setVisibility(View.GONE);
                break;
            case Cons.PPPOE:// pppoe
                changePPPoEUi(mllNetMode, context);
                break;
            case Cons.STATIC:// statuc
                changeStaticIpUi(mllNetMode, context);
                break;
        }
    }

    /**
     * A2.设置主选项板切换
     *
     * @param rbs
     * @param mllNetMode
     */
    private void setClick(RadioButton[] rbs, LinearLayout mllNetMode) {
    /* 1.设置RG子项点击事件 */
        for (int i = 0; i < rbs.length; i++) {
            int finalI = i;
            rbs[i].setOnClickListener(v -> {
                // 1.被点击的rb设置为选中状态
                for (int j = 0; j < rbs.length; j++) {
                    if (finalI == j) {
                        rbs[j].setChecked(true);
                    } else {
                        rbs[j].setChecked(false);
                    }
                }

                // 2.刷新选项组UI
                for (RadioButton rb : rbs) {
                    if (rb.isChecked()) {
                        rb.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_radio_on_normal, 0);
                    } else {
                        rb.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                }

                // 3.设置对应的点击事件
                switch (v.getId()) {
                    case R.id.mRb_netmode_dhcp:// MODE_DHCP
                        // 3.1.待变容器消隐
                        mllNetMode.setVisibility(View.GONE);

                        break;
                    case R.id.mRb_netmode_pppoe:// MODE_PPPOE
                        // 切换弹出为PPPOE的子项UI
                        changePPPoEUi(mllNetMode, context);

                        break;
                    case R.id.mRb_netmode_staticIp:// Static IP
                        // 切换弹出为STATICIP的子项UI
                        changeStaticIpUi(mllNetMode, context);
                        break;
                }
            });
        }
    }

    /**
     * A3.切换弹出为PPPOE的子项UI + 切换时接口回调
     *
     * @param mllNetMode
     * @param context
     */
    private void changePPPoEUi(LinearLayout mllNetMode, Context context) {
        mllNetMode.removeAllViews();// 清空

        NetModeUiItemHelper.ItemNetMode item_Account = NetModeUiItemHelper.getInstance(context).show(R.string.account);
        item_Account.itemEd.setCursorVisible(true);
        item_Account.itemEd.setText(wanInfo.getAccount());
        mllNetMode.addView(item_Account.itemView);

        NetModeUiItemHelper.ItemNetMode item_Password = NetModeUiItemHelper.getInstance(context).show(R.string.password);
        item_Password.itemEd.setInputType(0x81);// 密码类型
        item_Password.itemEd.setText(wanInfo.getPassword());
        mllNetMode.addView(item_Password.itemView);

        NetModeUiItemHelper.ItemNetMode item_MTU = NetModeUiItemHelper.getInstance(context).show(R.string.netmode_mtu);
        item_MTU.itemSubTv.setVisibility(View.VISIBLE);
        item_MTU.itemEd.setText(MTU);
        item_MTU.itemEd.setEnabled(true);
        mllNetMode.addView(item_MTU.itemView);

        mllNetMode.setVisibility(View.VISIBLE);// 显示

        if (onNetModeItemListener != null) {
            onNetModeItemListener.getValue(item_Account.itemEd, item_Password.itemEd, item_MTU.itemEd);
        }
    }

    /**
     * A4.切换弹出为StaticIP的子项UI + 切换时接口回调
     *
     * @param mllNetMode
     * @param context
     */
    private void changeStaticIpUi(LinearLayout mllNetMode, Context context) {
        mllNetMode.removeAllViews();// 清空
        NetModeUiItemHelper.ItemNetMode item_ipAddress = NetModeUiItemHelper.getInstance(context).show(R.string.ip_address);
        item_ipAddress.itemEd.setCursorVisible(true);
        item_ipAddress.itemEd.setText(wanInfo.getIpAddress());
        mllNetMode.addView(item_ipAddress.itemView);

        NetModeUiItemHelper.ItemNetMode item_subMask = NetModeUiItemHelper.getInstance(context).show(R.string.subnet_mask);
        item_subMask.itemEd.setText(wanInfo.getSubNetMask());
        mllNetMode.addView(item_subMask.itemView);

        mllNetMode.setVisibility(View.VISIBLE);// 显示

        if (onNetModeItemListener != null) {
            onNetModeItemListener.getValue(item_ipAddress.itemEd, item_subMask.itemEd);
        }
    }
}
