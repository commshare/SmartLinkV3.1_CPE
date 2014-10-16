package com.alcatel.smartlinkv3.business.model;

import com.alcatel.smartlinkv3.business.statistics.UsageHistoryItem;

public class UsageHistoryItemModel{
	public String m_strDate = new String();
	public long m_lDownloadBytes = 0;
	public long m_lUploadBytes = 0;
	
	public void setValue(UsageHistoryItem result){
		m_strDate = result.Date;
		m_lDownloadBytes = result.DownloadBytes;
		m_lUploadBytes = result.UploadBytes;
	}
}
