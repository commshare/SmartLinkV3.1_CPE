package com.alcatel.wifilink.rx.helper.base;

import android.app.Activity;
import android.content.Context;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.NetworkRegisterState;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

/**
 * Created by qianli.ma on 2017/11/24 0024.
 */

public abstract class NetworkInfoHelper {
    private Activity activity;

    public NetworkInfoHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 获取2G\3G\4G
     *
     * @param context
     * @param type
     * @return
     */
    public String getSignalType(Context context, int type) {
        if (type == Cons.NOSERVER) {
            return "- -";
        } else if (type >= Cons.GPRS && type <= Cons.EDGE) {
            return context.getString(R.string.home_network_type_2g);
        } else if (type >= Cons.HSPA && type <= Cons.DC_HSPA_PLUS) {
            return context.getString(R.string.home_network_type_3g);
        } else if (type >= Cons.LTE && type <= Cons.LTE_PLUS) {
            return context.getString(R.string.home_network_type_4g);
        } else if (type >= Cons.CDMA && type <= Cons.GSM) {
            return context.getString(R.string.home_network_type_2g);
        } else if (type == Cons.EVDO) {
            return context.getString(R.string.home_network_type_3g);
        } else if (type >= Cons.LTE_FDD && type <= Cons.LTE_TDD) {
            return context.getString(R.string.home_network_type_4g);
        } else if (type == Cons.CDMA_Ehrpd) {
            return context.getString(R.string.home_network_type_3g_plus);
        } else {
            return "- -";
        }
    }

    /**
     * 获取network对象
     */
    public void get() {
        RX.getInstant().getNetworkRegisterState(new ResponseObject<NetworkRegisterState>() {
            @Override
            protected void onSuccess(NetworkRegisterState result) {
                int registState = result.getRegist_state();
                if (registState == Cons.REGISTER_SUCCESSFUL) {
                    // TODO: 2017/11/26 0026 暂时不用
                    getNetworkInfo();
                } else {
                    noRegister();
                }
            }

            /**
             * 获取network信息
             */
            private void getNetworkInfo() {
                RX.getInstant().getNetworkInfo(new ResponseObject<NetworkInfos>() {
                    @Override
                    protected void onSuccess(NetworkInfos result) {
                        register(result);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        noRegister();
                    }

                    @Override
                    public void onError(Throwable e) {
                        noRegister();
                    }
                });
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                noRegister();
            }

            @Override
            public void onError(Throwable e) {
                noRegister();
            }

        });
    }

    public abstract void noRegister();

    public abstract void register(NetworkInfos result);
}
