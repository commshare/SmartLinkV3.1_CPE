package com.alcatel.smartlinkv3.rx.helper;

import android.app.Activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.Cons;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;

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
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                int state = result.getState();
                if (state == Cons.LOGIN) {
                    API.get().logout(new MySubscriber() {
                        @Override
                        protected void onSuccess(Object result) {
                            ToastUtil_m.show(context, context.getString(R.string.login_logout_successful));
                            logoutFinish();
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            ToastUtil_m.show(context, context.getString(R.string.logout_failed));
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtil_m.show(context, context.getString(R.string.logout_failed));
                        }
                    });
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                ToastUtil_m.show(context, context.getString(R.string.logout_failed));
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil_m.show(context, context.getString(R.string.logout_failed));
            }
        });
    }

}
