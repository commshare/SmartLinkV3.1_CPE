package com.alcatel.smartlinkv3.business;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;

import java.util.ArrayList;

public class FeatureVersionManager {
	
	private static FeatureVersionManager m_instance;
	
	//support device name
    public static synchronized   FeatureVersionManager getInstance()
    {
        if(m_instance == null) {
            m_instance = new FeatureVersionManager();
        }
        return m_instance;
    }

    public boolean isSupportForceLogin() {
        return isSupportApi("User", "ForceLogin");
    }

    public boolean isY900Project() {
        return BusinessManager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900");
    }

    public boolean isSupportFtp() {
        return isSupportApi("Sharing","GetFtpStatus");
    }

    public boolean isSupportDLNA() {
        return isSupportApi("Sharing", "GetDLNASettings") && isSupportApi("Sharing", "SetDLNASettings");
    }

    public boolean isSupportApi(String strModule,String strApi) {
    	boolean bSupport = false;

		if (strModule == null)
			return false;

		if (strApi == null)
			return false;

		if (strModule.equals(BaseRequest.ANY_MODULE))
			return true;

    	ArrayList<String> apiLst = BusinessManager.getInstance().getFeatures().getFeatures().get(strModule);
    	if(null != apiLst && apiLst.size() > 0) {
    		if(apiLst.contains(strApi))
    			bSupport = true;
    	}
    	
    	return bSupport;
    }
    
    public boolean isSupportModule(String strModule) {
    	boolean bSupport = false;
    	ArrayList<String> apiLst = BusinessManager.getInstance().getFeatures().getFeatures().get(strModule);
    	if(apiLst != null && apiLst.size() > 0)
    		bSupport = true;
    	return bSupport;
    }
    
    public boolean isSupportDevice(String strDeviceName) {
    	return BusinessManager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase(strDeviceName);
    }
}