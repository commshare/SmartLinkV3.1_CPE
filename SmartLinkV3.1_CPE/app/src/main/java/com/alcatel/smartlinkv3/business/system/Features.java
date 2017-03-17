package com.alcatel.smartlinkv3.business.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alcatel.smartlinkv3.business.BaseResult;

public class Features extends BaseResult{
	private String DeviceName = new String();
	private String HttpApiVersion = new String();
	private Map<String, ArrayList<String>> Features = new HashMap<String, ArrayList<String>>();
	
	@Override
	public void clear(){
		DeviceName = "";
		HttpApiVersion = "";
		Features.clear();
	}
	
	public String getDeviceName() {
		return DeviceName;
	}
	public void setDeviceName(String strDeviceName) {
		DeviceName = strDeviceName;
	}
	public String getHttpApiVersion() {
		return HttpApiVersion;
	}
	public void setHttpApiVersion(String httpApiVersion) {
		HttpApiVersion = httpApiVersion;
	}
	public Map<String, ArrayList<String>> getFeatures() {
		return Features;
	}
	public void setFeatures(Map<String, ArrayList<String>> features) {
		Features = features;
	}
}
