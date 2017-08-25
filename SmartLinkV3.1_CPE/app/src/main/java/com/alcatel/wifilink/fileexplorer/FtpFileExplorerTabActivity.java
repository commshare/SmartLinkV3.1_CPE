/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alcatel.wifilink.fileexplorer;

import android.app.ActionBar;
import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.ActionBar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.TextView;

import java.lang.reflect.Field;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.ui.activity.BaseFragmentActivity;

public class FtpFileExplorerTabActivity extends BaseFragmentActivity
	implements IConnectedActionMode {
	private ActionMode mActionMode;

	@Override
	public void setActionMode(ActionMode actionMode) {
		this.mActionMode = actionMode;
	}
	@Override
	public ActionMode getActionMode() {
		return this.mActionMode;
	}	
	@Override
	public ActionMode launchActionMode(ActionMode.Callback callback) {
		return startActionMode(callback);
	}
	@Override
	public ActionBar obtainActionBar() {
		return getActionBar();
	}

	public interface OnBackPressedListener {
		/**
		 * @return True: 表示已经处理; False: 没有处理，让基类处理。
		 */
		boolean onBack();
	}

	private OnBackPressedListener mBackPressedListener;

	private boolean initCustomActionBar(Activity activity) {
		final Activity _activity = activity;
		
		ActionBar mActionbar = _activity.getActionBar();
		if (mActionbar == null) {
			return false;
		}

		mActionbar.setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | 
				ActionBar.DISPLAY_SHOW_HOME | 
				ActionBar.DISPLAY_SHOW_TITLE);
		
		mActionbar.setDisplayShowCustomEnabled(true);
		
		mActionbar.setCustomView(R.layout.custom_actionbar);
		
		TextView tvTitle = (TextView) mActionbar.getCustomView().findViewById(R.id.tv_title_title);
		tvTitle.setText(R.string.microsd_file);
		mActionbar.getCustomView().findViewById(R.id.ib_title_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						_activity.finish();
					}
				});
		mActionbar.getCustomView().findViewById(R.id.tv_title_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						_activity.finish();
					}
				});
		return true;
	}
	
	private void setOverflowShowingAlways() {
	    try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        menuKeyField.setAccessible(true);
	        menuKeyField.setBoolean(config, false);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (initCustomActionBar(this)) {
		    setOverflowShowingAlways();
		    
			try {	
				Fragment fragment = Fragment.instantiate(this,
						FtpFileViewFragment.class.getName(), savedInstanceState);
				mBackPressedListener = (FtpFileViewFragment) fragment;
				mRequestExListener = (OnRequestExListener) fragment;
		
				FragmentManager fragmentManager = this.getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(android.R.id.content, fragment);
				fragmentTransaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
				this.finish();
			}
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		if (!mBackPressedListener.onBack()) {
			super.onBackPressed();
		}
	}
	
	private OnRequestExListener mRequestExListener;
	
	@Override 
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
	           
	    if (resultCode == Activity.RESULT_OK) {
            switch (reqCode) {
            
            case ExternalFileObtain.PICK_CONTACT : 
                ExternalFileObtain.getInstance(this).analysisFile(data);
                break;
                
            case IntentBuilder.REQUEST_EX :
                Uri uri = data.getData();
                if (mRequestExListener != null)
                    mRequestExListener.onRequestResult(reqCode, uri);
                break;
                
            }
	    }
        
        super.onActivityResult(reqCode, resultCode, data);
    }

}

interface OnRequestExListener {
    boolean onRequestResult(int reqCode, Uri uri);
}
