package com.alcatel.wifilink.business.update;

import com.alcatel.wifilink.business.BaseResult;

public class DeviceUpgradeStateInfo extends BaseResult {

	/*	0: No start update(UI does not send the start update command)
		1: updating (Download Firmware phase)
		2: complete
	 */
	private int Status=0;
	/*
	 * If status =1,process:1- 100 , and stand for the updating(download)process.
	 */
	private int Process=1;
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		Status=0;
		Process=1;
	}

	//Status
	public int getStatus(){
		return Status;
	}
	public void setStatus(int nStatus){
		Status = nStatus;
	}

	//Process
	public int getProcess(){
		return Process;
	}
	public void setProcess(int nProcess){
		Process = nProcess;
	}
}
