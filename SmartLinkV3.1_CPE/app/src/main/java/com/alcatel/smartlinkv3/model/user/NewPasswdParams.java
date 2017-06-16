package com.alcatel.smartlinkv3.model.user;

/**
 * Created by tao.j on 2017/6/15.
 */

public class NewPasswdParams {
    String UserName;
    String CurrPassword;
    String NewPassword;

    public NewPasswdParams(String userName, String currPassword, String newPassword) {
        UserName = userName;
        CurrPassword = currPassword;
        NewPassword = newPassword;
    }
}
