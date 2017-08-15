package com.alcatel.smartlinkv3.mediaplayer.center;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;
import com.alcatel.smartlinkv3.mediaplayer.util.CommonUtil;
import com.alcatel.smartlinkv3.mediaplayer.util.LogFactory;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.cybergarage.util.CommonLog;

public class DlnaService extends Service implements IBaseEngine,
													DeviceChangeListener,
													ControlCenterWorkThread.ISearchDeviceListener{
	
	private static final CommonLog log = LogFactory.createLog();
	public static  final String TAG="DlnaService";
	
	public static final String SEARCH_DEVICES = "com.alcatel.smartlinkv3.allshare.search_device";
	public static final String RESET_SEARCH_DEVICES = "com.alcatel.smartlinkv3.allshare.reset_search_device";
	
	private static final int NETWORK_CHANGE = 0x0001;
	private boolean firstReceiveNetworkChangeBR = true;
	private  NetworkStatusChangeBR mNetworkStatusChangeBR;
	
	
	private  ControlPoint mControlPoint;
	private  ControlCenterWorkThread mCenterWorkThread;
	private  AllShareProxy mAllShareProxy;
	private  Handler mHandler;
	


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG,"DlnaService onCreate");
		init();
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG,"onStartCommand: " + intent);
		if (intent != null && intent.getAction() != null){
			String action = intent.getAction();
			if (DlnaService.SEARCH_DEVICES.equals(action)) {
				startEngine();
			}else if (DlnaService.RESET_SEARCH_DEVICES.equals(action)){
				restartEngine();
			}
		}else{
			Log.d(TAG,"intent = " + intent);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG,"DlnaService onDestroy");
		unInit();
		super.onDestroy();
	}
	
	
	private void init(){
		mAllShareProxy = AllShareProxy.getInstance(this);
		
		mControlPoint = new ControlPoint();
		SmartLinkV3App.getInstance().setControlPoint(mControlPoint);
		mControlPoint.addDeviceChangeListener(this);
		mControlPoint.addSearchResponseListener(new SearchResponseListener() {		
			public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
			}
		});
	

		mCenterWorkThread = new ControlCenterWorkThread(this, mControlPoint);
		mCenterWorkThread.setSearchListener(this);
		
		mHandler = new Handler(){

			public void handleMessage(Message msg) {
				switch(msg.what){
					case NETWORK_CHANGE:
						mAllShareProxy.resetSearch();
						break;
				}
			}
			
		};
		
		registerNetworkStatusBR();
		
		boolean ret = CommonUtil.openWifiBrocast(this);
		Log.d(TAG,"openWifiBrocast = " + ret);
	}
	
	private void unInit(){
		unRegisterNetworkStatusBR();
		SmartLinkV3App.getInstance().setControlPoint(null);
		mCenterWorkThread.setSearchListener(null);
		mCenterWorkThread.exit();
	}

	
	@Override
	public boolean startEngine() {
		awakeWorkThread();
		return true;
	}


	@Override
	public boolean stopEngine() {
		exitWorkThread();
		return true;
	}


	@Override
	public boolean restartEngine() {
		mCenterWorkThread.reset();
		return true;
	}




	@Override
	public void deviceAdded(Device dev) {
		mAllShareProxy.addDevice(dev);
	}


	@Override
	public void deviceRemoved(Device dev) {
		Log.d(TAG,"deviceRemoved dev = " + dev.getUDN());
		mAllShareProxy.removeDevice(dev);
	}
	
	
	private void awakeWorkThread(){

		if (mCenterWorkThread.isAlive()){
			mCenterWorkThread.awakeThread();
		}else{
			mCenterWorkThread.start();
		}
	}
	
	private void exitWorkThread(){
		if (mCenterWorkThread != null && mCenterWorkThread.isAlive()){
			mCenterWorkThread.exit();
			long time1 = System.currentTimeMillis();
			while(mCenterWorkThread.isAlive()){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long time2 = System.currentTimeMillis();
			Log.d(TAG,"exitCenterWorkThread cost time:" + (time2 - time1));
			mCenterWorkThread = null;
		}
	}
	
	
	@Override
	public void onSearchComplete(boolean searchSuccess) {

		if (!searchSuccess){
			sendSearchDeviceFailBrocast(this);
		}
	}
	
	public static final String SEARCH_DEVICES_FAIL = "com.geniusgithub.allshare.search_devices_fail";
	public static void sendSearchDeviceFailBrocast(Context context){
		Log.d(TAG,"sendSearchDeviceFailBrocast");
		Intent intent = new Intent(SEARCH_DEVICES_FAIL);
		context.sendBroadcast(intent);
	}
	
	private class NetworkStatusChangeBR extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent != null){
				String action = intent.getAction();
				if (action != null){
					if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)){
						sendNetworkChangeMessage();
					}
				}
			}
			
		}
		
	}
	
	private void registerNetworkStatusBR(){
		if (mNetworkStatusChangeBR == null){
			mNetworkStatusChangeBR = new NetworkStatusChangeBR();
			registerReceiver(mNetworkStatusChangeBR, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
	}
	
	private void unRegisterNetworkStatusBR(){
		if (mNetworkStatusChangeBR != null){
			unregisterReceiver(mNetworkStatusChangeBR);
		}
	}
	
	private void sendNetworkChangeMessage(){
		if (firstReceiveNetworkChangeBR){
			Log.d(TAG,"first receive the NetworkChangeMessage, so drop it...");
			firstReceiveNetworkChangeBR = false;
			return ;
		}
		
		mHandler.removeMessages(NETWORK_CHANGE);
		mHandler.sendEmptyMessageDelayed(NETWORK_CHANGE, 500);
	}

}
