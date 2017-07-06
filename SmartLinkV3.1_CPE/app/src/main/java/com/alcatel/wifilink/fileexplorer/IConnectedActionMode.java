package com.alcatel.wifilink.fileexplorer;

import android.app.ActionBar;
import android.view.ActionMode;

/**
 * Created by haodi.liang on 2016/12/27.
 */

public interface IConnectedActionMode {
    public void setActionMode(ActionMode actionMode);

    public ActionMode getActionMode();

    public ActionMode launchActionMode(ActionMode.Callback callback);

    public ActionBar obtainActionBar();
}
