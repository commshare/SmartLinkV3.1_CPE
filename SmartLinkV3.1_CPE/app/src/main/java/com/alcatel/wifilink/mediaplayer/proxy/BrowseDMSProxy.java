package com.alcatel.wifilink.mediaplayer.proxy;

import java.util.List;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.UPnPStatus;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.content.Context;

import com.alcatel.wifilink.mediaplayer.upnp.MediaItem;
import com.alcatel.wifilink.mediaplayer.util.ParseUtil;

public class BrowseDMSProxy {

	public static interface BrowseRequestCallback
	{
		public void onGetItems(final List<MediaItem> list);
	}
	
	private static final CommonLog log = LogFactory.createLog();
	
	public static  void syncGetDirectory(final Context context, final BrowseRequestCallback callback) {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {

				List<MediaItem> list = null;
				try {
					list = getDirectory(context);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (callback != null){
					callback.onGetItems(list);
				}
			}
		});
		
		thread.start();
	}
	
	public static void syncGetItems(final Context context, final String id,final BrowseRequestCallback callback) {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				List<MediaItem> list = null;
				try {
					list = getItems(context, id);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (callback != null){
					callback.onGetItems(list);
				}
			}
		});
		
		thread.start();
		

	}
	
	public static List<MediaItem> getDirectory(Context context) throws Exception {
		
		Device selDevice = AllShareProxy.getInstance(context).getDMSSelectedDevice();
		if (selDevice == null) {
			return null;
		}
		
		
		
//		Node selDevNode = selDevice.getDeviceNode();
//		if (selDevNode != null){
//			selDevNode.print();
//		}
		
		org.cybergarage.upnp.Service service = selDevice
		.getService("urn:schemas-upnp-org:service:ContentDirectory:1");
		if (service == null)
		{
			return null;
		}
		
//		Node serverNode = service.getServiceNode();
//		if (serverNode != null){
//			serverNode.print();
//		}
	
		Action action = service.getAction("Browse");
		if(action == null)
		{
			return null;
		}
		ArgumentList argumentList = action.getArgumentList();
		argumentList.getArgument("ObjectID").setValue(0);
		argumentList.getArgument("BrowseFlag").setValue("BrowseDirectChildren");
		argumentList.getArgument("StartingIndex").setValue("0");
		argumentList.getArgument("RequestedCount").setValue("0");
		argumentList.getArgument("Filter").setValue("*");
		argumentList.getArgument("SortCriteria").setValue("");
		
		ArgumentList actionInputArgList = action.getInputArgumentList();	
//		int size = actionInputArgList.size();
//		for(int i = 0; i < size; i++){
//			Argument argument =  (Argument) (actionInputArgList.getInstant(i));
//			argument.getArgumentNode().print();
//		}

		if (action.postControlAction()) {
			ArgumentList outArgList = action.getOutputArgumentList();
			Argument result = outArgList.getArgument("Result");
		
			
			
			List<MediaItem> items = ParseUtil.parseResult(result);
			return items;
		} else {
			UPnPStatus err = action.getControlStatus();
		}
		return null;
	}
	
	public static List<MediaItem> getItems(Context context, String id) throws Exception{
		
		
		Device selDevice = AllShareProxy.getInstance(context).getDMSSelectedDevice();
		if (selDevice == null) {
			return null;
		}
	
		org.cybergarage.upnp.Service service = selDevice
		.getService("urn:schemas-upnp-org:service:ContentDirectory:1");
		if (selDevice == null)
		{
			return null;
		}
		
		Action action = service.getAction("Browse");
		if(action == null)
		{
			return null;
		}
	
	//	action.getActionNode().print();	
		
		ArgumentList argumentList = action.getArgumentList();
		argumentList.getArgument("ObjectID").setValue(id);
		argumentList.getArgument("BrowseFlag").setValue("BrowseDirectChildren");
		argumentList.getArgument("StartingIndex").setValue("0");
		argumentList.getArgument("RequestedCount").setValue("0");
		argumentList.getArgument("Filter").setValue("*");
		argumentList.getArgument("SortCriteria").setValue("");

		if (action.postControlAction()) {
			ArgumentList outArgList = action.getOutputArgumentList();
			Argument result = outArgList.getArgument("Result");
			
			List<MediaItem> items = ParseUtil.parseResult(result);
			return items;
		} else {
			UPnPStatus err = action.getControlStatus();
		}
		return null;
	}
}
