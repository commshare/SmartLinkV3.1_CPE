package com.alcatel.smartlinkv3.mediaplayer.proxy;

import java.util.List;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.UPnPStatus;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.content.Context;

import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItem;
import com.alcatel.smartlinkv3.mediaplayer.util.ParseUtil;

public class GetMetaDataProxy {

	public static interface GetMetaDataRequestCallback
	{
		public void onGetItemMetaData(MediaItem item);
	}
	
	private static final CommonLog log = LogFactory.createLog();
	
	public static  void syncGetMetaData(final Context context, final String path, final GetMetaDataRequestCallback callback) {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {

				List<MediaItem> items = null;
				try {
					items = getMetaData(context, path);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (callback != null){
					if(items != null && items.size() > 0)
						callback.onGetItemMetaData(items.get(0));
					else
						callback.onGetItemMetaData(null);
				}
			}
		});
		
		thread.start();
	}
	
	
	public static List<MediaItem> getMetaData(Context context, String path) throws Exception{
		
		
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
			return items;
		} else {
			UPnPStatus err = action.getControlStatus();
			System.out.println("Error Code = " + err.getCode());
			System.out.println("Error Desc = " + err.getDescription());
		}
		return null;
	}
}
