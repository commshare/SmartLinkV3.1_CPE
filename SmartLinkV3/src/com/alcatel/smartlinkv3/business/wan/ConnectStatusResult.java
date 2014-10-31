package com.alcatel.smartlinkv3.business.wan;

import com.alcatel.smartlinkv3.business.BaseResult;

public class ConnectStatusResult extends BaseResult{
    
	public int ConnectionStatus = 0;//0: disconnected 1: connecting 2:connected 3:disconnecting
	public String ConnectProfile = new String();
	public long UlRate = 0;//Current upload rate 
	public long DlRate = 0;//Current download rate 
	public long UlBytes = 0;//Upload bytes
	public long DlBytes = 0;//Download bytes
	public String IPv4Adrress = new String();
	public String IPv6Adrress = new String();
	public long UsedTimes  = 0;
	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		ConnectionStatus = 0;
		ConnectProfile = "";
		UlRate = 0;
		DlRate = 0;
		UlBytes = 0;
		DlBytes = 0;
		IPv4Adrress = "";
		IPv6Adrress = "";
		UsedTimes = 0;
	}

}
