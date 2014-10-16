package com.alcatel.smartlinkv3.business.model;

public class ServiceModel {

	public class SambaSettingModel {
		public String m_strHostName = new String();
		public String m_strDescription = new String();
		public String m_strWorkGroup = new String();
		public String m_strAccessPath = new String();
		public int m_nDevType = 0;
		public int m_nAnonymous = 0;
		public String m_strUserName = new String();
		public String m_strPassword = new String();
		public int m_nAuthType = 0;
	};
	
	public class DlnaSettingModel {	
		public String m_strFriendlyName = new String();
		public String m_strMediaDirectories = new String();		
		public int m_nDevType = 0;		

	};
	
	public class FtpSettingModel {
		public String m_strAccessPath = new String();
		public int m_nDevType = 0;
		public int m_nAnonymous = 0;
		public String m_strUserName = new String();
		public String m_strPassword = new String();
		public int m_nAuthType = 0;
	};

}
