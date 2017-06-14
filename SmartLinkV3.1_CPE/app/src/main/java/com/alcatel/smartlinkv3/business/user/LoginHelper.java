package com.alcatel.smartlinkv3.business.user;

import android.content.Context;

import com.alcatel.smartlinkv3.business.UserManager;
import com.alcatel.smartlinkv3.common.DataValue;

/**
 * Created by qianli.ma on 2017/6/9.
 */

public abstract class LoginHelper
{
    private Context context;

    public abstract void getLoginStatus(boolean success);

    public LoginHelper(Context context) {
        this.context = context;
        
    }

    public void login(DataValue data) {
        UserManager userManager = new UserManager(context);
        userManager.setOnLoginStatusListener(success -> {
            getLoginStatus(success);
        });
        userManager.login(data);
    }
}
