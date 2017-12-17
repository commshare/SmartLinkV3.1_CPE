package com.alcatel.wifilink.rx.helper.base;

import android.app.Activity;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

/**
 * Created by qianli.ma on 2017/11/16 0016.
 */

public abstract class LogoutHelper {

    private Activity context;

    public abstract void logoutFinish();

    public LogoutHelper(Activity context) {
        this.context = context;
        logout();
    }

    private void logout() {
        RX.getInstant().getLoginState(new ResponseObject<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                int state = result.getState();
                if (state == Cons.LOGIN) {
                    RX.getInstant().logout(new ResponseObject() {
                        @Override
                        protected void onSuccess(Object result) {
                            ToastUtil_m.show(context, context.getString(R.string.login_logout_successful));
                            logoutFinish();
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            ToastUtil_m.show(context, context.getString(R.string.login_logout_failed));
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtil_m.show(context, context.getString(R.string.login_logout_failed));
                        }
                    });
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                ToastUtil_m.show(context, context.getString(R.string.login_logout_failed));
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil_m.show(context, context.getString(R.string.login_logout_failed));
            }
        });
    }

}
