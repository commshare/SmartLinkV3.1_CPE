package com.alcatel.smartlinkv3.samba;
import java.net.UnknownHostException;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;
import android.os.Handler;

public class SmbLoginTask extends Thread
{
	private String mUserName;
	private String mPassword;
	private Handler mHandler;
	
	public SmbLoginTask(String userName, String password, Handler handler)
	{
		mUserName = userName;		
		mPassword = password;
		mHandler = handler;
	}

	@Override
	public void run()
	{		
		try {  
			String strServerIp = BusinessMannager.getInstance().getServerAddress();
            UniAddress dc = UniAddress.getByName(strServerIp);  
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(strServerIp,  
            		mUserName, mPassword);             
            HttpAccessLog.getInstance().writeLogToFile("Samba Login: user = "+ mUserName+ "    pwd = "+mPassword);	            
            SmbSession.logon(dc, auth);             
            SmbUtils.AUTH = auth;  
            onFinish();      
        } catch (SmbAuthException e) {           	
        	onError(e.getMessage());
            e.printStackTrace();  
            HttpAccessLog.getInstance().writeLogToFile("Samba error: Login: "+ e.getMessage());   
            HttpAccessLog.getInstance().writeLogToFile("Samba error: Login: netstatus: "+ e.getNtStatus());
        	Throwable t = e.getRootCause();				
			if(t != null)
				HttpAccessLog.getInstance().writeLogToFile(
						"Samba error: Login: getRootCause "
								+ t.toString());
           
        } catch (SmbException e) {         	
        	onError(e.getMessage());
            e.printStackTrace();
            HttpAccessLog.getInstance().writeLogToFile("Samba error: Login: "+ e.getMessage());
            HttpAccessLog.getInstance().writeLogToFile("Samba error: Login: netstatus: "+ e.getNtStatus());
          	Throwable t = e.getRootCause();				
        			if(t != null)
        				HttpAccessLog.getInstance().writeLogToFile(
        						"Samba error: Login: getRootCause "
        								+ t.toString());
        } catch(UnknownHostException e){   
        	onError(e.getMessage());
            e.printStackTrace();
            HttpAccessLog.getInstance().writeLogToFile("Samba error: Login: "+ e.getMessage());
        }		
	}

	private void onFinish()
	{
		mHandler.obtainMessage(
				SmbUtils.SMB_MSG_TASK_FINISH, null)
				.sendToTarget();		
	}
	
	private void onError(String strErr)
	{
		mHandler.obtainMessage(
				SmbUtils.SMB_MSG_TASK_ERROR, strErr)
				.sendToTarget();	
	}
}