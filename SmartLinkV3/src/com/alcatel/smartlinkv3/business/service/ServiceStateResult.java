package com.alcatel.smartlinkv3.business.service;

import com.alcatel.smartlinkv3.business.BaseResult;

public class ServiceStateResult extends BaseResult {
	

	/**
	 * ServiceType State,  Samba = 0; DLNA = 1; FTP = 2
	 */	
	public int ServiceType = 0;	

	/**
	 * service State,  Disabled = 0; Enabled = 1
	 */
	public int State = 0;

	@Override
	public void clear() {
		State = 0;
	}

}
