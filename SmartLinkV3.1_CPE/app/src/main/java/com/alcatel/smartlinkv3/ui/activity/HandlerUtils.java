package com.alcatel.smartlinkv3.ui.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.MessageUti;

import java.lang.reflect.Field;

public class HandlerUtils {
	
	  public static void replaceHandler()
	  {
	    try
	    {
	      Class<?> clazz = Class.forName("android.app.ActivityThread");
	      Object obj = clazz.getMethod("currentActivityThread").invoke(null);
	      Field handlerField = clazz.getDeclaredField("mH");
	      handlerField.setAccessible(true);
	      Handler handler = (Handler)handlerField.get(obj);
	      Field callbackField = Class.forName("android.os.Handler").getDeclaredField("mCallback");
	      callbackField.setAccessible(true);
	      callbackField.set(handler, new AcHandler((Handler.Callback)callbackField.get(handler)));	      
	    }
	    catch (Exception e)
	    {
	     e.printStackTrace();
	    }
	  }

	public static class AcHandler implements Handler.Callback {	

		public AcHandler(Handler.Callback paramCallback) {			
		}

		@Override
		public boolean handleMessage(Message msg) {			
			switch(msg.what)
			{
		
			//STOP_ACTIVITY_HIDE(home��ʽ�˳�)
			case 104:
			//CLEAN_UP_CONTEXT(back��ʽ�˳�)
			case 119:
		    	ActivityManager am = (ActivityManager)SmartLinkV3App.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);    	
		    	ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		    	if(!cn.getPackageName().equalsIgnoreCase("com.alcatel.smartlinkv3")) {
		    		userLogout();
		    	}				
				break;
			//SLEEPING(power)	
			case 137:			
				userLogout();
				break;
			}
		
			return false;
		}
	};
	
	public static void userLogout() {
		UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
		if(FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin") != true)
		{
			if (m_loginStatus != null && m_loginStatus == UserLoginStatus.login) {
				BusinessManager.getInstance().sendRequestMessage(
						MessageUti.USER_LOGOUT_REQUEST, null);
			}
		}
	}
}
