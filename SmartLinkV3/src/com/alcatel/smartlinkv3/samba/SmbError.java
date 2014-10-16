package com.alcatel.smartlinkv3.samba;

import java.util.HashMap;
import com.alcatel.smartlinkv3.R;

public class SmbError {	
	public static final int ERROR_RES = -1;	
	
	public static final int  SMB_ERR_LOGON_FAILURE = 0xC000006D;	
	public static final int  SMB_ERR_DISK_FULL = 0xC000007F;	

	public static HashMap<Integer, Integer> SmbErrorMap = new HashMap<Integer, Integer>();	
	
	static
	{	
		SmbErrorMap.put(SMB_ERR_LOGON_FAILURE, R.string.smb_error_logon_failure);
		SmbErrorMap.put(SMB_ERR_DISK_FULL, R.string.smb_error_disk_full);		
	}
	
	public static int getErrorDescription (int errCode) {
		int resID = ERROR_RES;
		try
		{
			resID = SmbErrorMap.get(errCode);
		}catch (Exception e) {		
			e.printStackTrace();
		}
		return resID;
	}	
}