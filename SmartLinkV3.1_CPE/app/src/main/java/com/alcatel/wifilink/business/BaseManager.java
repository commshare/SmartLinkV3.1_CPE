package com.alcatel.wifilink.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.NetworkConnectionException;
import com.alcatel.wifilink.httpservice.RestHttpClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.alcatel.wifilink.common.Const.ACTION_NETWORK_DISCONNECT;

public abstract class BaseManager {
	private final static String TAG = "BaseManager";
	protected boolean m_bStopBusiness = false;
	protected Context m_context;
	protected ManagerBroadcastReceiver m_msgReceiver;
	private CompositeSubscription msubscription;
	
	public BaseManager(Context context) {
    	m_context = context;
    	m_msgReceiver = new ManagerBroadcastReceiver();
    	m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.CPE_BUSINESS_STATUS_CHANGE));
    	m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
    }

//	public void sendBroadcast(BaseResponse response, String action) {
//		Intent intent = new Intent(action);
//		intent.putExtra(MessageUti.HTTP_RESPONSE, response);
//		m_context.sendBroadcast(intent);
//	}

	public boolean isWifiConnect() {
		return DataConnectManager.getInstance().getCPEWifiConnected();
	}

	protected abstract void onBroadcastReceive(Context context, Intent intent);
	protected abstract void clearData();
	protected abstract void stopRollTimer();
	
	private class ManagerBroadcastReceiver extends BroadcastReceiver
	{
	    @Override  
        public void onReceive(Context context, Intent intent) {
	    	
	    	if(intent.getAction().equals(MessageUti.CPE_BUSINESS_STATUS_CHANGE)) {
	    		m_bStopBusiness = intent.getBooleanExtra("stop", false);
	    		if(m_bStopBusiness) {
	    			stopRollTimer();
	    			clearData();
	    		}
	    	}
	    	
	    	if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
				if(!isWifiConnect()) {
					stopRollTimer();
					clearData();
				}
	    	}
	    	
	    	onBroadcastReceive(context,intent);
        }  	
	}

	/**
	 * Checks if the device has any active internet connection.
	 *
	 * @return true device with internet connection, otherwise false.
	 */
	public boolean isInternetConnected() {

		 if (DataConnectManager.getInstance().getCPEWifiConnected())
		 	return true;
		if (!RestHttpClient.getInstance().isSniffHttpServer() && DataConnectManager.getInstance().getWifiConnected())
			return true;

		return false;
	}

//	public <T> Observable<T> createObservable(BaseRequest baseRequest, Action1<? super T> onNext) {
		public <T> Observable<T> createObservable(BaseRequest baseRequest) {
		Observable<T> observable;
		if (!isInternetConnected()) {
			Throwable e = new NetworkConnectionException("network unavailable.");
			e.printStackTrace();
			observable = Observable.create( subscriber -> subscriber.onError(e));
			observable = observable.subscribeOn(Schedulers.io());
			if (m_context != null){
				Intent intent = new Intent(ACTION_NETWORK_DISCONNECT);
				m_context.sendBroadcast(intent);
			}
		} else {
			Observable.OnSubscribe<T> f = subscriber -> {
				RestHttpClient.getInstance().sendPostRequest(baseRequest, subscriber);
			};
			observable = Observable.create(f);
			observable = observable.subscribeOn(Schedulers.io()).doOnError(error -> {
				Log.e(TAG, "e.getMessage()-->:" + error.getMessage());
//				if (error instanceof NotFoundTException) {
//					NotFoundTException notFoundTException = (NotFoundTException) error;
//					if (notFoundTException.ErrorCode != -1) {
//						dispatchErrorCode(notFoundTException.ErrorCode, context);
//					}
//				}
				error.printStackTrace();
//			}).doOnNext(data -> {
//				if (data instanceof NetStatus) {
//					dispatchErrorCode(((NetStatus) data).getError_id(), context);
//				}

			});

//			if (onNext != null)
//				observable = observable.doOnNext(onNext);
		}

		observable = observable.observeOn(AndroidSchedulers.mainThread());
		return observable;
	}

}
