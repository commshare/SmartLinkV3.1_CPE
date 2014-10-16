package com.alcatel.smartlinkv3.business;

import java.util.ArrayList;

public class FeatureVersionManager {
	
	private static FeatureVersionManager m_instance;
	
	//support device name
	public static String VERSION_DEVICE_M100 = "MM100";
    public static synchronized   FeatureVersionManager getInstance()
    {
        if(m_instance == null) {
            m_instance = new FeatureVersionManager();
        }
        return m_instance;
    }
    
    public boolean isSupportApi(String strModule,String strApi) {
    	boolean bSupport = false;
    	ArrayList<String> apiLst = BusinessMannager.getInstance().getFeatures().getFeatures().get(strModule);
    	if(null != apiLst && apiLst.size() > 0) {
    		if(apiLst.contains(strApi))
    			bSupport = true;
    	}
    	
    	return bSupport;
    }
    
    public boolean isSupportDevice(String strDeviceName) {
    	return BusinessMannager.getInstance().getFeatures().getModel().equalsIgnoreCase(strDeviceName);
    }
}