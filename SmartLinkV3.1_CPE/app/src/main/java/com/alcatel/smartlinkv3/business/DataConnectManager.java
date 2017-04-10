package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;

public class DataConnectManager {
	private Context m_context = null;

	private static DataConnectManager m_instance;

	public static synchronized DataConnectManager getInstance() {
		if (m_instance == null) {
			m_instance = new DataConnectManager();
		}
		return m_instance;
	}

	private boolean m_bWifiConnected = false;// whether this phone have wifi
												// connected;
	private boolean m_bPreWifiConnected = false;
	private boolean m_bCPEWifiConnected = false;// whether or not connected to
												// CPE wifi;
	private boolean m_bPreCPEWifiConnected = false;
	private boolean m_bMobileNetworkConnected = false;
	private boolean m_bPreMobileConnected = false;

	// private Intent m_CPEWifiIntent = null;

	public void setContext(Context context) {
		m_context = context;
	}

	public void setNoConnected() {
		synchronized (this) {
			m_bPreMobileConnected = m_bMobileNetworkConnected;
			m_bPreWifiConnected = m_bWifiConnected;
			m_bPreCPEWifiConnected = m_bCPEWifiConnected;
			m_bMobileNetworkConnected = false;
			m_bWifiConnected = false;
			m_bCPEWifiConnected = false;

			if (m_bPreMobileConnected
					|| m_bPreWifiConnected
					|| m_bPreCPEWifiConnected) {

				HttpAccessLog.getInstance().writeLogToFile("CPE WIFI error:setNoConnected");
				Intent intent = new Intent(MessageUti.CPE_WIFI_CONNECT_CHANGE);
				m_context.sendBroadcast(intent);
			}
		}
	}

	public void setMobileConnected(boolean bConnected) {

		synchronized (this) {
			if (bConnected == true) {
				m_bPreWifiConnected = m_bWifiConnected;
				m_bWifiConnected = false;
				m_bPreCPEWifiConnected = m_bCPEWifiConnected;
				m_bCPEWifiConnected = false;
			}

			m_bPreMobileConnected = m_bMobileNetworkConnected;
			m_bMobileNetworkConnected = bConnected;

			if (m_bPreMobileConnected != m_bMobileNetworkConnected) {
				if (m_bCPEWifiConnected == false)
					HttpAccessLog.getInstance().writeLogToFile(
							"CPE WIFI error:setMobileConnected");
				Intent intent = new Intent(MessageUti.CPE_WIFI_CONNECT_CHANGE);
				m_context.sendBroadcast(intent);
			}
		}
	}

	public boolean getMobileConnected() {
		return m_bMobileNetworkConnected;
	}

	public void setWifiConnected(boolean bConnected) {
		synchronized (this) {
			if (bConnected) {
				m_bPreMobileConnected = m_bMobileNetworkConnected;
				m_bMobileNetworkConnected = false;
			} else {
				m_bPreCPEWifiConnected = m_bCPEWifiConnected;
				m_bCPEWifiConnected = false;
			}
			m_bPreWifiConnected = m_bWifiConnected;
			m_bWifiConnected = bConnected;

			if (m_bPreWifiConnected != m_bWifiConnected) {
				if (m_bCPEWifiConnected == false)
					HttpAccessLog.getInstance().writeLogToFile(
							"CPE WIFI error:setWifiConnected");
				Intent intent = new Intent(MessageUti.CPE_WIFI_CONNECT_CHANGE);
				m_context.sendBroadcast(intent);
			}
		}
	}

	public boolean getWifiConnected() {
		return m_bWifiConnected;
	}

	public void setCPEWifiConnected(boolean bConnected) {
		if (bConnected == true) {
			m_bPreMobileConnected = m_bMobileNetworkConnected;
			m_bMobileNetworkConnected = false;
		}

		m_bPreCPEWifiConnected = m_bCPEWifiConnected;
		m_bCPEWifiConnected = bConnected;

		if (m_bPreCPEWifiConnected != m_bCPEWifiConnected) {
			if (m_bCPEWifiConnected == false)
				HttpAccessLog.getInstance().writeLogToFile(
						"CPE WIFI error:setCPEWifiConnected(false)");
			Intent intent = new Intent(MessageUti.CPE_WIFI_CONNECT_CHANGE);
			m_context.sendBroadcast(intent);
			
			
		}
	}

	public boolean getCPEWifiConnected() {
		return m_bCPEWifiConnected;
	}

	private void sendConnectChangeMsg() {
		if (m_bPreMobileConnected != m_bMobileNetworkConnected
				|| m_bPreWifiConnected != m_bWifiConnected
				|| m_bPreCPEWifiConnected != m_bCPEWifiConnected) {
			Intent intent = new Intent(MessageUti.CPE_WIFI_CONNECT_CHANGE);
			m_context.sendBroadcast(intent);
		}
	}

	private void sendCPEWifiConnectChangeMsg() {
		if (m_bCPEWifiConnected != m_bPreCPEWifiConnected) {
			// Intent preIntent = m_CPEWifiIntent;
			// if(preIntent != null)
			// m_context.removeStickyBroadcast(preIntent);
			// m_CPEWifiIntent= new Intent(MessageUti.CPE_WIFI_CONNECT_CHANGE);
			// m_context.sendStickyBroadcast(m_CPEWifiIntent);
			Intent intent = new Intent(MessageUti.CPE_WIFI_CONNECT_CHANGE);
			m_context.sendBroadcast(intent);
		}
	}
}
