package com.alcatel.smartlinkv3.mediaplayer.center;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.util.CommonLog;

import android.content.Context;

import com.alcatel.smartlinkv3.mediaplayer.util.CommonUtil;
import com.alcatel.smartlinkv3.mediaplayer.util.LogFactory;

public class ControlCenterWorkThread extends Thread{

private static final CommonLog log = LogFactory.createLog();
	
	private static final int REFRESH_DEVICES_INTERVAL = 30 * 1000; 
	
	public static interface ISearchDeviceListener{
		public void onSearchComplete(boolean searchSuccess);
	}
	
	private ControlPoint mCP = null;
	private Context mContext = null;
	private boolean mStartComplete = false;
	private boolean mIsExit = false;
	private ISearchDeviceListener mSearchDeviceListener;
	
	public ControlCenterWorkThread(Context context, ControlPoint controlPoint){
		mContext = context;
		mCP = controlPoint; 
	}
	
	public void  setCompleteFlag(boolean flag){
		mStartComplete = flag;
	}
	
	public void setSearchListener(ISearchDeviceListener listener){
		mSearchDeviceListener = listener;
	}
	
	public void awakeThread(){
		synchronized (this) {
			notifyAll();
		}
	}
	
	
	public void reset(){
		setCompleteFlag(false);
		awakeThread();
	}
	
	public void exit(){
		mIsExit = true;
		awakeThread();
	}
	
	
	@Override
	public void run() {
		log.e("ControlCenterThread is running...");		
		
		while(true)
		{
			if (mIsExit){
				mCP.stop();
				break;
			}
			
			refreshDevices();
			
			synchronized(this)
			{		
				try
				{
					wait(REFRESH_DEVICES_INTERVAL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}				
			}
		}
		
		log.e("ControlCenterThread is over...");		
	}
	
	private void refreshDevices(){
		log.e("refreshDevices...");
		if (!CommonUtil.checkNetworkState(mContext)){
			return ;
		}

		try {
			if (mStartComplete){
				boolean searchRet = mCP.search();	
				log.e("mCP.search() ret = "  + searchRet);
				if (mSearchDeviceListener != null){
					mSearchDeviceListener.onSearchComplete(searchRet);
				}
			}else{
				boolean startRet = mCP.start();
				log.e("mCP.start() ret = "  + startRet);
				if (startRet){
					mStartComplete = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}



}
