package com.alcatel.smartlinkv3.mediaplayer.proxy;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.UPnPStatus;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItem;
import com.alcatel.smartlinkv3.mediaplayer.util.ParseUtil;

public class GetMetaDataProxy {

	public  ExecutorService executorService = Executors.newFixedThreadPool(5);
	
	public static interface GetMetaDataRequestCallback
	{
		public void onGetItemMetaData(MediaItem item);
	}
	
	private static final CommonLog log = LogFactory.createLog();
	
	public  void syncGetMetaData(final Context context, final String path, final GetMetaDataRequestCallback callback) {

		syncGetMetaData p = new syncGetMetaData(context, path,callback);
		executorService.submit(new syncGetMetaDataLoader(p));
		
	}
	// Task for the queue
	public class syncGetMetaData {
		public Context m_context; 
		public String m_path;
		public GetMetaDataRequestCallback m_callback;

		public syncGetMetaData(Context context, String path, GetMetaDataRequestCallback callback) {
			this.m_context = context;
			this.m_path = path;
			this.m_callback = callback;
		}
	}

	class syncGetMetaDataLoader implements Runnable {
		syncGetMetaData m_syncGetMetaData;

		syncGetMetaDataLoader(syncGetMetaData m_metadata) {
			this.m_syncGetMetaData = m_metadata;
		}

		@Override
		public void run() {

			List<MediaItem> items = null;
			try {
				items = getMetaData(m_syncGetMetaData.m_context, m_syncGetMetaData.m_path);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (m_syncGetMetaData.m_callback != null){
				if(items != null && items.size() > 0)
				{
					m_syncGetMetaData.m_callback.onGetItemMetaData(items.get(0));
				}
				else
				{
					m_syncGetMetaData.m_callback.onGetItemMetaData(null);
				}
			}
		}
	}
	
	
	
	
	
	public static synchronized List<MediaItem> getMetaData(Context context, String path) throws Exception{
		
		
		Device selDevice = AllShareProxy.getInstance(context).getDMSSelectedDevice();
		if (selDevice == null) {
			log.e("no selDevice!!!");
			return null;
		}
	
		org.cybergarage.upnp.Service service = selDevice
		.getService("urn:schemas-upnp-org:service:ContentDirectory:1");
		if (selDevice == null)
		{
			log.e("no service for ContentDirectory!!!");
			return null;
		}
		
		Action action = service.getAction("GetMetaData");
		if(action == null)
		{
			log.e("action for GetMetaData is null");
			return null;
		}
	
	//	action.getActionNode().print();	
		
		ArgumentList argumentList = action.getArgumentList();
		argumentList.getArgument("ObjectPath").setValue(path);		
		argumentList.getArgument("Filter").setValue("*");

		if (action.postControlAction()) {
			ArgumentList outArgList = action.getOutputArgumentList();
			Argument result = outArgList.getArgument("Result");
			log.d("result value = \n" + result.getValue());	
			
			List<MediaItem> items = ParseUtil.parseResult(result);
			log.d("path:" + path + " item:" + items.get(0).getRes() + "size = "+items.size());
			return items;
		} else {
			UPnPStatus err = action.getControlStatus();
			System.out.println("Error Code = " + err.getCode());
			System.out.println("Error Desc = " + err.getDescription());
		}
		return null;
	}
}