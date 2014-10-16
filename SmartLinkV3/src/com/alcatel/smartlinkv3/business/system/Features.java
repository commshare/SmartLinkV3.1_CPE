package com.alcatel.smartlinkv3.business.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alcatel.smartlinkv3.business.BaseResult;

public class Features extends BaseResult{
	private String Model = new String();
	private String HttpApiVersion = new String();
	private Map<String, ArrayList<String>> Features = new HashMap<String, ArrayList<String>>();
	
	@Override
	public void clear(){
		Model = "";
		HttpApiVersion = "";
		Features.clear();
	}
	
	public String getModel() {
		return Model;
	}
	public void setModel(String model) {
		Model = model;
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
